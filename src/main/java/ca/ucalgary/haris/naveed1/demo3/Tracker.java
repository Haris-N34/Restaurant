//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

package ca.ucalgary.haris.naveed1.demo3;

import java.util.*;

/**
 * The {@code Tracker} class holds nutritional information for McDonald's menu items.
 * <p>
 * It provides static maps that are globally accessible to retrieve nutritional data.
 * Nutritional details for each menu item are stored in a complete map and then distributed
 * into separate maps for individual nutrient lookups. This demonstrates encapsulation and
 * abstraction in data management.
 */
public class Tracker {

    // Map to store complete nutritional information for each menu item.
    // Each key (menu item name) maps to a List<Double> representing:
    // [Calories, Protein, Carbs, Sugars, Fat]
    public static final Map<String, List<Double>> itemNutrition = new HashMap<>();

    // Separate maps for individual nutritional components extracted from itemNutrition.
    // Each nutrient is stored as a single-value list for uniformity.
    public static final Map<String, List<Double>> calories = new HashMap<>();
    public static final Map<String, List<Double>> protein = new HashMap<>();
    public static final Map<String, List<Double>> fat = new HashMap<>();
    // The grams map is currently unused and reserved for future nutritional measures.
    public static final Map<String, List<Double>> grams = new HashMap<>();
    public static final Map<String, List<Double>> carbs = new HashMap<>();

    /**
     * Initializes the menu data by populating the nutritional information for each menu item.
     * <p>
     * Nutritional values are stored in the following order:
     * Calories, Protein, Carbs, Sugars, Fat.
     * <p>
     * After populating the complete nutritional info in {@code itemNutrition},
     * this method distributes these values into separate maps ({@code calories}, {@code protein},
     * {@code carbs}, {@code fat}) for easier individual lookups.
     * <p>
     * Note: The {@code grams} map is left unpopulated; modify as necessary if you need to track portion sizes.
     */
    public static void initializeMenu() {
        // Populate the complete nutritional information for each menu item.
        // Each call to put() stores an immutable list of nutrient values.
        itemNutrition.put("Big Mac", List.of(570.0, 24.0, 46.0, 8.0, 32.0));
        itemNutrition.put("McChicken", List.of(400.0, 14.0, 44.0, 5.0, 22.0));
        itemNutrition.put("Filet-O-Fish", List.of(410.0, 15.0, 44.0, 5.0, 20.0));
        itemNutrition.put("Cheeseburger", List.of(290.0, 15.0, 32.0, 7.0, 11.0));
        itemNutrition.put("Small Fries", List.of(220.0, 3.0, 29.0, 0.0, 10.0));
        itemNutrition.put("Medium Fries", List.of(340.0, 5.0, 45.0, 0.0, 17.0));
        itemNutrition.put("Large Fries", List.of(450.0, 6.0, 63.0, 0.0, 22.0));
        itemNutrition.put("McFlurry Regular", List.of(650.0, 13.0, 101.0, 83.0, 22.0));
        itemNutrition.put("McFlurry Snack Size", List.of(430.0, 9.0, 66.0, 54.0, 15.0));

        // Loop over each entry in the itemNutrition map to extract individual nutrients.
        for (Map.Entry<String, List<Double>> entry : itemNutrition.entrySet()) {
            String item = entry.getKey();
            List<Double> values = entry.getValue();
            // Extract calories: value at index 0.
            calories.put(item, List.of(values.get(0)));
            // Extract protein: value at index 1.
            protein.put(item, List.of(values.get(1)));
            // Extract carbohydrates: value at index 2.
            carbs.put(item, List.of(values.get(2)));
            // Extract fat: value at index 4, intentionally skipping sugars at index 3.
            fat.put(item, List.of(values.get(4)));
            // The grams map remains unpopulated; add logic here if portion sizes need to be tracked.
        }
    }
}
