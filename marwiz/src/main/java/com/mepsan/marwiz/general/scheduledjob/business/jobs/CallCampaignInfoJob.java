/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.07.2021 02:54:31
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

//Üst üste çalışmayı engelle
import com.mepsan.marwiz.service.paro.business.CallCampaignInfoService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class CallCampaignInfoJob implements Job {

    @Autowired
    CallCampaignInfoService callCampaignInfoService;

    public void setCallCampaignInfoService(CallCampaignInfoService callCampaignInfoService) {
        this.callCampaignInfoService = callCampaignInfoService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        callCampaignInfoService.callBranchCampaignInfoForAllBranches();
    }

}
