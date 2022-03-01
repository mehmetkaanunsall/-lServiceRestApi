/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.02.2018 07:39:56
 */
package com.mepsan.marwiz.inventory.stocktaking.dao;

import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IStockTakingItemDao {


    public List<StockTakingItem> findAllSaleControlList(StockTaking obj);

    public List<StockTakingItem> findAllUncountedStocks(StockTaking stockTaking);

    public List<StockTakingItem> findAllMinusStocks(StockTaking stockTaking);

    public int processStockTakingItem(int type, StockTaking stockTaking, boolean isReset);

    public int updateSaleControl(StockTaking stockTaking);

    public String importStockTakingItem(String json, StockTaking stockTaking);

    public List<StockTakingItem> findWithoutCategorization(StockTaking obj, String categories);

    public int delete(String ids);

    public String exportData(String where, StockTaking stockTaking);

    public List<StockTakingItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockTaking stockTaking);

    public int count(String where, StockTaking stockTaking);

    public String exportData(List<StockTakingItem> listOfItemUpdate, StockTaking stockTaking);
    
    public DataSource getDatasource();
}
