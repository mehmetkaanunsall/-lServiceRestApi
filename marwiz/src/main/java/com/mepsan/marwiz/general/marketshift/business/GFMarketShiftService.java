/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 01:53:29
 */
package com.mepsan.marwiz.general.marketshift.business;

import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFMarketShiftService extends GeneralFilterService<Shift> {

    @Autowired
    private IMarketShiftService marketShiftService;

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "shf.shiftno" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.begindate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.enddate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "sttd.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "shf.name" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "shf.shiftno" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.begindate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.enddate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "shf.name" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        searchResult = new CentrowizLazyDataModel<Shift>() {
            @Override
            public List<Shift> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                int count = marketShiftService.shiftBookCount(createWhereForBook(value), type, param);
                searchDataList = marketShiftService.shiftBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForBook(value), type, param);
                searchResult.setRowCount(count);

                return searchDataList;
            }
        };

    }

    public void makeSearch(String where, String value) {
        searchResult = new CentrowizLazyDataModel<Shift>() {
            @Override
            public List<Shift> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Shift> result;
                String where1 = where + " " + createWhere(value);
                int count = callDaoCount(where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<Shift> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return marketShiftService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }
    
    @Override
    public int callDaoCount(String where) {
        return marketShiftService.count(where);
    }

}
