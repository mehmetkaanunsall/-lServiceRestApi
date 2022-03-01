/**
 * This class ...
 *
 *
 * @author Gozde Gursel
 *
 * @date   09.03.2017 05:26:30
 */
package com.mepsan.marwiz.general.profile.presentation;

import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.common.HashPassword;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped

public class ProfileGeneralTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{userDataService}")
    public IUserDataService userDataService;

    private String oldPassword, newPassword, newPassword2;

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    @PostConstruct
    public void init() {
        System.out.println("------------ProfileGeneralTabBean-----------");

    }

    public void save() {
        HashPassword hashPassword = new HashPassword();
        boolean succsess = hashPassword.passwordMatches(oldPassword, sessionBean.getUser().getPassword());

        if (succsess) {//eski şifre doğru ise
            if (newPassword.equals(newPassword2)) {//şifreler eşleşiyor ise guncelle
                sessionBean.getUser().setPassword(hashPassword.encodePassword(newPassword));
                int result = userDataService.updatePassword(sessionBean.getUser());
                sessionBean.createUpdateMessage(result);
                if (result > 0) {
                    oldPassword = "";
                    newPassword = "";
                    newPassword2 = "";
                }
            } else {
                //şifreler eşleşmiyor
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("passwordsdonotmatch")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            //ieski şifre yanlış
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("oldpasswordisincorrect")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }
}
