/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 11:22:13
 */
package com.mepsan.marwiz.inventory.stockoperations.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.inventory.stockoperations.dao.StockOperations;
import java.util.Date;
import java.util.List;

public interface IStockOperationsService {

    public List<StockOperations> findAll(String where, int process);

    public String createWhere(Date beginDate, Date endDate, List<Stock> listOfStock, int process,StockOperations stockOperations,boolean isCentralSupplier, int supplierType);

    public int processPriceList(List<StockOperations> operationses);

}
