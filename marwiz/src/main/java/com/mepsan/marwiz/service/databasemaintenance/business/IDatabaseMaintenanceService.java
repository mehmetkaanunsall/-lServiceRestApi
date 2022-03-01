/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.04.2021 03:32:29
 */
package com.mepsan.marwiz.service.databasemaintenance.business;

public interface IDatabaseMaintenanceService {

    public void analyzeReindexDatabase();

    public void vacuumDatabase();
}
