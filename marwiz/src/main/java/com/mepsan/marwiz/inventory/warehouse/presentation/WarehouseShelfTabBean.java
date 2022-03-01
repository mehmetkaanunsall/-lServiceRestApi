/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 10:56:40
 */
package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseShelfService;
import com.mepsan.marwiz.inventory.warehouse.dao.IWarehouseShelfDao;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped

public class WarehouseShelfTabBean extends AuthenticationLists {

    private Warehouse wareHouse;
    private WarehouseShelf selectedObject;
    private int processType;
    private List<WarehouseShelf> listOfWarehouseShelf;
    private int activeIndex;

    @ManagedProperty(value = "#{sessionBean}") // session
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{warehouseShelfService}")
    private IWarehouseShelfService warehouseShelfService;

    @ManagedProperty(value = "#{warehouseShelfTabStockTabBean}")
    private WarehouseShelfTabStockTabBean warehouseShelfTabStockTabBean;

    public Warehouse getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(Warehouse wareHouse) {
        this.wareHouse = wareHouse;
    }

    public WarehouseShelf getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WarehouseShelf selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<WarehouseShelf> getListOfWarehouseShelf() {
        return listOfWarehouseShelf;
    }

    public void setListOfWarehouseShelf(List<WarehouseShelf> listOfWarehouseShelf) {
        this.listOfWarehouseShelf = listOfWarehouseShelf;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWarehouseShelfService(IWarehouseShelfService warehouseShelfService) {
        this.warehouseShelfService = warehouseShelfService;
    }

    public void setWarehouseShelfTabStockTabBean(WarehouseShelfTabStockTabBean warehouseShelfTabStockTabBean) {
        this.warehouseShelfTabStockTabBean = warehouseShelfTabStockTabBean;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    @PostConstruct
    public void init() {
        System.out.println("-------WarehouseShelfTabBean-----");
        wareHouse = new Warehouse();
        selectedObject = new WarehouseShelf();
        if (sessionBean.parameter instanceof Warehouse) {
            wareHouse = (Warehouse) sessionBean.parameter;
        }
        listOfWarehouseShelf = new ArrayList<>();
        listOfWarehouseShelf = warehouseShelfService.findAll(wareHouse);
        setListBtn(sessionBean.checkAuthority(new int[]{152, 153, 154}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{44}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    public void createDialog(int i) {
        processType = i;

        if (processType == 1) {
            selectedObject = new WarehouseShelf();
            selectedObject.setWareHouse(wareHouse);
        } else if (processType == 2) {
            selectedObject.setWareHouse(wareHouse);
            warehouseShelfTabStockTabBean.setWarehouseShelf(selectedObject);
            System.out.println("----creatediaolg------------" + selectedObject.getId());
            warehouseShelfTabStockTabBean.findAll();
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_warehouseshelfproc').show();");
    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            result = warehouseShelfService.create(selectedObject);
            selectedObject.setId(result);
            listOfWarehouseShelf.add(selectedObject);
            if (result > 0) {
                processType = 2;
                warehouseShelfTabStockTabBean.setWarehouseShelf(selectedObject);
                warehouseShelfTabStockTabBean.findAll();
                RequestContext.getCurrentInstance().update("tbvWarehouseProc:frmShelfTab:dtbShelf");
                RequestContext.getCurrentInstance().update("frmShelfTabProcess");

            }

        } else if (processType == 2) {
            result = warehouseShelfService.update(selectedObject);
            if (result > 0) {
                RequestContext.getCurrentInstance().update("tbvWarehouseProc:frmShelfTab:dtbShelf");
                RequestContext.getCurrentInstance().execute("PF('dlg_warehouseshelfproc').hide();");
            }
        }

        sessionBean.createUpdateMessage(result);
    }

    public void delete() {
        int result = 0;
        result = warehouseShelfService.delete(selectedObject);
        if (result > 0) {
            listOfWarehouseShelf.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_warehouseshelfproc').hide();");
            context.update("tbvWarehouseProc:frmShelfTab:dtbShelf");
        }
        sessionBean.createUpdateMessage(result);
    }

}
