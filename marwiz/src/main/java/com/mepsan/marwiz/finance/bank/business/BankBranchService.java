/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 11:36:12
 */
package com.mepsan.marwiz.finance.bank.business;

import com.mepsan.marwiz.finance.bank.dao.IBankBranchDao;
import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BankBranchService implements IBankBranchService {

    @Autowired
    private IBankBranchDao bankBranchDao;

    public void setBankBranchDao(IBankBranchDao bankBranchDao) {
        this.bankBranchDao = bankBranchDao;
    }

    @Override
    public List<BankBranch> selectBankBranchForBank(Bank bank) {
        return bankBranchDao.selectBankBranchForBank(bank);
    }

    @Override
    public int create(BankBranch obj) {
        return bankBranchDao.create(obj);
    }

    @Override
    public int update(BankBranch obj) {
        return bankBranchDao.update(obj);
    }

    @Override
    public List<BankBranch> selectBankBranch() {
        return bankBranchDao.selectBankBranch();
    }

    @Override
    public int testBeforeDelete(BankBranch bankBranch) {
        return bankBranchDao.testBeforeDelete(bankBranch);
    }

    @Override
    public int delete(BankBranch bankBranch) {
       return bankBranchDao.delete(bankBranch);
    }

}
