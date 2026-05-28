import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { StaffService } from '../../core/services/staff.service';
import { AdminService } from '../../core/services/admin.service';
import { Progress } from '../../core/models/progress.model';
import { Student } from '../../core/models/student.model';

@Component({
  selector: 'app-progress',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './progress.component.html',
  styleUrl: './progress.component.css'
})
export class ProgressComponent implements OnInit {
  private svc   = inject(StaffService);
  private admin = inject(AdminService);
  private fb    = inject(FormBuilder);

  students:     Student[]  = [];
  progressList: Progress[] = [];
  selectedId:   number | null = null;
  showModal     = false;
  editId?:      number;
  error         = '';

  form = this.fb.group({
    category:       ['CODE'],
    hoursCompleted: [0, [Validators.required, Validators.min(0)]],
    hoursRequired:  [20, [Validators.required, Validators.min(1)]],
    score:          [null as number | null]
  });

  ngOnInit(): void {
    this.admin.getStudents().subscribe(d => this.students = d);
  }

  onStudentChange(): void {
    if (this.selectedId) this.svc.getProgress(+this.selectedId).subscribe(d => this.progressList = d);
    else this.progressList = [];
  }

  openEdit(p: Progress): void {
    this.form.patchValue({ category: p.category, hoursCompleted: p.hoursCompleted, hoursRequired: p.hoursRequired, score: p.score ?? null });
    this.editId = p.id;
    this.error = '';
    this.showModal = true;
  }

  openNew(category: string): void {
    this.form.reset({ category, hoursCompleted: 0, hoursRequired: 20, score: null });
    this.editId = undefined;
    this.error = '';
    this.showModal = true;
  }

  save(): void {
    if (this.form.invalid) return;
    const payload = { ...this.form.value, studentId: this.selectedId };
    this.svc.upsertProgress(payload as any).subscribe({
      next: () => { this.showModal = false; this.onStudentChange(); },
      error: err => this.error = err.error?.error ?? 'Erreur'
    });
  }

  percent(p: Progress): number {
    return p.hoursRequired > 0 ? Math.min(100, Math.round((p.hoursCompleted / p.hoursRequired) * 100)) : 0;
  }
}
