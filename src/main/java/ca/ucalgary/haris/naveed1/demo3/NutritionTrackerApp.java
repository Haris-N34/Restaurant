//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

// NutritionTrackerApp.java
// This class encapsulates the main logic of the nutrition tracking application.
// It demonstrates composition by using other objects such as Scanner, Menu, OrderHistory, and RecommendationEngine.
// It also abstracts the overall workflow into methods for displaying the menu, placing orders, viewing order history, and generating recommendations.
package ca.ucalgary.haris.naveed1.demo3;

import java.util.*;

/**
 * NutritionTrackerApp encapsulates the core logic for the nutrition tracker application.
 * <p>
 * It utilizes composition to include objects like Scanner, Menu, OrderHistory, and RecommendationEngine,
 * thereby delegating specific tasks. It defines the main workflow by providing methods for displaying the menu,
 * placing orders, viewing order history, and generating order recommendations.
 * <p>
 * Customer information (name) is stored and used to personalize the experience, while session orders
 * are tracked for future recommendations.
 */
public class NutritionTrackerApp {
    // Scanner for reading user input from the console.
    private Scanner scanner;                     // For handling user input.
    // Menu object represents the collection of available menu items.
    private Menu menu;                           // Represents the menu of items.
    // OrderHistory handles persistent storage and retrieval of past orders.
    private OrderHistory orderHistory;           // Manages storing and retrieving order history.
    // RecommendationEngine provides logic to generate order recommendations.
    private RecommendationEngine recommendationEngine; // Provides order recommendation logic.
    // sessionOrders holds all orders (items) placed during the current session.
    private List<MenuItem> sessionOrders;        // Stores orders for the current session.
    // customerName stores the name of the current user.
    private String customerName;                 // Holds the customer's name (encapsulation).

    /**
     * Constructs a NutritionTrackerApp instance with the given customer name.
     * <p>
     * Dependency injection is demonstrated here: the customer's name is passed in
     * and other components (Scanner, Menu, OrderHistory, RecommendationEngine) are instantiated internally.
     *
     * @param customerName the name of the customer.
     */
    public NutritionTrackerApp(String customerName) {
        this.customerName = customerName;
        this.scanner = new Scanner(System.in);         // Initialize the Scanner for input.
        this.menu = new Menu();                          // Instantiate the Menu with available items.
        this.orderHistory = new OrderHistory();          // Create an OrderHistory instance.
        // Instantiate RecommendationEngine using the created Menu and OrderHistory.
        this.recommendationEngine = new RecommendationEngine(menu, orderHistory);
        this.sessionOrders = new ArrayList<>();          // Initialize session orders as an empty list.
    }

