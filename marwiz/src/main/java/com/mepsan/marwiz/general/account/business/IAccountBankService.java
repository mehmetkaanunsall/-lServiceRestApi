/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.11.2019 08:34:32
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBank;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IAccountBankService extends ICrudService<AccountBank> {

    public List<AccountBank> findAccountBank(Account account);

    public int delete(AccountBank obj);
}
