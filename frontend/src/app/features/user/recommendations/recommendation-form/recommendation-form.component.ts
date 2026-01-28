import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recommendation-form',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="recommendation-form-container">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-6">Generate Recommendation</h1>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <p class="text-gray-600 dark:text-gray-400">Recommendation form - Coming soon</p>
      </div>
    </div>
  `,
})
export class RecommendationFormComponent {}
