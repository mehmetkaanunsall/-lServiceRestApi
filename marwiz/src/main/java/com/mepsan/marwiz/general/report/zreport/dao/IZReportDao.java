/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:38:32 PM
 */
package com.mepsan.marwiz.general.report.zreport.dao;

import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.Date;
import java.util.List;

public interface IZReportDao extends ICrud<ZReport>, ILazyGrid<ZReport> {

    public List<ZReport> listOfTaxList(Date beginDate, Date endDate);

    public List<ZReport> listOfCateroyList(Date beginDate, Date endDate);

    public List<ZReport> listOfSalesTypeList(Date beginDate, Date endDate);
    
    public List<ZReport> listOfCashierList(Date beginDate, Date endDate);

    public List<ZReport> listOfStockGroupWithoutCategoies(Date beginDate, Date endDate);

    public List<ZReport> listOfOpenPayment(Date beginDate, Date endDate);

    public List<ZReport> listReceiptCount(Date beginDate, Date endDate);

    public List<ZReport> listReceiptTotal(Date beginDate, Date endDate);

}
