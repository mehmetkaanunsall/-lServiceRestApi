/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.04.2021 03:32:37
 */
package com.mepsan.marwiz.service.databasemaintenance.business;

import com.mepsan.marwiz.service.databasemaintenance.dao.IDatabaseMaintenanceDao;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseMaintenanceService implements IDatabaseMaintenanceService {

    @Autowired
    private IDatabaseMaintenanceDao databaseMaintenanceDao;

    public void setDatabaseMaintenanceDao(IDatabaseMaintenanceDao databaseMaintenanceDao) {
        this.databaseMaintenanceDao = databaseMaintenanceDao;
    }

    @Override
    public void analyzeReindexDatabase() {
        databaseMaintenanceDao.analyzeReindexDatabase();
    }

    @Override
    public void vacuumDatabase() {
        databaseMaintenanceDao.vacuumDatabase();
    }

}
