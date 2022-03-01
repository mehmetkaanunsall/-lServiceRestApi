/*
 * To change this license header, choose License Headers in Project Properties.       
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.          
 */
package com.mepsan.marwiz.automation.cardtype.business;

import com.mepsan.marwiz.general.model.automation.FuelCardType;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author asli.can
 */
public interface ICardTypeService extends ICrudService<FuelCardType>{
    
    public List<FuelCardType> findAll() ;
    
    public int delete(FuelCardType obj) ; 
}
