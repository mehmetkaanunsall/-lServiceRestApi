/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 05:19:03
 */
package com.mepsan.marwiz.general.report.orderlistreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.orderlistreport.dao.OrderListReport;
import java.util.List;
import java.util.Map;

public interface IOrderListReportService extends IReportService<OrderListReport> {

    public void exportPdf(String where, OrderListReport orderListReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> selectedBranchList);

    public void exportExcel(String where, OrderListReport orderListReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> selectedBranchList);

    public String exportPrinter(String where, OrderListReport orderListReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> selectedBranchList);

    public List<Warehouse> listWarehouse(String branchList);

    public String createWhere(OrderListReport obj, List<BranchSetting> branchList, int supplierType, boolean isCentralSupplier);

    public List<OrderListReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchID);

    public int count(String where, String branchID);

}
