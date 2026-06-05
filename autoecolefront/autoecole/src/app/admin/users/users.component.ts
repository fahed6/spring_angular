import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { UserInfo } from '../../models/user.model';
import { userRoleClass, userActiveClass } from '../../shared/utils/badge.utils';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgFor],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  private svc = inject(AdminService);
  private fb  = inject(FormBuilder);

  users:     UserInfo[] = [];
  showModal  = false;
  isEdit     = false;
  editId?:   number;
  error      = '';

  form = this.fb.group({
    fullName: ['', Validators.required],
    email:    ['', [Validators.required, Validators.email]],
    password: [''],
    role:     ['STAFF'],
    isActive: [true]
  });

  ngOnInit(): void { this.load(); }

  load(): void { this.svc.getUsers().subscribe(d => this.users = d); }

  openAdd(): void {
    this.form.reset({ role: 'STAFF', isActive: true });
    this.form.get('password')?.setValidators(Validators.required);
    this.form.get('password')?.updateValueAndValidity();
    this.isEdit = false;
    this.editId = undefined;
    this.error  = '';
    this.showModal = true;
  }

  openEdit(u: UserInfo): void {
    this.form.patchValue({ ...u, password: '' });
    this.form.get('password')?.clearValidators();
    this.form.get('password')?.updateValueAndValidity();
    this.isEdit = true;
    this.editId = u.id;
    this.error  = '';
    this.showModal = true;
  }

  save(): void {
    if (this.form.invalid) return;
    const data = this.form.value;
    const obs  = this.isEdit
      ? this.svc.updateUser(this.editId!, data as any)
      : this.svc.createUser(data as any);
    obs.subscribe({
      next: () => { this.showModal = false; this.load(); },
      error: err => this.error = err.error?.error ?? 'Erreur'
    });
  }

  toggle(u: UserInfo): void { this.svc.toggleUser(u.id!).subscribe(() => this.load()); }

  roleClass(role: string): string    { return userRoleClass(role); }
  activeClass(active: boolean): string { return userActiveClass(active); }

  delete(u: UserInfo): void {
    if (!confirm(`Supprimer l'utilisateur ${u.fullName} ?`)) return;
    this.svc.deleteUser(u.id!).subscribe(() => this.load());
  }
}
