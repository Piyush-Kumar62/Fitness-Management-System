import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './oauth2-redirect.component.html',
})
export class OAuth2RedirectComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  errorMessage: string = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const fragmentParams = this.parseFragment(window.location.hash);
      const token = fragmentParams.get('token') ?? params['token'];
      const error = fragmentParams.get('error') ?? params['error'];
      const status = fragmentParams.get('status') ?? params['status'];

      if (error) {
        this.errorMessage = 'OAuth2 authentication failed: ' + error;
        this.toast.error('Authentication failed');
        setTimeout(() => this.redirectToLogin(), 3000);
        return;
      }

      if (status === 'ROLE_SELECTION_REQUIRED') {
        this.toast.info('Please complete your profile to continue.');
      }

      if (token) {
        this.clearSensitiveUrlState();
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

  private parseFragment(fragment: string): URLSearchParams {
    const raw = fragment.startsWith('#') ? fragment.slice(1) : fragment;
    return new URLSearchParams(raw);
  }

  private clearSensitiveUrlState(): void {
    const cleanUrl = window.location.pathname + window.location.search;
    window.history.replaceState(null, document.title, cleanUrl);
  }

  redirectToLogin(): void {
    this.router.navigate(['/auth/login']);
  }
}
