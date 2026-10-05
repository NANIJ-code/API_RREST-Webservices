package com.apirestjava.example.apirest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String PROTECTED_PATH = "/api/currency/convert";
    private static final int MAX_REQUESTS = 20;
    private static final long WINDOW_MS = 60_000;

    private static class Window {
        volatile long start;
        int count;

        Window(long start) {
            this.start = start;
        }
    }

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    // Ne s'applique qu'à GET /api/currency/convert
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("GET".equals(request.getMethod()) && PROTECTED_PATH.equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        if (windows.size() > 10_000) { // nettoyage des IP inactives
            windows.values().removeIf(w -> now - w.start >= WINDOW_MS);
        }

        Window window = windows.computeIfAbsent(ip, k -> new Window(now));
        boolean allowed;
        long retryAfterSeconds = 0;

        synchronized (window) {
            if (now - window.start >= WINDOW_MS) {
                window.start = now;
                window.count = 0;
            }
            if (window.count < MAX_REQUESTS) {
                window.count++;
                allowed = true;
            } else {
                allowed = false;
                retryAfterSeconds = (window.start + WINDOW_MS - now + 999) / 1000;
            }
        }

        if (allowed) {
            filterChain.doFilter(request, response);
            return;
        }

        String message = "Limite de " + MAX_REQUESTS + " requêtes par minute dépassée. "
                + "Réessayez dans " + retryAfterSeconds + " seconde(s).";
        String json = "{\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":429,"
                + "\"error\":\"Too Many Requests\","
                + "\"message\":\"" + message + "\","
                + "\"path\":\"" + request.getRequestURI() + "\"}";

        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}