package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StockManager {

    // all products and their current stock
    public static Map<String, Integer> getAllStock() {
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT item_name, stock FROM inventory");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("item_name"), rs.getInt("stock"));
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Could not read inventory: " + e.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return map;
    }

    public static int getStock(String item) {
        Integer s = getAllStock().get(item);
        return (s == null) ? 0 : s;
    }

    // Takes qty out of stock. Runs inside the caller's transaction.
    // Fails (and the whole sale is rolled back) if there isn't enough stock.
    public static void deduct(Connection con, String item, int qty) throws SQLException {
        String sql = "UPDATE inventory SET stock = stock - ? WHERE item_name = ? AND stock >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setString(2, item);
            ps.setInt(3, qty);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Not enough stock for " + item + ".");
            }
        }
    }
}