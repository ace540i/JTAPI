package tstest;

import javax.swing.JTextArea;
import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.CallListener;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.PlatformException;
import javax.telephony.Provider;
import javax.telephony.ProviderEvent;
import javax.telephony.ProviderListener;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;

import com.avaya.jtapi.tsapi.adapters.ProviderListenerAdapter;

public class PhoneService  extends ProviderListenerAdapter {
	private PhoneCallData data;

	private final JTextArea trace;

	private Provider provider;

	private Terminal terminal;
	
	private CallListener listener;
	
	public PhoneService(PhoneCallData data, JTextArea area, CallListener listener) {
		this.data = data;
		this.trace = area;
		this.listener = listener;
	}

	public String[] getServices() throws Exception {
		try {
			// create JtapiPeer
			final JtapiPeer jtapiPeer = JtapiPeerFactory
					.getJtapiPeer(isNull(data.getPeer()) ? null : data.getPeer());
			return jtapiPeer.getServices();
		} catch (Exception e) {
			trace.append("init() caught exception " + e.getClass().getName()
					+ "\n");
			trace.append("Message: " + e.getMessage() + "\n\n");
			trace.append("Provider could not be created.\n Please verify " +
					"that parameters "
							+ "in the page are correct.\n\n");
			e.printStackTrace();
			throw e;
		}
	}

	public void getProvider() throws Exception {
		try {
//			String providerString = data.getService() + ";loginID=" + data.getLogin()
//					+ ";passwd=" + data.getPassword();
			String providerString = data.getService() + ";loginID=" + "OAISYSTSAPI"
					+ ";passwd=" + "Avaya1234567!";
			// create JtapiPeer
			JtapiPeer jtapiPeer = JtapiPeerFactory
					.getJtapiPeer(isNull(data.getPeer()) ? null : data.getPeer());

			// create Provider
			System.out.println("provider is "+providerString);
			provider = jtapiPeer.getProvider(providerString);
			trace.append("Provider created successfully.\n\n");
			trace.append("Waiting for the provider to initialize...\n");

			// add a ProviderListener to the Provider to be notified when it is
			// in service
			provider.addProviderListener(this);

			// wait to be notified when the Provider is in service --
			// corresponding notify is in the providerChangedEvent() method
			synchronized (this) {
				wait();
			}
			trace.append("Provider is in service.\n\n");
		} catch (Exception e) {
			trace.append("init() caught exception " + e.getClass().getName()
					+ "\n");
			trace.append("Message: " + e.getMessage() + "\n\n");
			trace.append("Provider could not be created." +
					"\n Please verify that parameters "
							+ "in the page are correct.\n\n");
			e.printStackTrace();
			throw e;
		}
	}

	private boolean isNull(String peer) {
		return (peer == null || "".equals(peer));
	}
	
	public void providerInService(ProviderEvent event)
	{
		synchronized (this) {
			notify(); // registerRouteCallback() is waiting on this
						// event
		}
	}

	public void makeCall() {
		
		System.out.println("** inside make Call");
		Address address = null;
		Call call = null;

		if (provider != null) {
			try {
				try {
					// In order to make a call, we need to obtain an Address and
					// a Terminal
					// object that represent the dialing extension. In Avaya's
					// implementation
					// of JTAPI, there is a one-to-one relationship between the
					// Terminal and
					// Address objects that represent an extension number.

					// create Address
					address = provider.getAddress(data.getCaller());
					System.out.println("Address " + data.getCaller()
							+ " created successfully.\n\n");
					trace.append("Address " + data.getCaller()
							+ " created successfully.\n\n");

					// create Terminal
					terminal = provider.getTerminal(data.getCaller());
					System.out.println("Terminal " + data.getCaller()
							+ " created successfully.\n\n");
					trace.append("Terminal " + data.getCaller()
							+ " created successfully.\n\n");
				} catch (Exception e) {
					e.printStackTrace();
					trace.append("\nPlease verify that the dialing extension number is correct.\n\n");
					e.printStackTrace();
					throw (e);
				}

				trace.append("Adding a callListener to the terminal.\n\n");

				try {
					// add calllistener to terminal
					terminal.addCallListener(listener);

					// create Call
					call = provider.createCall();
				} catch (Exception e) {
					e.printStackTrace();
					throw (e);
					
				}
				try {
					// makecall by using the connect method of the Call object
					
					System.out.println("Making call from " + data.getCaller()
							+ " to " + data.getCallee() + "...\n\n");
					trace.append("Making call from " + data.getCaller()
							+ " to " + data.getCallee() + "...\n\n");
					call.connect(terminal, address, data.getCallee());
				} catch (Exception e) {
					System.out.println("exception \nPlease verify that the phone that you're " +
							"dialing from is ready to initiate calls.\n\n");
					trace.append("\nPlease verify that the phone that you're " +
							"dialing from is ready to initiate calls.\n\n");
					e.printStackTrace();
					throw (e);
				}
			} catch (Exception e) {
				System.out.println("makecall() caught " + e + "\n");
				trace.append("makecall() caught " + e + "\n");
				trace.append("Message: " + e.getMessage() + "\n\n");
				System.out.println("Message: " + e.getMessage() + "\n\n");
				e.printStackTrace();
				return;
			}
		} else {
			System.out.println("Unable to make call - provider was not created " +
					"successfully.\n\n");
			trace.append("Unable to make call - provider was not created " +
					"successfully.\n\n");
		}
		return;
	}

	/**
	 * This method disconnects the specified terminalConnection.
	 */
	public void hangup() {
		if ( (terminal.getTerminalConnections()) != null) {
			TerminalConnection terminalConnection = terminal.getTerminalConnections()[0];
			try {
				// drop existing connection at the terminal
				System.out.println("\nDropping connection...\n\n");
				trace.append("\nDropping connection...\n\n");
				if (terminalConnection != null) {
					terminalConnection.getConnection().disconnect();
				}
			} catch (Exception e) {
				trace.append("hangup() caught " + e + "\n");
				trace.append("Message: " + e.getMessage() + "\n\n");
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * This method removes the CallListener on the Terminal and shuts down the
	 * Provider.
	 */
	public synchronized void shutProvider() {

		CallListener[] callListener = null;
		ProviderListener[] providerListener = null;

		if (provider != null) {

			if (provider.getState() == Provider.IN_SERVICE) {

				if ((providerListener = provider.getProviderListeners()) != null) {
					provider.removeProviderListener(providerListener[0]);
				}

				if ((callListener = terminal.getCallListeners()) != null) {
					terminal.removeCallListener(callListener[0]);
				}

				trace.append("\nShutting down provider...\n\n");

				try {
					// shutdown the provider
					provider.shutdown();
				} catch (PlatformException e) {
					trace.append("provider.shutdown() caught " +
							"PlatformException\n");
					trace.append("Message: " + e.getMessage() + "\n\n");
				}
			}
			provider = null;
		}
	}

	public void updateData(PhoneCallData testData) {
		this.data = testData;
	}

	public void shutdown(){
		if (provider!=null){
			provider.shutdown();
			provider = null;
		}
	}
}
