import java.util.concurrent.*;

public class problem6_rate_limiter {

    static class TokenBucket {
        private int tokens;
        private final int maxTokens;
        private long lastRefillTime;
        private final long refillIntervalMs;

        TokenBucket(int maxTokens, long refillIntervalMs) {
            this.tokens = maxTokens;
            this.maxTokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
            this.refillIntervalMs = refillIntervalMs;
        }

        synchronized void refillIfNeeded() {
            long now = System.currentTimeMillis();
            if (now - lastRefillTime >= refillIntervalMs) {
                tokens = maxTokens;
                lastRefillTime = now;
            }
        }

        synchronized boolean consume() {
            refillIfNeeded();
            if (tokens > 0) { tokens--; return true; }
            return false;
        }

        synchronized int getTokens()       { return tokens; }
        synchronized int getUsed()         { return maxTokens - tokens; }
        long getRetryAfterSeconds() {
            return Math.max(0,
                    (refillIntervalMs - (System.currentTimeMillis() - lastRefillTime)) / 1000);
        }
    }

    private final ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private final int maxTokens;
    private final long refillIntervalMs;

    public problem6_rate_limiter(int maxTokens, long refillIntervalMs) {
        this.maxTokens = maxTokens;
        this.refillIntervalMs = refillIntervalMs;
    }

    private TokenBucket getOrCreate(String clientId) {
        return clients.computeIfAbsent(clientId,
                id -> new TokenBucket(maxTokens, refillIntervalMs));
    }

    public boolean checkRateLimit(String clientId) {
        return getOrCreate(clientId).consume();
    }

    public RateLimitStatus getStatus(String clientId) {
        TokenBucket b = getOrCreate(clientId);
        return new RateLimitStatus(b.getUsed(), maxTokens, b.getTokens(),
                b.getRetryAfterSeconds());
    }

    public record RateLimitStatus(int used, int limit, int remaining, long retryAfterSeconds) {}
}