import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

// Reusable pagination component. Emits (pageChange) with the zero-based page index when the user clicks a page button.
@Component({
  selector: 'app-paginator',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <nav class="flex items-center justify-between px-2 py-3" *ngIf="totalPages > 1">
      <span class="text-sm text-slate-400">
        Page {{ currentPage + 1 }} of {{ totalPages }}
        &nbsp;·&nbsp;
        <span class="font-medium text-white">{{ totalElements }}</span> results
      </span>
      <div class="flex gap-1">
        <button
          class="paginator-btn"
          [disabled]="isFirst"
          (click)="changePage(currentPage - 1)"
          aria-label="Previous page">
          ‹
        </button>
        <button *ngFor="let p of visiblePages()"
          class="paginator-btn"
          [class.active]="p === currentPage"
          (click)="changePage(p)">
          {{ p + 1 }}
        </button>
        <button
          class="paginator-btn"
          [disabled]="isLast"
          (click)="changePage(currentPage + 1)"
          aria-label="Next page">
          ›
        </button>
      </div>
    </nav>
  `,
  styles: [`
    .paginator-btn {
      @apply min-w-[36px] h-9 px-3 rounded-lg text-sm font-medium
             bg-slate-800 text-slate-300 border border-slate-700
             hover:bg-indigo-600 hover:text-white hover:border-indigo-600
             disabled:opacity-40 disabled:cursor-not-allowed
             transition-colors duration-150;
    }
    .paginator-btn.active {
      @apply bg-indigo-600 text-white border-indigo-600;
    }
  `],
})
export class PaginatorComponent {
  @Input() currentPage = 0;
  @Input() totalPages = 1;
  @Input() totalElements = 0;
  @Input() isFirst = true;
  @Input() isLast = true;

  @Output() pageChange = new EventEmitter<number>();

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.pageChange.emit(page);
    }
  }

  // Returns up to 5 page numbers centered around the current page.
  visiblePages(): number[] {
    const range = 2;
    const start = Math.max(0, this.currentPage - range);
    const end = Math.min(this.totalPages - 1, this.currentPage + range);
    const pages: number[] = [];
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  }
}
