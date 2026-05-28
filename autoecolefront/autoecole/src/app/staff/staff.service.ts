import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../admin/admin.service';

export interface Planning {
  id?: number;
  studentId: number;
  studentName?: string;
  instructorId?: number;
  instructorName?: string;
  scheduledAt: string;
  type: string;
  status: string;
  notes?: string;
}

export interface Progress {
  id?: number;
  studentId: number;
  studentName?: string;
  category: string;
  hoursCompleted: number;
  hoursRequired: number;
  score?: number;
  updatedAt?: string;
  updatedBy?: string;
}

export interface Payment {
  id?: number;
  studentId: number;
  studentName?: string;
  amount: number;
  paidAt: string;
  method: string;
  status: string;
  description?: string;
  recordedBy?: string;
}

@Injectable({ providedIn: 'root' })
export class StaffService {
  private http = inject(HttpClient);

  // Students (read-only)
  getStudents(search?: string): Observable<Student[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    return this.http.get<Student[]>('/api/staff/students', { params });
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`/api/staff/students/${id}`);
  }

  // Planning
  getPlanning(studentId?: number): Observable<Planning[]> {
    let params = new HttpParams();
    if (studentId) params = params.set('studentId', studentId);
    return this.http.get<Planning[]>('/api/staff/planning', { params });
  }

  createPlanning(p: Planning): Observable<Planning> {
    return this.http.post<Planning>('/api/staff/planning', p);
  }

  updatePlanning(id: number, p: Planning): Observable<Planning> {
    return this.http.put<Planning>(`/api/staff/planning/${id}`, p);
  }

  deletePlanning(id: number): Observable<void> {
    return this.http.delete<void>(`/api/staff/planning/${id}`);
  }

  // Progress
  getProgress(studentId: number): Observable<Progress[]> {
    return this.http.get<Progress[]>('/api/staff/progress', { params: { studentId } });
  }

  upsertProgress(p: Progress): Observable<Progress> {
    return this.http.post<Progress>('/api/staff/progress', p);
  }

  // Payments
  getPayments(studentId?: number): Observable<Payment[]> {
    let params = new HttpParams();
    if (studentId) params = params.set('studentId', studentId);
    return this.http.get<Payment[]>('/api/staff/payments', { params });
  }

  createPayment(p: Payment): Observable<Payment> {
    return this.http.post<Payment>('/api/staff/payments', p);
  }

  updatePayment(id: number, p: Payment): Observable<Payment> {
    return this.http.put<Payment>(`/api/staff/payments/${id}`, p);
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`/api/staff/payments/${id}`);
  }
}