/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IBankAccountMovementDao {

    public List<BankAccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId);

    //public int count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate);
    public BankAccountMovement count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId);

    public int count(String where, BankAccount bankAcount, Branch branch);

    public String exportData(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId);

    public DataSource getDatasource();

    public int controlMovement(String where, BankAccount bankAcount, Branch branch);
}
