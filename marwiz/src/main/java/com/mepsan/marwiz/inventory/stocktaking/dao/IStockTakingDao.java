/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 11:24:11
 */
package com.mepsan.marwiz.inventory.stocktaking.dao;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IStockTakingDao {

    public List<StockTaking> findAll(String where);

    public List<StockTaking> selectStockTakingByWarehouse(Warehouse warehouse);

    public int delete(StockTaking stockTaking);

    public List<Account> employeList();

    public List<StockTaking> stockTakingDetail(StockTaking stockTaking);

    public List<StockTaking> findStockTakingDifference(StockTaking stockTaking);

    public int finisStockTaking(StockTaking stockTaking, FinancingDocument obj, String accounts);

    public int openStockTaking(StockTaking stockTaking);

    public StockTaking findOpenStock(StockTaking stockTaking);

    public int create(StockTaking obj, String categories);

    public int update(StockTaking obj, String deletedCategories, String insertedCategories, String items);

    public int update(StockTaking obj);

    public int findCategories(StockTaking obj, String where);

    public List<Stock> categoryOfStock(StockTaking obj, String where);

    public List<StockTaking> stockTakingProcessList();

}
