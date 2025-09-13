//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

// MenuItem.java
// This class represents a single menu item with various nutritional properties.
// It employs encapsulation to protect its fields and provides getters for accessing its data.
package ca.ucalgary.haris.naveed1.demo3;


public class MenuItem {
    // Private fields ensure that the properties are hidden from external modification.
    private String name;
    private double calories;
    private double protein;
    private double carbs;
    private double sugars;
    private double fat;

    // Constructor to initialize all properties of a MenuItem.
    // This is an example of abstraction, where the details of item creation are hidden behind the constructor.
    public MenuItem(String name, double calories, double protein, double carbs, double sugars, double fat) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
        this.fat = fat;
    }

    // Getter methods provide read-only access to private fields (encapsulation).
    public String getName() { return name; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getSugars() { return sugars; }
    public double getFat() { return fat; }

    // Override of the toString() method to provide a meaningful representation.
    // This is an example of polymorphism via method overriding.
    @Override
    public String toString() {
        return name;
    }
}
