/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.03.2019 05:39:35
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IAutomatShiftReportDetailDao {

    public List<AutomatSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AutomatShift shift);

    public List<AutomatSales> find(AutomatSales obj);

    public String exportData(AutomatShift shift, String where);

    public DataSource getDatasource();

    public List<AutomatSales> totals(String where, AutomatShift shift);

}
