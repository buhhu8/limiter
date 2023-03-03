package com.buhhu8.limiter.interception;

import com.buhhu8.limiter.annotation.Intercept;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private static final String HEADER_IP_ADDRESS = "IP-ADDRESS";

    @Value("${limiters.capacity}")
    private Long capacity;
    @Value("${limiters.duration}")
    private Long duration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        Intercept loginRequired = method.getMethodAnnotation(Intercept.class);

        String ipAddress = request.getHeader(HEADER_IP_ADDRESS);
        Bucket tokenBucket = resolveBucket(ipAddress);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return true;
        } else {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.sendError(HttpStatus.BAD_GATEWAY.value());
            return false;
        }
    }
    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }
    private Bucket newBucket(String apiKey) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.intervally(1, Duration.ofMinutes(duration))))
                .build();
    }
}