/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 01.10.2018 18:05:35
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.purchasesalesreport.business.IPurchaseSalesReportService;
import com.mepsan.marwiz.general.report.purchasesalesreport.dao.PurchaseSalesReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class PurchaseSalesReportBean extends GeneralReportBean<PurchaseSalesReport> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{purchaseSalesReportService}")
    private IPurchaseSalesReportService purchaseSalesReportService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    private AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    private String createWhereBranch;
    private String createWhere;
    private List<Categorization> listOfCategorization;
    private List<PurchaseSalesReport> listOfSubTotal;

    private List<PurchaseSalesReport> listOfStockDetail;
    private PurchaseSalesReport selectedPurchaseSales;
    private String categoryName;
    private List<Stock> listOfStock;
    private boolean isPurchase;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private int branchId;
    private boolean isThereListBranch;
    private int centralIngetrationInf;
    private PurchaseSalesReport purchaseSalesReport;
    private List<PurchaseSalesReport> listOfPurchaseSaleReports;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private boolean isCentralSupplier;
    private boolean isCentralSupplierIconView;

    public int getCentralIngetrationInf() {
        return centralIngetrationInf;
    }

    public void setCentralIngetrationInf(int centralIngetrationInf) {
        this.centralIngetrationInf = centralIngetrationInf;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setPurchaseSalesReportService(IPurchaseSalesReportService purchaseSalesReportService) {
        this.purchaseSalesReportService = purchaseSalesReportService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
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

    public String getCreateWhereBranch() {
        return createWhereBranch;
    }

    public void setCreateWhereBranch(String createWhereBranch) {
        this.createWhereBranch = createWhereBranch;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public boolean isIsThereListBranch() {
        return isThereListBranch;
    }

    public void setIsThereListBranch(boolean isThereListBranch) {
        this.isThereListBranch = isThereListBranch;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<PurchaseSalesReport> getListOfSubTotal() {
        return listOfSubTotal;
    }

    public void setListOfSubTotal(List<PurchaseSalesReport> listOfSubTotal) {
        this.listOfSubTotal = listOfSubTotal;
    }

    public PurchaseSalesReport getSelectedPurchaseSales() {
        return selectedPurchaseSales;
    }

    public void setSelectedPurchaseSales(PurchaseSalesReport selectedPurchaseSales) {
        this.selectedPurchaseSales = selectedPurchaseSales;
    }

    public List<PurchaseSalesReport> getListOfStockDetail() {
        return listOfStockDetail;
    }

    public void setListOfStockDetail(List<PurchaseSalesReport> listOfStockDetail) {
        this.listOfStockDetail = listOfStockDetail;
    }

    public PurchaseSalesReport getPurchaseSalesReport() {
        return purchaseSalesReport;
    }

    public void setPurchaseSalesReport(PurchaseSalesReport purchaseSalesReport) {
        this.purchaseSalesReport = purchaseSalesReport;
    }

    public List<PurchaseSalesReport> getListOfPurchaseSaleReports() {
        return listOfPurchaseSaleReports;
    }

    public void setListOfPurchaseSaleReports(List<PurchaseSalesReport> listOfPurchaseSaleReports) {
        this.listOfPurchaseSaleReports = listOfPurchaseSaleReports;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--------PurchaseSalesReportBean-----------");
        selectedObject = new PurchaseSalesReport();
        listOfCategorization = new ArrayList<>();
        listOfStock = new ArrayList<>();
        listOfSubTotal = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        listOfPurchaseSaleReports = new ArrayList<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch(); // kullanıcının yetkili olduğu branch listesini çeker
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
        }
        selectedObject.setCostType(1);
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

        isThereListBranch = false;

        Calendar cal = GregorianCalendar.getInstance();
        selectedObject.setEndDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);

        changePurchase();

    }

    @Override
    public void find() {
        isFind = true;
        listOfSubTotal.clear();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(selectedObject.getEndDate());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        selectedObject.getStockList().clear();

        cal.setTime(selectedObject.getBeginDate());
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);

        for (TaxGroup taxGroup : selectedObject.getTaxGroupList()) {
            if (selectedObject.getStocktaxgroup() != null) {
                if (selectedObject.getStocktaxgroup().getId() > 0) {
                    if (selectedObject.getStocktaxgroup().getId() == taxGroup.getId()) {
                        selectedObject.getStocktaxgroup().setRate(taxGroup.getRate());
                        selectedObject.getStocktaxgroup().setName(taxGroup.getName());
                    }
                }
            }
        }

        selectedObject.setBeginDate(cal.getTime());
        selectedObject.setIsPurchase(isPurchase);
        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);
        selectedObject.getStockList().addAll(listOfStock);

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (isCentralSupplier) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        createWhere = purchaseSalesReportService.createWhere(selectedObject, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, centralSupplierBookCheckboxFilterBean.getSupplierType(), isCentralSupplier);

        boolean isThere = false;
        int count = 0;
        for (BranchSetting branchSetting : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {

            isThere = false;
            if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                isThere = true;
            }
            if (isThere) {
                count++;
            }
        }
        centralIngetrationInf = -1;
        if (selectedBranchList.size() > 1) {
            if (count >= 1 && count < selectedBranchList.size()) {
                centralIngetrationInf = -1;
            } else if (count == selectedBranchList.size() && isThere) {
                centralIngetrationInf = 1;
            } else if (count == 0 && !isThere) {
                centralIngetrationInf = 0;
            }
        } else if (selectedBranchList.size() == 1) {
            if (selectedBranchList.get(0).isIsCentralIntegration()) {
                centralIngetrationInf = 1;
            } else if (!selectedBranchList.get(0).isIsCentralIntegration()) {
                centralIngetrationInf = 0;
            }
        }
        if (selectedObject.isIsPurchase()) {
            if (count > 0) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                setCountToggle(0);
            } else {
                toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                setCountToggle(1);
            }
        } else {
            if (count > 0) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true);
                setCountToggle(1);
            } else {
                toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true);
                setCountToggle(2);
            }
        }
        listOfSubTotal.clear();
        listOfObjects = findall(createWhere);

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        RequestContext.getCurrentInstance().update("frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport");

    }

    public void changePurchase() {

        selectedObject.setIsPurchase(isPurchase);
        if (selectedObject.isIsPurchase() == true) { // category kitabı için şube bilgisini göndermek için kullanılır.
            branchId = sessionBean.getUser().getLastBranch().getId();
            selectedObject.setTaxGroupList(purchaseSalesReportService.listOfTaxGroup(10, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList));

        } else {
            if (selectedBranchList.size() == 1) {
                branchId = selectedBranchList.get(0).getBranch().getId();
                selectedObject.setBranchSetting(selectedBranchList.get(0));
                isThereListBranch = false;

            } else {
                isThereListBranch = true;
                branchId = -1;
            }

        }

    }

    /**
     * Bu metot branch değiştiği anda string olarak branch id leri birleştirerek
     * where şartı oluşturur.
     */
    public void changeBranch() {
        branchList = "";
        if (selectedBranchList.size() == 1) { // category kitabı için şube bilgisini göndermek için kullanılır.
            branchId = selectedBranchList.get(0).getBranch().getId();
            selectedObject.setBranchSetting(selectedBranchList.get(0));
            isThereListBranch = false;

            selectedObject.getStocktaxgroup().setId(0);
            selectedObject.getTaxGroupList().clear();
            selectedObject.getStockList().clear();
            listOfStock.clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            selectedObject.setAccount(new Account());
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

            if (selectedBranchList.get(0).isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
                isCentralSupplierIconView = true;
            } else {
                isCentralSupplierIconView = false;
                isCentralSupplier = false;
            }

        } else {
            isThereListBranch = true;
            branchId = -1;
            selectedObject.getStocktaxgroup().setId(0);
            selectedObject.getTaxGroupList().clear();
            selectedObject.getStockList().clear();
            listOfStock.clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            selectedObject.setAccount(new Account());
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
            } else {
                isCentralSupplierIconView = false;
                isCentralSupplier = false;
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
        selectedObject.setTaxGroupList(purchaseSalesReportService.listOfTaxGroup(10, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList));

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
            RequestContext.getCurrentInstance().update("frmPurchaseSalesReport:txtStock");
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
            RequestContext.getCurrentInstance().update("frmPurchaseSalesReport:txtCentralSupplier");
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
            RequestContext.getCurrentInstance().update("frmPurchaseSalesReport:txtSupplier");
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

            RequestContext.getCurrentInstance().update("frmPurchaseSalesReport:txtCategory");
        }
    }

    /*
    Cari Kitabı Bu Fonksiyonu Çağırır, eğer hepsi seçili olursa bir daha seçim yaptırılmaz.Hepsi tekrar kitap açıldığında seçili getirilmez.
     */
    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account account = new Account(0);
                account.setName(sessionBean.loc.getString("all"));
                selectedObject.setAccount(account);
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            }
            RequestContext.getCurrentInstance().update("frmPurchaseSalesReport:txtCustomer");
            accountBookFilterBean.setSelectedData(null);
            accountBookFilterBean.isAll = false;
        }

        RequestContext.getCurrentInstance().update("frmPurchaseSalesReport:txtCustomer");

    }

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
        } else if (type == 2) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
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

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void showDetail() {
        listOfStockDetail = new ArrayList<>();
        selectedPurchaseSales.setEndDate(selectedObject.getEndDate());
        selectedPurchaseSales.setBeginDate(selectedObject.getBeginDate());
        selectedPurchaseSales.setIsPurchase(selectedObject.isIsPurchase());
        listOfStockDetail = purchaseSalesReportService.stockDetail(createWhere, selectedPurchaseSales, branchList);
        RequestContext.getCurrentInstance().execute("PF('dlg_PurchaseSalesProc').show();");
    }

    public void createPdf() {
        purchaseSalesReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedBranchList, centralIngetrationInf, listOfPurchaseSaleReports, isCentralSupplier);
    }

    public void createExcel() throws IOException {

        purchaseSalesReportService.exportExcel(createWhere, selectedObject, toogleList, branchList, selectedBranchList, centralIngetrationInf, isCentralSupplier);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(purchaseSalesReportService.exportPrinter(createWhere, selectedObject, toogleList, branchList, selectedBranchList, centralIngetrationInf, isCentralSupplier)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public BigDecimal calculateProfit(PurchaseSalesReport obj, int type) {

        BigDecimal profit = BigDecimal.valueOf(0);
        BigDecimal totalmoney = BigDecimal.valueOf(0);

        if (isPurchase) {
            totalmoney = obj.getSalesTotalMoney();
        } else {
            totalmoney = obj.getTotalMoney();
        }

        if (type == 1) {

            if (totalmoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                profit = ((totalmoney.subtract(obj.getPurchaseCost())).divide(totalmoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            }

        } else if (type == 2) {
            if (obj.getPurchaseCost().compareTo(BigDecimal.valueOf(0)) != 0) {
                profit = ((totalmoney.subtract(obj.getPurchaseCost())).divide(obj.getPurchaseCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            }
        }

        return profit;

    }

    @Override
    public LazyDataModel<PurchaseSalesReport> findall(String where) {
        return new CentrowizLazyDataModel<PurchaseSalesReport>() {

            @Override
            public List<PurchaseSalesReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<PurchaseSalesReport> result = purchaseSalesReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, branchList, centralIngetrationInf, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
                int count = 0;
                if (!result.isEmpty()) {
                    listOfSubTotal = purchaseSalesReportService.count(result.get(0), selectedObject);
                    for (PurchaseSalesReport purchaseSalesReport : listOfSubTotal) {
                        count += purchaseSalesReport.getStock().getId();
                    }
                }

                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                RequestContext.getCurrentInstance().update("frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport pgrPurchaseSalesReportDatatable frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport:alvs");

                return result;
            }
        };
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
}
