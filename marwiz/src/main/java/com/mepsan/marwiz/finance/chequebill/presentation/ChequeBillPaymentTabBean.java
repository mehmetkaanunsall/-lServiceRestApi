/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillPaymentService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.ChequeBillPayment;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class ChequeBillPaymentTabBean extends AuthenticationLists {

    private List<ChequeBillPayment> listOfObjects;
    private ChequeBillPayment selectedObject;
    private ChequeBill selectedChequeBill;
    private List<Type> typeList;
    private List<Safe> safeList;
    private List<BankAccount> bankAccountList;
    private int processType;
    private Currency outCurrency, inCurrency;
    private String exchange;
    private BigDecimal oldValue;
    List<CheckDelete> controlDeleteList;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{chequeBillPaymentService}")
    public IChequeBillPaymentService chequeBillPaymentService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    public List<ChequeBillPayment> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<ChequeBillPayment> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public ChequeBillPayment getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(ChequeBillPayment selectedObject) {
        this.selectedObject = selectedObject;
    }

    public ChequeBill getSelectedChequeBill() {
        return selectedChequeBill;
    }

    public void setSelectedChequeBill(ChequeBill selectedChequeBill) {
        this.selectedChequeBill = selectedChequeBill;
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Type> typeList) {
        this.typeList = typeList;
    }

    public List<Safe> getSafeList() {
        return safeList;
    }

    public void setSafeList(List<Safe> safeList) {
        this.safeList = safeList;
    }

    public List<BankAccount> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccount> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
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

    public void setChequeBillPaymentService(IChequeBillPaymentService chequeBillPaymentService) {
        this.chequeBillPaymentService = chequeBillPaymentService;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
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

    @PostConstruct
    public void init() {
        System.out.println("-------ChequeBillPaymentTabBean-----");
        listOfObjects = new ArrayList<>();
        selectedObject = new ChequeBillPayment();
        selectedChequeBill = new ChequeBill();
        safeList = new ArrayList<>();
        bankAccountList = new ArrayList<>();
        oldValue = BigDecimal.valueOf(0);
        controlDeleteList = new ArrayList<>();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof ChequeBill) {
                    selectedChequeBill = (ChequeBill) ((ArrayList) sessionBean.parameter).get(i);
                    listOfObjects = chequeBillPaymentService.listChequeBillPayment(selectedChequeBill);
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{28, 29, 30}, 0));

    }

    public void createDialog(int type) {
        processType = type;
        getPaymentType(); // nakit ,kredi kartı tipleri çekildi.
        safeList = safeService.selectSafe(selectedChequeBill.getBranch());
        inCurrency = new Currency();
        outCurrency = new Currency();
        if (processType == 1) {
            selectedObject = new ChequeBillPayment();
            selectedObject.getType().setId(18);
            changeType();
            // tarih Bugün olarak set edildi.
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            selectedObject.setProcessDate(calendar.getTime());
            selectedObject.getFinancingDocument().setDescription(sessionBean.getLoc().getString("portfoliono") + " : " + selectedChequeBill.getPortfolioNumber() + " - " + (selectedChequeBill.isIsCheque() ? (selectedChequeBill.isIsCustomer() ? sessionBean.getLoc().getString("chequerecovery") : sessionBean.getLoc().getString("chequepayment")) : (selectedChequeBill.isIsCustomer() ? sessionBean.getLoc().getString("billrecovery") : sessionBean.getLoc().getString("billpayment"))));
            selectedObject.setPrice(selectedChequeBill.getRemainingMoney());
            selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
            bringCurrency();

        } else if (processType == 2) {
            oldValue = selectedObject.getPrice().multiply(selectedObject.getExchangeRate());

            switch (selectedObject.getType().getId()) {
                case 18:// kredi kartı  
                    bankAccountList = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedChequeBill.getBranch());
                    break;
                case 75:// eft/havale 
                    bankAccountList = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedChequeBill.getBranch());
                    break;
                default:
                    break;
            }
        }
        RequestContext.getCurrentInstance().update("dlgChequeBillPaymentProcess");
        RequestContext.getCurrentInstance().execute("PF('dlg_ChequeBillPaymentProcess').show()");
    }

    public void getPaymentType() {
        typeList = new ArrayList<>();
        for (Type type : sessionBean.getTypes(15)) {
            if (type.getId() == 17 || type.getId() == 18 || type.getId() == 75) {
                if (selectedChequeBill.isIsCheque() && (type.getId() == 18 ||  type.getId() == 75)) {
                    typeList.add(type);
                }
                if (!selectedChequeBill.isIsCheque()) {
                    typeList.add(type);
                }

            }
        }
    }

    public void changeType() {
        selectedObject.setPrice(BigDecimal.ZERO);
        selectedObject.setCurrency(new Currency());
        selectedObject.setSafe(new Safe());
        selectedObject.setBankAccount(new BankAccount());
        selectedObject.setExchangeRate(BigDecimal.ZERO);
        selectedObject.getFinancingDocument().setDescription(" ");
        selectedObject.getFinancingDocument().setDocumentNumber(" ");
        inCurrency = null;
        outCurrency = null;

        switch (selectedObject.getType().getId()) {
            case 17: //nakit 
                if (!safeList.isEmpty()) {
                    outCurrency = safeList.get(0).getCurrency();
                }
                break;
            case 18:// kredi kartı  
                bankAccountList = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedChequeBill.getBranch());
                if (!bankAccountList.isEmpty()) {
                    outCurrency = bankAccountList.get(0).getCurrency();
                }
                break;
            case 75:// eft/havale 
                bankAccountList = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", selectedChequeBill.getBranch());
                if (!bankAccountList.isEmpty()) {
                    outCurrency = bankAccountList.get(0).getCurrency();
                }
                break;
            default:
                break;
        }
        selectedObject.getFinancingDocument().setDescription(sessionBean.getLoc().getString("portfoliono") + " : " + selectedChequeBill.getPortfolioNumber() + " - " + (selectedChequeBill.isIsCheque() ? (selectedChequeBill.isIsCustomer() ? sessionBean.getLoc().getString("chequerecovery") : sessionBean.getLoc().getString("chequepayment")) : (selectedChequeBill.isIsCustomer() ? sessionBean.getLoc().getString("billrecovery") : sessionBean.getLoc().getString("billpayment"))));
        selectedObject.setPrice(selectedChequeBill.getRemainingMoney());
        exchange = "";
        bringCurrency();
    }

    public void bringCurrency() {
        inCurrency = sessionBean.getUser().getLastBranch().getCurrency();
        switch (selectedObject.getType().getId()) {
            case 17: //nakit 
                for (Safe safeObj : safeList) {
                    if (selectedObject.getSafe().getId() == safeObj.getId()) {
                        outCurrency = safeObj.getCurrency();
                        break;
                    }
                }
                break;
            case 18:// kredi kartı
                for (BankAccount bankAccountObj : bankAccountList) {
                    if (bankAccountObj.getId() == selectedObject.getBankAccount().getId()) {
                        outCurrency = bankAccountObj.getCurrency();
                        break;
                    }
                }
                break;
            case 75:// eft/havale
                for (BankAccount bankAccountObj : bankAccountList) {
                    if (bankAccountObj.getId() == selectedObject.getBankAccount().getId()) {
                        outCurrency = bankAccountObj.getCurrency();
                        break;
                    }
                }
                break;
            default:
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

    public void bringType() {
        for (Type type : typeList) {
            if (selectedObject.getType().getId() == type.getId()) {
                selectedObject.getType().setTag(type.getNameMap().get(sessionBean.getLangId()).getName());

                break;
            }
        }
    }

    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedChequeBill.getExpiryDate())) {
            RequestContext context = RequestContext.getCurrentInstance();
            selectedObject.setChequeBill(selectedChequeBill);
            BigDecimal price = BigDecimal.ZERO;
            price = selectedObject.getPrice().multiply(selectedObject.getExchangeRate());
            boolean isThere = false;

            if (processType == 2) {
                price = price.subtract(oldValue);
            }

            if (selectedObject.getPrice().multiply(selectedObject.getExchangeRate()).compareTo(BigDecimal.ZERO) != 1) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else if (price.compareTo(selectedObject.getChequeBill().getRemainingMoney()) > 0) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theamounttobepaidcannotexceedtheremainingamount")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

            if (!isThere) {
                int result = 0;
                if (processType == 1) {
                    result = chequeBillPaymentService.create(selectedObject);

                    if (result > 0) {
                        selectedObject.setId(result);
                        bringType();
                        listOfObjects.add(selectedObject);
                    }
                } else if (processType == 2) {
                    result = chequeBillPaymentService.update(selectedObject);
                    if (result > 0) {
                        bringType();
                    }
                }
                if (result > 0) {
                    selectedChequeBill.setRemainingMoney(selectedChequeBill.getRemainingMoney().subtract(price));
                    if (selectedChequeBill.getRemainingMoney().compareTo(BigDecimal.ZERO) == 0) {
                        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                        ChequeBillProcessBean chequeBillProcessBean = (ChequeBillProcessBean) viewMap.get("chequeBillProcessBean");
                        chequeBillProcessBean.getSelectedObject().getStatus().setId(38);
                        chequeBillProcessBean.setIsDisabled(true);
                        context.update("frmChequeBillProcess:pgrChequeBillProcess");
                    } else if (selectedChequeBill.getRemainingMoney().compareTo(BigDecimal.ZERO) == 1 && processType == 2) {
                        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                        ChequeBillProcessBean chequeBillProcessBean = (ChequeBillProcessBean) viewMap.get("chequeBillProcessBean");
                        chequeBillProcessBean.getSelectedObject().getStatus().setId(31);
                        chequeBillProcessBean.setIsDisabled(false);
                        context.update("frmChequeBillProcess:pgrChequeBillProcess");
                    }

                    context.update("tbvChequeBill:frmChequeBillPaymentTab");
                    context.execute("PF('dlg_ChequeBillPaymentProcess').hide();");

                } else if (result == -101) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    sessionBean.createUpdateMessage(result);
                }

            }
        }
    }

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate()) && sessionBean.isPeriodClosed(selectedChequeBill.getExpiryDate())) {
            controlDeleteList.clear();
            controlDeleteList = chequeBillPaymentService.testBeforeDelete(selectedObject);
            if (!controlDeleteList.isEmpty()) {
                if (controlDeleteList.get(0).getR_response() < 0) {
                    if (controlDeleteList.get(0).getR_response() == -100) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {//Sil
                    RequestContext.getCurrentInstance().update("frmChequeBillPaymentProcess:dlgDelete");
                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                }
            }
        }
    }

    public void delete() {

        RequestContext context = RequestContext.getCurrentInstance();
        BigDecimal price = BigDecimal.ZERO;
        price = selectedObject.getPrice().multiply(selectedObject.getExchangeRate());
        int result = chequeBillPaymentService.delete(selectedObject);
        if (result > 0) {

            selectedChequeBill.setRemainingMoney(selectedChequeBill.getRemainingMoney().add(price));
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            ChequeBillProcessBean chequeBillProcessBean = (ChequeBillProcessBean) viewMap.get("chequeBillProcessBean");
            if (selectedChequeBill.getRemainingMoney().compareTo(BigDecimal.ZERO) > 0 && chequeBillProcessBean.isIsDisabled()) {

                chequeBillProcessBean.getSelectedObject().getStatus().setId(31);
                chequeBillProcessBean.setIsDisabled(false);
                context.update("frmChequeBillProcess:pgrChequeBillProcess");
            }
            listOfObjects.remove(selectedObject);
            context.update("tbvChequeBill:frmChequeBillPaymentTab");
            context.execute("PF('dlg_ChequeBillPaymentProcess').hide();");
        }

        sessionBean.createUpdateMessage(result);
    }

}
