export type AiUiSender = 'USER' | 'ASSISTANT' | 'SYSTEM' | 'TOOL' | 'ERROR';

export interface AiUiMessage {
  id: string;
  sender: AiUiSender;
  content: string;
  createdAt?: string;
  streaming?: boolean;
}
