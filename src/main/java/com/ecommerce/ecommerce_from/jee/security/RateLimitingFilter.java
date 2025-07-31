package com.ecommerce.ecommerce_from.jee.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitingFilter implements Filter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429); // HTTP Too Many Requests
            httpResponse.getWriter().write("Too many requests - Try again later.");
        }
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(10, Duration.ofMinutes(1)); // 10 requêtes / minute
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }
}
