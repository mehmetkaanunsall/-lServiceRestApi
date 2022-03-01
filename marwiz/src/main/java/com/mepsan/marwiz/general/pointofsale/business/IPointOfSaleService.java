/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:18:36
 */
package com.mepsan.marwiz.general.pointofsale.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IPointOfSaleService extends ICrudService<PointOfSale> {

    public List<PointOfSale> listOfPointOfSale();

    public int delete(PointOfSale obj);

    public List<PointOfSale> listIntegrationPointOfSale(Branch branch, String where);

    public int updateIntegrationCode(PointOfSale obj);

}
