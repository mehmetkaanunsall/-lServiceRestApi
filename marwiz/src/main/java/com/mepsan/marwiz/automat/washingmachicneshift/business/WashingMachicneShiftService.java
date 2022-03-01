/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:00:24 PM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.business;

import com.mepsan.marwiz.automat.washingmachicneshift.dao.IWashingMachicneShiftDao;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class WashingMachicneShiftService implements IWashingMachicneShiftService {

    @Autowired
    IWashingMachicneShiftDao washingMachicneShiftDao;

    public void setWashingMachicneShiftDao(IWashingMachicneShiftDao washingMachicneShiftDao) {
        this.washingMachicneShiftDao = washingMachicneShiftDao;
    }

    @Override
    public AutomatShift controlOpenShift() {
        return washingMachicneShiftDao.controlOpenShift();
    }

    @Override
    public int create(AutomatShift obj) {
        return washingMachicneShiftDao.create(obj);
    }

    @Override
    public int update(AutomatShift obj) {
        return washingMachicneShiftDao.update(obj);
    }

    @Override
    public List<AutomatShift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return washingMachicneShiftDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return washingMachicneShiftDao.count(where);
    }

}
