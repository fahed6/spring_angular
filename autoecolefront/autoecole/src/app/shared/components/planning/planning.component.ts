import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { StaffService } from '../../../services/staff.service';
import { Planning } from '../../../models/planning.model';
import { Student } from '../../../models/student.model';
import { sessionStatusClass, sessionTypeClass } from '../../utils/badge.utils';

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
      error: err => this.error = err.error?.message ?? 'Erreur lors de l\'enregistrement'
    });
  }

  delete(p: Planning): void {
    if (!confirm('Supprimer cette séance ?')) return;
    this.svc.deletePlanning(p.id!).subscribe(() => this.load());
  }

  statusClass(s: string): string { return sessionStatusClass(s); }
  typeClass(t: string): string   { return sessionTypeClass(t); }
  formatDate(d: string): string  { return d ? d.replace('T', ' ').slice(0, 16) : '—'; }
}
