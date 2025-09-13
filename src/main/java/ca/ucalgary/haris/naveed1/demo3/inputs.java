//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

package ca.ucalgary.haris.naveed1.demo3;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The {@code inputs} class provides a console-based user interface for McDonald's Nutrition Tracker.
 * <p>
 * It allows users to interactively display the menu, build and modify orders, view persistent order history,
 * and get order recommendations based on nutritional data.
 * <p>
 * Nutritional details for each menu item are obtained via the {@link Tracker} class.
 */
public class inputs {

    // Scanner object for reading user input from the console.
    private static final Scanner scanner = new Scanner(System.in);

    // Nutritional information maps from Tracker.
    // Each map uses the menu item name as key and a List<Double> representing its nutrients.
    // Nutrient order: Calories, Protein, Carbs, Sugars, Fat.
    private static final Map<String, List<Double>> calories = Tracker.calories;
    private static final Map<String, List<Double>> protein = Tracker.protein;
    private static final Map<String, List<Double>> fat = Tracker.fat;
    private static final Map<String, List<Double>> grams = Tracker.grams;
    private static final Map<String, List<Double>> carbs = Tracker.carbs;

    // In-memory user meal logs to accumulate orders during the session.
    // Each user's orders are stored as a List of Strings keyed by userName.
    private static final Map<String, List<String>> userMealLogs = new HashMap<>();

    // File name constant for storing order history persistently.
    private static final String ORDER_HISTORY_FILE = "order_history.txt";

    // Formatter to display date and time in a human-friendly format.
    // Example format: "March 3, 2025 at 4:43pm"
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mma");

    // Static block: executed once when the class is loaded.
    // It initializes Tracker menu data and makes sure that the order history file exists.
    static {
        Tracker.initializeMenu();      // Load the menu items and their nutritional info.
        ensureOrderHistoryFileExists();  // Create the order history file if it does not exist.
    }

