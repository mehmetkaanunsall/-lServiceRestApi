/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 11:59:28
 */
package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFWarehouseItemService extends GeneralFilterService<WarehouseItem> {

    @Autowired
    private IWarehouseItemService warehouseItemService;

    public void setWarehouseItemService(IWarehouseItemService warehouseItemService) {
        this.warehouseItemService = warehouseItemService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.code" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.centerproductcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "gunt.sortname" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sab.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "iwi.quantity" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "iwi.minstocklevel" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
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

    /**
     * Depoların stok tabında grid listelenirekn depo id ye bakıldığı için Depo
     * parametresi eklendi.
     *
     * @param value
     * @param wareHouse depo
     */
    public void makeSearch(String value, Warehouse wareHouse) {
        searchResult = new CentrowizLazyDataModel<WarehouseItem>() {
            @Override
            public List<WarehouseItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WarehouseItem> result;
                String where = createWhere(value);
                int count = callDaoCount(where, wareHouse);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, wareHouse);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    public List<WarehouseItem> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse warehouse) {
        return warehouseItemService.findAll(first, pageSize, sortField, sortOrder, filters, where, warehouse);
    }

    public int callDaoCount(String where, Warehouse warehouse) {
        return warehouseItemService.count(where, warehouse);
    }

    @Override
    public List<WarehouseItem> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
