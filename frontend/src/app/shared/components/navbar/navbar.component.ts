import {
  Component,
  Input,
  Output,
  EventEmitter,
  inject,
  computed,
  effect,
  HostListener,
  OnInit,
  OnDestroy,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';
import { NotificationService } from '../../../core/services/notification.service';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit, OnDestroy {
  @Input() role: 'MEMBER' | 'TRAINER' | 'OWNER' | 'ADMIN' = 'MEMBER';
  @Input() sidebarOpen = true;
  @Output() toggleSidebar = new EventEmitter<void>();

  authService = inject(AuthService);
  themeService = inject(ThemeService);
  notificationService = inject(NotificationService);
  userService = inject(UserService);
  toastService = inject(ToastService);
  router = inject(Router);

  showProfileMenu = false;
  showNotifications = false;

  profileImageSrc = signal<string | null>(null);
  user = this.authService.user;
  isDarkMode = computed(() => this.themeService.theme() === 'dark');
  notifications = this.notificationService.notifications;
  private imageSub?: Subscription;
  private currentImageObjectUrl: string | null = null;
  private readonly profileImageEffect = effect(() => {
    this.loadProfileImage(this.user()?.profileImageUrl);
  });

  // Close dropdowns when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;

    // Check if click is outside notifications
    if (this.showNotifications) {
      const notificationsBtn = target.closest('.notifications-btn');
      const notificationsDropdown = target.closest('.notifications-dropdown');

      if (!notificationsBtn && !notificationsDropdown) {
        this.showNotifications = false;
      }
    }

    // Check if click is outside profile menu
    if (this.showProfileMenu) {
      const profileBtn = target.closest('.profile-btn');
      const profileDropdown = target.closest('.profile-dropdown');

      if (!profileBtn && !profileDropdown) {
        this.showProfileMenu = false;
      }
    }
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  backToHome(): void {
    this.router.navigate(['/']);
  }

  onToggleTheme(): void {
    this.themeService.toggleTheme();
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    this.showProfileMenu = false;
  }

  navigateToProfile(): void {
    const route =
      this.role === 'ADMIN' ? '/admin/profile' : `/${this.role.toLowerCase()}/profile`;
    this.router.navigate([route]);
    this.showProfileMenu = false;
  }

  async onLogout(): Promise<void> {
    const confirmLogout = await this.toastService.confirm(
      'Logout now?',
      'Are you sure you want to logout from your account?',
      'Logout',
      'Cancel',
    );
    if (!confirmLogout) return;

    this.notificationService.disconnect();
    this.authService.logout();
    this.showProfileMenu = false;
  }

  getInitials(firstName?: string, lastName?: string): string {
    const first = firstName?.charAt(0) || '';
    const last = lastName?.charAt(0) || '';
    return (first + last).toUpperCase() || 'U';
  }

  ngOnInit(): void {
    this.notificationService.connect();
  }

  ngOnDestroy(): void {
    this.imageSub?.unsubscribe();
    this.revokeCurrentObjectUrl();
    this.notificationService.disconnect();
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
    this.showNotifications = false;
  }

  private loadProfileImage(profileImageUrl?: string): void {
    this.imageSub?.unsubscribe();
    this.revokeCurrentObjectUrl();

    this.imageSub = this.userService.getProfileImageBlobUrl(profileImageUrl).subscribe((blobUrl) => {
      if (!blobUrl) {
        this.profileImageSrc.set(null);
        return;
      }

      this.currentImageObjectUrl = blobUrl;
      this.profileImageSrc.set(blobUrl);
    });
  }

  private revokeCurrentObjectUrl(): void {
    if (this.currentImageObjectUrl) {
      URL.revokeObjectURL(this.currentImageObjectUrl);
      this.currentImageObjectUrl = null;
    }
  }
}
