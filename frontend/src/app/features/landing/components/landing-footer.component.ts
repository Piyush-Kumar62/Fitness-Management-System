import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-landing-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing-footer.component.html',
})
export class LandingFooterComponent {
  readonly content = LANDING_CONTENT;
}
