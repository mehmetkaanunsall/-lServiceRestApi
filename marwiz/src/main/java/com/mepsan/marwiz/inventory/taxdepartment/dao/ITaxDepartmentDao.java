/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.12.2019 01:52:42
 */
package com.mepsan.marwiz.inventory.taxdepartment.dao;

import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ITaxDepartmentDao extends ICrud<TaxDepartment> {

    public List<TaxDepartment> listOfTaxDepartment();

    public int delete(TaxDepartment obj);

    public int testBeforeDelete(TaxDepartment obj);

    public int changeStockTaxDepartment();

    public int changeTaxDepartmentToStockInNonCentralBranch(Stock stock);

}
