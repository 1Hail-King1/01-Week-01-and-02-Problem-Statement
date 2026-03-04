import java.util.*;

public class Problem10_MultiLevelCache {

    static class VideoData {
        String videoId, title;
        VideoData(String id, String title) { this.videoId = id; this.title = title; }
    }

    // L1: in-memory LRU via access-order LinkedHashMap
    private final LinkedHashMap<String, VideoData> l1Cache;
    // L2: simulated SSD-backed store
    private final HashMap<String, VideoData> l2Cache = new HashMap<>();
    // L3: database
    private final HashMap<String, VideoData> database = new HashMap<>();
    // Access counts for promotion decisions
    private final HashMap<String, Integer> accessCount = new HashMap<>();

    private final int l1Max, l2Max, promoteThreshold;
    private int l1Hits, l2Hits, l3Hits, misses;

    public Problem10_MultiLevelCache(int l1Max, int l2Max, int promoteThreshold) {
        this.l1Max = l1Max;
        this.l2Max = l2Max;
        this.promoteThreshold = promoteThreshold;

        this.l1Cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > l1Max;
            }
        };
    }

    public void seedDatabase(String videoId, String title) {
        database.put(videoId, new VideoData(videoId, title));
    }

    public VideoData getVideo(String videoId) {
        if (l1Cache.containsKey(videoId)) { l1Hits++; return l1Cache.get(videoId); }

        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            VideoData video = l2Cache.get(videoId);
            int count = accessCount.merge(videoId, 1, Integer::sum);
            if (count >= promoteThreshold) {
                l1Cache.put(videoId, video); // promote to L1
            }
            return video;
        }

        if (database.containsKey(videoId)) {
            l3Hits++;
            VideoData video = database.get(videoId);
            if (l2Cache.size() >= l2Max) evictL2();
            l2Cache.put(videoId, video);
            accessCount.put(videoId, 1);
            return video;
        }

        misses++;
        return null;
    }

    public void invalidate(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        accessCount.remove(videoId);
    }

    private void evictL2() {
        l2Cache.entrySet().stream()
                .min(Comparator.comparingInt(e -> accessCount.getOrDefault(e.getKey(), 0)))
                .map(Map.Entry::getKey)
                .ifPresent(k -> { l2Cache.remove(k); accessCount.remove(k); });
    }

    public CacheStats getStatistics() {
        int total = l1Hits + l2Hits + l3Hits + misses;
        return new CacheStats(
                total == 0 ? 0 : l1Hits * 100.0 / total,
                total == 0 ? 0 : l2Hits * 100.0 / total,
                total == 0 ? 0 : l3Hits * 100.0 / total,
                total == 0 ? 0 : (l1Hits + l2Hits + l3Hits) * 100.0 / total,
                l1Cache.size(), l2Cache.size(), total
        );
    }

    public record CacheStats(double l1HitRate, double l2HitRate, double l3HitRate,
                             double overallHitRate, int l1Size, int l2Size,
                             int totalRequests) {}
}