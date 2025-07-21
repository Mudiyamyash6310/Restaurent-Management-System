// Save this as src/application/RestaurantLogic.java (or whatever your package is)
package application;

import java.util.*;

public class RestaurantLogic {

    private HashMap<Integer, Double> tablePrices = new HashMap<>();
    private HashMap<Integer, String> menu = new HashMap<>();
    private HashMap<Integer, Double> menuPrices = new HashMap<>();

    private HashMap<Integer, String> bookedTables = new HashMap<>();
    private HashMap<Integer, String> tablePasswords = new HashMap<>();

    private double totalBill = 0.0;
    private int itemIdCounter = 10;

    private List<String> waitingStaff = new ArrayList<>();
    private List<String> kitchenStaff = new ArrayList<>();

    private HashMap<String, Integer> inventory = new HashMap<>();
    private HashMap<String, Integer> totalInventory = new HashMap<>();

    private HashMap<Integer, HashMap<String, Integer>> itemIngredients = new HashMap<>();

    private List<String> timeSlots = Arrays.asList("12:00 PM", "1:00 PM", "2:00 PM", "7:00 PM", "8:00 PM");

    // To track ordered items and their quantities
    private HashMap<Integer, Integer> orderedItems = new HashMap<>();

    // Define opening and closing time in 24-hour format for internal checks (not directly used by UI here)
    private final int OPEN_HOUR = 11;
    private final int OPEN_MINUTE = 30;
    private final int CLOSE_HOUR = 22;
    private final int CLOSE_MINUTE = 30;

    private double totalTips = 0.0;

    public RestaurantLogic() {
        initializeData();
    }

    private void initializeData() {
        tablePrices.put(1, 50.0);
        tablePrices.put(2, 50.0);
        tablePrices.put(3, 100.0);
        tablePrices.put(4, 100.0);
        

        menu.put(1, "chicken biriyani");
        menu.put(2, "veg biriyani");
        menu.put(3, "meals");
        menu.put(4, "coffee");
        menu.put(5, "tea");
        menu.put(6, "veg pizza");
        menu.put(7, "burger");
        menu.put(8, "corn pizza");
        menu.put(9, "paneer pizza");

        // Prices
        menuPrices.put(1, 250.0);
        menuPrices.put(2, 200.0);
        menuPrices.put(3, 120.0);
        menuPrices.put(4, 50.0);
        menuPrices.put(5, 40.0);
        menuPrices.put(6, 100.0);
        menuPrices.put(7, 80.0);
        menuPrices.put(8, 150.0);
        menuPrices.put(9, 180.0);

        HashMap<String, Integer> biryaniIngredients = new HashMap<>();
        biryaniIngredients.put("rice", 1);
        biryaniIngredients.put("chicken", 1);
        itemIngredients.put(1, biryaniIngredients);

        HashMap<String, Integer> vegBiryaniIngredients = new HashMap<>();
        vegBiryaniIngredients.put("rice", 1);
        vegBiryaniIngredients.put("vegetables", 1);
        itemIngredients.put(2, vegBiryaniIngredients);

        HashMap<String, Integer> coffeeIngredients = new HashMap<>();
        coffeeIngredients.put("milk", 1);
        coffeeIngredients.put("coffee powder", 1);
        itemIngredients.put(4, coffeeIngredients);

        // Initialize total and current inventory based on ingredients
        for (HashMap<String, Integer> ingredients : itemIngredients.values()) {
            for (String ingredient : ingredients.keySet()) {
                if (!totalInventory.containsKey(ingredient)) {
                    totalInventory.put(ingredient, 20); // Default initial quantity
                    inventory.put(ingredient, 20);      // Current available quantity
                }
            }
        }
        // Add some more general inventory items
        totalInventory.put("sugar", 50); inventory.put("sugar", 50);
        totalInventory.put("salt", 50); inventory.put("salt", 50);
        totalInventory.put("flour", 30); inventory.put("flour", 30);
    }

    // --- Public Methods for UI to interact with ---

    public String bookTable(String name, String password, int tableNum, String selectedTimeSlot) {
        if (bookedTables.containsKey(tableNum)) {
            return "Error: Table " + tableNum + " is already booked by another customer.";
        } else if (tablePrices.containsKey(tableNum)) {
            // Check if this time slot is already booked for this table
            boolean timeSlotBooked = bookedTables.entrySet().stream()
                    .anyMatch(entry -> entry.getKey().equals(tableNum) && entry.getValue().contains(selectedTimeSlot));

            if (timeSlotBooked) {
                return "Error: Table " + tableNum + " is already booked for " + selectedTimeSlot + ".";
            }

            bookedTables.put(tableNum, name + " booked at " + selectedTimeSlot);
            tablePasswords.put(tableNum, password);
            double price = tablePrices.get(tableNum);
            totalBill += price;
            return "Success: Table " + tableNum + " booked for Rs." + price + " at " + selectedTimeSlot;
        } else {
            return "Error: Invalid table number.";
        }
    }

