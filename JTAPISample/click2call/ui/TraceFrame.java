package click2call.ui;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class TraceFrame extends JFrame
{	
	private static final long serialVersionUID = 1L;

	public TraceFrame(TraceOutputUi trace)
	{
		super("Trace");
		setSize(400,180);
		setLocation(600,0);
		add((JTextArea)trace);
		setVisible(true);
	}
}
