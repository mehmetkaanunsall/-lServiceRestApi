/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 11:48:47
 */
package com.mepsan.marwiz.general.report.salesdetailreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.UserBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.purchasedetailreport.dao.PurchaseDetailReport;
import com.mepsan.marwiz.general.report.salesdetailreport.business.ISalesDetailReportService;
import com.mepsan.marwiz.general.report.salesdetailreport.dao.SalesDetailReport;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
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

@ManagedBean
@ViewScoped
public class SalesDetailReportBean extends GeneralReportBean<SalesDetailReport> {

    @ManagedProperty(value = "#{salesDetailReportService}")
    public ISalesDetailReportService salesDetailReportService;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{userBookCheckboxFilterBean}")
    private UserBookCheckboxFilterBean userBookCheckboxFilterBean;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    private String createWhere;
    private List<Stock> listOfStock;
    private List<Categorization> listOfCategorization;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private Boolean isThereListBranch;
    private int branchId;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private boolean isCentralSupplier;
    private boolean isCentralSupplierIconView;
    private List<UserData> listOfCashier;

    private List<SalesDetailReport> listOfTotal;
    private Map<Integer, BigDecimal> totalSalesQuantity;
    private Map<Integer, SalesDetailReport> totalMoney;
    private String subTotalSalesQuantity;
    private String subTotalMoney;
    private List<Unit> unitList;

    public Boolean getIsThereListBranch() {
        return isThereListBranch;
    }

    public void setIsThereListBranch(Boolean isThereListBranch) {
        this.isThereListBranch = isThereListBranch;
    }

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
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

    public void setSalesDetailReportService(ISalesDetailReportService salesDetailReportService) {
        this.salesDetailReportService = salesDetailReportService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
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

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
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

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setUserBookCheckboxFilterBean(UserBookCheckboxFilterBean userBookCheckboxFilterBean) {
        this.userBookCheckboxFilterBean = userBookCheckboxFilterBean;
    }

    public List<SalesDetailReport> getListOfTotal() {
        return listOfTotal;
    }

    public void setListOfTotal(List<SalesDetailReport> listOfTotal) {
        this.listOfTotal = listOfTotal;
    }

    public Map<Integer, BigDecimal> getTotalSalesQuantity() {
        return totalSalesQuantity;
    }

    public void setTotalSalesQuantity(Map<Integer, BigDecimal> totalSalesQuantity) {
        this.totalSalesQuantity = totalSalesQuantity;
    }

    public Map<Integer, SalesDetailReport> getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Map<Integer, SalesDetailReport> totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getSubTotalSalesQuantity() {
        return subTotalSalesQuantity;
    }

    public void setSubTotalSalesQuantity(String subTotalSalesQuantity) {
        this.subTotalSalesQuantity = subTotalSalesQuantity;
    }

    public String getSubTotalMoney() {
        return subTotalMoney;
    }

    public void setSubTotalMoney(String subTotalMoney) {
        this.subTotalMoney = subTotalMoney;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public IUnitService getUnitService() {
        return unitService;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------SalesDetailReportBean");

        listOfStock = new ArrayList<>();
        selectedObject = new SalesDetailReport();
        listOfCategorization = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfCashier = new ArrayList<>();
        isCentralSupplierIconView = sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration();
        
        totalSalesQuantity = new HashMap<>();
        totalMoney = new HashMap<>();

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true , true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true);
        }

        for (Boolean boolean1 : toogleList) {
            System.out.println("toogle*************"+boolean1);
        }
        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker
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

        selectedObject.getAccount().setId(1);
        selectedObject.getAccount().setIsEmployee(false);
        selectedObject.getAccount().setName(sessionBean.getLoc().getString("retailsalecustomer"));

        isThereListBranch = false;

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        setListBtn(new ArrayList<>());

    }

