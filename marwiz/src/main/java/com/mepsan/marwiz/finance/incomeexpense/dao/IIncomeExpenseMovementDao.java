/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.dao;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author esra.cabuk
 */
public interface IIncomeExpenseMovementDao extends ICrud<IncomeExpenseMovement> {
    
    public List<IncomeExpenseMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, IncomeExpense incomeExpense, Date beginDate, Date endDate);

    public List<IncomeExpenseMovement> totals(String where, IncomeExpense incomeExpense, Date beginDate, Date endDate);
    
    public String exportData(String where, IncomeExpense incomeExpense, Date beginDate, Date endDate);

    public DataSource getDatasource();
}
