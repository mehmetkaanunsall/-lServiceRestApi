/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.05.2020 11:01:14
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.business;

import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFWarehouseTransferService extends GeneralFilterService<WarehouseTransfer> {

    @Autowired
    private ITransferBetweenWarehouseService transferBetweenWarehouseService;

    public void setTransferBetweenWarehouseService(ITransferBetweenWarehouseService transferBetweenWarehouseService) {
        this.transferBetweenWarehouseService = transferBetweenWarehouseService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "wht.processdate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "wht.receiptnumber" + " ilike '%" + value + "%' ";
        where = where + "or " + "wh.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "wh2.name" + " ilike '%" + value + "%' ";
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void makeSearch(String value, String where) {
        searchResult = new CentrowizLazyDataModel<WarehouseTransfer>() {
            @Override
            public List<WarehouseTransfer> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WarehouseTransfer> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where + where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where + where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<WarehouseTransfer> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return transferBetweenWarehouseService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return transferBetweenWarehouseService.count(where);
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
