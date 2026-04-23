import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-pricing-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pricing-section.component.html',
})
export class PricingSectionComponent {
  router = inject(Router);
  readonly content = LANDING_CONTENT;
}
