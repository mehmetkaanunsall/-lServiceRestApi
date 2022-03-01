/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.02.2018 11:59:00
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IStockMovementService {

    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch);

    public StockMovement count(String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch);

    public List<StockMovement> listOfWarehouseAvailability(Stock stock, Warehouse warehouse, List<Branch> listOfBranch);

    public void exportPdf(String where, StockMovement stockMovement, List<Boolean> toogleList, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal remainingAmount, BigDecimal transferAmount);

    public void exportExcel(String where, StockMovement stockMovement, List<Boolean> toogleList, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal remainingAmount, BigDecimal transferAmount);

}
