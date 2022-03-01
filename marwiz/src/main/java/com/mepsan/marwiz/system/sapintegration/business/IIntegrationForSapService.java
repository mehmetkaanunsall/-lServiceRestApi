/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapintegration.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSap;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elif.mart
 */
public interface IIntegrationForSapService {

    public List<IntegrationForSap> listOfWarehouseReceipt(Date beginDate, Date endDate, Boolean send, BranchSetting selectedBranch);

    public List<IntegrationForSap> listOfSaleInvoices(Date beginDate, Date endDate, Boolean isRetail, BranchSetting selectedBranch);

    public List<IntegrationForSap> listOfPurchaseInvoices(Date beginDate, Date endDate, int purchaseInvoiceType, BranchSetting selectedBranch);

    public List<BranchSetting> findBranch();

    public void sendDataIntegration(List<IntegrationForSap> listOfSelectedSap, BranchSetting selectedBranch, int processType);

    public String getFinancingType(int typeId);

    public WarehouseReceipt findWarehouseReceipt(WarehouseReceipt warehouseReceipt, BranchSetting selectedBranch);

    public int openUpdate(List<IntegrationForSap> listOfSelectedSap);

    public int sendStatusUpdate(List<IntegrationForSap> listOfSelectedSap, int processType);
}
