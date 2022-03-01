/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:01:06 PM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IProdutReportSoldTogetherDao {

    public List<ProductReportSoldTogether> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductReportSoldTogether obj, BranchSetting selectedBranch);

    public int count(String where, ProductReportSoldTogether obj, BranchSetting selectedBranch);

    public String exportData(String where, ProductReportSoldTogether obj, BranchSetting selectedBranch);

    public DataSource getDatasource();

}
