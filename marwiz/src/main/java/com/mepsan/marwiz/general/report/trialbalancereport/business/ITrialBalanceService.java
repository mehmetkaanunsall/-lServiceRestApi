package com.mepsan.marwiz.general.report.trialbalancereport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.report.trialbalancereport.dao.TrialBalance;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author samet.dag
 */
public interface ITrialBalanceService {

    public HashMap<Integer, TrialBalance> findAll(Date date, Date firstPeriod, List<Boolean> chkBoxList, int typeStock,String whereBranchList);

    public List<TrialBalance> findDetail(Date date, Date firstPeriod, List<Boolean> chkBoxList, int typeStock,String whereBranchList);

    public void exportPdf(HashMap<Integer, TrialBalance> listOfTrialBalance, BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal totalBalance, Date date, Date firstPeriod, List<TrialBalance> detailTrialList, List<Boolean> chkBoxList);

    public void exportExcel(HashMap<Integer, TrialBalance> listOfTrialBalance, BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal totalBalance, Date date, Date firstPeriod, List<TrialBalance> detailTrialList, List<Boolean> chkBoxList);

    public String exportPrinter(HashMap<Integer, TrialBalance> listOfTrialBalance, BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal totalBalance, Date date, Date firstPeriod, List<TrialBalance> detailTrialList, List<Boolean> chkBoxList);
    
    public TreeNode createDetailTree(List<TrialBalance> listOfDetai);
    
    public String whereBranch(List<BranchSetting> listOfBranch);
}
