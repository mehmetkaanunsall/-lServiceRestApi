/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.05.2019 10:46:27
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

//Üst üste çalışmayı engelle
import com.mepsan.marwiz.service.automation.business.CheckAutomationItemService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class ListAutomationShiftJob implements Job {

    @Autowired
    CheckAutomationItemService checkAutomationItemService;

    public void setCheckAutomationItemService(CheckAutomationItemService checkAutomationItemService) {
        this.checkAutomationItemService = checkAutomationItemService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("---ListAutomationShiftJob Başladı!!!------");
        this.checkAutomationItemService.listAutomationShiftAsync();
    }

}
