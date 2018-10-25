package click2call;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.telephony.Address;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnectionEvent;

import click2call.ui.TraceOutputUi;

import com.avaya.jtapi.tsapi.LucentAddress;
import com.avaya.jtapi.tsapi.LucentTerminal;
import com.avaya.jtapi.tsapi.adapters.TerminalConnectionListenerAdapter;
import com.avaya.jtapi.tsapi.ITsapiCallIDPrivate;

public class MyCallListener extends TerminalConnectionListenerAdapter
{		
	private Address myAddress;
	Click2Call c2c;
	private HashMap<Integer, Integer> logHash = new HashMap<Integer, Integer>(10);
	private HashMap<Integer, String> callHash = new HashMap<Integer, String>(10);	
	TraceOutputUi trace;
	
	public HashMap<Integer, String> getCallHash() {
		return callHash;
	}

	public void setCallHash(HashMap<Integer, String> callHash) {
		this.callHash = callHash;
	}
	
	public HashMap<Integer, Integer> getLogHash() {
		return logHash;
	}
	
	public MyCallListener(Click2Call c2c, Address address, TraceOutputUi trace)
	{	
		this.c2c = c2c;
		this.myAddress = address;
		this.trace = trace;
	}		

	public void handleOutgoingCall( TerminalConnectionEvent event ) 
    {
		Address 		calledAddress;        		     
        String          calledNumber = "<Unknown>";             
        String          name = "";   
        calledAddress = event.getCall().getConnections()[1].getAddress(); 
        try
        {
            Date d = new Date();

            // get called number information
            if ( calledAddress != null ) 
            {
                calledNumber = calledAddress.getName();
            }
            
            // get called number information
            if ( calledAddress instanceof LucentAddress ) {
                
                try {
                    name = ( (LucentAddress) calledAddress).getDirectoryName();
                } 
                catch ( Exception e ) {}                           
            }
            
            if ( name == null ) 
            {
                name = calledNumber;
            }
            if((c2c.getCalledbackhash()).get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID()) == null)
            {
            	String date = DateFormat.getDateTimeInstance().format(d).toString();
	            // log call
	            trace.append( date + " Call made to: " + name);
	            c2c.displayCall(name, calledAddress.getName(), date, "Dialed");	            
	            callHash.put(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID(), calledAddress.getName());
	        }
        } 
        catch (Exception e) 
        {
        	trace.append( "handleOutgoingCall() caught " + e + "\n");
        	trace.append( "Message: " + e.getMessage() + "\n\n" );
            return;
    	}   
    }
   
    public void handleIncomingCall(TerminalConnectionEvent event) 
    {
    	Address 		callingAddress, calledAddress;
        Terminal        callingTerminal;        
        String          name = null;
        Date            d = new Date();
	            
        callingTerminal = event.getTerminalConnection().getTerminal();  
        callingAddress = event.getCall().getConnections()[0].getAddress(); 
        calledAddress = event.getCall().getConnections()[1].getAddress();

        if ( callingAddress != null ) 
        {
	        // verify that callingAddress is not our address, otherwise, it is us
            // initiating the call
            if (!( callingAddress.equals( c2c.myAddress )) ) 
            {                  
                name = null;                    
                // get called number information
                if ( callingTerminal instanceof LucentTerminal ) 
                {                    
                    try 
                    {
	                    name = ((LucentAddress) callingAddress).getDirectoryName();
                    } catch (Exception e) {}
    	        }   
                    
                if ( name == null ) {
                    name = callingAddress.getName();
                }
                String date = DateFormat.getDateTimeInstance().format(d).toString();
                trace.append( date + " Call received from: " + name);
                c2c.displayCall(name, callingAddress.getName(), date, "Ringing");
                logHash.put(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID(), c2c.getRow());
                c2c.setRow(c2c.getRow() + 1);
                callHash.put(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID(), calledAddress.getName());
            }
        } 
        else 
        {
        	trace.append( DateFormat.getDateTimeInstance().format(d) + " Call received from: <Unknown>");
        }
    }
	
    public void terminalConnectionRinging(TerminalConnectionEvent event) 
    {	
    	try
    	{
	    	Address calledAddress = event.getCall().getConnections()[1].getAddress(); 
			if(calledAddress.equals(myAddress))
				handleIncomingCall(event);
			else if((c2c.getCalledbackhash()).get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID()) == null)
				handleOutgoingCall(event);
    	}
    	catch(Exception e)
    	{
    		trace.append("Exception in terminal connection ringing event");
    	}
	}
    
    public void terminalConnectionDropped(TerminalConnectionEvent event) 
    {	
    	try
    	{
	    	if((c2c.getCalledbackhash()).get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID()) == null)
	    	{
		    	String calledAddress = callHash.get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID());
		    	if(calledAddress.equals(myAddress.getName()))
		    	{
		    		int r = logHash.get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID());
					r = logHash.size() - r - (c2c.getDeleted()+1);	
					if (((c2c.getAnsweredhash()).get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID()) == null) && c2c.getTable().getValueAt(r, TableConstants.CallLogTableConstants.STATUS_COLUMN_INDEX ).toString().equals("Ringing"))
						c2c.getTable().setValueAt("Missed", r, TableConstants.CallLogTableConstants.STATUS_COLUMN_INDEX);					
		    	}
	    	}
	    	if(c2c.getCall_status() != null)
	    	{
	    		c2c.clearCallStatus();
	    	}
	    	
    	}
    	catch(Exception e)
    	{
    		trace.append("Exception in terminal connection dropped event");
    	}
    }
    
    public void terminalConnectionActive(TerminalConnectionEvent event)
	{    
    	try
    	{
    		Terminal terminal = event.getTerminalConnection().getTerminal();
	    	Address callingAddress = event.getCall().getConnections()[0].getAddress();
	    	if(!callingAddress.equals(myAddress) && terminal.equals(c2c.myTerminal))
	    	{
	    		int r = logHash.get(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID());
				r = logHash.size() - r - (c2c.getDeleted()+1);	
				(c2c.getAnsweredhash()).put(((ITsapiCallIDPrivate)event.getCall()).getTsapiCallID(),1);
				if (c2c.getTable().getValueAt(r, TableConstants.CallLogTableConstants.STATUS_COLUMN_INDEX).toString().equals("Ringing"))
					c2c.getTable().setValueAt("Answered", r, TableConstants.CallLogTableConstants.STATUS_COLUMN_INDEX);					
	    	}
	    	else
	    	{
	    		if(c2c.getCall_status() != null)
				{
					c2c.getCall_status().getConnectedParty().setText("Answered...." + c2c.getCall_status().getCalledNumber());
				}
	    	}
    	}
    	catch(Exception e)
    	{
    		trace.append("Exception in terminal connection active event");
    	}
	}		
}
