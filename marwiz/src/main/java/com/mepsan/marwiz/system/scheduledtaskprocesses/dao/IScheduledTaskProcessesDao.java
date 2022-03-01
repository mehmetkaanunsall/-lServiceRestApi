/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.dao;

import com.mepsan.marwiz.general.model.general.ScheduledTaskProcesses;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IScheduledTaskProcessesDao extends ICrud<ScheduledTaskProcesses> {

    public List<ScheduledTaskProcesses> findAll();

    public int delete(ScheduledTaskProcesses obj);

}
