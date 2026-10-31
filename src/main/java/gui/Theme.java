package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/** One place for the whole app's colors. Call Theme.apply(this) at the END of a screen's constructor. */
public final class Theme {

    public static final Color ORANGE = new Color(255, 90, 10);
    public static final Color CREAM  = new Color(230, 226, 208);
    public static final Color FIELD  = new Color(239, 235, 228);
    public static final Color PAPER  = new Color(250, 248, 242);
    public static final Color TEXT   = new Color(34, 34, 34);
    public static final Color MUTED  = new Color(110, 100, 90);
    public static final Color LINE   = new Color(205, 199, 180);

    private Theme() { }

    public static void apply(JFrame frame) {
        frame.getContentPane().setBackground(CREAM);
        applyTo(frame.getContentPane());
        frame.repaint();
    }

    public static void apply(JDialog dialog) {
        dialog.getContentPane().setBackground(CREAM);
        applyTo(dialog.getContentPane());
        dialog.repaint();
    }
    public static void maximize(JFrame frame) {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Opens the screen maximized AND keeps its design at its normal size, centered in the window.
     * Use for screens with a fixed layout (logins, WOW, receipt, report, menus).
     */
    public static void maximizeCentered(JFrame frame) {
        Container inner = frame.getContentPane();
        JPanel holder = new JPanel(new GridBagLayout());   // a lone child is centered by default
        holder.setBackground(CREAM);
        frame.setContentPane(holder);
        holder.add(inner);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.revalidate();
    }

    private static void applyTo(Component c) {
        if (c instanceof JTable) {
            styleTable((JTable) c);
            return;
        }
        if (c instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) c;
            sp.setBackground(CREAM);
            sp.getViewport().setBackground(PAPER);
            sp.setBorder(BorderFactory.createLineBorder(LINE));
            Component view = sp.getViewport().getView();
            if (view != null) {
                applyTo(view);
                if (view instanceof JPanel) view.setBackground(PAPER);
            }
            return;
        }
        if (c instanceof JComboBox) {
            c.setBackground(FIELD);
            c.setForeground(TEXT);
            return;
        }
        if (c instanceof JSpinner) {
            JSpinner sp = (JSpinner) c;
            sp.setBackground(FIELD);
            if (sp.getEditor() instanceof JSpinner.DefaultEditor) {
                styleField(((JSpinner.DefaultEditor) sp.getEditor()).getTextField());
            }
            return;
        }
        if (c instanceof JTextComponent) {
            styleField((JTextComponent) c);
            return;
        }
        if (c instanceof JButton) {
            styleButton((JButton) c);
            return;
        }
        if (c instanceof JLabel) {
            styleLabel((JLabel) c);
            return;
        }
        if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            p.setOpaque(true);
            p.setBackground(CREAM);
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                applyTo(child);
            }
        }
    }

    private static void styleField(JTextComponent t) {
        t.setForeground(TEXT);
        t.setCaretColor(TEXT);
        if (t instanceof JTextArea) {
            t.setBackground(PAPER);          // receipts and notes look like paper
        } else {
            t.setBackground(FIELD);
            t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        }
    }

    private static void styleButton(JButton b) {
        if (Boolean.TRUE.equals(b.getClientProperty("themed"))) return;
        b.putClientProperty("themed", Boolean.TRUE);

        Color old = b.getBackground();
        boolean wasDark = old != null && (old.getRed() + old.getGreen() + old.getBlue()) / 3 < 90;

        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        if (wasDark) {                       // CANCEL / BACK / LOG OUT style buttons
            b.setBackground(FIELD);
            b.setForeground(TEXT);
            b.setBorder(BorderFactory.createLineBorder(LINE, 1));
        } else {                             // main action buttons
            b.setBackground(ORANGE);
            b.setForeground(Color.WHITE);
        }
    }

    private static void styleLabel(JLabel l) {
        Color f = l.getForeground();
        if (f == null) return;
        int r = f.getRed(), g = f.getGreen(), bl = f.getBlue();
        if (r > 230 && g > 230 && bl > 230) {
            l.setForeground(TEXT);                       // white text -> dark text
        } else if (r > 200 && g < 150 && bl < 90) {
            l.setForeground(ORANGE);                     // old accent -> new orange
        } else if (r > 170 && g > 160 && bl > 150) {
            l.setForeground(MUTED);                      // old light-gray text
        }
    }

    private static void styleTable(JTable t) {
        t.setBackground(PAPER);
        t.setForeground(TEXT);
        t.setGridColor(LINE);
        t.setSelectionBackground(ORANGE);
        t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setBackground(ORANGE);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(Font.BOLD));
    }
}