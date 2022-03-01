/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountCommissionService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseMovementService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class IncomeExpenseMovementTabBean extends GeneralReportBean<IncomeExpenseMovement> {

    private Date beginDate, endDate;
    private IncomeExpense incomeExpense;
    List<IncomeExpenseMovement> listOfTotals;
    private List<Safe> listOfSafe;
    private List<BankAccount> listOfBankAccount;
    private List<Type> listOfType;
    private int processType;
    private String bookType;
    private int firstId, secondId;
    private Currency outCurrency, inCurrency;
    private String exchange;
    private boolean isIncomeExpense;
    private String beanName;
    private boolean isUpdate;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private int response;
    private List<Branch> listOfBranch;
    private Branch branch;
    private BankAccountCommission selectedBankAccountCommission;
    private String commissionMessage;

    @ManagedProperty(value = "#{incomeExpenseMovementService}")
    public IIncomeExpenseMovementService incomeExpenseMovementService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{incomeExpenseService}")
    public IIncomeExpenseService incomeExpenseService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{bankAccountCommissionService}")
    public IBankAccountCommissionService bankAccountCommissionService;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    public void setIncomeExpenseMovementService(IIncomeExpenseMovementService incomeExpenseMovementService) {
        this.incomeExpenseMovementService = incomeExpenseMovementService;
    }

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

    public void setIncomeExpenseService(IIncomeExpenseService incomeExpenseService) {
        this.incomeExpenseService = incomeExpenseService;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public IncomeExpense getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(IncomeExpense incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public List<IncomeExpenseMovement> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<IncomeExpenseMovement> listOfTotals) {
        this.listOfTotals = listOfTotals;
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

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
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

    public boolean isIsIncomeExpense() {
        return isIncomeExpense;
    }

    public void setIsIncomeExpense(boolean isIncomeExpense) {
        this.isIncomeExpense = isIncomeExpense;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
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

    public int getRelatedRecordId() {
        return relatedRecordId;
    }

    public void setRelatedRecordId(int relatedRecordId) {
        this.relatedRecordId = relatedRecordId;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public void setBankAccountCommissionService(IBankAccountCommissionService bankAccountCommissionService) {
        this.bankAccountCommissionService = bankAccountCommissionService;
    }

    public String getCommissionMessage() {
        return commissionMessage;
    }

    public void setCommissionMessage(String commissionMessage) {
        this.commissionMessage = commissionMessage;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    
    

    @PostConstruct
    @Override
    public void init() {

        System.out.println("----------------------IncomeExpenseMovementTabBean");
        Calendar calendar = GregorianCalendar.getInstance();
        incomeExpense = new IncomeExpense();
        listOfBranch = new ArrayList<>();
        selectedBankAccountCommission = new BankAccountCommission();
        setEndDate(new Date());
        calendar.setTime(getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        setEndDate(calendar.getTime());

        calendar.setTime(getEndDate());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        setBeginDate(calendar.getTime());

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomeExpenseProcessBean incomeExpenseProcessBean = (IncomeExpenseProcessBean) viewMap.get("incomeExpenseProcessBean");
        if (incomeExpenseProcessBean != null) {
            incomeExpense = incomeExpenseProcessBean.getSelectedObject();
        }

        find();
        toogleList = Arrays.asList(true, true, true, true, true);
        selectedObject = new IncomeExpenseMovement();
        listOfSafe = new ArrayList<>();
        listOfBankAccount = new ArrayList<>();
        listOfType = new ArrayList<>();
        inCurrency = new Currency();
        outCurrency = new Currency();
        isIncomeExpense = true;
        controlDeleteList = new ArrayList<>();

        setListBtn(sessionBean.checkAuthority(new int[]{161, 162, 163, 266}, 0));
    }

    @Override
    public void find() {
        isFind = true;
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMovementDataTable:dtbMovement");

        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(" ");
    }

    @Override
    public void create() {
        processType = 1;
        listOfType.clear();
        isUpdate = true;
        listOfSafe = safeService.selectSafe();

        for (Type type : sessionBean.getTypes(20)) {
            if (incomeExpense.isIsIncome()) { //gelir ise
                if (type.getId() == 47 || type.getId() == 50 || type.getId() == 55 || type.getId() == 73) {
                    listOfType.add(type);
                }
            } else if (type.getId() == 48 || type.getId() == 49 || type.getId() == 56 || type.getId() == 74) {
                listOfType.add(type);
            }
        }

        isIncomeExpense = true;
        selectedObject = new IncomeExpenseMovement();
        resetFinancing();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_financingdocumentproc').show();");
        context.update("frmFinancingDocumentProcess");
    }

    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        if (processType == 1) {
            if (sessionBean.isPeriodClosed(selectedObject.getFinancingDocument().getDocumentDate())) {
                if (selectedObject.getFinancingDocument().getPrice().multiply(selectedObject.getFinancingDocument().getExchangeRate()).compareTo(BigDecimal.ZERO) != 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    selectedObject.getFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
                    result = financingDocumentService.create(selectedObject.getFinancingDocument(), firstId, secondId);
                    if (result > 0) {
                        selectedObject.getFinancingDocument().setId(result);
                        listOfObjects = findall(" ");
                        context.execute("PF('dlg_financingdocumentproc').hide();");
                        context.update("tbvMovement:frmMovementDataTable:dtbMovement");
                        if (incomeExpense.isIsIncome()) {
                            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                            IncomeBean incomeBean = (IncomeBean) viewMap.get("incomeBean");
                            if (incomeBean != null) {
                                incomeBean.setListOfIncomeExpense(incomeExpenseService.selectIncomeExpense(true));
                                incomeBean.createTree();
                                RequestContext.getCurrentInstance().update("frmIncome:dtbIncome");
                            }
                        } else if (!incomeExpense.isIsIncome()) {
                            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                            ExpenseBean expenseBean = (ExpenseBean) viewMap.get("expenseBean");
                            if (expenseBean != null) {
                                expenseBean.setListOfIncomeExpense(incomeExpenseService.selectIncomeExpense(false));
                                expenseBean.createTree();
                                RequestContext.getCurrentInstance().update("frmExpense:dtbExpense");
                            }
                        }
                    }
                    if (result == -101) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        sessionBean.createUpdateMessage(result);
                    }
                }

            }
        } else if (processType == 2) {//Finansman belgesi güncelleme
            if (sessionBean.isPeriodClosed(selectedObject.getFinancingDocument().getDocumentDate())) {
                if (selectedObject.getFinancingDocument().getBankAccountCommissionId() != 0) {//Komisyon tablosunda var ise
                    commissionMessage = sessionBean.getLoc().getString("ifthismovementisupdatedcommissionmovementrelatedtothisrecordwillbeupdated");
                    commissionMessage = commissionMessage + " " + sessionBean.getLoc().getString("areyousure");
                    RequestContext.getCurrentInstance().update("dlgCommissionWarning");
                    RequestContext.getCurrentInstance().execute("PF('dlg_CommissionWarning').show();");
                } else {
                    result = financingDocumentService.update(selectedObject.getFinancingDocument(), firstId, secondId);
                    if (result > 0) {
                        listOfObjects = findall(" ");
                        context.execute("PF('dlg_financingdocumentproc').hide();");
                        context.update("tbvMovement:frmMovementDataTable:dtbMovement");
                        if (incomeExpense.isIsIncome()) {
                            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                            IncomeBean incomeBean = (IncomeBean) viewMap.get("incomeBean");
                            if (incomeBean != null) {
                                incomeBean.setListOfIncomeExpense(incomeExpenseService.selectIncomeExpense(true));
                                incomeBean.createTree();
                                RequestContext.getCurrentInstance().update("frmIncome:dtbIncome");
                            }
                        } else if (!incomeExpense.isIsIncome()) {
                            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                            ExpenseBean expenseBean = (ExpenseBean) viewMap.get("expenseBean");
                            if (expenseBean != null) {
                                expenseBean.setListOfIncomeExpense(incomeExpenseService.selectIncomeExpense(false));
                                expenseBean.createTree();
                                RequestContext.getCurrentInstance().update("frmExpense:dtbExpense");
                            }
                        }
                    }
                    if (result == -101) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        sessionBean.createUpdateMessage(result);
                    }
                }

            }
        } else if (processType == 3) {//Devir bakiyesi güncelleme
            result = incomeExpenseMovementService.update(selectedObject);
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('dlg_TransferBalance').hide()");

                RequestContext.getCurrentInstance().update("tbvMovement:frmMovementDataTable:dtbMovement");
                if (incomeExpense.isIsIncome()) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    IncomeBean incomeBean = (IncomeBean) viewMap.get("incomeBean");
                    if (incomeBean != null) {
                        incomeBean.setListOfIncomeExpense(incomeExpenseService.selectIncomeExpense(true));
                        incomeBean.createTree();
                        RequestContext.getCurrentInstance().update("frmIncome:dtbIncome");
                    }
                } else if (!incomeExpense.isIsIncome()) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    ExpenseBean expenseBean = (ExpenseBean) viewMap.get("expenseBean");
                    if (expenseBean != null) {
                        expenseBean.setListOfIncomeExpense(incomeExpenseService.selectIncomeExpense(false));
                        expenseBean.createTree();
                        RequestContext.getCurrentInstance().update("frmExpense:dtbExpense");
                    }
                }

            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public void getfinancingDocument() {
        listOfType.clear();
        if (selectedObject.getFinancingDocument().getId() > 0) {
            processType = 2;
            listOfSafe = safeService.selectSafe();
            ////Bankayı tipe göre Çektik
            switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
                case 55://cari->banka
                    listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", sessionBean.getUser().getLastBranch());
                    break;
                case 56://banka->cari
                    listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", sessionBean.getUser().getLastBranch());
                    break;
                case 73:
                    listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", sessionBean.getUser().getLastBranch());
                    break;
                case 74:
                    listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", sessionBean.getUser().getLastBranch());
                    break;
                default:
                    break;
            }
            ///

            for (Type type : sessionBean.getTypes(20)) {
                if (type.getId() == 47 || type.getId() == 48 || type.getId() == 49 || type.getId() == 50 || type.getId() == 55 || type.getId() == 56 || type.getId() == 73 || type.getId() == 74) {
                    listOfType.add(type);
                }
            }

            selectedObject.setFinancingDocument(financingDocumentService.findFinancingDocument(selectedObject.getFinancingDocument()));
            //Update için kontrol yapıldı
            controlDeleteList.clear();
            controlDeleteList = financingDocumentService.testBeforeDelete(selectedObject.getFinancingDocument());
            if (!controlDeleteList.isEmpty()) {
                response = controlDeleteList.get(0).getR_response();
                relatedRecordId = controlDeleteList.get(0).getR_record_id();
                if (response < 0 && response != -108) {//Bağlı kayıt var git güncelle
                    isUpdate = false;
                } else {//Bağlı kayıt yok direk güncelle
                    isUpdate = true;
                }
            }
            /////
            bringFinancingDocument(selectedObject.getFinancingDocument());
            bringTempCurrency();
            if (inCurrency != null && outCurrency != null) {
                selectedObject.getFinancingDocument().setCurrency(outCurrency);
                exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
            } else {
                exchange = "";
            }

            RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_financingdocumentproc').show();");
            context.update("frmFinancingDocumentProcess");
        } else {
            processType = 3;
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("dlgTransferBalance");
            context.execute("PF('dlg_TransferBalance').show();");
        }
    }

    /**
     * bu metot gelen finansman belgesinin işlem tipine göre; hangi
     * birimden(kasa,banka,cari) hangi birime para transferi sağlandığını
     * ayrıştırır. firstId: ye para çıkışı olan birimin id sini atar, secondId:
     * ye para girişi olan birimin id sini atar.
     *
     * @param financingDocument finansman belgesi
     */
    public void bringFinancingDocument(FinancingDocument financingDocument) {

        switch (financingDocument.getFinancingType().getId()) {
            case 47://cari->kasa
                firstId = financingDocument.getIncomeExpense().getId();
                secondId = financingDocument.getInMovementId();
                break;
            case 48://kasa->cari
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getIncomeExpense().getId();
                break;
            case 49://borc dekontu
                firstId = financingDocument.getIncomeExpense().getId();
                break;
            case 50://alacak dekontu
                secondId = financingDocument.getIncomeExpense().getId();
                break;
            case 55://cari->banka
                firstId = financingDocument.getIncomeExpense().getId();
                secondId = financingDocument.getInMovementId();
                break;
            case 56://banka->cari
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getIncomeExpense().getId();
                break;
            case 73://cari->banka
                firstId = financingDocument.getIncomeExpense().getId();
                secondId = financingDocument.getInMovementId();
                break;
            case 74://banka->cari
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getIncomeExpense().getId();
                break;
            default:
                break;

        }
    }

    public void bringTempCurrency() {
        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == secondId) {
                        outCurrency.setId(safe.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 48://kasa->cari
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == firstId) {
                        outCurrency.setId(safe.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 49://borc
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                outCurrency.setId(selectedObject.getFinancingDocument().getCurrency().getId());
                break;
            case 50://alacak
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                outCurrency.setId(selectedObject.getFinancingDocument().getCurrency().getId());
                break;
            case 55://cari->banka
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 56://banka->cari
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 73://cari->banka
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 74://banka->cari
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
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

    /**
     * bu metot para cıkısı olan birimin currency objesini kuru hesaplamak için
     * bulur.
     *
     * tarafın dövizini hesapla 2:iki tarafın dövizini hesapla
     */
    public void bringCurrency() {

        bringTempCurrency();

        if (inCurrency != null && outCurrency != null) {
            selectedObject.getFinancingDocument().getCurrency().setId(outCurrency.getId());
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
            selectedObject.getFinancingDocument().setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
    }

    /**
     * işlem tipi değiştiğinde tetiklenir.
     */
    public void resetFinancing() {

        firstId = 0;
        secondId = 0;
        selectedObject.getFinancingDocument().getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        selectedObject.getFinancingDocument().setDocumentDate(new Date());
        inCurrency.setId(0);
        outCurrency.setId(0);
        exchange = "";
        selectedObject.getFinancingDocument().setAccount(new Account());
        selectedObject.getFinancingDocument().setIncomeExpense(incomeExpense);
        selectedObject.getFinancingDocument().setExchangeRate(BigDecimal.ONE);

        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
                bookType = "Musteri,MTedatikçi";
                firstId = incomeExpense.getId();
                break;
            case 48://kasa->cari
                bookType = "Tedarikçi,MTedatikçi";
                secondId = incomeExpense.getId();
                break;
            case 49://cari
                firstId = incomeExpense.getId();
                break;
            case 50://cari
                secondId = incomeExpense.getId();
                break;
            case 55://cari->banka
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", sessionBean.getUser().getLastBranch());
                bookType = "Musteri,MTedatikçi";
                firstId = incomeExpense.getId();
                break;
            case 56://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", sessionBean.getUser().getLastBranch());
                bookType = "Tedarikçi,MTedatikçi";
                secondId = incomeExpense.getId();
                break;
            case 73://cari->banka
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", sessionBean.getUser().getLastBranch());
                bookType = "Musteri,MTedatikçi";
                firstId = incomeExpense.getId();
                break;
            case 74://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", sessionBean.getUser().getLastBranch());
                bookType = "Tedarikçi,MTedatikçi";
                secondId = incomeExpense.getId();
                break;
            default:
                break;
        }
        bringCurrency();
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

    }

    @Override
    public LazyDataModel<IncomeExpenseMovement> findall(String where) {
        return new CentrowizLazyDataModel<IncomeExpenseMovement>() {
            @Override
            public List<IncomeExpenseMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<IncomeExpenseMovement> result = incomeExpenseMovementService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, incomeExpense, beginDate, endDate);
                listOfTotals = incomeExpenseMovementService.totals(where, incomeExpense, beginDate, endDate);
                int count = 0;
                for (IncomeExpenseMovement total : listOfTotals) {
                    count = count + total.getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getFinancingDocument().getDocumentDate())) {
            if (selectedObject.getFinancingDocument().getBankAccountCommissionId() != 0) {//Komisyon tablosunda var ise
                commissionMessage = sessionBean.getLoc().getString("ifthismovementisdeletedcommissionmovementrelatedtothisrecordwillbedeleted");
                commissionMessage = commissionMessage + " " + sessionBean.getLoc().getString("areyousure");
                RequestContext.getCurrentInstance().update("dlgCommissionDelete");
                RequestContext.getCurrentInstance().execute("PF('dlg_CommissionDelete').show();");
            } else {
                deleteControlMessage = "";
                deleteControlMessage1 = "";
                deleteControlMessage2 = "";
                relatedRecord = "";
                response = 0;

                if (!controlDeleteList.isEmpty()) {
                    response = controlDeleteList.get(0).getR_response();
                    if (response < 0) { //Var bağlı ise silme uyarı ver
                        switch (response) {
                            case -101:
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtomarketshift");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfrommarketshift");
                                deleteControlMessage2 = sessionBean.getLoc().getString("shiftno") + " : ";
                                break;
                            case -102:
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtofuelshift");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfromfuelshift");
                                deleteControlMessage2 = sessionBean.getLoc().getString("shiftno") + " : ";
                                break;
                            case -107: //depo sayımına bağlı
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtostocktaking");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyouopenthestatusofstocktaking");
                                deleteControlMessage2 = sessionBean.getLoc().getString("warehousestocktaking") + " : ";
                                break;
                            case -109: //depo sayımına bağlı
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleasedeletetheinvoicefromtheinvoicepage");
                                deleteControlMessage2 = sessionBean.getLoc().getString("documentno") + " : ";
                                break;
                            default:
                                break;
                        }
                        if (response != -108) {

                            relatedRecord += controlDeleteList.get(0).getR_recordno();

                            relatedRecordId = controlDeleteList.get(0).getR_record_id();
                            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                        } else if (response == -108) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        }
                    } else {//Sil
                        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:dlgDelete");

                        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                    }
                }
            }

        }

    }

    public void delete() {
        int result = 0;
        result = financingDocumentService.delete(selectedObject.getFinancingDocument());
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_financingdocumentproc').hide();");
            context.update("tbvMovement:frmMovementDataTable:dtbMovement");

        }
        sessionBean.createUpdateMessage(result);
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_financingdocumentproc').hide();");
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("PF('dlg_IncomeExpense').hide();");
        context.execute("goToRelatedRecord();");

    }

    public void goToRelatedRecord() {
        List<Object> list = new ArrayList<>();
        switch (response) {
            case -101:
                Shift shift = new Shift();
                shift.setId(relatedRecordId);
                shift = marketShiftService.findShift(shift);
                list.add(shift);
                marwiz.goToPage("/pages/general/marketshift/marketshifttransferprocess.xhtml", list, 1, 104);
                break;
            case -102:
                FuelShift fuelShift = new FuelShift();
                fuelShift.setId(relatedRecordId);
                fuelShift = fuelShiftTransferService.findShift(fuelShift);
                list.add(fuelShift);
                marwiz.goToPage("/pages/automation/fuelshift/fuelshifttransferprocesses.xhtml", list, 1, 108);
                break;
            case -107:
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof StockTaking) {
                        list.remove(i);
                    }
                }
                StockTaking stockTaking = new StockTaking();
                stockTaking.setId(relatedRecordId);
                stockTaking = stockTakingService.find(stockTaking);
                list.add(stockTaking);
                marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 1, 54);
                break;
            case -109:
                Invoice invoice1 = new Invoice();
                invoice1.setId(relatedRecordId);
                invoice1 = invoiceService.findInvoice(invoice1);
                list.add(invoice1);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                break;
            default:
                break;
        }
    }

    public void createPdf() {

        incomeExpenseMovementService.exportPdf(" ", toogleList, beginDate, endDate, incomeExpense, listOfTotals);

    }

    public void createExcel() throws IOException {
        incomeExpenseMovementService.exportExcel(" ", toogleList, beginDate, endDate, incomeExpense, listOfTotals);

    }

    public void createPrinter() {

        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(incomeExpenseMovementService.exportPrinter(" ", toogleList, beginDate, endDate, incomeExpense, listOfTotals)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void edit() {
        if (response != -107) {
            goToRelatedRecordBefore();
        } else {
            relatedRecord = "";
            deleteControlMessage2 = "";
            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtostocktakingitcannotbeupdated");
            deleteControlMessage1 = sessionBean.getLoc().getString("warehousestocktaking") + " : " + controlDeleteList.get(0).getR_recordno();

            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");

        }

    }

    public void updateCommission() {
        int result = 0;
        selectedBankAccountCommission = bankAccountCommissionService.findBankAccountCommission(selectedObject.getFinancingDocument().getBankAccountCommissionId());
        if (selectedBankAccountCommission.getFinancingDocument().getId() == selectedObject.getFinancingDocument().getId()) {//Bankadan bankaya virman
            selectedBankAccountCommission.getFinancingDocument().getFinancingType().setId(selectedObject.getFinancingDocument().getFinancingType().getId());
            selectedBankAccountCommission.getFinancingDocument().setId(selectedObject.getFinancingDocument().getId());
            selectedBankAccountCommission.getFinancingDocument().setDocumentNumber(selectedObject.getFinancingDocument().getDocumentNumber());
            selectedBankAccountCommission.getFinancingDocument().setPrice(selectedObject.getFinancingDocument().getPrice());
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getFinancingDocument().getPrice() != null) {
                selectedBankAccountCommission.getCommissionFinancingDocument().setPrice(selectedBankAccountCommission.getTotalMoney()
                        .subtract(selectedBankAccountCommission.getFinancingDocument().getPrice()));//Diğeri de güncellenmeli
            }
            selectedBankAccountCommission.setCommissionMoney(selectedBankAccountCommission.getCommissionFinancingDocument().getPrice());
            BigDecimal commissionRate = BigDecimal.valueOf(0);
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionMoney() != null) {
                commissionRate = BigDecimal.valueOf(100).multiply(selectedBankAccountCommission.getCommissionMoney());
                commissionRate = commissionRate.divide(selectedBankAccountCommission.getTotalMoney(), 4, RoundingMode.HALF_EVEN);
            }
            selectedBankAccountCommission.setCommissionRate(commissionRate);

            selectedBankAccountCommission.getFinancingDocument().getCurrency().setId(selectedObject.getFinancingDocument().getCurrency().getId());
            selectedBankAccountCommission.getFinancingDocument().setExchangeRate(selectedObject.getFinancingDocument().getExchangeRate());
            selectedBankAccountCommission.getFinancingDocument().setDocumentDate(selectedObject.getFinancingDocument().getDocumentDate());
            selectedBankAccountCommission.getFinancingDocument().setDescription(selectedObject.getFinancingDocument().getDescription());
            selectedBankAccountCommission.getFinancingDocument().setInMovementId(secondId);
            selectedBankAccountCommission.getFinancingDocument().setOutMovementId(firstId);
            selectedBankAccountCommission.getFinancingDocument().getBranch().setId(selectedObject.getFinancingDocument().getBranch().getId());
            selectedBankAccountCommission.getFinancingDocument().getTransferBranch().setId(selectedObject.getFinancingDocument().getTransferBranch().getId());
        } else {//kredi kartı ödeme komisyon
            selectedBankAccountCommission.getCommissionFinancingDocument().getFinancingType().setId(selectedObject.getFinancingDocument().getFinancingType().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setId(selectedObject.getFinancingDocument().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().getIncomeExpense().setId(selectedObject.getFinancingDocument().getIncomeExpense().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setDocumentNumber(selectedObject.getFinancingDocument().getDocumentNumber());
            selectedBankAccountCommission.getCommissionFinancingDocument().setPrice(selectedObject.getFinancingDocument().getPrice());
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionFinancingDocument().getPrice() != null) {
                selectedBankAccountCommission.getFinancingDocument().setPrice(selectedBankAccountCommission.getTotalMoney()
                        .subtract(selectedBankAccountCommission.getCommissionFinancingDocument().getPrice()));//Diğeri de güncellenmeli
            }
            selectedBankAccountCommission.setCommissionMoney(selectedObject.getFinancingDocument().getPrice());
            BigDecimal commissionRate = BigDecimal.valueOf(0);
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionMoney() != null) {
                commissionRate = BigDecimal.valueOf(100).multiply(selectedBankAccountCommission.getCommissionMoney());
                commissionRate = commissionRate.divide(selectedBankAccountCommission.getTotalMoney(), 4, RoundingMode.HALF_EVEN);
            }
            selectedBankAccountCommission.setCommissionRate(commissionRate);

            selectedBankAccountCommission.getCommissionFinancingDocument().getCurrency().setId(selectedObject.getFinancingDocument().getCurrency().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setExchangeRate(selectedObject.getFinancingDocument().getExchangeRate());
            selectedBankAccountCommission.getCommissionFinancingDocument().setDocumentDate(selectedObject.getFinancingDocument().getDocumentDate());
            selectedBankAccountCommission.getCommissionFinancingDocument().setDescription(selectedObject.getFinancingDocument().getDescription());
            selectedBankAccountCommission.getCommissionFinancingDocument().setInMovementId(secondId);
            selectedBankAccountCommission.getCommissionFinancingDocument().setOutMovementId(firstId);
            selectedBankAccountCommission.getCommissionFinancingDocument().getBranch().setId(selectedObject.getFinancingDocument().getBranch().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().getTransferBranch().setId(selectedObject.getFinancingDocument().getTransferBranch().getId());
        }
        boolean isNegative = false;
        if (selectedBankAccountCommission.getCommissionFinancingDocument().getPrice().compareTo(BigDecimal.valueOf(0)) != 1
                || selectedBankAccountCommission.getFinancingDocument().getPrice().compareTo(BigDecimal.valueOf(0)) != 1) {
            isNegative = true;
        }
        if (!isNegative) {
            result = bankAccountCommissionService.updateCommission(selectedBankAccountCommission);
            if (result > 0) {
                listOfObjects = findall(" ");
                RequestContext.getCurrentInstance().execute("PF('dlg_financingdocumentproc').hide();");
                RequestContext.getCurrentInstance().update("tbvMovement:frmMovementDataTable:dtbMovement");
            }

            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pricethatmorethancommissiontransferpricecannotbentered")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void deleteCommission() {
        int result = 0;
        selectedBankAccountCommission = new BankAccountCommission();
        selectedBankAccountCommission.setId(selectedObject.getFinancingDocument().getBankAccountCommissionId());
        result = bankAccountCommissionService.deleteCommission(selectedBankAccountCommission);
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_financingdocumentproc').hide();");
            context.update("tbvMovement:frmMovementDataTable:dtbMovement");
        }
        sessionBean.createUpdateMessage(result);
    }

}
