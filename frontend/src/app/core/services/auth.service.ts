import { Injectable, inject, signal, computed, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { User, UserRole } from '../models/user.model';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/auth.model';
import { StorageService, StorageKey } from './storage.service';
import { ToastService } from './toast.service';
import { JwtUtil } from '../utils/jwt.util';
import { ApiResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private storage = inject(StorageService);
  private toast = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  private baseUrl = environment.apiUrl + '/auth';
  private backendBaseUrl = environment.apiUrl.replace(/\/api(?:\/v\d+)?$/, '');

  // Reactive state using signals
  private userSignal = signal<User | null>(this.storage.get<User>(StorageKey.USER));
  private isAuthenticatedSignal = signal<boolean>(this.hasValidToken());

  // Public computed signals
  user = computed(() => this.userSignal());
  isAuthenticated = computed(() => this.isAuthenticatedSignal());
  userRole = computed(() => this.userSignal()?.role || null);
  isAdmin = computed(() => this.userRole() === UserRole.ADMIN);
  isTrainer = computed(() => this.userRole() === UserRole.TRAINER);
  isOwner = computed(() => this.userRole() === UserRole.OWNER);
  isMember = computed(() => this.userRole() === UserRole.MEMBER);

  constructor() {
    // Sync user state across tabs
    this.syncAuthState();
  }

  // Login user
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse | ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, credentials)
      .pipe(
      map((response) => this.unwrapResponse<AuthResponse>(response)),
      tap((response) => {
        this.handleAuthSuccess(response);
        this.toast.success('Login successful!');
      }),
      catchError(this.handleAuthError.bind(this)),
    );
  }

  // Register new user
  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse | ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, userData)
      .pipe(
      map((response) => this.unwrapResponse<AuthResponse>(response)),
      tap((response) => {
        this.handleAuthSuccess(response);
        this.toast.success('Registration successful!');
      }),
      catchError(this.handleAuthError.bind(this)),
    );
  }

  // Login with Google OAuth2
  loginWithGoogle(): void {
    window.location.href = `${this.backendBaseUrl}/oauth2/authorization/google`;
  }

  // Login with GitHub OAuth2
  loginWithGithub(): void {
    window.location.href = `${this.backendBaseUrl}/oauth2/authorization/github`;
  }

  // Handle OAuth2 token after redirect
  handleOAuth2Token(token: string): Observable<User> {
    // Store token
    this.storage.setToken(token);

    // Decode token to get user info
    const payload = JwtUtil.decode(token);
    if (!payload) {
      return throwError(() => new Error('Invalid token'));
    }

    // Fetch full user profile from backend
    return this.http
      .get<User | ApiResponse<User>>(`${environment.apiUrl}/users/profile`)
      .pipe(
      map((response) => this.unwrapResponse<User>(response)),
      tap((user) => {
        this.storage.set(StorageKey.USER, user);
        this.userSignal.set(user);
        this.isAuthenticatedSignal.set(true);

        // Navigate based on role
        const redirectUrl = this.getRedirectUrl(user.role);
        this.router.navigate([redirectUrl]);
      }),
      catchError((error) => {
        this.clearAuthData();
        return throwError(() => error);
      }),
    );
  }

  // Logout user
  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/auth/login']);
    this.toast.info('You have been logged out');
  }

  // Get current token
  getToken(): string | null {
    return this.storage.getToken();
  }

  // Check if user has specific role
  hasRole(role: UserRole): boolean {
    return this.userRole() === role;
  }

  // Check if token is valid
  private hasValidToken(): boolean {
    const token = this.storage.getToken();
    if (!token) return false;

    return !JwtUtil.isExpired(token);
  }

  // Handle successful authentication
  private handleAuthSuccess(response: AuthResponse): void {
    this.storage.setToken(response.token);

    this.storage.set(StorageKey.USER, response.user);
    this.userSignal.set(response.user);
    this.isAuthenticatedSignal.set(true);

    // Navigate based on role
    const redirectUrl = this.getRedirectUrl(response.user.role);
    this.router.navigate([redirectUrl]);
  }

  // Update user data
  updateUser(user: User): void {
    this.storage.set(StorageKey.USER, user);
    this.userSignal.set(user);
  }

  // Get redirect URL based on user role
  getRedirectUrl(role: UserRole): string {
    switch (role) {
      case UserRole.ADMIN:
        return '/admin/dashboard';
      case UserRole.TRAINER:
        return '/trainer/dashboard';
      case UserRole.OWNER:
        return '/owner/dashboard';
      case UserRole.MEMBER:
      default:
        return '/member/dashboard';
    }
  }

  // Clear authentication data
  private clearAuthData(): void {
    this.storage.removeToken();
    this.storage.remove(StorageKey.USER);
    this.userSignal.set(null);
    this.isAuthenticatedSignal.set(false);
  }

  // Handle authentication errors
  private handleAuthError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred during authentication';

    if (error.error) {
      if (typeof error.error === 'string') {
        try {
          const parsed = JSON.parse(error.error);
          errorMessage = parsed.message || parsed.error || error.error;
        } catch (e) {
          errorMessage = error.error;
        }
      } else if (error.error.message) {
        errorMessage = error.error.message;
      } else if (error.error.error) {
        errorMessage = error.error.error;
      }
    } else if (error.status === 401) {
      errorMessage = 'Invalid credentials provided. Please check your email and password.';
    } else if (error.status === 403) {
      errorMessage = 'You do not have permission to access this resource.';
    } else if (error.status === 0) {
      errorMessage = 'Unable to connect to the server. Please ensure the backend is running.';
    }

    this.toast.error(errorMessage, 'Authentication Failed');
    return throwError(() => error);
  }

  // Sync auth state across browser tabs
  private syncAuthState(): void {
    if (!this.isBrowser) return;

    window.addEventListener('storage', (event) => {
      if (event.key === StorageKey.ACCESS_TOKEN) {
        if (!event.newValue) {
          this.clearAuthData();
          this.router.navigate(['/auth/login']);
        } else {
          const user = this.storage.get<User>(StorageKey.USER);
          if (user) {
            this.userSignal.set(user);
            this.isAuthenticatedSignal.set(true);
          }
        }
      }
    });
  }

  private unwrapResponse<T>(response: T | ApiResponse<T>): T {
    if (
      response &&
      typeof response === 'object' &&
      'success' in (response as Record<string, unknown>)
    ) {
      const envelope = response as ApiResponse<T>;
      return (envelope.data as T) ?? ({} as T);
    }
    return response as T;
  }
}
