/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:04:15 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.automat.washingmachicne.dao.IWashingMachicnePlatformDao;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WashingMachicnePlatformService implements IWashingMachicnePlatformService {

    @Autowired
    IWashingMachicnePlatformDao washingMachicnePlatformDao;

    public void setWashingMachicnePlatformDao(IWashingMachicnePlatformDao washingMachicnePlatformDao) {
        this.washingMachicnePlatformDao = washingMachicnePlatformDao;
    }

    @Override
    public List<WashingPlatform> findAll(WashingMachicne obj) {
        return washingMachicnePlatformDao.findAll(obj);
    }

    @Override
    public int create(WashingPlatform obj) {
        return washingMachicnePlatformDao.create(obj);
    }

    @Override
    public int update(WashingPlatform obj) {
        return washingMachicnePlatformDao.update(obj);
    }
    
    @Override
    public List<WashingPlatform> findPlatformForWashingMachine(List<WashingMachicne> listOfWashingMachine) {

        String washingMachineId = "";
        String where = "";
        for (WashingMachicne washingMachicne : listOfWashingMachine) {
            washingMachineId = washingMachineId + "," + String.valueOf(washingMachicne.getId());
            if (washingMachicne.getId() == 0) {
                washingMachineId = "";
                break;
            }
        }

        if (!washingMachineId.equals("")) {
            washingMachineId = washingMachineId.substring(1, washingMachineId.length());
            where = where + " AND plf.washingmachine_id IN(" + washingMachineId + ") ";

        }

        return washingMachicnePlatformDao.findPlatformForWashingMachicne(where);
    }

    @Override
    public int delete(WashingPlatform obj) {
        return washingMachicnePlatformDao.delete(obj);
    }

    @Override
    public int testBeforeDelete(WashingPlatform obj) {
        return washingMachicnePlatformDao.testBeforeDelete(obj);
    }

}
