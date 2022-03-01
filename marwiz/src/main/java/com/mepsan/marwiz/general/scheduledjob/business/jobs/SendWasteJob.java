/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.07.2019 02:30:32
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.waste.business.SendWasteService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

//Üst üste çalışmayı engelle
@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class SendWasteJob implements Job {

    @Autowired
    SendWasteService sendWasteService;

    public void setSendWasteService(SendWasteService sendWasteService) {
        this.sendWasteService = sendWasteService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("----SendWasteJob Başladı!!!--------");
        this.sendWasteService.sendWasteAsync();
    }

}
