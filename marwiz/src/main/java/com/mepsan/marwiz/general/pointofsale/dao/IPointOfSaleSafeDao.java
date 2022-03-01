/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:11:55
 */

package com.mepsan.marwiz.general.pointofsale.dao;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.PointOfSaleSafeConnection;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IPointOfSaleSafeDao extends ICrud<PointOfSaleSafeConnection>{
    
    public List<PointOfSaleSafeConnection> listofPOSSafe(PointOfSale obj);
    
    public int delete(PointOfSaleSafeConnection obj);

}