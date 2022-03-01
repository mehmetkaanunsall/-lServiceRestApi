/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates     
 * and open the template in the editor.          
 */
package com.mepsan.marwiz.automation.cardtype.business;    

import com.mepsan.marwiz.automation.cardtype.dao.ICardTypeDao;
import com.mepsan.marwiz.general.model.automation.FuelCardType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author asli.can
 */
public class CardTypeService implements ICardTypeService{
    
    @Autowired
    public ICardTypeDao cardTypeDao;
    

    @Override
    public List<FuelCardType> findAll() {
        return cardTypeDao.findAll();
    }

    @Override
    public int create(FuelCardType obj) {
       return cardTypeDao.create(obj);
    }

    @Override
    public int update(FuelCardType obj) {
       return cardTypeDao.update(obj);
    }

    @Override
    public int delete(FuelCardType obj) {
        return cardTypeDao.delete(obj);
    }
    
}
