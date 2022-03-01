/**
 *
 *
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 08:29:15
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.model.inventory.StockUpload;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IStockService extends ILazyGrid<Stock> {

    public String createWhere(boolean isWithoutSalePrice, boolean isNoneZero, boolean isPassiveStock, List<Categorization> listCategorization,boolean isService);

    public List<Stock> stockBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int stockBookCount(String where, String type, List<Object> param);

    public int stockBarcodeControl(Stock stock);

    public int stockBarcodeControl(StockAlternativeBarcode stockAlternativeBarcode);

    public int updateUnit(Stock stock);

    public int testBeforeDelete(Stock stock);

    public int delete(Stock stock);

    public String importProductList(List<StockUpload> stocks);

    public String importProductListForCentral(List<StockUpload> stocks);

    public int updateDetail(Stock stock);

    public int batchUpdate(List<Stock> stockList, int changeField, Stock stock, List<Categorization> listCategorization);

    public Stock findStockLastPrice(int stockId, BranchSetting branchSetting);

    public List<Stock> totals(String where);

    public List<StockUpload> processUploadFile(InputStream inputStream);

    public List<StockUpload> processUploadFileForCentral(InputStream inputStream);

    public List<StockUpload> openUploadProcessPage();

    public List<StockUpload> openUploadProcessPageForCentral();

    public List<Stock> findFuelStock();

    public Stock findStokcUnit(String barcode, Invoice obj, boolean isAlternativeBarcode, BranchSetting branchSetting);

    public Stock findStockBarcode(String barcode);

    public void exportPdf(String where, List<Boolean> toogleList);

    public void exportExcel(String where, List<Boolean> toogleList);

    public int stockBarcodeControlRequest(Stock stock);

    public Stock findStockAccordingToBarcode(Stock stock);

    public int create(Stock stock, boolean isAvailableStock);

    public int update(Stock stock, boolean isAvailableStock);
    
    public void downloadSampleList(List<StockUpload> list);
}
