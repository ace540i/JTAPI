package click2call;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.telephony.Address;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.PlatformException;
import javax.telephony.Provider;
import javax.telephony.Terminal;
import javax.telephony.callcontrol.CallControlCall;

import com.avaya.jtapi.tsapi.ITsapiCallIDPrivate;
 
import click2call.ui.MyTextArea;
import click2call.ui.TraceOutputUi;

import click2call.ui.DirectoryLookupGUI;
import click2call.ui.LoginDialog;
import click2call.ui.Click2CallGUI;
import click2call.ui.TraceFrame;
import click2call.TableConstants.CallLogTableConstants;;

public class Click2Call extends JFrame
{
	JtapiPeer       jtapiPeer;
	Provider        provider;
	Terminal        myTerminal;
	Address         myAddress;
	
	private static final long serialVersionUID = 1L;
	private final String SECURITY_CREDENTAILS = "ldap_securityCredentails";
	private final String SECURITY_PRINCIPAL = "ldap_securityPrincipal";
	private String providerUrl;
    private String baseDN;
    private String securityPrincipal;
    private String securityCredentials;       

    // name of properties file.
    private final String PROPERTY_FILE = "click2call.properties";
    private final String PROVIDER_URL = "providerUrl";    
    private final String BASE_DN = "baseDN";    
    
	private DirectoryLookupGUI dirLookupGui;
	private DisplayParser callParser;
	private LDAPQueryExecutor queryExecutor;
	
	private TraceOutputUi trace = null;/* new MyTextArea(); */
	private MyProviderListener myProviderListener;
	
	private int row = 0;
	private int deleted = 0;
		
	private HashMap<Integer, Integer> answeredHash = new HashMap<Integer, Integer>(10);
	private HashMap<Integer, Integer> calledBackHash = new HashMap<Integer, Integer>(10);
	
	//Storing call object in calltodrophash so that it can be used to drop the call
	private HashMap<String, CallControlCall> callToDropHash = new HashMap<String, CallControlCall>(10);
	
	private CallStatus call_status = null;
	
	private static final Color BUTTON_BKGD_COLOR = new Color(80, 90, 170);
	private static final Color MISSED_CALL_COLOR = new Color(240, 200, 200);
	private static final Color DIALED_CALL_COLOR = new Color(255, 200, 100);
	private static final Color CALLED_BACK_CALL_COLOR = new Color(220, 180, 140);
	
	private JTable table;
	private MyTableModel myTableModel;
	private MyActionListener actionListener;
	private MyCallListener myCallListener;
	
	boolean type = false;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getDeleted() {
		return deleted;
	}

	public HashMap<Integer, Integer> getAnsweredhash() {
		return answeredHash;
	}

	public HashMap<Integer, Integer> getCalledbackhash() {
		return calledBackHash;
	}
	
	 public JTable getTable() {
		return table;
	}	

	private void bootstrapLdap() throws Exception {
		// Get all of the LDAP arguments from the click2call.properties file
	        ClassLoader cl = Click2Call.class.getClassLoader();
	        URL appURL = cl.getResource(PROPERTY_FILE);
	        Properties appProp = new Properties();
	        appProp.load(appURL.openStream());
	        providerUrl = appProp.getProperty(PROVIDER_URL).trim();
	        baseDN = appProp.getProperty(BASE_DN).trim();

	        securityPrincipal = appProp.getProperty(SECURITY_PRINCIPAL);
	        if (securityPrincipal != null) {
	            securityPrincipal = securityPrincipal.trim();
	        }
	        securityCredentials = appProp.getProperty(SECURITY_CREDENTAILS);
	        if (securityCredentials != null) {
	            securityCredentials = securityCredentials.trim();
	        }	        	       	        
	 }
	
	private Click2Call()
	{
		setLocationRelativeTo(null);
		trace = new MyTextArea(10,80);
		new TraceFrame(trace);		
		if ( !initJtapi() ) 
		{
			trace.append("Error: JtapiPeer could not be created.  Verify your Jtapi client install.");	        
	        return;
	    }
	    try {
			bootstrapLdap();
			// Create LoginDialog and show as modal
			(new LoginDialog(this, true, getServices())).setVisible(true);
		} catch (Exception e) {
			trace.append("Authentication failed. Check the credentials provided.");
		}
	}
	
