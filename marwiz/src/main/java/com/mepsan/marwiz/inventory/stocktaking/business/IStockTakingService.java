/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 11:24:42
 */
package com.mepsan.marwiz.inventory.stocktaking.business;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.math.BigDecimal;
import java.util.List;

public interface IStockTakingService {

    public List<StockTaking> findAll(String where);

    public StockTaking find(StockTaking stockTaking);

    public List<StockTaking> selectStockTakingByWarehouse(Warehouse warehouse);

    public int delete(StockTaking stockTaking);

    public List<Account> employeList();

    public List<StockTaking> stockTakingDetail(StockTaking stockTaking);
    
    public List<StockTaking> findStockTakingDifference(StockTaking stockTaking);
    
    public int finisStockTaking(StockTaking stockTaking,FinancingDocument obj,String accounts);
    
    public String jsonArrayAccounts(List<AccountMovement> accountMovements);
    
    public int openStockTaking(StockTaking stockTaking);
    
    public StockTaking findOpenStock (StockTaking stockTaking);
    
    public int create(StockTaking obj,List<Categorization> listOfCategorization);
    
    public int update(StockTaking obj,List<StockTakingItem> deletedList,List<Categorization> oldCategoryList);
    
    public int update(StockTaking obj);
    
    public int findCategories(StockTaking obj,String where);
    
    public List<Stock> categoryOfStock(StockTaking obj,String where);
    
    public List<StockTaking> stockTakingProcessList();
    
}
