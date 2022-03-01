/**
 * This class ...
 *
 *
 * @author Ali Kurt
 *
 * @date   11.01.2018 12:41:05
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.mepsan.marwiz.finance.invoice.business.GFInvoiceService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.InvoiceReport;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
public class InvoiceBean extends GeneralBean<Invoice> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    @ManagedProperty(value = "#{gfInvoiceService}")
    private GFInvoiceService gfInvoiceService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private InvoiceReport searchObject;
    private String createWhere;
    private List<BranchSetting> listOfBranch;

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public InvoiceReport getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(InvoiceReport searchObject) {
        this.searchObject = searchObject;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setGfInvoiceService(GFInvoiceService gfInvoiceService) {
        this.gfInvoiceService = gfInvoiceService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
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
        System.out.println("-------------------InvoiceBean");
        toogleList = createToggleList(sessionBean.getUser());
        listOfBranch = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true,false,false,false, true, true, true);
        }
        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof InvoiceReport) {
                    searchObject = (InvoiceReport) (((ArrayList) sessionBean.parameter).get(i));

                    accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
                    accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getAccountList());
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else {
                        accountBookCheckboxFilterBean.selectedDataList.clear();
                        accountBookCheckboxFilterBean.selectedDataList.addAll(searchObject.getAccountList());
                        accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
                    }

                    stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
                    stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getStockList());
                    if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else {
                        stockBookCheckboxFilterBean.selectedDataList.clear();
                        stockBookCheckboxFilterBean.selectedDataList.addAll(searchObject.getStockList());
                        stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
                    }
                    List<BranchSetting> temp = new ArrayList<>();
                    temp.addAll(searchObject.getSelectedBranchList());
                    searchObject.getSelectedBranchList().clear();
                    for (BranchSetting br : listOfBranch) {
                        for (BranchSetting sbr : temp) {
                            if (br.getBranch().getId() == sbr.getBranch().getId()) {
                                searchObject.getSelectedBranchList().add(br);
                            }
                        }
                    }
                    List<Type> temptypes = new ArrayList<>();
                    temptypes.addAll(searchObject.getListOfInvoiceType());
                    searchObject.getListOfInvoiceType().clear();
                    for (Type sty : sessionBean.getTypes(17)) {
                        for (Type ty : temptypes) {
                            if (sty.getId() == ty.getId()) {
                                searchObject.getListOfInvoiceType().add(sty);
                            }
                        }
                    }
                    break;
                }
            }
        }

        if (searchObject == null) {
            searchObject = new InvoiceReport();

            searchObject.setType(1); // satın alma her zaman seçili gelecek
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            searchObject.setBeginDate(calendar.getTime());
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            searchObject.setEndDate(calendar.getTime());

            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    searchObject.getSelectedBranchList().add(branchSetting);
                    break;
                }
            }
            changeBranch();
        }

        createWhere = invoiceService.createWhere(searchObject, listOfBranch);
        listOfObjects = findall(createWhere);

        setListBtn(sessionBean.checkAuthority(new int[]{1}, 0));

    }

    @Override
    public void create() {
        if (searchObject.getType() != 1 && sessionBean.getUser().getAccount().getId() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                    sessionBean.loc.getString("warning"),
                    sessionBean.loc.getString("onlypersonnelcanaddsalesinvoice"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            List<Object> list = new ArrayList<>();
            Invoice inv = new Invoice();
            inv.setIsPurchase(searchObject.getType() != 0);
            list.add(inv);
            marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 0, 26);
        }
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {
            listOfObjects = findall(createWhere);
        } else {
            gfInvoiceService.makeSearch(autoCompleteValue, createWhere);
            listOfObjects = gfInvoiceService.searchResult;
        }
    }

    @Override
    public LazyDataModel<Invoice> findall(String where) {

        return new CentrowizLazyDataModel<Invoice>() {
            @Override
            public List<Invoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Invoice> result = invoiceService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = invoiceService.count(where);
                listOfObjects.setRowCount(count);
                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmInvoice:dtbInvoice");
                if (dataTable != null) {
                    dataTable.setFirst(0);
                }
                return result;
            }
        };
    }

    public void find() {

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(searchObject.getEndDate());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        searchObject.setEndDate(cal.getTime());

        cal.setTime(searchObject.getBeginDate());
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        searchObject.setBeginDate(cal.getTime());

        createWhere = invoiceService.createWhere(searchObject, listOfBranch);
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmInvoice:dtbInvoice");
    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
            searchObject.getStockList().clear();
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

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
            searchObject.getStockList().addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());
            RequestContext.getCurrentInstance().update("frmInvoiceReport:txtStock");
        } else {
            searchObject.getAccountList().clear();
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
            searchObject.getAccountList().addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
            RequestContext.getCurrentInstance().update("frmInvoiceReport:txtCustomer");
        }
    }

    public void openDialog(int type) {

        if (type == 0) {//cari
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!searchObject.getAccountList().isEmpty()) {
                if (searchObject.getAccountList().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getAccountList());
        } else {//stok
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!searchObject.getStockList().isEmpty()) {
                if (searchObject.getStockList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getStockList());
        }
    }

    public void goToProcess() {
        List<Object> list = new ArrayList<>();
        list.add(searchObject);
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 0, 26);
    }

    @Override
    public void detailFilter() {

    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeBranch() {
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        searchObject.getStockList().clear();
        searchObject.getAccountList().clear();

    }
}
