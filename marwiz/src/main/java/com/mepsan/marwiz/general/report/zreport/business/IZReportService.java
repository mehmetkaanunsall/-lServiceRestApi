/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 6:04:33 PM
 */
package com.mepsan.marwiz.general.report.zreport.business;

import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import com.mepsan.marwiz.general.report.zreport.dao.ZReport;
import java.util.Date;
import java.util.List;

public interface IZReportService extends ICrudService<ZReport>, ILazyGridService<ZReport> {

    public List<ZReport> listOfTaxList(Date beginDate, Date endDate);

    public List<ZReport> listOfCateroyList(Date beginDate, Date endDate);

    public List<ZReport> listOfSalesTypeList(Date beginDate, Date endDate);
    
    public List<ZReport> listOfCashierList(Date beginDate, Date endDate);

    public List<ZReport> listOfStockGroupWithoutCategoies(Date beginDate, Date endDate);

    public List<ZReport> listOfOpenPayment(Date beginDate, Date endDate);

    public List<ZReport> listReceiptCount(Date beginDate, Date endDate);

    public List<ZReport> listReceiptTotal(Date beginDate, Date endDate);

}
