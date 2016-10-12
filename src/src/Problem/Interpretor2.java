package src.Problem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import Properties.ObjectTransaction;
import src.Control.Controller;

public class Interpretor2 {

    private Controller class_Controller;
    private OutputWriter outputWriter = new OutputWriter();

    boolean ECRopen = false; // for duplicate ECR-check
    boolean transactionRunning = false; // triggers some conclude
    private boolean transactionIssueFound = false;
    private int awaitingResponse = -1;
    boolean logging = false; // makes sure the first logged lines are included in conclude
    HashMap tagged = new HashMap();
    boolean requestLogged = false;
    String serviceLogged = ""; // used for checking for conclude
    boolean serviceFinished = true;

    private LocalDateTime timePreviousLine;
    private LocalDateTime timeCurrentLine;

    private ObjectTransaction transaction;

    public Interpretor2(Controller class_Controller) throws FileNotFoundException, UnsupportedEncodingException {
	try {
	    this.class_Controller = class_Controller;
	    transaction = new ObjectTransaction(class_Controller);
	} catch (Throwable e) {
	    class_Controller.PrintAction("An error occurred!");
	    StringWriter errors = new StringWriter();
	    e.printStackTrace(new PrintWriter(errors));
	    class_Controller.PrintAction(errors.toString());
	}
    }

    private void NewPeriod(String rawLine, String logFile, String source, String directory)
	    throws FileNotFoundException, UnsupportedEncodingException {
	if (logging && !this.transaction.fact_service.value.equals("")) {
	    Conclude(logFile, source, directory);
	}
	logging = true;
	transaction.saveSymptomStatus(rawLine, logFile);
    }

    private void NewRequest(String rawLine, String logFile, String source, String directory)
	    throws FileNotFoundException, UnsupportedEncodingException {
	if (logging) {
	    Conclude(logFile, source, directory);
	}
	logging = true;
	transaction.saveSymptomStatus(rawLine, logFile);
	transactionRunning = true;
	requestLogged = false;
	serviceLogged = "";
	serviceFinished = false;
    }

