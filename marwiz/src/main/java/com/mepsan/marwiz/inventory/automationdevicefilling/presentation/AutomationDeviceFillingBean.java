/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.01.2020 08:44:41
 */
package com.mepsan.marwiz.inventory.automationdevicefilling.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItem;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceItemService;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class AutomationDeviceFillingBean extends GeneralDefinitionBean<AutomationDevice> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{automationDeviceService}")
    public IAutomationDeviceService automationDeviceService;

    @ManagedProperty(value = "#{automationDeviceItemService}")
    public IAutomationDeviceItemService automationDeviceItemService;

    private int activeIndex;
    private List<AutomationDeviceItem> listOfAutomationItem;
    private AutomationDeviceItem selectedAutomationItem;
    private BigDecimal oldBalance;

    public List<AutomationDeviceItem> listOfItemFilteredObjects;
    public String autoCompleteValueItem;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setAutomationDeviceService(IAutomationDeviceService automationDeviceService) {
        this.automationDeviceService = automationDeviceService;
    }

    public List<AutomationDeviceItem> getListOfAutomationItem() {
        return listOfAutomationItem;
    }

    public void setListOfAutomationItem(List<AutomationDeviceItem> listOfAutomationItem) {
        this.listOfAutomationItem = listOfAutomationItem;
    }

    public AutomationDeviceItem getSelectedAutomationItem() {
        return selectedAutomationItem;
    }

    public void setSelectedAutomationItem(AutomationDeviceItem selectedAutomationItem) {
        this.selectedAutomationItem = selectedAutomationItem;
    }

    public void setAutomationDeviceItemService(IAutomationDeviceItemService automationDeviceItemService) {
        this.automationDeviceItemService = automationDeviceItemService;
    }

    public List<AutomationDeviceItem> getListOfItemFilteredObjects() {
        return listOfItemFilteredObjects;
    }

    public void setListOfItemFilteredObjects(List<AutomationDeviceItem> listOfItemFilteredObjects) {
        this.listOfItemFilteredObjects = listOfItemFilteredObjects;
    }

    public String getAutoCompleteValueItem() {
        return autoCompleteValueItem;
    }

    public void setAutoCompleteValueItem(String autoCompleteValueItem) {
        this.autoCompleteValueItem = autoCompleteValueItem;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfAutomationItem = new ArrayList<>();
        selectedAutomationItem = new AutomationDeviceItem();
        oldBalance = BigDecimal.valueOf(0);

        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true);

        setListTab(sessionBean.checkAuthority(new int[]{68, 69}, 1));
        setListBtn(sessionBean.checkAuthority(new int[]{301}, 0));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void update() {
        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }

        listOfAutomationItem = automationDeviceItemService.listOfShelfOnlyWithProduct(selectedObject);
        RequestContext.getCurrentInstance().update("pngAutomationDeviceFillingTab");
        RequestContext.getCurrentInstance().update("dlgAutomationDeviceFillingProcess");
        RequestContext.getCurrentInstance().execute("PF('dlg_AutomationDeviceFillingProcess').show();");
    }

    @Override
    public List<AutomationDevice> findall() {
        return automationDeviceService.findAll(" AND vm.type_id <> 111");
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));

        if (activeIndex == 69) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            AutomationDeviceFillingMovementTabBean automationDeviceFillingMovementTabBean = (AutomationDeviceFillingMovementTabBean) viewMap.get("automationDeviceFillingMovementTabBean");
            if (automationDeviceFillingMovementTabBean != null) {
                automationDeviceFillingMovementTabBean.init();
            }

            RequestContext.getCurrentInstance().update("tbvAutomationDeviceFillingProcess:frmMovementDataTable:dtbMovement");
        }
    }

    public void createShelfDialog() {
        if (selectedAutomationItem.getBalance() != null) {
            oldBalance = selectedAutomationItem.getBalance();
        } else {
            oldBalance = BigDecimal.valueOf(0);
            selectedAutomationItem.setBalance(BigDecimal.valueOf(0));
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_ShelfFillingProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;
        BigDecimal substractBalance = BigDecimal.valueOf(0);
        BigDecimal totalCount = BigDecimal.valueOf(0);

        for (AutomationDeviceItem item : listOfAutomationItem) {
            if (selectedAutomationItem.getStock().getId() == item.getStock().getId()) {
                if (item.getBalance() != null) {
                    totalCount = totalCount.add(item.getBalance());
                }
            }
        }

        if (totalCount.compareTo(selectedAutomationItem.getWarehouseAmount()) == 1) {
            selectedAutomationItem.setBalance(oldBalance);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("sumofavailablequantitycannotbegreaterthanwarehousequantity")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (selectedAutomationItem.getBalance().compareTo(selectedAutomationItem.getMaxStockLevel()) == 1) {
            selectedAutomationItem.setBalance(oldBalance);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("availablequantitycannotbegreaterthanmaxcapacity")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            if (selectedAutomationItem.getBalance().subtract(oldBalance).compareTo(BigDecimal.valueOf(0)) != 0) {
                AutomationDeviceItemMovement automationDeviceItemMovement = new AutomationDeviceItemMovement();
                automationDeviceItemMovement.getAutomationDeviceItem().setId(selectedAutomationItem.getId());
                automationDeviceItemMovement.getAutomationDeviceItem().getStock().setId(selectedAutomationItem.getStock().getId());
                automationDeviceItemMovement.setType(2);

                substractBalance = selectedAutomationItem.getBalance().subtract(oldBalance);

                if (substractBalance.compareTo(BigDecimal.valueOf(0)) == 1) {
                    automationDeviceItemMovement.setQuantity(substractBalance);
                    automationDeviceItemMovement.setIsDirection(true);

                } else {
                    automationDeviceItemMovement.setQuantity(substractBalance.multiply(BigDecimal.valueOf(-1)));
                    automationDeviceItemMovement.setIsDirection(false);
                }

                result = automationDeviceItemService.createMovement(automationDeviceItemMovement);

                if (result > 0) {
                    listOfAutomationItem = automationDeviceItemService.listOfShelfOnlyWithProduct(selectedObject);
                    RequestContext.getCurrentInstance().execute("PF('automationDeviceFillingShelfPF').filter();");
                    RequestContext.getCurrentInstance().update("tbvAutomationDeviceFillingProcess:frmShelfTab:dtbShelf");
                    RequestContext.getCurrentInstance().execute("PF('dlg_ShelfFillingProcess').hide();");
                }

            } else {
                result = 1;
                RequestContext.getCurrentInstance().execute("PF('dlg_ShelfFillingProcess').hide();");
            }

            sessionBean.createUpdateMessage(result);
        }

    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedAutomationItem = context.getApplication().evaluateExpressionGet(context, "#{automationDeviceItem}", AutomationDeviceItem.class);

        oldBalance = (BigDecimal) event.getOldValue();
        save();

        RequestContext.getCurrentInstance().execute("updateDatatable()");

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
