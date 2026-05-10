import { Component } from '@angular/core';

import { LANDING_CONTENT } from '../landing-content';
import { ScrollRevealDirective } from '../../../shared/directives/scroll-reveal.directive';
import { SpotlightDirective } from '../../../shared/directives/spotlight.directive';

@Component({
  selector: 'app-about-section',
  standalone: true,
  imports: [ScrollRevealDirective, SpotlightDirective],
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
