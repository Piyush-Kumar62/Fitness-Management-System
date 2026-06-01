import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { AiChatWindowComponent } from '../ai-chat-window/ai-chat-window.component';

@Component({
  selector: 'app-ai-assistant',
  standalone: true,
  imports: [AiChatWindowComponent],
  templateUrl: './ai-assistant.component.html',
  styleUrls: ['./ai-assistant.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AiAssistantComponent {
  isOpen = signal(false);

  toggleOpen(): void {
    this.isOpen.update((value) => !value);
  }

  close(): void {
    this.isOpen.set(false);
  }
}
