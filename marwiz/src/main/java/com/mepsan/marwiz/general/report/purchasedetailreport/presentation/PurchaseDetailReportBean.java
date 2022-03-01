package com.mepsan.marwiz.general.report.purchasedetailreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.purchasedetailreport.business.IPurchaseDetailReportService;
import com.mepsan.marwiz.general.report.purchasedetailreport.dao.PurchaseDetailReport;
import com.mepsan.marwiz.general.report.purchasesummaryreport.dao.PurchaseSummaryReport;
import com.mepsan.marwiz.general.report.stationsalessummaryreport.dao.StationSalesSummaryReport;
import com.mepsan.marwiz.general.report.totalgiroreport.dao.TotalGiroReport;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class PurchaseDetailReportBean extends GeneralReportBean<PurchaseDetailReport> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{purchaseDetailReportService}")
    public IPurchaseDetailReportService purchaseDetailReportService;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    private List<Stock> listOfStock;
    private List<Categorization> listOfCategorization;
    private boolean isFind;
    private String createWhere;
    private boolean isCentralSupplier;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private Boolean isThereListBranch;
    private int branchId;
    private boolean isCentralSupplierIconView;

    private List<PurchaseDetailReport> listOfTotals;
    private Map<Integer, BigDecimal> totalPurchaseQuantity;
    private Map<Integer, PurchaseDetailReport> totalMoney;
    private String subTotalPurchaseQuantity;
    private List<Unit> unitList;
    private String subTotalMoney;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setPurchaseDetailReportService(IPurchaseDetailReportService purchaseDetailReportService) {
        this.purchaseDetailReportService = purchaseDetailReportService;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    public Boolean getIsThereListBranch() {
        return isThereListBranch;
    }

    public void setIsThereListBranch(Boolean isThereListBranch) {
        this.isThereListBranch = isThereListBranch;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    public List<PurchaseDetailReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<PurchaseDetailReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public Map<Integer, BigDecimal> getTotalPurchaseQuantity() {
        return totalPurchaseQuantity;
    }

    public void setTotalPurchaseQuantity(Map<Integer, BigDecimal> totalPurchaseQuantity) {
        this.totalPurchaseQuantity = totalPurchaseQuantity;
    }

    public Map<Integer, PurchaseDetailReport> getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Map<Integer, PurchaseDetailReport> totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getSubTotalPurchaseQuantity() {
        return subTotalPurchaseQuantity;
    }

    public void setSubTotalPurchaseQuantity(String subTotalPurchaseQuantity) {
        this.subTotalPurchaseQuantity = subTotalPurchaseQuantity;
    }

    public IUnitService getUnitService() {
        return unitService;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public String getSubTotalMoney() {
        return subTotalMoney;
    }

    public void setSubTotalMoney(String subTotalMoney) {
        this.subTotalMoney = subTotalMoney;
    }

    @PostConstruct
    @Override
    public void init() {
        selectedObject = new PurchaseDetailReport();
        listOfStock = new ArrayList<>();
        listOfCategorization = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        
        totalPurchaseQuantity = new HashMap<>();
        totalMoney = new HashMap<>();

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);

        } else {

            toogleList = Arrays.asList(true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true);

        }

        Calendar calendar = GregorianCalendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());

        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(calendar.getTime());

        listOfBranch = branchSettingService.findUserAuthorizeBranch();
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) {
                selectedBranchList.add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranchList.add(branchSetting);
                    break;
                }
            }
        }

        changeBranch();

    }

    public void changeBranch() {
        branchList = "";
        listOfStock.clear();
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        selectedObject.getStockList().clear();
        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfCentralSupplier().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        int countCentralIntegration = 0;

        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                    countCentralIntegration++;
                }
            }
        } else {
            for (BranchSetting branchSetting : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                    countCentralIntegration++;
                }
            }
        }

        if (countCentralIntegration > 0) {
            isCentralSupplierIconView = true;
            toogleList.set(7, true);
        } else {
            isCentralSupplierIconView = false;
            isCentralSupplier = false;
            toogleList.set(7, false);
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

    }

    @Override
    public void find() {
        isFind = true;
        totalMoney.clear();
        totalPurchaseQuantity.clear();
        subTotalPurchaseQuantity = "";

        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);

        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (isCentralSupplier) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        createWhere = purchaseDetailReportService.createWhere(selectedObject, isCentralSupplier, centralSupplierBookCheckboxFilterBean.getSupplierType());

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmPurchaseDetailDatatable:dtbPurchaseDetail");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfObjects = findall(createWhere);
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
    public LazyDataModel<PurchaseDetailReport> findall(String where) {
        return new CentrowizLazyDataModel<PurchaseDetailReport>() {

            @Override
            public List<PurchaseDetailReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<PurchaseDetailReport> result = purchaseDetailReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList);
                listOfTotals = purchaseDetailReportService.totals(createWhere, branchList);

                int count = 0;

                for (PurchaseDetailReport total : listOfTotals) {
                    count += total.getId();
                }
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                calculateTotal();
                return result;
            }
        };
    }

    public void calculateTotal() {

        totalPurchaseQuantity.clear();
        totalMoney.clear();
        subTotalPurchaseQuantity = "";
        subTotalMoney = "";

        findUnit();

        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        for (PurchaseDetailReport total1 : listOfTotals) {

            if (totalMoney.containsKey(total1.getCurrency().getId())) {

                PurchaseDetailReport old = new PurchaseDetailReport();
                old.setCurrency(totalMoney.get(total1.getCurrency().getId()).getCurrency());
                old.setTotalMoney(totalMoney.get(total1.getCurrency().getId()).getTotalMoney());

                old.setTotalMoney(old.getTotalMoney().add(total1.getTotalMoney()));
                totalMoney.put(total1.getCurrency().getId(), old);

            } else {

                PurchaseDetailReport oldNew = new PurchaseDetailReport();

                oldNew.setCurrency(total1.getCurrency());
                oldNew.setTotalMoney(total1.getTotalMoney());

                totalMoney.put(total1.getCurrency().getId(), oldNew);
            }

           
        }

        int count2 = 0;
        for (Map.Entry<Integer, PurchaseDetailReport> entry : totalMoney.entrySet()) {
            Integer key = entry.getKey();
            PurchaseDetailReport value = entry.getValue();

            if (count2 == 1) {
                subTotalMoney += " + " + String.valueOf(formatter.format(entry.getValue().getTotalMoney())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
            } else {
                count2 = 1;
                subTotalMoney += " " + String.valueOf(formatter.format(entry.getValue().getTotalMoney())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
            }

        }


        for (PurchaseDetailReport total : listOfTotals) {

            if (totalPurchaseQuantity.containsKey(total.getStock().getUnit().getId())) {
                BigDecimal old = totalPurchaseQuantity.get(total.getStock().getUnit().getId());
                totalPurchaseQuantity.put(total.getStock().getUnit().getId(), old.add(total.getQuantity()));
            } else {
                totalPurchaseQuantity.put(total.getStock().getUnit().getId(), total.getQuantity());
            }

        }

        int count = 0;
        for (Map.Entry<Integer, BigDecimal> entry : totalPurchaseQuantity.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp != 0) {
                if (count == 0) {
                    count = 1;

                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            break;
                        }
                    }
                    subTotalPurchaseQuantity += String.valueOf(formatter.format(entry.getValue())) + " " + unit.getSortName();

                } else if (count == 1) {

                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            break;
                        }
                    }
                    subTotalPurchaseQuantity += " + " + String.valueOf(formatter.format(entry.getValue())) + " " + unit.getSortName();

                }
            }
        }

        if (subTotalPurchaseQuantity.isEmpty() || subTotalPurchaseQuantity.equals("")) {
            subTotalPurchaseQuantity = "0";
        }
        
        for (Map.Entry<Integer, BigDecimal> entry : totalPurchaseQuantity.entrySet()) {
            Integer key = entry.getKey();
            BigDecimal value = entry.getValue();
            
        }
        
    }

    public void findUnit() {
        unitList = new ArrayList<>();
        unitList = unitService.listOfUnitAllBranches();
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
            RequestContext.getCurrentInstance().update("frmPurchaseDetail:txtStock");
        } else if (event.getComponent().getParent().getParent().getId().equals("frmCentralSupplierBookFilterCheckbox")) {
            listOfCentralSupplier.clear();
            if (centralSupplierBookCheckboxFilterBean.isAll) {
                CentralSupplier s = new CentralSupplier(0);
                if (!centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    CentralSupplier centralSupplier = new CentralSupplier(0);
                    centralSupplier.setName(sessionBean.loc.getString("all"));
                    centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().add(0, centralSupplier);
                }
            } else if (!centralSupplierBookCheckboxFilterBean.isAll) {
                if (!centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().remove(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfCentralSupplier.addAll(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList());

            if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("centralsupplier") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmPurchaseDetail:txtCentralSupplier");
        } else if (event.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {
            listOfAccount.clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account s = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Account account = new Account(0);
                    account.setName(sessionBean.loc.getString("all"));
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, account);
                }
            } else if (!accountBookCheckboxFilterBean.isAll) {
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfAccount.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("supplier") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmPurchaseDetail:txtSupplier");
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

            RequestContext.getCurrentInstance().update("frmPurchaseDetail:txtCategory");
        }

    }

    public void openDialog(int type) {
        if (type == 1) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {

                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
        } else if (type == 2) {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }
            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);

        } else if (type == 3) {
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfCentralSupplier.isEmpty()) {
                if (listOfCentralSupplier.get(0).getId() == 0) {
                    centralSupplierBookCheckboxFilterBean.isAll = true;
                } else {
                    centralSupplierBookCheckboxFilterBean.isAll = false;
                }
            }
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfCentralSupplier);
        } else if (type == 4) {
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfAccount.isEmpty()) {
                if (listOfAccount.get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfAccount);
        }

    }

    public void createPdf() {

        purchaseDetailReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier ,subTotalPurchaseQuantity ,subTotalMoney );
    }

    public void createExcel() {

        purchaseDetailReportService.exportExcel(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier , subTotalPurchaseQuantity ,subTotalMoney);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(purchaseDetailReportService.exportPrinter(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier ,subTotalPurchaseQuantity ,subTotalMoney)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public void centralOrLocalSupplier(int type) {
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        if (type == 0) {
            isCentralSupplier = false;
        } else {
            isCentralSupplier = true;
        }
    }
}
