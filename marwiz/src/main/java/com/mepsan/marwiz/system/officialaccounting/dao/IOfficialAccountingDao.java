/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ali.kurt
 */
public interface IOfficialAccountingDao {

    List<OfficalAccounting> listOfAccount(BranchSetting selectedBranch);

    List<OfficalAccounting> listOfStock(BranchSetting selectedBranch);

    List<OfficalAccounting> listOfSafe(BranchSetting selectedBranch);

    List<OfficalAccounting> listOfBank(BranchSetting selectedBranch);

    List<OfficalAccounting> listOfWarehouse(BranchSetting selectedBranch);

    List<OfficalAccounting> listOfAccountMovement(int type, boolean isRetail, Date begin, Date end, BranchSetting selectedBranch);

    List<OfficalAccounting> listOfStockReceipt(int type, boolean isRetail, Date begin, Date end, BranchSetting selectedBranch);

    int update(OfficalAccounting officalAccounting, int processType);

    TotalCount getTotalCounts(int processType, String branchList);

    public List<BranchSetting> findBranch();
}
