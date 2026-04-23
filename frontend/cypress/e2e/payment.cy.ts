/**
 * E2E test: Stripe payment flow for membership purchase.
 * Uses Stripe test card: 4242 4242 4242 4242
 */
describe('Stripe Payment Flow', () => {
  beforeEach(() => {
    // Login as a MEMBER user
    cy.session('member-session', () => {
      cy.visit('/auth/login');
      cy.get('[data-cy="email-input"]').type('member@test.com');
      cy.get('[data-cy="password-input"]').type('Test@123');
      cy.get('[data-cy="login-btn"]').click();
      cy.url().should('include', '/member/dashboard');
    });
  });

  it('should display membership plans on marketplace', () => {
    cy.visit('/member/memberships');
    cy.get('[data-cy="membership-plan-card"]').should('have.length.greaterThan', 0);
  });

  it('should open Stripe checkout when clicking Subscribe', () => {
    cy.visit('/member/memberships');
    cy.get('[data-cy="subscribe-btn"]').first().click();
    // Stripe checkout dialog or stripe iframe should appear
    cy.get('[data-cy="stripe-checkout-dialog"], iframe[name*="stripe"]', { timeout: 10000 })
      .should('exist');
  });

  it('should complete payment with Stripe test card', () => {
    cy.visit('/member/memberships');
    cy.get('[data-cy="subscribe-btn"]').first().click();

    // Enter card number inside Stripe iframe
    cy.get('iframe[name*="stripe"]', { timeout: 10000 }).then(($iframe) => {
      const body = $iframe.contents().find('body');
      cy.wrap(body).find('[placeholder="Card number"]').type('4242424242424242');
      cy.wrap(body).find('[placeholder="MM / YY"]').type('12/30');
      cy.wrap(body).find('[placeholder="CVC"]').type('123');
    });

    cy.get('[data-cy="pay-btn"]').click();
    // Verify success toast or redirect to membership page
    cy.get('[data-cy="success-toast"], .swal2-success', { timeout: 15000 }).should('be.visible');
  });
});
