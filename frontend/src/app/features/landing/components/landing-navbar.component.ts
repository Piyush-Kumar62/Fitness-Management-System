import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LANDING_CONTENT } from '../landing-content';

@Component({
  selector: 'app-landing-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing-navbar.component.html',
})
export class LandingNavbarComponent {
  router = inject(Router);
  mobileMenuOpen = false;
  readonly content = LANDING_CONTENT;

  scrollTo(id: string): void {
    this.mobileMenuOpen = false;
    const section = document.getElementById(id);
    if (section) {
      section.scrollIntoView({ behavior: 'smooth' });
      return;
    }

    this.router.navigate(['/']).then(() => {
      setTimeout(() => document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }), 100);
    });
  }
}
