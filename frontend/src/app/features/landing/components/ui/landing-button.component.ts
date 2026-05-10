import { Component, Input, Output, EventEmitter } from '@angular/core';


export type ButtonVariant = 'primary' | 'secondary' | 'outline';

@Component({
  selector: 'app-landing-button',
  standalone: true,
  imports: [],
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
          'border border-slate-200 dark:border-emerald-100/25 bg-slate-100 dark:bg-white/10 hover:bg-slate-200 dark:hover:bg-emerald-300/15 text-slate-800 dark:text-white hover:border-slate-300 dark:hover:border-emerald-200/50 backdrop-blur-sm hover:-translate-y-0.5';
        break;
      case 'outline':
        variantClass =
          'text-slate-600 dark:text-slate-200 hover:text-slate-800 dark:hover:text-white bg-transparent hover:bg-slate-100 dark:hover:bg-emerald-300/10 border border-slate-300 dark:border-white/20 hover:border-slate-400 dark:hover:border-emerald-200/55 hover:-translate-y-0.5';
        break;
    }

    return `${baseClass} ${variantClass} ${this.customClass}`.trim();
  }
}
