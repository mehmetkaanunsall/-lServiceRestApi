/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 17:16:21
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.inventory.stock.dao.IStockAnalysisDao;
import com.mepsan.marwiz.inventory.stock.dao.StockAnalysis;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockAnalysisService implements IStockAnalysisService {

    @Autowired
    private IStockAnalysisDao stockAnalysisDao;

    public void setStockAnalysisDao(IStockAnalysisDao stockAnalysisDao) {
        this.stockAnalysisDao = stockAnalysisDao;
    }

    @Override
    public StockAnalysis selectStockAnalysis(Stock stock, Branch branch) {
        return stockAnalysisDao.selectStockAnalysis(stock, branch);
    }

    @Override
    public List<StockAnalysis> listOfMonthAverage(Stock stock, Branch branch) {
        List<StockAnalysis> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= day; i++) {
            StockAnalysis sa = new StockAnalysis();
            sa.setDay(i);
            sa.setLastMonth(BigDecimal.ZERO);
            list.add(sa);
        }
        List<StockAnalysis> list2 = stockAnalysisDao.listOfMonthAverage(stock, branch);

        for (int j = 0; j < list2.size(); j++) {
            for (int i = 0; i < list.size(); i++) {
                if (list2.get(j).getDay() == list.get(i).getDay()) {
                    list.get(i).setLastMonth(list2.get(j).getLastMonth());
                }
            }
        }

        return list;
    }

    @Override
    public List<StockAnalysis> listOfThreeMonthAverage(Stock stock, Branch branch) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        List<StockAnalysis> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            StockAnalysis sa = new StockAnalysis();
            sa.setMonth(calendar.get(Calendar.MONTH) + 1);
            sa.setYear(calendar.get(Calendar.YEAR));
            sa.setLastMonth(BigDecimal.ZERO);
            list.add(sa);

            calendar.add(Calendar.MONTH, -1);
        }

        List<StockAnalysis> list2 = stockAnalysisDao.listOfThreeMonthAverage(stock, branch);

        for (StockAnalysis sa : list) {
            for (StockAnalysis sa2 : list2) {
                if (sa.getMonth() == sa2.getMonth()) {
                    sa.setLastMonth(sa2.getLastMonth());
                }
            }
        }

        return list;

    }

    @Override
    public List<StockAnalysis> listOfYearAverage(Stock stock, Branch branch) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());


        List<StockAnalysis> list = stockAnalysisDao.listOfYearAverage(stock, branch);

        return list;
    }
}
