/**
 * Bu Sınıf ApplicationBean  dependency injection için
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   15.06.2016 11:39:06
 */
package com.mepsan.marwiz.general.appllication.presentation;

import com.btmatthews.atlas.quartz.AutowiringSpringBeanJobFactory;
import com.mepsan.marwiz.general.appllication.business.ApplicationService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.ScheduledJobTrigger;
import com.mepsan.marwiz.general.model.wot.UserFailer;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ApplicationList;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CallBranchInfoJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CallCampaignInfoJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CheckPriceChangeRequestJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CheckStockRequestJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CreateAnalyzeReIndexDatabaseJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CreateFileJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CreateFirstTriggerJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CreateOrderJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.CreateVacuumDatabaseJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ExchangeJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListAccountJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListAutomationShiftJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListBrandJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListCampaignInfoJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListCampaignJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListCentralCategoriesJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListCentralSupplier;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListHepsiburadaJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListNotificationJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListStarbucksStock;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListStockJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListTaxJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListUnitJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListVideosJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.ListWasteReasonJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendOrderJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendParoSalesJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendPriceChangeRequestJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendPurchaseJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendSaleJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendStockInfoJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendStockReceipt;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendStockRequestJob;
import com.mepsan.marwiz.general.scheduledjob.business.jobs.SendWasteJob;
import com.mepsan.marwiz.service.price.business.SendPriceChangeRequestService;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ApplicationBean {

    @Autowired
    private ApplicationService appService;

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, List<UserFailer>> map = new HashMap<>();
    private Map<String, Parameter> parameterMap = new HashMap<>();
    private Map<Integer, Boolean> branchShiftPaymentMap = new HashMap<>();
    private List<Country> countries;
    private List<Status> statuses;
    private List<Type> types;
    private List<Language> languages;
    private List<Currency> currencies;
    private List<Page> listOfPages;
    private List<Module> listOfModules;
    private Scheduler scheduler = null;
    private Map<Integer, BranchSetting> branchSettingMap;
    private Calendar cal = Calendar.getInstance();
    private Map<Integer, ScheduledJobTrigger> scheduledJobMap;

    public Map<Integer, Boolean> getBranchShiftPaymentMap() {
        return branchShiftPaymentMap;
    }

    public void setBranchShiftPaymentMap(Map<Integer, Boolean> branchShiftPaymentMap) {
        this.branchShiftPaymentMap = branchShiftPaymentMap;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<Module> getListOfModules() {
        return listOfModules;
    }

    public void setListOfModules(List<Module> listOfModules) {
        this.listOfModules = listOfModules;
    }

    public List<Page> getListOfPages() {
        return listOfPages;
    }

    public void setListOfPages(List<Page> listOfPages) {
        this.listOfPages = listOfPages;
    }

    public Map<String, List<UserFailer>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<UserFailer>> map) {
        this.map = map;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public ApplicationService getAppService() {
        return appService;
    }

    public void setAppService(ApplicationService appService) {
        this.appService = appService;
    }

    public Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Parameter> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public Map<Integer, BranchSetting> getBranchSettingMap() {
        return branchSettingMap;
    }

    public void setBranchSettingMap(Map<Integer, BranchSetting> branchSettingMap) {
        this.branchSettingMap = branchSettingMap;
    }

    public void refreshBranchSetting() {
        branchSettingMap = appService.bringBranchSettings();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void init() {
        //  try {

        countries = appService.countryListXml();

        ApplicationList applicationList = appService.appListXml();
        statuses = appService.customizeStatusXml(applicationList.getStatusJson());
        types = appService.customizeTypeXml(applicationList.getTypeJson());
        languages = appService.customizeLangXml(applicationList.getLangJson());
        currencies = appService.customizeCurrencyXml(applicationList.getCurrencyJson());
        branchShiftPaymentMap = appService.customizeBranchShiftPaymentXml(applicationList.getBranchShiftPayment());

        parameterMap = new HashMap<>();
        appService.parameterList(applicationList.getParameters()).stream().forEach((all) -> {
            parameterMap.put(all.getKeyword(), all);
        });
        //   listOfPages = appService.customizePage(null);

        listOfModules = appService.customizeModules();

        branchSettingMap = appService.bringBranchSettings();

        // System.out.println("*-*-*- test1 "+StaticMethods.convertToDateFormatWithSeconds("dd/MM/yyyy",branchSettingMap.get(1).getPastPeriodClosingDate()));
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            AutowiringSpringBeanJobFactory springBeanJobFactory = new AutowiringSpringBeanJobFactory();
            springBeanJobFactory.setApplicationContext(applicationContext);
            scheduler.setJobFactory(springBeanJobFactory);
            scheduler.start();

        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        createListCentralCategoriesJobTemp();
        createExchanceJob();
        createSendSaleJob();
        createSendPurchaseJob();
        createSendStockRequestJob();
        createCheckStockRequestJob();
        createListBrandJob();
        createListUnitJob();
        createListTaxJob();
        createListCentralSupplier();
        createListStockJob();
        createSendStockInfoJob();
        createSendParoSalesJob();
        createListNotificationJob();
        createSendPriceChangeRequestJob();
        createCheckPriceChangeRequestJob();
        createSendWasteJob();
        createListCampaignJob();
        createListAccountJob();
        createListWasteReasonJob();
        createFile();
        callBranchInfo();
        createSendOrderJob();
        createListCurrencyJob();
        createAnalyzeReindexDatabase();
        createVacuumDatabase();
        createListCampaignInfoJob();
        createListParoCampaignInfoJob();
        createListVideosJob();
        createListCentralCategoriesJob();

        Parameter pFirstTriggerJob = parameterMap.get("first_trigger_job");
        if (pFirstTriggerJob.getValue().equals("false")) {
            createFirstTriggerJob();
        }

        ////Starbucks url varsa ürünlerini çek
        if (appService.controlStarbucksSettingBranch() == 1) {
            createListStarbucksStockJob();
        }
        if (appService.controlBranchIntegration() == 1) {
            createListHepsiBuradaJob();
        }

        if (appService.controlAutomationSettingBranch(1) == 1) {
            createSendAutomationShiftJob();
        }
        scheduledJobMap = appService.scheduledJob();

        if (scheduledJobMap != null) {
            for (Map.Entry<Integer, ScheduledJobTrigger> entry : scheduledJobMap.entrySet()) {
                try {
                    createCreateOrderJob(entry.getValue());
                } catch (ParseException ex) {
                    Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private void createExchanceJob() {
        //kur almak için job ekliyoruz

        JobDetail job = newJob(ExchangeJob.class).withIdentity("job_exc", "group_exc").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && cal.get(Calendar.MINUTE) > 5) {//saat 16:00 i geçti ise ertesi gün 16:00 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 5);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_exc", "group_exc")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //kur alma job sonu

        JobDetail job1 = newJob(ExchangeJob.class).withIdentity("job_exc1", "group_exc1").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && cal.get(Calendar.MINUTE) > 15) {//saat 16:30 i geçti ise ertesi gün 16:30 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger1 = newTrigger()
                  .withIdentity("trigger_exc1", "group_exc1")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job1, trigger1);

        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        JobDetail job2 = newJob(ExchangeJob.class).withIdentity("job_exc2", "group_exc2").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && cal.get(Calendar.MINUTE) > 30) {//saat 17:00 i geçti ise ertesi gün 17:00 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger2 = newTrigger()
                  .withIdentity("trigger_exc2", "group_exc2")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job2, trigger2);

        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSendSaleJob() {

        JobDetail job = newJob(SendSaleJob.class).withIdentity("job_sendsale", "group_sendsale").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 20);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendsale", "group_sendsale")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1200)// 20 dakikada bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSendStockRequestJob() {
        JobDetail job = newJob(SendStockRequestJob.class).withIdentity("job_sendstockrequest", "group_sendstockrequest").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 3);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendstockrequest", "group_sendstockrequest")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 30)// 30 dakikada bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createCheckStockRequestJob() {
        JobDetail job = newJob(CheckStockRequestJob.class).withIdentity("job_checkstockrequest", "group_checkstockrequest").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 5);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_checkstockrequest", "group_checkstockrequest")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(7200)// 2 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSendPriceChangeRequestJob() {
        JobDetail job = newJob(SendPriceChangeRequestJob.class).withIdentity("job_sendpricechangerequest", "group_sendpricechangerequest").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 8);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendpricechangerequest", "group_sendpricechangerequest")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 30)// 30 dakikada bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createCheckPriceChangeRequestJob() {
        JobDetail job = newJob(CheckPriceChangeRequestJob.class).withIdentity("job_checkpricechangerequest", "group_checkpricechangerequest").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 10);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_checkpricechangerequest", "group_checkpricechangerequest")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(7200)// 2 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListStockJob() {
        JobDetail job = newJob(ListStockJob.class).withIdentity("job_liststock", "group_liststock").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 12);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_liststock", "group_liststock")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3600)// 1 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListBrandJob() {
        JobDetail job = newJob(ListBrandJob.class).withIdentity("job_listbrand", "group_listbrand").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) > 23) {//saat 23 i geçti ise ertesi gün 23 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listbrand", "group_listbrand")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListUnitJob() {
        JobDetail job = newJob(ListUnitJob.class).withIdentity("job_listunit", "group_listunit").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 30) {//saat 23:30 i geçti ise ertesi gün 23:30 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listunit", "group_listunit")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListTaxJob() {
        JobDetail job = newJob(ListTaxJob.class).withIdentity("job_listtax", "group_listtax").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) > 0) {//saat 24 i geçti ise ertesi gün 24 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listtax", "group_listtax")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSendPurchaseJob() {
        JobDetail job = newJob(SendPurchaseJob.class).withIdentity("job_sendpurchase", "group_sendpurchase").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 14);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendpurchase", "group_sendpurchase")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3600)// 1 saat bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSendOrderJob() {
        JobDetail job = newJob(SendOrderJob.class).withIdentity("job_sendorder", "group_sendorder").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 14);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendorder", "group_sendorder")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3600)// 1 saat bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSendStockInfoJob() {
        JobDetail job = newJob(SendStockInfoJob.class).withIdentity("job_sendstockinfo", "group_sendstockinfo").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 30);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendstockinfo", "group_sendstockinfo")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(30 * 60)// 30 dakikada bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Paro Satışlarını Gönderir
     */
    private void createSendParoSalesJob() {
        JobDetail job = newJob(SendParoSalesJob.class).withIdentity("job_sendparosales", "group_sendparosales").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 16);//cal.set(Calendar.MINUTE,0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendparosales", "group_sendparosales")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3600)// 1 saat bir çalış 3600
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListNotificationJob() {
        JobDetail job = newJob(ListNotificationJob.class).withIdentity("job_listnotification", "group_listnotification").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 25);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listnotification", "group_listnotification")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 2)// 2 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createSendAutomationShiftJob() {
        JobDetail job = newJob(ListAutomationShiftJob.class).withIdentity("job_sendautomationshift", "group_sendautomationshift").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 27);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendautomationshift", "group_sendautomationshift")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 2)// 2 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Atıkları merkeze gönderir.
    public void createSendWasteJob() {
        JobDetail job = newJob(SendWasteJob.class).withIdentity("job_sendwaste", "group_sendwaste").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 22 && cal.get(Calendar.MINUTE) > 30) {//saat 22:30 i geçti ise ertesi gün 22:30 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendwaste", "group_sendwaste")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListCampaignJob() {
        JobDetail job = newJob(ListCampaignJob.class).withIdentity("job_listcampaign", "group_listcampaign").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 28);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listcampaign", "group_listcampaign")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 2)// 2 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Logo için günde birkez çalışır. Resmi muhasebeye stok fişlerini gönderir.
     */
    public void createSendStockReceiptLogo() {
        JobDetail job = newJob(SendStockReceipt.class).withIdentity("job_sendstockreceipt", "group_sendstockreceipt").build();

        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 00);//birgün öncesiin verilerini gönderecek.
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_sendstockreceipt", "group_sendstockreceipt")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListAccountJob() {
        JobDetail job = newJob(ListAccountJob.class).withIdentity("job_listaccount", "group_listaccount").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 33);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listaccount", "group_listaccount")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 2)// 2 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createFile() {
        JobDetail job = newJob(CreateFileJob.class).withIdentity("job_listfile", "group_listfile").build();
        cal.setTime(new Date());

        if (cal.get(Calendar.HOUR_OF_DAY) > 0) {//saat 24 i geçti ise ertesi gün 24 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listfile", "group_listfile")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24) //Günde 1 kez
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListWasteReasonJob() {
        JobDetail job = newJob(ListWasteReasonJob.class).withIdentity("job_listwastereason", "group_listwastereason").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 45) {//saat 23:45 i geçti ise ertesi gün 23:45 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 45);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listwastereason", "group_listwastereason")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createListStarbucksStockJob() {
        JobDetail job = newJob(ListStarbucksStock.class).withIdentity("job_liststarbucksstock", "group_liststarbucksstock").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 15) {//saat 23:15 i geçti ise ertesi gün 23:15 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_liststarbucksstock", "group_liststarbucksstock")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListCentralSupplier() {
        JobDetail job = newJob(ListCentralSupplier.class).withIdentity("job_listcentralsuplier", "group_listcentralsuplier").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 25) {//saat 23:25 i geçti ise ertesi gün 23:25 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 25);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listcentralsuplier", "group_listcentralsuplier")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void callBranchInfo() {
        JobDetail job = newJob(CallBranchInfoJob.class).withIdentity("job_callbranchinfo", "group_callbranchinfo").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 40) {//saat 23:40 i geçti ise ertesi gün 23:40 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_callbranchinfo", "group_callbranchinfo")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createCreateOrderJob(ScheduledJobTrigger scheduledJobTrigger) throws ParseException {

        System.out.println("scheduledJobTrigger id" + scheduledJobTrigger.getId());
        String[] split = scheduledJobTrigger.getDays().split(",");
        String daysOfWeekString = "";
        int count = 0;
        for (int i = 0; i < split.length; i++) {
            String string = split[i];
            if (Integer.parseInt(string) == 1) {
                count++;
            }
            if (Integer.parseInt(string) == 1) {
                if (i == 0) {
                    daysOfWeekString = daysOfWeekString + "1,";
                } else if (i == 1) {
                    daysOfWeekString = daysOfWeekString + "2,";
                } else if (i == 2) {
                    daysOfWeekString = daysOfWeekString + "3,";
                } else if (i == 3) {
                    daysOfWeekString = daysOfWeekString + "4,";
                } else if (i == 4) {
                    daysOfWeekString = daysOfWeekString + "5,";
                } else if (i == 5) {
                    daysOfWeekString = daysOfWeekString + "6,";
                } else if (i == 6) {
                    daysOfWeekString = daysOfWeekString + "7,";
                }
            }
        }
        daysOfWeekString = daysOfWeekString.substring(0, daysOfWeekString.length() - 1);
        String[] split1 = daysOfWeekString.split(",");

        Integer[] daysOfWeek = new Integer[count];
        for (int i = 0; i < split1.length; i++) {
            daysOfWeek[i] = Integer.valueOf(split1[i]);
        }

        int hour = 0;
        int minute = 0;

        String[] daysDate = scheduledJobTrigger.getDaysDate().split(":");
        for (int i = 0; i < daysDate.length; i++) {
            String string = daysDate[i];
            if (i == 0) {
                if (Integer.parseInt(String.valueOf(string.charAt(0))) == 0) {
                    hour = Integer.parseInt(String.valueOf(string.charAt(1)));
                } else {
                    hour = Integer.parseInt(string);
                }
            }
            if (i == 1) {
                if (Integer.parseInt(String.valueOf(string.charAt(0))) == 0) {
                    minute = Integer.parseInt(String.valueOf(string.charAt(1)));
                } else {
                    minute = Integer.parseInt(string);
                }
            }

        }

        CronScheduleBuilder csb = CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(hour, minute, daysOfWeek);
        CronTrigger build = TriggerBuilder.newTrigger().withSchedule(csb).build();
        scheduledJobTrigger.setCronstring(build.getCronExpression());

        System.out.println("scheduledJobTrigger.getCronstring()" + scheduledJobTrigger.getCronstring());
        System.out.println("scheduledJobTrigger.getKey()" + scheduledJobTrigger.getKey());

        //JobDetail job = newJob(CreateOrderJob.class).withIdentity("job_createorder", "group_createorder").build();
        JobDetail job = newJob(CreateOrderJob.class).withIdentity(scheduledJobTrigger.getKey()).build();

        cal.setTime(new Date());
        System.out.println("cal.getTime()" + cal.getTime());
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_createorder_b" + scheduledJobTrigger.getBranch_id(), "group_createorder_b" + scheduledJobTrigger.getBranch_id())
                  .startAt(cal.getTime())
                  .withSchedule(CronScheduleBuilder.cronSchedule(scheduledJobTrigger.getCronstring()))
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createCreateOrderJob(int id) {

        ScheduledJobTrigger findScheduledJob = appService.findScheduledJob(id);
        if (findScheduledJob.getId() > 0) {
            try {
                createCreateOrderJob(findScheduledJob);
            } catch (ParseException ex) {
                Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void createListCurrencyJob() {
        JobDetail job = newJob(ListUnitJob.class).withIdentity("job_listcurrency", "group_listcurrency").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 22 && cal.get(Calendar.MINUTE) > 30) {//saat 22:30 i geçti ise ertesi gün 22:30 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listcurrency", "group_listcurrency")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createListHepsiBuradaJob() {
        JobDetail job = newJob(ListHepsiburadaJob.class).withIdentity("job_listhepsiburada", "group_listhepsiburada").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 40);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listhepsiburada", "group_listhepsiburada")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 1)// saatte 1 çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createAnalyzeReindexDatabase() {
        JobDetail job = newJob(CreateAnalyzeReIndexDatabaseJob.class).withIdentity("job_analyzereindexdatabase", "group_analyzereindexdatabase").build();

        cal.setTime(new Date());
        //cal.set(Calendar.MINUTE,0);
        if (cal.get(Calendar.HOUR_OF_DAY) >= 3 && cal.get(Calendar.MINUTE) > 0) {//saat 03:00 i geçti ise ertesi gün 03:00 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_analyzereindexdatabase", "group_analyzereindexdatabase")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            //.withIntervalInSeconds(60)
                            .withIntervalInSeconds(60 * 60 * 24)// günde 1 çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createVacuumDatabase() {
        JobDetail job = newJob(CreateVacuumDatabaseJob.class).withIdentity("job_vacuumdatabase", "group_vacuumdatabase").build();

        cal.setTime(new Date());
        //cal.set(Calendar.MINUTE,0);

        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            if (cal.get(Calendar.HOUR_OF_DAY) >= 4 && cal.get(Calendar.MINUTE) > 0) {//saat 04:00 i geçti ise ertesi pazar gün 04:00 da al
                cal.add(Calendar.DAY_OF_MONTH, 7);
            }
        } else {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }

        cal.set(Calendar.HOUR_OF_DAY, 4);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_vacuumdatabase", "group_vacuumdatabase")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            //.withIntervalInSeconds(60)
                            .withIntervalInSeconds(60 * 60 * 24 * 7)// 7 günde 1 çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void createListCampaignInfoJob() {
        JobDetail job = newJob(ListCampaignInfoJob.class).withIdentity("job_listcampaigninfo", "group_listcampaigninfo").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 30) {//saat 23:30 i geçti ise ertesi gün 23:30 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listcampaigninfo", "group_listcampaigninfo")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListParoCampaignInfoJob() {
        JobDetail job = newJob(CallCampaignInfoJob.class).withIdentity("job_listparocampaigninfo", "group_listparocampaigninfo").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) >= 23 && cal.get(Calendar.MINUTE) > 30) {//saat 23:30 i geçti ise ertesi gün 23:30 da al
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listparocampaigninfo", "group_listparocampaigninfo")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createFirstTriggerJob() {
        JobDetail job = newJob(CreateFirstTriggerJob.class).withIdentity("job_firsttrigger", "group_firsttriggerjob").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 10);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_firsttriggerjob", "group_firsttriggerjob")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListVideosJob() {
        JobDetail job = newJob(ListVideosJob.class).withIdentity("job_listvideos", "group_listvideos").build();

        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) > 23) {//saat 23 i geçti ise ertesi gün 23 de al
            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listvideos", "group_listvideos")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 24)// günde bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createListCentralCategoriesJob() {
        JobDetail job = newJob(ListCentralCategoriesJob.class).withIdentity("job_listcentralcategory", "group_listcentralcategory").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 50);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listcentralcategory", "group_listcentralcategory")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60 * 60 * 6)// 6 saatte bir çalış
                            .repeatForever())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     private void createListCentralCategoriesJobTemp() {
        JobDetail job = newJob(ListCentralCategoriesJob.class).withIdentity("job_listcentralcategorytemp", "group_listcentralcategorytemp").build();

        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 1);

        Trigger trigger = newTrigger()
                  .withIdentity("trigger_listcentralcategorytemp", "group_listcentralcategorytemp")
                  .startAt(cal.getTime())
                  .withSchedule(simpleSchedule())
                  .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
