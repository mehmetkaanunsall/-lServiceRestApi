/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 16:50:49
 */
package com.mepsan.marwiz.automation.nozzle.business;

import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFNozzleService extends GeneralFilterService<Nozzle> {

    @Autowired
    private INozzleService nozzleService;

    public void setNozzleService(INozzleService nozzleService) {
        this.nozzleService = nozzleService;
    }

    public void makeSearch(String value, String where) {
        searchResult = new CentrowizLazyDataModel<Nozzle>() {
            @Override
            public List<Nozzle> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Nozzle> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where + where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where + where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "AND (";
        where = where + "  " + "wr.name" + " ILIKE '%" + value + "%' ";
        where = where + "OR " + "nz.pumpno" + " ILIKE '%" + value + "%' ";
        where = where + "OR " + "nz.nozzleno" + " ILIKE '%" + value + "%' ";
        where = where + "OR " + "sttd.name" + " ILIKE '%" + value + "%' ";
        where = where + "OR " + "nz.description" + " ILIKE '%" + value + "%' ";
        where = where + "OR " + "to_char(" + "nz.index" + ",'99999999999999D9999')" + " ILIKE '%" + value + "%'  ";
        where = where + "OR " + "to_char(" + "sttd.status_id" + ",'99999999999999D9999')" + " ILIKE '%" + value + "%'  ";
        where = where + "OR " + "wr.name" + " ILIKE '%" + value + "%' ";
        where = where + "OR " + "sttd.name" + " ILIKE '%" + value + "%' ";
        where = where + ")";
        return where;
    }

    @Override
    public List<Nozzle> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return nozzleService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return nozzleService.count(where);
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
