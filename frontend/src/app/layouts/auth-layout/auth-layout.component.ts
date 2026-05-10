import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { LandingNavbarComponent } from '../../features/landing/components/landing-navbar.component';
import { LANDING_CONTENT } from '../../features/landing/landing-content';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet, LandingNavbarComponent],
  templateUrl: './auth-layout.component.html',
})
export class AuthLayoutComponent {
  readonly content = LANDING_CONTENT;
}
