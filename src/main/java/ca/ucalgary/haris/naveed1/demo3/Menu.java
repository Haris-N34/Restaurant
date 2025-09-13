//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)
// This class represents a menu that contains a collection of MenuItem objects.
// It demonstrates encapsulation (hiding the list of items) and abstraction by providing methods to interact with the menu.
package ca.ucalgary.haris.naveed1.demo3;

import java.util.ArrayList;
import java.util.List;

    public class Menu {
        // Private list of MenuItem objects. This is encapsulation: the items list is hidden from other classes.
        private List<MenuItem> items;

        // Constructor initializes the menu and loads default items.
        public Menu() {
            items = new ArrayList<>();
            // Method call to encapsulate the initialization logic for the menu.
            initializeDefaultMenu();
        }

        // Private method to create default menu items.
        // Abstraction is used here to hide the details of how items are added to the menu.
        private void initializeDefaultMenu() {
            items.add(new MenuItem("Big Mac", 570.0, 24.0, 46.0, 8.0, 32.0));
            items.add(new MenuItem("McChicken", 400.0, 14.0, 44.0, 5.0, 22.0));
            items.add(new MenuItem("Filet-O-Fish", 410.0, 15.0, 44.0, 5.0, 20.0));
            items.add(new MenuItem("Cheeseburger", 290.0, 15.0, 32.0, 7.0, 11.0));
            items.add(new MenuItem("Small Fries", 220.0, 3.0, 29.0, 0.0, 10.0));
            items.add(new MenuItem("Medium Fries", 340.0, 5.0, 45.0, 0.0, 17.0));
            items.add(new MenuItem("Large Fries", 450.0, 6.0, 63.0, 0.0, 22.0));
            items.add(new MenuItem("McFlurry Regular", 650.0, 13.0, 101.0, 83.0, 22.0));
            items.add(new MenuItem("McFlurry Snack Size", 430.0, 9.0, 66.0, 54.0, 15.0));
        }

        // Provides access to the list of items.
        // This method gives read-only access (via the list reference) to other parts of the application.
        public List<MenuItem> getItems() {
            return items;
        }

        // Searches for a menu item by name.
        // Abstraction is demonstrated here by hiding the iteration logic from the caller.
        public MenuItem getItemByName(String name) {
            for (MenuItem item : items) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            // Returns null if no matching item is found.
            return null;
        }
    }


