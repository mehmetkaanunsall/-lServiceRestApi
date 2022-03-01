/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.11.2019 08:38:08
 */

package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBank;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IAccountBankDao extends ICrud<AccountBank> {

    public List<AccountBank> findAccountBank(Account account);

    public int delete(AccountBank obj);
}