/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 6:05:22 PM
 */
package com.mepsan.marwiz.general.report.zreport.business;

import com.mepsan.marwiz.general.report.zreport.dao.IZReportDao;
import com.mepsan.marwiz.general.report.zreport.dao.ZReport;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class ZReportService implements IZReportService {

    @Autowired
    IZReportDao zReportDao;

    public void setzReportDao(IZReportDao zReportDao) {
        this.zReportDao = zReportDao;
    }

    @Override
    public int create(ZReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(ZReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ZReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return zReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return zReportDao.count(where);
    }

    @Override
    public List<ZReport> listOfTaxList(Date beginDate, Date endDate) {
        return zReportDao.listOfTaxList(beginDate, endDate);
    }

    @Override
    public List<ZReport> listOfCateroyList(Date beginDate, Date endDate) {
        return zReportDao.listOfCateroyList(beginDate, endDate);
    }

    @Override
    public List<ZReport> listOfSalesTypeList(Date beginDate, Date endDate) {
        return zReportDao.listOfSalesTypeList(beginDate, endDate);
    }

    @Override
    public List<ZReport> listOfStockGroupWithoutCategoies(Date beginDate, Date endDate) {
        return zReportDao.listOfStockGroupWithoutCategoies(beginDate, endDate);
    }

    @Override
    public List<ZReport> listOfOpenPayment(Date beginDate, Date endDate) {
        return zReportDao.listOfOpenPayment(beginDate, endDate);
    }

    @Override
    public List<ZReport> listReceiptCount(Date beginDate, Date endDate) {
        return zReportDao.listReceiptCount(beginDate, endDate);
    }

    @Override
    public List<ZReport> listReceiptTotal(Date beginDate, Date endDate) {
        return zReportDao.listReceiptTotal(beginDate, endDate);
    }

    @Override
    public List<ZReport> listOfCashierList(Date beginDate, Date endDate) {
        return zReportDao.listOfCashierList(beginDate, endDate);
    }

}
