import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-testimonials-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './testimonials-section.component.html',
})
export class TestimonialsSectionComponent {
  readonly content = LANDING_CONTENT;
}
