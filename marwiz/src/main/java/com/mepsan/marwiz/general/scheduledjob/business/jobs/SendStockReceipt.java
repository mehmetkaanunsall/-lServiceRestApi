/**
 *
 * @author ali.kurt
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.stockaccountreceipt.business.StockAccountReceiptService;
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
public class SendStockReceipt implements Job {

    @Autowired
    StockAccountReceiptService stockAccountReceiptService;

    public void setStockAccountReceiptService(StockAccountReceiptService stockAccountReceiptService) {
        this.stockAccountReceiptService = stockAccountReceiptService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("----SendStockReceiptService Başladı!!!--------");
        this.stockAccountReceiptService.sendStockAccountReceiptAsync();
    }

}
