/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.business;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IIncomeExpenseMovementService extends ICrud<IncomeExpenseMovement>{
    
    public List<IncomeExpenseMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, IncomeExpense incomeExpense, Date beginDate, Date endDate);

    public List<IncomeExpenseMovement> totals(String where, IncomeExpense incomeExpense, Date beginDate, Date endDate);
    
    public void exportPdf(String createWhere, List<Boolean> toogleList, Date beginDate, Date endDate, IncomeExpense incomeExpense, List<IncomeExpenseMovement> listOfTotals);

    public void exportExcel(String createWhere, List<Boolean> toogleList, Date beginDate, Date endDate, IncomeExpense incomeExpense, List<IncomeExpenseMovement> listOfTotals);

    public String exportPrinter(String createWhere, List<Boolean> toogleList, Date beginDate, Date endDate, IncomeExpense incomeExpense, List<IncomeExpenseMovement> listOfTotals);

}
