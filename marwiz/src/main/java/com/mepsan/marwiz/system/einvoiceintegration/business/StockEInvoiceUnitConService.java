/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.business;

import com.mepsan.marwiz.general.model.inventory.StockEInvoiceUnitCon;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IStockEInvoiceUnitConDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class StockEInvoiceUnitConService implements IStockEInvoiceUnitConService {

    @Autowired
    private IStockEInvoiceUnitConDao stockEInvoiceUnitConDao;

    public IStockEInvoiceUnitConDao getStockEInvoiceUnitConDao() {
        return stockEInvoiceUnitConDao;
    }

    public void setStockEInvoiceUnitConDao(IStockEInvoiceUnitConDao stockEInvoiceUnitConDao) {
        this.stockEInvoiceUnitConDao = stockEInvoiceUnitConDao;
    }

    @Override
    public List<StockEInvoiceUnitCon> findAll(StockEInvoiceUnitCon obj) {
        return stockEInvoiceUnitConDao.findAll(obj);
    }

    @Override
    public int create(StockEInvoiceUnitCon obj) {
        return stockEInvoiceUnitConDao.create(obj);
    }

    @Override
    public int update(StockEInvoiceUnitCon obj) {
        return stockEInvoiceUnitConDao.update(obj);
    }

    @Override
    public int delete(StockEInvoiceUnitCon obj) {
        return stockEInvoiceUnitConDao.delete(obj);
    }

}
