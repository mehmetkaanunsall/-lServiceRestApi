/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 11:23:30
 */
package com.mepsan.marwiz.inventory.stockoperations.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.inventory.stockoperations.dao.IStockOperationsDao;
import com.mepsan.marwiz.inventory.stockoperations.dao.StockOperations;
import static java.lang.Boolean.TRUE;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockOperationsService implements IStockOperationsService {

    @Autowired
    private IStockOperationsDao stockOperationsDao;

    @Autowired
    private SessionBean sessionBean;

    public void setStockOperationsDao(IStockOperationsDao stockOperationsDao) {
        this.stockOperationsDao = stockOperationsDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockOperations> findAll(String where, int process) {
        return stockOperationsDao.findAll(where, process);
    }

    @Override
    public String createWhere(Date beginDate, Date endDate, List<Stock> listOfStock, int process, StockOperations obj, boolean isCentralSupplier, int supplierType) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (process == 1) {
            where += " AND hst.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' ";
        } else if (process == 2) {
            where += " AND ntf.c_time BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' ";
        }
        String stockList = "";
        for (Stock stock : listOfStock) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " AND stck.id IN(" + stockList + ") ";
        }

        String categoryList = "";
        for (Categorization category : obj.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }
        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
            where = where + " AND stck.id IN(SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + "))";
        }

        String accountList = "";
            for (Account account : obj.getListOfAccount()) {
                accountList = accountList + "," + String.valueOf(account.getId());
                if (account.getId() == 0) {
                    accountList = "";
                    break;
                }
            }
            if (!accountList.equals("")) {
                accountList = accountList.substring(1, accountList.length());
                where = where + " AND stck.supplier_id IN(" + accountList + ") ";
            }
        
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            String centralSupplierList = "";
            for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                if (centralSupplier.getId() == 0) {
                    centralSupplierList = "";
                    break;
                }
            }

            if (!centralSupplierList.equals("")) {
                centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                where = where + " AND stck.centralsupplier_id IN(" + centralSupplierList + ") ";

            } else {
                if (isCentralSupplier) {
                    if (supplierType == 0) {
                        where = where + " AND (gcs.centersuppliertype_id != 2 OR gcs.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 1) {
                        where = where + " AND (gcs.centersuppliertype_id NOT IN (1,2) OR gcs.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 2) {
                        where = where + " AND gcs.centersuppliertype_id = 1 ";
                    }
                }
            }

        } 
        return where;
    }

    @Override
    public int processPriceList(List<StockOperations> operationses) {
        String jsonOperations = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonArray jsonArray = new JsonArray();
        for (StockOperations operation : operationses) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stockid", operation.getStock().getId());
            jsonObject.addProperty("currencyid", operation.getCurrency().getId());
            jsonObject.addProperty("processdate", sdf.format(new Date()));
            jsonObject.addProperty("price", operation.getPrice());
            jsonObject.addProperty("istaxincluded", Boolean.TRUE);

            jsonArray.add(jsonObject);
        }
        return stockOperationsDao.processPriceList(jsonArray.toString());
    }

}
