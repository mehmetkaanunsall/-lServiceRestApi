/**
 * @author Esra ÇABUK
 * @date 21.06.2018 10:23:12
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IWidgetUserDataConDao extends ICrud<WidgetUserDataCon> {

    public List<WidgetUserDataCon> findAll();

    public List<ChartItem> getMostSoldStocks(String where, boolean isAllBranches, int changeStock);

    public List<ChartItem> getMostCustomersBySale(String where, boolean isAllBranches, int changeCustomerPurchases);

    public List<ChartItem> getSalesByCategorization(String whereChange, String where, String whereLımıt, boolean isAllBranches, int type2);

    public List<ChartItem> getSalesByBrand(String where, boolean isAllBranches, int changeBrand);

    public List<ChartItem> getSalesByPumper(String where, boolean isAllBranches, int changePumper);

    public List<ChartItem> getDecreasingStocks(boolean isAllBranches);

    public List<ChartItem> getSalesByCashier(String where, boolean isAllBranches, int changeCashier);

    public List<ChartItem> getSalesBySaleType(String where, boolean isAllBranches);

    public List<ChartItem> getDuePayments(String where, boolean isAllBranches);

    public List<ChartItem> getFuelShiftSales(String where, boolean isAllBranches);

    public List<ChartItem> getReturnedStock(boolean isAllBranches);

    public List<ChartItem> getFuelStock(boolean isAllBranches);

    public List<WelcomeWidget> getWelcome(boolean isAllBranches);

    public int delete(WidgetUserDataCon obj);

    public List<ChartItem> getRecoveries(boolean isAllBranches);

    public List<ChartItem> getPayments(boolean isAllBranches);

    public List<ChartItem> getWeeklyCashFlow(boolean isAllBranches);

    public List<ChartItem> getPricesVaryingProducts(boolean isAllBranches);

    public List<ChartItem> getPurchasePriceHighProducts(boolean isAllBranches);

    public List<ChartItem> getProductProfitalibility(boolean isAllBranches);

    public List<ChartItem> getStationBySalesForWashingMachicne(Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getWashingSalesByQuantity(Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getWashingSalesByTurnover(Date beginDate, Date endDate, boolean isAllBranches);

    public List<ChartItem> getWashingSales(Date beginDate, Date endDate, int washingSales, boolean isAllBranches);

    public List<ChartItem> getLazyPricesVaryingProductsList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches);

    public int countPriceVarying(boolean isAllBranches);

    public List<ChartItem> tempProductProfitalibility(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches);

    public int count(boolean isAllBranches);

}
