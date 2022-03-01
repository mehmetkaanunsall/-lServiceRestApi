/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.01.2018 01:22:15
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IAccountMovementDao {

    public List<AccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId);

    public AccountMovement count(String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId);

    public String exportData(String where, Account account, int opType, Date beginDate, Date endDate, String sortField, String sortOrder, Date termDate, int termDateOpType, String branchList, int financingTypeId);

    public DataSource getDatasource();

    public int updatePrice(AccountMovement accountMovement);

}
