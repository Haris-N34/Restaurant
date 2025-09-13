//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

package ca.ucalgary.haris.naveed1.demo3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The MainController class is the primary controller for the McDonald's Nutrition Tracker UI.
 * <p>
 * It handles all the UI interactions including:
 * - Accepting the user's name.
 * - Displaying the menu.
 * - Placing orders using clickable item images and pop-up details.
 * - Viewing order history from a persistent file.
 * - Generating recommendations and allowing double-click ordering.
 * - Managing the order summary on the right-side panel.
 * <p>
 * It interacts with other classes such as Tracker, Menu, RecommendationEngine, and OrderHistory.
 */
public class MainController {
    // FXML-injected UI components
    @FXML private StackPane contentPane;            // Central content area where views are loaded.
    @FXML private Label statusLabel;                  // Status bar to display feedback messages.
    @FXML private TextField nameField;                // Text field for user to input their name.
    @FXML private ListView<String> orderSummaryView;  // Right panel order summary list.

    // Application data
    private Menu menu;                                // The menu containing available items.
    private RecommendationEngine recommendationEngine; // Engine for generating order recommendations.
    private List<MenuItem> currentOrder;              // Stores items that have been added but not submitted.
    private List<MenuItem> sessionOrders;             // Stores all items ordered during this session.
    private List<List<MenuItem>> currentRecommendations; // Holds the most recent set of generated recommendations.

    /**
     * Initializes the controller.
     * <p>
     * This method is automatically called after the FXML elements have been injected.
     * It sets up the menu, recommendation engine, order lists, and displays a default welcome message.
     */
    @FXML
    public void initialize() {
        // Initialize the nutritional tracking data.
        Tracker.initializeMenu();
        // Create a new Menu instance.
        menu = new Menu();
        // Initialize the order lists.
        currentOrder = new ArrayList<>();
        sessionOrders = new ArrayList<>();
        currentRecommendations = new ArrayList<>();
        // Instantiate the RecommendationEngine with the current menu and OrderHistory instance.
        recommendationEngine = new RecommendationEngine(menu, new OrderHistory());

        // Set a default welcome message in the center content pane.
        Label defaultLabel = new Label("Welcome! Please use the dashboard on the left to navigate.");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(defaultLabel);
    }

