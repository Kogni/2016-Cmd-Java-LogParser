package src.GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import src.Control.Controller;

@SuppressWarnings("serial")
class Frame_Forside extends JFrame implements ActionListener, MouseMotionListener {

    private Panel_MsgPanel msgPanel;

    public Frame_Forside(Brain_Forside Brain_Forside, Controller Class_Controller) {
	super("Expert system LogParser for troubleshooting");
	System.out.println(this.getClass().toString() + " started");
	setBackground(new Color((0), (255), (0)));

	msgPanel = new Panel_MsgPanel();
	msgPanel.setLayout(new BoxLayout(this.msgPanel, BoxLayout.LINE_AXIS));
	msgPanel.setVisible(true);
	this.add(msgPanel);
	
	this.pack();

	this.setVisible(true);
    }

    public void SettOpp() {

	this.setVisible(true);
    }

    public void SettingsComplete() {
	//msgPanel = new Panel_MsgPanel();
	msgPanel.setLayout(new BoxLayout(this.msgPanel, BoxLayout.LINE_AXIS));
	msgPanel.setVisible(true);
	setSize(700, 340);
	this.add(msgPanel);
    }

    public void AddProgressMessage(String Message) throws Exception {
	//System.out.println(this.toString().getClass().toString() + " AddProgressMessage a");
	try {
	    //System.out.println(this.toString().getClass().toString() + " AddProgressMessage a2");
	    Date Idag = new Date();
	    Message = Message.substring(0, Math.min(200, Message.length()));
	    msgPanel.MessageText.append(Idag.getHours() + "." + Idag.getMinutes() + ":" + Idag.getSeconds() + " " + Message + "\n");
	    if (msgPanel.MessageText.getLineCount() > (msgPanel.MessageText.getRows() - 1)) {
		Element root = msgPanel.MessageText.getDocument().getDefaultRootElement();
		Element first = root.getElement(0);
		try {
		    msgPanel.MessageText.getDocument().remove(first.getStartOffset(), first.getEndOffset());
		} catch (BadLocationException e) {
		}
	    }
	    msgPanel.MessageText.selectAll();
	    msgPanel.MessageText.setCaretPosition(msgPanel.MessageText.getDocument().getLength());
	    
	} catch (Exception e) {

	}
    }

    @SuppressWarnings("ucd")
    public void actionPerformed(ActionEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

}
