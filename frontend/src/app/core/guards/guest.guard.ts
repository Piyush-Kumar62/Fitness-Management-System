import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }

  // Redirect to dashboard based on role
  const userRole = authService.userRole();
  if (userRole) {
    router.navigate([authService.getRedirectUrl(userRole)]);
  } else {
    router.navigate(['/member/dashboard']);
  }
  return false;
};
