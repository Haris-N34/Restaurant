module ca.ucalgary.haris.naveed1.demo3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;
    requires junit;


    opens ca.ucalgary.haris.naveed1.demo3 to javafx.fxml;
    exports ca.ucalgary.haris.naveed1.demo3;
}