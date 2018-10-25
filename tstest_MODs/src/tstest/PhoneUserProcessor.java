package tstest;


import java.io.IOException;

import javax.naming.directory.Attributes;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PhoneUserProcessor  {
   	
//    public static void getUserPhone(String[] args) throws Exception
	public final void process(final HttpServletRequest request,
			final HttpServletResponse response,   String action)  {
		 action = "getUserPhone";
		System.out.println("**************************phone processor action" +action);
		if (action != null) {
//			if (FacilityConstants.GET_USER_PHONE.equalsIgnoreCase(action)) {
				this.getUserPhone(request, response);
		
//			}
		 }
		}
		
	public void getUserPhone(final HttpServletRequest request,
			final HttpServletResponse response) {	
		System.out.println("**************************getUserPhone") ;
		System.out.println("request userName "+request.getParameter("userName"));
    
  	  	String username = "mdarretta";
  	  	String cn = "";
          SsoClass sso = new SsoClass();			       
          Attributes att = sso.get_attributes(username);         
          if (att == null)
          {
                System.out.println("Sorry your User-id (network-id) is invalid...");
          }
          else
          {			
       	  cn = sso.parse_attributes();
        	  System.out.println(cn);
          }
          sso = null;             
    } 
}
