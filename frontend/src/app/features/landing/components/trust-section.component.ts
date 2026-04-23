import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-trust-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trust-section.component.html',
})
export class TrustSectionComponent {
  readonly content = LANDING_CONTENT;
  readonly metrics = [
    { value: 'Live', label: 'Progress Sync', note: 'Workouts, classes, and milestones update directly from your account activity.' },
    { value: 'Verified', label: 'Membership State', note: 'Plan status and renewals are shown from active billing records, not placeholder values.' },
    { value: 'Member-First', label: 'Experience Design', note: 'Every module is optimized for everyday member actions: book, train, track, repeat.' },
    { value: 'Protected', label: 'Secure Access', note: 'Role-based authentication, controlled sessions, and guarded API access for your data.' },
  ];

  readonly brands = [
    'PulseFit Club',
    'Metro Strength',
    'Athletica Pro',
    'Urban Burn',
    'ZenCore Studio',
  ];
}
