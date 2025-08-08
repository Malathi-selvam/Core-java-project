package Shop_Management_System;

import java.sql.*;
import java.util.Scanner;

public class Admin {
    private Scanner sc = new Scanner(System.in);

    public void adminside() {										
        System.out.print("Enter Admin Username: ");
        String name = sc.nextLine();
        System.out.print("Enter Password: ");
        String pwd = sc.nextLine();
        if ("admin".equalsIgnoreCase(name) && "87654321".equals(pwd)) {
            adminMenu();
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private void adminMenu() {
        int choice;
        do {
            System.out.println("\n----- Admin Menu -----");
            System.out.println("1. View Menu Items");
            System.out.println("2. Add Menu Item");
            System.out.println("3. Delete Menu Item");
            System.out.println("4. View Order History");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();
            switch (choice) {
                case 1 -> viewMenu();
                case 2 -> addItem();
                case 3 -> deleteItem();
                case 4 -> viewOrderHistoryByDate();
                case 5 -> System.out.println("Exiting admin panel.");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

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


    private void addItem() {
        System.out.print("Enter item ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        System.out.print("Enter item Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter category (Pizza/Burger/Snack/Drink): ");
        String category = sc.nextLine().trim().toLowerCase();

        String sql = "";
        boolean needsPrice = true;

        switch (category) {
            case "pizza" -> sql = "INSERT INTO pizzas (pizza_id, pizza_name, base_price) VALUES (?, ?, ?)";
            case "burger" -> sql = "INSERT INTO burgers (burger_id, burger_name, base_price) VALUES (?, ?, ?)";
            case "snack" -> sql = "INSERT INTO snacks (snack_id, snack_name, price) VALUES (?, ?, ?)";
            case "drink" -> {
                sql = "INSERT INTO drinks (drink_id, drink_name) VALUES (?, ?)";
                needsPrice = false;
            }
            default -> {
                System.out.println("❌ Invalid category.");
                return;
            }
        }

        try (Connection con = Mysql_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, name);
            if (needsPrice) {
                System.out.print("Enter price: ");
                int price = readInt();
                ps.setInt(3, price);
            }

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "✅ Item added successfully." : "❌ Failed to add item.");

        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }


    private void deleteItem() {
        System.out.print("Enter item category (Pizza/Burger/Snack/Drink): ");
        String category = sc.nextLine().trim().toLowerCase();
        System.out.print("Enter item ID to delete: ");
        String id = sc.nextLine().trim().toUpperCase();

        String sql = "";
//        String col = "";

        switch (category) {
            case "pizza" -> {
                sql = "DELETE FROM pizzas WHERE pizza_id = ?";
            }
            case "burger" -> {
                sql = "DELETE FROM burgers WHERE burger_id = ?";
            }
            case "snack" -> {
                sql = "DELETE FROM snacks WHERE snack_id = ?";
            }
            case "drink" -> {
                sql = "DELETE FROM drinks WHERE drink_id = ?";
            }
            default -> {
                System.out.println("❌ Invalid category.");
                return;
            }
        }

        try (Connection con = Mysql_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "✅ Item deleted successfully." : "❌ Item ID not found.");

        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    public void viewOrderHistoryByDate() {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String start = sc.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String end = sc.nextLine();

        String sql = "SELECT order_id, item_id, item_name, quantity, total_price, order_date " +
                     "FROM order_history WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";

        try (Connection con = Mysql_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, start);
            ps.setString(2, end);
            ResultSet rs = ps.executeQuery();

            System.out.printf("\n%-10s %-6s %-30s %-6s %-10s %-20s\n",
                    "OrderID", "ID", "Item", "Qty", "Price", "Date");

            while (rs.next()) {
                System.out.printf("%-10s %-6s %-30s %-6d ₹%-10d %-20s\n",
                        rs.getString("order_id"),
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getInt("total_price"),
                        rs.getString("order_date"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error filtering history: " + e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
