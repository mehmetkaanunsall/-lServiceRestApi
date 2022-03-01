/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 05:18:31
 */
package com.mepsan.marwiz.finance.safe.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.finance.safe.business.ISafeMovementService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportService;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.MarketShiftPayment;
import com.mepsan.marwiz.general.report.safeextract.presentation.SafeExtractBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
public class SafeMovementTabBean extends GeneralReportBean<SafeMovement> {

    @ManagedProperty(value = "#{safeMovementService}")
    public ISafeMovementService safeMovementService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{marketShiftReportService}")
    public IMarketShiftReportService marketShiftReportService;

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{creditService}")
    private ICreditService creditService;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private Safe safe;
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
    private boolean isExtract;
    private boolean isUpdate;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private int response;

    private boolean isIncomeExpense;
    private String beanName;
    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    private List<Safe> safeList;
    String safeString;
    private List<SafeMovement> listOfTotal;
    private List<Safe> listOfSafe2;
    private int financingTypeId;

    public void setSafeMovementService(ISafeMovementService safeMovementService) {
        this.safeMovementService = safeMovementService;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
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

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public List<Type> getListOfFinancingType() {
        return listOfFinancingType;
    }

    public void setListOfFinancingType(List<Type> listOfFinancingType) {
        this.listOfFinancingType = listOfFinancingType;
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

    public void setMarketShiftReportService(IMarketShiftReportService marketShiftReportService) {
        this.marketShiftReportService = marketShiftReportService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
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

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
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

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
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

    public List<SafeMovement> getListOfTotal() {
        return listOfTotal;
    }

    public void setListOfTotal(List<SafeMovement> listOfTotal) {
        this.listOfTotal = listOfTotal;
    }

    public List<Safe> getListOfSafe2() {
        return listOfSafe2;
    }

    public void setListOfSafe2(List<Safe> listOfSafe2) {
        this.listOfSafe2 = listOfSafe2;
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

    @PostConstruct

    @Override
    public void init() {
        System.out.println("----------------------SafeMovementTabBean");
        safe = new Safe();
        safeString = "";
        listOfBranch = new ArrayList<>();
        safeList = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        listOfTotal = new ArrayList<>();
        listOfSafe2 = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof SafeMovement) { // ekstre sayfasından geldiyse
                    processType = 2;
                    safe = ((SafeMovement) ((ArrayList) sessionBean.parameter).get(i)).getSafe();
                    safeList.addAll(((SafeMovement) ((ArrayList) sessionBean.parameter).get(i)).getListOfSafe());
                    selectedBranchList.addAll(((SafeMovement) ((ArrayList) sessionBean.parameter).get(i)).getListOfBranch());
                    beginDate = ((SafeMovement) ((ArrayList) sessionBean.parameter).get(i)).getBeginDate();
                    endDate = ((SafeMovement) ((ArrayList) sessionBean.parameter).get(i)).getEndDate();
                    isExtract = true;
                    break;

                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Safe) {
                    processType = 2;
                    safe = (Safe) ((ArrayList) sessionBean.parameter).get(i);
                    safeList.add((Safe) ((ArrayList) sessionBean.parameter).get(i));
                    selectedBranchList.add(sessionBean.getUser().getLastBranch());
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
                    isExtract = false;
                    break;

                }
            }
        }

        for (Safe sf : safeList) {
            safeString = safeString + "," + String.valueOf(sf.getId());
        }

        if (!safeString.equals("")) {
            safeString = safeString.substring(1, safeString.length());
        }

        // System.out.println("------beginDate----" + beginDate);
        // System.out.println("------endDate----" + endDate);
        selectedObject = new SafeMovement();
        setOpType(3);
        selectedObject.getFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
        find();
        if (isExtract) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(false, false, true, true, true, true, true, true, true, true, true, true, true);
        }

        listOfSafe = new ArrayList<>();
        listOfType = new ArrayList<>();
        listOfFinancingType = new ArrayList<>();
        inCurrency = new Currency();
        outCurrency = new Currency();
        controlDeleteList = new ArrayList<>();
        beanName = "safeMovementTabBean";
        listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        for (Type type : sessionBean.getTypes(20)) {
            if (type.getId() == 47 || type.getId() == 48 || type.getId() == 52 || type.getId() == 53 || type.getId() == 54) {
                listOfType.add(type);
                listOfFinancingType.add(type);
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{119, 120, 121}, 0));

    }

    @Override
    public void find() {
        isFind = true;
        DataTable dataTable;
        if (isExtract) {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMovementDataTable:dtbMovement");
        } else {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("tbvSafeProc:frmMovementDataTable:dtbMovement");

        }
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(" ");
    }

