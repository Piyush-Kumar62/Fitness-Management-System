import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-how-it-works-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './how-it-works-section.component.html',
})
export class HowItWorksSectionComponent {
  readonly steps = [
    {
      title: 'Create Your Member Account',
      description: 'Sign up once and set your fitness goals and preferences.',
      outcome: 'Personalized starting point',
    },
    {
      title: 'Choose Membership Plan',
      description: 'Pick the plan that matches your schedule and training needs.',
      outcome: 'Clear access and benefits',
    },
    {
      title: 'Book Classes',
      description: 'Reserve sessions from your class calendar in a few taps.',
      outcome: 'Consistent attendance',
    },
    {
      title: 'Follow Assigned Plans',
      description: 'Access trainer-assigned workout and diet plans from your dashboard.',
      outcome: 'Guided daily routine',
    },
    {
      title: 'Track Progress',
      description: 'Review activity, measurements, and milestones over time.',
      outcome: 'Visible long-term growth',
    },
  ];
}
