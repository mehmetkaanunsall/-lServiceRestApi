/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.12.2019 01:52:21
 */
package com.mepsan.marwiz.inventory.taxdepartment.business;

import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.inventory.taxdepartment.dao.ITaxDepartmentDao;

public class TaxDepartmentService implements ITaxDepartmentService {

    @Autowired
    private ITaxDepartmentDao taxDepartmentDao;

    public void setTaxDepartmentDao(ITaxDepartmentDao taxDepartmentDao) {
        this.taxDepartmentDao = taxDepartmentDao;
    }

    @Override
    public List<TaxDepartment> listOfTaxDepartment() {
        return taxDepartmentDao.listOfTaxDepartment();
    }

    @Override
    public int create(TaxDepartment obj) {
        return taxDepartmentDao.create(obj);
    }

    @Override
    public int update(TaxDepartment obj) {
        return taxDepartmentDao.update(obj);
    }

    @Override
    public int delete(TaxDepartment obj) {
        return taxDepartmentDao.delete(obj);
    }

    @Override
    public int testBeforeDelete(TaxDepartment obj) {
        return taxDepartmentDao.testBeforeDelete(obj);
    }

    @Override
    public int changeStockTaxDepartment() {
        return taxDepartmentDao.changeStockTaxDepartment();
    }

    @Override
    public int changeTaxDepartmentToStockInNonCentralBranch(Stock stock) {
        return taxDepartmentDao.changeTaxDepartmentToStockInNonCentralBranch(stock);
    }

}
