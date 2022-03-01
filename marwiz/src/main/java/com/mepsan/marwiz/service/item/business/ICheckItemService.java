/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 07.05.2018 09:27:49
 */
package com.mepsan.marwiz.service.item.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;

public interface ICheckItemService {

    public void listStock();
    public void listBrand();
    public void listUnit();
    public void listTax();
    public void listNotification(BranchSetting branchSetting);
    public void executeListNotification(List<BranchSetting> branchSettings);
    public void listNotificationAsync();
    public void listCampaign();
    public void listAccount();
    public void listWasteReason();
    public void listStarbucksStock();
    public void listCentralSupplier();
    public void listCurrency();
    public void listExchange();
    public void listCampaingInfo();
    public void listVideos();
    public void listCentralCategories() ;
    
}
