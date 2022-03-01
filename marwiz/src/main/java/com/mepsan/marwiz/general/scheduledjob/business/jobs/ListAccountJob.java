/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.10.2019 04:00:39
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.item.business.CheckItemService;
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
public class ListAccountJob implements Job {

    @Autowired
    CheckItemService checkItemService;

    public void setCheckItemService(CheckItemService checkItemService) {
        this.checkItemService = checkItemService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        this.checkItemService.listAccount();
    }

}
