/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 1:45:10 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.presentation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.httpclient.business.HttpClientConnection;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class WashingMachicneProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{washingMachicneService}")
    public IWashingMachicneService washingMachicneService;

    private int activeIndex;
    private WashingMachicne selectedObject;
    private int processType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setWashingMachicneService(IWashingMachicneService washingMachicneService) {
        this.washingMachicneService = washingMachicneService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public WashingMachicne getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WashingMachicne selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------WashingMachicneProcessBean----");

        if (sessionBean.parameter instanceof WashingMachicne) {
            selectedObject = (WashingMachicne) sessionBean.parameter;
            processType = 2;
        } else {
            processType = 1;
            selectedObject = new WashingMachicne();
        }

        setListBtn(sessionBean.checkAuthority(new int[]{236, 237, 238}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{56, 57, 58}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }
    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            selectedObject.getStatus().setId(51);
            result = washingMachicneService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                processType = 2;
                marwiz.goToPage("/pages/automat/washingmachicne/washingmachicneprocess.xhtml", selectedObject, 1, 120);
            }
        } else {
            result = washingMachicneService.update(selectedObject);
            if (result > 0) {
                Object object = new Object();
                marwiz.goToPage("/pages/automat/washingmachicne/washingmachicne.xhtml", object, 1, 119);
            }
        }
        sessionBean.createUpdateMessage(result);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));

    }

    public void bringStatus() {
        for (Status statu : sessionBean.getStatus(28)) {
            if (selectedObject.getStatus().getId() == statu.getId()) {
                selectedObject.getStatus().setTag(statu.getTag());
                break;
            }
        }
    }

    public void sendConfiguration() {
        boolean result = washingMachicneService.configureDetail(selectedObject);
        if (result) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("warning"), sessionBean.loc.getString("succesfuloperation")));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
        }
        RequestContext.getCurrentInstance().update("grwProcessMessage");
    }

    public void testBeforeDelete() {
        int result = 0;
        result = washingMachicneService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmWashingMachicneProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cannotbedeletedbecausethereisaplatformortankattachedtothewashingmachine")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void delete() {
        int result = 0;
        result = washingMachicneService.delete(selectedObject);
        if (result > 0) {
            Object object = new Object();
            marwiz.goToPage("/pages/automat/washingmachicne/washingmachicne.xhtml", object, 1, 119);
        }
        sessionBean.createUpdateMessage(result);
    }
}
