/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.10.2018 09:13:50
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPaymentFinancingDocumentCon;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.IMarketShiftPaymentDao;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.MarketShiftPayment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class MarketShiftPaymentService implements IMarketShiftPaymentService {

    @Autowired
    public IMarketShiftPaymentDao marketShiftPaymentDao;

    public void setMarketShiftPaymentDao(IMarketShiftPaymentDao marketShiftPaymentDao) {
        this.marketShiftPaymentDao = marketShiftPaymentDao;
    }

    @Override
    public List<MarketShiftPayment> listOfShiftPayment(Shift shift, String where) {
        return marketShiftPaymentDao.listOfShiftPayment(shift, where);
    }

    @Override
    public int update(MarketShiftPayment obj) {
        return marketShiftPaymentDao.update(obj);
    }

    @Override
    public int delete(MarketShiftPaymentFinancingDocumentCon marketShiftPaymentCon) {
        return marketShiftPaymentDao.delete(marketShiftPaymentCon);
    }

    @Override
    public int controlOpenShiftPayment() {
        return marketShiftPaymentDao.controlOpenShiftPayment();
    }

    @Override
    public int create(MarketShiftPayment obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ChartItem> chartListForShiftPayment(Shift shift) {
        return marketShiftPaymentDao.chartListForShiftPayment(shift);
    }

    @Override
    public List<ChartItem> chartListForPreviousCompare(Shift shift) {
        return marketShiftPaymentDao.chartListForPreviousCompare(shift);
    }

    @Override
    public int updateShiftPaymentForFinancingDoc(int type, MarketShiftPayment shiftPayment, FinancingDocument obj, int inmovementId, int outmovementId) {
        return marketShiftPaymentDao.updateShiftPaymentForFinancingDoc(type, shiftPayment, obj, inmovementId, outmovementId);
    }

    @Override
    public List<MarketShiftPaymentFinancingDocumentCon> findFinancingDocForShiftPayment(MarketShiftPayment shiftPayment) {
        return marketShiftPaymentDao.findFinancingDocForShiftPayment(shiftPayment);
    }

}
