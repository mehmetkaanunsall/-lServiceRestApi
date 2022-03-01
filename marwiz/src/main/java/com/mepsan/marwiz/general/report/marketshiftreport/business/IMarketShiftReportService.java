/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 03:03:53
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

public interface IMarketShiftReportService extends ICrudService<Shift>, ILazyGridService<Shift> {

    public List<Sales> listOfSalePOS(Shift obj);

    public List<Sales> listOfSaleUser(Shift obj);

    public List<SalePayment> listOfSaleType(Shift obj);

    public void exportPdf(String where, List<Boolean> toogleList);

    public void exportExcel(String where, List<Boolean> toogleList);

    public String exportPrinter(String where, List<Boolean> toogleList);

    public Shift controlShiftPayment(Shift obj);

    public Shift findMarketShift(FinancingDocument financingDocument);
}
