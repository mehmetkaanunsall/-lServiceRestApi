/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 04:50:55
 */
package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
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
public class WarehouseProcessBean extends AuthenticationLists{

    private int processType;
    private Warehouse selectedObject;
    private int activeIndex;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Warehouse getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Warehouse selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @PostConstruct
    public void init() {

        System.out.println("-------WarehouseProcessBean----------");

        if (sessionBean.parameter instanceof Warehouse) {
            selectedObject = (Warehouse) sessionBean.parameter;
            processType = 2;
        } else {
            selectedObject = new Warehouse();
            processType = 1;
        }
        
        setListBtn(sessionBean.checkAuthority(new int[]{146, 147}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{40, 41, 42}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            result = warehouseService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                marwiz.goToPage("/pages/inventory/warehouse/warehouseprocess.xhtml", selectedObject, 1, 28);
            }
        } else if (processType == 2) {

            result = warehouseService.update(selectedObject);
            if (result > 0) {
                marwiz.goToPage("/pages/inventory/warehouse/warehouse.xhtml", null, 1, 27);
            }

        }
        sessionBean.createUpdateMessage(result);

    }

    public void goToBack() {
        marwiz.goToPage("/pages/inventory/warehouse/warehouse.xhtml", null, 1, 27);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void testBeforeDelete() {
        
        if(selectedObject.isIsAutomat()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thiswarehousecannotbedeletedbecauseitbelongstotheautomationdevice")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
        else{
        int result = 0;
        result = warehouseService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmWarehouseProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausewarehousehasmovementorstock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
        }
    }

    public void delete() {
        int result = 0;
        result = warehouseService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/inventory/warehouse/warehouse.xhtml", null, 1, 27);
        }
        sessionBean.createUpdateMessage(result);
    }

}
