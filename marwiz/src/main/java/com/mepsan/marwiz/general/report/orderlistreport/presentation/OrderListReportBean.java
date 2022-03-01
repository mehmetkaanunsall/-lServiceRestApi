/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 04:58:15
 */
package com.mepsan.marwiz.general.report.orderlistreport.presentation;

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
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.orderlistreport.business.IOrderListReportService;
import com.mepsan.marwiz.general.report.orderlistreport.dao.OrderListReport;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
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

@ManagedBean
@ViewScoped
public class OrderListReportBean extends GeneralReportBean<OrderListReport> {

    @ManagedProperty(value = "#{orderListReportService}")
    public IOrderListReportService orderListReportService;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{warehouseService}")
    public IWarehouseService warehouseService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private List<Warehouse> listOfWarehouse;
    private List<Warehouse> selectedListOfWarehouse;
    private String createWhere;
    private List<Stock> listOfStock;
    private List<Categorization> listOfCategorization;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private boolean isCentralSupplier;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private boolean isCentralSupplierIconView;
    private String branchList;

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public IOrderListReportService getOrderListReportService() {
        return orderListReportService;
    }

    public void setOrderListReportService(IOrderListReportService orderListReportService) {
        this.orderListReportService = orderListReportService;
    }

    public List<Warehouse> getSelectedListOfWarehouse() {
        return selectedListOfWarehouse;
    }

    public void setSelectedListOfWarehouse(List<Warehouse> selectedListOfWarehouse) {
        this.selectedListOfWarehouse = selectedListOfWarehouse;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
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

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
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

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------OrderListReportBean");
        selectedListOfWarehouse = new ArrayList<>();
        listOfStock = new ArrayList<>();
        listOfCategorization = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
        }

        selectedObject = new OrderListReport();
        branchList = "";
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

        branchList = String.valueOf(sessionBean.getUser().getLastBranch().getId());
        listOfWarehouse = orderListReportService.listWarehouse(String.valueOf(sessionBean.getUser().getLastBranch().getId()));

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true);
        }
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            changeBranch();

        }
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    @Override
    public void find() {
        boolean centralIntegration = false;
        isFind = true;
        selectedObject.getWarehouseList().clear();
        selectedObject.getWarehouseList().addAll(selectedListOfWarehouse);

        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (isCentralSupplier) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        for (BranchSetting brs : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {
            if (brs.isIsCentralIntegration() && brs.getBranch().getConceptType() == 1) {
                centralIntegration = true;
                break;
            }
        }
        if (centralIntegration) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true);
        }

        createWhere = orderListReportService.createWhere(selectedObject, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, centralSupplierBookCheckboxFilterBean.getSupplierType(), isCentralSupplier);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmOrderListDatatable:dtbOrderList");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(createWhere);

    }

    @Override
    public LazyDataModel<OrderListReport> findall(String where) {
        return new CentrowizLazyDataModel<OrderListReport>() {
            @Override
            public List<OrderListReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<OrderListReport> result = orderListReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList);
                int count = orderListReportService.count(where, branchList);
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
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
            RequestContext.getCurrentInstance().update("frmOrderList:txtStock");
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
            RequestContext.getCurrentInstance().update("frmOrderList:txtCentralSupplier");
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
            RequestContext.getCurrentInstance().update("frmOrderList:txtSupplier");
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

            RequestContext.getCurrentInstance().update("frmOrderList:txtCategory");
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

        orderListReportService.exportPdf(createWhere, selectedObject, toogleList, isCentralSupplier, branchList, selectedBranchList);
    }

    public void createExcel() throws IOException {
        orderListReportService.exportExcel(createWhere, selectedObject, toogleList, isCentralSupplier, branchList, selectedBranchList);//, sessionBean.getLoc().getString("orderlistreport"));

    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(orderListReportService.exportPrinter(createWhere, selectedObject, toogleList, isCentralSupplier, branchList, selectedBranchList)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

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

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeBranch() {
        branchList = "";
        if (selectedBranchList.size() == 1) {
            listOfStock.clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            selectedObject.getStockList().clear();
            selectedListOfWarehouse.clear();
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            if (selectedBranchList.get(0).isIsCentralIntegration() && selectedBranchList.get(0).getBranch().getConceptType() == 1) {

                isCentralSupplierIconView = true;
            } else {
                isCentralSupplierIconView = false;
                isCentralSupplier = false;
            }
        } else {
            listOfStock.clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            selectedObject.getStockList().clear();
            selectedListOfWarehouse.clear();
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
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

        listOfWarehouse = orderListReportService.listWarehouse(branchList);

    }
}
