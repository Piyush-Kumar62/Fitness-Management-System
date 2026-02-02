import { Injectable, inject, signal, computed, effect, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError, BehaviorSubject, timer, of } from 'rxjs';
import { catchError, tap, switchMap, filter, take } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { User, UserRole } from '../models/user.model';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  TokenRefreshResponse,
} from '../models/auth.model';
import { StorageService, StorageKey } from './storage.service';
import { ToastService } from './toast.service';
import { JwtUtil } from '../utils/jwt.util';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private storage = inject(StorageService);
  private toast = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  private baseUrl = environment.apiUrl + '/auth';

  // Reactive state using signals
  private userSignal = signal<User | null>(this.storage.get<User>(StorageKey.USER));
  private isAuthenticatedSignal = signal<boolean>(this.hasValidToken());

  // Public computed signals
  user = computed(() => this.userSignal());
  isAuthenticated = computed(() => this.isAuthenticatedSignal());
  userRole = computed(() => this.userSignal()?.role || null);
  isAdmin = computed(() => this.userRole() === UserRole.ADMIN);
  isUser = computed(() => this.userRole() === UserRole.USER);

  // Token refresh management
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);
  private isRefreshing = false;

  constructor() {
    // Auto-refresh token before expiry
    this.setupTokenRefresh();

    // Sync user state across tabs
    this.syncAuthState();
  }

  /**
   * Login user
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap((response) => {
        this.handleAuthSuccess(response);
        this.toast.success('Login successful!');
      }),
      catchError(this.handleAuthError.bind(this)),
    );
  }

  /**
   * Register new user
   */
  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, userData).pipe(
      tap((response) => {
        this.handleAuthSuccess(response);
        this.toast.success('Registration successful!');
      }),
      catchError(this.handleAuthError.bind(this)),
    );
  }

  /**
   * Login with Google OAuth2
   */
  loginWithGoogle(): void {
    const backendUrl = environment.apiUrl.replace('/api', '');
    window.location.href = `${backendUrl}/oauth2/authorization/google`;
  }

  /**
   * Login with GitHub OAuth2
   */
  loginWithGithub(): void {
    const backendUrl = environment.apiUrl.replace('/api', '');
    window.location.href = `${backendUrl}/oauth2/authorization/github`;
  }

  /**
   * Handle OAuth2 token after redirect
   */
  handleOAuth2Token(token: string): Observable<User> {
    // Store token
    this.storage.setToken(token);

    // Decode token to get user info
    const payload = JwtUtil.decode(token);
    if (!payload) {
      return throwError(() => new Error('Invalid token'));
    }

    // Fetch full user profile from backend
    return this.http.get<User>(`${environment.apiUrl}/users/profile`).pipe(
      tap((user) => {
        this.storage.set(StorageKey.USER, user);
        this.userSignal.set(user);
        this.isAuthenticatedSignal.set(true);

        // Navigate based on role
        const redirectUrl = user.role === UserRole.ADMIN ? '/admin/dashboard' : '/user/dashboard';
        this.router.navigate([redirectUrl]);
      }),
      catchError((error) => {
        this.clearAuthData();
        return throwError(() => error);
      }),
    );
  }

  /**
   * Logout user
   */
  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/auth/login']);
    this.toast.info('You have been logged out');
  }

  /**
   * Refresh access token
   */
  refreshToken(): Observable<TokenRefreshResponse> {
    const refreshToken = this.storage.get<string>(StorageKey.REFRESH_TOKEN);

    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    if (this.isRefreshing) {
      return this.refreshTokenSubject.pipe(
        filter((token) => token !== null),
        take(1),
        switchMap((token) => of({ token: token! })),
      );
    }

    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);

    return this.http.post<TokenRefreshResponse>(`${this.baseUrl}/refresh`, { refreshToken }).pipe(
      tap((response) => {
        this.storage.setToken(response.token);
        if (response.refreshToken) {
          this.storage.set(StorageKey.REFRESH_TOKEN, response.refreshToken);
        }
        this.isRefreshing = false;
        this.refreshTokenSubject.next(response.token);
      }),
      catchError((error) => {
        this.isRefreshing = false;
        this.logout();
        return throwError(() => error);
      }),
    );
  }

  /**
   * Get current token
   */
  getToken(): string | null {
    return this.storage.getToken();
  }

  /**
   * Check if user has specific role
   */
  hasRole(role: UserRole): boolean {
    return this.userRole() === role;
  }

  /**
   * Check if token is valid
   */
  private hasValidToken(): boolean {
    const token = this.storage.getToken();
    if (!token) return false;

    return !JwtUtil.isExpired(token);
  }

  /**
   * Handle successful authentication
   */
  private handleAuthSuccess(response: AuthResponse): void {
    this.storage.setToken(response.token);

    if (response.refreshToken) {
      this.storage.set(StorageKey.REFRESH_TOKEN, response.refreshToken);
    }

    this.storage.set(StorageKey.USER, response.user);
    this.userSignal.set(response.user);
    this.isAuthenticatedSignal.set(true);

    // Navigate based on role
    const redirectUrl =
      response.user.role === UserRole.ADMIN ? '/admin/dashboard' : '/user/dashboard';
    this.router.navigate([redirectUrl]);
  }

  /**
   * Update user data
   */
  updateUser(user: User): void {
    this.storage.set(StorageKey.USER, user);
    this.userSignal.set(user);
  }

  /**
   * Clear authentication data
   */
  private clearAuthData(): void {
    this.storage.removeToken();
    this.storage.remove(StorageKey.USER);
    this.userSignal.set(null);
    this.isAuthenticatedSignal.set(false);
  }

  /**
   * Handle authentication errors
   */
  private handleAuthError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred during authentication';

    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.status === 401) {
      errorMessage = 'Invalid credentials';
    } else if (error.status === 403) {
      errorMessage = 'Access forbidden';
    } else if (error.status === 0) {
      errorMessage = 'Unable to connect to server';
    }

    this.toast.error(errorMessage);
    return throwError(() => error);
  }

  /**
   * Setup automatic token refresh
   */
  private setupTokenRefresh(): void {
    effect(() => {
      if (this.isAuthenticated()) {
        const token = this.getToken();
        if (token && JwtUtil.needsRefresh(token, environment.tokenRefreshThreshold)) {
          this.refreshToken().subscribe({
            error: () => console.error('Token refresh failed'),
          });
        }
      }
    });
  }

  /**
   * Sync auth state across browser tabs
   */
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
}
