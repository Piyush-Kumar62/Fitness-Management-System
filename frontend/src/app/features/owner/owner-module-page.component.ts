import { Component, Input } from '@angular/core';


@Component({
  selector: 'app-owner-module-page',
  standalone: true,
  imports: [],
  templateUrl: './owner-module-page.component.html',
})
export class OwnerModulePageComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) description!: string;
}
