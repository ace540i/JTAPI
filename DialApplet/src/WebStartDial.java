import java.applet.Applet;
import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


public class WebStartDial  {
 
	   public static void main(String[] args) {
  
	try { 
		System.out.println("Dialing...");
		Robot robot = new Robot();
 			robot.keyPress(KeyEvent.VK_CONTROL);
 				robot.keyPress(KeyEvent.VK_F1);
 				robot.keyRelease(KeyEvent.VK_F1);
 				robot.keyRelease(KeyEvent.VK_CONTROL);
 				System.out.println("Key Press");
 				
			}
		catch (AWTException e) {
			e.printStackTrace();
		}
	
	System.exit(0);
}

}