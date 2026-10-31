package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Session {
    private static String shiftId = null;
    private static String staffCode;
    private static String staffName;

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final java.security.SecureRandom RND = new java.security.SecureRandom();

    // random 5-character shift ID like 7HX4T
    private static String newShiftId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(CHARS.charAt(RND.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    // call this right after a successful staff login
    public static boolean startShift(String code, String name) {
        String sql = "INSERT INTO shifts (shift_id, staff_code, staff_name, start_time) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            for (int attempt = 0; attempt < 10; attempt++) {
                String id = newShiftId();
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.setString(2, code);
                    ps.setString(3, name);
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    ps.executeUpdate();
                    shiftId = id;
                    staffCode = code;
                    staffName = name;
                    return true;
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1062) throw e;   // 1062 = ID already used, try another
                }
            }
            throw new SQLException("Could not create a unique shift ID.");
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Could not start shift: " + e.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // closes the shift in the database and clears the session
    public static void endShift() {
        String sql = "UPDATE shifts SET end_time = ? WHERE shift_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, shiftId);
            ps.executeUpdate();
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Could not end shift: " + e.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        shiftId = null;
        staffCode = null;
        staffName = null;
    }

    public static boolean isActive()    { return shiftId != null; }
    public static String getShiftId()   { return shiftId; }
    public static String getStaffCode() { return staffCode; }
    public static String getStaffName() { return staffName; }
}