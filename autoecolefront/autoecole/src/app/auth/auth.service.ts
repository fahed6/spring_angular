import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export interface LoginResponse {
  token: string;
  role: string;
  fullName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http       = inject(HttpClient);
  private router     = inject(Router);
  private isBrowser  = isPlatformBrowser(inject(PLATFORM_ID));

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { email, password }).pipe(
      tap(res => {
        if (this.isBrowser) {
          localStorage.setItem('token',    res.token);
          localStorage.setItem('role',     res.role);
          localStorage.setItem('fullName', res.fullName);
        }
      })
    );
  }

  logout(): void {
    if (this.isBrowser) localStorage.clear();
    this.router.navigate(['/login']);
  }

  getToken():    string | null { return this.isBrowser ? localStorage.getItem('token')    : null; }
  getRole():     string | null { return this.isBrowser ? localStorage.getItem('role')     : null; }
  getFullName(): string | null { return this.isBrowser ? localStorage.getItem('fullName') : null; }
  isLoggedIn():  boolean       { return !!this.getToken(); }
  isAdmin():     boolean       { return this.getRole() === 'ADMIN'; }
}