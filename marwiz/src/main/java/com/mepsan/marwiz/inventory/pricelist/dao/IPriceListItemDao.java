/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:38:23
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;
import java.util.Map;

public interface IPriceListItemDao extends ICrud<PriceListItem> {

    public int createItem(PriceListItem item, Branch branch);

    public List<PriceListItem> listofPriceListItem(PriceList obj, String where);

    public PriceListItem findStockPrice(Stock stock, boolean isPurchase, Branch branch);

    public String processStockPriceList(String json, int priceListId, Boolean isUpdate);

    public int delete(PriceListItem obj);

    public List<PriceListItem> listOfStock(int type, int priceListId, String where);

    public List<PriceListItem> listOfUpdatingPriceStock(PriceList obj);

    public int updatingPriceStock(String listOfItem, PriceList priceList, Branch branch);

    public List<PriceListItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj);

    public int count(String where, PriceList obj);

    public List<PriceListItem> findAllRecordedStock(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj, int type);

    public int countRecordedStock(String where, PriceList obj);

    public int deleteRecordedStock(String deleteList);

}
