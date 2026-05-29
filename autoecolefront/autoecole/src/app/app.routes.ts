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
      { path: 'dashboard', loadComponent: () => import('./admin/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'students',  loadComponent: () => import('./admin/students/students.component').then(m => m.StudentsComponent) },
      { path: 'users',     loadComponent: () => import('./admin/users/users.component').then(m => m.UsersComponent) },
      { path: 'planning',  loadComponent: () => import('./staff/planning/planning.component').then(m => m.PlanningComponent) },
      { path: 'progress',  loadComponent: () => import('./staff/progress/progress.component').then(m => m.ProgressComponent) },
      { path: 'payments',  loadComponent: () => import('./staff/payments/payments.component').then(m => m.PaymentsComponent) }
    ]
  },
  {
    path: 'staff',
    canActivate: [staffGuard],
    loadComponent: () => import('./staff/layout/staff-layout.component').then(m => m.StaffLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./staff/dashboard/staff-dashboard.component').then(m => m.StaffDashboardComponent) },
      { path: 'students', loadComponent: () => import('./staff/students/staff-students.component').then(m => m.StaffStudentsComponent) },
      { path: 'planning', loadComponent: () => import('./staff/planning/planning.component').then(m => m.PlanningComponent) },
      { path: 'progress', loadComponent: () => import('./staff/progress/progress.component').then(m => m.ProgressComponent) },
      { path: 'payments', loadComponent: () => import('./staff/payments/payments.component').then(m => m.PaymentsComponent) }
    ]
  },
  { path: '**', redirectTo: 'login' }
];