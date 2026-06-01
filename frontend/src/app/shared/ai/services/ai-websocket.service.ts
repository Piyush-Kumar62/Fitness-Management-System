import { Injectable, inject, signal } from '@angular/core';
import { Client, Frame, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';
import { AiStreamPayload, AiStreamRequest } from '../models/ai-stream.model';

export type AiSocketState = 'disconnected' | 'connecting' | 'connected' | 'error';

@Injectable({
  providedIn: 'root',
})
export class AiWebsocketService {
  private authService = inject(AuthService);
  private client: Client | null = null;
  private subscriptions = new Map<number, StompSubscription>();
  private streams = new Map<number, Subject<AiStreamPayload>>();
  private pendingMessages: AiStreamRequest[] = [];

  connectionState = signal<AiSocketState>('disconnected');

  connect(): void {
    if (this.client || this.connectionState() === 'connecting') {
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      return;
    }

    const socketUrl = this.resolveSocketUrl();
    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 4000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
    });

    client.onConnect = () => {
      this.connectionState.set('connected');
      this.resubscribe();
      this.flushPendingMessages();
    };

    client.onStompError = (frame: Frame) => {
      this.connectionState.set('error');
      this.emitError(frame);
    };

    client.onWebSocketClose = () => {
      this.connectionState.set('disconnected');
      this.clearSubscriptions();
    };

    client.onWebSocketError = () => {
      this.connectionState.set('error');
      this.clearSubscriptions();
    };

    this.client = client;
    this.connectionState.set('connecting');
    client.activate();
  }

  disconnect(): void {
    this.connectionState.set('disconnected');
    this.clearSubscriptions();
    this.client?.deactivate();
    this.client = null;
  }

  stream(sessionId: number): Subject<AiStreamPayload> {
    const existing = this.streams.get(sessionId);
    if (existing) {
      this.ensureSubscription(sessionId, existing);
      return existing;
    }
    const subject = new Subject<AiStreamPayload>();
    this.streams.set(sessionId, subject);
    this.ensureSubscription(sessionId, subject);
    return subject;
  }

  sendMessage(sessionId: number, message: string): void {
    const payload: AiStreamRequest = {
      sessionId,
      message,
      timestamp: new Date().toISOString(),
    };

    if (!this.client || !this.client.connected) {
      this.pendingMessages.push(payload);
      this.connect();
      return;
    }

    this.client.publish({
      destination: '/app/ai/chat',
      body: JSON.stringify(payload),
    });
  }

  private ensureSubscription(sessionId: number, subject: Subject<AiStreamPayload>): void {
    if (!this.client || !this.client.connected) {
      this.connect();
      return;
    }
    if (this.subscriptions.has(sessionId)) {
      return;
    }

    const subscription = this.client.subscribe(`/topic/ai/${sessionId}`, (message) => {
      const payload = this.parsePayload(message);
      if (payload) {
        subject.next(payload);
      }
    });
    this.subscriptions.set(sessionId, subscription);
  }

  private resubscribe(): void {
    for (const [sessionId, subject] of this.streams.entries()) {
      this.ensureSubscription(sessionId, subject);
    }
  }

  private flushPendingMessages(): void {
    if (!this.client || !this.client.connected) {
      return;
    }
    const pending = [...this.pendingMessages];
    this.pendingMessages = [];
    pending.forEach((item) => this.sendMessage(item.sessionId, item.message));
  }

  private clearSubscriptions(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.subscriptions.clear();
  }

  private parsePayload(message: IMessage): AiStreamPayload | null {
    try {
      return JSON.parse(message.body) as AiStreamPayload;
    } catch {
      return null;
    }
  }

  private emitError(frame: Frame): void {
    const payload: AiStreamPayload = {
      type: 'ERROR',
      sessionId: 0,
      content: frame.headers?.message ?? 'Streaming connection failed',
      timestamp: new Date().toISOString(),
    };
    this.streams.forEach((subject) => subject.next(payload));
  }

  private resolveSocketUrl(): string {
    const base = environment.apiUrl.replace(/\/api(?:\/v\d+)?$/, '');
    return `${base}/ws`;
  }
}
