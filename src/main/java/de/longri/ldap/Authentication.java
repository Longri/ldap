package de.longri.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.net.UnknownHostException;
import java.util.*;

public class Authentication {
    private static final Logger log = LoggerFactory.getLogger(Authentication.class);
    private final LdapConfig LDAP_CONFIG;
    private List<String> GROUPS = null;

    public Authentication(LdapConfig config) {
        super();
        LDAP_CONFIG = config;
    }

    public boolean isAuthorised(final char[] password) throws UnknownHostException {
        //
        if (this.LDAP_CONFIG.getUSER_PRINCIPAL_NAME().isEmpty() || password == null || password.length == 0)
            return false;

        try {

            // Create a LDAP Context
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, this.LDAP_CONFIG.getUSER_PRINCIPAL_NAME());
            env.put(Context.SECURITY_CREDENTIALS, String.copyValueOf(password));
            env.put(Context.PROVIDER_URL, this.LDAP_CONFIG.getProviderUrl());
            LdapContext ctx = new InitialLdapContext(env, null);
            InitialDirContext inidircontext = new InitialDirContext(env);
            DirContext dirctx = new InitialLdapContext(env, null);
            System.out.println("Connection Successful.");

            // Print all attributes of the name in namespace
            SearchControls sctls = new SearchControls();
            String retatts[] = {"sn", "mail", "displayName", "sAMAccountName"};
            sctls.setReturningAttributes(retatts);
            sctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String srchfilter = "(&(objectClass=user)(mail=*))";
            String[] srchbase = LDAP_CONFIG.getLDAP_BASE();// "OU=Benutzer,OU=Biotrics,DC=botiss,DC=local";
            int totalresults = 0;

            for (String base : srchbase) {
                NamingEnumeration answer = dirctx.search(base, srchfilter, sctls);

                if (answer != null) {
                    while (answer.hasMoreElements()) {
                        SearchResult sr = (SearchResult) answer.next();
                        totalresults++;

                        Attributes attrs = sr.getAttributes();
                        if (answer == null || !answer.hasMore()) {
                            break;
                        }

                        if (attrs != null) {
                            try {
                                if (attrs.get("mail").get().toString().toLowerCase(Locale.ROOT).equals(LDAP_CONFIG.getUSER_PRINCIPAL_NAME())) {
                                    this.GROUPS = getGroups(inidircontext, attrs.get("sAMAccountName").get().toString(), base);
                                }
                            } catch (NullPointerException e) {
                                log.error("Error with get sAMAccountName", e);
                            }
                        }
                    }
                    log.debug("Total User count: {}", totalresults);
                } else {
                    return false;
                }
            }
            // close dir context
            dirctx.close();

            return true;
        } catch (AuthenticationException ex) {
            log.error("Authentication failed for " + LDAP_CONFIG, ex);
            return false;
        } catch (NamingException ex) {
            ex.printStackTrace();
            if (ex.getRootCause() instanceof UnknownHostException) {
                throw new UnknownHostException();
            }
        } finally {
            // clear password
            Arrays.fill(password, '*');
        }
        return false;
    }


//    private static String userBase = "OU=Benutzer,OU=Biotrics,DC=botiss,DC=local";

    private List<String> getGroups(InitialDirContext context, String username, String base) throws NamingException {
        List<String> list = new ArrayList<>();
        String[] attrIdsToSearch = new String[]{"memberOf"};
        String SEARCH_BY_SAM_ACCOUNT_NAME = "(sAMAccountName=%s)";
        String filter = String.format(SEARCH_BY_SAM_ACCOUNT_NAME, username);
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(attrIdsToSearch);
//        NamingEnumeration results = context.search(userBase, filter, constraints);
        NamingEnumeration results = context.search(base, filter, constraints);
        // Fail if no entries found
        if (results == null || !results.hasMore()) {
            return new ArrayList<>();
        }
        SearchResult result = (SearchResult) results.next();
        Attributes attrs = result.getAttributes();
        Attribute attr = attrs.get(attrIdsToSearch[0]);

        NamingEnumeration e = attr.getAll();

        while (e.hasMore()) {
            String value = (String) e.next();
            list.add(value);
        }
        return Collections.unmodifiableList(list);
    }


    public List<String> getGroups() {
        return GROUPS;
    }

    public boolean containsGroup(String group) {
        for (String groupStr : GROUPS) {
            if (groupStr.contains(group)) {
                return true;
            }
        }
        return false;
    }
}
