package ui.dialogs;

import javax.swing.*;

public class ReceiptDialog extends JDialog {
    public ReceiptDialog(JFrame parent, String receiptContent) {
        super(parent, "Customer Receipt", true);
        setSize(300, 400);
        setLocationRelativeTo(parent);

        JTextArea txtReceipt = new JTextArea(receiptContent);
        txtReceipt.setEditable(false);
        add(new JScrollPane(txtReceipt));
    }
}