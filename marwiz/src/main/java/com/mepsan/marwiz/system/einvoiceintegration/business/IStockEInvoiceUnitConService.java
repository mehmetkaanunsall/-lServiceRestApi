/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.business;

import com.mepsan.marwiz.general.model.inventory.StockEInvoiceUnitCon;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IStockEInvoiceUnitConService extends ICrudService<StockEInvoiceUnitCon> {
    
     public List<StockEInvoiceUnitCon> findAll(StockEInvoiceUnitCon obj);
     
       public int delete(StockEInvoiceUnitCon obj);
    
}
