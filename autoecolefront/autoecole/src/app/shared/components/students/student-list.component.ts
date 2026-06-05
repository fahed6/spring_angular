import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { AdminService } from '../../../services/admin.service';
import { StaffService } from '../../../services/staff.service';
import { Student } from '../../../models/student.model';
import { studentStatusClass } from '../../utils/badge.utils';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.css'
})
export class StudentListComponent implements OnInit {
  private auth  = inject(AuthService);
  private admin = inject(AdminService);
  private staff = inject(StaffService);
  private fb    = inject(FormBuilder);

  // Role determines which API endpoint and which UI actions are shown
  isAdmin = this.auth.isAdmin();

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
    const search = this.search || undefined;
    // Admin uses /api/admin/students (full CRUD), staff uses /api/staff/students (read-only)
    const obs = this.isAdmin
      ? this.admin.getStudents(search)
      : this.staff.getStudents(search);
    obs.subscribe(d => this.students = d);
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
      ? this.admin.updateStudent(this.editId!, data as any)
      : this.admin.createStudent(data as any);
    obs.subscribe({
      next: () => { this.showModal = false; this.load(); },
      error: err => this.error = err.error?.message ?? 'Erreur lors de l\'enregistrement'
    });
  }

  delete(s: Student): void {
    if (!confirm(`Supprimer ${s.firstName} ${s.lastName} ?`)) return;
    this.admin.deleteStudent(s.id!).subscribe(() => this.load());
  }

  statusClass(st: string): string {
    return studentStatusClass(st);
  }
}
