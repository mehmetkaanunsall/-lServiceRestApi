/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   22.01.2018 11:27:08
 */

package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;


public interface IWarehouseItemDao {
    
    public List<WarehouseItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse wareHouse);

    public int count(String where, Warehouse wareHouse);
    
    public int addStock(Warehouse warehouse,String where);
    
    public int update(WarehouseItem obj);
    
    public String exportData(String where);

    public DataSource getDatasource();

}