    @Override
    public void find() {

        isFind = true;

        selectedObject.getListOfCashier().clear();
        selectedObject.getListOfCashier().addAll(listOfCashier);

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

        createWhere = salesDetailReportService.createWhere(selectedObject, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, isCentralSupplier, centralSupplierBookCheckboxFilterBean.getSupplierType());
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSalesDetailDatatable:dtbSalesDetail");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(createWhere);
    }

    /**
     * Bu metot branch değiştiği anda string olara branch id leri birleştirerek
     * where şartı oluşturur.
     */
    public void changeBranch() {
        branchList = "";
        if (selectedBranchList.size() == 1) { // category kitabı için şube bilgisini göndermek için kullanılır.
            branchId = selectedBranchList.get(0).getBranch().getId();
            selectedObject.setBranchSetting(selectedBranchList.get(0));
            isThereListBranch = false;

            listOfStock.clear();
            selectedObject.getStockList().clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

            if (selectedBranchList.get(0).isIsCentralIntegration() && selectedBranchList.get(0).getBranch().getConceptType() == 1) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true , true);
                isCentralSupplierIconView = true;
            } else {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true , true);

                isCentralSupplierIconView = false;
                isCentralSupplier = false;
            }
        } else {
            isThereListBranch = true;
            branchId = -1;
            listOfStock.clear();
            selectedObject.getStockList().clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            int countCentralIntegration = 0;
            for (BranchSetting branchSetting : selectedBranchList) {
                if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                    countCentralIntegration++;
                }
            }
            if (countCentralIntegration > 0 || selectedBranchList.size() == 0) {
                isCentralSupplierIconView = true;
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,true ,true);

            } else {
                isCentralSupplierIconView = false;
                isCentralSupplier = false;
                toogleList = Arrays.asList(true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true,true ,true);

            }

        }
        for (BranchSetting branchSetting : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }
        selectedObject.getAccount().setName("");
        selectedObject.setAccount(new Account());

    }

    @Override
    public LazyDataModel<SalesDetailReport> findall(String where) {
        return new CentrowizLazyDataModel<SalesDetailReport>() {
            @Override
            public List<SalesDetailReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<SalesDetailReport> result = salesDetailReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList, selectedObject);

                listOfTotal = salesDetailReportService.totals(createWhere, branchList, selectedObject);

                int count = 0;

                for (SalesDetailReport total : listOfTotal) {
                    count += total.getId();
                }

                listOfObjects.setRowCount(count);
                calculateTotal();

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void calculateTotal() {

        totalSalesQuantity.clear();
        totalMoney.clear();
        subTotalSalesQuantity = "";
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
        
        for (SalesDetailReport total1 : listOfTotal) {

            if (totalMoney.containsKey(total1.getCurrency().getId())) {

                SalesDetailReport old = new SalesDetailReport();
                old.setCurrency(totalMoney.get(total1.getCurrency().getId()).getCurrency());
                old.setTotalMoney(totalMoney.get(total1.getCurrency().getId()).getTotalMoney());

                old.setTotalMoney(old.getTotalMoney().add(total1.getTotalMoney()));
                totalMoney.put(total1.getCurrency().getId(), old);

            } else {

                SalesDetailReport oldNew = new SalesDetailReport();

                oldNew.setCurrency(total1.getCurrency());
                oldNew.setTotalMoney(total1.getTotalMoney());

                totalMoney.put(total1.getCurrency().getId(), oldNew);
            }

           
        }

       
        int count2 = 0; 
        for (Map.Entry<Integer, SalesDetailReport> entry : totalMoney.entrySet()) {
            Integer key = entry.getKey();
            SalesDetailReport value = entry.getValue();

            if (count2 == 1) {
                subTotalMoney += " + " + String.valueOf(formatter.format(entry.getValue().getTotalMoney())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
            } else {
                count2 = 1;
                subTotalMoney += " " + String.valueOf(formatter.format(entry.getValue().getTotalMoney())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
            }

        }

        for (SalesDetailReport total : listOfTotal) {

            if (totalSalesQuantity.containsKey(total.getStock().getUnit().getId())) {
                BigDecimal old = totalSalesQuantity.get(total.getStock().getUnit().getId());
                totalSalesQuantity.put(total.getStock().getUnit().getId(), old.add(total.getQuantity()));
            } else {
                totalSalesQuantity.put(total.getStock().getUnit().getId(), total.getQuantity());
            }

        }

        int count = 0;
        for (Map.Entry<Integer, BigDecimal> entry : totalSalesQuantity.entrySet()) {
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
                    subTotalSalesQuantity += String.valueOf(formatter.format(entry.getValue())) + " " + unit.getSortName();

                } else if (count == 1) {

                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            break;
                        }
                    }
                    subTotalSalesQuantity += " + " + String.valueOf(formatter.format(entry.getValue())) + " " + unit.getSortName();                    

                }
            }
        }

        if (subTotalSalesQuantity.isEmpty() || subTotalSalesQuantity.equals("")) {
            subTotalSalesQuantity = "0";
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
            RequestContext.getCurrentInstance().update("frmSalesDetail:txtStock");
            
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
            RequestContext.getCurrentInstance().update("frmSalesDetail:txtCentralSupplier");
            
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
            RequestContext.getCurrentInstance().update("frmSalesDetail:txtSupplier");
            
        } else if (event.getComponent().getParent().getParent().getParent().getId().equals("frmUserBookFilterCheckbox")) {
            listOfCashier.clear();
            if (userBookCheckboxFilterBean.isAll) {
                UserData s = new UserData(0);
                s.setName(sessionBean.loc.getString("all"));
                if (!userBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    userBookCheckboxFilterBean.getTempSelectedDataList().add(0, s);
                }
            } else if (!userBookCheckboxFilterBean.isAll) {
                if (!userBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (userBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        userBookCheckboxFilterBean.getTempSelectedDataList().remove(userBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfCashier.addAll(userBookCheckboxFilterBean.getTempSelectedDataList());
            if (userBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                userBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (userBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                userBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                userBookCheckboxFilterBean.setSelectedCount(userBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("user") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmSalesDetail:txtCashier");
            
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

            RequestContext.getCurrentInstance().update("frmSalesDetail:txtCategory");
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
        } else if (type == 5) {
            userBookCheckboxFilterBean.getTempSelectedDataList().clear();
            userBookCheckboxFilterBean.getSelectedDataList().clear();
            if (!listOfCashier.isEmpty()) {
                if (listOfCashier.get(0).getId() == 0) {
                    userBookCheckboxFilterBean.isAll = true;
                } else {
                    userBookCheckboxFilterBean.isAll = false;
                }
            }

            userBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfCashier);
            userBookCheckboxFilterBean.getSelectedDataList().addAll(listOfCashier);
        }
    }

    public void createPdf() {

        salesDetailReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier , subTotalSalesQuantity ,subTotalMoney );
    }

    public void createExcel() throws IOException {
        salesDetailReportService.exportExcel(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier ,subTotalSalesQuantity ,subTotalMoney );
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(salesDetailReportService.exportPrinter(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier , subTotalSalesQuantity ,subTotalMoney )) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void centralOrLocalSupplier(int type) {
        
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        
        if (type == 0) {
            isCentralSupplier = false;
            selectedObject.getListOfCentralSupplier().clear();
        } else {
            isCentralSupplier = true;
        }
    }

    public void updateAllInformation() {

        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account account = new Account(0);
                account.setIsPerson(false);
                account.setName(sessionBean.loc.getString("all"));
                selectedObject.setAccount(account);
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            }
            RequestContext.getCurrentInstance().update("frmSalesDetail:txtCustomer");
            accountBookFilterBean.setSelectedData(null);
            accountBookFilterBean.isAll = false;
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

}
