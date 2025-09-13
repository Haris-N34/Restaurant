//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)


// RecommendationEngine.java
// This class generates personalized order recommendations based on user history and preferences.
// It demonstrates dependency injection by receiving a Menu and OrderHistory in its constructor.
// It encapsulates its recommendation logic in methods that use a greedy algorithm.
package ca.ucalgary.haris.naveed1.demo3;

import java.util.*;

/**
 * The {@code RecommendationEngine} class generates personalized order recommendations
 * based on the customer's historical orders and current session data.
 * <p>
 * It uses dependency injection to receive a {@link Menu} and an {@link OrderHistory},
 * promoting loose coupling and enhancing testability. The class provides a greedy
 * algorithm to generate candidate recommendations under a calorie constraint.
 */
public class RecommendationEngine {
    // Reference to the Menu object containing all available menu items.
    private Menu menu;
    // Reference to the OrderHistory which holds the persistent order data.
    private OrderHistory orderHistory;

    /**
     * Constructs a {@code RecommendationEngine} with the specified {@code Menu} and {@code OrderHistory}.
     * <p>
     * This demonstrates dependency injection, allowing the engine to use external components.
     *
     * @param menu         the {@code Menu} object with available items.
     * @param orderHistory the {@code OrderHistory} object for retrieving past orders.
     */
    public RecommendationEngine(Menu menu, OrderHistory orderHistory) {
        this.menu = menu;
        this.orderHistory = orderHistory;
    }

    /**
     * Generates a recommended order using a greedy algorithm.
     * <p>
     * This method iterates over the candidate list of menu items and adds items one by one,
     * as long as the total calories do not exceed {@code desiredCalories}.
     *
     * @param candidateList  the list of candidate {@code MenuItem} objects to consider.
     * @param desiredCalories the maximum total calorie count allowed for the recommendation.
     * @return a list of {@code MenuItem} objects that form the recommended order.
     */
    public List<MenuItem> greedyRecommendation(List<MenuItem> candidateList, int desiredCalories) {
        List<MenuItem> recommendation = new ArrayList<>();
        double sum = 0;
        // Iterate over candidate items and select them if they keep total calories under limit.
        for (MenuItem item : candidateList) {
            if (sum + item.getCalories() <= desiredCalories) {
                recommendation.add(item);
                sum += item.getCalories();
            }
        }
        return recommendation;
    }

    /**
     * Generates three variations of recommended orders based on the customer's historical ordering
     * frequency and current session orders, combined with new items.
     * <p>
     * It first builds a frequency map from the order history and current session orders.
     * It then divides the menu items into frequently ordered items and new items,
     * sorts them appropriately, and creates three candidate lists:
     * <ul>
     *   <li>Candidate 1: Familiar items only.</li>
     *   <li>Candidate 2: One new item (if available) followed by familiar items.</li>
     *   <li>Candidate 3: New items first, then familiar items.</li>
     * </ul>
     * Finally, it applies the {@link #greedyRecommendation(List, int)} method to each candidate
     * list to generate recommendations.
     *
     * @param customerName   the customer's name used to filter historical orders.
     * @param desiredCalories the calorie limit for the recommendation.
     * @param sessionOrders  the list of {@code MenuItem} objects ordered in the current session.
     * @return a list containing three recommended orders; each order is a list of {@code MenuItem} objects.
     */
    public List<List<MenuItem>> generateRecommendations(String customerName, int desiredCalories, List<MenuItem> sessionOrders) {
        // Build a frequency map from order history for the given customer.
        Map<String, Integer> frequency = orderHistory.buildFrequencyMap(customerName);

        // Include current session orders to reflect recent user preferences.
        for (MenuItem item : sessionOrders) {
            String name = item.getName();
            frequency.put(name, frequency.getOrDefault(name, 0) + 1); // Increase the count for each session order item.
        }

        // Separate menu items into those frequently ordered and new items.
        List<MenuItem> frequentItems = new ArrayList<>();
        List<MenuItem> newItems = new ArrayList<>();
        for (MenuItem item : menu.getItems()) {
            // If the item has been ordered before, add it to frequentItems; otherwise, to newItems.
            if (frequency.getOrDefault(item.getName(), 0) > 0) {
                frequentItems.add(item);
            } else {
                newItems.add(item);
            }
        }

        // Sort the frequent items in descending order based on frequency.
        frequentItems.sort((a, b) -> frequency.get(b.getName()) - frequency.get(a.getName()));
        // Sort new items alphabetically by name.
        newItems.sort(Comparator.comparing(MenuItem::getName));

        // Create three candidate lists using different combinations of frequent and new items.
        List<MenuItem> candidate1 = new ArrayList<>(frequentItems);

        List<MenuItem> candidate2 = new ArrayList<>();
        if (!newItems.isEmpty()) {
            candidate2.add(newItems.get(0)); // Start with one new item if available.
        }
        candidate2.addAll(frequentItems);

        List<MenuItem> candidate3 = new ArrayList<>(newItems);
        candidate3.addAll(frequentItems);

        // Apply the greedy recommendation algorithm to each candidate list.
        List<List<MenuItem>> recommendations = new ArrayList<>();
        recommendations.add(greedyRecommendation(candidate1, desiredCalories));
        recommendations.add(greedyRecommendation(candidate2, desiredCalories));
        recommendations.add(greedyRecommendation(candidate3, desiredCalories));

        return recommendations;
    }
}
