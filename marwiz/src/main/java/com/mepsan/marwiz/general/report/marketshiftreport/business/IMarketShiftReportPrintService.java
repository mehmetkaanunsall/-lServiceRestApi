/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   09.02.2018 04:37:23
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IMarketShiftReportPrintService extends ICrudService<Shift> {

    public List<SalePayment> listOfUser(Shift obj);

    public int transferShiftPaymentToMainSafe(int type, Shift obj, boolean isDesc);
}
