/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 18.07.2018 17:49:06
 */
package com.mepsan.marwiz.finance.salereturn.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.salereturn.dao.ISaleReturnDao;
import com.mepsan.marwiz.finance.salereturn.dao.ResponseSalesReturn;
import com.mepsan.marwiz.finance.salereturn.dao.SaleReturnReport;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class SaleReturnService implements ISaleReturnService {

    @Autowired
    private ISaleReturnDao saleReturnDao;

    public void setSaleReturnDao(ISaleReturnDao saleReturnDao) {
        this.saleReturnDao = saleReturnDao;
    }
    @Autowired
    private SessionBean sessionBean;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    @Override
    public List<SaleReturnReport> findReceipt(String receiptNo, Date processDate, int branchId) {
        return saleReturnDao.findReceipt(receiptNo, processDate, branchId);
    }

    @Override
    public List<SaleReturnReport> findSalePayment(int saleId) {
        return saleReturnDao.findSalePayment(saleId);
    }

    @Override
    public int acceptReturn(SaleReturnReport obj, BranchSetting branchSetting, UserData userdata) {
        return saleReturnDao.acceptReturn(obj, branchSetting, userdata);
    }

    @Override
    public List<SaleReturnReport> findCreditPayment(int saleId) {
        return saleReturnDao.findCreditPayment(saleId);
    }

    @Override
    public List<SaleReturnReport> findCreditPaymentDetail(int saleId) {
        return saleReturnDao.findCreditPaymentDetail(saleId);
    }

    @Override
    public List<SaleReturnReport> findSaleWithoutReceipt(List<Stock> stockList, Date beginDate, Date endDate, int branchId) {

        String stocks = "";
        for (Stock stock : stockList) {
            stocks = stocks + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stocks = "";
                break;
            }
        }
        String where = "";
        if (!stocks.equals("")) {
            stocks = stocks.substring(1, stocks.length());
            where = where + " AND sli.stock_id IN(" + stocks + ") ";

        }
        return saleReturnDao.findSaleWithoutReceipt(where, beginDate, endDate, branchId);
    }

    @Override
    public int checkSafeStatus(int saleId) {
        return saleReturnDao.checkSafeStatus(saleId);
    }


    @Override
    public int createParoSales(int saleId) {
        return saleReturnDao.createParoSales(saleId);

    }
    
    @Override
     public List<BranchSetting> findBranchSetting(){
     return saleReturnDao.findBranchSetting();
     }

}
