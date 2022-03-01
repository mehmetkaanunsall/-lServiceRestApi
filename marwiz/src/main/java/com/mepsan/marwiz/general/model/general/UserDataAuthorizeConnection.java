/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   02.02.2018 07:43:40
 */

package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;


public class UserDataAuthorizeConnection extends WotLogging {

    private int id;
    private UserData userData;
    private Authorize authorize;

    public UserDataAuthorizeConnection() {
        
        this.userData=new UserData();
        this.authorize=new Authorize();
        
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }
    
    
}
