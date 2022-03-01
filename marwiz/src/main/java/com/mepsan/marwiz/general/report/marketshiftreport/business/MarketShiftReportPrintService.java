/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   09.02.2018 04:38:25
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.IMarketShiftReportPrintDao;

public class MarketShiftReportPrintService implements IMarketShiftReportPrintService {

    @Autowired
    public IMarketShiftReportPrintDao marketShiftReportPrintDao;

    public void setMarketShiftReportPrintDao(IMarketShiftReportPrintDao marketShiftReportPrintDao) {
        this.marketShiftReportPrintDao = marketShiftReportPrintDao;
    }

    @Override
    public List<SalePayment> listOfUser(Shift obj) {
        return marketShiftReportPrintDao.listOfUser(obj);
    }

    @Override
    public int transferShiftPaymentToMainSafe(int type, Shift obj, boolean isDesc) {
        return marketShiftReportPrintDao.transferShiftPaymentToMainSafe(type, obj, isDesc);
    }

    @Override
    public int create(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
