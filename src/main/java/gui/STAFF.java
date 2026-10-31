/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author deLL
 */
public class STAFF extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(STAFF.class.getName());

    /**
     * Creates new form LOGIN
     */
    public STAFF() {
        initComponents();
        applyTheme();
        Theme.apply(this);
        Theme.maximizeCentered(this);
    }
    
    private void applyTheme() {
    Color bg = new Color(27, 20, 16);
    Color card = new Color(45, 29, 23);
    Color accent = new Color(255, 102, 0);
    Color text = Color.WHITE;
    Color sub = new Color(200, 190, 180);
    Color line = new Color(58, 44, 35);

    getContentPane().setBackground(bg);
    jPanel1.setBackground(card);
    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(line, 1));

    

    

    

    txtUsername.setBackground(bg);
    txtUsername.setForeground(text);
    txtUsername.setCaretColor(text);
    txtUsername.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(line, 1),
        javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
    ));

    

    btLogin.setBackground(accent);
    btLogin.setForeground(bg);
    btLogin.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

    
}
     private boolean isValidStaffCode(String code) {
        String sql = "SELECT 1 FROM staff WHERE staff_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();   // true if the code exists in the table
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
     private String[] findStaff(String code) {
        String sql = "SELECT staff_code, COALESCE(NULLIF(full_name, ''), staff_code) AS name "
                   + "FROM staff WHERE staff_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("staff_code"), rs.getString("name")};
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
      private String[] login(String code, char[] password) {
       
        String sql = "SELECT staff_code, COALESCE(NULLIF(full_name, ''), staff_code) AS name, "
                   + "password_hash, active, must_change FROM staff WHERE staff_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    if (hash == null) {
                        JOptionPane.showMessageDialog(this,
                            "This account has no password yet.\nPlease ask the admin to set one.",
                            "Login Failed", JOptionPane.WARNING_MESSAGE);
                        return null;
                    }
                    if (PasswordUtil.verify(password, hash)) {
                        if (rs.getInt("active") != 1) {
                            JOptionPane.showMessageDialog(this,
                                "This staff account is deactivated.\nPlease see the admin.",
                                "Login Failed", JOptionPane.WARNING_MESSAGE);
                            return null;
                        }
                        String staffCode = rs.getString("staff_code");
                        String staffName = rs.getString("name");
                        if (rs.getInt("must_change") == 1 && !chooseNewPassword(staffCode, password)) {
                            return null;   // they closed the box without choosing a password
                        }
                        return new String[]{staffCode, staffName};
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JOptionPane.showMessageDialog(this, "Invalid STAFF ID or password.",
            "Login Failed", JOptionPane.ERROR_MESSAGE);
        return null;
    
    }
      private boolean chooseNewPassword(String code, char[] oldPassword) {
        javax.swing.JPasswordField p1 = new javax.swing.JPasswordField(16);
        javax.swing.JPasswordField p2 = new javax.swing.JPasswordField(16);
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 0, 6));
        panel.add(new javax.swing.JLabel("Choose your own password (6 to 40 characters)"));
        panel.add(p1);
        panel.add(new javax.swing.JLabel("Type it again:"));
        panel.add(p2);

        while (true) {
            int ok = JOptionPane.showConfirmDialog(this, panel, "Choose Your Password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return false;

            char[] a = p1.getPassword();
            String problem = null;
            if (a.length < 6) problem = "The password must be at least 6 characters.";
            else if (a.length > 40) problem = "The password can be at most 40 characters.";
            else if (!java.util.Arrays.equals(a, p2.getPassword())) problem = "The two passwords do not match.";
            else if (java.util.Arrays.equals(a, oldPassword)) problem = "Choose a different password from the one you were given.";
            if (problem != null) {
                JOptionPane.showMessageDialog(this, problem);
                continue;
            }

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     "UPDATE staff SET password_hash = ?, must_change = 0 WHERE staff_code = ?")) {
                ps.setString(1, PasswordUtil.hash(a));
                ps.setString(2, code);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Could not save the password: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtUsername = new javax.swing.JTextField();
        btLogin = new javax.swing.JButton();
        btBack = new javax.swing.JButton();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(45, 29, 23));

        txtUsername.addActionListener(this::txtUsernameActionPerformed);

        btLogin.setBackground(new java.awt.Color(255, 102, 0));
        btLogin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btLogin.setText("LOGIN");
        btLogin.addActionListener(this::btLoginActionPerformed);

        btBack.setBackground(new java.awt.Color(255, 102, 0));
        btBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btBack.setText("BACK");
        btBack.addActionListener(this::btBackActionPerformed);

        jPasswordField1.addActionListener(this::jPasswordField1ActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PASSWORD");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("STAFF ID:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addComponent(btBack, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(127, 127, 127))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(143, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btBack, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(92, 92, 92))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(360, 360, 360)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(379, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLoginActionPerformed
                                            
        String userId = txtUsername.getText().trim();
        char[] password = jPasswordField1.getPassword();

        if (userId.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this,
                "Please enter your STAFF ID and password.",
                "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] staff = login(userId, password);
        if (staff == null) {
            jPasswordField1.setText("");
            jPasswordField1.requestFocus();
            return;
        }

        if (!Session.startShift(staff[0], staff[1])) {
            return;   // error message was already shown
        }
        new WOW().setVisible(true);
        this.dispose();
    

    }//GEN-LAST:event_btLoginActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void btBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBackActionPerformed
 new Options().setVisible(true);
    this.dispose();


        // TODO add your handling code here:
    }//GEN-LAST:event_btBackActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordField1ActionPerformed

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

        /* Create and display the form */
        new STAFF().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btBack;
    private javax.swing.JButton btLogin;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
