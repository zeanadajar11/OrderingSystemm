/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CHECKOUT extends javax.swing.JFrame {
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CHECKOUT.class.getName());
    

    /**
     * Creates new form CHECKOUT
     */
    public CHECKOUT() {
     initComponents();
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); 
    applyTheme();
    Theme.apply(this);
    Theme.maximizeCentered(this);
    buildReceiptLayout(); 
    }
   private String lastOrderId = null;
    private final javax.swing.JButton btnVoid = new javax.swing.JButton("VOID ORDER");
    private javax.swing.JPanel buttons;
    private void buildReceiptLayout() {
        Color orange = new Color(255, 90, 10);
        Color cream  = new Color(230, 226, 208);
        Color paper  = new Color(250, 248, 242);
        Color ink    = new Color(34, 34, 34);
        Color line   = new Color(205, 199, 180);
 
        Jtable1.setText("RECEIPT");
        Jtable1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jtable1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        Jtable1.setForeground(orange);
 
        txtReceipt.setEditable(false);
        txtReceipt.setFocusable(false);
        txtReceipt.setBackground(paper);
        txtReceipt.setForeground(ink);
        txtReceipt.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 14));
        txtReceipt.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 24, 20, 24));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(line, 1));
        jScrollPane1.getViewport().setBackground(paper);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(440, 430));
 
        btnVoid.setBackground(new Color(200, 40, 40));   // red = a risky action
        btnVoid.setForeground(Color.WHITE);
        btnVoid.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        btnVoid.setFocusPainted(false);
        for (java.awt.event.ActionListener l : btnVoid.getActionListeners()) btnVoid.removeActionListener(l);
        btnVoid.addActionListener(e -> voidThisOrder());
 
        btnClose.setBackground(orange);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        btnClose.setFocusPainted(false);
 
        buttons = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 14, 0));
        buttons.setOpaque(false);
        buttons.add(btnVoid);
        buttons.add(btnClose);
        buttons.setPreferredSize(new java.awt.Dimension(440, 48));
 
        jPanel1.removeAll();
        jPanel1.setLayout(new java.awt.BorderLayout(0, 14));
        jPanel1.setBackground(cream);
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 30, 24, 30));
        jPanel1.add(Jtable1, java.awt.BorderLayout.NORTH);
        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        jPanel1.add(buttons, java.awt.BorderLayout.SOUTH);
        jPanel1.revalidate();
        jPanel1.repaint();
        getContentPane().revalidate();
    }
 
    // VOID ORDER on the receipt: reason box, then manager approval, then the order is voided
    private void voidThisOrder() {
        if (lastOrderId == null) return;
 
        String reason = null;
        while (reason == null) {
            String typed = javax.swing.JOptionPane.showInputDialog(this,
                "Reason for voiding " + lastOrderId + ":", "Void Order",
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (typed == null) return;                       // cancelled
            typed = typed.trim();
            if (typed.length() < 3) {
                javax.swing.JOptionPane.showMessageDialog(this, "Please type the reason for voiding the order.");
            } else {
                reason = typed.length() > 200 ? typed.substring(0, 200) : typed;
            }
        }
 
        String manager = askManagerApproval();
        if (manager == null) return;
 
        if (executeVoid(reason, manager)) {
            txtReceipt.setText(String.join("\n",
                "          *** ORDER VOIDED ***",
                "",
                "Receipt #: " + lastOrderId,
                "",
                "This order was voided and its items",
                "were returned to stock.",
                "",
                "Enter the order again from the menu."));
            txtReceipt.setCaretPosition(0);
            // nothing left to void: only Close stays
            buttons.removeAll();
            buttons.setLayout(new java.awt.GridLayout(1, 1));
            buttons.add(btnClose);
            buttons.revalidate();
            buttons.repaint();
        }
    }
 
    // asks a manager (admin) for ID and password; returns the admin ID, or null
    private String askManagerApproval() {
        javax.swing.JTextField idField = new javax.swing.JTextField(16);
        javax.swing.JPasswordField pwField = new javax.swing.JPasswordField(16);
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 0, 6));
        panel.add(new javax.swing.JLabel("MANAGER APPROVAL to void " + lastOrderId));
        panel.add(new javax.swing.JLabel("Manager ID:"));
        panel.add(idField);
        panel.add(new javax.swing.JLabel("Manager password:"));
        panel.add(pwField);
 
        for (int attempt = 1; attempt <= 3; attempt++) {
            int ok = javax.swing.JOptionPane.showConfirmDialog(this, panel, "Manager Approval",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE);
            if (ok != javax.swing.JOptionPane.OK_OPTION) return null;
 
            String adminId = idField.getText().trim();
            String hash = adminId.isEmpty() ? null : loadAdminHash(adminId);
            if (hash != null && PasswordUtil.verify(pwField.getPassword(), hash)) {
                return adminId;
            }
            pwField.setText("");
            javax.swing.JOptionPane.showMessageDialog(this, "Invalid manager ID or password.",
                "Not Approved", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        javax.swing.JOptionPane.showMessageDialog(this, "Too many wrong attempts. The order was NOT voided.");
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
            javax.swing.JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
 
    // marks the order VOIDED and puts its items back in stock, all in one transaction
    private boolean executeVoid(String reason, String manager) {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String current;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT status FROM orders WHERE order_id = ? FOR UPDATE")) {
                    ps.setString(1, lastOrderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Order " + lastOrderId + " was not found.");
                        current = rs.getString("status");
                    }
                }
                if ("VOIDED".equals(current)) {
                    con.rollback();
                    javax.swing.JOptionPane.showMessageDialog(this, lastOrderId + " is already voided.");
                    return false;
                }
 
                try (PreparedStatement items = con.prepareStatement(
                         "SELECT item_name, qty FROM order_items WHERE order_id = ?");
                     PreparedStatement restock = con.prepareStatement(
                         "UPDATE inventory SET stock = stock + ? WHERE item_name = ?")) {
                    items.setString(1, lastOrderId);
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
                    ps.setString(2, Session.getStaffCode());
                    ps.setString(3, manager);
                    ps.setString(4, lastOrderId);
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Could not void the order: " + e.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
     private void applyTheme() {
    Color bg = new Color(27, 20, 16);
    Color card = new Color(45, 29, 23);
    Color accent = new Color(255, 102, 0);
    Color text = Color.WHITE;
    Color line = new Color(58, 44, 35);
 
    getContentPane().setBackground(bg);
    jPanel1.setBackground(card);
    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(line, 1));
 
    
 
    Jtable1.setForeground(accent);
    Jtable1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
 
    txtReceipt.setBackground(bg);
    txtReceipt.setForeground(text);
    txtReceipt.setCaretColor(text);
    txtReceipt.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
    txtReceipt.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
 
    jScrollPane1.getViewport().setBackground(bg);
    jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(line, 1));
 
    btnClose.setBackground(accent);
    btnClose.setForeground(bg);
    btnClose.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
 
    }
 
public boolean loadReceipt(DefaultTableModel orderModel, String total, double cash, double change, String orderType) {
    LocalDateTime now = LocalDateTime.now();
    String orderId = saveOrder(now, orderModel, total, orderType);
    if (orderId == null) {
        txtReceipt.setText("Transaction could not be saved.\nPlease try again.");
        return false;
    }
    lastOrderId = orderId;
    btnVoid.setEnabled(true);
 
    final int W = 40;
    String bar = "=".repeat(W);
    String dash = "-".repeat(W);
    String cashier = Session.getStaffName() == null ? "" : Session.getStaffName();
    String when = now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a"));
 
    java.util.List<String> r = new java.util.ArrayList<>();
    r.add(center("HOTDOG NI BESH", W));
    r.add(center("OFFICIAL RECEIPT", W));
    r.add(bar);
    r.add(field("Receipt #", orderId, W));
    r.add(field("Date", when, W));
    r.add(field("Order type", orderType, W));
    if (!cashier.isEmpty()) r.add(field("Cashier", cashier, W));
    r.add(bar);
    r.add(String.format("%-20s%6s%14s", "ITEM", "QTY", "AMOUNT"));
    r.add(dash);
    for (int i = 0; i < orderModel.getRowCount(); i++) {
        String item = orderModel.getValueAt(i, 0).toString();
        if (item.length() > 20) item = item.substring(0, 20);
        String qty = orderModel.getValueAt(i, 1).toString();
        String amount = String.format("%,.2f", toMoney(orderModel.getValueAt(i, 2).toString()));
        r.add(String.format("%-20s%6s%14s", item, qty, amount));
    }
    r.add(dash);
    r.add(field("TOTAL", "P" + String.format("%,.2f", toMoney(total)), W));
    r.add(field("CASH", String.format("P%,.2f", cash), W));
    r.add(field("CHANGE", String.format("P%,.2f", change), W));
    r.add(bar);
    r.add(center("Thank you for your order!", W));
    r.add(center("Please come again.", W));
 
    txtReceipt.setText(String.join("\n", r));  
    txtReceipt.setCaretPosition(0);              
    return true;
}
 
   
    private String field(String label, String value, int width) {
        int gap = Math.max(1, width - label.length() - value.length());
        return label + " ".repeat(gap) + value;
    }
 
    private String center(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }
 
   private java.math.BigDecimal toMoney(String s) {
        return new java.math.BigDecimal(s.replaceAll("[^0-9.]", ""));
    }
   
 
   
    private String saveOrder(LocalDateTime when, DefaultTableModel orderModel, String total, String orderType) {
 
        String shiftId = Session.getShiftId();
        if (shiftId == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "No active shift. Please log in again.",
                "Shift Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
 
        String orderSql = "INSERT INTO orders (order_id, order_date, total, shift_id, order_type) VALUES (?, ?, ?, ?, ?)";
        String itemSql  = "INSERT INTO order_items (order_id, item_name, qty, price) VALUES (?, ?, ?, ?)";
 
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);   
 
            try {
                String orderId = null;
                for (int attempt = 0; attempt < 5 && orderId == null; attempt++) {
                    String candidate = newOrderId();
                    try (PreparedStatement orderPs = con.prepareStatement(orderSql)) {
                        orderPs.setString(1, candidate);
                        orderPs.setTimestamp(2, Timestamp.valueOf(when));
                        orderPs.setBigDecimal(3, toMoney(total));
                        orderPs.setString(4, shiftId);
                        orderPs.setString(5, orderType);
                        orderPs.executeUpdate();
                        orderId = candidate;
                    } catch (SQLException e) {
                        if (e.getErrorCode() != 1062) throw e;   
                    }
                }
                if (orderId == null) {
                    throw new SQLException("Could not generate a unique receipt number.");
                }
 
                try (PreparedStatement itemPs = con.prepareStatement(itemSql)) {
                    for (int i = 0; i < orderModel.getRowCount(); i++) {
                        itemPs.setString(1, orderId);
                        itemPs.setString(2, orderModel.getValueAt(i, 0).toString());
                        itemPs.setInt(3, Integer.parseInt(orderModel.getValueAt(i, 1).toString().trim()));
                        itemPs.setBigDecimal(4, toMoney(orderModel.getValueAt(i, 2).toString()));
                        itemPs.addBatch();
                    }
                   itemPs.executeBatch();
                }
 
                
                for (int i = 0; i < orderModel.getRowCount(); i++) {
                    StockManager.deduct(con,
                        orderModel.getValueAt(i, 0).toString(),
                        Integer.parseInt(orderModel.getValueAt(i, 1).toString().trim()));
                }
 
                con.commit();
                return orderId;
 
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
 
        } catch (SQLException | RuntimeException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Could not save the transaction: " + e.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
 
    
    private String newOrderId() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder("OR-");
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    
    
    
    
    
    

    
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Jtable1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtReceipt = new javax.swing.JTextArea();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Jtable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Jtable1.setText("RECEIPT");

        txtReceipt.setColumns(20);
        txtReceipt.setRows(5);
        jScrollPane1.setViewportView(txtReceipt);

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(this::btnCloseActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Jtable1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(225, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(Jtable1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(169, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        
        this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_btnCloseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

            
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jtable1;
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtReceipt;
    // End of variables declaration//GEN-END:variables
}
