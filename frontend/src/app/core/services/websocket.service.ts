import { Injectable, inject } from '@angular/core';
import { Subject } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

export interface NotificationMessage {
  type: string;
  title: string;
  message: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private authService = inject(AuthService);
  private socket: WebSocket | null = null;
  public notifications$ = new Subject<NotificationMessage>();

  public connect(): void {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      return;
    }

    const token = this.authService.getToken();
    if (!token) return;

    // Convert http/https URL to ws/wss URL
    const wsUrl = environment.apiUrl.replace(/^http/, 'ws').replace(/\/api$/, '') + `/ws/notifications?token=${token}`;

    this.socket = new WebSocket(wsUrl);

    this.socket.onopen = () => {
      console.log('WebSocket connection established');
    };

    this.socket.onmessage = (event) => {
      try {
        const message: NotificationMessage = JSON.parse(event.data);
        this.notifications$.next(message);
      } catch (e) {
        console.error('Error parsing WebSocket message', e);
      }
    };

    this.socket.onclose = () => {
      console.log('WebSocket connection closed. Reconnecting in 5s...');
      setTimeout(() => this.connect(), 5000);
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error', error);
      this.socket?.close();
    };
  }

  public disconnect(): void {
    if (this.socket) {
      this.socket.onclose = null; // Prevent auto-reconnect
      this.socket.close();
      this.socket = null;
    }
  }
}
