/**
 * E2E test: Class booking flow for MEMBER users.
 * Verifies: list → book → WebSocket toast notification.
 */
describe('Class Booking Flow', () => {
  beforeEach(() => {
    cy.session('member-session', () => {
      cy.visit('/auth/login');
      cy.get('[data-cy="email-input"]').type('member@test.com');
      cy.get('[data-cy="password-input"]').type('Test@123');
      cy.get('[data-cy="login-btn"]').click();
      cy.url().should('include', '/member/dashboard');
    });
  });

  it('should display available classes', () => {
    cy.visit('/member/classes');
    cy.get('[data-cy="class-card"]').should('have.length.greaterThan', 0);
  });

  it('should book an available class and show confirmation toast', () => {
    cy.visit('/member/classes');
    // Click book on the first available class
    cy.get('[data-cy="book-class-btn"]').first().click();
    // Verify toast or success indicator
    cy.get('.swal2-success, [data-cy="success-toast"]', { timeout: 10000 })
      .should('be.visible');
  });

  it('should show booked class in My Bookings tab', () => {
    cy.visit('/member/classes');
    cy.get('[data-cy="book-class-btn"]').first().click();
    cy.get('.swal2-confirm').click(); // Dismiss

    // Navigate to bookings
    cy.get('[data-cy="my-bookings-tab"]').click();
    cy.get('[data-cy="booking-item"]').should('have.length.greaterThan', 0);
  });

  it('should prevent double booking of the same class', () => {
    cy.visit('/member/classes');
    cy.get('[data-cy="book-class-btn"]').first().click();
    cy.get('.swal2-confirm').click(); // Dismiss success

    // Try to book same class again
    cy.get('[data-cy="book-class-btn"]').first().click();
    cy.get('.swal2-error, [data-cy="error-toast"]', { timeout: 5000 }).should('be.visible');
  });
});
