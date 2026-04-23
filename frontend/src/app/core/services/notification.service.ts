import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

export interface LiveNotification {
  type: string;
  title: string;
  message: string;
  timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private authService = inject(AuthService);
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);
  private socket: WebSocket | null = null;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;

  private notificationsSignal = signal<LiveNotification[]>([]);
  notifications = computed(() => this.notificationsSignal());

  connect(): void {
    if (!this.isBrowser || this.socket) {
      return;
    }
    const token = this.authService.getToken();
    if (!token) {
      return;
    }
    const wsUrl = this.toWsUrl(token);
    this.socket = new WebSocket(wsUrl);
    this.socket.onmessage = (event) => this.handleMessage(event.data);
    this.socket.onclose = () => this.scheduleReconnect();
    this.socket.onerror = () => this.scheduleReconnect();
  }

  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  private handleMessage(payload: string): void {
    try {
      const item = JSON.parse(payload) as LiveNotification;
      this.notificationsSignal.update((items) => [item, ...items].slice(0, 20));
    } catch {
      // ignore malformed payloads
    }
  }

  private scheduleReconnect(): void {
    this.socket = null;
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }
    this.reconnectTimer = setTimeout(() => this.connect(), 3000);
  }

  private toWsUrl(token: string): string {
    const base = environment.apiUrl.replace(/\/api(?:\/v\d+)?$/, '');
    return base.replace(/^http/, 'ws') + '/ws/notifications?token=' + encodeURIComponent(token);
  }
}
