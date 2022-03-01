/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.12.2019 01:22:41
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class InvoiceBookFilterBean extends BookFilterBean<Invoice> {

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public List<Invoice> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<Invoice> callServiceLazyLoading(String where, List<Object> param, String type) {
        return new CentrowizLazyDataModel<Invoice>() {
            @Override
            public List<Invoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String whereSatment = " ";
                dataList = invoiceService.invoiceBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, whereSatment, type, param);
                int count= invoiceService.invoiceBookCount(whereSatment, type, param);
                dataListLazyLoading.setRowCount(count);
                return dataList;
            }
        };
    }

    @Override
    public void generalFilter(String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