    public Map<Integer, String> viewBookings() {
        return Collections.unmodifiableMap(bookedTables); // Return an unmodifiable map
    }

    public String cancelBooking(String name, String password, int tableNum) {
        if (bookedTables.containsKey(tableNum)) {
            String bookedInfo = bookedTables.get(tableNum);
            String storedPassword = tablePasswords.get(tableNum);
            String bookedNameOnly = bookedInfo.split(" booked at ")[0]; // Extract name portion

            if (bookedNameOnly.equalsIgnoreCase(name) && storedPassword.equals(password)) {
                double price = tablePrices.get(tableNum);
                totalBill -= price;
                bookedTables.remove(tableNum);
                tablePasswords.remove(tableNum);
                if (totalBill < 0) totalBill = 0; // Safeguard
                return "Success: Booking for table " + tableNum + " has been cancelled.";
            } else {
                return "Error: Invalid name or password. Cannot cancel booking.";
            }
        } else {
            return "Error: No such booking found.";
        }
    }

    public Map<Integer, String> getMenu() {
        return Collections.unmodifiableMap(menu);
    }

    public Map<Integer, Double> getMenuPrices() {
        return Collections.unmodifiableMap(menuPrices);
    }

    public String orderItem(int itemNum, int quantity) {
        if (quantity <= 0) {
            return "Error: Quantity must be positive.";
        }
        if (menu.containsKey(itemNum)) {
            if (itemIngredients.containsKey(itemNum)) {
                HashMap<String, Integer> required = itemIngredients.get(itemNum);
                int maxOrderAllowed = Integer.MAX_VALUE;

                for (String ing : required.keySet()) {
                    int needPerUnit = required.get(ing);
                    int avail = inventory.getOrDefault(ing, 0);
                    int maxForThisIng = avail / needPerUnit;
                    if (maxForThisIng < maxOrderAllowed) {
                        maxOrderAllowed = maxForThisIng;
                    }
                    int need = needPerUnit * quantity;
                    if (avail < need) {
                        return "Error: Not enough " + ing + ". Needed: " + need + ", Available: " + avail + "\n" +
                                "Maximum you can order is: " + maxOrderAllowed + " units.";
                    }
                }

                for (String ing : required.keySet()) {
                    int need = required.get(ing) * quantity;
                    inventory.put(ing, inventory.get(ing) - need);
                }
            }

            double price = menuPrices.get(itemNum) * quantity;
            totalBill += price;
            orderedItems.put(itemNum, orderedItems.getOrDefault(itemNum, 0) + quantity);
            return "Success: " + quantity + " x " + menu.get(itemNum) + " ordered for Rs." + price;
        } else {
            return "Error: Invalid item number.";
        }
    }

    public String deleteOrderedItem(int itemId, int qtyToRemove) {
        if (!orderedItems.containsKey(itemId)) {
            return "Error: You have not ordered this item.";
        }
        int currentQty = orderedItems.get(itemId);
        if (qtyToRemove <= 0 || qtyToRemove > currentQty) {
            return "Error: Invalid quantity to remove. Current: " + currentQty + ", Requested: " + qtyToRemove;
        }

        // Adjust inventory to restore ingredients
        if (itemIngredients.containsKey(itemId)) {
            HashMap<String, Integer> required = itemIngredients.get(itemId);
            for (String ing : required.keySet()) {
                int restoredQty = required.get(ing) * qtyToRemove;
                inventory.put(ing, inventory.getOrDefault(ing, 0) + restoredQty);
            }
        }

        // Adjust ordered items and total bill
        if (qtyToRemove == currentQty) {
            orderedItems.remove(itemId);
        } else {
            orderedItems.put(itemId, currentQty - qtyToRemove);
        }
        double priceReduction = menuPrices.get(itemId) * qtyToRemove;
        totalBill -= priceReduction;
        if (totalBill < 0) totalBill = 0; // safeguard

        return "Success: " + qtyToRemove + " of " + menu.get(itemId) + " removed. Rs." + String.format("%.2f", priceReduction) + " deducted from bill.";
    }

    public double getTotalBill() {
        return totalBill;
    }

