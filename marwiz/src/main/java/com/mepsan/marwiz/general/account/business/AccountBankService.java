/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.11.2019 08:34:45
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IAccountBankDao;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountBankService implements IAccountBankService {

    @Autowired
    public IAccountBankDao accountBankDao;

    public void setAccountBankDao(IAccountBankDao accountBankDao) {
        this.accountBankDao = accountBankDao;
    }

    @Override
    public List<AccountBank> findAccountBank(Account account) {
        return accountBankDao.findAccountBank(account);
    }

    @Override
    public int create(AccountBank obj) {
        return accountBankDao.create(obj);
    }

    @Override
    public int update(AccountBank obj) {
        return accountBankDao.update(obj);
    }

    @Override
    public int delete(AccountBank obj) {
        return accountBankDao.delete(obj);
    }

}
