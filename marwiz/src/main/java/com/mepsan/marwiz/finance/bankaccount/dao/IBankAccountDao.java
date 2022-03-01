/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:48:05
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IBankAccountDao extends ICrud<BankAccount> {

    public List<BankAccount> findAll();
    
    public int delete(BankAccount bankAccount);

    public List<BankAccount> bankAccountForSelect(String where, Branch branch);
    
    public List<BankAccount> bankAccountForSelect(String where, List<Branch> branchList);

}
