/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 08:36:26
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.invoice.business.IRelatedRecordService;
import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.finance.order.business.IOrderService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class RelatedRecordsTabBean {

    private RelatedRecord selectedObject;
    private List<RelatedRecord> listOfObjects;
    private Invoice selectedInvoice;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{invoiceProcessBean}")
    public InvoiceProcessBean invoiceProcessBean;

    @ManagedProperty(value = "#{relatedRecordService}")
    public IRelatedRecordService relatedRecordService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService waybillService;

    @ManagedProperty(value = "#{creditService}")
    public ICreditService creditService;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{orderService}")
    public IOrderService orderservice;

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setRelatedRecordService(IRelatedRecordService relatedRecordService) {
        this.relatedRecordService = relatedRecordService;
    }

    public void setInvoiceProcessBean(InvoiceProcessBean invoiceProcessBean) {
        this.invoiceProcessBean = invoiceProcessBean;
    }

    public void setSelectedInvoice(Invoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public RelatedRecord getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(RelatedRecord selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<RelatedRecord> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<RelatedRecord> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public void setOrderservice(IOrderService orderservice) {
        this.orderservice = orderservice;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------------RelatedRecordsTabBean");

        selectedInvoice = invoiceProcessBean.getSelectedObject();
        selectedObject = new RelatedRecord();
        listOfObjects = relatedRecordService.listOfRelatedRecords(selectedInvoice);

    }

    public void goToPage() {

        List<Object> list = (ArrayList) sessionBean.parameter;

        switch (selectedObject.getDocumentType()) {
            case 0://irsaliye ise
                Waybill waybill = waybillService.find(selectedObject.getRelatedId());
                list.add(waybill);
                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 0, 41);
                break;
            case 1://Kredi ise
                CreditReport cr = new CreditReport();
                cr.setId(selectedObject.getRelatedId());
                cr = creditService.findCreditReport(cr);
                list.add(cr);
                marwiz.goToPage("/pages/finance/credit/creditprocess.xhtml", list, 0, 79);
                break;
            case 2://Çek ise
            case 3://Senet ise
                ChequeBill chq = new ChequeBill();
                chq.setId(selectedObject.getRelatedId());
                chq = chequeBillService.findChequeBill(chq);
                list.add(chq);
                marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 0, 81);
                break;
            case 4://Sipariş ise
                Order order = orderservice.findOrder(selectedObject.getRelatedId());
                list.add(order);
                marwiz.goToPage("/pages/finance/order/orderprocess.xhtml", list, 0, 229);
                break;
            default:
                break;
        }

    }
}
