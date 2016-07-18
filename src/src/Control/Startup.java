package src.Control;

import java.io.IOException;

@SuppressWarnings("ucd")
public class Startup {

    public static void main(String[] args) throws IOException {
	System.out.println("Starting parser");

	Controller ny = new Controller();
	try {
	    ny.Startup_1_GUI();
	    ny.SettingsComplete();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	
    }
}
