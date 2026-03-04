import java.util.*;

public class problem3_dns_cache {

    static class DNSEntry {
        String ipAddress;
        long createdAt;
        long ttlMillis;

        DNSEntry(String ip, long ttlSeconds) {
            this.ipAddress = ip;
            this.createdAt = System.currentTimeMillis();
            this.ttlMillis = ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > createdAt + ttlMillis;
        }
    }

    private HashMap<String, DNSEntry> cache = new HashMap<>();
    private HashMap<String, String> upstreamDNS = new HashMap<>();
    private int hits = 0, misses = 0;

    public void addUpstreamRecord(String domain, String ip) {
        upstreamDNS.put(domain, ip);
    }

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            return entry.ipAddress;
        }

        // expired or not cached — query upstream
        if (entry != null) cache.remove(domain);
        misses++;
        return queryUpstream(domain, 300);
    }

    private String queryUpstream(String domain, long ttlSeconds) {
        String ip = upstreamDNS.getOrDefault(domain, null);
        if (ip != null) cache.put(domain, new DNSEntry(ip, ttlSeconds));
        return ip;
    }

    public void invalidate(String domain) {
        cache.remove(domain);
    }

    public void clearExpiredEntries() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    public double getHitRate() {
        int total = hits + misses;
        return total == 0 ? 0 : (hits * 100.0 / total);
    }

    public int getCacheSize() { return cache.size(); }
    public int getHits()      { return hits; }
    public int getMisses()    { return misses; }
}