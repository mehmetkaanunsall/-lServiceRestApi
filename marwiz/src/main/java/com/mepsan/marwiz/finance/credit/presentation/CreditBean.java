/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.presentation;

import com.mepsan.marwiz.finance.credit.business.GFCreditService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Gozde Gursel
 */
@ManagedBean
@ViewScoped
public class CreditBean extends GeneralBean<CreditReport> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{creditService}")
    private ICreditService creditService;

    @ManagedProperty(value = "#{gfCreditService}")
    private GFCreditService gfCreditService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private BigDecimal totalMoney, paidMoney, remainingMoney, paymentTotalMoney, paymentPaidMoney, paymentRemainingMoney;
    private Currency currency;
    private int creditType;

    private List<Branch> listOfBranch;
    String createWhere;

    public class CreditParam {

        private Branch branch;

        public CreditParam() {
            this.branch = new Branch();
        }

        public Branch getBranch() {
            return branch;
        }

        public void setBranch(Branch branch) {
            this.branch = branch;
        }

    }

    private CreditParam searchObject;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public ICreditService getCreditService() {
        return creditService;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public void setGfCreditService(GFCreditService gfCreditService) {
        this.gfCreditService = gfCreditService;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(BigDecimal remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    public BigDecimal getPaidMoney() {
        return paidMoney;
    }

    public void setPaidMoney(BigDecimal paidMoney) {
        this.paidMoney = paidMoney;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPaymentTotalMoney() {
        return paymentTotalMoney;
    }

    public void setPaymentTotalMoney(BigDecimal paymentTotalMoney) {
        this.paymentTotalMoney = paymentTotalMoney;
    }

    public BigDecimal getPaymentPaidMoney() {
        return paymentPaidMoney;
    }

    public void setPaymentPaidMoney(BigDecimal paymentPaidMoney) {
        this.paymentPaidMoney = paymentPaidMoney;
    }

    public BigDecimal getPaymentRemainingMoney() {
        return paymentRemainingMoney;
    }

    public void setPaymentRemainingMoney(BigDecimal paymentRemainingMoney) {
        this.paymentRemainingMoney = paymentRemainingMoney;
    }

    public int getCreditType() {
        return creditType;
    }

    public void setCreditType(int creditType) {
        this.creditType = creditType;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public CreditParam getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(CreditParam searchObject) {
        this.searchObject = searchObject;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("---------------CreditBean*********");
        toogleList = createToggleList(sessionBean.getUser());
        searchObject = new CreditParam();
        listOfBranch = new ArrayList<>();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true);

        listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof CreditParam) {
                    searchObject = (CreditParam) (((ArrayList) sessionBean.parameter).get(i));
                    break;
                }
            }
        } else {
            for (Branch b : listOfBranch) {
                if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                    searchObject.getBranch().setId(b.getId());
                    break;
                }
            }
        }
        find();

        setListBtn(new ArrayList<>());
    }

    public void find() {
        createWhere = creditService.createWhere(searchObject.getBranch());
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmCredit:dtbCredit");

    }

    public void goToProcess() {
        List<Object> list = new ArrayList<>();
        list.add(searchObject);
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/credit/creditprocess.xhtml", list, 0, 79);
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
    public void generalFilter() {
        if (autoCompleteValue == null) {
            listOfObjects = findall(createWhere);
        } else {
            gfCreditService.makeSearch(autoCompleteValue, createWhere, creditType);
            listOfObjects = gfCreditService.searchResult;
        }
    }

    @Override
    public LazyDataModel<CreditReport> findall(String where) {
        return new CentrowizLazyDataModel<CreditReport>() {
            @Override
            public List<CreditReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                String where1 = "";
                switch (creditType) {
                    case 0:///tümü
                        where1 = where + "  ";
                        break;
                    case 1://kredi tahsilatları
                        where1 = where + " AND crdt.is_customer=TRUE ";
                        break;
                    case 2://kredi ödemeleri
                        where1 = where + " AND crdt.is_customer=FALSE";
                        break;
                    default:
                        break;
                }

                List<CreditReport> result = creditService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                int count = creditService.count(where1);
                listOfObjects.setRowCount(count);

                calculateTotal(result); // Genel Toplamı verir
                return result;

            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void calculateTotal(List<CreditReport> list) {
        totalMoney = BigDecimal.valueOf(0);
        remainingMoney = BigDecimal.valueOf(0);
        paidMoney = BigDecimal.valueOf(0);
        paymentPaidMoney = BigDecimal.valueOf(0);
        paymentRemainingMoney = BigDecimal.valueOf(0);
        paymentTotalMoney = BigDecimal.valueOf(0);

        currency = new Currency();
        if (list.size() > 0) {
            totalMoney = list.get(0).getOverallmoney();
            remainingMoney = list.get(0).getOverallremainingmoney();
            paidMoney = totalMoney.subtract(remainingMoney);
            paymentTotalMoney = list.get(0).getOverallPaymentMoney();
            paymentRemainingMoney = list.get(0).getOverallPaymentRemaining();
            paymentPaidMoney = paymentTotalMoney.subtract(paymentRemainingMoney);
            currency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        }

    }

    public void bringListAccordingToIsCustomer() {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmCredit:dtbCredit");
        dataTable.setFirst(0);

        listOfObjects = findall(createWhere);
    }
}
