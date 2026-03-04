// ============================================================
// PROBLEM 2: E-commerce Flash Sale Inventory Manager
// FILE: Problem2_InventoryManager.java
// ============================================================
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class problem2_flash_sale {

    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private HashMap<String, LinkedList<Integer>> waitingLists = new HashMap<>();

    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingLists.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return stock != null ? stock.get() : 0;
    }

    // Thread-safe purchase using atomic compareAndSet
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) return "PRODUCT_NOT_FOUND";

        while (true) {
            int current = stock.get();
            if (current <= 0) {
                LinkedList<Integer> waitList = waitingLists.get(productId);
                waitList.add(userId);
                return "WAITING_LIST_POSITION_" + waitList.size();
            }
            if (stock.compareAndSet(current, current - 1)) {
                return "SUCCESS_" + (current - 1) + "_REMAINING";
            }
        }
    }

    public List<Integer> getWaitingList(String productId) {
        return Collections.unmodifiableList(
                waitingLists.getOrDefault(productId, new LinkedList<>()));
    }

    public Map<String, Integer> getAllStockLevels() {
        Map<String, Integer> levels = new HashMap<>();
        inventory.forEach((k, v) -> levels.put(k, v.get()));
        return levels;
    }
}