package com.mepsan.marwiz.general.salarypayment.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import java.util.List;

/**
 *
 * @author samet.dag
 */
public interface ISalaryPaymentDao {

    public List<EmployeeInfo> findAllEmployee();

    public int createFinancingDocument(FinancingDocument financingDocument, Safe safe, BankAccount bankAccount,
            List<EmployeeInfo> listPayableOrDebt,boolean isDebt,List<EmployeeInfo> listAccount,int whichAction);

}
