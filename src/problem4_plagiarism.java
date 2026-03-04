import java.util.*;

public class problem4_plagiarism {

    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();
    private HashMap<String, Integer> documentNgramCount = new HashMap<>();
    private int N;

    public problem4_plagiarism (int ngramSize) {
        this.N = ngramSize;
    }

    public void indexDocument(String docId, String text) {
        Set<String> ngrams = extractNgrams(text.toLowerCase().split("\\s+"));
        documentNgramCount.put(docId, ngrams.size());
        for (String ngram : ngrams) {
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    private Set<String> extractNgrams(String[] words) {
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < i + N; j++) {
                if (j > i) sb.append(" ");
                sb.append(words[j]);
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    // Returns docId → similarity% map
    public Map<String, Double> analyzeDocument(String text) {
        Set<String> ngrams = extractNgrams(text.toLowerCase().split("\\s+"));
        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String ngram : ngrams) {
            Set<String> docs = ngramIndex.get(ngram);
            if (docs != null) {
                for (String docId : docs) matchCount.merge(docId, 1, Integer::sum);
            }
        }

        Map<String, Double> similarities = new LinkedHashMap<>();
        matchCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> {
                    int total = Math.max(ngrams.size(),
                            documentNgramCount.getOrDefault(e.getKey(), 1));
                    similarities.put(e.getKey(), e.getValue() * 100.0 / total);
                });

        return similarities;
    }

    public boolean isPlagiarized(String text, double threshold) {
        return analyzeDocument(text).values().stream()
                .anyMatch(sim -> sim >= threshold);
    }

    public int getIndexedDocumentCount() { return documentNgramCount.size(); }
    public int getTotalNgrams()          { return ngramIndex.size(); }
}