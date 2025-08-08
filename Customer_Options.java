package Shop_Management_System;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

class OrderItem {
	String id;
	String name;
	int quantity;
	int totalPrice;

	public OrderItem(String id, String name, int quantity, int totalPrice) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}

	@Override
	public String toString() {
		return String.format("[%-4s] %-35s %-10s %-10d\n", id, name, quantity + " Pcs", totalPrice);
	}

}

public class Customer_Options {
    List<OrderItem> cartItems = new ArrayList<>();
    Scanner sc = new Scanner(System.in);
  
    public void viewMenu() {
        try (Connection con = Mysql_connection.getConnection();
             Statement stmt = con.createStatement()) {

            System.out.println("\n========= 📋 FULL MENU =========");

            // 🍕 Pizzas
            ResultSet rs = stmt.executeQuery("SELECT pizza_id, pizza_name, base_price FROM pizzas");
            System.out.println("\n--- 🍕 PIZZAS ---");
            System.out.printf("%-10s %-30s ₹%-5s\n", "ID", "Pizza", "Price");
            while (rs.next()) {
                System.out.printf("%-10s %-30s ₹%-5d\n",
                        rs.getString("pizza_id"),
                        rs.getString("pizza_name"),
                        rs.getInt("base_price"));
            }
            rs.close();

            // 🍔 Burgers
            rs = stmt.executeQuery("SELECT burger_id, burger_name, base_price FROM burgers");
            System.out.println("\n--- 🍔 BURGERS ---");
            System.out.printf("%-10s %-30s ₹%-5s\n", "ID", "Burger", "Price");
            while (rs.next()) {
                System.out.printf("%-10s %-30s ₹%-5d\n",
                        rs.getString("burger_id"),
                        rs.getString("burger_name"),
                        rs.getInt("base_price"));
            }
            rs.close();

            // 🍟 Snacks
            rs = stmt.executeQuery("SELECT snack_id, snack_name, price FROM snacks");
            System.out.println("\n--- 🍟 SNACKS ---");
            System.out.printf("%-10s %-30s ₹%-5s\n", "ID", "Snack", "Price");
            while (rs.next()) {
                System.out.printf("%-10s %-30s ₹%-5d\n",
                        rs.getString("snack_id"),
                        rs.getString("snack_name"),
                        rs.getInt("price"));
            }
            rs.close();

            // 🥤 Drinks
            rs = stmt.executeQuery("SELECT drink_id, drink_name FROM drinks");
            System.out.println("\n--- 🥤 DRINKS ---");
            System.out.printf("%-10s %-30s\n", "ID", "Drink");
            while (rs.next()) {
                System.out.printf("%-10s %-30s\n",
                        rs.getString("drink_id"),
                        rs.getString("drink_name"));
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error displaying menu.");
        }
    }


    public void orderFood() {
        int choice;
        do {
            System.out.println("\n1. Order Pizza\n2. Order Burger\n3. Snacks & Drinks\n4. View Cart / Checkout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt(); sc.nextLine();
            switch (choice) {
                case 1 -> pizza();
                case 2 -> burgers();
                case 3 -> snackAndDrinks();
                case 4 -> cart();
                default -> System.out.println("Invalid choice");
            }
        } while (choice != 4);
    }


    public void pizza() {
        try (Connection con = Mysql_connection.getConnection()) {
            // Step 1: Show Pizza Menu
            System.out.println("\n🍕 Pizzas:");
            String pizzaSql = "SELECT pizza_id, pizza_name, base_price FROM pizzas";
            PreparedStatement psPizza = con.prepareStatement(pizzaSql);
            ResultSet rsPizza = psPizza.executeQuery();
            Map<String, String> pizzaNames = new HashMap<>();
            Map<String, Integer> pizzaPrices = new HashMap<>();
            while (rsPizza.next()) {
                String id = rsPizza.getString("pizza_id");
                String name = rsPizza.getString("pizza_name");
                int price = rsPizza.getInt("base_price");
                System.out.printf("%s - %s (₹%d)\n", id, name, price);
                pizzaNames.put(id, name);
                pizzaPrices.put(id, price);
            }

            // Step 2: Show Crust Menu
            System.out.println("\n🥖 Crusts:");
            String crustSql = "SELECT crust_id, crust_type FROM crusts";
            PreparedStatement psCrust = con.prepareStatement(crustSql);
            ResultSet rsCrust = psCrust.executeQuery();
            Map<String, String> crustMap = new HashMap<>();
            while (rsCrust.next()) {
                System.out.printf("%s - %s\n", rsCrust.getString("crust_id"), rsCrust.getString("crust_type"));
                crustMap.put(rsCrust.getString("crust_id"), rsCrust.getString("crust_type"));
            }

            // Step 3: Show Size Menu
            System.out.println("\n📏 Sizes:");
            String sizeSql = "SELECT size_id, size_label, extra_price FROM sizes";
            PreparedStatement psSize = con.prepareStatement(sizeSql);
            ResultSet rsSize = psSize.executeQuery();
            Map<String, String> sizeMap = new HashMap<>();
            Map<String, Integer> sizePrice = new HashMap<>();
            while (rsSize.next()) {
                String sid = rsSize.getString("size_id");
                String label = rsSize.getString("size_label");
                int extra = rsSize.getInt("extra_price");
                System.out.printf("%s - %s (+₹%d)\n", sid, label, extra);
                sizeMap.put(sid, label);
                sizePrice.put(sid, extra);
            }

            // Step 4: Ask how many types of pizzas
            System.out.print("\nHow many different types of pizzas do you want? ");
            int types = sc.nextInt(); sc.nextLine();

            for (int i = 1; i <= types; i++) {
                System.out.printf("\n🍕 Order %d:\n", i);

                System.out.print("Enter Pizza ID: ");
                String pid = sc.nextLine().trim().toUpperCase();
                if (!pizzaNames.containsKey(pid)) {
                    System.out.println("❌ Invalid Pizza ID. Skipping.");
                    continue;
                }

                System.out.print("Enter Crust ID: ");
                String cid = sc.nextLine().trim().toUpperCase();
                if (!crustMap.containsKey(cid)) {
                    System.out.println("❌ Invalid Crust ID. Skipping.");
                    continue;
                }

                System.out.print("Enter Size ID: ");
                String sid = sc.nextLine().trim().toUpperCase();
                if (!sizeMap.containsKey(sid)) {
                    System.out.println("❌ Invalid Size ID. Skipping.");
                    continue;
                }

                System.out.print("Quantity: ");
                int qty = sc.nextInt(); sc.nextLine();

                // Build the final item
                String pizzaFullName = pizzaNames.get(pid) + " - " + crustMap.get(cid) + " (" + sizeMap.get(sid) + ")";
                int base = pizzaPrices.get(pid);
                int extra = sizePrice.get(sid);
                int total = (base + extra) * qty;

                boolean found = false;
                for (OrderItem existing : cartItems) {
                    if (existing.id.equals(pid) && existing.name.equals(pizzaFullName)) {
                        existing.quantity += qty;
                        existing.totalPrice += total;
                        System.out.println("✅ Updated in cart: " + existing);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    OrderItem item = new OrderItem(pid, pizzaFullName, qty, total);
                    cartItems.add(item);
                    System.out.println("✅ Added to cart: " + item);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void burgers() {
        try (Connection con = Mysql_connection.getConnection()) {
            // Step 1: Show Burger Menu
            String sql = "SELECT burger_id, burger_name, base_price FROM burgers";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Map<String, String> burgerNames = new HashMap<>();
            Map<String, Integer> burgerPrices = new HashMap<>();

            System.out.println("\n🍔 Available Burgers:");
            while (rs.next()) {
                String id = rs.getString("burger_id");
                String name = rs.getString("burger_name");
                int price = rs.getInt("base_price");
                System.out.printf("%s - %s (₹%d)\n", id, name, price);
                burgerNames.put(id, name);
                burgerPrices.put(id, price);
            }

            System.out.print("\nHow many different types of burgers do you want? ");
            int types = sc.nextInt(); sc.nextLine();

            for (int i = 1; i <= types; i++) {
                System.out.printf("\n🍔 Order %d:\n", i);

                System.out.print("Enter Burger ID: ");
                String bid = sc.nextLine().trim().toUpperCase();

                if (!burgerNames.containsKey(bid)) {
                    System.out.println("❌ Invalid Burger ID. Skipping...");
                    continue;
                }

                System.out.print("Quantity: ");
                int qty = sc.nextInt(); sc.nextLine();

                String burgerName = burgerNames.get(bid);
                int price = burgerPrices.get(bid) * qty;

                boolean found = false;
                for (OrderItem existing : cartItems) {
                    if (existing.id.equals(bid) && existing.name.equals(burgerName)) {
                        existing.quantity += qty;
                        existing.totalPrice += price;
                        System.out.println("✅ Updated in cart: " + existing);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    OrderItem item = new OrderItem(bid, burgerName, qty, price);
                    cartItems.add(item);
                    System.out.println("✅ Added to cart: " + item);
                }
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void snackAndDrinks() {
        try (Connection con = Mysql_connection.getConnection()) {

            // ============ SNACKS ============ //
            String snackQuery = "SELECT snack_id, snack_name, price FROM snacks";
            PreparedStatement snackStmt = con.prepareStatement(snackQuery);
            ResultSet snackRs = snackStmt.executeQuery();

            Map<String, String> snackNames = new HashMap<>();
            Map<String, Integer> snackPrices = new HashMap<>();

            System.out.println("\n🍟 Available Snacks:");
            while (snackRs.next()) {
                String sid = snackRs.getString("snack_id");
                String name = snackRs.getString("snack_name");
                int price = snackRs.getInt("price");
                System.out.printf("%s - %s (₹%d)\n", sid, name, price);
                snackNames.put(sid, name);
                snackPrices.put(sid, price);
            }

            System.out.print("\nHow many different snacks do you want? ");
            int snackCount = sc.nextInt(); sc.nextLine();

            for (int i = 1; i <= snackCount; i++) {
                System.out.printf("\nSnack %d:\n", i);
                System.out.print("Enter Snack ID: ");
                String sid = sc.nextLine().trim().toUpperCase();

                if (!snackNames.containsKey(sid)) {
                    System.out.println("❌ Invalid Snack ID. Skipping...");
                    continue;
                }

                System.out.print("Quantity: ");
                int qty = sc.nextInt(); sc.nextLine();
                int price = snackPrices.get(sid) * qty;

                boolean found = false;
                for (OrderItem existing : cartItems) {
                    if (existing.id.equals(sid) && existing.name.equals(snackNames.get(sid))) {
                        existing.quantity += qty;
                        existing.totalPrice += price;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cartItems.add(new OrderItem(sid, snackNames.get(sid), qty, price));
                }

                System.out.println("✅ Added: [" + sid + "] " + snackNames.get(sid) + "  " + qty + " Pcs  ₹" + price);
            }

            snackRs.close();
            snackStmt.close();

            // ============ DRINKS ============ //
            String drinkQuery = "SELECT drink_id, drink_name FROM drinks";
            PreparedStatement drinkStmt = con.prepareStatement(drinkQuery);
            ResultSet drinkRs = drinkStmt.executeQuery();

            Map<String, String> drinkNames = new LinkedHashMap<>();
            System.out.println("\n🥤 Drink Categories:");
            while (drinkRs.next()) {
                String id = drinkRs.getString("drink_id");
                String name = drinkRs.getString("drink_name");
                drinkNames.put(id, name);
                System.out.printf("%s - %s\n", id, name);
            }

            System.out.print("\nHow many drink categories do you want to order from? ");
            int drinkCategoryCount = sc.nextInt(); sc.nextLine();

            for (int i = 1; i <= drinkCategoryCount; i++) {
                System.out.printf("\nDrink %d:\n", i);
                System.out.print("Enter Drink ID (e.g., D01): ");
                String drinkId = sc.nextLine().trim().toUpperCase();

                if (!drinkNames.containsKey(drinkId)) {
                    System.out.println("❌ Invalid Drink ID. Skipping...");
                    continue;
                }

                String flavorQuery = "SELECT flavor_id, flavor_name, price FROM drink_flavors WHERE drink_id = ?";
                PreparedStatement flavorStmt = con.prepareStatement(flavorQuery);
                flavorStmt.setString(1, drinkId);
                ResultSet flavorRs = flavorStmt.executeQuery();

                Map<String, String> flavorNames = new HashMap<>();
                Map<String, Integer> flavorPrices = new HashMap<>();

                System.out.println("Available Flavors:");
                while (flavorRs.next()) {
                    String fid = flavorRs.getString("flavor_id");
                    String fname = flavorRs.getString("flavor_name");
                    int fprice = flavorRs.getInt("price");
                    System.out.printf("%s - %s (₹%d)\n", fid, fname, fprice);
                    flavorNames.put(fid, fname);
                    flavorPrices.put(fid, fprice);
                }

                System.out.print("How many flavors do you want from this category? ");
                int flavorCount = sc.nextInt(); sc.nextLine();

                for (int j = 1; j <= flavorCount; j++) {
                    System.out.print("Enter Flavor ID: ");
                    String fid = sc.nextLine().trim().toUpperCase();
                    if (!flavorNames.containsKey(fid)) {
                        System.out.println("❌ Invalid Flavor ID. Skipping...");
                        continue;
                    }

                    System.out.print("Quantity: ");
                    int qty = sc.nextInt(); sc.nextLine();
                    int price = flavorPrices.get(fid) * qty;

                    boolean found = false;
                    for (OrderItem existing : cartItems) {
                        if (existing.id.equals(fid) && existing.name.equals(flavorNames.get(fid))) {
                            existing.quantity += qty;
                            existing.totalPrice += price;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cartItems.add(new OrderItem(fid, flavorNames.get(fid), qty, price));
                    }

                    System.out.println("✅ Added: [" + fid + "] " + flavorNames.get(fid) + "  " + qty + " Pcs  ₹" + price);
                }

                flavorRs.close();
                flavorStmt.close();
            }

            drinkRs.close();
            drinkStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error occurred during snack & drink ordering.");
        }
    }

    public void cart() {
        System.out.println("\n=== Your Cart ===");
        int total = 0;
        for (OrderItem item : cartItems) {
            System.out.print(item);
            total += item.totalPrice;
        }
        System.out.printf("Total Amount: ₹%d\n", total);
        System.out.println("1. Remove item by ID\n2. Add more items\n3. Checkout");
        int option = sc.nextInt(); sc.nextLine();
        switch (option) {
            case 1 -> { removeItem(); cart(); }
            case 2 -> orderFood();
            case 3 -> askCheckout();
        }
    }


    public void removeItem() {
        System.out.print("Enter item ID to remove: ");
        String id = sc.nextLine().trim();
        boolean removed = cartItems.removeIf(it -> it.id.equalsIgnoreCase(id));
        System.out.println(removed ? "✔️ Removed." : "❌ Item not found.");
    }
    public void askCheckout() {
        System.out.print("Proceed to checkout? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.startsWith("y")) {
            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Address: ");
            String address = sc.nextLine();

            System.out.print("Phone: ");
            long mobile = sc.nextLong(); sc.nextLine();

            finalReceipt(name, address, mobile);
        } else {
            System.out.println("🛒 Returning to cart...");
            cart();
        }
    }

    


    public void finalReceipt(String name, String address, long mobile) {
        int total = 0;
        System.out.println("\n========= FINAL RECEIPT =========");
        for (OrderItem item : cartItems) {
            System.out.print(item);
            total += item.totalPrice;
        }
        System.out.printf("Total: ₹%d\n", total);
        System.out.println("Customer: " + name);
        System.out.println("Address : " + address);
        System.out.println("Phone   : " + mobile);
        System.out.println("✅ Order Confirmed! Thank you for shopping 💖");
        
    }
}