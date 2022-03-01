/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.07.2021 02:43:45
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
public class ListVideosJob implements Job {
    
    @Autowired
    CheckItemService checkItemService;

    public void setCheckItemService(CheckItemService checkItemService) {
        this.checkItemService = checkItemService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("------ListVideosJob Başladı!!");
        this.checkItemService.listVideos();
    }

}