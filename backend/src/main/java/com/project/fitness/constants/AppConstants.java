package com.project.fitness.constants;

/**
 * Application-wide constants.
 * <p>
 * Centralises magic strings / numbers so they are defined exactly once
 * and can be referenced from controllers, filters, and services without
 * duplication.
 * </p>
 */
public final class AppConstants {

    private AppConstants() {
        // utility class — do not instantiate
    }

    // ── API ─────────────────────────────────────────────────────────────
    /** Base path prefix for all REST endpoints. */
    public static final String API_V1 = "/api/v1";

    // ── JWT / Security ───────────────────────────────────────────────────
    /** Authorization header name. */
    public static final String AUTH_HEADER = "Authorization";

    /** Bearer token prefix. */
    public static final String BEARER_PREFIX = "Bearer ";

    // ── Pagination defaults ──────────────────────────────────────────────
    public static final int DEFAULT_PAGE      = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE     = 100;

    // ── Roles ────────────────────────────────────────────────────────────
    public static final String ROLE_ADMIN   = "ADMIN";
    public static final String ROLE_OWNER   = "OWNER";
    public static final String ROLE_TRAINER = "TRAINER";
    public static final String ROLE_USER    = "USER";

    // ── Correlation / Logging ────────────────────────────────────────────
    /** MDC key used to propagate a per-request correlation ID in logs. */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC    = "correlationId";
}
