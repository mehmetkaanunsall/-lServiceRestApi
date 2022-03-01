/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   24.01.2018 01:45:43
 */
package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseShelfStockConService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped

public class WarehouseShelfTabStockTabBean extends AuthenticationLists {

    private WarehouseShelf warehouseShelf;
    private WarehouseShelfStockCon selectedObject;
    private int processType;
    private Warehouse warehouse;
    private List<WarehouseShelfStockCon> listOfWarehouseShelfStockCon;

    @ManagedProperty(value = "#{sessionBean}") // session
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookFilterBean}") // session
    private StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{warehouseShelfStockConService}")
    private IWarehouseShelfStockConService warehouseShelfStockConService;

    public WarehouseShelfStockCon getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WarehouseShelfStockCon selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<WarehouseShelfStockCon> getListOfWarehouseShelfStockCon() {
        return listOfWarehouseShelfStockCon;
    }

    public void setListOfWarehouseShelfStockCon(List<WarehouseShelfStockCon> listOfWarehouseShelfStockCon) {
        this.listOfWarehouseShelfStockCon = listOfWarehouseShelfStockCon;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWarehouseShelfStockConService(IWarehouseShelfStockConService warehouseShelfStockConService) {
        this.warehouseShelfStockConService = warehouseShelfStockConService;
    }

    public WarehouseShelf getWarehouseShelf() {
        return warehouseShelf;
    }

    public void setWarehouseShelf(WarehouseShelf warehouseShelf) {
        this.warehouseShelf = warehouseShelf;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @PostConstruct
    public void init() {
        System.out.println("-------WarehouseShelfTabStockTabBean-----");
        selectedObject = new WarehouseShelfStockCon();
        listOfWarehouseShelfStockCon = new ArrayList<>();
        if (sessionBean.parameter instanceof Warehouse) {
            warehouse = (Warehouse) sessionBean.parameter;
        }

        setListBtn(sessionBean.checkAuthority(new int[]{155, 156, 157}, 0));

    }

    public void createDialog(int i) {
        processType = i;
        if (processType == 1) {
            selectedObject = new WarehouseShelfStockCon();

        }
        System.out.println("----createdialog 2------" + warehouseShelf.getId());
        selectedObject.setWarehouseShelf(warehouseShelf);

        RequestContext.getCurrentInstance().execute("PF('dlg_warehouseshelfstocktabproc').show();");
    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            result = warehouseShelfStockConService.create(selectedObject);
            selectedObject.setId(result);
            listOfWarehouseShelfStockCon.add(selectedObject);

        } else {
            result = warehouseShelfStockConService.update(selectedObject);
        }

        if (result > 0) {
            RequestContext.getCurrentInstance().update("frmShelfTabProcess:tbvWarehouseShelfTabStockProc:frmShelfTabStockTab:dtbShelf");
            RequestContext.getCurrentInstance().execute("PF('dlg_warehouseshelfstocktabproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null) {

            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmShelfStockTabProcess:txtStock");
            stockBookFilterBean.setSelectedData(null);
        }
    }

    public void findAll() {
        selectedObject.setWarehouseShelf(warehouseShelf);
        listOfWarehouseShelfStockCon = warehouseShelfStockConService.findAll("shelfTab", selectedObject);
    }

    public void delete() {
        int result = 0;
        result = warehouseShelfStockConService.delete(selectedObject);
        if (result > 0) {
            listOfWarehouseShelfStockCon.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_warehouseshelfstocktabproc').hide();");
            context.update("frmShelfTabProcess:tbvWarehouseShelfTabStockProc:frmShelfTabStockTab:dtbShelf");
        }
        sessionBean.createUpdateMessage(result);
    }

}
