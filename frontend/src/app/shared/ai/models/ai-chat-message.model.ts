export type ChatMessageSender = 'USER' | 'ASSISTANT' | 'SYSTEM' | string;
export type ChatMessageType = 'TEXT' | string;
export type AiResponseStatus = 'COMPLETED' | 'FAILED' | 'STREAMING' | string;

export interface AiChatMessage {
  id: number;
  sessionId: number;
  sender: ChatMessageSender;
  messageType: ChatMessageType;
  responseStatus: AiResponseStatus | null;
  content: string;
  tokenCount: number | null;
  createdAt: string;
}
