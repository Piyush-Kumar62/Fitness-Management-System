import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Response from file upload endpoint
 */
export interface UploadResponse {
  fileId: string;
  fileUrl: string;
  fileName: string;
  fileSize: number;
  message?: string;
}

/**
 * File metadata from backend
 */
export interface FileMetadata {
  id: string;
  fileName: string;
  originalFileName: string;
  fileType: string;
  fileSize: number;
  category: string;
  uploadedAt: string;
  thumbnailPath?: string;
}

/**
 * File Upload Service
 * Handles all file upload operations following Angular best practices
 */
@Injectable({
  providedIn: 'root',
})
export class FileUploadService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/files`;
  private readonly USER_API_URL = `${environment.apiUrl}/users`;

  /**
   * Upload file with category
   */
  uploadFile(file: File, category: string): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('category', category);

    return this.http.post<UploadResponse>(`${this.API_URL}/upload`, formData);
  }

  /**
   * Upload profile picture
   */
  uploadProfilePicture(file: File): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<UploadResponse>(`${this.USER_API_URL}/profile-picture`, formData);
  }

  /**
   * Get all files uploaded by current user
   */
  getMyFiles(category?: string): Observable<FileMetadata[]> {
    const url = category ? `${this.API_URL}/my-files/${category}` : `${this.API_URL}/my-files`;

    return this.http.get<FileMetadata[]>(url);
  }

  /**
   * Delete file by ID
   */
  deleteFile(fileId: string): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.API_URL}/${fileId}`);
  }

  /**
   * Get file URL for display
   */
  getFileUrl(fileId: string): string {
    return `${this.API_URL}/${fileId}`;
  }

  /**
   * Validate file before upload
   */
  validateFile(
    file: File,
    maxSizeMB: number = 5,
    allowedTypes: string[] = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  ): { valid: boolean; error?: string } {
    // Check file size
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      return {
        valid: false,
        error: `File size exceeds ${maxSizeMB}MB limit`,
      };
    }

    // Check file type
    if (!allowedTypes.includes(file.type)) {
      return {
        valid: false,
        error: 'Only image files (JPEG, PNG, GIF, WebP) are allowed',
      };
    }

    return { valid: true };
  }
}
