/**
 *
 * Bu sınıf, Unit sınıfına arayüz oluşturur.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 08:45:43
 */
package com.mepsan.marwiz.general.unit.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IUnitDao extends ICrud<Unit> {

    public List<Unit> findAll();
    
    public int testBeforeDelete(Unit unit);
    
    public int delete(Unit unit);
    
    public List<Unit> listOfUnit();
    
    public Unit findUnitAccordingToName(Unit unit);
    
    public int deleteForOtherBranch(Unit unit);
    
    public int updateAvailableUnit(int oldId, int newId);
    
    public List<Unit> listOfUnitAllBranches();

}
