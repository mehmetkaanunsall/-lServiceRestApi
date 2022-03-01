/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 11:23:52
 */
package com.mepsan.marwiz.inventory.stockoperations.dao;

import java.util.List;

public interface IStockOperationsDao {

    public List<StockOperations> findAll(String where, int process);

    public int processPriceList(String operaionsJson);

}
