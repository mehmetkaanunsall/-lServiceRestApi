/**
 * This class ...
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date   13.08.2018 12:21:35
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.presentation;

import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.business.IMovementReportBetweenWarehouseTakingsService;
import com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao.MovementReportBetweenWarehouseTakings;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;

@ManagedBean
@ViewScoped
public class MovementReportBetweenWarehouseTakingsBean extends GeneralReportBean<MovementReportBetweenWarehouseTakings> {

    @ManagedProperty(value = "#{movementReportBetweenWarehouseTakingsService}")
    public IMovementReportBetweenWarehouseTakingsService movementReportBetweenWarehouseTakingsService;

    @ManagedProperty(value = "#{stockTakingService}")
    public IStockTakingService stockTakingService;
    
    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private String createWhere;
    private List<StockTaking> listOfStockTaking1;
    private List<StockTaking> listOfStockTaking2;
    
    private List<Stock> listOfStock;
    private int differentType;
    private List<Categorization> listOfCategorization;

    private int taking1, taking2;
    
    private List<MovementReportBetweenWarehouseTakings> listOfTotals;
    private HashMap<Integer, BigDecimal> stockTotals;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IMovementReportBetweenWarehouseTakingsService getMovementReportBetweenWarehouseTakingsService() {
        return movementReportBetweenWarehouseTakingsService;
    }

    public void setMovementReportBetweenWarehouseTakingsService(IMovementReportBetweenWarehouseTakingsService movementReportBetweenWarehouseTakingsService) {
        this.movementReportBetweenWarehouseTakingsService = movementReportBetweenWarehouseTakingsService;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public List<StockTaking> getListOfStockTaking1() {
        return listOfStockTaking1;
    }

    public void setListOfStockTaking1(List<StockTaking> listOfStockTaking1) {
        this.listOfStockTaking1 = listOfStockTaking1;
    }

    public List<StockTaking> getListOfStockTaking2() {
        return listOfStockTaking2;
    }

    public void setListOfStockTaking2(List<StockTaking> listOfStockTaking2) {
        this.listOfStockTaking2 = listOfStockTaking2;
    }

    public int getTaking1() {
        return taking1;
    }

    public void setTaking1(int taking1) {
        this.taking1 = taking1;
    }

    public int getTaking2() {
        return taking2;
    }

    public void setTaking2(int taking2) {
        this.taking2 = taking2;
    }

    public List<MovementReportBetweenWarehouseTakings> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<MovementReportBetweenWarehouseTakings> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public HashMap<Integer, BigDecimal> getStockTotals() {
        return stockTotals;
    }

    public void setStockTotals(HashMap<Integer, BigDecimal> stockTotals) {
        this.stockTotals = stockTotals;
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

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    @Override
    @PostConstruct
    public void init() {
        selectedObject = new MovementReportBetweenWarehouseTakings();
        listOfStockTaking1 = new ArrayList<>();
        listOfStockTaking2 = new ArrayList<>();
        listOfStockTaking1 = stockTakingService.findAll("AND ist.status_id = 16");
        listOfTotals=new ArrayList<>();
        stockTotals=new HashMap<>();
        listOfStock=new ArrayList<>();
        listOfCategorization=new ArrayList<>();
        differentType=0;
        // listOfStockTaking2 = stockTakingService.findAll("");
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
    }

    @Override
    public void find() {
        isFind = true;
        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);
        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMovementReportBetweenWarehouseTakingsDatatable:dtbMovementReportBetweenWarehouseTakings");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        if (selectedObject.getStockTaking2().getId() <= 0) {
            selectedObject.getStockTaking2().setEndDate(new Date());
        }
        createWhere = movementReportBetweenWarehouseTakingsService.createWhere(selectedObject);
        listOfObjects = findall(createWhere);

    }

    @Override
    public LazyDataModel<MovementReportBetweenWarehouseTakings> findall(String where) {
        return new CentrowizLazyDataModel<MovementReportBetweenWarehouseTakings>() {

            @Override
            public List<MovementReportBetweenWarehouseTakings> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<MovementReportBetweenWarehouseTakings> result = movementReportBetweenWarehouseTakingsService.findAll(first, pageSize, sortField, sortField, filters, where, selectedObject);
                listOfTotals = movementReportBetweenWarehouseTakingsService.totals(where, selectedObject);
                int count = 0;
                for (MovementReportBetweenWarehouseTakings total : listOfTotals) {
                    count = count + total.getStock().getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void changeStockTaking1() {
        for (StockTaking s : listOfStockTaking1) {
            if (s.getId() == selectedObject.getStockTaking1().getId()) {
                selectedObject.getStockTaking1().setBeginDate(s.getBeginDate());
                selectedObject.getStockTaking1().setEndDate(s.getEndDate());
                selectedObject.getStockTaking1().setName(s.getName());
                selectedObject.getStockTaking1().getWarehouse().setId(s.getWarehouse().getId());

            }
        }
        listOfStockTaking2 = movementReportBetweenWarehouseTakingsService.listOfTaking(selectedObject.getStockTaking1());
        RequestContext.getCurrentInstance().update("frmMovementReportBetweenWarehouseTakings:slcWarehouseTaking2");
    }

    public void changeStockTaking2() {
        for (StockTaking s : listOfStockTaking2) {
            if (s.getId() == selectedObject.getStockTaking2().getId()) {
                selectedObject.getStockTaking2().setBeginDate(s.getBeginDate());
                selectedObject.getStockTaking2().setEndDate(s.getEndDate());
                selectedObject.getStockTaking2().setName(s.getName());
                selectedObject.getStockTaking2().getWarehouse().setId(s.getWarehouse().getId());
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
            RequestContext.getCurrentInstance().update("frmMovementReportBetweenWarehouseTakings:txtStock");
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

            RequestContext.getCurrentInstance().update("frmMovementReportBetweenWarehouseTakings:txtCategory");
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

        for (MovementReportBetweenWarehouseTakings u : listOfTotals) {
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


    public void createPdf() {
        System.out.println("PDF");
        movementReportBetweenWarehouseTakingsService.exportPdf("", selectedObject, toogleList,calculateTotals(1),calculateTotals(2));
    }

    public void createExcel() throws IOException {
        System.out.println("Excel");
        movementReportBetweenWarehouseTakingsService.exportExcel("", selectedObject, toogleList,calculateTotals(1),calculateTotals(2));

    }

    public void createPrinter() {
        System.out.println("Print");

        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(movementReportBetweenWarehouseTakingsService.exportPrinter("", selectedObject, toogleList,calculateTotals(1),calculateTotals(2))) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void bringWarehouse() {
        /*  for (Warehouse w : listOfWarehouse) {
            if (w.getId() == selectedObject.getWarehouse().getId()) {
                selectedObject.getWarehouse().setName(w.getName());
                break;
            }
        }*/
    }

    public int columnClass(BigDecimal result) {
        int i=0;

        System.out.println("---result----"+result);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            i=1;
        } else if (result.compareTo(BigDecimal.ZERO) > 0) {
            i=-1;
        }

        return i;
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
