/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.04.2021 11:22:31
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

//Üst üste çalışmayı engelle
import com.mepsan.marwiz.service.item.business.CheckItemService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class ListCampaignInfoJob implements Job{

    @Autowired
    CheckItemService checkItemService;

    public void setCheckItemService(CheckItemService checkItemService) {
        this.checkItemService = checkItemService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("---------ListCampaingInfoJob Başladı!!!");
        this.checkItemService.listCampaingInfo();
    }
}
