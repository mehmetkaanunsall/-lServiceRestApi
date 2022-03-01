package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseMovementTabService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 * Bu CLass Depo İşlemleri Sayfasında Ürün Hareketlerinin listelenmesini sağlar.
 *
 * @author Samet Dağ
 *
 * @date 17.10.2018
 */
@ManagedBean
@ViewScoped
public class WarehouseMovementTabBean extends GeneralBean<StockMovement> {

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    public StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{warehouseMovementTabService}")
    public IWarehouseMovementTabService warehouseMovementTabService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private Date beginDate, endDate;
    private int opType;
    private List<Stock> listOfStock;
    private Warehouse selectedWarehouse;

    public Warehouse getSelectedWarehouse() {
        return selectedWarehouse;
    }

    public void setSelectedWarehouse(Warehouse selectedWarehouse) {
        this.selectedWarehouse = selectedWarehouse;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public void setWarehouseMovementTabService(IWarehouseMovementTabService warehouseMovementTabService) {
        this.warehouseMovementTabService = warehouseMovementTabService;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    @Override
    @PostConstruct
    public void init() {

        if (sessionBean.parameter instanceof Warehouse) {
            selectedWarehouse = (Warehouse) sessionBean.parameter;
            Calendar calendar = GregorianCalendar.getInstance();
            setEndDate(new Date());
            calendar.setTime(getEndDate());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            setEndDate(calendar.getTime());

            calendar.setTime(getEndDate());
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            setBeginDate(calendar.getTime());

            setOpType(3);

            listOfStock = new ArrayList<>();
           find();

        }

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<StockMovement> findall(String where) {
        return new CentrowizLazyDataModel<StockMovement>() {
            @Override
            public List<StockMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<StockMovement> result = warehouseMovementTabService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, opType, beginDate, endDate, listOfStock, selectedWarehouse);

                int count = warehouseMovementTabService.count(where, listOfStock, opType, beginDate, endDate, selectedWarehouse);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void openDialog() {
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        if (!listOfStock.isEmpty()) {
            if (listOfStock.get(0).getId() == 0) {
                stockBookCheckboxFilterBean.isAll = true;
            } else {
                stockBookCheckboxFilterBean.isAll = false;
            }
        }
        stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
    }

    public void updateAllInformation() {

        listOfStock.clear();
        if (stockBookCheckboxFilterBean.isAll) {
        
            Stock s = new Stock(0);
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Stock stock = new Stock(0);
                stock.setName(sessionBean.loc.getString("all"));
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            }
        } else if (!stockBookCheckboxFilterBean.isAll) {
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

        if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
        }
        RequestContext.getCurrentInstance().update("tbvWarehouseProc:frmMovementTab:txtStock");

    }

    public void find() {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("tbvWarehouseProc:dtbItems");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(" ");

    }

}
