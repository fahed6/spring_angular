import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';
import { Planning } from '../models/planning.model';
import { Progress } from '../models/progress.model';
import { Payment } from '../models/payment.model';

export interface StaffDashboardStats {
  totalStudents: number;
  activeStudents: number;
  scheduledSessions: number;
  completedSessions: number;
  cancelledSessions: number;
  codeSessions: number;
  conduiteSessions: number;
  paidPayments: number;
  pendingPayments: number;
  partialPayments: number;
}

@Injectable({ providedIn: 'root' })
export class StaffService {
  private http = inject(HttpClient);

  getStaffDashboard(): Observable<StaffDashboardStats> {
    return this.http.get<StaffDashboardStats>('/api/staff/dashboard');
  }

  // Students (read-only)
  getStudents(search?: string): Observable<Student[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    return this.http.get<Student[]>('/api/staff/students', { params });
  }

  // Planning
  getPlanning(studentId?: number): Observable<Planning[]> {
    let params = new HttpParams();
    if (studentId) params = params.set('studentId', studentId);
    return this.http.get<Planning[]>('/api/staff/planning', { params });
  }

  createPlanning(p: Partial<Planning>): Observable<Planning> {
    return this.http.post<Planning>('/api/staff/planning', p);
  }

  updatePlanning(id: number, p: Partial<Planning>): Observable<Planning> {
    return this.http.put<Planning>(`/api/staff/planning/${id}`, p);
  }

  deletePlanning(id: number): Observable<void> {
    return this.http.delete<void>(`/api/staff/planning/${id}`);
  }

  // Progress
  getProgress(studentId: number): Observable<Progress[]> {
    return this.http.get<Progress[]>('/api/staff/progress', { params: { studentId } });
  }

  upsertProgress(p: Partial<Progress>): Observable<Progress> {
    return this.http.post<Progress>('/api/staff/progress', p);
  }

  deleteProgress(id: number): Observable<void> {
    return this.http.delete<void>(`/api/staff/progress/${id}`);
  }

  // Payments
  getPayments(studentId?: number): Observable<Payment[]> {
    let params = new HttpParams();
    if (studentId) params = params.set('studentId', studentId);
    return this.http.get<Payment[]>('/api/staff/payments', { params });
  }

  createPayment(p: Partial<Payment>): Observable<Payment> {
    return this.http.post<Payment>('/api/staff/payments', p);
  }

  updatePayment(id: number, p: Partial<Payment>): Observable<Payment> {
    return this.http.put<Payment>(`/api/staff/payments/${id}`, p);
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`/api/staff/payments/${id}`);
  }
}
