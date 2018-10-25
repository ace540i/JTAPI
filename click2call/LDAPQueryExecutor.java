/*
 * LDAPQueryExecutor.java
 * 
 * Copyright (c) 2002-2007 Avaya Inc. All rights reserved.
 * 
 * USE OR INSTALLATION OF THIS SAMPLE DEMONSTRATION SOFTWARE INDICATES THE END
 * USERS ACCEPTANCE OF THE GENERAL LICENSE TERMS AVAILABLE ON THE AVAYA WEBSITE
 * AT http://support.avaya.com/LicenseInfo/ (GENERAL LICENSE TERMS). DO NOT USE
 * THE SOFTWARE IF YOU DO NOT WISH TO BE BOUND BY THE GENERAL LICENSE TERMS. IN
 * ADDITION TO THE GENERAL LICENSE TERMS, THE FOLLOWING ADDITIONAL TERMS AND
 * RESTRICTIONS WILL TAKE PRECEDENCE AND APPLY TO THIS DEMONSTRATION SOFTWARE.
 * 
 * THIS DEMONSTRATION SOFTWARE IS PROVIDED FOR THE SOLE PURPOSE OF DEMONSTRATING
 * HOW TO USE THE SOFTWARE DEVELOPMENT KIT AND MAY NOT BE USED IN A LIVE OR
 * PRODUCTION ENVIRONMENT. THIS DEMONSTRATION SOFTWARE IS PROVIDED ON AN AS IS
 * BASIS, WITHOUT ANY WARRANTIES OR REPRESENTATIONS EXPRESS, IMPLIED, OR
 * STATUTORY, INCLUDING WITHOUT LIMITATION, WARRANTIES OF QUALITY, PERFORMANCE,
 * INFRINGEMENT, MERCHANTABILITY, OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * EXCEPT FOR PERSONAL INJURY CLAIMS, WILLFUL MISCONDUCT AND END USERS VIOLATION
 * OF AVAYA OR ITS SUPPLIERS INTELLECTUAL PROPERTY RIGHTS, INCLUDING THROUGH A
 * BREACH OF THE SOFTWARE LICENSE, NEITHER AVAYA, ITS SUPPLIERS NOR END USER
 * SHALL BE LIABLE FOR (i) ANY INCIDENTAL, SPECIAL, STATUTORY, INDIRECT OR
 * CONSEQUENTIAL DAMAGES, OR FOR ANY LOSS OF PROFITS, REVENUE, OR DATA, TOLL
 * FRAUD, OR COST OF COVER AND (ii) DIRECT DAMAGES ARISING UNDER THIS AGREEMENT
 * IN EXCESS OF FIFTY DOLLARS (U.S. $50.00).
 * 
 * To the extent there is a conflict between the General License Terms, your
 * Customer Sales Agreement and the terms and restrictions set forth herein, the
 * terms and restrictions set forth herein shall prevail solely for this Utility
 * Demonstration Software.
 */

//*****************************************************************************
//* Package
//*****************************************************************************

package click2call;

//*****************************************************************************
//* Imports
//*****************************************************************************

// Classes required to connect to and query LDAP server.
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;

// Java utilities.
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * The object of this class can be used to query a LDAP server to look up for
 * full name, phone number and mail id of a person whose first or last name is
 * supplied as search key.
 *  
 */

public class LDAPQueryExecutor implements TableConstants
{
    private Hashtable<String, String> env;
    private String baseDN = "";

