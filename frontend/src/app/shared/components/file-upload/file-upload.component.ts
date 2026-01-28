import { Component, EventEmitter, Input, Output, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadService, UploadResponse } from '../../../core/services/file-upload.service';
import { ToastService } from '../../../core/services/toast.service';

/**
 * Reusable File Upload Component
 * Features: File selection, image preview, validation, upload with feedback
 */
@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="file-upload-container">
      @if (!selectedFile()) {
        <!-- Upload Area -->
        <label
          class="upload-area border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center cursor-pointer hover:border-indigo-500 dark:hover:border-indigo-400 transition-colors"
        >
          <input
            type="file"
            [accept]="acceptedTypes"
            (change)="onFileSelected($event)"
            class="hidden"
          />
          <div class="flex flex-col items-center space-y-4">
            <svg
              class="w-16 h-16 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
              />
            </svg>
            <div>
              <p class="text-lg font-medium text-gray-700 dark:text-gray-300">{{ buttonText }}</p>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
                Maximum file size: {{ maxSizeMB }}MB
              </p>
            </div>
          </div>
        </label>
      } @else {
        <!-- Preview & Actions -->
        <div
          class="preview-container bg-white dark:bg-gray-800 rounded-lg p-6 border border-gray-200 dark:border-gray-700"
        >
          <!-- Image Preview -->
          @if (previewUrl()) {
            <div class="preview-image mb-4">
              <img
                [src]="previewUrl()"
                alt="Preview"
                class="max-w-full h-auto rounded-lg mx-auto max-h-64 object-contain"
              />
            </div>
          }

          <!-- File Info -->
          <div class="file-info mb-4">
            <p class="text-sm font-medium text-gray-900 dark:text-white truncate">
              {{ selectedFile()?.name }}
            </p>
            <p class="text-xs text-gray-500 dark:text-gray-400">
              {{ (selectedFile()?.size || 0) / 1024 / 1024 | number: '1.2-2' }} MB
            </p>
          </div>

          <!-- Upload Progress -->
          @if (isUploading()) {
            <div class="upload-progress mb-4">
              <div class="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                <div
                  class="bg-indigo-600 h-2 rounded-full transition-all duration-300 animate-pulse"
                  style="width: 90%"
                ></div>
              </div>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2 text-center">Uploading...</p>
            </div>
          }

          <!-- Actions -->
          <div class="flex items-center space-x-3">
            <button
              type="button"
              (click)="uploadFile()"
              [disabled]="isUploading()"
              class="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            >
              @if (isUploading()) {
                <span>Uploading...</span>
              } @else {
                <span>Upload</span>
              }
            </button>
            <button
              type="button"
              (click)="removeFile()"
              [disabled]="isUploading()"
              class="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Cancel
            </button>
          </div>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .upload-area {
        min-height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    `,
  ],
})
export class FileUploadComponent {
  @Input() category: string = 'PROGRESS_PHOTO';
  @Input() acceptedTypes: string = 'image/*';
  @Input() maxSizeMB: number = 5;
  @Input() buttonText: string = 'Upload File';
  @Output() fileUploaded = new EventEmitter<UploadResponse>();

  private fileUploadService = inject(FileUploadService);
  private toast = inject(ToastService);

  selectedFile = signal<File | null>(null);
  previewUrl = signal<string | null>(null);
  isUploading = signal(false);

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Validate file
    const validation = this.fileUploadService.validateFile(file, this.maxSizeMB);
    if (!validation.valid) {
      this.toast.error(validation.error || 'Invalid file');
      return;
    }

    this.selectedFile.set(file);

    // Generate preview for images
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl.set(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  }

  uploadFile(): void {
    const file = this.selectedFile();
    if (!file) return;

    this.isUploading.set(true);

    this.fileUploadService.uploadFile(file, this.category).subscribe({
      next: (response) => {
        this.toast.success('File uploaded successfully');
        this.fileUploaded.emit(response);
        this.reset();
      },
      error: (error) => {
        this.toast.error(error?.error?.error || 'Failed to upload file');
        this.isUploading.set(false);
      },
    });
  }

  removeFile(): void {
    this.reset();
  }

  private reset(): void {
    this.selectedFile.set(null);
    this.previewUrl.set(null);
    this.isUploading.set(false);
  }
}