    @Override
    public void create() {
        processType = 1;
        isUpdate = true;
        listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());
        isIncomeExpense = false;
        selectedObject = new SafeMovement();
        selectedObject.getFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
        selectedObject.getFinancingDocument().getTransferBranch().setId(sessionBean.getUser().getLastBranch().getId());
        resetFinancing();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_financingdocumentproc').show();");
        context.update("tbvSafeProc:frmFinancingDocumentProcess");

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
            }

            if (!isThere) {
                if (processType == 1) {
                    selectedObject.getFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
                    if (selectedObject.getFinancingDocument().getFinancingType().getId() == 52
                              || selectedObject.getFinancingDocument().getFinancingType().getId() == 53 || selectedObject.getFinancingDocument().getFinancingType().getId() == 54) {
                        if (selectedObject.getFinancingDocument().getBranch().getId() == selectedObject.getFinancingDocument().getTransferBranch().getId()) {
                            selectedObject.getFinancingDocument().getTransferBranch().setId(0);
                        }
                    }

                    result = financingDocumentService.create(selectedObject.getFinancingDocument(), firstId, secondId);
                    if (result > 0) {
                        selectedObject.getFinancingDocument().setId(result);
                    }
                } else { //Güncelleme
                    result = financingDocumentService.update(selectedObject.getFinancingDocument(), firstId, secondId);
                }

                if (result > 0) {
                    listOfObjects = findall(" ");
                    context.execute("PF('dlg_financingdocumentproc').hide();");
                    context.update("tbvSafeProc:frmMovementDataTable:dtbMovement");
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

    @Override
    public LazyDataModel<SafeMovement> findall(String where) {
        return new CentrowizLazyDataModel<SafeMovement>() {
            @Override
            public List<SafeMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<SafeMovement> result = safeMovementService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, safeString, selectedBranchList, opType, beginDate, endDate, financingTypeId);
                listOfTotal = safeMovementService.count(where, safeString, selectedBranchList, opType, beginDate, endDate, financingTypeId);

                int count = 0;
                for (SafeMovement sm : listOfTotal) {
                    count += sm.getId();
                }
                listOfObjects.setRowCount(count);
                SafeMovement safeMovement = new SafeMovement();
                if (!listOfTotal.isEmpty()) {
                    safeMovement = listOfTotal.get(0);
                }
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                if (!isExtract) {
                    hesapla(safeMovement);
                }

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

    public void hesapla(SafeMovement safeMovement) {
        totalBalance = BigDecimal.valueOf(0);
        transferringBalance = BigDecimal.valueOf(0);
        totalOutcoming = BigDecimal.valueOf(0);
        totalIncoming = BigDecimal.valueOf(0);

        if (safeMovement.getTransferringbalance() != null) {
            transferringBalance = safeMovement.getTransferringbalance();
        }
        if (safeMovement.getTotalIncoming() != null) {
            totalIncoming = safeMovement.getTotalIncoming();
        }
        if (safeMovement.getTotalOutcoming() != null) {
            totalOutcoming = safeMovement.getTotalOutcoming();
        }
        totalBalance = transferringBalance.add(totalIncoming).subtract(totalOutcoming);

    }

    public String calculateTotal(int type) {
        HashMap<Integer, BigDecimal> groupCurrencyTotal = new HashMap<>();
        groupCurrencyTotal.clear();
        String total = "";

        if (type == 0) {
            for (SafeMovement sm : listOfTotal) {
                if (groupCurrencyTotal.containsKey(sm.getSafe().getCurrency().getId())) {
                    BigDecimal old = groupCurrencyTotal.get(sm.getSafe().getCurrency().getId());
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), old.add(sm.getTotalIncoming()));
                } else {
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), sm.getTotalIncoming());
                }

            }
        } else if (type == 1) {
            for (SafeMovement sm : listOfTotal) {
                if (groupCurrencyTotal.containsKey(sm.getSafe().getCurrency().getId())) {
                    BigDecimal old = groupCurrencyTotal.get(sm.getSafe().getCurrency().getId());
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), old.add(sm.getTotalOutcoming()));
                } else {
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), sm.getTotalOutcoming());
                }

            }
        } else if (type == 2) {
            for (SafeMovement sm : listOfTotal) {
                if (groupCurrencyTotal.containsKey(sm.getSafe().getCurrency().getId())) {
                    BigDecimal old = groupCurrencyTotal.get(sm.getSafe().getCurrency().getId());
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), old.add(sm.getTotalIncoming().subtract(sm.getTotalOutcoming())));
                } else {
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), sm.getTotalIncoming().subtract(sm.getTotalOutcoming()));
                }

            }
        } else if (type == 3) {
            for (SafeMovement sm : listOfTotal) {
                if (groupCurrencyTotal.containsKey(sm.getSafe().getCurrency().getId())) {
                    BigDecimal old = groupCurrencyTotal.get(sm.getSafe().getCurrency().getId());
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), old.add(sm.getTransferringbalance()));
                } else {
                    groupCurrencyTotal.put(sm.getSafe().getCurrency().getId(), sm.getTransferringbalance());
                }

            }
        }

        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotal.entrySet()) {
            if (temp == 0) {
                temp = 1;
                total += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                if (entry.getKey() != 0) {
                    total += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            } else {
                total += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                if (entry.getKey() != 0) {
                    total += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            }
        }
        return total;
    }

    /**
     * hareketin finansman belgesini getirdik.
     */
    public void getfinancingDocument() {
        if (selectedObject.getFinancingDocument().getId() > 0) {

            Shift shift = marketShiftReportService.findMarketShift(selectedObject.getFinancingDocument());
            if (shift.getId() > 0) { //Kasa Hareketleri Vardiya Sonunda Ana Kasaya Aktarıldıysa Detay Görmek İçin Satış Raporlarına Gider
                List<Object> list = new ArrayList<>();
                for (Object object : (ArrayList) sessionBean.parameter) {
                    list.add(object);
                }
                list.add(shift);
                marwiz.goToPage("/pages/general/report/salesreceiptreport/salesreceiptreport.xhtml", list, 0, 67);
            } else {//Finansman belgesini gösterir
                processType = 2;
                listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());

                isUpdate = false;

                selectedObject.getFinancingDocument().getBranch().setId(sessionBean.getUser().getLastBranch().getId());
                selectedObject.setFinancingDocument(financingDocumentService.findFinancingDocument(selectedObject.getFinancingDocument()));
                if (selectedObject.getFinancingDocument().getTransferBranch().getId() == 0) {
                    selectedObject.getFinancingDocument().getTransferBranch().setId(selectedObject.getFinancingDocument().getBranch().getId());
                }
                changeTransferBranch();
                listOfSafe2 = safeService.selectSafe(selectedObject.getFinancingDocument().getTransferBranch());
                if (selectedObject.getFinancingDocument().getIncomeExpense().getId() > 0) {
                    isIncomeExpense = true;
                } else {
                    isIncomeExpense = false;
                }
                if (marwiz.getPageIdOfGoToPage() == 71) {//Kasa Ekstre
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
                    if (selectedObject.isIsDirection()) {
                        selectedObject.getFinancingDocument().setCurrency(outCurrency);
                    } else {
                        selectedObject.getFinancingDocument().setCurrency(outCurrency);
                    }
                    exchange = currencyCode(outCurrency.getId()) + " -> " + currencyCode(inCurrency.getId());// örn: $->€
                } else {
                    exchange = "";
                }

                RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

                RequestContext context = RequestContext.getCurrentInstance();
                context.update("dlgFinancingDocumentProc");
                context.execute("PF('dlg_financingdocumentproc').show();");
                context.update("tbvSafeProc:frmFinancingDocumentProcess");
            }

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
                default:
                    break;

            }
            accountBookFilterBean.setSelectedData(null);
        } else if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            selectedObject.getFinancingDocument().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
                case 47://cari->kasa
                    firstId = selectedObject.getFinancingDocument().getIncomeExpense().getId();
                    break;
                case 48://kasa->cari
                    secondId = selectedObject.getFinancingDocument().getIncomeExpense().getId();
                    break;
                default:
                    break;
            }
            incomeExpenseBookFilterBean.setSelectedData(null);
        }

        RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
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
                if (isIncomeExpense) {
                    firstId = financingDocument.getIncomeExpense().getId();
                } else {
                    firstId = financingDocument.getAccount().getId();
                }
                secondId = financingDocument.getInMovementId();
                break;
            case 48://kasa->cari
                firstId = financingDocument.getOutMovementId();
                if (isIncomeExpense) {
                    secondId = financingDocument.getIncomeExpense().getId();
                } else {
                    secondId = financingDocument.getAccount().getId();
                }
                break;
            case 52://kasa->kasa
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
            default:
                break;

        }

    }

    public void bringTempCurrency() {
        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                outCurrency.setId(safe.getCurrency().getId());
                break;
            case 48://kasa->cari
                outCurrency.setId(safe.getCurrency().getId());
                inCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 52://kasa->kasa

                for (Safe safe : listOfSafe) {
                    if (safe.getId() == firstId) {
                        outCurrency = safe.getCurrency();
                    }
                }
                for (Safe safe : listOfSafe2) {
                    if (safe.getId() == secondId) {
                        inCurrency = safe.getCurrency();
                    }
                }

                break;
            case 53://kasa->banka
                outCurrency.setId(safe.getCurrency().getId());
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        inCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                break;
            case 54://banka->kasa
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
                        outCurrency.setId(ba.getCurrency().getId());
                        break;
                    }
                }
                inCurrency.setId(safe.getCurrency().getId());

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
            exchange = currencyCode(outCurrency.getId()) + " -> " + currencyCode(inCurrency.getId());// örn: $->€
            selectedObject.getFinancingDocument().setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama

        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
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

        RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:txtCurrentName");
        RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:txtCurrentName2");

        changeTransferBranch();

        switch (selectedObject.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
                bookType = "Musteri,MTedatikçi";
                secondId = safe.getId();
                break;
            case 48://kasa->cari
                bookType = "Tedarikçi,MTedatikçi";
                firstId = safe.getId();
                break;
            case 52://kasa->kasa
                firstId = safe.getId();
                listOfSafe2 = safeService.selectSafe(listOfBranch);
                break;
            case 53://kasa->banka
                firstId = safe.getId();
                break;
            case 54://banka->kasa
                secondId = safe.getId();
                break;
            default:
                break;
        }
        bringCurrency();

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
            safeMovementService.exportPdf(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, safeList, selectedBranchList, true, calculateTotal(0), calculateTotal(1), calculateTotal(2), calculateTotal(3));

        } else {
            safeMovementService.exportPdf(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, safeList, selectedBranchList, false, calculateTotal(0), calculateTotal(1), calculateTotal(2), calculateTotal(3));
        }
    }

    public void createExcel() {
        if (isExtract) {
            safeMovementService.exportExcel(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, safeList, selectedBranchList, true, calculateTotal(0), calculateTotal(1), calculateTotal(2), calculateTotal(3));

        } else {
            safeMovementService.exportExcel(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, safeList, selectedBranchList, false, calculateTotal(0), calculateTotal(1), calculateTotal(2), calculateTotal(3));
        }
    }

    public void createPrinter() {

        if (isExtract) {
            RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(safeMovementService.exportPrinter(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, safeList, selectedBranchList, true, calculateTotal(0), calculateTotal(1), calculateTotal(2), calculateTotal(3))) + "');$(\"#printerPanel\").css('display','block');print_pageMovement();$(\"#printerPanel\").css('display','none');");

        } else {
            RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(safeMovementService.exportPrinter(" ", toogleList, transferringBalance, totalIncoming, totalOutcoming, totalBalance, opType, beginDate, endDate, financingTypeId, safeList, selectedBranchList, false, calculateTotal(0), calculateTotal(1), calculateTotal(2), calculateTotal(3))) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
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
                        case -109: //faturaya bağlı
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleasedeletetheinvoicefromtheinvoicepage");
                            deleteControlMessage2 = sessionBean.getLoc().getString("documentno") + " : " + controlDeleteList.get(0).getR_recordno();
                            break;
                        case -110:
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtocloseshiftitcannotbedeleted");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleasereopentheshift");
                            deleteControlMessage2 = sessionBean.getLoc().getString("shiftno") + " : " + controlDeleteList.get(0).getR_recordno();
                        default:
                            break;
                    }
                    if (response == -104) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date processDate = null;
                        try {
                            processDate = formatter.parse(controlDeleteList.get(0).getR_recordno());
                        } catch (ParseException ex) {
                            Logger.getLogger(SafeMovementTabBean.class.getName()).log(Level.SEVERE, null, ex);
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
                    RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:dlgDelete");
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
            context.update("tbvSafeProc:frmMovementDataTable:dtbMovement");
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
            case -110:
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

    public void changeSafe(int type) {
        if (firstId == secondId) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("betweensamesafescannotbetransferred")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

            if (type == 2) {
                secondId = 0;
                RequestContext.getCurrentInstance().update("tbvSafeProc:frmFinancingDocumentProcess:slcSafe3");
            }
        }
    }

    public void changeTransferBranch() {
        if (selectedObject.getFinancingDocument().getFinancingType().getId() == 53) {
            listOfBankAccount = bankAccountService.bankAccountForSelect("", selectedObject.getFinancingDocument().getTransferBranch());
        }
    }
}
