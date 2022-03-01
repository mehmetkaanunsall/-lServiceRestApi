/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 10:57:07
 */
package com.mepsan.marwiz.general.centralsupplier.dao;

import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;

public interface ICentralSupplierDao extends ILazyGrid<CentralSupplier>{

    public List<CentralSupplier> centralSupplierBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param, int supplierType);

    public int centralSupplierBookCount(String where, String type, List<Object> param, int supplierType);
    
     public List<CentralSupplier> findAllCentralSupplier(String where);

}
