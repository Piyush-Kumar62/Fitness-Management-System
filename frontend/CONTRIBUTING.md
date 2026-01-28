# Contributing to Fitness Management System

Thank you for your interest in contributing to the Fitness Management System! This document provides guidelines for contributing to this project.

## Code of Conduct

Please be respectful and constructive in all interactions. We are committed to providing a welcoming and inspiring community for all.

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue on GitHub with:

1. **Clear Title**: Briefly describe the issue
2. **Description**: Detailed explanation of the problem
3. **Steps to Reproduce**: List steps to reproduce the behavior
4. **Expected Behavior**: What you expected to happen
5. **Actual Behavior**: What actually happened
6. **Screenshots**: If applicable, add screenshots
7. **Environment**:
   - OS (Windows/Mac/Linux)
   - Browser (Chrome/Firefox/Safari)
   - Node.js version
   - Angular version

### Suggesting Features

We welcome feature suggestions! Please create an issue with:

1. **Feature Title**: Clear, concise title
2. **Problem Statement**: What problem does this solve?
3. **Proposed Solution**: How should this feature work?
4. **Alternatives**: Other solutions you've considered
5. **Additional Context**: Any other relevant information

## Development Workflow

### 1. Fork the Repository

Click the "Fork" button at the top right of the repository page.

### 2. Clone Your Fork

```bash
git clone https://github.com/YOUR_USERNAME/fitness-frontend.git
cd fitness-frontend
```

### 3. Create a Branch

```bash
# Create a feature branch
git checkout -b feature/amazing-feature

# Or a bug fix branch
git checkout -b fix/bug-description
```

### 4. Make Your Changes

