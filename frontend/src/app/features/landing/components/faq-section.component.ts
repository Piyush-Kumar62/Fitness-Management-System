import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-faq-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq-section.component.html',
})
export class FaqSectionComponent {
  openIndex = signal(0);

  toggle(index: number): void {
    this.openIndex.set(this.openIndex() === index ? -1 : index);
  }

  readonly faqs = [
    {
      question: 'What can I do as a member on this platform?',
      answer:
        'You can book classes, view your workout and diet plans, track progress, manage memberships, and review payment status from one dashboard.',
    },
    {
      question: 'Can I book and manage classes online?',
      answer:
        'Yes. Members can browse available classes, book slots, and keep track of upcoming sessions directly in the app.',
    },
    {
      question: 'Can I see my membership and payment status?',
      answer:
        'Yes. Your active plan, renewal timeline, and payment outcomes are shown in your member account for full transparency.',
    },
    {
      question: 'Will trainers be able to assign and update my plans?',
      answer:
        'Yes. Trainers can assign workout and diet plans and update them as your goals evolve, so your routine stays relevant.',
    },
  ];
}
