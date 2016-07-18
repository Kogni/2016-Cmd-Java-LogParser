package src.GUI;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Panel_MsgPanel extends JPanel {
    
    final JTextArea MessageText;
    //JEditorPane textarea = new JEditorPane("text/html", "");

    public Panel_MsgPanel() {
	this.setSize(this.getSize());
	this.setBackground(new Color((0), (255), (0)));
	JPanel messagePanel = new JPanel();
	messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.PAGE_AXIS));
	messagePanel.setBackground(new Color((255), (0), (0)));
	messagePanel.setMinimumSize(new Dimension(this.getSize()));
	this.add(messagePanel);
	MessageText = new JTextArea(100, 1);
	MessageText.setEditable(false);
	JScrollPane scrollPane = new JScrollPane(MessageText);
	messagePanel.add(scrollPane);
    }
}
