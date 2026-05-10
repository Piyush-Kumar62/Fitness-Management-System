import { Component, Input } from '@angular/core';


@Component({
  selector: 'app-stats-card',
  standalone: true,
  imports: [],
  template: `
    <article
      class="group relative h-full min-h-[186px] rounded-2xl border border-slate-200 dark:border-white/15 bg-white/60 dark:bg-[linear-gradient(180deg,rgba(15,23,42,0.38)_0%,rgba(30,41,59,0.28)_100%)] p-4 sm:p-5 backdrop-blur-xl transition-all duration-300 hover:-translate-y-1 hover:border-emerald-500/45 dark:hover:border-emerald-300/45 hover:shadow-[0_20px_35px_-20px_rgba(16,185,129,0.7)]"
    >
      <div
        class="mb-3 h-1.5 w-12 rounded-full bg-gradient-to-r from-emerald-300 via-cyan-300 to-lime-300 opacity-80 transition-all duration-300 group-hover:w-16 group-hover:opacity-100"
      ></div>
      <p class="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white leading-tight tracking-tight whitespace-nowrap">
        {{ value }}
      </p>
      <p class="mt-2 text-[0.7rem] sm:text-xs text-emerald-600 dark:text-emerald-100/95 tracking-[0.13em] uppercase font-semibold">
        {{ label }}
      </p>
      @if (note) {
        <p class="mt-3 text-sm text-slate-600 dark:text-slate-200 leading-relaxed">
          {{ note }}
        </p>
      }
    </article>
  `
})
export class StatsCardComponent {
  @Input({ required: true }) value!: string;
  @Input({ required: true }) label!: string;
  @Input() note = '';
}
