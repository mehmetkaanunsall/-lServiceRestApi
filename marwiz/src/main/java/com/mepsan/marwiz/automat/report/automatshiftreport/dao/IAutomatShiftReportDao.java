/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:46:59 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import javax.sql.DataSource;

public interface IAutomatShiftReportDao extends ILazyGrid<AutomatShiftReport> {

    public DataSource getDatasource();

    public String exportData(String where);

     public List<AutomatSales> listOfSaleStock(AutomatShiftReport obj);

    public List<AutomatSales> listOfSalePlatform(AutomatShiftReport obj);

    public List<AutomatSales> listOfSalePaymentType(AutomatShiftReport obj);

}
