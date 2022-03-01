/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.saletype.business;

import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author samet.dag
 */
public interface ISaleTypeService extends ICrudService<FuelSaleType> {

    public List<FuelSaleType> findAll();

    public int delete(FuelSaleType obj);
    
    public List<FuelSaleType> findSaleTypeForBranch(String where);

}
