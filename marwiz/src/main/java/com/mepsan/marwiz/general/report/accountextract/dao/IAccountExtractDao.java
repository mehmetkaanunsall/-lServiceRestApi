/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.03.2018 11:50:18
 */
package com.mepsan.marwiz.general.report.accountextract.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IAccountExtractDao {

    public List<AccountExtract> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AccountExtract accountExtract);

    public String exportData(String where, String sortField, String sortOrder, AccountExtract accountExtract);

    public DataSource getDatasource();

    public int findAccountCount(AccountExtract accountExtract, int pageId);

    public List<AccountExtract> totals(String where, AccountExtract accountExtract);
}
