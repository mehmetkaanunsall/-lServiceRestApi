/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 06:03:25
 */
package com.mepsan.marwiz.general.cashregister.business;

import com.mepsan.marwiz.general.cashregister.dao.ICashRegisterDao;
import com.mepsan.marwiz.general.model.general.CashRegister;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class CashRegisterService implements ICashRegisterService {

    @Autowired
    private ICashRegisterDao cashRegisterDao;

    public void setCashRegisterDao(ICashRegisterDao cashRegisterDao) {
        this.cashRegisterDao = cashRegisterDao;
    }
    @Override
    public List<CashRegister> selectListCashRegister() {
        return cashRegisterDao.selectListCashRegister();
    }

    @Override
    public List<CashRegister> listOfCashRegister() {
        return cashRegisterDao.listOfCashRegister();
    }

    @Override
    public int create(CashRegister obj) {
        return cashRegisterDao.create(obj);
    }

    @Override
    public int update(CashRegister obj) {
        return cashRegisterDao.update(obj);
    }

    @Override
    public int delete(CashRegister obj) {
        return cashRegisterDao.delete(obj);
    }

}
