import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';
import { CustomCursorComponent } from './shared/components/custom-cursor/custom-cursor.component';
import { ThemeService } from './core/services/theme.service';
import { WebSocketService } from './core/services/websocket.service';
import { ToastService } from './core/services/toast.service';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, LoadingSpinnerComponent, CustomCursorComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  standalone: true,
})
export class App implements OnInit {
  private themeService = inject(ThemeService);
  private webSocketService = inject(WebSocketService);
  private toastService = inject(ToastService);
  private authService = inject(AuthService);

  ngOnInit() {
    // Initialize theme
    this.themeService.theme();

    // Subscribe to auth state to connect/disconnect websocket
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.webSocketService.connect();
      } else {
        this.webSocketService.disconnect();
      }
    });

    // Listen to real-time notifications
    this.webSocketService.notifications$.subscribe(notification => {
      this.toastService.show(notification.message, 'info');
    });
  }
}
