import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit, OnDestroy {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);

  profile = signal<User | null>(null);
  profileImageSrc = signal<string | null>(null);
  isLoading = signal(true);
  isEditing = signal(false);
  isSaving = signal(false);
  isUploading = signal(false);
  isChangingPassword = signal(false);
  showPasswordChange = false;
  private imageSub?: Subscription;
  private currentImageObjectUrl: string | null = null;

  editForm = {
    firstName: '',
    lastName: '',
  };

  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.isLoading.set(true);
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.profile.set(user);
        this.loadProfileImage(user.profileImageUrl);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load profile:', error);
        this.toastService.error('Failed to load profile');
        this.isLoading.set(false);
      },
    });
  }

  getInitials(): string {
    const p = this.profile();
    if (!p) return '?';
    return `${p.firstName.charAt(0)}${p.lastName.charAt(0)}`.toUpperCase();
  }

  startEditing() {
    const p = this.profile();
    if (p) {
      this.editForm = {
        firstName: p.firstName,
        lastName: p.lastName,
      };
      this.isEditing.set(true);
    }
  }

  cancelEditing() {
    this.isEditing.set(false);
  }

  saveProfile() {
    this.isSaving.set(true);
    this.userService.updateProfile(this.editForm).subscribe({
      next: (updatedUser) => {
        this.profile.set(updatedUser);
        this.authService.updateUser(updatedUser);
        this.loadProfileImage(updatedUser.profileImageUrl);
        this.isEditing.set(false);
        this.isSaving.set(false);
        this.toastService.success('Profile updated successfully');
      },
      error: (error) => {
        console.error('Failed to update profile:', error);
        this.toastService.error('Failed to update profile');
        this.isSaving.set(false);
      },
    });
  }

  changePassword() {
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.toastService.error('New passwords do not match');
      return;
    }

    if (this.passwordForm.newPassword.length < 6) {
      this.toastService.error('Password must be at least 6 characters');
      return;
    }

    this.isChangingPassword.set(true);
    this.userService
      .changePassword(this.passwordForm.currentPassword, this.passwordForm.newPassword)
      .subscribe({
        next: () => {
          this.toastService.success('Password changed successfully');
          this.showPasswordChange = false;
          this.resetPasswordForm();
          this.isChangingPassword.set(false);
        },
        error: (error) => {
          console.error('Failed to change password:', error);
          this.toastService.error('Failed to change password');
          this.isChangingPassword.set(false);
        },
      });
  }

  resetPasswordForm() {
    this.passwordForm = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    };
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Basic validation
      if (!file.type.startsWith('image/')) {
        this.toastService.error('Please upload an image file');
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) {
        this.toastService.error('File size must be less than 5MB');
        return;
      }

      this.isUploading.set(true);
      this.userService.uploadProfileImage(file).subscribe({
        next: (updatedUser) => {
          this.profile.set(updatedUser);
          this.authService.updateUser(updatedUser);
          this.loadProfileImage(updatedUser.profileImageUrl);
          this.isUploading.set(false);
          this.toastService.success('Profile image updated');
        },
        error: (error) => {
          console.error('Failed to upload image:', error);
          this.toastService.error('Failed to upload image');
          this.isUploading.set(false);
        }
      });
    }
  }

  async deleteImage() {
    const confirmed = await this.toastService.confirm(
      'Delete Image',
      'Are you sure you want to delete your profile image?',
      'Yes, delete'
    );
    if (!confirmed) return;
    
    this.isUploading.set(true);
    this.userService.deleteProfileImage().subscribe({
      next: (updatedUser) => {
        this.profile.set(updatedUser);
        this.authService.updateUser(updatedUser);
        this.loadProfileImage(updatedUser.profileImageUrl);
        this.isUploading.set(false);
        this.toastService.success('Profile image removed');
      },
      error: (error) => {
        console.error('Failed to delete image:', error);
        this.toastService.error('Failed to remove image');
        this.isUploading.set(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.imageSub?.unsubscribe();
    this.revokeCurrentObjectUrl();
  }

  private loadProfileImage(profileImageUrl?: string): void {
    this.imageSub?.unsubscribe();
    this.revokeCurrentObjectUrl();

    this.imageSub = this.userService.getProfileImageBlobUrl(profileImageUrl).subscribe((blobUrl) => {
      if (!blobUrl) {
        this.profileImageSrc.set(null);
        return;
      }

      this.currentImageObjectUrl = blobUrl;
      this.profileImageSrc.set(blobUrl);
    });
  }

  private revokeCurrentObjectUrl(): void {
    if (this.currentImageObjectUrl) {
      URL.revokeObjectURL(this.currentImageObjectUrl);
      this.currentImageObjectUrl = null;
    }
  }
}
