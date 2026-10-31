package gui;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
 
// Staff screen (opened from WOW): void an order from this shift. A manager must approve it.
public class VOIDORDER extends JDialog {
 
    private final JTable tblOrders = new JTable();
    private final JTextField txtReason = new JTextField(34);
 
    private String preselectId = null;
    private final String shiftId;   // the shift whose orders can be voided
 
    public VOIDORDER(java.awt.Frame owner) {
        this(owner, null, Session.getShiftId());
    }
 
    // opened from the receipt: this order is already selected
    public VOIDORDER(java.awt.Frame owner, String preselectId) {
        this(owner, preselectId, Session.getShiftId());
    }
 
    // opened from the shift report: the shift is given (it has already ended)
    public VOIDORDER(java.awt.Frame owner, String preselectId, String shiftId) {
        super(owner, "Void Order", true);
        this.shiftId = shiftId;
        this.preselectId = preselectId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(860, 580);
        setLocationRelativeTo(owner);
        buildUi();
        loadOrders();
        Theme.apply(this);
    }
 
    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
 
        JLabel title = new JLabel("VOID AN ORDER", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(255, 102, 0));
 
        JLabel help = new JLabel("<html>Pick the order from <b>your shift</b>, type the reason, then a manager "
                + "must approve it.<br>The items go back into stock.</html>");
        help.setFont(new Font("Segoe UI", Font.PLAIN, 13));
 
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(title, BorderLayout.NORTH);
        top.add(help, BorderLayout.CENTER);
 
        tblOrders.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(tblOrders);
 
        JLabel lblReason = new JLabel("REASON:");
        lblReason.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JPanel reasonRow = new JPanel(new BorderLayout(10, 0));
        reasonRow.add(lblReason, BorderLayout.WEST);
        reasonRow.add(txtReason, BorderLayout.CENTER);
 
        JButton btnVoid = new JButton("REQUEST MANAGER APPROVAL");
        btnVoid.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoid.setPreferredSize(new Dimension(260, 38));
        btnVoid.addActionListener(e -> requestVoid());
 
        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setBackground(new Color(27, 20, 16));    // dark = shown as the light, secondary button
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setPreferredSize(new Dimension(120, 38));
        btnCancel.addActionListener(e -> dispose());
 
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnVoid);
        buttons.add(btnCancel);
 
        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.add(reasonRow, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);
 
        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }
 
   
    private void loadOrders() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"RECEIPT NUMBER", "TIME", "ITEMS", "TOTAL"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        if (shiftId != null) {
            String sql = "SELECT o.order_id, o.order_date, o.total, "
                       + "(SELECT GROUP_CONCAT(CONCAT(i.qty, 'x ', i.item_name) ORDER BY i.item_row_id SEPARATOR ', ') "
                       + " FROM order_items i WHERE i.order_id = o.order_id) AS items "
                       + "FROM orders o WHERE o.shift_id = ? AND o.status = 'COMPLETED' "
                       + "ORDER BY o.order_date DESC";
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, shiftId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("order_id"),
                            rs.getTimestamp("order_date").toLocalDateTime().format(fmt),
                            rs.getString("items"),
                            "₱" + rs.getBigDecimal("total")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Could not load your orders: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        tblOrders.setModel(model);
        if (model.getRowCount() > 0) {
            tblOrders.getColumnModel().getColumn(2).setPreferredWidth(380);
        }
        if (preselectId != null) {
            for (int r = 0; r < model.getRowCount(); r++) {
                if (preselectId.equals(model.getValueAt(r, 0))) {
                    tblOrders.setRowSelectionInterval(r, r);
                    break;
                }
            }
        }
    }
 
    private void requestVoid() {
            
        int row = tblOrders.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Click the order you want to void first.");
            return;
        }
        String reason = txtReason.getText().trim();
        if (reason.length() < 3) {
            JOptionPane.showMessageDialog(this, "Please type the reason for voiding the order.");
            txtReason.requestFocus();
            return;
        }
        if (reason.length() > 200) reason = reason.substring(0, 200);
 
        String orderId = tblOrders.getValueAt(row, 0).toString();
        String manager = askManagerApproval(orderId);
        if (manager == null) return;
 
        executeVoid(orderId, reason, manager);
    }
 
    // asks a manager (admin) to type their ID and password; returns the admin ID, or null
    private String askManagerApproval(String orderId) {
        JTextField idField = new JTextField(16);
        JPasswordField pwField = new JPasswordField(16);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.add(new JLabel("MANAGER APPROVAL to void " + orderId));
        panel.add(new JLabel("Manager ID:"));
        panel.add(idField);
        panel.add(new JLabel("Manager password:"));
        panel.add(pwField);
 
        for (int attempt = 1; attempt <= 3; attempt++) {
            int ok = JOptionPane.showConfirmDialog(this, panel, "Manager Approval",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return null;
 
            String adminId = idField.getText().trim();
            String hash = adminId.isEmpty() ? null : loadAdminHash(adminId);
            if (hash != null && PasswordUtil.verify(pwField.getPassword(), hash)) {
                return adminId;
            }
            pwField.setText("");
            JOptionPane.showMessageDialog(this,
                "Invalid manager ID or password.\n"
              + "(A manager who has never logged in must log in once to set a password.)",
                "Not Approved", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(this, "Too many wrong attempts. The order was NOT voided.");
        return null;
    }
 
    private String loadAdminHash(String adminId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT password_hash FROM admin_account WHERE admin_id = ?")) {
            ps.setString(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
 
    // the staff member who owned the shift (works even after the shift has ended)
    private String voidedByCode(Connection con) throws SQLException {
        String code = Session.getStaffCode();
        if (code != null) return code;
        try (PreparedStatement ps = con.prepareStatement("SELECT staff_code FROM shifts WHERE shift_id = ?")) {
            ps.setString(1, shiftId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("staff_code");
            }
        }
        return null;
    }
 
    // voids the order and puts its items back in stock, all in one transaction
    private void executeVoid(String orderId, String reason, String manager) {
        
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String current;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT status FROM orders WHERE order_id = ? AND shift_id = ? FOR UPDATE")) {
                    ps.setString(1, orderId);
                    ps.setString(2, shiftId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Order " + orderId + " is not in your shift.");
                        current = rs.getString("status");
                    }
                }
                if ("VOIDED".equals(current)) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, orderId + " is already voided.");
                    loadOrders();
                    return;
                }
 
                try (PreparedStatement items = con.prepareStatement(
                         "SELECT item_name, qty FROM order_items WHERE order_id = ?");
                     PreparedStatement restock = con.prepareStatement(
                         "UPDATE inventory SET stock = stock + ? WHERE item_name = ?")) {
                    items.setString(1, orderId);
                    try (ResultSet rs = items.executeQuery()) {
                        while (rs.next()) {
                            restock.setInt(1, rs.getInt("qty"));
                            restock.setString(2, rs.getString("item_name"));
                            restock.executeUpdate();
                        }
                    }
                }
 
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE orders SET status = 'VOIDED', voided_at = NOW(), void_reason = ?, "
                      + "voided_by = ?, approved_by = ? WHERE order_id = ?")) {
                    ps.setString(1, reason);
                    ps.setString(2, voidedByCode(con));
                    ps.setString(3, manager);
                    ps.setString(4, orderId);
                    ps.executeUpdate();
                }
 
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not void the order: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        JOptionPane.showMessageDialog(this,
            orderId + " was voided (approved by " + manager + ").\nIts items are back in stock.");
        dispose();
    }
}