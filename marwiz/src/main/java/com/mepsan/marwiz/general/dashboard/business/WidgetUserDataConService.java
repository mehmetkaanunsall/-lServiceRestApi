/**
 * @author Esra ÇABUK
 * @date 21.06.2018 10:23:12
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.dashboard.dao.IWidgetUserDataConDao;
import com.mepsan.marwiz.general.dashboard.dao.WelcomeWidget;
import com.mepsan.marwiz.general.model.admin.Grid;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WidgetUserDataConService implements IWidgetUserDataConService {

    @Autowired
    private IWidgetUserDataConDao widgetUserDataConDao;

    public void setWidgetUserDataConDao(IWidgetUserDataConDao widgetUserDataConDao) {
        this.widgetUserDataConDao = widgetUserDataConDao;
    }

    @Override
    public List<WidgetUserDataCon> findAll() {
        return widgetUserDataConDao.findAll();
    }

    @Override
    public int create(WidgetUserDataCon obj) {
        return widgetUserDataConDao.create(obj);
    }

    @Override
    public int update(WidgetUserDataCon obj) {
        return widgetUserDataConDao.update(obj);
    }

    @Override
    public List<ChartItem> getMostSoldStocks(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeStock) {
        String where = "";
        where = createWherePriceDifferentSales(type, beginDate, endDate) + createWhere(type, beginDate, endDate);

        return widgetUserDataConDao.getMostSoldStocks(where, isAllBranches, changeStock);
    }

    @Override
    public List<ChartItem> getMostCustomersBySale(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeCustomerPurchases) {
        return widgetUserDataConDao.getMostCustomersBySale(createWhere(type, beginDate, endDate), isAllBranches, changeCustomerPurchases);
    }

    @Override
    public List<ChartItem> getSalesByCategorization(int type, Date beginDate, Date endDate, List<Categorization> categorizationList, boolean isAllBranches, int type2) {
        String where = "", whereLımıt = "";
        String categorizationId = "";
        for (Categorization categorization : categorizationList) {
            categorizationId = categorizationId + "," + String.valueOf(categorization.getId());
            if (categorization.getId() == 0) {
                categorizationId = "";
                break;
            }
        }
        if (!categorizationId.equals("")) {
            categorizationId = categorizationId.substring(1, categorizationId.length());
            where = where + "AND scc.categorization_id IN(" + categorizationId + ") ";
        } else {
            whereLımıt = "LIMIT 5";
        }

        where = where + createWherePriceDifferentSales(type, beginDate, endDate);

        return widgetUserDataConDao.getSalesByCategorization(createWhere(type, beginDate, endDate), where, whereLımıt, isAllBranches, type2);
    }

    @Override
    public List<ChartItem> getSalesByBrand(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeBrand) {
        return widgetUserDataConDao.getSalesByBrand(createWhere(type, beginDate, endDate), isAllBranches, changeBrand);
    }

    @Override
    public List<ChartItem> getSalesByPumper(int type, Date beginDate, Date endDate, boolean isAllBranches, int changePumper) {
        return widgetUserDataConDao.getSalesByPumper(createWherePumperSales(type, beginDate, endDate), isAllBranches, changePumper);
    }

    @Override
    public List<ChartItem> getDecreasingStocks(boolean isAllBranches) {
        return widgetUserDataConDao.getDecreasingStocks(isAllBranches);
    }

    @Override
    public List<ChartItem> getSalesByCashier(int type, Date beginDate, Date endDate, boolean isAllBranches, int changeCashier) {
        return widgetUserDataConDao.getSalesByCashier(createWhere(type, beginDate, endDate), isAllBranches, changeCashier);
    }

    @Override
    public List<ChartItem> getSalesBySaleType(int type, Date beginDate, Date endDate, boolean isAllBranches) {
        return widgetUserDataConDao.getSalesBySaleType(createWhere(type, beginDate, endDate), isAllBranches);
    }

    @Override
    public List<ChartItem> getReturnedStock(boolean isAllBranches) {
        return widgetUserDataConDao.getReturnedStock(isAllBranches);
    }

    @Override
    public List<ChartItem> getFuelStock(boolean isAllBranches) {
        return widgetUserDataConDao.getFuelStock(isAllBranches);
    }

    @Override
    public List<ChartItem> getRecorveries(boolean isAllBranches) {
        return widgetUserDataConDao.getRecoveries(isAllBranches);
    }

    @Override
    public List<WelcomeWidget> getWelcome(boolean isAllBranches) {
        return widgetUserDataConDao.getWelcome(isAllBranches);
    }

    @Override
    public int delete(WidgetUserDataCon obj) {
        return widgetUserDataConDao.delete(obj);
    }

    public String createWhere(int type, Date beginDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        switch (type) {
            case 1:
                //Günlük
                where = " AND date_part('doy',  sl.processdate) =date_part('doy',  CURRENT_DATE) AND date_part('year',  sl.processdate) =date_part('year',  CURRENT_DATE) ";
                break;
            case 2:
                //Haftalık
                where = " AND date_part('week',  sl.processdate) =date_part('week',  CURRENT_DATE) AND date_part('year',  sl.processdate) =date_part('year',  CURRENT_DATE) ";
                break;
            case 3:
                //Aylık
                where = " AND date_part('month',  sl.processdate) =date_part('month',  CURRENT_DATE) AND date_part('year',  sl.processdate) =date_part('year',  CURRENT_DATE) ";
                break;
            case 4:
                //Aylık
                where = " AND sl.processdate BETWEEN '" + dateFormat.format(beginDate) + "' AND '" + dateFormat.format(endDate) + "' ";
                break;
            default:
                break;
        }
        return where;
    }

    public String createWhereDuePayments(int type, Date beginDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";
        switch (type) {

            case 1: //Günlük
                where = "AND date_part('doy',inv.duedate) = date_part('doy',CURRENT_DATE) AND date_part('year',inv.duedate) =date_part('year',CURRENT_DATE)";
                break;

            case 2: // Haftalık
                where = "AND date_part('week',inv.duedate) = date_part('week',CURRENT_DATE) AND date_part('year',inv.duedate) = date_part('year',CURRENT_DATE)";
                break;

            case 3: // Aylık
                where = "AND date_part('month',inv.duedate) = date_part('month',CURRENT_DATE) AND date_part('year', inv.duedate) = date_part ('year',CURRENT_DATE)";
                break;

            case 4: //Aylık
                where = " AND inv.duedate BETWEEN '" + dateFormat.format(beginDate) + "' AND '" + dateFormat.format(endDate) + "' ";
                break;
        }
        return where;
    }

    public String createWherePumperSales(int type, Date beginDate, Date endDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        switch (type) {
            case 1:
                //Günlük
                where = "AND date_part('doy',ssl.processdate) = date_part('doy',CURRENT_DATE) AND date_part ('year',ssl.processdate) = date_part ('year',CURRENT_DATE)";
                break;
            case 2: // Haftalık
                where = "AND date_part('week',ssl.processdate) = date_part('week',CURRENT_DATE) AND date_part('year',ssl.processdate) = date_part('year',CURRENT_DATE)";
                break;

            case 3: // Aylık
                where = "AND date_part('month',ssl.processdate) = date_part('month',CURRENT_DATE) AND date_part('year', ssl.processdate) = date_part ('year',CURRENT_DATE)";
                break;

            case 4: //Aylık
                where = " AND ssl.processdate BETWEEN '" + dateFormat.format(beginDate) + "' AND '" + dateFormat.format(endDate) + "' ";
                break;

        }
        return where;
    }

    public String createWherePriceDifferentSales(int type, Date beginDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        switch (type) {
            case 1:
                //Günlük
                where = where + " AND (CASE WHEN sli.is_calcincluded = TRUE AND date_part('doy',  sl.differentdate) =date_part('doy',  CURRENT_DATE) AND date_part('year',  sl.differentdate) =date_part('year',  CURRENT_DATE) THEN FALSE ELSE TRUE END)\n";
                break;
            case 2:
                //Haftalık
                where = where + " AND (CASE WHEN sli.is_calcincluded = TRUE AND date_part('week',  sl.differentdate) =date_part('week',  CURRENT_DATE) AND date_part('year',  sl.differentdate) =date_part('year',  CURRENT_DATE) THEN FALSE ELSE TRUE END)\n";
                break;
            case 3:
                //Aylık
                where = where + " AND (CASE WHEN sli.is_calcincluded = TRUE AND date_part('month',  sl.differentdate) =date_part('month',  CURRENT_DATE) AND date_part('year',  sl.differentdate) =date_part('year',  CURRENT_DATE) THEN FALSE ELSE TRUE END)\n";
                break;
            case 4:
                //Aylık
                where = where + " AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + dateFormat.format(beginDate) + "' AND '" + dateFormat.format(endDate) + "' THEN FALSE ELSE TRUE END)\n";
                break;
            default:
                break;
        }
        return where;
    }

    @Override
    public Grid getPageGrid(int pageId) {
//        for (Grid grid : applicationBean.getGridMap().values()) {
//            if (grid.getPageId() == pageId) {
//                return grid;
//            }
//        }
        return null;
    }

    @Override
    public List<ChartItem> getPayments(boolean isAllBranches) {
        return widgetUserDataConDao.getPayments(isAllBranches);
    }

    @Override
    public List<ChartItem> getWeeklyCashFlow(boolean isAllBranches) {
        return widgetUserDataConDao.getWeeklyCashFlow(isAllBranches);
    }

    @Override
    public List<ChartItem> getPricesVaryingProducts(boolean isAllBranches) {
        return widgetUserDataConDao.getPricesVaryingProducts(isAllBranches);
    }

    @Override
    public List<ChartItem> getPurchasePriceHighProducts(boolean isAllBranches) {
        return widgetUserDataConDao.getPurchasePriceHighProducts(isAllBranches);
    }

    @Override
    public List<ChartItem> getProductProfitalibility(boolean isAllBranches) {
        return widgetUserDataConDao.getProductProfitalibility(isAllBranches);
    }

    @Override
    public List<ChartItem> getStationBySalesForWashingMachicne(Date beginDate, Date endDate, boolean isAllBranches) {
        return widgetUserDataConDao.getStationBySalesForWashingMachicne(beginDate, endDate, isAllBranches);
    }

    @Override
    public List<ChartItem> getWashingSalesByQuantity(Date beginDate, Date endDate, boolean isAllBranches) {
        return widgetUserDataConDao.getWashingSalesByQuantity(beginDate, endDate, isAllBranches);
    }

    @Override
    public List<ChartItem> getWashingSalesByTurnover(Date beginDate, Date endDate, boolean isAllBranches) {
        return widgetUserDataConDao.getWashingSalesByTurnover(beginDate, endDate, isAllBranches);
    }

    @Override
    public List<ChartItem> getWashingSales(Date beginDate, Date endDate, int washingSales, boolean isAllBranches) {
        return widgetUserDataConDao.getWashingSales(beginDate, endDate, washingSales, isAllBranches);
    }

    @Override
    public List<ChartItem> getDuePayments(int type, Date beginDate, Date endDate, boolean isAllBranches) {
        return widgetUserDataConDao.getDuePayments(createWhereDuePayments(type, beginDate, endDate), isAllBranches);
    }

    @Override
    public List<ChartItem> getFuelShiftSales(int type, Date beginDate, Date endDate, boolean isAllBranches) {
        return widgetUserDataConDao.getFuelShiftSales(createWhere(type, beginDate, endDate), isAllBranches);
    }

    @Override
    public List<ChartItem> getLazyPricesVaryingProductsList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches) {
        return widgetUserDataConDao.getLazyPricesVaryingProductsList(first, pageSize, sortField, sortOrder, filters, isAllBranches);
    }

    @Override
    public int countPriceVarying(boolean isAllBranches) {
        return widgetUserDataConDao.countPriceVarying(isAllBranches);
    }

    @Override
    public List<ChartItem> tempProductProfitalibility(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches) {
        return widgetUserDataConDao.tempProductProfitalibility(first, pageSize, sortField, sortOrder, filters, isAllBranches);
    }

    @Override
    public int count(boolean isAllBranches) {
        return widgetUserDataConDao.count(isAllBranches);
    }
}
