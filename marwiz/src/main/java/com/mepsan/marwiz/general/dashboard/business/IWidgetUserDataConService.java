/**
 *
 *
 *
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.dashboard.dao.WelcomeWidget;
import com.mepsan.marwiz.general.model.admin.Grid;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IWidgetUserDataConService extends ICrudService<WidgetUserDataCon> {

    public List<WidgetUserDataCon> findAll();

    public List<ChartItem> getMostSoldStocks(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeStock);

    public Grid getPageGrid(int pageId);

    public List<ChartItem> getMostCustomersBySale(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeCustomerPurchases);

    public List<ChartItem> getSalesByCategorization(int type, Date beginDate, Date endDate, List<Categorization> categorizations, boolean isAllBranches, int type2);

    public List<ChartItem> getSalesByBrand(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeBrand);

    public List<ChartItem> getSalesByPumper(int type, Date beginDate, Date endDate, boolean isAllBranches, int changePumper);

    public List<ChartItem> getDecreasingStocks(boolean isAllBranches);

    public List<ChartItem> getSalesByCashier(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeCashier);

    public List<ChartItem> getSalesBySaleType(int type, Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getDuePayments(int type, Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getFuelShiftSales(int type, Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getReturnedStock(boolean isAllBranches);

    public List<ChartItem> getFuelStock(boolean isAllBranches);

    public List<ChartItem> getRecorveries(boolean isAllBranches);

    public List<ChartItem> getPayments(boolean isAllBranches);

    public List<ChartItem> getWeeklyCashFlow(boolean isAllBranches);

    public List<ChartItem> getPricesVaryingProducts(boolean isAllBranches);

    public List<ChartItem> getPurchasePriceHighProducts(boolean isAllBranches);

    public List<ChartItem> getStationBySalesForWashingMachicne(Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getWashingSalesByQuantity(Date beginDate, Date endDate, boolean isAllBranches);

    public List<WelcomeWidget> getWelcome(boolean isAllBranches);

    public int delete(WidgetUserDataCon obj);

    public List<ChartItem> getProductProfitalibility(boolean isAllBranches);

    public List<ChartItem> getWashingSalesByTurnover(Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getWashingSales(Date beginDate, Date endDate, int washingSales, boolean isAllBranches);

    public List<ChartItem> getLazyPricesVaryingProductsList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches);

    public int countPriceVarying(boolean isAllBranches);
    
    public List<ChartItem> tempProductProfitalibility(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters , boolean isAllBranches);

    public int count(boolean isAllBranches);

}
