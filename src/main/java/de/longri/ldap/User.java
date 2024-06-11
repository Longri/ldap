package de.longri.ldap;

import java.util.ArrayList;

/**
 * A class that contains the user information of a registered user
 */
public class User {

    /**
     * The user principal name. most the mail address
     */
    private final String USER_PRINCIPAL_NAME;

    /**
     * The user name for system log in (On Windows Domain like user principal name)
     */
    private final String USER_SYSTEM_NAME;

    private final boolean IS_ADMIN;

    private final ArrayList<String> ROLES = new ArrayList<>();

    public User(String user_principal_name, String user_system_name, boolean is_admin) {
        USER_PRINCIPAL_NAME = user_principal_name;
        USER_SYSTEM_NAME = user_system_name;
        IS_ADMIN = is_admin;
    }

    public String getUSER_PRINCIPAL_NAME() {
        return USER_PRINCIPAL_NAME;
    }

    public boolean isAdmin() {
        return IS_ADMIN;
    }

    public boolean authorised(String role) {
        if (IS_ADMIN) return true;
        for (String r : ROLES) {
            if (r.equals(role)) return true;
        }
        return false;
    }
}
