/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 12:21:35
 */
package com.mepsan.marwiz.general.report.fulltakingreport.presentation;

import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReport;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.mepsan.marwiz.general.report.fulltakingreport.business.IFullTakingReportService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@ManagedBean
@ViewScoped
public class FullTakingReportBean extends GeneralReportBean<FullTakingReport> {

    @ManagedProperty(value = "#{fullTakingReportService}")
    public IFullTakingReportService productInventoryReportService;

    @ManagedProperty(value = "#{warehouseService}")
    public IWarehouseService warehouseService;

    @ManagedProperty(value = "#{stockTakingService}")
    public IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    private List<Warehouse> listOfWarehouse;
    private String createWhere;
    private List<StockTaking> listOfStockTaking;
    private List<Stock> listOfStock;
    private int differentType;
    private List<FullTakingReport> listOfTotals;
    private List<Categorization> listOfCategorization;
    private FullTakingReport selectedFullTakingReport;
    private HashMap<Integer, BigDecimal> stockTotals;

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public void setProductInventoryReportService(IFullTakingReportService productInventoryReportService) {
        this.productInventoryReportService = productInventoryReportService;
    }

    public List<StockTaking> getListOfStockTaking() {
        return listOfStockTaking;
    }

    public void setListOfStockTaking(List<StockTaking> listOfStockTaking) {
        this.listOfStockTaking = listOfStockTaking;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public int getDifferentType() {
        return differentType;
    }

    public void setDifferentType(int differentType) {
        this.differentType = differentType;
    }

    public List<FullTakingReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<FullTakingReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public FullTakingReport getSelectedFullTakingReport() {
        return selectedFullTakingReport;
    }

    public void setSelectedFullTakingReport(FullTakingReport selectedFullTakingReport) {
        this.selectedFullTakingReport = selectedFullTakingReport;
    }

    public HashMap<Integer, BigDecimal> getStockTotals() {
        return stockTotals;
    }

    public void setStockTotals(HashMap<Integer, BigDecimal> stockTotals) {
        this.stockTotals = stockTotals;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------ProductInventoryReportBean");
        differentType = 0;
        selectedObject = new FullTakingReport();
        selectedFullTakingReport = new FullTakingReport();
        listOfStock = new ArrayList<>();
        listOfCategorization = new ArrayList<>();
        listOfWarehouse = warehouseService.selectListWarehouse(" AND iw.is_fuel=FALSE ");
        listOfStockTaking = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        stockTotals = new HashMap<>();
        if (!listOfWarehouse.isEmpty()) {
            listOfStockTaking = stockTakingService.selectStockTakingByWarehouse(listOfWarehouse.get(0));
            selectedObject.getWarehouse().setId(listOfWarehouse.get(0).getId());
        }

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FullTakingReport) {
                    selectedObject = (FullTakingReport) ((ArrayList) sessionBean.parameter).get(i);
                    find();
                }
            }
        }

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
    }

    @Override
    public void find() {
        isFind = true;

        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);
        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        createWhere = productInventoryReportService.createWhere(selectedObject, differentType);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmProductInventoryReportDatatable:dtbProductInventoryReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        bringWarehouse();
        bringStockTaking();
        listOfObjects = findall(createWhere);

    }

    @Override
    public LazyDataModel<FullTakingReport> findall(String where) {
        return new CentrowizLazyDataModel<FullTakingReport>() {

            @Override
            public List<FullTakingReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<FullTakingReport> result = productInventoryReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject);
                listOfTotals = productInventoryReportService.totals(where, selectedObject);
                int count = 0;
                for (FullTakingReport total : listOfTotals) {
                    count = count + total.getStock().getId();
                }
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void changeWarehouse() {
        listOfStockTaking.clear();
        listOfStockTaking = stockTakingService.selectStockTakingByWarehouse(selectedObject.getWarehouse());
    }

    public void changeStockTaking() {
        for (StockTaking s : listOfStockTaking) {
            if (s.getId() == selectedObject.getStockTaking().getId()) {
                selectedObject.getStockTaking().setBeginDate(s.getBeginDate());
            }
        }
    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
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
            RequestContext.getCurrentInstance().update("frmProductInventoryReport:txtStock");
        } else {
            listOfCategorization.clear();
            if (categoryBookCheckboxFilterBean.isAll) {
                Categorization s = new Categorization(0);
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().contains(s)) {
                    categoryBookCheckboxFilterBean.getListOfCategorization().add(0, new Categorization(0, sessionBean.loc.getString("all")));
                }
            } else if (!categoryBookCheckboxFilterBean.isAll) {
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                    if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                        categoryBookCheckboxFilterBean.getListOfCategorization().remove(categoryBookCheckboxFilterBean.getListOfCategorization().get(0));
                    }
                }
            }
            listOfCategorization.addAll(categoryBookCheckboxFilterBean.getListOfCategorization());
            if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
            }

            RequestContext.getCurrentInstance().update("frmProductInventoryReport:txtCategory");
        }
    }

    /*
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
        RequestContext.getCurrentInstance().update("frmProductInventoryReport:txtStock");

    }*/
    public void openDialog(int type) {
        if (type == 1) {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }

            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);
        } else {
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
    }

    public void createPdf() {
        productInventoryReportService.exportPdf(createWhere, selectedObject, toogleList, differentType,calculateTotals(1),calculateTotals(2));
    }

    public void createExcel() throws IOException {
        productInventoryReportService.exportExcel(createWhere, selectedObject, toogleList, differentType,calculateTotals(1),calculateTotals(2));//, sessionBean.getLoc().getString("orderlistreport"));

    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(productInventoryReportService.exportPrinter(createWhere, selectedObject, toogleList, differentType,calculateTotals(1),calculateTotals(2))) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void bringWarehouse() {
        for (Warehouse w : listOfWarehouse) {
            if (w.getId() == selectedObject.getWarehouse().getId()) {
                selectedObject.getWarehouse().setName(w.getName());
                break;
            }
        }
    }

    public void bringStockTaking() {
        for (StockTaking s : listOfStockTaking) {
            if (s.getId() == selectedObject.getStockTaking().getId()) {
                selectedObject.getStockTaking().setName(s.getName());
                break;
            }
        }
    }

    public BigDecimal calculatePurchaseTaxTotal(BigDecimal total, int taxRate) {
        BigDecimal tax = BigDecimal.valueOf(taxRate);
        BigDecimal taxFactor = tax.movePointLeft(2).add(BigDecimal.ONE);//1,08
        BigDecimal tot = BigDecimal.ZERO;
        if (taxRate == 0) {
            tot = total;
        } else {
            tot = total.multiply(taxFactor);
        }
        return tot;
    }

    public BigDecimal calculateSaleTaxTotal(BigDecimal total, int taxRate) {
        BigDecimal tax = BigDecimal.valueOf(taxRate);
        BigDecimal taxFactor = tax.movePointLeft(2).add(BigDecimal.ONE);//1,08
        BigDecimal tot = BigDecimal.ZERO;
        if (taxRate == 0) {
            tot = total;
        } else {
            tot = total.divide(taxFactor, RoundingMode.HALF_EVEN);
        }
        return tot;
    }

    public String calculateTotals(int type) {
        String total = "";

        stockTotals.clear();

        for (FullTakingReport u : listOfTotals) {
            switch (type) {
                case 1:
                    // alış fiyatı kdvsiz

                    if (u.getLastPurchaseCurrency().getId() == 0) {
                        u.setLastPurchaseCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getLastPurchaseCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getLastPurchaseCurrency().getId());
                        stockTotals.put(u.getLastPurchaseCurrency().getId(), old.add(u.getLastPurchasePrice()));
                    } else {
                        stockTotals.put(u.getLastPurchaseCurrency().getId(), u.getLastPurchasePrice());
                    }
                    break;

                case 2:
                    // satış fiyatı kdvli
                    if (u.getLastSaleCurrency().getId() == 0) {
                        u.setLastSaleCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getLastSaleCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getLastSaleCurrency().getId());
                        stockTotals.put(u.getLastSaleCurrency().getId(), old.add(u.getLastSalePrice()));
                    } else {
                        stockTotals.put(u.getLastSaleCurrency().getId(), u.getLastSalePrice());
                    }
                    break;
                default:
                    break;
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : stockTotals.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp != 0) {
                if (temp == 0) {
                    temp = 1;
                    if (entry.getKey() != 0) {
                        total += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else if (entry.getKey() != 0) {
                    total += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            }
        }
        if (total.isEmpty() || total.equals("")) {
            total = "0";
        }
        return total;
    }

    /**
     * Stok Sayfasının Hareket Tabına Geçiş Yapar
     */
    public void gotoStockMovement() {
        List<Stock> list = stockService.findAll(0, 20, null, "ASC", null, " AND stck.id = " + selectedFullTakingReport.getStock().getId());
        Stock stock = new Stock();
        if (list.size() > 0) {
            stock = list.get(0);
        }
        List<Object> items = new ArrayList<>();
        items.add(selectedObject);
        items.add(stock);

        marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", items, 1, 12);
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
