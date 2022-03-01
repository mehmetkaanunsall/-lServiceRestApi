/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 10:57:01
 */
package com.mepsan.marwiz.general.centralsupplier.business;

import com.mepsan.marwiz.general.centralsupplier.dao.ICentralSupplierDao;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class CentralSupplierService implements ICentralSupplierService {

    @Autowired
    private ICentralSupplierDao centralSupplierDao;

    public void setCentralSupplierDao(ICentralSupplierDao centralSupplierDao) {
        this.centralSupplierDao = centralSupplierDao;
    }

    @Override
    public List<CentralSupplier> centralSupplierBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param, int supplierType) {
        return centralSupplierDao.centralSupplierBook(first, pageSize, sortField, sortOrder, filters, where, type, param, supplierType);
    }

    @Override
    public int centralSupplierBookCount(String where, String type, List<Object> param, int supplierType) {
        return centralSupplierDao.centralSupplierBookCount(where, type, param, supplierType);
    }

    @Override
    public List<CentralSupplier> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return centralSupplierDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return centralSupplierDao.count(where);
    }

    @Override
    public List<CentralSupplier> findAllCentralSupplier(String where) {
        return centralSupplierDao.findAllCentralSupplier(where);
    }

}
