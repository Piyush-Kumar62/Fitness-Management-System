import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div
      class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800"
    >
      <div class="text-center">
        <div
          class="animate-spin rounded-full h-16 w-16 border-b-4 border-indigo-600 mx-auto mb-4"
        ></div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">Completing Sign In...</h2>
        <p class="text-gray-600 dark:text-gray-400">Please wait while we authenticate you</p>
        @if (errorMessage) {
          <div
            class="mt-4 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg"
          >
            <p class="text-red-700 dark:text-red-400">{{ errorMessage }}</p>
            <button
              (click)="redirectToLogin()"
              class="mt-2 text-sm text-red-600 dark:text-red-400 hover:underline"
            >
              Return to login
            </button>
          </div>
        }
      </div>
    </div>
  `,
})
export class OAuth2RedirectComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  errorMessage: string = '';

  ngOnInit(): void {
    // Get token from URL query parameters
    this.route.queryParams.subscribe((params) => {
      const token = params['token'];
      const error = params['error'];

      if (error) {
        this.errorMessage = 'OAuth2 authentication failed: ' + error;
        this.toast.error('Authentication failed');
        setTimeout(() => this.redirectToLogin(), 3000);
        return;
      }

      if (token) {
        this.authService.handleOAuth2Token(token).subscribe({
          next: () => {
            this.toast.success('Successfully signed in!');
            // Navigation is handled in auth service
          },
          error: (err) => {
            console.error('OAuth2 token handling error:', err);
            this.errorMessage = 'Failed to process authentication token';
            this.toast.error('Authentication failed');
            setTimeout(() => this.redirectToLogin(), 3000);
          },
        });
      } else {
        this.errorMessage = 'No authentication token received';
        this.toast.error('Authentication failed');
        setTimeout(() => this.redirectToLogin(), 3000);
      }
    });
  }

  redirectToLogin(): void {
    this.router.navigate(['/auth/login']);
  }
}
