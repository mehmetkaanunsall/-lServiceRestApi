/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.03.2020 12:19:06
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.crateFile.business.CreateFileService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class CreateFileJob implements Job {

    @Autowired
    CreateFileService createFileService;

    public void setCreateFileService(CreateFileService createFileService) {
        this.createFileService = createFileService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("-----CreateFileJob Çalıştı!!!-------");
        this.createFileService.createSalesFileForAllBranches();

    }

}
