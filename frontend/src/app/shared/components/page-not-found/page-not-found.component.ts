import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-page-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div
      class="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8"
    >
      <div class="max-w-md w-full text-center">
        <h1 class="text-9xl font-bold text-indigo-600 dark:text-indigo-400">404</h1>
        <h2 class="mt-4 text-3xl font-bold text-gray-900 dark:text-white">Page Not Found</h2>
        <p class="mt-2 text-gray-600 dark:text-gray-400">
          The page you are looking for doesn't exist or has been moved.
        </p>
        <div class="mt-6">
          <a
            routerLink="/auth/login"
            class="inline-flex items-center px-4 py-2 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            Go Home
          </a>
        </div>
      </div>
    </div>
  `,
})
export class PageNotFoundComponent {}
