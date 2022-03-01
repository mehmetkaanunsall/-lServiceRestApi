/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditPaymentService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.CreditPayment;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Gozde Gursel
 */
@ManagedBean
@ViewScoped
public class CreditPaymentTabBean extends AuthenticationLists {
    
    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;
    
    @ManagedProperty(value = "#{creditPaymentService}")
    public ICreditPaymentService creditPaymentService;
    
    @ManagedProperty(value = "#{creditProcessBean}")
    public CreditProcessBean creditProcessBean;
    
    @ManagedProperty(value = "#{applicationBean}")
    public ApplicationBean applicationBean;
    
    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;
    
    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;
    
    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;
    
    @ManagedProperty(value = "#{bankBranchService}")
    public IBankBranchService bankBranchService;
    
    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;
    
    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;
    
    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;
    
    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;
    
    private List<CreditPayment> listOfObjects;
    private CreditPayment selectedObject;
    private CreditReport selectedCredit;
    private List<Type> paymentList;
    private int firstId, secondId, processType;
    private List<Safe> listOfSafe;
    private List<BankAccount> listOfBankAccount;
    private Currency outCurrency, inCurrency;
    private List<DocumentNumber> listOfDocumentNumber;
    private String exchange;
    private List<City> cities;
    private String relatedRecord;
    List<CheckDelete> controlDeleteList;
    
    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2;
    
    private List<BankBranch> listOfBankBranch;
    private BigDecimal oldValue;
    
    public List<City> getCities() {
        return cities;
    }
    
