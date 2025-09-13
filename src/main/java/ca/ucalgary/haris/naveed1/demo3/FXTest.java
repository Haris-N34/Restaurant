package ca.ucalgary.haris.naveed1.demo3;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// TEST CASES FOR TRACKER FILE
/**
 * This test class checks if the Tracker class is working prperly.
 */
public class FXTest {

    /**
     * Tests that after calling initializeMenu, the itemNutrition map is not empty
     */
    @org.junit.Test
    public void testInitializeMenuNotEmpty() {
        // Initialize menu inside the test
        Tracker.initializeMenu();

        // Check that itemNutrition is not empty
        assertFalse(Tracker.itemNutrition.isEmpty(),
                "itemNutrition map shouldnt be empty after init");
    }

    /**
     * Tests that Big Mac has the correct values in itemNutrition
     */
    @org.junit.Test
    public void testInitializeMenuCorrectValuesForBigMac() {
        // Initialize menu inside the test
        Tracker.initializeMenu();

        // Expect Big Mac to have [570, 24, 46, 8, 32]
        List<Double> bigMacVals = Tracker.itemNutrition.get("Big Mac");
        assertNotNull(bigMacVals, "Big Mac should exist in itemNutrition");
        assertEquals(570.0, bigMacVals.get(0), 0.001, "Calories should be 570");
        assertEquals(24.0,  bigMacVals.get(1), 0.001, "Protein should be 24g");
        assertEquals(32.0,  bigMacVals.get(4), 0.001, "Fat shoud be 32g");
    }

    // TEST CASES FOR INPUTS FILE

    /**
     * Ensures that the file is created if it doesn't exist
     */
    @org.junit.Test
    public void testEnsureFileCreated() {
        String fileName = "order_history.txt";
        File file = new File(fileName);

        // Delete the file if it exists and then confirm it's gone
        if (file.exists()) {
            file.delete();
        }
        assertFalse(file.exists());

        // Now making sure it's created
        inputs.ensureOrderHistoryFileExists();
        assertTrue(file.exists());
    }

