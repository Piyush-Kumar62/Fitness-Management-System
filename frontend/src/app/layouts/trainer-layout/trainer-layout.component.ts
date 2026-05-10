import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { ModuleHeaderComponent } from '../../shared/components/module-header/module-header.component';

@Component({
  selector: 'app-trainer-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent, ModuleHeaderComponent],
  templateUrl: './trainer-layout.component.html',
})
export class TrainerLayoutComponent {
  isSidebarOpen = signal(true);

  onToggleSidebar(): void {
    this.isSidebarOpen.update((value) => !value);
  }
}
