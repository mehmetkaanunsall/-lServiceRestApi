/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 14.09.2018 08:40:04
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public interface IPriceListBatchOperationsService {

    public int updateStocks(int processType, int priceListId, boolean isRate, BigDecimal price, String where);

    public List<PriceListItem> processUploadFileStock(InputStream inputStream);
}
