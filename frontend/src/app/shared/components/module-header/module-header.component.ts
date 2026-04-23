import { Component, DestroyRef, Input, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/services/auth.service';
import {
  ADMIN_MENU,
  MEMBER_MENU,
  MenuItem,
  OWNER_MENU,
  TRAINER_MENU,
} from '../../../core/config/menu.config';

@Component({
  selector: 'app-module-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section
      class="mb-5 rounded-2xl border border-white/20 bg-[linear-gradient(120deg,rgba(15,23,42,0.68),rgba(30,41,59,0.52))] backdrop-blur-md p-4 md:p-5"
    >
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-3">
        <div class="min-w-0">
          <p class="text-xs uppercase tracking-[0.16em] text-emerald-200/95">{{ roleLabel() }} Module</p>
          <h1 class="text-2xl md:text-3xl font-bold text-white leading-tight mt-1">
            {{ pageTitle() }}
          </h1>
          <p class="text-sm text-slate-200 mt-1">
            {{ pageSubtitle() }}
          </p>
        </div>
        <div
          class="inline-flex items-center gap-2 rounded-xl border border-emerald-200/35 bg-emerald-300/10 px-3 py-2 text-emerald-100 text-sm font-semibold w-fit"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M13 10V3L4 14h7v7l9-11h-7z"
            />
          </svg>
          {{ roleLabel() }}
        </div>
      </div>
    </section>
  `,
})
export class ModuleHeaderComponent {
  @Input() role: 'MEMBER' | 'TRAINER' | 'OWNER' | 'ADMIN' = 'MEMBER';

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly currentUrl = signal(this.router.url);

  constructor() {
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((event) => {
        this.currentUrl.set(event.urlAfterRedirects);
      });
  }

  readonly roleLabel = computed(() => {
    switch (this.role) {
      case 'ADMIN':
        return 'Administrator';
      case 'OWNER':
        return 'Gym Owner';
      case 'TRAINER':
        return 'Trainer';
      case 'MEMBER':
      default:
        return 'Member';
    }
  });

  readonly pageTitle = computed(() => this.getCurrentMenuItem()?.label ?? this.getDefaultTitle());

  readonly pageSubtitle = computed(() => {
    const firstName = this.authService.user()?.firstName || 'there';
    return `Hi ${firstName}, you are currently viewing ${this.pageTitle()}.`;
  });

  private getDefaultTitle(): string {
    switch (this.role) {
      case 'ADMIN':
        return 'Admin Dashboard';
      case 'OWNER':
        return 'Owner Dashboard';
      case 'TRAINER':
        return 'Dashboard';
      case 'MEMBER':
      default:
        return 'Dashboard';
    }
  }

  private getCurrentMenuItem(): MenuItem | undefined {
    const currentPath = this.currentUrl().split('?')[0];
    const menuItems = this.getRoleMenu();

    return menuItems
      .filter((item) => currentPath === item.route || currentPath.startsWith(`${item.route}/`))
      .sort((a, b) => b.route.length - a.route.length)[0];
  }

  private getRoleMenu(): MenuItem[] {
    switch (this.role) {
      case 'ADMIN':
        return ADMIN_MENU;
      case 'OWNER':
        return OWNER_MENU;
      case 'TRAINER':
        return TRAINER_MENU;
      case 'MEMBER':
      default:
        return MEMBER_MENU;
    }
  }
}
