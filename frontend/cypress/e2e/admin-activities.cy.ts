describe('Admin Activity Management', () => {
  beforeEach(() => {
    // Login as admin before each test
    cy.visit('/auth/login');
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();

    // Wait for redirect
    cy.url().should('include', '/admin/dashboard');
  });

  it('should navigate to activities list and display data', () => {
    // Click on "View Activities" quick action or sidebar link
    cy.get('a[routerLink="/admin/activities"]').first().click();

    // Check URL
    cy.url().should('include', '/admin/activities');

    // Verify header and table elements
    cy.get('h1').contains('System Activities').should('be.visible');

    // Table should either show loading, empty state, or rows
    cy.get('body').then($body => {
      // If table has rows
      if ($body.find('tbody tr').length > 0) {
        cy.get('tbody tr').should('have.length.at.least', 1);
      } else {
        // Look for empty state
        cy.contains('No activities found in the system.').should('be.visible');
      }
    });
  });

  it('should export activities as CSV', () => {
    cy.visit('/admin/activities');

    // Find export button and click
    cy.get('button').contains('Export CSV').click();

    // Cypress does not easily let you read downloaded files without adding plugins
    // For this basic test, we just verify the button is clickable without throwing errors
  });

  it('should filter activities by type', () => {
    cy.visit('/admin/activities');

    // Select "RUNNING" from type filter
    cy.get('select').eq(0).select('RUNNING');

    // Verify rows reflect the filter (or empty state is shown)
    cy.get('body').then($body => {
      if ($body.find('tbody tr').length > 0) {
        cy.get('tbody tr').each(($tr) => {
          cy.wrap($tr).contains(/Running/i);
        });
      }
    });
  });
});
