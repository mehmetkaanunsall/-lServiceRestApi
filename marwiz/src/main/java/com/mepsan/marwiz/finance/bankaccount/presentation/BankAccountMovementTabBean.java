/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2018 10:55:41
 */
package com.mepsan.marwiz.finance.bankaccount.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountCommissionService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountMovementService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.bankextract.presentation.BankExtractBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
public class BankAccountMovementTabBean extends GeneralReportBean<BankAccountMovement> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{bankAccountMovementService}")
    public IBankAccountMovementService bankAccountMovementService;

    @ManagedProperty(value = "#{bankAccountCommissionService}")
    public IBankAccountCommissionService bankAccountCommissionService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{creditService}")
    private ICreditService creditService;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private BankAccount selectedBankAccount;
    private BigDecimal transferringBalance, totalBalance, totalIncoming, totalOutcoming;
    private List<Safe> listOfSafe;
    private List<BankAccount> listOfBankAccount;
    private Currency outCurrency, inCurrency;
    private int firstId, secondId;
    private String exchange;
    private Date beginDate, endDate;
    private int opType;
    private List<Type> listOfType;
    private List<Type> listOfFinancingType;
    private int processType;
    private String bookType;
    private boolean isIncomeExpense;
    private String beanName;
    private boolean isExtract;
    private boolean isUpdate;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private int response;
    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    private String branchList;
    private List<BankAccount> listOfBankAccount2;
    private int financingTypeId;
    private BankAccountCommission selectedBankAccountCommission;
    private boolean isCommission;
    private String commissionMessage;

    public BankAccount getSelectedBankAccount() {
        return selectedBankAccount;
    }

    public void setSelectedBankAccount(BankAccount selectedBankAccount) {
        this.selectedBankAccount = selectedBankAccount;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBankAccountMovementService(IBankAccountMovementService bankAccountMovementService) {
        this.bankAccountMovementService = bankAccountMovementService;
    }

    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }

    public void setListOfBankAccount(List<BankAccount> listOfBankAccount) {
        this.listOfBankAccount = listOfBankAccount;
    }

    public List<BankAccount> getListOfBankAccount2() {
        return listOfBankAccount2;
    }

    public void setListOfBankAccount2(List<BankAccount> listOfBankAccount2) {
        this.listOfBankAccount2 = listOfBankAccount2;
    }

    public List<Type> getListOfFinancingType() {
        return listOfFinancingType;
    }

    public void setListOfFinancingType(List<Type> listOfFinancingType) {
        this.listOfFinancingType = listOfFinancingType;
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

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
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

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
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

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
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

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
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

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isIsExtract() {
        return isExtract;
    }

    public void setIsExtract(boolean isExtract) {
        this.isExtract = isExtract;
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

    public String getCommissionMessage() {
        return commissionMessage;
    }

    public void setCommissionMessage(String commissionMessage) {
        this.commissionMessage = commissionMessage;
    }

    public void setBankAccountCommissionService(IBankAccountCommissionService bankAccountCommissionService) {
        this.bankAccountCommissionService = bankAccountCommissionService;
    }

    public BankAccountCommission getSelectedBankAccountCommission() {
        return selectedBankAccountCommission;
    }

    public void setSelectedBankAccountCommission(BankAccountCommission selectedBankAccountCommission) {
        this.selectedBankAccountCommission = selectedBankAccountCommission;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------------------BankAccountMovementTabBean");

        selectedBankAccount = new BankAccount();
        selectedBankAccount.getBankAccountBranchCon().setCommissionBankAccount(new BankAccount());
        selectedBankAccountCommission = new BankAccountCommission();
        listOfBranch = new ArrayList<>();
        listOfBankAccount2 = new ArrayList<>();
        listOfBranch = branchService.findUserAuthorizeBranchForBankAccount();// kullanıcının yetkili olduğu branch listesini çeker

        selectedBranchList = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof BankAccountMovement) { // ekstre sayfasından geldiyse
                    processType = 2;
                    selectedBankAccount = ((BankAccountMovement) ((ArrayList) sessionBean.parameter).get(i)).getBankAccount();
                    selectedBranchList.addAll(((BankAccountMovement) ((ArrayList) sessionBean.parameter).get(i)).getBranchList());
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    BankExtractBean bankExtractBean = (BankExtractBean) viewMap.get("bankExtractBean");
                    beginDate = bankExtractBean.getBeginDate();
                    endDate = bankExtractBean.getEndDate();
                    isExtract = true;
                    break;

                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof BankAccount) {
                    selectedBankAccount = (BankAccount) ((ArrayList) sessionBean.parameter).get(i);
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
                    processType = 2;
                    for (Branch branch : listOfBranch) {
                        if (branch.getId() == sessionBean.getUser().getLastBranch().getId()) {
                            selectedBranchList.add(branch);
                            break;
                        }
                    }
                    break;
                }

            }
        }

        selectedObject = new BankAccountMovement();
        setOpType(3);

        find();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true);

        controlDeleteList = new ArrayList();
        listOfSafe = new ArrayList<>();
        listOfType = new ArrayList<>();
        listOfFinancingType = new ArrayList<>();
        inCurrency = new Currency();
        outCurrency = new Currency();
        beanName = "bankAccountMovementTabBean";
        for (Type type : sessionBean.getTypes(20)) {
            if (type.getId() == 51 || type.getId() == 53 || type.getId() == 54
                      || type.getId() == 55 || type.getId() == 56
                      || type.getId() == 73 || type.getId() == 74) {
                listOfFinancingType.add(type);
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{113, 114, 115}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        isUpdate = true;

        listOfType.clear();
        for (Type type : sessionBean.getTypes(20)) {
            if (type.getId() == 51 || type.getId() == 53 || type.getId() == 54) {
                listOfType.add(type);
            } else if (type.getId() == 55 || type.getId() == 56) {
                if (selectedBankAccount.getType().getId() == 14) {
                    listOfType.add(type);
                }
            } else if (type.getId() == 73 || type.getId() == 74) {
                if (selectedBankAccount.getType().getId() == 16 || selectedBankAccount.getType().getId()==104) {
                    listOfType.add(type);
                }
            }
        }

        isIncomeExpense = false;
        selectedObject = new BankAccountMovement();
        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedObject.getFinancingDocument().getBranch().setId(b.getId());
                selectedObject.getFinancingDocument().getTransferBranch().setId(b.getId());
                break;
            }
        }
        resetFinancing();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_financingdocumentproc').show();");
        context.update("tbvBankAccountProc:frmFinancingDocumentProcess");

    }

    @Override
    public void save() {
        if (isCommission == true) {//Commission Dialog
            int result = 0;
            //1. finansman belgesi
            selectedBankAccountCommission.getFinancingDocument().getFinancingType().setId(51);
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionMoney() != null) {
                selectedBankAccountCommission.getFinancingDocument().setPrice(selectedBankAccountCommission.getTotalMoney().subtract(selectedBankAccountCommission.getCommissionMoney()));
            } else {
                selectedBankAccountCommission.getFinancingDocument().setPrice(BigDecimal.valueOf(0));
            }
            selectedBankAccountCommission.getFinancingDocument().getCurrency().setId(selectedBankAccount.getCurrency().getId());
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            BankAccountProcessBean bankAccountProcessBean = (BankAccountProcessBean) viewMap.get("bankAccountProcessBean");
            Currency curr = new Currency();
            for (BankAccount ba : bankAccountProcessBean.getListOfBankAccount()) {
                if (ba.getId() == selectedBankAccount.getBankAccountBranchCon().getCommissionBankAccount().getId()) {
                    curr.setId(ba.getCurrency().getId());
                }
            }
            selectedBankAccountCommission.getFinancingDocument().setExchangeRate(exchangeService.bringExchangeRate(selectedBankAccount.getCurrency(), curr, sessionBean.getUser()));
            selectedBankAccountCommission.getFinancingDocument().setDocumentDate(new Date());
            selectedBankAccountCommission.getFinancingDocument().setInMovementId(selectedBankAccount.getBankAccountBranchCon().getCommissionBankAccount().getId());
            selectedBankAccountCommission.getFinancingDocument().setOutMovementId(selectedBankAccount.getId());
            selectedBankAccountCommission.getFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
            selectedBankAccountCommission.getFinancingDocument().getTransferBranch().setId(sessionBean.getUser().getLastBranch().getId());

            //2. finansman belgesi
            selectedBankAccountCommission.getCommissionFinancingDocument().getFinancingType().setId(74);
            selectedBankAccountCommission.getCommissionFinancingDocument().getIncomeExpense().setId(selectedBankAccount.getBankAccountBranchCon().getCommissionIncomeExpense().getId());
            if (selectedBankAccountCommission.getCommissionMoney() != null) {
                selectedBankAccountCommission.getCommissionFinancingDocument().setPrice(selectedBankAccountCommission.getCommissionMoney());
            } else {
                selectedBankAccountCommission.getCommissionFinancingDocument().setPrice(BigDecimal.valueOf(0));
            }
            selectedBankAccountCommission.getCommissionFinancingDocument().getCurrency().setId(selectedBankAccount.getCurrency().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setExchangeRate(BigDecimal.valueOf(1));
            selectedBankAccountCommission.getCommissionFinancingDocument().setDocumentDate(new Date());
            selectedBankAccountCommission.getCommissionFinancingDocument().setInMovementId(selectedBankAccount.getBankAccountBranchCon().getCommissionIncomeExpense().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setOutMovementId(selectedBankAccount.getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().getTransferBranch().setId(sessionBean.getUser().getLastBranch().getId());

            result = bankAccountCommissionService.createCommission(selectedBankAccountCommission);
            if (result > 0) {
                listOfObjects = findall(" ");
                RequestContext.getCurrentInstance().execute("PF('dlg_Commission').hide();");
                RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmMovementDataTable:dtbMovement");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            if (sessionBean.isPeriodClosed(selectedObject.getFinancingDocument().getDocumentDate())) {
                int result = 0;
                RequestContext context = RequestContext.getCurrentInstance();
                boolean isThere = false;
                if (selectedObject.getFinancingDocument().getPrice().multiply(selectedObject.getFinancingDocument().getExchangeRate()).compareTo(BigDecimal.ZERO) != 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }

                if (!isThere) {
                    if (processType == 1) {
                        if (selectedObject.getFinancingDocument().getFinancingType().getId() == 51
                                  || selectedObject.getFinancingDocument().getFinancingType().getId() == 53 || selectedObject.getFinancingDocument().getFinancingType().getId() == 54) {
                            if (selectedObject.getFinancingDocument().getBranch().getId() == selectedObject.getFinancingDocument().getTransferBranch().getId()) {
                                selectedObject.getFinancingDocument().getTransferBranch().setId(0);
                            }
                        }
                        result = financingDocumentService.create(selectedObject.getFinancingDocument(), firstId, secondId);
                        if (result > 0) {
                            selectedObject.getFinancingDocument().setId(result);
                        }

                    } else {//Güncelleme
                        if (selectedObject.getFinancingDocument().getBankAccountCommissionId() != 0) {//Komisyon tablosunda var ise
                            commissionMessage = sessionBean.getLoc().getString("ifthismovementisupdatedcommissionmovementrelatedtothisrecordwillbeupdated");
                            commissionMessage = commissionMessage + " " + sessionBean.getLoc().getString("areyousure");
                            RequestContext.getCurrentInstance().update("dlgCommissionWarning");
                            RequestContext.getCurrentInstance().execute("PF('dlg_CommissionWarning').show();");
                        } else {
                            result = financingDocumentService.update(selectedObject.getFinancingDocument(), firstId, secondId);
                        }
                    }
                    if (selectedObject.getFinancingDocument().getBankAccountCommissionId() == 0) {
                        if (result > 0) {
                            listOfObjects = findall(" ");
                            context.execute("PF('dlg_financingdocumentproc').hide();");
                            context.update("tbvBankAccountProc:frmMovementDataTable:dtbMovement");
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
        }

    }

    @Override
    public void find() {
        isFind = true;
        DataTable dataTable;
        if (isExtract) {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMovementDataTable:dtbMovement");
        } else {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("tbvBankAccountProc:frmMovementDataTable:dtbMovement");
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
    }

    @Override
    public LazyDataModel<BankAccountMovement> findall(String where) {
        return new CentrowizLazyDataModel<BankAccountMovement>() {
            @Override
            public List<BankAccountMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<BankAccountMovement> result = bankAccountMovementService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedBankAccount, opType, beginDate, endDate, branchList, financingTypeId);
                BankAccountMovement bankAccountMovement = bankAccountMovementService.count(where, selectedBankAccount, opType, beginDate, endDate, branchList, financingTypeId);
                listOfObjects.setRowCount(bankAccountMovement.getId());
                RequestContext.getCurrentInstance().execute("count=" + bankAccountMovement.getId() + ";");

                calculate(bankAccountMovement);

                return result;
            }
        };

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

    public void calculate(BankAccountMovement bankAccountMovement) {
        totalBalance = BigDecimal.valueOf(0);
        transferringBalance = BigDecimal.valueOf(0);
        totalOutcoming = BigDecimal.valueOf(0);
        totalIncoming = BigDecimal.valueOf(0);

        transferringBalance = bankAccountMovement.getTransferringbalance();
        totalIncoming = bankAccountMovement.getTotalIncoming();
        totalOutcoming = bankAccountMovement.getTotalOutcoming();
        totalBalance = transferringBalance.add(totalIncoming).subtract(totalOutcoming);

    }

    public void getfinancingDocument() {
        if (selectedObject.getFinancingDocument().getId() > 0) {
            processType = 2;
            isUpdate = false;

            listOfType.clear();
            for (Type type : sessionBean.getTypes(20)) {
                if (type.getId() == 51 || type.getId() == 53 || type.getId() == 54 || type.getId() == 55 || type.getId() == 56 || type.getId() == 73 || type.getId() == 74) {
                    listOfType.add(type);
                }
            }

            selectedObject.setFinancingDocument(financingDocumentService.findFinancingDocument(selectedObject.getFinancingDocument()));
            if (selectedObject.getFinancingDocument().getTransferBranch().getId() == 0) {
                selectedObject.getFinancingDocument().getTransferBranch().setId(selectedObject.getBranch().getId());
            }
            if (selectedObject.getFinancingDocument().getIncomeExpense().getId() > 0) {
                isIncomeExpense = true;
            } else {
                isIncomeExpense = false;
            }
            changeBranch();
            changeTransferBranch();

            if (marwiz.getPageIdOfGoToPage() == 70) {//Banka Ekstre
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
            /////
            bringFinancingDocument(selectedObject.getFinancingDocument());
            bringTempCurrency();
            if (inCurrency != null && outCurrency != null) {
                selectedObject.getFinancingDocument().setCurrency(outCurrency);
                exchange = currencyCode(outCurrency.getId()) + " -> " + currencyCode(inCurrency.getId());// örn: $->€
            } else {
                exchange = "";
            }

            RequestContext.getCurrentInstance()
                      .update("tbvBankAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

            RequestContext context = RequestContext.getCurrentInstance();
            context.update("dlgFinancingDocumentProc");
            context.execute("PF('dlg_financingdocumentproc').show();");
            context.update("tbvBankAccountProc:frmFinancingDocumentProcess");
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
                case 55://cari->banka
                case 73:
                    firstId = selectedObject.getFinancingDocument().getAccount().getId();
                    break;
                case 56://banka->cari
                case 74:
                    secondId = selectedObject.getFinancingDocument().getAccount().getId();
                    break;
                default:
                    break;

            }
            accountBookFilterBean.setSelectedData(null);
        } else if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            selectedObject.getFinancingDocument().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
                case 55://cari->banka
                case 73:
                    firstId = selectedObject.getFinancingDocument().getIncomeExpense().getId();
                    break;
                case 56://banka->cari
                case 74:
                    secondId = selectedObject.getFinancingDocument().getIncomeExpense().getId();
                default:
                    break;

            }
            incomeExpenseBookFilterBean.setSelectedData(null);
        }

        RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
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
            case 51://banka->banka
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getInMovementId();
                break;
            case 53://kasa->banka
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getInMovementId();
                break;
            case 54://banka->kasa
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getInMovementId();
                break;
            case 55://cari->banka
            case 73:
                if (isIncomeExpense) {
                    firstId = financingDocument.getIncomeExpense().getId();
                } else {
                    firstId = financingDocument.getAccount().getId();
                }
                secondId = financingDocument.getInMovementId();
                break;
            case 56://banka->cari
            case 74:
                firstId = financingDocument.getOutMovementId();
                if (isIncomeExpense) {
                    secondId = financingDocument.getIncomeExpense().getId();
                } else {
                    secondId = financingDocument.getAccount().getId();
                }
                break;
            default:
                break;

        }
    }

    public void bringTempCurrency() {
        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {

            case 51://banka->banka

                outCurrency.setId(selectedBankAccount.getCurrency().getId());
                for (BankAccount ba : listOfBankAccount2) {
                    if (ba.getId() == secondId) {
                        inCurrency.setId(ba.getCurrency().getId());
                    }
                }

                break;

            case 53://kasa->banka

                for (Safe safe : listOfSafe) {
                    if (safe.getId() == firstId) {
                        outCurrency.setId(safe.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(selectedBankAccount.getCurrency().getId());

                break;
            case 54://banka->kasa
                outCurrency.setId(selectedBankAccount.getCurrency().getId());
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == secondId) {
                        inCurrency.setId(safe.getCurrency().getId());
                        selectedObject.getFinancingDocument().getTransferBranch().setId(safe.getBranch().getId());
                        break;
                    }
                }

                break;
            case 55://cari->banka
            case 73:
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                outCurrency.setId(selectedBankAccount.getCurrency().getId());
                break;
            case 56://banka->cari
            case 74:
                outCurrency.setId(selectedBankAccount.getCurrency().getId());
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
            selectedObject.getFinancingDocument().setCurrency(outCurrency);
            selectedObject.getFinancingDocument().setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            exchange = currencyCode(outCurrency.getId()) + " -> " + currencyCode(inCurrency.getId());// örn: $->€
        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance()
                  .update("tbvBankAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
    }

    /**
     * işlem tipi değiştiğinde tetiklenir.
     */
    public void resetFinancing() {
        firstId = 0;
        secondId = 0;
        selectedObject.getFinancingDocument().getCurrency().setId(0);
        selectedObject.getFinancingDocument().setDocumentDate(new Date());
        inCurrency.setId(0);
        outCurrency.setId(0);
        exchange = "";
        selectedObject.getFinancingDocument().getAccount().setId(0);
        selectedObject.getFinancingDocument().getAccount().setName("");
        selectedObject.getFinancingDocument().getIncomeExpense().setId(0);
        selectedObject.getFinancingDocument().setExchangeRate(BigDecimal.ONE);
        RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmFinancingDocumentProcess:txtCurrentName4");
        RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmFinancingDocumentProcess:txtCurrentName5");
        changeBranch();
        changeTransferBranch();

        bringCurrency();
        RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

    }

    /**
     * gelen currency ıd bilgisine göre para sign bilgisini döndürür.
     *
     * @param currencyId gelen currency id si
     * @return $,€,TL
     */
    public String currencyCode(int currencyId) {
        for (Currency c : sessionBean.getCurrencies()) {
            if (c.getId() == currencyId) {
                return c.getCode();
            }
        }
        return "";
    }

    public void createPdf() {
        if (isExtract) {
            bankAccountMovementService.exportPdf(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, selectedBankAccount, true, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);

        } else {
            bankAccountMovementService.exportPdf(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, selectedBankAccount, false, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
        }
    }

    public void createExcel() throws IOException {
        if (isExtract) {
            bankAccountMovementService.exportExcel(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, selectedBankAccount, true, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
        } else {
            bankAccountMovementService.exportExcel(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, selectedBankAccount, false, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
        }
    }

    public void createPrinter() {
        if (isExtract) {
            RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(bankAccountMovementService.exportPrinter(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, selectedBankAccount, true, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList)) + "');$(\"#printerPanel\").css('display','block');print_pageMovement();$(\"#printerPanel\").css('display','none');");
        } else {
            RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(bankAccountMovementService.exportPrinter(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, selectedBankAccount, false, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

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
            if (selectedObject.getFinancingDocument().getBankAccountCommissionId() != 0) {//Komisyon tablosunda var ise
                commissionMessage = sessionBean.getLoc().getString("ifthismovementisdeletedcommissionmovementrelatedtothisrecordwillbedeleted");
                commissionMessage = commissionMessage + sessionBean.getLoc().getString("areyousure");
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
                            default:
                                break;
                        }
                        if (response == -104) {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date processDate = null;
                            try {
                                processDate = formatter.parse(controlDeleteList.get(0).getR_recordno());
                            } catch (ParseException ex) {
                                Logger.getLogger(BankAccountMovementTabBean.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            relatedRecord += selectedObject.getFinancingDocument().getAccount().getName() + " - " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), processDate);
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
                        RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmFinancingDocumentProcess:dlgDelete");
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
            context.update("tbvBankAccountProc:frmMovementDataTable:dtbMovement");
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
            default:
                break;
        }
    }

    public void edit() {
        if (response != -106) {
            goToRelatedRecordBefore();
        } else {
            relatedRecord = "";
            deleteControlMessage1 = "";
            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosaleitcannotbeupdated");
            deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : " + controlDeleteList.get(0).getR_recordno();

            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
        }

    }

    public void changeBank(int type) {
        if (firstId == secondId && selectedObject.getFinancingDocument().getTransferBranch().getId() == selectedObject.getFinancingDocument().getBranch().getId()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("betweensamebankaccountscannotbetransferred")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            if (type == 2) {
                secondId = 0;
                RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmFinancingDocumentProcess:slcBank2");
            }
        }
    }

    public void changeBranch() {
        if (processType == 1) {
            selectedObject.getFinancingDocument().getAccount().setId(0);
            selectedObject.getFinancingDocument().getAccount().setName("");
            selectedObject.getFinancingDocument().getAccount().setTitle("");
            selectedObject.getFinancingDocument().getIncomeExpense().setId(0);
            selectedObject.getFinancingDocument().getIncomeExpense().setName("");
        }
        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {

            case 51://banka->banka
                firstId = selectedBankAccount.getId();
                listOfBankAccount = bankAccountService.bankAccountForSelect(" ", selectedObject.getFinancingDocument().getBranch());
                break;
            case 53://kasa->banka
                listOfSafe = safeService.selectSafe(selectedObject.getFinancingDocument().getBranch());
                listOfBankAccount = bankAccountService.bankAccountForSelect(" ", selectedObject.getFinancingDocument().getBranch());
                secondId = selectedBankAccount.getId();
                break;
            case 54://banka->kasa
                if (processType == 1) {
                    listOfSafe = safeService.selectSafe(listOfBranch);
                } else {
                    listOfSafe = safeService.selectSafe(new Branch(selectedObject.getFinancingDocument().getTransferBranch().getId(), ""));
                }
                listOfBankAccount = bankAccountService.bankAccountForSelect(" ", selectedObject.getFinancingDocument().getBranch());
                firstId = selectedBankAccount.getId();
                break;
            case 55://cari->banka
                secondId = selectedBankAccount.getId();
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", selectedObject.getFinancingDocument().getBranch());
                bookType = "Musteri,MTedatikçi";
                break;
            case 56://banka->cari
                firstId = selectedBankAccount.getId();
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id=14 ", selectedObject.getFinancingDocument().getBranch());
                bookType = "Tedarikçi,MTedatikçi";
                break;
            case 73://cari->banka
                secondId = selectedBankAccount.getId();
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedObject.getFinancingDocument().getBranch());
                bookType = "Musteri,MTedatikçi";
                break;
            case 74://banka->cari
                firstId = selectedBankAccount.getId();
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedObject.getFinancingDocument().getBranch());
                bookType = "Tedarikçi,MTedatikçi";
                break;

            default:
                break;
        }
    }

    public void changeTransferBranch() {
        if (selectedObject.getFinancingDocument().getFinancingType().getId() == 51) {
            listOfBankAccount2 = bankAccountService.bankAccountForSelect("", selectedObject.getFinancingDocument().getTransferBranch());
        }
    }

    public void createBankAccountCommission() {
        isCommission = true;
        selectedBankAccountCommission = new BankAccountCommission();
        selectedBankAccountCommission.setCommissionRate(selectedBankAccount.getBankAccountBranchCon().getCommissionRate());
        RequestContext.getCurrentInstance().update("dlgCommission");
        RequestContext.getCurrentInstance().execute("PF('dlg_Commission').show();");
    }

    public void calculateCommission() {
        BigDecimal commission = BigDecimal.valueOf(0);
        if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionRate() != null) {
            commission = selectedBankAccountCommission.getTotalMoney().multiply(selectedBankAccountCommission.getCommissionRate());
            commission = commission.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
        }
        selectedBankAccountCommission.setCommissionMoney(commission);
    }

    public void calculateCommissionRate() {
        BigDecimal commissionRate = BigDecimal.valueOf(0);
        if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionMoney() != null) {
            commissionRate = BigDecimal.valueOf(100).multiply(selectedBankAccountCommission.getCommissionMoney());
            commissionRate = commissionRate.divide(selectedBankAccountCommission.getTotalMoney(), 4, RoundingMode.HALF_EVEN);
        }
        selectedBankAccountCommission.setCommissionRate(commissionRate);
    }

    public void closeCommissionDialog() {
        isCommission = false;
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
                RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmMovementDataTable:dtbMovement");
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
            context.update("tbvBankAccountProc:frmMovementDataTable:dtbMovement");
        }
        sessionBean.createUpdateMessage(result);
    }

}
