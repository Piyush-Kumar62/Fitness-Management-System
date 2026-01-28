import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast.service';
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

  // Computed filtered users
  filteredUsers = computed(() => {
    let filtered = this.users();

    // Search filter
    const query = this.searchQuery().toLowerCase();
    if (query) {
      filtered = filtered.filter(
        (u) =>
          u.firstName.toLowerCase().includes(query) ||
          u.lastName.toLowerCase().includes(query) ||
          u.email.toLowerCase().includes(query),
      );
    }

    // Role filter
    const role = this.selectedRole();
    if (role !== 'ALL') {
      filtered = filtered.filter((u) => u.role === role);
    }

    // Sort
    const sortBy = this.sortBy();
    const order = this.sortOrder();
    filtered = [...filtered].sort((a, b) => {
      let comparison = 0;

      switch (sortBy) {
        case 'name':
          comparison = `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`);
          break;
        case 'email':
          comparison = a.email.localeCompare(b.email);
          break;
        case 'createdAt':
          comparison =
            new Date(a.createdAt || '').getTime() - new Date(b.createdAt || '').getTime();
          break;
      }

      return order === 'asc' ? comparison : -comparison;
    });

    return filtered;
  });

  // Paginated users
  paginatedUsers = computed(() => {
    const filtered = this.filteredUsers();
    const start = this.currentPage() * this.pageSize();
    const end = start + this.pageSize();
    return filtered.slice(start, end);
  });

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);

    // TODO: Replace with actual admin API call
    // Mock data for demonstration
    setTimeout(() => {
      const mockUsers: User[] = [
        {
          id: '1',
          email: 'john.doe@example.com',
          firstName: 'John',
          lastName: 'Doe',
          role: UserRole.USER,
          createdAt: '2025-01-15T10:00:00Z',
          updatedAt: '2025-01-20T15:30:00Z',
        },
        {
          id: '2',
          email: 'jane.smith@example.com',
          firstName: 'Jane',
          lastName: 'Smith',
          role: UserRole.ADMIN,
          createdAt: '2025-01-10T08:00:00Z',
          updatedAt: '2025-01-25T12:00:00Z',
        },
        {
          id: '3',
          email: 'mike.johnson@example.com',
          firstName: 'Mike',
          lastName: 'Johnson',
          role: UserRole.USER,
          createdAt: '2025-01-20T14:00:00Z',
          updatedAt: '2025-01-26T09:00:00Z',
        },
      ];

      this.users.set(mockUsers);
      this.totalElements.set(mockUsers.length);
      this.totalPages.set(Math.ceil(mockUsers.length / this.pageSize()));
      this.isLoading.set(false);
    }, 1000);
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
    this.currentPage.set(0);
  }

  onRoleChange(role: 'ALL' | 'USER' | 'ADMIN'): void {
    this.selectedRole.set(role);
    this.currentPage.set(0);
  }

  onSortChange(sortBy: 'name' | 'email' | 'createdAt'): void {
    if (this.sortBy() === sortBy) {
      this.sortOrder.update((order) => (order === 'asc' ? 'desc' : 'asc'));
    } else {
      this.sortBy.set(sortBy);
      this.sortOrder.set('desc');
    }
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(0);
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
    }
  }

  nextPage(): void {
    const maxPage = Math.ceil(this.filteredUsers().length / this.pageSize()) - 1;
    if (this.currentPage() < maxPage) {
      this.currentPage.update((page) => page + 1);
    }
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
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
      // TODO: Implement delete API call
      this.toast.success('User deleted successfully');
      this.loadUsers();
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
    const maxPages = Math.ceil(this.filteredUsers().length / this.pageSize());
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
