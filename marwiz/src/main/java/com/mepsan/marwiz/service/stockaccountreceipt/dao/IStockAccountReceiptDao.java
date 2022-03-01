/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.stockaccountreceipt.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ali.kurt
 */
public interface IStockAccountReceiptDao {

    public List<BranchSetting> listOfAllBranch();
    
    public List<OfficalAccounting> findNotSendedAllStockReceipt(BranchSetting bs,Date begin,Date end);

    public List<OfficalAccounting> findNotSendedAllAccountReceipt(BranchSetting bs,Date begin,Date end);
    
    public int updateStockReceipt(OfficalAccounting officalAccounting);

    public int updateAccountReceipt(OfficalAccounting officalAccounting);
}
