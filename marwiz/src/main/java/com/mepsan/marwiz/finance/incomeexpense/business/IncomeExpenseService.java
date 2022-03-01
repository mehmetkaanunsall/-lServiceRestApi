/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.business;

import com.mepsan.marwiz.finance.incomeexpense.dao.IIncomeExpenseDao;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseService implements IIncomeExpenseService {

    @Autowired
    private IIncomeExpenseDao incomeExpenseDao;

    public void setIncomeExpenseDao(IIncomeExpenseDao incomeExpenseDao) {
        this.incomeExpenseDao = incomeExpenseDao;
    }

    @Override
    public List<IncomeExpense> listofIncomeExpense(Branch branch) {
        return incomeExpenseDao.listofIncomeExpense(branch);
    }

    @Override
    public int delete(IncomeExpense incomeExpense) {
        return incomeExpenseDao.delete(incomeExpense);
    }

    @Override
    public int create(IncomeExpense obj) {
        return incomeExpenseDao.create(obj);
    }

    @Override
    public int update(IncomeExpense obj) {
        return incomeExpenseDao.update(obj);
    }

    @Override
    public List<IncomeExpense> selectIncomeExpense(boolean isIncome) {
        return incomeExpenseDao.selectIncomeExpense(isIncome);
    }

    @Override
    public int testBeforeDelete(IncomeExpense incomeExpense) {
        return incomeExpenseDao.testBeforeDelete(incomeExpense);
    }

    @Override
    public List<IncomeExpense> totalIncomeExpense(Date beginDate, Date endDate, String branchList) {
        return incomeExpenseDao.totalIncomeExpense(beginDate, endDate, branchList);
    }

}
