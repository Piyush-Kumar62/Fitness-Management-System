import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadService, FileUploadResponse } from '../../../core/services/file-upload.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.component.html',
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
    private toastService: ToastService,
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
