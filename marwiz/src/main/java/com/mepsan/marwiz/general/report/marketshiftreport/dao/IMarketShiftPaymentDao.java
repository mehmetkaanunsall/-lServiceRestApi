/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.10.2018 08:57:01
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPaymentFinancingDocumentCon;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IMarketShiftPaymentDao extends ICrud<MarketShiftPayment> {

    public List<MarketShiftPayment> listOfShiftPayment(Shift shift, String where);

    public int delete(MarketShiftPaymentFinancingDocumentCon marketShiftPaymentCon);

    public int controlOpenShiftPayment();

    public List<ChartItem> chartListForShiftPayment(Shift shift);

    public List<ChartItem> chartListForPreviousCompare(Shift shift);

    public int updateShiftPaymentForFinancingDoc(int type, MarketShiftPayment shiftPayment, FinancingDocument obj, int inmovementId, int outmovementId);

    public List<MarketShiftPaymentFinancingDocumentCon> findFinancingDocForShiftPayment(MarketShiftPayment shiftPayment);

}
