/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 10:40:11
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.UserBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.salesreceiptreport.dao.SalesReport;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.mepsan.marwiz.general.report.salesreceiptreport.business.ISalesReceiptReportService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;

@ManagedBean
@ViewScoped
public class SalesReceiptReportBean extends GeneralReportBean<SalesReport> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{salesReceiptReportService}")
    public ISalesReceiptReportService salesReceiptReportService;

    @ManagedProperty(value = "#{userBookCheckboxFilterBean}")
    private UserBookCheckboxFilterBean userBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private List<Type> salesTypeList;
    private String createWhere;
    private BigDecimal totalMoney;
    private Currency currency;
    private List<SaleItem> saleItemList;
    private List<SalePayment> listOfTotalSaleType;
    private SalesReport selectedSaleReport;
    private List<UserData> listOfCashier;
    private List<SalesReport> listOfTotals;
    private Shift selectedShift;
    private boolean isSafe;
    private List<BranchSetting> selectedBranchList;
    private List<BranchSetting> listOfBranch;
    private String branchList;

    public List<Type> getSalesTypeList() {
        return salesTypeList;
    }

    public void setSalesTypeList(List<Type> salesTypeList) {
        this.salesTypeList = salesTypeList;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSalesReceiptReportService(ISalesReceiptReportService salesReceiptReportService) {
        this.salesReceiptReportService = salesReceiptReportService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<SaleItem> getSaleItemList() {
        return saleItemList;
    }

    public void setSaleItemList(List<SaleItem> saleItemList) {
        this.saleItemList = saleItemList;
    }

    public List<SalePayment> getListOfTotalSaleType() {
        return listOfTotalSaleType;
    }

    public void setListOfTotalSaleType(List<SalePayment> listOfTotalSaleType) {
        this.listOfTotalSaleType = listOfTotalSaleType;
    }

    public SalesReport getSelectedSaleReport() {
        return selectedSaleReport;
    }

    public void setSelectedSaleReport(SalesReport selectedSaleReport) {
        this.selectedSaleReport = selectedSaleReport;
    }

    public void setUserBookCheckboxFilterBean(UserBookCheckboxFilterBean userBookCheckboxFilterBean) {
        this.userBookCheckboxFilterBean = userBookCheckboxFilterBean;
    }

    public List<SalesReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<SalesReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public boolean isIsSafe() {
        return isSafe;
    }

    public void setIsSafe(boolean isSafe) {
        this.isSafe = isSafe;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------SalesReportBean-------");
        selectedObject = new SalesReport();
        selectedSaleReport = new SalesReport();
        saleItemList = new ArrayList<>();
        listOfTotalSaleType = new ArrayList<>();
        salesTypeList = new ArrayList<>();
        List<Type> tempSaleTypeList = new ArrayList<>();
        tempSaleTypeList = sessionBean.getTypes(15);
        for (Type t : tempSaleTypeList) {
            if (t.getId() != 106) {
                salesTypeList.add(t);
            }
        }
        listOfCashier = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());

        selectedObject.setMinSalesPrice(null);
        selectedObject.setMaxSalesPrice(null);
        setListBtn(new ArrayList<>());

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Shift) {
                    selectedShift = new Shift();
                    selectedShift = (Shift) ((ArrayList) sessionBean.parameter).get(i);
                    selectedObject.setShiftNo(selectedShift.getShiftNo());
                    selectedObject.setBeginDate(selectedShift.getBeginDate());
                    selectedObject.setEndDate(selectedShift.getEndDate());
                    selectedObject.getSaleTypeList().add("17");
                    find();
                    isSafe = true;
                    break;

                }
            }
        }

        selectedBranchList = new ArrayList<>();
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

        selectedObject.getAccount().setId(1);
        selectedObject.getAccount().setIsEmployee(false);
        selectedObject.getAccount().setName(sessionBean.getLoc().getString("retailsalecustomer"));
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);
    }

    @Override
    public void find() {
        isFind = true;

        selectedObject.getListOfCashier().clear();
        selectedObject.getListOfCashier().addAll(listOfCashier);

        createWhere = salesReceiptReportService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSalesReportDatatable:dtbSalesReport");
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
        listOfCashier.clear();
        userBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

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
    public LazyDataModel<SalesReport> findall(String where) {
        return new CentrowizLazyDataModel<SalesReport>() {

            @Override
            public List<SalesReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<SalesReport> result = salesReceiptReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, branchList, selectedObject);
                listOfTotals = salesReceiptReportService.totals(where, branchList, selectedObject);
                int count = 0;
                for (SalesReport total : listOfTotals) {
                    count = count + total.getId();
                }
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
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
            RequestContext.getCurrentInstance().update("frmSalesReport:txtCustomer");
            accountBookFilterBean.setSelectedData(null);
            accountBookFilterBean.isAll = false;
        }

    }

    public void updatePaginator() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSalesReportDatatable:dtbSalesReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
    }

    public void createPdf() {
        salesReceiptReportService.exportPdf(createWhere, selectedObject, toogleList, listOfTotals, branchList, selectedBranchList);
    }

    public void createExcel() throws IOException {
        salesReceiptReportService.exportExcel(createWhere, selectedObject, toogleList, listOfTotals, branchList, selectedBranchList);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(salesReceiptReportService.exportPrinter(createWhere, selectedObject, toogleList, listOfTotals, branchList, selectedBranchList)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void createSalesDetailDialog() {
        saleItemList = salesReceiptReportService.findSaleItem(selectedSaleReport);
        listOfTotalSaleType = salesReceiptReportService.findSalePayment(selectedSaleReport);

        RequestContext.getCurrentInstance().execute("PF('dlg_SalesDetail').show();");
    }

    public void updateAllInformation(ActionEvent event) {
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
        RequestContext.getCurrentInstance().update("frmSalesReport:txtCashier");

    }

    public void openDialog() {
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

    public BigDecimal calculateProductBasedDiscount() {
        BigDecimal total = BigDecimal.valueOf(0);
        if (selectedSaleReport.getTotalDiscount() != null && selectedSaleReport.getDiscountPrice() != null) {
            total = selectedSaleReport.getTotalDiscount().subtract(selectedSaleReport.getDiscountPrice());
        }

        return total;
    }

    public int calculateCardOperationCount() {
        int operationCount = 0;
        for (SalesReport s : listOfTotals) {
            operationCount = operationCount + s.getCardOperationCount();
        }
        return operationCount;
    }
}
