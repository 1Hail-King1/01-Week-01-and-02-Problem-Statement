
import java.util.*;

public class Problem1_UsernameChecker {

    private HashMap<String, Integer> registeredUsers = new HashMap<>();
    private HashMap<String, Integer> attemptFrequency = new HashMap<>();
    private int nextUserId = 1001;

    public void loadExistingUsers(Map<String, Integer> users) {
        registeredUsers.putAll(users);
    }

    // O(1) lookup
    public boolean checkAvailability(String username) {
        attemptFrequency.merge(username, 1, Integer::sum);
        return !registeredUsers.containsKey(username);
    }

    public boolean registerUser(String username) {
        if (checkAvailability(username)) {
            registeredUsers.put(username, nextUserId++);
            return true;
        }
        return false;
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String s = username + i;
            if (!registeredUsers.containsKey(s)) suggestions.add(s);
        }
        String dotted = username.replace("_", ".");
        if (!registeredUsers.containsKey(dotted)) suggestions.add(dotted);
        String dated = username + "_2024";
        if (!registeredUsers.containsKey(dated)) suggestions.add(dated);
        return suggestions;
    }

    public String getMostAttempted() {
        return attemptFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Map<String, Integer> getAttemptFrequency() {
        return Collections.unmodifiableMap(attemptFrequency);
    }
}