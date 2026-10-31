/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
   import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
/**
 *
 * @author deLL
 */
public class RECORDS extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RECORDS.class.getName());
 
   private static final String ALL_STAFF = "ALL STAFF";
    private static final int COL_STATUS = 8;
 
    private final JComboBox<String> cmbStaff = new JComboBox<>();
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"ALL ORDERS", "COMPLETED", "VOIDED"});
    private final JTextField txtSearch = new JTextField(22);
    private final JLabel lblSummary = new JLabel(" ");
    /**
     * Creates new form RECORDS
     */
    public RECORDS() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        buildFilterBar();
        loadStaffList();
        

        // listeners are added after the dropdown is filled, so they don't fire early
         cmbStaff.addActionListener(e -> loadRecords());
        cmbStatus.addActionListener(e -> loadRecords());
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { loadRecords(); }
            public void removeUpdate(DocumentEvent e)  { loadRecords(); }
            public void changedUpdate(DocumentEvent e) { loadRecords(); }
        });
 
        loadRecords();
        setSize(1000, 620);
        setLocationRelativeTo(null);
        Theme.apply(this);
        Theme.maximizeCentered(this);
    }

    // Rearranges your existing title, table and EXIT button, and adds the filter bar.
    // (This is done in code, so the Design tab will still show the old layout.)
   private void buildFilterBar() {
        Color black = new Color(0, 0, 0);
        Color accent = new Color(255, 102, 0);
        Color dark = new Color(51, 51, 51);
 
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(black);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
 
        jLabel2.setHorizontalAlignment(JLabel.CENTER);
 
        JLabel lblStaff = new JLabel("STAFF:");
        lblStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStaff.setForeground(Color.WHITE);
 
        JLabel lblSearch = new JLabel("SEARCH:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(Color.WHITE);
 
        cmbStaff.setBackground(dark);
        cmbStaff.setForeground(Color.WHITE);
        cmbStaff.setPreferredSize(new Dimension(220, 30));
 
        JLabel lblShow = new JLabel("SHOW:");
        lblShow.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblShow.setForeground(Color.WHITE);
        cmbStatus.setBackground(dark);
        cmbStatus.setForeground(Color.WHITE);
        cmbStatus.setPreferredSize(new Dimension(150, 30));
 
        txtSearch.setBackground(dark);
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setPreferredSize(new Dimension(260, 30));
        txtSearch.setToolTipText("Search by staff code, shift ID, receipt number, product, or date (e.g. Oct 10)");
 
        JButton btnClear = new JButton("CLEAR");
        btnClear.setBackground(accent);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbStaff.setSelectedIndex(0);
            cmbStatus.setSelectedIndex(0);
            loadRecords();
        });
 
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setBackground(black);
        filters.add(lblStaff);
        filters.add(cmbStaff);
        filters.add(lblShow);
        filters.add(cmbStatus);
        filters.add(lblSearch);
        filters.add(txtSearch);
        filters.add(btnClear);
 
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(black);
        top.add(jLabel2, BorderLayout.NORTH);
        top.add(filters, BorderLayout.CENTER);
 
        jScrollPane2.getViewport().setBackground(dark);
        jTable1.setRowHeight(26);
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                boolean voided = "VOIDED".equals(String.valueOf(t.getValueAt(row, COL_STATUS)));
                if (!sel) c.setForeground(voided ? new Color(185, 55, 55) : Theme.TEXT);
                return c;
            }
        });
 
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSummary.setForeground(accent);
 
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(black);
        bottom.add(lblSummary, BorderLayout.WEST);
        bottom.add(jButton1, BorderLayout.EAST);
 
        root.add(top, BorderLayout.NORTH);
        root.add(jScrollPane2, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }
 
    // fills the staff dropdown: "CODE - Name"
    private void loadStaffList() {
        cmbStaff.addItem(ALL_STAFF);
        String sql = "SELECT staff_code, COALESCE(NULLIF(full_name, ''), staff_code) AS name "
                   + "FROM staff ORDER BY staff_code";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbStaff.addItem(rs.getString("staff_code") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load staff list: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecords() {
        String staff = (String) cmbStaff.getSelectedItem();
        boolean allStaff = staff == null || ALL_STAFF.equals(staff);
        String staffCode = allStaff ? null : staff.split(" - ", 2)[0];
        String status = (String) cmbStatus.getSelectedItem();
        boolean anyStatus = status == null || status.startsWith("ALL");
        String kw = txtSearch.getText().trim();
 
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"STAFF ID", "NAME", "RECEIPT NUMBER", "ORDER TYPE", "PRODUCT", "QUANTITY",
                             "PRICE", "DATE", "STATUS", "VOIDED AT", "VOID REASON"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
 
        StringBuilder sql = new StringBuilder(
              "SELECT s.staff_code, s.staff_name, o.order_id, o.order_type, i.item_name, i.qty, i.price, "
            + "o.order_date, o.status, o.voided_at, o.void_reason "
            + "FROM orders o "
            + "JOIN shifts s ON s.shift_id = o.shift_id "
            + "JOIN order_items i ON i.order_id = o.order_id "
            + "WHERE 1=1");
 
        ArrayList<String> params = new ArrayList<>();
        if (!allStaff) {
            sql.append(" AND s.staff_code = ?");
            params.add(staffCode);
        }
        if (!anyStatus) {
            sql.append(" AND o.status = ?");
            params.add(status);
        }
        if (!kw.isEmpty()) {
            sql.append(" AND (s.staff_code LIKE ? OR s.staff_name LIKE ? OR s.shift_id LIKE ?"
                     + " OR o.order_id LIKE ? OR i.item_name LIKE ? OR o.void_reason LIKE ? OR o.order_type LIKE ?"
                     + " OR DATE_FORMAT(o.order_date, '%b %d, %Y') LIKE ?)");
            String like = "%" + kw + "%";
            for (int i = 0; i < 8; i++) params.add(like);
        }
        sql.append(" ORDER BY o.order_date DESC, o.order_id, i.item_row_id ASC");
 
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        HashSet<String> receipts = new HashSet<>();
        HashSet<String> voided = new HashSet<>();
        int items = 0;
 
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
 
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("order_id");
                    receipts.add(id);
                    if ("VOIDED".equals(rs.getString("status"))) voided.add(id);
                    items++;
                    model.addRow(new Object[]{
                        rs.getString("staff_code"),
                        rs.getString("staff_name"),
                        id,
                        rs.getString("order_type"),
                        rs.getString("item_name"),
                        rs.getInt("qty"),
                        "₱" + rs.getBigDecimal("price"),
                        rs.getTimestamp("order_date").toLocalDateTime().format(fmt),
                        rs.getString("status"),
                        rs.getTimestamp("voided_at") == null ? ""
                            : rs.getTimestamp("voided_at").toLocalDateTime().format(fmt),
                        rs.getString("void_reason") == null ? "" : rs.getString("void_reason")
                    });
                }
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load records: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
 
        jTable1.setModel(model);
        lblSummary.setText("Receipts: " + receipts.size() + "     Voided: " + voided.size()
                         + "     Item lines: " + items);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jTable1.setBackground(new java.awt.Color(51, 51, 51));
        jTable1.setForeground(new java.awt.Color(255, 102, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RECEIPT NUMBER", "PRODUCT", "QUANTITY", "TOTAL", "DATE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 102, 0));
        jLabel2.setText("RECORDS");

        jButton1.setBackground(new java.awt.Color(255, 102, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("EXIT");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(310, 310, 310))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 739, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new RECORDS().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
