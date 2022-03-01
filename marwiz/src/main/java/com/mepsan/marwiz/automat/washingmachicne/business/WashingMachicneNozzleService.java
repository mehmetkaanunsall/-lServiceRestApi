/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:22:30 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.automat.washingmachicne.dao.IWashingMachicneNozzleDao;
import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WashingMachicneNozzleService implements IWashingMachicneNozzleService {

    @Autowired
    IWashingMachicneNozzleDao washingMachicneNozzleDao;

    public void setWashingMachicneNozzleDao(IWashingMachicneNozzleDao washingMachicneNozzleDao) {
        this.washingMachicneNozzleDao = washingMachicneNozzleDao;
    }

    @Override
    public List<WashingNozzle> findAll(WashingPlatform obj) {
        return washingMachicneNozzleDao.findAll(obj);
    }

    @Override
    public int create(WashingNozzle obj) {
        return washingMachicneNozzleDao.create(obj);
    }

    @Override
    public int update(WashingNozzle obj) {
        return washingMachicneNozzleDao.update(obj);
    }
    
    @Override
    public List<WashingNozzle> findNozzleForPlatform(List<WashingPlatform> listOfPlatform) {

        String platformId = "";
        String where = "";
        for (WashingPlatform platform : listOfPlatform) {
            platformId = platformId + "," + String.valueOf(platform.getId());
            if (platform.getId() == 0) {
                platformId = "";
                break;
            }
        }

        if (!platformId.equals("")) {
            platformId = platformId.substring(1, platformId.length());
            where = where + " AND nz.platform_id IN(" + platformId + ") ";

        }

        return washingMachicneNozzleDao.findNozzleForPlatform(where);
    }

    @Override
    public int delete(WashingNozzle obj) {
        return washingMachicneNozzleDao.delete(obj);
    }

}
