/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.11.2019 11:02:45
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IAccountNoteDao;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountNote;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountNoteService implements IAccountNoteService {

    @Autowired
    public IAccountNoteDao accountNoteDao;

    public void setAccountNoteDao(IAccountNoteDao accountNoteDao) {
        this.accountNoteDao = accountNoteDao;
    }

    @Override
    public List<AccountNote> findAccountNote(Account account) {
       return accountNoteDao.findAccountNote(account);
    }

    @Override
    public int delete(AccountNote obj) {
       return accountNoteDao.delete(obj);
    }

    @Override
    public int create(AccountNote obj) {
       return accountNoteDao.create(obj);
    }

    @Override
    public int update(AccountNote obj) {
        return accountNoteDao.update(obj);
    }

}
