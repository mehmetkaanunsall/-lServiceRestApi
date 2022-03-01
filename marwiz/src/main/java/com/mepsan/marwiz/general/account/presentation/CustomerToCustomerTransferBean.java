/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:19:10 PM
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.account.dao.TransferCustomerToCustomer;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class CustomerToCustomerTransferBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private TransferCustomerToCustomer customerToCustomer;
    private List<BankAccount> listOfBankAccount;
    private List<Safe> listOfSafe;
    private int processType;
    private String exchange;
    private Currency outCurrency, inCurrency;
    private int firstId, secondId;
    private List<Type> listOfType;
    private List<Branch> listOfBranch;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public TransferCustomerToCustomer getCustomerToCustomer() {
        return customerToCustomer;
    }

    public void setCustomerToCustomer(TransferCustomerToCustomer customerToCustomer) {
        this.customerToCustomer = customerToCustomer;
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

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
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

    @PostConstruct
    public void init() {
        System.out.println("---------CustomerToCustomerTransferBean-----");

        customerToCustomer = new TransferCustomerToCustomer();
        listOfBranch = new ArrayList<>();
        listOfBranch = branchService.findUserAuthorizeBranch();

        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                customerToCustomer.getBranch().setId(b.getId());
                break;
            }
        }
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    customerToCustomer.setOutAccount((Account) ((ArrayList) sessionBean.parameter).get(i));
                }
            }
        }
        listOfType = new ArrayList<>();
        for (Type type : sessionBean.getTypes(20)) { // nakit ödeme - nakit tahsilat - kreid kartı tahsilat - kredi kartı ödeme
            if (type.getId() == 47 || type.getId() == 48 || type.getId() == 73 || type.getId() == 74 || type.getId() == 55 || type.getId() == 56) {
                listOfType.add(type);
            }
        }

        processType = 1;
        inCurrency = new Currency();
        outCurrency = new Currency();
        customerToCustomer.setDocumentDate(new Date());
    }

    public void resetFinancing() {
        customerToCustomer.getInAccount().setId(0);
        customerToCustomer.getInAccount().setName("");
        customerToCustomer.setInMovementId(0);
        customerToCustomer.setOutMovementId(0);
        customerToCustomer.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        customerToCustomer.setDocumentDate(new Date());
        inCurrency.setId(0);
        outCurrency.setId(0);
        firstId = 0;
        secondId = 0;
        exchange = "";
        customerToCustomer.setPrice(BigDecimal.ZERO);
        customerToCustomer.setDescription(null);
        changeBranch();

        RequestContext.getCurrentInstance().update("frmFinancingDocumentAccountProcess:txtOutAccount");
        customerToCustomer.setExchangeRate(BigDecimal.ONE);

        bringCurrency();
        RequestContext.getCurrentInstance().update("frmFinancingDocumentAccountProcess:pgrFinancingDocumentProcess");

    }

    /*
    * cari kitabına cıft tıkladıgımızda calısır
     */
    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null) {
            customerToCustomer.setInAccount(accountBookFilterBean.getSelectedData());
            accountBookFilterBean.setSelectedData(null);
        }
        switch (customerToCustomer.getFinancingType().getId()) {
            case 47://cari->kasa
                firstId = customerToCustomer.getOutAccount().getId();
                break;
            case 48://kasa->cari
                secondId = customerToCustomer.getOutAccount().getId();
                break;
            case 73: // cari - > banka
                firstId = customerToCustomer.getOutAccount().getId();
                break;
            case 74: // banka -> cari
                secondId = customerToCustomer.getInAccount().getId();
                break;
            case 55: // cari - > banka
                firstId = customerToCustomer.getOutAccount().getId();
                break;
            case 56: // banka -> cari
                secondId = customerToCustomer.getInAccount().getId();
                break;
            default:
                break;
        }
        RequestContext.getCurrentInstance().update("frmFinancingDocumentAccountProcess:pgrFinancingDocumentProcess");
        RequestContext.getCurrentInstance().update("frmFinancingDocumentAccountProcess:txtOutAccount");
    }

    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (sessionBean.isPeriodClosed(customerToCustomer.getDocumentDate())) {
            if (customerToCustomer.getInAccount().getId() != customerToCustomer.getOutAccount().getId()) {
                int result = 0;
                boolean isThere = false;
                if (customerToCustomer.getPrice().multiply(customerToCustomer.getExchangeRate()).compareTo(BigDecimal.ZERO) != 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isThere = true;
                }
                if (!isThere) {
                    FinancingDocument financingDocument = new FinancingDocument();
                    financingDocument.getFinancingType().setId(customerToCustomer.getFinancingType().getId());
                    financingDocument.setPrice(customerToCustomer.getPrice());
                    financingDocument.setAccount(customerToCustomer.getOutAccount());
                    financingDocument.setExchangeRate(customerToCustomer.getExchangeRate());
                    financingDocument.setTotal(customerToCustomer.getPrice().multiply(customerToCustomer.getExchangeRate()));
                    financingDocument.setDocumentNumber(customerToCustomer.getDocumentNumber());
                    financingDocument.setDocumentDate(customerToCustomer.getDocumentDate());
                    financingDocument.getCurrency().setId(customerToCustomer.getCurrency().getId());
                    financingDocument.setDescription(customerToCustomer.getDescription());
                    financingDocument.getBranch().setId(customerToCustomer.getBranch().getId());

                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    AccountMovementTabBean accountMovementTabBean = (AccountMovementTabBean) viewMap.get("accountMovementTabBean");

                    switch (financingDocument.getFinancingType().getId()) {
                        case 47:
                            secondId = customerToCustomer.getOutMovementId();
                            result = financingDocumentService.create(financingDocument, firstId, secondId);
                            if (result > 0) {
                                financingDocument.getFinancingType().setId(48);
                                firstId = customerToCustomer.getOutMovementId();
                                secondId = customerToCustomer.getInAccount().getId();
                                int resultVal = financingDocumentService.create(financingDocument, firstId, secondId);
                                if (resultVal > 0) {
                                    accountMovementTabBean.findall(" ");
                                    RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                                    RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').hide();");
                                } else if (result == -101) {
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                }
                            }
                            break;
                        case 48:
                            firstId = customerToCustomer.getOutMovementId();
                            secondId = customerToCustomer.getOutAccount().getId();
                            result = financingDocumentService.create(financingDocument, firstId, secondId);
                            if (result > 0) {
                                financingDocument.getFinancingType().setId(47);
                                firstId = customerToCustomer.getInAccount().getId();
                                secondId = customerToCustomer.getOutMovementId();
                                int resultVal = financingDocumentService.create(financingDocument, firstId, secondId);
                                if (resultVal > 0) {
                                    accountMovementTabBean.findall(" ");
                                    RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                                    RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').hide();");
                                }
                            } else if (result == -101) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");
                            }
                            break;
                        case 73:
                            secondId = customerToCustomer.getOutMovementId();
                            result = financingDocumentService.create(financingDocument, firstId, secondId);
                            if (result > 0) {
                                financingDocument.getFinancingType().setId(74);
                                firstId = customerToCustomer.getOutMovementId();
                                secondId = customerToCustomer.getInAccount().getId();
                                int resultVal = financingDocumentService.create(financingDocument, firstId, secondId);
                                if (resultVal > 0) {
                                    accountMovementTabBean.findall(" ");
                                    RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                                    RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').hide();");
                                }
                            }
                            break;
                        case 74:
                            firstId = customerToCustomer.getOutMovementId();
                            secondId = customerToCustomer.getOutAccount().getId();
                            result = financingDocumentService.create(financingDocument, firstId, secondId);
                            if (result > 0) {
                                financingDocument.getFinancingType().setId(73);
                                firstId = customerToCustomer.getInAccount().getId();
                                secondId = customerToCustomer.getOutMovementId();
                                int resultVal = financingDocumentService.create(financingDocument, firstId, secondId);
                                if (resultVal > 0) {
                                    accountMovementTabBean.findall(" ");
                                    RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                                    RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').hide();");
                                }
                            }
                            break;
                        case 55:
                            secondId = customerToCustomer.getOutMovementId();
                            result = financingDocumentService.create(financingDocument, firstId, secondId);
                            if (result > 0) {
                                financingDocument.getFinancingType().setId(56);
                                firstId = customerToCustomer.getOutMovementId();
                                secondId = customerToCustomer.getInAccount().getId();
                                int resultVal = financingDocumentService.create(financingDocument, firstId, secondId);
                                if (resultVal > 0) {
                                    accountMovementTabBean.findall(" ");
                                    RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                                    RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').hide();");
                                }
                            }
                            break;
                        case 56:
                            firstId = customerToCustomer.getOutMovementId();
                            secondId = customerToCustomer.getOutAccount().getId();
                            result = financingDocumentService.create(financingDocument, firstId, secondId);
                            if (result > 0) {
                                financingDocument.getFinancingType().setId(55);
                                firstId = customerToCustomer.getInAccount().getId();
                                secondId = customerToCustomer.getOutMovementId();
                                int resultVal = financingDocumentService.create(financingDocument, firstId, secondId);
                                if (resultVal > 0) {
                                    accountMovementTabBean.findall(" ");
                                    RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                                    RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').hide();");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("notransfertothesamecustomer"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("grwProcessMessage");
            }
        }
    }

    public void bringTempCurrency() {
        switch (customerToCustomer.getFinancingType().getId()) {
            case 47://cari->kasa
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == customerToCustomer.getOutMovementId()) {
                        outCurrency.setId(safe.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 48://kasa->cari
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == customerToCustomer.getOutMovementId()) {
                        outCurrency.setId(safe.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 73: // banka - cari
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == customerToCustomer.getOutMovementId()) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 74:
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == customerToCustomer.getOutMovementId()) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 55:
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == customerToCustomer.getOutMovementId()) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 56:
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == customerToCustomer.getOutMovementId()) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            default:
                break;
        }
    }

    public void bringCurrency() {

        bringTempCurrency();
        if (inCurrency != null && outCurrency != null) {
            customerToCustomer.setCurrency(outCurrency);
            customerToCustomer.setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }
        RequestContext.getCurrentInstance().update("frmFinancingDocumentAccountProcess:pgrFinancingDocumentProcess");
    }

    public void changeBranch() {
        customerToCustomer.getInAccount().setId(0);
        customerToCustomer.getInAccount().setName("");
        customerToCustomer.getInAccount().setTitle("");
        switch (customerToCustomer.getFinancingType().getId()) {
            case 73:
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", customerToCustomer.getBranch());
                break;
            case 74:
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", customerToCustomer.getBranch());
                break;
            case 47:
                listOfSafe = safeService.selectSafe(customerToCustomer.getBranch());
                break;
            case 48:
                listOfSafe = safeService.selectSafe(customerToCustomer.getBranch());
                break;
            case 55://cari->banka
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", customerToCustomer.getBranch());
                break;
            case 56://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", customerToCustomer.getBranch());
                break;
        }
    }
}
