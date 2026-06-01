export interface AiChatSession {
  id: number;
  title: string;
  role: 'MEMBER' | 'TRAINER' | 'OWNER' | 'ADMIN' | string;
  status: string;
  createdAt: string;
  updatedAt: string;
}