    public void CatchInfo(String rawLine, String logFile, String directory) throws FileNotFoundException, UnsupportedEncodingException {
	//System.out.println("CatchInfo "+ rawLine);
	// time

	try {
	    if (tagged.get(logFile) == null) {
		tagged.put(logFile, true);
		CatchInfo("Expert System Log parser by Berit Larsen", logFile, directory);
	    }
	    String tempDate = rawLine;
	    int aar = new Date().getYear() + 1900;
	    int index = tempDate.indexOf(aar + "");
	    /*
	     * try {//requires java 1.8! tempDate = tempDate.substring(index); try { tempDate = tempDate.substring(0, tempDate.indexOf(
	     * " INFO")); } catch (Exception e) { } try { tempDate = tempDate.substring(0, tempDate.indexOf(" ERROR")); } catch (Exception
	     * e) { } tempDate = tempDate.substring(0, tempDate.indexOf(",")); DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
	     * "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH); LocalDateTime date = LocalDateTime.parse(tempDate, formatter); this.timeCurrentLine =
	     * date;
	     * 
	     * } catch (Throwable e) { }
	     */
	    /*
	     * try {//requires java 1.8! long timeDiffSeconds = (timeCurrentLine.getLong(ChronoField.SECOND_OF_DAY) -
	     * timePreviousLine.getLong(ChronoField.SECOND_OF_DAY)); if (timeDiffSeconds > 10) { if (rawLine.toLowerCase().contains(
	     * "Connected to /".toLowerCase())) {// CONCLUDE THEN START NEW TRANSACTION NewPeriod(rawLine, logFile, "Connected to /",
	     * directory); } } } catch (Throwable e) { }
	     */

	    if (rawLine.toLowerCase().contains("PosPayClientImpl: Request:".toLowerCase())) {// CONCLUDE THEN START NEW TRANSACTION
		// request kommer alltid før running service
		// det kan komme requests som ikke trigger noen service.
		// nye requests før forrige service er ferdig, kan ikke starte ny trx
		if (serviceFinished == true) {
		    NewRequest(rawLine, logFile, "PosPayClientImpl: Request:", directory);
		}
		requestLogged = true;
	    }
	    if (rawLine.toLowerCase().contains("Running service ".toLowerCase())) {// Kommer med og uten request, ved hver trx og ved
										   // oppstart
		if (serviceFinished == true) {
		    Conclude(logFile, "Running service ", directory);
		    // NewRequest(rawLine, logFile, "Running service ", directory);
		}
		String start = "Running service ".toLowerCase();
		String temp = rawLine.toLowerCase().substring(rawLine.toLowerCase().indexOf(start));
		temp = temp.substring(start.length());
		temp = temp.substring(0, temp.indexOf(" ") + 1);
		serviceLogged = temp;
	    }
	    if ((rawLine.toLowerCase().contains("New request received from PMS:".toLowerCase()))) {// protel only
		if (serviceFinished == true) {
		    Conclude(logFile, "New request received from PMS:", directory);
		    // NewRequest(rawLine, logFile, "Running service ", directory);
		}
		String start = "New request received from PMS:";
		String temp = rawLine.substring(rawLine.indexOf(start));
		temp = temp.substring(start.length());
		temp = temp.substring(0, temp.indexOf(" ") + 1);
		serviceLogged = temp;
	    }
	    if (rawLine.toLowerCase().contains(" is done *****".toLowerCase())) {
		serviceFinished = true;
		// det kan komme events etter "is done"
	    }
	    if (rawLine.toLowerCase().contains("Closed connection: ".toLowerCase())) {
		serviceFinished = true;
		// det kan komme events etter "is done"
	    }

	    if (rawLine.toLowerCase()
		    .contains("[AWT-EventQueue-0] com.pos.pospayinterface.PosPayClientProxy: getPosPayClient starting".toLowerCase())) {
		// CONCLUDE THEN START NEW TRANSACTION
		NewRequest(rawLine, logFile, "getPosPayClient starting", directory);
	    }

	    if (rawLine.toLowerCase().contains("PPPDLLServer::SendRequest(".toLowerCase())) {// CONCLUDE THEN START NEW TRANSACTION
		NewRequest(rawLine, logFile, "PPPDLLServer::SendRequest(", directory);
	    }
	    if (rawLine.toLowerCase().contains("Connected to /".toLowerCase())) {// CONCLUDE THEN START NEW TRANSACTION
		// System.out.println("timePreviousLine="+timePreviousLine+" timeCurrentLine="+timeCurrentLine);
		logging = true;
		transactionRunning = false;
	    }

	    if ((rawLine.toLowerCase().contains("receiptService is done".toLowerCase()))) {
		// System.out.println(transaction.fact_service.value + " " + rawLine);
		outputWriter.WriteLine(rawLine, logFile, directory);
		transaction.saveSymptomStatus(rawLine, logFile);
		Conclude(logFile, "receiptService is done", directory);
		transactionRunning = false;
		return;
	    }
	    if ((rawLine.toLowerCase().contains("shutting down...".toLowerCase()))) {
		// System.out.println(transaction.fact_service.value + " " + rawLine);
		Conclude(logFile, "shutting down", directory);
		outputWriter.WriteLine(rawLine, logFile, directory);
		transaction.saveSymptomStatus(rawLine, logFile);
		transactionRunning = false;
		ECRopen = false;
		logging = true;
		return;
	    }

	    if ((rawLine.toLowerCase().contains("Starting PosPay Client...".toLowerCase()))) {
		// System.out.println("Starting PosPay Client " + ECRopen + " " + logFile + " " + rawLine);
		if (ECRopen == true) { // duplicate ECR!
		    if (!logFile.contains("api_")) {// api_pppclient viser ikke når pospayservice lukkes.
			outputWriter.WriteLine("Two ECRs are open!a", logFile, directory); // print to log
			transaction.saveSymptomStatus("Two ECRs are open!a", logFile); // send to diagnostics
		    }
		    Conclude(logFile, "Starting PosPay Client...", directory);
		} else { // starten av loggen+restart
		    // System.out.println("Starting PosPay Client " + ECRopen + " " + logFile + " logging=" + logging + " " + rawLine);
		    if (logging) {// restart
			Conclude(logFile, "Starting PosPay Client...", directory);
		    }
		}
		this.class_Controller.newSession();
		ECRopen = true;
		outputWriter.WriteLine(rawLine, logFile, directory);
		transaction.saveSymptomStatus(rawLine, logFile);
		transactionRunning = false;
		serviceFinished = true; // the next service will be a separate stack
		return;
	    }

	    if ((rawLine.toLowerCase().contains("dispatching event: [1".toLowerCase()))) {
		String temp;
		temp = rawLine.substring(rawLine.indexOf("dispatching event: "));
		temp = temp.substring("dispatching event: ".length());
		if ((temp.contains("1905"))) {
		    // System.out.println(transaction.fact_service.value + " " + rawLine);
		    outputWriter.WriteLine(rawLine, logFile, directory);
		    transaction.saveSymptomStatus(rawLine, logFile);
		    Conclude(logFile, "receiptService is done", directory);
		    transactionRunning = false;
		    return;
		}
		// may be event 17 - bonus scanned
		// return;
	    }

	    if (// FORCE CONCLUDE THEN START NEW TRANSACTION
	    (rawLine.toLowerCase().contains("Regular boot".toLowerCase())) // terminal
	    ) {
		//System.out.println("Regular boot "+rawLine);
		Conclude(logFile, "Regular boot", directory);
	    } else if (// FORCE CONCLUDE THEN START NEW TRANSACTION
	    (rawLine.toLowerCase().contains("Starting event".toLowerCase()))
		    || (rawLine.toLowerCase().contains("Wait for transaction: Transaction received".toLowerCase())
			    || (rawLine.toLowerCase().contains("Executing sale transaction".toLowerCase()))
			    )// terminal
	    ) {
		//System.out.println("Transaction received "+rawLine);
		if (transactionRunning == true) {
		    Conclude(logFile, "Transaction received", directory);
		} else {
		    NewRequest(rawLine, logFile, "Transaction received", directory);
		}
		transactionRunning = true;
	    } else if (// FORCE CONCLUDE THEN START NEW TRANSACTION
	    (rawLine.toLowerCase().contains("openPED called..".toLowerCase()))) {// terminal
		if (transactionRunning == true) {
		    Conclude(logFile, "openPED", directory);
		}
		NewRequest(rawLine, logFile, "openPED", directory);
		try {
		    // outputWriter.WriteLine(rawLine, logFile);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		// ENDING TRANSACTION
	    } else // START NEW TRANSACTION WITHOUT CONCLUDE
	    if ((rawLine.toLowerCase().contains("Starting state machine".toLowerCase()))
		    || (rawLine.toLowerCase().contains("PPPDLLServer::SendRequest".toLowerCase()))
		    || (rawLine.toLowerCase().contains("[trx] Initiating ".toLowerCase()))) {
		transactionRunning = true;
	    } else // ENDING TRANSACTION
	    if ((rawLine.toLowerCase().contains("Cannot log input! Input is NULL.".toLowerCase()))
		    || (rawLine.toLowerCase().contains("[i18n] Message NO-569:".toLowerCase()))
		    || (rawLine.toLowerCase().contains("The async flushing thread ended".toLowerCase()))) {
		outputWriter.WriteLine(rawLine, logFile, directory);
		Conclude(logFile, "B", directory);
		return;
	    }
	    if (transactionIssueFound) {
		return;
	    }
	    // teller hvor lenge PPCL har ventet pÃ¥ reaksjon fra terminal
	    if (rawLine.toLowerCase().contains("PPCL awaiting response from terminal!".toLowerCase()) == false) {
		if (rawLine.toLowerCase().contains("Response received".toLowerCase())) {
		    awaitingResponse = -1;
		}
		if (rawLine.toLowerCase().contains("Timeout on incomplete service".toLowerCase())) {
		    awaitingResponse = -1;
		}
		if (rawLine.toLowerCase().contains("STATE_READY_TO_RUN".toLowerCase())) {
		    awaitingResponse = -1;
		}
		if (awaitingResponse > -1) {
		    awaitingResponse++;
		    if (awaitingResponse > 60) {

		    } else if (awaitingResponse > 5) {
			CatchInfo("PPCL awaiting response from terminal! " + awaitingResponse, logFile, directory);
		    }
		}
	    }
	    // registrerer hvilken info som er mottatt fra terminal
	    // FOUND ISSUE
	    if ((rawLine.toLowerCase().contains("Timeout on incomplete service".toLowerCase()))
		    || (rawLine.toLowerCase().contains("Transaction failed".toLowerCase()))
		    || (rawLine.toLowerCase().contains("00000027".toLowerCase()))
		    || (rawLine.toLowerCase().contains("REJECTED".toLowerCase()))) {
	    }
	    if ((rawLine.toLowerCase().contains("Response received".toLowerCase()) == false)
		    && (rawLine.toLowerCase().contains("Message to send (serial-style): 06".toLowerCase()) == false)) {
		if (rawLine.contains("EVENT") == true) {
		    // System.out.println(this.getClass().toString() + " Skriver ut: " + rawLine);
		}
		outputWriter.WriteLine(rawLine, logFile, directory);
	    }
	    // symptoms = 0;
	    // les igjennom globale variabler
	    try {
		transaction.saveSymptomStatus(rawLine, logFile);
	    } catch (Exception e) {
	    }
	    timePreviousLine = this.timeCurrentLine;
	} catch (Throwable e) {
	    class_Controller.PrintAction("An error occurred!");
	    StringWriter errors = new StringWriter();
	    e.printStackTrace(new PrintWriter(errors));
	    class_Controller.PrintAction(errors.toString());
	}
    }

    @SuppressWarnings("javadoc")
    public void Conclude(String logFile, String source, String directory) throws FileNotFoundException, UnsupportedEncodingException { // kalles
																       // fra
																       // CatchInfo
	// System.out.println("-> Interpretor concluding problem. Source=" + source + " issues=" + transaction.issues_last); // og fra
	outputWriter.WriteLine("Conclusion Source=" + source, logFile, directory); // read_pppclient
	outputWriter.WriteLine("__________________________________", logFile, directory);
	if (!noConclusion()) {
	    ClearLogAndStartNewTrx(); // ADDS NO CONCLUSION
	    return;
	}
	String problemname = transaction.getproblemname(logFile);// alltid hente problem name først, for her skjer konklusjoner!
	String description = "";
	if (!addConclusion(problemname)) {
	    return;
	}
	description = transaction.getDescription_session(logFile);
	// WILL ADD CONCLUSION
	try {
	    if (logFile.contains("DEBUG") || logFile.contains("LOG.")) {
		description = transaction.getDescription_transaction(logFile);
	    } else if ((class_Controller.config.getParam("PosPayService").equals("true"))) {
		description = transaction.getDescription_transaction(logFile);
	    }
	} catch (Exception e) {
	}

	// summarize transaction
	outputWriter.WriteLine(description, logFile, directory);

	NoteConclusion(logFile, problemname, problemname, directory);

    }

    private void NoteConclusion(String logFile, String ProbableName, String conclusion, String directory)
	    throws FileNotFoundException, UnsupportedEncodingException {
	// System.out.println("Interpretor shall note conclusion " + conclusion);
	outputWriter.WriteLine("====== CONCLUSION: " + conclusion + " ===============================================", logFile, directory);
	this.class_Controller.PrintAction("Transaction read: " + ProbableName + "");
	// ListReasons();
	ClearLogAndStartNewTrx();
    }

    private void ClearLogAndStartNewTrx() {
	transactionIssueFound = false;
	awaitingResponse = -1;
	try {
	    transaction = new ObjectTransaction(class_Controller);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private boolean addConclusion(String problemname) {
	// System.out.println("addConclusion " + problemname + " " + (transaction.issues_last + transaction.issues_first).length());
	if ((transaction.issues_last + transaction.issues_first).length() > 0) {
	    return true;
	}
	if (problemname.contains("merchant") || problemname.contains("getProperties") || problemname.contains("login")
		|| problemname.contains("? - ? - ? - ? - ?") || problemname.contains("receipt")) {
	    return false;
	}
	return true;
    }

    private boolean noConclusion() {
	/*
	 * if (transaction.issues_last.length() <= 1) { // ingen issues if (!this.logging && !this.transactionRunning) { return false; } }
	 */
	return true;
    }
}
