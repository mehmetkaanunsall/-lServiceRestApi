/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.04.2021 03:51:11
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.databasemaintenance.business.DatabaseMaintenanceService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class CreateVacuumDatabaseJob implements Job {

    @Autowired
    DatabaseMaintenanceService databaseMaintenanceService;

    public void setDatabaseMaintenanceService(DatabaseMaintenanceService databaseMaintenanceService) {
        this.databaseMaintenanceService = databaseMaintenanceService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("------CreateVacuumDatabaseJob Başladı!!");
        this.databaseMaintenanceService.vacuumDatabase();
    }

}
