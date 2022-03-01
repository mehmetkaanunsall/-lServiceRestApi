/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:08:21 AM
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.AccountCard;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IAccountCardService extends ICrudService<AccountCard> {

    public List<AccountCard> findAccountCard(AccountCard accountCard);

    public int delete(AccountCard obj);

}
