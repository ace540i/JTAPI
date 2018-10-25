package click2call.ui;

import java.awt.Color;

import javax.swing.JTextArea;

public class MyTextArea extends JTextArea implements TraceOutputUi 
{
	private static final long serialVersionUID = 7975518618013507643L;
	
	int          currentLength;
    boolean      tracing = false;
    
	public MyTextArea(int rows,int cols){
		super(rows, cols);
		setEditable(false);
	}

    public MyTextArea() {
        super();
        currentLength = 0;
        setEditable(false);
        setBackground(Color.white);
    }
        
    public synchronized void append(boolean always, String str) {
        
        if ( tracing || always ) {
            currentLength += str.length();
            if (currentLength > 28000) {
                replaceRange("", 0, 14000);
                currentLength = getText().length();
            }
            super.append(str + "\n");            
        }
    }
    
    public synchronized void append (String str) {
        
        this.append(true, str);
    }
    
    public void setState (boolean state) {

        if (state) {
            tracing = true;
        } else {
            tracing = false;
        }
    }
    
    public void clear () {

        this.setText("");
    }    
}