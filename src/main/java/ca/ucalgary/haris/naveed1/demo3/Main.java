//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)
// Main.java
// This file contains the application's entry point (the main method)
// and demonstrates instantiation of objects as well as basic OOP concepts such as inheritance, abstraction, and composition.
package ca.ucalgary.haris.naveed1.demo3;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create a Scanner object to read user input from the console.
        Scanner scanner = new Scanner(System.in);

        // Prompt the user for their name.
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        // Create a Customer object by passing the user's name.
        // This demonstrates object instantiation and encapsulation of user data.
        Customer customer = new Customer(name);
        System.out.println(customer);

        // Instantiate the NutritionTrackerApp using the customer's name.
        // This shows composition, where the app is built using various objects (customer, menu, order history, etc.).
        NutritionTrackerApp app = new NutritionTrackerApp(customer.getName());
        // Run the main application loop.
        app.run();
    }

    // Abstract base class representing a user.
    // This class uses abstraction to define a general user type and enforces that all users must implement the greet() method.
    // It also demonstrates inheritance, as other user types (like Customer) will extend this class.
    static abstract class User {
        // Protected field ensures that only this class and its subclasses can access the name (encapsulation).
        protected final String name;

        // Constructor that initializes the user's name.
        public User(String name) {
            this.name = name;
        }

        // Getter method to access the user's name (encapsulation).
        public String getName() {
            return name;
        }

        // Abstract method to be implemented by subclasses.
        // This is an example of polymorphism, where different user types can have their own version of greeting.
        public abstract void greet();
    }


    // Implementing comparable
    // Customer extends the abstract class user and allows to sort Customer objects by name
    static class Customer extends User implements Comparable<Customer> {

        // Constructor that calls the superclass to intialize the names
        public Customer(String name) {
            super(name);
        }

        // Overriding the greet method from user class to print a message for the customers
        @Override
        public void greet() {
            System.out.println("Hello, " + name);
        }

        // Overriding tostring to return string rep of the customers
        @Override
        public String toString() {
            return "Name: "+ name;

        }

        // implemetning extends equals
        // Overriding to check if 2 objects have same name
        @Override
        public boolean equals(Object o) {
            // If both references are poitning to the same object they're equal
            if (this == o) return true;
            // If object is null or its not customer they are not equal
            if (o == null || getClass() != o.getClass()) return false;
            // Cast object to a customer then compare the names
            Customer customer = (Customer) o;
            return name.equals(customer.name);
        }

        // Implementing compareTo to sort customers by name
        @Override
        public int compareTo(Customer o) {
            return this.name.compareTo(o.name);
        }
    }

}
