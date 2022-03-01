/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.07.2021 01:30:45
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.firstriggerjob.business.FirstTriggerJobService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class CreateFirstTriggerJob implements Job {

    @Autowired
    FirstTriggerJobService firstTriggerJobService;

    public void setFirstTriggerJobService(FirstTriggerJobService firstTriggerJobService) {
        this.firstTriggerJobService = firstTriggerJobService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("------callFirstTriggerJob Başladı!!");
        this.firstTriggerJobService.callFirstTriggerJob();
    }

}
