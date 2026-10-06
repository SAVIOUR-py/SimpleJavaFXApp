package com.example.hellofx;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Customer Manager (ICT261 Lecture 3 lab).
 *
 * Responsibilities:
 *   View       - buildForm(), buildTable(), buildMenu() create the controls.
 *   Controller - onSave(), onDelete(), onClear() react to the user.
 *   Service    - CustomerService keeps the ObservableList (in memory).
 *   Rules      - CustomerValidator checks the input.
 */
public class CustomerManagerApp extends Application {

    private static final Logger LOG = Logger.getLogger(CustomerManagerApp.class.getName());

    private final CustomerService service = new CustomerService();

    private Stage stage;
    private TextField nameField;
    private ComboBox<String> provinceBox;
    private Button saveButton;
    private Label statusLabel;
    private TableView<Customer> table;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setTop(buildMenu());

        Label title = new Label("Customer Manager");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label(
                "Enter a customer's name and province, then save. Select a row in the table to delete it.");
        subtitle.getStyleClass().add("app-subtitle");
        subtitle.setWrapText(true);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status");
        statusLabel.setWrapText(true);
        statusLabel.setMinHeight(24);

        Label tableTitle = new Label("Saved customers");
        tableTitle.getStyleClass().add("section-title");

        VBox content = new VBox(14,
                title, subtitle, buildForm(), statusLabel, tableTitle, buildTable(), buildBottomBar());
        content.setPadding(new Insets(20, 24, 20, 24));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);

        Scene scene = new Scene(root, 760, 620);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        stage.setTitle("Customer Manager - 202512536");
        stage.setMinWidth(560);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();

        nameField.requestFocus(); // keyboard users start in the first field
    }

    // ---------------------------------------------------------------- View

    private MenuBar buildMenu() {
        MenuItem closeItem = new MenuItem("_Close");
        closeItem.setMnemonicParsing(true);
        closeItem.setOnAction(e -> stage.close());
        Menu fileMenu = new Menu("_File", null, closeItem);
        fileMenu.setMnemonicParsing(true);

        MenuItem aboutItem = new MenuItem("_About");
        aboutItem.setMnemonicParsing(true);
        aboutItem.setOnAction(e -> {
            Alert about = new Alert(Alert.AlertType.INFORMATION);
            about.initOwner(stage);
            about.setTitle("About");
            about.setHeaderText("Customer Manager");
            about.setContentText("ICT261 Lecture 3 lab.\nCustomers are kept in memory, so closing the app clears them.");
            about.showAndWait();
        });
        Menu helpMenu = new Menu("_Help", null, aboutItem);
        helpMenu.setMnemonicParsing(true);

        return new MenuBar(fileMenu, helpMenu);
    }

    private GridPane buildForm() {
        Label nameLabel = new Label("Customer _name");
        nameLabel.setMnemonicParsing(true);
        nameField = new TextField();
        nameField.setPromptText("e.g., Mary Banda");
        nameLabel.setLabelFor(nameField);

        Label provinceLabel = new Label("_Province");
        provinceLabel.setMnemonicParsing(true);
        provinceBox = new ComboBox<>();
        provinceBox.getItems().addAll(
                "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
                "Muchinga", "Northern", "North-Western", "Southern", "Western");
        provinceBox.setPromptText("Choose a province");
        provinceBox.setMaxWidth(Double.MAX_VALUE);
        // A non-editable ComboBox can show a blank box once its value is cleared;
        // this cell shows the prompt text again whenever nothing is selected.
        provinceBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? provinceBox.getPromptText() : item);
            }
        });
        provinceLabel.setLabelFor(provinceBox);

        saveButton = new Button("_Save customer");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setDefaultButton(true); // Enter presses Save
        saveButton.setOnAction(e -> onSave());

        Button clearButton = new Button("C_lear form");
        clearButton.setOnAction(e -> onClear());

        HBox buttons = new HBox(10, saveButton, clearButton);

        GridPane form = new GridPane();
        form.getStyleClass().add("form-pane");
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(14));

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(120);
        labelColumn.setPrefWidth(130);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labelColumn, fieldColumn);

        // Adding in this order gives the Tab order: name, province, Save, Clear
        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(provinceLabel, 0, 1);
        form.add(provinceBox, 1, 1);
        form.add(buttons, 1, 2);
        return form;
    }

    private TableView<Customer> buildTable() {
        table = new TableView<>();
        table.setItems(service.getCustomers()); // the table listens to this same list
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No customers yet. Add one using the form above."));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Customer name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(380);

        TableColumn<Customer, String> provinceCol = new TableColumn<>("Province");
        provinceCol.setCellValueFactory(new PropertyValueFactory<>("province"));

        table.getColumns().add(nameCol);
        table.getColumns().add(provinceCol);

        // Keyboard users can press the Delete key on the selected row
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                onDelete();
                event.consume();
            }
        });
        return table;
    }

    private HBox buildBottomBar() {
        Button deleteButton = new Button("_Delete selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> onDelete());

        Label countLabel = new Label();
        countLabel.getStyleClass().add("count");
        countLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            int n = service.getCustomers().size();
            return n + (n == 1 ? " customer saved" : " customers saved");
        }, service.getCustomers()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10, deleteButton, spacer, countLabel);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // ---------------------------------------------------------- Controller

    private void onSave() {
        try {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String nameProblem = CustomerValidator.checkName(name);
            if (nameProblem != null) {
                showError(nameProblem);
                nameField.requestFocus();
                return; // keep what the user typed
            }

            String province = provinceBox.getValue();
            String provinceProblem = CustomerValidator.checkProvince(province);
            if (provinceProblem != null) {
                showError(provinceProblem);
                provinceBox.requestFocus();
                return; // keep the name the user typed
            }

            service.add(new Customer(name, province));
            showSuccess("Customer saved: " + name + " (" + province + ").");

            // Clear the fields only after the save succeeded
            clearFields();
            nameField.requestFocus();
        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, "Could not save customer", ex); // technical details go to the log
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Save failed");
            alert.setHeaderText("Customer could not be saved");
            alert.setContentText("Check the details and try again.");
            alert.showAndWait();
        }
    }

    private void onClear() {
        clearFields();
        statusLabel.setText("");
        statusLabel.getStyleClass().removeAll("status-error", "status-success");
        nameField.requestFocus();
    }

    private void onDelete() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a customer first.");
            table.requestFocus();
            return;
        }

        ButtonType delete = new ButtonType("Delete");
        Alert ask = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getName() + " (" + selected.getProvince() + ")?",
                delete, ButtonType.CANCEL);
        ask.initOwner(stage);
        ask.setTitle("Delete customer");
        ask.setHeaderText("Confirm deletion");

        // Closing the dialog or pressing Cancel leaves the record unchanged
        if (ask.showAndWait().orElse(ButtonType.CANCEL) == delete) {
            service.remove(selected);
            showSuccess("Customer deleted: " + selected.getName() + ".");
        } else {
            showSuccess("Deletion cancelled. No customer was changed.");
        }
        table.requestFocus();
    }

    // ------------------------------------------------------------- Helpers

    private void clearFields() {
        nameField.clear();
        provinceBox.getSelectionModel().clearSelection();
        provinceBox.setValue(null);
    }

    private void showError(String message) {
        setStatus("⚠ " + message, "status-error");
    }

    private void showSuccess(String message) {
        setStatus("✓ " + message, "status-success");
    }

    private void setStatus(String text, String styleClass) {
        statusLabel.setText(text);
        statusLabel.getStyleClass().removeAll("status-error", "status-success");
        statusLabel.getStyleClass().add(styleClass);
    }
}
