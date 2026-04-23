import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service';
import { AuthService } from '../services/auth.service';

// Centralized HTTP Error Interceptor Extracts the most useful message from every backend error and shows it to the user via SweetAlert2. Prioritises the backend's own `message` field so domain-level validation messages are surfaced verbatim.
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const toast = inject(ToastService);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // ── 1. Extract the most helpful error message ──────────────────────
      let errorMessage = 'An unexpected error occurred. Please try again.';
      let errorTitle = 'Error';

      if (error.error instanceof ErrorEvent) {
        // Network / client-side error
        errorMessage = `Network error: ${error.error.message}`;
        errorTitle = 'Connection Error';
      } else {
        // Extract backend message (our GlobalExceptionHandler always sends { message, ... })
        const backendMsg: string | undefined =
          error.error?.message || error.error?.error || error.error?.detail;

        switch (error.status) {
          case 400:
            errorTitle = 'Bad Request';
            errorMessage =
              backendMsg ||
              error.error?.errors?.join(', ') ||
              'The request contained invalid data. Please check your inputs.';
            break;

          case 401:
            errorTitle = 'Session Expired';
            errorMessage = 'Your session has expired. Please log in again.';
            auth.logout();
            router.navigate(['/auth/login']);
            break;

          case 403:
            errorTitle = 'Access Denied';
            errorMessage =
              backendMsg ||
              'You do not have permission to perform this action.';
            break;

          case 404:
            errorTitle = 'Not Found';
            errorMessage =
              backendMsg ||
              'The requested resource could not be found.';
            break;

          case 409:
            errorTitle = 'Conflict';
            errorMessage =
              backendMsg ||
              'This record already exists or conflicts with an existing one.';
            break;

          case 422:
            errorTitle = 'Validation Failed';
            errorMessage =
              backendMsg ||
              'The submitted data failed server-side validation. Please review the form.';
            break;

          case 429:
            errorTitle = 'Too Many Requests';
            errorMessage =
              backendMsg ||
              'You are making requests too quickly. Please wait a moment and try again.';
            break;

          case 500:
            errorTitle = 'Server Error';
            errorMessage =
              'An internal server error occurred. Our team has been notified.';
            break;

          case 503:
            errorTitle = 'Service Unavailable';
            errorMessage =
              'The service is temporarily unavailable. Please try again shortly.';
            break;

          case 0:
            errorTitle = 'No Connection';
            errorMessage =
              'Unable to reach the server. Please check your internet connection.';
            break;

          default:
            errorTitle = `Error ${error.status}`;
            errorMessage = backendMsg || `An error occurred (HTTP ${error.status}).`;
        }
      }

      // ── 2. Show the right type of alert based on severity ───────────────
      const isAuthEndpoint = req.url.includes('/auth/');

      if (!isAuthEndpoint) {
        if (error.status === 500 || error.status === 0 || error.status === 503) {
          // Critical: full-page error dialog
          toast.criticalError(errorTitle, errorMessage);
        } else {
          // Non-critical: toast notification
          toast.error(errorMessage, errorTitle);
        }
      } else {
        // Auth endpoints – always show error (login/register failures must be surfaced)
        toast.error(errorMessage, errorTitle);
      }

      return throwError(() => error);
    }),
  );
};
