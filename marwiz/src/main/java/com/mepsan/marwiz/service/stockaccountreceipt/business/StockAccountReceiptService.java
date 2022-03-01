/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.stockaccountreceipt.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.service.stockaccountreceipt.dao.IStockAccountReceiptDao;
import java.util.Calendar;

/**
 *
 * @author ali.kurt
 */
public class StockAccountReceiptService implements IStockAccountReceiptService {

    @Autowired
    IStockAccountReceiptDao stockAccountReceiptDao;

    public IStockAccountReceiptDao getStockAccountReceiptDao() {
        return stockAccountReceiptDao;
    }

    public void setStockAccountReceiptDao(IStockAccountReceiptDao stockAccountReceiptDao) {
        this.stockAccountReceiptDao = stockAccountReceiptDao;
    }

    @Override
    public void sendStockAccountReceiptAsync() {

        Date begin = new Date();
        Date end;
        //1 gün öncesi
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(Calendar.DAY_OF_MONTH,-1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        begin = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        
        end = cal.getTime();
        
        
        List<BranchSetting> listOfBranch = stockAccountReceiptDao.listOfAllBranch();
        List<OfficalAccounting> accountings;

        //Entegrasyonu Logo-Akbim olan tüm şubeler için tek tek gönder
        for (BranchSetting bs : listOfBranch) {
            accountings = stockAccountReceiptDao.findNotSendedAllStockReceipt(bs,begin,end);
            if (!accountings.isEmpty()) {
                executeSendStockAccountReceipt(accountings,bs, 1);
            }

            accountings = stockAccountReceiptDao.findNotSendedAllAccountReceipt(bs,begin,end);
            if (!accountings.isEmpty()) {
                executeSendStockAccountReceipt(accountings,bs, 2);
            }
        }

    }

    public void executeSendStockAccountReceipt(List<OfficalAccounting> accountings,BranchSetting bs, int type) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (OfficalAccounting accounting : accountings) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendReceipt(accounting,bs, type);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    
    public void sendReceipt(OfficalAccounting accounting,BranchSetting bs, int type) {
        String res = null;
        String url = bs.getErpUrl();

        if (type == 1) {
            url = url + "/stockfis";
        } else if (type == 2) {
            url = url + "/carifis";
        }
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            res = webServiceClient.request(url, null, null, accounting.getSendData());

            if (res.equals("0")) {
                accounting.setIsSend(true);
            }

        } catch (Exception ex) {
        }

        accounting.setSendDate(new Date());
        accounting.setResponse(res);
        updateStockReceipt(accounting);
    }

    
    public int updateStockReceipt(OfficalAccounting accounting) {
        return stockAccountReceiptDao.updateStockReceipt(accounting);
    }

    
    public int updateAccountReceipt(OfficalAccounting accounting) {
        return stockAccountReceiptDao.updateAccountReceipt(accounting);
    }

}
