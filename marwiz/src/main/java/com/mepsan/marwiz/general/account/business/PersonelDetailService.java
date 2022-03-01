/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IPersonelDetailDao;
import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author samet.dag
 */
public class PersonelDetailService implements IPersonelDetailService {

    @Autowired
    public IPersonelDetailDao personelDetailDao;

    public void setPersonelDetailDao(IPersonelDetailDao personelDetailDao) {
        this.personelDetailDao = personelDetailDao;
    }

    @Override
    public int update(String integrationcode, BigDecimal exactsalary, int agi, Date startDate, Date endDate, int accountId) {
        return personelDetailDao.update(integrationcode, exactsalary, agi, startDate, endDate, accountId);
    }

    @Override
    public EmployeeInfo find(int accountId) {
        return personelDetailDao.find(accountId);
    }

}