    /**
     * Main method that provides the console-driven menu interface.
     * <p>
     * It repeatedly prompts the user with options: view menu, place order, view order history,
     * get order recommendations, or exit. The selected option triggers a corresponding action.
     *
     * @param customer the {@link Main.Customer} representing the current user.
     */
    public static void runProgram(Main.Customer customer) {
        System.out.println("Welcome to McDonald's Nutrition Tracker System!");

        // Get the customer name from the provided Customer object.
        String userName = customer.name;

        // Initialize the meal log for the current user.
        userMealLogs.put(userName, new ArrayList<>());

        // Main loop to repeatedly prompt the user for an option.
        while (true) {
            System.out.println("\n1. View Menu");
            System.out.println("2. Place Order");
            System.out.println("3. View Order History");
            System.out.println("4. Recommend Order");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                // Read the user's input as an integer option.
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                // Handle invalid (non-numeric) inputs.
                System.out.println("Invalid input. Please enter a number from 1 to 5.");
                continue;
            }

            // Execute the corresponding method based on the user's choice.
            switch (choice) {
                case 1:
                    displayMenu();
                    break;
                case 2:
                    placeOrder(userName);
                    break;
                case 3:
                    orderHistory(userName);
                    break;
                case 4:
                    recommendOrder(userName);
                    break;
                case 5:
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    /**
     * Displays the McDonald's menu.
     * <p>
     * Iterates through the menu items (keys in {@link Tracker#itemNutrition}) and prints each item.
     */
    public static void displayMenu() {
        System.out.println("\nMcDonald's Menu:");
        // Loop through each menu item in the Tracker's nutritional information.
        for (String item : Tracker.itemNutrition.keySet()) {
            System.out.println("- " + item);
        }
    }

    /**
     * Allows the user to manually build an order through the console.
     * <p>
     * The user can add item names until they type "done", then review and optionally modify the order.
     * Finally, the order is stored persistently.
     *
     * @param userName the name of the current user.
     */
    public static void placeOrder(String userName) {
        // Create a new list to store the user's order.
        List<String> userOrder = new ArrayList<>();
        System.out.println("\nEnter item name (type 'done' to complete order):");

        // Loop to read items until user types "done"
        while (true) {
            String item = scanner.nextLine();
            if (item.equalsIgnoreCase("done")) {
                break;
            }
            // Check if the item exists in the menu.
            if (Tracker.itemNutrition.containsKey(item)) {
                userOrder.add(item);
                System.out.println(item + " added to your order.");
                // Display the current order summary after adding the item.
                printOrderSummary(userOrder);
            } else {
                System.out.println("Invalid item name. Please try again.");
            }
        }

        // If the user built a non-empty order, allow them to review and modify it.
        if (!userOrder.isEmpty()) {
            System.out.println("\nCurrent Order Summary:");
            printOrderSummary(userOrder);
            System.out.print("Would you like to modify your order? (y/n): ");
            String modify = scanner.nextLine();
            if (modify.equalsIgnoreCase("y")) {
                // Invoke the interactive order editing method.
                editOrder(userName, userOrder);
            }
            System.out.println("\nFinal Order Summary:");
            printOrderSummary(userOrder);
            // Add the finalized order to the user's session log.
            userMealLogs.get(userName).addAll(userOrder);
            // Calculate total calories and persist the order to file.
            double totalCalories = calculateTotalCalories(userOrder);
            storeOrderToFile(userName, userOrder, totalCalories);
        } else {
            System.out.println("No items were added to the order.");
        }
    }

    /**
     * Ensures that the order history file exists.
     * <p>
     * If the file does not exist, it creates a new one.
     */
    public static void ensureOrderHistoryFileExists() {
        File file = new File(ORDER_HISTORY_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating order history file: " + e.getMessage());
        }
    }

    /**
     * Stores a customer's order to the order history file.
     * <p>
     * The record format is: userName|formattedDateTime|orderTotalCalories|item1,item2,...
     *
     * @param userName          the name of the user.
     * @param orderItems        list of ordered item names.
     * @param orderTotalCalories the total calorie count of the order.
     */
    public static void storeOrderToFile(String userName, List<String> orderItems, double orderTotalCalories) {
        ensureOrderHistoryFileExists();
        // Get the current date and time.
        LocalDateTime now = LocalDateTime.now();
        // Format the date and time for human-friendly display.
        String formattedDateTime = now.format(dateTimeFormatter)
                .replace("AM", "am")
                .replace("PM", "pm");
        // Join the ordered items into a comma-separated string.
        String items = String.join(",", orderItems);
        // Create the record string.
        String record = userName + "|" + formattedDateTime + "|" + orderTotalCalories + "|" + items;
        // Write the record to the order history file.
        try (FileWriter fw = new FileWriter(ORDER_HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(record);
        } catch (IOException e) {
            System.out.println("Error writing order history to file: " + e.getMessage());
        }
    }

    /**
     * Reads and displays the stored order history for the given user.
     * <p>
     * The records are expected in the format:
     * userName|formattedDateTime|orderTotalCalories|item1,item2,...
     *
     * @param userName the name of the user whose order history is to be displayed.
     */
    public static void orderHistory(String userName) {
        ensureOrderHistoryFileExists();
        System.out.println("\nStored Order History:");
        boolean found = false;
        // Read the file line by line.
        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_HISTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the record by the pipe delimiter.
                String[] parts = line.split("\\|");
                if (parts.length == 4 && parts[0].equals(userName)) {
                    found = true;
                    String dateTime = parts[1];
                    String orderTotal = parts[2];
                    String items = parts[3];
                    System.out.println("Date & Time: " + dateTime
                            + ", Order Calories: " + orderTotal
                            + ", Order: " + items);
                }
            }
            if (!found) {
                System.out.println("No stored order history found for " + userName);
            }
        } catch (IOException e) {
            System.out.println("Error reading order history file: " + e.getMessage());
        }
    }

    /**
     * Builds a frequency map of the user's orders by reading both the stored order history
     * and the current session's orders.
     * <p>
     * The frequency map indicates how many times each menu item was ordered.
     *
     * @param userName the name of the user.
     * @return a Map with menu item names as keys and their order frequency as values.
     */
    private static Map<String, Integer> buildFrequencyMap(String userName) {
        Map<String, Integer> frequency = new HashMap<>();
        // Build frequency map from file history.
        ensureOrderHistoryFileExists();
        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_HISTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4 && parts[0].equals(userName)) {
                    // Split the items in the order.
                    String[] items = parts[3].split(",");
                    for (String item : items) {
                        frequency.put(item, frequency.getOrDefault(item, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading order history for frequency map: " + e.getMessage());
        }
        // Include orders from the current session.
        List<String> sessionOrders = userMealLogs.get(userName);
        if (sessionOrders != null) {
            for (String item : sessionOrders) {
                frequency.put(item, frequency.getOrDefault(item, 0) + 1);
            }
        }
        return frequency;
    }

    /**
     * Recommends an order based on the user's order frequency and desired calorie amount.
     * <p>
     * It builds three candidate recommendation options:
     * - Option 1: Familiar items only.
     * - Option 2: One new item (if available) followed by familiar items.
     * - Option 3: New items first, then familiar items.
     * Then, it uses a greedy algorithm to fill each recommendation without exceeding the desired calories.
     *
     * @param userName the name of the user.
     */
    public static void recommendOrder(String userName) {
        // Build the frequency map using historical and session orders.
        Map<String, Integer> frequency = buildFrequencyMap(userName);

        // Separate items into those ordered before (familiar) and new items.
        List<String> frequentItems = new ArrayList<>();
        List<String> newItems = new ArrayList<>();
        for (String item : Tracker.itemNutrition.keySet()) {
            if (frequency.getOrDefault(item, 0) > 0) {
                frequentItems.add(item);
            } else {
                newItems.add(item);
            }
        }
        // Sort familiar items by descending frequency.
        frequentItems.sort((a, b) -> frequency.get(b) - frequency.get(a));
        // Sort new items alphabetically.
        Collections.sort(newItems);

        // Prompt for desired calorie amount.
        System.out.print("Enter desired calorie amount for your recommended order: ");
        int desiredCalories;
        try {
            desiredCalories = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid calorie amount. Aborting recommendation.");
            return;
        }

        // Build three candidate lists for recommendations.
        List<String> candidate1 = new ArrayList<>(frequentItems);
        List<String> candidate2 = new ArrayList<>();
        if (!newItems.isEmpty()) {
            candidate2.add(newItems.get(0));
        }
        candidate2.addAll(frequentItems);
        List<String> candidate3 = new ArrayList<>(newItems);
        candidate3.addAll(frequentItems);

        // Generate recommendations with the greedy algorithm.
        List<List<String>> recommendations = new ArrayList<>();
        recommendations.add(greedyRecommendation(candidate1, desiredCalories));
        recommendations.add(greedyRecommendation(candidate2, desiredCalories));
        recommendations.add(greedyRecommendation(candidate3, desiredCalories));

        // Display recommendations.
        System.out.println("Recommended Orders:");
        for (int i = 0; i < recommendations.size(); i++) {
            List<String> rec = recommendations.get(i);
            double sum = calculateTotalCalories(rec);
            System.out.println((i + 1) + ". Total Calories: " + sum
                    + " | Items: " + String.join(", ", rec));
        }
        System.out.print("Choose an option (1-3): ");
        int option;
        try {
            option = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection. Aborting recommendation.");
            return;
        }
        if (option < 1 || option > 3) {
            System.out.println("Invalid option. Aborting recommendation.");
            return;
        }
        // Get the chosen recommendation.
        List<String> chosenOrder = recommendations.get(option - 1);
        System.out.println("You selected the following recommended order:");
        printOrderSummary(chosenOrder);

        // Allow the user to modify the recommendation interactively.
        editOrder(userName, chosenOrder);

        double totalOrderCalories = calculateTotalCalories(chosenOrder);
        System.out.println("Final Recommended Order:");
        printOrderSummary(chosenOrder);

        // Add the finalized recommended order to session logs and store it persistently.
        userMealLogs.get(userName).addAll(chosenOrder);
        storeOrderToFile(userName, chosenOrder, totalOrderCalories);
    }

    /**
     * Greedy algorithm to build a recommended order from a candidate list without exceeding the calorie limit.
     *
     * @param candidateList  List of candidate items.
     * @param desiredCalories The maximum allowed total calories.
     * @return A list of items forming the recommended order.
     */
    public static List<String> greedyRecommendation(List<String> candidateList, int desiredCalories) {
        List<String> rec = new ArrayList<>();
        double sum = 0;
        // Add items one by one if the total calories remain within the limit.
        for (String item : candidateList) {
            double itemCal = Tracker.itemNutrition.get(item).get(0);
            if (sum + itemCal <= desiredCalories) {
                rec.add(item);
                sum += itemCal;
            }
        }
        return rec;
    }

    /**
     * Calculates the total calorie count for the given order.
     *
     * @param order List of ordered item names.
     * @return The sum of calories.
     */
    public static double calculateTotalCalories(List<String> order) {
        double sum = 0;
        for (String item : order) {
            sum += Tracker.itemNutrition.get(item).get(0);
        }
        return sum;
    }

    /**
     * Prints a formatted order summary showing each item's nutritional macros and overall totals.
     *
     * @param order The list of ordered item names.
     */
    private static void printOrderSummary(List<String> order) {
        if (order.isEmpty()) {
            System.out.println("Order is empty.");
            return;
        }
        // Define formatting for table header and rows.
        String headerFormat = "%-20s %10s %10s %10s %10s %10s\n";
        String rowFormat = "%-20s %10.1f %10.1f %10.1f %10.1f %10.1f\n";
        System.out.printf(headerFormat, "Item", "Calories", "Protein", "Carbs", "Sugars", "Fat");
        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalSugars = 0, totalFat = 0;
        for (String item : order) {
            List<Double> nutrition = Tracker.itemNutrition.get(item);
            System.out.printf(rowFormat, item, nutrition.get(0), nutrition.get(1), nutrition.get(2), nutrition.get(3), nutrition.get(4));
            totalCalories += nutrition.get(0);
            totalProtein += nutrition.get(1);
            totalCarbs += nutrition.get(2);
            totalSugars += nutrition.get(3);
            totalFat += nutrition.get(4);
        }
        System.out.println("---------------------------------------------------------------");
        System.out.printf(rowFormat, "TOTAL", totalCalories, totalProtein, totalCarbs, totalSugars, totalFat);
    }

    /**
     * Allows the user to interactively modify an order by adding or removing items.
     * <p>
     * Displays options repeatedly until the user chooses to finish editing.
     *
     * @param userName The name of the user (for context).
     * @param order    The order (list of item names) to be modified.
     */
    private static void editOrder(String userName, List<String> order) {
        while (true) {
            System.out.println("\nWould you like to modify your order?");
            System.out.println("1. Add an item");
            System.out.println("2. Remove an item");
            System.out.println("3. Finish editing");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please choose 1, 2, or 3.");
                continue;
            }
            // Option to add an item.
            if (choice == 1) {
                System.out.print("Enter item name to add: ");
                String newItem = scanner.nextLine();
                if (Tracker.itemNutrition.containsKey(newItem)) {
                    order.add(newItem);
                    System.out.println(newItem + " added.");
                } else {
                    System.out.println("Invalid item name.");
                }
                // Print the updated summary after addition.
                printOrderSummary(order);
            }
            // Option to remove an item.
            else if (choice == 2) {
                if (order.isEmpty()) {
                    System.out.println("Order is empty. Nothing to remove.");
                } else {
                    System.out.println("Current Order:");
                    for (int i = 0; i < order.size(); i++) {
                        System.out.println((i + 1) + ". " + order.get(i));
                    }
                    System.out.print("Enter the number of the item to remove: ");
                    int index;
                    try {
                        index = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        continue;
                    }
                    if (index < 1 || index > order.size()) {
                        System.out.println("Invalid item number.");
                    } else {
                        String removed = order.remove(index - 1);
                        System.out.println(removed + " removed.");
                    }
                    // Print the updated summary after removal.
                    printOrderSummary(order);
                }
            }
            // Option to finish editing.
            else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }
}
