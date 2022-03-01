/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stockpricerequest.business;

import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IStockPriceRequestService{

    public List<StockPriceRequest> findall();
    
    public int create(StockPriceRequest obj);
    
}
