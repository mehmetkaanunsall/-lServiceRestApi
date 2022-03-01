/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:38:04
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IPriceListItemService extends ICrudService<PriceListItem> {

    public int createItem(PriceListItem item, Branch branch);

    public PriceListItem findStockPrice(Stock stock, boolean isPurchase, Branch branch);

    public List<PriceListItem> listofPriceListItem(PriceList obj, String where);

    public int delete(PriceListItem obj);

    public List<PriceListItem> listOfStock(int type, int priceListId, String where);

    public String processStockPriceList(List<PriceListItem> listItems, int priceListId, Boolean isUpdate);

    public List<PriceListItem> listOfUpdatingPriceStock(PriceList obj);

    public int updatingPriceStock(PriceList priceList, List<PriceListItem> listOfItem, Branch branch);

    public List<PriceListItem> processUploadFile(InputStream inputStream);

    public List<PriceListItem> createSampleList();

    public List<PriceListItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj);

    public int count(String where, PriceList obj);

    public List<PriceListItem> matchExcelToList(List<PriceListItem> excelList, PriceList obj);

    public List<PriceListItem> findAllRecordedStock(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj, int type);

    public int countRecordedStock(String where, PriceList obj);

    public int deleteRecordedStock(String deleteList);
    
    public void downloadSampleList (List<PriceListItem>sampleList);

}
