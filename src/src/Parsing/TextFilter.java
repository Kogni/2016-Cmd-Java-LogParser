package src.Parsing;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import src.Control.Controller;
import src.Problem.Interpretor2;

class TextFilter {

    TextFilter(Controller class_Controller) throws FileNotFoundException, UnsupportedEncodingException {

    }

    void ParseLine(String line, String logFile, Interpretor2 interpretor, String directory)
	    throws FileNotFoundException, UnsupportedEncodingException {
	System.out.println("Parsing lines: " + line);
	/*
	 * The lines below decide what information is picked up, interpreted and posted to parsing results
	 */

	if ((line.toLowerCase().contains(" DEBUG ".toLowerCase()))) {
	    return;
	} else if ((line.toLowerCase().contains("se.atg.".toLowerCase()))) {
	    return;
	} else {
	    // System.out.println("Parsing lines -DEBUG: " + line);

	    if ((line.toLowerCase().contains("WARN".toLowerCase()))
		    && (line.toLowerCase().contains("Thread interrupted while trying to take events from queue.".toLowerCase()) == false)
		    && (!line.toLowerCase().contains("warnOfflineLimit".toLowerCase()))
		    && (!line.toLowerCase().contains("Template for profile ".toLowerCase()))
		    && (!line.toLowerCase().contains("offlineWarn".toLowerCase()))
		    && (!line.toLowerCase().contains("Merchant/terminal id mapped to".toLowerCase()))
		    && (!line.toLowerCase().contains("Wrong response".toLowerCase()))
		    && (!line.toLowerCase().contains("Failed to disable keys ".toLowerCase()))) {
		ProcessLine("WARN 		- " + line, logFile, interpretor, "AA", directory);
	    } else

	    if ((line.toLowerCase().contains("Exception".toLowerCase()) || line.toLowerCase().contains("Caused by:".toLowerCase()))
		    && (line.toLowerCase().contains("Template".toLowerCase()) == false)
	    // && (line.toLowerCase().contains("Could not retrieve CardExtension from
	    // database".toLowerCase()) == false)
	    // && (line.toLowerCase().contains("not supported in CARD_EXTENSIONS
	    // state".toLowerCase()) == false)
	    // && (line.toLowerCase().contains("not supported in READY state".toLowerCase()) ==
	    // false)
	    ) {
		ProcessLine("EXCEPTION	- " + line, logFile, interpretor, "Å", directory);
	    } else

	    if (((line.toLowerCase().contains("timeout".toLowerCase())) || (line.toLowerCase().contains("time-out".toLowerCase()))
		    || (line.toLowerCase().contains(" timed ".toLowerCase())) || (line.toLowerCase().contains("timed out".toLowerCase())))
		    && (line.toLowerCase().contains("timeout=".toLowerCase()) == false)
		    && (line.toLowerCase().contains("0 TIMEOUT messages".toLowerCase()) == false)
		    && (line.toLowerCase().contains(".timeout.".toLowerCase()) == false)
		    && (line.toLowerCase().contains("TimeoutOptimizedSocketAccess".toLowerCase()) == false)
		    && (line.toLowerCase().contains("ConfigHelper".toLowerCase()) == false)
		    && (line.toLowerCase().contains("CREATE MEMORY TABLE".toLowerCase()) == false)
		    && (line.toLowerCase().contains("PosPayClientProxy".toLowerCase()) == false)) {
		ProcessLine("TIMEOUT	    - " + line, logFile, interpretor, "Ø", directory);
	    } else

	    if ((line.toLowerCase().contains("error".toLowerCase()))
		    && (line.toLowerCase().contains("Initialized OK".toLowerCase()) == false)
		    && (!line.toLowerCase().contains("errorString".toLowerCase()))
		    && (!line.toLowerCase().contains("0 ERROR messages".toLowerCase()))
		    && (!line.toLowerCase().contains("0 SYNTAX errors ".toLowerCase()))
		    && (line.toLowerCase().contains("Could not retrieve CardExtension from database".toLowerCase()) == false)) {
		ProcessLine("ERROR	    - " + line, logFile, interpretor, "Ø", directory);
	    } else

	    if ((line.toLowerCase().contains("abort".toLowerCase()))
		    && (line.toLowerCase().contains("abortOnInvalidVoiceAuthorizationCode".toLowerCase()) == false)
		    && (line.toLowerCase().contains("PosPayClientProxy".toLowerCase()) == false)
		    && (line.toLowerCase().contains("aborting all input-requests".toLowerCase()) == false)
		    && (line.toLowerCase().contains("abortReason".toLowerCase()) == false)) {
		ProcessLine("ABORT - " + line, logFile, interpretor, "Z", directory);
	    } else

	    if ((line.toLowerCase().contains("cancel".toLowerCase()))
		    && (line.toLowerCase().contains("Nothing to cancel".toLowerCase()) == false)
		    && (line.toLowerCase().contains("PosPayClientProxy".toLowerCase()) == false)
		    && (line.toLowerCase().contains("ReceiptVo".toLowerCase()) == false)
		    && (line.toLowerCase().contains("CREATE MEMORY TABLE".toLowerCase()) == false)
		    && (line.toLowerCase().contains("Send cancel to terminal and ecr if not in ready".toLowerCase()) == false)) {
		ProcessLine("CANCEL 		- " + line, logFile, interpretor, "X", directory);
	    } else

	    // User actions
	    if ((line.toLowerCase().contains("User cancelled the transaction".toLowerCase()))
		    || (line.toLowerCase().contains("Cancel called".toLowerCase()))
		    || (line.toLowerCase().contains(" - CANCELLED)".toLowerCase()))
		    || (line.toLowerCase().contains("00000027".toLowerCase()))) {
		ProcessLine("CANCEL	 - " + line, logFile, interpretor, "R", directory);
	    } else

	    // general known problem phrases
	    if (((line.toLowerCase().contains("Payment card does not allow split payment".toLowerCase()))
		    || (line.toLowerCase().contains("Rejected offline".toLowerCase()))
		    || (line.toLowerCase()
			    .contains("Fallback not allowed / Fallback not allowed offline / Fallback took too long.".toLowerCase()))
		    || (line.toLowerCase().contains("(code ".toLowerCase()))
		    || (line.toLowerCase().contains("Duplicate transaction, canceling".toLowerCase()))
		    || (line.toLowerCase().contains("Failed to deliver message stored offline".toLowerCase()))
		    || (line.toLowerCase().contains("Error packing ISO message".toLowerCase()))
		    || (line.toLowerCase().contains("NEW_KEYS_COMMAND command failed".toLowerCase()))
		    || (line.toLowerCase().contains("Unhandled TSI".toLowerCase()))
		    || (line.toLowerCase().contains("Flush failed".toLowerCase()))
		    || (line.toLowerCase().contains("Failed to download list".toLowerCase()))
		    || (line.toLowerCase().contains("Could not get cardissuer".toLowerCase()))
		    || (line.toLowerCase().contains("Error occured during processing".toLowerCase()))
		    || (line.toLowerCase().contains("Mismatch with client".toLowerCase()))
		    || (line.toLowerCase().contains("track1=null, track2=null, track3=null".toLowerCase()))
		    || (line.toLowerCase().contains("Unable to extract track data".toLowerCase()))
		    || (line.toLowerCase().contains("No AID definition found".toLowerCase()))
		    || (line.toLowerCase().contains("Transaction was online rejected by acquirer".toLowerCase()))
		    || (line.toLowerCase().contains("PIN entry required and PIN pad not present or not working".toLowerCase()))
		    || (line.toLowerCase().contains("Transaction was rejected with no specified reason".toLowerCase()))
		    || (line.toLowerCase().contains("ERROR RESPONSE FROM HOST".toLowerCase()))
		    || (line.toLowerCase().contains("Parameter/format/BINCSV failure".toLowerCase()))
		    || (line.toLowerCase().contains("system error".toLowerCase()))
		    || (line.toLowerCase().contains("unsuccessful".toLowerCase()))
		    || (line.toLowerCase().contains("could not".toLowerCase()))
		    || (line.toLowerCase().contains("no cardissuer".toLowerCase()))
		    || (line.toLowerCase().contains("unable to".toLowerCase())) || (line.toLowerCase().contains("is needed".toLowerCase()))
		    || (line.toLowerCase().contains("at com.payex.pospay.client".toLowerCase()))
		    || (line.toLowerCase().contains("incorrectly".toLowerCase()))
		    || (line.toLowerCase().contains("No masked pan in the complete response".toLowerCase()))
		    || ((line.toLowerCase().contains("reset".toLowerCase())) && (!line.toLowerCase().contains("idle reset".toLowerCase()))
			    && (!line.toLowerCase().contains("GpaAccess: reset() ".toLowerCase())))
		    || (line.toLowerCase().contains("not allowed".toLowerCase())) || (line.toLowerCase().contains("wrong".toLowerCase()))
		    || (line.toLowerCase().contains("mismatch".toLowerCase()))
		    || (line.toLowerCase().contains("TransactionStatusUI".toLowerCase())))
		    || (line.toLowerCase().contains("Notice:".toLowerCase()))
		    || (line.toLowerCase().contains("problem reading ".toLowerCase()))
		    && ((line.toLowerCase().contains("CREATE MEMORY TABLE".toLowerCase()) == false))) {
		ProcessLine("ISSUE 		- " + line, logFile, interpretor, "V", directory);
	    } else

	    // Start action-indicators
	    if ((line.toLowerCase().contains("Request: [ENCR]".toLowerCase()))
		    // || (line.toLowerCase().contains("Downloading".toLowerCase()))
		    || (line.toLowerCase().contains("###START###".toLowerCase()))
		    || (line.toLowerCase().contains("[ecr] Starting state machine".toLowerCase()))
		    || (line.toLowerCase().contains("Starting event".toLowerCase()))
		    || (line.toLowerCase().contains("[trx] Initiating ".toLowerCase()))
		    || (line.toLowerCase().contains("Transaction received".toLowerCase()))// terminal
		    || (line.toLowerCase().contains("Executing ".toLowerCase()))// terminal
		    || (line.toLowerCase().contains("ChainSequence".toLowerCase()))// api_ppp
		    || (line.toLowerCase().contains("Opened connection:".toLowerCase()))// api_ppp
		    || (line.toLowerCase().contains("ProcessStart time: ".toLowerCase()))) {
		ProcessLine("TRX START	" + line, logFile, interpretor, "A", directory);
	    } else

	    // Activity indicators
	    // API cmd
	    if ((line.toLowerCase().contains("APIServiceRequestHandler: /".toLowerCase())) // debug
		    || (line.toLowerCase().contains("BasicInformationPanel: executing ".toLowerCase()))
		    || (line.toLowerCase().contains("CorePosPayClientImpl: ".toLowerCase()))
		    || (line.toLowerCase().contains("SendHttpApiRequest: Sending ".toLowerCase()))) {
		ProcessLine("API CMD		- " + line, logFile, interpretor, "B", directory);
	    } else

	    // SERVICE
	    if ((line.toLowerCase().contains("***** Running service".toLowerCase()))
		    || (line.toLowerCase().contains("Running service".toLowerCase()))
		    || (line.toLowerCase().contains("Protel Bridge running".toLowerCase()))
		    || (line.toLowerCase().contains(" is done *****".toLowerCase()))) {
		ProcessLine("SERVICE		- " + line, logFile, interpretor, "C", directory);
	    } else

	    // STATES
	    if ((line.toLowerCase().contains("****STATE CHANGE: Changed state to ".toLowerCase()))
		    || (line.toLowerCase().contains("Setting state to ".toLowerCase()))) {
		ProcessLine("STATE		- " + line, logFile, interpretor, "D", directory);
	    } else

	    // METHOD
	    if (((line.toLowerCase().contains("***** Executing command:".toLowerCase()))
		    || (line.toLowerCase().contains(" is done ***** ".toLowerCase()))
		    || (line.toLowerCase().contains("***** Done executing command:".toLowerCase())))
		    && (!line.toLowerCase().contains("CommunicationBroke".toLowerCase()))) {
		ProcessLine("METHOD		- " + line, logFile, interpretor, "E", directory);
	    } else

	    // RESPONSE
	    if ((line.toLowerCase().contains("Response".toLowerCase()))
		    && (line.toLowerCase().contains("CompleteResponseHandler".toLowerCase()) == false)
		    && (line.toLowerCase().contains("HandleCompleteResponse".toLowerCase()) == false)
		    && (line.toLowerCase().contains("DefaultResponseProvider".toLowerCase()) == false)
		    && (line.toLowerCase().contains("TransactionCommandResponse".toLowerCase()) == false)
		    && (line.toLowerCase().contains("hostResponseCode".toLowerCase()) == false)
		    && (line.toLowerCase().contains("WAIT_FOR_RESPONSE".toLowerCase()) == false)
		    && (line.toLowerCase().contains("getResponse".toLowerCase()) == false)
		    && (line.toLowerCase().contains(".response.".toLowerCase()) == false)
		    && (line.toLowerCase().contains("waiting for ".toLowerCase()) == false)
		    && (line.toLowerCase().contains(",response".toLowerCase()) == false)
		    && (line.toLowerCase().contains(", response".toLowerCase()) == false)
		    && (line.toLowerCase().contains("\"inputResponses\"".toLowerCase()) == false)
		    && (line.toLowerCase().contains("AUTHORIZATION_RESPONSE_CODE".toLowerCase()) == false)
		    && (line.toLowerCase().contains("ADDITIONAL_RESPONSE_DATA_P44".toLowerCase()) == false)
		    && (line.toLowerCase().contains("response-session".toLowerCase()) == false)
		    && (!line.toLowerCase().contains("PosPayClientProxy: 	response".toLowerCase()))
		    && (line.toLowerCase().contains("PosPayClientProxy".toLowerCase()) == false)
		    && (line.toLowerCase().contains("ConfigHelper".toLowerCase()) == false)
		    && (line.toLowerCase().contains("INSERT INTO RESPONSES VALUES".toLowerCase()) == false)
		    && (line.toLowerCase().contains("DELETE FROM RESPONSES".toLowerCase()) == false)
		    && (line.toLowerCase().contains("ALTER TABLE".toLowerCase()) == false)
		    && (line.toLowerCase().contains("GenericInputRequestResponseValidatorImpl".toLowerCase()) == false)
		    && (line.toLowerCase().contains("[ecr]".toLowerCase()) == false)
		    && (line.toLowerCase().contains("response.id".toLowerCase()) == false)) {
		ProcessLine("RESPONSE	- " + line, logFile, interpretor, "F", directory);
	    } else

	    // REQUEST
	    if (((line.toLowerCase().contains("request".toLowerCase())))
		    && (line.toLowerCase().contains("originalRequestType".toLowerCase()) == false)
		    && (line.toLowerCase().contains("orgRequestType".toLowerCase()) == false)
		    && (line.toLowerCase().contains("Requesting".toLowerCase()) == false)
		    && (line.toLowerCase().contains(", request:".toLowerCase()) == false)
		    && (line.toLowerCase().contains("Added track to server request".toLowerCase()) == false)
		    && (line.toLowerCase().contains("Created request".toLowerCase()) == false)
		    && (line.toLowerCase().contains("APIServiceRequestHandler".toLowerCase()) == false)
		    && (line.toLowerCase().contains("RequestStateContext".toLowerCase()) == false)
		    && (line.toLowerCase().contains("HttpRequestExecutor".toLowerCase()) == false)
		    && (line.toLowerCase().contains("pendingRequest".toLowerCase()) == false)
		    && (line.toLowerCase().contains("RequestState".toLowerCase()) == false)
		    && (line.toLowerCase().contains("DefaultRequestDirector".toLowerCase()) == false)
		    && (line.toLowerCase().contains("Merchant id in request".toLowerCase()) == false)
		    && (line.toLowerCase().contains("SchemaUpdate".toLowerCase()) == false)
		    && (line.toLowerCase().contains("INPUTREQUEST".toLowerCase()) == false)
		    && (line.toLowerCase().contains("OfflineRequestJob".toLowerCase()) == false)
		    && (line.toLowerCase().contains("REQUEST_EXPIRE_DATE_RETRY".toLowerCase()) == false)
		    && (line.toLowerCase().contains(".request=".toLowerCase()) == false)
		    && (line.toLowerCase().contains("RequestSentState".toLowerCase()) == false)
		    && (line.toLowerCase().contains("request.".toLowerCase()) == false)
		    && (line.toLowerCase().contains("APIServiceRequestHandler".toLowerCase()) == false)
		    && (line.toLowerCase().contains("Handling transaction result request".toLowerCase()) == false)
		    && (line.toLowerCase().contains("SendHttpApiRequest".toLowerCase()) == false)
		    && (line.toLowerCase().contains("No offline transactions pending".toLowerCase()) == false)
		    && (line.toLowerCase().contains("PosPayClientProxy".toLowerCase()) == false)
		    && (line.toLowerCase().contains("INSERT INTO REQUESTS VALUES".toLowerCase()) == false)
		    && (line.toLowerCase().contains("ALTER TABLE".toLowerCase()) == false)
		    && (line.toLowerCase().contains("DELETE FROM REQUESTS".toLowerCase()) == false)
		    && (line.toLowerCase().contains("CREATE MEMORY TABLE".toLowerCase()) == false)

		    && (line.toLowerCase().contains("RequestStateContext".toLowerCase()) == false)) {
		ProcessLine("REQUEST		- " + line, logFile, interpretor, "G", directory);
	    } else

	    // ECR info
	    if (line.toLowerCase().contains("Jar file loaded".toLowerCase())) {
		ProcessLine("JAR	- " + line, logFile, interpretor, "I", directory);
	    } else

	    if (line.toLowerCase().contains("ItemRestricted".toLowerCase())) {
		ProcessLine("RESTRICTION	- " + line, logFile, interpretor, "I2", directory);
	    } else
	    // pospay client version
	    if (line.toLowerCase().contains("PosPay client version".toLowerCase())) {
		ProcessLine("PPCL VERSION	- " + line, logFile, interpretor, "J", directory);
		/*
		 * } else if (line.toLowerCase().contains("AMOUNT_AUTHORIZED".toLowerCase())) { ProcessLine(line, logFile, interpretor,
		 * "E");
		 */
	    } else

	    // term sw client version
	    if (line.toLowerCase().contains("software version: ".toLowerCase())) {
		ProcessLine("TERM SW VERSION	- " + line, logFile, interpretor, "J", directory);
		/*
		 * } else if (line.toLowerCase().contains("AMOUNT_AUTHORIZED".toLowerCase())) { ProcessLine(line, logFile, interpretor,
		 * "E");
		 */
	    } else

	    if ((line.toLowerCase().contains("PAN is".toLowerCase())) || (line.toLowerCase().contains(",\"pan\":".toLowerCase()))) {
		ProcessLine("PAN	- " + line, logFile, interpretor, "K", directory);
	    } else

	    // Completeresponse TLV tags:
	    if ((line.toLowerCase().contains("TLV Tags:".toLowerCase()))//
		    || (line.toLowerCase().contains(" 9f998015".toLowerCase()))// SOFTWARE_DATE
		    || (line.toLowerCase().contains(" 9f998013".toLowerCase()))// SOFTWARE_VERSION_NAME
		    || (line.toLowerCase().contains(" 9f998012".toLowerCase()))// EVENT_TEXT
		    || (line.toLowerCase().contains(" 9f99801e".toLowerCase()))// OFFLINE_COUNT
		    || (line.toLowerCase().contains(" 9f998004".toLowerCase()))// TRACK2
		    || (line.toLowerCase().contains(" 9f99800a".toLowerCase()))// FALLBACK
		    || (line.toLowerCase().contains(" 009f9920".toLowerCase()))// AUTHORIZATION_METHOD
		    || (line.toLowerCase().contains(" 009f991f".toLowerCase()))// ID_METHOD
		    || (line.toLowerCase().contains(" 009f9913".toLowerCase()))// ONLINE_INDICATOR
		    || (line.toLowerCase().contains(" 009f9919".toLowerCase()))// APPLICATION_EXPIRY_DATE
		    || (line.toLowerCase().contains(" 009f9907".toLowerCase()))// TRANSACTION_STATUS
		    || (line.toLowerCase().contains(" 009f9904".toLowerCase()))// TRANSACTION_ID
		    || (line.toLowerCase().contains(" 009f990e".toLowerCase()))// PAN
		    || (line.toLowerCase().contains(" 00009f39".toLowerCase()))// POS_ENTRY_MODE
		    || (line.toLowerCase().contains(" 9f998045".toLowerCase()))// ENTRY_MODE
		    || (line.toLowerCase().contains(" 00000095".toLowerCase()))// TERMINAL_VERIFICATION_RESULTS
		    || (line.toLowerCase().contains(" 0000008a".toLowerCase()))// AUTHORIZATION_RESPONSE_CODE
		    || (line.toLowerCase().contains(" 009f990b".toLowerCase()))// ISSUER_ID
		    || (line.toLowerCase().contains(" 009f990f".toLowerCase()))// PAYMENT_APPLICATION_LABEL
		    || (line.toLowerCase().contains(" 009f9936".toLowerCase()))// PAYMENT_APPLICATION_NAME
		    || (line.toLowerCase().contains(" 9f998001".toLowerCase()))// ADDITIONAL_RESPONSE_DATA_P44,
									       // for bl.a
									       // trumf
		    || (line.toLowerCase().contains(" 9f998031".toLowerCase()))// TMS update result
		    || (line.toLowerCase().contains(" 9f998046".toLowerCase()))// CANCEL_RESPONSE
		    || (line.toLowerCase().contains(" 9f99803c".toLowerCase()))// ECR_TAG_CARD_TYPE_DIALOG
		    || (line.toLowerCase().contains("CardDataResponse data".toLowerCase()))) {
		// System.out.println("Rawline picked: " + line);
		ProcessLine("TLV			- " + line, logFile, interpretor, "L", directory);
	    } else

	    // API service request handler response
	    if ((line.toLowerCase().contains("\"hostResponseCode\"".toLowerCase()))
		    || (line.toLowerCase().contains("\"canceled\"".toLowerCase()))
		    || (line.toLowerCase().contains(" 009f991f".toLowerCase()))
		    || (line.toLowerCase().contains("BasicInformationPanel: executing ".toLowerCase()))
		    || (line.toLowerCase().contains("\"acquirerString\"".toLowerCase()))) {
		// System.out.println("Rawline picked: " + line);
		ProcessLine("API RSP		- " + line, logFile, interpretor, "M", directory);
	    } else

	    // Storepoint TLV info
	    if (line.toLowerCase().contains("Auth code:".toLowerCase())) {
		ProcessLine("AUTH CODE	 - " + line, logFile, interpretor, "N", directory);
	    } else

	    // communication
	    if ((line.toLowerCase().contains("Transaction has been forced offline".toLowerCase()))
		    || (line.toLowerCase().contains("COMMS ERROR".toLowerCase()))
		    || (line.toLowerCase().contains("timed out".toLowerCase()))
		    || (line.toLowerCase().contains("OFFLINE_COUNT".toLowerCase()))
		    || (line.toLowerCase().contains("Online/offline:			OFFLINE".toLowerCase()))
		    || (line.toLowerCase().contains("Timeout on ".toLowerCase()))) {
		ProcessLine("OFFLINE	    - " + line, logFile, interpretor, "T", directory);
	    } else if (line.toLowerCase().contains("Online/offline:".toLowerCase())) {
		ProcessLine("ONLINE		 - " + line, logFile, interpretor, "O", directory);
	    } else

	    // EID
	    if ((line.toLowerCase().contains("complete-event received:".toLowerCase()))
		    || (line.toLowerCase().contains(" dispatching ".toLowerCase()))
		    // || (line.toLowerCase().contains("[i18n] Message ".toLowerCase()))//for
		    // mye unyttig info
		    // || (line.toLowerCase().contains("[i18n] Message ".toLowerCase())) //for
		    // mye unyttig info
		    || (line.toLowerCase().contains("eventDescription".toLowerCase()))
		    || (line.toLowerCase().contains("transactionStateInformation".toLowerCase()))
		    || (line.toLowerCase().contains("received event".toLowerCase()))
		    || (line.toLowerCase().contains(": Event(".toLowerCase()))
		    || (line.toLowerCase().contains("DefaultEventDispatcher:".toLowerCase()))
		    || (line.toLowerCase().contains("eventid :".toLowerCase()))) {
		ProcessLine("EVENT		- " + line, logFile, interpretor, "Q", directory);
	    } else

	    // TVR
	    if ((line.toLowerCase().contains("**Byte ".toLowerCase()))) {
		ProcessLine("TVR - " + line, logFile, interpretor, "S", directory);
	    } else

	    // External issues

	    // rejection o.l
	    if ((line.toLowerCase().contains("rejected".toLowerCase())
		    && ((line.toLowerCase().contains("CREATE MEMORY TABLE".toLowerCase()) == false)))) {
		ProcessLine("REJECT		- " + line, logFile, interpretor, "U", directory);
	    } else
	    /*
	     * if ((line.toLowerCase().contains("avvist".toLowerCase()))) { ProcessLine(line, logFile, interpretor); } else
	     */

	    // general error phrases
	    if (((line.toLowerCase().contains("failed".toLowerCase())) && (line.toLowerCase()
		    .contains("Failed to connect to pospay event service. Retrying in 1000 milliseconds if timer is not exhausted"
			    .toLowerCase()) == false))
		    || (line.toLowerCase().contains("Transaction failed:".toLowerCase()))
		    // || (line.toLowerCase().contains("Teknisk
		    // feil".toLowerCase()))
		    // || (line.toLowerCase().contains("SendHttpApiRequest: Status
		    // code ".toLowerCase()))
		    || (line.toLowerCase().contains("[ecr] Sending event.".toLowerCase()))) {
		ProcessLine("FAIL		- " + line, logFile, interpretor, "P", directory);
	    } else

	    if ((line.toLowerCase().contains("invalid ".toLowerCase()))) {
		ProcessLine("INVALID 	- " + line, logFile, interpretor, "Y", directory);
	    } else

	    if ((line.toLowerCase().contains("errno".toLowerCase()))) {
		ProcessLine("ERRNO - " + line, logFile, interpretor, "Æ", directory);
	    } else

	    if ((line.toLowerCase().contains("cardIssName".toLowerCase()))
		    // || (line.toLowerCase().contains("PAYMENT_APPLICATION_".toLowerCase()))
		    || (line.toLowerCase().contains("Found card issuer".toLowerCase()))
	    // || (line.toLowerCase().contains("ISSUER_ID".toLowerCase()))
	    ) {
		ProcessLine("IDENTIFY	 - " + line, logFile, interpretor, "a2", directory);
	    } else

	    if ((line.toLowerCase().contains("Accepted connection from".toLowerCase()))
		    || (line.toLowerCase().contains("Connected to".toLowerCase()))
		    || (line.toLowerCase().contains("Connecting to ".toLowerCase()))
		    || (line.toLowerCase().contains("Connected".toLowerCase()))
		    || (line.toLowerCase().contains("Accepting connections..".toLowerCase()))
		    || (line.toLowerCase().contains("Communication check:".toLowerCase()))
		    || (line.toLowerCase().contains("Successfully started socket at".toLowerCase()))) {
		ProcessLine("CONNECTION	 - " + line, logFile, interpretor, "a22", directory);
	    } else

	    if (
	    // (line.toLowerCase().contains("configGetParam()".toLowerCase())) ||
	    (line.toLowerCase().contains("supportsAddPrint".toLowerCase()))
		    || (line.toLowerCase().contains("Current config:".toLowerCase()))
		    || (line.toLowerCase().contains(".CorePosPayClientProxy: 	".toLowerCase()))) {
		ProcessLine("SETTINGS - " + line, logFile, interpretor, "a3", directory);
	    } else if (((line.toLowerCase().contains("Shutting down".toLowerCase()))
		    || (line.toLowerCase().contains("[trx] End of transaction".toLowerCase())) // terminal
		    || (line.toLowerCase().contains(" ms elapsed since ".toLowerCase()))
		    || (line.toLowerCase().contains("[26]".toLowerCase()))
		    || (line.toLowerCase().contains("Message to send (serial-style): 06".toLowerCase()))
		    || (line.toLowerCase().contains("sending cancel request to terminal".toLowerCase()))
		    || (line.toLowerCase().contains("Found AID definition".toLowerCase()))
		    || (line.toLowerCase().contains("getPosPayClient starting".toLowerCase()))
		    || (line.toLowerCase().contains("initialize starting".toLowerCase()))
		    || (line.toLowerCase().contains("getPosPayClient returning result".toLowerCase()))
		    || (line.toLowerCase().contains("Status code ".toLowerCase()))
		    || (line.toLowerCase().contains("Starting PosPay Client... ".toLowerCase()))
		    || (line.toLowerCase().contains("Closed connection:".toLowerCase()))
		    || (line.toLowerCase().contains("Received data from GPA".toLowerCase()))
		    || (line.toLowerCase().contains("Transaction complete".toLowerCase()))
		    || (line.toLowerCase().contains("Handling: ".toLowerCase())) || (line.toLowerCase().contains("Loyalty".toLowerCase()))
		    || (line.toLowerCase().contains("split payment".toLowerCase()))
		    || (line.toLowerCase().contains("pospay.client.address=".toLowerCase()))
		    || (line.toLowerCase().contains("pospay.client.address2=".toLowerCase()))
		    // SSL
		    || (line.toLowerCase().contains("localeventhandler.enable".toLowerCase()))
		    || (line.toLowerCase().contains(".ssl.".toLowerCase())) || (line.toLowerCase().contains(".nfc.".toLowerCase()))
		    || (line.toLowerCase().contains("eventHandler.timeout".toLowerCase()))
		    //
		    || (line.toLowerCase().contains("CVP:".toLowerCase())) || (line.toLowerCase().contains("Tag 1001".toLowerCase()))
		    // || (line.toLowerCase().contains("[emv]".toLowerCase())) //for mye info
		    || (line.toLowerCase().contains("EMVDC step:".toLowerCase())) || (line.toLowerCase().contains("Declined".toLowerCase()))
		    || (line.toLowerCase().contains("[trx]".toLowerCase()))// mye unødvendig info
		    || (line.toLowerCase().contains("Added entry: Tag 200".toLowerCase()))
		    // || (line.toLowerCase().contains(".relay.".toLowerCase()))//for mye info
		    || (line.toLowerCase().contains("server.".toLowerCase()))
		    || (line.toLowerCase().contains(".connectionType".toLowerCase()))
		    || (line.toLowerCase().contains("AMOUNT_AUTHORIZED".toLowerCase()))
		    || (line.toLowerCase().contains("[host]".toLowerCase())) // terminal
		    || (line.toLowerCase().contains("[trxDB]".toLowerCase())) // terminal
		    || (line.toLowerCase().contains("LinkLayer".toLowerCase())) // terminal
		    || (line.toLowerCase().contains("[hmi]".toLowerCase())) // terminal
		    || (line.toLowerCase().contains("Starting PosPay Client...".toLowerCase()))
		    || (line.toLowerCase().contains("DISCONNECT".toLowerCase()))
		    || (line.toLowerCase().contains("openPED called..".toLowerCase()))
		    || (line.toLowerCase().contains("Connected to ".toLowerCase()))
		    || (line.toLowerCase().contains("CardExtensionIdentifier".toLowerCase()))
		    || (line.toLowerCase().contains("Param found. ".toLowerCase()))
		    || (line.toLowerCase().contains("pospay.client.".toLowerCase())
			    && !line.toLowerCase().contains("com.payex.pospay.client".toLowerCase()))
			    // || (line.toLowerCase().contains("APIServiceRequestHandler: /".toLowerCase()))//debug
		    || (line.toLowerCase().contains("startPosPayService sleeping".toLowerCase()))
		    || (line.toLowerCase().contains("INFO  [main] com.pos.pospayinterface.CorePosPayClientProxy: 	".toLowerCase()))// ppcl
																	 // config
		    || (line.toLowerCase().contains("INSERT INTO ".toLowerCase()))
		    || (line.toLowerCase().contains(" approved".toLowerCase())) || (line.toLowerCase().contains(" valid".toLowerCase())))
		    && ((line.toLowerCase().contains("CREATE MEMORY TABLE".toLowerCase()) == false))) {
		ProcessLine("INFO		- " + line, logFile, interpretor, "H", directory);

	    } else if (logFile.contains("config.properties")) {
		if ((line.toLowerCase().contains("pospay.client.".toLowerCase()))) {
		    ProcessLine("CONFIG		- " + line, logFile, interpretor, "H", directory);
		}
	    } else {
		// System.out.println(this.getClass().toString() + " ProcessLine un-caught info: " +
		// line);
	    }
	}

    }

    private void ProcessLine(String line, String logFile, Interpretor2 interpretor, String Source, String directory)
	    throws FileNotFoundException, UnsupportedEncodingException {
	System.out.println(this.getClass().toString() + " line: " + line);
	if (line.contains("Avvist") == true) {
	    // System.out.println(this.getClass().toString() + " line: " + line);
	}
	// if (line.contains("supportsAddPrint") == true) {
	// System.out.println(this.getClass().toString() + " ProcessLine: " + line + " source=" +
	// Source);
	// }
	interpretor.CatchInfo(line, logFile, directory);
    }

    void StartNewParse() {

    }
}
