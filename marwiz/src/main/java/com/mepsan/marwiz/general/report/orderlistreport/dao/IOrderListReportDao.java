/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 05:16:25
 */
package com.mepsan.marwiz.general.report.orderlistreport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IOrderListReportDao extends ILazyGrid<OrderListReport> {

    public String exportData(String where, String branchID);

    public DataSource getDatasource();

    public List<Warehouse> listWarehouse(String branchID);

    public List<OrderListReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchID);

    public int count(String where, String branchID);

}
