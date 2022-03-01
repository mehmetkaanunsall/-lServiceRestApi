/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.inventory.stock.dao.IStockAlternativeUnitDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class StockAlternativeUnitService implements IStockAlternativeUnitService{

    @Autowired
    private IStockAlternativeUnitDao stockAlternativeUnitDao;
    
    public void setStockAlternativeUnitDao(IStockAlternativeUnitDao stockAlternativeUnitDao) {
        this.stockAlternativeUnitDao = stockAlternativeUnitDao;
    }
   
    @Override
    public List<StockUnitConnection> findAll(Stock stock) {
        return stockAlternativeUnitDao.findAll(stock);
    }

    @Override
    public int create(StockUnitConnection obj) {
        return stockAlternativeUnitDao.create(obj);
    }

    @Override
    public int update(StockUnitConnection obj) {
        return stockAlternativeUnitDao.update(obj);
    }

    @Override
    public int delete(StockUnitConnection stockUnitConnection) {
        return stockAlternativeUnitDao.delete(stockUnitConnection);
    }

    @Override
    public List<StockUnitConnection> findStockUnitConnection(Stock stock, BranchSetting branchSetting) {
       return stockAlternativeUnitDao.findStockUnitConnection(stock, branchSetting);
    }
    
}
