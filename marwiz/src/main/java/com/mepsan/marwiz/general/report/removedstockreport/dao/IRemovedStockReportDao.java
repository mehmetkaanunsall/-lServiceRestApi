/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 09:14:16
 */
package com.mepsan.marwiz.general.report.removedstockreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.log.RemovedStock;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedShift;
import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedStockReport;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

public interface IRemovedStockReportDao {

    public List<RemovedStockReport> listOfMonthlyLog(Date date, String branchList);

    public List<RemovedStock> listOfLog(Date beginDate,Date endDate, UserData userData, Branch branch);

    public List<ChartItem> yearlyRemovedStock(Date date, String branchList);

    public List<ChartItem> monthlyRemovedStock(Date beginDate, Date EndDate, String branchList);

    public List<ChartItem> dailyRemovedStock(String branchList);
    
    public List<ChartItem> weeklyRemovedStock(Date beginDate, Date EndDate, String branchList);


    public List<Shift> listOfShift(Date date);
    
      public String exportData();
    
    public DataSource getDatasource();
    
    public List<RemovedStock> listOfRemovedStockForMarketShift(Shift shift);

}
