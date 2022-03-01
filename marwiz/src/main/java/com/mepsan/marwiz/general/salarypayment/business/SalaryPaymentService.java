package com.mepsan.marwiz.general.salarypayment.business;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import com.mepsan.marwiz.general.salarypayment.dao.ISalaryPaymentDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author samet.dag
 */
public class SalaryPaymentService implements ISalaryPaymentService {

    @Autowired
    public ISalaryPaymentDao salaryPaymentDao;

    public void setSalaryPaymentDao(ISalaryPaymentDao salaryPaymentDao) {
        this.salaryPaymentDao = salaryPaymentDao;
    }

    @Override
    public List<EmployeeInfo> findAllEmployee() {
        return salaryPaymentDao.findAllEmployee();
    }

    @Override
    public int createFinancingDocument(FinancingDocument financingDocument, Safe safe, BankAccount bankAccount, List<EmployeeInfo> payable, boolean isDebt, List<EmployeeInfo> listAccount, int whichAction) {
        return salaryPaymentDao.createFinancingDocument(financingDocument, safe, bankAccount, payable, isDebt, listAccount, whichAction);
    }

}
