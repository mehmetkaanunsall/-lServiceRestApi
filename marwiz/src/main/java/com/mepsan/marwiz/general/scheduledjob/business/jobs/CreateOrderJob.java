/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.order.business.CreateOrderService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
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
public class CreateOrderJob implements Job{

    @Autowired
    CreateOrderService createOrderService;

    public void setCreateOrderService(CreateOrderService createOrderService) {
        this.createOrderService = createOrderService;
    }
    
    
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("CreateOrderJob başladı");
        JobKey jobKey = jec.getJobDetail().getKey();
        String[] keyString = jobKey.getName().split("_");
        int branchid=Integer.parseInt(keyString[1]);
        int result = this.createOrderService.createOrderJob(branchid);
        System.out.println("result"+result);
    }
    
}
