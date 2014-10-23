/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.lib;

import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Andrew
 */
public class UserPermission {
    public static int LOGGED_IN=0;
    public static int OWNER_MATCH=1;
    
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
