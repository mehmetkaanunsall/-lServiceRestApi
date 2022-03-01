/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   09.02.2018 02:14:21
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IMarketShiftReportPrintDao extends ICrud<SalePayment> {

    public List<SalePayment> listOfUser(Shift obj);

    public int transferShiftPaymentToMainSafe(int type, Shift obj, boolean isDesc);

}
