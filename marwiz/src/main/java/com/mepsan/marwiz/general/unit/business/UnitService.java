/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2018 10:30:13
 */
package com.mepsan.marwiz.general.unit.business;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.unit.dao.IUnitDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class UnitService implements IUnitService {

    @Autowired
    private IUnitDao unitDao;

    public void setUnitDao(IUnitDao unitDao) {
        this.unitDao = unitDao;
    }

    @Override
    public int create(Unit obj) {
        return unitDao.create(obj);
    }

    @Override
    public int update(Unit obj) {
        return unitDao.update(obj);
    }

    @Override
    public List<Unit> findAll() {
        return unitDao.findAll();
    }

    @Override
    public int testBeforeDelete(Unit unit) {
        return unitDao.testBeforeDelete(unit);
    }

    @Override
    public int delete(Unit unit) {
        return unitDao.delete(unit);
    }

    @Override
    public List<Unit> listOfUnit() {
        return unitDao.listOfUnit();
    }

    @Override
    public Unit findUnitAccordingToName(Unit unit) {
        return unitDao.findUnitAccordingToName(unit);
    }

    @Override
    public int deleteForOtherBranch(Unit unit) {
        return unitDao.deleteForOtherBranch(unit);
    }

    @Override
    public int updateAvailableUnit(int oldId, int newId) {
        return unitDao.updateAvailableUnit(oldId, newId);
    }

    @Override
    public List<Unit> listOfUnitAllBranches() {
        return unitDao.listOfUnitAllBranches();
    }

}
