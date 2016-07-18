package Properties;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import src.Control.Controller;

/**
 * @author Berit Larsen
 *
 */
public class ObjectTransaction {
    // Nytt objekt skal lages nÃ¥r... ny transaction starter
    // Brukes av... problemer
    private Controller class_Controller;
    // FACTS, clues, cues, certain things
    public Service fact_service;
    private AUTHORIZATION_METHOD fact_authmeth;
    private TRANSACTION_STATUS fact_TS;
    private AUTHORIZATION_RESPONSE_CODE fact_rspCode;
    private String fact_issuerName;
    boolean PPPkey_correct = true;
    int Progress = 0;
    /*
     * 1 = 2 = Request sendt/mottatt 3 = Bedt om kort 4 = Sendt/Mottatt carddata 5 = Bedt om CVM 6 = Event 118/sjekker validitet 7 = 8 = 9 =
     * 10 = Fullført
     */
    // log
    // public boolean pppclient_log = true;
    String ECR = "";
    boolean terminallogg = false;
    // request
    public boolean fact_RequestReceived = false;
    public boolean fact_REQUEST_SENT = false;
    // terminal
    private ONLINE_INDICATOR fact_OI;
    private POS_ENTRY_MODE fact_EntryMode;
    private ID_METHOD fact_iDMeth;
    private String lastEMVStep;
    private boolean fact_terminal_server_connection = false;
    // card
    int fact_CardDataResponse = 0;
    public boolean fact_askedCard = false;
    boolean fact_cardEntered;
    boolean fact_cardEjected;
    boolean fact_BonusCardRead = false; // Bonuskort < Loyalty card < Trumf
    boolean fact_TrumfRead = false;
    String fact_Issuer_ID = "";
    // fuel
    private boolean fact_fuelTrx;
    boolean fact_FuelCard = false;
    // connection
    boolean fact_online = true;
    boolean fact_forcedOnline = false;
    // boolean fact_client_server_conn_proved = false;
    // boolean fact_client_POSserver_conn_proved = false;
    // boolean fact_serverconnection = false;
    // trx
    boolean fact_recurring = false;
    boolean fact_propertieResponse = false;
    boolean fact_abstractRequest = false;
    boolean fact_pinBypassCheck = false;
    boolean fact_completeResponse = false;
    boolean fact_receipt = false;
    int fact_approved = 0;// 0=unknown, 1=yes, 2=no
    String fact_completeEventReceived = "";
    // datadump
    boolean fact_state_DATA_DUMP_REQUESTED = false;
    boolean fact_StartDataDumpAction_started = false;
    boolean fact_StartDataDumpAction_done = false;
    boolean fact_TerminalFileHandler_started = false;
    // login
    boolean fact_XMLRequest = false;
    boolean fact_TCPconnection = false;
    boolean fact_CommCheck = false;
    // starting
    boolean fact_SettingsRead = false;
    // MobilePay
    // int fact_MP_activated = 0;
    private boolean fact_MobileTrx;
    boolean fact_MP_available = false;
    boolean fact_MP_checkin = false;
    boolean fact_MP_accept = false;
    boolean fact_MP_pay = false;
    public boolean fact_TrxComplete = false;
    boolean fact_AIDfound = false;
    boolean fact_usingImmidiatePrintEvents = false;
    HashMap events = new HashMap();
    // THEORY, possible scenarios, interpretations
    public String issues_first = "";
    public String issues_last = "";
    private String solutions = "";
    private boolean suspect_AID = false;
    private boolean suspect_key = false;
    private boolean suspect_PPS = false;
    private boolean suspect_Cancelcustomer = false;
    private boolean suspect_cancelTerminalsw = false;
    private boolean suspect_cancelPPCL = false;
    private boolean suspect_cancelECR = false;
    private boolean suspect_DeclinedTerminal = false;
    private boolean suspect_DeclinedHost = false;
    private boolean suspect_DeclinedServer = false;
    private boolean suspect_DeclinedClient = false;
    private LocalDateTime timePreviousLine;
    private LocalDateTime timeCurrentLine;
    // amounts
    int Amount_total = 0;
    int Amount_tip = 0;
    // database.script
    boolean trxSaved = false;
    @SuppressWarnings({ "javadoc" })
    public ObjectTransaction(Controller class_Controller) throws FileNotFoundException, UnsupportedEncodingException {
	System.out.println("ObjectTransaction");
	try {
	    this.class_Controller = class_Controller;
	    Progress = 0;
	    fact_fuelTrx = false;
	    fact_service = new Service();
	    fact_authmeth = new AUTHORIZATION_METHOD();
	    fact_TS = new TRANSACTION_STATUS();
	    fact_iDMeth = new ID_METHOD();
	    fact_OI = new ONLINE_INDICATOR();
	    fact_EntryMode = new POS_ENTRY_MODE();
	    fact_rspCode = new AUTHORIZATION_RESPONSE_CODE();
	    fact_approved = 0;
	    fact_cardEjected = false;
	    // result = "";
	    fact_issuerName = "";
	    issues_last = "";
	    solutions = class_Controller.newline;
	    // notes = "";
	    timePreviousLine = null;
	    Amount_total = 0;
	    Amount_tip = 0;
	    fact_completeEventReceived = "";
	} catch (Throwable e) {
	    class_Controller.PrintAction("An error occurred!");
	    StringWriter errors = new StringWriter();
	    e.printStackTrace(new PrintWriter(errors));
	    class_Controller.PrintAction(errors.toString());
	}
	System.out.println("ObjectTransaction. cardEject=" + this.fact_cardEjected);
    }
    public void saveSymptomStatus(String raw, String logFile) {
	System.out.println("saveSymptomStatus=" + raw);
	if (raw.contains("7:Avvist:")) {
	    // System.out.println("7-avvist A11=" + raw);
	}
	if (raw == null) {
	    try {
		throw new Exception("Null line received");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	// time
	String tempDate = raw;
	int aar = new Date().getYear() + 1900;
	int index = tempDate.indexOf(aar + "");
	if (index > 0) {
	    try {
		tempDate = tempDate.substring(index);
		try {
		    tempDate = tempDate.substring(0, tempDate.indexOf(" INFO"));
		} catch (Exception e) {}
		try {
		    tempDate = tempDate.substring(0, tempDate.indexOf(" ERROR"));
		} catch (Exception e) {}
		if (tempDate.indexOf(" WARN".toLowerCase()) > 0) {
		    tempDate = tempDate.substring(0, tempDate.indexOf(" WARN"));
		}
		if (tempDate.indexOf(",") > 0) {
		    tempDate = tempDate.substring(0, tempDate.indexOf(","));
		}
		if (tempDate.indexOf(".") > 0) {
		    tempDate = tempDate.substring(0, tempDate.indexOf("."));
		}
		/*
		 * //requires java 1.8! DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		 * LocalDateTime date = LocalDateTime.parse(tempDate, formatter); this.timeCurrentLine = date;
		 */
	    } catch (Throwable e) {
		e.printStackTrace();
		System.out.println("E " + tempDate);
	    }
	}
	if (raw.contains("logSystemInfo()") || raw.contains(".cpp")) {
	    // System.out.println("This is terminal logg A " + raw);
	    class_Controller.config.addParam("pppclient.log", "false");
	    class_Controller.config.addParam("debug.log", "true");
	} else if (raw.contains("com.pos.") || raw.contains("com.payex.")) {
	    class_Controller.config.addParam("pppclient.log", "true");
	    class_Controller.config.addParam("debug.log", "falsse");
	}
	if (raw.toLowerCase().contains("DefaultEventDispatcher:".toLowerCase())) {
	    // System.out.println("raw=" + raw);
	    if (raw.contains("complete-event received:")) {
		String tag = "complete-event received: ";
		String temp = raw.substring(raw.indexOf(tag));
		// System.out.println("temp=" + temp);
		temp = temp.substring(tag.length());
		fact_completeEventReceived = temp;
		// System.out.println("fact_completeEventReceived=" + fact_completeEventReceived);
		if (fact_completeEventReceived.contains("1708")) {
		    this.fact_service.value = "Loyalty cash in purchase (duplicate posreference)";
		}
	    }
	}
	if ((raw.contains("CorePosPayClientImpl: Request:")) || (raw.contains("new string api request:")) || (raw.contains("TRX Amount"))) {
	    fact_RequestReceived = true;
	}
	if (raw.contains("CARD_EXTENSIONS")) {
	    fact_fuelTrx = true;
	}
	if (raw.contains("DEBUG")) {// parser ignores these lines anyway
	    class_Controller.config.addParam("DEBUG logging", "true");
	}
	if (raw.contains("merchantIdEnabled=true")) {
	    class_Controller.config.addParam("merchantIdEnabled", "true");
	} else if (raw.contains("merchantIdEnabled=false")) {
	    class_Controller.config.addParam("merchantIdEnabled", "false");
	}
	if (raw.contains("fuelSplitPaymentAllowed=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("fuelSplitPaymentAllowed="));
	    temp = temp.substring("fuelSplitPaymentAllowed=".length());
	    if (temp.contains("true")) {
		class_Controller.config.addParam("fuelSplitPaymentAllowed", "true");
	    } else if (temp.contains("false")) {
		class_Controller.config.addParam("fuelSplitPaymentAllowed", "false");
	    }
	}
	if (raw.contains("forceItemSummaryList=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("forceItemSummaryList="));
	    temp = temp.substring("forceItemSummaryList=".length());
	    if (temp.contains("true")) {
		class_Controller.config.addParam("forceItemSummaryList", "true");
	    } else if (temp.contains("false")) {
		class_Controller.config.addParam("forceItemSummaryList", "false");
	    }
	}
	if (raw.contains("request: r0{")) {
	    class_Controller.config.addParam("PosPayService", "true");
	    String temp;
	    temp = raw.substring(raw.indexOf("request: r0{"));
	    temp = temp.substring("request: r0{".length());
	    temp = temp.substring(0, (temp.indexOf("}")));
	    fact_RequestReceived = true;
	    // System.out.println("request received: " + temp);
	    if (temp.contains("PU")) {
		fact_service.value = "Purchase";
	    }
	    if (temp.contains("PC")) {
		fact_service.value = "Purchase w Cashback";
	    }
	    if (temp.contains("RT")) {
		fact_service.value = "Return";
	    }
	    if (temp.contains("RV")) {
		fact_service.value = "Manual Reversal";
	    }
	    if (temp.contains("OR")) {
		fact_service.value = "Reconciliation";
	    }
	    if (temp.contains("RP")) {
		fact_service.value = "Report";
	    }
	    if (temp.contains("PA")) {
		fact_service.value = "PreAuth";
	    }
	    if (temp.contains("PJ")) {
		fact_service.value = "PreAuth Adjust";
	    }
	    if (temp.contains("CP")) {
		fact_service.value = "Close PreAuth";
	    }
	    if (temp.contains("GP")) {
		fact_service.value = "Get PreAuth";
	    }
	    if (temp.contains("DP")) {
		fact_service.value = "Delete PreAuth";
	    }
	    if (temp.contains("BA")) {
		fact_service.value = "Balance";
	    }
	    if (temp.contains("DE")) {
		fact_service.value = "Deposit";
	    }
	    if (temp.contains("WD")) {
		fact_service.value = "Withdrawal";
	    }
	    if (temp.contains("VE")) {
		fact_service.value = "Verification";
	    }
	    if (temp.contains("AC")) {
		fact_service.value = "Activation";
	    }
	    if (temp.contains("VA")) {
		fact_service.value = "Void Activation";
	    }
	    if (temp.contains("TRV")) {
		fact_service.value = "Technical Reversal";
	    }
	    if (temp.contains("CA")) {
		fact_service.value = "Cash";
	    }
	    if (temp.contains("GA")) {
		fact_service.value = "Get All PreAuths";
	    }
	    if (temp.contains("LD")) {
		fact_service.value = "Late Debit";
	    }
	    if (temp.contains("LC")) {
		fact_service.value = "Late Credit";
	    }
	    if (temp.contains("OP")) {
		fact_service.value = "Open PED";
	    }
	}
	if (raw.contains("ChainSequence")) {
	    class_Controller.config.addParam("PosPayService", "true");
	    String temp;
	    temp = raw.substring(raw.indexOf("<"));
	    temp = temp.substring("<".length());
	    temp = temp.substring(0, (temp.indexOf(" ChainSequence")));
	    fact_RequestReceived = true;
	    // System.out.println("request received: " + temp);
	    if (temp.contains("Debit")) {
		fact_service.value = "Purchase";
	    }
	}
	if (raw.contains("Running service ")) {
	    class_Controller.config.addParam("PosPayService", "true");
	    String temp;
	    temp = raw.substring(raw.indexOf("Running service "));
	    temp = temp.substring("Running service  ".length());
	    temp = temp.substring(0, (temp.indexOf("Service\" [")));
	    fact_RequestReceived = true;
	    if (temp.contains("purchaseService")) {
		fact_service.value = "Purchase";
	    }
	    fact_service.value = temp;
	    class_Controller.config.addParam("cardInTerminal", "true");
	    // }
	    if (temp.contains("receipt")) {
		class_Controller.config.addParam("cardInTerminal", "false");
	    }
	    System.out.println("Running service " + fact_service.value + ". cardEject=" + this.fact_cardEjected + " " + raw);
	} else if (raw.contains("PPP_HOME=")) {
	    fact_service.value = "Starting";
	    // fact_PPS_started = true;
	    class_Controller.config.addParam("PosPayService", "true");
	} else if (raw.contains("shutting down...")) {
	    fact_service.value = "Closing PosPayService";
	    this.Progress = 10;
	} else if (raw.contains("openPED called..")) {
	    fact_service.value = "OpenPED";
	}
	if (raw.contains("Update kernel amounts not updated, no amounts present")) {
	    fact_service.value = "OpenPED";
	    fact_RequestReceived = true;
	}
	if ((raw.contains("Initiating debit transaction")) || (raw.contains("Executing sale transaction"))) {
	    fact_service.value = "Purchase";
	    fact_RequestReceived = true;
	}
	if (raw.contains("Regular boot")) {
	    fact_service.value = "Restart";
	}
	if (raw.contains("Starting PosPay Client...")) {
	    fact_service.value = "Starting PosPayService";
	    class_Controller.config.addParam("Terminal connected in software", "false");
	}
	if (raw.contains("AUTHORIZATION_METHOD")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("AUTHORIZATION_METHOD	-	"));
	    temp = temp.substring("AUTHORIZATION_METHOD	-	".length());
	    if (temp.contains("receipt") == false) {
		fact_authmeth.value = temp;
	    }
	}
	if (raw.contains(",authorisationMethod=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf(",authorisationMethod="));
	    temp = temp.substring(",authorisationMethod=".length());
	    temp = temp.toLowerCase().substring(0, temp.indexOf(",idNumber".toLowerCase()));
	    temp = temp.toLowerCase().substring(0, 1);
	    fact_authmeth.value = temp;
	}
	if (raw.contains("TRANSACTION_STATUS")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("TRANSACTION_STATUS	-	"));
	    temp = temp.substring("TRANSACTION_STATUS	-	".length());
	    if (temp.contains("receipt") == false) {
		fact_TS.value = temp;
	    }
	}
	if (raw.contains("ID_METHOD")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("ID_METHOD	-	"));
	    temp = temp.substring("ID_METHOD	-	".length());
	    if (temp.contains("receipt") == false) {
		fact_iDMeth.value = temp;
	    }
	}
	if (raw.contains(",verificationMethod=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf(",verificationMethod="));
	    temp = temp.substring(",verificationMethod=".length());
	    temp = temp.substring(0, temp.indexOf(",cryptogram"));
	    fact_iDMeth.value = temp;
	}
	if (raw.contains("ONLINE_INDICATOR")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("ONLINE_INDICATOR	-	"));
	    temp = temp.substring("ONLINE_INDICATOR	-	".length());
	    if (temp.contains("receipt") == false) {
		fact_OI.value = temp;
	    }
	}
	if (raw.contains("Online/offline")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("Online/offline:			"));
	    temp = temp.substring("Online/offline:			".length());
	    if (temp.contains("receipt") == false) {
		fact_OI.value = temp;
	    }
	}
	if (raw.contains("Online/offline:			")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("Online/offline:			"));
	    temp = temp.substring("Online/offline:			".length());
	    if (temp.contains("receipt") == false) {
		fact_OI.value = temp;
	    }
	}
	if (raw.contains("9f998045		                                        ENTRY_MODE	-	")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("9f998045		                                        ENTRY_MODE	-	"));
	    temp = temp.substring("9f998045		                                        ENTRY_MODE	-	".length());
	    if (temp.contains("D")) {
		fact_EntryMode.value = "Magstripe";
	    } else if (temp.contains("C")) {
		fact_EntryMode.value = "CHIP";
	    } else if (temp.contains("K")) {
		fact_EntryMode.value = "Contactless";
	    } else if (temp.contains("L")) {
		fact_EntryMode.value = "Contactless";
	    } else if (temp.contains("-")) {
		fact_EntryMode.value = "None";
	    } else {
		fact_EntryMode.value = "Manual PAN entry";
	    }
	}
	if (raw.contains("Magnetic card data received")) {
	    fact_EntryMode.value = "Magstripe";
	    fact_CardDataResponse++;
	    fact_cardEntered = true;
	    class_Controller.config.addParam("cardInTerminal", "true");
	    this.Progress = 4;
	}
	if ((raw.contains("Chip card data received")) || (raw.contains("Chip card inserted")) || (raw.contains("Chip card is detected"))) {
	    fact_EntryMode.value = "CHIP";
	    fact_CardDataResponse++;
	    fact_cardEntered = true;
	    class_Controller.config.addParam("cardInTerminal", "true");
	    this.Progress = 4;
	}
	if (raw.contains("APPLICATION_ID")) {
	    fact_EntryMode.value = "CHIP";
	    // fact_CardDataResponse = true;
	    fact_cardEntered = true;
	    class_Controller.config.addParam("cardInTerminal", "true");
	    class_Controller.config.addParam("Terminal connected in software", "true");
	}
	if (raw.contains("Found AID definition")) {
	    this.fact_AIDfound = true;
	}
	if (raw.contains("TRACK2")) {
	    fact_EntryMode.value = "Magstripe";
	    // fact_CardDataResponse = true;
	    fact_cardEntered = true;
	    class_Controller.config.addParam("cardInTerminal", "true");
	    class_Controller.config.addParam("Terminal connected in software", "true");
	}
	if (raw.contains("AUTHORIZATION_RESPONSE_CODE")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("AUTHORIZATION_RESPONSE_CODE	-	"));
	    temp = temp.substring("AUTHORIZATION_RESPONSE_CODE	-	".length());
	    fact_rspCode.value = temp;
	}
	if (raw.contains("Host response:			")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("Host response:			"));
	    temp = temp.substring("Host response:			".length());
	    fact_rspCode.value = temp;
	}
	if (raw.contains("P39 Tag 10455591, Length 2, Value: ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("P39 Tag 10455591, Length 2, Value: "));
	    temp = temp.substring("P39 Tag 10455591, Length 2, Value: ".length());
	    fact_rspCode.value = temp;
	    if (temp.equals("58")) {
		addIssue("Rejected by server with 58", true, null);
		addSolution(
			"This transaction possibly contradicts SAT agreements.\nIn the case of illegal transactions, PayEx must pay the customer for these trx'es.\nWipe terminal, load it with the correct setup and then return it to customer.");
		suspect_DeclinedServer = true;
	    }
	}
	if (raw.contains("Initializing client...")) {
	    this.Progress = 10;
	}
	if (raw.contains("loginService is done")) {
	    fact_TrxComplete = true;
	    this.Progress = 10;
	}
	if (raw.contains("receiptService is done")) {
	    fact_TrxComplete = true;
	    this.Progress = 10;
	}
	if (raw.contains("getPropertiesService is done")) {
	    fact_TrxComplete = true;
	    this.Progress = 10;
	}
	if ((raw.contains("ISSUER_ID	-	83"))) {
	    fact_Issuer_ID = fact_Issuer_ID + "83" + " ";
	}
	if ((raw.contains("PAYMENT_APPLICATION_LABEL	-	euroShell"))) {
	    this.fact_FuelCard = true;
	}
	if ((raw.contains("Name=euroShell"))) {
	    this.fact_FuelCard = true;
	}
	if ((raw.contains("getPosPayClient returning result"))) {
	    class_Controller.config.addParam("PosPayService", "true");
	}
	if ((raw.contains("Magnetic card data received, performing validation "))) {
	    fact_EntryMode.value = "Magstripe";
	}
	if ((raw.contains("Issuing immediatePrintEvent"))) {
	    fact_usingImmidiatePrintEvents = true;
	}
	if ((raw.contains("ECR_TAG_CARD_TYPE_DIALOG	-	3"))) {
	    fact_fuelTrx = true;
	    fact_service.value = "Cash";
	}
	/*
	 * if ((raw.contains("MobileBeacon")) || (raw.contains("Mobile beacon")) || (raw.contains("asynchPurchase")) || (raw.contains(
	 * "dispatching event: [2"))) { fact_MobileTrx = true; }
	 */
	if (raw.contains("asyncPurchase.enabled=")) {
	    if (raw.contains("asyncPurchase.enabled=true")) {
		class_Controller.config.addParam("asyncPurchase.enabled", "true");
	    } else if ((raw.contains("asyncPurchase.enabled=false"))) {
		class_Controller.config.addParam("asyncPurchase.enabled", "false");
	    }
	}
	if (raw.contains("<Async")) {
	    class_Controller.config.addParam("asyncPurchase.enabled", "true");
	}
	if ((raw.contains("PropertiesResponse: TLV Tags")) || (raw.contains("Executing get terminal properties"))) {
	    fact_propertieResponse = true;
	    class_Controller.config.addParam("Terminal connection", "true");
	    class_Controller.config.addParam("Terminal connected in software", "true");
	    this.Progress = 1;
	    // System.out.println("PPCL connected to terminal "+raw);
	}
	if (raw.contains("AbstractRequest: TLV Tags")) {
	    fact_abstractRequest = true;
	}
	if ((raw.contains("Start ECR Get Card Info"))) {
	    fact_CardDataResponse++;
	}
	if (raw.contains("Executing command: ReadyToStartPinBypassDialog")) {
	    fact_pinBypassCheck = true;
	}
	if (raw.contains("CompleteResponse: TLV Tags")) {
	    fact_completeResponse = true;
	}
	if (raw.contains("Running service \"receiptService")) {
	    fact_receipt = true;
	}
	if (raw.contains("continuing to create receipt")) {
	    fact_receipt = true;
	}
	// results
	if (raw.contains("Received data from GPA")) {
	    fact_completeResponse = true;
	}
	if (raw.contains("[1708]")) {
	    fact_approved = 1;
	    fact_fuelTrx = true;
	}
	if (raw.contains("Approved:			YES")) {
	    fact_approved = 1;
	}
	if (raw.contains("TRUMF REGISTRERT")) {}
	if (raw.contains("responseTextLong=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("responseTextPrint="));
	    temp = temp.substring("responseTextPrint=".length());
	    temp = temp.substring(0, temp.indexOf(",responseTextHost"));
	    /*
	     * if ((!result.contains(temp)) && (!temp.contains("null"))) { result = result + "A" + temp + ", "; }
	     */
	}
	if (raw.contains("Status code")) {
	    // System.out.println("status code A " + raw);
	    String temp;
	    temp = raw.substring(raw.indexOf("Status code "));
	    // System.out.println("status code B " + temp);
	    temp = temp.substring("Status code ".length());
	    // System.out.println("status code C " + temp);
	    // temp = temp.substring(0, temp.indexOf(")"));
	    if (temp.contains("200")) {} else if (temp.contains("404")) {
		addIssue("404 not found", false, "Status code");
	    } else if (temp.contains("409")) {
		suspect_DeclinedClient = true;
		addIssue("Status code 409", false, "Status code");
		addSolution("Verify terminal is properly connected.");
		addSolution("Verify no service is running already.");
	    } else {
		String resultTemp = temp;
		// result = result + resultTemp + ", ";
	    }
	}
	if (raw.contains(" Event(")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("Event("));
	    temp = temp.substring("Event".length());
	    temp = temp.substring(0, temp.indexOf(")"));
	    if (temp.contains("(1 - ")) { // insert card
		fact_askedCard = true;
	    } else if (temp.equals("(3 - ")) { // enter pin
		fact_cardEntered = true;
		class_Controller.config.addParam("cardInTerminal", "true");
		Progress = 5;
	    } else if (temp.contains("(4 - ")) { // behandler
	    } else if (temp.contains("(11 - ")) { // fjern kort
	    } else if (temp.contains("118 - ")) { // autoriserer
		this.Progress = 6;
	    } else if (temp.contains("200 - ")) { // MP available
	    } else if (temp.contains("201 - ")) { // MP checked in
	    } else if (temp.contains("202 - ")) { // MP accepted
	    } else if (temp.contains("705 - ")) { // fjernet kort
		class_Controller.config.addParam("cardInTerminal", "false");
	    } else if (temp.contains("1527 - ")) { // duplicate
		addIssue(temp, true, " Event(");
		suspect_DeclinedClient = true;
	    } else {
		/*
		 * if ((result.contains(resultTemp))) { result = result + resultTemp + ", "; }
		 */
	    }
	}
	if (raw.contains("Eventid : ")) {// pppcomserver
	    String temp;
	    temp = raw.substring(raw.indexOf("Eventid : "));
	    temp = temp.substring("Eventid : ".length());
	    temp = temp.substring(0, temp.indexOf(" "));
	    // if ((!temp.equals("705")) && (!temp.equals("118")) && (!temp.equals("11")) && (!temp.equals("10")) && (!temp.equals("1"))) {
	    // System.out.println(fact_cardEntered+" "+temp);
	    if (temp.equals("1")) {// WAIT_CARD
		fact_askedCard = true;
		fact_RequestReceived = true;
		this.fact_service.value = "Transaction";
	    } else if (temp.equals("3")) {// WAIT_PIN_AND_VERIFY
		fact_cardEntered = true;
		class_Controller.config.addParam("cardInTerminal", "true");
		fact_iDMeth.value = "PIN";
		class_Controller.config.addParam("Terminal connected in software", "true");
	    } else if (temp.equals("4")) {// WAIT_RESPONSE
	    } else if (temp.equals("6")) {// WAIT_VERIFY
	    } else if (temp.equals("10")) {// USE_MAGNETIC_STRIPE_CARD
		fact_EntryMode.value = "Magstripe";
		class_Controller.config.addParam("Terminal connected in software", "true");
	    } else if (temp.equals("11")) {// REMOVE_CARD
		class_Controller.config.addParam("Terminal connected in software", "true");
	    } else if (temp.equals("19")) {// WAIT_TOTAL_AMOUNT_ENTRY
	    } else if (temp.equals("118")) {// AUTHORIZATION_IN_PROGRESS
		this.Progress = 6;
	    } else if (temp.equals("200")) {// MP available
	    } else if (temp.equals("201")) {// MP checked in
	    } else if (temp.equals("202")) {// MP accepted
	    } else if (temp.equals("705")) {
		class_Controller.config.addParam("cardInTerminal", "false");
	    } else if (temp.equals("999")) {// ingen hendelse
		addIssue(temp, false, "Eventid : ");
	    } else if (temp.equals("1005")) {} else if (temp.equals("1510")) {
		fact_approved = 2;
	    } else if (temp.equals("1511")) {
		fact_approved = 2;
	    } else if (temp.equals("1527")) {
		suspect_DeclinedClient = true;
	    } else if (temp.equals("1700")) {
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("1702")) {
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("1710")) {
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("1711")) {
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("1712")) {
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("1901")) { // rapport utført
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("1905")) {
		fact_approved = 1;
		fact_TrxComplete = true;
		this.Progress = 10;
	    } else if (temp.equals("4000")) {// receipt_Response
	    } else if (temp.equals("4002")) {// INPUT_REQUEST_RESPONSE
	    } else if (temp.equals("4003")) {// Inputdialog kansellert. Kan være forced signature
		addIssue(temp, false, "Eventid : ");
	    } else {
		addIssue(temp, false, "Eventid : ");
	    }
	    // }
	}
	if (raw.contains("Sending event. ")) {// DEBUG
	    String temp;
	    temp = raw.substring(raw.indexOf("Sending event. "));
	    temp = temp.substring("Sending event. ".length());
	    // temp = temp.substring(0, temp.indexOf("\""));
	    // System.out.println(fact_cardEntered+" "+temp);
	    if (temp.contains("Internal ID 0, external ID 3")) {// godkjent
		this.fact_approved = 1;
	    } else if (temp.equals("1")) {} else if (temp.contains("Internal ID 2, external ID 7")) {// rejected
		this.fact_approved = 2;
		this.suspect_DeclinedTerminal = true;
	    } else if (temp.contains("Internal ID 3, external ID 9")) {// tast pin
		fact_cardEntered = true;
		class_Controller.config.addParam("cardInTerminal", "true");
		Progress = 5;
	    } else if (temp.contains("Internal ID 5, external ID 11")) {// sett inn kort
		this.fact_askedCard = true;
	    } else if (temp.contains("Internal ID 8, external ID 14")) {// ta ut kort, vennligst vent. Helt normalt
	    } else if (temp.contains("Internal ID 9, external ID 14")) {// vennligst vent. Prosesserer CVM
	    } else if (temp.contains("Internal ID 9, external ID 16")) {// ta ut kort. Kunden blir bedt om å fjerne kort før det er scannet.
		this.suspect_DeclinedTerminal = true;
		fact_approved = 2;
	    } else if (temp.contains("Internal ID 9, external ID 27")) {// PIN bypass triggered by cardholder
		fact_iDMeth.value = "Forced signature";
	    } else if (temp.contains("Internal ID 10, external ID 16")) {// ta ut kort. Helt normalt
		// } else if (temp.contains("Internal ID 11, external ID 16")) {// ta ut kort
		// } else if (temp.contains("Internal ID 12, external ID 28")) {// ta ut kort
	    } else if (temp.contains("Internal ID 14, external ID 55")) {// avbrutt
		this.suspect_DeclinedTerminal = true;
	    } else if (temp.contains("Internal ID 17, external ID 218")) {// dra kort
		this.fact_askedCard = true;
	    } else if (temp.equals("39")) {// avbrutt
		fact_approved = 2;
	    } else if (temp.contains("Internal ID 19, external ID 705")) {// kort fjernet
		class_Controller.config.addParam("cardInTerminal", "false");
	    } else {
		// System.out.println(temp);
		try {
		    addIssue(temp, false, "Sending event. ");
		} catch (Exception e) {}
	    }
	    // }
	}
	if (raw.contains("complete-event received: ")) {
	    // System.out.println(raw);
	    String start = "complete-event received: ";
	    String temp;
	    temp = raw.substring(raw.indexOf(start));
	    temp = temp.substring(start.length());
	    // temp = temp.substring(0, temp.indexOf("]"));
	    // System.out.println("temp="+temp);
	    events.put(temp, true);
	    if (temp.contains("1005")) {// Inputdialog kansellert. Kan være forced signature
		suspect_DeclinedHost = true;
		fact_approved = 2;
	    } else if (temp.contains("1051")) {// Inputdialog kansellert. Kan være forced signature
		suspect_DeclinedHost = true;
		fact_approved = 2;
	    } else if (temp.contains("1510")) {
		if (!this.suspect_cancelECR) {
		    this.suspect_DeclinedTerminal = true;
		}
		fact_approved = 2;
	    } else if (temp.contains("1511")) {
		this.suspect_DeclinedTerminal = true;
		fact_approved = 2;
	    } else if (temp.contains("1530")) {
		this.suspect_DeclinedTerminal = true;
		fact_approved = 2;
	    } else if (temp.contains("1549")) {
		this.suspect_DeclinedTerminal = true;
		fact_approved = 2;
	    } else if (temp.contains("1702")) {
		fact_approved = 1;
	    } else if (temp.contains("1905")) {
		fact_approved = 1;
	    } else if (temp.contains("3003")) {} else if (temp.contains("3028")) {
		this.suspect_DeclinedTerminal = true;
		fact_approved = 2;
		addIssue("Terminal server connection fails", true, null);
		addSolution("Check terinal logs for cause of offline");
	    } else if (temp.contains("4003")) {// Inputdialog kansellert. Kan være forced signature
	    } else if ((!temp.contains("118")) && (!temp.contains("Event(11"))) {}
	    if (fact_iDMeth.value.toLowerCase().contains("signatur")) {
		// System.out.println("signatur=" + fact_iDMeth.value);
		if ((!temp.contains("1701")) && (!temp.contains("1702")) && (!temp.contains("1510"))) {
		    addIssue("CVM mismatch terminal and PPCL", true, null);
		}
	    }
	}
	if (raw.contains("Received event, ")) {// Received event, 3:Godkjent: med signatur
	    String start = "Received event, ";
	    String temp;
	    temp = raw.substring(raw.indexOf(start));
	    temp = temp.substring(start.length());
	    // temp = temp.substring(0, temp.indexOf("]"));
	    // System.out.println("A: "+raw);
	    try {
		int kode = Integer.parseInt(temp);
	    } catch (Exception e) {}
	    // non-problematic, for trx result
	    if ((!temp.contains("3:")) && (!temp.contains("9:")) && (!temp.contains("11:")) && (!temp.contains("13:"))
		    && (!temp.contains("14:")) && (!temp.contains("16:")) && (!temp.contains("Event(10 -")) && (!temp.contains("218:"))
		    && (!temp.contains("705:")) && (!temp.contains("832:"))) {}
	    // problematic
	    if ((temp.contains("705"))) {
		class_Controller.config.addParam("cardInTerminal", "false");
	    } else if (temp.contains("3003")) {
		addIssue(temp, false, null);
	    } else if (temp.contains("4003")) {// Inputdialog kansellert. Kan være forced signature
	    }
	    if (!issues_last.contains(temp)) {
		String issuesTemp = temp;
		if ((temp.contains("7:"))) {// avbryt fra terminal
		    // System.out.println("7-avvist=" + raw);
		    if (raw.contains("Ekthetskontroll på")) {
			issuesTemp = "Host says bad keys";
			addSolution("Verify that you are using test keys for test host, or prod keys for prod host.");
		    }
		    this.suspect_cancelTerminalsw = true;
		    // System.out.println("fact_service.value.indexOf(openPED)=" + fact_service.value.indexOf("openPED"));
		    if (this.fact_service.value.indexOf("openPED") > -1) {
			issuesTemp = "openPED uten 123=1 på terminal";
		    }
		    addIssue(issuesTemp, true, null);
		} else if (temp.contains("11:")) {// WAIT_CARD
		    fact_askedCard = true;
		} else if ((temp.contains("16:"))) {// ta ut kort
		    if (fact_approved != 2) {
			fact_cardEjected = true;
			// System.out.println("cardEjected " + raw);
		    }
		} else if ((temp.contains("832:"))) {
		    addIssue(issuesTemp, false, null);
		}
	    }
	}
	if (raw.contains("dispatching event: ")) {
	    String start = "dispatching event: ";
	    String temp;
	    temp = raw.substring(raw.indexOf(start));
	    temp = temp.substring(start.length());
	    temp = temp.substring(0, temp.indexOf(" ") + 1);
	    // System.out.println(raw);
	    // System.out.println(temp);
	    // non-problematic + covered, rest goes to trx result
	    if ((!temp.contains("[1]")) && (!temp.contains("[3]")) && (!temp.contains("[4]")) && (!temp.contains("[8]"))
		    && (!temp.contains("[10]")) && (!temp.contains("[11]")) && (!temp.contains("[24]")) && (!temp.contains("[30]"))
		    && (!temp.contains("[118]")) && (!temp.contains("[200]")) && (!temp.contains("[201]")) && (!temp.contains("[202]"))
		    && (!temp.contains("[204]")) && (!temp.contains("[272]")) && (!temp.contains("[705]")) && (!temp.contains("[832]"))
		    && (!temp.contains("[1055]")) && (!temp.contains("[1510]")) && (!temp.contains("[1712]")) && (!temp.contains("[3028]"))
		    && (!temp.contains("[4002]"))) {
		events.put(temp, true);
		/*
		 * if ((!issues.contains(temp)) && (!result.contains(temp))) { result = result + "" + temp + ", "; }
		 */
	    }
	    if ((temp.contains("[1]"))) {
		this.fact_askedCard = true;
		this.Progress = 3;
	    } else if ((temp.contains("[3]"))) {
		this.Progress = 5;
	    } else if ((temp.contains("[6]"))) {
		this.Progress = 5;
	    } else if ((temp.contains("[16]"))) {
		if (fact_approved != 2) {
		    fact_cardEjected = true;
		    // System.out.println("cardEjected " + raw);
		}
	    } else if ((temp.contains("[17]"))) {
		this.fact_BonusCardRead = true;
	    } else if ((temp.contains("[30]"))) {
		addIssue(temp, false, null);
	    } else if ((temp.contains("[118]"))) {
		this.Progress = 6;
	    } else if ((temp.contains("[121]"))) {
		fact_approved = 1;
	    } else if ((temp.contains("[122]"))) {
		addIssue("TMS update failed", false, null);
	    } else if ((temp.contains("[200]"))) { // MP available
		fact_MP_available = true;
	    } else if ((temp.contains("[201]"))) { // MP checkin
		fact_MP_checkin = true;
	    } else if ((temp.contains("[202]"))) { // MP accepted
		this.fact_MP_accept = true;
	    } else if ((temp.contains("1700"))) { // MP accepted
		fact_approved = 1;
		if (this.fact_MP_accept == true) {
		    this.fact_MP_pay = true;
		}
	    } else if ((temp.contains("1701"))) {
		fact_approved = 1;
	    } else if ((temp.contains("1702"))) {
		fact_approved = 1;
	    } else if ((temp.contains("1709"))) {
		fact_approved = 1;
	    } else if ((temp.contains("1711"))) {
		fact_approved = 1;
	    } else if ((temp.contains("1712"))) {
		fact_approved = 1;
	    } else if ((temp.contains("1900"))) {
		fact_approved = 1;
	    } else if ((temp.contains("1901"))) {
		fact_approved = 1;
	    }
	    // problematic
	    if ((temp.contains("204"))) {// MP teknisk feil
		addIssue("MobilePay login error [204]", true, null);
	    }
	    if ((temp.contains("705"))) {
		class_Controller.config.addParam("cardInTerminal", "false");
		class_Controller.config.addParam("Terminal connected in software", "true");
	    }
	    if ((temp.contains("826"))) {
		addSolution("Enter operator manu, support, file management, delete all offlines");
	    }
	    if (temp.contains("999")) {
		addIssue(temp, false, null);
	    }
	    if ((temp.contains("1062"))) {
		suspect_DeclinedTerminal = true;
		temp = "Service not allowed for this card";
		class_Controller.config.addParam("Terminal connected in software", "true");
		addIssue(temp, true, null);
	    }
	    if ((temp.contains("1055"))) {// wrong pin code, not a problem
		temp = "Wrong PIN code";
		addIssue(temp, false, null);
	    }
	    if ((temp.contains("1515"))) {
		suspect_DeclinedTerminal = true;
		class_Controller.config.addParam("Terminal connected in software", "true");
		temp = "Service not enabled for card";
		addIssue(temp, true, null);
	    }
	    if ((temp.contains("1527"))) {
		suspect_DeclinedClient = true;
		temp = "Duplicate trx";
		addIssue(temp, true, null);
	    }
	    if (temp.contains("3003")) {
		String issuesTemp = temp + " Unknown issuer";
		addIssue(issuesTemp, false, null);
	    }
	    if (temp.contains("3997")) {
		String issuesTemp = temp + " ppp.key inaccessible";
		addIssue(issuesTemp, false, null);
		addSolution("Fix terminal connection");
	    }
	    if (temp.contains("4003")) {// Inputdialog kansellert. Kan være forced signature. Ikke et problem
		// addIssue(temp + " dialog stoppet", false);
	    }
	    if (!issues_last.contains(temp)) { // events not specified
		String issuesTemp = temp;
		if ((temp.contains("832"))) {
		    addIssue(issuesTemp, false, null);
		} else if ((temp.contains("999"))) {
		    addIssue(issuesTemp, false, null);
		} else if ((temp.contains("1550"))) {
		    addIssue(issuesTemp, false, null);
		} else if ((temp.contains("3028"))) {
		    addIssue(issuesTemp, false, null);
		    addSolution("Verify terminal config for addresses");
		}
	    }
	}
	if (raw.contains("Status code ")) {// api_pppclient
	    String start = "Status code ";
	    String temp = raw.substring(raw.indexOf(start));
	    temp = temp.substring(start.length());
	    // temp = temp.substring(0, temp.indexOf("]"));
	    // System.out.println("Status code " + raw);
	    try {
		int kode = Integer.parseInt(temp);
	    } catch (Exception e) {}
	    // non-problematic, rest goes to trx result
	    if ((!temp.contains("3:")) && (!temp.contains("9:")) && (!temp.contains("11:")) && (!temp.contains("13:"))
		    && (!temp.contains("14:")) && (!temp.contains("16:")) && (!temp.contains("Event(10 -")) && (!temp.contains("218:"))
		    && (!temp.contains("705:")) && (!temp.contains("832:"))) {
		/*
		 * if ((!temp.contains(temp))) { result = result + "" + temp + ", "; }
		 */
		// System.out.println("B: " + raw);
	    }
	    // problematic
	    if ((temp.contains("705"))) {
		class_Controller.config.addParam("cardInTerminal", "false");
		class_Controller.config.addParam("Terminal connected in software", "true");
	    } else if (temp.contains("3003")) {
		String issuesTemp = temp + " Unknown issuer";
		addIssue(issuesTemp, false, null);
	    } else if (temp.contains("4003")) {// Inputdialog kansellert. Kan være forced signature
		addIssue(temp + " dialog stoppet", false, null);
	    }
	    if (!issues_last.contains(temp)) {
		String issuesTemp = temp;
		if ((temp.contains("7:"))) {// avbryt fra terminal
		    class_Controller.config.addParam("Terminal connected in software", "true");
		    this.suspect_cancelTerminalsw = true;
		    // System.out.println("fact_service.value.indexOf(openPED)=" + fact_service.value.indexOf("openPED"));
		    if (this.fact_service.value.indexOf("openPED") > -1) {
			issuesTemp = "openPED uten 123=1 på terminal";
		    }
		    if (issues_last.indexOf(issuesTemp) < 0) {
			addIssue(issuesTemp, false, null);
		    }
		} else if (temp.contains("11:")) {// WAIT_CARD
		    fact_askedCard = true;
		} else if ((temp.contains("832:"))) {
		    addIssue(issuesTemp, false, null);
		}
	    }
	}
	if (raw.toLowerCase().contains("transactionStateInformation".toLowerCase())) { // GPA, from terminal
										       // System.out.println("A " + raw);
	    String start = "transactionStateInformation(";
	    String temp;
	    temp = raw.substring(raw.indexOf(start));
	    // System.out.println("B " + temp);
	    temp = temp.substring(start.length());
	    // System.out.println("C " + temp);
	    // temp = temp.replaceAll(")", "");//funker ikke, stopper videre processing
	    // temp = temp.substring(0, temp.indexOf(",") + 1);
	    // System.out.println("D " + temp);
	    events.put(temp, true);
	    // non-problematic, rest goes to trx result
	    if ((!temp.contains("0,")) && (!temp.contains("1,")) && (!temp.contains("5,")) && (!temp.contains("6,"))
		    && (!temp.contains("7,")) && (!temp.contains("259,")) && (!temp.contains("272,")) && (!temp.contains("518,"))
		    && (!temp.contains("519,")) && (!temp.contains("1510,") && (!temp.contains("1552,")))) {
		/*
		 * if ((!issues.contains(temp)) && (!result.contains(temp))) { result = result + "" + temp + ", "; }
		 */
	    }
	    if ((temp.equals("0"))) {
		this.fact_askedCard = true;
	    }
	    if ((temp.contains("17"))) {
		this.fact_BonusCardRead = true;
	    }
	    // problematic
	    if ((temp.contains("263"))) {
		suspect_DeclinedTerminal = true;
	    }
	    if ((temp.contains("487"))) { // kunden tok ut kort før trx er ferdig
		class_Controller.config.addParam("cardInTerminal", "false");
		String issuesTemp = "Kort tatt ut for tidlig (" + temp;
		addIssue(issuesTemp, true, null);
		this.suspect_Cancelcustomer = true;
	    }
	    if ((temp.contains("705"))) {
		class_Controller.config.addParam("cardInTerminal", "false");
		class_Controller.config.addParam("Terminal connected in software", "true");
	    }
	    if ((temp.contains("826"))) {
		addSolution("Enter operator manu, support, file management, delete all offlines");
	    }
	    if ((temp.contains("1062"))) {}
	    if ((temp.contains("1510"))) {}
	    if ((temp.contains("1515"))) {
		String resultTemp = "Service not enabled for card";
		suspect_DeclinedTerminal = true;
		class_Controller.config.addParam("Terminal connected in software", "true");
		/*
		 * if (!(result.contains(temp))) { result = resultTemp + result; }
		 */
	    }
	    if ((temp.contains("1552"))) {}
	    if (temp.contains("3003")) {
		String issuesTemp = temp + " Unknown issuer";
		addIssue(issuesTemp, false, null);
		suspect_DeclinedTerminal = true;
		class_Controller.config.addParam("Terminal connected in software", "true");
	    }
	    if (temp.contains("4003")) {// Inputdialog kansellert. Kan være forced signature
	    }
	    if (!issues_last.contains(temp)) { // events not specified
		String issuesTemp = temp;
		if ((temp.contains("832"))) {
		    addIssue(issuesTemp, false, null);
		} else if ((temp.contains("1550"))) {
		    addIssue(issuesTemp, false, null);
		} else if ((temp.contains("3028"))) {
		    addIssue(issuesTemp, false, null);
		    addSolution("Verify addresses in terminal config");
		}
	    }
	}
	if (raw.contains("Sending event. Internal ID ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("Sending event. Internal ID "));
	    temp = temp.substring("Sending event. Internal ID ".length());
	    temp = temp.substring(raw.indexOf("external ID "));
	    temp = temp.substring("external ID ".length());
	    temp = temp.substring(0, temp.indexOf(","));
	    if (temp.contains("11")) { // insert card
		fact_askedCard = true;
	    } else {}
	}
	// end results
	if (!this.fact_completeResponse) {
	    if (raw.contains("ISSUER_ID	-	")) {
		try {
		    String temp;
		    temp = raw.substring(raw.indexOf("ISSUER_ID	-	"));
		    temp = temp.substring("ISSUER_ID	-	".length());
		    fact_Issuer_ID = fact_Issuer_ID + Integer.parseInt(temp) + " ";
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	if (raw.contains("Found card issuer")) {
	    // System.out.println(raw);
	    try {
		String temp = raw;
		temp = temp.substring(temp.indexOf("Name="));
		temp = temp.substring("Name=".length());
		// temp = temp.substring(0, temp.indexOf(","));
		String issuername = temp.substring(0, temp.indexOf(","));
		if (!fact_issuerName.contains(issuername)) {
		    fact_issuerName = fact_issuerName + issuername + " ";
		}
		temp = temp.substring(temp.indexOf("AcquirerIssuerId="));
		temp = temp.substring("AcquirerIssuerId=".length());
		temp = temp.substring(0, temp.indexOf("]"));
		// System.out.println(temp);
		fact_Issuer_ID = fact_Issuer_ID + Integer.parseInt(temp) + " ";
		// System.out.println("fact_Issuer_ID "+fact_Issuer_ID);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	if (raw.contains("CardIssuer [")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("CardIssuer ["));
	    temp = temp.substring("CardIssuer [".length());
	    // fact_Issuer_ID = Integer.parseInt(temp.substring(0, temp.indexOf(",")));
	    temp = temp.substring(temp.indexOf("Name="));
	    temp = temp.substring(0, temp.indexOf(","));
	    String issuername = temp;
	    if (!fact_issuerName.contains(issuername)) {
		fact_issuerName = fact_issuerName + issuername + " ";
	    }
	}
	if (raw.contains(",cardIssName=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf(",cardIssName="));
	    temp = temp.substring(",cardIssName=".length());
	    temp = temp.substring(0, temp.indexOf(",cardNo="));
	    String issuername = temp;
	    if (!fact_issuerName.contains(issuername)) {
		fact_issuerName = fact_issuerName + issuername + " ";
	    }
	}
	if (fact_issuerName == "") { // CompleteResponse kommer ofte 2 ganger
	    if (raw.contains("PAYMENT_APPLICATION_NAME")) {
		String temp;
		temp = raw.substring(raw.indexOf("PAYMENT_APPLICATION_NAME	-	"));
		temp = temp.substring("PAYMENT_APPLICATION_NAME	-	".length());
		String issuername = temp;
		if (!fact_issuerName.contains(issuername)) {
		    fact_issuerName = fact_issuerName + issuername + " ";
		}
	    }
	}
	// status
	if (raw.contains("REQUEST_SENT") || raw.contains("Done executing command: SendRequest")) {
	    this.fact_REQUEST_SENT = true;
	    this.Progress = 2;
	}
	if (raw.contains("CardDataResponse: TLV Tags")) {
	    this.fact_cardEntered = true;
	    this.fact_CardDataResponse++;
	    class_Controller.config.addParam("cardInTerminal", "true");
	    this.Progress = 4;
	    class_Controller.config.addParam("Terminal connected in software", "true");
	    // System.out.println(raw);
	}
	if ((raw.contains("[1]")) || (raw.contains("Sending event. Internal ID 5, external ID 11"))) {//
	    this.fact_askedCard = true;
	}
	if (raw.contains("Transaction exceeds floor limit")) {
	    this.fact_forcedOnline = true;
	}
	if (raw.contains("startPosPayService sleeping")) {
	    class_Controller.config.addParam("PosPayService", "true");
	}
	if (raw.contains("pinBeforeAmount=")) {
	    if (raw.contains("pinBeforeAmount=false")) {
		class_Controller.config.addParam("pospay.client.pinBeforeAmount", "false");
	    } else {
		class_Controller.config.addParam("pospay.client.pinBeforeAmount", "true");
	    }
	}
	if ((raw.contains("AUTHORIZATION_RESPONSE_CODE")) || (raw.toLowerCase().contains("connected to "))) {
	    if (!(raw.contains("Z3")) && !(raw.contains("Z1"))) {
		fact_terminal_server_connection = true;
	    }
	}
	if (raw.contains("ResponseCode from server: ")) {// terminal conn?
	    // fact_client_server_conn_proved = true;
	    fact_OI.value = "Online";
	}
	if (raw.contains("Host response:")) {// terminal conn?
	    // fact_client_server_conn_proved = true;
	    fact_OI.value = "Online";
	}
	if (raw.contains("Could not connect to Host")) {
	    fact_OI.value = "Offline";
	    if (this.fact_service.value.contains("Flush")) {
		this.fact_approved = 2;
	    }
	}
	if (raw.contains("Failed connecting")) {
	    fact_OI.value = "Offline";
	}
	if (raw.contains("cardExtensions.enabled=")) {
	    if (raw.contains("cardExtensions.enabled=false")) {
		class_Controller.config.addParam("cardExtensions.enabled", "false");
	    } else {
		class_Controller.config.addParam("cardExtensions.enabled", "true");
	    }
	}
	if (raw.contains("strictTLVEncoding=")) {
	    if (raw.contains("strictTLVEncoding=false")) {
		class_Controller.config.addParam("strictTLVEncoding", "false");
	    } else {
		class_Controller.config.addParam("strictTLVEncoding", "true");
	    }
	}
	if (raw.contains("pinBypass.enabled=")) {
	    if (raw.contains("pinBypass.enabled=false")) {
		class_Controller.config.addParam("pospay.client.pinBypass.enabled", "false");
	    } else {
		class_Controller.config.addParam("pospay.client.pinBypass.enabled", "true");
	    }
	}
	if (raw.contains("supportsAddPrint=")) {
	    if (raw.contains("supportsAddPrint=false")) {
		class_Controller.config.addParam("pospay.client.supportsAddPrint", "false");
	    } else {
		class_Controller.config.addParam("pospay.client.supportsAddPrint", "true");
	    }
	}
	if (raw.contains("PropertiesResponse: TLV Tags")) {
	    class_Controller.config.addParam("cardInTerminal", "false");
	    class_Controller.config.addParam("Terminal connection", "true");
	    class_Controller.config.addParam("Terminal connected in software", "true");
	}
	if (raw.contains("Accepting connections..")) {
	    class_Controller.config.addParam("Terminal connection", "true");
	    class_Controller.config.addParam("Terminal connected in software", "true");
	}
	if (raw.contains("Socket connecting")) {
	    class_Controller.config.addParam("Terminal connection", "true");
	    class_Controller.config.addParam("Terminal connected in software", "true");
	}
	if (raw.contains("Connected to ")) {
	    // class_Controller.config.addParam("Terminal connection", "true");
	}
	if (raw.contains("Handling: Loyalty card")) {
	    this.fact_BonusCardRead = true;
	}
	if (raw.contains("[1709]")) {
	    this.fact_BonusCardRead = true;
	}
	if (raw.contains("The transaction was ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("The transaction was "));
	    temp = temp.substring("The transaction was ".length());
	    String issuesTemp = temp;
	    if (temp.toLowerCase().contains("not") || temp.toLowerCase().contains("declined")) {
		// System.out.println("Rejected " + raw);
		// kommer også når cardholder kansellerer
		this.suspect_cancelTerminalsw = true;
	    } else if (temp.toLowerCase().contains("approved")) {
		// System.out.println("Approved " + raw);
		fact_approved = 1;
	    }
	}
	if (raw.contains("TAG_TRANSACTION_DECLINED")) {
	    fact_approved = 2;
	    suspect_DeclinedTerminal = true;
	    addIssue("Declined in " + lastEMVStep.toLowerCase(), true, null);
	}
	if ((fact_approved == 1) && raw.contains("[emv] EMVDC step: ")) {
	    String temp = raw.substring(raw.indexOf("[emv] EMVDC step: "));
	    temp = temp.substring("[emv] EMVDC step: ".length());
	    temp = temp.substring(0, temp.indexOf("TAG"));
	    this.lastEMVStep = temp;
	}
	if (raw.contains("card inserted")) {
	    fact_cardEntered = true;
	}
	if (raw.contains("Read entry: Tag 80, Length ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("Value: "));
	    temp = temp.substring("Value: ".length());
	    String issuername = temp;
	    if (!fact_issuerName.contains(issuername)) {
		fact_issuerName = fact_issuerName + issuername + " ";
	    }
	    // System.out.println("fact_issuerName=" + fact_issuerName);
	    if (fact_issuerName.contains("Trumf")) {
		fact_TrumfRead = true;
		fact_BonusCardRead = true;
	    }
	}
	if (raw.contains("Chip card inserted")) {
	    fact_EntryMode.value = "Chip";
	}
	if (raw.contains("PPP address 2: FAILED")) {// (not a problem)
						    // result = result + "PPP address 2 failed (not a problem), ";
	}
	if (raw.contains(".telium.")) {
	    class_Controller.config.addParam("Terminal type", "Mynt");
	}
	if (raw.contains(".gpa.")) {
	    class_Controller.config.addParam("Terminal type", "GPA");
	}
	if (raw.contains("Payment software version")) {
	    class_Controller.config.addParam("Terminal type", "Mynt");
	    this.fact_service.value = "Startup";
	}
	if (raw.contains("INSERT INTO TRANSACTIONS VALUES(")) {
	    if (!trxSaved) {
		this.trxSaved = true;
	    } else {
		addIssue("Duplicate transaction saved", true, null);
	    }
	}
	if (raw.contains("Async transaction started")) {
	    this.fact_MobileTrx = true;
	}
	if (raw.contains("Cancel is invoked")) {// terminal
	    if (!this.suspect_DeclinedTerminal) {
		this.suspect_Cancelcustomer = true;
		addIssue("Canceled by card holder", true, null);
	    }
	}
	if (raw.contains("Done parsing request frame")) {// terminal
	    fact_RequestReceived = true;
	}
	if (raw.contains("Executing ") && (!raw.contains("client"))) {// terminal
	    String temp;
	    temp = raw.substring(raw.indexOf("Executing "));
	    temp = temp.substring("Executing ".length());
	    this.fact_service.value = temp;
	}
	if (raw.contains("Connecting to server with address: ")) {// terminal
	    String temp;
	    temp = raw.substring(raw.indexOf("Connecting to server with address: "));
	    temp = temp.substring("Connecting to server with address: ".length());
	    class_Controller.config.addParam("Server address", temp);
	}
	if (raw.toLowerCase().contains("Param ".toLowerCase())) {
	    // System.out.println("A " + raw);
	    if (raw.toLowerCase().contains("Param found. Tag ".toLowerCase())) {
		// System.out.println("B " + raw);
		String param;
		param = raw.substring(raw.indexOf("Param found. Tag "));
		param = param.substring("Param found. Tag ".length());
		param = param.substring(0, param.indexOf(","));
		String value;
		value = raw.substring(raw.indexOf("value "));
		value = value.substring("value ".length() + 1);
		value = value.substring(0, value.length() - 1);
		// System.out.println("E param=" + param + " value=" + value);
		// , Terminal zone, used in PosPay Direct communication
		String descr = "";
		if (param.equals("3")) {
		    descr = " (Disable ACKing ECR packets)";
		    if (value.equals("0")) {
			value = value + " (Enabled)";
		    } else if (value.equals("1")) {
			value = value + " (Disabled)";
		    }
		} else if (param.equals("4")) {
		    descr = " (Disable awaiting ECR ACKS)";
		    if (value.equals("0")) {
			value = value + " (Enabled)";
		    } else if (value.equals("1")) {
			value = value + " (Disabled)";
		    }
		} else if (param.equals("28")) {
		    descr = " (Currency code. 578=no, 752=se, 208=dk)";
		} else if (param.equals("35")) {
		    // param = param + ", Terminal zone, used in PosPay Direct communication";
		} else if (param.equals("36")) {
		    descr = " (Terminal ID)";
		} else if (param.equals("43")) {
		    descr = " (Acquiring inst. ident.)";
		} else if (param.equals("63")) {
		    descr = " (Host address)";
		    if (value.equals("test-pospaydirecttx.payex.com")) {
			value = value + " (Default, test, WAN)";
		    } else if (value.equals("195.225.28.97")) {
			value = value + " (SSL, LAN SSL)";
		    } else if (value.equals("195.225.0.42")) {
			value = value + " (VPN, LAN VPN, Serial)";
		    } else if (value.equals("1.1.1.1")) {
			value = value + " (USB non-SSL)";
		    } else if (value.equals("test-pospaytx.payex.com")) {
			value = value + " (SSL)";
		    }
		} else if (param.equals("64")) {
		    if (value.equals("443")) {
			value = value + " (SSL, LAN SSL)";
		    } else if (value.equals("4413")) {
			value = value + " (SSL)";
		    } else if (value.equals("9034")) {
			value = value + " (Default, VPN, LAN VPN, Serial, USB, WAN)";
		    } else if (value.equals("9036")) {
			value = value + " (iWL non-SSL, non-VPN)";
		    } else if (value.equals("9048")) {
			value = value + " (LAN)";
		    } else {
			value = value + "<-- (Uncommon port!)";
		    }
		} else if (param.equals("71")) {
		    descr = " (Supervisor password.)";
		} else if (param.equals("74")) {
		    descr = " (Enable tips)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("77")) {
		    descr = " (Enable cents)";
		    if (value.equals("0")) {
			value = value + " (Off) (Default)";
		    } else if (value.equals("1")) {
			value = value + " (On) (iWL)";
		    }
		} else if (param.equals("78")) {
		    descr = " (Enable VAT)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On) (iWL)";
		    }
		} else if (param.equals("86")) {
		    descr = " (Enable manual PAN entry)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("89")) {
		    descr = " (??)";
		} else if (param.equals("100")) {
		    descr = " (Host connection type)";
		    if (value.equals("0")) {
			value = value + " (Ethernet) (Default, LAN, LAN SSL, SSL, USB, VPN)";
		    } else if (value.equals("1")) {
			value = value + " (GPRS) (iWL, LAN, SSL, VPN)";
		    } else if (value.equals("2")) {
			value = value + " () (Serial)";
		    } else {
			value = value + " (Abnormal value!)";
		    }
		} else if (param.equals("109")) {
		    descr = " (ECR mode)";
		    if (value.equals("0")) {
			value = value + " (Off) (Default, Test, SSL, VPN)";
		    } else if (value.equals("1")) {
			value = value + " (Deprecated) (Serial)";
		    } else if (value.equals("2")) {
			value = value + " (PPP) (USB)";
		    } else if (value.equals("3")) {
			value = value + " (Ethernet) (LAN SSL, LAN VPN)";
		    } else {
			value = value + " (Abnormal value!)";
		    }
		} else if (param.equals("110")) {
		    descr = " (TMS port)";
		    if (value.equals("7003")) {
			value = value + " (USB, iWL)";
		    } else if (value.equals("7005")) {
			value = value + " (LAN SSL, SSL)";
		    } else if (value.equals("9046")) {
			value = value + " (Default)";
		    } else if (value.equals("9048")) {
			value = value + " (LAN VPN, Serial, VPN, WAN)";
		    } else {
			value = value + " (Abnormal value!)";
		    }
		} else if (param.equals("111")) {
		    descr = " (TMS IP)";
		    if (value.equals("1.1.1.1")) {
			value = value + " (USB)";
		    } else if (value.equals("91.208.214.34")) {
			value = value + " (LAN SSL, SSL)";
		    } else if (value.equals("195.225.0.45")) {
			value = value + " (Default, LAN VPN, Serial, VPN, WAN)";
		    } else {
			value = value + " (Abnormal value!)";
		    }
		} else if (param.equals("120")) {
		    descr = " (Disable fallback)";
		    if (value.equals("0")) {
			value = value + " (Allowed)";
		    } else if (value.equals("1")) {
			value = value + " (Disallowed)";
		    }
		} else if (param.equals("123")) {
		    descr = " (Allow openPED)";
		    if (value.equals("0")) {
			value = value + " (Disallowed)";
		    } else if (value.equals("1")) {
			value = value + " (Allowed)";
		    }
		} else if (param.equals("125")) {
		    descr = " (ECR comm. interface)";
		    if (value.equals("COM0")) {
			value = value + " (Serial)";
		    } else if (value.equals("COM5")) {
			value = value + " (Default, SSL)";
		    }
		} else if (param.equals("133")) {
		    descr = " (Sending events to ECR)";
		    if (value.equals("0")) {
			value = value + " (Off) (iWL)";
		    } else if (value.equals("1")) {
			value = value + " (On) (Default)";
		    }
		} else if (param.equals("134")) {
		    descr = " (Disable offline fallback)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("138")) {
		    descr = " (??)";
		    if (value.equals("3")) {
			value = value + " (iWL)";
		    }
		} else if (param.equals("140")) {
		    descr = " (Autoselect AID)";
		} else if (param.equals("141")) {
		    descr = " (Receipts. 0=Give merchant, ask customer. 1=CVM=signature=merchant, ask customer. 2=CVM=signature=merchant, alwways give customer on refund)";
		    if (value.equals("0")) {
			value = value + " (Give merchant, ask customer)";
		    } else if (value.equals("1")) {
			value = value + " (CVM=signature=merchant, ask customer)";
		    } else if (value.equals("2")) {
			value = value + " (CVM=signature=merchant, alwways give customer on refund)";
		    }
		} else if (param.equals("142")) {
		    descr = " (TCP timeout)";
		    if (value.equals("3")) {
			value = value + " (USB non-SSL, WAN)";
		    } else if (value.equals("10")) {
			value = value + " (SSL)";
		    } else if (value.equals("30")) {
			value = value + " (Default)";
		    }
		} else if (param.equals("143")) {
		    descr = " (Timeout sending to host)";
		} else if (param.equals("144")) {
		    descr = " (DHCP)";
		    if (value.equals("0")) {
			value = value + " (Off) (LAN SSL, LAN VPN, VPN)";
		    } else if (value.equals("1")) {
			value = value + " (On) (Default, SSL)";
		    } else if (value.equals("2")) {
			value = value + " (User-controlled) (Default, SSL)";
		    }
		} else if (param.equals("145")) {
		    descr = " (Max no_cvm amount)";
		} else if (param.equals("150")) {
		    descr = " (Host SSL)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("153")) {
		    descr = " (Manual PAN for value codes)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("156")) {
		    descr = " (Host receiving timeout)";
		    if (value.equals("50")) {
			value = value + " (Test, default, SSL)";
		    } else if (value.equals("30")) {
			value = value + " (LAN SSL, LAN VPN, Serial, USB non-SSL, )";
		    }
		} else if (param.equals("157")) {
		    descr = " (Use salesReport file)";
		} else if (param.equals("166")) {
		    descr = " (Disable logging)";
		    if (value.equals("0")) {
			value = value + " (Logging)";
		    } else if (value.equals("1")) {
			value = value + " (No logging)";
		    }
		} else if (param.equals("168")) {
		    descr = " (Beep after a transaction)";
		} else if (param.equals("173")) {
		    descr = " (Enable ECR preliminary response)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("174")) {
		    descr = " (Manually activate/deactivate header and footer when using custom background)";
		} else if (param.equals("178")) {
		    descr = " (Timeout for ECR State Machine states)";
		} else if (param.equals("180")) {
		    descr = " (Timeout for entering/swiping card)";
		} else if (param.equals("187")) {
		    descr = " (Max offline transactions)";
		} else if (param.equals("188")) {
		    descr = " (Disable multiple users. 0=multiple users, 1=single user)";
		} else if (param.equals("206")) {
		    descr = " (Enable/Disable transaction list)";
		} else if (param.equals("211")) {
		    descr = " (PIN bypass\force signature)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("213")) {
		    descr = " (Recceipt standard. 0=CHAOI, 1=OTRS)";
		} else if (param.equals("222")) {
		    descr = " (Enable Fuel)";
		    if (value.equals("0")) {
			value = value + " (Off)";
		    } else if (value.equals("1")) {
			value = value + " (On)";
		    }
		} else if (param.equals("223")) {
		    descr = " (Timeout displaying 'list input' selection on non-goal terminals)";
		} else if (param.equals("224")) {
		    descr = " (PayEx TMS IP)";
		    if (value.equals("1.1.1.1")) {
			value = value + " (SSL)";
		    } else if (value.equals("82.115.146.56")) {
			value = value + " (LAN, LAN SSL, LAN VPN, Serial)";
		    } else if (value.equals("172.25.129.20")) {
			value = value + " (Default)";
		    } else if (value.equals("195.225.28.104")) {
			value = value + " (SSL)";
		    } else if (value.equals("test-pospaydirecttx.payex.com")) {
			value = value + " (Default)";
		    } else {
			value = value + " (Abnormal value!)";
		    }
		} else if (param.equals("225")) {
		    descr = " (PayEx TMS port)";
		    if (value.equals("443")) {
			value = value + " (Niklas TMS)";
		    } else if (value.equals("8080")) {
			value = value + " (Default)";
		    } else if (value.equals("9010")) {
			value = value + " (SSL)";
		    } else {
			value = value + " (Abnormal value!)";
		    }
		} else if (param.equals("253")) {
		    descr = " (Enable TLV-encoding)";
		} else if (param.equals("255")) {
		    descr = " (Don't send EMV approval offline msg's)";
		    if (value.equals("0")) {
			value = value + " (For PPCL <=4.8)";
		    }
		} else if (param.equals("506")) {
		    if (value.equals("0")) {
			value = value + " (Test, SSL)";
		    } else if (value.equals("1")) {
			value = value + " (Prod Non-SSL)";
		    }
		}
		class_Controller.config.addParam("Param " + param, value + descr);
	    }
	}
	if (raw.contains("AMOUNT_AUTHORIZED	-	")) {
	    String tag = "AMOUNT_AUTHORIZED	-	";
	    String temp = raw.substring(raw.indexOf(tag));
	    temp = temp.substring(tag.length());
	    this.Amount_total = Integer.parseInt(temp);
	}
	if (raw.contains("tipAmount=")) {
	    String tag = "tipAmount=";
	    String temp = raw.substring(raw.indexOf(tag));
	    temp = temp.substring(tag.length());
	    temp = temp.substring(0, temp.indexOf(","));
	    if (temp.equals("null")) {
		temp = "0";
	    }
	    this.Amount_tip = Integer.parseInt(temp);
	}
	if (raw.contains("TimeoutOptimizedSocketAccess: Connected")) {
	    class_Controller.config.addParam("PPP address 1", "true");
	}
	if (raw.contains("Connected to ")) {
	    class_Controller.config.addParam("PPP address 1", "true");
	    if (raw.contains("82.115.146.56:9034")) {
		class_Controller.config.addParam("82.115.146.56:9034", "true");
	    } else if (raw.contains("91.208.214.34:7005")) {
		class_Controller.config.addParam("91.208.214.34:7005", "true");
	    } else if (raw.contains("91.208.214.34:7007")) {
		class_Controller.config.addParam("91.208.214.34:7007", "true");
	    } else if (raw.contains("192.168.243.103:9039")) {
		class_Controller.config.addParam("192.168.243.103:9039", "true");
	    } else if (raw.contains("195.225.28.97:443")) {
		class_Controller.config.addParam("195.225.28.97:443", "true");
	    } else if (raw.contains("195.225.28.97:4412")) {
		class_Controller.config.addParam("195.225.28.97:4412", "true");
	    } else if (raw.contains("195.225.0.42:9034")) {
		class_Controller.config.addParam("195.225.0.42:9034", "true");
	    }
	}
	if (raw.contains("Connecting to server with address: ")) {
	    if (raw.contains("195.225.28.97")) {
		class_Controller.config.addParam("195.225.28.97", "false");
	    }
	}
	if (raw.contains("Successfully started socket at: ")) {
	    if (raw.contains("1.1.1.2:5188")) {
		class_Controller.config.addParam("1.1.1.2:5188", "true");
	    }
	}
	if (raw.contains("Accepted connection from")) {
	    if (raw.contains("pospaytx.payex.com:443")) {
		class_Controller.config.addParam("pospaytx.payex.com:443", "true");
	    } else if (raw.contains("91.208.214.34:7005")) {
		class_Controller.config.addParam("91.208.214.34:7005", "true");
	    } else if (raw.contains("pospaytx.payex.com:4412")) {
		class_Controller.config.addParam("pospaytx.payex.com:4412", "true");
	    } else if (raw.contains("1.1.1.2")) {
		class_Controller.config.addParam("1.1.1.2", "true");
	    }
	}
	if (raw.contains("Sending XML request")) {
	    this.fact_XMLRequest = true;
	}
	if (raw.contains("Accepting connections..")) {
	    if (raw.contains("91.208.214.34:7005")) {
		class_Controller.config.addParam("91.208.214.34:7005", "true");
	    } else if (raw.contains("pospaytx.payex.com:443")) {
		class_Controller.config.addParam("pospaytx.payex.com:443", "true");
	    } else if (raw.contains("pospaytx.payex.com:4412")) {
		class_Controller.config.addParam("pospaytx.payex.com:4412", "true");
	    }
	    if (this.fact_service.value.contains("login")) {
		this.fact_TCPconnection = true;
	    }
	}
	if (raw.contains("Communication check:")) {
	    if (this.fact_service.value.contains("login")) {
		this.fact_CommCheck = true;
	    }
	}
	if (raw.contains("com.pos.pospayinterface.CorePosPayClientProxy:")) {
	    if (this.fact_service.value.contains("starting")) {
		this.fact_SettingsRead = true;
	    }
	}
	if (raw.contains("Executing command: Cancel")) {
	    if (!this.suspect_cancelECR) {
		this.suspect_cancelPPCL = true;
	    }
	}
	if (raw.contains("Starting HttpTerminalFileHandler")) {
	    this.fact_TerminalFileHandler_started = true;
	}
	if (raw.contains("STATE CHANGE: Changed state to ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("STATE CHANGE: Changed state to "));
	    temp = temp.substring("STATE CHANGE: Changed state to ".length());
	    temp = temp.substring(0, temp.indexOf(" state"));
	    if (temp.equals("DATA_DUMP_REQUESTED")) {
		this.fact_state_DATA_DUMP_REQUESTED = true;
	    } else if (temp.equals("DATA_DUMP_REQUESTED")) {
		this.fact_state_DATA_DUMP_REQUESTED = true;
	    } else {}
	}
	if (raw.contains("***** Executing command: ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("***** Executing command: "));
	    temp = temp.substring("***** Executing command: ".length());
	    temp = temp.substring(0, temp.indexOf(" *****"));
	    if (temp.equals("StartDataDumpAction")) {
		this.fact_StartDataDumpAction_started = true;
	    } else {}
	}
	if (raw.contains("***** Done executing command: ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("***** Done executing command: "));
	    temp = temp.substring("***** Done executing command: ".length());
	    temp = temp.substring(0, temp.indexOf(" *****"));
	    if (temp.equals("StartDataDumpAction")) {
		this.fact_StartDataDumpAction_done = true;
	    } else {}
	}
	if (raw.contains("CorePosPayClientImpl: MobileBeacon Id: ")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("CorePosPayClientImpl: MobileBeacon Id: "));
	    temp = temp.substring("CorePosPayClientImpl: MobileBeacon Id: ".length());
	    try {
		long activated = Long.parseLong(temp);
		class_Controller.config.addParam("MobileBeacon Id", activated + "");
	    } catch (Exception e) {
		e.printStackTrace();
		addIssue("MobilePay beacon not working", true, null);
		addSolution("Replace MobilePay beacon");
	    }
	}
	if (raw.contains("trxDisplayEnterCard")) {
	    this.fact_askedCard = true;
	}
	if (raw.contains("Klarte ikke å gå online")) {
	    this.fact_online = false;
	}
	if (raw.contains("Releasing for offline sending")) {
	    if (this.fact_service.value == null) {
		fact_service.value = "Flushing";
	    }
	}
	if (raw.contains("The transaction was successfully sent and response was received from host")) {
	    if (this.fact_service.value == null) {
		fact_service.value = "Flushing";
	    }
	}
	if (raw.contains("Requesting force online")) {
	    fact_forcedOnline = true;
	}
	if (raw.contains("INPUT_PIN_ON: Perform online PIN validation")) {
	    this.Progress = 5;
	}
	if (raw.contains("Sending event. Internal ID 7, external ID 13")) {
	    this.Progress = 6;
	}
	if (raw.contains("c:\\PPPclient")) {
	    this.ECR = "Storepoint";
	}
	if (raw.contains("EMSP\\BANK\\PosPay")) {
	    this.ECR = "Elguide";
	}
	// issues. Order is decided by occurence in log
	if (raw.contains("Premature end of file")) {
	    addIssue("Premature end of file", true, null);
	    addSolution("Feil i server. Gi Bjørnar tidspunkt for feilen, merchantID og hvilket miljø det skjedde i (prod/dev/test).");
	    addSolution("Reinstallering kan hjelpe.");
	    addSolution("Sjekk at det ikke er installert Test-klient i Prod-miljø eller motsatt.");
	}
	if (raw.contains("PIN entry aborted (by ECR or user pulled card)")) {
	    addIssue("User cancelled trx", true, null);
	    suspect_Cancelcustomer = true;
	    suspect_cancelECR = true;
	    fact_approved = 2;
	}
	if (raw.contains("problem reading ")) {
	    addIssue("Problem reading card", true, null);
	}
	if (raw.contains("No matching type definition found")) {
	    // System.out.println("fact_Issuer_ID="+fact_Issuer_ID);
	    if (!this.fact_Issuer_ID.equals("1 ") && !this.fact_Issuer_ID.equals("5 ")) {
		addIssue("Client lacking type definitions to recognize Fuel cards", true, null);
		addSolution("Verify agreements in SAT");
		addSolution("Trigger TMS update");
		addSolution("Delete database.script+database.log and restart PosPayService");
	    }
	}
	if (raw.contains("Setting transaction errorType: ")) {
	    String tag = "Setting transaction errorType: ";
	    String temp = raw.substring(raw.indexOf(tag));
	    temp = temp.substring(tag.length());
	    // System.out.println(raw);
	    if (temp.equals("3")) {
		this.suspect_Cancelcustomer = true;
		addIssue("Canceled by card holder", true, null);
	    } else {
		addIssue("setting transaction errorType: " + temp, true, null);
	    }
	    this.Amount_tip = Integer.parseInt(temp);
	}
	if (raw.contains("The terminal is now locked, reason: 02-58")) {
	    addIssue("Terminal locked from bad server response", true, null);
	    addSolution("This transaction possibly contradicts SAT agreements");
	}
	if (raw.contains("An offline transaction was declined by the host")) {
	    addIssue("Transaction declined by host", true, null);
	    addSolution("Verify merchant agreements");
	}
	if (raw.contains("peer not authenticated")) {
	    addIssue("Improper SSL certificate in keystore", true, null);
	    addSolution("Import new certification chain");
	}
	if (raw.contains("No async payment host found")) {
	    addIssue("MobilePay missing in SAT", true, null);
	    addSolution("Set up MobilePay agreement in SAT");
	}
	// client startup issues
	if (raw.contains("Failed to initialize client proxy")) {
	    addIssue("Failed to start client", false, null);
	    addSolution("Possibly temporary. Try again.");
	    addSolution("Try copy-pasting the data-folder from an installation that works");
	    addSolution("Verify that PosPay/Sw/Java jars are not corrupt");
	    addSolution("Verify ppp.key is valid");
	}
	if (raw.contains("(VersionHandler.java:18)")) {
	    addIssue("Failed to start client", false, null);
	    addSolution("Possibly temporary. Try again.");
	    addSolution("Try copy-pasting the data-folder from an installation that works");
	    addSolution("Verify that PosPay/Sw/Java jars are not corrupt");
	    addSolution("Verify ppp.key is valid");
	}
	// terminal connection issues
	if ((raw.contains("Could not initialize ServerSocket")) || (raw.contains("Failed to connect to pospay service"))
		|| (raw.contains("Cannot communicate with terminal")) || (raw.contains("Login/ping failed"))
		// || (raw.contains("connect timed out"))
		|| (raw.contains("jvm_bind")) || (raw.contains("Failed to connect to terminal"))
		// || (raw.contains("Socket is closed"))
		|| (raw.contains("Connection refused: connect")) || (raw.contains("Failed to download security settings for "))
		|| (raw.contains("Process failed, received status: 150"))) {
	    // class_Controller.config.addParam("Terminal connection", "false");
	    suspect_DeclinedClient = true;
	    addSolution("If not already tried, check if PcPos gets the same issue.");
	    addSolution("Try restarting PosPayService. Check logs for communication failures");
	    if (class_Controller.config.getParam("Terminal connected in software") == "true") {
		addIssue("Client lost connection to terminal", true, null);
		addSolution("Verify physical terminal connection");
	    } else {
		addIssue("Client lacks connection to terminal", true, null);
		addSolution("Verify Routing and Remote Access is running properly");
		addSolution("Verify IPV6 is deleted");
		addSolution("Verify terminal connection settings");
		addSolution("Verify config.properties does not have typos");
		addSolution("Verify local com port settings");
		addSolution("Try reinstalling USB drivers");
		addSolution("Verify network settings");
		addSolution("Verify terminal ECR mode is PPP");
	    }
	}
	if (raw.contains("Failed to download security settings for ")) {
	    addSolution("Possibly caused by blocked ports. Check for connect timed out's");
	    addSolution("Possibly outdated certificate. Check for 'Failed to validate client certificate'");
	}
	if (raw.contains("Executing command: CommunicationBroke")) {
	    if (fact_completeEventReceived.equals("")) {
		class_Controller.config.addParam("Terminal connection", "false");
		if (this.isTransaction()) {
		    if (this.fact_approved != 1) {
			addIssue("CommunicationBroke - PosPayService lost connection to terminal", true, null);
		    }
		}
	    }
	}
	//
	if (raw.contains("LinkLayer code -1002")) {
	    addIssue("SSL certificates failing", true, null);
	    this.fact_terminal_server_connection = false;
	    addSolution("Terminal must be swiped and reloaded");
	    addSolution("Load SSL certificates on host disk");
	    addSolution("Set correct password on param 155");
	}
	// Network issues
	if ((raw.contains("Failed to send AdditionalData")) || (raw.contains("Failed to connect to socket"))
		|| (raw.contains("CommunicationException"))) {
	    addIssue("Could not connect to server", true, null);
	    addSolution("Verify DNS settings");
	    addSolution("Verify network settings");
	}
	if ((raw.contains("UnknownHostException: pospay.payex.com")) || (raw.contains("unknown host"))) {
	    addIssue("Could not connect to server", true, null);
	    addSolution("Put in an entry to config.properties asking the client to use a specific IP-adress instead");
	    addSolution("Verify host address");
	    addSolution("Verify DNS settings");
	    addSolution("Verify network settings");
	}
	if (raw.contains("Could not connect to Host")) {
	    addIssue("Could not connect to host", true, null);
	    this.fact_terminal_server_connection = false;
	}
	if (raw.contains("Communication problems when sending offline transaction")) {
	    addIssue("Flushing failed", false, null);
	    this.fact_terminal_server_connection = false;
	}
	if (raw.contains("ServerAccessException")) {
	    addIssue("Could not connect to server", true, null);
	    this.fact_terminal_server_connection = false;
	}
	if (raw.contains("TimeoutOptimizedSocketAccess.java:147")) {
	    addIssue("Could not connect to server", true, null);
	    this.fact_terminal_server_connection = false;
	}
	if (raw.contains("Socket is closed")) {
	    // problem avhenger av andre feil, kan være enten terminal eller server conn.
	}
	if (raw.contains("Failed to read from stream")) {
	    addIssue("Protel TCP fail", true, null);
	}
	if (raw.contains("HttpEventProxy: Unspecified error")) {
	    String issuesTemp = "Failed to start HTTP client";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("Two ECRs are open!")) {
	    // System.out.println(logFile);
	    if (!logFile.contains("api_")) {
		String issuesTemp = "Two ECRs are open!b";
		addIssue(issuesTemp, true, null);
		addSolution("Do not open a new instance of ECR while one is already running!");
	    }
	}
	if (raw.contains("Could not retrieve URL for class path resource ")) {
	    if (raw.contains("Could not retrieve URL for class path resource [pospaywan.payex.com")) {
		String issuesTemp = "pospaywan.payex.com unavailable";
		suspect_DeclinedClient = true;
		addIssue(issuesTemp, true, null);
		addSolution("Host file needs to have pospaywan.payex.com added");
	    } else {
		String issuesTemp = "Address unavailable";
		suspect_DeclinedClient = true;
		addIssue(issuesTemp, true, null);
		addSolution("Host file needs to have an address added");
	    }
	}
	if (raw.contains("Unable to establish loopback connection")) {
	    String issuesTemp = "Loopback failed";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, true, null);
	    addSolution("Host file might need to have pospaywan.payex.com added");
	}
	if ((raw.contains("Unable to use spring beans, might not be able to correctly initialize"))
		|| (raw.contains("LifecycleProcessor not initialized"))) {
	    String issuesTemp = "Datastore blocked (Unable to use spring beans)";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	    addSolution("Verify that TMS ports are not blocked");
	    addSolution("Verify that relay ports are not blocked");
	    addSolution("Verify that PPP.key is correct");
	    addSolution("Data-folder could be corrupt. Try copy-pasting form an installation that works");
	    addSolution("Verify that terminal ID matches SAT setup");
	    addSolution("Host file might need to have pospaywan.payex.com added");
	}
	if (raw.contains("Transaction was rejected due to a communication error")) { // kan skyldes kansellering etter kort før pin
	    suspect_DeclinedClient = true;
	}
	if (raw.contains("Detected unexpected EOF while reading response")) {
	    suspect_DeclinedClient = true;
	    addIssue("Connection issues", false, null);
	}
	if (raw.contains("Failed to connect to pospay service within timeout")) {
	    suspect_DeclinedClient = true;
	    addIssue("Connection issues", false, "Failed to connect to pospay service within timeout");
	    addSolution("Check connection settings");
	    addSolution("Verify PPP.key is valid");
	}
	if (raw.contains("AsyncStatusResponse") && raw.contains("CANCELED")) {
	    String issuesTemp = "MP timed out";
	    suspect_DeclinedHost = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("transactionStateInformation") && this.fact_service.value.contains("receipt")) {
	    String issuesTemp = "Terminal and client out of sync";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("TSI indicating an error ")) { // (not a problem)
	    String issuesTemp = "TSI indicating an error ";
	    addIssue(issuesTemp, true, null);
	    addSolution("Check logs for missing salt keystore (will be logged on client startup)");
	    addSolution("Possibly defect chip scanner");
	}
	if (raw.contains("Transaction was rejected due to a communication error. (code 8. online.)")) {
	    String issuesTemp = "Bad card usage";
	    addIssue(issuesTemp, true, null);
	    addSolution("Don't insert card before(or after) you have declined MobilePay");
	}
	if (raw.contains("AsyncResponse cannot be cast to com.payex.pospay.client.data.model.AsyncStatusResponse")) {
	    String issuesTemp = "AsyncResponse cannot be cast to AsyncStatusResponse";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	    addSolution("MobilePay might not be enabled for terminal in SAT");
	    addSolution("MobilePay might be started with a cless card on top of terminal");
	}
	if (raw.contains("Failed to get Async status") || raw.contains("AsyncPurchaseService.polling(")) {
	    String issuesTemp = "MobilePay missing in datastore";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, true, "Failed to get Async status");
	    addSolution("MobilePay must be enabled for terminal in SAT");
	}
	if (raw.contains("Unable to save issuerId")) {
	    String issuesTemp = "Unable to save issuerId to datastore";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("PosPay is busy: Another service is already running")) {
	    String issuesTemp = "PosPay is busy";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("ClassCastException")) {
	    String issuesTemp = "ClassCastException";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Failed to stop service")) {
	    String issuesTemp = "Failed to stop service";
	    addIssue(issuesTemp, false, "Failed to stop service");
	}
	if (raw.contains("NullPointerException")) {
	    String issuesTemp = "NullPointerException";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains(",FALSE,FALSE,NULL,NULL,NULL,'0')")) {
	    String issuesTemp = "Null transaction saved";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Given final block not properly padded")) {
	    String issuesTemp = "Bad key used for deciphering";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("Updating security settings, keystore is missing")) {
	    String issuesTemp = "Keystore is missing";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("Failed validating ecr field 9f990e")) {
	    String issuesTemp = "Terminal rejected PAN";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, "Failed validating ecr field 9f990e");
	    addSolution("Likely mis-entered manual PAN");
	}
	if (raw.contains("Received event, 825:")) {
	    String issuesTemp = "Terminal rejected command";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	    addSolution("Terminal and client possibly out of sync");
	    addSolution("Possibly invalid trx data sent to terminal");
	    addSolution("Possibly incompatible data format - Try upgrading the client to 4.17.0");
	}
	if (raw.contains("external ID 825,")) {
	    String issuesTemp = "Terminal rejected command";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	    addSolution("Terminal and client are not in sync");
	}
	if (raw.contains("Failed to login")) {
	    String issuesTemp = "Failed to login";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, "Failed to login");
	    addSolution("Possibly invalid SSL setup");
	}
	if (raw.contains("Failed to get response(reversal required)")) {
	    String issuesTemp = "Failed to get response(reversal required)";
	    addIssue(issuesTemp, false, "Failed to get response(reversal required)");
	    addSolution("Possibly invalid SSL setup");
	}
	if (raw.contains("No such terminal ")) {
	    String issuesTemp = "No such terminal ";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	    addSolution("Possibly invalid SSL setup");
	}
	if (raw.contains("Error in printing TLVTags. null")) {
	    String issuesTemp = "TLV tags invalid!";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("AuthorizationMethod do not contain the value")) {
	    String issuesTemp = "AuthorizationMethod invalid!";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.toLowerCase().contains("status_err_feature_not_supported")) {
	    String issuesTemp = "Terminal says The request attempted to invoke unsupported functionality";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.toLowerCase().contains("status_card_reject_expired")) {
	    String issuesTemp = "Terminal says 'Expired card'";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_card_pci_pan_forbidden")) {
	    String issuesTemp = "Terminal says A PCI PAN is not allowed for this type of transaction (used with \"purchase with reference number\")";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_card_reject_use_chip")) {
	    String issuesTemp = "Terminal says 'Chip capable card swiped. Use chip to continue'";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.toLowerCase().contains("status_card_reject_bin_forbidden")) {
	    String issuesTemp = "Terminal says 'Record was not found from BINCSV for swiped card'";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("status_card_reject_pan_mod10")) {
	    String issuesTemp = "Terminal says 'PAN Mod10 (Luhn) check failed'";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("status_card_reject_pan_length")) {
	    String issuesTemp = "Terminal says 'PAN length doesn't match BINCSV PAN fit criterias'";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_card_reject_track2mask")) {
	    String issuesTemp = "Terminal says 'track2 masking filter rejected the card'";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_card_reject_atm_only")) {
	    String issuesTemp = "Terminal says card service codes indicate it is for ATM only";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_card_reject_notrecognized")) {
	    String issuesTemp = "Terminal says card track data not readable";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_bax_configuration")) {
	    String issuesTemp = "Terminal says BAX configuration error - Crypto not set up correctly";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_rev_transaction_missing")) {
	    String issuesTemp = "Terminal says Transaction to reverse is missing. Reversal transaction failed to proceed";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_fallback_offline_not_allowed")) {
	    String issuesTemp = "Terminal says Fallback in offline is not allowed";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_fallback_timeout")) {
	    String issuesTemp = "Terminal says Fallback timed out";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_fallback_cancelled")) {
	    String issuesTemp = "Terminal says Fallback was aborted by user";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_fallback_not_allowed")) {
	    String issuesTemp = "Terminal says Chip failed. Fallback was not allowed";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_comm_failed")) {
	    String issuesTemp = "Terminal says Communication failure";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, "status_err_comm_failed");
	}
	if (raw.toLowerCase().contains("status_err_bbs_rejected")) {
	    String issuesTemp = "Terminal says Host rejected transaction";
	    suspect_DeclinedHost = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_card")) {
	    String issuesTemp = "Terminal says Card errors: Card read failed";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_timeout")) {
	    String issuesTemp = "Terminal says No activity. Transaction aborted";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.toLowerCase().contains("status_err_bax_reject")) {
	    String issuesTemp = "Terminal says BAX rejected transaction";
	    suspect_DeclinedHost = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_user_abort")) {
	    if (this.fact_approved != 2) {
		String issuesTemp = "Terminal says User aborted transaction";
		if (!this.suspect_cancelECR) {
		    this.suspect_Cancelcustomer = true;
		    addIssue(issuesTemp, true, null);
		}
	    }
	}
	if (raw.toLowerCase().contains("status_err_format")) {
	    String issuesTemp = "Terminal says Request format error";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.toLowerCase().contains("status_err_parameter")) {
	    String issuesTemp = "Terminal says Parameter error";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("STATUS_CARD_REJECT_SERVICE_FORBIDDEN")) {
	    String issuesTemp = "Terminal says BIN.CSV does not allow that service for this card. May also be bad card.";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	    addSolution("Could be a bad card");
	    addSolution("If card is bonus card, the reason could be 'Could not retrieve CardExtension from database' (bad SAT settings)");
	}
	if ((raw.contains("STATUS_CARD_REJECT_BINCSV_CONFIGURE")) || (raw.contains("Parameter/format/BINCSV failure (status 50)"))) {
	    String issuesTemp = "Terminal BIN.CSV error";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	    solutions = solutions + "Fix BIN.CSV" + class_Controller.newline;
	}
	if (raw.contains(".floorLimitCheck(")) {
	    String issuesTemp = "Amount is above offline floor limit";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Card is not supported ")) {
	    String issuesTemp = "Card is not supported ";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("TSI indicating an error")) {
	    String issuesTemp = "TSI indicating an error";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("No AID definition found for AID:")) {
	    fact_AIDfound = false;
	    String issuesTemp = "No AID definition found";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("There seems to be something wrong with the frame numbers")) {
	    String issuesTemp = "Mismatch mellom sendt data og data fra pinpad";
	    addIssue(issuesTemp, false, null);
	    this.solutions = solutions + " - Check logs for missing salt keystore (will be logged at client startup)"
		    + class_Controller.newline;
	}
	if ((raw.contains("(534,")) && !(raw.contains("INSERT INTO "))) {
	    String issuesTemp = "Terminal is blocked";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	    this.solutions = solutions + " - Terminal must be unblocked" + class_Controller.newline;
	}
	if (raw.contains("Failed to start PosPayService")) {
	    String issuesTemp = "Failed to start PosPayService";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, true, "Failed to start PosPayService");
	}
	if (raw.contains("Transaction was rejected due to a communication error.")) {
	    String issuesTemp = "Online communication error - Possible ECR cancel";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("unable to execute listener-method")) {
	    String issuesTemp = "unable to execute listener-method ";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if ((raw.contains("(499,")) && (!raw.contains("INSERT INTO"))) {
	    String issuesTemp = "449 . Teknisk feil";
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("PaymentController Exception, trying to continue")) {
	    String issuesTemp = "PaymentController Exception";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("FAILURE TO UPDATE PAYMENT TERMINAL, CLIENT WILL NOT BOOT")) {
	    String issuesTemp = "Failed to update terminal - client cannot boot";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, false, "FAILURE TO UPDATE PAYMENT TERMINAL, CLIENT WILL NOT BOOT");
	    this.solutions = solutions + " - Check terminal cabels' connections" + class_Controller.newline
		    + " - Try changing terminal cabels" + class_Controller.newline + " - Verify power to terminal"
		    + class_Controller.newline + " - Verify com ports for terminal, try on a different computer" + class_Controller.newline
		    + " - Remove all magnetic radiation" + class_Controller.newline + " - Verify keys" + class_Controller.newline
		    + " - Verify PED-updater working properly";
	}
	if (raw.contains("Error creating bean with name ")) {
	    if (raw.contains("Error creating bean with name 'gpaAccess'")) {
		String issuesTemp = "Bean 'secureSocketFactory' issues";
		addIssue(issuesTemp, false, null);
		addSolution("Kunden har et com-usb adapter och com-port numret har ändrat sig sedan omstart av pc");
		addSolution("Kontrollera kabeln - pröva att byt kabel med en annan fungerande terminal");
		addSolution("Kontrollera strömförsörjningen - pröva att byt strömförsörjning med en annan fungerande terminall");
		addSolution("Kontrollera kassans com-port - testa en annan terminal på kassan och se om det fungerar");
		addSolution(
			"Se till att EM-strålning inte påverkar. - Stäng av utrustning som kan generera EM strålning, t.eks stöld larm, radar osv");
	    }
	    if (raw.contains("Error creating bean with name 'secureSocketFactory'")) {
		String issuesTemp = "Bean 'secureSocketFactory' issues";
		addIssue(issuesTemp, false, null);
	    }
	    if (raw.contains("Error creating bean with name 'sslProtocolSupport'")) {
		String issuesTemp = "Bean 'sslProtocolSupport' issues";
		addIssue(issuesTemp, false, null);
	    }
	    if (raw.contains("Error creating bean with name 'keyStoreManager'")) {
		String issuesTemp = "keyStoreManager issues";
		addIssue(issuesTemp, false, null);
		addSolution("Check that SSL certificates are renewed automatically");
		addSolution("Terminal may need replacement");
	    }
	    if (raw.contains("Error creating bean with name 'terminalSettingsHandler'")) {// årsak må sees i "caused by"
		// suspect_AID = true;
		class_Controller.config.addParam("PosPayService", "false");
		suspect_DeclinedClient = true;
		addSolution("Might be caused by issues in database.script/log");
		addSolution("Could be caused by something else in the data-folder. Try copy-pasting from an installation that works.");
		addSolution("Could be an invalid ppp.key");
		addSolution("Double-check that merchant ID and terminal ID is correct for the key folder.");
		addSolution("Mulig feil med en avtale i SAT");
		addSolution("Make sure you are not running a prod install in dev or vice versa, because that won't work.");
		addSolution("Something is wrong on the server. Contact Bjørnar with timestamp, merchant ID and environment");
	    }
	    if (raw.contains("'clientKeyStore'")) {
		suspect_DeclinedClient = true;
		if (raw.contains("Failed to load keys")) {
		    addIssue("Keystore unavailable", true, null);
		}
	    }
	    if (raw.contains("Error creating bean with name 'secureSocketFactory'")) {
		String issuesTemp = "Bean 'secureSocketFactory' issues";
		addIssue(issuesTemp, false, null);
		addSolution("här behöver kliente reinstalleras");
	    }
	    if (raw.contains("Error creating bean with name 'defaultCardExtensionsHandler'")) {
		String issuesTemp = "Bean 'secureSocketFactory' issues";
		addIssue(issuesTemp, false, null);
		addSolution("Something is wrong on the server. Contact Bjørnar with timestamp, merchant ID and environment");
		addSolution("Reinstalling the client might help.");
		addSolution("Check that you're not running Test-client in Prod environment or vice versa.");
	    }
	}
	if (raw.contains("Unknown error while attempting payment terminal software update")) {
	    String issuesTemp = "Could not update terminal software";
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Failed to validate client certificate, certificate is expired")) {
	    String issuesTemp = "SSL certificate outdated";
	    addIssue(issuesTemp, true, "Failed to validate client certificate");
	    addSolution("Terminal may need replacement");
	}
	if (raw.contains("Failed to initialise PPPCOMServer")) {
	    String issuesTemp = "Failed to initialise PPPCOMServer";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("No PosPayClient is found")) {
	    String issuesTemp = "No PosPayClient is found";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("No string encryptor registered for hibernate with name \"hibernateStringEncryptor")) {
	    String issuesTemp = "hibernateStringEncryptor error";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Failed to get transaction")) {
	    String issuesTemp = "Failed to get previous transaction";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Failed to check if previous transaction was completed")) {
	    String issuesTemp = "Failed to check previous trx";
	    addIssue(issuesTemp, false, null);
	    addSolution("Check database.script for offline transactions");
	}
	if (raw.contains("threads could not be stopped")) {
	    String issuesTemp = "Threads could not be stopped";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Invalid JavaBean property 'triggers'")) {// (not a problem)
	    String issuesTemp = "Invalid JavaBean property (not a problem)";
	}
	if (raw.contains("Salt keystore does not exist.")) {
	    String issuesTemp = "Salt keystore does not exist";
	    suspect_DeclinedClient = true;
	    suspect_DeclinedTerminal = true;
	    addIssue(issuesTemp, true, null);
	    addSolution("Client must be reinstalled. If that does not work, the terminal must be replaced");
	}
	if (raw.contains("Fatal transport error")) {
	    String issuesTemp = "Fatal transport error";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	    addSolution("Check logs for missing salt keystore (will be logged at client startup)");
	}
	if (raw.contains("Unable to find ATE with STAN:")) {
	    String issuesTemp = "Unable to find ATE with STAN";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	    addSolution("Check logs for missing salt keystore (will be logged at client startup)");
	}
	if (raw.contains("Could not retrieve CardExtension from database")) { // card is not Fuel-card
	    if (this.fact_Issuer_ID.contains("80")) {
		addIssue("Trumf not recognised!", false, null);
	    }
	}
	if (raw.contains("pospay.client.address=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("pospay.client.address="));
	    temp = temp.substring("pospay.client.address=".length());
	    if (temp.contains("pospaywan.payex.com:9024")) {
		temp = temp + " (default prod)";
	    } else if (temp.contains("pospaywan.payex.com:9010")) {
		temp = temp + " (prod address2)";
	    } else if (temp.contains("pospay.payex.com:443")) {
		temp = temp + " (prod SSL)";
	    } else if (temp.contains("seutv362as.utvnet.net:9010")) {
		temp = temp + " (new test)";
	    } else if (temp.contains("test-pospaydirecttx.payex.com:9010")) {
		temp = temp + " (old test)";
	    } else if (temp.contains("192.168.243.100:9010")) {
		temp = temp + " (default dev)";
	    } else if (temp.contains("195.225.28.97")) {
		temp = temp + " (LAN SSL)";
	    } else {
		temp = temp + " (uncommon address)";
	    }
	    class_Controller.config.addParam("pospay.client.address", temp);
	}
	if (raw.contains("configDownloadAddress=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("configDownloadAddress="));
	    temp = temp.substring("configDownloadAddress=".length());
	    if (temp.contains("pospaywan.payex.com:9010")) {
		temp = temp + " (default prod)";
	    } else if (temp.contains("test-pospaydirecttx.payex.com:9010")) {
		temp = temp + " (default test)";
	    } else if (temp.contains("192.168.243.101:9010")) {
		temp = temp + " (default dev)";
	    } else if (temp.contains("pospay.payex.com:443")) {
		temp = temp + " (prod SSL)";
	    }
	    class_Controller.config.addParam("pospay.client.configDownloadAddress", temp);
	}
	if (raw.contains("relay.host.dst.addr=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("relay.host.dst.addr="));
	    temp = temp.substring("relay.host.dst.addr=".length());
	    if (temp.contains("pospaytx.payex.com")) {
		temp = temp + " (default prod/SSL)";
	    } else if (temp.contains("test-pospaydirecttx.payex.com")) {
		temp = temp + " (default test)";
	    } else if (temp.contains("195.225.0.42")) {
		temp = temp + " (unknown use case)";
	    } else {
		temp = temp + " (uncommon address)";
	    }
	    class_Controller.config.addParam("pospay.client.relay.host.dst.addr", temp);
	}
	if (raw.contains("relay.host.ssl.address=")) {
	    String temp;
	    temp = raw.substring(raw.indexOf("relay.host.ssl.address="));
	    temp = temp.substring("relay.host.ssl.address=".length());
	    if (temp.contains("test-pospaytx.payex.com")) {
		temp = temp + " (default test/SSL)";
	    } else if (temp.contains("pospaytx.payex.com")) {
		temp = temp + " (default prod)";
	    } else {
		temp = temp + " (uncommon address)";
	    }
	    class_Controller.config.addParam("pospay.client.relay.host.ssl.address", temp);
	}
	if (raw.contains("pospay.client.relay.tms.dst.addr=")) {
	    String config = "pospay.client.relay.tms.dst.addr";
	    String temp;
	    temp = raw.substring(raw.indexOf(config + "="));
	    temp = temp.substring((config + "=").length());
	    if (temp.contains("pospaytmswan.payex.com")) {
		temp = temp + " (default prod)";
	    } else if (temp.contains("91.208.214.34")) {
		temp = temp + " (default test)";
	    } else {
		temp = temp + " (uncommon address)";
	    }
	    class_Controller.config.addParam(config, temp);
	}
	if (raw.contains("pospay.client.relay.nfc.dst.addr=")) {
	    String config = "pospay.client.relay.nfc.dst.addr";
	    String temp;
	    temp = raw.substring(raw.indexOf(config + "="));
	    temp = temp.substring((config + "=").length());
	    if (temp.contains("195.225.0.42")) {
		temp = temp + " (default prod)";
	    } else if (temp.contains("192.168.243.103")) {
		temp = temp + " (default test+SSL)";
	    } else {
		temp = temp + " (uncommon address)";
	    }
	    class_Controller.config.addParam(config, temp);
	}
	if (raw.contains("ssl.enabled=")) {
	    if (raw.contains("ssl.enabled=true")) {
		class_Controller.config.addParam("ssl.enabled", "true");
	    } else if (raw.contains("ssl.enabled=false")) {
		class_Controller.config.addParam("ssl.enabled", "false");
	    }
	}
	if (raw.contains("relay.host.ssl.enabled=")) {
	    if (raw.contains("relay.host.ssl.enabled=true")) {
		class_Controller.config.addParam("relay.host.ssl.enabled", "true");
	    } else if (raw.contains("relay.host.ssl.enabled=false")) {
		class_Controller.config.addParam("relay.host.ssl.enabled", "false");
	    }
	}
	/*
	 * if (timeCurrentLine != null) {//requires java 1.8 try { // System.out.println("timeCurrentLine=" + timeCurrentLine); long
	 * timeDiffSeconds = (timeCurrentLine.getLong(ChronoField.SECOND_OF_DAY) - timePreviousLine.getLong(ChronoField.SECOND_OF_DAY)); if
	 * (timeDiffSeconds > 10) { // System.out.println("timeDiff=" + timeDiffSeconds); if
	 * ((!raw.toLowerCase().contains("CommunicationBroke".toLowerCase())) && (!raw.toLowerCase().contains("shutting down"
	 * .toLowerCase())) && (!raw.toLowerCase().contains("Starting ".toLowerCase())) &&
	 * (!raw.toLowerCase().contains("merchantInformationService".toLowerCase())) && (!raw.toLowerCase().contains("Connected to "
	 * .toLowerCase()))) { // System.out.println(raw); // String issuesTemp = timeDiffSeconds + " seconds idle"; String issuesTemp =
	 * "Abnormal idle time"; System.out.println("timeDiff " + raw); if (timeDiffSeconds > 120) { issuesTemp = "Abnormal idle: " +
	 * timeDiffSeconds + " seconds"; // System.out.println("timeDiff=" + timeDiffSeconds); // System.out.println("timeCurrentLine=" +
	 * timeCurrentLine); // System.out.println("timePreviousLine=" + timePreviousLine); } if (!issues_last.contains("Abnormal idle") &&
	 * !this.fact_service.value.contains("pendingRequest")) { addIssue(issuesTemp, false, null); } } } } catch (NullPointerException e)
	 * { // System.out.println("timePreviousLine null?=" + timePreviousLine); // e.printStackTrace(); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */
	if (raw.contains("Invalid card, expected a loyalty card")) {}
	if (raw.contains("Failed to handle loyalty cash")) {
	    String issuesTemp = null;
	    suspect_DeclinedClient = true;
	    if (class_Controller.config.getParam("cardExtensions.enabled").equals("true")) {
		if (this.fact_FuelCard == false) {
		    issuesTemp = "Non-Fuel kort til Fuel client";
		    addIssue(issuesTemp, true, null);
		}
	    } else {
		if (this.fact_FuelCard == true) {
		    issuesTemp = "Fuel kort til Non-Fuel client";
		    addIssue(issuesTemp, true, null);
		}
	    }
	}
	if (raw.contains("Unparseable date")) {
	    String issuesTemp = raw.substring(raw.indexOf("Unparseable date"));
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Failed to read expire date")) {
	    String issuesTemp = "Invalid expiration date";
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, true, null);
	}
	if (raw.contains("protel.fakepms.FakePmsUI: Connect failed")) {
	    String issuesTemp;
	    issuesTemp = raw.substring(raw.indexOf("protel.fakepms.FakePmsUI: Connect failed"));
	    suspect_DeclinedClient = true;
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("Wrong response from Service or Client method:")) {
	    String issuesTemp;
	    issuesTemp = raw.substring(raw.indexOf("Wrong response from Service or Client method:"));
	    suspect_DeclinedClient = true;
	    if (!issues_last.contains(issuesTemp)) {
		if (issuesTemp.contains(": 101")) {
		    addIssue("PROCESS_BUSY - PosPay Client receives a new request and already is handling a request", false, null);
		} else if (issuesTemp.contains(": 111")) {
		    addIssue("SYNCH_FAILED - PosPay Client fails to complete a synchronized function", false, null);
		} else if (issuesTemp.contains(": 114")) {
		    addIssue("Terminal skjønner ikke Open PED", true, null);
		    addSolution("Set terminal param #123 to 1");
		} else if (issuesTemp.contains(": 299")) {
		    addIssue("PROCESS_FAILED_TO_START", false, null);
		} else {
		    addIssue(issuesTemp, false, null);
		}
	    }
	}
	if (raw.contains("Failed to download product restrictions for ")) {
	    suspect_DeclinedClient = true;
	    addIssue("Could not parse server config at PPCL startup", false, null);
	    addSolution("Possibly caused by blocked ports. Check for connect timed out'");
	}
	if (raw.contains("Cannot create integer value from TLV Tag. Size too big")) {
	    suspect_DeclinedClient = true;
	    addIssue("Bad TLV settings", true, null);
	    addSolution("In terminal, set parameter 253 to 1, OR In Config.properties, set pospay.client.strictTLVEncoding=true");
	}
	if (raw.contains("REJECTED_BEFORE_AUTH")) {
	    String temp = "Card rejected";
	    if (!this.suspect_cancelECR) {
		suspect_DeclinedTerminal = true;
		if (this.suspect_cancelTerminalsw == true) {// terminal has already cancelled
		    temp = "Terminal set to Fuel in param 222?";
		    addIssue(temp, false, null);
		} else if (fact_cardEntered) {
		    addIssue(temp, false, null);
		}
	    }
	}
	if (raw.contains("Cancel called by operator")) {
	    if (fact_approved != 2) {
		fact_approved = 2;
		suspect_cancelECR = true;
		if ((this.fact_CardDataResponse > 0)) {
		    if (this.fact_propertieResponse == false) {
			addIssue("Client does not handle cancel at this point. Terminal conn. is slow", true, null);
			addSolution("Fix terminal connection");
			// System.out.println("Client does not handle cancel at this point "+fact_propertieResponse+"
			// "+fact_CardDataResponse);
		    } else {
			addIssue("Cancel called from ECR", true, null);
		    }
		} else {
		    if (this.fact_cardEjected) {
			addIssue("Terminal ejected card", true, null);
			addSolution("Check terminal logs to find cause of card ejection");
			this.suspect_cancelTerminalsw = true;
		    } else {
			addIssue("Cancel called from ECR - ECR possibly hanging", true, null);
			if (class_Controller.config.getParam("cardExtensions.enabled") != null
				&& class_Controller.config.getParam("cardExtensions.enabled").contains("true")) {} else {
			    if (this.fact_CardDataResponse == 0) {
				addSolution("Verify terminal is not set to Fuel");
				addSolution("Check terminal logs for clues to async");
			    }
			}
		    }
		}
	    }
	}
	if ((raw.contains("CANCEL_RESPONSE	-	0")) || (raw.contains("cancel() "))) {
	    suspect_cancelECR = true;
	    addIssue("Cancel called from ECR", true, null);
	}
	if (raw.contains("pinBeforeAmount=false")) {
	    class_Controller.config.addParam("pospay.client.pinBeforeAmount", "false");
	    if (fact_service.value.equals("OpenPED")) {
		addIssue("Called OpenPED with pinBeforeAmount disabled", true, null);
	    }
	}
	if (raw.contains("Invalid response code ")) {
	    suspect_DeclinedServer = true;
	    if (raw.contains("Invalid response code 404")) {
		addIssue("Invalid response code 404", false, null);
		addSolution("Possibly outdated certificate. Check for 'Failed to validate client certificate'");
	    } else if (raw.contains("Invalid response code 500:  Server Error")) {
		addIssue("RSP 500: Keystore unavailable from server", false, null);
		addSolution("Sjekk at det ikke er installert Test-klient i Prod-miljø eller motsatt.");
	    } else if (raw.contains("Invalid response code 503:  Service Unavailable")) {
		addIssue("Declined by server", false, null);
	    } else {
		addIssue("Invalid server response code", false, null);
	    }
	}
	if (raw.contains("createAidDefinitions(PPPTerminalSettings.java:")) {
	    suspect_AID = true;
	    if ((raw.contains("createAidDefinitions(PPPTerminalSettings.java:147)"))
		    || (raw.contains("createAidDefinitions(PPPTerminalSettings.java:150)"))) {// aidDefinitions[i].setIssuerName(issuerGrp.getGrpName());
		addIssue("Found no issuer for agreement", true, null);
		addSolution("Check issuer agreement");
	    } else if (issues_last.contains("AID issues") == false) {
		addIssue("AID issues", true, null);
		// if (solutions.contains("- AID issues skyldes feil med en avtale i SAT") == false) {
		addSolution("Something is wrong with agreement in SAT");
		// }
	    }
	}
	if (raw.contains("PosPay Service was not running when trying to shut it down")) {
	    suspect_PPS = true;
	    addIssue("PosPayService not starting?", true, null);
	    // if (solutions.contains("- Try starting PcPos with admin rights") == false) {
	    addSolution("Try starting PcPos with admin rights");
	}
	if (raw.contains("Access is denied")) {
	    suspect_PPS = true;
	    class_Controller.config.addParam("PosPayService", "false");
	    suspect_DeclinedClient = true;
	    addIssue("PosPayService access denied", true, null);
	    // if (solutions.contains("- Try starting PcPos with admin rights") == false) {
	    addSolution("Try starting PcPos with admin rights");
	}
	if (raw.contains("Declined: Unable to go online")) {
	    suspect_DeclinedTerminal = true;
	    addIssue("Declined: Unable to go online", true, null);
	    fact_online = false;
	}
	if (raw.contains("PIN entry required and PIN pad not present or not working")) {
	    suspect_DeclinedTerminal = true;
	    addIssue("PIN encryption failed", true, null);
	}
	if (raw.contains("Cardholder verification was not successful")) {
	    suspect_DeclinedTerminal = true;
	    addIssue("CVM failed", true, null);
	}
	if (raw.contains("Failed to download terminal settings")) {
	    suspect_DeclinedClient = true;
	    addIssue("Failed to download terminal settings", true, null);
	    addSolution("Incorrect PPP.key.");
	    PPPkey_correct = false;
	}
	if (raw.contains("Failed to parse date:")) {
	    addIssue("Failed to parse date", false, null);
	    solutions = solutions + " - System time possibly differs from server time";
	    addSolution("System time possibly differs from server time");
	}
	if ((raw.contains("Failed to load keys")) && !(issues_last.contains("Failed to load keys"))) {
	    addIssue("Keystore unavailable", true, null);
	}
	if (raw.contains("(ClientKeyStore.java:120)")) {
	    addIssue("Failed to load PPP.key", true, null);
	    addSolution("PPP.key missing");
	    PPPkey_correct = false;
	}
	if (raw.contains("BIN_FORBIDDEN")) {
	    addIssue("BIN_FORBIDDEN", true, null);
	    suspect_DeclinedTerminal = true;
	}
	if (raw.contains("Ping tx server failed")) {
	    addIssue("Ping tx server failed", false, null);
	}
	if (raw.contains("Mismatch with client / server key")) {
	    addIssue("Mismatch with client / server key", true, null);
	    suspect_DeclinedServer = true;
	}
	if (raw.contains("Could not connect to TMS")) {
	    addIssue("Could not connect to TMS", false, null);
	}
	if (raw.contains("Transaction rejected")) {
	    String issuesTemp = "Transaction rejected";
	    addIssue(issuesTemp, false, null);
	}
	if (raw.contains("[1511]")) {
	    addIssue("[1511] unknown error", false, null);
	}
	if (raw.contains("[3025]")) {
	    String issuesTemp = "EID 3025 - Communication error";
	    addIssue(issuesTemp, false, null);
	    addSolution("Check server connection");
	}
	if (raw.contains("Database lock acquisition failure")) {
	    suspect_DeclinedClient = true;
	    addIssue("Database lock acquisition failure", true, null);
	    addSolution("Restart computer");
	}
	if ((raw.contains("N1")) && (raw.contains("validateProductSetOnServerResponse") == false)) {
	    String issuesTemp = "N1 - Restricted wares";
	    addIssue(issuesTemp, true, "validateProductSetOnServerResponse");
	    addSolution("Remove restricted wares and try again");
	    suspect_DeclinedServer = true;
	}
	if (raw.contains("TrackData contains no CardIssuer")) {
	    if (this.fact_CardDataResponse > 0) {
		String issuesTemp = "TrackData contains no CardIssuer";
		addIssue(issuesTemp, true, null);
		addSolution("Verify that Card issuer is registered for merchant in SAT");
	    } else {
		addSolution("Verify that terminal param #199 is 1.");
	    }
	}
	if (raw.contains("Z3")) {// kommer i duplicate trx
	}
	if (raw.contains("item validation failed because of restriction")) {
	    addIssue("item validation failed because of restriction", true, null);
	    suspect_DeclinedClient = true;
	}
	if (raw.contains("Card error or card not recognized")) {
	    suspect_DeclinedTerminal = true;
	    addIssue("Card error or card not recognized", true, null);
	    addSolution("Compare the PAN with the BIN.CSV");
	    addSolution("Verify emv.par to be correct");
	    addSolution("Verify terminal config to be correct");
	    addSolution("Make sure the terminal is online");
	    addSolution("Try again");
	}
	if (raw.contains("timed out")) {
	    if (raw.contains("connect timed out")) {
		// System.out.println("connect timed out " + raw);
		String temp = "Connection issues";
		fact_online = false;
		// solutions = solutions + " - Possibly invalid SSL setup.class_Controller.newline"; //occurs without this cause
		if (raw.toLowerCase().contains("1.1.1.1")) {// server connection
		    // System.out.println("Server connection fail");
		    addIssue("connect timed out", false, null);
		    fact_online = false;
		    addSolution("Verify DNS settings");
		    addSolution("Verify port is not closed");
		    addSolution("Verify network settings");
		    addSolution("Verify firewall settings");
		} else if (raw.toLowerCase().contains("src ")) {
		    // System.out.println(temp + " src");
		} else if (raw.toLowerCase().contains("PPP address 1")) {
		    if (raw.toLowerCase().contains("pospay.payex.com:443")) {
			addIssue("connect timed out", true, null);
			fact_online = false;
			addSolution("Verify DNS settings");
			addSolution("Verify port is not closed");
			addSolution("Verify network settings");
			addSolution("Verify firewall settings");
		    }
		} else if (raw.toLowerCase().contains("PPP address 2")) {// not a problem
		    // System.out.println(temp + " PPP address 2");
		    if (raw.contains("pospaywan.payex.com:9010")) {// not a problem
			// System.out.println(temp + " PPP address 2 v2");
			// } else if (!(issues_last+issues_first).contains(temp)) {
		    }
		} else if (raw.toLowerCase().contains("1.1.1.2")) {// terminal connection
		    // System.out.println("Terminal connection fail");
		    addIssue("Terminal connection fail", true, null);
		    addSolution("Verify physical terminal connection");
		    addSolution("Verify terminal connection settings");
		    addSolution("Verify local com port settings");
		    addSolution("Verify terminal ECR mode is PPP");
		    addSolution("Try reinstalling USB drivers");
		} else if (raw.toLowerCase().contains("evc.payex.com:443")) {} else {
		    // System.out.println("connect timed out else " + raw);
		    addIssue("connect timed out", false, null);
		    fact_online = false;
		}
	    }
	}
	if (raw.contains("Transaction was rejected due to a communication error")) {
	    addIssue("Transaction was rejected due to a communication error", false, null);
	}
	if (raw.contains("Calling PPPDLLServer::CancelReq()")) {
	    suspect_cancelECR = true;
	}
	if ((raw.contains("Received event, 39:")) || (raw.contains("User cancelled the transaction (code 3. online.)"))) {
	    if (this.fact_approved != 2) {
		fact_approved = 2;
		if (!suspect_cancelECR) {
		    if (fact_askedCard) {
			if (fact_cardEntered) {
			    addIssue("Card refused by or removed from terminal", false, null);
			    this.suspect_DeclinedTerminal = true;
			} else {
			    suspect_Cancelcustomer = true;
			    addIssue("Cancel called by customer", false, null);
			}
		    } else {
			addIssue("Transaction refused by terminal(possible cancel)", false, null);
		    }
		} else {
		    suspect_cancelECR = true;
		}
	    }
	}
	if (raw.contains("Offline Dynamic Data Authentication (DDA) failed")) { // not a problem!
										// issues = issues + "Card datas failed authentication" + ",
										// ";
	}
	if (raw.contains("CVM_FAILED")) {
	    if (fact_cardEntered) {
		addIssue("CVM_FAILED", false, null);
	    }
	}
	if ((raw.contains("OFFLINE_COUNT	-	")) && (raw.contains("OFFLINE_COUNT	-	00000000") == false)) {
	    addIssue("Offline transactions pending", false, null);
	    fact_online = false;
	}
	if (raw.contains("Cannot start TCPHandler")) {
	    suspect_DeclinedClient = true;
	    addIssue("Cannot start TCPHandler", false, null);
	}
	if (raw.contains("Could not execute Expire Date dialog")) {
	    suspect_DeclinedClient = true;
	    addIssue("Could not execute Expire Date dialog", false, null);
	}
	if (raw.contains("request-type: EXPIRE_DATE, failed: null")) {
	    addIssue("User cancelled obligatory dialog", true, null);
	}
	if (raw.contains("Previous transaction was not approved")) {
	    String temp = "Attempted reversal on rejected transaction";
	    addIssue(temp, true, null);
	}
	if ((raw.contains("Process failed"))) {
	    suspect_DeclinedClient = true;
	    String temp = raw.substring(raw.indexOf("Process failed"));
	    if (raw.contains("Process failed, received status: 101")) {
		addIssue(temp, false, null);
	    } else if (raw.contains("Process failed, received status: 150")) {}
	}
	if (raw.contains("Event(1510 - ")) {
	    addIssue("Cancelled", false, null);
	}
	if (raw.contains("query did not return a unique result")) {
	    String temp = "Merchant has more than one terminal with that ID";
	    addIssue(temp, true, null);
	}
	// dynamisk
	if (raw.contains("Transaction failed")) {
	    String issuesTemp;
	    issuesTemp = raw.substring(raw.indexOf("Transaction failed"));
	    if (!issuesTemp.contains("1510")) {
		addIssue(issuesTemp, false, null);
	    }
	}
	if (raw.contains("AUTHORIZATION_RESPONSE_CODE	-	")) {
	    if ((raw.contains("AUTHORIZATION_RESPONSE_CODE	-	00") == false)
		    && (raw.contains("AUTHORIZATION_RESPONSE_CODE	-	Y") == false)) {
		String temp = raw;
		temp = temp.substring(temp.indexOf("AUTHORIZATION_RESPONSE_CODE	-	"));
		temp = temp.substring("AUTHORIZATION_RESPONSE_CODE	-	".length());
		if (temp.contains("Z3")) {// kommer også i duplicate trx
		    if (this.fact_CardDataResponse > 0) {
			temp = temp + " (offline reject)";
			// suspect_DeclinedTerminal = true;
			// addSolution("Look for other reasons why the trx failed");
			addSolution("Check that BIN-file is set up for allowing offline purchases");
			addSolution("Offline max limits");
			addSolution("Verify that the terminal is configured for online(USB) and not offline(LAN))");
			// fact_online = false;
			addIssue("Host rsp code " + temp, false, null);
		    }
		} else if (temp.contains("05")) {
		    temp = temp + " (various causes)";
		    addSolution("Terminal was possibly denied to charge for trx");
		    addSolution("Client possibly needs new PPP-key");
		    addSolution("'Kassen var stengt av'?");
		    addIssue("Host rsp code " + temp, true, null);
		} else if (temp.contains("51")) {
		    suspect_DeclinedHost = true;
		    temp = temp + " (insufficient funds)";
		    addIssue("Host rsp code " + temp, true, null);
		} else {
		    addIssue("Host rsp code " + temp, false, null);
		}
	    }
	}
	if ((raw.contains("Caused by:"))
		&& (raw.contains("IOException: ") || raw.contains("CommunicationException: ") || raw.contains("ServerAccessException: "))) {
	    String temp;
	    temp = raw;
	    while (temp.contains("Exception: ")) {
		temp = temp.substring(temp.indexOf("Exception: "));
		temp = temp.substring("Exception: ".length());
	    }
	    if (raw.contains("is not a valid COM port")) {
		addIssue("Bad COM port for async purchase", false, null);
		addSolution("Set COM port to #10");
	    } else {
		addIssue(temp, false, null);
	    }
	} else {
	    if (raw.contains("Exception:")) {
		String temp;
		temp = raw;
		while (temp.contains("Exception: ")) {
		    temp = temp.substring(temp.indexOf("Exception: "));
		    temp = temp.substring("Exception: ".length());
		}
		if (temp.length() > 4) {
		    if (raw.contains("java.io.IOException: Socket Closed")) {
			// flere årsaker avhengig av andre symptomer
			addIssue("Socket Closed", false, null);
			// addSolution("Try reinstalling PPCL");
			// addSolution("Check terminal logs if terminal is offline");
		    } else if (raw.contains("TransactionRejectedException")) {// already taken care of
		    } else if (raw.contains("LifecycleProcessor not initialized")) {// already taken care of
		    } else if (raw.contains("UnknownHostException")) {// already taken care of
		    } else if (raw.contains("CommunicationException")) {// already taken care of
		    } else if (raw.contains("SSLPeerUnverifiedException")) {// already taken care of
		    } else if (raw.contains("SocketTimeoutException")) {// (not a problem)
		    } else if (raw.contains("GenericInputRequestException")) {// (not a problem)
			// addIssue("Unable to retrieve info from ECR", false, "GenericInputRequestException");
		    } else if (raw.contains("BeanCreationNotAllowedException")) {
			addIssue("BeanCreationNotAllowedException", false, "BeanCreationNotAllowedException");
		    } else if (raw.contains("IllegalStateException")) {
			addIssue("IllegalStateException", false, "IllegalStateException");
		    } else if (raw.contains("CardExpiredException")) {
			addIssue("Card is expired", true, "CardExpiredException");
		    } else if (raw.contains("Failed to validate client certificate")) {
			String issuesTemp = "SSL certificate outdated";
			addIssue(issuesTemp, true, "Failed to validate client certificate");
			addSolution("Terminal may need replacement");
		    } else if (raw.contains("UnknownHostException: pospay.payex.com")) {
			String issuesTemp = "UnknownHostException: pospay.payex.com";
			addIssue(issuesTemp, false, null);
			addSolution("Fix the DNS" + class_Controller.newline
				+ "Put in an entry to config.properties asking the client to use a specific IP-adress insted");
		    } else if (raw.contains("no response to pick up")) {
			String issuesTemp = "Bad use of client API";
			addIssue(issuesTemp, true, null);
			addSolution("Tell Opus to stop using HTTP API '/InputDialog' at this point in trx");
		    } else if (raw.contains("jvm_bind")) {} else if (raw.contains(
			    "Failed to connect to terminal")) {} else if (raw.contains("Failed to get response(reversal required)")) {
			suspect_DeclinedClient = true;
			addIssue("Missing required server connection", false, "PPPServerAccess");
		    } else if (raw.contains("is not supported in ")) {
			if (this.fact_cardEjected) {} else {
			    if (this.suspect_cancelECR) {} else {
				String issuesTemp = "Client and terminal disagree on service";
				if (raw.contains("Start async purchase is not supported in WAIT_FOR_RESPONSE state")) {
				    issuesTemp = "Bad card usage";
				    suspect_DeclinedClient = true;
				    addIssue(issuesTemp, true, null);
				    addSolution("Don't insert card before(or after) you have declined MobilePay");
				} else if (raw.contains("Ready to start Pin bypass dialog is not supported in CARD_EXTENSIONS state")) {// not
																	// a
																	// problem
				} else if ((this.fact_CardDataResponse == 0) && (this.fact_completeResponse == true)) {
				    addSolution("Check terminal logs for causes of async");
				} else {
				    addIssue(issuesTemp, false, null);
				    addSolution("Check client and terminal for connection issues");
				    addSolution("Check terminal logs for causes of async");
				}
			    }
			}
		    } else if (raw.contains("system cannot find the file specified")) {
			temp = raw.substring(raw.indexOf("FileNotFoundException:"));
			temp = temp.substring("FileNotFoundException:".length());
			String issuesTemp = temp;
			suspect_DeclinedClient = true;
			addIssue(issuesTemp, false, null);
		    } else {
			addIssue(temp, false, "Exception:");
		    }
		    if (raw.contains("ARN cannot exceed 10 characters")) {
			addSolution("Enter proper ARN");
		    }
		}
	    }
	}
	// symptom combinations
	if ((raw.contains("Invalid response code 550")) || (raw.contains("Failed to load keys"))
		|| (raw.contains("Failed to initialize client proxy"))) {
	    if ((issues_last.contains("Invalid response code 550")) && (issues_last.contains("Failed to load keys"))
		    && (issues_last.contains("Failed to initialize client proxy"))) {
		suspect_DeclinedClient = true;
		addIssue("PPP-key missing or invalid", true, null);
		addSolution("Fix PPP.key");
		PPPkey_correct = false;
	    }
	}
	// notes
	if (raw.contains("Running offline cleaner task")) {}
	if ((raw.contains("Merchant forced transaction online")) || (raw.toLowerCase().contains("merchant forced transaction online"))) {
	    fact_forcedOnline = true;
	}
	if (this.fact_Issuer_ID.contains("80")) {
	    this.fact_TrumfRead = true;
	    fact_BonusCardRead = true;
	}
	if (this.fact_state_DATA_DUMP_REQUESTED) {
	    if (this.events.get("[124]") != null) {} else {
		// addIssue("Datadump not completed", true, null);
	    }
	    if (fact_TerminalFileHandler_started == true) {} else {
		// addIssue("TerminalFileHandler not started", true, null);
	    }
	    if (this.fact_StartDataDumpAction_started == true) {
		if (this.fact_StartDataDumpAction_done == true) {} else {
		    // addIssue("StartDataDumpAction not completed", true, null);
		}
	    } else {
		// addIssue("StartDataDumpAction not started", true, null);
	    }
	}
	if (fact_iDMeth.value != null) {
	    if (fact_iDMeth.value.equals("A")) {
		fact_iDMeth.value = fact_iDMeth.value + " (PIN online)";
	    } else if (fact_iDMeth.value.equals("1")) {
		fact_iDMeth.value = fact_iDMeth.value + " (PIN offline)";
	    } else if (fact_iDMeth.value.equals("@")) {
		fact_iDMeth.value = fact_iDMeth.value + " (Signature)";
	    } else if (fact_iDMeth.value.equals("/")) {
		fact_iDMeth.value = fact_iDMeth.value + " (No CVM required)";
	    } else if (fact_iDMeth.value.equals("-")) {
		fact_iDMeth.value = fact_iDMeth.value + " (No CVM performed)";
	    } else if (fact_iDMeth.value.equals(" ")) {
		fact_iDMeth.value = fact_iDMeth.value + " (CVM failed)";
	    } else if (fact_iDMeth.value.equals("b")) {
		fact_iDMeth.value = fact_iDMeth.value + " (Combined)";
	    }
	}
	if (this.fact_authmeth.value != null) {
	    if (fact_authmeth.value.equals("1")) {
		fact_authmeth.value = fact_authmeth.value + " (Host)";
	    } else if (fact_authmeth.value.equals("2")) {
		fact_authmeth.value = fact_authmeth.value + " (Terminal)";
	    } else if (fact_authmeth.value.equals("4")) {
		fact_authmeth.value = fact_authmeth.value + " (Phone)";
	    } else if (fact_authmeth.value.equals("5")) {
		fact_authmeth.value = fact_authmeth.value + " (None)";
	    } else if (fact_authmeth.value.equals("-")) {
		fact_authmeth.value = fact_authmeth.value + " (Declined)";
	    }
	}
	this.timePreviousLine = this.timeCurrentLine;
    }
    public String getConclusion(String logFile) {
	return getproblemname(logFile);
    }
    public String getproblemname(String logFile) { // also do last check of issues
	String print = "";
	if ((issues_last.length() + issues_first.length()) > 1) {
	    print = "----";
	}
	// trx type
	if (fact_fuelTrx == true) {
	    print = print + " FUEL -";
	} else if (fact_MobileTrx == true) {
	    print = print + " Mobile -";
	}
	// service type
	if (fact_service.value != null) {
	    print = print + " " + fact_service.value + " ";
	} else {
	    print = print + " " + "?" + " ";
	}
	// approved
	// System.out.println("Progress=" + Progress);
	if ((fact_service.value != null) && (isTransaction() || fact_service.value.contains("Flush"))) {
	    // System.out.println("fact_service.value=" + fact_service.value + " isTransaction=" + isTransaction());
	    // System.out.println("B fact_service.value=" + fact_service.value + " fact_approved=" + fact_approved);
	    if (this.fact_approved == 1) {
		print = print + "- Success ";
	    } else if (suspect_DeclinedHost == true) {
		print = print + "- Rejected by host (" + Progress + ") ";
	    } else if (suspect_DeclinedTerminal == true) {
		print = print + "- Rejected by terminal (" + Progress + ") ";
	    } else if (suspect_DeclinedClient == true) {
		print = print + "- Rejected by client (" + Progress + ") ";
	    } else if (suspect_DeclinedServer == true) {
		print = print + "- Rejected by server (" + Progress + ") ";
	    } else if (suspect_cancelECR == true) {
		print = print + "- Canceled from ECR (" + Progress + ") ";
	    } else if (this.suspect_cancelPPCL == true) {
		print = print + "- Canceled by PosPayService (" + Progress + ") ";
	    } else if (suspect_Cancelcustomer == true) {
		print = print + "- Canceled by card holder (" + Progress + ") ";
	    } else if (suspect_cancelTerminalsw == true) {
		print = print + "- Canceled from terminal (" + Progress + ") ";
	    } else if (fact_approved == 2) {
		print = print + "- Failed (" + Progress + ") ";
	    } else {
		if (Progress < 10) {
		    print = print + "- Unknown status (" + Progress + ") ";
		} else {
		    print = print + "- Success ";
		}
	    }
	} else {
	    if (Progress < 10) {
		print = print + "- Unknown status (" + Progress + ") ";
	    } else {
		print = print + "- Success ";
	    }
	}
	// System.out.println("print=" + print);
	// entry mode
	if (fact_EntryMode.value != null) {
	    print = print + "- " + fact_EntryMode.value + " ";
	} else {
	    print = print + "- " + "?" + " ";
	}
	//
	if (fact_issuerName.length() > 1) {
	    print = print + "- " + fact_issuerName + " ";
	} else {
	    print = print + "- " + "?" + " ";
	}
	//
	if ((this.fact_TrumfRead == true) && (!fact_issuerName.contains("Trumf"))) {
	    print = print + "+Trumf" + " ";
	}
	//
	while (this.fact_Issuer_ID.contains(" ")) {
	    // System.out.println("space=" + fact_Issuer_ID.indexOf(" ") + " " + fact_Issuer_ID + " length=" + fact_Issuer_ID.length());
	    if (fact_Issuer_ID.indexOf(" ") < fact_Issuer_ID.length() - 1) {
		// System.out.println("replacing space");
		fact_Issuer_ID = fact_Issuer_ID.replaceFirst(" ", "+");
	    } else {
		fact_Issuer_ID = fact_Issuer_ID.replaceFirst(" ", "");
	    }
	}
	if (this.fact_Issuer_ID.length() > 0) {
	    print = print + "/" + fact_Issuer_ID + " ";
	}
	//
	if (fact_OI.value != null) {
	    print = print + "- " + fact_OI.value + " ";
	} else {
	    print = print + "- " + "?" + " ";
	}
	//
	if (fact_iDMeth.value != null) {
	    print = print + "- " + fact_iDMeth.value + " ";
	} else {
	    print = print + "- " + "?" + " ";
	}
	if (Amount_total > 0) {
	    double kr_total = Amount_total / 100.0;
	    if (Amount_tip > 0) {
		double kr_tip = Amount_tip / 100.0;
		print = print + "- Amt " + kr_total + " (Tip " + kr_tip + ") ";
	    } else {
		print = print + "- Amt " + kr_total + " ";
	    }
	}
	// issues
	if ((fact_MobileTrx == true) && (fact_CardDataResponse > 0)) {
	    String temp = "Card and MP combined!";
	    addIssue(temp, true, "Card and MP combined!");
	}
	if ((fact_service.value != null)) {
	    if (this.fact_service.value.contains("starting")) {
		if (!this.fact_SettingsRead) {
		    addIssue("PosPayService did not read settings at startup", true, null);
		}
	    }
	    if (this.fact_service.value.contains("login")) {
		if (!this.fact_XMLRequest) {
		    addIssue("Login did not connect to server", false, null);
		}
		if (!this.fact_TCPconnection) {
		    addIssue("Login did not connect to TCP", false, null);
		}
		if (!this.fact_CommCheck) {// skjer ikke alltid, ikke problem
		    // addIssue("Login did not do comm. check", false, null);
		}
	    }
	    // påminnelse om status-problemer
	    if ((isTerminalLogg() == false) && (fact_service.value != null) && !fact_service.value.contains("PosPayService")
		    && !fact_service.value.contains("getProperties") && !fact_service.value.contains("ping")
		    && !fact_service.value.contains("reconciliation") && !fact_service.value.contains("pendingRequest")
		    && !fact_service.value.contains("cardInfo") && !fact_service.value.contains("preAuthAdjust")
		    && !fact_service.value.contains("getPreAuth") && !fact_service.value.contains("receipt")
		    && !fact_service.value.contains("report") && !fact_service.value.contains("login")
		    && !fact_service.value.contains("tmsUpdate") && !this.fact_completeEventReceived.contains("1708")
		    && !fact_service.value.contains("merchantInformation") && !fact_service.value.contains("manualReversal")
		    && !fact_service.value.contains("Starting")) {
		if ((class_Controller.config.getParam("asyncPurchase.enabled") != null)
			&& (class_Controller.config.getParam("asyncPurchase.enabled").equals("true"))) {
		    if ((class_Controller.config.getParam("MobileBeacon Id") != null)
			    && (class_Controller.config.getParam("MobileBeacon Id").equals("0"))) {
			addIssue("MobilePay beacon not activated", true, null);
		    }
		}
		if (class_Controller.config.getParam("Terminal connection") == "false") {
		    addIssue("Terminal not properly connected to computer", false, null);
		}
		if (fact_completeResponse == false) {
		    String temp = "No completeResponse received";
		    // addIssue(temp, true, null);
		}
		if ((fact_CardDataResponse > 0) && (fact_EntryMode.value == null)) {
		    this.fact_EntryMode.value = "Manual";
		}
		// Mynt only
		if (logFile.toLowerCase() != null && (isTerminalLogg() == false)
			&& (class_Controller.config.getParam("Terminal type") != null)
			&& class_Controller.config.getParam("pppclient.log").contains("true")
			&& (!class_Controller.config.getParam("Terminal type").equals("GPA"))) {
		    // System.out.println("pppclient.log " + class_Controller.config.getParam("pppclient.log"));
		    if (class_Controller.config.getParam("cardInTerminal") == "true") {
			// addIssue("Card still in terminal", false, null);
		    }
		    if ((fact_CardDataResponse == 0) && (fact_MobileTrx == false)) {
			String temp = "No cardDataResponse received";
			addIssue(temp, false, null);
			if (this.suspect_cancelTerminalsw) {} else if (this.suspect_cancelECR) {} else {
			    if (this.fact_completeResponse == false) {
				addSolution("Verify that terminal param #199 is 1.");
			    }
			}
		    } else {
			class_Controller.config.addParam("Terminal connection", "true");
		    }
		    if (fact_propertieResponse == false) { // (not a problem)
			String temp = "No propertiesResponse from terminal";
			// addIssue(temp, false);
		    } else {
			class_Controller.config.addParam("Terminal connection", "true");
		    }
		    if (fact_abstractRequest == false) {
			String temp = "No abstractRequest sent";
			addIssue(temp, false, null);
		    }
		    if ((fact_pinBypassCheck == false) && (fact_MobileTrx == false)) { // not a problem
			String temp = "PinBypass not checked";
			// addIssue(temp, false, null);
		    }
		    if (this.fact_completeEventReceived.equals("")) {
			String temp = "No complete-event sent";
			addIssue(temp, false, null);
		    }
		    if (!this.fact_completeEventReceived.equals("")) {
			if ((fact_CardDataResponse > 0)) {
			    if (fact_MP_checkin == true) {
				String temp = "Terminal completed service before card";
				addIssue(temp, true, null);
			    }
			}
		    }
		}
	    }
	}
	//
	String temp = "";
	try {
	    if (class_Controller.config.getParam("cardExtensions.enabled").equals("true")) {} else {
		if (this.fact_FuelCard == true) {
		    String issuesTemp = "Fuel kort til Non-Fuel client";
		    addIssue(issuesTemp, false, null);
		}
	    }
	} catch (Exception e) {}
	if ((class_Controller.config.getParam("fuelSplitPaymentAllowed") != null)
		&& (class_Controller.config.getParam("fuelSplitPaymentAllowed").equals("false"))) {
	    if (fact_Issuer_ID.contains("84")) {
		String issuesTemp = "Fuel-kort uten Fuel-innstillinger";
		addIssue(issuesTemp, false, null);
	    }
	    if (fact_FuelCard == true) {
		String issuesTemp = "Fuel-kort uten Fuel-innstillinger";
		addIssue(issuesTemp, false, null);
	    }
	    if (fact_fuelTrx == true) {// not a problem
		String issuesTemp = "Fuel-trx uten Fuel-innstillinger";
	    }
	}
	if (this.fact_FuelCard == false) {
	    if ((fact_Issuer_ID.contains("83")) || (fact_Issuer_ID.contains("84"))) {
		String issuesTemp = "Fuel-kort ikke gjenkjent som Fuel-kort";
		addIssue(issuesTemp, true, null);
	    }
	}
	if ((fact_TS != null) && (fact_TS.value != null) && (this.fact_TS.value.contains("STATUS_ERR_COMM_FAILED"))) {
	    if ((fact_iDMeth != null) && (fact_iDMeth.value != null) && (fact_iDMeth.value.contains("CVM_FAILED"))) {
		if (fact_OI.value.contains("online")) {
		    // result = "Cancelled from ECR, " + result;
		    suspect_cancelECR = true;
		}
	    }
	}
	if ((fact_Issuer_ID.equals("")) && ((fact_issuerName == "")) && ((fact_issuerName == null))) {
	    fact_cardEntered = true;
	    // System.out.println("fact_Issuer_ID=" + fact_Issuer_ID + " fact_issuerName=" + fact_issuerName);
	}
	if ((fact_MP_checkin == true) && (fact_MP_accept == false)) {
	    addIssue("MP trx stopped after check-in", false, null);
	}
	if (fact_MP_accept && !fact_MP_pay) {
	    addIssue("MP trx stopped after payment accepted", false, null);
	}
	if (PPPkey_correct == false && class_Controller.config.getParam("PPP address 1").contains("false")) {
	    addIssue("Server connection issues makes PPP.key unavailable", true, null);
	    addSolution("Verify that terminal ID matches SAT setup");
	}
	// alltid vente til slutten av metoden med å printe results og issues
	// System.out.println("print 9=" + print);
	print = print + "-" + " ";
	// System.out.println("print 10=" + print);
	if (issues_first.length() > 1) {
	    String issuesRest = issues_first;
	    while (issuesRest.contains(",")) {
		String issueNext = issuesRest.substring(0, issuesRest.indexOf(","));
		issuesRest = issuesRest.substring(issuesRest.indexOf(",") + 1);
		if (!print.contains(issueNext)) {
		    print = print + issueNext + ", ";
		}
	    }
	    // print = print + "- " + issues;
	}
	// System.out.println("print A=" + print);
	if (issues_last.length() > 1) {
	    // System.out.println("print A2 issues_last=" + issues_last);
	    String issuesRest = issues_last;
	    while (issuesRest.contains(", ")) {
		String issueNext = issuesRest.substring(0, issuesRest.indexOf(", "));
		issuesRest = issuesRest.substring(issuesRest.indexOf(", ") + 2);
		if (!print.contains(issueNext)) {
		    print = print + issueNext + ", ";
		}
	    }
	    // print = print + "- " + issues;
	}
	// System.out.println("print B=" + print);
	return print;
    }
    public String getDescription_session(String logFile) {
	// System.out.println("getDescription_session a");
	String tekst = "FACTS for session__________________________" + class_Controller.newline;
	tekst = description_session(tekst, logFile);
	tekst = description_Fuel(tekst, logFile);
	tekst = description_MobilePay(tekst, logFile);
	tekst = description_Trx(tekst, logFile);
	tekst = description_issues(tekst, logFile);
	return tekst;
    }
    public String getDescription_transaction(String logFile) {
	// System.out.println("getDescription_transaction a");
	String tekst = "FACTS for transaction__________________________" + class_Controller.newline;
	tekst = description_session(tekst, logFile);
	tekst = description_Fuel(tekst, logFile);
	tekst = description_MobilePay(tekst, logFile);
	tekst = description_Trx(tekst, logFile);
	tekst = description_issues(tekst, logFile);
	return tekst;
    }
    private String description_session(String tekst, String logFile) {
	// if (isTransaction() && isTerminalLogg() == false) {
	// if (isTerminalLogg() == true && (class_Controller.config.getParam("Terminal type") != null) &&
	// (class_Controller.config.getParam("Terminal type").contains("Mynt"))) {
	// System.out.println("description_session " + isLogg(logFile) + " " + isTerminalLogg());
	if (isLogg(logFile)) {
	    if ((isTerminalLogg() == false)) {
		tekst = tekst + "== Client session ==" + class_Controller.newline;
		tekst = tekst + "= Client config =" + class_Controller.newline;
		tekst = addParam(tekst, "PosPayService");
		tekst = addParam(tekst, "pospay.client.pinBypass.enabled");
		tekst = addParam(tekst, "pospay.client.supportsAddPrint");
		tekst = addParam(tekst, "strictTLVEncoding");
		tekst = addParam(tekst, "pospay.client.pinBeforeAmount");
		tekst = addParam(tekst, "multiMerchantEnabled");
		tekst = tekst + "= Terminal state =" + class_Controller.newline;
		tekst = addParam(tekst, "Terminal type");
		tekst = addParam(tekst, "Terminal connection");
		tekst = addParam(tekst, "1.1.1.2:5188");
		tekst = tekst + "Terminal connected to server: " + fact_terminal_server_connection + class_Controller.newline;
		tekst = tekst + "= Client server connection =" + class_Controller.newline;
		tekst = tekst + "Client server connection: " + class_Controller.config.getParam("PPP address 1") + class_Controller.newline;
		tekst = addParam(tekst, "pospay.client.address");
		tekst = addParam(tekst, "pospay.client.configDownloadAddress");
		tekst = addParam(tekst, "pospay.client.relay.host.dst.addr");
		tekst = addParam(tekst, "pospay.client.relay.tms.dst.addr");
		tekst = addParam(tekst, "pospay.client.relay.nfc.dst.addr");
		tekst = addParam(tekst, "91.208.214.34:7005");
		tekst = addParam(tekst, "91.208.214.34:7007");
		tekst = addParam(tekst, "192.168.243.103:9039");
		tekst = addParam(tekst, "195.225.0.42:9034");
		tekst = addParam(tekst, "195.225.28.97");
		tekst = addParam(tekst, "195.225.28.97:443");
		tekst = addParam(tekst, "195.225.28.97:4412");
		tekst = addParam(tekst, "pospay.payex.com:443");
		tekst = addParam(tekst, "pospaytx.payex.com:443");
		tekst = addParam(tekst, "pospaytx.payex.com:4412");
		tekst = addParam(tekst, "pospaywan.payex.com:9010");
		tekst = addParam(tekst, "1.1.1.1:7003");
		tekst = addParam(tekst, "1.1.1.1:9034");
		tekst = addParam(tekst, "1.1.1.1:9039");
		tekst = tekst + "= SSL settings =" + class_Controller.newline;
		tekst = addParam(tekst, "ssl.enabled");
		if ((class_Controller.config.getParam("ssl.enabled") != null)
			&& (class_Controller.config.getParam("ssl.enabled").contains("true"))) {
		    tekst = addParam(tekst, "pospay.client.relay.host.ssl.address");
		    tekst = addParam(tekst, "relay.host.ssl.enabled");
		}
	    } else if (isTerminalLogg() == true) {
		if ((class_Controller.config.getParam("Terminal type") != null)) {
		    if ((class_Controller.config.getParam("Terminal type").contains("Mynt"))) {
			tekst = tekst + "== Terminal state ==" + class_Controller.newline;
			tekst = addParam(tekst, "Terminal type");
			tekst = tekst + "= Server connection =" + class_Controller.newline;
			tekst = addParam(tekst, "Server address");
			tekst = addParam(tekst, "Param 115");
			tekst = addParam(tekst, "Param 142");
			tekst = addParam(tekst, "Param 143");
			tekst = addParam(tekst, "Param 144");
			tekst = addParam(tekst, "Param 156");
			tekst = addParam(tekst, "Param 196");
			tekst = addParam(tekst, "Param 224");
			tekst = addParam(tekst, "Param 225");
			tekst = addParam(tekst, "Param 251");
			tekst = tekst + "-- SSL settings --" + class_Controller.newline;
			tekst = addParam(tekst, "Param 63");
			tekst = addParam(tekst, "Param 64");
			tekst = addParam(tekst, "Param 100");
			tekst = addParam(tekst, "Param 110");
			tekst = addParam(tekst, "Param 111");
			tekst = addParam(tekst, "Param 150");
			tekst = tekst + "-- ECR communication --" + class_Controller.newline;
			tekst = addParam(tekst, "Param 3");
			tekst = addParam(tekst, "Param 4");
			tekst = addParam(tekst, "Param 109");
			tekst = addParam(tekst, "Param 125");
			tekst = addParam(tekst, "Param 133");
			tekst = addParam(tekst, "Param 173");
			tekst = addParam(tekst, "Param 253");
			tekst = addParam(tekst, "Param 254");
			tekst = addParam(tekst, "Param 255");
			// tekst = tekst + "SSL settings --" + "class_Controller.newline";
			tekst = tekst + "-- Terminal functions --" + class_Controller.newline;
			for (int i = 0; i < 510; i++) {
			    if ((i != 63) && (i != 64) && (i != 74) && (i != 86) && (i != 100) && (i != 109) && (i != 123) && (i != 125)
				    && (i != 133) && (i != 142) && (i != 143) && (i != 144) && (i != 150) && (i != 153) && (i != 156)
				    && (i != 173) && (i != 251) && (i != 253) && (i != 255)) {
				// System.out.println("-" + i + "-");
				tekst = addParam(tekst, "Param " + i);
			    }
			}
			/*
			 * tekst = tekst + "Param 63, Host address: " + this.class_Controller.config.getParam("Param 63") +
			 * "class_Controller.newline"; tekst = tekst + "Param 64, Host port: " + this.class_Controller.config.getParam(
			 * "Param 64") + "class_Controller.newline"; tekst = tekst + "Param 100, Host connection type: " +
			 * this.class_Controller.config.getParam("Param 100") + "class_Controller.newline"; tekst = tekst +
			 * "Param 109, ECR mode: " + this.class_Controller.config.getParam("Param 109") + "class_Controller.newline"; tekst
			 * = tekst + "Param 110, TMS port: " + this.class_Controller.config.getParam("Param 110") +
			 * "class_Controller.newline"; tekst = tekst + "Param 111, TMS IP: " + this.class_Controller.config.getParam(
			 * "Param 111") + "class_Controller.newline"; tekst = tekst + "Param 142, TCP timeout: " +
			 * this.class_Controller.config.getParam( "Param 142") + "class_Controller.newline"; tekst = tekst +
			 * "Param 150, SSL: " + this.class_Controller.config.getParam("Param 150") + "class_Controller.newline"; tekst =
			 * tekst + "Param 156, Host timeout: " + this.class_Controller.config.getParam("Param 156") +
			 * "class_Controller.newline"; tekst = tekst + "Param 196, WyWallet port: " + this.class_Controller.config.getParam(
			 * "Param 196") + "class_Controller.newline"; tekst = tekst + "Param 224, PayEx TMS IP: " +
			 * this.class_Controller.config.getParam( "Param 224") + "class_Controller.newline"; tekst = tekst +
			 * "Param 225, PayEx TMS port: " + this.class_Controller.config.getParam("Param 225") + "class_Controller.newline";
			 */
		    }
		}
	    }
	}
	return tekst;
    }
    private String description_Fuel(String tekst, String logFile) {
	if (isLogg(logFile) && isTransaction() && class_Controller.config.getParam("Terminal type") != null
		&& (class_Controller.config.getParam("Terminal type").contains("Mynt"))) {
	    tekst = tekst + "= Fuel settings =" + class_Controller.newline;
	    if (class_Controller.config.getParam("cardExtensions.enabled") != null
		    && class_Controller.config.getParam("cardExtensions.enabled").contains("true")) {
		tekst = addParam(tekst, "cardExtensions.enabled");
		tekst = addParam(tekst, "fuelSplitPaymentAllowed");
		tekst = addParam(tekst, "forceItemSummaryList");
		if (isTerminalLogg() == true && (class_Controller.config.getParam("Terminal type").contains("Mynt"))) {
		    tekst = addParam(tekst, "Param 222");
		} else if (isTransaction()) {
		    tekst = tekst + "Fuel trx: " + fact_fuelTrx + class_Controller.newline;
		    tekst = tekst + "Fuel card: " + this.fact_FuelCard + class_Controller.newline;
		}
	    } else {
		tekst = addParam(tekst, "cardExtensions.enabled");
	    }
	}
	return tekst;
    }
    private String description_MobilePay(String tekst, String logFile) {
	if (isLogg(logFile) && isTransaction() && class_Controller.config.getParam("Terminal type") != null
		&& (class_Controller.config.getParam("Terminal type").contains("Mynt"))) {
	    tekst = tekst + "= MobilePay =" + class_Controller.newline;
	    if (isTerminalLogg() == true && (class_Controller.config.getParam("Terminal type").contains("Mynt"))) {} else {
		boolean active = false;
		try {
		    if (class_Controller.config.getParam("MobileBeacon Id") != null) {
			long value = Long.parseLong(class_Controller.config.getParam("MobileBeacon Id"));
			active = true;
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		tekst = tekst + "MP beacon activated: " + active + class_Controller.newline;
		tekst = addParam(tekst, "asyncPurchase.enabled");
		tekst = tekst + "MP initiated: " + this.fact_MobileTrx + class_Controller.newline;
		if (fact_MobileTrx == true) {
		    tekst = tekst + "MP available: " + this.fact_MP_available + " (EID 200)" + class_Controller.newline;
		    tekst = tekst + "MP checked in: " + this.fact_MP_checkin + " (EID 201)" + class_Controller.newline;
		    tekst = tekst + "MP accepted: " + this.fact_MP_accept + " (EID 202)" + class_Controller.newline;
		    tekst = tekst + "MP paid: " + this.fact_MP_pay + class_Controller.newline;
		} else {}
	    }
	}
	return tekst;
    }
    private String description_Trx(String tekst, String logFile) {
	tekst = tekst + "ECR: " + ECR + class_Controller.newline;
	tekst = tekst + "Asked for card: " + this.fact_askedCard + class_Controller.newline;
	tekst = tekst + "Service: " + fact_service.value + class_Controller.newline;
	// System.out.println(isTransaction() + " " + isTerminalLogg());
	if (isTransaction()) {
	    // System.out.println("description_Trx A");
	    if (isTerminalLogg() == true) {} else {
		tekst = tekst + "AbstractRequest: " + this.fact_abstractRequest + class_Controller.newline;
		tekst = tekst + "REQUEST_SENT: " + this.fact_REQUEST_SENT + class_Controller.newline;
		tekst = tekst + "Properties response: : " + this.fact_propertieResponse + class_Controller.newline;
	    }
	    // System.out.println("description_Trx B");
	    tekst = tekst + "= Card =" + class_Controller.newline;
	    // System.out.println(tekst);
	    tekst = tekst + "Asked for card: " + this.fact_askedCard + class_Controller.newline;
	    tekst = tekst + "Card entered: " + this.fact_cardEntered + class_Controller.newline;
	    // System.out.println("description_Trx C");
	    if ((fact_cardEntered) || (fact_Issuer_ID.length() > 0)) {
		tekst = tekst + "CardDataResponse: " + this.fact_CardDataResponse + class_Controller.newline;
		tekst = tekst + "Issuer ID: " + this.fact_Issuer_ID + class_Controller.newline;
		tekst = tekst + "Issuer name: " + fact_issuerName + class_Controller.newline;
		tekst = tekst + "Entry mode used: " + fact_EntryMode.value + class_Controller.newline;
		tekst = tekst + "Bonus card: " + this.fact_BonusCardRead + class_Controller.newline;
		tekst = tekst + "Trumf card: " + this.fact_TrumfRead + "(Issuer 80/Trumf)" + class_Controller.newline;
	    }
	    tekst = addParam(tekst, "cardInTerminal");
	    tekst = tekst + "= " + class_Controller.newline;
	    tekst = tekst + ((class_Controller.config.getParam("Terminal type") != null)
		    && (class_Controller.config.getParam("Terminal type").contains("Mynt"))
			    ? "ReadyToStartPinBypassDialog: " + this.fact_pinBypassCheck + class_Controller.newline : "");
	    tekst = tekst + "ID.method: " + fact_iDMeth.value + class_Controller.newline;
	    // tekst = tekst + "receiptService: " + this.fact_receipt + "class_Controller.newline";
	    tekst = tekst + "Server connection: " + this.fact_terminal_server_connection + class_Controller.newline;
	    tekst = tekst + ((class_Controller.config.getParam("Terminal type") != null)
		    && (class_Controller.config.getParam("Terminal type").contains("Mynt"))
			    ? "Trans.status: " + fact_TS.value + class_Controller.newline : "");
	    tekst = tekst + "Host resp: " + fact_rspCode.value + class_Controller.newline;
	    tekst = tekst + "Actual online: " + this.fact_online + class_Controller.newline;
	    tekst = tekst + "Auth.method: " + fact_authmeth.value + class_Controller.newline;
	    tekst = tekst + "Forced online: " + this.fact_forcedOnline + class_Controller.newline;
	    tekst = tekst + "Online: " + fact_OI.value + class_Controller.newline;
	    tekst = tekst + "CompleteResponse: " + this.fact_completeResponse + class_Controller.newline;
	    tekst = tekst + "Complete-event: " + this.fact_completeEventReceived + class_Controller.newline;
	    tekst = tekst + "Approved: " + fact_approved + class_Controller.newline;
	    // System.out.println("description_Trx D");
	}
	tekst = tekst + "-- Events --" + class_Controller.newline;
	for (Object key : events.keySet()) {
	    boolean value = ((Boolean) (events.get(key)));
	    String eventText = key.toString();
	    if (key.toString().contains("4004")) {
		eventText = eventText + " (product list)";
	    }
	    tekst = tekst + "Event " + eventText + class_Controller.newline;
	}
	tekst = tekst + "-- States --" + class_Controller.newline;
	tekst = tekst + "DATA_DUMP_REQUESTED: " + this.fact_state_DATA_DUMP_REQUESTED + class_Controller.newline;
	return tekst;
    }
    private String description_card(String tekst) {
	if (isTransaction()) {
	    tekst = tekst + "Card -----" + class_Controller.newline;
	    tekst = tekst + "Asked for card: " + this.fact_askedCard + class_Controller.newline;
	    tekst = tekst + "Card entered: " + this.fact_cardEntered + class_Controller.newline;
	    if ((fact_cardEntered) || (fact_Issuer_ID.length() > 0)) {
		tekst = tekst + "CardDataResponse: " + this.fact_CardDataResponse + class_Controller.newline;
		tekst = tekst + "Issuer ID: " + this.fact_Issuer_ID + class_Controller.newline;
		tekst = tekst + "Issuer name: " + fact_issuerName + class_Controller.newline;
		tekst = tekst + "Entry mode used: " + fact_EntryMode.value + class_Controller.newline;
		tekst = tekst + "Bonus card: " + this.fact_BonusCardRead + " (event 17)" + class_Controller.newline;
		tekst = tekst + "Trumf card: " + this.fact_TrumfRead + "(Issuer 80/Trumf)" + class_Controller.newline;
	    }
	    tekst = addParam(tekst, "cardInTerminal");
	}
	return tekst;
    }
    private String description_issues(String tekst, String logFile) {
	if ((issues_last + issues_first).length() > 1) {
	    tekst = tekst + "SUSPECTS______________________" + class_Controller.newline;
	    tekst = tekst + "Issues: " + issues_first + issues_last + class_Controller.newline;
	    if (isLogg(logFile)) {
		tekst = tekst + (this.suspect_AID ? ("AID: " + this.suspect_AID + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_key ? ("Key: " + this.suspect_key + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_PPS ? ("PosPayService: " + this.suspect_PPS + class_Controller.newline) : "");
		tekst = tekst + (this.fact_AIDfound ? "" : ("AID found: " + this.fact_AIDfound + class_Controller.newline));
		tekst = tekst + (this.suspect_Cancelcustomer
			? ("Canceled by cardholder: " + this.suspect_Cancelcustomer + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_DeclinedTerminal
			? ("Declined by terminal: " + this.suspect_DeclinedTerminal + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_cancelTerminalsw
			? ("Cancel from terminal: " + this.suspect_cancelTerminalsw + class_Controller.newline) : "");
		tekst = tekst
			+ (this.suspect_DeclinedHost ? ("Declined by host: " + this.suspect_DeclinedHost + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_DeclinedServer
			? ("Declined by server: " + this.suspect_DeclinedServer + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_DeclinedClient
			? ("Declined from PosPayService: " + this.suspect_DeclinedClient + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_cancelPPCL
			? ("Cancel from PosPayService: " + this.suspect_cancelPPCL + class_Controller.newline) : "");
		tekst = tekst + (this.suspect_cancelECR ? ("Cancel from ECR: " + this.suspect_cancelECR + class_Controller.newline) : "");
		tekst = tekst + "Solutions: " + solutions + "";
	    }
	}
	return tekst;
    }
    private boolean isTransaction() {
	if (fact_service != null) {
	    // System.out.println("isTransaction fact_service=" + fact_service);
	    if (fact_service.value != null) {
		// System.out.println("isTransaction fact_service.value A=" + fact_service.value);
		if ((!fact_service.value.contains("Closing")) && (!fact_service.value.contains("Starting"))
			&& (!fact_service.value.contains("login")) && (!fact_service.value.contains("flush"))
			&& (!fact_service.value.contains("merchant")) && (!fact_service.value.contains("receipt"))
			&& (!fact_service.value.contains("getProperties")) && (!fact_service.value.contains("reconciliation"))) {
		    // System.out.println("isTransaction fact_service.value B=" + fact_service.value);
		    if (fact_RequestReceived) {
			return true;
		    }
		}
	    }
	}
	return false;
    }
    private boolean isLogg(String logFile) {
	if ((class_Controller.config.getParam("Terminal type") != null) || logFile.contains("comserver")) {
	    return true;
	}
	return false;
    }
    private boolean isTerminalLogg() {
	// System.out.println("isTerminalLogg " + class_Controller.config.getParam("debug.log"));
	if (class_Controller.config.getParam("debug.log") != null) {
	    if (class_Controller.config.getParam("debug.log").equals("true")) {
		return true;
	    }
	}
	return false;
    }
    private void addSolution(String add) {
	if (!this.solutions.contains(add)) {
	    solutions = solutions + " - " + add + class_Controller.newline;
	}
    }
    private void addIssue(String add, boolean front, String source) {
	// System.out.println("addIssue " + add + " " + source);
	if (add.contains("validate")) {}
	// }
	if (add.length() <= 1) {
	    try {
		throw new Exception();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	if (!this.issues_last.contains(add) && !this.issues_first.contains(add)) {
	    if (front) {
		issues_first = add + ", " + issues_first;
	    } else {
		issues_last = issues_last + add + ", ";
	    }
	}
    }
    private String addParam(String tekst, String param) {
	if (class_Controller.config.getParam(param) != null) {
	    if (param.contains("Approved")) {
		if (class_Controller.config.getParam(param).equals(0)) {
		    return tekst = tekst
			    + (param + ": " + this.class_Controller.config.getParam(param) + "(Reject)" + class_Controller.newline);
		} else if (class_Controller.config.getParam(param).equals(1)) {
		    return tekst = tekst
			    + (param + ": " + this.class_Controller.config.getParam(param) + "(Approved)" + class_Controller.newline);
		} else if (class_Controller.config.getParam(param).equals(2)) {
		    return tekst = tekst
			    + (param + ": " + this.class_Controller.config.getParam(param) + "(Unknown)" + class_Controller.newline);
		}
	    } else if (param.contains("pospay.client.pinBypass.enabled")) {
		if (class_Controller.config.getParam(param).equals(true)) {
		    return tekst = tekst + (param + ": " + this.class_Controller.config.getParam(param) + "(Pin bypass always forced)"
			    + class_Controller.newline);
		} else {
		    return tekst = tekst + (param + ": " + this.class_Controller.config.getParam(param) + class_Controller.newline);
		}
	    } else {
		return tekst = tekst + (param + ": " + this.class_Controller.config.getParam(param) + class_Controller.newline);
	    }
	}
	return tekst;
    }
}
