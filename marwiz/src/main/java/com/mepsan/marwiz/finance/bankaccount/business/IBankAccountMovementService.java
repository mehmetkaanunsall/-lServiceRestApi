/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBankAccountMovementService {

    public List<BankAccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId);

    //public int count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate);
    public BankAccountMovement count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId);

    public int count(String where, BankAccount bankAcount, Branch branch);

    public void exportPdf(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, BankAccount selectedBankAccount, boolean isExtract, List<Branch> listOfBranch);

    public void exportExcel(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, BankAccount selectedBankAccount, boolean isExtract, List<Branch> listOfBranch);

    public String exportPrinter(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, BankAccount selectedBankAccount, boolean isExtract, List<Branch> listOfBranch);

    public int controlMovement(String where, BankAccount bankAcount, Branch branch); 
}
