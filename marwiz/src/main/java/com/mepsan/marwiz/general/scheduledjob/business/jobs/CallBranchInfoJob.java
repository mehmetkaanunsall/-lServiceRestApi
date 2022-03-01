/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.07.2020 12:21:01
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

import com.mepsan.marwiz.service.branchinfo.business.GetBranchInfoService;
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
public class CallBranchInfoJob implements Job {

    @Autowired
    GetBranchInfoService getBranchInfoService;

    public void setGetBranchInfoService(GetBranchInfoService getBranchInfoService) {
        this.getBranchInfoService = getBranchInfoService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        getBranchInfoService.callBranchInfoForAllBranches();
    }

}