    public void setCities(List<City> cities) {
        this.cities = cities;
    }
    
    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }
    
    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }
    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }
    
    public ICreditPaymentService getCreditPaymentService() {
        return creditPaymentService;
    }
    
    public void setCreditPaymentService(ICreditPaymentService creditPaymentService) {
        this.creditPaymentService = creditPaymentService;
    }
    
    public void setCreditProcessBean(CreditProcessBean creditProcessBean) {
        this.creditProcessBean = creditProcessBean;
    }
    
    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }
    
    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
    
    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }
    
    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }
    
    public List<CreditPayment> getListOfObjects() {
        return listOfObjects;
    }
    
    public void setListOfObjects(List<CreditPayment> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }
    
    public CreditPayment getSelectedObject() {
        return selectedObject;
    }
    
    public void setSelectedObject(CreditPayment selectedObject) {
        this.selectedObject = selectedObject;
    }
    
    public CreditReport getSelectedCredit() {
        return selectedCredit;
    }
    
    public void setSelectedCredit(CreditReport selectedCredit) {
        this.selectedCredit = selectedCredit;
    }
    
    public List<Type> getPaymentList() {
        return paymentList;
    }
    
    public void setPaymentList(List<Type> paymentList) {
        this.paymentList = paymentList;
    }
    
    public int getFirstId() {
        return firstId;
    }
    
    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }
    
    public int getSecondId() {
        return secondId;
    }
    
    public void setSecondId(int secondId) {
        this.secondId = secondId;
    }
    
    public List<Safe> getListOfSafe() {
        return listOfSafe;
    }
    
    public void setListOfSafe(List<Safe> listOfSafe) {
        this.listOfSafe = listOfSafe;
    }
    
    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }
    
    public void setListOfBankAccount(List<BankAccount> listOfBankAccount) {
        this.listOfBankAccount = listOfBankAccount;
    }
    
    public int getProcessType() {
        return processType;
    }
    
    public void setProcessType(int processType) {
        this.processType = processType;
    }
    
    public Currency getOutCurrency() {
        return outCurrency;
    }
    
    public void setOutCurrency(Currency outCurrency) {
        this.outCurrency = outCurrency;
    }
    
    public Currency getInCurrency() {
        return inCurrency;
    }
    
    public void setInCurrency(Currency inCurrency) {
        this.inCurrency = inCurrency;
    }
    
    public String getExchange() {
        return exchange;
    }
    
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
    
    public List<BankBranch> getListOfBankBranch() {
        return listOfBankBranch;
    }
    
    public void setListOfBankBranch(List<BankBranch> listOfBankBranch) {
        this.listOfBankBranch = listOfBankBranch;
    }
    
    public List<DocumentNumber> getListOfDocumentNumber() {
        return listOfDocumentNumber;
    }
    
    public void setListOfDocumentNumber(List<DocumentNumber> listOfDocumentNumber) {
        this.listOfDocumentNumber = listOfDocumentNumber;
    }
    
    public String getDeleteControlMessage() {
        return deleteControlMessage;
    }
    
    public void setDeleteControlMessage(String deleteControlMessage) {
        this.deleteControlMessage = deleteControlMessage;
    }
    
    public String getDeleteControlMessage1() {
        return deleteControlMessage1;
    }
    
    public void setDeleteControlMessage1(String deleteControlMessage1) {
        this.deleteControlMessage1 = deleteControlMessage1;
    }
    
    public String getDeleteControlMessage2() {
        return deleteControlMessage2;
    }
    
    public void setDeleteControlMessage2(String deleteControlMessage2) {
        this.deleteControlMessage2 = deleteControlMessage2;
    }
    
    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }
    
    public String getRelatedRecord() {
        return relatedRecord;
    }
    
    public void setRelatedRecord(String relatedRecord) {
        this.relatedRecord = relatedRecord;
    }
    
    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("-------CreditPaymentTabBean-----");
        listOfObjects = new ArrayList<>();
        selectedObject = new CreditPayment();
        selectedCredit = new CreditReport();
        listOfSafe = new ArrayList<>();
        controlDeleteList = new ArrayList<>();
        selectedCredit = creditProcessBean.getSelectedObject();
        selectedObject.getChequeBill().setAccount(creditProcessBean.getSelectedObject().getAccount());
        listOfBankBranch = bankBranchService.selectBankBranch();
        if (selectedCredit != null) {
            listOfObjects = creditPaymentService.listCreditPayment(selectedCredit);
        }
        getPaymentType(); // nakit ,kredi kartı, eft/havale ,çek ve senet ödeme tipleri çekildi.
        listOfSafe = safeService.selectSafe(selectedCredit.getBranchSetting().getBranch());
        
        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(18), selectedCredit.getBranchSetting().getBranch());
        
        cities = new ArrayList<>();
        oldValue = BigDecimal.valueOf(0);
        
        setListBtn(sessionBean.checkAuthority(new int[]{22, 23, 24}, 0));
        
    }
    
    public void createDialog(int type) {
        processType = type;
        if (processType == 1) {
            if (selectedCredit.isIsInvoice() == false && selectedCredit.getRemainingMoney().compareTo(selectedCredit.getMoney()) == 0) {
                RequestContext.getCurrentInstance().execute("PF('dlgConfirmationVar').show()");
            } else {
                selectedObject = new CreditPayment();
                // tarih Bugün olarak set edildi.
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                selectedObject.setProcessDate(new Date());
                RequestContext.getCurrentInstance().update("dlgCreditPaymentProcess");
                RequestContext.getCurrentInstance().execute("PF('dlg_CreditPaymentProcess').show()");
            }
            
        } else {
            //kredi kartı ise kredi bankaları çek
            if (selectedObject.getType().getId() == 18) {
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedCredit.getBranchSetting().getBranch());
            } else if (selectedObject.getType().getId() == 75) {//EFT/Havale ise ticari bankaları çek
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedCredit.getBranchSetting().getBranch());
            }
            selectedObject.getChequeBill().getStatus().setId(31);
            selectedObject.getChequeBill().setIsCheque(selectedObject.getType().getId() == 66);
            selectedObject.getChequeBill().setAccount(creditProcessBean.getSelectedObject().getAccount());
            oldValue = selectedObject.getPrice().multiply(selectedObject.getExchangeRate());
            updateCity();
            RequestContext.getCurrentInstance().update("dlgCreditPaymentProcess");
            RequestContext.getCurrentInstance().execute("PF('dlg_CreditPaymentProcess').show()");
            
        }
        // sessionBean.createUpdateMessage(result);

    }
    
    public void getPaymentType() {
        paymentList = new ArrayList<>();
        for (Type type : sessionBean.getTypes(15)) {
            if (type.getId() == 17 || type.getId() == 18 || type.getId() == 66 || type.getId() == 69 || type.getId() == 75) {
                type.getNameMap().get(sessionBean.getLangId()).getName();
                paymentList.add(type);
            }
        }
    }
    
    public void changeType() {
        
        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        selectedObject.getFinancingDocument().setDescription(" ");
        selectedObject.getFinancingDocument().setDocumentNumber(" ");
        selectedObject.getChequeBill().setAccount(creditProcessBean.getSelectedObject().getAccount());
        inCurrency = null;
        outCurrency = null;

        //ödeme tipi çek seçildi ise ve satış faturası ise
        if (selectedObject.getType().getId() == 66 || selectedObject.getType().getId() == 69) {
            selectedObject.getChequeBill().setExpiryDate(new Date());
            selectedObject.getChequeBill().setBillCollocationDate(new Date());
            selectedObject.getChequeBill().getStatus().setId(31);
            selectedObject.getChequeBill().setIsCheque(selectedObject.getType().getId() == 66);
            selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
            
            selectedObject.getChequeBill().getCountry().setId(Integer.valueOf(applicationBean.getParameterMap().get("default_country").getValue()));
            if (selectedObject.getChequeBill().getCountry().getId() > 0) {
                updateCity();
            }
        } else {
            selectedObject.getChequeBill().setIsCheque(false);
        }

        //kredi kartı ise kredi bankaları çek
        if (selectedObject.getType().getId() == 18) {
            listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedCredit.getBranchSetting().getBranch());
        } else if (selectedObject.getType().getId() == 75) {//EFT/Havale ise ticari bankaları çek
            listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedCredit.getBranchSetting().getBranch());
        }
        bringCurrency();
    }
    
    public void bringCurrency() {
        inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
        switch (selectedObject.getType().getId()) {
            case 17: //nakit 
                for (Safe safeObj : listOfSafe) {
                    if (selectedObject.getSafe().getId() == safeObj.getId()) {
                        outCurrency = safeObj.getCurrency();
                        break;
                    }
                }
                break;
            case 75:
            case 18:// kredi kartı
                for (BankAccount bankAccountObj : listOfBankAccount) {
                    if (bankAccountObj.getId() == selectedObject.getBankAccount().getId()) {
                        outCurrency = bankAccountObj.getCurrency();
                        break;
                    }
                }
                break;
            
            default:
                outCurrency = selectedObject.getCurrency();
                break;
        }
        
        if (inCurrency != null && outCurrency != null) {
            selectedObject.setCurrency(outCurrency);
            selectedObject.setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }
    }
    
    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedCredit.getDueDate())) {
            
            RequestContext context = RequestContext.getCurrentInstance();
            BigDecimal price = BigDecimal.ZERO;
            price = selectedObject.getPrice().multiply(selectedObject.getExchangeRate());
            if (selectedObject.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                if (processType == 2) {
                    price = price.subtract(oldValue);
                }
                
                if (price.compareTo(selectedCredit.getRemainingMoney()) <= 0) {
                    int result = 0;
                    selectedObject.getCredit().setId(selectedCredit.getId());
                    selectedObject.getCredit().setAccount(selectedCredit.getAccount());
                    selectedObject.getCredit().setIsCustomer(selectedCredit.isIsCustomer());
                    selectedObject.getCredit().getBranchSetting().getBranch().setId(selectedCredit.getBranchSetting().getBranch().getId());
                    
                    if (processType == 1) {
                        result = creditPaymentService.create(selectedObject, selectedObject.getCurrency());
                    } else {//Güncelleme
                        result = creditPaymentService.update(selectedObject);
                    }
                    
                    if (result > 0) {
                        selectedCredit.setRemainingMoney(selectedCredit.getRemainingMoney().subtract(price));
                        if (selectedCredit.getRemainingMoney().compareTo(BigDecimal.ZERO) == 0) {
                            selectedCredit.setIsPaid(Boolean.TRUE);
                        } else if (selectedCredit.getRemainingMoney().compareTo(BigDecimal.ZERO) == 1 && processType == 2) {
                            selectedCredit.setIsPaid(Boolean.FALSE);
                        }
                        creditProcessBean.setSelectedObject(selectedCredit);
                        listOfObjects = creditPaymentService.listCreditPayment(selectedCredit);
                        context.execute("PF('dlg_CreditPaymentProcess').hide();");
                        context.update("frmCreditProcess:pgrCreditProcess");
                        context.update("tbvCredit:frmCreditPaymentTab");
                    } else if (result == -101) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        sessionBean.createUpdateMessage(result);
                    }
                    
                } else {
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("theamounttobepaidcannotexceedtheinstallmentamount"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("grwProcessMessage");
                }
            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("pleasefillintherequiredfields"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("grwProcessMessage");
            }
        }
    }
    
    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedCredit.getDueDate())) {
            deleteControlMessage = "";
            deleteControlMessage1 = "";
            deleteControlMessage2 = "";
            relatedRecord = "";
            controlDeleteList.clear();
            controlDeleteList = creditPaymentService.testBeforeDelete(selectedObject);
            if (!controlDeleteList.isEmpty()) {
                if (controlDeleteList.get(0).getR_response() < 0) { //Var bağlı ise silme uyarı ver
                    if (controlDeleteList.get(0).getR_response() == -100) {
                        deleteControlMessage = sessionBean.getLoc().getString("paymentdelete");
                        deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                        deleteControlMessage2 = sessionBean.getLoc().getString("portfoliono") + " : ";
                        relatedRecord = selectedObject.getChequeBill().getPortfolioNumber();
                    } else if (controlDeleteList.get(0).getR_response() == -101) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                    if (controlDeleteList.get(0).getR_response() != -101) {
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                    }
                } else {//Sil
                    RequestContext.getCurrentInstance().update("frmCreditPaymentProcess:dlgDelete");
                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                }
            }
        }
    }
    
    public void delete() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedCredit.getDueDate())) {
            RequestContext context = RequestContext.getCurrentInstance();
            BigDecimal price = BigDecimal.ZERO;
            price = selectedObject.getPrice().multiply(selectedObject.getExchangeRate());
            int result = creditPaymentService.delete(selectedObject);
            if (result > 0) {
                selectedCredit.setRemainingMoney(selectedCredit.getRemainingMoney().add(price));
                if (selectedCredit.getRemainingMoney().compareTo(BigDecimal.ZERO) > 0) {
                    selectedCredit.setIsPaid(Boolean.FALSE);
                }
                creditProcessBean.setSelectedObject(selectedCredit);
                listOfObjects = creditPaymentService.listCreditPayment(selectedCredit);
                context.execute("PF('creditPaymentPF').filter();");
                context.execute("PF('dlg_CreditPaymentProcess').hide();");
                context.update("frmCreditProcess:pgrCreditProcess");
                context.update("tbvCredit:frmCreditPaymentTab");
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    /**
     * bu metot cek ve senette ülke seçildiğinde şehirleri günceller
     */
    public void updateCity() {
        for (Country c : sessionBean.getCountries()) {
            if (c.getId() == selectedObject.getChequeBill().getCountry().getId()) {
                cities = c.getListOfCities();
                break;
            }
        }
    }

    //cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObject.getChequeBill().setAccount(accountBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmCreditPaymentProcess:txtAccountName");
        }
    }

    /**
     * Byu metot çek senet için belge numarası seçmede olayı için yazılmıştır.
     */
    public void bringDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getChequeBill().getDocumentNumber().getId()) {
                selectedObject.getChequeBill().getDocumentNumber().setActualNumber(dn.getActualNumber());
                selectedObject.getChequeBill().getDocumentNumber().setSerial(dn.getSerial());
                selectedObject.getChequeBill().setDocumentSerial(dn.getSerial());
                selectedObject.getChequeBill().setDocumentNo("" + dn.getActualNumber());
            }
        }
    }
    
    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_CreditPaymentProcess').hide();");
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("goToRelatedRecord();");
        
    }
    
    public void goToRelatedRecord() {
        ChequeBill chequeBill = new ChequeBill();
        chequeBill.setId(selectedObject.getChequeBill().getId());
        chequeBill = chequeBillService.findChequeBill(chequeBill);
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        boolean isThere = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof ChequeBill) {
                isThere = true;
                list.remove(i);
                list.add(chequeBill);
            }
        }
        if (!isThere) {
            list.add(chequeBill);
        }
        
        marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 1, 81);
    }
    
}