	private boolean initJtapi() 
    {
        try {
		    //get default implementation of the JtapiPeer object by sending null,
		    //optionally you may send com.avaya.jtapi.tsapi.TsapiPeer
		    jtapiPeer = JtapiPeerFactory.getJtapiPeer( null );
		    trace.append("JtapiPeer created successfully.");
        } catch (JtapiPeerUnavailableException e) {
	        try{
			jtapiPeer = JtapiPeerFactory.getJtapiPeer( "com.avaya.jtapi.tsapi.TsapiPeer" );
			trace.append("JtapiPeer created successfully.");
		} catch (JtapiPeerUnavailableException e2) {
			trace.append("JtapiPeerFactory.getJtapiPeer: caught JtapiPeerUnavailableException");
			trace.append("Error: JtapiPeer could not be created.  Verify your Jtapi client install.");
            return false;
			}
        }	    
	return true;
    }
    
    private String[] getServices() 
    {
	    String[] services = null;

        try {
		    // get service providers on the network -- this depends on 
	        // the administration in the tsapi.pro file
			services = jtapiPeer.getServices();
		}
		catch (PlatformException e) {
			trace.append("JtapiPeer.getServices(): caught PlatformException\n" + e.getMessage());
	        return null;
	    }
	    return services;
	}
	
    public synchronized void handleProviderInService()
	{		
    	notify();
	}
    
	public synchronized void login(Hashtable<String, String> args) 
    {
        String          serviceName;
        String          login;
        String          password;
        String          callingExt;
        String          providerString;

        serviceName = (String) args.get("serviceName");
        serviceName = "10.16.199.224";
		login = (String) args.get("login");
		login = "5246";
		password = (String) args.get("password");
		password =  "5246";
        callingExt  = (String) args.get( "callingExt" );
        callingExt  = "5246";
        
		providerString = serviceName + ";loginID=" + login + ";passwd=" + password;

		if ( jtapiPeer == null ) 
		{
		    return;
		}
		    
		try {       
		    // create provider
			provider = jtapiPeer.getProvider(providerString);
			trace.append("Provider created successfully. Waiting for the provider to initialize...");
		        
		    //add a Provider Listener to the provider
			myProviderListener = new MyProviderListener(this);
		    provider.addProviderListener( myProviderListener );	
		    
		    wait();//Wait for provider to be in 'in service' state
		    trace.append("Provider is in service.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"Please verify that the login information is correct.","Authentication",JOptionPane.ERROR_MESSAGE);
    	    return;
    	}             			
		        
        try 
        {
            try 
            {                
                myAddress = provider.getAddress( callingExt );                 
                trace.append("Address " + callingExt + " created successfully.");
                    
                // create Terminal
        	    myTerminal = provider.getTerminal(callingExt);
        	    trace.append("Terminal " + callingExt + " created successfully.");
            } 
            catch (Exception e) 
            {
            	trace.append("Please verify that the dialing extension number is correct.");
    	        throw (e);
    	    }
            
         // add call listener to terminal
            trace.append("Adding a call listener to the terminal.");                        
            myCallListener = new MyCallListener(this, myAddress, trace); 
            myTerminal.addCallListener( myCallListener );    	    
    	} 
        catch (Exception e)
    	{
        	trace.append("login() caught " + e + "Message: " + e.getMessage());            
            return;
    	}  
        
        actionListener = new MyActionListener();
		table = new JTable();
		String[] tableHeader = { "Name", "Phone Number", "Call Time","Call Status" };
		String[][] tableData = null;
		myTableModel = new MyTableModel(tableData, tableHeader);		
		Click2CallGUI c2cgui = new Click2CallGUI(actionListener, table, myTableModel);
		table.setDefaultRenderer(table.getColumnClass(0), new ColoredTableCellRenderer());
		c2cgui.addWindowListener(new MyWindowAdapter());		
		c2cgui.setVisible(true);
		dirLookupGui = new DirectoryLookupGUI(actionListener);
		dirLookupGui.setVisible(true);
		queryExecutor = new LDAPQueryExecutor(providerUrl, baseDN, securityPrincipal, securityCredentials);
		callParser = new DisplayParser();
    }
	
	public static void main(String[] args)
	{		
		new Click2Call();
	}
	
	public void displayCall(String name, String number, String time, String callType) 
	{
		Vector<String> temp = new Vector<String>(4, 0);

		temp.add(name);
		temp.add(number);
		temp.add(time);
		temp.add(callType);

		myTableModel.insertRow(0, temp);
	}

	private String getNumberToCall() throws Exception 
	{
		int selectedRowIndex = table.getSelectedRow();

		// no row is selected, return null.
		if (selectedRowIndex == -1) {
			return null;
		} else {
			String accessCode = getAccessCode();

			// User pressed cancel when prompted for access code, so return
			// null
			// to cancel the call.
			if (accessCode == null) {
				return null;
			} else {
				accessCode = accessCode.trim();
			}

			if (accessCode.length() == 0) {
				return (String) table.getValueAt(selectedRowIndex,
						CallLogTableConstants.NUMBER_COLUMN_INDEX);
			} else {
				// Check if the access code is a number
				try {
					Integer.parseInt(accessCode);
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Invalid access code",
							"Call Error", JOptionPane.ERROR_MESSAGE);

					throw new Exception("Invalid access code.");
				}
				return accessCode
						+ (String) table.getValueAt(selectedRowIndex,
								CallLogTableConstants.NUMBER_COLUMN_INDEX);
			}
		}
	}
	