    /**
     * Tests storing an order to the file and checks if the record is present
     */
    @org.junit.Test
    public void testStoreOrder() throws IOException {
        String fileName = "order_history.txt";
        inputs.ensureOrderHistoryFileExists();

        String userName = "TestUser";
        List<String> items = Arrays.asList("Big Mac", "Small Fries");
        double totalCalories = 790.0;

        // Write to file
        inputs.storeOrderToFile(userName, items, totalCalories);

        // Verify line is found
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(userName) && line.contains("790.0") && line.contains("Big Mac")) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found);
    }

    /**
     * Tests calling orderHistory on an empty file to ensure no crash occurs
     */
    @org.junit.Test
    public void testEmptyFile() throws IOException {
        String fileName = "order_history.txt";
        // Make the file empty
        Files.write(Paths.get(fileName), new byte[0]);

        // Call the method; we only check that it doesn't crash
        inputs.orderHistory("NoDataUser");
        assertTrue(true);
    }

    /**
     * Tests to see if an empty order returns 0 calories
     */
    @org.junit.Test
    public void testCalculateTotalCaloriesEmptyOrder() {
        Tracker.initializeMenu();
        List<String> emptyOrder = new ArrayList<>();
        double result = inputs.calculateTotalCalories(emptyOrder);

        assertEquals(0.0, result, 0.001, "Empty order should have 0 cals");
    }

    /**
     * Tests that multiple items sum up correcly for total calories
     */
    @org.junit.Test
    public void testCalculateTotalCaloriesMultipleItems() {
        Tracker.initializeMenu();
        List<String> order = new ArrayList<>();
        order.add("Big Mac");      // 570 cals
        order.add("Small Fries");  // 220 cals

        double result = inputs.calculateTotalCalories(order);
        assertEquals(790.0, result, 0.001, "Total cals should be 790");
    }

    /**
     * Tests that all items are included if the calorie limit is high enough
     */
    @org.junit.Test
    public void testGreedyRecommendationAllUnderLimit() {
        Tracker.initializeMenu();
        List<String> candidateList = new ArrayList<>();
        candidateList.add("Big Mac");
        candidateList.add("Small Fries");
        candidateList.add("Cheeseburger");

        // A high limit so all items fit
        List<String> result = inputs.greedyRecommendation(candidateList, 2000);

        // all 3 items should be included
        assertEquals(3, result.size(), "Should contain all 3 items");
        assertTrue(result.contains("Big Mac"));
        assertTrue(result.contains("Small Fries"));
        assertTrue(result.contains("Cheeseburger"));
    }

    /**
     * Tests that items stop being added once the calorie limit is over the limit
     */
    @org.junit.Test
    public void testGreedyRecommendationStopsWhenOverLimit() {
        Tracker.initializeMenu();
        List<String> candidateList = new ArrayList<>();
        candidateList.add("Big Mac");
        candidateList.add("Cheeseburger");
        candidateList.add("Small Fries");

        // Limit is 600, so after adding Big Mac (570), adding anything else goes over 600
        List<String> result = inputs.greedyRecommendation(candidateList, 600);

        // Expect only Big Mac
        assertEquals(1, result.size(), "Shoud only contain Big Mac");
        assertEquals("Big Mac", result.getFirst());
    }




    // DEMO 2 TEST CASES
    // test getters from MenuItem.java
    @org.junit.Test
    public void testMenuItemGetters() {
        MenuItem item = new MenuItem("TestItem", 100.0, 10.0, 20.0, 5.0, 2.0);
        // check name
        assertEquals("TestItem", item.getName());
        // check calories
        assertEquals(100.0, item.getCalories());
        // check protein
        assertEquals(10.0, item.getProtein());
        // check carbs
        assertEquals(20.0, item.getCarbs());
        // check sugars
        assertEquals(5.0, item.getSugars());
        // check fat
        assertEquals(2.0, item.getFat());
    }

    // test addItem and getTotalCalories from Order.java
    @org.junit.Test
    public void testOrderAddAndTotalCalories() {
        Order order = new Order("TestCustomer");
        MenuItem item1 = new MenuItem("Item1", 100.0, 10.0, 20.0, 5.0, 2.0);
        MenuItem item2 = new MenuItem("Item2", 200.0, 15.0, 25.0, 3.0, 1.0);
        order.addItem(item1);
        order.addItem(item2);
        // check order size
        assertEquals(2, order.getItems().size());
        // check total calories sum
        assertEquals(300.0, order.getTotalCalories(), 0.001);
    }
    // test removeItem from Order.java
    @org.junit.Test
    public void testOrderRemoveItem() {
        Order order = new Order("TestCustomer");
        MenuItem item = new MenuItem("Item1", 100.0, 10.0, 20.0, 5.0, 2.0);
        order.addItem(item);
        // check order size before removal
        assertEquals(1, order.getItems().size());
        // remove the first item
        order.removeItem(0);
        // check order size after removal
        assertEquals(0, order.getItems().size());
    }

    // test greedyRecommendation from RecommendationEngine.java
    @org.junit.Test
    public void testGreedyRecommendation() {
        // create candidate list of items
        MenuItem item1 = new MenuItem("Item1", 100.0, 0, 0, 0, 0);
        MenuItem item2 = new MenuItem("Item2", 150.0, 0, 0, 0, 0);
        MenuItem item3 = new MenuItem("Item3", 200.0, 0, 0, 0, 0);
        List<MenuItem> candidates = Arrays.asList(item1, item2, item3);

        // create dummy menu and orderHistory for engine
        RecommendationEngine engine = new RecommendationEngine(new Menu(), new OrderHistory());
        // get recommendation with calorie limit of 250
        List<MenuItem> recommendation = engine.greedyRecommendation(candidates, 250);

        // check that recommendation contains only items within limit
        assertEquals(2, recommendation.size());
        assertEquals("Item1", recommendation.get(0).getName());
        assertEquals("Item2", recommendation.get(1).getName());
    }
}