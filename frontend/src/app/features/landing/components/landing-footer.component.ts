import { Component } from '@angular/core';

import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-landing-footer',
  standalone: true,
  imports: [],
  templateUrl: './landing-footer.component.html',
})
export class LandingFooterComponent {
  readonly content = LANDING_CONTENT;
}
