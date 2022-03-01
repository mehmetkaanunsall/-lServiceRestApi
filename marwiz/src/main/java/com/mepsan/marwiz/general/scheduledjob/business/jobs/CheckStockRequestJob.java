/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 30.04.2018 10:47:23
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.stock.business.SendStockRequestService;
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

public class CheckStockRequestJob implements Job {

    @Autowired
    SendStockRequestService sendStockRequestService;

    public void setSendStockRequestService(SendStockRequestService sendStockRequestService) {
        this.sendStockRequestService = sendStockRequestService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("---------CheckStockRequestJob Başladı!!!");
        this.sendStockRequestService.checkStockRequestAsync();
    }
}
