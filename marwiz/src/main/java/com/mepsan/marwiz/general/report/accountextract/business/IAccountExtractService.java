/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.03.2018 12:00:25
 */
package com.mepsan.marwiz.general.report.accountextract.business;

import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import java.util.List;
import java.util.Map;

public interface IAccountExtractService {

    public List<AccountExtract> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AccountExtract accountExtract);

    public String createWhere(AccountExtract obj, int pageId);

    public void exportPdf(String where, AccountExtract accountExtract, List<Boolean> toogleList, int pageId, List<AccountExtract> listOfTotals, String sortField, String sortOrder);

    public void exportExcel(String where, AccountExtract accountExtract, List<Boolean> toogleList, int pageId, List<AccountExtract> listOfTotals, String sortField, String sortOrder);

    public String exportPrinter(String where, AccountExtract accountExtract, List<Boolean> toogleList, int pageId, List<AccountExtract> listOfTotals, String sortField, String sortOrder);

    public int findAccountCount(AccountExtract accountExtract, int pageId);

    public List<AccountExtract> totals(String where, AccountExtract accountExtract);
}
