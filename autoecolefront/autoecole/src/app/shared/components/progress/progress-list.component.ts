import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { StaffService } from '../../../services/staff.service';
import { Progress } from '../../../models/progress.model';
import { Student } from '../../../models/student.model';

@Component({
  selector: 'app-progress-list',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './progress-list.component.html',
  styleUrl: './progress-list.component.css'
})
export class ProgressListComponent implements OnInit {
  private svc = inject(StaffService);
  private fb  = inject(FormBuilder);

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
    this.svc.getStudents().subscribe(d => this.students = d);
  }

  onStudentChange(): void {
    if (this.selectedId) {
      this.svc.getProgress(+this.selectedId).subscribe(d => this.progressList = d);
    } else {
      this.progressList = [];
    }
  }

  openEdit(p: Progress): void {
    this.form.patchValue({
      category: p.category,
      hoursCompleted: p.hoursCompleted,
      hoursRequired: p.hoursRequired,
      score: p.score ?? null
    });
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
      error: err => this.error = err.error?.message ?? 'Erreur lors de l\'enregistrement'
    });
  }

  percent(p: Progress): number {
    return p.hoursRequired > 0
      ? Math.min(100, Math.round((p.hoursCompleted / p.hoursRequired) * 100))
      : 0;
  }

  categoryClass(cat: string): string {
    return cat === 'CODE'
      ? 'bg-purple-50 text-purple-800 border-purple-200'
      : 'bg-blue-50 text-blue-800 border-blue-200';
  }
}
