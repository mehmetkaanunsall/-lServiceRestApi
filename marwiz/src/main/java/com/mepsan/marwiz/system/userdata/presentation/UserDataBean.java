/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 11:28:50
 */
package com.mepsan.marwiz.system.userdata.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class UserDataBean extends GeneralDefinitionBean<UserData> {

    private Object object;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{userDataService}")
    private IUserDataService userDataService;

    @ManagedProperty(value = "#{marwiz}")  //centrowiz
    public Marwiz marwiz;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }


    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------UserBean--------");
        
        object=new Object();
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{174}, 0));

    }

    @Override
    public void create() {
        
        marwiz.goToPage("/pages/system/userdata/userdataprocess.xhtml", object, 0, 13);
    }


    @Override
    public List<UserData> findall() {
        return userDataService.findAll();
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
