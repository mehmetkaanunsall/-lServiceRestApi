/**
 *
 *
 *
 * @author Ali Kurt
 *   
 * Created on 12.01.2018 10:43:03
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IStockDao extends ILazyGrid<Stock> {

    public List<Stock> stockBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int stockBookCount(String where, String type, List<Object> paramd);

    public int stockBarcodeControl(Stock stock);

    public int stockBarcodeControl(StockAlternativeBarcode stockAlternativeBarcode);

    public int updateUnit(Stock stock);

    public int testBeforeDelete(Stock stock);

    public int delete(Stock stock);

    public String importProductList(String json);

    public String importProductListForCentral(String json);

    public int updateDetail(Stock stock);

    public int batchUpdate(String where, int changeField, Stock stock);

    public Stock findStockLastPrice(int stockId, BranchSetting branchSetting);

    public List<Stock> totals(String where);

    public List<Stock> findFuelStock();

    public Stock findStokcUnit(String barcode, Invoice obj, boolean isAlternativeBarcode, BranchSetting branchSetting);

    public Stock findSaleMandatoryPrice(int stockId, BranchSetting branchSetting);

    public Stock findStockBarcode(String barcode);

    public String exportData(String where);

    public DataSource getDatasource();

    public int stockBarcodeControlRequest(Stock stock);

    public Stock findStockAccordingToBarcode(Stock stock);

    public int create(Stock stock, boolean isAvailableStock);

    public int update(Stock stock, boolean isAvailableStock);

}
