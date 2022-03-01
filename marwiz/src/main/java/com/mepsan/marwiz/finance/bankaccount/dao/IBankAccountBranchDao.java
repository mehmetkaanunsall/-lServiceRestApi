/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:17:01
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountBranchCon;
import java.util.List;

public interface IBankAccountBranchDao {

    public List<BankAccountBranchCon> findBankAccountBranchCon(BankAccount bankAccount);

    public int delete(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount);

    public int create(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount);

    public int update(BankAccountBranchCon bankAccountBranchCon);

    public int createBeginningMovement(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount);

    public int testBeforeDeleteBankAccount(BankAccount bankAccount);
}
