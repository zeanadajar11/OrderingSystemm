/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;/**
 *
 * @author deLL
 */
public class WOW extends javax.swing.JFrame {
    
      private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(WOW.class.getName());

    private final javax.swing.table.DefaultTableModel orderModel =
        new javax.swing.table.DefaultTableModel(
            new Object[][]{}, new String[]{"Item", "Quantity", "Price"}
        );

    /**
     * Creates new form WOW
     */
    public WOW() {
     initComponents();
    setLocationRelativeTo(null);
applyTheme();
  refreshOrderDisplay();
  Theme.apply(this);
buildMenu();
Theme.maximizeCentered(this);
addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowActivated(java.awt.event.WindowEvent e) {
        refreshMenu();   // reloads the stock numbers from the database
    }
});
}
        
    
    private void applyTheme() {
        Color bg = new Color(27, 20, 16);
        Color card = new Color(45, 29, 23);
        Color accent = new Color(255, 102, 0);
        Color text = Color.WHITE;

        getContentPane().setBackground(bg);
        jPanel4.setBackground(bg);
        jPanel6.setBackground(bg);
        jPanel8.setBackground(card);
        jPanel10.setBackground(bg);

        lblOrderSummary.setForeground(text);
        lblTotal.setForeground(text);

        txtTotal.setBackground(card);
        txtTotal.setForeground(accent);
        txtTotal.setCaretColor(accent);

        
        btnDeleteItem.setBackground(card);
        btnDeleteItem.setForeground(text);
        btnCheckout.setBackground(accent);
        btnCheckout.setForeground(bg);
        btnEndDuty.setBackground(card);
btnEndDuty.setForeground(text);

        styleSpinner(spnBurger, card, text);
        styleSpinner(spnFries, card, text);
        styleSpinner(spnChicken, card, text);
        styleSpinner(spnSoftdrinks, card, text);
    }

    private void styleSpinner(javax.swing.JSpinner spinner, Color bg, Color fg) {
        spinner.setBackground(bg);
        javax.swing.JComponent editor = spinner.getEditor();
        if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
            javax.swing.JFormattedTextField textField =
                ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(bg);
            textField.setForeground(fg);
            textField.setCaretColor(fg);
        }
    }

    private void refreshOrderDisplay() {
        pnlOrderItems.removeAll();

        for (int i = 0; i < orderModel.getRowCount(); i++) {
            String item = orderModel.getValueAt(i, 0).toString();
            String qty = orderModel.getValueAt(i, 1).toString();
            String price = orderModel.getValueAt(i, 2).toString();

            pnlOrderItems.add(createOrderRow(item, qty, price));
            pnlOrderItems.add(javax.swing.Box.createVerticalStrut(8));
            
        }

        pnlOrderItems.revalidate();
        pnlOrderItems.repaint();
        refreshMenu();
        
    }

    private javax.swing.JPanel createOrderRow(String name, String qty, String price) {
        int q = Integer.parseInt(qty.trim());
        double lineTotal = Double.parseDouble(price);
        double unit = (q == 0) ? 0 : lineTotal / q;

        javax.swing.JPanel row = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        row.setBackground(Theme.PAPER);
        row.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(Theme.LINE),
            javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        row.setPreferredSize(new java.awt.Dimension(260, 64));
        row.setMinimumSize(new java.awt.Dimension(100, 64));
        row.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 64));
        row.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        javax.swing.JLabel nameLbl = new javax.swing.JLabel(name);
        nameLbl.setForeground(Theme.TEXT);
        nameLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));

        javax.swing.JLabel qtyLbl = new javax.swing.JLabel(q + " x \u20B1" + String.format("%.2f", unit));
        qtyLbl.setForeground(Theme.MUTED);
        qtyLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        javax.swing.JPanel leftSide = new javax.swing.JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        leftSide.setOpaque(false);
        leftSide.add(nameLbl);
        leftSide.add(qtyLbl);

        javax.swing.JLabel priceLbl = new javax.swing.JLabel("\u20B1" + String.format("%.2f", lineTotal));
        priceLbl.setForeground(Theme.ORANGE);
        priceLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));

        row.add(leftSide, java.awt.BorderLayout.WEST);
        row.add(priceLbl, java.awt.BorderLayout.EAST);
        return row;
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
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblMenu = new javax.swing.JLabel();
        pnlChicken = new javax.swing.JPanel();
        lblChickenPrice = new javax.swing.JLabel();
        btnAddChicken = new javax.swing.JButton();
        lblChickenImage = new javax.swing.JLabel();
        lblChicken = new javax.swing.JLabel();
        spnChicken = new javax.swing.JSpinner();
        jPanel9 = new javax.swing.JPanel();
        lblBurger = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnAddBurger = new javax.swing.JButton();
        lblBurgerImage = new javax.swing.JLabel();
        spnBurger = new javax.swing.JSpinner();
        pnlFries = new javax.swing.JPanel();
        lblFries = new javax.swing.JLabel();
        lblFriesPrice = new javax.swing.JLabel();
        btnAddFries = new javax.swing.JButton();
        lblFriesImage = new javax.swing.JLabel();
        spnFries = new javax.swing.JSpinner();
        pnlSoftdrinks = new javax.swing.JPanel();
        lblSoftdrinksPrice = new javax.swing.JLabel();
        btnAddSoftdrinks = new javax.swing.JButton();
        lblSoftdrinksImage = new javax.swing.JLabel();
        lblSoftdrinks = new javax.swing.JLabel();
        spnSoftdrinks = new javax.swing.JSpinner();
        jPanel10 = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        lblOrderSummary = new javax.swing.JLabel();
        btnDeleteItem = new javax.swing.JButton();
        btnCheckout = new javax.swing.JButton();
        pnlOrderList = new javax.swing.JScrollPane();
        pnlOrderItems = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btnEndDuty = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel8.setBackground(new java.awt.Color(45, 29, 23));

        jPanel7.setBackground(new java.awt.Color(45, 29, 23));

        lblMenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMenu.setForeground(new java.awt.Color(255, 102, 0));
        lblMenu.setText("    MENU");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lblMenu)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(lblMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlChicken.setBackground(new java.awt.Color(27, 20, 16));

        lblChickenPrice.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblChickenPrice.setForeground(new java.awt.Color(255, 102, 0));
        lblChickenPrice.setText("₱120.00");

        btnAddChicken.setBackground(new java.awt.Color(255, 102, 0));
        btnAddChicken.setForeground(new java.awt.Color(27, 20, 16));
        btnAddChicken.setText("Add");
        btnAddChicken.addActionListener(this::btnAddChickenActionPerformed);

        lblChickenImage.setText("🍗");

        lblChicken.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblChicken.setForeground(new java.awt.Color(255, 255, 255));
        lblChicken.setText("Chicken");

        javax.swing.GroupLayout pnlChickenLayout = new javax.swing.GroupLayout(pnlChicken);
        pnlChicken.setLayout(pnlChickenLayout);
        pnlChickenLayout.setHorizontalGroup(
            pnlChickenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChickenLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlChickenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChickenLayout.createSequentialGroup()
                        .addComponent(lblChicken, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlChickenLayout.createSequentialGroup()
                        .addComponent(lblChickenPrice)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlChickenLayout.createSequentialGroup()
                        .addComponent(spnChicken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(btnAddChicken)
                        .addGap(14, 14, 14))))
            .addGroup(pnlChickenLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(lblChickenImage, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlChickenLayout.setVerticalGroup(
            pnlChickenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChickenLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblChickenImage, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblChicken, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblChickenPrice)
                .addGap(18, 18, 18)
                .addGroup(pnlChickenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddChicken)
                    .addComponent(spnChicken, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        jPanel9.setBackground(new java.awt.Color(27, 20, 16));

        lblBurger.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblBurger.setForeground(new java.awt.Color(255, 255, 255));
        lblBurger.setText("Burger");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("₱100.00");

        btnAddBurger.setBackground(new java.awt.Color(255, 102, 0));
        btnAddBurger.setForeground(new java.awt.Color(27, 20, 16));
        btnAddBurger.setText("Add");
        btnAddBurger.addActionListener(this::btnAddBurgerActionPerformed);

        lblBurgerImage.setText("🍔");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBurger, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(spnBurger, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(btnAddBurger)))
                .addGap(14, 14, 14))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(lblBurgerImage, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblBurgerImage, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBurger, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddBurger)
                    .addComponent(spnBurger, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        pnlFries.setBackground(new java.awt.Color(27, 20, 16));

        lblFries.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblFries.setForeground(new java.awt.Color(255, 255, 255));
        lblFries.setText("Fries");

        lblFriesPrice.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblFriesPrice.setForeground(new java.awt.Color(255, 102, 0));
        lblFriesPrice.setText("₱50.00");

        btnAddFries.setBackground(new java.awt.Color(255, 102, 0));
        btnAddFries.setForeground(new java.awt.Color(27, 20, 16));
        btnAddFries.setText("Add");
        btnAddFries.addActionListener(this::btnAddFriesActionPerformed);

        lblFriesImage.setText("🍟");

        javax.swing.GroupLayout pnlFriesLayout = new javax.swing.GroupLayout(pnlFries);
        pnlFries.setLayout(pnlFriesLayout);
        pnlFriesLayout.setHorizontalGroup(
            pnlFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFriesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFriesLayout.createSequentialGroup()
                        .addGroup(pnlFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFries, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFriesPrice))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlFriesLayout.createSequentialGroup()
                        .addComponent(spnFries, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(btnAddFries)
                        .addGap(14, 14, 14))))
            .addGroup(pnlFriesLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(lblFriesImage, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlFriesLayout.setVerticalGroup(
            pnlFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFriesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblFriesImage)
                .addGap(18, 18, 18)
                .addComponent(lblFries, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFriesPrice)
                .addGap(18, 18, 18)
                .addGroup(pnlFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddFries)
                    .addComponent(spnFries, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        pnlSoftdrinks.setBackground(new java.awt.Color(27, 20, 16));

        lblSoftdrinksPrice.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lblSoftdrinksPrice.setForeground(new java.awt.Color(255, 102, 0));
        lblSoftdrinksPrice.setText("₱30.00");

        btnAddSoftdrinks.setBackground(new java.awt.Color(255, 102, 0));
        btnAddSoftdrinks.setForeground(new java.awt.Color(27, 20, 16));
        btnAddSoftdrinks.setText("Add");
        btnAddSoftdrinks.addActionListener(this::btnAddSoftdrinksActionPerformed);

        lblSoftdrinksImage.setText("🥤");

        lblSoftdrinks.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblSoftdrinks.setForeground(new java.awt.Color(255, 255, 255));
        lblSoftdrinks.setText("Softdrinks");

        javax.swing.GroupLayout pnlSoftdrinksLayout = new javax.swing.GroupLayout(pnlSoftdrinks);
        pnlSoftdrinks.setLayout(pnlSoftdrinksLayout);
        pnlSoftdrinksLayout.setHorizontalGroup(
            pnlSoftdrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoftdrinksLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlSoftdrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSoftdrinksLayout.createSequentialGroup()
                        .addComponent(spnSoftdrinks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(btnAddSoftdrinks)
                        .addGap(14, 14, 14))
                    .addGroup(pnlSoftdrinksLayout.createSequentialGroup()
                        .addGroup(pnlSoftdrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSoftdrinksPrice)
                            .addComponent(lblSoftdrinks))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(pnlSoftdrinksLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(lblSoftdrinksImage, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlSoftdrinksLayout.setVerticalGroup(
            pnlSoftdrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoftdrinksLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSoftdrinksImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSoftdrinks, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSoftdrinksPrice)
                .addGap(18, 18, 18)
                .addGroup(pnlSoftdrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddSoftdrinks)
                    .addComponent(spnSoftdrinks, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlChicken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFries, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSoftdrinks, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(251, 251, 251))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlFries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlChicken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSoftdrinks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(82, 82, 82))
        );

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotal.setText("TOTAL:");

        txtTotal.addActionListener(this::txtTotalActionPerformed);

        lblOrderSummary.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblOrderSummary.setText("ORDER SUMMARY");

        btnDeleteItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeleteItem.setText("DELETE ITEM");
        btnDeleteItem.addActionListener(this::btnDeleteItemActionPerformed);

        btnCheckout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCheckout.setText("CHECKOUT ITEM");
        btnCheckout.addActionListener(this::btnCheckoutActionPerformed);

        pnlOrderList.setBackground(new java.awt.Color(27, 20, 16));

        pnlOrderItems.setBackground(new java.awt.Color(27, 20, 16));
        pnlOrderItems.setLayout(new javax.swing.BoxLayout(pnlOrderItems, javax.swing.BoxLayout.Y_AXIS));
        pnlOrderList.setViewportView(pnlOrderItems);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(btnDeleteItem)
                                .addGap(18, 18, 18)
                                .addComponent(btnCheckout))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(lblOrderSummary))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(pnlOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblOrderSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(pnlOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal))
                .addGap(30, 30, 30)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteItem)
                    .addComponent(btnCheckout))
                .addGap(12, 12, 12))
        );

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("MENU");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        btnEndDuty.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEndDuty.setText("END DUTY");
        btnEndDuty.addActionListener(this::btnEndDutyActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnEndDuty)))
                .addContainerGap(163, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEndDuty, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 1015, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(330, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckoutActionPerformed
        
          if (orderModel.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No items in the order!");
            return;
        }

        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
        }

        PAYMENT payment = new PAYMENT(this, orderModel, total);
        payment.setVisible(true);
    }

    // called by PAYMENT after the receipt is saved and shown
    void completeOrder() {
        orderModel.setRowCount(0);
        refreshOrderDisplay();
        txtTotal.setText("0.00");
        spnBurger.setValue(0);
        spnFries.setValue(0);
        spnChicken.setValue(0);
        spnSoftdrinks.setValue(0);
    
    }//GEN-LAST:event_btnCheckoutActionPerformed

    private void btnDeleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteItemActionPerformed
        
    if (orderModel.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No items to delete.");
            return;
        }

        String[] itemNames = new String[orderModel.getRowCount()];
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            itemNames[i] = orderModel.getValueAt(i, 0).toString()
                + " (Qty: " + orderModel.getValueAt(i, 1) + ")";
        }

        String selected = (String) javax.swing.JOptionPane.showInputDialog(
            this, "Select item to delete:", "Delete Item",
            javax.swing.JOptionPane.QUESTION_MESSAGE, null, itemNames, itemNames[0]
        );

        if (selected == null) return;

        int selectedRow = java.util.Arrays.asList(itemNames).indexOf(selected);

        int currentQty = Integer.parseInt(orderModel.getValueAt(selectedRow, 1).toString());
        double currentRowTotal = Double.parseDouble(orderModel.getValueAt(selectedRow, 2).toString());
        double pricePerUnit = currentRowTotal / currentQty;

        String input = javax.swing.JOptionPane.showInputDialog(
            this, "Current quantity: " + currentQty + "\nHow many do you want to remove?"
        );
        if (input == null || input.trim().isEmpty()) return;

        int qtyToRemove;
        try {
            qtyToRemove = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            return;
        }
        if (qtyToRemove <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Enter a number greater than 0.");
            return;
        }

        if (qtyToRemove >= currentQty) {
            orderModel.removeRow(selectedRow);
        } else {
            int newQty = currentQty - qtyToRemove;
            double newRowTotal = newQty * pricePerUnit;
            orderModel.setValueAt(newQty, selectedRow, 1);
            orderModel.setValueAt(newRowTotal, selectedRow, 2);
        }

        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
        }
        txtTotal.setText(String.format("%.2f", total));
        refreshOrderDisplay();
    

    }//GEN-LAST:event_btnDeleteItemActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void btnAddBurgerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBurgerActionPerformed
    int quantity = (int) spnBurger.getValue();
