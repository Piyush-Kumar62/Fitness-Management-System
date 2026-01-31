import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
      <!-- Navbar -->
      <app-navbar [role]="'ADMIN'" (toggleSidebar)="onToggleSidebar()"></app-navbar>

      <div class="flex pt-16">
        <!-- Sidebar -->
        <app-sidebar
          [role]="'ADMIN'"
          [isOpen]="isSidebarOpen()"
          (closeSidebar)="onToggleSidebar()"
        ></app-sidebar>

        <!-- Backdrop for mobile -->
        @if (isSidebarOpen()) {
          <div class="fixed inset-0 bg-black/50 z-20 md:hidden" (click)="onToggleSidebar()"></div>
        }

        <!-- Main Content -->
        <main
          class="flex-1 p-4 md:p-6 overflow-auto transition-all duration-300"
          [class.md:ml-280]="isSidebarOpen()"
          [class.md:ml-0]="!isSidebarOpen()"
        >
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
})
export class AdminLayoutComponent {
  isSidebarOpen = signal(true);

  onToggleSidebar(): void {
    this.isSidebarOpen.update((value) => !value);
  }
}
