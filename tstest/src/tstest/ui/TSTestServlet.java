package tstest.ui;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JTextArea;
import javax.telephony.CallEvent;
import javax.telephony.ConnectionEvent;
import javax.telephony.MetaEvent;
import javax.telephony.TerminalConnectionEvent;
import javax.telephony.TerminalConnectionListener;

import tstest.TSTestData;
import tstest.TSTestService;

import com.avaya.jtapi.tsapi.tsapiInterface.Tsapi;

public class TSTestServlet extends HttpServlet  {

	private static final long serialVersionUID = 1L;

	public static final String DATA = "data";
	public static final String LISTENER = "listener";
	public static final String SERVICE = "service";
	public static final String SERVICES = "services";
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost DATA:LISTENER:SERVICE:SERVICES "+DATA+": "+LISTENER+": "+SERVICE+": "+SERVICES);
		if (request.getRequestURI().endsWith("login.do")){
			Map<String, String> errors = saveCredentials(request, response);
			if (errors == null){
				response.sendRedirect("test.jsp");
			}
			else{
				response.sendRedirect("login.jsp");
			}
		} 
		else if (request.getRequestURI().endsWith("call.do")){
			Map<String, String> errors = saveCallData(request, response);
			if (errors ==null){
				TSTestService testService = (TSTestService)request.getSession().getAttribute(SERVICE);
				System.out.println("request.getSession().getAttribute(SERVICE) "+request.getSession().getAttribute(SERVICE).toString());
				Listener listener = (Listener)request.getSession().getAttribute(LISTENER);
				System.out.println("request.getSession().getAttribute(LISTENER) "+request.getSession().getAttribute(LISTENER).toString());
				if (!listener.isLoggedIn()){
					try {
						testService.login();
						listener.setLoggedIn(true);
					} catch (Exception e) {
						response.sendRedirect("test.jsp");
						return;
					}
				}
				if (!listener.isConnected()){
					testService.makeCall();              //  **********make call()
				}
				else{
					testService.hangup();
				}
				response.sendRedirect("test.jsp");
			}
			else{
				response.sendRedirect("test.jsp");
			}
		}
		else {
			throw new IllegalArgumentException("Unsupported operation");
		}
	}

	private Map<String, String> saveCallData(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(" in saveCallData ");
		String caller = request.getParameter("caller");
		String callee = request.getParameter("callee");
		String service = request.getParameter("service");
		if (request.getSession().getAttribute(DATA)==null){
			request.getSession().setAttribute(DATA, new TSTestData());
		}
		System.out.println("request.getSession().getAttribute(DATA) "+request.getSession().getAttribute(DATA).toString());
		TSTestData data = (TSTestData)request.getSession().getAttribute(DATA);
		data.setCaller(caller);
		data.setCallee(callee);
		data.setService(service);
		Map<String, String> errors = data.errors();
		if (errors.get("caller") == null && errors.get("callee") == null && errors.get("service") == null){
			if (request.getSession().getAttribute(LISTENER)==null){
				request.getSession().setAttribute(LISTENER, new Listener());
			}
			Listener listener = (Listener)request.getSession().getAttribute(LISTENER);
			if (request.getSession().getAttribute(SERVICE)==null){
				request.getSession().setAttribute(SERVICE, new TSTestService(data, listener.getLog(), listener));
			}
			return null;
		}
		else{
			return errors;
		}
	}

	private Map<String, String> saveCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String server = request.getParameter("server");
		System.out.println(" in saveCredentials "+user+":"+password+":"+server);
		if (request.getSession().getAttribute(DATA)==null){
			request.getSession().setAttribute(DATA, new TSTestData());
		}
		TSTestData data = (TSTestData)request.getSession().getAttribute(DATA);
		data.setLogin(user);
		data.setPassword(password);
		data.setServer(server);
		Map<String, String> errors = data.errors();
		if (errors.get("login") == null && errors.get("server") == null && errors.get("password") == null){
			System.setProperty("com.avaya.jtapi.tsapi.servers", server);
			Tsapi.updateVolatileConfigurationValues(); //force an update
			request.getSession().setAttribute(SERVICES, Tsapi.getServices());
			return null;
		}
		else{
			return errors;
		}
	}
	
	public static class Listener implements TerminalConnectionListener{
		private final JTextArea log = new JTextArea();
		private boolean connected = false;
		private boolean loggedIn = false;
		public Listener(){
			
		}
		public void terminalConnectionActive(TerminalConnectionEvent arg0) {
			log.append("Received TerminalConnectionActive Event\n");
			connected = true;
		}

		public void terminalConnectionCreated(TerminalConnectionEvent arg0) {
			log.append("Received TerminalConnectionCreated Event\n");
		}

		public void terminalConnectionDropped(TerminalConnectionEvent arg0) {
			log.append("Received TerminalConnectionDropped Event\n");
			connected = false;
		}

		public void terminalConnectionPassive(TerminalConnectionEvent arg0) {
			log.append("Received TerminalConnectionPassive Event\n");
		}

		public void terminalConnectionRinging(TerminalConnectionEvent arg0) {
			log.append("Received TerminalConnectionRinging Event\n");
		}

		public void terminalConnectionUnknown(TerminalConnectionEvent arg0) {
			log.append("Received TerminalConnectionUnknown Event\n");
		}

		public void connectionAlerting(ConnectionEvent arg0) {
			log.append("Received ConnectionAlerting Event\n");
		}

		public void connectionConnected(ConnectionEvent arg0) {
			log.append("Received ConnectionConnected Event\n");
		}

		public void connectionCreated(ConnectionEvent arg0) {
			log.append("Received ConnectionCreated Event\n");
		}

		public void connectionDisconnected(ConnectionEvent arg0) {
			log.append("Received ConnectionDisconnected Event\n");
		}

		public void connectionFailed(ConnectionEvent arg0) {
			log.append("Received ConnectionFailed Event\n");
		}

		public void connectionInProgress(ConnectionEvent arg0) {
			log.append("Received ConnectionInProgress Event\n");
		}

		public void connectionUnknown(ConnectionEvent arg0) {
			log.append("Received ConnectionUnknown Event\n");
		}

		public void callActive(CallEvent arg0) {
			log.append("Received CallActive Event\n");
		}

		public void callEventTransmissionEnded(CallEvent arg0) {
			log.append("Received CallEventTransmissionEnded Event\n");
			connected = false;
		}

		public void callInvalid(CallEvent arg0) {
			log.append("Received CallInvalid Event\n");
		}

		public void multiCallMetaMergeEnded(MetaEvent arg0) {
			log.append("Received MultiCallMetaMergeEnded Event\n");
		}

		public void multiCallMetaMergeStarted(MetaEvent arg0) {
			log.append("Received MultiCallMetaMergeStarted Event\n");
		}

		public void multiCallMetaTransferEnded(MetaEvent arg0) {
			log.append("Received MultiCallMetaTransferEnded Event\n");
		}

		public void multiCallMetaTransferStarted(MetaEvent arg0) {
			log.append("Received MultiCallMetaTransferStarted Event\n");
		}

		public void singleCallMetaProgressEnded(MetaEvent arg0) {
			log.append("Received SingleCallMetaProgressEnded Event\n");
		}

		public void singleCallMetaProgressStarted(MetaEvent arg0) {
			log.append("Received SingleCallMetaProgressStarted Event\n");
		}

		public void singleCallMetaSnapshotEnded(MetaEvent arg0) {
			log.append("Received SingleCallMetaSnapshotEnded Event\n");
		}

		public void singleCallMetaSnapshotStarted(MetaEvent arg0) {
			log.append("Received SingleCallMetaSnapshotStarted Event\n");
		}
		public JTextArea getLog() {
			return log;
		}
		public boolean isConnected() {
			return connected;
		}
		public boolean isLoggedIn() {
			return loggedIn;
		}
		public void setLoggedIn(boolean loggedIn) {
			this.loggedIn = loggedIn;
		}
	}
}