    public Map<Integer, Integer> getOrderedItems() {
        return Collections.unmodifiableMap(orderedItems);
    }

    public String makePayment(double paidAmount, double tipAmount, boolean isScannerPayment) {
        if (totalBill <= 0 && tipAmount <= 0) {
            return "Error: No pending bill to pay.";
        }

        if (tipAmount < 0) {
            return "Error: Tip cannot be negative.";
        }

        double finalBillWithTip = totalBill + tipAmount;

        if (paidAmount < finalBillWithTip) {
            return "Error: Insufficient payment. Rs." + String.format("%.2f", (finalBillWithTip - paidAmount)) + " still due.";
        } else {
            double change = paidAmount - finalBillWithTip;
            totalTips += tipAmount; // Accumulate total tips
            totalBill = 0.0; // Reset bill for next customer
            bookedTables.clear();
            tablePasswords.clear();
            orderedItems.clear();
            // Do not reset totalTips here, it should accumulate over time for admin view
            return "Success: Payment successful. Change: Rs." + String.format("%.2f", change) +
                   (tipAmount > 0 ? " (Including tip of Rs." + String.format("%.2f", tipAmount) + ")" : "");
        }
    }


    // Admin Panel Methods
    public String addMenuItem(String name, double price) {
        // Find next available ID
        int newId = itemIdCounter;
        while(menu.containsKey(newId)) {
            newId++;
        }
        itemIdCounter = newId + 1; // Update counter for next use

        menu.put(newId, name);
        menuPrices.put(newId, price);
        return "Success: Item '" + name + "' added with ID: " + newId;
    }

    public String removeMenuItem(int removeId) {
        if (menu.containsKey(removeId)) {
            String itemName = menu.get(removeId);
            menu.remove(removeId);
            menuPrices.remove(removeId);
            itemIngredients.remove(removeId);
            orderedItems.remove(removeId); // Ensure consistency
            return "Success: Item '" + itemName + "' (ID: " + removeId + ") removed.";
        } else {
            return "Error: Item not found with ID: " + removeId + ".";
        }
    }

    public String updateItemPrice(int updateId, double newPrice) {
        if (menu.containsKey(updateId)) {
            String itemName = menu.get(updateId);
            double oldPrice = menuPrices.get(updateId);
            menuPrices.put(updateId, newPrice);
            return "Success: Price for '" + itemName + "' (ID: " + updateId + ") updated from Rs." + oldPrice + " to Rs." + newPrice + ".";
        } else {
            return "Error: Item not found with ID: " + updateId + ".";
        }
    }

    public String addWaitingStaff(String name) {
        if (!name.trim().isEmpty() && !waitingStaff.contains(name.trim())) {
            waitingStaff.add(name.trim());
            return "Success: Waiting staff '" + name + "' added.";
        }
        return "Error: Invalid or duplicate waiting staff name.";
    }

    public String addKitchenStaff(String name) {
        if (!name.trim().isEmpty() && !kitchenStaff.contains(name.trim())) {
            kitchenStaff.add(name.trim());
            return "Success: Kitchen staff '" + name + "' added.";
        }
        return "Error: Invalid or duplicate kitchen staff name.";
    }

    public List<String> getWaitingStaff() {
        return Collections.unmodifiableList(waitingStaff);
    }

    public List<String> getKitchenStaff() {
        return Collections.unmodifiableList(kitchenStaff);
    }

    public String updateInventory(String invItem, int qtyChange) {
        if (invItem.trim().isEmpty()) {
            return "Error: Ingredient name cannot be empty.";
        }
        int currentTotal = totalInventory.getOrDefault(invItem, 0);
        int currentAvail = inventory.getOrDefault(invItem, 0);

        int newTotal = currentTotal + qtyChange;
        int newAvail = currentAvail + qtyChange;

        if (newTotal < 0 || newAvail < 0) {
            return "Error: Quantity cannot be negative after update.";
        } else {
            totalInventory.put(invItem, newTotal);
            inventory.put(invItem, newAvail);
            return "Success: Inventory for '" + invItem + "' updated by " + qtyChange + ". New total: " + newTotal + ", New available: " + newAvail + ".";
        }
    }

    public Map<String, Integer> getTotalInventory() {
        return Collections.unmodifiableMap(totalInventory);
    }

    public Map<String, Integer> getAvailableInventory() {
        return Collections.unmodifiableMap(inventory);
    }

    public double getTotalTips() {
        return totalTips;
    }

    public List<String> getTimeSlots() {
        return Collections.unmodifiableList(timeSlots);
    }
}