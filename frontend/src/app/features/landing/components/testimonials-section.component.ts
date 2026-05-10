import { Component } from '@angular/core';

import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-testimonials-section',
  standalone: true,
  imports: [],
  templateUrl: './testimonials-section.component.html',
})
export class TestimonialsSectionComponent {
  readonly content = LANDING_CONTENT;
}
