/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:47:25
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IBankAccountService extends ICrudService<BankAccount> {

    public List<BankAccount> findAll();

    public int delete(BankAccount bankAccount);

    public List<BankAccount> bankAccountForSelect(String where, Branch branch);
    
    public List<BankAccount> bankAccountForSelect(String where, List<Branch> branchList);

}
