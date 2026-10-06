module com.example.hellofx {
    requires javafx.controls;
    exports com.example.hellofx;
    // PropertyValueFactory reads Customer getters by reflection
    opens com.example.hellofx to javafx.base;
}