	private String getAccessCode() 
	{
		String accessCode;

		try 
		{
			accessCode = (String) JOptionPane
					.showInputDialog("If you have to dial an access code \n"
							+ "before dialing this number, please \n"
							+ "enter it now.");

			return accessCode;
		} 
		catch (HeadlessException hle) 
		{
			trace.append("An exception occured while obtaining access code");
			return null;
		}
	}
	
	private void callBack(String number, CallControlCall call) throws Exception 
	{
		// If no row is selected, prompt the user to select a row first.
		if ((number == null) && (table.getSelectedRow() == -1)) {
			JOptionPane.showMessageDialog(null,
					"Please select a number (table row) to call.",
					"Call Error", JOptionPane.ERROR_MESSAGE);
		} else if (number == null) 
		{
			// User pressed cancel when prompted for access code.
			JOptionPane.showMessageDialog(null,"Call canceled");
		} 
		else if (number.trim().length() == 0) 
		{
			// Check if callee number is valid, if length of number is greater than 0
			JOptionPane.showMessageDialog(null, "Invalid callee number.",
					"Call Error", JOptionPane.ERROR_MESSAGE);
		} 
		else 
		{
			int selectedRowIndex = table.getSelectedRow();

			// check that the calling party matches the selected row
			// row remains selected if a call was made in the ldap lookup window
			String calledBackNumber = (String) table
					.getValueAt(selectedRowIndex,
							CallLogTableConstants.NUMBER_COLUMN_INDEX);
			type = true;			
			call.connect(myTerminal, myAddress, calledBackNumber);
			callToDropHash.put(number, call);
			calledBackHash.put(((ITsapiCallIDPrivate)call).getTsapiCallID(), 1);
			call_status = new CallStatus(actionListener, number, call);
			if (number.trim().equalsIgnoreCase(calledBackNumber)) {

				// The call went through, change call status.
				if (((String) table.getValueAt(selectedRowIndex,
						CallLogTableConstants.STATUS_COLUMN_INDEX))
						.equals("Missed")) {
					table.setValueAt("Called back", selectedRowIndex,
							CallLogTableConstants.STATUS_COLUMN_INDEX);

				} else {
					table.setValueAt("Dialed", selectedRowIndex,
							CallLogTableConstants.STATUS_COLUMN_INDEX);
				}
			}
		}
	}
   
