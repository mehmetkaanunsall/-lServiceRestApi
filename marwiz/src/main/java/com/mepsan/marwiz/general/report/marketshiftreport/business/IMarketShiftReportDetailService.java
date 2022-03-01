/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 10:00:31
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.lowagie.text.pdf.PdfPTable;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface IMarketShiftReportDetailService extends ICrudService<Sales>, ILazyGridService<Sales> {

    public List<Sales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift);

    public int count(String where, Shift shift);

    public List<SaleItem> find(Sales obj);

    public List<SalePayment> listOfSaleType(Sales sales);

    public void exportPdf(Shift shift, List<Boolean> toogleList, List<SalePayment> listOfTotals, String totals, Boolean isStockView, List<SalePayment> listStockDetailOfTotals, int oldId);

    public void exportExcel(Shift shift, List<Boolean> toogleList, List<SalePayment> listOfTotals, String totals, Boolean isStockView, List<SalePayment> listStockDetailOfTotals, int oldId);

    public String exportPrinter(Shift shift, List<Boolean> toogleList, List<SalePayment> listOfTotals, String totals, Boolean isStockView, List<SalePayment> listStockDetailOfTotals, int oldId);

    public List<SalePayment> totals(String where, Shift shift);

    public List<SaleItem> findStockDetailList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift);

    public List<SalePayment> totalsStockDetailList(String where, Shift shift);
    
    

}
