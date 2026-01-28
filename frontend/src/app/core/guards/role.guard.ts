import { inject } from '@angular/core';
import { Router, CanActivateFn, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { UserRole } from '../models/user.model';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toast = inject(ToastService);

  const requiredRoles = route.data['roles'] as UserRole[];
  const userRole = authService.userRole();

  if (!userRole) {
    toast.error('Access denied');
    router.navigate(['/auth/login']);
    return false;
  }

  if (requiredRoles && requiredRoles.includes(userRole as UserRole)) {
    return true;
  }

  toast.error('You do not have permission to access this page');
  router.navigate(['/user/dashboard']);
  return false;
};