   	private void displayMessage(int msgType, String msg) 
	{
		if (msg != null) 
		{
			switch (msgType) 
			{
				case JOptionPane.ERROR_MESSAGE: 
				{
					JOptionPane.showMessageDialog(null, msg, "Error",
							JOptionPane.ERROR_MESSAGE);
					break;
				}
				default: 
				{
					JOptionPane.showMessageDialog(null, msg, "Message",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}	
	
	public class MyActionListener implements ActionListener 
	{
		/**
		 * Actions generated by GUI are received here and corresponding actions
		 * are taken.
		 */
		public void actionPerformed(ActionEvent ae) 
		{
			// determine which button is pressed by the user and take
			// appropriate action
			String buttonPressed = ae.getActionCommand();

			if (buttonPressed == null) 
			{
				return;
			} 
			else if (buttonPressed.equals("delete")) 
			{
				int selectedRowIndex = table.getSelectedRow();
				deleted++;
				// if no row is selected, selectedRowIndex would be -1
				if (selectedRowIndex == -1) 
				{
					JOptionPane.showMessageDialog(null,
							"Please select a table row to delete.", "Delete Error",
							JOptionPane.ERROR_MESSAGE);
				} 
				else 
				{
					myTableModel.removeRow(selectedRowIndex);			
					row--;			
				}
			} 
			else if (buttonPressed.equals("callback")) 
			{
				String number = null;				

				try {
					number = getNumberToCall();					

					if ((number != null) && (number.trim().length() != 0)) 
					{
						// If caller number is not available,
						// NumberFormatException will be thrown and
						// message would be displayed to user.
						Double.parseDouble(number);
						callBack(number,(CallControlCall)provider.createCall());
					} 
					else 
					{						
						callBack(number, (CallControlCall)provider.createCall());
					}
				}				
				catch (NumberFormatException nfe) 
				{
					displayMessage(JOptionPane.ERROR_MESSAGE,
							"Invalid number string; call not attemped.");
				} 
				catch (Exception e) 
				{
					JOptionPane.showMessageDialog(null,"Error while attempting call.");
				}
			} 
			else if (buttonPressed.equals("deleteall")) 
			{	
				row = 0;
				myCallListener.getCallHash().clear();
				myCallListener.getLogHash().clear();
				deleted = 0;
				int rowCount = table.getRowCount();

				for (int i = rowCount - 1; i >= 0; i--) 
				{
					myTableModel.removeRow(i);
				}							
			} 
			else if (buttonPressed.equals("hangup")) 
			{
				try 
				{					
					callToDropHash.remove(call_status.getCalledNumber()).drop();
					clearCallStatus();
				} 
				catch (Exception cce) 
				{					
					
				}				
			} 
			else if (buttonPressed.equals("lookup")) 
			{
				// Find the search string (name to look up). If it is null or
				// empty, simply return.
				String nameToLookUp = dirLookupGui.getNameToLookup();

				if ((nameToLookUp == null) || (nameToLookUp.equals(""))) {
					return;
				}				
				else 
				{					
					dirLookupGui.deleteAll();					
					ArrayList<?> queryResult;

					// Use LDAPQueryExecutor object to perform a search for
					// name entered.
					try {
						queryResult = queryExecutor.dirLookup(nameToLookUp);
					} catch (NamingException ne) {
						dirLookupGui.displayMessage(
								JOptionPane.WARNING_MESSAGE,
								"An exception occured while attempting directory"
										+ "lookup");
						ne.printStackTrace();
						return;
					}

					// If no result is found in response to query, display a
					// message to user. Else, display the results in the
					// DirectoryLookupGUI table.
					if ((queryResult == null) || (queryResult.size() == 0)) {
						dirLookupGui.displayMessage(
								JOptionPane.INFORMATION_MESSAGE,
								"No information found corresponding to "
										+ nameToLookUp);
					} else {
						dirLookupGui.displayData(queryResult);
					}
				}
			}			
			else if (buttonPressed.equals("DIRcall")) 
			{ 	
				// User performed a * directory lookup and now wants to make a 
				// call.
				// nameAndNumber would be used to store String array returned 
				//by dirLookupGui.getNameAndNumberToDial() method. The first //
				//String in the array would be name and second would be number.
				 String[] nameAndNumber; 
				 String numberToDial; 
				 			 
				 nameAndNumber = dirLookupGui.getNameAndNumberToDial();
				 
				 if (nameAndNumber == null) { return; }
				 
				 numberToDial = nameAndNumber[1];
				 
				 if (numberToDial == null) { return; }
				 
				 try 
				 {
				 
					 numberToDial = callParser.parseNumberToDial(numberToDial);
					 			 		
					CallControlCall c_c = (CallControlCall)provider.createCall();
					c_c.connect(myTerminal, myAddress,numberToDial);
					callToDropHash.put(numberToDial, c_c);
					 
					 call_status = new CallStatus(actionListener, numberToDial, c_c);
					 // This dialed call would be logged when user // disconnects it. 
				 }			 
				 catch(NumberFormatException nfe) 
				 { 
					 dirLookupGui.displayMessage(
							 JOptionPane.ERROR_MESSAGE, "Invalid call number");
					 return; 
				 }
				 catch (NullPointerException npe) 
				 { 
					 dirLookupGui.displayMessage(
						 JOptionPane.ERROR_MESSAGE, "Invalid call number"); 
				 	return; 
				 }
				 catch(Exception e)
				 {
					 JOptionPane.showMessageDialog(null,"Error calling back.","Callback",JOptionPane.ERROR_MESSAGE);
				 }
			 }			 
			else if (buttonPressed.equals("DIRdeleteall")) 
			{
				// tell dirLookupGui to delete all entries.
				dirLookupGui.deleteAll();
			} 
			else if (buttonPressed.equals("DIRdelete")) 
			{
				// tell dirLookupGui to delete selected entries.
				dirLookupGui.delete();
			}
		}		
	}
	
	public class MyTableModel extends DefaultTableModel 
	{		
		private static final long serialVersionUID = 1L;

		public MyTableModel(Object[][] data, Object[] header) 
		{
			super(data, header);
		}
	}

	private class ColoredTableCellRenderer extends DefaultTableCellRenderer 
	{		
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			setEnabled(table == null || table.isEnabled());

			// display missed call in different color than answered calls.
			if (((String) table.getValueAt(row, 3)).trim().equals("Missed")) {
				setBackground(MISSED_CALL_COLOR);
			} else if (((String) table.getValueAt(row, 3)).trim().equals(
					"Dialed")) {
				setBackground(DIALED_CALL_COLOR);
			} else if (((String) table.getValueAt(row, 3)).trim().equals(
					"Called back")) {
				setBackground(CALLED_BACK_CALL_COLOR);
			} else {
				setBackground(Color.lightGray);
			}
			super.getTableCellRendererComponent(table, value, selected,
					focused, row, column);
			return this;
		}
	}

	/**
	 * Custom window adapter to exit the application if gui is closed.
	 */
	private class MyWindowAdapter extends WindowAdapter 
	{
		public void windowClosing(WindowEvent we) 
		{
			setVisible(false);
			
			System.exit(0);
		}
	}
	
	public class CallStatus extends JFrame 
	{
        private static final long serialVersionUID = 1L;            
        
        JPanel jp = null;
        private String calledNumber;
        private JLabel connectedParty;        
        private CallStatus(final ActionListener actionListener, final String calledNumber, final CallControlCall call) 
        {
            super("Call Status"); 
            setLocationRelativeTo(null);
            Container contentpane = getContentPane();
            contentpane.setLayout(new GridLayout(1, 0));
            this.calledNumber = calledNumber;
            jp = new JPanel();
            jp.setLayout(new GridLayout(3, 0));
            jp.setBackground(Color.WHITE);

            connectedParty = new JLabel("Calling.... " + calledNumber);
            jp.add(connectedParty);

            JButton jb = new JButton("Hang Up");
            jb.setActionCommand("hangup");
            jb.addActionListener(actionListener);
            jb.setBackground(BUTTON_BKGD_COLOR);
            jb.setForeground(Color.WHITE);
            jp.add(jb);
            contentpane.add(jp);
            JFrame.setDefaultLookAndFeelDecorated(true);
            setSize(300, 100);
            setVisible(true);            
        }   
        public String getCalledNumber()
        {
        	return calledNumber;
        }
		public JLabel getConnectedParty() {
			return connectedParty;
		}				
    }

	public CallStatus getCall_status() {
		return call_status;
	}	
	
	public void clearCallStatus()
	{
		call_status.setVisible(false);
		call_status = null;
	}
}
