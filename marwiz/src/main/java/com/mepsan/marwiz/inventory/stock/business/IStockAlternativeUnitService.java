/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IStockAlternativeUnitService extends ICrudService<StockUnitConnection> {

    public List<StockUnitConnection> findAll(Stock stock);

    public int delete(StockUnitConnection stockUnitConnection);

    public List<StockUnitConnection> findStockUnitConnection(Stock stock, BranchSetting branchSetting);

}
