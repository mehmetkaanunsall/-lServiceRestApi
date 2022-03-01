/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:47:54
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.mepsan.marwiz.finance.bankaccount.dao.IBankAccountDao;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class BankAccountService implements IBankAccountService {

    @Autowired
    private IBankAccountDao bankAccountDao;

    public void setBankAccountDao(IBankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountDao.findAll();
    }

    @Override
    public int create(BankAccount obj) {
        return bankAccountDao.create(obj);
    }

    @Override
    public int update(BankAccount obj) {
        return bankAccountDao.update(obj);
    }


    @Override
    public int delete(BankAccount bankAccount) {
        return bankAccountDao.delete(bankAccount);
    }

    @Override
    public List<BankAccount> bankAccountForSelect(String where, Branch branch) {
        return bankAccountDao.bankAccountForSelect(where, branch);
    }

    @Override
    public List<BankAccount> bankAccountForSelect(String where, List<Branch> branchList) {
        return bankAccountDao.bankAccountForSelect(where, branchList);
    }

}
