/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IAccountBranchDao;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBranchCon;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class AccountBranchService implements IAccountBranchService{

    @Autowired
    IAccountBranchDao accountBranchDao;
    
    @Override
    public List<AccountBranchCon> findAccountBranchCon(Account account) {
        return accountBranchDao.findAccountBranchCon(account);
    }

    @Override
    public int delete(AccountBranchCon accountBranchCon) {
        return accountBranchDao.delete(accountBranchCon);
    }

    @Override
    public int testBeforeDelete(AccountBranchCon accountBranchCon) {
        return accountBranchDao.testBeforeDelete(accountBranchCon);
    }

    @Override
    public int create(AccountBranchCon obj) {
        return accountBranchDao.create(obj);
    }

    @Override
    public int update(AccountBranchCon obj) {
        return accountBranchDao.update(obj);
    }
    
}
