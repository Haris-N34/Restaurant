# Restaurant
How to use it:
Enter your name at the top and click submit. Then use the sidebar to use the other functions of the program.

## Overview
Viewing the menu:
Click "View Menu" to see all items with cal, protein etc bascially all the nutrional facts.
you can scroll the list in center.

This project is a McDonald's Nutrition Tracker that now features a modern GUI built with JavaFX. The GUI uses a BorderPane layout with a dedicated menu, dynamic content area, and status bar for improved user experience.
Placing an order:
Click "Place Order" to see all food items with images.
Click on any item to open its detail popup. click "Add to Order" to add it.
Your current order is shown on the right side.

## Getting Started
Submit / clear order:
Click "Submit Order" to save it. You will get a popup confirmation.
Click "Clear Order" to remove everything from current order list.

Order history:
Click "Order History" and it will show past orders from the file
(you need to have entered your name first to see it)

Recommendations:
Click "Recommend Order", enter desired calories, then click "Generate".
it will show 3 options. double click any option to add it to your order.

Exit:
Will exit the program

How to run it FROM AND IDE:
open in intellij and run MainGUI.java

Using the jar file:
You can run it with:
java --module-path "path/to/javafx-sdk-23/lib" --add-modules javafx.controls,javafx.fxml -jar CPSC233W25PROJECT.jar

Building the jar:
in intellij go File - Project Structure - Artifacts - make jar from module,
include your Main.fxml and style.css, then build artifact
rename jar to CPSC233W25PROJECT.jar

