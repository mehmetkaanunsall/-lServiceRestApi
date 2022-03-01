/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:09:42 AM
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IAccountCardDao;
import com.mepsan.marwiz.general.model.general.AccountCard;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountCardService implements IAccountCardService {

    @Autowired
    IAccountCardDao accountCardDao;

    public void setAccountCardDao(IAccountCardDao accountCardDao) {
        this.accountCardDao = accountCardDao;
    }

    @Override
    public List<AccountCard> findAccountCard(AccountCard accountCard) {
        return accountCardDao.findAccountCard(accountCard);
    }

    @Override
    public int delete(AccountCard obj) {
        return accountCardDao.delete(obj);
    }

    @Override
    public int create(AccountCard obj) {
        return accountCardDao.create(obj);
    }

    @Override
    public int update(AccountCard obj) {
        return accountCardDao.update(obj);
    }

}
