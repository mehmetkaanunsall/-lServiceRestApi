/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 01:34:57
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IAccountDao extends ICrud<Account> {

    public List<Account> accountBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int accountBookCount(String where, String type, List<Object> param);

    public List<Account> findAll(String where);

    public List<Account> findAllAccount(int typeId);

    public int delete(Account account);

    public int testBeforeDelete(Account account);

    public List<Account> findAllAccountToIntegrationCode();

    public List<Account> findSupplier();

    public String saveAccount(String json);

    public int controlCashierUser(Account account);

}
