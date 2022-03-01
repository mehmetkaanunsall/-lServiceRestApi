/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.ScheduledTaskProcesses;
import com.mepsan.marwiz.system.scheduledtaskprocesses.dao.ScheduledTaskProcessesDao;
import com.mepsan.marwiz.system.scheduledtaskprocesses.dao.ScheduledTaskProcessesDay;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.Application;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class ScheduledTaskProcessesService implements IScheduledTaskProcessesService {

    @Autowired
    public ScheduledTaskProcessesDao scheduledTaskProcessesDao;

    @Autowired
    public SessionBean sessionBean;
    
    @Autowired
    public ApplicationBean applicationBean;

    public void setScheduledTaskProcessesDao(ScheduledTaskProcessesDao scheduledTaskProcessesDao) {
        this.scheduledTaskProcessesDao = scheduledTaskProcessesDao;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    @Override
    public int create(ScheduledTaskProcesses obj) {
        int create = scheduledTaskProcessesDao.create(obj);
        applicationBean.createCreateOrderJob(create);
        return create;
    }

    @Override
    public int update(ScheduledTaskProcesses obj) {
        int update = scheduledTaskProcessesDao.update(obj);
        try {
            applicationBean.getScheduler().deleteJob(new JobKey(obj.getId() + "_" +obj.getBranch().getId()));
            applicationBean.createCreateOrderJob(obj.getId());
        } catch (SchedulerException ex) {
            Logger.getLogger(ScheduledTaskProcessesService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return update;
    }

    @Override
    public List<ScheduledTaskProcesses> findAll() {
        return scheduledTaskProcessesDao.findAll();
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ScheduledTaskProcessesDay> listDay() {

        List<ScheduledTaskProcessesDay> dayLists = new ArrayList<>();

        ScheduledTaskProcessesDay obj = new ScheduledTaskProcessesDay();
        obj.setId(1);
        obj.setName(sessionBean.getLoc().getString("sunday"));
        dayLists.add(obj);

        ScheduledTaskProcessesDay obj1 = new ScheduledTaskProcessesDay();
        obj1.setId(2);
        obj1.setName(sessionBean.getLoc().getString("monday"));
        dayLists.add(obj1);

        ScheduledTaskProcessesDay obj2 = new ScheduledTaskProcessesDay();
        obj2.setId(3);
        obj2.setName(sessionBean.getLoc().getString("tuesday"));
        dayLists.add(obj2);

        ScheduledTaskProcessesDay obj3 = new ScheduledTaskProcessesDay();
        obj3.setId(4);
        obj3.setName(sessionBean.getLoc().getString("wednesday"));
        dayLists.add(obj3);

        ScheduledTaskProcessesDay obj4 = new ScheduledTaskProcessesDay();
        obj4.setId(5);
        obj4.setName(sessionBean.getLoc().getString("thursday"));
        dayLists.add(obj4);

        ScheduledTaskProcessesDay obj5 = new ScheduledTaskProcessesDay();
        obj5.setId(6);
        obj5.setName(sessionBean.getLoc().getString("friday"));
        dayLists.add(obj5);

        ScheduledTaskProcessesDay obj6 = new ScheduledTaskProcessesDay();
        obj6.setId(7);
        obj6.setName(sessionBean.getLoc().getString("saturday"));
        dayLists.add(obj6);

        return dayLists;
    }

    @Override
    public ScheduledTaskProcesses createDaysEndDate(ScheduledTaskProcesses obj, List<ScheduledTaskProcessesDay> listOfDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String days = "";
        for (ScheduledTaskProcessesDay schDay : listOfDay) {
            if (schDay.isIsSelected()) {
                days = days + "," + "1";
            } else {
                days = days + "," + "0";

            }
        }

        obj.setDays(days.substring(1, days.length()));
        obj.setDaysDate(sdf.format(obj.getWorkingTime()));
        return obj;
    }

    @Override
    public List<ScheduledTaskProcesses> convertDaysEndDate(List<ScheduledTaskProcesses> list, List<ScheduledTaskProcessesDay> listOfDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (ScheduledTaskProcesses schd : list) {
            String days = "";
            if (schd.getDays() != null && !schd.getDays().isEmpty()) {
                String[] words = schd.getDays().split(",");

                for (int i = 0; i < listOfDays.size(); i++) {
                    for (int j = 0; j < words.length; j++) {
                        if (i == j && words[j].toString().equalsIgnoreCase("1")) {
                            days = days + " , " + listOfDays.get(i).getName();
                            break;
                        }

                    }
                }

            }
            if (!days.isEmpty()) {

                schd.setConvertDays(days.substring(3, days.length()));

            }

            if (schd.getDaysDate() != null && !schd.getDaysDate().isEmpty()) {
                try {
                    schd.setWorkingTime(sdf.parse(schd.getDaysDate()));
                } catch (ParseException ex) {
                    Logger.getLogger(ScheduledTaskProcessesService.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        return list;
    }

    @Override
    public int delete(ScheduledTaskProcesses obj) {
        try {
            applicationBean.getScheduler().deleteJob(new JobKey(obj.getId() + "_" +obj.getBranch().getId()));
        } catch (SchedulerException ex) {
            Logger.getLogger(ScheduledTaskProcessesService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scheduledTaskProcessesDao.delete(obj);
    }

}