if (!hasStock("Burger", quantity)) return;
        if (quantity > 0) {
            orderModel.addRow(new Object[]{"Burger", quantity, quantity * 100.00});

            double total = 0;
            for (int i = 0; i < orderModel.getRowCount(); i++) {
                total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
            }
            txtTotal.setText(String.format("%.2f", total));
            spnBurger.setValue(0);
            refreshOrderDisplay();
        

}
    
        


// TODO add your handling code here:
    }//GEN-LAST:event_btnAddBurgerActionPerformed

    private void btnAddFriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFriesActionPerformed
int quantity = (int) spnFries.getValue();
if (!hasStock("Fries", quantity)) return;
    if (quantity > 0) {
        orderModel.addRow(new Object[]{"Fries", quantity, quantity * 50.00});

        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
        }
        txtTotal.setText(String.format("%.2f", total));
        spnFries.setValue(0);
        refreshOrderDisplay();
    }


        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddFriesActionPerformed

    private void btnAddChickenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddChickenActionPerformed
int quantity = (int) spnChicken.getValue();
if (!hasStock("Chicken", quantity)) return;
    if (quantity > 0) {
        orderModel.addRow(new Object[]{"Chicken", quantity, quantity * 120.00});

        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
        }
        txtTotal.setText(String.format("%.2f", total));
        spnChicken.setValue(0);
        refreshOrderDisplay();
    }


        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddChickenActionPerformed

    private void btnAddSoftdrinksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSoftdrinksActionPerformed

