/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   22.01.2018 11:28:32
 */

package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.util.List;
import java.util.Map;


public interface IWarehouseItemService {

     public List<WarehouseItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse wareHouse);

    public int count(String where,Warehouse wareHouse);
    
    public int addStock(Warehouse warehouse , List<Stock> listOfStock);
    
    public int update(WarehouseItem obj);
    
    public void exportPdf(String where, List<Boolean> toogleList, Warehouse wareHouse);
}
