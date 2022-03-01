/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.04.2021 03:32:47
 */
package com.mepsan.marwiz.service.databasemaintenance.dao;

public interface IDatabaseMaintenanceDao {

    public void analyzeReindexDatabase();

    public void vacuumDatabase();
}
