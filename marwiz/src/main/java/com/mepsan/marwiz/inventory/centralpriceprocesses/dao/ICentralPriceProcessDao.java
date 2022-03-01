/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.06.2020 11:33:50
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.dao;

import java.util.List;
import java.util.Map;

public interface ICentralPriceProcessDao {

    public List<CentralPriceProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int branchStock, String branchID);

    public int count(String where, int branchStock, String branchID);

    public int save(String selectedCentralPrice, boolean isPurchase);

}
