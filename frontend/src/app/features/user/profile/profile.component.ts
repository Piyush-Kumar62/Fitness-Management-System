import { Component, OnInit, inject, signal, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { FileUploadService } from '../../../core/services/file-upload.service';
import { User } from '../../../core/models/user.model';
import { environment } from '../../../../environments/environment';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="profile-container max-w-4xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-6">Profile</h1>

      @if (isLoading()) {
        <div class="flex justify-center items-center p-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      } @else if (profile()) {
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow">
          <!-- Profile Header -->
          <div class="bg-gradient-to-r from-indigo-500 to-purple-600 p-6 rounded-t-lg">
            <div class="flex items-center space-x-4">
              <!-- Profile Picture with Upload -->
              <div class="relative">
                <div
                  class="h-20 w-20 rounded-full bg-white flex items-center justify-center text-3xl font-bold overflow-hidden"
                  [class.text-indigo-600]="!profilePictureBlobUrl()"
                >
                  @if (profilePictureBlobUrl()) {
                    <img
                      [src]="profilePictureBlobUrl()"
                      alt="Profile"
                      class="w-full h-full object-cover"
                      (error)="onImageError($event)"
                    />
                  } @else {
                    <span>{{ getInitials() }}</span>
                  }
                </div>

                <!-- Upload Button Overlay -->
                <label
                  class="absolute bottom-0 right-0 bg-white hover:bg-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-full p-1.5 cursor-pointer shadow-lg transition-colors"
                >
                  <input
                    type="file"
                    accept="image/*"
                    (change)="onProfilePictureSelected($event)"
                    class="hidden"
                    [disabled]="isUploadingPicture()"
                  />
                  <svg
                    class="w-4 h-4 text-indigo-600 dark:text-indigo-400"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"
                    />
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                  </svg>
                </label>

                @if (isUploadingPicture()) {
                  <div
                    class="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center"
                  >
                    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
                  </div>
                }
              </div>

              <div class="text-white">
                <h2 class="text-2xl font-bold">
                  {{ profile()?.firstName }} {{ profile()?.lastName }}
                </h2>
                <p class="text-indigo-100">{{ profile()?.email }}</p>
              </div>
            </div>
          </div>

          <!-- Profile Form -->
          <div class="p-6">
            @if (!isEditing()) {
              <!-- View Mode -->
              <div class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >First Name</label
                    >
                    <p class="mt-1 text-lg text-gray-900 dark:text-white">
                      {{ profile()?.firstName }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >Last Name</label
                    >
                    <p class="mt-1 text-lg text-gray-900 dark:text-white">
                      {{ profile()?.lastName }}
                    </p>
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                    >Email</label
                  >
                  <p class="mt-1 text-lg text-gray-900 dark:text-white">{{ profile()?.email }}</p>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                    >Role</label
                  >
                  <span
                    class="mt-1 inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
                    [class.bg-purple-100]="profile()?.role === 'ADMIN'"
                    [class.text-purple-800]="profile()?.role === 'ADMIN'"
                    [class.bg-blue-100]="profile()?.role === 'USER'"
                    [class.text-blue-800]="profile()?.role === 'USER'"
                  >
                    {{ profile()?.role }}
                  </span>
                </div>

                <div class="flex space-x-3 pt-4">
                  <button
                    (click)="startEditing()"
                    class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
                  >
                    Edit Profile
                  </button>
                  <button
                    (click)="showPasswordChange = !showPasswordChange"
                    class="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition"
                  >
                    Change Password
                  </button>
                </div>
              </div>
            } @else {
              <!-- Edit Mode -->
              <form (ngSubmit)="saveProfile()" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >First Name</label
                    >
                    <input
                      type="text"
                      [(ngModel)]="editForm.firstName"
                      name="firstName"
                      class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                      required
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >Last Name</label
                    >
                    <input
                      type="text"
                      [(ngModel)]="editForm.lastName"
                      name="lastName"
                      class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                      required
                    />
                  </div>
                </div>

                <div class="flex space-x-3 pt-4">
                  <button
                    type="submit"
                    [disabled]="isSaving()"
                    class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition disabled:opacity-50"
                  >
                    {{ isSaving() ? 'Saving...' : 'Save Changes' }}
                  </button>
                  <button
                    type="button"
                    (click)="cancelEditing()"
                    class="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            }

            <!-- Change Password Section -->
            @if (showPasswordChange) {
              <div class="mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                  Change Password
                </h3>
                <form (ngSubmit)="changePassword()" class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >Current Password</label
                    >
                    <input
                      type="password"
                      [(ngModel)]="passwordForm.currentPassword"
                      name="currentPassword"
                      class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                      required
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >New Password</label
                    >
                    <input
                      type="password"
                      [(ngModel)]="passwordForm.newPassword"
                      name="newPassword"
                      class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                      required
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                      >Confirm New Password</label
                    >
                    <input
                      type="password"
                      [(ngModel)]="passwordForm.confirmPassword"
                      name="confirmPassword"
                      class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                      required
                    />
                  </div>
                  <div class="flex space-x-3">
                    <button
                      type="submit"
                      [disabled]="isChangingPassword()"
                      class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition disabled:opacity-50"
                    >
                      {{ isChangingPassword() ? 'Changing...' : 'Change Password' }}
                    </button>
                    <button
                      type="button"
                      (click)="showPasswordChange = false; resetPasswordForm()"
                      class="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
})
export class ProfileComponent implements OnInit, OnDestroy {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private fileUploadService = inject(FileUploadService);
  private http = inject(HttpClient);
  private sanitizer = inject(DomSanitizer);

  profile = signal<User | null>(null);
  profilePictureBlobUrl = signal<SafeUrl | null>(null);
  isLoading = signal(true);
  isEditing = signal(false);
  isSaving = signal(false);
  isChangingPassword = signal(false);
  isUploadingPicture = signal(false);
  showPasswordChange = false;

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

  ngOnDestroy() {
    // Clean up blob URL to prevent memory leaks
    const blobUrl = this.profilePictureBlobUrl();
    if (blobUrl && typeof blobUrl === 'string') {
      URL.revokeObjectURL(blobUrl);
    }
  }

  loadProfilePicture(profilePictureUrl: string): void {
    if (!profilePictureUrl) {
      this.profilePictureBlobUrl.set(null);
      return;
    }

    // Construct full URL
    let fullUrl = profilePictureUrl;
    if (!fullUrl.startsWith('http://') && !fullUrl.startsWith('https://')) {
      fullUrl = `${environment.apiUrl.replace('/api', '')}${fullUrl}`;
    }

    // Load image with authentication via HttpClient
    this.http.get(fullUrl, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        // Revoke old blob URL if exists
        const oldUrl = this.profilePictureBlobUrl();
        if (oldUrl && typeof oldUrl === 'string') {
          URL.revokeObjectURL(oldUrl);
        }

        // Create new blob URL
        const objectUrl = URL.createObjectURL(blob);
        this.profilePictureBlobUrl.set(this.sanitizer.bypassSecurityTrustUrl(objectUrl));
      },
      error: (error) => {
        console.error('Failed to load profile picture:', error);
        this.profilePictureBlobUrl.set(null);
      },
    });
  }

  getProfilePictureUrl(): string | null {
    const url = this.profile()?.profilePictureUrl;
    if (!url) return null;

    // If URL is already absolute, return it
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }

    // If URL is relative, prepend the API base URL
    return `${environment.apiUrl.replace('/api', '')}${url}`;
  }

  onImageError(event: Event): void {
    console.error('Failed to load profile picture');
    this.profilePictureBlobUrl.set(null);
  }

  loadProfile() {
    this.isLoading.set(true);
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.profile.set(user);
        // Load profile picture with authentication
        if (user.profilePictureUrl) {
          this.loadProfilePicture(user.profilePictureUrl);
        } else {
          this.profilePictureBlobUrl.set(null);
        }
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

  onProfilePictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    // Validate file before upload
    const validation = this.fileUploadService.validateFile(file, 5, [
      'image/jpeg',
      'image/png',
      'image/gif',
      'image/webp',
    ]);

    if (!validation.valid) {
      this.toastService.error(validation.error || 'Invalid file');
      input.value = ''; // Reset input
      return;
    }

    // Upload profile picture
    this.isUploadingPicture.set(true);
    this.fileUploadService.uploadProfilePicture(file).subscribe({
      next: (response) => {
        this.toastService.success('Profile picture updated successfully');
        // Reload profile to get the updated picture URL
        this.loadProfile();
        this.isUploadingPicture.set(false);
        input.value = ''; // Reset input
      },
      error: (error) => {
        console.error('Failed to upload profile picture:', error);
        this.toastService.error(error?.error?.message || 'Failed to upload profile picture');
        this.isUploadingPicture.set(false);
        input.value = ''; // Reset input
      },
    });
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
}
