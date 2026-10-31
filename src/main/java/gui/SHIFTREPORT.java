/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author deLL
 */
public class SHIFTREPORT extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SHIFTREPORT.class.getName());

    /**
     * Creates new form SHIFTREPORT
     */
     private String shiftId;
    private final javax.swing.JButton btnVoid = new javax.swing.JButton("VOID ORDER");
    
   public SHIFTREPORT(String shiftId) {
        initComponents();
    setSize(760, 560);
    setLocationRelativeTo(null);
    applyTheme();
    loadReport(shiftId);
    Theme.apply(this);
    Theme.maximizeCentered(this);
    }
   private void buildLayout() {
        btnVoid.setBackground(new java.awt.Color(27, 20, 16));   // dark = shown as the light, secondary button
        btnVoid.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnVoid.setFocusPainted(false);
        btnVoid.addActionListener(e -> {
            new VOIDORDER(this, null, shiftId).setVisible(true);
            loadReport(shiftId);          // refresh: voided orders drop out of the report
        });
 
        javax.swing.JPanel header = new javax.swing.JPanel();
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.Y_AXIS));
        lblTitle.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblStaff.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblPeriod.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        header.add(lblTitle);
        header.add(javax.swing.Box.createVerticalStrut(8));
        header.add(lblStaff);
        header.add(lblPeriod);
 
        javax.swing.JPanel buttons = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        btnVoid.setPreferredSize(new java.awt.Dimension(150, 38));
        btnClose.setPreferredSize(new java.awt.Dimension(120, 38));
        buttons.add(btnVoid);
        buttons.add(btnClose);
 
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.BorderLayout(20, 0));
        bottom.add(lblSummary, java.awt.BorderLayout.CENTER);
        bottom.add(buttons, java.awt.BorderLayout.EAST);
 
        javax.swing.JPanel root = new javax.swing.JPanel(new java.awt.BorderLayout(0, 14));
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 40, 24, 40));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(700, 380));
        root.add(header, java.awt.BorderLayout.NORTH);
        root.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        root.add(bottom, java.awt.BorderLayout.SOUTH);
        setContentPane(root);
    }
    
    
    private void applyTheme() {
    java.awt.Color bg = new java.awt.Color(27, 20, 16);
    java.awt.Color card = new java.awt.Color(45, 29, 23);
    java.awt.Color accent = new java.awt.Color(255, 102, 0);
    java.awt.Color line = new java.awt.Color(58, 44, 35);

    getContentPane().setBackground(bg);

    lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
    lblTitle.setForeground(accent);
    lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    lblStaff.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
    lblStaff.setForeground(java.awt.Color.WHITE);

    lblPeriod.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
    lblPeriod.setForeground(new java.awt.Color(217, 201, 190));

    lblSummary.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
    lblSummary.setForeground(accent);

    tblReport.setBackground(card);
    tblReport.setForeground(accent);
    tblReport.setRowHeight(24);
    tblReport.setGridColor(line);
    tblReport.getTableHeader().setBackground(line);
    tblReport.getTableHeader().setForeground(java.awt.Color.WHITE);
    jScrollPane1.getViewport().setBackground(card);
    jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(line));

    btnClose.setBackground(accent);
    btnClose.setForeground(bg);
    btnClose.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
}
    
    
    private void loadReport(String shiftId) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"RECEIPT NUMBER", "TIME", "ITEMS", "TOTAL"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        String shiftSql = "SELECT staff_code, staff_name, start_time, end_time FROM shifts WHERE shift_id = ?";
        String ordersSql = "SELECT o.order_id, o.order_date, o.total, "
                + "(SELECT COALESCE(SUM(i.qty), 0) FROM order_items i WHERE i.order_id = o.order_id) AS items "
                + "FROM orders o WHERE o.shift_id = ? AND o.status = 'COMPLETED' ORDER BY o.order_date ASC";

        BigDecimal sales = BigDecimal.ZERO;
        int count = 0;

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                ps.setString(1, shiftId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                       lblStaff.setText("Staff: " + rs.getString("staff_name") + "  (" + rs.getString("staff_code") + ")");
                        String start = rs.getTimestamp("start_time").toLocalDateTime().format(fmt);
                        java.sql.Timestamp endTs = rs.getTimestamp("end_time");
                        String end = (endTs == null) ? "ongoing" : endTs.toLocalDateTime().format(fmt);
                        lblPeriod.setText("Duty: " + start + "  to  " + end);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(ordersSql)) {
                ps.setString(1, shiftId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        BigDecimal total = rs.getBigDecimal("total");
                        sales = sales.add(total);
                        count++;
                        model.addRow(new Object[]{
                            rs.getString("order_id"),
                            rs.getTimestamp("order_date").toLocalDateTime().format(fmt),
                            rs.getInt("items"),
                            "₱" + total
                        });
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Could not load report: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        tblReport.setModel(model);
        lblSummary.setText("Transactions: " + count + "     Total Sales: ₱" + sales);
    
}       

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblStaff = new javax.swing.JLabel();
        lblPeriod = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        lblSummary = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setText("END OF DUTY REPORT");

        lblStaff.setText("Staff:");

        lblPeriod.setText("Duty:");

        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblReport);
        if (tblReport.getColumnModel().getColumnCount() > 0) {
            tblReport.getColumnModel().getColumn(0).setResizable(false);
            tblReport.getColumnModel().getColumn(1).setResizable(false);
            tblReport.getColumnModel().getColumn(2).setResizable(false);
            tblReport.getColumnModel().getColumn(3).setResizable(false);
        }

        lblSummary.setText("Transactions: 0");

        btnClose.setText("CLOSE");
        btnClose.addActionListener(this::btnCloseActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(lblSummary, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblPeriod, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStaff, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSummary, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
new Options().setVisible(true);
        
        
        dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCloseActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPeriod;
    private javax.swing.JLabel lblStaff;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblReport;
    // End of variables declaration//GEN-END:variables
}
