import { Routes } from '@angular/router';
import { adminGuard, staffGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./admin/layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'students',  loadComponent: () => import('./shared/components/students/student-list.component').then(m => m.StudentListComponent) },
      { path: 'users',     loadComponent: () => import('./admin/users/user-list.component').then(m => m.UserListComponent) },
      { path: 'planning',  loadComponent: () => import('./shared/components/planning/planning-list.component').then(m => m.PlanningListComponent) },
      { path: 'progress',  loadComponent: () => import('./shared/components/progress/progress-list.component').then(m => m.ProgressListComponent) },
      { path: 'payments',  loadComponent: () => import('./shared/components/payments/payment-list.component').then(m => m.PaymentListComponent) }
    ]
  },
  {
    path: 'staff',
    canActivate: [staffGuard],
    loadComponent: () => import('./staff/layout/staff-layout.component').then(m => m.StaffLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./staff/dashboard/staff-dashboard.component').then(m => m.StaffDashboardComponent) },
      { path: 'students',  loadComponent: () => import('./shared/components/students/student-list.component').then(m => m.StudentListComponent) },
      { path: 'planning',  loadComponent: () => import('./shared/components/planning/planning-list.component').then(m => m.PlanningListComponent) },
      { path: 'progress',  loadComponent: () => import('./shared/components/progress/progress-list.component').then(m => m.ProgressListComponent) },
      { path: 'payments',  loadComponent: () => import('./shared/components/payments/payment-list.component').then(m => m.PaymentListComponent) }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
