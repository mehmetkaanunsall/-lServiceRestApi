/**
 * This class ...
 *
 *
 * @author Gozde Gursel
 *
 * @date   22.05.2017 10:32:20
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
public class ProfileSecurityTabBean {

    @ManagedProperty(value = "#{profileService}") // session
    public IProfileService profileService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int s1, s2, s3;

    public void setProfileService(IProfileService profileService) {
        this.profileService = profileService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

 

    public int getS1() {
        return s1;
    }

    public void setS1(int s1) {
        this.s1 = s1;
    }

    public int getS2() {
        return s2;
    }

    public void setS2(int s2) {
        this.s2 = s2;
    }

    public int getS3() {
        return s3;
    }

    public void setS3(int s3) {
        this.s3 = s3;
    }

    @PostConstruct
    public void init() {
        System.out.println("-------ProfileSecurityTabBean----");
   
    }

    public void save() {
        String s = s1 + "," + s2 + "," + s3;
    }

    public void saveMobile() {
        int result = 0;
//        result = profileService.allowMobile(selectedObject);
//        sessionBean.createUpdateMessage(result);
    }

    public void saveQRCode() {
        int result = 0;
//        result = profileService.qrCodeLoginOnly(selectedObject);
//        sessionBean.createUpdateMessage(result);
    }

    public void savePageInformation() {
        int result = 0;
//        result = profileService.displayPageInfo(selectedObject);
//        sessionBean.createUpdateMessage(result);
//        sessionBean.getUser().getUserConfig().setPageInfo(selectedObject.isPageInfo());
    }
}
