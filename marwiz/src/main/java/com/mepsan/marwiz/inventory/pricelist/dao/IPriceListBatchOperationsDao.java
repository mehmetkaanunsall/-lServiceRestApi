/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 14.09.2018 08:43:18
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import java.math.BigDecimal;

public interface IPriceListBatchOperationsDao {

    public int updateStocks(int processType,int priceListId,boolean isRate, BigDecimal price,String where);
}
