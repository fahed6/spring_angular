import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Student {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  address: string;
  status: string;
  registeredAt?: string;
  registeredBy?: string;
}

export interface UserInfo {
  id?: number;
  fullName: string;
  email: string;
  password?: string;
  role: string;
  isActive?: boolean;
  createdAt?: string;
}

export interface DashboardStats {
  totalStudents: number;
  activeStudents: number;
  totalUsers: number;
  newStudentsThisMonth: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);

  getDashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>('/api/admin/dashboard');
  }

  getStudents(search?: string): Observable<Student[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    return this.http.get<Student[]>('/api/admin/students', { params });
  }

  createStudent(s: Student): Observable<Student> {
    return this.http.post<Student>('/api/admin/students', s);
  }

  updateStudent(id: number, s: Student): Observable<Student> {
    return this.http.put<Student>(`/api/admin/students/${id}`, s);
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/students/${id}`);
  }

  getUsers(): Observable<UserInfo[]> {
    return this.http.get<UserInfo[]>('/api/admin/users');
  }

  createUser(u: UserInfo): Observable<UserInfo> {
    return this.http.post<UserInfo>('/api/admin/users', u);
  }

  updateUser(id: number, u: UserInfo): Observable<UserInfo> {
    return this.http.put<UserInfo>(`/api/admin/users/${id}`, u);
  }

  toggleUser(id: number): Observable<UserInfo> {
    return this.http.patch<UserInfo>(`/api/admin/users/${id}/toggle`, {});
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/users/${id}`);
  }
}