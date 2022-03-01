/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 11:34:37
 */
package com.mepsan.marwiz.finance.bank.business;

import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IBankBranchService extends ICrudService<BankBranch> {

    public List<BankBranch> selectBankBranchForBank(Bank bank);

    public List<BankBranch> selectBankBranch();

    public int testBeforeDelete(BankBranch bankBranch);

    public int delete(BankBranch bankBranch);
}
