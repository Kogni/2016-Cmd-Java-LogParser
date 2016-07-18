package src.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.Control.Controller;

@SuppressWarnings("serial") class Panel_Settings extends JPanel implements ActionListener, ItemListener {
    private
    Controller Class_Controller;

    public Panel_Settings(Controller Class_Controller) {
	this.Class_Controller = Class_Controller;
	this.setSize(this.getSize());
	this.setBackground(new Color((255), (255), (255)));

	//-----------------------------------
	JPanel PanelSource = new JPanel(new GridLayout(0, 1));

	JLabel sourcelabel = new JLabel("Sources");
	PanelSource.add(sourcelabel);

	add(PanelSource, BorderLayout.LINE_START);

	//-----------------------------------
	JPanel PanelRestrict = new JPanel(new GridLayout(0, 1));

	JLabel restrictlabel = new JLabel("Restrict search");
	PanelRestrict.add(restrictlabel);

	add(PanelRestrict, BorderLayout.LINE_START);

	//-----------------------------------
	JPanel PanelSave = new JPanel(new GridLayout(0, 1));

	JLabel savelabel = new JLabel("Output saved");
	PanelSave.add(savelabel);

	add(PanelSave, BorderLayout.LINE_START);

	//-----------------------------------
	JPanel PanelSort = new JPanel(new GridLayout(0, 1));

	JLabel sortlabel = new JLabel("Output sorting");
	PanelSort.add(sortlabel);

	add(PanelSort, BorderLayout.LINE_START);

	//-----------------------------------

	setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    @SuppressWarnings("ucd")
    public void actionPerformed(ActionEvent e) {
	if ("Start".equals(e.getActionCommand())) {
	    Class_Controller.SettingsComplete();
	    this.setVisible(false);
	}
    }

    public void itemStateChanged(ItemEvent e) {
	// TODO Auto-generated method stub

    }

}
