/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.general.UserData;
import java.io.Serializable;
import java.util.Date;

public class WotLogging implements Serializable {

    private UserData userCreated;
    private Date dateCreated;
    private UserData userUpdated;
    private Date dateUpdated;
    private boolean deleted;
    private Date dateDeleted;

    public UserData getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(UserData userCreated) {
        this.userCreated = userCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public UserData getUserUpdated() {
        return userUpdated;
    }

    public void setUserUpdated(UserData userUpdated) {
        this.userUpdated = userUpdated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

}
