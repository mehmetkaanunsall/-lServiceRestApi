/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 11:37:13
 */
package com.mepsan.marwiz.finance.bank.dao;

import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IBankBranchDao extends ICrud<BankBranch> {

    public List<BankBranch> selectBankBranchForBank(Bank bank);

    public List<BankBranch> selectBankBranch();

    public int testBeforeDelete(BankBranch bankBranch);

    public int delete(BankBranch bankBranch);

}
