package com.mepsan.marwiz.finance.order.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.order.business.IOrderRelatedRecordService;
import com.mepsan.marwiz.finance.order.dao.OrderRelatedRecord;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
/**
 *
 * @author ebubekir.buker
 */
@ManagedBean
@ViewScoped
public class OrderRelatedRecordBean {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService waybillService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{orderRelatedRecordService}")
    public IOrderRelatedRecordService orderRelatedRecordService;

    @ManagedProperty(value = "#{orderProcessBean}")
    public OrderProcessBean orderProcessBean;

    private OrderRelatedRecord selectedObject;
    private List<OrderRelatedRecord> listOfObject;
    private Order selectedOrder;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public OrderRelatedRecord getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(OrderRelatedRecord selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<OrderRelatedRecord> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<OrderRelatedRecord> listOfObject) {
        this.listOfObject = listOfObject;
    }


    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setOrderRelatedRecordService(IOrderRelatedRecordService orderRelatedRecordService) {
        this.orderRelatedRecordService = orderRelatedRecordService;
    }

    public OrderProcessBean getOrderProcessBean() {
        return orderProcessBean;
    }

    public void setOrderProcessBean(OrderProcessBean orderProcessBean) {
        this.orderProcessBean = orderProcessBean;
    }

    @PostConstruct
    public void init() {
        listOfObject = new ArrayList<>();
        System.out.println("-----OrderRelatedRecordBean---");
        selectedOrder = orderProcessBean.getSelectedObject();
        selectedObject = new OrderRelatedRecord();
        listOfObject = orderRelatedRecordService.listOfOrderRelatedRecords(selectedOrder);
    }

    public void goToPage() {
        List<Object> list = (ArrayList) sessionBean.parameter;

        switch (selectedObject.getDocumentType()) {
            case 0:
                Waybill waybill = waybillService.find(selectedObject.getRelatedId());
                list.add(waybill);
                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 0, 41);
                break;

            case 1:
                Invoice invoice = invoiceService.findInvoice(new Invoice(selectedObject.getRelatedId()));
                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 0, 26);
                break;
        }

    }
}
