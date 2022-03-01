/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.duepaymentsreport.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.InvoiceReport;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.duepaymentsreport.business.IDuePaymentsReportService;
import com.mepsan.marwiz.general.report.duepaymentsreport.dao.DuePaymentsReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author ebubekir.buker
 */
@ManagedBean
@ViewScoped
public class DuePaymentsReportBean extends GeneralReportBean<DuePaymentsReport> {

    @ManagedProperty(value = "#{sessionBean}")
    SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{duePaymentsReportService}")
    private IDuePaymentsReportService duePaymentsReportService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    private List<BranchSetting> listOfBranch;
    private String branchList;
    private int branchId;
    private boolean isThereListBranch;
    private InvoiceReport searchObject;
    String createWhere;

    private List<DuePaymentsReport> listOfTotal;
    private String totalRemainingMoney;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Marwiz getMarwiz() {
        return marwiz;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public IDuePaymentsReportService getDuePaymentsReportService() {
        return duePaymentsReportService;
    }

    public void setDuePaymentsReportService(IDuePaymentsReportService duePaymentsReportService) {
        this.duePaymentsReportService = duePaymentsReportService;
    }

    public AccountBookCheckboxFilterBean getAccountBookCheckboxFilterBean() {
        return accountBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public IInvoiceService getInvoiceService() {
        return invoiceService;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
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

    public InvoiceReport getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(InvoiceReport searchObject) {
        this.searchObject = searchObject;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public List<DuePaymentsReport> getListOfTotal() {
        return listOfTotal;
    }

    public void setListOfTotal(List<DuePaymentsReport> listOfTotal) {
        this.listOfTotal = listOfTotal;
    }

    public String getTotalRemainingMoney() {
        return totalRemainingMoney;
    }

    public void setTotalRemainingMoney(String totalRemainingMoney) {
        this.totalRemainingMoney = totalRemainingMoney;
    }

    @PostConstruct
    @Override
    public void init() {

        System.out.println("<-------DuePaymentsReportBean------->");
        listOfBranch = new ArrayList<>();
        listOfTotal = new ArrayList<>();
        selectedObject = new DuePaymentsReport();
        selectedObject.setSelectedBranchList(new ArrayList<>());
        selectedObject.setListOfAccount(new ArrayList<>());
        selectedObject.setInvoiceType(2);
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof DuePaymentsReport) {
                    selectedObject = ((DuePaymentsReport) ((ArrayList) sessionBean.parameter).get(i));
                }
            }
        }

        if (selectedObject.getId() > 0) {

            find();
        } else {
            listOfBranch = branchSettingService.findUserAuthorizeBranch();
            if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                for (BranchSetting branchSetting : listOfBranch) {
                    selectedObject.getSelectedBranchList().add(branchSetting);
                }
            } else {
                for (BranchSetting branchSetting : listOfBranch) {
                    if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                        selectedObject.getSelectedBranchList().add(branchSetting);
                        break;
                    }
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 01);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            selectedObject.setBeginDate(calendar.getTime());
            calendar = Calendar.getInstance();
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            selectedObject.setEndDate(calendar.getTime());

        }

        toogleList = Arrays.asList(true, true, true, true, true, true);
        changeBranch();

    }

    @Override
    public void find() {
        isFind = true;
        createWhere = duePaymentsReportService.createWhere(selectedObject);
        listOfObjects = findall(createWhere);
        listOfTotal.clear();

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmDuePaymentsReportDatatable:dtbDuePaymentsReport");

        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmDuePaymentsReportDatatable:dtbDuePaymentsReport");

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //Şimdilik ihtiyac yok
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //Şimdilik ihtiyac yok
    }

    @Override
    public LazyDataModel<DuePaymentsReport> findall(String where) {
        return new CentrowizLazyDataModel<DuePaymentsReport>() {
            @Override
            public List<DuePaymentsReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<DuePaymentsReport> result = new ArrayList<>();
                result = duePaymentsReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList, selectedObject);
                listOfTotal = duePaymentsReportService.totals(where);

                int count = 0;
                for (DuePaymentsReport total : listOfTotal) {
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

        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        totalRemainingMoney = "";

        int count = 0;

        for (DuePaymentsReport t : listOfTotal) {
            if (count == 1) {
                totalRemainingMoney += " + " + String.valueOf(formatter.format(t.getRemainingMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            } else {
                count = 1;
                totalRemainingMoney += " " + String.valueOf(formatter.format(t.getRemainingMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            }
        }
    }

    public void changeBranch() {

        if (selectedObject.getSelectedBranchList().size() == 1) {
            branchId = selectedObject.getSelectedBranchList().get(0).getBranch().getId();
            selectedObject.setBranchSetting(selectedObject.getSelectedBranchList().get(0));
            isThereListBranch = false;

        } else {
            isThereListBranch = true;
            branchId = -1;
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            accountBookCheckboxFilterBean.getSelectedDataList().clear();
            selectedObject.getListOfAccount().clear();
            selectedObject.setInvoiceType(2);
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        }

        selectedObject.getAccount().setName("");
        selectedObject.setAccount(new Account());

    }

    public void openDialog() {
        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();

        if (!selectedObject.getListOfAccount().isEmpty()) {
            if (selectedObject.getListOfAccount().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.isAll = true;
            } else {
                accountBookCheckboxFilterBean.isAll = false;
            }
        }
    }

    public void updateAllInformation(ActionEvent event) {

        selectedObject.getListOfAccount().clear();

        if (accountBookCheckboxFilterBean.isAll) {
            Account s = new Account(0);
            if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Account a = new Account(0);
                a.setName(sessionBean.loc.getString("all"));
                accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, a);
            }
        } else if (!accountBookCheckboxFilterBean.isAll) {
            if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
        }

        selectedObject.getListOfAccount().addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
        RequestContext.getCurrentInstance().update("frmDuePayments:txtCustomer");

    }

    public void createPdf() {
        duePaymentsReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedObject.getSelectedBranchList(), totalRemainingMoney);
    }

    public void createExcel() {
        duePaymentsReportService.exportExcel(createWhere, selectedObject, toogleList, branchList, selectedObject.getSelectedBranchList(), totalRemainingMoney);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(duePaymentsReportService.exportPrinter(createWhere, selectedObject, toogleList, branchList, selectedObject.getSelectedBranchList(), totalRemainingMoney)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void onRowDoubleClick(final SelectEvent event) {
        DuePaymentsReport duePaymentsReport = (DuePaymentsReport) event.getObject();
        List<Object> list = new ArrayList<>();

        Invoice invoice = new Invoice();
        invoice.setId(duePaymentsReport.getId());

        invoice = invoiceService.findInvoice(invoice);
        list.add(invoice);
        marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);

    }

}
