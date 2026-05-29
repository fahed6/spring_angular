import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';
import { UserInfo } from '../models/user.model';
import { DashboardStats } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);

  // ── Dashboard ─────────────────────────────────────────────────────────────
  getDashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>('/api/admin/dashboard');
  }

  // ── Students (CRUD) ───────────────────────────────────────────────────────
  getStudents(search?: string): Observable<Student[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    return this.http.get<Student[]>('/api/admin/students', { params });
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`/api/admin/students/${id}`);
  }

  createStudent(s: Partial<Student>): Observable<Student> {
    return this.http.post<Student>('/api/admin/students', s);
  }

  updateStudent(id: number, s: Partial<Student>): Observable<Student> {
    return this.http.put<Student>(`/api/admin/students/${id}`, s);
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/students/${id}`);
  }

  // ── Users (CRUD) ──────────────────────────────────────────────────────────
  getUsers(): Observable<UserInfo[]> {
    return this.http.get<UserInfo[]>('/api/admin/users');
  }

  getUser(id: number): Observable<UserInfo> {
    return this.http.get<UserInfo>(`/api/admin/users/${id}`);
  }

  createUser(u: Partial<UserInfo>): Observable<UserInfo> {
    return this.http.post<UserInfo>('/api/admin/users', u);
  }

  updateUser(id: number, u: Partial<UserInfo>): Observable<UserInfo> {
    return this.http.put<UserInfo>(`/api/admin/users/${id}`, u);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/users/${id}`);
  }

  toggleUser(id: number): Observable<UserInfo> {
    return this.http.patch<UserInfo>(`/api/admin/users/${id}/toggle`, {});
  }
}
