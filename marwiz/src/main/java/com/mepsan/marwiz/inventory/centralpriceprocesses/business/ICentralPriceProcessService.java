/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.06.2020 11:37:12
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.inventory.centralpriceprocesses.dao.CentralPriceProcess;
import java.util.List;
import java.util.Map;

public interface ICentralPriceProcessService {

    public List<CentralPriceProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int branchStock, String branchID);

    public int count(String where, int branchStock, String branchID);

    public int save(List<BranchSetting> listOfSelectedBranch, List<CentralPriceProcess> selectedCentralPrice, boolean isPurchase);

    public String jsonArrayCentralPrice(List<BranchSetting> branchList, List<CentralPriceProcess> centralPriceList);

}
