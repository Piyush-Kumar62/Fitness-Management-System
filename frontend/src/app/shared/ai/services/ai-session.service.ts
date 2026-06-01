import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { Page } from '../../../core/models/page.model';
import { AiChatSession } from '../models/ai-chat-session.model';

@Injectable({
  providedIn: 'root',
})
export class AiSessionService {
  private api = inject(ApiService);

  listSessions(page = 0, size = 30): Observable<Page<AiChatSession>> {
    return this.api.get<Page<AiChatSession>>('ai/sessions', {
      page,
      size,
      sort: 'updatedAt,desc',
    });
  }

  getSession(sessionId: number): Observable<AiChatSession> {
    return this.api.get<AiChatSession>(`ai/sessions/${sessionId}`);
  }

  createSession(title?: string | null): Observable<AiChatSession> {
    return this.api.post<AiChatSession>('ai/sessions', {
      title: title?.trim() || null,
    });
  }

  renameSession(sessionId: number, title: string): Observable<AiChatSession> {
    return this.api.patch<AiChatSession>(`ai/sessions/${sessionId}`, {
      title: title.trim(),
    });
  }

  deleteSession(sessionId: number): Observable<void> {
    return this.api.delete<void>(`ai/sessions/${sessionId}`);
  }
}
