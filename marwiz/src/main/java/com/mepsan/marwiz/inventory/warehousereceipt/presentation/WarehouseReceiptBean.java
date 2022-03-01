/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.01.2018 02:50:38s
 */
package com.mepsan.marwiz.inventory.warehousereceipt.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.warehousereceipt.business.GFWarehouseReceiptService;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseReceiptService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped

public class WarehouseReceiptBean extends GeneralBean<WarehouseReceipt> {

    private int isDirection;

    @ManagedProperty(value = "#{warehouseReceiptService}")
    private IWarehouseReceiptService warehouseReceiptService;

    @ManagedProperty(value = "#{gfWarehouseReceiptService}")
    private GFWarehouseReceiptService gfWarehouseReceiptService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setWarehouseReceiptService(IWarehouseReceiptService warehouseReceiptService) {
        this.warehouseReceiptService = warehouseReceiptService;
    }

    public int getIsDirection() {
        return isDirection;
    }

    public void setIsDirection(int isDirection) {
        this.isDirection = isDirection;
    }

    public void setGfWarehouseReceiptService(GFWarehouseReceiptService gfWarehouseReceiptService) {
        this.gfWarehouseReceiptService = gfWarehouseReceiptService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----WarehouseReceiptBean-----------");
        isDirection = 2;
        listOfObjects = findall(" ");
        toogleList = Arrays.asList(true, true, true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{31}, 0));

    }

    @Override
    public void create() {
        List<Object> list = new ArrayList<>();
        WarehouseReceipt receipt = new WarehouseReceipt();
        if (isDirection == 0) {
            receipt.setIsDirection(true);
        } else if (isDirection == 1) {
            receipt.setIsDirection(false);
        } else {
            receipt.setIsDirection(true);
        }
        list.add(receipt);
        marwiz.goToPage("/pages/inventory/warehousereceipt/warehousereceiptprocess.xhtml", list, 0, 40);
    }

    public void update() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/inventory/warehousereceipt/warehousereceiptprocess.xhtml", list, 0, 40);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        String where = "";
        switch (isDirection) {
            case 0://giriş fişi
                where = " AND whr.is_direction=true ";
                break;
            case 1://çıkış fişi
                where = " AND whr.is_direction=false ";
                break;
            case 2://hepsi
                where = " ";
                break;
            default:
                break;
        }
        if (autoCompleteValue == null) {

            listOfObjects = findall(where);
        } else {
            gfWarehouseReceiptService.makeSearch(autoCompleteValue, where);
            listOfObjects = gfWarehouseReceiptService.searchResult;
        }
    }

    @Override
    public LazyDataModel<WarehouseReceipt> findall(String where) {
        return new CentrowizLazyDataModel<WarehouseReceipt>() {
            @Override
            public List<WarehouseReceipt> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                String where1 = "";
                switch (isDirection) {
                    case 0://giriş fişi
                        where1 = where + " AND whr.is_direction=true ";
                        break;
                    case 1://çıkış fişi
                        where1 = where + " AND whr.is_direction=false ";
                        break;
                    case 2://hepsi
                        where1 = where + " ";
                        break;
                    default:
                        break;
                }

                List<WarehouseReceipt> result = warehouseReceiptService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                int count = warehouseReceiptService.count(where1);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void bringListAccordingToIsDirection() {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmWarehouseReceipt:dtbWarehouseReceipt");
        dataTable.setFirst(0);

        listOfObjects = findall(" ");
    }

}
