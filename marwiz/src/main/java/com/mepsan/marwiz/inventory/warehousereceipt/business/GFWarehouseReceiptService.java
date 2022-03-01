/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 11:09:31
 */

package com.mepsan.marwiz.inventory.warehousereceipt.business;

import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;


public class GFWarehouseReceiptService extends GeneralFilterService<WarehouseReceipt> {

    @Autowired
    private IWarehouseReceiptService warehouseReceiptService;

    public void setWarehouseReceiptService(IWarehouseReceiptService warehouseReceiptService) {
        this.warehouseReceiptService = warehouseReceiptService;
    }
    
    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "whr.processdate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "whr.receiptnumber" + " ilike '%" + value + "%' ";
        where = where + "or " + "wh.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "typd.name" + " ilike '%" + value + "%'  ";
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
        searchResult = new CentrowizLazyDataModel<WarehouseReceipt>() {
            @Override
            public List<WarehouseReceipt> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WarehouseReceipt> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where+where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where+where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<WarehouseReceipt> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return warehouseReceiptService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return warehouseReceiptService.count(where);
    }

}
