import java.applet.Applet;
import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


public class DialApplet extends Applet {

	   String fileToRead = "test1.txt";
	   StringBuffer strBuff;
//	   TextArea txtArea;
//	   Graphics g;
//	 public void init(){
////		 String phone  =  "812017254798";
////	      String phone  = this.getParameter("phone1");
////	      if (phone  != null) 
////	    	  fileToRead = new String(prHtml); 
//    
   	//  keyCommand();
	// }
	 
 
//	 public void command(String command){
//		    final String cmd = command;
//
//		    java.security.AccessController.doPrivileged(
//		        new java.security.PrivilegedAction(){
//		            public Object run() {
//		                // execute the privileged command
//		                command(cmd);
//		                // we must return an object, so we'll return an empty string
//		                return "";
//		            }
//		        }
//		    );
//		}
	 
   	  public void start() {
   		try { 
          System.out.println("starting...");
  		Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_F1);
				robot.keyRelease(KeyEvent.VK_F1);
				robot.keyRelease(KeyEvent.VK_CONTROL);
		}
	catch (AWTException e) {
		e.printStackTrace();
	}
  
	try { 
//		Robot robot = new Robot();
//				robot.keyPress(KeyEvent.VK_CONTROL);
//				robot.keyPress(KeyEvent.VK_F1);
//				robot.keyRelease(KeyEvent.VK_F1);
//				robot.keyRelease(KeyEvent.VK_CONTROL);
				stop();
			}

		catch (AWTException e) {
			e.printStackTrace();
		}
}


  public void stop() {
        System.out.println("stopping...");
        destroy();
  }
  public void  destroy() {
        System.out.println("preparing to unload...");
  }
//	
//	   String fileToRead = "test1.txt";
//	   StringBuffer strBuff;
//	   TextArea txtArea;
//	   Graphics g;
//	   public void init(){
//	      txtArea = new TextArea(100, 100);
//	      txtArea.setEditable(false);
//	      add(txtArea, "center");
//	      String prHtml = this.getParameter("fileToRead");
//	      if (prHtml != null) fileToRead = new String(prHtml);  
//	      readFile();
//	   }
//	   public void readFile(){
//	      String line;
//	      URL url = null;
//	      try{
//	         url = new URL(getCodeBase(), fileToRead);
//	      }
//	      catch(MalformedURLException e){}
//	      try{
//	         InputStream in = url.openStream();
//	         BufferedReader bf = new BufferedReader
//	         (new InputStreamReader(in));
//	         strBuff = new StringBuffer();
//	         while((line = bf.readLine()) != null){
//	            strBuff.append(line + "\n");
//	         }
//	         txtArea.append("File Name : " + fileToRead + "\n");
//	         txtArea.append(strBuff.toString());
//	      }
//	      catch(IOException e){
//	      e.printStackTrace();
//	   }
	
	}
	
 