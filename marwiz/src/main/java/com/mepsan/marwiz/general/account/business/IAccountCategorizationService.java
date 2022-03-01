/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:10:53
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCategorizationConnection;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.ICategorizationService;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IAccountCategorizationService extends ICrudService<AccountCategorizationConnection>,  ICategorizationService<Account, AccountCategorizationConnection> {

    public int allCreat(AccountCategorizationConnection obj, List<AccountCategorizationConnection> choseeCategorizations, Item ci);

}
