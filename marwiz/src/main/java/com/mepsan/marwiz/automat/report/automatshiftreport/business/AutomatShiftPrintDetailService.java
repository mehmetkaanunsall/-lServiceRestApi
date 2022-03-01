/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:50:50 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.business;

import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.IAutomatShiftPrintDetailDao;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomatShiftPrintDetailService implements IAutomatShiftPrintDetailService {

    @Autowired
    IAutomatShiftPrintDetailDao automatShiftPrintDetailDao;

    public void setAutomatShiftPrintDetailDao(IAutomatShiftPrintDetailDao automatShiftPrintDetailDao) {
        this.automatShiftPrintDetailDao = automatShiftPrintDetailDao;
    }

    @Override
    public List<AutomatShiftReport> listOfPaymentType(AutomatShiftReport obj) {
        return automatShiftPrintDetailDao.listOfPaymentType(obj);
    }

    @Override
    public List<AutomatShiftReport> listOfProduct(AutomatShiftReport obj) {
        return automatShiftPrintDetailDao.listOfProduct(obj);
    }

    @Override
    public List<AutomatShiftReport> listOfPlatform(AutomatShiftReport obj) {
        return automatShiftPrintDetailDao.listOfPlatform(obj);
    }

    @Override
    public String createWhere(AutomatShiftReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AutomatShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return automatShiftPrintDetailDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return automatShiftPrintDetailDao.count(where);
    }

}
