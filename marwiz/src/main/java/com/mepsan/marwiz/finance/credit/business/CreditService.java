/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.business;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.credit.dao.ICreditDao;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Gozde Gursel
 */
public class CreditService implements ICreditService {

    @Autowired
    private ICreditDao creditDao;

    public void setCreditDao(ICreditDao creditDao) {
        this.creditDao = creditDao;
    }

    @Override
    public String createWhere(Branch branch) {
        String where = "";
        if (branch.getId() != 0) {
            where = " AND crdt.branch_id = " + branch.getId() + " ";
        }
        return where;
    }

    @Override
    public int create(CreditReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(CreditReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CreditReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return creditDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return creditDao.count(where);
    }

    @Override
    public CreditReport findCreditReport(CreditReport obj) {
        Map<String, Object> filt = new HashMap<>();

        List<CreditReport> list = creditDao.findAll(0, 10, "crdt.id", "ASC", filt, " AND crdt.id = " + obj.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CreditReport();
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(CreditReport credit) {
        return creditDao.testBeforeDelete(credit);
    }

    @Override
    public int delete(CreditReport credit) {
        return creditDao.delete(credit);
    }

    @Override
    public List<CreditReport> findShiftCredit(Date beginDate, Date endDate) {
        return creditDao.findShiftCredit(beginDate, endDate);
    }

}
