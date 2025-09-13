//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

package ca.ucalgary.haris.naveed1.demo3;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Order} class represents an order made by a customer.
 * <p>
 * It uses encapsulation to protect its fields and provides methods to modify
 * and retrieve order details. It also includes a method to calculate the total calories
 * of the order. This class demonstrates abstraction by exposing behavior derived
 * from its internal state (e.g., total calorie calculation).
 */
public class Order {
    // Private list of MenuItem objects representing the items in this order.
    // Encapsulation is applied so that external classes cannot modify the list directly.
    private List<MenuItem> items;

    // The name of the customer who placed the order.
    private String customerName;

    // The timestamp when the order was created.
    private LocalDateTime orderTime;

    /**
     * Constructs a new {@code Order} with the specified customer name.
     * <p>
     * Initializes an empty list for order items and sets the order creation time to now.
     *
     * @param customerName the name of the customer placing the order.
     */
    public Order(String customerName) {
        this.customerName = customerName;
        this.items = new ArrayList<>();   // Initializes the items list as empty.
        this.orderTime = LocalDateTime.now(); // Captures the creation time of the order.
    }

    /**
     * Adds a {@code MenuItem} to this order.
     * <p>
     * This method provides controlled access to the internal list of items.
     *
     * @param item the {@code MenuItem} to add.
     */
    public void addItem(MenuItem item) {
        items.add(item);
    }

    /**
     * Removes the {@code MenuItem} at the specified index from the order.
     * <p>
     * Ensures that the index is within bounds before removing the item.
     *
     * @param index the index of the item to remove.
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    /**
     * Returns the list of items in the order.
     * <p>
     * Although this returns a direct reference to the internal list,
     * it should be used in a controlled manner to prevent unintended modifications.
     *
     * @return the list of {@code MenuItem} objects.
     */
    public List<MenuItem> getItems() {
        return items;
    }

    /**
     * Calculates and returns the total calories of all items in the order.
     * <p>
     * This method abstracts the behavior of computing a value based on internal state.
     *
     * @return the total calorie count as a double.
     */
    public double getTotalCalories() {
        double total = 0;
        // Sum calories for each item in the order.
        for (MenuItem item : items) {
            total += item.getCalories();
        }
        return total;
    }

    /**
     * Returns the name of the customer who placed the order.
     *
     * @return the customer name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the timestamp when the order was created.
     *
     * @return the order creation {@code LocalDateTime}.
     */
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
}
