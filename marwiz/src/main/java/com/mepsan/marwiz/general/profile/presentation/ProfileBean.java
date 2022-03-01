/**
 *
 *
 *
 * @author Emine Eser
 *
 * @date   07.10.2016 13:56:05
 */
package com.mepsan.marwiz.general.profile.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.ftpConnection.presentation.FtpConnectionBean;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.log_general.UserDataLogin;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ProfileBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{ftpConnectionBean}")
    public FtpConnectionBean ftpConnectionBean;

    private List<String> images;

    private int activeIndex;
    private UserData user;

    private UserDataLogin userDataLogin;

    public void setFtpConnectionBean(FtpConnectionBean ftpConnectionBean) {
        this.ftpConnectionBean = ftpConnectionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public UserDataLogin getUserDataLogin() {
        return userDataLogin;
    }

    public void setUserDataLogin(UserDataLogin userDataLogin) {
        this.userDataLogin = userDataLogin;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------------ProfileBean");
        sessionBean.parameter = sessionBean.getUser();
        user = sessionBean.getUser();
        ftpConnectionBean.initializeImage("profil",String.valueOf(sessionBean.getUser().getId()));
        images = new ArrayList<>();
        activeIndex = 1;
        for (int i = 1; i <= 12; i++) {
            images.add("nature" + i + ".jpg");
        }

    }

    /**
     * Bu metot buton tıklama olayında çalışır.İlgili butonun beani çağırılır.
     *
     * @param index
     */
    public void activeIndex(int index) {
        activeIndex = index;
       
    }
}
