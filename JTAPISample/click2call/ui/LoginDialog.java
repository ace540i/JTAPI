package click2call.ui;
/*
	A basic extension of the java.awt.Dialog class
 */

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import click2call.Click2Call;

public class LoginDialog extends Dialog
{
	private static final long serialVersionUID = 1L;


	public LoginDialog(Frame parent)
	{		
		super(parent);
		setLocationRelativeTo(null);		
		setLayout(null);
		setFont(new Font("SansSerif", Font.PLAIN, 11));
		setSize(314,180);
		setVisible(false);
		mainPanel.setLayout(null);
		add(mainPanel);
		mainPanel.setBounds(0,0,310,170);
		loginLabel.setText("Login:");
		loginLabel.setAlignment(java.awt.Label.RIGHT);
		mainPanel.add(loginLabel);
		loginLabel.setBounds(19,41,75,22);
		servicenameLabel.setText("Service Name:");
		servicenameLabel.setAlignment(java.awt.Label.RIGHT);
		mainPanel.add(servicenameLabel);
		servicenameLabel.setBounds(8,16,86,22);
		pwdLabel.setText("Password:");
		pwdLabel.setAlignment(java.awt.Label.RIGHT);
		mainPanel.add(pwdLabel);
		pwdLabel.setBounds(19,66,75,22);
		fromLabel.setText("Dialing from:");
		fromLabel.setAlignment(java.awt.Label.RIGHT);
		mainPanel.add(fromLabel);
		fromLabel.setBounds(19,91,75,22);
		mainPanel.add(loginTextfield);
		loginTextfield.setBounds(106,42,100,18);
		pwdTextfield.setEchoChar('*');
		mainPanel.add(pwdTextfield);
		pwdTextfield.setBounds(106,67,100,18);
		mainPanel.add(fromTextfield);
		fromTextfield.setBounds(106,92,100,18);
		mainPanel.add(serviceNameChoice);
		serviceNameChoice.setBounds(106,11,190,24);
		loginButton.setLabel("Login");
		mainPanel.add(loginButton);
		loginButton.setBackground(java.awt.Color.lightGray);
		loginButton.setBounds(87,141,64,21);
		cancelButton.setLabel("Cancel");
		mainPanel.add(cancelButton);
		cancelButton.setBackground(java.awt.Color.lightGray);
		cancelButton.setBounds(157,141,64,21);
		setTitle("Login");
		//}}

		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymAction lSymAction = new SymAction();
		loginButton.addActionListener(lSymAction);
		cancelButton.addActionListener(lSymAction);
		SymFocus aSymFocus = new SymFocus();
		loginTextfield.addFocusListener(aSymFocus);
		pwdTextfield.addFocusListener(aSymFocus);
		fromTextfield.addFocusListener(aSymFocus);
		SymKey aSymKey = new SymKey();
		loginTextfield.addKeyListener(aSymKey);
		pwdTextfield.addKeyListener(aSymKey);
		fromTextfield.addKeyListener(aSymKey);
		//}}
		parentFrame = parent;
	}
	
	public LoginDialog(Frame parent, boolean modal)
	{
		this(parent);
        setModal(modal);
	}
	
	public void addNotify()
	{
  	    // Record the size of the window prior to calling parents addNotify.
	    Dimension d = getSize();

		super.addNotify();

		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		Insets insets = getInsets();
		setSize(insets.left + insets.right + d.width, insets.top + insets.bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets.left, insets.top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

    // Used for addNotify check.
	boolean fComponentsAdjusted = false;


	public LoginDialog(Frame parent, boolean modal, String[] services)
	{
		this(parent, modal);
		parentFrame = parent;
	    if (services != null) 
	    {
	        for ( int i=0; i < services.length; i++ ) 
	        {
        	    serviceNameChoice.addItem( services[i] );
        	}
	    }	                    
	}

    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
		if(b)
		{
			Rectangle bounds = getParent().getBounds();
			Rectangle abounds = getBounds();
	
			setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
				 bounds.y + (bounds.height - abounds.height)/2);
		}
		super.setVisible(b);
	}

	//{{DECLARE_CONTROLS
	java.awt.Panel mainPanel = new java.awt.Panel();
	java.awt.Label loginLabel = new java.awt.Label();
	java.awt.Label servicenameLabel = new java.awt.Label();
	java.awt.Label pwdLabel = new java.awt.Label();
	java.awt.Label fromLabel = new java.awt.Label();
	java.awt.TextField loginTextfield = new java.awt.TextField();
	java.awt.TextField pwdTextfield = new java.awt.TextField();
	java.awt.TextField fromTextfield = new java.awt.TextField();
	java.awt.Choice serviceNameChoice = new java.awt.Choice();
	java.awt.Button loginButton = new java.awt.Button();
	java.awt.Button cancelButton = new java.awt.Button();
	//}}
	Frame           parentFrame;	

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == LoginDialog.this)
				LoginDialog_WindowClosing(event);
		}
	}
	
	void LoginDialog_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);
	}
	
   /**
    * This method creates the JtapiPeer and the Provider.
    */
    public Hashtable<String, String> collectInfo() {
        String          serviceName;
        String          login;
        String          password;
        String          callingExt;
        Hashtable<String, String>       args = new Hashtable<String, String>(6);
        
        args.put("function", "login");
		serviceName = serviceNameChoice.getSelectedItem();
		login = loginTextfield.getText();
		password = pwdTextfield.getText();
        callingExt = fromTextfield.getText();                
        
		args.put("serviceName", serviceName);
		args.put("login", login);
		args.put("password", password);
		args.put("callingExt", callingExt);
		
		return args;
    }


	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == loginButton)
				loginButton_ActionPerformed(event);
			else if (object == cancelButton)
				cancelButton_ActionPerformed(event);
		}
	}

	void loginButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		try {
			setVisible(false);
			Hashtable<String, String> args = this.collectInfo();
			((Click2Call)parentFrame).login ( args );
			dispose();
		} catch (Exception e) {
		}
	}

	void cancelButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		try {
			// LoginDialog Hide the LoginDialog
			this.setVisible(false);
			dispose();
		} catch (Exception e) {
		}
	}

	class SymFocus extends java.awt.event.FocusAdapter
	{
		public void focusLost(java.awt.event.FocusEvent event)
		{
			Object object = event.getSource();
			((TextField)object).select(0, 0);
		}

		public void focusGained(java.awt.event.FocusEvent event)
		{
			Object object = event.getSource();
			((TextField)object).selectAll();
		}
	}

	class SymKey extends java.awt.event.KeyAdapter
	{
		public void keyPressed(java.awt.event.KeyEvent event)
		{
			switch (event.getKeyCode()) {
    	        case KeyEvent.VK_ENTER:
	                loginButton_ActionPerformed( null );
	                break;
	            case KeyEvent.VK_ESCAPE:
	                cancelButton_ActionPerformed( null );
	                break;
	            default:
	                break;
	        }
		}
	}
}
