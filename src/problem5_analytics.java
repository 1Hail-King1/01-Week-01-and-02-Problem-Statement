// ============================================================
// PROBLEM 5: Real-Time Analytics Dashboard
// FILE: Problem5_AnalyticsDashboard.java
// ============================================================
import java.util.*;
import java.util.stream.*;

public class problem5_analytics {

    private HashMap<String, Integer>      pageViews       = new HashMap<>();
    private HashMap<String, Set<String>>  uniqueVisitors  = new HashMap<>();
    private HashMap<String, Integer>      trafficSources  = new HashMap<>();
    private int totalEvents = 0;

    public void processEvent(String url, String userId, String source) {
        pageViews.merge(url, 1, Integer::sum);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        trafficSources.merge(source, 1, Integer::sum);
        totalEvents++;
    }

    // Top N pages by view count
    public List<Map.Entry<String, Integer>> getTopPages(int n) {
        return pageViews.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(n)
                .collect(Collectors.toList());
    }

    public int getUniqueVisitors(String url) {
        return uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
    }

    // Source → percentage
    public Map<String, Double> getTrafficSourceBreakdown() {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        trafficSources.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> breakdown.put(e.getKey(),
                        totalEvents == 0 ? 0 : e.getValue() * 100.0 / totalEvents));
        return breakdown;
    }

    public int getTotalEvents()            { return totalEvents; }
    public int getPageViewCount(String url){ return pageViews.getOrDefault(url, 0); }
    public Map<String, Integer> getAllPageViews() {
        return Collections.unmodifiableMap(pageViews);
    }
}