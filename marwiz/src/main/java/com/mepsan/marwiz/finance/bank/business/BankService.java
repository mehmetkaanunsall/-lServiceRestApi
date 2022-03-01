/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bank.business;

import com.mepsan.marwiz.finance.bank.dao.IBankDao;
import com.mepsan.marwiz.general.model.finance.Bank;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class BankService implements IBankService {

    @Autowired
    private IBankDao bankDao;

    public void setBankDao(IBankDao bankDao) {
        this.bankDao = bankDao;
    }

    /**
     * Bu metot sessiondaki sirketin tüm bankalarını ve bankaya baglı şubelerini
     * listeler
     *
     * @return banka listesi
     */
    @Override
    public List<Bank> findAll() {
        return bankDao.findAll();
    }

    @Override
    public int create(Bank obj) {
        return bankDao.create(obj);
    }

    @Override
    public int update(Bank obj) {
        return bankDao.update(obj);
    }

    @Override
    public List<Bank> bankBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        return bankDao.bankBook(first, pageSize, sortField, sortOrder, filters, where, type, param);
    }

    @Override
    public int bankBookCount(String where, String type, List<Object> param) {
        return bankDao.bankBookCount(where, type, param);
    }

    @Override
    public List<Bank> selectBank() {
        return bankDao.selectBank();
    }

    @Override
    public int testBeforeDelete(String branchList) {
       return bankDao.testBeforeDelete(branchList);
    }

    @Override
    public int delete(Bank bank) {
      return bankDao.delete(bank);
    }
}
