package de.longri.ldap;

import java.util.Locale;

public class LdapConfig {

    private final String LDAP_SERVER;
    private final String LDAP_PORT;
    private final String LDAP_BASE[];
    private String USER_PRINCIPAL_NAME;
    private boolean REMEMBER_ME;


    /**
     * Create new Instance
     *
     * @param LDAP_SERVER as address like "bm-dc1.botiss.local"
     * @param LDAP_PORT   as Number String ("389")
     * @param LDAP_BASE   as String like "ou=Benutzer,ou=Biotrics,dc=botiss,dc=local" or "OU=SBSUsers,OU=Users,OU=MyBusiness,DC=botiss,DC=local"
     */
    public LdapConfig(String LDAP_SERVER, String LDAP_PORT, String... LDAP_BASE) {
        this.LDAP_SERVER = LDAP_SERVER;
        this.LDAP_PORT = LDAP_PORT;
        this.LDAP_BASE = LDAP_BASE;
    }

    public void setUSER_PRINCIPAL_NAME(String USER_PRINCIPAL_NAME) {
        this.USER_PRINCIPAL_NAME = USER_PRINCIPAL_NAME.toLowerCase(Locale.ROOT);
    }

    public void setRememberMe() {
        this.REMEMBER_ME = true;
    }


    public boolean getRememberMe() {
        return this.REMEMBER_ME;
    }

    public String getUSER_PRINCIPAL_NAME() {
        return USER_PRINCIPAL_NAME;
    }

    public String getLDAP_PORT() {
        return LDAP_PORT;
    }

    public String getLDAP_SERVER() {
        return LDAP_SERVER;
    }

    public String[] getLDAP_BASE() {
        return LDAP_BASE;
    }

    public String getProviderUrl() {
        return "ldap://" + LDAP_SERVER + ":" + LDAP_PORT + "/";
    }

    public String toString() {
        return "LDAP config= uid:" + USER_PRINCIPAL_NAME + " @ " + getProviderUrl();
    }


}
