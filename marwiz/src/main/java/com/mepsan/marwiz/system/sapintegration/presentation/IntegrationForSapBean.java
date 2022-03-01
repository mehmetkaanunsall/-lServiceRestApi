/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapintegration.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseReceiptService;
import com.mepsan.marwiz.system.sapintegration.business.IIntegrationForSapService;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSap;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSapResponseDetail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class IntegrationForSapBean extends GeneralDefinitionBean<IntegrationForSap> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{integrationForSapService}")
    private IIntegrationForSapService integrationForSapService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService waybillService;

    @ManagedProperty(value = "#{warehouseReceiptService}")
    public IWarehouseReceiptService warehouseReceiptService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Date beginDate, endDate, maxDate;
    private int processType;
    private int invoiceType;
    private boolean isFind;
    private boolean isSend;
    private List<IntegrationForSap> listOfSap, listOfSelectedSap;
    private List<BranchSetting> listOfBranch;
    private BranchSetting selectedBranch;
    private boolean isRetail;
    private int purchaseInvoiceType;
    private boolean reverseposting;
    private IntegratinForSapParam integratinForSapParam;
    private IntegrationForSap selectedRetail;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
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

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public List<IntegrationForSap> getListOfSap() {
        return listOfSap;
    }

    public void setListOfSap(List<IntegrationForSap> listOfSap) {
        this.listOfSap = listOfSap;
    }

    public List<IntegrationForSap> getListOfSelectedSap() {
        return listOfSelectedSap;
    }

    public void setListOfSelectedSap(List<IntegrationForSap> listOfSelectedSap) {
        this.listOfSelectedSap = listOfSelectedSap;
    }

    public IIntegrationForSapService getIntegrationForSapService() {
        return integrationForSapService;
    }

    public void setIntegrationForSapService(IIntegrationForSapService integrationForSapService) {
        this.integrationForSapService = integrationForSapService;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public BranchSetting getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(BranchSetting selectedBranch) {
        this.selectedBranch = selectedBranch;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public boolean isIsRetail() {
        return isRetail;
    }

    public void setIsRetail(boolean isRetail) {
        this.isRetail = isRetail;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public int getPurchaseInvoiceType() {
        return purchaseInvoiceType;
    }

    public void setPurchaseInvoiceType(int purchaseInvoiceType) {
        this.purchaseInvoiceType = purchaseInvoiceType;
    }

    public boolean isReverseposting() {
        return reverseposting;
    }

    public void setReverseposting(boolean reverseposting) {
        this.reverseposting = reverseposting;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public IWaybillService getWaybillService() {
        return waybillService;
    }

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public IWarehouseReceiptService getWarehouseReceiptService() {
        return warehouseReceiptService;
    }

    public void setWarehouseReceiptService(IWarehouseReceiptService warehouseReceiptService) {
        this.warehouseReceiptService = warehouseReceiptService;
    }

    public IntegrationForSap getSelectedRetail() {
        return selectedRetail;
    }

    public void setSelectedRetail(IntegrationForSap selectedRetail) {
        this.selectedRetail = selectedRetail;
    }

    //Sayfada tutulan filtreleme parametreleri için class oluşturuldu
    public class IntegratinForSapParam {

        private int procecessType;
        private int invoiceType;
        private BranchSetting selectedBranch;
        private Date beginDate;
        private Date endDate;
        private boolean isSend;
        private boolean reverseposting;
        private boolean isFind;
        private List<IntegrationForSap> listOfObjects;
        private int purchaseInvoiceType;

        public IntegratinForSapParam() {
            selectedBranch = new BranchSetting();
            listOfObjects = new ArrayList<>();
        }

        public int getProcecessType() {
            return procecessType;
        }

        public void setProcecessType(int procecessType) {
            this.procecessType = procecessType;
        }

        public int getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(int invoiceType) {
            this.invoiceType = invoiceType;
        }

        public BranchSetting getSelectedBranch() {
            return selectedBranch;
        }

        public void setSelectedBranch(BranchSetting selectedBranch) {
            this.selectedBranch = selectedBranch;
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

        public boolean isIsSend() {
            return isSend;
        }

        public void setIsSend(boolean isSend) {
            this.isSend = isSend;
        }

        public boolean isReverseposting() {
            return reverseposting;
        }

        public void setReverseposting(boolean reverseposting) {
            this.reverseposting = reverseposting;
        }

        public boolean isIsFind() {
            return isFind;
        }

        public void setIsFind(boolean isFind) {
            this.isFind = isFind;
        }

        public List<IntegrationForSap> getListOfObjects() {
            return listOfObjects;
        }

        public void setListOfObjects(List<IntegrationForSap> listOfObjects) {
            this.listOfObjects = listOfObjects;
        }

        public int getPurchaseInvoiceType() {
            return purchaseInvoiceType;
        }

        public void setPurchaseInvoiceType(int purchaseInvoiceType) {
            this.purchaseInvoiceType = purchaseInvoiceType;
        }

    }

    public IntegratinForSapParam getIntegratinForSapParam() {
        return integratinForSapParam;
    }

    public void setIntegratinForSapParam(IntegratinForSapParam integratinForSapParam) {
        this.integratinForSapParam = integratinForSapParam;
    }

    @Override
    @PostConstruct
    public void init() {

        System.out.println("-----------IntegrationForSapBean------------");
        listOfSap = new ArrayList<>();
        listOfSelectedSap = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranch = new BranchSetting();
        listOfObjects = new ArrayList<>();
        integratinForSapParam = new IntegratinForSapParam();
        selectedRetail = new IntegrationForSap();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();
        maxDate = cal.getTime();

        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        beginDate = cal.getTime();

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true);
        listOfBranch = integrationForSapService.findBranch(); // kullanıcının yetkili olduğu branch listesini çeker

        for (BranchSetting brSetting : listOfBranch) {
            if (brSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedBranch.getBranch().setId(brSetting.getBranch().getId());
                selectedBranch.getBranch().setName(brSetting.getBranch().getName());
                selectedBranch.setErpUrl(brSetting.getErpUrl());
                selectedBranch.setErpUsername(brSetting.getErpUsername());
                selectedBranch.setErpPassword(brSetting.getErpPassword());
                selectedBranch.setErpTimeout(brSetting.getErpTimeout());
                selectedBranch.setErpEntegrationCode(brSetting.getErpEntegrationCode());
                selectedBranch.setErpIntegrationId(brSetting.getErpIntegrationId());
                selectedBranch.setIsCentralIntegration(brSetting.isIsCentralIntegration());
            }

        }
        processType = 1;
        invoiceType = 1;
        isSend = false;
        reverseposting = false;

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof IntegratinForSapParam) {
                    processType = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getProcecessType();
                    invoiceType = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getInvoiceType();
                    selectedBranch = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getSelectedBranch();
                    beginDate = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getBeginDate();
                    endDate = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getEndDate();
                    isSend = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).isIsSend();
                    reverseposting = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).isReverseposting();
                    isFind = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).isIsFind();
                    purchaseInvoiceType = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getPurchaseInvoiceType();
                    listOfObjects = ((IntegratinForSapParam) ((ArrayList) sessionBean.parameter).get(i)).getListOfObjects();
                    break;
                }
            }
            RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");

        }

        setListBtn(sessionBean.checkAuthority(new int[]{352}, 0));

    }

    public void find() {
        listOfObjects.clear();
        listOfSap.clear();
        isFind = true;
        findall();
        RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");
    }

    @Override
    public List<IntegrationForSap> findall() {
        if (processType == 1) { //Depo Fişleri
            listOfSap = integrationForSapService.listOfWarehouseReceipt(beginDate, endDate, isSend, selectedBranch);

        } else if (processType == 2) {//Satınalma faturası
            listOfSap = integrationForSapService.listOfPurchaseInvoices(beginDate, endDate, purchaseInvoiceType, selectedBranch);
        } else {//Satış faturası
            List<IntegrationForSap> listOfRetail = new ArrayList<>();
            if (invoiceType == 2) { //Perakende ise

                Date oldBegin = beginDate;
                Date oldEnd = endDate;
                Date oldMaxDate = maxDate;
                int days = 0;
                days = (int) ((endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24));
                for (int i = 0; i <= days; i++) {

                    Calendar c = Calendar.getInstance();
                    c.setTime(endDate);
                    c.set(Calendar.HOUR_OF_DAY, 23);
                    c.set(Calendar.MINUTE, 59);
                    c.set(Calendar.SECOND, 59);
                    endDate = c.getTime();
                    maxDate = c.getTime();

                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(c.getTime());
                    c2.set(Calendar.HOUR_OF_DAY, 0);
                    c2.set(Calendar.MINUTE, 0);
                    c2.set(Calendar.SECOND, 0);
                    beginDate = c2.getTime();

                    listOfRetail.addAll(integrationForSapService.listOfSaleInvoices(beginDate, endDate, true, selectedBranch));

                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(c.getTime());
                    c3.set(Calendar.HOUR_OF_DAY, 0);
                    c3.set(Calendar.MINUTE, 0);
                    c3.set(Calendar.SECOND, 0);
                    c3.add(Calendar.DAY_OF_MONTH, -1);
                    maxDate = c3.getTime();
                    endDate = maxDate;

                }
                beginDate = oldBegin;
                endDate = oldEnd;
                maxDate = oldMaxDate;
                if (listOfRetail.size() > 0) {
                    listOfSap.addAll(listOfRetail);
                }
            } else {
                listOfSap = integrationForSapService.listOfSaleInvoices(beginDate, endDate, false, selectedBranch);
            }
        }

        changeIsSend(listOfSap);

  

        
       
        
        
        
        return listOfSap;
    }

    public void changeIsSend(List<IntegrationForSap> listOfSap) {
        this.listOfSap = listOfSap;
        listOfObjects.clear();

        for (IntegrationForSap sap : listOfSap) {
            if (sap.isIsSend()) {
                if (isSend) {
                    listOfObjects.add(sap);
                }
            } else {
                if (!isSend) {
                    listOfObjects.add(sap);
                }
            }
        }

        if (processType == 3 && invoiceType == 1) {
            for (Iterator<IntegrationForSap> iterator = listOfObjects.iterator(); iterator.hasNext();) {
                IntegrationForSap value = iterator.next();
                if (reverseposting) {
                    if (value.getEvent() != 3) {
                        iterator.remove();
                    }
                } else {
                    if (value.getEvent() == 3) {
                        iterator.remove();
                    }

                }
            }
        }

        if (!listOfSelectedSap.isEmpty()) {
            listOfSelectedSap.clear();
        }
        autoCompleteValue = "";
        RequestContext.getCurrentInstance().execute("PF('sapIntegrationPF').filter();");
        RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");

    }

    /**
     * Bu metot seçilen listeyi sap web servisine gönderir
     */
    public void sendIntegration() {
        if (!listOfSelectedSap.isEmpty()) {
            integrationForSapService.sendDataIntegration(listOfSelectedSap, selectedBranch, processType);
            listOfSelectedSap.clear();
            RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");

            RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration:btnResponseDetail");

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nodocumenthasbeenselectedpleaseselectdocumentstosendtosap")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

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

    public void changeBranch() {
        for (BranchSetting br : listOfBranch) {
            if (selectedBranch.getBranch().getId() == br.getBranch().getId()) {
                selectedBranch = new BranchSetting();
                selectedBranch.getBranch().setName(br.getBranch().getName());
                selectedBranch.setErpUrl(br.getErpUrl());
                selectedBranch.setErpUsername(br.getErpUsername());
                selectedBranch.setErpPassword(br.getErpPassword());
                selectedBranch.setErpTimeout(br.getErpTimeout());
                selectedBranch.setErpEntegrationCode(br.getErpEntegrationCode());
                selectedBranch.setErpIntegrationId(br.getErpIntegrationId());
                selectedBranch.getBranch().setId(br.getBranch().getId());
                selectedBranch.setIsCentralIntegration(br.isIsCentralIntegration());
                break;
            }
        }

    }

    public String getFinancingType(int typeId) {

        return integrationForSapService.getFinancingType(typeId);
    }

    public void changeInvoiceType() {
        if (invoiceType == 1) {
            isRetail = false;
        } else {
            isRetail = true;
        }

        if (processType == 3 && invoiceType == 2) {

            Calendar c = Calendar.getInstance();
            c.setTime(beginDate);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            beginDate = c.getTime();

            Calendar c2 = Calendar.getInstance();
            c2.setTime(beginDate);
            c2.add(Calendar.DAY_OF_MONTH, +20);
            c2.set(Calendar.HOUR_OF_DAY, 23);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            maxDate = c2.getTime();
            endDate = maxDate;

        } else {

            Calendar c = Calendar.getInstance();
            c.setTime(beginDate);
            beginDate = c.getTime();
            maxDate = c.getTime();

            Calendar c2 = Calendar.getInstance();
            c2.setTime(c.getTime());
            c2.set(Calendar.HOUR_OF_DAY, 23);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            c2.add(Calendar.MONTH, +1);
            endDate = c2.getTime();
            maxDate = c2.getTime();
        }

        RequestContext.getCurrentInstance().update("frmSapIntegration");

    }

    public void onRowDoubleClick(final SelectEvent event) {

        IntegrationForSap obj = (IntegrationForSap) event.getObject();
        List<Object> list = new ArrayList<>();
        integratinForSapParam.setProcecessType(processType);
        integratinForSapParam.setInvoiceType(invoiceType);
        integratinForSapParam.setSelectedBranch(selectedBranch);
        integratinForSapParam.setBeginDate(beginDate);
        integratinForSapParam.setEndDate(endDate);
        integratinForSapParam.setIsSend(isSend);
        integratinForSapParam.setReverseposting(reverseposting);
        integratinForSapParam.setIsFind(isFind);
        integratinForSapParam.setListOfObjects(listOfObjects);
        integratinForSapParam.setPurchaseInvoiceType(purchaseInvoiceType);
        list.add(integratinForSapParam);

        if ((processType == 3 && invoiceType == 1) || (processType == 2 && purchaseInvoiceType == 1)) {

            if (obj.getId() > 0) {
                Invoice invoice = new Invoice();
                invoice.setId(obj.getObjectId());
                invoice = invoiceService.findInvoice(invoice);

                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);

            }
        } else if (processType == 2 && purchaseInvoiceType == 2) {

            if (obj.getId() > 0) {
                Waybill waybill = new Waybill();
                waybill.setId(obj.getObjectId());
                waybill = waybillService.findWaybill(waybill);

                list.add(waybill);
                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 1, 41);

            }
        } else if (processType == 2 && purchaseInvoiceType == 3) {
            if (obj.getId() > 0) {
                Invoice invoice = new Invoice();
                invoice.setId(obj.getObjectId());
                invoice = invoiceService.findInvoice(invoice);

                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);

            }

        } else if (processType == 1) {

            if (obj.getId() > 0) {
                WarehouseReceipt warehouseReceipt = new WarehouseReceipt();
                warehouseReceipt.setId(obj.getObjectId());
                warehouseReceipt = integrationForSapService.findWarehouseReceipt(warehouseReceipt, selectedBranch);

                list.add(warehouseReceipt);
                marwiz.goToPage("/pages/inventory/warehousereceipt/warehousereceiptprocess.xhtml", list, 1, 40);

            }

        }

    }

    //Sapye başarılı olarak gönderilmiş perakende kayıtlarını tekrar gönderebilmek için düzenlemeyi aç butonuna basılınca çalışır.
    public void openUpdate() {

        if (!listOfSelectedSap.isEmpty()) {
            int result = 0;
            result = integrationForSapService.openUpdate(listOfSelectedSap);
            if (result > 0) {

                for (IntegrationForSap sap : listOfSelectedSap) {
                    sap.setIsSend(false);
                    for (Iterator<IntegrationForSap> iterator = listOfObjects.iterator(); iterator.hasNext();) {
                        IntegrationForSap next = iterator.next();
                        if (sap.getId() == next.getId()) {
                            iterator.remove();
                        }
                    }
                }
                listOfSelectedSap.clear();
            }

            sessionBean.createUpdateMessage(result);
            RequestContext.getCurrentInstance().execute("PF('sapIntegrationPF').filter();");
            RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectatleastoneentrytoedit")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

    public void updateSendStatus() {
        if (!listOfSelectedSap.isEmpty()) {

            RequestContext.getCurrentInstance().update("dlgConfirm");
            RequestContext.getCurrentInstance().execute("PF('dlg_Confirm').show()");

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectthedocumentsyouwanttoupdatetheshippingstatus")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

    public void sendStatusUpdateYes() {

        if (!listOfSelectedSap.isEmpty()) {

            for (IntegrationForSap sap : listOfSelectedSap) {
                sap.setIsSend(true);
                for (Iterator<IntegrationForSap> iterator = listOfObjects.iterator(); iterator.hasNext();) {
                    IntegrationForSap next = iterator.next();
                    if (sap.getId() == next.getId()) {
                        iterator.remove();
                    }
                }
            }

            int result = 0;
            result = integrationForSapService.sendStatusUpdate(listOfSelectedSap, processType);
            listOfSelectedSap.clear();
            sessionBean.createUpdateMessage(result);
            RequestContext.getCurrentInstance().execute("PF('sapIntegrationPF').filter();");
            RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");

        }

    }

    public void goToResponseDetail() {


        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_responsedetail').show()");

    }

}
