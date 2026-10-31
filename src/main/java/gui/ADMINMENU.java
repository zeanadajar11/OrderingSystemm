package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ADMINMENU extends JFrame {

    private final Color bg = new Color(27, 20, 16);
    private final Color card = new Color(45, 29, 23);
    private final Color accent = new Color(255, 102, 0);
    private final Color line = new Color(58, 44, 35);

    public ADMINMENU() {
        setTitle("Admin Menu");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 440);
        setLocationRelativeTo(null);
        buildUi();
        Theme.apply(this);
        Theme.maximizeCentered(this);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(bg);
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("ADMIN MENU", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(accent);

        JButton btnRecords = makeButton("RECORDS", true);
        JButton btnInventory = makeButton("INVENTORY", true);
        JButton btnProducts = makeButton("PRODUCTS", true);
        JButton btnRegister = makeButton("REGISTER STAFF", true);
        JButton btnSales = makeButton("SALES SUMMARY", true);
        JButton btnLogout = makeButton("LOG OUT", false);

        
        btnRecords.addActionListener(e -> new RECORDS().setVisible(true));
        btnInventory.addActionListener(e -> new INVENTORY().setVisible(true));
                btnProducts.addActionListener(e -> new PRODUCTS().setVisible(true));
        btnRegister.addActionListener(e -> new REGISTERSTAFF().setVisible(true));
        btnSales.addActionListener(e -> new SALESSUMMARY().setVisible(true));
        btnLogout.addActionListener(e -> {
            new Options().setVisible(true);
            dispose();
        });

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 16));
        buttons.setBackground(card);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line, 1),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)));
        buttons.add(btnRecords);
        buttons.add(btnInventory);
        buttons.add(btnProducts);
        buttons.add(btnRegister);
        buttons.add(btnSales);
        buttons.add(btnLogout);

        root.add(title, BorderLayout.NORTH);
        root.add(buttons, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton makeButton(String text, boolean primary) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setPreferredSize(new Dimension(300, 48));
        if (primary) {
            b.setBackground(accent);
            b.setForeground(bg);
        } else {
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
        }
        return b;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new ADMINMENU().setVisible(true));
    }
}