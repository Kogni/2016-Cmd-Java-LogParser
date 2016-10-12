package src.Parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Logger;

import src.Control.Controller;
import src.Problem.Interpretor2;
import src.Problem.config;

public class ReadFile2 {

    private Controller class_Controller;

    private TextFilter parser;
    private Interpretor2 interpretor;

    public ReadFile2(Controller class_Controller) throws FileNotFoundException, UnsupportedEncodingException {
	this.class_Controller = class_Controller;
	if (class_Controller.workingDir.contains("Users\\bls\\Git\\LogParser")) { // located in dev
	    class_Controller.workingDir = "c:\\PosPay\\logs\\";
	}
	this.class_Controller.PrintAction("Working directory: " + class_Controller.workingDir);
	if (class_Controller.workingDir.contains(" ")) {
	    this.class_Controller
		    .PrintAction("WARNING: The current directory contains spaces. Spaces will reduce processing speed considerably!");
	}

	this.class_Controller.PrintAction("Checking for log files");
	try {
	    parser = new TextFilter(class_Controller);
	    // interpretor = new Interpretor2(class_Controller);
	    // new Interpretor3(class_Controller);
	    File curDir = new File(class_Controller.workingDir);
	    File[] filesList = curDir.listFiles();
	    this.class_Controller.PrintAction("Looping list of files");
	    for (File f : filesList) {
		if (f.isFile()) {
		    //System.out.println(f.getAbsolutePath() + " " + f.getAbsolutePath().toLowerCase() + " " + f.getAbsolutePath().toLowerCase().contains(".log"));
		    // this.class_Controller.PrintAction("Found file: " + f);
		    if ((f.getAbsolutePath().toLowerCase().contains(".log") || f.getAbsolutePath().toLowerCase().contains("log.") || f.getAbsolutePath().toLowerCase().contains(".txt")
			    || f.getAbsolutePath().toLowerCase().contains(".script")) && !f.getAbsolutePath().contains("Parsingresults")) {
			interpretor = new Interpretor2(class_Controller);
			Read_pppclient(f.getName());
		    }
		}
	    }
	} catch (NullPointerException e) {
	    class_Controller.PrintAction("Ingen filer ble funnet i path " + class_Controller.workingDir);
	} catch (Exception e) {
	    class_Controller.PrintAction("An Exception occurred!");
	    class_Controller.PrintAction(e.getMessage());
	    try {
		class_Controller.PrintAction(e.getCause().getMessage());
	    } catch (Exception f) {

	    }

	    StringWriter errors = new StringWriter();
	    e.printStackTrace(new PrintWriter(errors));
	    class_Controller.PrintAction(errors.toString());

	    class_Controller.PrintAction("---------------");

	    StackTraceElement[] stack = new Exception().getStackTrace();
	    String theTrace = "";
	    for (StackTraceElement line : stack) {
		theTrace += line.toString() + "class_Controller.newline";
	    }
	    class_Controller.PrintAction(theTrace);
	} catch (Throwable e) {
	    class_Controller.PrintAction("An error occurred!");

	    StringWriter errors = new StringWriter();
	    e.printStackTrace(new PrintWriter(errors));
	    class_Controller.PrintAction(errors.toString());

	    class_Controller.PrintAction("---------------");

	    StackTraceElement[] stack = new Exception().getStackTrace();
	    String theTrace = "";
	    for (StackTraceElement line : stack) {
		theTrace += line.toString() + "class_Controller.newline";
	    }
	    class_Controller.PrintAction(theTrace);
	}
	this.class_Controller.PrintAction("Finished reading all files.");
    }

    private void Read_pppclient(String logFile) {
	//System.out.println("Reading " + logFile);

	File readFile = new File(class_Controller.workingDir + logFile);
	if (!readFile.exists()) {
	    return;
	}
	logFile = logFile + ".txt";
	File ResultsFile = new File(class_Controller.workingDir + "Parsingresults_" + logFile);
	try {
	    if (ResultsFile.exists()) {
		ResultsFile.delete();
		ResultsFile.createNewFile();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	this.class_Controller.PrintAction("~*~*~ Reading file " + readFile);
	this.class_Controller.PrintAction("Output is printed to " + ResultsFile);
	class_Controller.config = new config();
	// System.out.println("Reading file " + readFile + " parser=" + parser);
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(readFile));
	    String line = null;
	    parser.StartNewParse();
	    while ((line = reader.readLine()) != null) {
		System.out.println("Reading line " + line);
		parser.ParseLine(line, logFile, interpretor, class_Controller.workingDir);
	    }
	    reader.close();
	    interpretor.Conclude(logFile, "EOF", class_Controller.workingDir);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	this.class_Controller.PrintAction("-.-. Finished reading " + logFile);
	// System.out.println("Finished reading " + logFile);
    }
}
