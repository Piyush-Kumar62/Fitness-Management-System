import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LANDING_CONTENT } from '../landing-content';
import { ScrollRevealDirective } from '../../../shared/directives/scroll-reveal.directive';
import { SpotlightDirective } from '../../../shared/directives/spotlight.directive';
import { TiltDirective } from '../../../shared/directives/tilt.directive';

@Component({
  selector: 'app-pricing-section',
  standalone: true,
  imports: [CommonModule, ScrollRevealDirective, SpotlightDirective, TiltDirective],
  templateUrl: './pricing-section.component.html',
})
export class PricingSectionComponent {
  router = inject(Router);
  readonly content = LANDING_CONTENT;
}
