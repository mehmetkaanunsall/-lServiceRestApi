/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class ChequeBillProcessBean extends AuthenticationLists {

    private ChequeBill selectedObject;
    private int processType;
    private List<DocumentNumber> listOfDocumentNumber;
    private List<BankBranch> listOfBankBranch;
    private int activeIndex;
    private String exchange;
    private boolean isDisabled;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private boolean isDeleteButton;
    private List<Branch> listOfBranch;
    private List<BankAccount> bankAccountList;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;

    @ManagedProperty(value = "#{bankBranchService}")
    public IBankBranchService bankBranchService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public ChequeBill getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(ChequeBill selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
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

    public List<BankBranch> getListOfBankBranch() {
        return listOfBankBranch;
    }

    public void setListOfBankBranch(List<BankBranch> listOfBankBranch) {
        this.listOfBankBranch = listOfBankBranch;
    }

    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public boolean isIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
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

    public List<CheckDelete> getControlDeleteList() {
        return controlDeleteList;
    }

    public void setControlDeleteList(List<CheckDelete> controlDeleteList) {
        this.controlDeleteList = controlDeleteList;
    }

    public int getRelatedRecordId() {
        return relatedRecordId;
    }

    public void setRelatedRecordId(int relatedRecordId) {
        this.relatedRecordId = relatedRecordId;
    }

    public boolean isIsDeleteButton() {
        return isDeleteButton;
    }

    public void setIsDeleteButton(boolean isDeleteButton) {
        this.isDeleteButton = isDeleteButton;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
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

    public List<BankAccount> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccount> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostConstruct
    public void init() {

        System.out.println("------------ChequeBillProcessBean------------");
        bankAccountList = new ArrayList<>();

        controlDeleteList = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof ChequeBill) {
                    processType = 2;
                    selectedObject = (ChequeBill) ((ArrayList) sessionBean.parameter).get(i);
                    citiesAndCountiesBean.updateCityAndCounty(selectedObject.getCountry(), selectedObject.getPaymentCity());
                    listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(18), selectedObject.getBranch());//çek senet için seri numarları çektik.
                    listOfBankBranch = bankBranchService.selectBankBranch();
                    listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker
                    if (selectedObject.getCollectingBankAccount().getId() > 0) {
                        bankAccountList = bankAccountService.bankAccountForSelect(" ", selectedObject.getBranch());
                    }
                    if (selectedObject.getStatus().getId() == 38) {
                        isDisabled = true;
                    }
                    break;
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{26, 27}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{8}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }
    }

    //cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObject.setEndorsedAccount(accountBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmChequeBillProcess:txtEndorsedAccountName");
        }
    }

    /**
     * bu metot para birimi değiştiğinde tetiklenir.
     */
    public void changeExchange() {
        selectedObject.setExchangeRate(exchangeService.bringExchangeRate(selectedObject.getCurrency(), sessionBean.getUser().getLastBranch().getCurrency(), sessionBean.getUser()));
        exchange = sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0) + " -> " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);// örn: $->€

    }

    public void bringDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getDocumentNumber().getId()) {
                selectedObject.getDocumentNumber().setActualNumber(dn.getActualNumber());
                selectedObject.getDocumentNumber().setSerial(dn.getSerial());
                selectedObject.setDocumentSerial(dn.getSerial());
                selectedObject.setDocumentNo("" + dn.getActualNumber());
            }
        }
    }

    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getExpiryDate())) {

            int result = 0;
            if (processType == 1) {
                result = chequeBillService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                    List<Object> list = new ArrayList<>();
                    list.addAll((ArrayList) sessionBean.parameter);
                    list.add(selectedObject);
                    marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 1, 81);
                    sessionBean.createUpdateMessage(result);
                }

            } else if (processType == 2) {
                result = chequeBillService.update(selectedObject);
                if (result == -101) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("youcannotchoosethisstatusbecauseitisapayment")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else if (result == -102) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("youcannotchoosethisstatuswithoutpayment")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else if (result > 0) {
                    ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                    List<Object> list = new ArrayList<>();
                    list.addAll((ArrayList) sessionBean.parameter);
                    marwiz.goToPage("/pages/finance/chequebill/chequebill.xhtml", list, 1, 80);
                    sessionBean.createUpdateMessage(result);
                }
            }
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getExpiryDate())) {

            deleteControlMessage = "";
            deleteControlMessage1 = "";
            deleteControlMessage2 = "";
            relatedRecord = "";
            controlDeleteList.clear();
            controlDeleteList = chequeBillService.testBeforeDelete(selectedObject);
            if (!controlDeleteList.isEmpty()) {
                if (controlDeleteList.get(0).getR_response() < 0) { //Var bağlı ise silme uyarı ver
                    isDeleteButton = false;
                    switch (controlDeleteList.get(0).getR_response()) {
                        case -100: //fiş satışına bağlı. satışı iade etmek gerek
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosale");
                            deleteControlMessage1 = sessionBean.getLoc().getString("todeletetherecordyouneedtoreturnthereceipt");
                            deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : ";
                            relatedRecord = controlDeleteList.get(0).getR_recordno();
                            relatedRecordId = controlDeleteList.get(0).getR_record_id();
                            break;
                        case -101: //faturaya bağlı statüsü kapalı açığa çekilmesi lazım
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("todeletedtherecordpleaseopentheinvoicestatusordeletetheinvoice");
                            deleteControlMessage2 = sessionBean.getLoc().getString("invoiceno") + " : ";
                            relatedRecord = controlDeleteList.get(0).getR_recordno();
                            relatedRecordId = controlDeleteList.get(0).getR_record_id();
                            break;
                        case -102: //faturaya bağlı statüsü iptal silinemez
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("itcannotbedeletedbecauserelatedinvoicecanceled");
                            deleteControlMessage2 = "";
                            break;
                        default:
                            break;
                    }
                    if (controlDeleteList.get(0).getR_response() != -103) {
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                    } else if (controlDeleteList.get(0).getR_response() == -103) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {//Sil
                    deleteControlMessage = sessionBean.getLoc().getString("chequebilldelete");
                    deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                    deleteControlMessage2 = "";
                    isDeleteButton = true;
                    RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                    RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                }
            }
        }

    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedObject.getExpiryDate())) {
            int result = 0;
            result = chequeBillService.delete(selectedObject);
            if (result > 0) {
                List<Object> list = new ArrayList<>();
                list.addAll((ArrayList) sessionBean.parameter);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof ChequeBill) {
                        list.remove(list.get(i));
                    }
                }
                marwiz.goToPage("/pages/finance/chequebill/chequebill.xhtml", list, 1, 80);
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public void goToRelatedRecordBefore() {

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("rcgoToRelatedRecord()");

    }

    public void goToRelatedRecord() {

        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        if (controlDeleteList.get(0).getR_response() == -100) {  //fiş satışına bağlı. satışı iade etmek gerek
            marwiz.goToPage("/pages/finance/salereturn/salereturn.xhtml", list, 1, 82);
        } else if (controlDeleteList.get(0).getR_response() == -101) { //faturaya bağlı statüsü kapalı açığa çekilmesi lazım

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Invoice) {
                    list.remove(i);
                }
            }
            Invoice invoice = new Invoice();
            invoice.setId(relatedRecordId);
            invoice = invoiceService.findInvoice(invoice);
            list.add(invoice);
            marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
        }
    }

    public void changeStatus() {
        selectedObject.getCollectingBankAccount().setId(0);
        selectedObject.getEndorsedAccount().setId(0);
        if (processType == 2 && (selectedObject.getStatus().getId() == 33 || selectedObject.getStatus().getId() == 34)) {
            bankAccountList = bankAccountService.bankAccountForSelect(" ", selectedObject.getBranch());
        }
    }

}
