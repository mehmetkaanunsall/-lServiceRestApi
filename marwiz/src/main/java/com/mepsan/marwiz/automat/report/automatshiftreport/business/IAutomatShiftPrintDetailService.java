/**
 * 
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:49:51 PM
 */

package com.mepsan.marwiz.automat.report.automatshiftreport.business;

import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.general.pattern.IReportService;
import java.util.List;


public interface IAutomatShiftPrintDetailService extends IReportService<AutomatShiftReport> {

    public List<AutomatShiftReport> listOfPaymentType(AutomatShiftReport obj);

    public List<AutomatShiftReport> listOfProduct(AutomatShiftReport obj);

    public List<AutomatShiftReport> listOfPlatform(AutomatShiftReport obj);

}