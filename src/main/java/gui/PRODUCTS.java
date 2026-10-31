package gui;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
 
public class PRODUCTS extends JFrame {
 
    private final JTextField txtName = new JTextField(16);
    private final JTextField txtPrice = new JTextField(16);
    private final JTextField txtStock = new JTextField("20", 16);
    private final JTable tblProducts = new JTable();
 
    public PRODUCTS() {
        setTitle("Products");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 560);
        setLocationRelativeTo(null);
        buildUi();
        loadProducts();
        Theme.apply(this);
    }
 
    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
 
        JLabel title = new JLabel("PRODUCTS", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(255, 102, 0));
 
        // ---- left: add a product ----
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 0);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridx = 0;
 
        JLabel formTitle = makeLabel("ADD A PRODUCT");
        formTitle.setForeground(new Color(255, 102, 0));
        gc.gridy = 0; form.add(formTitle, gc);
        gc.gridy = 1; form.add(makeLabel("PRODUCT NAME"), gc);
        gc.gridy = 2; form.add(txtName, gc);
        gc.gridy = 3; form.add(makeLabel("PRICE (PHP)"), gc);
        gc.gridy = 4; form.add(txtPrice, gc);
        gc.gridy = 5; form.add(makeLabel("STARTING STOCK"), gc);
        gc.gridy = 6; form.add(txtStock, gc);
        JButton btnAdd = new JButton("ADD PRODUCT");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(200, 40));
        btnAdd.addActionListener(e -> addProduct());
        gc.gridy = 7; gc.insets = new Insets(16, 0, 6, 0);
        form.add(btnAdd, gc);
 
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(330, 0));
        left.add(form, BorderLayout.NORTH);
 
        // ---- right: product list ----
        JScrollPane scroll = new JScrollPane(tblProducts);
 
        JPanel center = new JPanel(new BorderLayout(24, 0));
        center.add(left, BorderLayout.WEST);
        center.add(scroll, BorderLayout.CENTER);
 
        // ---- bottom: actions on the selected product ----
        JButton btnPrice = new JButton("CHANGE PRICE");
        JButton btnToggle = new JButton("HIDE / SHOW");
        for (JButton b : new JButton[]{btnPrice, btnToggle}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(150, 36));
        }
        btnPrice.addActionListener(e -> changePrice());
        btnToggle.addActionListener(e -> toggleActive());
 
        JButton btnClose = new JButton("CLOSE");
        btnClose.setBackground(new Color(27, 20, 16));    // dark = shown as the light, secondary button
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setPreferredSize(new Dimension(120, 36));
        btnClose.addActionListener(e -> dispose());
 
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.add(btnPrice);
        actions.add(btnToggle);
 
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(actions, BorderLayout.WEST);
        bottom.add(btnClose, BorderLayout.EAST);
 
        root.add(title, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }
 
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }
 
    // ---------- actions ----------
 
    private void addProduct() {
        String name = txtName.getText().trim();
        if (name.isEmpty() || name.length() > 50) {
            JOptionPane.showMessageDialog(this, "Enter a product name (up to 50 characters).");
            return;
        }
        BigDecimal price;
        int stock;
        try {
            price = new BigDecimal(txtPrice.getText().trim()).setScale(2, java.math.RoundingMode.HALF_UP);
            if (price.signum() <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The price must be a number greater than 0, for example 85 or 85.50.");
            return;
        }
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The starting stock must be a whole number, 0 or more.");
            return;
        }
 
        String sql = "INSERT INTO inventory (item_name, stock, low_stock_level, price, active) "
                   + "VALUES (?, ?, 10, ?, 1)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, stock);
            ps.setBigDecimal(3, price);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "A product named \"" + name + "\" already exists.",
                        "Already Exists", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not add the product: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
 
        JOptionPane.showMessageDialog(this, name + " was added to the menu.");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("20");
        loadProducts();
    }
 
    private String selectedName() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product in the list first.");
            return null;
        }
        return tblProducts.getValueAt(row, 0).toString();
    }
 
    private void changePrice() {
        String name = selectedName();
        if (name == null) return;
        String input = JOptionPane.showInputDialog(this, "New price for " + name + " (PHP):");
        if (input == null || input.trim().isEmpty()) return;
        BigDecimal price;
        try {
            price = new BigDecimal(input.trim()).setScale(2, java.math.RoundingMode.HALF_UP);
            if (price.signum() <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The price must be a number greater than 0.");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE inventory SET price = ? WHERE item_name = ?")) {
            ps.setBigDecimal(1, price);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not change the price: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        loadProducts();
    }
 
    // hides a product from the menu (or shows it again). Old receipts keep working.
    private void toggleActive() {
        String name = selectedName();
        if (name == null) return;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE inventory SET active = 1 - active WHERE item_name = ?")) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not update the product: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        loadProducts();
    }
 
    private void loadProducts() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"PRODUCT", "PRICE", "STOCK", "ON MENU"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        String sql = "SELECT item_name, price, stock, active "
                   + "FROM inventory ORDER BY item_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("item_name"),
                    "₱" + rs.getBigDecimal("price"),
                    rs.getInt("stock"),
                    rs.getInt("active") == 1 ? "Yes" : "Hidden"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load products: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        tblProducts.setModel(model);
    }
 
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new PRODUCTS().setVisible(true));
    }
}