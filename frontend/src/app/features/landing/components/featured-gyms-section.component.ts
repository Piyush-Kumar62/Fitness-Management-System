import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { GymService } from '../../../core/services/gym.service';
import { GymInfo } from '../../../core/models/subscription.model';
import { ScrollRevealDirective } from '../../../shared/directives/scroll-reveal.directive';
import { SpotlightDirective } from '../../../shared/directives/spotlight.directive';
import { TiltDirective } from '../../../shared/directives/tilt.directive';

@Component({
  selector: 'app-featured-gyms-section',
  standalone: true,
  imports: [CommonModule, RouterLink, ScrollRevealDirective, SpotlightDirective, TiltDirective],
  templateUrl: './featured-gyms-section.component.html',
})
export class FeaturedGymsSectionComponent implements OnInit {
  private gymService = inject(GymService);
  private router = inject(Router);

  gyms = signal<GymInfo[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    this.gymService.getPublicGyms().subscribe({
      next: (gyms) => {
        this.gyms.set(gyms.slice(0, 4)); // Show top 4
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  joinGym(gymId: string): void {
    this.router.navigate(['/auth/register'], { queryParams: { gymId } });
  }
}
