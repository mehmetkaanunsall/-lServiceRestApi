/**
 * Bu interface UnitService sınıfına arayüz sağlar
 *
 * @author Ali Kurt
 *
 * @date on 12.01.2018 09:49:23
 */
package com.mepsan.marwiz.general.unit.business;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IUnitService extends ICrudService<Unit> {

    public List<Unit> findAll();

    public int testBeforeDelete(Unit unit);

    public int delete(Unit unit);

    public List<Unit> listOfUnit();

    public Unit findUnitAccordingToName(Unit unit);
    
    public int deleteForOtherBranch(Unit unit);
    
    public int updateAvailableUnit(int oldId, int newId);
    
    public List<Unit> listOfUnitAllBranches();
}
