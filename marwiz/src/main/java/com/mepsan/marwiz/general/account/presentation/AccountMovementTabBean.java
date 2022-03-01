/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 05:44:31
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.account.business.IAccountMovementService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

@ManagedBean
@ViewScoped
public class AccountMovementTabBean extends GeneralReportBean<AccountMovement> {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{accountMovementService}")
    public IAccountMovementService accountMovementService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{creditService}")
    private ICreditService creditService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{accountTransferBalanceProcessBean}")
    public AccountTransferBalanceProcessBean accountTransferBalanceProcessBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private Account account;
    private BigDecimal transferringBalance, totalBalance;
    private List<BankAccount> listOfBankAccount;
    private List<Safe> listOfSafe;
    private Currency outCurrency, inCurrency;
    private int firstId, secondId;
    private String exchange;
    private Date beginDate, endDate;
    private int opType;
    private BigDecimal totalIncoming, totalOutcoming;
    private boolean isAccountExtract;
    private List<Type> listOfType;
    private int processType;
    private String bookType;
    private boolean isIncomeExpense;
    private String beanName;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private int response;
    private boolean isUpdate;
    private boolean isAccountTransfer;
    private String sortFieldForImport, sortOrderForImport;
    private Date termDate;
    private int termDateOpType;
    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    private String branchList;
    private int financingTypeId;
    private List<Type> listOfFinancingType;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setAccountMovementService(IAccountMovementService accountMovementService) {
        this.accountMovementService = accountMovementService;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
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

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public BigDecimal getTransferringBalance() {
        return transferringBalance;
    }

    public void setTransferringBalance(BigDecimal transferringBalance) {
        this.transferringBalance = transferringBalance;
    }

    public BigDecimal getTotalIncoming() {
        return totalIncoming;
    }

    public void setTotalIncoming(BigDecimal totalIncoming) {
        this.totalIncoming = totalIncoming;
    }

    public BigDecimal getTotalOutcoming() {
        return totalOutcoming;
    }

    public void setTotalOutcoming(BigDecimal totalOutcoming) {
        this.totalOutcoming = totalOutcoming;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
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

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }

    public boolean isIsAccountExtract() {
        return isAccountExtract;
    }

    public void setIsAccountExtract(boolean isAccountExtract) {
        this.isAccountExtract = isAccountExtract;
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

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public boolean isIsIncomeExpense() {
        return isIncomeExpense;
    }

    public void setIsIncomeExpense(boolean isIncomeExpense) {
        this.isIncomeExpense = isIncomeExpense;
    }

    public void setAccountTransferBalanceProcessBean(AccountTransferBalanceProcessBean accountTransferBalanceProcessBean) {
        this.accountTransferBalanceProcessBean = accountTransferBalanceProcessBean;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isIsAccountTransfer() {
        return isAccountTransfer;
    }

    public void setIsAccountTransfer(boolean isAccountTransfer) {
        this.isAccountTransfer = isAccountTransfer;
    }

    public Date getTermDate() {
        return termDate;
    }

    public void setTermDate(Date termDate) {
        this.termDate = termDate;
    }

    public int getTermDateOpType() {
        return termDateOpType;
    }

    public void setTermDateOpType(int termDateOpType) {
        this.termDateOpType = termDateOpType;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<Branch> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<Branch> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public int getFinancingTypeId() {
        return financingTypeId;
    }

    public void setFinancingTypeId(int financingTypeId) {
        this.financingTypeId = financingTypeId;
    }

    public List<Type> getListOfFinancingType() {
        return listOfFinancingType;
    }

    public void setListOfFinancingType(List<Type> listOfFinancingType) {
        this.listOfFinancingType = listOfFinancingType;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------------------AccountMovementTabBean");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                selectedObject = new AccountMovement();
                listOfBranch = new ArrayList<>();
                listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

                selectedBranchList = new ArrayList<>();
                if (((ArrayList) sessionBean.parameter).get(i) instanceof AccountExtract) {//Cari Ekstre Sayfasından geldiyse
                    account = new Account();
                    AccountExtract accountExtract = new AccountExtract();
                    accountExtract = (AccountExtract) ((ArrayList) sessionBean.parameter).get(i);
                    account = ((AccountExtract) ((ArrayList) sessionBean.parameter).get(i)).getAccountList().get(0);
                    selectedBranchList.addAll(((AccountExtract) ((ArrayList) sessionBean.parameter).get(i)).getBranchList());
                    selectedObject.setAccount(account);
                    setOpType(3);
                    Calendar calendar = GregorianCalendar.getInstance();
                    setEndDate(accountExtract.getEndDate());
                    setBeginDate(accountExtract.getBeginDate());
                    setTermDate(accountExtract.getTermDate());
                    setTermDateOpType(accountExtract.getTermDateOpType());
                    isAccountExtract = true;

                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    account = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    selectedObject.setAccount(account);
                    setOpType(3);
                    Calendar calendar = GregorianCalendar.getInstance();
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

                    isAccountExtract = false;
                    for (Branch branch : listOfBranch) {
                        if (branch.getId() == sessionBean.getUser().getLastBranch().getId()) {
                            selectedBranchList.add(branch);
                            break;
                        }
                    }
                }

                find();
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            }
        }
        if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
            toogleList.set(10, true);
        } else {
            toogleList.set(10, false);
        }
        controlDeleteList = new ArrayList();
        listOfSafe = new ArrayList<>();
        listOfType = new ArrayList<>();
        listOfFinancingType = new ArrayList<>();
        inCurrency = new Currency();
        outCurrency = new Currency();
        beanName = "accountMovementTabBean";
        isAccountTransfer = false;
        for (Type type : sessionBean.getTypes(20)) {
            if (type.getId() == 47 || type.getId() == 48 || type.getId() == 49 || type.getId() == 50 || type.getId() == 55 || type.getId() == 56 || type.getId() == 73 || type.getId() == 74 || type.getId() == 108 || type.getId() == 109) {
                listOfType.add(type);
                listOfFinancingType.add(type);
            }
        }

        if (marwiz.getPageIdOfGoToPage() == 11) {//Cari İşlemleri sayfası için
            setListBtn(sessionBean.checkAuthority(new int[]{62, 63, 64, 65}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{66, 67, 68, 69}, 0));
        }

    }

    @Override
    public LazyDataModel<AccountMovement> findall(String where) {
        return new CentrowizLazyDataModel<AccountMovement>() {
            @Override
            public List<AccountMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<AccountMovement> result = accountMovementService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, account, opType, beginDate, endDate, termDate, termDateOpType, branchList, financingTypeId);
                AccountMovement accountMovement = accountMovementService.count(where, account, opType, beginDate, endDate, termDate, termDateOpType, branchList, financingTypeId);
                listOfObjects.setRowCount(accountMovement.getId());
                sortFieldForImport = sortField;
                sortOrderForImport = convertSortOrder(sortOrder);
                RequestContext.getCurrentInstance().execute("count=" + accountMovement.getId() + ";");

                calculate(accountMovement);
                return result;
            }
        };
    }

    @Override
    public void find() {
        isFind = true;
        DataTable dataTable;
        if (isAccountExtract) {//Cari Extreden geldiyse
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMovementDataTable:dtbMovement");

        } else {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("tbvAccountProc:frmMovementDataTable:dtbMovement");

        }
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        branchList = "";
        if (selectedBranchList.isEmpty()) {
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }

        } else {
            for (Branch br : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }
        listOfObjects = findall(" ");

        RequestContext context = RequestContext.getCurrentInstance();

        context.update("tbvAccountProc:frmMovementDataTable:dtbMovement");

    }

    public void calculate(AccountMovement result) {
        totalBalance = BigDecimal.valueOf(0);
        transferringBalance = BigDecimal.valueOf(0);
        totalOutcoming = BigDecimal.valueOf(0);
        totalIncoming = BigDecimal.valueOf(0);

        transferringBalance = result.getTransferringbalance();
        totalIncoming = result.getTotalIncoming();
        totalOutcoming = result.getTotalOutcoming();
        totalBalance = transferringBalance.add(totalIncoming).subtract(totalOutcoming);

    }

    @Override
    public void create() {
        selectedObject = new AccountMovement();
        processType = 1;
        isUpdate = true;
        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedObject.getFinancingDocument().getBranch().setId(b.getId());
                break;
            }
        }

        for (Iterator<Type> iterator = listOfType.iterator(); iterator.hasNext();) {
            Type value = iterator.next();
            if (value.getId() == 108 || value.getId() == 109) {
                iterator.remove();
            }
        }

        isIncomeExpense = false;

        resetFinancing();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_financingdocumentproc').show();");
        context.update("tbvAccountProc:frmFinancingDocumentProcess");
        RequestContext.getCurrentInstance().update("pngAccountBook");

    }

    @Override
    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getFinancingDocument().getDocumentDate())) {
            int result = 0;
            RequestContext context = RequestContext.getCurrentInstance();
            boolean isThere = false;
            if (selectedObject.getFinancingDocument().getPrice().multiply(selectedObject.getFinancingDocument().getExchangeRate()).compareTo(BigDecimal.ZERO) != 1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isThere = true;
            }
            if (!isThere) {
                if (processType == 1) {
                    result = financingDocumentService.create(selectedObject.getFinancingDocument(), firstId, secondId);
                    if (result > 0) {
                        selectedObject.getFinancingDocument().setId(result);
                    }
                } else {//Güncelleme
                    result = financingDocumentService.update(selectedObject.getFinancingDocument(), firstId, secondId);
                }

                if (result > 0) {
                    listOfObjects = findall(" ");
                    context.execute("PF('dlg_financingdocumentproc').hide();");
                    context.update("tbvAccountProc:frmMovementDataTable:dtbMovement");
                }
                if (result == -101) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    sessionBean.createUpdateMessage(result);
                }
            }

        }
    }

    /**
     * hareketin finansman belgesini getirdik.
     */
    public void getfinancingDocument() {
        listOfFinancingType.clear();
        listOfFinancingType = sessionBean.getTypes(20);
        listOfType = sessionBean.getTypes(20);
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedObject.getFinancingDocument().getId() == 0 && selectedObject.getInvoice().getId() == 0 && selectedObject.getReceipt().getId() == 0
                && selectedObject.getChequeBill().getId() == 0) {

            accountTransferBalanceProcessBean.getSelectedObject().setPrice(selectedObject.getPrice());
            accountTransferBalanceProcessBean.getSelectedObject().setId(selectedObject.getId());

            context.update("dlgTransferBalance");
            context.execute("PF('dlg_TransferBalance').show();");
        } else if (selectedObject.getFinancingDocument().getId() > 0) {//finansman belgesi ise

            if (selectedObject.getShiftId() > 0) {

                List<Object> list = new ArrayList<>();
                list.addAll((ArrayList) sessionBean.getParameter());
                Map<String, Object> filt = new HashMap<>();
                List<Shift> findAll = marketShiftService.findAll(0, 2, "shf.id", "ASC", filt, " AND shf.id = " + selectedObject.getShiftId());
                if (!findAll.isEmpty()) {
                    list.add(findAll.get(0));
                    marwiz.goToPage("/pages/general/marketshift/marketshifttransferprocess.xhtml", list, 1, 104);
                } else {

                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("shiftdoesnotbelongtothisbranch"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("grwProcessMessage");

                }
            } else if (selectedObject.getStockTakingId() > 0) {
                List<Object> list = new ArrayList<>();
                list.addAll((ArrayList) sessionBean.getParameter());
                List<StockTaking> findAll = stockTakingService.findAll("AND ist.id=" + selectedObject.getStockTakingId());
                if (!findAll.isEmpty()) {
                    list.add(findAll.get(0));
                    marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 1, 54);
                } else {
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("stocktakingdoesnotbelongtothisbranch"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("grwProcessMessage");
                }

            } else {
                processType = 2;
                isUpdate = false;
                selectedObject.setFinancingDocument(financingDocumentService.findFinancingDocument(selectedObject.getFinancingDocument()));
                changeBranch();
                if (marwiz.getPageIdOfGoToPage() == 72) {//Cari Ekstre
                    isUpdate = false;
                } else {
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
                }

                bringFinancingDocument(selectedObject.getFinancingDocument());
                bringTempCurrency();

                if (inCurrency != null && outCurrency != null) {
                    selectedObject.getFinancingDocument().setCurrency(outCurrency);
                    exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
                } else {
                    exchange = "";
                }

                RequestContext.getCurrentInstance().update("tbvAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

                RequestContext.getCurrentInstance().update("pngAccountBook");

                context.update("dlgFinancingDocumentProc");

                context.execute("PF('dlg_financingdocumentproc').show();");

            }

        } else if (selectedObject.getChequeBill().getId() > 0) {
            selectedObject.setChequeBill(chequeBillService.findChequeBill(selectedObject.getChequeBill()));
            goToProcess(2);
        } else//fatura ise
        if (selectedObject.getInvoice().getId() > 0) {
            selectedObject.setInvoice(invoiceService.findInvoice(selectedObject.getInvoice()));
            goToProcess(1);
        }
        context.update("tbvAccountProc:frmFinancingDocumentProcess");

    }

    public void goToProcess(int type) {
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        boolean isThere = false;
        if (type == 1) {

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Invoice) {
                    isThere = true;
                    list.remove(i);
                    list.add(selectedObject.getInvoice());
                }
            }
            if (!isThere) {
                list.add(selectedObject.getInvoice());
            }
            marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
        } else if (type == 2) {

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof ChequeBill) {
                    isThere = true;
                    list.remove(i);
                    list.add(selectedObject.getChequeBill());
                }
            }
            if (!isThere) {
                list.add(selectedObject.getChequeBill());
            }
            marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 1, 81);
        }

    }

    /*
    * cari kitabına cıft tıkladıgımızda calısır
     */
    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObject.getFinancingDocument().setAccount(accountBookFilterBean.getSelectedData());
            accountBookFilterBean.setSelectedData(null);
            switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
                case 47://cari->kasa
                    firstId = selectedObject.getFinancingDocument().getAccount().getId();
                    break;
                case 48://kasa->cari
                    secondId = selectedObject.getFinancingDocument().getAccount().getId();
                    break;
                case 49://borc dekontu
                    firstId = selectedObject.getAccount().getId();
                    bringCurrency();
                    break;
                case 50://alacak dekontu
                    secondId = selectedObject.getAccount().getId();
                    bringCurrency();
                    break;
                case 55://cari->banka
                case 73:
                    firstId = selectedObject.getAccount().getId();
                    break;
                case 56://banka->cari
                case 74:
                    secondId = selectedObject.getAccount().getId();
                    break;
                default:
                    break;

            }
            accountBookFilterBean.setSelectedData(null);
        } else if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            selectedObject.getFinancingDocument().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            incomeExpenseBookFilterBean.setSelectedData(null);
        }

        RequestContext.getCurrentInstance().update("tbvAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
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
                firstId = financingDocument.getAccount().getId();
                secondId = financingDocument.getInMovementId();
                break;
            case 48://kasa->cari
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getAccount().getId();
                break;
            case 49://borc dekontu
                firstId = financingDocument.getAccount().getId();
                break;
            case 50://alacak dekontu
                secondId = financingDocument.getAccount().getId();
                break;
            case 55://cari->banka
            case 73:
                firstId = financingDocument.getAccount().getId();
                secondId = financingDocument.getInMovementId();
                break;
            case 56://banka->cari
            case 74:
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getAccount().getId();
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
            case 73:
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 56://banka->cari
            case 74:
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 108://alacak
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                outCurrency.setId(selectedObject.getFinancingDocument().getCurrency().getId());

                break;
            case 109://alacak
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                outCurrency.setId(selectedObject.getFinancingDocument().getCurrency().getId());

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

        RequestContext.getCurrentInstance().update("tbvAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
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
        selectedObject.getFinancingDocument().setAccount(account);
        selectedObject.getFinancingDocument().getIncomeExpense().setId(0);
        selectedObject.getFinancingDocument().setExchangeRate(BigDecimal.ONE);

        changeBranch();

        bringCurrency();
        RequestContext.getCurrentInstance().update("tbvAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

    }

    /**
     * Carmi yoksa gelir gider kartımı seçeceğini belirleyen butona tıkladıkça
     * tetiklenir.
     */
    public void accountOrIncomeExp() {
        if (isIncomeExpense) {//cari seçecek
            selectedObject.getFinancingDocument().setIncomeExpense(new IncomeExpense());
            isIncomeExpense = false;
        } else {//gelirgider sececek
            selectedObject.getFinancingDocument().setAccount(new Account());
            isIncomeExpense = true;
        }

    }

    public void createPdf() {
        if (isAccountExtract) {//Cari Extreden geldiyse
            accountMovementService.exportPdf(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, account, true, marwiz.getPageIdOfGoToPage(), sortFieldForImport, sortOrderForImport, termDate, termDateOpType, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, financingTypeId);
        } else {
            accountMovementService.exportPdf(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, account, false, marwiz.getPageIdOfGoToPage(), sortFieldForImport, sortOrderForImport, termDate, termDateOpType, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, financingTypeId);
        }

    }

    public void createExcel() throws IOException {
        if (isAccountExtract) {//Cari Extreden geldiyse
            accountMovementService.exportExcel(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, account, true, marwiz.getPageIdOfGoToPage(), sortFieldForImport, sortOrderForImport, termDate, termDateOpType, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, financingTypeId);
        } else {
            accountMovementService.exportExcel(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, account, false, marwiz.getPageIdOfGoToPage(), sortFieldForImport, sortOrderForImport, termDate, termDateOpType, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, financingTypeId);
        }

    }

    public void createPrinter() {
        if (isAccountExtract) {//Cari Extreden geldiyse
            RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(accountMovementService.exportPrinter(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, account, true, marwiz.getPageIdOfGoToPage(), sortFieldForImport, sortOrderForImport, termDate, termDateOpType, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, financingTypeId)) + "');$(\"#printerPanel\").css('display','block');print_pageMovement();$(\"#printerPanel\").css('display','none');");
        } else {
            RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(accountMovementService.exportPrinter(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, account, false, marwiz.getPageIdOfGoToPage(), sortFieldForImport, sortOrderForImport, termDate, termDateOpType, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, financingTypeId)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
        }
    }

    public BigDecimal convertBalanceSign(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.valueOf(0)) == -1) {
            balance = balance.multiply(BigDecimal.valueOf(-1));
        } else {
            return balance;
        }
        return balance;
    }

    public boolean manageBalanceSign(BigDecimal balance) {
        if (balance != null) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getFinancingDocument().getDocumentDate())) {
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
                        case -103:
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtochequebill");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfromchequebill");
                            deleteControlMessage2 = sessionBean.getLoc().getString("portfoliono") + " : ";
                            break;
                        case -104:
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtocredit");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfromcredit");
                            deleteControlMessage2 = sessionBean.getLoc().getString("customername") + " - " + sessionBean.getLoc().getString("paymentdate") + " : ";
                            break;
                        case -105:
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfrominvoice");
                            deleteControlMessage2 = sessionBean.getLoc().getString("documentno") + " : ";
                            break;
                        case -106:
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosaleitcannotbedeleted");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyourefundits");
                            deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : " + controlDeleteList.get(0).getR_recordno();
                            break;
                        case -107: //depo sayımına bağlı
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtostocktaking");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyouopenthestatusofstocktaking");
                            deleteControlMessage2 = sessionBean.getLoc().getString("warehousestocktaking") + " : ";
                            break;
                        default:
                            break;
                    }
                    if (response == -104) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date processDate = null;
                        try {
                            processDate = formatter.parse(controlDeleteList.get(0).getR_recordno());

                        } catch (ParseException ex) {
                            Logger.getLogger(AccountMovementTabBean.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        relatedRecord += account.getName() + " - " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), processDate);
                    } else if (response != -106) {
                        relatedRecord += controlDeleteList.get(0).getR_recordno();
                    }
                    if (response != -108) {
                        relatedRecordId = controlDeleteList.get(0).getR_record_id();
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                    } else if (response == -108) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {//Sil
                    if (isAccountExtract) {
                        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:dlgDelete");
                    } else {
                        RequestContext.getCurrentInstance().update("tbvAccountProc:frmFinancingDocumentProcess:dlgDelete");
                    }

                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
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
            if (isAccountExtract) {
                context.update("frmMovementDataTable:dtbMovement");
            } else {
                context.update("tbvAccountProc:frmMovementDataTable:dtbMovement");
            }
        }
        sessionBean.createUpdateMessage(result);
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_financingdocumentproc').hide();");
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("goToRelatedRecord();");

    }

    public void goToRelatedRecord() {
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
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
            case -103:
                ChequeBill chequeBill = new ChequeBill();
                chequeBill.setId(relatedRecordId);
                chequeBill = chequeBillService.findChequeBill(chequeBill);
                list.add(chequeBill);
                marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 1, 81);
                break;
            case -104:
                CreditReport credit = new CreditReport();
                credit.setId(relatedRecordId);
                credit = creditService.findCreditReport(credit);
                list.add(credit);
                marwiz.goToPage("/pages/finance/credit/creditprocess.xhtml", list, 1, 79);
                break;
            case -105:
                Invoice invoice = new Invoice();
                invoice.setId(relatedRecordId);
                invoice = invoiceService.findInvoice(invoice);
                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                break;
            case -107:
                StockTaking stockTaking = new StockTaking();
                stockTaking.setId(relatedRecordId);
                stockTaking = stockTakingService.find(stockTaking);
                list.add(stockTaking);
                marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 1, 54);
            default:
                break;
        }
    }

    public void edit() {
        if (response != -106) {
            goToRelatedRecordBefore();
        } else {
            relatedRecord = "";
            if (response == -106) {
                deleteControlMessage1 = "";
                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosaleitcannotbeupdated");
                deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : " + controlDeleteList.get(0).getR_recordno();
            } else if (response == -107) {
                deleteControlMessage1 = "";
                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosaleitcannotbeupdated");
                deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : " + controlDeleteList.get(0).getR_recordno();
            }

            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
        }

    }

    /**
     * Bu metot cariden cariye virman işlemi için dialog açar.
     */
    public void showCustomerMovementDialog() {
        isAccountTransfer = true;
        bookType = "Musteri,MTedatikçi";
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        CustomerToCustomerTransferBean customerToCustomerTransferBean = (CustomerToCustomerTransferBean) viewMap.get("customerToCustomerTransferBean");

        customerToCustomerTransferBean.init();
        customerToCustomerTransferBean.resetFinancing();

        RequestContext.getCurrentInstance().update("dlgCustomerToCustomerTransferProcess");
        RequestContext.getCurrentInstance().execute("PF('dlg_customertocustomerprocess').show();");
        RequestContext.getCurrentInstance().update("pngAccountBook");

    }

    public void termDateUpdate() {

        if (termDate == null) {
            termDateOpType = 0;
        }

    }

    public void changeBranch() {
        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
                bookType = "Musteri,MTedatikçi";
                listOfSafe = safeService.selectSafe(selectedObject.getFinancingDocument().getBranch());
                firstId = account.getId();
                break;
            case 48://kasa->cari
                bookType = "Tedarikçi,MTedatikçi";
                secondId = account.getId();
                listOfSafe = safeService.selectSafe(selectedObject.getFinancingDocument().getBranch());
                break;
            case 49://cari
                firstId = account.getId();
                break;
            case 50://cari
                secondId = account.getId();
                break;
            case 55://cari->banka
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", selectedObject.getFinancingDocument().getBranch());

                bookType = "Musteri,MTedatikçi";
                firstId = account.getId();
                break;
            case 56://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", selectedObject.getFinancingDocument().getBranch());

                bookType = "Tedarikçi,MTedatikçi";
                secondId = account.getId();
                break;
            case 73:
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedObject.getFinancingDocument().getBranch());

                bookType = "Musteri,MTedatikçi";
                firstId = account.getId();
                break;
            case 74:
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedObject.getFinancingDocument().getBranch());

                bookType = "Tedarikçi,MTedatikçi";
                secondId = account.getId();
                break;
            default:
                break;
        }
    }

}
