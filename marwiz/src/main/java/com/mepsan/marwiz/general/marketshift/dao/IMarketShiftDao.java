/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2018 10:41:40
 */
package com.mepsan.marwiz.general.marketshift.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IMarketShiftDao extends ICrud<Shift>, ILazyGrid<Shift> {

    public Shift controlHaveOpenShift();

    public int delete(Shift shift);

    public int updateShift(Shift shift);

    public DataSource getDatasource();

    public String shiftStockDetail(Shift shift);

    public List<MarketShiftPreview> shiftStockDetailList(Shift shift);

    public String shiftTaxRateDetail(Shift shift);

    public List<MarketShiftPreview> shiftStockGroupList(Shift shift);

    public List<MarketShiftPreview> shiftStockGroupDetail(Shift shift);

    public List<MarketShiftPreview> shiftTaxRateDetailList(Shift shift);

    public String shiftCurrencyDetail(Shift shift);

    public List<MarketShiftPreview> shiftCurrencyDetailList(Shift shift);

    public String shiftCashierPaymentCashDetail(Shift shift);

    public List<MarketShiftPreview> shiftCashierPaymentCashDetailList(Shift shift);

    public String shiftCashierPaymentBankDetail(Shift shift);

    public List<MarketShiftPreview> shiftCashierPaymentBankDetailList(Shift shift);

    public String shiftDeficitGiveMoney(Shift shift);

    public List<MarketShiftPreview> shiftDeficitGiveMoneyList(Shift shift);

    public String shiftDeficitGiveMoneyEmployee(Shift shift);

    public List<MarketShiftPreview> shiftDeficitGiveMoneyEmployeeList(Shift shift);

    public String shiftSummary(Shift shift);

    public List<MarketShiftPreview> shiftSummaryList(Shift shift);

    public String shiftGeneral(Shift shift);

    public List<MarketShiftPreview> shiftGeneralList(Shift shift);

    public String shiftCreditPaymentDetail(Shift shift);

    public List<MarketShiftPreview> shiftCreditPaymentDetailList(Shift shift);

    public List<Invoice> controlOpenAmountInvoice(Shift shift);

    public int controlReopenShift(Shift shift);

    public int reopenShift(Shift shift);

    public List<MarketShiftPreview> shiftStockGroupListWithoutCategories(Shift shift);

    public String shiftStockGroupDetailWithoutCategories(Shift shift);

    public List<Shift> shiftBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int shiftBookCount(String where, String type, List<Object> param);

    public List<PointOfSale> listPointOfSale();

    public int create(Shift obj, Boolean isOfflineControl);

    public String lastUserShiftReport();

    public String updateLastUserShiftReport(String str);

    public List<MarketShiftPreview> shiftAccountGroupList(Shift shift);

    public List<MarketShiftPreview> shiftSafeTransferList(Shift shift);

    public boolean controlIsCheck();

    public String updateIsCheck(Shift shift);
}
