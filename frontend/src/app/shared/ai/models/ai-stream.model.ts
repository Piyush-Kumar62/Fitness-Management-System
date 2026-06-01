export type StreamPayloadType = 'TEXT' | 'TOOL_CALL' | 'TOOL_RESULT' | 'ERROR' | 'COMPLETE';

export interface AiStreamPayload {
  type: StreamPayloadType;
  sessionId: number;
  messageId?: number;
  content?: string;
  completed?: boolean;
  timestamp?: string;
}

export interface AiStreamRequest {
  sessionId: number;
  message: string;
  timestamp: string;
}
