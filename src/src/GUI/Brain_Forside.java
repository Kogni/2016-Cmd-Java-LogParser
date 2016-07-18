package src.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import src.Control.Controller;

/**
 * @author Berit Larsen
 *
 */
public class Brain_Forside {

    private Controller Class_Controller;

    private Frame_Forside Class_GUI_Forside;

    private boolean RunTimer = true;

    public Brain_Forside(Controller Class_Controller) {
	this.Class_Controller = Class_Controller;
	//System.out.println(this.getClass().toString() + " started");
	//SettOppGUI();

    }

    public void SettOppGUI() {
	System.out.println(this.getClass().toString() + " SettOppGUI");
	Class_GUI_Forside = new Frame_Forside(this, Class_Controller);
	Class_GUI_Forside.SettOpp();
	Class_GUI_Forside.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Timeren();
    }

    private void Timeren() {
	int delay = 10; //milliseconds
	ActionListener taskPerformer = new ActionListener() {

	    public void actionPerformed(ActionEvent evt) {
		if (RunTimer) {
		}
	    }
	};
	new Timer(delay, taskPerformer).start();
    }

    public void AddProgressMessage(String actionMessage) {
	//System.out.println(this.getClass().toString() + " AddProgressMessage " + actionMessage);
	try {
	    Class_GUI_Forside.AddProgressMessage(actionMessage);
	} catch (Exception e) {
	    //System.out.println(this.getClass().toString() + " AddProgressMessage " + actionMessage);
	}
    }

    public void SettingsComplete() {
	Class_GUI_Forside.SettingsComplete();
    }

}
