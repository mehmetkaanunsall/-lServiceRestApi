/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 11:24:52
 */
package com.mepsan.marwiz.inventory.stocktaking.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.inventory.stocktaking.dao.IStockTakingDao;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockTakingService implements IStockTakingService {

    @Autowired
    private IStockTakingDao stockTakingDao;

    public void setStockTakingDao(IStockTakingDao stockTakingDao) {
        this.stockTakingDao = stockTakingDao;
    }

    @Override
    public List<StockTaking> findAll(String where) {
        return stockTakingDao.findAll(where);
    }

    @Override
    public int create(StockTaking obj, List<Categorization> listOfCategorization) {

        String categories = null;

        if (!obj.getListOfCategorization().isEmpty()) {
            if (obj.getListOfCategorization().get(0).getId() != 0) {
                JsonArray jsonArray = new JsonArray();
                for (Categorization categorization : listOfCategorization) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", categorization.getId());
                    jsonArray.add(jsonObject);
                }
                categories = jsonArray.toString();
            }
        }
        return stockTakingDao.create(obj, categories);
    }

    @Override
    public int update(StockTaking obj) {
        return stockTakingDao.update(obj);
    }

    @Override
    public int update(StockTaking obj, List<StockTakingItem> deletedList, List<Categorization> oldCategoryList) {

        String items = "";
        for (StockTakingItem stockTakingItem : deletedList) {
            items = items + "," + String.valueOf(stockTakingItem.getId());
            if (stockTakingItem.getId() == 0) {
                items = "";
                break;
            }
        }
        if (!items.equals("")) {
            items = items.substring(1, items.length());
        }

        String deleteCategorizationIds = "";
        String insertedCategorizationIds = "";

        if (obj.getListOfCategorization().isEmpty() || obj.getListOfCategorization().get(0).getId() == 0) {
            insertedCategorizationIds = "";
            String s = "";
            for (Categorization categorization : oldCategoryList) {
                s = s + "," + String.valueOf(categorization.getId());
                if (categorization.getId() == 0) {
                    s = "";
                    break;
                }
            }
            if (!s.equals("")) {
                s = s.substring(1, s.length());
            }
            deleteCategorizationIds = s;
        } else if (oldCategoryList.isEmpty() || oldCategoryList.get(0).getId() == 0) {
            deleteCategorizationIds = "";
            String s = "";
            for (Categorization categorization : obj.getListOfCategorization()) {
                s = s + "," + String.valueOf(categorization.getId());
                if (categorization.getId() == 0) {
                    s = "";
                    break;
                }
            }
            if (!s.equals("")) {
                s = s.substring(1, s.length());
            }
            insertedCategorizationIds = s;
        } else {
            deleteCategorizationIds = controlList((List) oldCategoryList, (List) obj.getListOfCategorization());
            insertedCategorizationIds = controlList((List) obj.getListOfCategorization(), (List) oldCategoryList);
        }

        return stockTakingDao.update(obj, deleteCategorizationIds, insertedCategorizationIds, items);
    }

    /**
     * bu metot gelen listelerden eklenen ve cıkarılanları belirler
     *
     * @param oldList
     * @param newList
     * @return oldListte olup newListte olmayanları döndürür.
     */
    public String controlList(List<Object> oldList, List<Object> newList) {
        String Ids = "";
        int id = 0;

        for (Object old : oldList) {
            id = 0;
            for (Object nw : newList) {
                if (old.hashCode() == nw.hashCode()) {
                    id = old.hashCode();
                }
            }
            if (id == 0) {
                if (Ids.equals("")) {
                    Ids = String.valueOf(old.hashCode());
                } else {
                    Ids = Ids + "," + String.valueOf(old.hashCode());
                }
            }
        }

        return Ids;
    }

    @Override
    public List<StockTaking> selectStockTakingByWarehouse(Warehouse warehouse) {
        return stockTakingDao.selectStockTakingByWarehouse(warehouse);
    }

    @Override
    public int delete(StockTaking stockTaking) {
        return stockTakingDao.delete(stockTaking);
    }

    @Override
    public StockTaking find(StockTaking stockTaking) {

        List<StockTaking> list = stockTakingDao.findAll(" AND ist.id = " + stockTaking.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new StockTaking();
        }
    }

    @Override
    public List<Account> employeList() {
        return stockTakingDao.employeList();
    }

    @Override
    public List<StockTaking> stockTakingDetail(StockTaking stockTaking) {
        return stockTakingDao.stockTakingDetail(stockTaking);
    }

    @Override
    public List<StockTaking> findStockTakingDifference(StockTaking stockTaking) {
        return stockTakingDao.findStockTakingDifference(stockTaking);
    }

    @Override
    public int finisStockTaking(StockTaking stockTaking, FinancingDocument obj, String accounts) {
        return stockTakingDao.finisStockTaking(stockTaking, obj, accounts);
    }

    @Override
    public String jsonArrayAccounts(List<AccountMovement> accountMovements) {
        JsonArray jsonArray = new JsonArray();
        for (AccountMovement obj : accountMovements) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("account_id", obj.getAccount().getId());
            jsonObject.addProperty("price", obj.getPrice());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public int openStockTaking(StockTaking stockTaking) {
        return stockTakingDao.openStockTaking(stockTaking);
    }

    @Override
    public StockTaking findOpenStock(StockTaking stockTaking) {
        return stockTakingDao.findOpenStock(stockTaking);
    }

    @Override
    public int findCategories(StockTaking obj, String where) {
        return stockTakingDao.findCategories(obj, where);
    }

    @Override
    public List<Stock> categoryOfStock(StockTaking obj, String where) {
        return stockTakingDao.categoryOfStock(obj, where);
    }

    @Override
    public List<StockTaking> stockTakingProcessList() {
        return stockTakingDao.stockTakingProcessList();
    }

}
