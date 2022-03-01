/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.02.2018 07:41:45
 */
package com.mepsan.marwiz.inventory.stocktaking.business;

import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IStockTakingItemService {

//    public List<StockTakingItem> findAll(StockTaking stockTaking);

    public String jsonArrayStockTakingItems(List<StockTakingItem> stockTakingItems);

    public List<StockTakingItem> findAllSaleControlList(StockTaking stockTaking);

    public List<StockTakingItem> findAllUncountedStocks(StockTaking stockTaking);

    public List<StockTakingItem> findAllMinusStocks(StockTaking stockTaking);

    public int updateSaleControl(StockTaking stockTaking);

   public int processStockTakingItem(int type, StockTaking stockTaking, List<StockTakingItem> listOfItems, boolean isReset);

    public String importStockTakingItem(List<StockTakingItem> stockTakingItems, StockTaking stockTaking);

    public void exportStocks(String where, StockTaking stockTaking);

    public List<StockTakingItem> processUploadFile(InputStream inputStream);

    public List<StockTakingItem> processUploadFileTxt(InputStream inputStream, Integer barcodeLengthStart, Integer barcodeLengthEnd, Integer pieceLengthStart, Integer pieceLengthEnd, Integer processDateLengthStart, Integer processDateLengthEnd, Date batchProcessDate, Integer dateFormatId);

    public List<StockTakingItem> createSampleList();

    public List<StockTakingItem> findWithoutCategorization(StockTaking obj, String categories);

    public int delete(List<StockTakingItem> stockTakingItems);

    public void exportExcel(List<StockTakingItem> listOfItemUpdate, StockTaking stockTaking);

    public void exportPdf(List<StockTakingItem> listOfItemUpdate, StockTaking stockTaking);

    public List<StockTakingItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockTaking stockTaking);

     public int count(String where, StockTaking stockTaking);

}
