import {
  Component,
  Input,
  Output,
  EventEmitter,
  inject,
  computed,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  @Input() role: 'USER' | 'ADMIN' = 'USER';
  @Output() toggleSidebar = new EventEmitter<void>();

  authService = inject(AuthService);
  themeService = inject(ThemeService);
  router = inject(Router);

  showProfileMenu = false;
  showNotifications = false;

  user = this.authService.user;
  isDarkMode = computed(() => this.themeService.theme() === 'dark');

  /**
   * Close dropdowns when clicking outside
   */
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

  onToggleTheme(): void {
    this.themeService.toggleTheme();
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
    this.showNotifications = false;
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    this.showProfileMenu = false;
  }

  navigateToProfile(): void {
    const route = this.role === 'ADMIN' ? '/user/profile' : '/user/profile';
    this.router.navigate([route]);
    this.showProfileMenu = false;
  }

  onLogout(): void {
    this.authService.logout();
    this.showProfileMenu = false;
  }

  getInitials(firstName?: string, lastName?: string): string {
    const first = firstName?.charAt(0) || '';
    const last = lastName?.charAt(0) || '';
    return (first + last).toUpperCase() || 'U';
  }
}
