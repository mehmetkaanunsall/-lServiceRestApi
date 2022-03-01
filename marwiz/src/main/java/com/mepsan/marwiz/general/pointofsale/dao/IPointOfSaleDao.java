/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:19:17
 */
package com.mepsan.marwiz.general.pointofsale.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IPointOfSaleDao extends ICrud<PointOfSale> {

    public List<PointOfSale> listOfPointOfSale();

    public int delete(PointOfSale obj);

    public List<PointOfSale> listIntegrationPointOfSale(Branch branch, String where);

    public int updateIntegrationCode(PointOfSale obj);

}
