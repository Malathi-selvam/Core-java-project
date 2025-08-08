package Shop_Management_System;

import java.util.*;

public class Main {
	public static void main(String[] args) {
		Scanner role = new Scanner(System.in);
		int option;

		do {
			System.out.println("1. Customer");
			System.out.println("2. Admin");
			System.out.print("Enter option: ");
			option = role.nextInt();
		} while (option != 1 && option != 2);

		if (option == 1) {
			User_details obj1 = new User_details();
			obj1.about_user();

		} else if (option == 2) {
			Admin obj2 = new Admin();

			obj2.adminside();
		}

		role.close();
	}
}
