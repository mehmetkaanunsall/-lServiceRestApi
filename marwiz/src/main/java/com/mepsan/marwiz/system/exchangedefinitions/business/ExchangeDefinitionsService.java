/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.exchangedefinitions.business;

import com.mepsan.marwiz.system.exchangedefinitions.dao.IExchangeDefinitionsDao;
import com.mepsan.marwiz.general.model.system.Currency;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sinem.arslan
 */
public class ExchangeDefinitionsService implements IExchangeDefinitionsService {

    @Autowired
    private IExchangeDefinitionsDao exchangeDefinitionsDao;

    public void setExchangeDefinitionsDao(IExchangeDefinitionsDao exchangeDefinitionsDao) {
        this.exchangeDefinitionsDao = exchangeDefinitionsDao;
    }

    @Override
    public int create(Currency obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Currency obj) {
        return exchangeDefinitionsDao.update(obj);
    }

    @Override
    public List<Currency> findAll() {
        return exchangeDefinitionsDao.findAll();
    }

}
