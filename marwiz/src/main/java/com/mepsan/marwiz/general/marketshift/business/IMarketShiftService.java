/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2018 10:39:31
 */
package com.mepsan.marwiz.general.marketshift.business;

import com.mepsan.marwiz.general.marketshift.dao.ErrorOfflinePos;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPreview;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IMarketShiftService extends ICrudService<Shift>, ILazyGridService<Shift> {

    public Shift controlHaveOpenShift();

    public int updateShift(Shift shift);

    public int delete(Shift shift);

    public void createExcelFile(Shift shift, String totalShiftAmount, List<String> selectedOptions);

    public void createPdfFile(Shift shift, String totalShiftAmount, List<String> selectedOptions);

    public List<Invoice> controlOpenAmountInvoice(Shift shift);

    public List<MarketShiftPreview> shiftStockDetailList(Shift shift);

    public List<MarketShiftPreview> shiftStockGroupList(Shift shift);

    public List<MarketShiftPreview> shiftTaxRateDetailList(Shift shift);

    public List<MarketShiftPreview> shiftCurrencyDetailList(Shift shift);

    public List<MarketShiftPreview> shiftCashierPaymentCashDetailList(Shift shift);

    public List<MarketShiftPreview> shiftCashierPaymentBankDetailList(Shift shift);

    public List<MarketShiftPreview> shiftDeficitGiveMoneyList(Shift shift);

    public List<MarketShiftPreview> shiftDeficitGiveMoneyEmployeeList(Shift shift);

    public List<MarketShiftPreview> shiftSummaryList(Shift shift);

    public List<MarketShiftPreview> shiftGeneralList(Shift shift);

    public List<MarketShiftPreview> shiftCreditPaymentDetailList(Shift shift);

    public Shift findShift(Shift obj);

    public int controlReopenShift(Shift shift);

    public int reopenShift(Shift shift);

    public List<MarketShiftPreview> shiftStockGroupListWithoutCategories(Shift shift);

    public List<Shift> shiftBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int shiftBookCount(String where, String type, List<Object> param);

    public List<PointOfSale> listPointOfSale();

    public List<ErrorOfflinePos> controlOfflinePos(List<PointOfSale> listOfPos);

    public int create(Shift obj, Boolean isOfflineControl);

    public void transferShiftAndPos(List<PointOfSale> listOfPos);

    public String lastUserShiftReport();

    public String updateLastUserShiftReport(String str);

    public String createWhere(Date beginDate, Date endDate);

    public List<MarketShiftPreview> shiftAccountGroupList(Shift shift);

    public List<MarketShiftPreview> shiftSafeTransferList(Shift shift);

    public boolean controlIsCheck();

    public String updateIsCheck(Shift shift);
}
