import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-about-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about-section.component.html',
})
export class AboutSectionComponent {
  readonly content = LANDING_CONTENT;
  readonly pillars = [
    {
      title: 'Simple Member Dashboard',
      description: 'Access your classes, plans, progress, and account settings without switching tools.',
    },
    {
      title: 'Guided Daily Routines',
      description: 'Receive workout and nutrition plans clearly organized by day and goal.',
    },
    {
      title: 'Secure And Reliable Access',
      description: 'Your account activity and member data are protected with role-based access controls.',
    },
  ];

  readonly highlights = [
    { value: 'Real-Time', label: 'Progress Updates' },
    { value: 'Clear', label: 'Plan Visibility' },
    { value: 'Anytime', label: 'Mobile Access' },
  ];
}
