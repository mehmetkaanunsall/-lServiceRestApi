/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.10.2018 08:12:11
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IPurchaseSalesReportDao {

    public List<PurchaseSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSalesReport obj,String branchList, int centralIngetrationInf);

    public List<PurchaseSalesReport> stockDetail(String where, PurchaseSalesReport obj, String branchList);

    public List<PurchaseSalesReport> count(String where, PurchaseSalesReport obj,String branchList, int centralIngetrationInf);

    public String exportData(String where, PurchaseSalesReport obj, String branchList, int centralIngetrationInf);

    public DataSource getDatasource();

    public List<TaxGroup> listOfTaxGroup(int type, List<BranchSetting> branchList);

}
