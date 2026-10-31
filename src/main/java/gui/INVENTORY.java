package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class INVENTORY extends JFrame {

    private final Color bg = new Color(27, 20, 16);
    private final Color card = new Color(45, 29, 23);
    private final Color accent = new Color(255, 102, 0);
    private final Color line = new Color(58, 44, 35);

    private final JTable tblInventory = new JTable();

    public INVENTORY() {
        setTitle("Inventory");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        buildUi();
        loadInventory();
        Theme.apply(this);
        Theme.maximize(this);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(bg);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("INVENTORY", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(accent);

        tblInventory.setBackground(card);
        tblInventory.setForeground(Color.WHITE);
        tblInventory.setRowHeight(30);
        tblInventory.setGridColor(line);
        tblInventory.setSelectionBackground(accent);
        tblInventory.setSelectionForeground(bg);
        tblInventory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblInventory.getTableHeader().setBackground(line);
        tblInventory.getTableHeader().setForeground(Color.WHITE);
        tblInventory.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tblInventory);
        scroll.getViewport().setBackground(card);
        scroll.setBorder(BorderFactory.createLineBorder(line));

        JButton btnAdd = makeButton("ADD STOCK");
        JButton btnSet = makeButton("SET STOCK");
        JButton btnLow = makeButton("SET LOW LEVEL");
        JButton btnClose = makeButton("CLOSE");

        btnAdd.addActionListener(e -> addStock());
        btnSet.addActionListener(e -> setStock());
        btnLow.addActionListener(e -> setLowLevel());
        btnClose.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(bg);
        buttons.add(btnAdd);
        buttons.add(btnSet);
        buttons.add(btnLow);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(bg);
        bottom.add(buttons, BorderLayout.WEST);
        bottom.add(btnClose, BorderLayout.EAST);

        root.add(title, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(accent);
        b.setForeground(bg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(140, 36));
        return b;
    }

    private void loadInventory() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"PRODUCT", "STOCK", "LOW-STOCK LEVEL", "STATUS"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT item_name, stock, low_stock_level FROM inventory ORDER BY item_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int stock = rs.getInt("stock");
                int low = rs.getInt("low_stock_level");
                String status = (stock <= 0) ? "OUT OF STOCK" : (stock <= low ? "LOW STOCK" : "IN STOCK");
                model.addRow(new Object[]{rs.getString("item_name"), stock, low, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load inventory: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        tblInventory.setModel(model);

       
        tblInventory.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (!sel) {
                    String s = String.valueOf(v);
                    if (s.equals("OUT OF STOCK")) c.setForeground(new Color(255, 90, 90));
                    else if (s.equals("LOW STOCK")) c.setForeground(new Color(255, 193, 7));
                    else c.setForeground(new Color(102, 220, 120));
                }
                return c;
            }
        });
    }

   
    private String selectedItem() {
        int row = tblInventory.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product first.");
            return null;
        }
        return tblInventory.getValueAt(row, 0).toString();
    }

    
    private int askNumber(String message, int min) {
        String input = JOptionPane.showInputDialog(this, message);
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int n = Integer.parseInt(input.trim());
            if (n < min) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number of " + min + " or more.");
            return -1;
        }
    }

    private void runUpdate(String sql, int value, String item) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, value);
            ps.setString(2, item);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not update inventory: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        loadInventory();
    }

    private void addStock() {
        String item = selectedItem();
        if (item == null) return;
        int qty = askNumber("How many " + item + " are you adding?", 1);
        if (qty < 0) return;
        runUpdate("UPDATE inventory SET stock = stock + ? WHERE item_name = ?", qty, item);
    }

    private void setStock() {
        String item = selectedItem();
        if (item == null) return;
        int qty = askNumber("Set the stock of " + item + " to:", 0);
        if (qty < 0) return;
        runUpdate("UPDATE inventory SET stock = ? WHERE item_name = ?", qty, item);
    }

    private void setLowLevel() {
        String item = selectedItem();
        if (item == null) return;
        int qty = askNumber("Show LOW STOCK for " + item + " when stock is at or below:", 0);
        if (qty < 0) return;
        runUpdate("UPDATE inventory SET low_stock_level = ? WHERE item_name = ?", qty, item);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new INVENTORY().setVisible(true));
    }
}