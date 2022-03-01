package com.mepsan.marwiz.inventory.wastereason.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.wastereason.business.IWasteReasonService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class WasteReasonBean extends GeneralDefinitionBean<WasteReason> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{wasteReasonService}")
    public IWasteReasonService wasteReasonService;

    private int processType;
    private int oldId;
    private int newId;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IWasteReasonService getWasteReasonService() {
        return wasteReasonService;
    }

    public void setWasteReasonService(IWasteReasonService wasteReasonService) {
        this.wasteReasonService = wasteReasonService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------WasteReasonBean------");
        listOfObjects = findall();

        setListBtn(sessionBean.checkAuthority(new int[]{302, 303, 304}, 0));

    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new WasteReason();
        oldId = 0;
        newId = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_wastereasondeffinitionproc').show();");
    }

    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();

        boolean isAvailable = false;

        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            WasteReason foundWasteReason = new WasteReason();
            foundWasteReason = wasteReasonService.findAccordingToName(selectedObject);
            if (foundWasteReason.getId() > 0) {
                newId = foundWasteReason.getId();
                selectedObject = foundWasteReason;
                selectedObject.setId(oldId);
                isAvailable = true;
            }

        }

        if (processType == 1) {
            if (isAvailable) {
                selectedObject.setId(newId);
                result = wasteReasonService.update(selectedObject);
            } else {
                result = wasteReasonService.create(selectedObject);
            }
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
                context.execute("PF('wasteReasonPF').filter();");
            }

        }
        if (processType == 2) {
            if (isAvailable) {
                selectedObject.setId(newId);
                result = wasteReasonService.updateAvailableWasteReason(oldId, newId);
            } else {
                result = wasteReasonService.update(selectedObject);
            }

            if (result > 0) {
                if (isAvailable) {
                    listOfObjects = findall();
                }
                context.execute("PF('wasteReasonPF').filter();");
            }
        }
        if (result > 0) {
            context.execute("PF('dlg_wastereasondeffinitionproc').hide();");
            context.update("frmWasteRasonDefinition:dtbWasteReasonDefinition");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<WasteReason> findall() {
        return wasteReasonService.findAll();

    }

    public void update() {
        processType = 2;
        oldId = selectedObject.getId();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgWasteReasonDefinitionProc");
        context.execute("PF('dlg_wastereasondeffinitionproc').show();");
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedObject.getCenterwastereason_id() > 0) {
            result = wasteReasonService.deleteForOtherBranch(selectedObject);
        } else {
            result = wasteReasonService.delete(selectedObject);
        }
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("frmWasteRasonDefinition:dtbWasteReasonDefinition");
            context.execute("PF('wasteReasonPF').filter();");
            context.execute("PF('dlg_wastereasondeffinitionproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void testBeforeDelete() {
        int result = 0;
        result = wasteReasonService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmWasteRasonDefinition:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausewastereasonisrelatedtowaste")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

}