- Follow the [Coding Standards](#coding-standards)
- Write or update tests as needed
- Update documentation if needed

### 5. Test Your Changes

```bash
# Run tests
npm test

# Build to check for errors
npm run build:prod

# Test locally
npm start
```

### 6. Commit Your Changes

Use [Conventional Commits](https://www.conventionalcommits.org/):

```bash
# Feature
git commit -m "feat: add user export functionality"

# Bug fix
git commit -m "fix: resolve login redirect issue"

# Documentation
git commit -m "docs: update README installation steps"

# Style changes
git commit -m "style: format code with prettier"

# Refactoring
git commit -m "refactor: simplify auth service logic"

# Tests
git commit -m "test: add tests for activity service"

# Chores
git commit -m "chore: update dependencies"
```

### 7. Push to Your Fork

```bash
git push origin feature/amazing-feature
```

### 8. Create a Pull Request

1. Go to the original repository
2. Click "New Pull Request"
3. Select your fork and branch
4. Fill in the PR template
5. Submit the pull request

## Pull Request Guidelines

### PR Title Format

Use conventional commit format:

- `feat: add new feature`
- `fix: resolve bug`
- `docs: update documentation`
- `style: format code`
- `refactor: improve code structure`
- `test: add or update tests`
- `chore: update dependencies`

### PR Description

Include:

1. **What**: What changes does this PR introduce?
2. **Why**: Why are these changes needed?
3. **How**: How were these changes implemented?
4. **Testing**: How were these changes tested?
5. **Screenshots**: For UI changes, include before/after screenshots
6. **Related Issues**: Link to related issues (e.g., "Closes #123")

### PR Checklist

Before submitting, ensure:

- [ ] Code follows project coding standards
- [ ] All tests pass (`npm test`)
- [ ] New tests added for new features
- [ ] Documentation updated if needed
- [ ] No console errors or warnings
- [ ] Code compiles without errors (`npm run build:prod`)
- [ ] Branch is up to date with main
- [ ] Commit messages follow conventional commits

## Coding Standards

### TypeScript

```typescript
// ✅ Good: Use meaningful names
getUserActivities(): Observable<Activity[]> {
  return this.http.get<Activity[]>(`${this.apiUrl}/activities`);
}

// ❌ Bad: Generic names
getData(): Observable<any> {
  return this.http.get(`${this.apiUrl}/activities`);
}

// ✅ Good: Use TypeScript types
interface User {
  id: string;
  email: string;
  firstName: string;
}

// ❌ Bad: Using 'any'
interface User {
  id: any;
  email: any;
  firstName: any;
}

// ✅ Good: Use Angular Signals
readonly user = signal<User | null>(null);
readonly isLoggedIn = computed(() => this.user() !== null);

// ✅ Good: Use dependency injection
export class UserService {
  private http = inject(HttpClient);
}
```

### Component Structure

```typescript
// Recommended component structure
@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss',
})
export class UserDashboardComponent implements OnInit {
  // 1. Injected dependencies
  private userService = inject(UserService);

  // 2. Signals
  user = signal<User | null>(null);
  isLoading = signal(false);

  // 3. Computed signals
  userName = computed(() => {
    const u = this.user();
    return u ? `${u.firstName} ${u.lastName}` : '';
  });

  // 4. Lifecycle hooks
  ngOnInit(): void {
    this.loadUser();
  }

  // 5. Public methods
  loadUser(): void {
    this.isLoading.set(true);
    // ...
  }

  // 6. Private methods
  private processUserData(data: User): void {
    // ...
  }
}
```

### HTML Templates

```html
<!-- ✅ Good: Use @if and @for (Angular 17+) -->
@if (user()) {
<div>{{ user().name }}</div>
} @for (activity of activities(); track activity.id) {
<div>{{ activity.type }}</div>
}

<!-- ✅ Good: Use signal bindings -->
<div>{{ userName() }}</div>

<!-- ✅ Good: Use proper event handling -->
<button (click)="loadUser()">Load User</button>

<!-- ❌ Bad: Inline complex logic -->
<div>{{ user ? user.firstName + ' ' + user.lastName : 'Guest' }}</div>
```

### SCSS Styles

```scss
// ✅ Good: Use Tailwind classes first
<div class="flex items-center gap-4 p-4 bg-white dark:bg-gray-800">

// ✅ Good: Component-specific styles for complex cases
.user-card {
  &__header {
    @apply flex items-center justify-between;
  }

  &__avatar {
    @apply w-12 h-12 rounded-full;
  }
}

// ❌ Bad: Recreating Tailwind utilities
.flex-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}
```

### File Naming

- Components: `user-dashboard.component.ts`
- Services: `auth.service.ts`
- Models: `user.model.ts`
- Guards: `auth.guard.ts`
- Interceptors: `auth.interceptor.ts`
- Pipes: `date-format.pipe.ts`

### Folder Structure

Follow the existing structure:

```
src/app/
├── core/           # Singleton services, guards, interceptors
├── features/       # Feature modules
├── shared/         # Shared components, pipes, directives
└── layouts/        # Layout wrappers
```

## Testing Guidelines

### Unit Tests

```typescript
// Example component test
describe('UserDashboardComponent', () => {
  let component: UserDashboardComponent;
  let fixture: ComponentFixture<UserDashboardComponent>;
  let userService: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['getProfile']);

    await TestBed.configureTestingModule({
      imports: [UserDashboardComponent],
      providers: [{ provide: UserService, useValue: userServiceSpy }],
    }).compileComponents();

    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    fixture = TestBed.createComponent(UserDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user on init', () => {
    const mockUser = { id: '1', name: 'Test User' };
    userService.getProfile.and.returnValue(of(mockUser));

    component.ngOnInit();

    expect(component.user()).toEqual(mockUser);
  });
});
```

### Service Tests

```typescript
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should login successfully', () => {
    const mockResponse = { token: 'abc123', user: { id: '1' } };

    service.login('test@example.com', 'password').subscribe((response) => {
      expect(response.token).toBe('abc123');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
```

## Documentation

### Code Comments

```typescript
/**
 * Retrieves user activities with optional filtering
 * @param userId - The ID of the user
 * @param type - Optional activity type filter
 * @returns Observable of activities array
 */
getUserActivities(userId: string, type?: ActivityType): Observable<Activity[]> {
  // Implementation
}
```

### README Updates

If your changes affect usage:

- Update installation steps
- Add new configuration options
- Update examples
- Add troubleshooting tips

## Review Process

1. **Automated Checks**: CI/CD pipeline runs tests and linting
2. **Code Review**: Maintainers review your code
3. **Feedback**: Address any requested changes
4. **Approval**: Once approved, your PR will be merged
5. **Thank You**: You'll be added to contributors list!

## Getting Help

- **Questions**: Open a discussion on GitHub
- **Bugs**: Create an issue with bug template
- **Features**: Create an issue with feature template
- **Chat**: Join our community (if available)

## Recognition

Contributors will be:

- Added to the Contributors section
- Mentioned in release notes
- Given credit in documentation

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing! 🎉
