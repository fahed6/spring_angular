import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { StaffService } from '../../services/staff.service';
import { Planning } from '../../models/planning.model';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-planning',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgFor],
  templateUrl: './planning.component.html',
  styleUrl: './planning.component.css'
})
export class PlanningComponent implements OnInit {
  private svc = inject(StaffService);
  private fb  = inject(FormBuilder);

  planning:  Planning[] = [];
  students:  Student[]  = [];
  showModal  = false;
  isEdit     = false;
  editId?:   number;
  error      = '';

  form = this.fb.group({
    studentId:   [0, [Validators.required, Validators.min(1)]],
    scheduledAt: ['', Validators.required],
    type:        ['CONDUITE'],
    status:      ['SCHEDULED'],
    notes:       ['']
  });

  ngOnInit(): void {
    this.svc.getStudents().subscribe(d => this.students = d);
    this.load();
  }

  load(): void { this.svc.getPlanning().subscribe(d => this.planning = d); }

  openAdd(): void {
    this.form.reset({ type: 'CONDUITE', status: 'SCHEDULED', studentId: 0, notes: '' });
    this.isEdit = false; this.editId = undefined; this.error = ''; this.showModal = true;
  }

  openEdit(p: Planning): void {
    this.form.patchValue({ ...p, scheduledAt: p.scheduledAt?.slice(0, 16) });
    this.isEdit = true; this.editId = p.id; this.error = ''; this.showModal = true;
  }

  save(): void {
    if (this.form.invalid) return;
    const obs = this.isEdit
      ? this.svc.updatePlanning(this.editId!, this.form.value as any)
      : this.svc.createPlanning(this.form.value as any);
    obs.subscribe({
      next: () => { this.showModal = false; this.load(); },
      error: err => this.error = err.error?.error ?? 'Erreur'
    });
  }

  delete(p: Planning): void {
    if (!confirm('Supprimer cette séance ?')) return;
    this.svc.deletePlanning(p.id!).subscribe(() => this.load());
  }

  statusClass(s: string): string {
    return { SCHEDULED: 'bg-sky-50 text-sky-800 border-sky-200', COMPLETED: 'bg-green-50 text-green-800 border-green-200', CANCELLED: 'bg-red-50 text-red-800 border-red-200' }[s] ?? 'bg-slate-50 text-slate-600 border-slate-200';
  }

  typeClass(t: string): string {
    return t === 'CODE' ? 'bg-purple-50 text-purple-800 border-purple-200' : 'bg-blue-50 text-blue-800 border-blue-200';
  }

  formatDate(d: string): string { return d ? d.replace('T', ' ').slice(0, 16) : '—'; }
}
