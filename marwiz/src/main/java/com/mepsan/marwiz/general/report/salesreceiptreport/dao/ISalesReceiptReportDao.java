/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 01:29:03
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.dao;

import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface ISalesReceiptReportDao extends ILazyGrid<SalesReport> {

    public List<SalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String subTotal, String branchList, SalesReport salesReport);

    public int count(String where, String subTotal, String branchList, SalesReport salesReport);

    public String exportData(String where, String subTotal, String branchList,SalesReport salesReport);

    public DataSource getDatasource();

    public List<SaleItem> findSaleItem(SalesReport salesReport);

    public List<SalePayment> findSalePayment(SalesReport salesReport);

    public List<SalesReport> totals(String where, String branchList, SalesReport salesReport);
}
