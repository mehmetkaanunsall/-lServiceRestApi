/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 08:29:26
 */
package com.mepsan.marwiz.finance.safe.business;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ISafeMovementService {

    public List<SafeMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId);

    //public int count(String where, Safe safe, int opType, Date beginDate, Date endDate);
    public List<SafeMovement> count(String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId);

    public int count(String where, Safe safe, Branch branch);

    public void exportPdf(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, List<Safe> listOfSafe, List<Branch> listOfBranch, boolean isExtract, String inC, String outC, String balance, String transfer);

    public void exportExcel(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, List<Safe> listOfSafe, List<Branch> listOfBranch, boolean isExtract, String inC, String outC, String balance, String transfer);

    public String exportPrinter(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, List<Safe> listOfSafe, List<Branch> listOfBranch, boolean isExtract, String inC, String outC, String balance, String transfer);
}
