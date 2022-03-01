/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:33:50 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.automat.washingmachicne.dao.IWashingMachicneTankDao;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WashingMachicneTankService implements IWashingMachicneTankService {

    @Autowired
    IWashingMachicneTankDao washingMachicneTankDao;

    public void setWashingMachicneTankDao(IWashingMachicneTankDao washingMachicneTankDao) {
        this.washingMachicneTankDao = washingMachicneTankDao;
    }

    @Override
    public List<WashingTank> findAll(WashingMachicne obj) {
        return washingMachicneTankDao.findAll(obj);
    }

    @Override
    public int create(WashingTank obj) {
        return washingMachicneTankDao.create(obj);
    }

    @Override
    public int update(WashingTank obj) {
        return washingMachicneTankDao.update(obj);
    }

    @Override
    public int delete(WashingTank obj) {
        return washingMachicneTankDao.delete(obj);
    }

    @Override
    public int testBeforeDelete(WashingTank obj) {
        return washingMachicneTankDao.testBeforeDelete(obj);
    }

}
