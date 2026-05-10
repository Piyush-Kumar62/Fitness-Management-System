import { Component } from '@angular/core';
import { ScrollRevealDirective } from '../../../shared/directives/scroll-reveal.directive';
import { SpotlightDirective } from '../../../shared/directives/spotlight.directive';

@Component({
  selector: 'app-roles-section',
  standalone: true,
  imports: [ScrollRevealDirective, SpotlightDirective],
  templateUrl: './roles-section.component.html',
})
export class RolesSectionComponent {
  readonly roles = [
    {
      title: 'Join & Set Goals',
      description: 'Start with your personal goals so your dashboard adapts to what you want to achieve.',
      benefits: ['Goal-based onboarding', 'Personal profile setup', 'Fitness baseline capture'],
      iconPath: 'M3 10l9-7 9 7v10a1 1 0 01-1 1h-5v-6H9v6H4a1 1 0 01-1-1V10z',
      image:
        'https://images.unsplash.com/photo-1552674605-db6ffd4facb5?auto=format&fit=crop&w=1000&q=80',
    },
    {
      title: 'Train With Structure',
      description: 'Follow your workout and nutrition plans with class schedules in one timeline.',
      benefits: ['Plan visibility', 'Class booking flow', 'Routine adherence support'],
      iconPath: 'M12 14a4 4 0 100-8 4 4 0 000 8zm-7 7a7 7 0 0114 0',
      image:
        'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?auto=format&fit=crop&w=1000&q=80',
    },
    {
      title: 'Track & Improve',
      description: 'See your consistency and progress with clear history across activities and milestones.',
      benefits: ['Measurement history', 'Activity trends', 'Membership and payment clarity'],
      iconPath: 'M8 7a4 4 0 118 0 4 4 0 01-8 0zm-4 14a8 8 0 0116 0',
      image:
        'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=1000&q=80',
    },
  ];
}
