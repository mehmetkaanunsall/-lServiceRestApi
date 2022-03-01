package com.mepsan.marwiz.finance.customeragreements.presentation;

import com.mepsan.marwiz.finance.customeragreements.business.ICustomerAgreementsService;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class CustomerAgreementsBean extends GeneralDefinitionBean<CustomerAgreements> {

    @ManagedProperty(value = "#{customerAgreementsService}")
    public ICustomerAgreementsService customerAgreementsService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private boolean isFind;
    String createWhere;

    private CustomerAgreements selectedAgreement;
    private List<Branch> listOfBranch;
    private int invoiceType;
    private List<CustomerAgreements> selectedCustomerAgreementsList;
    private boolean isCheck;
    private boolean accountControl;
    private List<CustomerAgreements> selectedAccountObject;
    private boolean control;
    private String totalMoney;

    public boolean isControl() {
        return control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setCustomerAgreementsService(ICustomerAgreementsService customerAgreementsService) {
        this.customerAgreementsService = customerAgreementsService;
    }

    public CustomerAgreements getSelectedAgreement() {
        return selectedAgreement;
    }

    public void setSelectedAgreement(CustomerAgreements selectedAgreement) {
        this.selectedAgreement = selectedAgreement;
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

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public AccountBookCheckboxFilterBean getAccountBookCheckboxFilterBean() {
        return accountBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<CustomerAgreements> getSelectedCustomerAgreementsList() {
        return selectedCustomerAgreementsList;
    }

    public void setSelectedCustomerAgreementsList(List<CustomerAgreements> selectedCustomerAgreementsList) {
        this.selectedCustomerAgreementsList = selectedCustomerAgreementsList;
    }

    public boolean isIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public List<CustomerAgreements> getSelectedAccountObject() {
        return selectedAccountObject;
    }

    public void setSelectedAccountObject(List<CustomerAgreements> selectedAccountObject) {
        this.selectedAccountObject = selectedAccountObject;
    }

    public boolean isAccountControl() {

        return accountControl;
    }

    public void setAccountControl(boolean accountControl) {
        this.accountControl = accountControl;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Override
    @PostConstruct
    public void init() {
        selectedAgreement = new CustomerAgreements();
        listOfFilteredObjects = new ArrayList<>();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        selectedAgreement.setBeginDate(calendar.getTime());

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        selectedAgreement.setEndDate(calendar.getTime());

        toogleList = new ArrayList<>();

        listOfBranch = new ArrayList<>();
        selectedCustomerAgreementsList = new ArrayList<>();
        selectedAgreement.setListOfAccount(new ArrayList<>());

        listOfObjects = new ArrayList<>();
        selectedAccountObject = new ArrayList<>();
        selectedAgreement.setCreditType(1);

        listOfBranch = branchService.findUserAuthorizeBranch();
        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedAgreement.getBranchSetting().getBranch().setId(b.getId());
                break;
            }
        }
        changeBranch();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {

                if (((ArrayList) sessionBean.parameter).get(i) instanceof CustomerAgreements) {
                    selectedAgreement = (CustomerAgreements) (((ArrayList) sessionBean.parameter).get(i));
                    accountBookCheckboxFilterBean.setSelectedCount(selectedAgreement.getSelectedAccountCount());

                    find();

                    if (selectedAgreement.getCreditIds() != null) {
                        String[] credits = selectedAgreement.getCreditIds().split(",");
                        for (int j = 0; j < credits.length; j++) {
                            for (CustomerAgreements customer : listOfFilteredObjects) {
                                if (Integer.valueOf(credits[j]) == customer.getId()) {
                                    selectedCustomerAgreementsList.add(customer);
                                    customer.setIsCheck(true);
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }

        // 0-faturacheckbox ,1-fatura tipi, 2-Kredi tarihi, 3-müşteri ,4-plaka , 5-ürün, 6-miktar ,7-birim fiyatı , 8-tutar
        if (toogleList.isEmpty()) {

            if (selectedAgreement.isChcCredit()) //kredi bazında göster
            {

                if (selectedAgreement.getInvoiceType() == 0) //fatura tipi-faturalanmamış
                {
                    if (selectedAgreement.getCreditType() == 1) {
                        toogleList = Arrays.asList(true, false, true, true, false, false, false, false, true);
                    }

                    if (selectedAgreement.getCreditType() == 2) {
                        toogleList = Arrays.asList(true, false, true, true, true, true, true, true, true);
                    }
                }

                if (selectedAgreement.getInvoiceType() == 1) //fatura tipi - faturalanmış
                {
                    if (selectedAgreement.getCreditType() == 1) {
                        toogleList = Arrays.asList(false, false, true, true, false, false, false, false, true);
                    }

                    if (selectedAgreement.getCreditType() == 2) {
                        toogleList = Arrays.asList(false, false, true, true, true, true, true, true, true);
                    }
                }
                if (selectedAgreement.getInvoiceType() == 2) //fatura tipi - hepsi
                {

                    if (selectedAgreement.getCreditType() == 1) {
                        toogleList = Arrays.asList(true, false, true, true, false, false, false, false, true);
                    }

                    if (selectedAgreement.getCreditType() == 2) {
                        toogleList = Arrays.asList(true, false, true, true, true, true, true, true, true);
                    }

                }

            } // 0-faturacheckbox ,1-fatura tipi, 2-Kredi tarihi, 3-müşteri ,4-plaka , 5-ürün, 6-miktar ,7-birim fiyatı , 8-tutar
            else//cari bazında göster 
            {
                if (selectedAgreement.getInvoiceType() == 0) {
                    toogleList = Arrays.asList(false, false, false, true, false, false, false, false, true);
                }
                if (selectedAgreement.getInvoiceType() == 1) {
                    toogleList = Arrays.asList(false, false, false, true, false, false, false, false, true);
                }
                if (selectedAgreement.getInvoiceType() == 2) {
                    toogleList = Arrays.asList(false, true, false, true, false, false, false, false, true);
                }
            }

        }

        RequestContext.getCurrentInstance().update("frmCustomerAgreements:txtCustomer");
        RequestContext.getCurrentInstance().update("frmCustomerAgreementsDatatable:dtbCustomerAgreements");

    }

    public void find() {
        isFind = true;
        selectedAgreement.setCheckAll(false);
        selectedCustomerAgreementsList.clear();
        setCountToggle(0);
        // 0-faturacheckbox ,1-fatura tipi, 2-Kredi tarihi, 3-müşteri ,4-plaka , 5-ürün, 6-miktar ,7-birim fiyatı , 8-tutar
        if (selectedAgreement.isChcCredit()) //kredi bazında göster
        {

            if (selectedAgreement.getInvoiceType() == 0) //fatura tipi-faturalanmamış
            {
                if (selectedAgreement.getCreditType() == 1) {
                    toogleList = Arrays.asList(true, false, true, true, false, false, false, false, true);
                }

                if (selectedAgreement.getCreditType() == 2) {
                    toogleList = Arrays.asList(true, false, true, true, true, true, true, true, true);
                }
            }

            if (selectedAgreement.getInvoiceType() == 1) //fatura tipi - faturalanmış
            {
                if (selectedAgreement.getCreditType() == 1) {
                    toogleList = Arrays.asList(false, false, true, true, false, false, false, false, true);
                }

                if (selectedAgreement.getCreditType() == 2) {
                    toogleList = Arrays.asList(false, false, true, true, true, true, true, true, true);
                }
            }
            if (selectedAgreement.getInvoiceType() == 2) //fatura tipi - hepsi
            {

                if (selectedAgreement.getCreditType() == 1) {
                    toogleList = Arrays.asList(true, false, true, true, false, false, false, false, true);
                }

                if (selectedAgreement.getCreditType() == 2) {
                    toogleList = Arrays.asList(true, false, true, true, true, true, true, true, true);
                }

            }

        } // 0-faturacheckbox ,1-fatura tipi, 2-Kredi tarihi, 3-müşteri ,4-plaka , 5-ürün, 6-miktar ,7-birim fiyatı , 8-tutar
        else//cari bazında göster 
        {
            if (selectedAgreement.getInvoiceType() == 0) {
                toogleList = Arrays.asList(false, false, false, true, false, false, false, false, true);
            }
            if (selectedAgreement.getInvoiceType() == 1) {
                toogleList = Arrays.asList(false, false, false, true, false, false, false, false, true);
            }
            if (selectedAgreement.getInvoiceType() == 2) {
                toogleList = Arrays.asList(false, true, false, true, false, false, false, false, true);

            }
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedAgreement.getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedAgreement.setEndDate(calendar.getTime());

        calendar.setTime(selectedAgreement.getBeginDate());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        selectedAgreement.setBeginDate(calendar.getTime());

        createWhere = customerAgreementsService.createWhere(selectedAgreement);
        listOfObjects = findall();

        listOfFilteredObjects.clear();
        listOfFilteredObjects.addAll(listOfObjects);

        calculate();
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmCustomerAgreementsDatatable:dtbCustomerAgreements");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        RequestContext.getCurrentInstance().update("frmCustomerAgreementsDatatable:dtbCustomerAgreements");

    }

    public void goToProcess() {

        if (!selectedAgreement.isChcCredit()) {
            if (selectedObject.isIsInvoice()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theselectedrecordhasalreadybeenbilled")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                RequestContext.getCurrentInstance().update("pgrCustomerAgreementsDatatable");
            } else {
                List<Object> list = new ArrayList<>();
                selectedObject.setBeginDate(selectedAgreement.getBeginDate());
                selectedObject.setEndDate(selectedAgreement.getEndDate());

                selectedObject.getBranchSetting().getBranch().setId(selectedAgreement.getBranchSetting().getBranch().getId());
                selectedObject.setInvoiceType(selectedAgreement.getInvoiceType());
                selectedObject.getListOfAccount().clear();
                selectedObject.getListOfAccount().addAll(selectedAgreement.getListOfAccount());
                selectedObject.setChcCredit(selectedAgreement.isChcCredit());
                selectedObject.setCreditType(selectedAgreement.getCreditType());
                selectedObject.setPlate(selectedAgreement.getPlate());
                list.add(selectedObject);
                list.add(new Object());
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
            }
        }

    }

    public void goToClickProcess() {

        String creditId = "";
        CustomerAgreements customerAgreements = new CustomerAgreements();
        List<Object> list = new ArrayList<>();
        for (CustomerAgreements customer : selectedCustomerAgreementsList) {
            creditId += "," + customer.getId();
        }
        if (!creditId.isEmpty()) {
            creditId = creditId.substring(1, creditId.length());
        }

        customerAgreements.getBranchSetting().getBranch().setId(selectedAgreement.getBranchSetting().getBranch().getId());
        customerAgreements.setBeginDate(selectedAgreement.getBeginDate());
        customerAgreements.setEndDate(selectedAgreement.getEndDate());
        customerAgreements.setInvoiceType(selectedAgreement.getInvoiceType());
        customerAgreements.getListOfAccount().clear();
        customerAgreements.getListOfAccount().addAll(selectedAgreement.getListOfAccount());
        customerAgreements.setChcCredit(selectedAgreement.isChcCredit());
        customerAgreements.setCreditType(selectedAgreement.getCreditType());
        customerAgreements.setPlate(selectedAgreement.getPlate());

        customerAgreements.setCreditIds(creditId);
        customerAgreements.setAccount(selectedCustomerAgreementsList.get(0).getAccount());
        customerAgreements.getCurrency().setId(selectedCustomerAgreementsList.get(0).getCurrency().getId());
        customerAgreements.getBranchSetting().setIsCentralIntegration(selectedCustomerAgreementsList.get(0).getBranchSetting().isIsCentralIntegration());
        customerAgreements.getBranchSetting().setIsInvoiceStockSalePriceList(selectedCustomerAgreementsList.get(0).getBranchSetting().isIsInvoiceStockSalePriceList());
        customerAgreements.getBranchSetting().getBranch().getCurrency().setId(selectedCustomerAgreementsList.get(0).getBranchSetting().getBranch().getCurrency().getId());
        customerAgreements.getBranchSetting().getBranch().setIsAgency(selectedCustomerAgreementsList.get(0).getBranchSetting().getBranch().isIsAgency());
        customerAgreements.getBranchSetting().setIsUnitPriceAffectedByDiscount(selectedCustomerAgreementsList.get(0).getBranchSetting().isIsUnitPriceAffectedByDiscount());
        customerAgreements.setSelectedAccountCount(accountBookCheckboxFilterBean.getSelectedCount());

        list.add(customerAgreements);
        list.add(new Object());
        marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);

    }

    public void openDialog() {
        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();

        if (!selectedAgreement.getListOfAccount().isEmpty()) {
            if (selectedAgreement.getListOfAccount().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.isAll = true;
            } else {
                accountBookCheckboxFilterBean.isAll = false;
            }
        }
        accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedAgreement.getListOfAccount());
    }

    public void updateAllInformation(ActionEvent event) {

        selectedAgreement.getListOfAccount().clear();
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

        selectedAgreement.getListOfAccount().addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
        RequestContext.getCurrentInstance().update("frmCustomerAgreements:txtCustomer");

    }

    public void setSelected() {
        for (CustomerAgreements select : listOfObjects) {
            selectedCustomerAgreementsList.add(select);
        }
    }

    public void checkAccount(CustomerAgreements customerAgreements) {

        if (selectedCustomerAgreementsList.isEmpty()) {
            if (customerAgreements.isIsCheck()) {
                selectedCustomerAgreementsList.add(customerAgreements);
            }
        } else {

            if (customerAgreements.isIsCheck()) {
                boolean isThere = true;
                for (CustomerAgreements customer : selectedCustomerAgreementsList) {
                    if (customer.getAccount().getId() != customerAgreements.getAccount().getId()) {
                        isThere = false;
                        break;
                    } else {
                        isThere = true;
                    }
                }
                if (!isThere) {
                    customerAgreements.setIsCheck(false);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("youcannotchoosedifferentcurrent")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    selectedCustomerAgreementsList.add(customerAgreements);
                }
                RequestContext.getCurrentInstance().update("pgrCustomerAgreementsDatatable");
            } else {
                for (Iterator<CustomerAgreements> iterator = selectedCustomerAgreementsList.iterator(); iterator.hasNext();) {
                    CustomerAgreements value = iterator.next();
                    if (value.getId() == customerAgreements.getId()) {
                        iterator.remove();
                    }
                }
            }

        }

    }

    public void creditControl() {//Cari seçmeden faturalandır butonuna tıklarsa çıkacak uyarı

        if (selectedCustomerAgreementsList.size() > 0) {
            goToClickProcess();
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("selectthecreditaccountyouwanttobill")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            RequestContext.getCurrentInstance().update("pgrCustomerAgreementsDatatable");
        }

    }

    public void selectAllCheckbox() {
        selectedCustomerAgreementsList.clear();
        boolean isThere = false;

        List<CustomerAgreements> tempList = new ArrayList<>();
        if (listOfObjects.size() != listOfFilteredObjects.size()) {
            tempList.addAll(listOfFilteredObjects);
        } else {
            tempList.addAll(listOfObjects);
        }

        if (listOfFilteredObjects != null) {
            if (!tempList.isEmpty()) {

                if (selectedAgreement.isCheckAll()) {
                    for (CustomerAgreements customer1 : tempList) {

                        int count = 0;

                        for (Iterator<CustomerAgreements> iterator = listOfFilteredObjects.iterator(); iterator.hasNext();) {
                            CustomerAgreements value = iterator.next();
                            if (customer1.getAccount().getId() == value.getAccount().getId()) {
                                count++;

                            } else {
                                isThere = false;
                                break;
                            }
                        }

                        if ((tempList.size() > 1 && count > 1) || (tempList.size() == 1 && count == 1)) {
                            isThere = true;
                            if (!customer1.isIsInvoice()) {

                                customer1.setIsCheck(true);
                                selectedCustomerAgreementsList.add(customer1);

                            }
                        }

                        if (!isThere) {
                            break;
                        }
                    }
                    if (!isThere) {
                        selectedCustomerAgreementsList.clear();
                        selectedAgreement.setCheckAll(false);
                        for (CustomerAgreements customer1 : listOfObjects) {
                            if (customer1.isIsCheck()) {
                                customer1.setIsCheck(false);
                            }
                        }

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("notallofthemcanbeselectedastherearedifferentconcubinesinthelist")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }

                } else {

                    selectedCustomerAgreementsList.clear();
                    for (CustomerAgreements customer1 : listOfObjects) {
                        if (customer1.isIsCheck()) {
                            customer1.setIsCheck(false);
                        }
                    }

                }

            } else {
                selectedAgreement.setCheckAll(false);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("norecordsavailableforselection")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

            RequestContext.getCurrentInstance().update("frmCustomerAgreementsDatatable");
        }
    }

    public String calculate() {
        totalMoney = "";
        HashMap<Integer, BigDecimal> hashMap = new HashMap();
        hashMap.clear();

        for (CustomerAgreements c : listOfObjects) {

            if (hashMap.containsKey(c.getCurrency().getId())) {
                BigDecimal old = hashMap.get(c.getCurrency().getId());
                BigDecimal val = old.add(c.getMoney());
                hashMap.put(c.getCurrency().getId(), val);
            } else {
                hashMap.put(c.getCurrency().getId(), c.getMoney());
            }
        }

        int temp = 0;
        String currencyCode = "";
        for (Map.Entry<Integer, BigDecimal> entry : hashMap.entrySet()) {

            for (CustomerAgreements currency : listOfObjects) {
                if (entry.getKey() == currency.getCurrency().getId()) {
                    currencyCode = currency.getCurrency().getCode();

                    break;
                }
            }
            if (temp == 0) {
                temp = 1;
                totalMoney += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                if (entry.getKey() != 0) {
                    totalMoney += " " + currencyCode;

                }
            } else {
                totalMoney += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                if (entry.getKey() != 0) {
                    totalMoney += " " + currencyCode;
                }
            }
        }
        return totalMoney;
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
    public List<CustomerAgreements> findall() {
        return customerAgreementsService.findAll(createWhere, selectedAgreement.isChcCredit(), selectedAgreement.getCreditType());
    }

    public void changeBranch() {
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        selectedAgreement.getListOfAccount().clear();
    }

    public void changeReportType() {
        selectedAgreement.setPlate(null);
    }

}
