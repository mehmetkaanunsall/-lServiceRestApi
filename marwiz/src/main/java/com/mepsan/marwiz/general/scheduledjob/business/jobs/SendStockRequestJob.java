/**
 * @author Mehmet ERGÜLCÜ
 * @date 20.03.2017 06:12:16
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
public class SendStockRequestJob implements Job {

    @Autowired
    SendStockRequestService sendStockRequestService;

    public void setSendStockRequestService(SendStockRequestService sendStockRequestService) {
        this.sendStockRequestService = sendStockRequestService;
    }
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("----SendStockRequestJob Başladı!!!-------");
        this.sendStockRequestService.sendNotSendedStockRequestAsync();
    }

}
