package com.mepsan.marwiz.automation.saletype.dao;

import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author Samet Dağ
 */
public interface ISaleTypeDao extends ICrud<FuelSaleType> {

    public List<FuelSaleType> findAll();

    public int delete(FuelSaleType obj);
    
    public List<FuelSaleType> findSaleTypeForBranch(String where);
}
