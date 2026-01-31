import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface BMIResult {
  bmi: number;
  category: string;
  color: string;
  healthStatus: string;
  recommendations: string[];
}

@Component({
  selector: 'app-bmi-calculator',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bmi-calculator-container max-w-4xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-6">BMI Calculator</h1>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Calculator Card -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-4">
            Calculate Your BMI
          </h2>

          <form (ngSubmit)="calculateBMI()" class="space-y-4">
            <!-- Unit Selection -->
            <div class="flex space-x-4 mb-4">
              <label class="flex items-center cursor-pointer">
                <input
                  type="radio"
                  [(ngModel)]="unit"
                  name="unit"
                  value="metric"
                  class="mr-2 text-indigo-600 focus:ring-indigo-500"
                />
                <span class="text-gray-700 dark:text-gray-300">Metric (kg, cm)</span>
              </label>
              <label class="flex items-center cursor-pointer">
                <input
                  type="radio"
                  [(ngModel)]="unit"
                  name="unit"
                  value="imperial"
                  class="mr-2 text-indigo-600 focus:ring-indigo-500"
                />
                <span class="text-gray-700 dark:text-gray-300">Imperial (lbs, in)</span>
              </label>
            </div>

            @if (unit === 'metric') {
              <!-- Metric Units -->
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Weight (kg)
                </label>
                <input
                  type="number"
                  [(ngModel)]="weight"
                  name="weight"
                  min="1"
                  max="500"
                  step="0.1"
                  required
                  class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="Enter weight in kg"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Height (cm)
                </label>
                <input
                  type="number"
                  [(ngModel)]="height"
                  name="height"
                  min="50"
                  max="300"
                  step="0.1"
                  required
                  class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="Enter height in cm"
                />
              </div>
            } @else {
              <!-- Imperial Units -->
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Weight (lbs)
                </label>
                <input
                  type="number"
                  [(ngModel)]="weight"
                  name="weight"
                  min="1"
                  max="1100"
                  step="0.1"
                  required
                  class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="Enter weight in lbs"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Height (inches)
                </label>
                <input
                  type="number"
                  [(ngModel)]="height"
                  name="height"
                  min="20"
                  max="120"
                  step="0.1"
                  required
                  class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="Enter height in inches"
                />
              </div>
            }

            <button
              type="submit"
              class="w-full bg-indigo-600 text-white py-3 rounded-lg hover:bg-indigo-700 transition font-semibold"
            >
              Calculate BMI
            </button>
          </form>
        </div>

        <!-- Results Card -->
        @if (result()) {
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
            <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-4">Your Results</h2>

            <div class="text-center mb-6">
              <div
                class="inline-flex items-center justify-center w-32 h-32 rounded-full mb-4"
                [ngStyle]="{
                  'background-color': result()!.color + '20',
                  border: '4px solid ' + result()!.color,
                }"
              >
                <div class="text-4xl font-bold" [ngStyle]="{ color: result()!.color }">
                  {{ result()!.bmi }}
                </div>
              </div>
              <h3 class="text-2xl font-bold mb-2" [ngStyle]="{ color: result()!.color }">
                {{ result()!.category }}
              </h3>
              <p class="text-gray-600 dark:text-gray-400">{{ result()!.healthStatus }}</p>
            </div>

            <div class="space-y-3">
              <h4 class="font-semibold text-gray-900 dark:text-white">Recommendations:</h4>
              <ul class="space-y-2">
                @for (rec of result()!.recommendations; track rec) {
                  <li class="flex items-start">
                    <svg
                      class="w-5 h-5 text-green-500 mr-2 mt-0.5 flex-shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M5 13l4 4L19 7"
                      ></path>
                    </svg>
                    <span class="text-gray-700 dark:text-gray-300">{{ rec }}</span>
                  </li>
                }
              </ul>
            </div>
          </div>
        }
      </div>

      <!-- BMI Chart Reference -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6 mt-6">
        <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-4">BMI Categories</h2>
        <div class="space-y-3">
          <div
            class="flex items-center justify-between p-3 rounded-lg bg-blue-50 dark:bg-blue-900/20"
          >
            <span class="font-medium text-blue-900 dark:text-blue-300">Underweight</span>
            <span class="text-blue-700 dark:text-blue-400">BMI &lt; 18.5</span>
          </div>
          <div
            class="flex items-center justify-between p-3 rounded-lg bg-green-50 dark:bg-green-900/20"
          >
            <span class="font-medium text-green-900 dark:text-green-300">Normal weight</span>
            <span class="text-green-700 dark:text-green-400">BMI 18.5 - 24.9</span>
          </div>
          <div
            class="flex items-center justify-between p-3 rounded-lg bg-yellow-50 dark:bg-yellow-900/20"
          >
            <span class="font-medium text-yellow-900 dark:text-yellow-300">Overweight</span>
            <span class="text-yellow-700 dark:text-yellow-400">BMI 25 - 29.9</span>
          </div>
          <div
            class="flex items-center justify-between p-3 rounded-lg bg-red-50 dark:bg-red-900/20"
          >
            <span class="font-medium text-red-900 dark:text-red-300">Obese</span>
            <span class="text-red-700 dark:text-red-400">BMI ≥ 30</span>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class BmiCalculatorComponent {
  unit = 'metric';
  weight: number | null = null;
  height: number | null = null;
  result = signal<BMIResult | null>(null);

  calculateBMI() {
    if (!this.weight || !this.height) {
      return;
    }

    let bmi: number;

    if (this.unit === 'metric') {
      // BMI = weight (kg) / (height (m))^2
      const heightInMeters = this.height / 100;
      bmi = this.weight / (heightInMeters * heightInMeters);
    } else {
      // BMI = (weight (lbs) / (height (in))^2) × 703
      bmi = (this.weight / (this.height * this.height)) * 703;
    }

    bmi = Math.round(bmi * 10) / 10;

    let category: string;
    let color: string;
    let healthStatus: string;
    let recommendations: string[];

    if (bmi < 18.5) {
      category = 'Underweight';
      color = '#3B82F6';
      healthStatus = 'You may need to gain weight';
      recommendations = [
        'Increase your calorie intake with nutrient-rich foods',
        'Include more protein in your diet',
        'Consider strength training exercises',
        'Consult a nutritionist for a personalized plan',
      ];
    } else if (bmi < 25) {
      category = 'Normal Weight';
      color = '#10B981';
      healthStatus = 'You are at a healthy weight';
      recommendations = [
        'Maintain your current healthy lifestyle',
        'Continue regular physical activity',
        'Eat a balanced diet',
        'Stay hydrated and get enough sleep',
      ];
    } else if (bmi < 30) {
      category = 'Overweight';
      color = '#F59E0B';
      healthStatus = 'You may benefit from losing weight';
      recommendations = [
        'Increase physical activity to 150+ minutes per week',
        'Focus on portion control',
        'Choose whole foods over processed foods',
        'Consider consulting a healthcare provider',
      ];
    } else {
      category = 'Obese';
      color = '#EF4444';
      healthStatus = 'Consult a healthcare provider';
      recommendations = [
        'Seek professional medical advice',
        'Create a structured weight loss plan',
        'Start with low-impact exercises',
        'Monitor your progress regularly',
        'Consider joining a support group',
      ];
    }

    this.result.set({
      bmi,
      category,
      color,
      healthStatus,
      recommendations,
    });
  }
}
