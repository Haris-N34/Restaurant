//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

package ca.ucalgary.haris.naveed1.demo3;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The {@code OrderHistory} class is responsible for storing and retrieving order history data from a file.
 * <p>
 * This class demonstrates encapsulation by hiding file I/O details behind methods and provides
 * functions to store orders, print past orders, and analyze order frequencies.
 */
public class OrderHistory {
    // Constant for the order history file name (encapsulates configuration details).
    private static final String ORDER_HISTORY_FILE = "order_history.txt";
    // Formatter to present the order time in a human-friendly format.
    // Example format: "March 3, 2025 at 4:43pm"
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mma");

    /**
     * Constructs an OrderHistory instance and ensures that the order history file exists.
     */
    public OrderHistory() {
        ensureFileExists();
    }

    /**
     * Checks if the order history file exists, and creates it if it does not.
     * <p>
     * This method encapsulates file management details by handling file existence and creation.
     */
    private void ensureFileExists() {
        File file = new File(ORDER_HISTORY_FILE);
        if (!file.exists()) {
            try {
                // Create a new file if it does not exist.
                file.createNewFile();
            } catch (IOException e) {
                // Print an error message if file creation fails.
                System.out.println("Error creating order history file: " + e.getMessage());
            }
        }
    }

    /**
     * Stores a new order in the history file.
     * <p>
     * Assembles order data into a single record in the format:
     * {@code customerName|formattedDateTime|orderTotalCalories|item1,item2,...}
     * and appends it to the order history file.
     *
     * @param order the {@code Order} object containing the order details.
     */
    public void storeOrder(Order order) {
        ensureFileExists();
        // Format the order time using the specified formatter and convert AM/PM to lowercase.
        String formattedDateTime = order.getOrderTime().format(formatter)
                .replace("AM", "am")
                .replace("PM", "pm");

        // Build a comma-separated list of menu item names from the order.
        List<String> itemNames = new ArrayList<>();
        for (MenuItem item : order.getItems()) {
            itemNames.add(item.getName());
        }
        String itemsStr = String.join(",", itemNames);

        // Create the record string with customer name, formatted date/time, total calories, and items.
        String record = order.getCustomerName() + "|" + formattedDateTime + "|" + order.getTotalCalories() + "|" + itemsStr;

        // Append the record to the order history file using PrintWriter and BufferedWriter for efficiency.
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true)))) {
            out.println(record);
        } catch (IOException e) {
            // Inform the user if writing to the file fails.
            System.out.println("Error writing order history: " + e.getMessage());
        }
    }

    /**
     * Reads and prints the order history for a specific customer.
     * <p>
     * The records are expected to be in the format:
     * {@code customerName|formattedDateTime|orderTotalCalories|item1,item2,...}
     * Only records matching the provided customer name are printed.
     *
     * @param customerName the name of the customer whose order history is to be displayed.
     */
    public void printOrderHistory(String customerName) {
        ensureFileExists();
        System.out.println("\nStored Order History:");
        boolean found = false;
        // Read the file line by line.
        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_HISTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the record string using the pipe '|' delimiter.
                String[] parts = line.split("\\|");
                // Verify that the record has 4 parts and matches the provided customer name.
                if (parts.length == 4 && parts[0].equals(customerName)) {
                    found = true;
                    String dateTime = parts[1];
                    String totalCalories = parts[2];
                    String items = parts[3];
                    // Print details of the order record.
                    System.out.println("Date & Time: " + dateTime
                            + ", Order Calories: " + totalCalories
                            + ", Order: " + items);
                }
            }
            // If no matching records were found, notify the user.
            if (!found) {
                System.out.println("No order history found for " + customerName);
            }
        } catch (IOException e) {
            // Print an error message if file reading fails.
            System.out.println("Error reading order history: " + e.getMessage());
        }
    }

    /**
     * Builds a frequency map of items ordered by the user.
     * <p>
     * Reads both the persistent order history and the current session's orders to determine
     * how many times each menu item has been ordered, which can be used for recommendations.
     *
     * @param customerName the name of the user.
     * @return a {@code Map} where keys are menu item names and values are the frequencies of orders.
     */
    public Map<String, Integer> buildFrequencyMap(String customerName) {
        Map<String, Integer> frequency = new HashMap<>();
        ensureFileExists();
        // Process the order history file.
        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_HISTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split each record into parts.
                String[] parts = line.split("\\|");
                // Only consider records that match the specified customer.
                if (parts.length == 4 && parts[0].equals(customerName)) {
                    // Split the ordered items by comma.
                    String[] items = parts[3].split(",");
                    // Count each occurrence of the menu item.
                    for (String item : items) {
                        frequency.put(item, frequency.getOrDefault(item, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            // Inform if there is an error reading the order history.
            System.out.println("Error reading order history for frequency map: " + e.getMessage());
        }
        return frequency;
    }
}
