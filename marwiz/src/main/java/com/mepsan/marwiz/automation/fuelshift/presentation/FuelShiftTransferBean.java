/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 03:32:36
 */
package com.mepsan.marwiz.automation.fuelshift.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.bankaccount.business.BankAccountService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.safe.business.SafeService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
public class FuelShiftTransferBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    private IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{bankAccountService}")
    private BankAccountService bankAccountService;

    @ManagedProperty(value = "#{safeService}")
    private SafeService safeService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{creditService}")
    public ICreditService creditService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private List<FuelShiftSales> listOfAttendant;
    private List<FuelShiftSales> listOfTempAttendant;
    private List<FuelShiftSales> listOfAttendantNotInSystem;
    List<ShiftPayment> listOfShiftPayment;
    private int activeIndex;
    private ShiftPayment selectedShiftPayment;
    private FuelShiftSales activeTabFuelShiftSale;
    private boolean isIncome;
    private FuelShift selectedShift;
    private BranchSetting branchSetting;
    private String exchange;

    List<BankAccount> listOfBankAccount;
    List<BankAccount> listOfBankAccountGeneral;
    List<Safe> listOfSafe;
    List<Safe> listOfSafeGeneral;
    private BigDecimal totalShiftSale, totalShiftPayment, totalRemaining;
    private int processType;
    private ShiftPayment selectedFinancingDocument;

    private int firstId, secondId;
    private Currency inCurrency, outCurrency;
    private List<Type> listOfType;
    private boolean isIncomeExpense;
    private String messageFinancing;
    private int ftype;
    private String beanName;
    private boolean isDialog;
    private BigDecimal overallTotalShiftPayment;
    private List<FuelShiftSales> listOfSalesToSaleType;
    private List<FuelShiftSales> listOfTempSalesToSaleType;
    private boolean isDeleteShift;
    private boolean isCredit;
    List<CheckDelete> controlDeleteList;
    List<FuelShiftSales> listOfTestSales;
    List<FuelShiftSales> listOfAll;
    List<FuelShiftSales> listOfAutomationSales;
    private boolean isAutoRun;
    private boolean isAutomationTab, isOkey;
    private boolean isApproveShiftExcel, isApproveShiftPdf, isPreview;

    private String updateCondition;
    private boolean isAttendant;
    private String bookType;
    private boolean isFinancingDoc;
    private boolean isUpdate;
    private BigDecimal oldValue;
    private BigDecimal cashPrice, creditCardPrice, incomeExpensePrice, paroPointPrice, accountCollectionPaymentPrice;
    private boolean isDestroy;
    private boolean isParoPoint;
    private List<Branch> listOfBranch;
    private boolean isAccountCollectionPayment;
    private boolean isAccountCollection;

    public int getFtype() {
        return ftype;
    }

    public void setFtype(int ftype) {
        this.ftype = ftype;
    }

    public String getMessageFinancing() {
        return messageFinancing;
    }

    public void setMessageFinancing(String messageFinancing) {
        this.messageFinancing = messageFinancing;
    }

    public List<FuelShiftSales> getListOfAttendant() {
        return listOfAttendant;
    }

    public void setListOfAttendant(List<FuelShiftSales> listOfAttendant) {
        this.listOfAttendant = listOfAttendant;
    }

    public List<ShiftPayment> getListOfShiftPayment() {
        return listOfShiftPayment;
    }

    public void setListOfShiftPayment(List<ShiftPayment> listOfShiftPayment) {
        this.listOfShiftPayment = listOfShiftPayment;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public ShiftPayment getSelectedShiftPayment() {
        return selectedShiftPayment;
    }

    public void setSelectedShiftPayment(ShiftPayment selectedShiftPayment) {
        this.selectedShiftPayment = selectedShiftPayment;
    }

    public boolean isIsIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }

    public List<BankAccount> getListOfBankAccountGeneral() {
        return listOfBankAccountGeneral;
    }

    public void setListOfBankAccountGeneral(List<BankAccount> listOfBankAccountGeneral) {
        this.listOfBankAccountGeneral = listOfBankAccountGeneral;
    }

    public FuelShiftSales getActiveTabFuelShiftSale() {
        return activeTabFuelShiftSale;
    }

    public void setActiveTabFuelShiftSale(FuelShiftSales activeTabFuelShiftSale) {
        this.activeTabFuelShiftSale = activeTabFuelShiftSale;
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

    public List<Safe> getListOfSafeGeneral() {
        return listOfSafeGeneral;
    }

    public void setListOfSafeGeneral(List<Safe> listOfSafeGeneral) {
        this.listOfSafeGeneral = listOfSafeGeneral;
    }

    public void setBankAccountService(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setSafeService(SafeService safeService) {
        this.safeService = safeService;
    }

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public BigDecimal getTotalShiftSale() {
        return totalShiftSale;
    }

    public void setTotalShiftSale(BigDecimal totalShiftSale) {
        this.totalShiftSale = totalShiftSale;
    }

    public BigDecimal getTotalShiftPayment() {
        return totalShiftPayment;
    }

    public void setTotalShiftPayment(BigDecimal totalShiftPayment) {
        this.totalShiftPayment = totalShiftPayment;
    }

    public BigDecimal getTotalRemaining() {
        return totalRemaining;
    }

    public void setTotalRemaining(BigDecimal totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public ShiftPayment getSelectedFinancingDocument() {
        return selectedFinancingDocument;
    }

    public void setSelectedFinancingDocument(ShiftPayment selectedFinancingDocument) {
        this.selectedFinancingDocument = selectedFinancingDocument;
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

    public Currency getInCurrency() {
        return inCurrency;
    }

    public void setInCurrency(Currency inCurrency) {
        this.inCurrency = inCurrency;
    }

    public Currency getOutCurrency() {
        return outCurrency;
    }

    public void setOutCurrency(Currency outCurrency) {
        this.outCurrency = outCurrency;
    }

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
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

    public boolean isIsDialog() {
        return isDialog;
    }

    public void setIsDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    public FuelShift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(FuelShift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public List<FuelShiftSales> getListOfAttendantNotInSystem() {
        return listOfAttendantNotInSystem;
    }

    public void setListOfAttendantNotInSystem(List<FuelShiftSales> listOfAttendantNotInSystem) {
        this.listOfAttendantNotInSystem = listOfAttendantNotInSystem;
    }

    public BigDecimal getOverallTotalShiftPayment() {
        return overallTotalShiftPayment;
    }

    public void setOverallTotalShiftPayment(BigDecimal overallTotalShiftPayment) {
        this.overallTotalShiftPayment = overallTotalShiftPayment;
    }

    public List<FuelShiftSales> getListOfSalesToSaleType() {
        return listOfSalesToSaleType;
    }

    public void setListOfSalesToSaleType(List<FuelShiftSales> listOfSalesToSaleType) {
        this.listOfSalesToSaleType = listOfSalesToSaleType;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public boolean isIsDeleteShift() {
        return isDeleteShift;
    }

    public void setIsDeleteShift(boolean isDeleteShift) {
        this.isDeleteShift = isDeleteShift;
    }

    public boolean isIsCredit() {
        return isCredit;
    }

    public void setIsCredit(boolean isCredit) {
        this.isCredit = isCredit;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public boolean isIsAutoRun() {
        return isAutoRun;
    }

    public void setIsAutoRun(boolean isAutoRun) {
        this.isAutoRun = isAutoRun;
    }

    public boolean isIsAutomationTab() {
        return isAutomationTab;
    }

    public void setIsAutomationTab(boolean isAutomationTab) {
        this.isAutomationTab = isAutomationTab;
    }

    public boolean isIsApproveShiftExcel() {
        return isApproveShiftExcel;
    }

    public void setIsApproveShiftExcel(boolean isApproveShiftExcel) {
        this.isApproveShiftExcel = isApproveShiftExcel;
    }

    public boolean isIsApproveShiftPdf() {
        return isApproveShiftPdf;
    }

    public void setIsApproveShiftPdf(boolean isApproveShiftPdf) {
        this.isApproveShiftPdf = isApproveShiftPdf;
    }

    public boolean isIsPreview() {
        return isPreview;
    }

    public void setIsPreview(boolean isPreview) {
        this.isPreview = isPreview;
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

    public String getUpdateCondition() {
        return updateCondition;
    }

    public void setUpdateCondition(String updateCondition) {
        this.updateCondition = updateCondition;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public boolean isIsFinancingDoc() {
        return isFinancingDoc;
    }

    public void setIsFinancingDoc(boolean isFinancingDoc) {
        this.isFinancingDoc = isFinancingDoc;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public BigDecimal getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(BigDecimal cashPrice) {
        this.cashPrice = cashPrice;
    }

    public BigDecimal getParoPointPrice() {
        return paroPointPrice;
    }

    public void setParoPointPrice(BigDecimal paroPointPrice) {
        this.paroPointPrice = paroPointPrice;
    }

    public BigDecimal getCreditCardPrice() {
        return creditCardPrice;
    }

    public void setCreditCardPrice(BigDecimal creditCardPrice) {
        this.creditCardPrice = creditCardPrice;
    }

    public BigDecimal getIncomeExpensePrice() {
        return incomeExpensePrice;
    }

    public void setIncomeExpensePrice(BigDecimal incomeExpensePrice) {
        this.incomeExpensePrice = incomeExpensePrice;
    }

    public boolean isIsDestroy() {
        return isDestroy;
    }

    public void setIsDestroy(boolean isDestroy) {
        this.isDestroy = isDestroy;
    }

    public boolean isIsParoPoint() {
        return isParoPoint;
    }

    public void setIsParoPoint(boolean isParoPoint) {
        this.isParoPoint = isParoPoint;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public boolean isIsAccountCollectionPayment() {
        return isAccountCollectionPayment;
    }

    public void setIsAccountCollectionPayment(boolean isAccountCollectionPayment) {
        this.isAccountCollectionPayment = isAccountCollectionPayment;
    }

    public boolean isIsAccountCollection() {
        return isAccountCollection;
    }

    public void setIsAccountCollection(boolean isAccountCollection) {
        this.isAccountCollection = isAccountCollection;
    }

    public BigDecimal getAccountCollectionPaymentPrice() {
        return accountCollectionPaymentPrice;
    }

    public void setAccountCollectionPaymentPrice(BigDecimal accountCollectionPaymentPrice) {
        this.accountCollectionPaymentPrice = accountCollectionPaymentPrice;
    }

    @PostConstruct
    public void init() {
        listOfAttendant = new ArrayList<>();
        selectedShiftPayment = new ShiftPayment();
        listOfTempAttendant = new ArrayList<>();
        listOfAttendantNotInSystem = new ArrayList<>();
        overallTotalShiftPayment = BigDecimal.valueOf(0);
        listOfSalesToSaleType = new ArrayList<>();
        listOfTempSalesToSaleType = new ArrayList<>();
        totalRemaining = BigDecimal.valueOf(0);
        listOfTestSales = new ArrayList<>();
        listOfAll = new ArrayList<>();
        listOfAutomationSales = new ArrayList<>();
        isDeleteShift = true;
        listOfShiftPayment = new ArrayList<>();
        isUpdate = true;
        oldValue = BigDecimal.valueOf(0);
        isDestroy = false;
        listOfBranch = new ArrayList<>();
        isAccountCollectionPayment = false;
        isAccountCollection = false;

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FuelShift) {
                    selectedShift = (FuelShift) ((ArrayList) sessionBean.parameter).get(i);
                    overallTotalShiftPayment = selectedShift.getShiftPaymentTotal();
                    branchSetting = sessionBean.getLastBranchSetting();
                    if (branchSetting.getParoUrl() != null && !branchSetting.getParoUrl().isEmpty() && !branchSetting.getParoUrl().equals("") && branchSetting.getAutomationId() == 2 && branchSetting.getAutomationScoreAccount().getId() != 0) {
                        isParoPoint = true;
                    }
                    listOfTempAttendant = fuelShiftTransferService.findAllAttendant(selectedShift, branchSetting);
                    for (FuelShiftSales f : listOfTempAttendant) {
                        boolean isThere = false;
                        if (f.getAccount().getId() != 0) {
                            for (FuelShiftSales f1 : listOfAttendant) {
                                if (f1.getAttendantCode().equals(f.getAttendantCode())) {
                                    isThere = true;
                                    break;
                                }
                            }
                            if (!isThere) {
                                listOfAttendant.add(f);
                            }
                        } else {
                            listOfAttendantNotInSystem.add(f);
                        }
                    }
                    if (!listOfAttendantNotInSystem.isEmpty()) {
                        for (FuelShiftSales f : listOfAttendantNotInSystem) {
                            if (f.getAttendant() != null) {
                                if (f.getAttendant().trim().equals("")) {
                                    FuelShiftSales fs = new FuelShiftSales();
                                    fs.getAccount().setName(sessionBean.getLoc().getString("withoutattendant"));
                                    fs.setAttendantCode(f.getAttendantCode());
                                    listOfAttendant.add(fs);

                                }
                            }
                        }
                        for (Iterator<FuelShiftSales> iterator = listOfAttendantNotInSystem.iterator(); iterator.hasNext();) {
                            FuelShiftSales next = iterator.next();
                            if (next.getAttendant().trim().equals("")) {
                                iterator.remove();
                                break;
                            }
                        }
                        if (!listOfAttendantNotInSystem.isEmpty()) {
                            isAutoRun = true; //F5 e basınca block hatası verdiği için remotecommandla açıldı diyalog
                        }
                    }

                    activeIndex = 0;
                    beanName = "fuelShiftTransferBean";
                    activeTabFuelShiftSale = new FuelShiftSales();
                    selectedFinancingDocument = new ShiftPayment();
                    listOfBankAccountGeneral = bankAccountService.bankAccountForSelect(" AND bka.currency_id=" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND bka.type_id IN(16, 104) ", sessionBean.getUser().getLastBranch());

                    listOfSafeGeneral = safeService.findSafeByCurrency(" AND sf.shiftmovementsafe_id IS NULL AND sf.currency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId());
                    /////

                    listOfAll = fuelShiftTransferService.findAllSale(selectedShift);

                    for (FuelShiftSales f : listOfAll) {
                        if (branchSetting.getAutomationId() == 1) {
                            if (f.getFuelSaleType().getTypeno() == 5) {
                                listOfTestSales.add(f);
                            } else if (f.getFuelSaleType().getTypeno() == 1 || f.getFuelSaleType().getTypeno() == 2 || f.getFuelSaleType().getTypeno() == 11
                                    || f.getFuelSaleType().getTypeno() == 12 || f.getFuelSaleType().getTypeno() == 21 || f.getFuelSaleType().getTypeno() == 22
                                    || f.getFuelSaleType().getTypeno() == 35 || f.getFuelSaleType().getTypeno() == 41 || f.getFuelSaleType().getTypeno() == 51
                                    || f.getFuelSaleType().getTypeno() == 55 || f.getFuelSaleType().getTypeno() == 56 || f.getFuelSaleType().getTypeno() == 57
                                    || f.getFuelSaleType().getTypeno() == 60 || f.getFuelSaleType().getTypeno() == 61 || f.getFuelSaleType().getTypeno() == 71
                                    || f.getFuelSaleType().getTypeno() == 81 || f.getFuelSaleType().getTypeno() == 91 || f.getFuelSaleType().getTypeno() == 101
                                    || f.getFuelSaleType().getTypeno() == 102 || f.getFuelSaleType().getTypeno() == 103 || f.getFuelSaleType().getTypeno() == 104
                                    || f.getFuelSaleType().getTypeno() == 106) {
                                listOfAutomationSales.add(f);
                            }
                        } else if (branchSetting.getAutomationId() == 2) {
                            if (f.getFuelSaleType().getTypeno() == 6) {
                                listOfTestSales.add(f);
                            } else if (f.getFuelSaleType().getTypeno() == 2 || f.getFuelSaleType().getTypeno() == 3 || f.getFuelSaleType().getTypeno() == 4 || f.getFuelSaleType().getTypeno() == 7
                                    || f.getFuelSaleType().getTypeno() == 8 || f.getFuelSaleType().getTypeno() == 9 || f.getFuelSaleType().getTypeno() == 16) {
                                listOfAutomationSales.add(f);
                            }
                        } else if (branchSetting.getAutomationId() == 4) {
                            if (f.getFuelSaleType().getTypeno() == 1 || f.getFuelSaleType().getTypeno() == 2) {
                                listOfAutomationSales.add(f);
                            }
                        } else if (branchSetting.getAutomationId() == 3) {
                            if (f.getFuelSaleType().getTypeno() == 2) {
                                listOfAutomationSales.add(f);
                            }
                        }else if (branchSetting.getAutomationId() == 5) {
                            if (f.getFuelSaleType().getTypeno() == 3) {
                                listOfAutomationSales.add(f);
                            }
                        }
                    }

                    if (!listOfAutomationSales.isEmpty()) {
                        FuelShiftSales fs = new FuelShiftSales();
                        fs.getAccount().setName(sessionBean.getLoc().getString("automationsales"));
                        fs.getAccount().setId(-2);
                        listOfAttendant.add(fs);
                    }
                    if (!listOfTestSales.isEmpty()) {

                        FuelShiftSales fsTest = new FuelShiftSales();
                        fsTest.getAccount().setName(sessionBean.getLoc().getString("testsales"));
                        fsTest.getAccount().setId(-1);
                        listOfAttendant.add(fsTest);
                    }

                    /////
                    if (!listOfAttendant.isEmpty()) {
                        onTabChange(listOfAttendant.get(0));
                    }

                    if (selectedShift.isIsConfirm()) {
                        isUpdate = false;
                    } else {
                        isUpdate = true;
                    }

                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{54}, 0));

    }

    public void onTabChange(FuelShiftSales fuelShiftSales) {
        isAutomationTab = false;
        isDestroy = false;
        selectedShiftPayment = new ShiftPayment();
        isAccountCollectionPayment = false;
        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer:tbvFuelShiftTransfer:" + activeIndex + ":pngAccountIncome");

        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer:tbvFuelShiftTransfer");
        if (fuelShiftSales.getAccount().getId() == -1) {//Test Satışları Gelecek
            listOfSalesToSaleType.clear();
            totalShiftSale = BigDecimal.valueOf(0);
            for (FuelShiftSales fuelShiftSales1 : listOfTestSales) {
                listOfSalesToSaleType.add(fuelShiftSales1);
                totalShiftSale = totalShiftSale.add(fuelShiftSales1.getTotalMoney());
            }

        } else if (fuelShiftSales.getAccount().getId() == -2) {//Otomasyon Tabı
            isAutomationTab = true;
            listOfSalesToSaleType.clear();
            listOfShiftPayment.clear();
            totalShiftPayment = BigDecimal.valueOf(0);
            totalShiftSale = BigDecimal.valueOf(0);

            for (FuelShiftSales fuelShiftSales1 : listOfAutomationSales) {
                listOfSalesToSaleType.add(fuelShiftSales1);
                totalShiftSale = totalShiftSale.add(fuelShiftSales1.getTotalMoney());
            }

            listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, new Account(), 2);

            for (ShiftPayment sp : listOfShiftPayment) {
                totalShiftPayment = totalShiftPayment.add(sp.getFinancingDocument().getPrice());
            }
        } else {
            fuelShiftSales.getFuelShift().setId(selectedShift.getId());
            fuelShiftSales.getFuelShift().setIsDeleted(selectedShift.isIsDeleted());
            fuelShiftSales.getFuelShift().setDeletedTime(selectedShift.getDeletedTime());
            listOfTempSalesToSaleType = fuelShiftTransferService.findAllAttendantSale(fuelShiftSales);
            listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, fuelShiftSales.getAccount(), 0);
            activeTabFuelShiftSale = fuelShiftSales;
            calculateTotal();
        }

        if (fuelShiftSales.getAccount().getId() == 0) {//Pompacısız
            isAttendant = false;
        } else {
            isAttendant = true;
        }

    }

    public void calculateTotal() {
        totalRemaining = BigDecimal.valueOf(0);
        totalShiftPayment = BigDecimal.valueOf(0);
        totalShiftSale = BigDecimal.valueOf(0);
        listOfSalesToSaleType.clear();
        for (FuelShiftSales f : listOfTempSalesToSaleType) {
            if (branchSetting.getAutomationId() == 1) { //Stawiz İçin İşlem Yapılacak Satış Tutarı
                if (f.getFuelSaleType().getTypeno() == 0 || f.getFuelSaleType().getTypeno() == 3 || f.getFuelSaleType().getTypeno() == 4
                        || f.getFuelSaleType().getTypeno() == 6 || f.getFuelSaleType().getTypeno() == 8 || f.getFuelSaleType().getTypeno() == 9
                        || f.getFuelSaleType().getTypeno() == 18 || f.getFuelSaleType().getTypeno() == 19) {
                    totalShiftSale = totalShiftSale.add(f.getTotalMoney());
                    listOfSalesToSaleType.add(f);
                }
            } else if (branchSetting.getAutomationId() == 2) { //Türpak İçin İşlem Yapılacak Satış Tutarı
                if (f.getFuelSaleType().getTypeno() == 1 || f.getFuelSaleType().getTypeno() == 5
                        || f.getFuelSaleType().getTypeno() == 10 || f.getFuelSaleType().getTypeno() == 11
                        || f.getFuelSaleType().getTypeno() == 18 || f.getFuelSaleType().getTypeno() == 99 || f.getFuelSaleType().getTypeno() == 98) {
                    totalShiftSale = totalShiftSale.add(f.getTotalMoney());
                    listOfSalesToSaleType.add(f);
                }
            } else if (branchSetting.getAutomationId() == 4) { //Stawiz İçin İşlem Yapılacak Satış Tutarı
                if (f.getFuelSaleType().getTypeno() == 0) {
                    totalShiftSale = totalShiftSale.add(f.getTotalMoney());
                    listOfSalesToSaleType.add(f);
                }
            } else if (branchSetting.getAutomationId() == 3) { //Asis İçin İşlem Yapılacak Satış Tutarı
                if (f.getFuelSaleType().getTypeno() == 1) {
                    totalShiftSale = totalShiftSale.add(f.getTotalMoney());
                    listOfSalesToSaleType.add(f);
                }
            }else if (branchSetting.getAutomationId() == 5) { //Turpak Shell İçin İşlem Yapılacak Satış Tutarı
                if (f.getFuelSaleType().getTypeno() == 1 || f.getFuelSaleType().getTypeno() == 2) {
                    totalShiftSale = totalShiftSale.add(f.getTotalMoney());
                    listOfSalesToSaleType.add(f);
                }
            }
        }
        for (ShiftPayment s : listOfShiftPayment) {
            if (s.getBankAccount().getId() > 0 || s.getSafe().getId() > 0 || (s.getFinancingDocument().getIncomeExpense().getId() > 0 && !s.getFinancingDocument().getIncomeExpense().isIsIncome())
                    || (s.getFinancingDocument().getIncomeExpense().getId() == 0 && s.getFinancingDocument().getFinancingType().getId() == 49)
                    || s.getCredit().getAccount().getId() > 0
                    || (s.getFuelSaleType().getTypeno() == 98 && branchSetting.getAutomationId() == 2)) {
                totalShiftPayment = totalShiftPayment.add(s.getFinancingDocument().getPrice());
            } else {
                totalShiftPayment = totalShiftPayment.subtract(s.getFinancingDocument().getPrice());
            }
        }
        totalRemaining = totalShiftSale.subtract(totalShiftPayment);
    }

    public int compareShiftPayment() {
        if (totalRemaining.compareTo(BigDecimal.valueOf(0)) == 1) {
            return 0;
        } else if (totalRemaining.compareTo(BigDecimal.valueOf(0)) == -1) {
            return 1;
        } else {
            return 2;
        }
    }

    public void saveShiftPayment(int type) {
        int result = 0;
        if (type == 0 && selectedShiftPayment.getSafe().getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectsafe")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 1 && selectedShiftPayment.getBankAccount().getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectbank")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 2 && selectedShiftPayment.getFinancingDocument().getIncomeExpense().getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectincomeexpense")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 0 && (cashPrice == null || cashPrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 1 && (creditCardPrice == null || creditCardPrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 2 && (incomeExpensePrice == null || incomeExpensePrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 3 && (paroPointPrice == null || paroPointPrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 4 && selectedShiftPayment.getFinancingDocument().getAccount().getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectaccount")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 4 && (accountCollectionPaymentPrice == null || accountCollectionPaymentPrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            if (type != 3) {
                SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
                String dateformat = format.format(selectedShift.getBeginDate());

                selectedShiftPayment.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shift") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");

                selectedShiftPayment.getFinancingDocument().setDocumentDate(selectedShift.getBeginDate());
                selectedShiftPayment.getShift().setId(selectedShift.getId());
                selectedShiftPayment.getAccount().setId(activeTabFuelShiftSale.getAccount().getId());

                if (type == 0) {//Kasa
                    selectedShiftPayment.getFinancingDocument().getIncomeExpense().setId(0);
                    selectedShiftPayment.getBankAccount().setId(0);
                    selectedShiftPayment.getFinancingDocument().getFinancingType().setId(47);//kasaya para geldi
                    selectedShiftPayment.getFinancingDocument().setPrice(cashPrice);
                } else if (type == 1) {//Banka
                    selectedShiftPayment.getFinancingDocument().getIncomeExpense().setId(0);
                    selectedShiftPayment.getSafe().setId(0);
                    selectedShiftPayment.getFinancingDocument().getFinancingType().setId(73);//bankaya para girdi
                    selectedShiftPayment.getFinancingDocument().setPrice(creditCardPrice);
                } else if (type == 2) {//Gelir-Gider
                    selectedShiftPayment.getBankAccount().setId(0);
                    selectedShiftPayment.getSafe().setId(0);
                    if (selectedShiftPayment.getFinancingDocument().getIncomeExpense().isIsIncome()) {//Gelir
                        selectedShiftPayment.getFinancingDocument().getFinancingType().setId(50);
                    } else {//Gider
                        selectedShiftPayment.getFinancingDocument().getFinancingType().setId(49);
                    }
                    selectedShiftPayment.getFinancingDocument().setPrice(incomeExpensePrice);
                } else if (type == 4) { //Cari Tahsilat- Ödeme
                    selectedShiftPayment.getFinancingDocument().getIncomeExpense().setId(0);
                    selectedShiftPayment.getSafe().setId(0);
                    selectedShiftPayment.getBankAccount().setId(0);
                    selectedShiftPayment.setAttendantAccount(new Account());
                    selectedShiftPayment.getAttendantAccount().setId(activeTabFuelShiftSale.getAccount().getId());
                    selectedShiftPayment.setAccount(selectedShiftPayment.getFinancingDocument().getAccount());

                    if (!isAccountCollection) {//tahsilat
                        selectedShiftPayment.getFinancingDocument().getFinancingType().setId(50);
                    } else {//Ödeme
                        selectedShiftPayment.getFinancingDocument().getFinancingType().setId(49);
                    }

                    selectedShiftPayment.getFinancingDocument().setPrice(accountCollectionPaymentPrice);

                }

                if (sessionBean.isPeriodClosed(selectedShiftPayment.getFinancingDocument().getDocumentDate())) {
                    result = fuelShiftTransferService.createFinDocAndShiftPayment(1, selectedShiftPayment, "");

                    if (result > 0) {
                        listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, activeTabFuelShiftSale.getAccount(), 0);
                        calculateTotalShiftPayment(selectedShiftPayment, 1);
                        calculateTotal();
                        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                    }
                    sessionBean.createUpdateMessage(result);

                }
            } else if (type == 3) {
                SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
                String dateformat = format.format(selectedShift.getBeginDate());

                selectedShiftPayment.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shift") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");

                selectedShiftPayment.getFinancingDocument().setDocumentDate(selectedShift.getBeginDate());
                selectedShiftPayment.getShift().setId(selectedShift.getId());
                selectedShiftPayment.getAccount().setId(activeTabFuelShiftSale.getAccount().getId());
                selectedShiftPayment.getFinancingDocument().getFinancingType().setId(49);
                selectedShiftPayment.getFinancingDocument().setPrice(paroPointPrice);

                if (sessionBean.isPeriodClosed(selectedShiftPayment.getFinancingDocument().getDocumentDate())) {
                    result = fuelShiftTransferService.createFinDocAndShiftPayment(5, selectedShiftPayment, "");

                    if (result > 0) {
                        listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, activeTabFuelShiftSale.getAccount(), 0);
                        calculateTotalShiftPayment(selectedShiftPayment, 1);
                        calculateTotal();
                        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                    }
                    sessionBean.createUpdateMessage(result);

                }
            }

            selectedShiftPayment = new ShiftPayment();
            incomeExpensePrice = null;
            cashPrice = null;
            creditCardPrice = null;
            paroPointPrice = null;
            accountCollectionPaymentPrice = null;

        }

    }

    public void incomeOrExpense() {
        if (isIncome) {
            isIncome = false;
        } else {
            isIncome = true;
        }

    }

    public void updateAllInformation() {
        if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            if (isDialog) {

                selectedFinancingDocument.getFinancingDocument().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
                RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
            } else {
                selectedShiftPayment.getFinancingDocument().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
                RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
            }

            incomeExpenseBookFilterBean.setSelectedData(null);
        } else if (accountBookFilterBean.getSelectedData() != null) {

            if (accountBookFilterBean.getType().equals("accountCollectionPayment") && !isFinancingDoc) {

                selectedShiftPayment.getFinancingDocument().setAccount(accountBookFilterBean.getSelectedData());
                accountBookFilterBean.setSelectedData(null);
                RequestContext.getCurrentInstance().update("frmFuelShiftTransfer:tbvFuelShiftTransfer:" + activeIndex + ":slcCollectionPayment");
            } else {

                selectedFinancingDocument.getFinancingDocument().setAccount(accountBookFilterBean.getSelectedData());
                accountBookFilterBean.setSelectedData(null);
                switch (selectedFinancingDocument.getFinancingDocument().getFinancingType().getId()) {
                    case 47://cari->kasa
                        firstId = selectedFinancingDocument.getFinancingDocument().getAccount().getId();
                        break;
                    case 49://borc dekontu
                        firstId = selectedFinancingDocument.getFinancingDocument().getAccount().getId();
                        bringCurrency();
                        break;
                    case 50://alacak dekontu
                        secondId = selectedFinancingDocument.getFinancingDocument().getAccount().getId();
                        bringCurrency();
                        break;
                    case 55://cari->banka
                    case 73:
                        firstId = selectedFinancingDocument.getFinancingDocument().getAccount().getId();
                        break;
                    default:
                        break;

                }
                RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
                accountBookFilterBean.setSelectedData(null);

            }

        }

    }

    /**
     * hareketin finansman belgesini getirdik.
     */
    public void getfinancingDocument() {
        bookType = "";
        if (!selectedShift.isIsDeleted()) { //VArdiya Silinmesiyse işlemlere izin verir
            processType = 2;
            isDeleteShift = false;
            listOfType = sessionBean.getTypes(20);
            selectedFinancingDocument.getShift().setId(selectedShift.getId());
            RequestContext context = RequestContext.getCurrentInstance();
            if (selectedFinancingDocument.getFinancingDocument().getId() > 0) {//finansman belgesi ise
                isDialog = true;
                isFinancingDoc = true;

                isDestroy = true;
                RequestContext.getCurrentInstance().update("pngBook");
                selectedFinancingDocument.setFinancingDocument(financingDocumentService.findFinancingDocument(selectedFinancingDocument.getFinancingDocument()));
                if ((selectedFinancingDocument.getFinancingDocument().getFinancingType().getId() == 49
                        || selectedFinancingDocument.getFinancingDocument().getFinancingType().getId() == 50) && selectedFinancingDocument.getFinancingDocument().getAccount().getId() != 0) {
                    if (!isAttendant) {//cari değişebilir
                        updateCondition = "fuelShiftTransferBeanForAccount";
                        if (selectedFinancingDocument.getFinancingDocument().getAccount().isIsEmployee()) { //Personel hareketi ise
                            bookType = "fuelshiftattendant";
                        } else { //Cari tahsilat ödeme ise
                            bookType = "accountCollectionPayment";
                        }

                    } else {

                        if (selectedFinancingDocument.getFinancingDocument().getAccount().isIsEmployee()) { //Personel hareketi ise
                            updateCondition = "fuelShiftTransferBean";
                        } else { //Cari tahsilat ödeme ise
                            updateCondition = "fuelShiftTransferBeanForAccount";
                        }

                    }
                } else {

                    if (selectedFinancingDocument.getFuelSaleType().getTypeno() == 3 || selectedFinancingDocument.getFuelSaleType().getTypeno() == 4) {
                        updateCondition = "fuelShiftTransferBeanForSpecialSaleType";
                    } else {
                        updateCondition = "fuelShiftTransferBean";
                    }

                }

                if (selectedFinancingDocument.getFinancingDocument().getIncomeExpense().getId() > 0) {
                    isIncomeExpense = true;
                } else {
                    isIncomeExpense = false;
                }

                if (selectedFinancingDocument.getFuelSaleType().getTypeno() == 99) {
                    isUpdate = false;
                }
                changeBranch();
                bringFinancingDocument(selectedFinancingDocument.getFinancingDocument());
                bringCurrency();
                oldValue = selectedFinancingDocument.getFinancingDocument().getPrice();
                context.execute("PF('dlg_FinancingDocument').show();");

            } else if (selectedFinancingDocument.getCredit().getId() > 0) {//Kredi
                isCredit = true;
                context.update("dlgPostPaid");
                context.execute("PF('dlg_PostPaid').show();");
            }
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
            case 55:
            case 73:
                //cari->banka
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

    /**
     * bu metot para cıkısı olan birimin currency objesini kuru hesaplamak için
     * bulur.
     *
     * tarafın dövizini hesapla 2:iki tarafın dövizini hesapla
     */
    public void bringCurrency() {

        switch (selectedFinancingDocument.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == secondId) {
                        outCurrency = safe.getCurrency();
                        break;
                    }
                }
                break;
            case 48://kasa->cari
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == firstId) {
                        outCurrency = safe.getCurrency();
                        break;
                    }
                }
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                break;
            case 49://borc
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                outCurrency = selectedFinancingDocument.getFinancingDocument().getCurrency();
                break;
            case 50://alacak
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                outCurrency = selectedFinancingDocument.getFinancingDocument().getCurrency();
                break;
            case 55://cari->banka
            case 73:
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        outCurrency = ba.getCurrency();
                        break;
                    }
                }
                break;
            case 56://banka->cari
            case 74:
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
                        outCurrency = ba.getCurrency();
                        break;
                    }
                }
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                break;
            default:
                break;
        }
        if (inCurrency != null && outCurrency != null) {
            selectedFinancingDocument.getFinancingDocument().setCurrency(outCurrency);
            if (selectedFinancingDocument.getFinancingDocument().getId() == 0) {//guncelleme değilse döviz hesapla
                selectedFinancingDocument.getFinancingDocument().setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            }
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
    }

    public void delete() {
        int result = 0;
        if (!selectedShift.isIsConfirm()) {
            if (isCredit) {
                result = fuelShiftTransferService.delete(selectedFinancingDocument);
                if (result > 0) {
                    RequestContext context = RequestContext.getCurrentInstance();
                    context.execute("PF('dlg_PostPaid').hide();");
                    listOfShiftPayment.remove(selectedFinancingDocument);
                    calculateTotal();
                    calculateTotalShiftPayment(selectedFinancingDocument, 0);
                    context.update("frmFuelShiftTransfer");
                    isCredit = false;
                }
                sessionBean.createUpdateMessage(result);
            } else if (!isDeleteShift) {//Finansman Belgeisni

                if (sessionBean.isPeriodClosed(selectedFinancingDocument.getFinancingDocument().getDocumentDate())) {
                    result = fuelShiftTransferService.delete(selectedFinancingDocument);
                    if (result > 0) {
                        RequestContext context = RequestContext.getCurrentInstance();
                        context.execute("PF('dlg_FinancingDocument').hide();");
                        listOfShiftPayment.remove(selectedFinancingDocument);
                        calculateTotal();
                        calculateTotalShiftPayment(selectedFinancingDocument, 0);
                        context.update("frmFuelShiftTransfer");
                    }
                    sessionBean.createUpdateMessage(result);
                    isDeleteShift = true;
                }
            } else {//Vardiyayı Sil
                result = fuelShiftTransferService.delete(selectedShift);
                if (result > 0) {
                    marwiz.goToPage("/pages/automation/fuelshift/fuelshift.xhtml", null, 1, 107);
                } else if (result == -101) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausecreditofshiftisrelatedtoinvoice")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
                if (result != -101) {
                    sessionBean.createUpdateMessage(result);
                }
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseofshiftisapproved")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void askConfirmationChargeUser(int type) {
        messageFinancing = "";
        ftype = type;
        if (activeTabFuelShiftSale.getAccount().getId() == 0) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            FuelShiftAttendantBean fuelShiftAttendantBean = (FuelShiftAttendantBean) viewMap.get("fuelShiftAttendantBean");
            fuelShiftAttendantBean.create();

            RequestContext.getCurrentInstance().update("dlgWithoutAttendantFD");
            RequestContext.getCurrentInstance().execute("PF('dlg_WithoutAttendantFD').show();");
        } else if (activeTabFuelShiftSale.getAccount().getId() > 0) {
            if (type == 1) {//Kullanıcıyı borçlandır
                messageFinancing = sessionBean.getLoc().getString("areyousuretowantcharginguser");
            } else if (type == 2) {//Kullanıcya Ver
                messageFinancing = sessionBean.getLoc().getString("areyousuretowanttransferringusertoexcessamount");
            }
            RequestContext.getCurrentInstance().update("dlgConfirmationFD");
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmationFD').show();");
        }

    }

    /**
     * Kullancıyı borçlandır butonuna basılınca açılan Confirm Dialog da evet
     * butonuna basıldığında çalışır.
     */
    public void createTempFinancingDocument() {
        selectedFinancingDocument = new ShiftPayment();
        if (activeTabFuelShiftSale.getAccount().getId() == 0) {//pompacısız
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            FuelShiftAttendantBean fuelShiftAttendantBean = (FuelShiftAttendantBean) viewMap.get("fuelShiftAttendantBean");
            String accounts = null;
            if (fuelShiftAttendantBean.getListOfSelectedObjects().size() > 0) {
                accounts = fuelShiftTransferService.jsonArrayAccounts(fuelShiftAttendantBean.getListOfSelectedObjects());
            }

            SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
            String dateformat = format.format(selectedShift.getBeginDate());

            selectedFinancingDocument.getShift().setId(selectedShift.getId());

            selectedFinancingDocument.getFinancingDocument().setDocumentDate(selectedShift.getBeginDate());

            selectedFinancingDocument.getFinancingDocument().setPrice(fuelShiftAttendantBean.getAttendantPrice());
            if (ftype == 1) {///Kullanıcıyı borçlandır

                selectedFinancingDocument.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shiftdeficit") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                selectedFinancingDocument.getFinancingDocument().getFinancingType().setId(49);
            } else {//Kullanıcya Ver

                selectedFinancingDocument.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shiftexcess") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                selectedFinancingDocument.getFinancingDocument().getFinancingType().setId(50);

                outCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();

            }

            int result = 0;
            if (sessionBean.isPeriodClosed(selectedFinancingDocument.getFinancingDocument().getDocumentDate())) {
                result = fuelShiftTransferService.createFinDocAndShiftPayment(3, selectedFinancingDocument, accounts);
                if (result > 0) {
                    listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, activeTabFuelShiftSale.getAccount(), 0);
                    if (ftype == 1) {
                        overallTotalShiftPayment = overallTotalShiftPayment.add(totalRemaining);
                    } else {
                        totalRemaining = totalRemaining.multiply(BigDecimal.valueOf(-1));
                        overallTotalShiftPayment = overallTotalShiftPayment.subtract(totalRemaining);
                    }
                    calculateTotal();
                    RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                }
                sessionBean.createUpdateMessage(result);
            }

        } else if (activeTabFuelShiftSale.getAccount().getId() > 0) {
            if (ftype == 1) {
                createFinancingDocument(1);
            } else if (ftype == 2) {
                createFinancingDocument(2);
            }
        }

    }

    public void createFinancingDocument(int type) {

        selectedFinancingDocument = new ShiftPayment();
        int result = 0;
        isIncomeExpense = false;
        SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        String dateformat = format.format(selectedShift.getBeginDate());

        selectedFinancingDocument.getFinancingDocument().setCurrency(sessionBean.getUser().getLastBranch().getCurrency());

        selectedFinancingDocument.getShift().setId(selectedShift.getId());
        selectedFinancingDocument.getAccount().setId(activeTabFuelShiftSale.getAccount().getId());
        selectedFinancingDocument.getAttendantAccount().setId(activeTabFuelShiftSale.getAccount().getId());
        selectedFinancingDocument.getFinancingDocument().getAccount().setId(activeTabFuelShiftSale.getAccount().getId());

        selectedFinancingDocument.getFinancingDocument().setDocumentDate(selectedShift.getBeginDate());
        selectedFinancingDocument.getFinancingDocument().setPrice(totalRemaining);
        if (type == 1) {//Kullanıcıyı Borçlandır.

            selectedFinancingDocument.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shiftdeficit") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
            selectedFinancingDocument.getFinancingDocument().getFinancingType().setId(49);
            firstId = activeTabFuelShiftSale.getAccount().getId();
            bringCurrency();

        } else if (type == 2) {//Fazlalığı kullanıcıya ver!!

            selectedFinancingDocument.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shiftexcess") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
            selectedFinancingDocument.getFinancingDocument().getFinancingType().setId(50);

            selectedFinancingDocument.getFinancingDocument().setPrice(selectedFinancingDocument.getFinancingDocument().getPrice().multiply(BigDecimal.valueOf(-1)));
            secondId = selectedFinancingDocument.getFinancingDocument().getAccount().getId();

            outCurrency = sessionBean.getUser().getLastBranch().getCurrency();
            inCurrency = sessionBean.getUser().getLastBranch().getCurrency();

            if (inCurrency != null && outCurrency != null) {
                selectedFinancingDocument.getFinancingDocument().setCurrency(outCurrency);
                if (selectedFinancingDocument.getFinancingDocument().getId() == 0) {//guncelleme değilse döviz hesapla
                    selectedFinancingDocument.getFinancingDocument().setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
                }
                exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
            } else {
                exchange = "";
            }

            RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

        } else if (type == 3) {//Gider Oluştur.
            isDialog = true;
            processType = 1;
            selectedFinancingDocument.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shiftdeficit") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");

            listOfType = sessionBean.getTypes(20);
            isIncomeExpense = true;

            selectedFinancingDocument.getFinancingDocument().getFinancingType().setId(49);
            firstId = activeTabFuelShiftSale.getAccount().getId();
            bringCurrency();

            updateCondition = "fuelShiftTransferBean";

            RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess");
            RequestContext.getCurrentInstance().execute("PF('dlg_FinancingDocument').show();");
        } else if (type == 4) {//Gelr Oluştur
            isDialog = true;
            selectedFinancingDocument.getFinancingDocument().setDescription(sessionBean.getLoc().getString("shiftexcess") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");

            processType = 1;
            listOfType = sessionBean.getTypes(20);
            isIncomeExpense = true;

            updateCondition = "fuelShiftTransferBean";

            selectedFinancingDocument.getFinancingDocument().getFinancingType().setId(50);

            selectedFinancingDocument.getFinancingDocument().setPrice(selectedFinancingDocument.getFinancingDocument().getPrice().multiply(BigDecimal.valueOf(-1)));
            secondId = selectedFinancingDocument.getFinancingDocument().getAccount().getId();

            outCurrency = sessionBean.getUser().getLastBranch().getCurrency();
            inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
            selectedFinancingDocument.getFinancingDocument().setCurrency(outCurrency);
            bringCurrency();

            RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
            RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess");
            RequestContext.getCurrentInstance().execute("PF('dlg_FinancingDocument').show();");
        }

        if (type == 1 || type == 2) {
            if (sessionBean.isPeriodClosed(selectedFinancingDocument.getFinancingDocument().getDocumentDate())) {
                result = fuelShiftTransferService.createFinDocAndShiftPayment(1, selectedFinancingDocument, "");
                if (result > 0) {
                    listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, activeTabFuelShiftSale.getAccount(), 0);
                    calculateTotal();
                    calculateTotalShiftPayment(selectedFinancingDocument, 1);
                    RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                }
                sessionBean.createUpdateMessage(result);
            }
        }

    }

    public void save() {
        if (!selectedShift.isIsConfirm()) {
            if (sessionBean.isPeriodClosed(selectedFinancingDocument.getFinancingDocument().getDocumentDate())) {
                int result = 0;
                if (processType == 1) {
                    result = fuelShiftTransferService.createFinDocAndShiftPayment(1, selectedFinancingDocument, "");
                    if (result > 0) {

                        RequestContext.getCurrentInstance().execute("PF('dlg_FinancingDocument').hide();");
                        listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, activeTabFuelShiftSale.getAccount(), 0);
                        calculateTotal();
                        calculateTotalShiftPayment(selectedFinancingDocument, 1);
                        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                    }
                } else if (processType == 2) {//Güncelleme
                    selectedFinancingDocument.getAccount().setId(activeTabFuelShiftSale.getAccount().getId());
                    if ((selectedFinancingDocument.getFinancingDocument().getFinancingType().getId() == 49 || selectedFinancingDocument.getFinancingDocument().getFinancingType().getId() == 50) && selectedFinancingDocument.getFinancingDocument().getIncomeExpense().getId() == 0) {

                        selectedFinancingDocument.getAttendantAccount().setId(activeTabFuelShiftSale.getAccount().getId());
                    }

                    switch (selectedFinancingDocument.getFinancingDocument().getFinancingType().getId()) {
                        case 47://cari->kasa
                            selectedFinancingDocument.getFinancingDocument().getAccount().setId(firstId);
                            selectedFinancingDocument.getSafe().setId(secondId);
                            break;
                        case 48://kasa->cari
                            selectedFinancingDocument.getSafe().setId(firstId);
                            selectedFinancingDocument.getFinancingDocument().getAccount().setId(secondId);
                            break;
                        case 49://borc dekontu
                            selectedFinancingDocument.getFinancingDocument().getAccount().setId(firstId);
                            if (selectedFinancingDocument.getFinancingDocument().getIncomeExpense().getId() > 0) {
                                selectedFinancingDocument.getFinancingDocument().getIncomeExpense().setIsIncome(false);
                            }
                            break;
                        case 50://alacak dekontu
                            selectedFinancingDocument.getFinancingDocument().getAccount().setId(secondId);
                            if (selectedFinancingDocument.getFinancingDocument().getIncomeExpense().getId() > 0) {
                                selectedFinancingDocument.getFinancingDocument().getIncomeExpense().setIsIncome(true);
                            }
                            break;
                        case 55:
                        case 73:
                            //cari->banka
                            selectedFinancingDocument.getFinancingDocument().getAccount().setId(firstId);
                            selectedFinancingDocument.getBankAccount().setId(secondId);
                            break;
                        case 56://banka->cari
                        case 74:
                            selectedFinancingDocument.getBankAccount().setId(firstId);
                            selectedFinancingDocument.getFinancingDocument().getAccount().setId(secondId);
                            break;
                        default:
                            break;

                    }

                    result = fuelShiftTransferService.updateFinDocAndShiftPayment(4, selectedFinancingDocument, "");
                    if (result > 0) {
                        RequestContext.getCurrentInstance().execute("PF('dlg_FinancingDocument').hide();");
                        listOfShiftPayment = fuelShiftTransferService.findAllShiftPayment(selectedShift, activeTabFuelShiftSale.getAccount(), 0);
                        calculateTotal();
                        calculateTotalShiftPayment(selectedFinancingDocument, 2);
                        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                    }
                }
                isDialog = false;
                sessionBean.createUpdateMessage(result);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbeupdatedbecauseofshiftisapproved")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void beforeApproveShift() {
        isApproveShiftPdf = false;
        isApproveShiftExcel = false;
        isPreview = false;
        if (!controlApproveShift()) {
            if (selectedShift.isIsConfirm()) {//Vardiya Onayını Aç
                approveShift();
            } else {//Vardiya Onayla
                RequestContext.getCurrentInstance().update("dlgApproveShift");
                RequestContext.getCurrentInstance().execute("PF('dlg_ApproveShift').show();");
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterallshiftpayment")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void approveShift() {
        int result = 0;
        if (selectedShift.isIsConfirm()) {//Vardiya Onayını Aç
            selectedShift.setIsConfirm(false);
        } else {//Vardiya Onayla
            selectedShift.setIsConfirm(true);
        }
        result = fuelShiftTransferService.update(selectedShift);
        if (result > 0) {
            if (selectedShift.isIsConfirm()) {
                isUpdate = false;
            } else {
                isUpdate = true;
            }
            isOkey = true;
            RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
        } else {
            isOkey = false;
        }
        sessionBean.createUpdateMessage(result);
    }

    public void calculateTotalShiftPayment(ShiftPayment payment, int type) {
        if (type == 0) {//Silme
            if (payment.getBankAccount().getId() > 0 || payment.getSafe().getId() > 0 || (payment.getFinancingDocument().getIncomeExpense().getId() > 0 && !payment.getFinancingDocument().getIncomeExpense().isIsIncome())
                    || (payment.getFinancingDocument().getIncomeExpense().getId() == 0 && payment.getFinancingDocument().getFinancingType().getId() == 49)
                    || payment.getCredit().getAccount().getId() > 0 || (payment.getFuelSaleType().getTypeno() == 98 && branchSetting.getAutomationId() == 2)) {
                overallTotalShiftPayment = overallTotalShiftPayment.subtract(payment.getFinancingDocument().getPrice());
            } else {
                overallTotalShiftPayment = overallTotalShiftPayment.add(payment.getFinancingDocument().getPrice());
            }
        } else if (type == 1) {//Kaydet
            if (payment.getBankAccount().getId() > 0 || payment.getSafe().getId() > 0 || (payment.getFinancingDocument().getIncomeExpense().getId() > 0 && !payment.getFinancingDocument().getIncomeExpense().isIsIncome())
                    || (payment.getFinancingDocument().getIncomeExpense().getId() == 0 && payment.getFinancingDocument().getFinancingType().getId() == 49)
                    || payment.getCredit().getAccount().getId() > 0 || (payment.getFuelSaleType().getTypeno() == 98 && branchSetting.getAutomationId() == 2)) {
                overallTotalShiftPayment = overallTotalShiftPayment.add(payment.getFinancingDocument().getPrice());
            } else {
                overallTotalShiftPayment = overallTotalShiftPayment.subtract(payment.getFinancingDocument().getPrice());
            }
        } else if (type == 2) {//Güncelleme
            if (payment.getBankAccount().getId() > 0 || payment.getSafe().getId() > 0 || (payment.getFinancingDocument().getIncomeExpense().getId() > 0 && !payment.getFinancingDocument().getIncomeExpense().isIsIncome())
                    || (payment.getFinancingDocument().getIncomeExpense().getId() == 0 && payment.getFinancingDocument().getFinancingType().getId() == 49)
                    || payment.getCredit().getAccount().getId() > 0 || (payment.getFuelSaleType().getTypeno() == 98 && branchSetting.getAutomationId() == 2)) {
                overallTotalShiftPayment = overallTotalShiftPayment.subtract(oldValue);
                overallTotalShiftPayment = overallTotalShiftPayment.add(payment.getFinancingDocument().getPrice());
            } else {
                overallTotalShiftPayment = overallTotalShiftPayment.add(oldValue);
                overallTotalShiftPayment = overallTotalShiftPayment.subtract(payment.getFinancingDocument().getPrice());
            }
        }

    }

    public void testBeforeDelete() {
        if (isCredit) {//Kredi İçin
            CreditReport creditReport = new CreditReport();
            creditReport.setId(selectedFinancingDocument.getCredit().getId());
            if (sessionBean.isPeriodClosed(selectedFinancingDocument.getCredit().getDueDate())) {
                controlDeleteList = new ArrayList<>();
                controlDeleteList = creditService.testBeforeDelete(creditReport);
                if (!controlDeleteList.isEmpty()) {
                    if (controlDeleteList.get(0).getR_response() < 0) {//Kredi Faturalandırıldıysa silmeye izin verme
                        if (controlDeleteList.get(0).getR_response() == -105) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseofrelatedrecords")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        }

                    } else {
                        RequestContext.getCurrentInstance().update("frmFuelShiftTransfer:dlgDelete");
                        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                    }
                }
            }

        }

    }

    public boolean controlApproveShift() {
        boolean isOpen = false;
        List<ShiftPayment> tList = new ArrayList<>();
        List<FuelShiftSales> sList = new ArrayList<>();
        BigDecimal tShiftPayment = BigDecimal.valueOf(0);
        BigDecimal tSale = BigDecimal.valueOf(0);
        sList = fuelShiftTransferService.findAllSaleForShift(selectedShift);
        tList = fuelShiftTransferService.findAllShiftPayment(selectedShift, new Account(), 1);
        for (FuelShiftSales fuelShiftSales : listOfAttendant) {
            if (fuelShiftSales.getAccount().getId() != -1 && fuelShiftSales.getAccount().getId() != -2) {
                for (ShiftPayment shiftPayment : tList) {
                    if (!shiftPayment.isIsAutomation()) {
                        if (fuelShiftSales.getAccount().getId() == shiftPayment.getAccount().getId()) {
                            if (shiftPayment.getBankAccount().getId() > 0 || shiftPayment.getSafe().getId() > 0 || (shiftPayment.getFinancingDocument().getIncomeExpense().getId() > 0 && !shiftPayment.getFinancingDocument().getIncomeExpense().isIsIncome())
                                    || (shiftPayment.getFinancingDocument().getIncomeExpense().getId() == 0 && shiftPayment.getFinancingDocument().getFinancingType().getId() == 49)
                                    || shiftPayment.getCredit().getAccount().getId() > 0 || (shiftPayment.getFuelSaleType().getTypeno() == 98 && branchSetting.getAutomationId() == 2)) {
                                tShiftPayment = tShiftPayment.add(shiftPayment.getFinancingDocument().getPrice());
                            } else {
                                tShiftPayment = tShiftPayment.subtract(shiftPayment.getFinancingDocument().getPrice());
                            }
                        }
                    }
                }
                for (FuelShiftSales fuelShiftSales1 : sList) {
                    if (fuelShiftSales.getAttendantCode().equals(fuelShiftSales1.getAttendantCode())) {
                        if (branchSetting.getAutomationId() == 1) { //Stawiz İçin İşlem Yapılacak Satış Tutarı
                            if (fuelShiftSales1.getFuelSaleType().getTypeno() == 0 || fuelShiftSales1.getFuelSaleType().getTypeno() == 3
                                    || fuelShiftSales1.getFuelSaleType().getTypeno() == 4 || fuelShiftSales1.getFuelSaleType().getTypeno() == 6
                                    || fuelShiftSales1.getFuelSaleType().getTypeno() == 8 || fuelShiftSales1.getFuelSaleType().getTypeno() == 9
                                    || fuelShiftSales1.getFuelSaleType().getTypeno() == 18 || fuelShiftSales1.getFuelSaleType().getTypeno() == 19) {
                                tSale = tSale.add(fuelShiftSales1.getTotalMoney());

                            }
                        } else if (branchSetting.getAutomationId() == 2) { //Türpak İçin İşlem Yapılacak Satış Tutarı
                            if (fuelShiftSales1.getFuelSaleType().getTypeno() == 1
                                    || fuelShiftSales1.getFuelSaleType().getTypeno() == 5
                                    || fuelShiftSales1.getFuelSaleType().getTypeno() == 10 || fuelShiftSales1.getFuelSaleType().getTypeno() == 11
                                    || fuelShiftSales1.getFuelSaleType().getTypeno() == 18 || fuelShiftSales1.getFuelSaleType().getTypeno() == 98 || fuelShiftSales1.getFuelSaleType().getTypeno() == 99) {
                                tSale = tSale.add(fuelShiftSales1.getTotalMoney());

                            }
                        } else if (branchSetting.getAutomationId() == 4) { //Stawiz İçin İşlem Yapılacak Satış Tutarı
                            if (fuelShiftSales1.getFuelSaleType().getTypeno() == 0) {
                                tSale = tSale.add(fuelShiftSales1.getTotalMoney());

                            }
                        } else if (branchSetting.getAutomationId() == 3) { //Asis İçin İşlem Yapılacak Satış Tutarı
                            if (fuelShiftSales1.getFuelSaleType().getTypeno() == 1) {
                                tSale = tSale.add(fuelShiftSales1.getTotalMoney());

                            }
                        }else if (branchSetting.getAutomationId() == 5) { //Turpak Shell İçin İşlem Yapılacak Satış Tutarı
                            if (fuelShiftSales1.getFuelSaleType().getTypeno() == 1 || fuelShiftSales1.getFuelSaleType().getTypeno() == 2) {
                                tSale = tSale.add(fuelShiftSales1.getTotalMoney());

                            }
                        }

                    }
                }

                if (tSale.compareTo(tShiftPayment) != 0) {
                    isOpen = true;
                    break;
                } else {
                    tSale = BigDecimal.valueOf(0);
                    tShiftPayment = BigDecimal.valueOf(0);
                }

            } else if (fuelShiftSales.getAccount().getId() == -2) {//Otomasyon İçin
                for (ShiftPayment s : tList) {
                    if (s.isIsAutomation()) {
                        tShiftPayment = tShiftPayment.add(s.getFinancingDocument().getPrice());
                    }

                }
                for (FuelShiftSales fuelShiftSales1 : listOfAutomationSales) {
                    tSale = tSale.add(fuelShiftSales1.getTotalMoney());
                }

                if (tSale.compareTo(tShiftPayment) != 0) {
                    isOpen = true;
                    break;
                } else {
                    tSale = BigDecimal.valueOf(0);
                    tShiftPayment = BigDecimal.valueOf(0);
                }
            }

        }
        return isOpen;

    }

    public void bringCreditSales() {

        isDestroy = true;
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        FuelShiftCreateCreditBean fuelShiftCreateCreditBean = (FuelShiftCreateCreditBean) viewMap.get("fuelShiftCreateCreditBean");
        fuelShiftCreateCreditBean.setListOfObjects(fuelShiftTransferService.findAllCreditSales(selectedShift, activeTabFuelShiftSale, branchSetting, false));
        fuelShiftCreateCreditBean.setIsCheck(false);
        fuelShiftCreateCreditBean.setAutoCompleteValue(null);
        RequestContext.getCurrentInstance().execute("PF('creditSalesPF').filter();");
        RequestContext.getCurrentInstance().update("pngBook");
        RequestContext.getCurrentInstance().execute("PF('dlg_CreditSales').show();");
    }

    public void bringBooleanValue() {
        isDialog = false;
        isDeleteShift = true;
        isFinancingDoc = false;
        isDestroy = false;

    }

    public void resetDialog() {
        isCredit = false;
        isDeleteShift = true;
    }

    /**
     * Vardiyaya Ait Excel Dosyasını Oluşturur.
     */
    public void createExcelFile() {
        fuelShiftTransferService.createExcelFile(selectedShift, branchSetting);
    }

    /**
     * Vardiyaya Ait Pdf Dosyasını Oluşturur.
     */
    public void creatPdfFile() {
        fuelShiftTransferService.createPdfFile(selectedShift, branchSetting);
    }

    /**
     * Vardiya Excel,Pdf Önizleme Sayfasını Açmak İçin
     */
    public void showPreview() {
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        boolean isThere = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof FuelShift) {
                isThere = true;
                list.remove(i);
                list.add(selectedShift);
            }
        }
        if (!isThere) {
            list.add(selectedShift);
        }
        list.add(new Object());
        marwiz.goToPage("/pages/automation/fuelshift/fuelshiftpreview.xhtml", list, 0, 117);
    }

    public void transferExcelPdf() {
        approveShift();
        if (isOkey) {
            if (isApproveShiftExcel) {
                RequestContext.getCurrentInstance().execute("bringExcel();");
            }
            if (isApproveShiftPdf) {
                RequestContext.getCurrentInstance().execute("bringPdf();");
            }
            if (isPreview) {
                RequestContext.getCurrentInstance().execute("goToPreview();");
            }
        }
    }

    public void bringOption(int type) {
        if (type == 1) {
            if (isApproveShiftExcel) {
                isApproveShiftPdf = false;
            }
        } else if (type == 2) {
            if (isApproveShiftPdf) {
                isApproveShiftExcel = false;
            }
        }

    }

    public void changeBranch() {
        switch (selectedFinancingDocument.getFinancingDocument().getFinancingType().getId()) {
            case 47://cari->kasa
            case 48://kasa->cari
                listOfSafe = safeService.findSafeByCurrency(" AND sf.shiftmovementsafe_id IS NULL AND sf.currency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId());
                break;
            case 55://cari->banka
            case 56://banka->cari
            case 73://cari->banka
            case 74://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.currency_id=" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND bka.type_id IN(16, 104) ", sessionBean.getUser().getLastBranch());
                break;
            default:
                break;
        }
    }

    public void incomeExpenseOrAccountCollectionPayment() {
        if (isAccountCollectionPayment) {
            isDestroy = false;
            setIsAccountCollectionPayment(false);
        } else {
            isDestroy = true;
            setIsAccountCollectionPayment(true);

        }
        isFinancingDoc = false;
        RequestContext.getCurrentInstance().update("pngAccountBook");

    }

    public void collectionOrPayment() {
        if (isAccountCollection) {
            isAccountCollection = false;
        } else {
            isAccountCollection = true;
        }

    }

}
