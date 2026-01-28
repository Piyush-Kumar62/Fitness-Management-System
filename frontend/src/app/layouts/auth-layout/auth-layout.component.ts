import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `
    <div
      class="auth-layout min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 py-12 px-4 sm:px-6 lg:px-8"
    >
      <div class="auth-container max-w-md w-full space-y-8">
        <!-- Logo & Branding -->
        <div class="text-center">
          <h1 class="text-4xl font-bold text-indigo-600 dark:text-indigo-400">
            Fitness Management
          </h1>
          <p class="mt-2 text-gray-600 dark:text-gray-400">Track your fitness journey</p>
        </div>

        <!-- Auth Form Content -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8">
          <router-outlet></router-outlet>
        </div>
      </div>
    </div>
  `,
})
export class AuthLayoutComponent {}
