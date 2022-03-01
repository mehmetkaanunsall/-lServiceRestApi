/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:53:15 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.presentation;

import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class WashingMachicnesBean extends GeneralDefinitionBean<WashingMachicne> {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}") // marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{washingMachicneService}")
    public IWashingMachicneService washingMachicneService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWashingMachicneService(IWashingMachicneService washingMachicneService) {
        this.washingMachicneService = washingMachicneService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----WashingMachicnesBean--");
        selectedObject = new WashingMachicne();

        listOfObjects = new ArrayList<>();
        listOfObjects = findall();
        
        setListBtn(sessionBean.checkAuthority(new int[]{235}, 0));
    }

    @Override
    public void create() {
        Object object = new Object();
        marwiz.goToPage("/pages/automat/washingmachicne/washingmachicneprocess.xhtml", object, 0, 120);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<WashingMachicne> findall() {
        return washingMachicneService.findAll("");
    }

}
