/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 04.02.2019 13:35:23
 */
package com.mepsan.marwiz.automation.tank.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.automation.tank.business.ITankService;
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
public class TankProcessBean extends AuthenticationLists {

    private int processType;
    private Warehouse selectedObject;
    private int activeIndex;
    private List<WarehouseItem> listOfWareHouseItem;
    private WarehouseItem selectedWareHouseItem;
    private boolean isThereWarehouseItem;

    @ManagedProperty(value = "#{tankService}")
    private ITankService tankService;

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

    public void setTankService(ITankService tankService) {
        this.tankService = tankService;
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

    public List<WarehouseItem> getListOfWareHouseItem() {
        return listOfWareHouseItem;
    }

    public void setListOfWareHouseItem(List<WarehouseItem> listOfWareHouseItem) {
        this.listOfWareHouseItem = listOfWareHouseItem;
    }

    public WarehouseItem getSelectedWareHouseItem() {
        return selectedWareHouseItem;
    }

    public void setSelectedWareHouseItem(WarehouseItem selectedWareHouseItem) {
        this.selectedWareHouseItem = selectedWareHouseItem;
    }

    @PostConstruct
    public void init() {

        System.out.println("-------TankProcessBean----------");
        isThereWarehouseItem = false;
        selectedObject = new Warehouse();
        processType = 1;
        if (sessionBean.parameter instanceof ArrayList) {
            System.out.println("Array L'st");
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) != null) {
                    System.out.println("(ArrayList) sessionBean.parameter).get(i)=" + ((ArrayList) sessionBean.parameter).get(i));

                }
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Warehouse) {//Tabanca Sayfasından Geri Geldi İse
                    processType = 2;
                   // activeIndex = 1;
                    selectedObject = (Warehouse) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }
        System.out.println("processType=" + processType + "size ==== " + ((ArrayList) sessionBean.parameter).size());
        selectedWareHouseItem = new WarehouseItem();
        listOfWareHouseItem = tankService.selectListWareHouseItem(selectedObject);
        for (WarehouseItem warehouseItem : listOfWareHouseItem) {//Tanka Bağlı Ürün Olduğunda Getirir. Yok İse Obje Yenilenmiş Olur
            if (warehouseItem.getId() > 0) {
                selectedWareHouseItem = warehouseItem;
                isThereWarehouseItem = true;
                break;
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{165, 166}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{46, 47}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void save() {
        int result = 0;
        int resultItem = 0;
        if (processType == 1) {
            result = tankService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                if (!isThereWarehouseItem) {
                    resultItem = tankService.createWareHouseItem(selectedWareHouseItem.getStock(), selectedObject);
                }
                if (resultItem > 0) {
                    marwiz.goToPage("/pages/automation/tank/tank.xhtml", null, 1, 110);
                }
            }
            sessionBean.createUpdateMessage(result);
        } else if (processType == 2) {
            result = tankService.update(selectedObject);
            if (isThereWarehouseItem) {
                resultItem = tankService.updateWareHouseItem(selectedWareHouseItem, selectedObject);
            } else {
                resultItem = tankService.createWareHouseItem(selectedWareHouseItem.getStock(), selectedObject);
            }
            if (result > 0 && resultItem > 0) {
                sessionBean.createUpdateMessage(result);
                marwiz.goToPage("/pages/automation/tank/tank.xhtml", null, 1, 110);
            } else {
                sessionBean.createUpdateMessage(-1);
            }
        }

    }

    public void goToBack() {
        List<Object> list = new ArrayList<>();
        list.addAll((ArrayList) sessionBean.getParameter());
        marwiz.goToPage("/pages/automation/tank/tank.xhtml", list, 1, 110);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void testBeforeDelete() {
        int result = 0;
        result = tankService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmTankProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausewarehousehasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = tankService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/automation/tank/tank.xhtml", null, 1, 110);
        }
        sessionBean.createUpdateMessage(result);
    }

}
