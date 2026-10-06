module com.example.hellofx {
    requires javafx.controls;
    requires java.logging;

    opens com.example.hellofx to javafx.base;
    exports com.example.hellofx;
}