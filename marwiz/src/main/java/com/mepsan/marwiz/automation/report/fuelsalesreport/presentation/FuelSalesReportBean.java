/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelsalesreport.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.automation.report.fuelsalesreport.business.IFuelSalesReportService;
import com.mepsan.marwiz.automation.saletype.business.ISaleTypeService;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.general.model.automation.FuelSalesReport;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import org.primefaces.model.LazyDataModel;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.SortOrder;

/**
 *
 * @author ebubekir.buker
 */
@ManagedBean
@ViewScoped
public class FuelSalesReportBean extends GeneralReportBean<FuelSalesReport> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{fuelSalesReportService}")
    public IFuelSalesReportService fuelSalesReportService;


    @ManagedProperty(value = "#{saleTypeService}")
    private ISaleTypeService saleTypeService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private String createWhere;
    private BigDecimal totalMoney;
    private Currency currency;

    private FuelSalesReport selectedFuelSalesReport;
    private List<FuelSalesReport> listOfTotals;
    private List<Branch> listOfBranch;
    private String branchList;
    private List<FuelSaleType> saleTypeList;
    private String selectedCountAttendant;
    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IFuelSalesReportService getFuelSalesReportService() {
        return fuelSalesReportService;
    }

    public void setFuelSalesReportService(IFuelSalesReportService fuelSalesReportService) {
        this.fuelSalesReportService = fuelSalesReportService;
    }

   
    public AccountBookCheckboxFilterBean getAccountBookCheckboxFilterBean() {
        return accountBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public IBranchService getBranchService() {
        return branchService;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
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

    public FuelSalesReport getSelectedFuelSalesReport() {
        return selectedFuelSalesReport;
    }

    public void setSelectedFuelSalesReport(FuelSalesReport selectedFuelSalesReport) {
        this.selectedFuelSalesReport = selectedFuelSalesReport;
    }

    public List<FuelSalesReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<FuelSalesReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    public List<FuelSaleType> getSaleTypeList() {
        return saleTypeList;
    }

    public void setSaleTypeList(List<FuelSaleType> saleTypeList) {
        this.saleTypeList = saleTypeList;
    }

    public void setSaleTypeService(ISaleTypeService saleTypeService) {
        this.saleTypeService = saleTypeService;
    }

    public String getSelectedCountAttendant() {
        return selectedCountAttendant;
    }

    public void setSelectedCountAttendant(String selectedCountAttendant) {
        this.selectedCountAttendant = selectedCountAttendant;
    }


    

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------------FuelSalesReportBean-------------------");
        
        selectedObject = new FuelSalesReport();
        selectedFuelSalesReport = new FuelSalesReport();
        listOfTotals = new ArrayList<>();
        saleTypeList = new ArrayList<>();

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());

        selectedObject.setMinSalesPrice(null);
        selectedObject.setMaxSalesPrice(null);


        listOfBranch = branchService.findUserAuthorizeBranch();
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (Branch branch : listOfBranch) {
                selectedObject.getSelectedBranchList().add(branch);
            }
        } else {
            for (Branch branch : listOfBranch) {
                if (branch.getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedObject.getSelectedBranchList().add(branch);
                    break;
                }
            }
        }

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        changeBranch();
        
    }

    @Override
    public void find() {
        isFind = true;

 
        createWhere = fuelSalesReportService.createWhere(selectedObject);

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmFuelSalesReportDatatable:dtbFuelSalesReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmFuelSalesReportDatatable:dtbFuelSalesReport");
    }

    public void changeBranch() {
        selectedObject.getSelectedFuelSaleTypeList().clear();
        
        branchList = "";
        for (Branch branch : selectedObject.getSelectedBranchList().isEmpty() ? listOfBranch : selectedObject.getSelectedBranchList()) {
            branchList = branchList + "," + String.valueOf(branch.getId());
            if (branch.getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        saleTypeList = saleTypeService.findSaleTypeForBranch(" AND fst.branch_id IN (" + branchList + ")");
        
        selectedObject.getAccount().setName("");
        selectedObject.getSelectedFuelSaleTypeList().clear();
        selectedObject.getListOfPumper().clear();
        selectedObject.getListOfAccount().clear();
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        setSelectedCountAttendant(sessionBean.loc.getString("all"));
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
    public LazyDataModel<FuelSalesReport> findall(String where) {
        return new CentrowizLazyDataModel<FuelSalesReport>() {
            @Override
            public List<FuelSalesReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<FuelSalesReport> result = new ArrayList<>();
                result = fuelSalesReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList, selectedObject);
                int count = 0;
                count = fuelSalesReportService.count(where);
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void updateAllInformation(ActionEvent event) {

        if (event.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {

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
            RequestContext.getCurrentInstance().update("frmFuelSalesReport:txtCustomer");

        }

        if (event.getComponent().getParent().getParent().getId().equals("frmAttendantBookFilter")) {
            selectedObject.getListOfPumper().clear();
            
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
                setSelectedCountAttendant(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                setSelectedCountAttendant(sessionBean.loc.getString("all"));
            } else {
                setSelectedCountAttendant(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("pumpper") + " " + sessionBean.loc.getString("selected"));
            }

            selectedObject.getListOfPumper().addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
            RequestContext.getCurrentInstance().update("frmFuelSalesReport:txtPumper");

        }

    }

    public void createPdf() {
        fuelSalesReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedObject.getSelectedBranchList());
    }

    public void createExcel() throws IOException {
        fuelSalesReportService.exportExcel(createWhere, selectedObject, toogleList, listOfTotals, branchList, selectedObject.getSelectedBranchList());
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(fuelSalesReportService.exportPrinter(createWhere, selectedObject, toogleList, listOfTotals, branchList, selectedObject.getSelectedBranchList())) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void openDialog(int type) {
        if (type == 1) {
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            accountBookCheckboxFilterBean.getSelectedDataList().clear();
            if (!selectedObject.getListOfAccount().isEmpty()) {
                if (selectedObject.getListOfAccount().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }

            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getListOfAccount());
            accountBookCheckboxFilterBean.getSelectedDataList().addAll(selectedObject.getListOfAccount());
        } else if (type == 2) {
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            accountBookCheckboxFilterBean.getSelectedDataList().clear();
            if (!selectedObject.getListOfPumper().isEmpty()) {
                if (selectedObject.getListOfPumper().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }

            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getListOfPumper());
            accountBookCheckboxFilterBean.getSelectedDataList().addAll(selectedObject.getListOfPumper());
        }

    }

}