int quantity = (int) spnSoftdrinks.getValue();

if (!hasStock("SoftDrinks", quantity)) return;
    if (quantity > 0) {
        orderModel.addRow(new Object[]{"Softdrinks", quantity, quantity * 30.00});

        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
        }
        txtTotal.setText(String.format("%.2f", total));
        spnSoftdrinks.setValue(0);
        refreshOrderDisplay();
    }

        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddSoftdrinksActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
new Options().setVisible(true);
    this.dispose();                // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnEndDutyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndDutyActionPerformed
int ok = JOptionPane.showConfirmDialog(this,
            "End your duty and generate the report?", "End Duty",
            JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        String shiftId = Session.getShiftId();   // read it first
        Session.endShift();                       // saves the shift end time
        new SHIFTREPORT(shiftId).setVisible(true);
        this.dispose();


    }//GEN-LAST:event_btnEndDutyActionPerformed

    private int qtyInOrder(String item) {
        int sum = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            if (orderModel.getValueAt(i, 0).toString().equals(item)) {
                sum += Integer.parseInt(orderModel.getValueAt(i, 1).toString());
            }
        }
        return sum;
    }

    // true if there is enough stock for qty more of this product
    private boolean hasStock(String item, int qty) {
        if (qty <= 0) return true;
        int left = StockManager.getStock(item) - qtyInOrder(item);
        if (qty > left) {
            javax.swing.JOptionPane.showMessageDialog(this,
                left <= 0 ? item + " is out of stock."
                          : "Only " + left + " " + item + " left in stock.");
            return false;
        }
        return true;
    }

    // disables a product and shows OUT OF STOCK when it runs out
    private void applyStock(java.util.Map<String, Integer> stock, String item,
                            javax.swing.JButton btn, javax.swing.JSpinner spn) {
        boolean out = stock.getOrDefault(item, 0) <= 0;
        btn.setEnabled(!out);
        spn.setEnabled(!out);
        btn.setText(out ? "OUT OF STOCK" : "Add");
        if (out) spn.setValue(0);
    }

    private void refreshStock() {
        java.util.Map<String, Integer> stock = StockManager.getAllStock();
        applyStock(stock, "Burger", btnAddBurger, spnBurger);
        applyStock(stock, "Fries", btnAddFries, spnFries);
        applyStock(stock, "Chicken", btnAddChicken, spnChicken);
        applyStock(stock, "Softdrinks", btnAddSoftdrinks, spnSoftdrinks);
    }
    
     private JPanel pnlMenuGrid;
    private final java.util.Map<String, JSpinner> cardSpinners = new java.util.LinkedHashMap<>();

    private void buildMenu() {
        Dimension size = jPanel8.getPreferredSize();
        if (size.width < 400 || size.height < 400) size = new Dimension(560, 640);

        jPanel8.removeAll();
        jPanel8.setLayout(new BorderLayout(0, 10));
        jPanel8.setPreferredSize(size);
        jPanel8.setBackground(Theme.CREAM);
        jPanel8.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("MENU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Theme.ORANGE);

        pnlMenuGrid = new JPanel(new GridLayout(0, 2, 18, 18));
        pnlMenuGrid.setBackground(Theme.CREAM);
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(Theme.CREAM);
        holder.add(pnlMenuGrid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.CREAM);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel8.add(title, BorderLayout.NORTH);
        jPanel8.add(scroll, BorderLayout.CENTER);

        refreshMenu();
    }

    private void refreshMenu() {
        if (pnlMenuGrid == null) return;

        java.util.Map<String, Integer> keep = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, JSpinner> e : cardSpinners.entrySet()) {
            keep.put(e.getKey(), (Integer) e.getValue().getValue());
        }
        cardSpinners.clear();
        pnlMenuGrid.removeAll();

        String sql = "SELECT item_name, price, stock, low_stock_level "
                   + "FROM inventory WHERE active = 1 ORDER BY item_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("item_name");
                pnlMenuGrid.add(createProductCard(name, rs.getBigDecimal("price"),
                        rs.getInt("stock"), rs.getInt("low_stock_level"),
                        null, keep.getOrDefault(name, 0)));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load the menu: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        if (pnlMenuGrid.getComponentCount() == 0) {
            JLabel empty = new JLabel("No products on the menu yet. Ask the admin to add some.");
            empty.setForeground(Theme.MUTED);
            pnlMenuGrid.add(empty);
        }
        pnlMenuGrid.revalidate();
        pnlMenuGrid.repaint();
    }

    private JPanel createProductCard(String name, java.math.BigDecimal price, int stock,
                                     int lowLevel, byte[] image, int keepQty) {
        int available = stock - qtyInOrder(name);
        boolean out = available <= 0;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.PAPER);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.LINE),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        card.setPreferredSize(new Dimension(220, 300));

        JLabel pic = new JLabel("", JLabel.CENTER);
        if (image != null) {
            Image img = new ImageIcon(image).getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            pic.setIcon(new ImageIcon(img));
        } else {
            pic.setText(name.substring(0, 1).toUpperCase());
            pic.setFont(new Font("Segoe UI", Font.BOLD, 44));
            pic.setOpaque(true);
            pic.setBackground(Theme.FIELD);
            pic.setForeground(Theme.MUTED);
        }
        pic.setPreferredSize(new Dimension(130, 130));
        pic.setMinimumSize(new Dimension(130, 130));
        pic.setMaximumSize(new Dimension(130, 130));
        pic.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel(name, JLabel.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLbl.setForeground(Theme.TEXT);
        nameLbl.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel("\u20B1" + price.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                JLabel.CENTER);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLbl.setForeground(Theme.ORANGE);
        priceLbl.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel stockLbl;
        if (out) {
            stockLbl = new JLabel("Out of stock", JLabel.CENTER);
            stockLbl.setForeground(new Color(200, 40, 40));
        } else if (available <= lowLevel) {
            stockLbl = new JLabel("Only " + available + " left", JLabel.CENTER);
            stockLbl.setForeground(new Color(200, 120, 0));
        } else {
            stockLbl = new JLabel(available + " in stock", JLabel.CENTER);
            stockLbl.setForeground(Theme.MUTED);
        }
        stockLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockLbl.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        int max = Math.max(available, 0);
        JSpinner spn = new JSpinner(new SpinnerNumberModel(Math.min(keepQty, max), 0, max, 1));
        spn.setPreferredSize(new Dimension(64, 30));
        spn.setEnabled(!out);
        if (spn.getEditor() instanceof JSpinner.DefaultEditor) {
            javax.swing.JFormattedTextField tf = ((JSpinner.DefaultEditor) spn.getEditor()).getTextField();
            tf.setBackground(Theme.FIELD);
            tf.setForeground(Theme.TEXT);
        }

        JButton btn = new JButton(out ? "OUT OF STOCK" : "Add");
        btn.setBackground(Theme.ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setEnabled(!out);
        btn.addActionListener(e -> addToOrder(name, price.doubleValue(), (Integer) spn.getValue()));

        cardSpinners.put(name, spn);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        controls.setOpaque(false);
        controls.add(spn);
        controls.add(btn);
        controls.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        card.add(pic);
        card.add(javax.swing.Box.createVerticalStrut(8));
        card.add(nameLbl);
        card.add(priceLbl);
        card.add(stockLbl);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(controls);
        return card;
    }

    private void addToOrder(String name, double unitPrice, int qty) {
        if (qty <= 0) return;
        if (!hasStock(name, qty)) return;

        boolean merged = false;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            if (orderModel.getValueAt(i, 0).toString().equals(name)) {
                int newQty = Integer.parseInt(orderModel.getValueAt(i, 1).toString()) + qty;
                orderModel.setValueAt(newQty, i, 1);
                orderModel.setValueAt(Math.round(newQty * unitPrice * 100) / 100.0, i, 2);
                merged = true;
                break;
            }
        }
        if (!merged) {
            orderModel.addRow(new Object[]{name, qty, Math.round(qty * unitPrice * 100) / 100.0});
        }

        double total = 0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(orderModel.getValueAt(i, 2).toString());
        }
        txtTotal.setText(String.format("%.2f", total));

        JSpinner box = cardSpinners.get(name);
        if (box != null) box.setValue(0);

        refreshOrderDisplay();
    }
    
    
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

    java.awt.EventQueue.invokeLater(() -> new WOW().setVisible(true));
        
        
        
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBurger;
    private javax.swing.JButton btnAddChicken;
    private javax.swing.JButton btnAddFries;
    private javax.swing.JButton btnAddSoftdrinks;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JButton btnEndDuty;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblBurger;
    private javax.swing.JLabel lblBurgerImage;
    private javax.swing.JLabel lblChicken;
    private javax.swing.JLabel lblChickenImage;
    private javax.swing.JLabel lblChickenPrice;
    private javax.swing.JLabel lblFries;
    private javax.swing.JLabel lblFriesImage;
    private javax.swing.JLabel lblFriesPrice;
    private javax.swing.JLabel lblMenu;
    private javax.swing.JLabel lblOrderSummary;
    private javax.swing.JLabel lblSoftdrinks;
    private javax.swing.JLabel lblSoftdrinksImage;
    private javax.swing.JLabel lblSoftdrinksPrice;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlChicken;
    private javax.swing.JPanel pnlFries;
    private javax.swing.JPanel pnlOrderItems;
    private javax.swing.JScrollPane pnlOrderList;
    private javax.swing.JPanel pnlSoftdrinks;
    private javax.swing.JSpinner spnBurger;
    private javax.swing.JSpinner spnChicken;
    private javax.swing.JSpinner spnFries;
    private javax.swing.JSpinner spnSoftdrinks;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables


}
