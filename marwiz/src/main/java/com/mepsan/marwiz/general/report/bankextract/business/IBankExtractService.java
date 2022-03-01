/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 08.03.2018 15:18:56
 */
package com.mepsan.marwiz.general.report.bankextract.business;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBankExtractService {

    public List<BankAccountMovement> findAll(Date beginDate, Date endDate, String where);

    public String createWhere(List<BankAccount> selectedBank, List<Branch> selectedBranchList);

    public void exportPdf(String where, List<BankAccountMovement> listOfBankExtract, Date beginDate, Date endDate, List<BankAccount> selectedBank, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance);

    public void exportExcel(String where, List<BankAccountMovement> listOfBankExtract, Date beginDate, Date endDate, List<BankAccount> selectedBank, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance);

    public String exportPrinter(String where, List<BankAccountMovement> listOfBankExtract, Date beginDate, Date endDate, List<BankAccount> selectedBank, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance);
}
