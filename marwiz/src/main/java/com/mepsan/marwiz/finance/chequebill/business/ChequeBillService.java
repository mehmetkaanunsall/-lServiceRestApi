/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.business;

import com.mepsan.marwiz.finance.chequebill.dao.IChequeBillDao;
import com.mepsan.marwiz.finance.chequebill.presentation.ChequeBillBean.ChequeBillParam;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class ChequeBillService implements IChequeBillService {

    @Autowired
    private IChequeBillDao chequeBillDao;

    public void setChequeBillDao(IChequeBillDao chequeBillDao) {
        this.chequeBillDao = chequeBillDao;
    }

    @Override
    public String createWhere(ChequeBillParam searchObject, List<Branch> listOfBranch) {
        String where = "";
        String branchs = "";
        where = where + " AND cqb.branch_id IN (";
        if (!searchObject.getSelectedBranchList().isEmpty()) {
            for (Branch br : searchObject.getSelectedBranchList()) {
                branchs = branchs + br.getId() + ",";
            }
        } else {
            for (Branch br : listOfBranch) {
                branchs = branchs + br.getId() + ",";
            }
        }
        branchs = branchs.substring(0, branchs.length() - 1);
        where = where + branchs + ") ";
        return where;
    }

    @Override
    public List<ChequeBill> findAll(int chequeBillType, String where) {
        return chequeBillDao.findAll(chequeBillType, where);
    }

    @Override
    public int create(ChequeBill obj) {
        return chequeBillDao.create(obj);
    }

    @Override
    public int update(ChequeBill obj) {
        return chequeBillDao.update(obj);
    }

    @Override
    public ChequeBill findChequeBill(ChequeBill obj) {
        List<ChequeBill> list = chequeBillDao.findAll(0, " AND cqb.id = " + obj.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new ChequeBill();
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(ChequeBill chequeBill) {
        return chequeBillDao.testBeforeDelete(chequeBill);
    }

    @Override
    public int delete(ChequeBill chequeBill) {
        return chequeBillDao.delete(chequeBill);
    }

}
