package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date 12.02.2018 10:21:29
 */
public interface IMarketShiftReportDetailDao extends ICrud<Sales>, ILazyGrid<Sales> {

    public List<Sales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift);

    public int count(String where, Shift shift);

    public List<SaleItem> find(Sales obj);

    public List<SalePayment> listOfSaleType(Sales sales);

    public String exportData(Shift shift);

    public DataSource getDatasource();

    public List<SalePayment> totals(String where, Shift shift);

    public List<SaleItem> findStockDetailList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift);

    public List<SalePayment> totalsStockDetailList(String where, Shift shift);
    
     public String exportDataStockDetail(Shift shift);

}
