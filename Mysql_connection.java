package Shop_Management_System;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mysql_connection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/Crust_and_Cup", 
                "root", 
                "malu"
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); 
            return null;         
        }
    }
}
