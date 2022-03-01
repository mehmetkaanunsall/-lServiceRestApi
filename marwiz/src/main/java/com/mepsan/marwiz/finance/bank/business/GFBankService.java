/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 08:43:16
 */
package com.mepsan.marwiz.finance.bank.business;

import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFBankService extends GeneralFilterService<Bank> {

    @Autowired
    private IBankService bankService;

    public void setBankService(IBankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public String createWhere(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createWhereForBook(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "bnk.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "bnk.code" + " ilike '%" + value + "%'  ";
        where = where + ")";
        System.out.println("*****where*******" + where);
        return where;
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        searchResult = new CentrowizLazyDataModel<Bank>() {
            @Override
            public List<Bank> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                int count = bankService.bankBookCount(createWhereForBook(value), type, param);
                searchDataList = bankService.bankBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForBook(value), type, param);
                searchResult.setRowCount(count);
                return searchDataList;
            }
        };
    }

    @Override
    public List<Bank> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
