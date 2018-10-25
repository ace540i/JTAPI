package click2call.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import click2call.Click2Call.*;

public class Click2CallGUI extends JFrame
{	
	private static final long serialVersionUID = 1L;
	private static final Color BUTTON_BKGD_COLOR = new Color(80, 90, 170);		
	
	public Click2CallGUI(MyActionListener action_listener, JTable table, MyTableModel myTableModel)
	{
		super("Click2Call");
		setLocationRelativeTo(null);
		setVisible(false);		
		// obtain the ContentPane and set BorderLayout.
		Container contentpane = getContentPane();
		contentpane.setLayout(new BorderLayout());

		// Set table header and data.		

		// if log is null, display empty table otherwise initialize the JTable
		// with data in log file.		

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// set table model, foreground, background, font etc.		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setBackground(Color.WHITE);
		table.getTableHeader().setFont(
				new Font(table.getTableHeader().getFont().getName(), Font.BOLD,
						10));
		table.setGridColor(BUTTON_BKGD_COLOR);
		table.setModel(myTableModel);

		// set table cell renderer for using custom colors in table cells
		// depending upon cell data.		

		// create scroll pane, add table to it.
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;

		JScrollPane jsp = new JScrollPane(table, v, h);

		jsp.getViewport().setBackground(Color.WHITE);

		// Create a panel and add various buttons to it. Actions generated
		// by these buttons are sent to action_listener.
		JPanel jp1 = new JPanel();
		jp1.setLayout(new FlowLayout());
		jp1.setBackground(Color.white);

		JButton jb = new JButton("Call Back");
		jb.setActionCommand("callback");
		jb.addActionListener(action_listener);
		jb.setBackground(BUTTON_BKGD_COLOR);
		jb.setForeground(Color.WHITE);
		jp1.add(jb);

		jb = new JButton("Delete");
		jb.setActionCommand("delete");
		jb.addActionListener(action_listener);
		jb.setBackground(BUTTON_BKGD_COLOR);
		jb.setForeground(Color.WHITE);
		jp1.add(jb);

		jb = new JButton("Delete All");
		jb.setActionCommand("deleteall");
		jb.addActionListener(action_listener);
		jb.setBackground(BUTTON_BKGD_COLOR);
		jb.setForeground(Color.WHITE);
		jp1.add(jb);

		JPanel jp2 = new JPanel();
		JLabel jl = new JLabel("Please select a table row"
				+ " and click \"Call Back\" to make the call.");
		jl.setForeground(BUTTON_BKGD_COLOR);
		jl.setFont(new Font(jl.getFont().getName(), Font.BOLD, 16));

		jp2.setLayout(new FlowLayout());
		jp2.setBackground(Color.white);
		jp2.add(jl);

		// add various panels and scroll pane to content pane.
		contentpane.add(jp2, BorderLayout.NORTH);
		contentpane.add(jsp, BorderLayout.CENTER);
		contentpane.add(jp1, BorderLayout.SOUTH);

		// set size of content pane and display it.
		setSize(800, 400);

		setVisible(true);

		// add custom window listener to exit the application if GUI is closed.		

		JFrame.setDefaultLookAndFeelDecorated(true);
	}
}
