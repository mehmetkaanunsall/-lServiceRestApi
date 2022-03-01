/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:18:12
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.mepsan.marwiz.finance.bankaccount.dao.IBankAccountBranchDao;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountBranchCon;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BankAccountBranchService implements IBankAccountBranchService {

    @Autowired
    public IBankAccountBranchDao bankAccountBranchDao;

    public void setBankAccountBranchDao(IBankAccountBranchDao bankAccountBranchDao) {
        this.bankAccountBranchDao = bankAccountBranchDao;
    }

    @Override
    public List<BankAccountBranchCon> findBankAccountBranchCon(BankAccount bankAccount) {
        return bankAccountBranchDao.findBankAccountBranchCon(bankAccount);
    }

    @Override
    public int delete(BankAccountBranchCon obj, BankAccount bankAccount) {
        return bankAccountBranchDao.delete(obj, bankAccount);
    }

    @Override
    public int create(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount) {
        return bankAccountBranchDao.create(bankAccountBranchCon, bankAccount);
    }

    @Override
    public int update(BankAccountBranchCon bankAccountBranchCon) {
        return bankAccountBranchDao.update(bankAccountBranchCon);
    }

    @Override
    public int createBeginningMovement(BankAccountBranchCon bankAccountBranchCon, BankAccount bankAccount) {
        return bankAccountBranchDao.createBeginningMovement(bankAccountBranchCon, bankAccount);
    }

    @Override
    public int testBeforeDeleteBankAccount(BankAccount bankAccount) {
        return bankAccountBranchDao.testBeforeDeleteBankAccount(bankAccount);
    }

}
