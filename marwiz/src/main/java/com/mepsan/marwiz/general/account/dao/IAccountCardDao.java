/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:10:28 AM
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.AccountCard;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IAccountCardDao extends ICrud<AccountCard> {

    public List<AccountCard> findAccountCard(AccountCard accountCard);

    public int delete(AccountCard obj);

}
