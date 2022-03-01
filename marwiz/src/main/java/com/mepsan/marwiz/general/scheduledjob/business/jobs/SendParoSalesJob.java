/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 12:53:07
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.paro.business.ParoOfflineSalesService;
import com.mepsan.marwiz.service.purchace.business.SendPurchaseService;
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
public class SendParoSalesJob implements Job {

    @Autowired
    ParoOfflineSalesService paroOfflineSalesService;

    public void setParoOfflineSalesService(ParoOfflineSalesService paroOfflineSalesService) {
        this.paroOfflineSalesService = paroOfflineSalesService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("-----SendParoSalesJob Başladı!!!-------");
        this.paroOfflineSalesService.sendSalesAsync();
    }

}
