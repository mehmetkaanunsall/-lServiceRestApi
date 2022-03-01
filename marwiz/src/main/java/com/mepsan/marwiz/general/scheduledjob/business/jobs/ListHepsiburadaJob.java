/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.03.2021 12:19:24
 */
package com.mepsan.marwiz.general.scheduledjob.business.jobs;

//Üst üste çalışmayı engelle
import com.mepsan.marwiz.service.hepsiburada.business.HepsiburadaService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
//DataMap değer değişikliklerini sonraki iş için tutar
@PersistJobDataAfterExecution
public class ListHepsiburadaJob implements Job {

    @Autowired
    HepsiburadaService hepsiburadaService;

    public void setHepsiburadaService(HepsiburadaService hepsiburadaService) {
        this.hepsiburadaService = hepsiburadaService;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("------ListHepsiburadaJob Başladı!!");
        this.hepsiburadaService.listHepsiburadaAsync();
    }

}
