/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.arp.instagrim.lib;

import uk.ac.dundee.computing.arp.instagrim.stores.LoggedIn;

/**
 * User Permission class
 * 
 * @author Andrew
 */
public class UserPermission {
    public static int LOGGED_IN=0;
    public static int OWNER_MATCH=1;
    
    /**
     * Checks if the current logged in user has permission to access or modify the resource
     * returns the string 'success' if so, otherwise returns an appropriate message.
     * 
     * @param type The type of permission to check for (logged in, owner)
     * @param lg LoggedIn object belonging to the user
     * @param owner String representing the owner of the resource
     * @return String representing if they have permission
     */
    public static String hasPermission(int type, LoggedIn lg, String owner)
    {
        if (lg == null || !lg.getlogedin())
        {
            //Error.error(, request, response);
            return "You are not logged in!";
        }
        else if (type == LOGGED_IN)
        {
            return "success";
        }
        else if (type == OWNER_MATCH)
        {
            String user = lg.getUsername();
            if (!user.equals(owner))
            {
                //Error.error(, request, response);
                return "You do not have permission to do that!";
            }
        }
        return "success";
    }
}
