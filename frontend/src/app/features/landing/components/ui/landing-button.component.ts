import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ButtonVariant = 'primary' | 'secondary' | 'outline';

@Component({
  selector: 'app-landing-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      [type]="type"
      [class]="getButtonClass()"
      (click)="onClick.emit($event)"
    >
      <ng-content></ng-content>
    </button>
  `
})
export class LandingButtonComponent {
  @Input() variant: ButtonVariant = 'primary';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() customClass = '';
  @Output() onClick = new EventEmitter<MouseEvent>();

  getButtonClass(): string {
    const baseClass =
      'px-5 sm:px-8 py-2.5 sm:py-3.5 text-sm sm:text-base font-semibold whitespace-nowrap transition-all duration-300 inline-flex items-center justify-center rounded-xl';
    
    let variantClass = '';
    switch (this.variant) {
      case 'primary':
        variantClass =
          'text-slate-950 bg-gradient-to-r from-emerald-300 via-cyan-300 to-lime-300 hover:from-emerald-200 hover:via-cyan-200 hover:to-lime-200 shadow-[0_14px_30px_-14px_rgba(16,185,129,0.85)] border border-white/20 hover:-translate-y-0.5';
        break;
      case 'secondary':
        variantClass =
          'border border-emerald-100/25 bg-white/10 hover:bg-emerald-300/15 text-white hover:border-emerald-200/50 backdrop-blur-sm hover:-translate-y-0.5';
        break;
      case 'outline':
        variantClass =
          'text-slate-200 hover:text-white bg-transparent hover:bg-emerald-300/10 border border-white/20 hover:border-emerald-200/55 hover:-translate-y-0.5';
        break;
    }

    return `${baseClass} ${variantClass} ${this.customClass}`.trim();
  }
}
