package gui;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
 
public class REGISTERSTAFF extends JFrame {
 
    private final JTextField txtCode = new JTextField(18);
    private final JTextField txtName = new JTextField(18);
    private final JTextField txtPhone = new JTextField(18);
    private final JPasswordField txtPass = new JPasswordField(18);
    private final JPasswordField txtPass2 = new JPasswordField(18);
    private final JTable tblStaff = new JTable();
 
    public REGISTERSTAFF() {
        setTitle("Register Staff");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(860, 820);
        setLocationRelativeTo(null);
        buildUi();
        txtCode.setEditable(false);
        txtCode.setText(nextCode());
        loadStaff();
        Theme.apply(this);
    }
 
    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
 
        JLabel title = new JLabel("REGISTER STAFF", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(255, 102, 0));
 
        // ---- form ----
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 12);
        gc.anchor = GridBagConstraints.WEST;
 
        addRow(form, gc, 0, "STAFF ID (AUTO)", txtCode);
        addRow(form, gc, 1, "FULL NAME", txtName);
        addRow(form, gc, 2, "PHONE NUMBER", txtPhone);
        addRow(form, gc, 3, "STARTING PASSWORD", txtPass);
        addRow(form, gc, 4, "CONFIRM PASSWORD", txtPass2);
 
        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setPreferredSize(new Dimension(160, 38));
        btnRegister.addActionListener(e -> registerStaff());
 
        gc.gridx = 1; gc.gridy = 5; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(14, 0, 6, 0);
        form.add(btnRegister, gc);
 
        // ---- registered staff list ----
        JLabel listTitle = makeLabel("REGISTERED STAFF");
        JScrollPane scroll = new JScrollPane(tblStaff);
 
        JPanel center = new JPanel(new BorderLayout(0, 10));
        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.add(form, BorderLayout.NORTH);
        top.add(listTitle, BorderLayout.SOUTH);
        center.add(top, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
 
        // ---- bottom buttons ----
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.add(makeSecondaryButton("RESET PASSWORD", 170, e -> resetPassword()));
        actions.add(makeSecondaryButton("EDIT PHONE", 130, e -> editPhone()));
        actions.add(makeSecondaryButton("DEACTIVATE / ACTIVATE", 210, e -> toggleActive()));
 
        JButton btnClose = makeSecondaryButton("CLOSE", 110, e -> dispose());
 
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(actions, BorderLayout.WEST);
        bottom.add(btnClose, BorderLayout.EAST);
 
        root.add(title, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }
 
    // a light "secondary" button (a dark background is turned into the light style by Theme)
    private JButton makeSecondaryButton(String text, int width, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setBackground(new Color(27, 20, 16));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(width, 36));
        b.addActionListener(action);
        return b;
    }
 
    // one label + one input box on a form row
    private void addRow(JPanel form, GridBagConstraints gc, int row, String label, javax.swing.JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(makeLabel(label), gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, gc);
    }
 
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }
 
    // makes a random, unused 5-character staff ID like K7M2Q (no look-alike characters)
    private String nextCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        String candidate = "";
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            }
            candidate = sb.toString();
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT 1 FROM staff WHERE staff_code = ?")) {
                ps.setString(1, candidate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return candidate;
                }
            } catch (SQLException e) {
                return candidate;
            }
        }
        return candidate;
    }
 
    // returns a problem message, or null when the password is fine
    private String checkPassword(char[] p1, char[] p2) {
        if (p1.length < 6) return "The password must be at least 6 characters.";
        if (p1.length > 40) return "The password can be at most 40 characters.";
        if (!java.util.Arrays.equals(p1, p2)) return "The two passwords do not match.";
        return null;
    }
 
    private void registerStaff() {
        String code = txtCode.getText().trim().toUpperCase();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        char[] pass = txtPass.getPassword();
        char[] pass2 = txtPass2.getPassword();
 
        if (code.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in the full name.");
            return;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the staff's phone number.");
            return;
        }
        if (!phone.matches("\\+?[0-9 \\-]{7,20}") || phone.replaceAll("[^0-9]", "").length() < 7) {
            JOptionPane.showMessageDialog(this,
                "Enter a valid phone number (numbers only, you may use + - and spaces).");
            return;
        }
        if (!code.matches("[A-Z0-9_-]+")) {
            JOptionPane.showMessageDialog(this,
                "The staff code can only use letters, numbers, - and _ (no spaces).");
            return;
        }
        String passProblem = checkPassword(pass, pass2);
        if (passProblem != null) {
            JOptionPane.showMessageDialog(this, passProblem);
            return;
        }
 
        try (Connection con = DBConnection.getConnection()) {
 
            try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM staff WHERE staff_code = ?")) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                            "The staff code " + code + " is already registered. Use a different code.",
                            "Already Registered", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
 
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO staff (staff_code, full_name, phone, password_hash, must_change) VALUES (?, ?, ?, ?, 1)")) {
                ps.setString(1, code);
                ps.setString(2, name);
                ps.setString(3, phone);
                ps.setString(4, PasswordUtil.hash(pass));
                ps.executeUpdate();
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not register staff: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        JOptionPane.showMessageDialog(this,
            name + " is registered.\nStaff ID: " + code + "\nThey log in with this ID and the starting password,\nthen choose their own password.");
        txtName.setText("");
        txtPhone.setText("");
        txtPass.setText("");
        txtPass2.setText("");
        txtCode.setText(nextCode());
        loadStaff();
        selectStaff(code);
    }
 
    // returns the staff ID of the selected row, or null (and tells the admin to click one)
    private String selectedCode() {
        int row = tblStaff.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Click a staff in the list first.");
            return null;
        }
        return tblStaff.getValueAt(row, 0).toString();
    }
 
    // makes a random temporary password like K7M2QX9P (no look-alike characters)
    private String tempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
 
    // gives the selected staff a new random password and shows it once
    private void resetPassword() {
        String code = selectedCode();
        if (code == null) return;
 
        int ok = JOptionPane.showConfirmDialog(this,
            "Make a new temporary password for " + code + "?\nTheir old password will stop working.",
            "Reset Password", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
 
        String temp = tempPassword();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE staff SET password_hash = ?, must_change = 1 WHERE staff_code = ?")) {
            ps.setString(1, PasswordUtil.hash(temp.toCharArray()));
            ps.setString(2, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not reset the password: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        JTextField shown = new JTextField(temp);
        shown.setEditable(false);
        shown.setFont(new Font("Consolas", Font.BOLD, 20));
        shown.setHorizontalAlignment(JTextField.CENTER);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.add(new JLabel("New temporary password for " + code + ":"));
        panel.add(shown);
        panel.add(new JLabel("Tell the staff now. They will choose their own password when they log in."));
        JOptionPane.showMessageDialog(this, panel, "Password Reset", JOptionPane.INFORMATION_MESSAGE);
        loadStaff();
        selectStaff(code);
    }
 
    // changes the phone number of the selected staff
    private void editPhone() {
        String code = selectedCode();
        if (code == null) return;
 
        String current = tblStaff.getValueAt(tblStaff.getSelectedRow(), 2).toString();
        Object input = JOptionPane.showInputDialog(this, "New phone number for " + code + ":",
                "Edit Phone", JOptionPane.PLAIN_MESSAGE, null, null, current);
        if (input == null) return;
 
        String phone = input.toString().trim();
        if (!phone.matches("\\+?[0-9 \\-]{7,20}") || phone.replaceAll("[^0-9]", "").length() < 7) {
            JOptionPane.showMessageDialog(this,
                "Enter a valid phone number (numbers only, you may use + - and spaces).");
            return;
        }
 
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE staff SET phone = ? WHERE staff_code = ?")) {
            ps.setString(1, phone);
            ps.setString(2, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not update the phone number: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadStaff();
        selectStaff(code);
    }
 
    // turns the selected staff off (they can no longer log in) or back on
    private void toggleActive() {
        String code = selectedCode();
        if (code == null) return;
 
        boolean isActive = "Active".equals(tblStaff.getValueAt(tblStaff.getSelectedRow(), 4).toString());
        String question = isActive
            ? "Deactivate " + code + "?\nThey will not be able to log in. Their past orders stay in the records."
            : "Activate " + code + " again?\nThey will be able to log in.";
        int ok = JOptionPane.showConfirmDialog(this, question,
                isActive ? "Deactivate Staff" : "Activate Staff", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
 
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE staff SET active = ? WHERE staff_code = ?")) {
            ps.setInt(1, isActive ? 0 : 1);
            ps.setString(2, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not change the status: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadStaff();
        selectStaff(code);
    }
 
    // highlights the staff row and scrolls the list to it
    private void selectStaff(String code) {
        for (int i = 0; i < tblStaff.getRowCount(); i++) {
            if (code.equals(tblStaff.getValueAt(i, 0))) {
                tblStaff.setRowSelectionInterval(i, i);
                tblStaff.scrollRectToVisible(tblStaff.getCellRect(i, 0, true));
                return;
            }
        }
    }
 
    private void loadStaff() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"STAFF ID", "FULL NAME", "PHONE", "PASSWORD", "STATUS"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        String sql = "SELECT staff_code, COALESCE(full_name, '') AS full_name, "
                   + "COALESCE(phone, '') AS phone, (password_hash IS NOT NULL) AS has_pass, "
                   + "active FROM staff ORDER BY staff_code";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("staff_code"),
                        rs.getString("full_name"), rs.getString("phone"),
                        rs.getBoolean("has_pass") ? "Set" : "NOT SET",
                        rs.getInt("active") == 1 ? "Active" : "Deactivated"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load staff list: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        tblStaff.setModel(model);
    }
 
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new REGISTERSTAFF().setVisible(true));
    }
}