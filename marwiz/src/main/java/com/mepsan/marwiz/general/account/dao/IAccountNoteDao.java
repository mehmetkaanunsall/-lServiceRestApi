/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.11.2019 11:03:19
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountNote;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IAccountNoteDao extends ICrud<AccountNote> {

    public List<AccountNote> findAccountNote(Account account);

    public int delete(AccountNote obj);

}
