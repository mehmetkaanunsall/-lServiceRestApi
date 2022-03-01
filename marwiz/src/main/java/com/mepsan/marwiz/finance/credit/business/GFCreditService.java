/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.business;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Gozde Gursel
 */
public class GFCreditService extends GeneralFilterService<CreditReport> {

    @Autowired
    private ICreditService creditService;

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "crdt.processdate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "acc.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "crdt.money" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "crdt.duedate " + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "crdt.remainingmoney" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
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

    public void makeSearch(String value, String where, int creditType) {
        searchResult = new CentrowizLazyDataModel<CreditReport>() {
            @Override
            public List<CreditReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<CreditReport> result;
                String where1 = createWhere(value);
                switch (creditType) {
                    case 0:///tümü
                        where1 = where1 + where + "  ";
                        break;
                    case 1://kredi tahsilatları
                        where1 = where1 + where + " AND crdt.is_customer=TRUE ";
                        break;
                    case 2://kredi ödemeleri
                        where1 = where1 + where + " AND crdt.is_customer=FALSE";
                        break;
                    default:
                        break;
                }
                int count = callDaoCount(where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<CreditReport> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return creditService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return creditService.count(where);
    }

}
