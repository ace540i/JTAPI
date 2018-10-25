package tstest.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.telephony.CallEvent;
import javax.telephony.ConnectionEvent;
import javax.telephony.MetaEvent;
import javax.telephony.TerminalConnectionEvent;
import javax.telephony.TerminalConnectionListener;

import tstest.TSTestData;
import tstest.TSTestService;

import com.avaya.jtapi.tsapi.TsapiPeer;

public class TSTestFrame extends JFrame implements TerminalConnectionListener {
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	private final JTextArea area = new JTextArea();

	// page 1
	private JTextField fieldName;

	private JPasswordField fieldPass;

	private JTextField fieldClass;

	// page 2
	private JButton button;
 
	private JComboBox serviceName;

	private JTextField fieldCaller;

	private JTextField fieldCallee;

	private TSTestService service;

	public TSTestFrame() {
		setTitle("TSTest Application");
		setSize(350, 1000);
		setResizable(false);
		setBackground(Color.gray);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		getContentPane().add(mainPanel);

		// Create the tab pages and message panel
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Login info", createPage1());
		tabbedPane.addTab("TSTest info", createPage2());
		mainPanel.add(tabbedPane);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				service.shutdown();
				System.exit(0);
			}
		});
		area.setEditable(false);
		area.setColumns(36);
		area.setRows(10);
		area.setBorder(BorderFactory.createEtchedBorder());

		tabbedPane.setEnabledAt(1, false);
		JScrollPane scrollPane = new JScrollPane(area);
		mainPanel.add(scrollPane);

		pack();
		setLocationRelativeTo(null); // center frame on screen
	}

	public JPanel createPage1() {
		JPanel panel1 = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		panel1.setLayout(gbl);

		// line 1
		JLabel label1 = new JLabel("Login: ");
		panel1.add(label1);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbl.setConstraints(label1, gbc);

		fieldName = new JTextField("", 30);
		panel1.add(fieldName);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbl.setConstraints(fieldName, gbc);

		// line 2
		JLabel label2 = new JLabel("Password: ");
		panel1.add(label2);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbl.setConstraints(label2, gbc);

		fieldPass = new JPasswordField("", 30);
		panel1.add(fieldPass);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbl.setConstraints(fieldPass, gbc);

		// line 3
		JLabel label3 = new JLabel("JTAPI Class: ");
		panel1.add(label3);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbl.setConstraints(label3, gbc);

		fieldClass = new JTextField("", 30);
		panel1.add(fieldClass);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbl.setConstraints(fieldClass, gbc);

		// line 4
		button = new JButton("Next");
		panel1.add(button);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(button, gbc);

		service = new TSTestService(getTSTestData(), area, this);
		
		// add listeners
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setEnabledAt(1, true);
				tabbedPane.setSelectedIndex(1);
				service.updateData(getTSTestData());
				try {
					for (Object o : service.getServices()) {
						boolean add = true;
						for(int i=0;i<serviceName.getItemCount();i++){
							if(serviceName.getItemAt(i).equals(o)){
								add = false;
								break;
							}
						}
						
						if (add){
							serviceName.addItem((String) o);
						}
					}
				} catch (Exception e1) {
					tabbedPane.setEnabledAt(1, false);
					tabbedPane.setSelectedIndex(0);
				}
			}
		});

		fieldClass.setText(TsapiPeer.class.getName());
		// button.setEnabled(false);
		return panel1;
	}

	public JPanel createPage2() {
		JPanel panel2 = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		panel2.setLayout(gbl);

		// line 1
		JLabel label1 = new JLabel("Service: ");
		panel2.add(label1);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbl.setConstraints(label1, gbc);

		serviceName = new JComboBox();
		panel2.add(serviceName);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(serviceName, gbc);

		// line 2
		JLabel label2 = new JLabel("Caller: ");
		panel2.add(label2);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbl.setConstraints(label2, gbc);

		fieldCaller = new JTextField("", 30);
		panel2.add(fieldCaller);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbl.setConstraints(fieldCaller, gbc);

		// line 3
		JLabel label3 = new JLabel("Callee: ");
		panel2.add(label3);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbl.setConstraints(label3, gbc);

		fieldCallee = new JTextField("", 30);
		panel2.add(fieldCallee);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbl.setConstraints(fieldCallee, gbc);

		// line 4
		button = new JButton("Dial");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				tabbedPane.setEnabledAt(1, true);
				tabbedPane.setSelectedIndex(1);
				service.updateData(getTSTestData());
				if ( "Dial".equals( button.getText() ) ) { 
				    //user wants to make a call
				    try {
						service.login();
					} catch (Exception e1) {
						tabbedPane.setEnabledAt(1, false);
						tabbedPane.setSelectedIndex(0);
					}
				    service.makeCall(); 
				} else {
				    //user wants to disconnect existing call
				    service.hangup();
				}
			}

		});
		panel2.add(button);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(button, gbc);
		return panel2;
	}

	// Main method to get things started
	public static void main(String args[]) {
		// Create an instance of the test application
		TSTestFrame mainFrame = new TSTestFrame();
		mainFrame.setVisible(true);
	}

	public TSTestData getTSTestData() {
		if (serviceName!=null ){ //page 1 and page 2 both initialized
			return new TSTestData(fieldName.getText(),
				new String(fieldPass.getPassword()), fieldClass.getText(),
				(String) serviceName.getSelectedItem(), fieldCaller.getText(),
				fieldCallee.getText());
		}
		return null;
	}
	

	public void terminalConnectionActive(TerminalConnectionEvent arg0)
	{
		area.append("Received TerminalConnectionActive Event\n");
		button.setText("Hang Up");
	}

	public void terminalConnectionCreated(TerminalConnectionEvent arg0)
	{
		area.append("Received TerminalConnectionCreated Event\n");
	}

	public void terminalConnectionDropped(TerminalConnectionEvent arg0)
	{
		area.append("Received TerminalConnectionDropped Event\n");
		button.setText("Dial");
	}

	public void terminalConnectionPassive(TerminalConnectionEvent arg0)
	{
		area.append("Received TerminalConnectionPassive Event\n");
	}

	public void terminalConnectionRinging(TerminalConnectionEvent arg0)
	{
		area.append("Received TerminalConnectionRinging Event\n");
	}

	public void terminalConnectionUnknown(TerminalConnectionEvent arg0)
	{
		area.append("Received TerminalConnectionUnknown Event\n");
	}

	public void connectionAlerting(ConnectionEvent arg0)
	{
		area.append("Received ConnectionAlerting Event\n");
	}

	public void connectionConnected(ConnectionEvent arg0)
	{
		area.append("Received ConnectionConnected Event\n");
	}

	public void connectionCreated(ConnectionEvent arg0)
	{
		area.append("Received ConnectionCreated Event\n");
	}

	public void connectionDisconnected(ConnectionEvent arg0)
	{
		area.append("Received ConnectionDisconnected Event\n");
	}

	public void connectionFailed(ConnectionEvent arg0)
	{
		area.append("Received ConnectionFailed Event\n");
	}

	public void connectionInProgress(ConnectionEvent arg0)
	{
		area.append("Received ConnectionInProgress Event\n");
	}

	public void connectionUnknown(ConnectionEvent arg0)
	{
		area.append("Received ConnectionUnknown Event\n");
	}

	public void callActive(CallEvent arg0)
	{
		area.append("Received CallActive Event\n");		
	}

	public void callEventTransmissionEnded(CallEvent arg0)
	{
		area.append("Received CallEventTransmissionEnded Event\n");
		new Thread (){
		    public void run(){
		        try {
                    Thread.sleep(2000);
                    service.logout();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
		    }
		}.start();
		    
		button.setText("Dial");
	}

	public void callInvalid(CallEvent arg0)
	{
		area.append("Received CallInvalid Event\n");
	}

	public void multiCallMetaMergeEnded(MetaEvent arg0)
	{
		area.append("Received MultiCallMetaMergeEnded Event\n");
	}

	public void multiCallMetaMergeStarted(MetaEvent arg0)
	{
		area.append("Received MultiCallMetaMergeStarted Event\n");
	}

	public void multiCallMetaTransferEnded(MetaEvent arg0)
	{
		area.append("Received MultiCallMetaTransferEnded Event\n");
	}

	public void multiCallMetaTransferStarted(MetaEvent arg0)
	{
		area.append("Received MultiCallMetaTransferStarted Event\n");
	}

	public void singleCallMetaProgressEnded(MetaEvent arg0)
	{
		area.append("Received SingleCallMetaProgressEnded Event\n");
	}

	public void singleCallMetaProgressStarted(MetaEvent arg0)
	{
		area.append("Received SingleCallMetaProgressStarted Event\n");
	}

	public void singleCallMetaSnapshotEnded(MetaEvent arg0)
	{
		area.append("Received SingleCallMetaSnapshotEnded Event\n");
	}

	public void singleCallMetaSnapshotStarted(MetaEvent arg0)
	{
		area.append("Received SingleCallMetaSnapshotStarted Event\n");
	}
}
