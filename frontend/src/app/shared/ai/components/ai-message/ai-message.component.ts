import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  inject,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SecurityContext } from '@angular/platform-browser';
import { ToastService } from '../../../../core/services/toast.service';
import { AiUiMessage } from '../../models/ai-ui-message.model';

@Component({
  selector: 'app-ai-message',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ai-message.component.html',
  styleUrls: ['./ai-message.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AiMessageComponent implements OnChanges {
  @Input({ required: true }) message!: AiUiMessage;

  private sanitizer = inject(DomSanitizer);
  private toast = inject(ToastService);

  renderedHtml = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['message']) {
      this.renderedHtml = this.toSafeHtml(this.message.content);
    }
  }

  async copyMessage(): Promise<void> {
    try {
      if (typeof navigator !== 'undefined' && navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(this.message.content);
        this.toast.success('Message copied');
      } else {
        this.toast.warning('Clipboard unavailable');
      }
    } catch {
      this.toast.error('Unable to copy message');
    }
  }

  private toSafeHtml(content: string): string {
    if (this.message.sender !== 'ASSISTANT') {
      return '';
    }
    const html = this.renderMarkdown(content);
    return this.sanitizer.sanitize(SecurityContext.HTML, html) ?? '';
  }

  private renderMarkdown(content: string): string {
    const escaped = this.escapeHtml(content);
    const withCodeBlocks = escaped.replace(/```([\s\S]*?)```/g, (_, code) => {
      return `<pre><code>${code.trim()}</code></pre>`;
    });

    let html = withCodeBlocks
      .replace(/^###\s(.+)$/gm, '<h3>$1</h3>')
      .replace(/^##\s(.+)$/gm, '<h2>$1</h2>')
      .replace(/^#\s(.+)$/gm, '<h1>$1</h1>')
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(?!\s)([^*]+?)\*/g, '<em>$1</em>')
      .replace(/`([^`]+?)`/g, '<code>$1</code>')
      .replace(/^\s*[-*]\s(.+)$/gm, '<li>$1</li>');

    html = html.replace(/(<li>.*<\/li>\s*)+/g, (match) => `<ul>${match}</ul>`);
    html = html.replace(/\n{2,}/g, '<br><br>').replace(/\n/g, '<br>');
    return html;
  }

  private escapeHtml(value: string): string {
    return value
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }
}
