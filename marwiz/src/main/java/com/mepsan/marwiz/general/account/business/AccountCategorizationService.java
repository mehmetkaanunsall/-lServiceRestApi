/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:10:59
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.AccountCategorizationDao;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCategorizationConnection;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountCategorizationService implements IAccountCategorizationService {

    @Autowired
    private AccountCategorizationDao accountCategorizationDao;

    public void setAccountCategorizationDao(AccountCategorizationDao accountCategorizationDao) {
        this.accountCategorizationDao = accountCategorizationDao;
    }

    @Override
    public List<Categorization> listCategorization(Account obj, Item ci) {
        return accountCategorizationDao.listCategorization(obj, ci);
    }

    @Override
    public int create(AccountCategorizationConnection obj) {
        return accountCategorizationDao.create(obj);
    }

    @Override
    public int update(AccountCategorizationConnection obj) {
        return accountCategorizationDao.update(obj);
    }


    /* Parametre uyuşmazlığı yüzünden Interface'sin kendisine yeniden yazıldı. */
    @Override
    public int allCreat(Account obj, List<AccountCategorizationConnection> choseeCategorizations, Item ci) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * secili olan kategori listesini stringe cevirip dao daki metodu tetikler
     *
     * @param obj account objesi
     * @param choseeCategorizations kategori con listesi
     */
    @Override
    public int allCreat(AccountCategorizationConnection obj, List<AccountCategorizationConnection> choseeCategorizations, Item ci) {
        String choose = "";
        if (choseeCategorizations.size() > 0) {
            choose = String.valueOf(choseeCategorizations.get(0).getCategorization().getId());

            for (int i = 1; i < choseeCategorizations.size(); i++) {
                choose = choose + "," + choseeCategorizations.get(i).getCategorization().getId();
            }
            return accountCategorizationDao.allCreat(obj, choose, ci);
        } else {
            return accountCategorizationDao.allCreat(obj, "0", ci);
        }
    }

    /**
     * secili olan kategori listesini stringe cevirip dao daki metodu tetikler
     *
     * @param obj account objesi
     * @param choseeCategorizations kategori con listesi
     */
    @Override
    public int allUpdate(Account obj, List<AccountCategorizationConnection> choseeCategorizations) {
        String choose = "";
        if (choseeCategorizations.size() > 0) {
            choose = String.valueOf(choseeCategorizations.get(0).getCategorization().getId());

            for (int i = 1; i < choseeCategorizations.size(); i++) {
                choose = choose + "," + choseeCategorizations.get(i).getCategorization().getId();
            }
            return accountCategorizationDao.allUpdate(obj, choose);
        }
        return 0;
    }

}
