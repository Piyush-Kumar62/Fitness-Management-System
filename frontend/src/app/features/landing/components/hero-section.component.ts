import { Component, inject } from '@angular/core';

import { Router } from '@angular/router';

import { LandingButtonComponent } from './ui/landing-button.component';
import { StatsCardComponent } from './ui/stats-card.component';
import { ScrollRevealDirective } from '../../../shared/directives/scroll-reveal.directive';
import { TiltDirective } from '../../../shared/directives/tilt.directive';

@Component({
  selector: 'app-hero-section',
  standalone: true,
  imports: [LandingButtonComponent, StatsCardComponent, ScrollRevealDirective, TiltDirective],
  templateUrl: './hero-section.component.html',
})
export class HeroSectionComponent {
  router = inject(Router);
  readonly quickStats = [
    {
      value: 'Daily',
      label: 'Action Plan',
      note: 'Know exactly what to train each day with guided plans.',
    },
    {
      value: 'Easy',
      label: 'Class Booking',
      note: 'Reserve classes quickly and see your schedule in one view.',
    },
    {
      value: 'All-in-One',
      label: 'Progress Tracking',
      note: 'Track workouts, body measurements, and membership status together.',
    },
  ];

  scrollToFeatures(): void {
    document.getElementById('roles')?.scrollIntoView({ behavior: 'smooth' });
  }
}
