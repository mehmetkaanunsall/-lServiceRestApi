/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 9:14:30 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.automat.washingmachicne.dao.IWashingMachicneExpenseUnitPricesDao;
import com.mepsan.marwiz.general.model.automat.ExpenseUnitPrice;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WashingMachicneExpenseUnitPricesService implements IWashingMachicneExpenseUnitPricesService {

    @Autowired
    IWashingMachicneExpenseUnitPricesDao washingMachicneExpenseUnitPricesDao;

    public void setWashingMachicneExpenseUnitPricesDao(IWashingMachicneExpenseUnitPricesDao washingMachicneExpenseUnitPricesDao) {
        this.washingMachicneExpenseUnitPricesDao = washingMachicneExpenseUnitPricesDao;
    }

    @Override
    public List<ExpenseUnitPrice> findAll(WashingMachicne obj) {
        return washingMachicneExpenseUnitPricesDao.findAll(obj);
    }

    @Override
    public int create(ExpenseUnitPrice obj) {
        return washingMachicneExpenseUnitPricesDao.create(obj);
    }

    @Override
    public int update(ExpenseUnitPrice obj) {
        return washingMachicneExpenseUnitPricesDao.update(obj);
    }

    @Override
    public int delete(ExpenseUnitPrice obj) {
        return washingMachicneExpenseUnitPricesDao.delete(obj);
    }

}
