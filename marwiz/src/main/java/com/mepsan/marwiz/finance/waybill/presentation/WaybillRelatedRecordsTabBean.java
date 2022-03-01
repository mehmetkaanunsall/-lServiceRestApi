/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 08:36:26
 */
package com.mepsan.marwiz.finance.waybill.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.finance.order.business.IOrderService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import com.mepsan.marwiz.finance.waybill.business.IWaybillRelatedRecordService;
import com.mepsan.marwiz.general.model.finance.Order;

@ManagedBean
@ViewScoped
public class WaybillRelatedRecordsTabBean {

    private RelatedRecord selectedObject;
    private List<RelatedRecord> listOfObjects;
    private Waybill selectedWaybill;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{waybillProcessBean}")
    public WaybillProcessBean waybillProcessBean;

    @ManagedProperty(value = "#{waybillRelatedRecordService}")
    public IWaybillRelatedRecordService waybillRelatedRecordService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService waybillService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{orderService}")
    public IOrderService orderservice;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWaybillProcessBean(WaybillProcessBean waybillProcessBean) {
        this.waybillProcessBean = waybillProcessBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setWaybillRelatedRecordService(IWaybillRelatedRecordService waybillRelatedRecordService) {
        this.waybillRelatedRecordService = waybillRelatedRecordService;
    }

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
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

    public Waybill getSelectedWaybill() {
        return selectedWaybill;
    }

    public void setSelectedWaybill(Waybill selectedWaybill) {
        this.selectedWaybill = selectedWaybill;
    }

    public void setOrderservice(IOrderService orderservice) {
        this.orderservice = orderservice;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------------RelatedRecordsTabBean");

        selectedWaybill = waybillProcessBean.getSelectedObject();
        selectedObject = new RelatedRecord();
        listOfObjects = waybillRelatedRecordService.listOfRelatedRecords(selectedWaybill);

    }

    public void goToPage() {
        List<Object> list = (ArrayList) sessionBean.parameter;

        switch (selectedObject.getDocumentType()) {
            case 0://fatura ise
                Invoice invoice = invoiceService.findInvoice(new Invoice(selectedObject.getRelatedId()));
                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 0, 26);
                break;
            case 1://Sipariş ise
                Order order = orderservice.findOrder(selectedObject.getRelatedId());
                list.add(order);
                marwiz.goToPage("/pages/finance/order/orderprocess.xhtml", list, 0, 229);
                break;
        }
    }

}
