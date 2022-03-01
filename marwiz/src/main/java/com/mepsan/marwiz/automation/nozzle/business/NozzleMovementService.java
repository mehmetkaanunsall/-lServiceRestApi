/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 11.02.2019 14:17:35
 */
package com.mepsan.marwiz.automation.nozzle.business;

import com.mepsan.marwiz.automation.nozzle.dao.INozzleMovementDao;
import com.mepsan.marwiz.automation.nozzle.dao.NozzleMovement;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class NozzleMovementService implements INozzleMovementService {

    @Autowired
    public INozzleMovementDao nozzleMovementDao;

    public void setNozzleMovementDao(INozzleMovementDao nozzleMovementDao) {
        this.nozzleMovementDao = nozzleMovementDao;
    }

    @Override
    public List<NozzleMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return nozzleMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return nozzleMovementDao.count(where);
    }

    @Override
    public String createWhere(Date beginDate, Date endDate, Nozzle nozzle) {
        String where = "";

        where += nozzle.getId() != 0 ? " AND shs.nozzle_id= " + nozzle.getId() + " " : " ";

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND shs.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' ";

        return where;
    }


}
