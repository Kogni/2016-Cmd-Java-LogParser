package src.Control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import src.GUI.Brain_Forside;
import src.Parsing.ReadFile2;
import src.Problem.config;

public class Controller {

    private Brain_Forside class_Brain_Forside;

    public config config;
    public String workingDir = System.getProperty("user.dir")+"\\";
    public String newline = System.getProperty("line.separator");

    public Controller() {

    }

    void Startup_1_GUI() throws Exception {
	class_Brain_Forside = new Brain_Forside(this);
	class_Brain_Forside.SettOppGUI();
    }

    private void Startup_6_EnableSearch() { // enabler normale søk
	try {
	    new ReadFile2(this);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void SettingsComplete() {
	class_Brain_Forside.SettingsComplete();

	//PrintAction(this.getClass().toString() + " Settings adjusted");

	try {

	    Startup_6_EnableSearch();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean PrintAction(String actionMessage) {
	this.class_Brain_Forside.AddProgressMessage(actionMessage);

	return false;
    }

    public void newSession() {
	config.newSession();
    }

}
