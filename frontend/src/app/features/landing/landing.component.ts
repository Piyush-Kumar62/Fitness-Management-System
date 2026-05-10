import { Component } from '@angular/core';

import { LandingNavbarComponent } from './components/landing-navbar.component';
import { HeroSectionComponent } from './components/hero-section.component';
import { AboutSectionComponent } from './components/about-section.component';
import { TrustSectionComponent } from './components/trust-section.component';
import { HowItWorksSectionComponent } from './components/how-it-works-section.component';
import { RolesSectionComponent } from './components/roles-section.component';
import { DemoSectionComponent } from './components/demo-section.component';
import { PricingSectionComponent } from './components/pricing-section.component';
import { TestimonialsSectionComponent } from './components/testimonials-section.component';
import { CtaSectionComponent } from './components/cta-section.component';
import { FaqSectionComponent } from './components/faq-section.component';
import { ContactSectionComponent } from './components/contact-section.component';
import { FeaturedGymsSectionComponent } from './components/featured-gyms-section.component';
import { LandingFooterComponent } from './components/landing-footer.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    LandingNavbarComponent,
    HeroSectionComponent,
    AboutSectionComponent,
    TrustSectionComponent,
    HowItWorksSectionComponent,
    FeaturedGymsSectionComponent,
    RolesSectionComponent,
    DemoSectionComponent,
    PricingSectionComponent,
    TestimonialsSectionComponent,
    CtaSectionComponent,
    FaqSectionComponent,
    ContactSectionComponent,
    LandingFooterComponent
],
  templateUrl: './landing.component.html',
})
export class LandingComponent {}
