import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900">
      <!-- Navigation -->
      <nav class="fixed w-full bg-white/10 backdrop-blur-md z-50 border-b border-white/20">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between items-center h-16">
            <div class="flex items-center space-x-3">
              <svg
                class="w-10 h-10 text-white"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
              <span class="text-2xl font-bold text-white">Fitness Management System</span>
            </div>
            <div class="flex space-x-4">
              <button
                (click)="navigateToLogin()"
                class="px-6 py-2 text-white border-2 border-white rounded-lg hover:bg-white hover:text-indigo-900 transition duration-300"
              >
                Login
              </button>
              <button
                (click)="navigateToRegister()"
                class="px-6 py-2 bg-white text-indigo-900 rounded-lg hover:bg-gray-100 transition duration-300 font-semibold"
              >
                Get Started
              </button>
            </div>
          </div>
        </div>
      </nav>

      <!-- Hero Section -->
      <section class="pt-32 pb-20 px-4 sm:px-6 lg:px-8">
        <div class="max-w-7xl mx-auto">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <!-- Left Content -->
            <div class="text-white space-y-8">
              <h1 class="text-5xl sm:text-6xl font-extrabold leading-tight">
                Transform Your
                <span
                  class="text-transparent bg-clip-text bg-gradient-to-r from-pink-400 to-purple-400"
                >
                  Fitness Journey
                </span>
              </h1>
              <p class="text-xl text-gray-300">
                Track your activities, get AI-powered recommendations, and achieve your fitness
                goals with our comprehensive management system.
              </p>
              <div class="flex flex-col sm:flex-row gap-4">
                <button
                  (click)="navigateToRegister()"
                  class="px-8 py-4 bg-gradient-to-r from-pink-500 to-purple-500 text-white rounded-xl
                         hover:from-pink-600 hover:to-purple-600 transition duration-300 font-semibold text-lg
                         shadow-xl hover:shadow-2xl transform hover:scale-105"
                >
                  Start Free Today
                </button>
                <button
                  (click)="scrollToFeatures()"
                  class="px-8 py-4 bg-white/10 backdrop-blur-sm text-white rounded-xl border-2 border-white/20
                         hover:bg-white/20 transition duration-300 font-semibold text-lg"
                >
                  Learn More
                </button>
              </div>

              <!-- Stats -->
              <div class="grid grid-cols-3 gap-8 pt-8">
                <div>
                  <div class="text-4xl font-bold text-white">10K+</div>
                  <div class="text-gray-400">Active Users</div>
                </div>
                <div>
                  <div class="text-4xl font-bold text-white">50K+</div>
                  <div class="text-gray-400">Workouts Tracked</div>
                </div>
                <div>
                  <div class="text-4xl font-bold text-white">95%</div>
                  <div class="text-gray-400">Satisfaction</div>
                </div>
              </div>
            </div>

            <!-- Right Image/Illustration -->
            <div class="relative">
              <div
                class="relative z-10 bg-gradient-to-br from-purple-500/20 to-pink-500/20 backdrop-blur-sm
                          rounded-3xl p-8 border border-white/20 shadow-2xl"
              >
                <svg
                  class="w-full h-full text-white opacity-80"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="1.5"
                    d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                  />
                </svg>
              </div>
              <!-- Decorative elements -->
              <div
                class="absolute -top-4 -right-4 w-72 h-72 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"
              ></div>
              <div
                class="absolute -bottom-4 -left-4 w-72 h-72 bg-pink-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"
                style="animation-delay: 1s;"
              ></div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section id="features" class="py-20 px-4 sm:px-6 lg:px-8 bg-white/5 backdrop-blur-sm">
        <div class="max-w-7xl mx-auto">
          <div class="text-center mb-16">
            <h2 class="text-4xl font-bold text-white mb-4">Powerful Features</h2>
            <p class="text-xl text-gray-300">
              Everything you need to track and improve your fitness
            </p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <!-- Feature 1 -->
            <div
              class="bg-white/10 backdrop-blur-sm rounded-2xl p-8 border border-white/20 hover:bg-white/20 transition duration-300 transform hover:scale-105"
            >
              <div
                class="w-16 h-16 bg-gradient-to-br from-pink-500 to-purple-500 rounded-xl flex items-center justify-center mb-6"
              >
                <svg
                  class="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 10V3L4 14h7v7l9-11h-7z"
                  />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">Activity Tracking</h3>
              <p class="text-gray-300">
                Track running, cycling, swimming, yoga, and more. Log duration, calories, and custom
                metrics.
              </p>
            </div>

            <!-- Feature 2 -->
            <div
              class="bg-white/10 backdrop-blur-sm rounded-2xl p-8 border border-white/20 hover:bg-white/20 transition duration-300 transform hover:scale-105"
            >
              <div
                class="w-16 h-16 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center mb-6"
              >
                <svg
                  class="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"
                  />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">AI Recommendations</h3>
              <p class="text-gray-300">
                Get personalized workout suggestions, improvements, and safety tips powered by AI.
              </p>
            </div>

            <!-- Feature 3 -->
            <div
              class="bg-white/10 backdrop-blur-sm rounded-2xl p-8 border border-white/20 hover:bg-white/20 transition duration-300 transform hover:scale-105"
            >
              <div
                class="w-16 h-16 bg-gradient-to-br from-green-500 to-emerald-500 rounded-xl flex items-center justify-center mb-6"
              >
                <svg
                  class="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                  />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">Progress Analytics</h3>
              <p class="text-gray-300">
                Visualize your progress with detailed charts and statistics to stay motivated.
              </p>
            </div>

            <!-- Feature 4 -->
            <div
              class="bg-white/10 backdrop-blur-sm rounded-2xl p-8 border border-white/20 hover:bg-white/20 transition duration-300 transform hover:scale-105"
            >
              <div
                class="w-16 h-16 bg-gradient-to-br from-yellow-500 to-orange-500 rounded-xl flex items-center justify-center mb-6"
              >
                <svg
                  class="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z"
                  />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">BMI Calculator</h3>
              <p class="text-gray-300">
                Calculate and track your Body Mass Index to monitor your health metrics over time.
              </p>
            </div>

            <!-- Feature 5 -->
            <div
              class="bg-white/10 backdrop-blur-sm rounded-2xl p-8 border border-white/20 hover:bg-white/20 transition duration-300 transform hover:scale-105"
            >
              <div
                class="w-16 h-16 bg-gradient-to-br from-red-500 to-pink-500 rounded-xl flex items-center justify-center mb-6"
              >
                <svg
                  class="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">User Profiles</h3>
              <p class="text-gray-300">
                Manage your personal information, preferences, and track your fitness journey.
              </p>
            </div>

            <!-- Feature 6 -->
            <div
              class="bg-white/10 backdrop-blur-sm rounded-2xl p-8 border border-white/20 hover:bg-white/20 transition duration-300 transform hover:scale-105"
            >
              <div
                class="w-16 h-16 bg-gradient-to-br from-indigo-500 to-purple-500 rounded-xl flex items-center justify-center mb-6"
              >
                <svg
                  class="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
                  />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">Dark Mode</h3>
              <p class="text-gray-300">
                Easy on the eyes with beautiful dark mode support for comfortable viewing.
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="py-20 px-4 sm:px-6 lg:px-8">
        <div class="max-w-4xl mx-auto text-center">
          <h2 class="text-4xl sm:text-5xl font-bold text-white mb-6">
            Ready to Start Your Fitness Journey?
          </h2>
          <p class="text-xl text-gray-300 mb-8">
            Join thousands of users who are already achieving their fitness goals.
          </p>
          <button
            (click)="navigateToRegister()"
            class="px-12 py-5 bg-gradient-to-r from-pink-500 to-purple-500 text-white rounded-xl
                   hover:from-pink-600 hover:to-purple-600 transition duration-300 font-bold text-xl
                   shadow-2xl hover:shadow-3xl transform hover:scale-105"
          >
            Get Started for Free
          </button>
        </div>
      </section>

      <!-- Footer -->
      <footer class="border-t border-white/20 bg-white/5 backdrop-blur-sm py-8">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div
            class="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0"
          >
            <div class="flex items-center space-x-3">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
              <span class="text-white font-semibold">Fitness Management System</span>
            </div>
            <div class="text-gray-400">© 2026 Fitness Management System. All rights reserved.</div>
            <div class="flex space-x-6">
              <a
                href="https://github.com/Piyush-Kumar62"
                target="_blank"
                class="text-gray-400 hover:text-white transition"
              >
                GitHub
              </a>
              <a
                href="https://www.linkedin.com/in/piyush-kumar62/"
                target="_blank"
                class="text-gray-400 hover:text-white transition"
              >
                LinkedIn
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  `,
  styles: [
    `
      @keyframes pulse {
        0%,
        100% {
          opacity: 0.2;
        }
        50% {
          opacity: 0.3;
        }
      }
    `,
  ],
})
export class LandingComponent {
  constructor(private router: Router) {}

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/auth/register']);
  }

  scrollToFeatures(): void {
    const element = document.getElementById('features');
    element?.scrollIntoView({ behavior: 'smooth' });
  }
}