    /**
     * Handles the submission of the user's name.
     * <p>
     * Validates that the user entered a non-empty name. Updates the status label and displays a greeting.
     */
    @FXML
    public void handleSubmitName() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            // Show a warning if the name is empty.
            showAlert(Alert.AlertType.WARNING, "Name Required", "Please enter your name.");
        } else {
            // Update the status and display a greeting message.
            statusLabel.setText("Welcome, " + name + "!");
            Label greeting = new Label("Hello " + name + ", please use the dashboard on the left to navigate.");
            greeting.setStyle("-fx-font-size: 18px;");
            contentPane.getChildren().clear();
            contentPane.getChildren().add(greeting);
        }
    }

    /**
     * Displays the full menu list in the center pane.
     * <p>
     * Each menu item is displayed along with its nutritional information.
     */
    @FXML
    public void handleViewMenu() {
        ListView<String> menuList = new ListView<>();
        // Loop through each item in the menu and display its nutritional details.
        for (MenuItem item : menu.getItems()) {
            List<Double> nutrition = Tracker.itemNutrition.get(item.getName());
            String info = String.format("%s - %.0f cal, %.1fg protein, %.1fg carbs, %.1fg sugars, %.1fg fat",
                    item.getName(), nutrition.get(0), nutrition.get(1), nutrition.get(2), nutrition.get(3), nutrition.get(4));
            menuList.getItems().add(info);
        }
        menuList.setPrefHeight(400);
        statusLabel.setText("Viewing Menu");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(menuList);
    }

    /**
     * Handles the ordering view by displaying a gallery of clickable images.
     * <p>
     * When an image is clicked, a pop-up window appears showing an enlarged image and detailed nutritional info.
     * The user can add the item to their order by clicking the "Add to Order" button.
     */
    @FXML
    public void handlePlaceOrder() {
        HBox orderInterface = new HBox(20);
        orderInterface.setPadding(new Insets(10));

        // Create a FlowPane to hold the clickable item images.
        FlowPane itemsFlowPane = new FlowPane();
        itemsFlowPane.setHgap(15);
        itemsFlowPane.setVgap(15);
        itemsFlowPane.setPrefWidth(500);

        // Loop through each item in the menu to create its corresponding UI box.
        for (MenuItem item : menu.getItems()) {
            VBox itemBox = new VBox(5);
            itemBox.setAlignment(Pos.CENTER);
            // Retrieve the image path for the item.
            String imagePath = getImagePath(item.getName());
            ImageView imageView;
            try {
                imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            } catch (Exception ex) {
                // Fallback: Create an empty ImageView if image is not found.
                imageView = new ImageView();
            }
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            // Display basic nutritional info (calories and protein).
            List<Double> nutrition = Tracker.itemNutrition.get(item.getName());
            String info = String.format("%.0f cal\n%.1fg protein", nutrition.get(0), nutrition.get(1));
            Label nameLabel = new Label(item.getName());
            Label infoLabel = new Label(info);
            itemBox.getChildren().addAll(imageView, nameLabel, infoLabel);

            // Set an on-click event to open a detailed pop-up window.
            itemBox.setOnMouseClicked(e -> {
                // Create the pop-up stage.
                Stage popupStage = new Stage();
                VBox popupBox = new VBox(10);
                popupBox.setAlignment(Pos.CENTER);
                popupBox.setPadding(new Insets(10));
                popupBox.getStyleClass().add("content-pane");
                // Create an enlarged image for a better view.
                ImageView enlargedImage = new ImageView(new Image(getClass().getResourceAsStream(getImagePath(item.getName()))));
                enlargedImage.setFitWidth(300);
                enlargedImage.setFitHeight(300);
                enlargedImage.setPreserveRatio(true);
                // Prepare detailed nutritional info.
                String detailedInfo = String.format("%s\n%.0f cal, %.1fg protein, %.1fg carbs, %.1fg sugars, %.1fg fat",
                        item.getName(), nutrition.get(0), nutrition.get(1), nutrition.get(2), nutrition.get(3), nutrition.get(4));
                Label detailedInfoLabel = new Label(detailedInfo);
                // "Add to Order" button in the pop-up.
                Button addButton = new Button("Add to Order");
                addButton.setOnAction(ev -> {
                    // Add the item to the current order and update the summary.
                    currentOrder.add(item);
                    updateOrderSummary();
                    statusLabel.setText(item.getName() + " added to order.");
                    popupStage.close();
                });
                popupBox.getChildren().addAll(enlargedImage, detailedInfoLabel, addButton);
                Scene popupScene = new Scene(popupBox);
                popupStage.setScene(popupScene);
                popupStage.setTitle(item.getName());
                popupStage.show();
            });
            itemsFlowPane.getChildren().add(itemBox);
        }
        orderInterface.getChildren().addAll(itemsFlowPane);
        statusLabel.setText("Place your order by clicking on items.");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(orderInterface);
    }

    /**
     * Displays the order history for the current user.
     * <p>
     * Reads the order history from a file ("order_history.txt") and shows only entries for the entered customer name.
     */
    @FXML
    public void handleViewOrderHistory() {
        String customerName = nameField.getText();
        if (customerName == null || customerName.trim().isEmpty()) {
            statusLabel.setText("Please enter your name at the top to view order history.");
            return;
        }
        OrderHistory orderHistory = new OrderHistory();
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(10));
        Label historyLabel = new Label("Order History for: " + customerName);
        TextArea historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefHeight(300);
        StringBuilder historyContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("order_history.txt"))) {
            String line;
            // Parse each line to filter by customer name.
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4 && parts[0].equals(customerName)) {
                    historyContent.append("Date: ").append(parts[1])
                            .append(" - Calories: ").append(parts[2])
                            .append(" - Items: ").append(parts[3]).append("\n");
                }
            }
        } catch (Exception ex) {
            historyContent.append("Error reading order history.");
        }
        historyArea.setText(historyContent.toString());
        historyBox.getChildren().addAll(historyLabel, historyArea);
        statusLabel.setText("Viewing Order History");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(historyBox);
    }

    /**
     * Opens a pop-up alert box with app info, version, and contact information.
     */
    @FXML
    public void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About This Program");
        alert.setHeaderText("McDonald's Nutrition Tracker");
        alert.setContentText(
                "Version: 1.0\n" +
                        "Creators: Shayan Shaikh, Arash Ajdari, Haris Naveed\n\n" +
                        "Contact: shayan.shaikh@ucalgary.ca\n\n" +
                        "This app helps you view, build, and track McDonald's orders\n" +
                        "based on nutritional goals. Designed for CPSC 233 Demo 3."
        );
        alert.showAndWait();
    }


    /**
     * Generates order recommendations based on desired calories and the session's order history.
     * <p>
     * Allows the user to input desired calories, generates multiple recommendations via RecommendationEngine,
     * and displays them in a list. Double-clicking on a recommendation will add that recommended set to the current order.
     */
    @FXML
    public void handleRecommendOrder() {
        VBox recommendBox = new VBox(10);
        recommendBox.setPadding(new Insets(10));
        Label titleLabel = new Label("Order Recommendations");
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        Label caloriesLabel = new Label("Desired Calories:");
        TextField caloriesField = new TextField();
        Button generateButton = new Button("Generate");
        inputBox.getChildren().addAll(caloriesLabel, caloriesField, generateButton);
        ListView<String> recommendationListView = new ListView<>();
        recommendationListView.setPrefHeight(200);

        // Handle recommendation generation on button click.
        generateButton.setOnAction(e -> {
            int desiredCalories;
            try {
                desiredCalories = Integer.parseInt(caloriesField.getText());
            } catch (NumberFormatException ex) {
                statusLabel.setText("Please enter a valid calorie amount.");
                return;
            }
            String customerName = nameField.getText();
            if (customerName == null || customerName.trim().isEmpty()) {
                statusLabel.setText("Please enter your name at the top.");
                return;
            }
            // Generate recommendations using the RecommendationEngine.
            currentRecommendations = recommendationEngine.generateRecommendations(customerName, desiredCalories, sessionOrders);
            recommendationListView.getItems().clear();
            int option = 1;
            // Format each recommendation option for display.
            for (List<MenuItem> rec : currentRecommendations) {
                double totalCals = 0;
                StringBuilder recString = new StringBuilder("Option " + option + ": ");
                for (MenuItem item : rec) {
                    List<Double> nut = Tracker.itemNutrition.get(item.getName());
                    totalCals += nut.get(0);
                    recString.append(item.getName())
                            .append(" (").append(nut.get(0).intValue()).append(" cal), ");
                }
                recString.append("Total: ").append((int) totalCals).append(" cal");
                recommendationListView.getItems().add(recString.toString());
                option++;
            }
            statusLabel.setText("Recommendations generated. Double-click an option to add it to your order.");
        });

        // Double-click listener: add selected recommended order to the current order.
        recommendationListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = recommendationListView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < currentRecommendations.size()) {
                    List<MenuItem> selectedRec = currentRecommendations.get(index);
                    currentOrder.addAll(selectedRec);
                    updateOrderSummary();
                    statusLabel.setText("Recommended order added to your current order.");
                }
            }
        });

        recommendBox.getChildren().addAll(titleLabel, inputBox, recommendationListView);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(recommendBox);
    }

    /**
     * Handles submission of the current order (from the right-side summary panel).
     * <p>
     * Validates the user name and non-empty order. Creates a new order, stores it persistently,
     * adds it to the session order history, and clears the current order.
     */
    @FXML
    public void handleSubmitCurrentOrder() {
        String customerName = nameField.getText();
        if (customerName == null || customerName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Name Required", "Please enter your name at the top.");
            return;
        }
        if (currentOrder.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Order", "Your order is empty.");
            return;
        }
        // Create and store the order.
        Order order = new Order(customerName);
        for (MenuItem item : currentOrder) {
            order.addItem(item);
        }
        OrderHistory oh = new OrderHistory();
        oh.storeOrder(order);
        sessionOrders.addAll(currentOrder);
        currentOrder.clear();
        updateOrderSummary();
        statusLabel.setText("Order submitted and saved.");
        showAlert(Alert.AlertType.INFORMATION, "Order Submitted", "Your order has been submitted successfully!");
    }

    /**
     * Clears the current order and updates the order summary.
     */
    @FXML
    public void handleClearOrder() {
        currentOrder.clear();
        updateOrderSummary();
        statusLabel.setText("Order cleared.");
    }

    /**
     * Updates the order summary ListView in the right panel.
     * <p>
     * Iterates over the current order items and displays a summary for each.
     */
    private void updateOrderSummary() {
        orderSummaryView.getItems().clear();
        for (MenuItem orderItem : currentOrder) {
            List<Double> nut = Tracker.itemNutrition.get(orderItem.getName());
            String summary = String.format("%s - %.0f cal, %.1fg protein",
                    orderItem.getName(), nut.get(0), nut.get(1));
            orderSummaryView.getItems().add(summary);
        }
    }

    /**
     * Utility method to display an alert dialog.
     *
     * @param type    the type of alert (WARNING, INFORMATION, etc.)
     * @param title   the title of the alert dialog.
     * @param content the content message of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Returns the relative path to an image resource based on the item name.
     * <p>
     * The method maps specific menu item names to image file paths located under /image/.
     *
     * @param itemName the name of the menu item.
     * @return the relative path to the corresponding image resource.
     */
    private String getImagePath(String itemName) {
        switch (itemName) {
            case "Big Mac":
                return "/image/big-mac-close-up-5rkpe1pbc6gkrlh7-2.png";
            case "McChicken":
                return "/image/McD-McChicken_(transparent).png";
            case "Filet-O-Fish":
                return "/image/NR_202302_5926-999_Filet-O-Fish_HalfSlice_2000x2000.png";
            case "Cheeseburger":
                return "/image/purepng.com-cheeseburgerburger-food-cheese-fast-cheeseburger-hamburger-9415246000238fulo.png";
            case "Small Fries":
                return "/image/product-Small-Fries-mobile_1.png";
            case "Medium Fries":
                return "/image/pngtree-french-fries-in-a-packet-png-image_19402311.png";
            case "Large Fries":
                return "/image/pngtree-french-fries-in-a-packet-png-image_19402311.png";
            case "McFlurry Regular":
                return "/image/McFlurry-Oreo.png";
            case "McFlurry Snack Size":
                return "/image/705-7053044_mcflurry-oreo-mcdonalds-png-mcflurry-mcdonalds.png";
            // If additional images are needed, add cases here.
            default:
                return "/image/placeholder.png";
        }
    }

    /**
     * Exits the application.
     */
    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
