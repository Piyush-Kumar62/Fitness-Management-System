# Fitness Management System Refactor Guide

## Target Modular Backend Structure

```text
backend/src/main/java/com/project/fitness
  common/
    exception/
    response/
    util/
  modules/
    user/application/
    gym/application/
    membership/application/
    payment/application/
    fitness/application/
  domain/
    user/
    gym/
    membership/
    payment/
    fitness/
    trainer/
    notification/
  security/
    SecurityConfig.java
    JwtAuthenticationFilter.java
    ...
```

## Implemented Changes

1. Added application layer services per module:
   - `modules/user/application/UserApplicationService.java`
   - `modules/gym/application/GymApplicationService.java`
   - `modules/membership/application/MembershipApplicationService.java`
   - `modules/payment/application/PaymentApplicationService.java`
   - `modules/fitness/application/FitnessApplicationService.java`
2. Rewired controllers to call module application services (controller -> application -> domain service).
3. Added API compatibility for membership routes:
   - `@RequestMapping({"/api", "/api/v1"})` in membership controller.
4. Security refactor:
   - split OAuth and API/JWT processing into separate ordered filter chains.
   - preserved role matrix and added legacy `/api/*` matcher compatibility.
5. Standardized v1 JSON response envelope:
   - `ApiResponseBodyAdvice` wraps `/api/v1/**` JSON responses in `ApiResponse`.
6. Frontend compatibility updates:
   - `ApiService` now unwraps `ApiResponse<T>` automatically.
   - `AuthService` now unwraps both raw and envelope responses.
7. Global layout system additions:
   - added `:root` spacing/container tokens plus `.app-container` and `.app-section` utility classes in `frontend/src/styles.scss`.

## Frontend Reusable Landing Components

Reusable landing primitives are in:
- `frontend/src/app/features/landing/components/hero-section.component.ts`
- `frontend/src/app/features/landing/components/ui/stats-card.component.ts`
- `frontend/src/app/features/landing/components/ui/landing-button.component.ts`

These are now cleanly reusable and integrated into a mobile-first layout.

## Safe Migration Steps

1. Keep existing domain services untouched.
2. Move orchestration logic to `modules/*/application`.
3. Update controllers to use application services only.
4. Keep backward-compatible routes while introducing `/api/v1`.
5. Enable v1 envelope standardization and keep frontend dual-format support.
6. Roll out frontend section-by-section with shared layout tokens/classes.
7. Run compile/build checks after each slice.

## Best Practices Applied

- Layering: Controller -> Application Use Case -> Domain Service -> Repository.
- Backward compatibility preserved for existing consumers.
- Security concerns separated by flow (OAuth vs JWT API).
- API format consistency introduced without forcing immediate consumer rewrites.
- Mobile-first, shared spacing/container system for maintainable UI alignment.
