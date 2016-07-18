package src.Problem;

import java.io.FileWriter;
import java.io.IOException;

class OutputWriter {

    void WriteLine(String line, String logFile, String directory) {

	if (line == null) {
	    try {
		throw new Exception("Null line to write");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	if (line.contains("EVENT") == true) {
	    // System.out.println(this.getClass().toString() + " Skriver ut: " + line);
	}
	// System.out.println("wants to write: " + line + " to file " + logFile);

	try {
	    String filename = directory + "Parsingresults_" + logFile;
	    FileWriter fw = new FileWriter(filename, true); // the true will append the new data
	    fw.write(line + System.getProperty("line.separator"));// appends the string to the file
	    fw.close();
	} catch (IOException ioe) {
	    System.err.println("IOException: " + ioe.getMessage());
	}

    }
}
