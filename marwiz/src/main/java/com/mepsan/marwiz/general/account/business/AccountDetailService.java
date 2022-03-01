/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IAccountDetailDao;
import com.mepsan.marwiz.general.model.general.AccountInfo;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author samet.dag
 */
public class AccountDetailService implements IAccountDetailService {

    @Autowired
    public IAccountDetailDao accountDetailDao;

    public void setAccountDetailDao(IAccountDetailDao accountDetailDao) {
        this.accountDetailDao = accountDetailDao;
    }

    @Override
    public int update(String fuelintegrationcode, String accountingintegrationcode, int accountId) {
        return accountDetailDao.update(fuelintegrationcode, accountingintegrationcode, accountId);
    }

    @Override
    public AccountInfo find(int accountId) {
        return accountDetailDao.find(accountId);
    }

}
