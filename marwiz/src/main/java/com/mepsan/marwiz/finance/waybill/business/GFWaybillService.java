/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 03:10:48
 */

package com.mepsan.marwiz.finance.waybill.business;

import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;


public class GFWaybillService extends GeneralFilterService<Waybill> {

    @Autowired
    private WaybillService waybillService;

    public void setWaybillService(WaybillService waybillService) {
        this.waybillService = waybillService;
    }
      
    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "wb.waybilldate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "wb.documentnumber" + " ilike '%" + value + "%' ";
        where = where + "or " + "wb.documentserial" + " ilike '%" + value + "%' ";
        where = where + "or " + "acc.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "wb.deliveryperson" + " ilike '%" + value + "%'  ";
        where = where + "or " + "typd.name" + " ilike '%" + value + "%'  ";
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
        searchResult = new CentrowizLazyDataModel<Waybill>() {
            @Override
            public List<Waybill> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Waybill> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where+where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where+where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

     
    @Override
    public List<Waybill> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return waybillService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return waybillService.count(where);
    }

}
