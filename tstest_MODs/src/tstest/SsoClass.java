package tstest;

/** *********************************************************
 * Process SSO (Single Sign On, Active Directoty / LDAP)
 * 
 * @author tcondron
 * @version Aug 1, 2015
 * 
 * **********************************************************
 */

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
//import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
//import javax.naming.directory.DirContext;
//import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;


public class SsoClass  {
	String app;	
	Attributes attrs = null;
            
	public Attributes get_attributes(String username)
    {
            final String ATTRIBUTE_FOR_USER = "sAMAccountName";
            final String _domain  = "spectraeastnj.com";
            final String host 	  = "njfs2";
            final String dn		  = "DC=spectraeastnj,DC=com";

//                  String returnedAtts[] ={ "cn", "name", "mail", "sAMAccountName", "distinguishedName","telephonenumber" };
              String returnedAtts[] ={"telephonenumber" };
                  String searchFilter = "(&(objectClass=user)(" + ATTRIBUTE_FOR_USER + "=" + username + "))";
                  //Create the search controls
                  SearchControls searchCtls = new SearchControls();
                  searchCtls.setReturningAttributes(returnedAtts);
                  //Specify the search scope
                  searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                  String searchBase = dn;
                  Hashtable environment = new Hashtable();
                  environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                  environment.put("com.sun.jndi.ldap.read.timeout", "5000");
                  environment.put(Context.PROVIDER_URL, "ldap://" + host + ":389");
                  environment.put(Context.SECURITY_AUTHENTICATION, "simple");
                  environment.put(Context.SECURITY_PRINCIPAL, "ssoldaptest" + "@" + _domain);
                  environment.put(Context.SECURITY_CREDENTIALS,"Likely!2");
                  
                  LdapContext ctxGC = null;
                  try
                  {
                        ctxGC = new InitialLdapContext(environment, null);
                    //    DirContext ctx = new InitialDirContext(environment);
                          
                        NamingEnumeration answer = ctxGC.search(searchBase, searchFilter, searchCtls);
                        while (answer.hasMoreElements())
                        {
                              SearchResult sr = (SearchResult)answer.next();
                              this.attrs = sr.getAttributes();
                              if (attrs != null)
                              {
                                    return attrs;
                              }
                        }
                   }
//                  catch (CommunicationException ce) {
//                	  ce.printStackTrace();
//                  }
                  catch (NamingException e)
                  {
                        System.out.println("#####################################################");
                        System.out.println("### ERROR, Unable to get <InitialLdapContext>.... ###");
                        System.out.println("#####################################################");
                        e.printStackTrace();
                  }
                  return null;
    }
            
        public String parse_attributes()
        {
        	String cn = "";
        	try {
				cn=this.attrs.get("telephonenumber").get().toString();
			} catch (NamingException e) {
				e.printStackTrace();
			}
        	return cn;
        }
    } 


	
