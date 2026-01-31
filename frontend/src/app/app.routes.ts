import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { guestGuard } from './core/guards/guest.guard';
import { UserRole } from './core/models/user.model';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/landing/landing.component').then((m) => m.LandingComponent),
  },
  {
    path: 'auth',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./layouts/auth-layout/auth-layout.component').then((m) => m.AuthLayoutComponent),
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
      },
    ],
  },
  {
    path: 'user',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layouts/user-layout/user-layout.component').then((m) => m.UserLayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/user/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/user/profile/profile.component').then((m) => m.ProfileComponent),
      },
      {
        path: 'activities',
        loadComponent: () =>
          import('./features/user/activities/activity-list/activity-list.component').then(
            (m) => m.ActivityListComponent,
          ),
      },
      {
        path: 'activities/new',
        loadComponent: () =>
          import('./features/user/activities/activity-form/activity-form.component').then(
            (m) => m.ActivityFormComponent,
          ),
      },
      {
        path: 'activities/:id',
        loadComponent: () =>
          import('./features/user/activities/activity-detail/activity-detail.component').then(
            (m) => m.ActivityDetailComponent,
          ),
      },
      {
        path: 'recommendations',
        loadComponent: () =>
          import('./features/user/recommendations/recommendation-list/recommendation-list.component').then(
            (m) => m.RecommendationListComponent,
          ),
      },
      {
        path: 'recommendations/new',
        loadComponent: () =>
          import('./features/user/recommendations/recommendation-form/recommendation-form.component').then(
            (m) => m.RecommendationFormComponent,
          ),
      },
      {
        path: 'goals',
        loadComponent: () =>
          import('./features/user/goals/goal-list/goal-list.component').then(
            (m) => m.GoalListComponent,
          ),
      },
      {
        path: 'goals/new',
        loadComponent: () =>
          import('./features/user/goals/goal-form/goal-form.component').then(
            (m) => m.GoalFormComponent,
          ),
      },
      {
        path: 'goals/edit/:id',
        loadComponent: () =>
          import('./features/user/goals/goal-form/goal-form.component').then(
            (m) => m.GoalFormComponent,
          ),
      },
      {
        path: 'goals/:id',
        loadComponent: () =>
          import('./features/user/goals/goal-detail/goal-detail.component').then(
            (m) => m.GoalDetailComponent,
          ),
      },
      {
        path: 'measurements',
        loadComponent: () =>
          import('./features/user/measurements/measurement-list/measurement-list.component').then(
            (m) => m.MeasurementListComponent,
          ),
      },
      {
        path: 'measurements/new',
        loadComponent: () =>
          import('./features/user/measurements/measurement-form/measurement-form.component').then(
            (m) => m.MeasurementFormComponent,
          ),
      },
      {
        path: 'measurements/edit/:id',
        loadComponent: () =>
          import('./features/user/measurements/measurement-form/measurement-form.component').then(
            (m) => m.MeasurementFormComponent,
          ),
      },
      {
        path: 'bmi-calculator',
        loadComponent: () =>
          import('./features/user/bmi-calculator/bmi-calculator.component').then(
            (m) => m.BmiCalculatorComponent,
          ),
      },
    ],
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] },
    loadComponent: () =>
      import('./layouts/admin-layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/dashboard/admin-dashboard.component').then(
            (m) => m.AdminDashboardComponent,
          ),
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/admin/users/user-list/user-list.component').then(
            (m) => m.UserListComponent,
          ),
      },
      {
        path: 'users/new',
        loadComponent: () =>
          import('./features/admin/users/user-form/user-form.component').then(
            (m) => m.UserFormComponent,
          ),
      },
      {
        path: 'users/edit/:id',
        loadComponent: () =>
          import('./features/admin/users/user-form/user-form.component').then(
            (m) => m.UserFormComponent,
          ),
      },
      {
        path: 'activities',
        loadComponent: () =>
          import('./features/admin/activities/activity-management.component').then(
            (m) => m.ActivityManagementComponent,
          ),
      },
      {
        path: 'analytics',
        loadComponent: () =>
          import('./features/admin/analytics/analytics.component').then(
            (m) => m.AnalyticsComponent,
          ),
      },
    ],
  },
  {
    path: '**',
    loadComponent: () =>
      import('./shared/components/page-not-found/page-not-found.component').then(
        (m) => m.PageNotFoundComponent,
      ),
  },
];
