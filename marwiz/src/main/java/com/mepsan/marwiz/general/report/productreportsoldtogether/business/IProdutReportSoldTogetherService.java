/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:01:23 PM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.report.productreportsoldtogether.dao.ProductReportSoldTogether;
import java.util.List;
import java.util.Map;

public interface IProdutReportSoldTogetherService {

    public String createWhere(ProductReportSoldTogether obj);

    public List<ProductReportSoldTogether> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductReportSoldTogether obj, BranchSetting selectedBranch);

    public int count(String where, ProductReportSoldTogether obj, BranchSetting selectedBranch);

    public void exportPdf(String where, ProductReportSoldTogether productReportSoldTogether, List<Boolean> toogleList, BranchSetting selectedBranch);

    public void exportExcel(String where, ProductReportSoldTogether productReportSoldTogether, List<Boolean> toogleList, BranchSetting selectedBranch);

    public String exportPrinter(String where, ProductReportSoldTogether productReportSoldTogether, List<Boolean> toogleList, BranchSetting selectedBranch);

}
