import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './students.component.html',
  styleUrl: './students.component.css'
})
export class StudentsComponent implements OnInit {
  private svc = inject(AdminService);
  private fb  = inject(FormBuilder);

  students:  Student[] = [];
  showModal  = false;
  isEdit     = false;
  editId?:   number;
  error      = '';
  search     = '';

  form = this.fb.group({
    firstName:   ['', Validators.required],
    lastName:    ['', Validators.required],
    email:       ['', [Validators.required, Validators.email]],
    phone:       [''],
    dateOfBirth: [''],
    address:     [''],
    status:      ['ACTIVE']
  });

  ngOnInit(): void { this.load(); }

  load(): void {
    this.svc.getStudents(this.search || undefined).subscribe(d => this.students = d);
  }

  openAdd(): void {
    this.form.reset({ status: 'ACTIVE' });
    this.isEdit = false;
    this.editId = undefined;
    this.error  = '';
    this.showModal = true;
  }

  openEdit(s: Student): void {
    this.form.patchValue(s as any);
    this.isEdit = true;
    this.editId = s.id;
    this.error  = '';
    this.showModal = true;
  }

  save(): void {
    if (this.form.invalid) return;
    const data = this.form.value;
    const obs  = this.isEdit
      ? this.svc.updateStudent(this.editId!, data as any)
      : this.svc.createStudent(data as any);
    obs.subscribe({
      next: () => { this.showModal = false; this.load(); },
      error: err => this.error = err.error?.error ?? 'Erreur'
    });
  }

  delete(s: Student): void {
    if (!confirm(`Supprimer ${s.firstName} ${s.lastName} ?`)) return;
    this.svc.deleteStudent(s.id!).subscribe(() => this.load());
  }

  statusClass(st: string): string {
    return { ACTIVE: 'bg-green-50 text-green-800 border-green-200', COMPLETED: 'bg-blue-50 text-blue-800 border-blue-200', SUSPENDED: 'bg-red-50 text-red-800 border-red-200' }[st] ?? 'bg-slate-50 text-slate-600 border-slate-200';
  }
}
