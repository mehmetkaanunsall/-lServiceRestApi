/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.03.2019 05:39:53
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.business;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import java.util.List;
import java.util.Map;

public interface IAutomatShiftReportDetailService {

    public List<AutomatSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AutomatShift shift);

    public List<AutomatSales> totals(String where, AutomatShift shift);

    public void exportPdf(AutomatShift shift, List<Boolean> toogleList, List<AutomatSales> listOfTotals, String where);

    public void exportExcel(AutomatShift shift, List<Boolean> toogleList, List<AutomatSales> listOfTotals, String where);

    public String exportPrinter(AutomatShift shift, List<Boolean> toogleList, List<AutomatSales> listOfTotals, String where);

    public List<AutomatSales> find(AutomatSales obj);

}
