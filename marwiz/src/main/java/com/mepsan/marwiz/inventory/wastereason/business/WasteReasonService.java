package com.mepsan.marwiz.inventory.wastereason.business;

import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.inventory.wastereason.dao.IWasteReasonDao;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class WasteReasonService implements IWasteReasonService {

    @Autowired
    private IWasteReasonDao wasteReasonDao;

    public IWasteReasonDao getWasteReasonDao() {
        return wasteReasonDao;
    }

    public void setWasteReasonDao(IWasteReasonDao wasteReasonDao) {
        this.wasteReasonDao = wasteReasonDao;
    }

    @Override
    public int create(WasteReason obj) {
        return wasteReasonDao.create(obj);
    }

    @Override
    public int update(WasteReason obj) {
        return wasteReasonDao.update(obj);
    }

    @Override
    public List<WasteReason> findAll() {
        List<WasteReason> list = new ArrayList<>();
        list = wasteReasonDao.findAll();

        return list;
    }

    @Override
    public int delete(WasteReason wasteReason) {
        return wasteReasonDao.delete(wasteReason);
    }

    @Override
    public int testBeforeDelete(WasteReason wasteReason) {
        return wasteReasonDao.testBeforeDelete(wasteReason);
    }

    @Override
    public WasteReason findAccordingToName(WasteReason wasteReason) {
        return wasteReasonDao.findAccordingToName(wasteReason);
    }

    @Override
    public int deleteForOtherBranch(WasteReason wasteReason) {
        return wasteReasonDao.deleteForOtherBranch(wasteReason);
    }

    @Override
    public List<WasteReason> selectWasteReason(int isCentral) {
        return wasteReasonDao.selectWasteReason(isCentral);
    }

    @Override
    public int updateAvailableWasteReason(int oldId, int newId) {
       return wasteReasonDao.updateAvailableWasteReason(oldId, newId);
    }

}
