import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="admin-dashboard-container">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-6">Admin Dashboard</h1>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <p class="text-gray-600 dark:text-gray-400">Admin dashboard - Coming soon</p>
      </div>
    </div>
  `,
})
export class AdminDashboardComponent {}
