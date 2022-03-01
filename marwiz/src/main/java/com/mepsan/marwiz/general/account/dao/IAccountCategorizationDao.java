/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:11:07
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCategorizationConnection;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.ICategorization;
import com.mepsan.marwiz.general.pattern.ICrud;

public interface IAccountCategorizationDao extends ICrud<AccountCategorizationConnection>, ICategorization<Account> {

    public int allCreat(AccountCategorizationConnection obj, String choseeCategorizations, Item ci);

}
