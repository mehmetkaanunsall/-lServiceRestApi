/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:25:31 PM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

public interface IStationSaleSummaryReportDao {

    public List<StationSalesSummaryReport> findFuelSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findFuelCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findMarketSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findMarketCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findFuelSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findMarketSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public DataSource getDatasource();

}
