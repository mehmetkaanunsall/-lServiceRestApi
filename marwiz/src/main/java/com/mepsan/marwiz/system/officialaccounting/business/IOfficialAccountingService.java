/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.system.officialaccounting.dao.TotalCount;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ali.kurt
 */
public interface IOfficialAccountingService {

    List<OfficalAccounting> listOfIntegration(int processType, boolean isRetail, Date begin, Date end, BranchSetting selectedBranch);

    void sendDataIntegration(List<OfficalAccounting> list,  BranchSetting branchSetting);

    public TotalCount getTotalCounts(String branchList);

    public String getFinancingType(int typeId);
    
     public List<BranchSetting> findBranch();
}