    /**
     * Runs the main application loop.
     * <p>
     * This method contains the primary control flow: it displays a menu of options,
     * reads the user's choice, and calls the appropriate methods based on the input.
     */
    public void run() {
        System.out.println("Welcome to McDonald's Nutrition Tracker System!");
        while (true) {
            // Display menu options to the user.
            System.out.println("\n1. View Menu");
            System.out.println("2. Place Order");
            System.out.println("3. View Order History");
            System.out.println("4. Recommend Order");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                // Parse the user's input as an integer.
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                // Inform the user of invalid input and prompt again.
                System.out.println("Invalid input. Please enter a number from 1 to 5.");
                continue;
            }

            // Execute the method corresponding to the user's choice.
            switch (choice) {
                case 1:
                    displayMenu();
                    break;
                case 2:
                    placeOrder();
                    break;
                case 3:
                    viewOrderHistory();
                    break;
                case 4:
                    recommendOrder();
                    break;
                case 5:
                    // Close the Scanner and exit the program.
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    // Handle cases where choice is not within valid range.
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays the current menu items.
     * <p>
     * Iterates over the Menu object to print each menu item's name to the console.
     */
    private void displayMenu() {
        System.out.println("\nMcDonald's Menu:");
        // Loop through all items in the menu and print their names.
        for (MenuItem item : menu.getItems()) {
            System.out.println("- " + item.getName());
        }
    }

    /**
     * Handles the process of placing an order.
     * <p>
     * This method prompts the user to add items to the order until they type "done".
     * If the order is non-empty, it allows the user to modify the order before finalizing it.
     * The finalized order is added to the session log and stored persistently.
     */
    private void placeOrder() {
        // Create a new Order object with the stored customerName.
        Order order = new Order(customerName);
        System.out.println("\nEnter item name (type 'done' to complete order):");
        while (true) {
            // Read user input for an item name.
            String input = scanner.nextLine();
            // Check if the user has finished adding items.
            if (input.equalsIgnoreCase("done")) break;
            // Retrieve the MenuItem object corresponding to the input name.
            MenuItem item = menu.getItemByName(input);
            if (item != null) {
                // If found, add the item to the current order.
                order.addItem(item);
                System.out.println(item.getName() + " added to your order.");
                // Display the current order summary after addition.
                printOrderSummary(order);
            } else {
                // Inform the user that the item name is invalid.
                System.out.println("Invalid item name. Please try again.");
            }
        }

        // If no items were added, inform the user and exit the order process.
        if (order.getItems().isEmpty()) {
            System.out.println("No items were added to the order.");
            return;
        }

        // Ask the user if they wish to modify the order.
        System.out.print("Would you like to modify your order? (y/n): ");
        String modify = scanner.nextLine();
        if (modify.equalsIgnoreCase("y")) {
            // Call the editOrder method to allow interactive editing.
            editOrder(order);
        }

        // Save the finalized order items to the session log.
        sessionOrders.addAll(order.getItems());
        // Persist the order data to a file.
        orderHistory.storeOrder(order);
    }

    /**
     * Displays past orders for the current customer.
     * <p>
     * Delegates the task to the OrderHistory object.
     */
    private void viewOrderHistory() {
        orderHistory.printOrderHistory(customerName);
    }

    /**
     * Generates order recommendations based on calorie constraints and order history.
     * <p>
     * The user is prompted for a desired calorie amount, after which recommendations are generated using the RecommendationEngine.
     * The recommended order is then used to create a new order which the user can modify before finalizing.
     */
    private void recommendOrder() {
        System.out.print("Enter desired calorie amount for your recommended order: ");
        int desiredCalories;
        try {
            // Parse the desired calorie amount entered by the user.
            desiredCalories = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            // Inform the user of invalid calorie input.
            System.out.println("Invalid calorie amount. Aborting recommendation.");
            return;
        }

        // Generate a list of recommended orders via the RecommendationEngine.
        List<List<MenuItem>> recommendations = recommendationEngine.generateRecommendations(customerName, desiredCalories, sessionOrders);
        System.out.println("Recommended Orders:");
        // Iterate through and display each recommended order with its total calories.
        for (int i = 0; i < recommendations.size(); i++) {
            List<MenuItem> rec = recommendations.get(i);
            double totalCals = 0;
            List<String> names = new ArrayList<>();
            // Compute total calories and collect item names.
            for (MenuItem item : rec) {
                totalCals += item.getCalories();
                names.add(item.getName());
            }
            System.out.println((i + 1) + ". Total Calories: " + totalCals + " | Items: " + String.join(", ", names));
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

        // Build a new order based on the selected recommended option.
        List<MenuItem> chosenItems = recommendations.get(option - 1);
        Order recommendedOrder = new Order(customerName);
        for (MenuItem item : chosenItems) {
            recommendedOrder.addItem(item);
        }
        // Display the order summary.
        printOrderSummary(recommendedOrder);
        // Allow the user to modify the recommended order if desired.
        editOrder(recommendedOrder);
        // Update the session orders and persist the order.
        sessionOrders.addAll(recommendedOrder.getItems());
        orderHistory.storeOrder(recommendedOrder);
    }

    /**
     * Prints a detailed nutritional summary of the given order.
     * <p>
     * The summary is printed in a formatted table that includes item names and their nutritional macros,
     * followed by a total for the entire order.
     *
     * @param order the Order to summarize.
     */
    private void printOrderSummary(Order order) {
        List<MenuItem> items = order.getItems();
        if (items.isEmpty()) {
            System.out.println("Order is empty.");
            return;
        }

        // Define format strings for the header and rows.
        String headerFormat = "%-20s %10s %10s %10s %10s %10s\n";
        String rowFormat = "%-20s %10.1f %10.1f %10.1f %10.1f %10.1f\n";
        System.out.printf(headerFormat, "Item", "Calories", "Protein", "Carbs", "Sugars", "Fat");

        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalSugars = 0, totalFat = 0;
        // Iterate through each item and print its nutritional data.
        for (MenuItem item : items) {
            System.out.printf(rowFormat, item.getName(), item.getCalories(), item.getProtein(), item.getCarbs(), item.getSugars(), item.getFat());
            totalCalories += item.getCalories();
            totalProtein += item.getProtein();
            totalCarbs += item.getCarbs();
            totalSugars += item.getSugars();
            totalFat += item.getFat();
        }

        // Print a separator line.
        System.out.println("---------------------------------------------------------------");
        // Print totals for the order.
        System.out.printf(rowFormat, "TOTAL", totalCalories, totalProtein, totalCarbs, totalSugars, totalFat);
    }

    /**
     * Allows the user to interactively modify an order by adding or removing items.
     * <p>
     * This method repeatedly prompts the user to either add an item, remove an item, or finish editing the order.
     * The current state of the order is printed after each modification.
     *
     * @param order the Order to be modified.
     */
    private void editOrder(Order order) {
        while (true) {
            System.out.println("\nWould you like to modify your order?");
            System.out.println("1. Add an item");
            System.out.println("2. Remove an item");
            System.out.println("3. Finish editing");
            System.out.print("Choose an option: ");
            int choice;
            try {
                // Parse the user's choice.
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please choose 1, 2, or 3.");
                continue;
            }
            // Option to add an item.
            if (choice == 1) {
                System.out.print("Enter item name to add: ");
                String newItemName = scanner.nextLine();
                // Retrieve the MenuItem using its name.
                MenuItem newItem = menu.getItemByName(newItemName);
                if (newItem != null) {
                    order.addItem(newItem);
                    System.out.println(newItem.getName() + " added.");
                } else {
                    System.out.println("Invalid item name.");
                }
                // Print updated order summary.
                printOrderSummary(order);
            }
            // Option to remove an item.
            else if (choice == 2) {
                if (order.getItems().isEmpty()) {
                    System.out.println("Order is empty. Nothing to remove.");
                } else {
                    // Display the current order with numbered list.
                    System.out.println("Current Order:");
                    List<MenuItem> items = order.getItems();
                    for (int i = 0; i < items.size(); i++) {
                        System.out.println((i + 1) + ". " + items.get(i).getName());
                    }
                    System.out.print("Enter the number of the item to remove: ");
                    int index;
                    try {
                        index = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        continue;
                    }
                    // Validate the provided index.
                    if (index < 1 || index > items.size()) {
                        System.out.println("Invalid item number.");
                    } else {
                        // Remove the selected item.
                        MenuItem removed = items.remove(index - 1);
                        System.out.println(removed.getName() + " removed.");
                    }
                    // Print updated order summary.
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
