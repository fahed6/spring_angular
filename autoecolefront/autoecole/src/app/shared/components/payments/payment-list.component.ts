import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { StaffService } from '../../../services/staff.service';
import { Payment } from '../../../models/payment.model';
import { Student } from '../../../models/student.model';
import { paymentStatusClass } from '../../utils/badge.utils';

@Component({
  selector: 'app-payment-list',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './payment-list.component.html',
  styleUrl: './payment-list.component.css'
})
export class PaymentListComponent implements OnInit {
  private svc = inject(StaffService);
  private fb  = inject(FormBuilder);

  payments:          Payment[] = [];
  students:          Student[] = [];
  selectedStudentId: number | null = null;
  showModal  = false;
  isEdit     = false;
  editId?:   number;
  error      = '';

  form = this.fb.group({
    studentId:   [0, [Validators.required, Validators.min(1)]],
    amount:      [0, [Validators.required, Validators.min(0)]],
    paidAt:      [new Date().toISOString().slice(0, 10), Validators.required],
    method:      ['CASH'],
    status:      ['PAID'],
    description: ['']
  });

  ngOnInit(): void {
    this.svc.getStudents().subscribe(d => this.students = d);
    this.load();
  }

  load(): void {
    this.svc.getPayments(this.selectedStudentId ?? undefined)
            .subscribe(d => this.payments = d);
  }

  openAdd(): void {
    this.form.reset({
      method: 'CASH', status: 'PAID', studentId: 0, amount: 0,
      paidAt: new Date().toISOString().slice(0, 10), description: ''
    });
    this.isEdit = false; this.editId = undefined; this.error = ''; this.showModal = true;
  }

  openEdit(p: Payment): void {
    this.form.patchValue(p as any);
    this.isEdit = true; this.editId = p.id; this.error = ''; this.showModal = true;
  }

  save(): void {
    if (this.form.invalid) return;
    const obs = this.isEdit
      ? this.svc.updatePayment(this.editId!, this.form.value as any)
      : this.svc.createPayment(this.form.value as any);
    obs.subscribe({
      next: () => { this.showModal = false; this.load(); },
      error: err => this.error = err.error?.message ?? 'Erreur lors de l\'enregistrement'
    });
  }

  delete(p: Payment): void {
    if (!confirm('Supprimer ce paiement ?')) return;
    this.svc.deletePayment(p.id!).subscribe(() => this.load());
  }

  statusClass(s: string): string { return paymentStatusClass(s); }

  methodLabel(m: string): string {
    return ({ CASH: 'Espèces', CARD: 'Carte', TRANSFER: 'Virement' } as Record<string, string>)[m] ?? m;
  }

  studentName(id: number): string {
    const s = this.students.find(x => x.id === id);
    return s ? `${s.firstName} ${s.lastName}` : '—';
  }
}
