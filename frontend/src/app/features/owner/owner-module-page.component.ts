import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-owner-module-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-module-page.component.html',
})
export class OwnerModulePageComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) description!: string;
}
