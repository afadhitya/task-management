package com.afadhitya.taskmanagement.infrastructure.filter;

import com.afadhitya.taskmanagement.infrastructure.security.JwtService;
import com.afadhitya.taskmanagement.infrastructure.service.BucketProvider;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class RateLimitFilter extends OncePerRequestFilter {

    private final BucketProvider bucketProvider;
    private final JwtService jwtService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_KEY_HEADER = "X-Api-Key";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String identifier = extractIdentifier(request);
        boolean isApiKey = isApiKeyRequest(request);

        Bucket bucket = isApiKey
            ? bucketProvider.getApiKeyBucket(identifier)
            : bucketProvider.getDefaultBucket(identifier);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for identifier: {}, URI: {}", identifier, request.getRequestURI());
            sendRateLimitResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setHeader("Retry-After", "60");
        response.getWriter().write(
            "{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Rate limit exceeded. Retry after 60 seconds.\"}"
        );
    }

    private String extractIdentifier(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        if (jwt != null && jwtService.isTokenValid(jwt) && !jwtService.isRefreshToken(jwt)) {
            try {
                Long userId = jwtService.extractUserId(jwt);
                return "user:" + userId;
            } catch (Exception e) {
                log.debug("Could not extract userId from JWT, falling back to IP");
            }
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (StringUtils.hasText(apiKey)) {
            return "apikey:" + apiKey;
        }

        String clientIp = extractClientIp(request);
        return "ip:" + clientIp;
    }

    private boolean isApiKeyRequest(HttpServletRequest request) {
        return StringUtils.hasText(request.getHeader(API_KEY_HEADER));
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
