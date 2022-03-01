/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   18.04.2018 12:20:57
 */
package com.mepsan.marwiz.inventory.stockrequest.business;

import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import com.mepsan.marwiz.inventory.stockrequest.dao.IStockRequestDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockRequestService implements IStockRequestService {

    @Autowired
    private IStockRequestDao stockRequestDao;

    public void setStockRequestDao(IStockRequestDao stockRequestDao) {
        this.stockRequestDao = stockRequestDao;
    }

    @Override
    public List<StockRequest> findall() {
        return stockRequestDao.findall();
    }

    @Override
    public int create(StockRequest obj) {
        return stockRequestDao.create(obj);
    }

    @Override
    public int update(StockRequest obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SendStockRequest> checkRequestSend(StockRequest obj) {
        return stockRequestDao.checkRequestSend(obj);
    }

    @Override
    public int update(StockRequest obj, boolean isSend, int type) {
        return stockRequestDao.update(obj, isSend, type);
    }

    @Override
    public List<SendStockRequest> checkRequestSendAllRecord(StockRequest obj) {
       return stockRequestDao.checkRequestSendAllRecord(obj);
    }

    @Override
    public int controlStockRequest(String where, StockRequest stockRequest) {
        return stockRequestDao.controlStockRequest(where, stockRequest);
    }

}
