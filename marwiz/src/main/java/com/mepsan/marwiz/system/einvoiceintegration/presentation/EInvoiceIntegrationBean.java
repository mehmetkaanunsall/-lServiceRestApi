/**
 *
 * @author elif.mart
 */
package com.mepsan.marwiz.system.einvoiceintegration.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.log.SendEInvoice;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.system.einvoiceintegration.business.EInvoiceIntegrationService;
import com.mepsan.marwiz.system.einvoiceintegration.business.IEInvoiceIntegrationService;
import com.mepsan.marwiz.system.einvoiceintegration.business.IIncomingEInvoicesService;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.jmx.export.annotation.ManagedAttribute;

@ManagedBean
@ViewScoped
public class EInvoiceIntegrationBean extends GeneralReportBean<EInvoice> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{eInvoiceIntegrationService}")
    private IEInvoiceIntegrationService eInvoiceIntegrationService;

    @ManagedProperty(value = "#{incomingEInvoicesService}")
    private IIncomingEInvoicesService incomingEInvoicesService;

    @ManagedProperty(value = "#{incomingEInvoicesBean}")
    private IncomingEInvoicesBean incomingEInvoicesBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    private Date beginDate, endDate;
    private int processType;//1:E-Fatura Gönderimi 2:E-Arşiv Gönderimi
    private int operationType;//1:Gelen E-Fatura 2:Giden E-Fatura
    private boolean isFind;
    private String isSend;
    private List<EInvoice> listOfObject;
    private List<EInvoice> listOfSelectedObjects;
    private String createWhere;
    private boolean isIncomingEInvoices;
    private boolean isApproval;
    private BranchSetting brSetting;
    private int invoiceSourceType;
    private String invoiceNo;
    private List<Account> accountList;
    private int archiveOperationType;
    private EInvoice selectedInvoice;
    private int dateFilterType;

    public boolean isIsApproval() {
        return isApproval;
    }

    public void setIsApproval(boolean isApproval) {
        this.isApproval = isApproval;
    }

    public IncomingEInvoicesBean getIncomingEInvoicesBean() {
        return incomingEInvoicesBean;
    }

    public void setIncomingEInvoicesBean(IncomingEInvoicesBean incomingEInvoicesBean) {
        this.incomingEInvoicesBean = incomingEInvoicesBean;
    }

    public IIncomingEInvoicesService getIncomingEInvoicesService() {
        return incomingEInvoicesService;
    }

    public void setIncomingEInvoicesService(IIncomingEInvoicesService incomingEInvoicesService) {
        this.incomingEInvoicesService = incomingEInvoicesService;
    }

    public boolean isIsIncomingEInvoices() {
        return isIncomingEInvoices;
    }

    public void setIsIncomingEInvoices(boolean isIncomingEInvoices) {
        this.isIncomingEInvoices = isIncomingEInvoices;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public void seteInvoiceIntegrationService(IEInvoiceIntegrationService eInvoiceIntegrationService) {
        this.eInvoiceIntegrationService = eInvoiceIntegrationService;
    }

    public List<EInvoice> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<EInvoice> listOfObject) {
        this.listOfObject = listOfObject;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
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

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public List<EInvoice> getListOfSelectedObjects() {
        return listOfSelectedObjects;
    }

    public void setListOfSelectedObjects(List<EInvoice> listOfSelectedObjects) {
        this.listOfSelectedObjects = listOfSelectedObjects;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public BranchSetting getBrSetting() {
        return brSetting;
    }

    public void setBrSetting(BranchSetting brSetting) {
        this.brSetting = brSetting;
    }

    public int getInvoiceSourceType() {
        return invoiceSourceType;
    }

    public void setInvoiceSourceType(int invoiceSourceType) {
        this.invoiceSourceType = invoiceSourceType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public AccountBookCheckboxFilterBean getAccountBookCheckboxFilterBean() {
        return accountBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public int getArchiveOperationType() {
        return archiveOperationType;
    }

    public void setArchiveOperationType(int archiveOperationType) {
        this.archiveOperationType = archiveOperationType;
    }

    public EInvoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(EInvoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public int getDateFilterType() {
        return dateFilterType;
    }

    public void setDateFilterType(int dateFilterType) {
        this.dateFilterType = dateFilterType;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------EInvoiceIntegrationBean---------");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        beginDate = cal.getTime();

        listOfSelectedObjects = new ArrayList<>();
        isIncomingEInvoices = false;
        processType = 1;
        operationType = 1;
        invoiceSourceType = 1;
        archiveOperationType = 1;
        dateFilterType = 1;
        isSend = "2";
        isApproval = true;
        brSetting = new BranchSetting();
        brSetting = eInvoiceIntegrationService.bringBranchAdress();
        invoiceNo = "";
        accountList = new ArrayList<>();
        invoiceNo = "";
        selectedInvoice = new EInvoice();
        selectedObject = new EInvoice();
    }

    @Override
    public void find() {

        isFind = true;
        listOfSelectedObjects.clear();
        if (operationType == 2 || (operationType == 3 && archiveOperationType == 2)) {
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");
            if (dataTable != null) {
                dataTable.setFirst(0);
            }
            int send = Integer.parseInt(isSend);
            createWhere = eInvoiceIntegrationService.createWhere(beginDate, endDate, send, processType, accountList, invoiceNo);

            listOfObject = eInvoiceIntegrationService.listOfEInvoices(createWhere, operationType);
            if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

                for (int i = 0; i < listOfObject.size(); i++) {
                    if (listOfObject.get(i).getSendEInvoice().getResponseDescription() != null) {
                        if (listOfObject.get(i).getSendEInvoice().getResponseDescription().equalsIgnoreCase("Lütfen fatura alıcısını en az iki karakter olarak belirtiniz.") || listOfObject.get(i).getSendEInvoice().getResponseDescription().equalsIgnoreCase("Lütfen fatura alıcısını en az iki karakter olarak belirtiniz..EFATURASRV")) {
                            listOfObject.get(i).getSendEInvoice().setResponseDescription(sessionBean.loc.getString("ifthecurrenttypeisindividualpleaseentertheelevendigittcidnumberandifthecurrenttypeiscorporatepleaseenterthetendigittaxidentificationnumber"));

                        } else if (listOfObject.get(i).getSendEInvoice().getResponseDescription().equalsIgnoreCase("e-fatura tipinde olan faturaların PROFILEID alanı EARSIV olamaz.") || listOfObject.get(i).getSendEInvoice().getResponseDescription().equalsIgnoreCase("e-fatura tipinde olan faturaların PROFILEID alanı EARSIV olamaz..EFATURASRV")) {
                            listOfObject.get(i).getSendEInvoice().setResponseDescription(sessionBean.loc.getString("currenttypeandinvoicetypedonotmatchsincetheeinvoiceisthetaxpayerinvoicetypeshouldbeeinvoice"));

                        } else if (listOfObject.get(i).getSendEInvoice().getResponseDescription().equalsIgnoreCase("Verilen alias sistem kullanıcıları listesinde bulunmuyor..EFATURASRV")) {
                            listOfObject.get(i).getSendEInvoice().setResponseDescription(sessionBean.loc.getString("aliasisnotincludedinthelistofsystemuserspleasechecktheinvoicealiasinformation"));
                        }
                    }
                }
            }

            RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");

        } else {
            incomingInvoices();
        }
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Bu metot seçilen listeyi e-fatura web servisine gönderir
     */
    public void sendIntegration() {
        String partyIdentificationScheme = "";
        boolean isPerson = false;
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            if (processType == 1) {
                eInvoiceIntegrationService.sendEInvoice(listOfSelectedObjects, brSetting);
            } else {
                eInvoiceIntegrationService.sendEArchive(listOfSelectedObjects, brSetting);
            }
            listOfSelectedObjects.clear();
            find();//tabloyu güncelle

        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

            if (processType == 1) {

                for (int i = 0; i < listOfSelectedObjects.size(); i++) {
                    partyIdentificationScheme = listOfSelectedObjects.get(i).getAccount().getTaxNo().length() == 10 ? "VKN" : listOfSelectedObjects.get(i).getAccount().getTaxNo().length() == 11 ? "TCKN" : "VKN";
                    if ((listOfSelectedObjects.get(i).getAccount().getIsPerson() && partyIdentificationScheme.equalsIgnoreCase("VKN")) || (!listOfSelectedObjects.get(i).getAccount().getIsPerson() && partyIdentificationScheme.equalsIgnoreCase("TCKN"))) {
                        isPerson = true;
                    }
                }
                if (isPerson) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sessionBean.loc.getString("ifthecurrenttypeisindividualpleaseentertheelevendigittcidnumberandifthecurrenttypeiscorporatepleaseenterthetendigittaxidentificationnumber")));
                    RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");
                } else {
                    listOfSelectedObjects = selectedInvoiceStatusInquiry(listOfSelectedObjects);
                    if (listOfSelectedObjects.size() > 0) {
                        eInvoiceIntegrationService.sendUEInvoice(listOfSelectedObjects, brSetting);
                    }

                }
            } else {
                listOfSelectedObjects = selectedInvoiceStatusInquiry(listOfSelectedObjects);
                if (listOfSelectedObjects.size() > 0) {
                    eInvoiceIntegrationService.sendUEArchive(listOfSelectedObjects, brSetting);
                }

            }
            listOfSelectedObjects.clear();
            find();//tabloyu güncelle
        }

    }

    // Listedeki seçilen faturaların gönderim durumunu sorgular, eğer daha önce gönderilmiş ve başarıyla GİB'e iletilmiş olan faturalar varsa listeden çıkarır ve tablodaki seçimleri günceller.
    public List<EInvoice> selectedInvoiceStatusInquiry(List<EInvoice> list) {

        List<EInvoice> tempList = new ArrayList<>();

        for (EInvoice ei : listOfSelectedObjects) {
            tempList.add(ei);
        }

        if (!list.isEmpty()) {
            List<SendEInvoice> listSendEInvoice = new ArrayList<>();
            for (EInvoice ei : list) {
                if (ei.getSendEInvoice().getIntegrationInvoice() != null) {
                    listSendEInvoice.add(ei.getSendEInvoice());
                }
            }
            eInvoiceIntegrationService.uInvoiceStatusInquiry(listSendEInvoice);
            find();
            listOfSelectedObjects.clear();
            for (EInvoice ei : listOfObject) {

                for (EInvoice einv : tempList) {
                    if (einv.getDocumentNumber().equalsIgnoreCase(ei.getDocumentNumber()) && !einv.getSendEInvoice().isIsSend()) {

                        listOfSelectedObjects.add(ei);

                    }
                }

            }

            RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");

            if (tempList.size() != listOfSelectedObjects.size()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("someoftheselectedeinvoiceswerenotsentagainbecausetheywerepreviouslysenttotheıopandsuccessfullydelivered")));
                RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

            }

        }
        return listOfSelectedObjects;
    }

    //E-Faturaların gönderim durumlarını sorgular
    public void invoiceStatusInquiry() {

        List<SendEInvoice> listSendEInvoice = new ArrayList<>();
        listSendEInvoice = eInvoiceIntegrationService.listSendEInvocie();

        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            eInvoiceIntegrationService.invoiceStatusInquiry(listSendEInvoice);
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
            eInvoiceIntegrationService.uInvoiceStatusInquiry(listSendEInvoice);
        }
        find();

    }

    @Override
    public LazyDataModel<EInvoice> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void incomingInvoices() {
        isIncomingEInvoices = true;
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomingEInvoicesBean incomingEInvoicesBean = (IncomingEInvoicesBean) viewMap.get("incomingEInvoicesBean");
        incomingEInvoicesBean.findInvoices();
    }

    //Fatura gönderilmek için seçildiğinde çalışır, eğer daha önce başarıyla GİB'e iletilmş ise (isSend=true ise), seçimini kaldırır.
    public void rowSelect(SelectEvent evt) {

        if (evt != null && evt.getObject() != null
                && evt.getObject() instanceof EInvoice) {

            EInvoice ei = (EInvoice) evt.getObject();

            if (ei.getListInvoiceItem().size() == 0) {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sessionBean.loc.getString("pleaseaddproducttotheinvoice")));
                RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");
                listOfSelectedObjects.remove(ei);
                RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");

            } else {

                if (ei.getSendEInvoice().isIsSend()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sessionBean.loc.getString("sincetheeinvoicefortheselectedonehasbeensenttotherevenueadministrationbeforeandithasbeensuccessfullytransmitteditcannotbesentagain")));
                    RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");
                    listOfSelectedObjects.remove(ei);
                    RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");
                }
            }

        }

    }

    public void openDialog() {

        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
        if (!accountList.isEmpty()) {
            if (accountList.get(0).getId() == 0) {
                accountBookCheckboxFilterBean.isAll = true;
            } else {
                accountBookCheckboxFilterBean.isAll = false;
            }
        }
        accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(accountList);

    }

    public void updateAllInformation(ActionEvent event) {

        accountList.clear();
        if (accountBookCheckboxFilterBean.isAll) {
            Account s = new Account(0);
            if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Account a = new Account(0);
                a.setName(sessionBean.loc.getString("all"));
                accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, a);
            }
        } else if (!accountBookCheckboxFilterBean.isAll) {
            if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }

        if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
        }
        accountList.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
        RequestContext.getCurrentInstance().update("frmEInvoiceIntegration:txtCustomer");
    }

    public void changeOperationType() {
        if (!accountList.isEmpty()) {
            accountList.clear();
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            accountBookCheckboxFilterBean.setIsAll(false);
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        }
        if (listOfObject != null && !listOfObject.isEmpty()) {
            listOfObject.clear();
        }

        if (listOfSelectedObjects != null && !listOfSelectedObjects.isEmpty()) {
            listOfSelectedObjects.clear();
        }

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomingEInvoicesBean incomingEInvoicesBean = (IncomingEInvoicesBean) viewMap.get("incomingEInvoicesBean");
        incomingEInvoicesBean.changeInvoiceSource();

    }

    public void addArchive() {

        if (!listOfSelectedObjects.isEmpty()) {
            int result = 0;
            int result1 = 0;
            String ids = "";
            List<EInvoice> listInsertLogRecords = new ArrayList<>();
            List<EInvoice> listUpdateLogRecords = new ArrayList<>();

            for (EInvoice obj : listOfSelectedObjects) {
                if (obj.getSendEInvoice().getId() > 0) {
                    listUpdateLogRecords.add(obj);
                } else {
                    listInsertLogRecords.add(obj);
                }
            }
            if (!listUpdateLogRecords.isEmpty()) {
                for (EInvoice obj : listUpdateLogRecords) {
                    ids = ids + "," + obj.getSendEInvoice().getId();
                }
                if (!ids.isEmpty()) {
                    ids = ids.substring(1, ids.length());
                    result = eInvoiceIntegrationService.updateArchive(ids, 1);
                    if (result > 0) {

                        for (EInvoice ei : listUpdateLogRecords) {
                            for (Iterator<EInvoice> iterator = listOfObject.iterator(); iterator.hasNext();) {
                                EInvoice next = iterator.next();
                                if (next.getId() == ei.getId()) {
                                    iterator.remove();

                                }
                            }

                        }
                        // listOfSelectedObjects.clear();

                    }

                }

                //   sessionBean.createUpdateMessage(result);
            }

            if (!listInsertLogRecords.isEmpty()) {
                for (EInvoice obj : listInsertLogRecords) {
                    obj.getSendEInvoice().setSendBeginDate(null);
                    obj.getSendEInvoice().setSendEndDate(null);
                    obj.getSendEInvoice().setResponseCode(null);
                    obj.getSendEInvoice().setSendData(null);
                    obj.getSendEInvoice().setResponseDescription(null);
                    obj.getSendEInvoice().setIntegrationInvoice(null);
                    obj.getSendEInvoice().setGibInvoice(null);
                    obj.getSendEInvoice().setResponseDescription(null);
                    obj.getSendEInvoice().setIsSend(false);
                }

                result1 = eInvoiceIntegrationService.createLogForArchive(listInsertLogRecords);

            }
            if (result > 0 || result1 > 0) {

                for (EInvoice ei : listInsertLogRecords) {
                    for (Iterator<EInvoice> iterator = listOfObject.iterator(); iterator.hasNext();) {
                        EInvoice next = iterator.next();
                        if (next.getId() == ei.getId()) {
                            iterator.remove();

                        }
                    }

                }

            }

            listOfSelectedObjects.clear();
            RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");
            sessionBean.createUpdateMessage(result);

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), "  " + sessionBean.loc.getString("pleaseselectinvoicestobearchived")));
            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

        }

    }

    public void addArchiveRecord() {
        int result = 0;
        String ids = "";
        ids = Integer.toString(selectedObject.getSendEInvoice().getId());
        result = eInvoiceIntegrationService.updateArchive(ids, 1);

        if (result > 0) {

            for (Iterator<EInvoice> iterator = listOfObject.iterator(); iterator.hasNext();) {
                EInvoice next = iterator.next();
                if (next.getId() == selectedObject.getId()) {
                    iterator.remove();
                }
            }

            // listOfSelectedObjects.clear();
            RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void unArchive() {

        int result = 0;
        String ids = "";

        if (!listOfSelectedObjects.isEmpty()) {
            for (EInvoice obj : listOfSelectedObjects) {
                ids = ids + "," + obj.getSendEInvoice().getId();
            }
            if (!ids.isEmpty()) {
                ids = ids.substring(1, ids.length());
                result = eInvoiceIntegrationService.updateArchive(ids, 0);
                if (result > 0) {

                    for (EInvoice ei : listOfSelectedObjects) {
                        for (Iterator<EInvoice> iterator = listOfObject.iterator(); iterator.hasNext();) {
                            EInvoice next = iterator.next();
                            if (next.getId() == ei.getId()) {
                                iterator.remove();

                            }
                        }

                    }
                    listOfSelectedObjects.clear();

                }

            }
            RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");
            sessionBean.createUpdateMessage(result);
            //   sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), "  " + sessionBean.loc.getString("pleaseselecttheinvoicestoberemovedfromthearchive")));
            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");
        }

    }

    public void unArchiveRecord() {

        int result = 0;
        String ids = "";
        ids = Integer.toString(selectedInvoice.getSendEInvoice().getId());
        result = eInvoiceIntegrationService.updateArchive(ids, 0);

        if (result > 0) {

            for (Iterator<EInvoice> iterator = listOfObject.iterator(); iterator.hasNext();) {
                EInvoice next = iterator.next();
                if (next.getId() == selectedInvoice.getId()) {
                    iterator.remove();
                }
            }
            selectedInvoice = new EInvoice();
            listOfSelectedObjects.clear();
            RequestContext.getCurrentInstance().update("frmEInvoiceIntegrationDatatable:dtbEInvoiceIntegration");
        }
        sessionBean.createUpdateMessage(result);

    }

}
