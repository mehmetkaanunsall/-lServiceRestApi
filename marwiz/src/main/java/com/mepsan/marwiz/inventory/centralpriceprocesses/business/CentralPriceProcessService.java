/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.06.2020 11:37:23
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.inventory.centralpriceprocesses.dao.CentralPriceProcess;
import com.mepsan.marwiz.inventory.centralpriceprocesses.dao.ICentralPriceProcessDao;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class CentralPriceProcessService implements ICentralPriceProcessService {

    @Autowired
    private ICentralPriceProcessDao centralPriceProcessDao;

    public void setCentralPriceProcessDao(ICentralPriceProcessDao centralPriceProcessDao) {
        this.centralPriceProcessDao = centralPriceProcessDao;
    }

    @Override
    public List<CentralPriceProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int branchStock, String branchID) {
        return centralPriceProcessDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchStock,branchID);
    }

    @Override
    public int count(String where, int branchStock, String branchID) {
        return centralPriceProcessDao.count(where, branchStock,branchID);
    }

    @Override
    public int save(List<BranchSetting> listOfSelectedBranch, List<CentralPriceProcess> selectedCentralPrice, boolean isPurchase) {
        return centralPriceProcessDao.save(jsonArrayCentralPrice(listOfSelectedBranch, selectedCentralPrice), isPurchase);
    }

    @Override
    public String jsonArrayCentralPrice(List<BranchSetting> branchList, List<CentralPriceProcess> centralPriceList) {
        JsonArray jsonArray = new JsonArray();

        String branchs = "";
        if (!branchList.isEmpty()) {
            for (BranchSetting br : branchList) {
                branchs = branchs + br.getBranch().getId() + ",";
            }
        }
        branchs = branchs.substring(0, branchs.length() - 1);

        for (CentralPriceProcess obj : centralPriceList) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stock_id", obj.getPriceListItem().getStock().getId());
            jsonObject.addProperty("price", obj.getPriceListItem().getPrice());
            jsonObject.addProperty("currency_id", obj.getPriceListItem().getCurrency().getId());
            jsonObject.addProperty("is_taxincluded", obj.getPriceListItem().isIs_taxIncluded());
            jsonObject.addProperty("branch_ids", branchs);

            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

}
