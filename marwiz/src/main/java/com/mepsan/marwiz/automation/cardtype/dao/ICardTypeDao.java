/*
 * To change this license header, choose License Headers in Project Properties.          
 * To change this template file, choose Tools | Templates             
 * and open the template in the editor.            
 */     
package com.mepsan.marwiz.automation.cardtype.dao;

import com.mepsan.marwiz.general.model.automation.FuelCardType;    
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author asli.can
 */
public interface ICardTypeDao extends ICrud<FuelCardType>{
    
    public List<FuelCardType> findAll();
    
    public int delete(FuelCardType obj) ; 
}
