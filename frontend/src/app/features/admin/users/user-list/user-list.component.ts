import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast.service';
import { UserService } from '../../../../core/services/user.service';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
})
export class UserListComponent implements OnInit {
  private toast = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private userService = inject(UserService);

  users = signal<User[]>([]);
  isLoading = signal(false);

  // Pagination
  currentPage = signal(0);
  pageSize = signal(10);
  totalElements = signal(0);
  totalPages = signal(0);

  // Filters
  searchQuery = signal('');
  selectedRole = signal<'ALL' | 'MEMBER' | 'TRAINER' | 'OWNER' | 'ADMIN'>('ALL');
  lockRoleFilter = signal(false);
  sortBy = signal<'name' | 'email' | 'createdAt'>('createdAt');
  sortOrder = signal<'asc' | 'desc'>('desc');

  Math = Math;

  ngOnInit(): void {
    const defaultRole = this.route.snapshot.data['defaultRole'] as
      | 'MEMBER'
      | 'TRAINER'
      | 'OWNER'
      | 'ADMIN'
      | undefined;
    const lockRole = !!this.route.snapshot.data['lockRoleFilter'];
    if (defaultRole) {
      this.selectedRole.set(defaultRole);
    }
    this.lockRoleFilter.set(lockRole);
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);

    const query = this.searchQuery();
    const page = this.currentPage();
    const size = this.pageSize();
    const selectedRole = this.selectedRole();

    // When role filter is used, fetch larger window and paginate client-side.
    // This preserves behavior without backend API changes.
    const useClientRoleFilter = selectedRole !== 'ALL';
    const request$ = query
      ? this.userService.searchUsers(query, useClientRoleFilter ? 0 : page, useClientRoleFilter ? 1000 : size)
      : this.userService.getAllUsers(useClientRoleFilter ? 0 : page, useClientRoleFilter ? 1000 : size);

    request$.subscribe({
      next: (pageData) => {
        let records = [...(pageData.content ?? [])];

        if (selectedRole !== 'ALL') {
          records = records.filter((user) => user.role === selectedRole);
        }

        if (this.sortBy() === 'name') {
          records.sort((a, b) =>
            `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`),
          );
        } else if (this.sortBy() === 'email') {
          records.sort((a, b) => a.email.localeCompare(b.email));
        } else {
          records.sort(
            (a, b) =>
              new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime(),
          );
        }

        if (this.sortOrder() === 'asc') {
          records.reverse();
        }

        if (useClientRoleFilter) {
          const start = page * size;
          const end = start + size;
          this.users.set(records.slice(start, end));
          this.totalElements.set(records.length);
          this.totalPages.set(Math.max(1, Math.ceil(records.length / size)));
        } else {
          this.users.set(records);
          this.totalElements.set(pageData.totalElements);
          this.totalPages.set(pageData.totalPages);
        }
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.toast.error('Failed to load users');
        this.isLoading.set(false);
      },
    });
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
    this.currentPage.set(0);
    this.loadUsers();
  }

  onRoleChange(role: 'ALL' | 'MEMBER' | 'TRAINER' | 'OWNER' | 'ADMIN'): void {
    if (this.lockRoleFilter()) return;
    this.selectedRole.set(role);
    this.currentPage.set(0);
    this.loadUsers();
  }

  onSortChange(sortBy: 'name' | 'email' | 'createdAt'): void {
    // Note: Backend sort support would be needed here.
    // Currently staying with default backend sort (usually ID or created).
    this.sortBy.set(sortBy);
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(0);
    this.loadUsers();
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((page) => page + 1);
      this.loadUsers();
    }
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadUsers();
  }

  navigateToNew(): void {
    if (this.lockRoleFilter() && this.selectedRole() === 'TRAINER') {
      this.router.navigate(['/admin/users/new'], { queryParams: { role: 'TRAINER' } });
      return;
    }
    this.router.navigate(['/admin/users/new']);
  }

  navigateToEdit(userId: string): void {
    this.router.navigate(['/admin/users', userId, 'edit']);
  }

  async deleteUser(event: Event, userId: string, userName: string): Promise<void> {
    event.stopPropagation();

    const confirmed = await this.toast.confirm(
      `Delete user "${userName}"?`,
      'This action cannot be undone.',
      'Delete user',
    );
    if (!confirmed) return;

    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.toast.success('User deleted successfully');
        this.loadUsers();
      },
      error: (error) => {
        console.error('Error deleting user:', error);
        this.toast.error('Failed to delete user');
      },
    });
  }

  approveUser(event: Event, userId: string, userName: string): void {
    event.stopPropagation();
    this.userService.approveUser(userId).subscribe({
      next: () => {
        this.toast.success(`${userName} approved successfully`);
        this.loadUsers();
      },
      error: () => this.toast.error('Failed to approve user'),
    });
  }

  rejectUser(event: Event, userId: string, userName: string): void {
    event.stopPropagation();
    this.userService.rejectUser(userId).subscribe({
      next: () => {
        this.toast.success(`${userName} rejected successfully`);
        this.loadUsers();
      },
      error: () => this.toast.error('Failed to reject user'),
    });
  }

  deactivateUser(event: Event, userId: string, userName: string, currentStatus: boolean): void {
    event.stopPropagation();
    this.userService.deactivateUser(userId).subscribe({
      next: () => {
        this.toast.success(`${userName} ${currentStatus ? 'deactivated' : 'activated'} successfully`);
        this.loadUsers();
      },
      error: () => this.toast.error(`Failed to ${currentStatus ? 'deactivate' : 'activate'} user`),
    });
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  }

  getInitials(firstName: string, lastName: string): string {
    return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
  }

  getRoleColor(role?: string | null): string {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400';
      case 'TRAINER':
        return 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400';
      case 'OWNER':
        return 'bg-amber-100 text-amber-800 dark:bg-amber-900/20 dark:text-amber-400';
      case 'MEMBER':
      default:
        return 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400';
    }
  }

  getStatusColor(status?: string): string {
    switch (status) {
      case 'APPROVED':
        return 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/20 dark:text-emerald-400';
      case 'REJECTED':
        return 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400';
      case 'PENDING':
      default:
        return 'bg-amber-100 text-amber-800 dark:bg-amber-900/20 dark:text-amber-400';
    }
  }

  get visiblePages(): number[] {
    const maxPages = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];

    const start = Math.max(0, current - 2);
    const end = Math.min(maxPages - 1, current + 2);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  }

  pageTitle(): string {
    return this.lockRoleFilter() && this.selectedRole() === 'TRAINER'
      ? 'Trainer Management'
      : 'User Management';
  }

  pageSubtitle(): string {
    return this.lockRoleFilter() && this.selectedRole() === 'TRAINER'
      ? 'Manage trainers and coaching accounts'
      : 'Manage system users and permissions';
  }

  addButtonLabel(): string {
    return this.lockRoleFilter() && this.selectedRole() === 'TRAINER' ? 'Add Trainer' : 'Add User';
  }
}
