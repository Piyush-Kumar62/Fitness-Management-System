import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast.service';
import { UserService } from '../../../../core/services/user.service';
import { User, UserRole } from '../../../../core/models/user.model';

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
  selectedRole = signal<'ALL' | 'USER' | 'ADMIN'>('ALL');
  sortBy = signal<'name' | 'email' | 'createdAt'>('createdAt');
  sortOrder = signal<'asc' | 'desc'>('desc');

  Math = Math;

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);

    const query = this.searchQuery();
    const page = this.currentPage();
    const size = this.pageSize();

    const request$ = query
      ? this.userService.searchUsers(query, page, size)
      : this.userService.getAllUsers(page, size);

      request$.subscribe({
        next: (pageData) => {
          this.users.set(pageData.content);
          this.totalElements.set(pageData.totalElements);
          this.totalPages.set(pageData.totalPages);
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

  onRoleChange(role: 'ALL' | 'USER' | 'ADMIN'): void {
    this.selectedRole.set(role);
    this.currentPage.set(0);
    // Note: Role filtering should ideally be done on the backend now. 
    // For now, we are displaying limits of search/all. 
    // If backend doesn't support role filtering in search, we might need to add it or note it as a limitation.
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
    this.router.navigate(['/admin/users/new']);
  }

  navigateToEdit(userId: string): void {
    this.router.navigate(['/admin/users', userId, 'edit']);
  }

  deleteUser(event: Event, userId: string, userName: string): void {
    event.stopPropagation();

    if (confirm(`Are you sure you want to delete user "${userName}"?`)) {
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

  getRoleColor(role: string): string {
     return role === 'ADMIN'
      ? 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400'
      : 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400';
  }

  get visiblePages(): number[] {
    const maxPages = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];

    let start = Math.max(0, current - 2);
    let end = Math.min(maxPages - 1, current + 2);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  }
}
