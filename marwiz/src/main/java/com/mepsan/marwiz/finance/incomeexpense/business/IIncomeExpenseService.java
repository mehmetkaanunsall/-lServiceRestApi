/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.business;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IIncomeExpenseService extends ICrudService<IncomeExpense> {

    public List<IncomeExpense> listofIncomeExpense(Branch branch);

    public List<IncomeExpense> selectIncomeExpense(boolean isIncome);

    public int testBeforeDelete(IncomeExpense incomeExpense);

    public int delete(IncomeExpense incomeExpense);

    public List<IncomeExpense> totalIncomeExpense(Date beginDate, Date endDate, String branchList);
}
