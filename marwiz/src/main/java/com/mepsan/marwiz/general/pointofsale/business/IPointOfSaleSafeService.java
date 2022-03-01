/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:08:35
 */
package com.mepsan.marwiz.general.pointofsale.business;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.PointOfSaleSafeConnection;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IPointOfSaleSafeService extends ICrudService<PointOfSaleSafeConnection> {

    public List<PointOfSaleSafeConnection> listofPOSSafe(PointOfSale obj);
    
    public int delete(PointOfSaleSafeConnection obj);
    

}
