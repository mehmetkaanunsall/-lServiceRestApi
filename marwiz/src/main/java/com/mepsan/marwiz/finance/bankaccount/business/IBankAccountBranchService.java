/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:18:03
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountBranchCon;
import java.util.List;

public interface IBankAccountBranchService {

    public List<BankAccountBranchCon> findBankAccountBranchCon(BankAccount bankAccount);

    public int delete(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount);

    public int create(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount);

    public int update(BankAccountBranchCon bankAccountBranchCon);

    public int createBeginningMovement(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount);

    public int testBeforeDeleteBankAccount(BankAccount bankAccount);
}
