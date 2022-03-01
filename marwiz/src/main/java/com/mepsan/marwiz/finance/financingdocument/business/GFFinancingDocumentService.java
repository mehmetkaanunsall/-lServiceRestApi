/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 04:04:04
 */
package com.mepsan.marwiz.finance.financingdocument.business;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFFinancingDocumentService extends GeneralFilterService<FinancingDocument> {

    @Autowired
    private IFinancingDocumentService financingDocumentService;

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "fdoc.documentdate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "typd.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "fdoc.documentnumber" + " ilike '%" + value + "%' ";
        where = where + "or " + "fdoc.description" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "fdoc.price" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "fdoc.exchangerate" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        
        
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

    public void makeSearch(String value, String where) {
        searchResult = new CentrowizLazyDataModel<FinancingDocument>() {
            @Override
            public List<FinancingDocument> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<FinancingDocument> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where + where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where + where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<FinancingDocument> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return financingDocumentService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return financingDocumentService.count(where);
    }

}
