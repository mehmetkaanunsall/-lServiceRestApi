/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.order.business.SendOrderService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
//Üst üste çalışmayı engelle
@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class SendOrderJob implements Job{
    
    @Autowired
    SendOrderService sendOrderService;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("-----SendOrderJob Başladı!!!-------");
        this.sendOrderService.sendOrderNotSendedToCenterAsync();
    }
    
}
