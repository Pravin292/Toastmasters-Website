package com.rathinam.toastmasters.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 10;
    private static final long WINDOW_DURATION_MS = 60_000L; // 1 minute

    private final ConcurrentHashMap<String, Deque<Long>> clientRequestMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Rate limit critical authentication endpoints
        if (isRateLimitedPath(path)) {
            String clientIp = getClientIp(request);
            long now = System.currentTimeMillis();
            long windowStart = now - WINDOW_DURATION_MS;

            Deque<Long> timestamps = clientRequestMap.computeIfAbsent(clientIp, k -> new ConcurrentLinkedDeque<>());

            boolean allowed;
            synchronized (timestamps) {
                while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                    timestamps.pollFirst();
                }

                if (timestamps.size() >= MAX_REQUESTS_PER_WINDOW) {
                    allowed = false;
                } else {
                    timestamps.addLast(now);
                    allowed = true;
                }
            }

            // Periodically clean up idle IPs to avoid memory growth
            if (clientRequestMap.size() > 500) {
                cleanupExpiredEntries(windowStart);
            }

            if (!allowed) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Too many attempts from this IP. Please wait a minute before trying again.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedPath(String path) {
        return path != null && (
                path.endsWith("/api/v1/auth/login") ||
                path.endsWith("/api/v1/auth/register")
        );
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void cleanupExpiredEntries(long windowStart) {
        clientRequestMap.entrySet().removeIf(entry -> {
            Deque<Long> dq = entry.getValue();
            synchronized (dq) {
                while (!dq.isEmpty() && dq.peekFirst() < windowStart) {
                    dq.pollFirst();
                }
                return dq.isEmpty();
            }
        });
    }
}
