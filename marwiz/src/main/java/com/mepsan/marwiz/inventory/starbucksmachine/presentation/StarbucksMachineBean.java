/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.starbucksmachine.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StarbucksMachine;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.starbucksmachine.business.IStarbucksMachineService;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ebubekir.buker
 */
@ManagedBean
@ViewScoped

public class StarbucksMachineBean extends GeneralDefinitionBean<StarbucksMachine> {

    @ManagedProperty(value = "#{starbucksMachineService}")
    private IStarbucksMachineService starbucksMachineService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private int processType;


    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }


    public void setStarbucksMachineService(IStarbucksMachineService starbucksMachineService) {
        this.starbucksMachineService = starbucksMachineService;
    }

    @PostConstruct
    @Override
    public void init() {
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{349, 350, 351}, 0));

    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new StarbucksMachine();
        RequestContext.getCurrentInstance().execute("PF('dlg_StarbucksMachineProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (processType == 1) {
            result = starbucksMachineService.create(selectedObject);
            selectedObject.setId(result);
            listOfObjects.add(selectedObject);
        } else if (processType == 2) {
            result = starbucksMachineService.update(selectedObject);
        }
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_StarbucksMachineProcess').hide();");
            RequestContext.getCurrentInstance().update("frmStarbucksMachine:dtbStarbucksMachine");
            RequestContext.getCurrentInstance().execute("PF('starbucksMachinePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<StarbucksMachine> findall() {
        return starbucksMachineService.listOfStarbucksMachine();
    }

    public void update() {
        processType = 2;
        RequestContext.getCurrentInstance().execute("PF('dlg_StarbucksMachineProcess').show();");
    }

    public void delete() {
        int result = 0;
        result = starbucksMachineService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_StarbucksMachineProcess').hide();");
            context.update("frmStarbucksMachine:dtbStarbucksMachine");
            context.execute("PF('starbucksMachinePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }
}
