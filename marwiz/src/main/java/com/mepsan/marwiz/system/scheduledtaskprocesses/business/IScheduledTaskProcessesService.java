/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.business;

import com.mepsan.marwiz.general.model.general.ScheduledTaskProcesses;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.system.scheduledtaskprocesses.dao.ScheduledTaskProcessesDay;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IScheduledTaskProcessesService extends ICrudService<ScheduledTaskProcesses> {

    public List<ScheduledTaskProcesses> findAll();

    public List<ScheduledTaskProcessesDay> listDay();

    public ScheduledTaskProcesses createDaysEndDate(ScheduledTaskProcesses obj, List<ScheduledTaskProcessesDay> listOfDay);

    public List<ScheduledTaskProcesses> convertDaysEndDate(List<ScheduledTaskProcesses> list, List<ScheduledTaskProcessesDay> listOfDays);

    public int delete(ScheduledTaskProcesses obj);
}
