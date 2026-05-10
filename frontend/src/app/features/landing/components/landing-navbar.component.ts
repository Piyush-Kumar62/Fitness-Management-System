import { Component, inject } from '@angular/core';

import { Router } from '@angular/router';
import { LANDING_CONTENT } from '../landing-content';

import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-landing-navbar',
  standalone: true,
  imports: [],
  templateUrl: './landing-navbar.component.html',
})
export class LandingNavbarComponent {
  router = inject(Router);
  themeService = inject(ThemeService);
  mobileMenuOpen = false;
  readonly content = LANDING_CONTENT;

  isDarkMode() {
    return this.themeService.theme() === 'dark';
  }

  onToggleTheme() {
    this.themeService.toggleTheme();
  }

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
