import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  EventEmitter,
  Output,
  ViewChild,
  ElementRef,
  inject,
  signal,
  computed,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription } from 'rxjs';
import { AiSessionService } from '../../services/ai-session.service';
import { AiChatService } from '../../services/ai-chat.service';
import { AiWebsocketService } from '../../services/ai-websocket.service';
import { AiChatSession } from '../../models/ai-chat-session.model';
import { AiChatMessage } from '../../models/ai-chat-message.model';
import { AiStreamPayload } from '../../models/ai-stream.model';
import { AiUiMessage } from '../../models/ai-ui-message.model';
import { ToastService } from '../../../../core/services/toast.service';
import { AiMessageComponent } from '../ai-message/ai-message.component';
import { AiSessionSidebarComponent } from '../ai-session-sidebar/ai-session-sidebar.component';

@Component({
  selector: 'app-ai-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule, AiMessageComponent, AiSessionSidebarComponent],
  templateUrl: './ai-chat-window.component.html',
  styleUrls: ['./ai-chat-window.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AiChatWindowComponent {
  @Output() close = new EventEmitter<void>();
  @ViewChild('messagesViewport') messagesViewport?: ElementRef<HTMLDivElement>;

  private sessionsService = inject(AiSessionService);
  private chatService = inject(AiChatService);
  private websocketService = inject(AiWebsocketService);
  private toast = inject(ToastService);
  private destroyRef = inject(DestroyRef);
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  sessions = signal<AiChatSession[]>([]);
  selectedSessionId = signal<number | null>(null);
  messages = signal<AiUiMessage[]>([]);
  composer = signal('');
  isStreaming = signal(false);
  isLoadingSessions = signal(false);
  isLoadingMessages = signal(false);
  activeStreamSessionId = signal<number | null>(null);

  connectionState = computed(() => this.websocketService.connectionState());
  hasSessions = computed(() => this.sessions().length > 0);
  selectedSession = computed(() =>
    this.sessions().find((item) => item.id === this.selectedSessionId()) ?? null,
  );

  private streamSubscription?: Subscription;
  private activeAssistantMessageId: string | null = null;

  constructor() {
    this.websocketService.connect();
    this.loadSessions();
    this.destroyRef.onDestroy(() => this.websocketService.disconnect());
  }

  onClose(): void {
    this.close.emit();
  }

  onCreateNewSession(): void {
    this.createSession('New Chat');
  }

  onSelectSession(sessionId: number): void {
    if (this.selectedSessionId() === sessionId) {
      return;
    }
    this.selectedSessionId.set(sessionId);
    this.loadMessages(sessionId);
    this.listenToStream(sessionId);
  }

  onRenameSession(payload: { id: number; title: string }): void {
    const title = payload.title.trim();
    if (!title) {
      return;
    }
    this.sessionsService
      .renameSession(payload.id, title)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (session) => {
          this.sessions.update((items) =>
            items.map((item) => (item.id === session.id ? session : item)),
          );
        },
        error: () => this.toast.error('Failed to rename the session.'),
      });
  }

  async onDeleteSession(sessionId: number): Promise<void> {
    const confirmed = await this.toast.confirm(
      'Delete session?',
      'This will remove the session and its messages.',
      'Delete',
      'Cancel',
    );
    if (!confirmed) {
      return;
    }
    this.sessionsService
      .deleteSession(sessionId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.sessions.update((items) => items.filter((item) => item.id !== sessionId));
          if (this.selectedSessionId() === sessionId) {
            const nextSession = this.sessions()[0] ?? null;
            this.selectedSessionId.set(nextSession?.id ?? null);
            if (nextSession) {
              this.loadMessages(nextSession.id);
              this.listenToStream(nextSession.id);
            } else {
              this.messages.set([]);
            }
          }
        },
        error: () => this.toast.error('Failed to delete the session.'),
      });
  }

  onComposerKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage(): void {
    const raw = this.composer();
    const content = raw.trim();
    if (!content || this.isStreaming()) {
      return;
    }

    this.composer.set('');

    const sessionId = this.selectedSessionId();
    if (!sessionId) {
      this.createSession(this.titleFromPrompt(content), content);
      return;
    }

    this.appendUserMessage(content);
    this.beginStreaming(sessionId);
    this.websocketService.sendMessage(sessionId, content);
  }

  private createSession(title: string | null, initialMessage?: string): void {
    this.sessionsService
      .createSession(title)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (session) => {
          this.sessions.update((items) => [session, ...items]);
          this.selectedSessionId.set(session.id);
          this.messages.set([]);
          this.listenToStream(session.id);
          if (initialMessage) {
            this.appendUserMessage(initialMessage);
            this.beginStreaming(session.id);
            this.websocketService.sendMessage(session.id, initialMessage);
          }
        },
        error: () => this.toast.error('Failed to start a new session.'),
      });
  }

  private loadSessions(): void {
    this.isLoadingSessions.set(true);
    this.sessionsService
      .listSessions()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (page) => {
          this.sessions.set(page.content ?? []);
          this.isLoadingSessions.set(false);
          if (!this.selectedSessionId() && page.content?.length) {
            const first = page.content[0];
            this.selectedSessionId.set(first.id);
            this.loadMessages(first.id);
            this.listenToStream(first.id);
          }
        },
        error: () => {
          this.isLoadingSessions.set(false);
          this.toast.error('Failed to load chat sessions.');
        },
      });
  }

  private loadMessages(sessionId: number): void {
    this.isLoadingMessages.set(true);
    this.chatService
      .listMessages(sessionId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (page) => {
          const mapped = (page.content ?? []).map((item) => this.mapMessage(item));
          this.messages.set(mapped);
          this.isLoadingMessages.set(false);
          this.scrollToBottom();
        },
        error: () => {
          this.isLoadingMessages.set(false);
          this.toast.error('Failed to load chat history.');
        },
      });
  }

  private listenToStream(sessionId: number): void {
    if (this.activeStreamSessionId() === sessionId) {
      return;
    }
    this.activeStreamSessionId.set(sessionId);
    this.streamSubscription?.unsubscribe();
    this.streamSubscription = this.websocketService
      .stream(sessionId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((payload) => this.handleStreamPayload(payload));
  }

  private handleStreamPayload(payload: AiStreamPayload): void {
    if (!payload || (payload.sessionId && this.selectedSessionId() !== payload.sessionId)) {
      return;
    }

    switch (payload.type) {
      case 'TEXT':
        this.appendStreamChunk(payload.content ?? '');
        break;
      case 'TOOL_CALL':
      case 'TOOL_RESULT':
        this.appendToolMessage(payload);
        break;
      case 'ERROR':
        this.appendErrorMessage(payload.content ?? 'Streaming failed.');
        this.isStreaming.set(false);
        this.activeAssistantMessageId = null;
        break;
      case 'COMPLETE':
        this.isStreaming.set(false);
        this.markStreamingComplete();
        this.activeAssistantMessageId = null;
        this.loadMessages(this.selectedSessionId() ?? payload.sessionId);
        this.loadSessions();
        break;
    }
  }

  private appendUserMessage(content: string): void {
    this.messages.update((items) => [
      ...items,
      {
        id: this.createLocalId('user'),
        sender: 'USER',
        content,
        createdAt: new Date().toISOString(),
      },
    ]);
    this.scrollToBottom();
  }

  private beginStreaming(sessionId: number): void {
    this.isStreaming.set(true);
    this.activeAssistantMessageId = this.createLocalId('assistant');
    this.messages.update((items) => [
      ...items,
      {
        id: this.activeAssistantMessageId as string,
        sender: 'ASSISTANT',
        content: '',
        createdAt: new Date().toISOString(),
        streaming: true,
      },
    ]);
    this.scrollToBottom();
  }

  private appendStreamChunk(chunk: string): void {
    if (!this.activeAssistantMessageId) {
      this.beginStreaming(this.selectedSessionId() ?? 0);
    }
    const id = this.activeAssistantMessageId as string;
    this.messages.update((items) =>
      items.map((item) =>
        item.id === id
          ? {
              ...item,
              content: `${item.content}${chunk}`,
              streaming: true,
            }
          : item,
      ),
    );
    this.scrollToBottom();
  }

  private appendToolMessage(payload: AiStreamPayload): void {
    const content = payload.content?.trim() || 'Tool update received.';
    this.messages.update((items) => [
      ...items,
      {
        id: this.createLocalId('tool'),
        sender: 'TOOL',
        content,
        createdAt: payload.timestamp ?? new Date().toISOString(),
      },
    ]);
    this.scrollToBottom();
  }

  private appendErrorMessage(content: string): void {
    this.messages.update((items) => [
      ...items,
      {
        id: this.createLocalId('error'),
        sender: 'ERROR',
        content,
        createdAt: new Date().toISOString(),
      },
    ]);
    this.toast.error(content, 'AI streaming');
    this.scrollToBottom();
  }

  private markStreamingComplete(): void {
    if (!this.activeAssistantMessageId) {
      return;
    }
    const id = this.activeAssistantMessageId;
    this.messages.update((items) =>
      items.map((item) => (item.id === id ? { ...item, streaming: false } : item)),
    );
  }

  private mapMessage(message: AiChatMessage): AiUiMessage {
    const sender =
      message.sender === 'ASSISTANT'
        ? 'ASSISTANT'
        : message.sender === 'SYSTEM'
          ? 'SYSTEM'
          : 'USER';
    return {
      id: `msg-${message.id}`,
      sender,
      content: message.content,
      createdAt: message.createdAt,
      streaming: false,
    };
  }

  private titleFromPrompt(prompt: string): string | null {
    const trimmed = prompt.replace(/\s+/g, ' ').trim();
    if (trimmed.length < 4) {
      return null;
    }
    return trimmed.length > 48 ? `${trimmed.slice(0, 48)}...` : trimmed;
  }

  private createLocalId(prefix: string): string {
    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
  }

  private scrollToBottom(): void {
    if (!this.isBrowser) {
      return;
    }
    requestAnimationFrame(() => {
      const element = this.messagesViewport?.nativeElement;
      if (element) {
        element.scrollTop = element.scrollHeight;
      }
    });
  }
}
