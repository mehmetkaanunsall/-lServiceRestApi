/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 03:04:39
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.sql.ResultSet;
import java.util.List;
import javax.sql.DataSource;

public interface IMarketShiftReportDao extends ICrud<Shift>, ILazyGrid<Shift> {

    public List<Sales> listOfSalePOS(Shift obj);

    public List<Sales> listOfSaleUser(Shift obj);

    public List<SalePayment> listOfSaleType(Shift obj);

    public String exportData(String where);

    public DataSource getDatasource();

    public Shift controlShiftPayment(Shift obj);
    
    public Shift findMarketShift(FinancingDocument financingDocument);
    
}
