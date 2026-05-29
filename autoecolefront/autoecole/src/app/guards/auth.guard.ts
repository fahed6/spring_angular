import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (auth.isLoggedIn() && auth.isAdmin()) return true;
  inject(Router).navigate(['/login']);
  return false;
};

export const staffGuard: CanActivateFn = () => {
  if (inject(AuthService).isLoggedIn()) return true;
  inject(Router).navigate(['/login']);
  return false;
};
