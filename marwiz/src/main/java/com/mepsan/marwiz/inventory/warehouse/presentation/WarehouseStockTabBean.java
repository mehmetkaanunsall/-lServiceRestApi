package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.warehouse.business.GFWarehouseItemService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseItemService;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date 22.01.2018 10:58:58
 */
@ManagedBean
@ViewScoped
public class WarehouseStockTabBean extends GeneralBean<WarehouseItem> {

    private Warehouse wareHouse;
    int processType;
    private List<Stock> listOfStock;
    private int activeIndex;

    @ManagedProperty(value = "#{sessionBean}") // session
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{warehouseStockTabShelfTabBean}")
    private WarehouseStockTabShelfTabBean warehouseStockTabShelfTabBean;

    @ManagedProperty(value = "#{warehouseItemService}")
    private IWarehouseItemService warehouseItemService;

    @ManagedProperty(value = "#{gfWarehouseItemService}")
    private GFWarehouseItemService gfWarehouseItemService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWarehouseStockTabShelfTabBean(WarehouseStockTabShelfTabBean warehouseStockTabShelfTabBean) {
        this.warehouseStockTabShelfTabBean = warehouseStockTabShelfTabBean;
    }

    public void setWarehouseItemService(IWarehouseItemService warehouseItemService) {
        this.warehouseItemService = warehouseItemService;
    }

    public Warehouse getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(Warehouse wareHouse) {
        this.wareHouse = wareHouse;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public int getProcessType() {
        return processType;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public void setGfWarehouseItemService(GFWarehouseItemService gfWarehouseItemService) {
        this.gfWarehouseItemService = gfWarehouseItemService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------WarehouseStockTabBean--------");

        if (sessionBean.parameter instanceof Warehouse) {
            wareHouse = (Warehouse) sessionBean.parameter;
        }

        toogleList = Arrays.asList(true, true, true, true, true, true);

        listOfObjects = findall(" ");
        listOfStock = new ArrayList();
        setListBtn(sessionBean.checkAuthority(new int[]{148}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{43}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void update() {
        processType = 2;
        selectedObject.setWareHouse(wareHouse);
        warehouseStockTabShelfTabBean.setStock(selectedObject.getStock());
        warehouseStockTabShelfTabBean.setWarehouse(selectedObject.getWareHouse());
        warehouseStockTabShelfTabBean.getSelectedObject().setStock(selectedObject.getStock());
        warehouseStockTabShelfTabBean.getSelectedObject().getWarehouseShelf().setWareHouse(selectedObject.getWareHouse());
        warehouseStockTabShelfTabBean.findAll();
        RequestContext.getCurrentInstance().execute("PF('dlg_warehousestockproc').show();");
    }

    public void onCellEdit(CellEditEvent event) {
        int result = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        selectedObject = context.getApplication().evaluateExpressionGet(context, "#{Stock}", WarehouseItem.class);

        result = warehouseItemService.update(selectedObject);
        sessionBean.createUpdateMessage(result);

    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {
            listOfObjects = findall(" ");
        } else {
            gfWarehouseItemService.makeSearch(autoCompleteValue, wareHouse);
            listOfObjects = gfWarehouseItemService.searchResult;
        }
    }

    @Override
    public LazyDataModel<WarehouseItem> findall(String where) {
        return new CentrowizLazyDataModel<WarehouseItem>() {
            @Override
            public List<WarehouseItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<WarehouseItem> result = warehouseItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, wareHouse);

                int count = warehouseItemService.count(where, wareHouse);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public NumberFormat unitNumberFormat(int currencyRounding) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(currencyRounding);
        formatter.setMinimumFractionDigits(currencyRounding);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        return formatter;
    }

    public void openDialog() {

        stockBookCheckboxFilterBean.getSelectedDataList().clear();
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        stockBookCheckboxFilterBean.setIsWithoutSalePrice(false);
        
       // stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
    }

    public void updateAllInformation(ActionEvent event) {

        listOfStock.clear();
        if (stockBookCheckboxFilterBean.isAll) {

            Stock s = new Stock(0);
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Stock stock = new Stock(0);
                stock.setName(sessionBean.loc.getString("all"));
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            }
        } else if (!stockBookCheckboxFilterBean.isAll) {

            if (stockBookCheckboxFilterBean.isIsWithoutSalePrice()) { // satış fiyatı olanlar checkboxı seçili ise
                Stock stock = new Stock(-1);
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            } else if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());
        RequestContext.getCurrentInstance().update("dlgConfirmTransferStock");
        RequestContext.getCurrentInstance().execute("PF('dlgConfirm').show();");

    }

    public void addStock() {
        if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {

            int result = 0;
            result = warehouseItemService.addStock(wareHouse, listOfStock);
            if (result > 0) {

                listOfObjects = findall(" ");
                RequestContext.getCurrentInstance().update("tbvWarehouseProc:frmStockTab");
            }
            sessionBean.createUpdateMessage(result);

        }

    }

    public void createPdf() {
        String createWhere = " ";
        if (autoCompleteValue != null) {
            createWhere = createWhere + " " + gfWarehouseItemService.createWhere(autoCompleteValue);
        }
        warehouseItemService.exportPdf(createWhere, toogleList, wareHouse);

    }

    public void showWarn() {
        if (listOfObjects.getRowCount() > 200000) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("youdontexportrecordwhichisbiggerthan200000")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void showLimitMessege() {
        FacesMessage message = new FacesMessage();

        message.setSummary(sessionBean1.loc.getString("notification"));

        message.setDetail(sessionBean1.loc.getString("youdontexportrecordwhichisbiggerthan60000"));

        message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("grwProcessMessage");
    }

    public void waitToExportPdf() throws InterruptedException {
        try {
            Double d = (double) listOfObjects.getRowCount() / 6;
            TimeUnit.MILLISECONDS.sleep(d.intValue());
        } catch (Exception e) {
        }

    }

}
