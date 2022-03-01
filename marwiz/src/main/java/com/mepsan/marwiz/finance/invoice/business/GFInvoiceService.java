/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 03:36:00
 */

package com.mepsan.marwiz.finance.invoice.business;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;


public class GFInvoiceService extends GeneralFilterService<Invoice> {

    @Autowired
    private IInvoiceService invoiceService;

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    
    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "inv.invoicedate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "inv.documentnumber" + " ilike '%" + value + "%' ";
        where = where + "or " + "inv.documentserial" + " ilike '%" + value + "%' ";
        where = where + "or " + "acc.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "inv.totalmoney" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sttd.name" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void makeSearch(String value,String where) {
        searchResult = new CentrowizLazyDataModel<Invoice>() {
            @Override
            public List<Invoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Invoice> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where+where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where+where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<Invoice> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return invoiceService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return invoiceService.count(where);
    }

}
