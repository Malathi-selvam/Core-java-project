package Shop_Management_System;

import java.sql.*;
import java.util.*;

public class User_details extends Customer_Options {

	Scanner input = new Scanner(System.in);

	public void about_user() {
		while (true) {
			System.out.println("\n~~~~~~~~ Welcome To Our Shop ~~~~~~~~");
			System.out.println("To Verify Your Account:");
			System.out.println(" 1. Sign Up");
			System.out.println(" 2. Login");
			System.out.println(" 3. Exit");
			System.out.print("Choose your option: ");

			int option = input.nextInt();
			input.nextLine();

			switch (option) {
			case 1:
				Signup();
				break;
			case 2:
				Login();
				break;
			case 3:
				System.out.println("👋 Exiting the Shop. Thank you!");
				return;
			default:
				System.out.println("❌ Invalid choice. Please try again.");
			}
		}
	}

	public void Signup() {
		try {
			Connection con = Mysql_connection.getConnection();
			if (con == null) {
				System.out.println("❌ Database connection failed.");
				return;
			}

			String u_email;

			System.out.println("\n--- Sign Up Form ---");
			System.out.println("Type 'back' to return to the main menu.");

			while (true) {
				System.out.print("Email: ");
				u_email = input.nextLine();

				if (u_email.equalsIgnoreCase("back")) {
					System.out.println("↩️ Returning to main menu...");
					return;
				}

				String check = "SELECT * FROM users WHERE email = ?";
				PreparedStatement ps = con.prepareStatement(check);
				ps.setString(1, u_email);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					System.out.println("⚠️ Email already exists. Try another.");
				} else {
					rs.close();
					ps.close();
					break;
				}

				rs.close();
				ps.close();

			}

			String u_pwd;
			while (true) {
				System.out.print("Password (min 8 characters): ");
				u_pwd = input.nextLine();
				if (u_pwd.length() >= 8)
					break;
				System.out.println("❌ Password too short. Try again.");
			}

			System.out.print("Name: ");
			String u_name = input.nextLine();

			System.out.print("Mobile Number: ");
			long mobile = input.nextLong();
			input.nextLine();

			System.out.print("Address: ");
			String address = input.nextLine();

			
			String insert = "INSERT INTO users (name, email, password, mobile, address) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(insert);
			stmt.setString(1, u_name);
			stmt.setString(2, u_email);
			stmt.setString(3, u_pwd);
			stmt.setLong(4, mobile);
			stmt.setString(5, address);

			int result = stmt.executeUpdate();
			if (result > 0) {
				System.out.println("✅ Account created successfully! Please log in now.");

			} else {
				System.out.println("❌ Signup failed.");
			}

			stmt.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Login() {
		try {
			Connection con = Mysql_connection.getConnection();
			if (con == null) {
				System.out.println("❌ Database connection failed.");
				return;
			}

			System.out.println("\n--- Login ---");
			System.out.println("Type 'back' at any time to return to the main menu.");

			System.out.print("Email: ");
			String u_email = input.nextLine();
			if (u_email.equalsIgnoreCase("back")) {
				System.out.println("↩️ Returning to main menu...");
				return;
			}

			System.out.print("Password: ");
			String u_pwd = input.nextLine();
			if (u_pwd.equalsIgnoreCase("back")) {
				System.out.println("↩️ Returning to main menu...");
				return;
			}

			String query = "SELECT * FROM users WHERE email = ? AND password = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, u_email);
			stmt.setString(2, u_pwd);

			ResultSet rs = stmt.executeQuery();
			int co = 0;
			if (rs.next()) {

				String name = rs.getString("name");
				String address = rs.getString("address");
				long mobile = rs.getLong("mobile");

				System.out.println("✅ Login successful! Welcome, " + name);

				do {
					System.out.println(" 1. View Menu ");
					System.out.println(" 2. Order Item ");
					System.out.println(" 3. Cart ");
					System.out.println(" 4. Exit ");
					System.out.print("Choose Option: ");

					if (input.hasNextInt()) {
						co = input.nextInt();
						input.nextLine();
					} else {
						System.out.println("❌ Invalid input. Please enter a number.");
						input.nextLine();
						continue;
					}

					switch (co) {
					case 1:
						this.viewMenu();
						break;
					case 2:
						this.orderFood();
						break;
					case 3:
						this.cart();
						break;
					case 4:
						System.out.println("Confirming your order...");
						this.finalReceipt(name, address, mobile);
						break;
					default:
						System.out.println("Invalid option, please try again.");
					}
				} while (co != 4);

			} else {
				System.out.println("❌ Invalid email or password.");
			}

			rs.close();
			stmt.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		input.close();
	}

}