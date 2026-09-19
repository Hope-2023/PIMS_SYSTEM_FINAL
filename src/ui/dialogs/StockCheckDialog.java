package ui.dialogs;

import javax.swing.*;

public class StockCheckDialog extends JDialog {
    public StockCheckDialog(JFrame parent) {
        super(parent, "Quick Stock Check", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
    }
}