/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.11.2019 11:00:36
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountNote;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IAccountNoteService extends ICrudService<AccountNote> {

    public List<AccountNote> findAccountNote(Account account);

    public int delete(AccountNote obj);

}
