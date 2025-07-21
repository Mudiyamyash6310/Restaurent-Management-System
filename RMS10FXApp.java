// Save this as src/application/RMS10FXApp.java (or whatever your package is)
package application;

import javafx.application.Application; 
import javafx.geometry.Insets; 
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Optional;

public class RMS10FXApp extends Application {

    private RestaurantLogic restaurantLogic = new RestaurantLogic(); // Instance of your backend logic

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sai's Family Restaurant");

        // Main Layout (VBox for vertical arrangement)
        VBox root = new VBox(20); // 20 pixels spacing between children
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER); // Center content

        // Title Label
        Label titleLabel = new Label("--- Sai's Family Restaurant ---");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #336699;"); // CSS styling

        Label hoursLabel = new Label("Opening Hours: 11:30 A.M to 10:30 P.M");
        hoursLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        hoursLabel.setStyle("-fx-text-fill: #555555;");

        // Buttons Grid (for better organization of main menu buttons)
        GridPane buttonGrid = new GridPane();
        buttonGrid.setVgap(10); // Vertical gap
        buttonGrid.setHgap(10); // Horizontal gap
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setPadding(new Insets(20, 0, 0, 0));

        // Create Buttons
        Button bookTableButton = createStyledButton("1. Book Table");
        Button viewBookingsButton = createStyledButton("2. View Bookings");
        Button cancelBookingButton = createStyledButton("3. Cancel Booking");
        Button viewMenuButton = createStyledButton("4. View Menu");
        Button orderItemButton = createStyledButton("5. Order Item");
        Button deleteOrderItemButton = createStyledButton("6. Delete Ordered Item");
        Button viewBillButton = createStyledButton("7. View Bill");
        Button makePaymentButton = createStyledButton("8. Make Payment");
        Button adminPanelButton = createStyledButton("9. Admin Panel");
        Button exitButton = createStyledButton("10. Exit");

        // Add buttons to grid
        // Row 0
        buttonGrid.add(bookTableButton, 0, 0);
        buttonGrid.add(viewBookingsButton, 1, 0);
        // Row 1
        buttonGrid.add(cancelBookingButton, 0, 1);
        buttonGrid.add(viewMenuButton, 1, 1);
        // Row 2
        buttonGrid.add(orderItemButton, 0, 2);
        buttonGrid.add(deleteOrderItemButton, 1, 2);
        // Row 3
        buttonGrid.add(viewBillButton, 0, 3);
        buttonGrid.add(makePaymentButton, 1, 3);
        // Row 4
        buttonGrid.add(adminPanelButton, 0, 4);
        buttonGrid.add(exitButton, 1, 4);


        root.getChildren().addAll(titleLabel, hoursLabel, buttonGrid);

        // Set actions for buttons
        bookTableButton.setOnAction(e -> showBookTableDialog(primaryStage));
        viewBookingsButton.setOnAction(e -> showViewBookingsDialog(primaryStage));
        cancelBookingButton.setOnAction(e -> showCancelBookingDialog(primaryStage));
        viewMenuButton.setOnAction(e -> showViewMenuDialog(primaryStage));
        orderItemButton.setOnAction(e -> showOrderItemDialog(primaryStage));
        deleteOrderItemButton.setOnAction(e -> showDeleteOrderItemDialog(primaryStage));
        viewBillButton.setOnAction(e -> showViewBillDialog(primaryStage));
        makePaymentButton.setOnAction(e -> showMakePaymentDialog(primaryStage));
        adminPanelButton.setOnAction(e -> showAdminPanelDialog(primaryStage));
        exitButton.setOnAction(e -> handleExit());

        Scene scene = new Scene(root, 600, 700); // Width, Height
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Link CSS file
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method for consistent button styling
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE); // Make buttons fill available width in grid
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #6699CC; -fx-text-fill: white; -fx-background-radius: 5;");
        return button;
    }

    // --- Dialog Methods for each functionality ---

    private void showBookTableDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Book Table");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Set a password");
        TextField tableNumField = new TextField();
        tableNumField.setPromptText("Table number (1-4)");
        ComboBox<String> timeSlotComboBox = new ComboBox<>();
        timeSlotComboBox.getItems().addAll(restaurantLogic.getTimeSlots());
        timeSlotComboBox.getSelectionModel().selectFirst(); // Select first item by default

        Button bookButton = new Button("Book Table");
        bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        grid.add(new Label("Your Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Set Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Table Number (1-4):"), 0, 2);
        grid.add(tableNumField, 1, 2);
        grid.add(new Label("Time Slot:"), 0, 3);
        grid.add(timeSlotComboBox, 1, 3);
        grid.add(bookButton, 1, 4);

        bookButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText();
            String tableNumStr = tableNumField.getText().trim();
            String selectedTimeSlot = timeSlotComboBox.getValue();

            if (name.isEmpty() || password.isEmpty() || tableNumStr.isEmpty() || selectedTimeSlot == null) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
                return;
            }

            try {
                int tableNum = Integer.parseInt(tableNumStr);
                String result = restaurantLogic.bookTable(name, password, tableNum, selectedTimeSlot);
                if (result.startsWith("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Booking Success", result);
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Booking Error", result);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Table number must be a valid number.");
            }
        });

        Scene scene = new Scene(grid);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showViewBookingsDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("View Bookings");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);

        TextArea bookingDisplayArea = new TextArea();
        bookingDisplayArea.setEditable(false);
        bookingDisplayArea.setPrefRowCount(10);
        bookingDisplayArea.setPrefColumnCount(30);

        Map<Integer, String> bookings = restaurantLogic.viewBookings();
        if (bookings.isEmpty()) {
            bookingDisplayArea.setText("No tables are currently booked.");
        } else {
            StringBuilder sb = new StringBuilder("Booked tables:\n");
            bookings.forEach((tableNum, info) -> sb.append("Table ").append(tableNum).append(" - ").append(info).append("\n"));
            bookingDisplayArea.setText(sb.toString());
        }

        vbox.getChildren().add(bookingDisplayArea);
        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showCancelBookingDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Cancel Booking");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        nameField.setPromptText("Your name");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Booking password");
        TextField tableNumField = new TextField();
        tableNumField.setPromptText("Table number to cancel");

        Button cancelButton = new Button("Cancel Booking");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");


        grid.add(new Label("Your Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Booking Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Table Number:"), 0, 2);
        grid.add(tableNumField, 1, 2);
        grid.add(cancelButton, 1, 3);

        cancelButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText();
            String tableNumStr = tableNumField.getText().trim();

            if (name.isEmpty() || password.isEmpty() || tableNumStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
                return;
            }

            try {
                int tableNum = Integer.parseInt(tableNumStr);
                String result = restaurantLogic.cancelBooking(name, password, tableNum);
                if (result.startsWith("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Cancellation Success", result);
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Cancellation Error", result);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Table number must be a valid number.");
            }
        });

        Scene scene = new Scene(grid);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showViewMenuDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Restaurant Menu");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);

        TextArea menuDisplayArea = new TextArea();
        menuDisplayArea.setEditable(false);
        menuDisplayArea.setPrefRowCount(15);
        menuDisplayArea.setPrefColumnCount(40);

        updateMenuDisplay(menuDisplayArea);

        vbox.getChildren().add(menuDisplayArea);
        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void updateMenuDisplay(TextArea textArea) {
        StringBuilder sb = new StringBuilder("---- Menu ----\n");
        Map<Integer, String> menuItems = restaurantLogic.getMenu();
        Map<Integer, Double> menuPrices = restaurantLogic.getMenuPrices();
        menuItems.forEach((id, name) ->
                sb.append(String.format("%d. %-20s - Rs. %.2f%n", id, name, menuPrices.get(id)))
        );
        textArea.setText(sb.toString());
    }

    private void showOrderItemDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Order Item");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        TextArea menuDisplayArea = new TextArea();
        menuDisplayArea.setEditable(false);
        menuDisplayArea.setPrefRowCount(10);
        menuDisplayArea.setPrefColumnCount(40);
        updateMenuDisplay(menuDisplayArea); // Show the menu

        GridPane inputGrid = new GridPane();
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.setAlignment(Pos.CENTER);

        TextField itemNumField = new TextField();
        itemNumField.setPromptText("Enter item number");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");
        Button orderButton = new Button("Add to Order");
        orderButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");


        inputGrid.add(new Label("Item Number:"), 0, 0);
        inputGrid.add(itemNumField, 1, 0);
        inputGrid.add(new Label("Quantity:"), 0, 1);
        inputGrid.add(quantityField, 1, 1);
        inputGrid.add(orderButton, 1, 2);

        root.getChildren().addAll(menuDisplayArea, inputGrid);

        orderButton.setOnAction(e -> {
            String itemNumStr = itemNumField.getText().trim();
            String quantityStr = quantityField.getText().trim();

            if (itemNumStr.isEmpty() || quantityStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter both item number and quantity.");
                return;
            }

            try {
                int itemNum = Integer.parseInt(itemNumStr);
                int quantity = Integer.parseInt(quantityStr);

                String result = restaurantLogic.orderItem(itemNum, quantity);
                if (result.startsWith("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Order Success", result);
                    itemNumField.clear();
                    quantityField.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Order Error", result);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Item number and quantity must be valid numbers.");
            }
        });

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showDeleteOrderItemDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Delete Ordered Item");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        TextArea orderedItemsDisplayArea = new TextArea();
        orderedItemsDisplayArea.setEditable(false);
        orderedItemsDisplayArea.setPrefRowCount(8);
        orderedItemsDisplayArea.setPrefColumnCount(30);
        updateOrderedItemsDisplay(orderedItemsDisplayArea);

        GridPane inputGrid = new GridPane();
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.setAlignment(Pos.CENTER);

        TextField itemIdField = new TextField();
        itemIdField.setPromptText("Enter item ID to remove");
        TextField qtyToRemoveField = new TextField();
        qtyToRemoveField.setPromptText("Quantity to remove");
        Button deleteButton = new Button("Remove Item");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");


        inputGrid.add(new Label("Item ID:"), 0, 0);
        inputGrid.add(itemIdField, 1, 0);
        inputGrid.add(new Label("Quantity to Remove:"), 0, 1);
        inputGrid.add(qtyToRemoveField, 1, 1);
        inputGrid.add(deleteButton, 1, 2);

        root.getChildren().addAll(orderedItemsDisplayArea, inputGrid);

        deleteButton.setOnAction(e -> {
            String itemIdStr = itemIdField.getText().trim();
            String qtyToRemoveStr = qtyToRemoveField.getText().trim();

            if (itemIdStr.isEmpty() || qtyToRemoveStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter both item ID and quantity.");
                return;
            }

            try {
                int itemId = Integer.parseInt(itemIdStr);
                int qtyToRemove = Integer.parseInt(qtyToRemoveStr);

                String result = restaurantLogic.deleteOrderedItem(itemId, qtyToRemove);
                if (result.startsWith("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Success", result);
                    updateOrderedItemsDisplay(orderedItemsDisplayArea); // Refresh display
                    itemIdField.clear();
                    qtyToRemoveField.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Error", result);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Item ID and quantity must be valid numbers.");
            }
        });

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void updateOrderedItemsDisplay(TextArea textArea) {
        Map<Integer, Integer> orderedItems = restaurantLogic.getOrderedItems();
        if (orderedItems.isEmpty()) {
            textArea.setText("No ordered items to display.");
            return;
        }
        StringBuilder sb = new StringBuilder("Your ordered items:\n");
        Map<Integer, String> menuItems = restaurantLogic.getMenu();
        Map<Integer, Double> menuPrices = restaurantLogic.getMenuPrices();

        orderedItems.forEach((itemId, qty) ->
                sb.append(String.format("%d. %s - Quantity: %d (Rs. %.2f)%n",
                        itemId, menuItems.get(itemId), qty, qty * menuPrices.get(itemId)))
        );
        textArea.setText(sb.toString());
    }

    private void showViewBillDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Your Bill");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);

        TextArea billDisplayArea = new TextArea();
        billDisplayArea.setEditable(false);
        billDisplayArea.setPrefRowCount(10);
        billDisplayArea.setPrefColumnCount(30);

        updateBillDisplay(billDisplayArea);

        vbox.getChildren().add(billDisplayArea);
        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void updateBillDisplay(TextArea textArea) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total Bill: Rs. %.2f%n", restaurantLogic.getTotalBill()));

        Map<Integer, Integer> orderedItems = restaurantLogic.getOrderedItems();
        if (!orderedItems.isEmpty()) {
            sb.append("\nOrdered items details:\n");
            Map<Integer, String> menuItems = restaurantLogic.getMenu();
            Map<Integer, Double> menuPrices = restaurantLogic.getMenuPrices();
            orderedItems.forEach((itemId, qty) ->
                    sb.append(String.format("%d x %s = Rs. %.2f%n",
                            qty, menuItems.get(itemId), qty * menuPrices.get(itemId)))
            );
        }
        textArea.setText(sb.toString());
    }

    private void showMakePaymentDialog(Stage parentStage) {
        if (restaurantLogic.getTotalBill() <= 0) {
            showAlert(Alert.AlertType.INFORMATION, "No Bill", "No pending bill to pay.");
            return;
        }

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Make Payment");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label currentBillLabel = new Label(String.format("Final Bill Amount: Rs. %.2f", restaurantLogic.getTotalBill()));
        currentBillLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        currentBillLabel.setStyle("-fx-text-fill: #333333;");

        ToggleGroup paymentMethodGroup = new ToggleGroup();
        RadioButton cashRadio = new RadioButton("Cash Payment");
        cashRadio.setToggleGroup(paymentMethodGroup);
        RadioButton scannerRadio = new RadioButton("Scanner Payment");
        scannerRadio.setToggleGroup(paymentMethodGroup);
        cashRadio.setSelected(true); // Default selection

        HBox paymentMethodButtons = new HBox(15, cashRadio, scannerRadio);
        paymentMethodButtons.setAlignment(Pos.CENTER);

        TextField amountPaidField = new TextField();
        amountPaidField.setPromptText("Enter amount paid");
        TextField tipField = new TextField("0.0"); // Default tip to 0
        tipField.setPromptText("Enter tip amount (optional)");

        cashRadio.setOnAction(e -> tipField.setDisable(false));
        scannerRadio.setOnAction(e -> {
            tipField.setText("0.0");
            tipField.setDisable(true);
        });

        Button payButton = new Button("Process Payment");
        payButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold;");


        vbox.getChildren().addAll(
                currentBillLabel,
                paymentMethodButtons,
                new Label("Amount Paid:"),
                amountPaidField,
                new Label("Tip Amount:"),
                tipField,
                payButton
        );

        payButton.setOnAction(e -> {
            try {
                double paidAmount = Double.parseDouble(amountPaidField.getText());
                double tipAmount = Double.parseDouble(tipField.getText());
                boolean isScannerPayment = scannerRadio.isSelected();

                String result = restaurantLogic.makePayment(paidAmount, tipAmount, isScannerPayment);

                if (result.startsWith("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Payment Success", result);
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Payment Error", result);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values for amounts.");
            }
        });

        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }


    private void showAdminPanelDialog(Stage parentStage) {
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Admin Access");
        passwordDialog.setHeaderText("Enter Admin Password");
        passwordDialog.setContentText("Password:");
        Optional<String> result = passwordDialog.showAndWait();

        if (result.isEmpty() || !result.get().equals("admin123")) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "Incorrect admin password.");
            return;
        }

        Stage adminStage = new Stage();
        adminStage.initModality(Modality.WINDOW_MODAL);
        adminStage.initOwner(parentStage);
        adminStage.setTitle("Admin Panel");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox menuButtons = new VBox(10);
        menuButtons.setPadding(new Insets(0, 20, 0, 0));
        menuButtons.setAlignment(Pos.TOP_LEFT);

        Button addMenuItemBtn = createStyledButton("Add Menu Item");
        Button removeMenuItemBtn = createStyledButton("Remove Menu Item");
        Button updateItemPriceBtn = createStyledButton("Update Item Price");
        Button viewMenuAdminBtn = createStyledButton("View Full Menu");
        Button manageStaffBtn = createStyledButton("Manage Staff");
        Button manageInventoryBtn = createStyledButton("Manage Inventory");
        Button viewInventoryBtn = createStyledButton("View Inventory");
        Button backBtn = createStyledButton("Back to Main");

        menuButtons.getChildren().addAll(
                addMenuItemBtn, removeMenuItemBtn, updateItemPriceBtn,
                viewMenuAdminBtn, manageStaffBtn, manageInventoryBtn,
                viewInventoryBtn, backBtn
        );
        root.setLeft(menuButtons);

        // Content Area for different admin functions
        StackPane contentArea = new StackPane();
        root.setCenter(contentArea);

        // Set initial view
        showAdminViewMenuPanel(contentArea); // Default view when admin panel opens

        // Button Actions
        addMenuItemBtn.setOnAction(e -> showAddMenuItemPanel(contentArea));
        removeMenuItemBtn.setOnAction(e -> showRemoveMenuItemPanel(contentArea));
        updateItemPriceBtn.setOnAction(e -> showUpdateItemPricePanel(contentArea));
        viewMenuAdminBtn.setOnAction(e -> showAdminViewMenuPanel(contentArea));
        manageStaffBtn.setOnAction(e -> showManageStaffPanel(contentArea));
        manageInventoryBtn.setOnAction(e -> showManageInventoryPanel(contentArea));
        viewInventoryBtn.setOnAction(e -> showViewInventoryPanel(contentArea));
        backBtn.setOnAction(e -> adminStage.close());

        Scene scene = new Scene(root, 900, 600); // Larger size for admin panel
        adminStage.setScene(scene);
        adminStage.showAndWait();
    }

    // Admin sub-panels (similar structure to Swing, but using JavaFX components)
    private void showAddMenuItemPanel(StackPane contentArea) {
        contentArea.getChildren().clear(); // Clear previous content

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter item name");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter price");
        Button addButton = new Button("Add Item");
        addButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");


        grid.add(new Label("Item Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(addButton, 1, 2);

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            if (name.isEmpty() || priceStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields.");
                return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                String result = restaurantLogic.addMenuItem(name, price);
                showAlert(Alert.AlertType.INFORMATION, "Add Item", result);
                nameField.clear();
                priceField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a number.");
            }
        });
        contentArea.getChildren().add(grid);
    }

    private void showRemoveMenuItemPanel(StackPane contentArea) {
        contentArea.getChildren().clear();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        idField.setPromptText("Enter item ID to remove");
        Button removeButton = new Button("Remove Item");
        removeButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-weight: bold;");


        grid.add(new Label("Item ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(removeButton, 1, 1);

        removeButton.setOnAction(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter an item ID.");
                return;
            }
            try {
                int removeId = Integer.parseInt(idStr);
                String result = restaurantLogic.removeMenuItem(removeId);
                showAlert(Alert.AlertType.INFORMATION, "Remove Item", result);
                idField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Item ID must be a number.");
            }
        });
        contentArea.getChildren().add(grid);
    }

    private void showUpdateItemPricePanel(StackPane contentArea) {
        contentArea.getChildren().clear();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        idField.setPromptText("Enter item ID to update");
        TextField newPriceField = new TextField();
        newPriceField.setPromptText("Enter new price");
        Button updateButton = new Button("Update Price");
        updateButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold;");


        grid.add(new Label("Item ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("New Price:"), 0, 1);
        grid.add(newPriceField, 1, 1);
        grid.add(updateButton, 1, 2);

        updateButton.setOnAction(e -> {
            String idStr = idField.getText().trim();
            String newPriceStr = newPriceField.getText().trim();
            if (idStr.isEmpty() || newPriceStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields.");
                return;
            }
            try {
                int updateId = Integer.parseInt(idStr);
                double newPrice = Double.parseDouble(newPriceStr);
                String result = restaurantLogic.updateItemPrice(updateId, newPrice);
                showAlert(Alert.AlertType.INFORMATION, "Update Price", result);
                idField.clear();
                newPriceField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID and price must be numbers.");
            }
        });
        contentArea.getChildren().add(grid);
    }

    private void showAdminViewMenuPanel(StackPane contentArea) {
        contentArea.getChildren().clear();
        TextArea menuDisplayArea = new TextArea();
        menuDisplayArea.setEditable(false);
        menuDisplayArea.setPrefRowCount(20);
        menuDisplayArea.setPrefColumnCount(50);
        updateMenuDisplay(menuDisplayArea); // Reuse existing menu update logic
        contentArea.getChildren().add(menuDisplayArea);
    }

    private void showManageStaffPanel(StackPane contentArea) {
        contentArea.getChildren().clear();

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

        Label waitingStaffLabel = new Label("Waiting Staff:");
        TextArea waitingStaffArea = new TextArea();
        waitingStaffArea.setEditable(false);
        waitingStaffArea.setPrefRowCount(5);
        waitingStaffArea.setText(String.join("\n", restaurantLogic.getWaitingStaff()));

        TextField addWaiterField = new TextField();
        addWaiterField.setPromptText("Add new waiting staff name");
        Button addWaiterButton = new Button("Add Waiter");
        addWaiterButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");


        Label kitchenStaffLabel = new Label("Kitchen Staff:");
        TextArea kitchenStaffArea = new TextArea();
        kitchenStaffArea.setEditable(false);
        kitchenStaffArea.setPrefRowCount(5);
        kitchenStaffArea.setText(String.join("\n", restaurantLogic.getKitchenStaff()));

        TextField addCookField = new TextField();
        addCookField.setPromptText("Add new kitchen staff name");
        Button addCookButton = new Button("Add Cook");
        addCookButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");


        addWaiterButton.setOnAction(e -> {
            String name = addWaiterField.getText().trim();
            if (!name.isEmpty()) {
                String result = restaurantLogic.addWaitingStaff(name);
                showAlert(result.startsWith("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Add Waiter", result);
                waitingStaffArea.setText(String.join("\n", restaurantLogic.getWaitingStaff()));
                addWaiterField.clear();
            }
        });

        addCookButton.setOnAction(e -> {
            String name = addCookField.getText().trim();
            if (!name.isEmpty()) {
                String result = restaurantLogic.addKitchenStaff(name);
                showAlert(result.startsWith("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Add Cook", result);
                kitchenStaffArea.setText(String.join("\n", restaurantLogic.getKitchenStaff()));
                addCookField.clear();
            }
        });

        vbox.getChildren().addAll(
                waitingStaffLabel, waitingStaffArea, new HBox(10, addWaiterField, addWaiterButton),
                new Separator(), // Visual separator
                kitchenStaffLabel, kitchenStaffArea, new HBox(10, addCookField, addCookButton)
        );
        contentArea.getChildren().add(vbox);
    }

    private void showManageInventoryPanel(StackPane contentArea) {
        contentArea.getChildren().clear();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField ingredientNameField = new TextField();
        ingredientNameField.setPromptText("Ingredient Name (e.g., rice)");
        TextField quantityChangeField = new TextField();
        quantityChangeField.setPromptText("Quantity to add/reduce (+/-)");
        Button updateButton = new Button("Update Inventory");
        updateButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold;");


        grid.add(new Label("Ingredient Name:"), 0, 0);
        grid.add(ingredientNameField, 1, 0);
        grid.add(new Label("Quantity Change:"), 0, 1);
        grid.add(quantityChangeField, 1, 1);
        grid.add(updateButton, 1, 2);

        updateButton.setOnAction(e -> {
            String name = ingredientNameField.getText().trim();
            String qtyChangeStr = quantityChangeField.getText().trim();
            if (name.isEmpty() || qtyChangeStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields.");
                return;
            }
            try {
                int qtyChange = Integer.parseInt(qtyChangeStr);
                String result = restaurantLogic.updateInventory(name, qtyChange);
                showAlert(result.startsWith("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Update Inventory", result);
                ingredientNameField.clear();
                quantityChangeField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity change must be a number.");
            }
        });
        contentArea.getChildren().add(grid);
    }

    private void showViewInventoryPanel(StackPane contentArea) {
        contentArea.getChildren().clear();
        TextArea inventoryDisplayArea = new TextArea();
        inventoryDisplayArea.setEditable(false);
        inventoryDisplayArea.setPrefRowCount(20);
        inventoryDisplayArea.setPrefColumnCount(50);

        StringBuilder sb = new StringBuilder("--- Current Inventory Status ---\n");
        sb.append(String.format("%-20s %-15s %-15s%n", "Ingredient", "Total Qty", "Available Qty"));
        Map<String, Integer> totalInv = restaurantLogic.getTotalInventory();
        Map<String, Integer> availInv = restaurantLogic.getAvailableInventory();

        totalInv.forEach((ingredient, totalQty) -> {
            sb.append(String.format("%-20s %-15d %-15d%n",
                    ingredient, totalQty, availInv.getOrDefault(ingredient, 0)));
        });
        inventoryDisplayArea.setText(sb.toString());
        contentArea.getChildren().add(inventoryDisplayArea);
    }


    private void handleExit() {
        if (restaurantLogic.getTotalBill() > 0) {
            showAlert(Alert.AlertType.WARNING, "Exit Warning", "Please make payment before exiting.");
        } else {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Exit Application");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to exit?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                showAlert(Alert.AlertType.INFORMATION, "Goodbye", "Exiting system. Thank you! Visit again!");
                System.exit(0);
            }
        }
    }

    // Helper method for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args); // This is how JavaFX applications are launched
    }
}