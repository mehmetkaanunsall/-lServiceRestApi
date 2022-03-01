/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 04:31:31
 */
package com.mepsan.marwiz.finance.financingdocument.presentation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountCommissionService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.financingdocument.business.GFFinancingDocumentService;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.financingdocument.dao.FinancingDocumentVoucher;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documenttemplate.business.DocumentTemplateService;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.DataTableColumn;
import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import com.mepsan.marwiz.general.model.wot.PrintDocumentTemplate;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class FinancingDocumentBean extends GeneralBean<FinancingDocument> {

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{gfFinancingDocumentService}")
    public GFFinancingDocumentService gfFinancingDocumentService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{documentTemplateService}")
    public DocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{creditService}")
    private ICreditService creditService;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{stockTakingService}")
    public IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{bankAccountCommissionService}")
    public IBankAccountCommissionService bankAccountCommissionService;

    private int processType;
    private List<BankAccount> listOfBankAccount;
    private List<Safe> listOfSafe;
    private int firstId, secondId;
    private Currency outCurrency, inCurrency;
    private String exchange, bookType;
    private String wordFromNumber;
    private List<FinancingDocumentVoucher> listOfDocumentVoucher;
    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private int response;
    private boolean isIncomeExpense;
    private List<Branch> listOfBranch;
    private Branch branch;
    private BranchSetting branchSettingForSelection;
    private List<BankAccount> listOfBankAccount2;
    private List<Safe> listOfSafe2;
    private BankAccountCommission selectedBankAccountCommission;
    private String commissionMessage;

    private List<Type> listOfFinancingType;

    private boolean isUpdate;
    String createWhere;

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public String getWordFromNumber() {
        return wordFromNumber;
    }

    public void setWordFromNumber(String wordFromNumber) {
        this.wordFromNumber = wordFromNumber;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
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

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
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

    public List<Safe> getListOfSafe() {
        return listOfSafe;
    }

    public void setListOfSafe(List<Safe> listOfSafe) {
        this.listOfSafe = listOfSafe;
    }

    public List<Safe> getListOfSafe2() {
        return listOfSafe2;
    }

    public void setListOfSafe2(List<Safe> listOfSafe2) {
        this.listOfSafe2 = listOfSafe2;
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

    public void setGfFinancingDocumentService(GFFinancingDocumentService gfFinancingDocumentService) {
        this.gfFinancingDocumentService = gfFinancingDocumentService;
    }

    public String getDeleteControlMessage() {
        return deleteControlMessage;
    }

    public void setDeleteControlMessage(String deleteControlMessage) {
        this.deleteControlMessage = deleteControlMessage;
    }

    public String getDeleteControlMessage2() {
        return deleteControlMessage2;
    }

    public void setDeleteControlMessage2(String deleteControlMessage2) {
        this.deleteControlMessage2 = deleteControlMessage2;
    }

    public String getDeleteControlMessage1() {
        return deleteControlMessage1;
    }

    public void setDeleteControlMessage1(String deleteControlMessage1) {
        this.deleteControlMessage1 = deleteControlMessage1;
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

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public boolean isIsIncomeExpense() {
        return isIncomeExpense;
    }

    public void setIsIncomeExpense(boolean isIncomeExpense) {
        this.isIncomeExpense = isIncomeExpense;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
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

    public List<Type> getListOfFinancingType() {
        return listOfFinancingType;
    }

    public void setListOfFinancingType(List<Type> listOfFinancingType) {
        this.listOfFinancingType = listOfFinancingType;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-----------FinancingDocumentBean");
        listOfBranch = new ArrayList<>();
        branch = new Branch();
        branchSettingForSelection = new BranchSetting();
        listOfSafe = new ArrayList<>();
        listOfSafe2 = new ArrayList<>();
        selectedBankAccountCommission = new BankAccountCommission();
        listOfFinancingType = new ArrayList<>();
        toogleList = createToggleList(sessionBean.getUser());
        controlDeleteList = new ArrayList();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true,false, sessionBean.getUser().getLastBranchSetting().isIsForeignCurrency());
        }

        listOfBankAccount = new ArrayList<>();
        listOfBankAccount2 = new ArrayList<>();

        listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker
        listOfFinancingType = sessionBean.getTypes(20);

        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                branch.setId(b.getId());
                break;
            }
        }

        find();
        setListBtn(sessionBean.checkAuthority(new int[]{18, 19, 20}, 0));

    }

    @Override
    public void create() {
        processType = 1;
        isUpdate = true;
        isIncomeExpense = false;
        selectedObject = new FinancingDocument();
        selectedObject.setDocumentDate(new Date());
        listOfFinancingType = sessionBean.getTypes(20);

        for (Iterator<Type> iterator = listOfFinancingType.iterator(); iterator.hasNext();) {
            Type value = iterator.next();
            if (value.getId() == 108 || value.getId() == 109) {
                iterator.remove();
            }
        }

        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedObject.getBranch().setId(b.getId());
                selectedObject.getTransferBranch().setId(b.getId());
                break;
            }
        }
        resetFinancing();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgFinancingDocumentProc");
        context.execute("PF('dlg_financingdocumentproc').show();");
        context.update("frmFinancingDocumentProcess");
    }

    public void update() {
        
        processType = 2;
        isUpdate = false;
        listOfFinancingType = sessionBean.getTypes(20);

        listOfDocumentVoucher = financingDocumentService.listOfVancourDetail(selectedObject);

        if (selectedObject.getIncomeExpense().getId() > 0) {
            isIncomeExpense = true;
        } else {
            isIncomeExpense = false;
        }

        if (selectedObject.getTransferBranch().getId() == 0) {
            selectedObject.getTransferBranch().setId(selectedObject.getBranch().getId());
        }
        ///
        bringFinancingDocument(selectedObject);
        changeBranch();
        changeTransferBranch();
        bringTempCurrency();
        if (inCurrency != null && outCurrency != null) {
            selectedObject.setCurrency(outCurrency);
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

        //Update için kontrol yapıldı
        controlDeleteList.clear();
        controlDeleteList = financingDocumentService.testBeforeDelete(selectedObject);
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

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgFinancingDocumentProc");
        context.update("frmFinancingDocumentProcess");
        context.execute("PF('dlg_financingdocumentproc').show();");

    }

    @Override
    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getDocumentDate())) {
            int result = 0;
            RequestContext context = RequestContext.getCurrentInstance();

            if (processType == 1) {
                if (selectedObject.getFinancingType().getId() == 51 || selectedObject.getFinancingType().getId() == 52
                          || selectedObject.getFinancingType().getId() == 53 || selectedObject.getFinancingType().getId() == 54) {
                    if (selectedObject.getBranch().getId() == selectedObject.getTransferBranch().getId()) {
                        selectedObject.getTransferBranch().setId(0);
                    }
                }
                result = financingDocumentService.create(selectedObject, firstId, secondId);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects = findall(createWhere);
                }

            } else {//güncelleme
                if (selectedObject.getBankAccountCommissionId() != 0) {//Komisyon tablosunda var ise
                    commissionMessage = sessionBean.getLoc().getString("ifthismovementisupdatedcommissionmovementrelatedtothisrecordwillbeupdated");
                    commissionMessage = commissionMessage + " " + sessionBean.getLoc().getString("areyousure");
                    RequestContext.getCurrentInstance().update("dlgCommissionWarning");
                    RequestContext.getCurrentInstance().execute("PF('dlg_CommissionWarning').show();");
                } else {
                    result = financingDocumentService.update(selectedObject, firstId, secondId);
                }
            }
            if (selectedObject.getBankAccountCommissionId() == 0) {
                if (result > 0) {
                    context.execute("PF('financingDocumentPF').filter();");
                    context.execute("PF('dlg_financingdocumentproc').hide();");
                    context.update("frmFinancingDocument:dtbFinancingDocument");
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
    public LazyDataModel<FinancingDocument> findall(String where) {
        return new CentrowizLazyDataModel<FinancingDocument>() {
            @Override
            public List<FinancingDocument> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<FinancingDocument> result;
                int count;
                result = financingDocumentService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                count = financingDocumentService.count(where);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
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
            case 49://borc dekontu
                if (isIncomeExpense) {
                    firstId = financingDocument.getIncomeExpense().getId();
                } else {
                    firstId = financingDocument.getAccount().getId();
                }
                break;
            case 50://alacak dekontu
                if (isIncomeExpense) {
                    secondId = financingDocument.getIncomeExpense().getId();
                } else {
                    secondId = financingDocument.getAccount().getId();
                }
                break;
            case 51://banka->banka
                firstId = financingDocument.getOutMovementId();
                secondId = financingDocument.getInMovementId();
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
            case 55://cari->banka ticari
            case 73://kredi kartı
                if (isIncomeExpense) {
                    firstId = financingDocument.getIncomeExpense().getId();
                } else {
                    firstId = financingDocument.getAccount().getId();
                }
                secondId = financingDocument.getInMovementId();
                break;
            case 56://banka->cari ticari
            case 74://kredi kartı
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
        switch (selectedObject.getFinancingType().getId()) {
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
                outCurrency = selectedObject.getCurrency();
                break;
            case 50://alacak
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                outCurrency = selectedObject.getCurrency();
                break;
            case 51://banka->banka
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
                        outCurrency = ba.getCurrency();
                    }
                }
                for (BankAccount ba : listOfBankAccount2) {
                    if (ba.getId() == secondId) {
                        inCurrency = ba.getCurrency();
                    }
                }
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
                        selectedObject.getTransferBranch().setId(safe.getBranch().getId());
                    }
                }
                break;
            case 53://kasa->banka
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == firstId) {
                        outCurrency = safe.getCurrency();
                        break;
                    }
                }
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        inCurrency = ba.getCurrency();
                        break;
                    }
                }
                break;
            case 54://banka->kasa
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == firstId) {
                        outCurrency = ba.getCurrency();
                        break;
                    }
                }
                for (Safe safe : listOfSafe) {
                    if (safe.getId() == secondId) {
                        inCurrency = safe.getCurrency();
                        selectedObject.getTransferBranch().setId(safe.getBranch().getId());
                        break;
                    }
                }
                break;
            case 55://cari->banka ticari
            case 73://kredi kartı
                inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                for (BankAccount ba : listOfBankAccount) {
                    if (ba.getId() == secondId) {
                        outCurrency = ba.getCurrency();
                        break;
                    }
                }
                break;
            case 56://banka->cari ticari
            case 74://kredi kartı
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
            selectedObject.setCurrency(outCurrency);
            selectedObject.setExchangeRate(exchangeService.bringExchangeRate(outCurrency, inCurrency, sessionBean.getUser()));//1. ve 2. arasındaki kuru hesaplama
            exchange = sessionBean.currencySignOrCode(outCurrency.getId(), 0) + " -> " + sessionBean.currencySignOrCode(inCurrency.getId(), 0);// örn: $->€
        } else {
            exchange = "";
        }

        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
    }

    /*
    * cari kitabına cıft tıkladıgımızda calısır
     */
    public void updateAllInformation() {

        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            accountBookFilterBean.setSelectedData(null);
            switch (selectedObject.getFinancingType().getId()) {
                case 47://cari->kasa
                    firstId = selectedObject.getAccount().getId();
                    break;
                case 48://kasa->cari
                    secondId = selectedObject.getAccount().getId();
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
                default:
                    break;

            }
            accountBookFilterBean.setSelectedData(null);
        } else if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            selectedObject.setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            switch (selectedObject.getFinancingType().getId()) {
                case 47://cari->kasa
                    firstId = selectedObject.getIncomeExpense().getId();
                    break;
                case 48://kasa->cari
                    secondId = selectedObject.getIncomeExpense().getId();
                    break;
                case 49://borc dekontu
                    firstId = selectedObject.getIncomeExpense().getId();
                    bringCurrency();
                    break;
                case 50://alacak dekontu
                    secondId = selectedObject.getIncomeExpense().getId();
                    bringCurrency();
                    break;
                case 55://cari->banka
                case 73:
                    firstId = selectedObject.getIncomeExpense().getId();
                    break;
                case 56://banka->cari
                case 74:
                    secondId = selectedObject.getIncomeExpense().getId();
                default:
                    break;

            }
            incomeExpenseBookFilterBean.setSelectedData(null);
        }
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");
    }

    /**
     * işlem tipi değiştiğinde tetiklenir.
     */
    public void resetFinancing() {
        firstId = 0;
        secondId = 0;
        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        selectedObject.setExchangeRate(BigDecimal.ONE);
        selectedObject.setDocumentDate(new Date());
        inCurrency = null;
        outCurrency = null;
        exchange = "";
        selectedObject.getAccount().setId(0);
        selectedObject.getAccount().setName("");
        selectedObject.getIncomeExpense().setId(0);
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:txtCurrentName");
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:txtCurrentName2");
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:txtCurrentName3");
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:txtCurrentName4");
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:txtCurrentName5");
        changeBranch();
        changeTransferBranch();

        switch (selectedObject.getFinancingType().getId()) {
            case 47://cari->kasa
                bookType = "Musteri,MTedatikçi";
                break;
            case 48://kasa->cari
                bookType = "Tedarikçi,MTedatikçi";
                break;
            case 55://cari->banka Ticari
                bookType = "Musteri,MTedatikçi";
                break;
            case 56://banka->cari Ticari
                bookType = "Tedarikçi,MTedatikçi";
                break;
            case 73://cari->banka Kredikartı
                bookType = "Musteri,MTedatikçi";
                break;
            case 74://banka->cari Kredikartı
                bookType = "Tedarikçi,MTedatikçi";
                break;
            default:
                break;
        }
        RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:pgrFinancingDocumentProcess");

    }

    @Override
    public void detailFilter() {

    }

    public void find() {
        
        createWhere = financingDocumentService.createWhere(branch);
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmFinancingDocument:dtbFinancingDocument");
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {

            listOfObjects = findall(createWhere);
        } else {
            gfFinancingDocumentService.makeSearch(autoCompleteValue, createWhere);
            listOfObjects = gfFinancingDocumentService.searchResult;
        }
    }

    /**
     * Carmi yoksa gelir gider kartımı seçeceğini belirleyen butona tıkladıkça
     * tetiklenir.
     */
    public void accountOrIncomeExp() {
        if (isIncomeExpense) {//cari seçecek
            selectedObject.setIncomeExpense(new IncomeExpense());
            isIncomeExpense = false;
        } else {//gelirgider sececek
            selectedObject.setAccount(new Account());
            isIncomeExpense = true;
        }

    }

    public void loadJson() {
        int documentBringId = 0;
        branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranch());
        if (selectedObject.getFinancingType().getId() == 56 || selectedObject.getFinancingType().getId() == 48) {//tediye
            documentBringId = 71;
        } else if (selectedObject.getFinancingType().getId() == 55 || selectedObject.getFinancingType().getId() == 47) {//tahsilat
            documentBringId = 70;
        }
        DocumentTemplate documentTemplate = documentTemplateService.bringInvoiceTemplate(documentBringId);
        if (documentTemplate.getId() <= 0) {
            return;
        }
        Gson gson = new Gson();
        PrintDocumentTemplate logs = gson.fromJson(documentTemplate.getJson(), new TypeToken<PrintDocumentTemplate>() {
        }.getType());

        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("printPanel");
        droppable.getChildren().clear();
        for (DocumentTemplateObject dto : logs.getListOfObjects()) {
            if (dto.getKeyWord().contains("container")) {
                OutputPanel op = new OutputPanel();
                op.setStyle("border: 1px solid black ;width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                op.setId(dto.getKeyWord());

                droppable.getChildren().add(op);
            } else if (dto.getKeyWord().contains("imagepnl")) {
                GraphicImage gr = new GraphicImage();
                gr.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                gr.setUrl("../upload/template/" + documentTemplate.getId() + "_" + dto.getKeyWord() + "." + "png");
                gr.setCache(false);
                droppable.getChildren().add(gr);
                RequestContext.getCurrentInstance().update("printPanel");
            } else if (dto.getKeyWord().contains("itemspnl")) {
                OutputPanel op = new OutputPanel();
                op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                op.setId(dto.getKeyWord());

                droppable.getChildren().add(op);
                RequestContext.getCurrentInstance().update("printPanel");
                String direction = documentTemplate.isIsVertical() == true ? "horizontal" : "landscape";
                System.out.println(dto.getFontSize());
                StringBuilder sb = new StringBuilder();
                sb.append(
                          " <style>"
                          + "        #" + dto.getKeyWord() + " table {"
                          + "            font-family: arial, sans-serif;"
                          + "            border-collapse: collapse;"
                          + "            width: 100%;"
                          + "       table-layout: fixed;"
                          + "        }"
                          + "        #" + dto.getKeyWord() + " table tr td, #" + dto.getKeyWord() + " table tr th {"
                          + "            border: 1px solid #dddddd;"
                          + "            text-align: " + dto.getFontAlign() + ";"
                          + "            padding: 8px;"
                          + "            word-wrap: break-word;"
                          + "            height: 33px;"
                          + "            font-size: " + dto.getFontSize() + "pt;"
                          + "        }"
                          + "   @page { size: " + direction + ";"
                          + "margin-top: " + documentTemplate.getMargin_top() + "mm;"
                          + "margin-bottom: " + documentTemplate.getMargin_bottom() + "mm;"
                          + "margin-left: " + documentTemplate.getMargin_left() + "mm;"
                          + "margin-right: " + documentTemplate.getMargin_right() + "mm;}"
                          + "   @media print {"
                          + "     html, body {"
                          + "    width: " + documentTemplate.getWidth() + "mm;"
                          + "    height: " + documentTemplate.getHeight() + "mm;"
                          + "     }}"
                          + "    </style> ");
                sb.append("<table><colgroup> ");
                List<Integer> widthList = new ArrayList<>();
                int id = 0;
                int countWidths = 0;
                int j = 0;
                for (DataTableColumn dtc : logs.getItems()) {
                    if (dtc.isVisibility()) {
                        int width = (int) (dtc.getWidth() * 100 / (dto.getWidth() * 4));
                        System.out.println(dtc.getWidth() + " ------ " + dto.getWidth());
                        widthList.add(id, width);
                        countWidths = countWidths + width;
                        id++;
                        if (width == 0) {
                            j++;
                        }
                    }
                }
                for (Integer i : widthList) {
                    if (i == 0) {
                        i = (100 - countWidths) / j;
                    }
                    System.out.println("i " + i);
                    sb.append("<col style=\"width:" + i + "%\" />");
                }
                sb.append("</colgroup>");

                sb.append("<tr>");
                for (DataTableColumn dtc : logs.getItems()) {
                    if (dtc.isVisibility()) {
                        sb.append("<th> ").append(sessionBean.getLoc().getString(dtc.getId())).append("</th>");
                    }
                }
                sb.append("</tr>");

                //int rowCount = (int) (dto.getHeight() / (33 * 0.25));
                //  for (int i = 0; i < rowCount - 1; i++) {
                for (int i = 0; i < listOfDocumentVoucher.size(); i++) {
                    sb.append("<tr>");
                    if (i < listOfDocumentVoucher.size()) {
                        FinancingDocumentVoucher fdv = listOfDocumentVoucher.get(i);
                        if (logs.getItems().get(0).isVisibility()) {
                            sb.append("<td>").append((fdv.getAccount().getName()) == null ? "" : fdv.getAccount().getName()).append("</td>");
                        }
                        if (logs.getItems().get(1).isVisibility()) {
                            sb.append("<td>").append((fdv.getBankAccount().getBankBranch().getBank().getName()) == null ? "" : fdv.getBankAccount().getBankBranch().getBank().getName()).append("</td>");
                        }
                        if (logs.getItems().get(2).isVisibility()) {
                            sb.append("<td>").append((fdv.getBankAccount().getBankBranch().getName()) == null ? "" : fdv.getBankAccount().getBankBranch().getName()).append("</td>");
                        }
                        if (logs.getItems().get(3).isVisibility()) {
                            String docnumber = "";
                            docnumber += fdv.getChequeBill().getDocumentSerial() == null ? "" : fdv.getChequeBill().getDocumentSerial();
                            docnumber += "" + fdv.getChequeBill().getDocumentNo() == null ? "" : fdv.getChequeBill().getDocumentNo();
                            sb.append("<td>").append((docnumber).equals("null") ? "" : docnumber).append("</td>");
                        }
                        if (logs.getItems().get(4).isVisibility()) {
                            sb.append("<td>").append((fdv.getChequeBill().getAccountNumber()) == null ? "" : fdv.getChequeBill().getAccountNumber()).append("</td>");
                        }
                        if (logs.getItems().get(5).isVisibility()) {
                            sb.append("<td>").append((fdv.getChequeBill().getId() > 0 ? (fdv.getChequeBill().getExpiryDate() == null ? "" : fdv.getChequeBill().getExpiryDate()) : "")).append("</td>");
                        }
                        if (logs.getItems().get(6).isVisibility()) {
                            sb.append("<td>").append((fdv.getChequeBill().getId() > 0 ? (fdv.getPrice() == null ? "" : sessionBean.getNumberFormat().format(fdv.getPrice()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)) : "")).append("</td>");
                        }
                        if (logs.getItems().get(7).isVisibility()) {
                            sb.append("<td>").append((fdv.getPrice()) == null ? "" : sessionBean.getNumberFormat().format(fdv.getPrice()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                        }
                    } else {
                        if (logs.getItems().get(0).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(1).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(2).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(3).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(4).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(5).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(6).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(7).isVisibility()) {
                            sb.append("<td></td>");
                        }
                    }
                    sb.append("</tr>");

                }
                sb.append("</table>");

                RequestContext.getCurrentInstance().execute("$('#" + dto.getKeyWord() + "').append('" + sb + "')");
            } else {
                OutputPanel op = new OutputPanel();
                op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");

                OutputLabel label = new OutputLabel();
                OutputLabel labelTitle = new OutputLabel();
                labelTitle.setRendered(!dto.isLabel());
                if (dto.getFontStyle().size() > 0) {
                    String style = "";
                    for (String s : dto.getFontStyle()) {
                        if (s.equals("italic")) {
                            style = style + "font-style:italic;";
                        } else {
                            style = style + "font-weight:700 !important;";
                        }
                    }
                    labelTitle.setStyle("float:left;font-weight:700 !important;word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                    label.setStyle(style + "word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                } else {
                    labelTitle.setStyle("float:left;font-weight:700 !important;word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                    label.setStyle("word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");

                }

                if (!(dto.getKeyWord().contains("branchname") || /*dto.getKeyWord().contains("branchaddress") || dto.getKeyWord().contains("branchmail") || dto.getKeyWord().contains("branchtelephone")
                        || dto.getKeyWord().contains("branchtaxnumber") || dto.getKeyWord().contains("branchtaxoffice") ||*/ dto.getKeyWord().contains("grandtotalmoneywrite"))) {
                    labelTitle.setValue(dto.getName() + " : ");
                }

                if (dto.getKeyWord().trim().contains("textpnl")) {
                    labelTitle.setValue(dto.getName());
                }
                System.out.println(dto.getName() + " *-*-*-*- *****" + dto.getKeyWord());

                if (dto.getKeyWord().contains("customertitlepnl")) {

                    label.setValue(((selectedObject.getAccount().getTitle()) == null ? "" : selectedObject.getAccount().getTitle()));

                } else if (dto.getKeyWord().contains("customeraddresspnl")) {

                    label.setValue(((selectedObject.getAccount().getAddress()) == null ? "" : selectedObject.getAccount().getAddress()));

                } else if (dto.getKeyWord().contains("customerphonepnl")) {
                    label.setValue(((selectedObject.getAccount().getPhone()) == null ? "" : selectedObject.getAccount().getPhone()));
                } else if (dto.getKeyWord().contains("customertaxofficepnl")) {
                    label.setValue(((selectedObject.getAccount().getTaxOffice()) == null ? "" : selectedObject.getAccount().getTaxOffice()));
                } else if (dto.getKeyWord().contains("customertaxnumberpnl")) {
                    label.setValue(((selectedObject.getAccount().getTaxNo()) == null ? "" : selectedObject.getAccount().getTaxNo()));
                } else if (dto.getKeyWord().contains("customerbalancepnl")) {
                    label.setValue(((selectedObject.getAccount().getBalance()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getAccount().getBalance()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                } else if (dto.getKeyWord().contains("invoicenopnl")) {
                    label.setValue(((selectedObject.getDocumentNumber()) == null ? "" : selectedObject.getDocumentNumber()));
                } else if (dto.getKeyWord().contains("customertaxofficenumberpnl")) {
                    String a = "";
                    a += (selectedObject.getAccount().getTaxOffice()) == null ? "" : selectedObject.getAccount().getTaxOffice();
                    a += "" + (selectedObject.getAccount().getTaxNo()) == null ? "" : selectedObject.getAccount().getTaxNo();
                    label.setValue(a.equals("null") ? "" : a);
                } else if (dto.getKeyWord().contains("exchangeratepnl")) {
                    label.setValue(((selectedObject.getPrice()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getPrice())));
                } else if (dto.getKeyWord().contains("totalmoneypnl")) {
                    label.setValue(((selectedObject.getPrice()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getPrice()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                } else if (dto.getKeyWord().contains("invoicedatepnl")) {
                    label.setValue(((selectedObject.getDocumentDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDocumentDate())));
                } else if (dto.getKeyWord().contains("branchnamepnl")) {
                    label.setValue(((branchSettingForSelection.getBranch().getName()) == null ? "" : branchSettingForSelection.getBranch().getName()));
                } else if (dto.getKeyWord().contains("branchaddresspnl")) {
                    label.setValue(((branchSettingForSelection.getBranch().getAddress()) == null ? "" : branchSettingForSelection.getBranch().getAddress()));
                } else if (dto.getKeyWord().contains("branchmailpnl")) {
                    label.setValue(((branchSettingForSelection.getBranch().getMail()) == null ? "" : branchSettingForSelection.getBranch().getMail()));
                } else if (dto.getKeyWord().contains("branchtelephonepnl")) {
                    label.setValue(((branchSettingForSelection.getBranch().getPhone()) == null ? "" : branchSettingForSelection.getBranch().getPhone()));
                } else if (dto.getKeyWord().contains("branchtaxofficepnl")) {
                    label.setValue(((branchSettingForSelection.getBranch().getTaxOffice()) == null ? "" : branchSettingForSelection.getBranch().getTaxOffice()));
                } else if (dto.getKeyWord().contains("branchtaxnumberpnl")) {
                    label.setValue(((branchSettingForSelection.getBranch().getTaxNo()) == null ? "" : branchSettingForSelection.getBranch().getTaxNo()));
                } else if (dto.getKeyWord().contains("cashpnl")) {
                    BigDecimal bd = BigDecimal.ZERO;
                    if (selectedObject.getFinancingType().getId() == 47 || selectedObject.getFinancingType().getId() == 48) {//Nakit Ödeme Yada Nakit Tahsilat
                        bd = selectedObject.getPrice();
                    }
                    label.setValue(((bd) == null ? "" : sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                } /* else if (dto.getKeyWord().contains("checkbillpnl")) {
                    label.setValue(((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                }*/ else if (dto.getKeyWord().contains("grandtotalmoneywritepnl")) {
                    label.setValue(wordFromNumber);
                } else if (dto.getKeyWord().contains("recipientpersonpnl")) {
                    label.setValue(((selectedObject.getUserCreated().getName()) == null ? "" : selectedObject.getUserCreated().getName()));
                } else if (dto.getKeyWord().contains("deliverypersonpnl")) {
                    label.setValue(((selectedObject.getAccount().getName()) == null ? "" : selectedObject.getAccount().getName()));
                }
                op.getChildren().add(labelTitle);
                op.getChildren().add(label);
                droppable.getChildren().add(op);
                RequestContext.getCurrentInstance().update("printPanel");

            }
        }

        RequestContext.getCurrentInstance().execute("printData();");

    }

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getDocumentDate())) {
            if (selectedObject.getBankAccountCommissionId() != 0) {//Komisyon tablosunda var ise
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
                            case -109: //faturaya bağlı
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleasedeletetheinvoicefromtheinvoicepage");
                                deleteControlMessage2 = sessionBean.getLoc().getString("documentno") + " : ";
                                break;
                            case -110:
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtocloseshiftitcannotbedeleted");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleasereopentheshift");
                                deleteControlMessage2 = sessionBean.getLoc().getString("shiftno") + " : " + controlDeleteList.get(0).getR_recordno();
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
                                Logger.getLogger(FinancingDocumentBean.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            relatedRecord += selectedObject.getAccount().getName() + " - " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), processDate);
                        } else if (response != -106) {
                            relatedRecord += controlDeleteList.get(0).getR_recordno();
                        }
                        if (response != -108) {
                            relatedRecordId = controlDeleteList.get(0).getR_record_id();
                            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
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
        result = financingDocumentService.delete(selectedObject);
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_financingdocumentproc').hide();");
            context.update("frmFinancingDocument:dtbFinancingDocument");
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
            case -107:
                StockTaking stockTaking = new StockTaking();
                stockTaking.setId(relatedRecordId);
                stockTaking = stockTakingService.find(stockTaking);
                list.add(stockTaking);
                marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 1, 54);
                break;
            default:
                break;
        }
    }

    public void edit() {
        if (response != -106 && response != -107) {
            goToRelatedRecordBefore();
        } else {
            relatedRecord = "";
            if (response == -106) {
                deleteControlMessage1 = "";
                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosaleitcannotbeupdated");
                deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : " + controlDeleteList.get(0).getR_recordno();
            } else if (response == -107) {
                deleteControlMessage2 = "";
                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtostocktakingitcannotbeupdated");
                deleteControlMessage1 = sessionBean.getLoc().getString("warehousestocktaking") + " : " + controlDeleteList.get(0).getR_recordno();
            }

            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
        }

    }

    public void changeBank(int type) {
        if (firstId == secondId && selectedObject.getTransferBranch().getId() == selectedObject.getBranch().getId()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("betweensamebankaccountscannotbetransferred")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

            if (type == 1) {
                firstId = 0;
                RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:slcBank");
            } else if (type == 2) {
                secondId = 0;
                RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:slcBank2");
            }
        }
    }

    public void changeSafe(int type) {
        if (firstId == secondId && selectedObject.getTransferBranch().getId() == selectedObject.getBranch().getId()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("betweensamesafescannotbetransferred")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

            if (type == 1) {
                firstId = 0;
                RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:slcSafe2");
            } else if (type == 2) {
                secondId = 0;
                RequestContext.getCurrentInstance().update("frmFinancingDocumentProcess:slcSafe3");
            }
        }
    }

    public void changeBranch() {
        if (processType == 1) {
            selectedObject.getAccount().setId(0);
            selectedObject.getAccount().setName("");
            selectedObject.getAccount().setTitle("");
            selectedObject.getIncomeExpense().setId(0);
            selectedObject.getIncomeExpense().setName("");
        }
        switch (selectedObject.getFinancingType().getId()) {
            case 47://cari->kasa
            case 48://kasa->cari
                listOfSafe = safeService.selectSafe(selectedObject.getBranch());
                break;
            case 51://banka->banka
                listOfBankAccount = bankAccountService.bankAccountForSelect("", selectedObject.getBranch());
                break;
            case 52://kasa->kasa
                listOfSafe = safeService.selectSafe(selectedObject.getBranch());
                if (processType == 1) {
                    listOfSafe2 = safeService.selectSafe(listOfBranch);
                } else {
                    listOfSafe2 = safeService.selectSafe(selectedObject.getTransferBranch());
                }
                break;
            case 53: //kasa->banka
                listOfSafe = safeService.selectSafe(selectedObject.getBranch());
                break;
            case 54:  //banka->kasa
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedObject.getBranch());//ticari
                if (processType == 1) {
                    listOfSafe = safeService.selectSafe(listOfBranch);
                } else {
                    listOfSafe = safeService.selectSafe(selectedObject.getTransferBranch());
                }
                break;
            case 55://cari->banka
            case 56://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedObject.getBranch());
                break;
            case 73://cari->banka
            case 74://banka->cari
                listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedObject.getBranch());
                break;
            default:
                break;
        }
    }

    public void changeTransferBranch() {
        if (selectedObject.getFinancingType().getId() == 51) {
            listOfBankAccount2 = bankAccountService.bankAccountForSelect("", selectedObject.getTransferBranch());
        } else if (selectedObject.getFinancingType().getId() == 53) {
            listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedObject.getTransferBranch());//ticari
        }
    }

    public void updateCommission() {
        int result = 0;
        selectedBankAccountCommission = bankAccountCommissionService.findBankAccountCommission(selectedObject.getBankAccountCommissionId());
        if (selectedBankAccountCommission.getFinancingDocument().getId() == selectedObject.getId()) {//Bankadan bankaya virman
            selectedBankAccountCommission.getFinancingDocument().getFinancingType().setId(selectedObject.getFinancingType().getId());
            selectedBankAccountCommission.getFinancingDocument().setId(selectedObject.getId());
            selectedBankAccountCommission.getFinancingDocument().setDocumentNumber(selectedObject.getDocumentNumber());
            selectedBankAccountCommission.getFinancingDocument().setPrice(selectedObject.getPrice());
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

            selectedBankAccountCommission.getFinancingDocument().getCurrency().setId(selectedObject.getCurrency().getId());
            selectedBankAccountCommission.getFinancingDocument().setExchangeRate(selectedObject.getExchangeRate());
            selectedBankAccountCommission.getFinancingDocument().setDocumentDate(selectedObject.getDocumentDate());
            selectedBankAccountCommission.getFinancingDocument().setDescription(selectedObject.getDescription());
            selectedBankAccountCommission.getFinancingDocument().setInMovementId(secondId);
            selectedBankAccountCommission.getFinancingDocument().setOutMovementId(firstId);
            selectedBankAccountCommission.getFinancingDocument().getBranch().setId(selectedObject.getBranch().getId());
            selectedBankAccountCommission.getFinancingDocument().getTransferBranch().setId(selectedObject.getTransferBranch().getId());
        } else {//kredi kartı ödeme komisyon
            selectedBankAccountCommission.getCommissionFinancingDocument().getFinancingType().setId(selectedObject.getFinancingType().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setId(selectedObject.getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().getIncomeExpense().setId(selectedObject.getIncomeExpense().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setDocumentNumber(selectedObject.getDocumentNumber());
            selectedBankAccountCommission.getCommissionFinancingDocument().setPrice(selectedObject.getPrice());
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionFinancingDocument().getPrice() != null) {
                selectedBankAccountCommission.getFinancingDocument().setPrice(selectedBankAccountCommission.getTotalMoney()
                          .subtract(selectedBankAccountCommission.getCommissionFinancingDocument().getPrice()));//Diğeri de güncellenmeli
            }
            selectedBankAccountCommission.setCommissionMoney(selectedObject.getPrice());
            BigDecimal commissionRate = BigDecimal.valueOf(0);
            if (selectedBankAccountCommission.getTotalMoney() != null && selectedBankAccountCommission.getCommissionMoney() != null) {
                commissionRate = BigDecimal.valueOf(100).multiply(selectedBankAccountCommission.getCommissionMoney());
                commissionRate = commissionRate.divide(selectedBankAccountCommission.getTotalMoney(), 4, RoundingMode.HALF_EVEN);
            }
            selectedBankAccountCommission.setCommissionRate(commissionRate);

            selectedBankAccountCommission.getCommissionFinancingDocument().getCurrency().setId(selectedObject.getCurrency().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().setExchangeRate(selectedObject.getExchangeRate());
            selectedBankAccountCommission.getCommissionFinancingDocument().setDocumentDate(selectedObject.getDocumentDate());
            selectedBankAccountCommission.getCommissionFinancingDocument().setDescription(selectedObject.getDescription());
            selectedBankAccountCommission.getCommissionFinancingDocument().setInMovementId(secondId);
            selectedBankAccountCommission.getCommissionFinancingDocument().setOutMovementId(firstId);
            selectedBankAccountCommission.getCommissionFinancingDocument().getBranch().setId(selectedObject.getBranch().getId());
            selectedBankAccountCommission.getCommissionFinancingDocument().getTransferBranch().setId(selectedObject.getTransferBranch().getId());
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
                RequestContext.getCurrentInstance().update("frmFinancingDocument:dtbFinancingDocument");
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
        selectedBankAccountCommission.setId(selectedObject.getBankAccountCommissionId());
        result = bankAccountCommissionService.deleteCommission(selectedBankAccountCommission);
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_financingdocumentproc').hide();");
            context.update("frmFinancingDocument:dtbFinancingDocument");
        }
        sessionBean.createUpdateMessage(result);
    }

}
