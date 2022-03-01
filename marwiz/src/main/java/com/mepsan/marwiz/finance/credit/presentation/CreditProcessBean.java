/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
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
 * @author Gozde Gursel
 */
@ManagedBean
@ViewScoped
public class CreditProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{creditService}")
    public ICreditService creditService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private CreditReport selectedObject;
    private int activeIndex;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private boolean isDeleteButton;
    private List<BranchSetting> listOfBranch;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public CreditReport getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(CreditReport selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
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

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------------CredtiProcessBean --------");
        selectedObject = new CreditReport();
        controlDeleteList = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof CreditReport) {
                    selectedObject = (CreditReport) ((ArrayList) sessionBean.parameter).get(i);
                    break;
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{21}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{7}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void gotoInvoice() {
        List<Object> list = new ArrayList<>();
        selectedObject.setBeginDate(selectedObject.getProcessDate());
        selectedObject.setEndDate(selectedObject.getProcessDate());
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);

    }

    public void testBeforeDelete() {
        if (sessionBean.isPeriodClosed(selectedObject.getDueDate())) {

            deleteControlMessage = "";
            deleteControlMessage1 = "";
            deleteControlMessage2 = "";
            relatedRecord = "";
            controlDeleteList.clear();
            controlDeleteList = creditService.testBeforeDelete(selectedObject);
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
                        case -103: //kredi periyodik faturaya bağlı olduğu için silinemez
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("itcannotbedeletedbecauserelatedinvoiceiscreatedfromthiscredit");
                            deleteControlMessage2 = "";
                            break;
                        case -104: //kredinin bağlı olduğu otomasyon vardiyası kapalı olduğu için
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtofuelshift");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfromfuelshift");
                            deleteControlMessage2 = sessionBean.getLoc().getString("shiftno") + " : ";
                            relatedRecord = controlDeleteList.get(0).getR_recordno();
                            relatedRecordId = controlDeleteList.get(0).getR_record_id();
                            break;
                        default:
                            break;
                    }
                    if (controlDeleteList.get(0).getR_response() != -105) {
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoProcess");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoProcess').show();");
                    } else if (controlDeleteList.get(0).getR_response() == -105) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {//Sil
                    deleteControlMessage = sessionBean.getLoc().getString("creditdelete");
                    deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                    deleteControlMessage2 = "";
                    isDeleteButton = true;
                    RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoProcess");
                    RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoProcess').show();");
                }
            }
        }

    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedObject.getDueDate())) {
            int result = 0;
            result = creditService.delete(selectedObject);
            if (result > 0) {
                List<Object> list = new ArrayList<>();
                list.addAll((ArrayList) sessionBean.parameter);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CreditReport) {
                        list.remove(list.get(i));
                    }
                }
                marwiz.goToPage("/pages/finance/credit/credit.xhtml", list, 1, 78);
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public void goToRelatedRecordBefore() {

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInfoProcess').hide();");
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
        } else if (controlDeleteList.get(0).getR_response() == -104) {
            FuelShift fuelShift = new FuelShift();
            fuelShift.setId(relatedRecordId);
            fuelShift = fuelShiftTransferService.findShift(fuelShift);
            list.add(fuelShift);
            marwiz.goToPage("/pages/automation/fuelshift/fuelshifttransferprocesses.xhtml", list, 1, 108);
        }
    }

}
