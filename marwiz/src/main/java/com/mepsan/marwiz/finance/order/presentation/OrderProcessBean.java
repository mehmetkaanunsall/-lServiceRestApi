/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.presentation;

import com.mepsan.marwiz.finance.order.business.IOrderService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.log.SendOrder;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.service.order.business.ISendOrderService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class OrderProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{orderService}")
    private IOrderService orderService;
    
    @ManagedProperty(value = "#{sendOrderService}")
    public ISendOrderService  sendOrderService;
    
    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService  waybillService;

    private Order selectedObject;
    private int processType;
    private int activeIndex;
    private List<Status> listOfStatus;
    private List<DocumentNumber> listOfDocumentNumber;
    private BranchSetting branchSetting;
    private List<BranchSetting> listOfBranch;
    private BranchSetting branchSettingForSelection;
    
    private CheckDelete checkDelete;
    private String deleteControlMessage = "";
    private String deleteControlMessage1 = "";
    private String deleteControlMessage2 = "";
    private String relatedRecord = "";
    
    private boolean isSendCenter = false;

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public Order getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Order selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<Status> getListOfStatus() {
        return listOfStatus;
    }

    public void setListOfStatus(List<Status> listOfStatus) {
        this.listOfStatus = listOfStatus;
    }

    public List<DocumentNumber> getListOfDocumentNumber() {
        return listOfDocumentNumber;
    }

    public void setListOfDocumentNumber(List<DocumentNumber> listOfDocumentNumber) {
        this.listOfDocumentNumber = listOfDocumentNumber;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public BranchSetting getBranchSettingForSelection() {
        return branchSettingForSelection;
    }

    public void setBranchSettingForSelection(BranchSetting branchSettingForSelection) {
        this.branchSettingForSelection = branchSettingForSelection;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }

    public void setOrderService(IOrderService orderService) {
        this.orderService = orderService;
    }

    public boolean isIsSendCenter() {
        return isSendCenter;
    }

    public void setIsSendCenter(boolean isSendCenter) {
        this.isSendCenter = isSendCenter;
    }

    public void setSendOrderService(ISendOrderService sendOrderService) {
        this.sendOrderService = sendOrderService;
    }

    public CheckDelete getCheckDelete() {
        return checkDelete;
    }

    public void setCheckDelete(CheckDelete checkDelete) {
        this.checkDelete = checkDelete;
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

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }
    
    

    @PostConstruct
    public void init() {
        System.out.println("----------------------OrderProcessBean");
        listOfStatus = new ArrayList<>();
        listOfStatus = sessionBean.getStatus(40);
        listOfBranch = new ArrayList<>();
        branchSettingForSelection = new BranchSetting();
        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        branchSetting = sessionBean.getLastBranchSetting();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {
                    selectedObject = (Order) ((ArrayList) sessionBean.parameter).get(i);

                    if (selectedObject.getId() == 0) {//ekleme ise
                        processType = 1;
                        selectedObject.setOrderDate(new Date());
                        selectedObject.getStatus().setId(59);//default açık
                        selectedObject.getType().setId(100);

                        for (BranchSetting b : listOfBranch) {
                            if (b.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                                selectedObject.getBranchSetting().getBranch().setId(b.getBranch().getId());
                                selectedObject.getBranchSetting().getBranch().setIsAgency(b.getBranch().isIsAgency());
                                break;
                            }
                        }
                        changeBranch();

                    } else {
                        processType = 2;

                        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(40), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                        branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                        RequestContext.getCurrentInstance().update("frmOrderProcess");
                    }
                    break;
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{331, 332, 333}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{73, 77, 74}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void changeBranch() {

        selectedObject.getAccount().setId(0);
        selectedObject.getAccount().setName("");
        selectedObject.getAccount().setTitle("");
        for (BranchSetting b : listOfBranch) {
            if (b.getBranch().getId() == selectedObject.getBranchSetting().getBranch().getId()) {
                selectedObject.getBranchSetting().getBranch().setId(b.getBranch().getId());
                selectedObject.getBranchSetting().setIsCentralIntegration(b.isIsCentralIntegration());
                selectedObject.getBranchSetting().getBranch().getCurrency().setId(b.getBranch().getCurrency().getId());
                selectedObject.getBranchSetting().getBranch().setIsAgency(b.getBranch().isIsAgency());
                break;
            }
        }
        branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(40), selectedObject.getBranchSetting().getBranch());//irsaliye için seri numarları çektik.
    }

    //cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {

            selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmOrderProcess:pgrOrderProcess");

            accountBookFilterBean.setSelectedData(null);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
        RequestContext.getCurrentInstance().update("tbvOrder:frmStokTab");
    }

    public void bringDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getdNumber().getId()) {
                selectedObject.getdNumber().setActualNumber(dn.getActualNumber());
                selectedObject.getdNumber().setSerial(dn.getSerial());
                selectedObject.setDocumentSerial(dn.getSerial());
                selectedObject.setDocumentNumber("" + dn.getActualNumber());
                break;
            }
        }
    }

    /**
     *
     *
     */
    public void save() {

        int result = 0;

        if (processType == 1) {

            selectedObject.getType().setId(100);
            result = orderService.create(selectedObject);
            if (result > 0) {
                selectedObject.setUserCreated(sessionBean.getUser());
                selectedObject.setDateCreated(new Date());
                isSendCenter=true;

                selectedObject.setId(result);
                if (selectedObject.getdNumber().getId() > 0) {
                    selectedObject.setDocumentNumber("" + selectedObject.getdNumber().getActualNumber());
                }
                activeIndex = 73;
                List<Object> list = new ArrayList<>();
                list.add(selectedObject);
                marwiz.goToPage("/pages/finance/order/orderprocess.xhtml", list, 1, 229);
            }
        } else if (processType == 2) {

            result = orderService.update(selectedObject);//güncelle

            if (result > 0) {
                isSendCenter=true;

                marwiz.goToPage("/pages/finance/order/order.xhtml", null, 1, 228);

            }
        }

        sessionBean.createUpdateMessage(result);
    }

    public void askBeforeExcel() {

        //RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmStatus').show();");
        apprroveExcel();

    }

    public void apprroveExcel() {
//        selectedObject.getStatus().setId(61);
//
//        int updateStatus = orderService.updateStatus(selectedObject);
//        if (updateStatus > 0) {
//            isSendCenter=true;
//
//            List<Object> list = new ArrayList<>();
//            for (Object object : (ArrayList) sessionBean.parameter) {
//                list.add(object);
//            }
//            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i) instanceof Order) {
//                    list.remove(i);
//                }
//            }
//            list.add(selectedObject);
//
//            marwiz.goToPage("/pages/finance/order/orderprocess.xhtml", list, 1, 229);
//        }
//
//        sessionBean.createUpdateMessage(updateStatus);
        RequestContext.getCurrentInstance().execute("bringExcel();");
        
    }

    public void createExcelFile() {
   
        orderService.exportExcel(selectedObject);
    }
    
     public void testBeforeDelete() {

            deleteControlMessage = "";
            deleteControlMessage1 = "";
            deleteControlMessage2 = "";

            checkDelete = orderService.testBeforeDelete(selectedObject);

            if (checkDelete != null) {
                switch (checkDelete.getR_response()) {
                    case -101://faturaya bağlı ise silme uyarı ver
                        deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtowaybill");
                        deleteControlMessage1 = sessionBean.getLoc().getString("pleaseuyoudeletethewaybill");
                        deleteControlMessage2 = sessionBean.getLoc().getString("documentnumber");
                        relatedRecord = checkDelete.getR_recordno();
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoOrder");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoOrder').show();");
                        break;
                    default:
                        //Sil
                        checkDelete.setR_response(1);
                        deleteControlMessage = sessionBean.getLoc().getString("orderdelete");
                        deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                        deleteControlMessage2 = "";
                        relatedRecord = "";
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoOrder");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoOrder').show();");
                        break;
                }
            }
        

    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInfoOrder').hide();");
        context.execute("goToRelatedRecordOrder();");

    }

    public void goToRelatedRecord() {

        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        switch (checkDelete.getR_response()) {
            case -101:
                Waybill waybill = new Waybill();
                waybill.setId(checkDelete.getR_record_id());
                waybill = waybillService.findWaybill(waybill);
                list.add(waybill);
                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 1, 41);
                break;
            default:
                break;
        }
    }

    
    public void delete(){

        int result = 0;
        result = orderService.delete(selectedObject);
        if (result > 0) {

            isSendCenter = true;
            marwiz.goToPage("/pages/finance/order/order.xhtml", null, 1, 228);
            
        }
        sessionBean.createUpdateMessage(result);
    }
    
     /**
     * Bu metot sayfadan çıkıldığı anda tetiklenir. Merkez entegrasyonu var ise
     * ve değişikli oldu ise gerekli fonksiyonu tetkiler
     */
    @PreDestroy
    public void destroy() {
        int result;
        if (isSendCenter && selectedObject.getBranchSetting().isIsCentralIntegration()) {//merkeze gönderilecek ise ve merkez entegrasyonu var ise ve silinmedi ise

            result = orderService.sendOrderCenter(selectedObject);

            if (result > 0) {//işlem başarılı loga kayıt eklendi ise gönderme metodunu çağır.

                SendOrder order = sendOrderService.findByOrderId(selectedObject.getId());
                sendOrderService.sendOrderToCenter(order);
                
            }
        }

    }

}
