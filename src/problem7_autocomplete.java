import java.util.*;
import java.util.stream.*;

public class problem7_autocomplete {

    static class TrieNode {
        HashMap<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        String fullWord = null;
    }

    private final TrieNode root = new TrieNode();
    private final HashMap<String, Integer> frequency = new HashMap<>();

    public void addQuery(String query, int freq) {
        frequency.put(query.toLowerCase(), freq);
        TrieNode curr = root;
        for (char c : query.toLowerCase().toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);
        }
        curr.isEndOfWord = true;
        curr.fullWord = query.toLowerCase();
    }

    public void updateFrequency(String query) {
        String q = query.toLowerCase();
        int newFreq = frequency.merge(q, 1, Integer::sum);
        if (newFreq == 1) addQuery(q, 1); // first time — also insert into trie
    }

    // Returns top K suggestions for the given prefix, sorted by frequency
    public List<String> getSuggestions(String prefix, int topK) {
        TrieNode curr = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!curr.children.containsKey(c)) return Collections.emptyList();
            curr = curr.children.get(c);
        }
        List<String> results = new ArrayList<>();
        collectWords(curr, results);
        results.sort((a, b) -> frequency.getOrDefault(b, 0) - frequency.getOrDefault(a, 0));
        return results.stream().limit(topK).collect(Collectors.toList());
    }

    private void collectWords(TrieNode node, List<String> results) {
        if (node.isEndOfWord) results.add(node.fullWord);
        for (TrieNode child : node.children.values()) collectWords(child, results);
    }

    public int getFrequency(String query) {
        return frequency.getOrDefault(query.toLowerCase(), 0);
    }

    public int getTotalQueries() { return frequency.size(); }
}