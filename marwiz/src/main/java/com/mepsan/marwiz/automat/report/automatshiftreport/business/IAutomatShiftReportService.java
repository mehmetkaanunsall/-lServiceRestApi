/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:47:56 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.business;

import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

public interface IAutomatShiftReportService extends ICrudService<AutomatShiftReport>, ILazyGridService<AutomatShiftReport> {

    public void exportPdf(String where, List<Boolean> toogleList);

    public void exportExcel(String where, List<Boolean> toogleList);

    public String exportPrinter(String where, List<Boolean> toogleList);

    public List<AutomatSales> listOfSaleStock(AutomatShiftReport obj);

    public List<AutomatSales> listOfSalePlatform(AutomatShiftReport obj);

    public List<AutomatSales> listOfSalePaymentType(AutomatShiftReport obj);

}
