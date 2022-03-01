/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:51:17 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import javax.sql.DataSource;

public interface IAutomatShiftPrintDetailDao extends ILazyGrid<AutomatShiftReport> {

    public List<AutomatShiftReport> listOfPaymentType(AutomatShiftReport obj);

    public List<AutomatShiftReport> listOfProduct(AutomatShiftReport obj);

    public List<AutomatShiftReport> listOfPlatform(AutomatShiftReport obj);

    public DataSource getDatasource();

}
