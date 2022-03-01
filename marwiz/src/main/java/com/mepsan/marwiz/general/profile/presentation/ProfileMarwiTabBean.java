/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:52:01 PM
 */
package com.mepsan.marwiz.general.profile.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.profile.business.IProfileService;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ProfileMarwiTabBean {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{profileService}")
    private IProfileService profileService;

    private String licanseText;
    private int resultType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setProfileService(IProfileService profileService) {
        this.profileService = profileService;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public String getLicanseText() {
        return licanseText;
    }

    public void setLicanseText(String licanseText) {
        this.licanseText = licanseText;
    }

    @PostConstruct
    public void init() {
        System.out.println("ProfileMarwiTabBean");
        licanseText = profileService.findLicanse();
        System.out.println("----licanseText----" + licanseText);
        if (licanseText.equals("licancenotfound")) {
            resultType = 0;
        } else if (licanseText.equals("error")) {
            resultType = -1;
        } else {
            resultType = 1;
        }
    }
}
