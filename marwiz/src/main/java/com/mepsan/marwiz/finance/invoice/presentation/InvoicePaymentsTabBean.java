/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 22.03.2018 11:26:09
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.invoice.business.IInvoicePaymentService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoicePayment;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class InvoicePaymentsTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{invoiceProcessBean}")
    public InvoiceProcessBean invoiceProcessBean;

    @ManagedProperty(value = "#{invoicePaymentService}")
    public IInvoicePaymentService invoicePaymentService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;

    @ManagedProperty(value = "#{bankBranchService}")
    public IBankBranchService bankBranchService;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{creditService}")
    private ICreditService creditService;

    private String exchange;
    private InvoicePayment selectedObject;
    private Invoice selectedInvoice;
    private List<InvoicePayment> listOfObjects;
    private List<BankBranch> listOfBankBranch;
    private List<BankAccount> listOfBankAccount;
    private List<DocumentNumber> listOfDocumentNumber;
    private List<Safe> listOfSafe;
    int processType;
    private BigDecimal sumPayments;
    private List<City> cities;
    private List<Type> paymentList;
    private String relatedRecord;
    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2;
    List<CheckDelete> controlDeleteList;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public List<BankBranch> getListOfBankBranch() {
        return listOfBankBranch;
    }

    public void setListOfBankBranch(List<BankBranch> listOfBankBranch) {
        this.listOfBankBranch = listOfBankBranch;
    }

    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public BigDecimal getSumPayments() {
        return sumPayments;
    }

    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }

    public List<DocumentNumber> getListOfDocumentNumber() {
        return listOfDocumentNumber;
    }

    public void setListOfDocumentNumber(List<DocumentNumber> listOfDocumentNumber) {
        this.listOfDocumentNumber = listOfDocumentNumber;
    }

    public void setSumPayments(BigDecimal sumPayments) {
        this.sumPayments = sumPayments;
    }

    public void setInvoicePaymentService(IInvoicePaymentService invoicePaymentService) {
        this.invoicePaymentService = invoicePaymentService;
    }

    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }

    public void setListOfBankAccount(List<BankAccount> listOfBankAccount) {
        this.listOfBankAccount = listOfBankAccount;
    }

    public List<Safe> getListOfSafe() {
        return listOfSafe;
    }

    public void setListOfSafe(List<Safe> listOfSafe) {
        this.listOfSafe = listOfSafe;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setInvoiceProcessBean(InvoiceProcessBean invoiceProcessBean) {
        this.invoiceProcessBean = invoiceProcessBean;
    }

    public Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(Invoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public InvoicePayment getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(InvoicePayment selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<InvoicePayment> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<InvoicePayment> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public List<Type> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Type> paymentList) {
        this.paymentList = paymentList;
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

    public String getRelatedRecord() {
        return relatedRecord;
    }

    public void setRelatedRecord(String relatedRecord) {
        this.relatedRecord = relatedRecord;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------------InvoicePaymentsTabBean");
        controlDeleteList = new ArrayList();
        selectedInvoice = invoiceProcessBean.getSelectedObject();
        paymentList = new ArrayList<>();
        for (Type type : sessionBean.getTypes(15)) {
            if (type.getId() == 17 || type.getId() == 18 || type.getId() == 19 || type.getId() == 66
                      || type.getId() == 69 || type.getId() == 75) {
                paymentList.add(type);
            }
        }
        listOfBankBranch = bankBranchService.selectBankBranch();
        if (selectedInvoice.getId() > 0) {
            listOfObjects = invoicePaymentService.listOfPayments(selectedInvoice);
            if (!listOfObjects.isEmpty()) {
                calculatePayments(0);
            }
        } else {
            listOfObjects = new ArrayList<>();
        }

        listOfSafe = safeService.selectSafe(selectedInvoice.getBranchSetting().getBranch());
        listOfBankAccount = new ArrayList<>();
        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(18), selectedInvoice.getBranchSetting().getBranch());//fatura için seri numarları çektik.
        cities = new ArrayList<>();

        //Satış faturası ise ve sale id bilgisi yok ise sale id getir.
        if (!selectedInvoice.isIsPurchase() && selectedInvoice.getSaleId() == 0 && selectedInvoice.getId() > 0 && !selectedInvoice.isIsPeriodInvoice()) {
            selectedInvoice.setSaleId(invoiceService.getInvoiceSaleId(selectedInvoice));
        }
        setListBtn(sessionBean.checkAuthority(new int[]{8, 9, 10}, 0));
    }

    public void createDialog(int type) {

        processType = type;
        if (type == 1) {//create 
            selectedObject = new InvoicePayment();
            selectedObject.setIsDirection(true);
            selectedObject.setInvoice(selectedInvoice);
            selectedObject.setProcessDate(new Date());
            selectedObject.getFinancingDocument().setDocumentDate(new Date());
            selectedObject.getFinancingDocument().setAccount(selectedInvoice.getAccount());
        } else {
            if (selectedObject.getType().getId() == 18) {
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedInvoice.getBranchSetting().getBranch());
            } else if (selectedObject.getType().getId() == 75) {//EFT/Havale ise ticari bankaları çek
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedInvoice.getBranchSetting().getBranch());
            }

            selectedObject.setInvoice(selectedInvoice);
            selectedObject.getFinancingDocument().setAccount(selectedInvoice.getAccount());

            selectedObject.getChequeBill().setAccount(selectedInvoice.getAccount());
            selectedObject.getChequeBill().setIsCheque(selectedObject.getType().getId() == 66);
            updateCity();
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_PaymentProcess').show()");
        RequestContext.getCurrentInstance().update("frmInvoicePaymentProcess");

    }

    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {

            int result = 0;

            if (selectedInvoice.getType().getId() == 26 && (selectedInvoice.getPriceDifferenceTotalMoney() == null || selectedInvoice.getPriceDifferenceTotalMoney().doubleValue() <= 0)) {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.loc.getString("warning"));
                message.setDetail(sessionBean.loc.getString("firstaddtheproducttotheinvoice"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");
            } else if (selectedInvoice.getType().getId() != 26 && (selectedInvoice.getTotalMoney() == null || selectedInvoice.getTotalMoney().doubleValue() <= 0)) {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.loc.getString("warning"));
                message.setDetail(sessionBean.loc.getString("firstaddtheproducttotheinvoice"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");
            } else {

                if (selectedInvoice.getRemainingMoney() == null) {//fatura ilk eklendi ise
                    if (selectedInvoice.getType().getId() == 26) {
                        selectedInvoice.setRemainingMoney(selectedInvoice.getPriceDifferenceTotalMoney());
                    } else {
                        selectedInvoice.setRemainingMoney(selectedInvoice.getTotalMoney());
                    }

                }

                calculatePayments(0);
                BigDecimal tempSum = new BigDecimal(0.0);
                tempSum = sumPayments;
                if (processType == 1) {
                    if (tempSum != null) {
                        tempSum = tempSum.add(selectedObject.getPrice().multiply(selectedObject.getExchangeRate()));
                    }
                }

                if (selectedInvoice.getType().getId() == 26 && selectedInvoice.getPriceDifferenceTotalMoney().add(BigDecimal.ONE).doubleValue() < (tempSum).doubleValue()) {
                    //uyerı ver
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.loc.getString("warning"));
                    message.setDetail(sessionBean.loc.getString("theamountofpaymentcannotbegreaterthantheremainingamountoftheinvoice"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext context = RequestContext.getCurrentInstance();
                    context.update("grwProcessMessage");
                    return;
                } //fatura kalan tutardan +1 fazla ödeyebilir. küsüratı kurtarmak için.
                else if (selectedInvoice.getTotalMoney().add(BigDecimal.ONE).doubleValue() < (tempSum).doubleValue()) {
                    //uyerı ver
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.loc.getString("warning"));
                    message.setDetail(sessionBean.loc.getString("theamountofpaymentcannotbegreaterthantheremainingamountoftheinvoice"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext context = RequestContext.getCurrentInstance();
                    context.update("grwProcessMessage");
                    return;
                }

                selectedObject.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());

                if (processType == 1) {

                    result = invoicePaymentService.create(selectedObject);
                    if (result > 0) {
                        selectedObject.setId(result);
                    }
                } else if (processType == 2) {
                    result = invoicePaymentService.update(selectedObject);
                }

                if (result > 0) {
                    selectedInvoice.setRemainingMoney(selectedInvoice.getRemainingMoney().subtract(selectedObject.getPrice().multiply(selectedObject.getExchangeRate())));
                    listOfObjects = invoicePaymentService.listOfPayments(selectedInvoice);
                    calculatePayments(1);

                    if (!listOfObjects.isEmpty()) {
                        if (listOfObjects.size() == 1) {
                            selectedInvoice.setIsPayment(true);
                            RequestContext.getCurrentInstance().update("frmInvoiceProcess");
                        }
                    }

                    RequestContext.getCurrentInstance().execute("PF('dlg_PaymentProcess').hide()");

                    if (!selectedInvoice.isIsPurchase()) {//satış ise merkeze gönder
                        invoiceProcessBean.setIsSendCenter(true);
                    }
                } else if (result == -101) {//
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.loc.getString("warning"));
                    message.setDetail(sessionBean.loc.getString("creditlimitisinsufficient"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext context = RequestContext.getCurrentInstance();
                    context.update("grwProcessMessage");
                } else if (result == -106) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    sessionBean.createUpdateMessage(result);
                }

                RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoicePaymentsTab:dtbPayments");
                RequestContext.getCurrentInstance().update("frmInvoiceProcess:grdPaymentProcess");
            }
        }
    }

    /**
     * bu metot stokun currency bilgisi ile faturaın currency bilgisi arasında
     * kuru getirir
     */
    public void calcExchangeRate() {
        selectedObject.setExchangeRate(exchangeService.bringExchangeRate(selectedObject.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
        exchange = sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0) + " -> " + sessionBean.currencySignOrCode(selectedInvoice.getCurrency().getId(), 0);// örn: $->€

    }

    /**
     * işlem tipi değiştiğinde tetiklenir.
     */
    public void changeType() {

        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());

        //ödeme tipi çek seçildi ise ve satış faturası ise
        if (selectedObject.getType().getId() == 66 || selectedObject.getType().getId() == 69) {
            selectedObject.getChequeBill().getStatus().setId(31);
            selectedObject.getChequeBill().setExpiryDate(new Date());
            selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
            selectedObject.getChequeBill().setAccount(selectedInvoice.getAccount());
            selectedObject.getChequeBill().setIsCheque(selectedObject.getType().getId() == 66);
            calcExchangeRate();
            selectedObject.getChequeBill().getCountry().setId(Integer.parseInt(applicationBean.getParameterMap().get("default_country").getValue()));
            if (selectedObject.getChequeBill().getCountry().getId() > 0) {
                updateCity();
            }
        } else {
            selectedObject.getChequeBill().setIsCheque(false);
        }
        //kredi kartı ise kredi bankaları çek
        if (selectedObject.getType().getId() == 18) {
            listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedInvoice.getBranchSetting().getBranch());
        } else if (selectedObject.getType().getId() == 75) {//EFT/Havale ise ticari bankaları çek
            listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedInvoice.getBranchSetting().getBranch());
        }
        bringCurrency();

        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
    }

    /**
     * bu metot para cıkısı olan birimin currency objesini kuru hesaplamak için
     * bulur.
     *
     * tarafın dövizini hesapla 2:iki tarafın dövizini hesapla
     */
    public void bringCurrency() {

        Currency inCurrency = selectedInvoice.getCurrency();
        Currency outCurrency = selectedObject.getCurrency();
        switch (selectedObject.getType().getId()) {
            case 17://nakit kasa seçildi
                for (Safe s : listOfSafe) {
                    if (s.getId() == selectedObject.getSafe().getId()) {
                        outCurrency = s.getCurrency();
                        break;
                    }
                }
                break;
            case 18://kredi kartı banka seçildi
            case 75:
                for (BankAccount b : listOfBankAccount) {
                    if (b.getId() == selectedObject.getBankAccount().getId()) {
                        outCurrency = b.getCurrency();
                        break;
                    }
                }
                break;
        }

        if (inCurrency != null && outCurrency != null) {
            selectedObject.setCurrency(outCurrency);
            selectedObject.setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance().update("frmInvoicePaymentProcess:grdPaymentProcess");
    }

    /**
     * Yapılan ödemelerin toplamını hesaplar
     *
     * @param type 0 ise ilk açılşta çalışır, 1 ise ödeme eklendiğinde
     */
    public void calculatePayments(int type) {

        sumPayments = BigDecimal.ZERO;

        listOfObjects.stream().forEach((payment) -> {
            //if (payment.isIsDirection()) {
            sumPayments = sumPayments.add(payment.getPrice().multiply(payment.getExchangeRate()));
            System.out.println("---payment.getPrice().multiply(payment.getExchangeRate())" + payment.getPrice().multiply(payment.getExchangeRate()));
            // }
        });

        if ((selectedInvoice.getTotalMoney().doubleValue() <= sumPayments.doubleValue() && type == 1) || (selectedInvoice.getType().getId()==26 && selectedInvoice.getPriceDifferenceTotalMoney().doubleValue() <= sumPayments.doubleValue() && type == 1)) {//ödeme eklendi ve tutar kanapdı ise
            selectedInvoice.getStatus().setId(29);//kapalıya çek.
            RequestContext.getCurrentInstance().update("frmInvoiceProcess");
            RequestContext.getCurrentInstance().update("frmInvoiceProcess");
            RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab");
            RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoicePaymentsTab");
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

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {
            deleteControlMessage = "";
            deleteControlMessage1 = "";
            deleteControlMessage2 = "";
            relatedRecord = "";
            controlDeleteList.clear();
            controlDeleteList = invoicePaymentService.testBeforeDelete(selectedObject);
            if (!controlDeleteList.isEmpty()) {
                if (controlDeleteList.get(0).getR_response() < 0) { //Var bağlı ise silme uyarı ver
                    switch (controlDeleteList.get(0).getR_response()) {
                        case -100:
                            deleteControlMessage = sessionBean.getLoc().getString("paymentdelete");
                            deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                            deleteControlMessage2 = sessionBean.getLoc().getString("portfoliono") + " : ";
                            relatedRecord = selectedObject.getChequeBill().getPortfolioNumber();
                            break;
                        case -101:
                            deleteControlMessage = sessionBean.getLoc().getString("paymentdelete");
                            deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                            deleteControlMessage2 = sessionBean.getLoc().getString("customername") + " - " + sessionBean.getLoc().getString("paymentdate") + " : ";
                            relatedRecord = selectedInvoice.getAccount().getName() + " - " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getCredit().getDueDate());
                            break;
                        default:
                            break;
                    }
                    if (controlDeleteList.get(0).getR_response() == -102) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                    }
                } else {//Sil
                    RequestContext.getCurrentInstance().update("frmInvoicePaymentProcess:dlgDelete");
                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                }
            }
        }

    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {

            RequestContext context = RequestContext.getCurrentInstance();
            int result = invoicePaymentService.delete(selectedObject);
            if (result > 0) {
                selectedInvoice.setRemainingMoney(selectedInvoice.getRemainingMoney().add(selectedObject.getPrice().multiply(selectedObject.getExchangeRate())));
                listOfObjects.remove(selectedObject);
                selectedInvoice.getStatus().setId(28);//açık yaptık
                sumPayments = BigDecimal.ZERO;

                if (!listOfObjects.isEmpty()) {
                    listOfObjects.stream().forEach((payment) -> {
                        sumPayments = sumPayments.add(payment.getPrice().multiply(payment.getExchangeRate()));
                    });
                }
                if (listOfObjects.isEmpty()) {
                    selectedInvoice.setIsPayment(false);
                    RequestContext.getCurrentInstance().update("frmInvoiceProcess");
                }

                context.update("frmInvoiceProcess");
                context.update("tbvInvoice:frmInvoiceStokTab");
                context.update("tbvInvoice:frmInvoicePaymentsTab");
                context.execute("PF('dlg_PaymentProcess').hide()");
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    /**
     * Bu metot ödemeyi silmek istediğinde bağlı kayıt var hatası alır ise bağlı
     * kayda gder
     */
    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_PaymentProcess').hide();");
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("goToRelatedRecord();");

    }

    /**
     * Bu metot ödeme detayına dialogdan direk gider.
     */
    public void goToDetail() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_PaymentProcess').hide();");
        context.execute("goToRelatedRecord();");

    }

    public void goToRelatedRecord() {
        List<Object> list = new ArrayList<>();
        if (selectedObject.getType().getId() == 19) {//Kredi
            CreditReport creditReport = new CreditReport();
            creditReport.setId(selectedObject.getCredit().getId());
            creditReport = creditService.findCreditReport(creditReport);
            for (Object object : (ArrayList) sessionBean.parameter) {
                list.add(object);
            }
            boolean isThere = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof CreditReport) {
                    isThere = true;
                    list.remove(i);
                    list.add(creditReport);
                }
            }
            if (!isThere) {
                list.add(creditReport);
            }
            marwiz.goToPage("/pages/finance/credit/creditprocess.xhtml", list, 1, 79);

        } else {
            ChequeBill chequeBill = new ChequeBill();
            chequeBill.setId(selectedObject.getChequeBill().getId());
            chequeBill = chequeBillService.findChequeBill(chequeBill);

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
}
