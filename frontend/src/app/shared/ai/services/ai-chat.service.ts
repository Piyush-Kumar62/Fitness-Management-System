import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { Page } from '../../../core/models/page.model';
import { AiChatMessage } from '../models/ai-chat-message.model';

export interface AiChatMessagePair {
  userMessage: AiChatMessage;
  assistantMessage: AiChatMessage;
}

@Injectable({
  providedIn: 'root',
})
export class AiChatService {
  private api = inject(ApiService);

  listMessages(sessionId: number, page = 0, size = 200): Observable<Page<AiChatMessage>> {
    return this.api.get<Page<AiChatMessage>>(`ai/sessions/${sessionId}/messages`, {
      page,
      size,
      sort: 'createdAt,asc',
    });
  }

  sendMessage(sessionId: number, content: string): Observable<AiChatMessagePair> {
    return this.api.post<AiChatMessagePair>(`ai/sessions/${sessionId}/messages`, {
      content,
    });
  }
}
