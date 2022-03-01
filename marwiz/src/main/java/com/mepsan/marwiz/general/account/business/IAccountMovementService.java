/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.01.2018 01:17:11
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IAccountMovementService {

    public List<AccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId);

    public AccountMovement count(String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId);

    public void exportPdf(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, Account account, boolean isExtract, int pageId, String sortField, String sortOrder, Date termDate, int termDateUpType, List<Branch> listOfBranch, int financingTypeId);

    public void exportExcel(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, Account account, boolean isExtract, int pageId, String sortField, String sortOrder, Date termDate, int termDateUpType, List<Branch> listOfBranch, int financingTypeId);

    public String exportPrinter(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, Account account, boolean isExtract, int pageId, String sortField, String sortOrder, Date termDate, int termDateUpType, List<Branch> listOfBranch, int financingTypeId);

    public int updatePrice(AccountMovement accountMovement);
}
