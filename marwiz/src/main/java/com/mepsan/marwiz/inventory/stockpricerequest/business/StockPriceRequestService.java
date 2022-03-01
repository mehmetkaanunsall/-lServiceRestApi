/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stockpricerequest.business;

import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import com.mepsan.marwiz.inventory.stockpricerequest.dao.IStockPriceRequestDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class StockPriceRequestService implements IStockPriceRequestService{

    @Autowired
    private IStockPriceRequestDao stockPriceRequestDao;

    public void setStockPriceRequestDao(IStockPriceRequestDao stockPriceRequestDao) {
        this.stockPriceRequestDao = stockPriceRequestDao;
    }
    
    @Override
    public List<StockPriceRequest> findall() {
        return stockPriceRequestDao.findall();
    }

    @Override
    public int create(StockPriceRequest obj) {
        return stockPriceRequestDao.create(obj);
    }
    
}
