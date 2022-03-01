/**
 * This class ...
 *
 *
 * @author SALİM VELA ABDULHADİ
 *
 * @date   08.01.2019 03:36:00
 */
package com.mepsan.marwiz.general.history.business;

import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GfHistoryService extends GeneralFilterService<History> {

    @Autowired
    private IHistoryService historyService;

    public void setHistoryService(IHistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = where + "his.oldvalue" + " ilike '%" + value + "%' ";
        where = where + "or his.newvalue ilike '%" + value + "%' ";
        where = where + "or his.fk_oldvalue" + " ilike '%" + value + "%' ";
        where = where + "or his.fk_newvalue ilike '%" + value + "%' ";

        where = where + ")";
        System.out.println("-*-*-*-*- " + where);
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

    public void makeSearch(String value, String where, int pageId, int rowId, String tableName) {

        searchResult = new CentrowizLazyDataModel<History>() {
            @Override
            public List<History> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                System.out.println("make search ");
                List<History> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where + where1, rowId, tableName, pageId);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where + where1, rowId, pageId, tableName);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    public List<History> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int rowId, int pageId, String tableName) {
        return historyService.findAll(first, pageSize, filters, where, rowId, tableName, pageId);
    }

    public int callDaoCount(String where, int rowId, String tableName, int pageId) {
        return historyService.count(where, rowId, tableName, pageId);
    }

    @Override
    public List<History> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
