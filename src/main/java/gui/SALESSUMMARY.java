package gui;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
 
/** Admin sales summary: totals, best sellers, sales per staff and per shift. Only COMPLETED orders count as sales. */
public class SALESSUMMARY extends JFrame {
 
    private static final String[] PERIODS = {"TODAY", "YESTERDAY", "LAST 7 DAYS", "THIS MONTH", "ALL TIME"};
 
    private final JComboBox<String> cbPeriod = new JComboBox<>(PERIODS);
    private final JLabel lblTotal = big("₱0.00");
    private final JLabel lblOrders = big("0");
    private final JLabel lblAverage = big("₱0.00");
    private final JLabel lblVoided = big("0");
    private final JTable tblItems = new JTable();
    private final JTable tblStaff = new JTable();
    private final JTable tblShifts = new JTable();
    private final JTable tblType = new JTable();
 
    public SALESSUMMARY() {
        setTitle("SALES SUMMARY");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 760);
        setLayout(new BorderLayout(0, 12));
 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.add(new JLabel("PERIOD"));
        top.add(cbPeriod);
        JButton btRefresh = new JButton("REFRESH");
        btRefresh.addActionListener(e -> load());
        top.add(btRefresh);
        JButton btClose = new JButton("CLOSE");
        btClose.setBackground(new Color(40, 40, 40));   // dark = themed as a light secondary button
        btClose.addActionListener(e -> dispose());
        top.add(btClose);
        cbPeriod.addActionListener(e -> load());
 
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.add(card("TOTAL SALES", lblTotal));
        cards.add(card("ORDERS", lblOrders));
        cards.add(card("AVERAGE ORDER", lblAverage));
        cards.add(card("VOIDED ORDERS", lblVoided));
 
        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 16));
        north.add(top, BorderLayout.NORTH);
        north.add(cards, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);
 
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("BEST SELLERS", new JScrollPane(tblItems));
        tabs.addTab("SALES PER STAFF", new JScrollPane(tblStaff));
        tabs.addTab("SALES PER SHIFT", new JScrollPane(tblShifts));
        tabs.addTab("DINE-IN / TAKEOUT", new JScrollPane(tblType));
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        center.add(tabs, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
 
        Theme.apply(this);
        setLocationRelativeTo(null);
        load();
    }
 
    private static JLabel big(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 26f));
        return l;
    }
 
    private static JPanel card(String caption, JLabel value) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.LINE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel c = new JLabel(caption, SwingConstants.CENTER);
        c.setForeground(Theme.MUTED);
        p.add(c, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }
 
    /** Start (inclusive) and end (exclusive) of the chosen period. */
    private Timestamp[] range() {
        LocalDate today = LocalDate.now();
        LocalDateTime from, to = today.plusDays(1).atStartOfDay();
        switch (String.valueOf(cbPeriod.getSelectedItem())) {
            case "TODAY":        from = today.atStartOfDay(); break;
            case "YESTERDAY":    from = today.minusDays(1).atStartOfDay(); to = today.atStartOfDay(); break;
            case "LAST 7 DAYS":  from = today.minusDays(6).atStartOfDay(); break;
            case "THIS MONTH":   from = today.withDayOfMonth(1).atStartOfDay(); break;
            default:             from = LocalDateTime.of(2000, 1, 1, 0, 0); break;
        }
        return new Timestamp[]{Timestamp.valueOf(from), Timestamp.valueOf(to)};
    }
 
    private static String peso(BigDecimal v) {
        return "₱" + (v == null ? "0.00" : v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
    }
 
    private void load() {
        Timestamp[] r = range();
        try (Connection con = DBConnection.getConnection()) {
            // headline numbers
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COALESCE(SUM(CASE WHEN status='COMPLETED' THEN total END),0) AS sales, "
                  + "SUM(status='COMPLETED') AS orders, SUM(status='VOIDED') AS voided "
                  + "FROM orders WHERE order_date >= ? AND order_date < ?")) {
                ps.setTimestamp(1, r[0]);
                ps.setTimestamp(2, r[1]);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal sales = rs.getBigDecimal("sales");
                        int orders = rs.getInt("orders");
                        lblTotal.setText(peso(sales));
                        lblOrders.setText(String.valueOf(orders));
                        lblAverage.setText(orders == 0 ? "₱0.00"
                            : peso(sales.divide(BigDecimal.valueOf(orders), 2, java.math.RoundingMode.HALF_UP)));
                        lblVoided.setText(String.valueOf(rs.getInt("voided")));
                    }
                }
            }
            fill(con, tblItems,
                "SELECT i.item_name, SUM(i.qty) AS sold, SUM(i.qty * i.price) AS sales "
              + "FROM order_items i JOIN orders o ON o.order_id = i.order_id "
              + "WHERE o.status='COMPLETED' AND o.order_date >= ? AND o.order_date < ? "
              + "GROUP BY i.item_name ORDER BY sold DESC, sales DESC",
                r, new String[]{"#", "PRODUCT", "QUANTITY SOLD", "SALES"}, new boolean[]{false, false, true}, true);
            fill(con, tblStaff,
                "SELECT s.staff_code, COALESCE(MAX(NULLIF(s.staff_name,'')), s.staff_code) AS name, "
              + "COUNT(*) AS orders, SUM(o.total) AS sales "
              + "FROM orders o JOIN shifts s ON s.shift_id = o.shift_id "
              + "WHERE o.status='COMPLETED' AND o.order_date >= ? AND o.order_date < ? "
              + "GROUP BY s.staff_code ORDER BY sales DESC",
                r, new String[]{"STAFF ID", "NAME", "ORDERS", "SALES"}, new boolean[]{false, false, false, true}, false);
            fill(con, tblShifts,
                "SELECT o.shift_id, s.staff_code, DATE(MIN(o.order_date)) AS day, COUNT(*) AS orders, SUM(o.total) AS sales "
              + "FROM orders o JOIN shifts s ON s.shift_id = o.shift_id "
              + "WHERE o.status='COMPLETED' AND o.order_date >= ? AND o.order_date < ? "
              + "GROUP BY o.shift_id, s.staff_code ORDER BY MIN(o.order_date) DESC",
                r, new String[]{"SHIFT ID", "STAFF ID", "DATE", "ORDERS", "SALES"}, new boolean[]{false, false, false, false, true}, false);
            fill(con, tblType,
                "SELECT o.order_type, COUNT(*) AS orders, SUM(o.total) AS sales "
              + "FROM orders o "
              + "WHERE o.status='COMPLETED' AND o.order_date >= ? AND o.order_date < ? "
              + "GROUP BY o.order_type ORDER BY sales DESC",
                r, new String[]{"ORDER TYPE", "ORDERS", "SALES"}, new boolean[]{false, false, true}, false);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load the sales summary: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    /** Runs a query and shows it in a table. moneyLast marks columns shown as pesos; numbered adds a rank column. */
    private void fill(Connection con, JTable table, String sql, Timestamp[] r,
                      String[] headers, boolean[] money, boolean numbered) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, r[0]);
            ps.setTimestamp(2, r[1]);
            try (ResultSet rs = ps.executeQuery()) {
                int cols = rs.getMetaData().getColumnCount();
                int rank = 0;
                while (rs.next()) {
                    Object[] row = new Object[headers.length];
                    int k = 0;
                    if (numbered) row[k++] = ++rank;
                    for (int c = 1; c <= cols; c++, k++) {
                        boolean isMoney = money[Math.min(c - 1, money.length - 1)] && c == cols;
                        row[k] = isMoney ? peso(rs.getBigDecimal(c)) : String.valueOf(rs.getObject(c));
                    }
                    model.addRow(row);
                }
            }
        }
        table.setModel(model);
    }
}