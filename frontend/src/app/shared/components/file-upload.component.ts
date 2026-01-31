import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadService, FileUploadResponse } from '../../core/services/file-upload.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="file-upload-container">
      <label
        [class.dragging]="isDragging"
        class="file-upload-label block w-full text-center p-6 border-2 border-dashed rounded-lg cursor-pointer hover:border-indigo-500 transition"
        (dragover)="onDragOver($event)"
        (dragleave)="onDragLeave($event)"
        (drop)="onDrop($event)">
        <input
          #fileInput
          type="file"
          [accept]="acceptedTypes"
          class="hidden"
          (change)="onFileSelected($event)" />

        @if (uploading) {
          <div class="text-gray-700 dark:text-gray-300">
            <svg
              class="animate-spin h-10 w-10 mx-auto mb-2 text-indigo-600"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p>Uploading...</p>
          </div>
        } @else {
          <div class="text-gray-700 dark:text-gray-300">
            <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
              <path
                d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
            <p class="mt-2 text-sm">
              @if (placeholder) {
                {{ placeholder }}
              } @else {
                <span class="text-indigo-600 hover:text-indigo-500">Upload a file</span>
                or drag and drop
              }
            </p>
            @if (acceptedTypes) {
              <p class="mt-1 text-xs text-gray-500">{{ acceptedTypes }}</p>
            }
          </div>
        }
      </label>
    </div>
  `,
  styles: [
    `
      .file-upload-label.dragging {
        border-color: #4f46e5;
        background-color: rgba(79, 70, 229, 0.05);
      }
    `,
  ],
})
export class FileUploadComponent {
  @Input() acceptedTypes: string = 'image/*';
  @Input() placeholder: string = '';
  @Output() fileUploaded = new EventEmitter<FileUploadResponse>();

  uploading = false;
  isDragging = false;

  constructor(
    private fileUploadService: FileUploadService,
    private toastService: ToastService
  ) {}

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.uploadFile(files[0]);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.uploadFile(input.files[0]);
    }
  }

  private uploadFile(file: File): void {
    this.uploading = true;

    this.fileUploadService.uploadFile(file).subscribe({
      next: (response) => {
        this.toastService.success('File uploaded successfully');
        this.fileUploaded.emit(response);
        this.uploading = false;
      },
      error: (error) => {
        console.error('File upload failed:', error);
        this.toastService.error('File upload failed');
        this.uploading = false;
      },
    });
  }
}
