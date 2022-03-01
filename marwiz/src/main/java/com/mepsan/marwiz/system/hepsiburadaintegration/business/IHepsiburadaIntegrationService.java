/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2021 03:08:03
 */
package com.mepsan.marwiz.system.hepsiburadaintegration.business;

import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.model.inventory.ECommerceStock;
import java.util.List;

public interface IHepsiburadaIntegrationService {

    public String listingStock(BranchIntegration branchIntegration);

    public boolean updateListing(List<ECommerceStock> listOfCommerceStock, BranchIntegration branchIntegration, boolean isRemoveFromSale, boolean isSendAllStock, String tempSendData);

    public List<ECommerceStock> bringListing(String stockList, int first, int pageSize, boolean isBringListing, String where);

    public int count(String where);

    public String createWhere(ECommerceStock obj);

    public String findSendingHepsiburada();

}