    /**
     * Set the environment (Hashtable containing initial context factory and
     * provider url) inside the constructor as the environment is going to be
     * constant for this object.
     * 
     * @param providerUrl
     *            URL of LDAP server. It should be of the form
     *            ldap://ldap_server_address:port/search_root providerUrl is
     *            supplied through properties file.
     */
    public LDAPQueryExecutor(String providerUrl,String baseDN, String securityPrincipal, String securityCredentails)
    {
        env = new Hashtable<String, String>();

        env.put(
            Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.ldap.LdapCtxFactory");
        if(securityPrincipal != null && !"".equals(securityPrincipal)
                && securityCredentails != null && !"".equals(securityCredentails)) {
            env.put(
                Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_PRINCIPAL,securityPrincipal);
            env.put(Context.SECURITY_CREDENTIALS,securityCredentails);
        }
        env.put(Context.PROVIDER_URL, providerUrl);
        this.baseDN = baseDN;
    }
    
    /**
     * This method performs a directory search using LDAP and returns the
     * results obtained in an ArrayList object. The array list object contains
     * Name &/or number &/or mail id of every matching entry (person) found. If
     * an exception occurs, it is passed to the calling method.
     * 
     * @param nameToLookUp
     * @return ArrayList containing query results.
     * 
     * @throws NamingException
     */

    public ArrayList<String[]> dirLookup(String nameToLookUp) throws NamingException
    {

        DirContext cntx = new InitialDirContext(env);
        SearchControls ctls = new SearchControls();

        // We are only interested in full name (first and last name), telephone-
        // number and mail id of search matches found.
        String[] attrIDs = { "givenName", "sn", "telephoneNumber", "mail" };
        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE); // search all sub-directories

        ArrayList<String[]> result;
        NamingEnumeration<?> queryResult;

        result = new ArrayList<String[]>();

        // Specify the search filter to match
        // Ask for objects that have the attribute "sn" (surname) or
        // "givenName" (first name) equal to argument supplied to this
        // method.
        String filter =
            "(|(sn=" + nameToLookUp + ")(givenName=" + nameToLookUp + "))";

        // Search for objects using the filter
        queryResult = cntx.search(baseDN, filter, ctls);

        while (queryResult.hasMore())
        {
            SearchResult sr = (SearchResult) queryResult.next();

            String attr = sr.getAttributes().toString();
            
            // Remove the leading "{" and trailing "}" from attr.
            StringTokenizer tokens =
                new StringTokenizer(attr.substring(1, attr.length() - 1), ",");

            // userdata will hold name, number and mail id of matches found.
            String[] userdata =
                new String[DirTableConstants.TABLE_COLUMN_COUNT];

            while (tokens.hasMoreTokens())
            {

                StringTokenizer innertokens =
                    new StringTokenizer(tokens.nextToken(), ":");

                while (innertokens.hasMoreTokens())
                {
                    String currentToken = innertokens.nextToken();

                    if (currentToken.endsWith("telephoneNumber"))
                    {
                        userdata[DirTableConstants.NUMBER_COLUMN_INDEX] =
                            innertokens.nextToken();
                    }
                    else if (currentToken.endsWith("mail"))
                    {
                        userdata[DirTableConstants.MAIL_COLUMN_INDEX] =
                            innertokens.nextToken();
                    }
                    else if (currentToken.endsWith("givenName"))
                    {
                        if (userdata[DirTableConstants.NAME_COLUMN_INDEX]
                            == null)
                        {
                            userdata[DirTableConstants.NAME_COLUMN_INDEX] =
                                innertokens.nextToken();
                        }
                        else
                        {
                            userdata[DirTableConstants.NAME_COLUMN_INDEX] =
                                innertokens.nextToken()
                                    + " "
                                    + userdata[DirTableConstants
                                        .NAME_COLUMN_INDEX];
                        }
                    }
                    else if (currentToken.endsWith("sn"))
                    {
                        if (userdata[DirTableConstants.NAME_COLUMN_INDEX]
                            == null)
                        {
                            userdata[DirTableConstants.NAME_COLUMN_INDEX] =
                                innertokens.nextToken();
                        }
                        else
                        {
                            userdata[DirTableConstants.NAME_COLUMN_INDEX] =
                                userdata[DirTableConstants.NAME_COLUMN_INDEX]
                                    + " "
                                    + innertokens.nextToken();
                        }
                    }
                }
            }

            result.add(userdata);

        }

        return result;

    }
}
