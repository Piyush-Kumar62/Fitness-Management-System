import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiService } from './api.service';

export interface FileUploadResponse {
  id: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  fileUrl: string;
  uploadedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class FileUploadService {
  private api = inject(ApiService);

  private apiUrl = `${environment.apiUrl}/files`;

  uploadFile(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.api.post<FileUploadResponse>('files/upload', formData);
  }

  getMyFiles(): Observable<FileUploadResponse[]> {
    return this.api.get<FileUploadResponse[]>('files/user/me');
  }

  deleteFile(fileId: string): Observable<void> {
    return this.api.delete<void>(`files/${fileId}`);
  }

  getFileUrl(fileId: string): string {
    return `${this.apiUrl}/${fileId}`;
  }
}
