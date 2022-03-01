/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.12.2018 05:31:27
 */
package com.mepsan.marwiz.general.marketshift.presentation;

import com.google.gson.Gson;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.marketshift.business.MarketShiftService;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPaymentFinancingDocumentCon;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.log.RemovedStock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftPaymentService;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportPrintService;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportService;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.MarketShiftPayment;
import com.mepsan.marwiz.general.report.removedstockreport.business.IRemovedStockReportService;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@ManagedBean
@ViewScoped
public class MarketShiftTransferBean {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marketShiftPaymentService}")
    private IMarketShiftPaymentService marketShiftPaymentService;

    @ManagedProperty(value = "#{safeService}")
    private ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    private IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{marketShiftReportService}")
    private IMarketShiftReportService marketShiftReportService;

    @ManagedProperty(value = "#{marketShiftReportPrintService}")
    private IMarketShiftReportPrintService marketShiftReportPrintService;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{marketShiftService}")
    public MarketShiftService marketShiftService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{removedStockReportService}")
    public IRemovedStockReportService removedStockReportService;

    private Shift selectedShift;
    private List<MarketShiftPayment> listOfShiftPayment;
    private MarketShiftPayment selectedShiftPayment;
    private Boolean isClosed, isBigger, isExpense, isIncome;
    private FinancingDocument financingDocument;
    private int firstId, secondId;
    private Currency inCurrency, outCurrency, inCrr, outCrr;
    private String exchange;
    private List<Type> listOfType;
    private HashMap<Integer, BigDecimal> groupCurrencyTotal;
    int tempSaleId;
    private List<Safe> listOfSafe;
    private List<BankAccount> listOfBankAccount;
    private int processType;
    private String message;
    private Shift tempShift;
    private int tempType;

    private boolean isIncomeExpense;
    private String beanName;
    private String messageFinancing;
    private int ftype;
    private List<RemovedStock> listOfRemovedStock;
    //Vardiya Onaylanmak İstendiğinde Excel Oluşturmak İçin Kullanıldı
    private boolean createExcel, createPdf, showPreview;
    private String totalShiftAmount;
    private boolean isUpdate;
    private MarketShiftPaymentFinancingDocumentCon shiftPaymentFinancingDocumentCon;
    private boolean isIncomeForCon;
    private BigDecimal differenceAmount, remainingAmount;
    private BigDecimal incomeExpensePrice, employeePrice;
    private boolean isDirection;
    private MarketShiftPaymentFinancingDocumentCon selectedFinancingDocumentForDialog;
    private List<MarketShiftPaymentFinancingDocumentCon> listOfPaymentFinancingDocCon;
    private Boolean isCreateFinancingDocument;
    private boolean isExcel;
    private List<String> selectedOptions;
    private List<Option> importOptionsList;
    private boolean isClickApprove;
    private List<Branch> listOfBranch;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Shift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(Shift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public List<MarketShiftPayment> getListOfShiftPayment() {
        return listOfShiftPayment;
    }

    public void setListOfShiftPayment(List<MarketShiftPayment> listOfShiftPayment) {
        this.listOfShiftPayment = listOfShiftPayment;
    }

    public MarketShiftPayment getSelectedShiftPayment() {
        return selectedShiftPayment;
    }

    public void setSelectedShiftPayment(MarketShiftPayment selectedShiftPayment) {
        this.selectedShiftPayment = selectedShiftPayment;
    }

    public void setMarketShiftPaymentService(IMarketShiftPaymentService marketShiftPaymentService) {
        this.marketShiftPaymentService = marketShiftPaymentService;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Boolean getIsBigger() {
        return isBigger;
    }

    public void setIsBigger(Boolean isBigger) {
        this.isBigger = isBigger;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
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

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMarketShiftReportService(IMarketShiftReportService marketShiftReportService) {
        this.marketShiftReportService = marketShiftReportService;
    }

    public void setMarketShiftReportPrintService(IMarketShiftReportPrintService marketShiftReportPrintService) {
        this.marketShiftReportPrintService = marketShiftReportPrintService;
    }

    public Shift getTempShift() {
        return tempShift;
    }

    public void setTempShift(Shift tempShift) {
        this.tempShift = tempShift;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public Boolean getIsExpense() {
        return isExpense;
    }

    public void setIsExpense(Boolean isExpense) {
        this.isExpense = isExpense;
    }

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public Boolean getIsIncome() {
        return isIncome;
    }

    public void setIsIncome(Boolean isIncome) {
        this.isIncome = isIncome;
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

    public String getMessageFinancing() {
        return messageFinancing;
    }

    public void setMessageFinancing(String messageFinancing) {
        this.messageFinancing = messageFinancing;
    }

    public void setMarketShiftService(MarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public List<RemovedStock> getListOfRemovedStock() {
        return listOfRemovedStock;
    }

    public void setListOfRemovedStock(List<RemovedStock> listOfRemovedStock) {
        this.listOfRemovedStock = listOfRemovedStock;
    }

    public void setRemovedStockReportService(IRemovedStockReportService removedStockReportService) {
        this.removedStockReportService = removedStockReportService;
    }

    public boolean isCreateExcel() {
        return createExcel;
    }

    public void setCreateExcel(boolean createExcel) {
        this.createExcel = createExcel;
    }

    public int getTempType() {
        return tempType;
    }

    public void setTempType(int tempType) {
        this.tempType = tempType;
    }

    public boolean isCreatePdf() {
        return createPdf;
    }

    public void setCreatePdf(boolean createPdf) {
        this.createPdf = createPdf;
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public MarketShiftPaymentFinancingDocumentCon getShiftPaymentFinancingDocumentCon() {
        return shiftPaymentFinancingDocumentCon;
    }

    public void setShiftPaymentFinancingDocumentCon(MarketShiftPaymentFinancingDocumentCon shiftPaymentFinancingDocumentCon) {
        this.shiftPaymentFinancingDocumentCon = shiftPaymentFinancingDocumentCon;
    }

    public boolean isIsIncomeForCon() {
        return isIncomeForCon;
    }

    public void setIsIncomeForCon(boolean isIncomeForCon) {
        this.isIncomeForCon = isIncomeForCon;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public BigDecimal getIncomeExpensePrice() {
        return incomeExpensePrice;
    }

    public void setIncomeExpensePrice(BigDecimal incomeExpensePrice) {
        this.incomeExpensePrice = incomeExpensePrice;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public BigDecimal getEmployeePrice() {
        return employeePrice;
    }

    public void setEmployeePrice(BigDecimal employeePrice) {
        this.employeePrice = employeePrice;
    }

    public MarketShiftPaymentFinancingDocumentCon getSelectedFinancingDocumentForDialog() {
        return selectedFinancingDocumentForDialog;
    }

    public void setSelectedFinancingDocumentForDialog(MarketShiftPaymentFinancingDocumentCon selectedFinancingDocumentForDialog) {
        this.selectedFinancingDocumentForDialog = selectedFinancingDocumentForDialog;
    }

    public List<MarketShiftPaymentFinancingDocumentCon> getListOfPaymentFinancingDocCon() {
        return listOfPaymentFinancingDocCon;
    }

    public void setListOfPaymentFinancingDocCon(List<MarketShiftPaymentFinancingDocumentCon> listOfPaymentFinancingDocCon) {
        this.listOfPaymentFinancingDocCon = listOfPaymentFinancingDocCon;
    }

    public Boolean getIsCreateFinancingDocument() {
        return isCreateFinancingDocument;
    }

    public void setIsCreateFinancingDocument(Boolean isCreateFinancingDocument) {
        this.isCreateFinancingDocument = isCreateFinancingDocument;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public List<Option> getImportOptionsList() {
        return importOptionsList;
    }

    public void setImportOptionsList(List<Option> importOptionsList) {
        this.importOptionsList = importOptionsList;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public class Option {

        private int id;
        private String tableName;

        public Option(int id, String tableName) {
            this.id = id;
            this.tableName = tableName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

    }

    @PostConstruct
    public void init() {
        selectedShiftPayment = new MarketShiftPayment();
        financingDocument = new FinancingDocument();
        groupCurrencyTotal = new HashMap<>();
        listOfBankAccount = new ArrayList<>();
        listOfSafe = new ArrayList<>();
        listOfRemovedStock = new ArrayList<>();
        tempShift = new Shift();
        isUpdate = true;
        listOfPaymentFinancingDocCon = new ArrayList<>();
        importOptionsList = new ArrayList<>();
        selectedOptions = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Shift) {
                    selectedShift = (Shift) ((ArrayList) sessionBean.parameter).get(i);
                    listOfShiftPayment = marketShiftPaymentService.listOfShiftPayment(selectedShift, "");
                    tempShift = marketShiftReportService.controlShiftPayment(selectedShift);
                    if (!listOfShiftPayment.isEmpty()) {
                        chartShiftPaymentGraphic();
                        chartComparePreviousShift();
                    }
                }
            }
        }
        //  System.out.println("selectedShift+"+selectedShift.getShiftNo());
        beanName = "marketShiftTransferBean";
    }

    public void onCellEdit(CellEditEvent event) {

        int result = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        selectedShiftPayment = context.getApplication().evaluateExpressionGet(context, "#{shiftPayment}", MarketShiftPayment.class);
        if (event.getColumn().getClientId().contains("clmActualSalesAmount")) {
            if (selectedShiftPayment.getSalesPrice().compareTo(selectedShiftPayment.getActualSalesPrice()) == 0) {
                selectedShiftPayment.setIs_check(true);
            } else {
                selectedShiftPayment.setIs_check(false);
            }
            selectedShiftPayment.setExchangeRate(BigDecimal.valueOf(1));
            result = marketShiftPaymentService.update(selectedShiftPayment);
            controlOpenShiftPayment();
        } else {//Devir kolonu
            FinancingDocument fdoc = new FinancingDocument();
            int inM = 0, outM = 0;
            fdoc.getFinancingType().setId(48);
            fdoc.getIncomeExpense().setId(0);
            fdoc.setPrice(selectedShiftPayment.getInheritedMoney());
            inCrr = sessionBean.getUser().getLastBranch().getCurrency();

            inM = selectedShiftPayment.getSafe().getId();
            outM = selectedShiftPayment.getAccount().getId();
            listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());
            for (Safe safe : listOfSafe) {
                if (safe.getId() == inM) {
                    outCrr = safe.getCurrency();
                    break;
                }
            }

            if (inCrr != null && outCrr != null) {
                fdoc.setCurrency(outCrr);
                if (fdoc.getId() == 0) {//guncelleme değilse döviz hesapla
                    fdoc.setExchangeRate(exchangeService.bringExchangeRate(outCrr, inCrr, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
                }
            }

            fdoc.setDocumentDate(selectedShift.getBeginDate());
            SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
            String dateformat = format.format(selectedShift.getBeginDate());
            fdoc.setDescription(sessionBean.getLoc().getString("shiftinheritedamount") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
            if (selectedShiftPayment.getInheritedMoney().compareTo(BigDecimal.valueOf(0)) == 0) {
                result = marketShiftPaymentService.updateShiftPaymentForFinancingDoc(2, selectedShiftPayment, fdoc, inM, outM);
            } else {
                result = marketShiftPaymentService.updateShiftPaymentForFinancingDoc(1, selectedShiftPayment, fdoc, inM, outM);
            }

            if (result > 0) {
                RequestContext.getCurrentInstance().update("frmMarketShiftTransfer:dtbDistribution");
            } else if (result == -101) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                sessionBean.createUpdateMessage(result);
            }
        }

        selectedShiftPayment = new MarketShiftPayment();
        sessionBean.createUpdateMessage(result);

    }

    public BigDecimal calculateDifference(BigDecimal salesPrice, BigDecimal actualSalesPrice, int id) {
        isClosed = null;
        isBigger = null;
        isCreateFinancingDocument = null;
        BigDecimal difference = BigDecimal.valueOf(0);
        BigDecimal differenceMultiply = BigDecimal.valueOf(0);
        if (salesPrice == null || actualSalesPrice == null) {
            difference = null;
            differenceMultiply = null;
        } else {
            difference = salesPrice.subtract(actualSalesPrice);
            differenceMultiply = difference;
        }
        if (difference != null) {
            switch (difference.compareTo(BigDecimal.valueOf(0))) {
                case 1:
                    isClosed = false;
                    isBigger = false;
                    isIncome = true;
                    differenceMultiply = differenceMultiply.multiply(BigDecimal.valueOf(-1));
                    isCreateFinancingDocument = true;
                    break;
                case 0:
                    isClosed = true;
                    isBigger = false;
                    isIncome = false;
                    isExpense = false;
                    isCreateFinancingDocument = false;
                    break;
                case -1:
                    isClosed = false;
                    isBigger = true;
                    isExpense = true;
                    isCreateFinancingDocument = true;
                    differenceMultiply = differenceMultiply.multiply(BigDecimal.valueOf(-1));
                    break;
                default:
                    isClosed = null;
                    isBigger = false;
                    isCreateFinancingDocument = true;
                    break;
            }
        } else {
            isClosed = null;
            isBigger = null;
        }
        return differenceMultiply;

    }

    public String calculateTotalMoneyForAccount(int accountId) {

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');

        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        String totalMoney = "";

        groupCurrencyTotal.clear();

        for (MarketShiftPayment mp : listOfShiftPayment) {
            if (mp.getAccount().getId() == accountId) {
                if (groupCurrencyTotal.containsKey(mp.getCurrency().getId())) {
                    BigDecimal old = groupCurrencyTotal.get(mp.getCurrency().getId());
                    groupCurrencyTotal.put(mp.getCurrency().getId(), old.add(mp.getSalesPrice()));
                } else {
                    groupCurrencyTotal.put(mp.getCurrency().getId(), mp.getSalesPrice());
                }
            }
        }
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotal.entrySet()) {
            if (temp == 0) {
                temp = 1;
                totalMoney += String.valueOf(numberFormat.format(entry.getValue()));
                if (entry.getKey() != 0) {
                    totalMoney += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            } else {
                totalMoney += " + " + String.valueOf(numberFormat.format(entry.getValue()));
                if (entry.getKey() != 0) {
                    totalMoney += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            }
        }
        if (totalMoney.isEmpty() || totalMoney.equals("")) {
            totalMoney = "0";
        }
        return totalMoney;
    }

    /**
     * Carmi yoksa gelir gider kartımı seçeceğini belirleyen butona tıkladıkça
     * tetiklenir.
     */
    public void accountOrIncomeExp() {
        if (isIncomeExpense) {//cari seçecek
            financingDocument.setIncomeExpense(new IncomeExpense());
            isIncomeExpense = false;
        } else {//gelirgider sececek
            financingDocument.setAccount(new Account());
            isIncomeExpense = true;
        }

    }

    /**
     * hareketin finansman belgesini getirdik.
     */
    public void getfinancingDocument() {
        processType = 2;
        listOfType = sessionBean.getTypes(20);

        financingDocument.setId(selectedFinancingDocumentForDialog.getFinancingDocument().getId());

        RequestContext context = RequestContext.getCurrentInstance();
        if (financingDocument.getId() > 0) {//finansman belgesi ise
            if (listOfSafe.isEmpty()) {
                listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());
                listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
            }
            financingDocument = financingDocumentService.findFinancingDocument(financingDocument);
            if (financingDocument.getIncomeExpense().getId() > 0) {
                isIncomeExpense = true;
            } else {
                isIncomeExpense = false;
            }
            bringFinancingDocument(financingDocument);
            bringCurrency();
            context.execute("PF('dlg_FinancingDocument').show();");

        }

    }

    public void updateAllInformation() {
        if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            financingDocument.setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            shiftPaymentFinancingDocumentCon.getFinancingDocument().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            incomeExpenseBookFilterBean.setSelectedData(null);
        }
        RequestContext.getCurrentInstance().update("frmCreateFinanacingDocument");
    }

    public void delete() {
        int result = 0;
        if (selectedShift.isIsAvailableSale()) {
            if (sessionBean.isPeriodClosed(financingDocument.getDocumentDate())) {
                if (selectedShift.isIs_Confirm()) {//Silmeye izin verme
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseofshiftisapproved")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    result = marketShiftPaymentService.delete(selectedFinancingDocumentForDialog);
                    if (result > 0) {
                        selectedShiftPayment.setIs_check(false);
                        RequestContext context = RequestContext.getCurrentInstance();
                        context.execute("PF('dlg_FinancingDocument').hide();");
                        listOfShiftPayment = marketShiftPaymentService.listOfShiftPayment(selectedShift, "");
                        for (Iterator<MarketShiftPaymentFinancingDocumentCon> iterator = listOfPaymentFinancingDocCon.iterator(); iterator.hasNext();) {
                            MarketShiftPaymentFinancingDocumentCon next = iterator.next();
                            if (next.getId() == selectedFinancingDocumentForDialog.getId()) {
                                iterator.remove();
                                break;
                            }
                        }
                        shiftPaymentFinancingDocumentCon.getShiftPayment().setIs_check(false);
                        calculateRemainingAmount();
                        context.update("frmMarketShiftTransfer");
                        context.update("frmCreateFinanacingDocument");
                        controlOpenShiftPayment();
                    }
                    sessionBean.createUpdateMessage(result);
                }
            }
        } else {
            result = marketShiftService.delete(selectedShift);
            if (result > 0) {
                controlOpenShiftPayment();
                marwiz.goToPage("/pages/general/marketshift/marketshift.xhtml", null, 1, 66);
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    public void approveProcessShift() {
        createExcel = false;
        createPdf = false;
        showPreview = false;
        int type;
        if (selectedShift.isIs_Confirm()) {
            type = 1;
        } else {
            type = 0;
        }
        message = "";
        tempType = type;

        if (type == 0) {//onayla
            if (!listOfShiftPayment.isEmpty()) {
                tempShift = marketShiftReportService.controlShiftPayment(selectedShift);
                if (tempShift.isIs_ShiftPaymentCheck()) {//Kontrol hepsi is_check mi diye
                    if (tempShift.isIs_MovementSafe()) {
                        message = sessionBean.getLoc().getString("shiftwillbeclosedandshiftpaymentswillbetransferredtomainsafe");
                        message += sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                    } else {
                        message = sessionBean.getLoc().getString("areyousureyouwanttoapproveshift");
                    }

                    RequestContext.getCurrentInstance().update("dlgApproveOrOpenShift");
                    RequestContext.getCurrentInstance().execute("PF('dlg_ApproveOrOpenShift').show();");

                } else {//is_check hepsi değil ise
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterallshiftpayment")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else {

                if (tempShift.isIs_MovementSafe()) {
                    message = sessionBean.getLoc().getString("shiftwillbeclosedandshiftpaymentswillbetransferredtomainsafe");
                    message += sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                } else {
                    message = sessionBean.getLoc().getString("areyousureyouwanttoapproveshift");
                }

                RequestContext.getCurrentInstance().update("dlgApproveOrOpenShift");
                RequestContext.getCurrentInstance().execute("PF('dlg_ApproveOrOpenShift').show();");
            }

        } else if (type == 1) {//Onayı Aç İşlem Yapabilir
            if (tempShift.isIs_MovementSafe()) {
                message = sessionBean.getLoc().getString("alltransferstomainsafewillberetrieved");
                message += sessionBean.getLoc().getString("areyousureyouwanttocontinue");
            } else {
                message = sessionBean.getLoc().getString("areyousureyouwanttoremoveshiftapproved");
            }
            RequestContext.getCurrentInstance().update("dlgApproveOrOpenShift");
            RequestContext.getCurrentInstance().execute("PF('dlg_ApproveOrOpenShift').show();");
        }

    }

    public void transferShiftPaymentToMainSafe() {
        if ((createPdf && tempType == 0) || (createExcel && tempType == 0)) {
            isClickApprove = true;
            if (createPdf && tempType == 0) {
                askBeforePdf();
            } else {
                askBeforeExcel();
            }

        } else {
            isClickApprove = false;
            int result = 0;
            if (tempType == 0) {//Onayla
                SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
                String dateformat = format.format(selectedShift.getBeginDate());
                selectedShift.setDescription(sessionBean.getLoc().getString("shiftinheritedamount") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                result = marketShiftReportPrintService.transferShiftPaymentToMainSafe(3, selectedShift, true);
            } else {//Geri al
                result = marketShiftReportPrintService.transferShiftPaymentToMainSafe(4, selectedShift, false);
            }

            if (result > 0) {
                if (tempType == 0) {//Onayla
                    selectedShift.setIs_Confirm(true);
                    tempShift.setIs_Confirm(true);
                } else {
                    selectedShift.setIs_Confirm(false);
                    tempShift.setIs_Confirm(false);
                }

                if (showPreview && tempType == 0) {
                    RequestContext.getCurrentInstance().execute("goToPreview();");
                } else {//Goto Page Çalışınca Form Bulunamıyor
                    RequestContext.getCurrentInstance().execute("updateDatatable()");
                }
                RequestContext.getCurrentInstance().execute("PF('dlg_ApproveOrOpenShift').hide();");

            } else if (result == -101) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                sessionBean.createUpdateMessage(result);
            }
        }

    }

    /**
     * Aynı Anda Excel Pdf Yazdırılmasını Engellemek İçin Select One Menu'de
     * Kullanılan Fonksiyon
     *
     * @param type 1:Excelden Çağrıldı İse 2: Pdfden Çağrıldı İse
     */
    public void fileProcess(int type) {
        if (type == 1) {
            if (createPdf && createExcel) {
                createPdf = false;
            }
        } else if (type == 2) {
            if (createPdf && createExcel) {
                createExcel = false;
            }
        }
        RequestContext.getCurrentInstance().update("frmAproval");

    }

    public void controlOpenShiftPayment() {
        int isopenShiftPayment = 0;
        isopenShiftPayment = marketShiftPaymentService.controlOpenShiftPayment();
        if (isopenShiftPayment == 0) {
            applicationBean.getBranchShiftPaymentMap().put(sessionBean.getUser().getLastBranch().getId(), false);
        } else if (isopenShiftPayment == 1) {
            applicationBean.getBranchShiftPaymentMap().put(sessionBean.getUser().getLastBranch().getId(), true);
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

    public void bringCurrency() {
        switch (financingDocument.getFinancingType().getId()) {

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
            case 50://alacak
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                outCurrency = financingDocument.getCurrency();
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
            financingDocument.setCurrency(outCurrency);
            if (financingDocument.getId() == 0) {//guncelleme değilse döviz hesapla
                financingDocument.setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            }
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

    }

    public void chartShiftPaymentGraphic() {
        List<ChartItem> list = marketShiftPaymentService.chartListForShiftPayment(selectedShift);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("shiftpaymenttotype(" + "'" + data + "'" + ")");
    }

    public void chartComparePreviousShift() {
        List<ChartItem> list = marketShiftPaymentService.chartListForPreviousCompare(selectedShift);
        for (ChartItem chartItem : list) {
            if (chartItem.getTypeId() == selectedShift.getId()) {
                chartItem.setName1(sessionBean.getLoc().getString("thisshift"));
            } else {
                chartItem.setName1(sessionBean.getLoc().getString("previousshift"));
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("comparepreviousshift(" + "'" + data + "'" + ")");
    }

    public String readXml(String totalSaleAmount) {

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');

        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        String totalAmount = "";
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(totalSaleAmount));
            src.setEncoding("UTF-8");
            org.w3c.dom.Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();
            int count = 0;
            NodeList list = doc.getElementsByTagName("shiftsaletotal");
            if (list.getLength() == 0) {
                totalAmount = Integer.toString(0);
            }
            for (int s = 0; s < list.getLength(); s++) {
                NodeList elements = list.item(s).getChildNodes();
                if (count == 0) {
                    totalAmount += (numberFormat.format(new BigDecimal(elements.item(0).getTextContent()))).toString() + " " + sessionBean.currencySignOrCode(Integer.valueOf(elements.item(1).getTextContent()), 0);
                } else {
                    totalAmount += " + " + (numberFormat.format(new BigDecimal(elements.item(0).getTextContent()))).toString() + " " + sessionBean.currencySignOrCode(Integer.valueOf(elements.item(1).getTextContent()), 0);
                }
                count = 1;

            }
        } catch (ParserConfigurationException ex) {
        } catch (SAXException | IOException ex) {
        }
        totalShiftAmount = totalAmount;
        return totalAmount;

    }

    public void openDialogRemovedStock() {
        listOfRemovedStock = removedStockReportService.listOfRemovedStockForMarketShift(selectedShift);
        RequestContext.getCurrentInstance().update("dlgRemovedStock");
        RequestContext.getCurrentInstance().execute("PF('dlg_RemovedStock').show();");
    }

    public void updateShiftName() {
        int result = marketShiftService.updateShift(selectedShift);
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Vardiyaya Ait Excel Dosyasını Oluşturur.
     */
    public void createExcelFile() {
        marketShiftService.createExcelFile(selectedShift, totalShiftAmount, selectedOptions);
    }

    /**
     * Vardiyaya Ait Pdf Dosyasını Oluşturur.
     */
    public void creatPdfFile() {
        marketShiftService.createPdfFile(selectedShift, totalShiftAmount, selectedOptions);

    }

    public void showPreview() {
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }

        marwiz.goToPage("/pages/general/marketshift/marketshiftpreview.xhtml", list, 0, 116);
    }

    public void createFinancingDocumentInSameTime(int id) {
        shiftPaymentFinancingDocumentCon = new MarketShiftPaymentFinancingDocumentCon();
        selectedFinancingDocumentForDialog = new MarketShiftPaymentFinancingDocumentCon();
        differenceAmount = BigDecimal.valueOf(0);
        remainingAmount = BigDecimal.valueOf(0);
        incomeExpensePrice = null;
        employeePrice = null;

        for (MarketShiftPayment msp : listOfShiftPayment) {
            if (msp.getId() == id) {
                differenceAmount = msp.getSalesPrice().subtract(msp.getActualSalesPrice());
                shiftPaymentFinancingDocumentCon.getShiftPayment().getAccount().setName(msp.getAccount().getName());
                shiftPaymentFinancingDocumentCon.getShiftPayment().getAccount().setId(msp.getAccount().getId());
                shiftPaymentFinancingDocumentCon.getShiftPayment().setId(msp.getId());
                shiftPaymentFinancingDocumentCon.getShiftPayment().setIs_check(msp.isIs_check());
                shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().setId(msp.getSaleType().getId());
                shiftPaymentFinancingDocumentCon.getShiftPayment().getSafe().setId(msp.getSafe().getId());
                shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().setId(msp.getBankAccount().getId());
                shiftPaymentFinancingDocumentCon.getShiftPayment().setSalesPrice(msp.getSalesPrice());
                shiftPaymentFinancingDocumentCon.getShiftPayment().setActualSalesPrice(msp.getActualSalesPrice());
                shiftPaymentFinancingDocumentCon.getShiftPayment().getCurrency().setId(msp.getCurrency().getId());

                break;
            }
        }
        listOfPaymentFinancingDocCon = marketShiftPaymentService.findFinancingDocForShiftPayment(shiftPaymentFinancingDocumentCon.getShiftPayment());

        if (differenceAmount.compareTo(BigDecimal.valueOf(0)) == -1) {//Kullanıcıya Aktar ve Gelir oluştur
            isIncomeForCon = true;
            isDirection = false;
            differenceAmount = differenceAmount.multiply(BigDecimal.valueOf(-1));
        } else { //Kullanıcıyı Borçlandır ve Gider oluştur 
            isIncomeForCon = false;
            isDirection = true;
        }
        calculateRemainingAmount();
        RequestContext.getCurrentInstance().execute("PF('dlg_CreateFinancingDocumentInSameTime').show();");
    }

    public void saveShiftPaymentFinancingDocCon(int type) {
        if (type == 1 && shiftPaymentFinancingDocumentCon.getShiftPayment().getAccount().getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("employeefieldcannotbeempty")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 1 && (employeePrice == null || employeePrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 2 && shiftPaymentFinancingDocumentCon.getFinancingDocument().getIncomeExpense().getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectincomeexpense")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (type == 2 && (incomeExpensePrice == null || incomeExpensePrice.compareTo(BigDecimal.valueOf(0)) != 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
            String dateformat = format.format(selectedShift.getBeginDate());
            financingDocument.setDocumentDate(selectedShift.getBeginDate());
            financingDocument.getAccount().setId(shiftPaymentFinancingDocumentCon.getShiftPayment().getAccount().getId());
            financingDocument.getAccount().setName(shiftPaymentFinancingDocumentCon.getShiftPayment().getAccount().getName());

            if (type == 1) {//Personel
                financingDocument.setPrice(employeePrice);
                if (isDirection) {//Kullanıcıyı Borçlandır!!
                    financingDocument.setDescription(sessionBean.getLoc().getString("shiftdeficit") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                    if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 17) {
                        listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());

                        financingDocument.getFinancingType().setId(48);
                        firstId = shiftPaymentFinancingDocumentCon.getShiftPayment().getSafe().getId();
                        secondId = financingDocument.getAccount().getId();
                        bringCurrency();

                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 18) {
                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        financingDocument.getFinancingType().setId(74);

                        firstId = shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId();
                        secondId = financingDocument.getAccount().getId();
                        bringCurrency();

                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 75) {
                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        financingDocument.getFinancingType().setId(56);
                        firstId = shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId();
                        secondId = financingDocument.getAccount().getId();
                        bringCurrency();

                    }
                } else {//Kullancıya Aktar
                    financingDocument.setDescription(sessionBean.getLoc().getString("shiftexcess") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                    financingDocument.getFinancingType().setId(50);
                    secondId = financingDocument.getAccount().getId();
                    if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 17) {
                        listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());
                        for (Safe safe : listOfSafe) {
                            if (safe.getId() == shiftPaymentFinancingDocumentCon.getShiftPayment().getSafe().getId()) {
                                outCurrency = safe.getCurrency();
                                break;
                            }
                        }
                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 18) {
                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        for (BankAccount ba : listOfBankAccount) {
                            if (ba.getId() == shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId()) {
                                outCurrency = ba.getCurrency();
                                break;
                            }
                        }
                    }
                    inCurrency = sessionBean.getUser().getLastBranch().getCurrency();

                    if (inCurrency != null && outCurrency != null) {
                        financingDocument.setCurrency(outCurrency);
                        if (financingDocument.getId() == 0) {//guncelleme değilse döviz hesapla
                            financingDocument.setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
                        }
                        exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
                    } else {
                        exchange = "";
                    }
                }

            } else if (type == 2) {//Gelir- Gider
                financingDocument.setPrice(incomeExpensePrice);
                if (isDirection) {//Gider oluştur!!
                    financingDocument.setDescription(sessionBean.getLoc().getString("shiftdeficit") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                    listOfType = sessionBean.getTypes(20);
                    isIncomeExpense = true;
                    if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 17) {
                        listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());

                        financingDocument.getFinancingType().setId(48);//Nakit Tahsilat
                        firstId = shiftPaymentFinancingDocumentCon.getShiftPayment().getSafe().getId();
                        bringCurrency();

                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 18) {//Giden Havaleler
                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        financingDocument.getFinancingType().setId(74);

                        firstId = shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId();
                        bringCurrency();
                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 75) {//Kredi Kartı 
                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        financingDocument.getFinancingType().setId(56);
                        firstId = shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId();
                        bringCurrency();
                    }
                } else {//Gelir Oluştur
                    financingDocument.setDescription(sessionBean.getLoc().getString("shiftexcess") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                    listOfType = sessionBean.getTypes(20);
                    isIncomeExpense = true;

                    if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 17) {
                        listOfSafe = safeService.selectSafe(sessionBean.getUser().getLastBranch());

                        financingDocument.getFinancingType().setId(47);//Nakit Tahsilat
                        secondId = shiftPaymentFinancingDocumentCon.getShiftPayment().getSafe().getId();
                        bringCurrency();

                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 18) {//GELEN Havaleler
                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        financingDocument.getFinancingType().setId(73);

                        secondId = shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId();
                        bringCurrency();
                    } else if (shiftPaymentFinancingDocumentCon.getShiftPayment().getSaleType().getId() == 75) {//Kredi Kartı

                        listOfBankAccount = bankAccountService.bankAccountForSelect(" ", sessionBean.getUser().getLastBranch());
                        financingDocument.getFinancingType().setId(55);
                        secondId = shiftPaymentFinancingDocumentCon.getShiftPayment().getBankAccount().getId();
                        bringCurrency();
                    }
                }
            }

            if (remainingAmount.compareTo(BigDecimal.valueOf(0)) == 1) {
                if (financingDocument.getPrice().compareTo(remainingAmount) == 0 || financingDocument.getPrice().compareTo(remainingAmount) == -1) {
                    if (sessionBean.isPeriodClosed(financingDocument.getDocumentDate())) {
                        BigDecimal tempTotal = BigDecimal.valueOf(0);
                        tempTotal = remainingAmount.subtract(financingDocument.getPrice());
                        if (tempTotal.compareTo(BigDecimal.valueOf(0)) == 0) {
                            shiftPaymentFinancingDocumentCon.getShiftPayment().setIs_check(true);
                        } else {
                            shiftPaymentFinancingDocumentCon.getShiftPayment().setIs_check(false);
                        }
                        int result = marketShiftPaymentService.updateShiftPaymentForFinancingDoc(0, shiftPaymentFinancingDocumentCon.getShiftPayment(), financingDocument, firstId, secondId);
                        if (result > 0) {
                            listOfPaymentFinancingDocCon = marketShiftPaymentService.findFinancingDocForShiftPayment(shiftPaymentFinancingDocumentCon.getShiftPayment());
                            calculateRemainingAmount();
                            controlOpenShiftPayment();
                            if (shiftPaymentFinancingDocumentCon.getShiftPayment().isIs_check()) {
                                for (MarketShiftPayment m : listOfShiftPayment) {
                                    if (shiftPaymentFinancingDocumentCon.getShiftPayment().getId() == m.getId()) {
                                        m.setIs_check(shiftPaymentFinancingDocumentCon.getShiftPayment().isIs_check());
                                        m.setIsAvaialbelFinancingDocument(true);
                                    }
                                }
                            }
                            RequestContext.getCurrentInstance().update("frmMarketShiftTransfer:dtbDistribution");
                        } else if (result == -101) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        } else {
                            sessionBean.createUpdateMessage(result);
                        }

                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbeenteredbiggerthanremainingamount")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("differenceofpaymentisclosed")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");

            }
            employeePrice = null;
            incomeExpensePrice = null;
            shiftPaymentFinancingDocumentCon.setFinancingDocument(new FinancingDocument());
            financingDocument = new FinancingDocument();
            RequestContext.getCurrentInstance().update("frmCreateFinanacingDocument");

        }

    }

    public void calculateRemainingAmount() {
        BigDecimal t = BigDecimal.valueOf(0);
        for (MarketShiftPaymentFinancingDocumentCon con : listOfPaymentFinancingDocCon) {
            t = t.add(con.getFinancingDocument().getPrice());
        }
        remainingAmount = differenceAmount.subtract(t);
    }

    public void askBeforePdf() {
        isExcel = false;
        createOptionList();
        RequestContext.getCurrentInstance().update("dlgAskBeforeImport");
        RequestContext.getCurrentInstance().execute("PF('dlg_AskBeforeImport').show();");
    }

    public void askBeforeExcel() {
        isExcel = true;
        createOptionList();
        RequestContext.getCurrentInstance().update("dlgAskBeforeImport");
        RequestContext.getCurrentInstance().execute("PF('dlg_AskBeforeImport').show();");
    }

    public void createOptionList() {
        selectedOptions.clear();
        importOptionsList.clear();
        importOptionsList.add(new Option(1, sessionBean.getLoc().getString("saleslist")));
        importOptionsList.add(new Option(2, sessionBean.getLoc().getString("salesreturncategorytotaldump")));
        importOptionsList.add(new Option (3,sessionBean.getLoc().getString("accounttotaldump")));
        importOptionsList.add(new Option(4, sessionBean.getLoc().getString("foreigncurrencytotals")));
        importOptionsList.add(new Option(5, sessionBean.getLoc().getString("tax") + " " + sessionBean.getLoc().getString("totalmoney")));
        importOptionsList.add(new Option(6, sessionBean.getLoc().getString("deficit") + " - " + sessionBean.getLoc().getString("surplus") + " (" + sessionBean.getLoc().getString("employee") + ")"));
        importOptionsList.add(new Option(7, sessionBean.getLoc().getString("deficit") + " - " + sessionBean.getLoc().getString("surplus") + " (" + sessionBean.getLoc().getString("incomeexpense") + ")"));
        importOptionsList.add(new Option(8, sessionBean.getLoc().getString("cashdelivery")));
        importOptionsList.add(new Option(9, sessionBean.getLoc().getString("creditcarddelivery")));
        importOptionsList.add(new Option(10, sessionBean.getLoc().getString("creditdelivery")));
        importOptionsList.add(new Option(11, sessionBean.getLoc().getString("safetransfers")));
        importOptionsList.add(new Option(12, sessionBean.getLoc().getString("shiftgeneral")));
        importOptionsList.add(new Option(13, sessionBean.getLoc().getString("shiftsummary")));

        String resultUser = "";
        resultUser = marketShiftService.lastUserShiftReport();
        if (resultUser != null) {
            String[] tempArray = resultUser.split(",");
            for (int i = 0; i < tempArray.length; i++) {
                String string = tempArray[i];
                if (string.equals("1")) {
                    selectedOptions.add(Integer.toString(importOptionsList.get(i).getId()));
                }
            }
        } else {
            for (Option s : importOptionsList) {
                selectedOptions.add(Integer.toString(s.getId()));
            }
        }
    }

    public void importExcelPdf() {
        if (isClickApprove) {
            int result = 0;
            if (tempType == 0) {//Onayla
                SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
                String dateformat = format.format(selectedShift.getBeginDate());
                selectedShift.setDescription(sessionBean.getLoc().getString("shiftinheritedamount") + " (" + dateformat + " - " + selectedShift.getShiftNo() + ")");
                result = marketShiftReportPrintService.transferShiftPaymentToMainSafe(3, selectedShift, true);
            } else {//Geri al
                result = marketShiftReportPrintService.transferShiftPaymentToMainSafe(4, selectedShift, false);
            }

            if (result > 0) {
                if (tempType == 0) {//Onayla
                    selectedShift.setIs_Confirm(true);
                    tempShift.setIs_Confirm(true);
                } else {
                    selectedShift.setIs_Confirm(false);
                    tempShift.setIs_Confirm(false);
                }

                if (createPdf && tempType == 0) {
                    RequestContext.getCurrentInstance().execute("bringPdf();");
                }
                if (createExcel && tempType == 0) {
                    RequestContext.getCurrentInstance().execute("bringExcel();");
                }
                if (showPreview && tempType == 0) {
                    RequestContext.getCurrentInstance().execute("goToPreview();");
                } else {
                    RequestContext.getCurrentInstance().execute("updateDatatable()");
                }

                RequestContext.getCurrentInstance().execute("PF('dlg_ApproveOrOpenShift').hide();");

            } else if (result == -101) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                sessionBean.createUpdateMessage(result);
            }
        } else if (!isClickApprove) {
            if (isExcel) {
                RequestContext.getCurrentInstance().execute("bringExcel();");
            } else {
                RequestContext.getCurrentInstance().execute("bringPdf();");
            }
        }

        boolean isThere = false;
        String str = "";
        for (Option opt : importOptionsList) {
            isThere = false;
            for (String st : selectedOptions) {
                if (st.equals(Integer.toString(opt.getId()))) {
                    isThere = true;
                    break;

                } else {
                    isThere = false;
                }
            }

            if (isThere) {

                str = str + "1" + ",";
            } else {
                str = str + "0" + ",";
            }

        }
        str = str.substring(0, str.length() - 1);
        String resultUser = "";
        resultUser = marketShiftService.updateLastUserShiftReport(str);

    }

    public void resetApproveDialog() {
        isClickApprove = false;
    }

}
