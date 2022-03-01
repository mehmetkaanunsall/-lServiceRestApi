/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2021 03:45:14
 */
package com.mepsan.marwiz.system.hepsiburadaintegration.dao;

import com.mepsan.marwiz.general.model.inventory.ECommerceStock;
import java.util.List;

public interface IHepsiburadaIntegrationDao {

    public int updateListing(String updateSendData, String updateResult, String updateControlResult, Boolean isSuccess);

    public List<ECommerceStock> bringListing(String stockList, int first, int pageSize, boolean isBringListing, String where);

    public int count(String where);

    public String findSendingHepsiburada();
}
