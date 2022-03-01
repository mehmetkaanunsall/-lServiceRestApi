/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 02:29:18
 */
package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseShelfService;
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
public class WarehouseStockTabShelfTabBean extends AuthenticationLists {

    private Stock stock;
    private Warehouse warehouse;
    private WarehouseShelfStockCon selectedObject;
    private int processType;
    private List<WarehouseShelfStockCon> listOfWarehouseShelfStockCon;
    private List<WarehouseShelf> listOfWarehouseShelf;

    @ManagedProperty(value = "#{sessionBean}") // session
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{warehouseShelfStockConService}")
    private IWarehouseShelfStockConService warehouseShelfStockConService;

    @ManagedProperty(value = "#{warehouseShelfService}")
    private IWarehouseShelfService warehouseShelfService;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public WarehouseShelfStockCon getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WarehouseShelfStockCon selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<WarehouseShelfStockCon> getListOfWarehouseShelfStockCon() {
        return listOfWarehouseShelfStockCon;
    }

    public void setListOfWarehouseShelfStockCon(List<WarehouseShelfStockCon> listOfWarehouseShelfStockCon) {
        this.listOfWarehouseShelfStockCon = listOfWarehouseShelfStockCon;
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

    public void setWarehouseShelfStockConService(IWarehouseShelfStockConService warehouseShelfStockConService) {
        this.warehouseShelfStockConService = warehouseShelfStockConService;
    }

    public void setWarehouseShelfService(IWarehouseShelfService warehouseShelfService) {
        this.warehouseShelfService = warehouseShelfService;
    }

    @PostConstruct
    public void init() {
        System.out.println("-------WarehouseStockTabShelfTabBean-----");
        stock = new Stock();
        warehouse = new Warehouse();
        selectedObject = new WarehouseShelfStockCon();
        listOfWarehouseShelfStockCon = new ArrayList<>();
        listOfWarehouseShelf = new ArrayList<>();
        if (sessionBean.parameter instanceof Warehouse) {
            warehouse = (Warehouse) sessionBean.parameter;
        }
        selectedObject.getWarehouseShelf().setWareHouse(warehouse);
        setListBtn(sessionBean.checkAuthority(new int[]{149, 150, 151}, 0));

    }

    public void createDialog(int i) {
        processType = i;
        if (processType == 1) {
            selectedObject = new WarehouseShelfStockCon();

        }
        System.out.println("-- create dialog stok --" + stock.getId());
        selectedObject.setStock(stock);
        selectedObject.getWarehouseShelf().setWareHouse(warehouse);
        listOfWarehouseShelf = warehouseShelfService.selectShelfWithoutCon(selectedObject.getWarehouseShelf().getWareHouse(), selectedObject.getStock());
        if (processType == 2) {
            WarehouseShelf warehouseShelf=new WarehouseShelf();
            warehouseShelf.setId(selectedObject.getWarehouseShelf().getId());
            warehouseShelf.setCode(selectedObject.getWarehouseShelf().getCode());
            warehouseShelf.setName(selectedObject.getWarehouseShelf().getName());
            listOfWarehouseShelf.add(0, warehouseShelf);
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_warehousestockshelftabproc').show();");
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
            bringShelf();
            RequestContext.getCurrentInstance().update("frmWarehouseStockProcess:tbvWarehouseStockTabShelfProc:frmStockTabShelfTab:dtbShelf");
            RequestContext.getCurrentInstance().execute("PF('dlg_warehousestockshelftabproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void bringShelf() {
        for (WarehouseShelf warehouseShelf : listOfWarehouseShelf) {
            if (warehouseShelf.getId() == selectedObject.getWarehouseShelf().getId()) {
                selectedObject.getWarehouseShelf().setName(warehouseShelf.getName());
                selectedObject.getWarehouseShelf().setCode(warehouseShelf.getCode());
                break;
            }
        }
    }

    public void findAll() {
        listOfWarehouseShelfStockCon = warehouseShelfStockConService.findAll("stockTab", selectedObject);
    }

    public void delete() {
        int result = 0;
        result = warehouseShelfStockConService.delete(selectedObject);
        if (result > 0) {
            listOfWarehouseShelfStockCon.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_warehousestockshelftabproc').hide();");
            context.update("frmWarehouseStockProcess:tbvWarehouseStockTabShelfProc:frmStockTabShelfTab:dtbShelf");
        }
        sessionBean.createUpdateMessage(result);
    }

}
