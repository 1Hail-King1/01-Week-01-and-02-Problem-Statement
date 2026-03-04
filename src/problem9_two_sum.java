import java.util.*;

public class problem9_two_sum {

    static class Transaction {
        int id, amount, accountId;
        String merchant, time;

        Transaction(int id, int amount, String merchant, String time, int accountId) {
            this.id = id; this.amount = amount;
            this.merchant = merchant; this.time = time;
            this.accountId = accountId;
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) { transactions.add(t); }

    // Classic Two-Sum: O(n) using complement map
    public List<int[]> findTwoSum(int target) {
        HashMap<Integer, Integer> seen = new HashMap<>(); // amount → id
        List<int[]> pairs = new ArrayList<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (seen.containsKey(complement)) {
                pairs.add(new int[]{seen.get(complement), t.id});
            }
            seen.put(t.amount, t.id);
        }
        return pairs;
    }

    // Duplicate detection: same amount + merchant, different accounts
    public Map<String, Set<Integer>> detectDuplicates() {
        HashMap<String, List<Transaction>> grouped = new HashMap<>();
        for (Transaction t : transactions) {
            grouped.computeIfAbsent(t.amount + "_" + t.merchant,
                    k -> new ArrayList<>()).add(t);
        }
        Map<String, Set<Integer>> duplicates = new LinkedHashMap<>();
        grouped.forEach((key, list) -> {
            Set<Integer> accounts = new HashSet<>();
            list.forEach(t -> accounts.add(t.accountId));
            if (accounts.size() > 1) duplicates.put(key, accounts);
        });
        return duplicates;
    }

    // K-Sum: find K transactions summing to target
    public List<List<Integer>> findKSum(int k, int target) {
        int[] amounts = transactions.stream().mapToInt(t -> t.amount).toArray();
        int[] ids     = transactions.stream().mapToInt(t -> t.id).toArray();
        List<List<Integer>> results = new ArrayList<>();
        kSumHelper(amounts, ids, 0, k, target, new ArrayList<>(), results);
        return results;
    }

    private void kSumHelper(int[] amounts, int[] ids, int start, int k,
                            int remaining, List<Integer> current,
                            List<List<Integer>> results) {
        if (k == 0 && remaining == 0) { results.add(new ArrayList<>(current)); return; }
        if (k == 0 || start >= amounts.length) return;
        for (int i = start; i < amounts.length; i++) {
            current.add(ids[i]);
            kSumHelper(amounts, ids, i + 1, k - 1, remaining - amounts[i], current, results);
            current.remove(current.size() - 1);
        }
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}