import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiChatSession } from '../../models/ai-chat-session.model';

@Component({
  selector: 'app-ai-session-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-session-sidebar.component.html',
  styleUrls: ['./ai-session-sidebar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AiSessionSidebarComponent {
  @Input() sessions: AiChatSession[] = [];
  @Input() selectedSessionId: number | null = null;
  @Input() isLoading = false;

  @Output() select = new EventEmitter<number>();
  @Output() create = new EventEmitter<void>();
  @Output() rename = new EventEmitter<{ id: number; title: string }>();
  @Output() delete = new EventEmitter<number>();

  editingSessionId = signal<number | null>(null);
  titleDraft = signal('');

  startRename(session: AiChatSession): void {
    this.editingSessionId.set(session.id);
    this.titleDraft.set(session.title);
  }

  cancelRename(): void {
    this.editingSessionId.set(null);
    this.titleDraft.set('');
  }

  submitRename(session: AiChatSession): void {
    const title = this.titleDraft().trim();
    if (!title || title === session.title) {
      this.cancelRename();
      return;
    }
    this.rename.emit({ id: session.id, title });
    this.cancelRename();
  }

  trackById(_: number, session: AiChatSession): number {
    return session.id;
  }
}
