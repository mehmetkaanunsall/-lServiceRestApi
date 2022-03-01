/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:26:55
 */
package com.mepsan.marwiz.automation.nozzle.business;

import com.mepsan.marwiz.automation.nozzle.dao.INozzleDao;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class NozzleService implements INozzleService {

    @Autowired
    private INozzleDao nozzleDao;

    @Override
    public List<Nozzle> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return nozzleDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return nozzleDao.count(where);
    }

    @Override
    public int create(Nozzle obj) {
        return nozzleDao.create(obj);
    }

    @Override
    public int update(Nozzle obj) {
        return nozzleDao.update(obj);
    }

    @Override
    public int checkNozzle(Nozzle nozzle) {
        return nozzleDao.checkNozzle(nozzle);

    }

    @Override
    public int delete(Nozzle nozzle) {
        return nozzleDao.delete(nozzle);
    }

    @Override
    public int testBeforeDelete(Nozzle nozzle) {
        return nozzleDao.testBeforeDelete(nozzle);
    }

}
