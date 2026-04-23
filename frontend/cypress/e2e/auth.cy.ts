describe('Authentication Flow', () => {
  beforeEach(() => {
    // Clear local storage and cookies before each test
    cy.clearLocalStorage();
    cy.clearCookies();
  });

  it('should successfully register a new user', () => {
    cy.visit('/auth/register');

    // Generate random email to avoid conflicts
    const randomEmail = `testuser${Math.floor(Math.random() * 10000)}@example.com`;

    cy.get('input[formControlName="firstName"]').type('Test');
    cy.get('input[formControlName="lastName"]').type('User');
    cy.get('input[formControlName="email"]').type(randomEmail);
    cy.get('input[formControlName="password"]').type('password123');

    cy.get('button[type="submit"]').click();

    // Since we redirect to login or dashboard, check for token or successful navigation
    cy.url().should('include', '/member/dashboard');
    cy.window().its('localStorage.token').should('exist');
  });

  it('should successfully log in as admin', () => {
    cy.visit('/auth/login');

    // Assuming there's a seeded admin user in the system
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');

    cy.get('button[type="submit"]').click();

    // Verify navigation to admin dashboard
    cy.url().should('include', '/admin/dashboard');
    cy.get('h1').contains('Admin Dashboard').should('be.visible');
    
    // Check local storage for token and role
    cy.window().its('localStorage.token').should('exist');
    cy.window().its('localStorage.user').should('include', 'ADMIN');
  });

  it('should show error for invalid credentials', () => {
    cy.visit('/auth/login');

    cy.get('input[formControlName="email"]').type('invalid@example.com');
    cy.get('input[formControlName="password"]').type('wrongpassword');

    cy.get('button[type="submit"]').click();

    // Expected error handling (adjust based on actual implementation)
    // Could be a toast notification or an inline error text
    cy.get('.swal-modal').should('be.visible');
    cy.get('.swal-title').contains('Login Failed').should('be.visible');
  });
});
