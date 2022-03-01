package com.mepsan.marwiz.general.report.trialbalancereport.dao;

import java.util.Date;
import java.util.List;

/**
 *
 * @author samet.dag
 */
public interface ITrialBalanceDao {

    public List<TrialBalance> findAll(Date date, Date firstPeriod, List<Boolean> chkBoxList,int typeStock,String whereBranchList);

    public List<TrialBalance> findDetail(Date date, Date firstPeriod, List<Boolean> chkBoxList,int typeStock,String whereBranchList);
}
