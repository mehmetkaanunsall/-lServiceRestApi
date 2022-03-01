/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.ScheduledTaskProcesses;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.service.order.business.ICreateOrderService;
import com.mepsan.marwiz.system.scheduledtaskprocesses.business.IScheduledTaskProcessesService;
import com.mepsan.marwiz.system.scheduledtaskprocesses.dao.ScheduledTaskProcessesDay;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class ScheduledTaskProcessesBean extends GeneralDefinitionBean<ScheduledTaskProcesses> {

    @ManagedProperty(value = "#{scheduledTaskProcessesService}")
    public IScheduledTaskProcessesService scheduledTaskProcessesService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;
    
    @ManagedProperty(value = "#{createOrderService}")
    public ICreateOrderService createOrderService;

    private int processType;
    private List<Type> listOfType;
    private List<ScheduledTaskProcessesDay> listOfDays;
    private Date workingTime;

    public void setScheduledTaskProcessesService(IScheduledTaskProcessesService scheduledTaskProcessesService) {
        this.scheduledTaskProcessesService = scheduledTaskProcessesService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public List<ScheduledTaskProcessesDay> getListOfDays() {
        return listOfDays;
    }

    public void setListOfDays(List<ScheduledTaskProcessesDay> listOfDays) {
        this.listOfDays = listOfDays;
    }


    public Date getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Date workingTime) {
        this.workingTime = workingTime;
    }

    public void setCreateOrderService(ICreateOrderService createOrderService) {
        this.createOrderService = createOrderService;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------ScheduledTaskProcessesBean-------");
        selectedObject = new ScheduledTaskProcesses();
        listOfType = new ArrayList<>();
        listOfDays = new ArrayList<>();

        Calendar calendarstart = Calendar.getInstance();
        int year = calendarstart.get(Calendar.YEAR);
        int month = calendarstart.get(Calendar.MONTH);
        int day = calendarstart.get(Calendar.DATE);
        calendarstart.set(year, month, day, 00, 00, 00);
        workingTime = calendarstart.getTime();

        setListBtn(sessionBean.checkAuthority(new int[]{335, 336, 337, 341}, 0));

        listOfType = sessionBean.getTypes(41);
        listOfDays = scheduledTaskProcessesService.listDay();

        listOfObjects = findall();
        listOfObjects = scheduledTaskProcessesService.convertDaysEndDate(listOfObjects, listOfDays);

    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new ScheduledTaskProcesses();

        Calendar calendarstart = Calendar.getInstance();
        int year = calendarstart.get(Calendar.YEAR);
        int month = calendarstart.get(Calendar.MONTH);
        int day = calendarstart.get(Calendar.DATE);
        calendarstart.set(year, month, day, 00, 00, 00);
        selectedObject.setWorkingTime(calendarstart.getTime());
        RequestContext.getCurrentInstance().update("dlgScheduledTaskProc");
        RequestContext.getCurrentInstance().execute("PF('dlg_scheduledtaskproc').show();");
    }

    @Override
    public void save() {
        selectedObject = scheduledTaskProcessesService.createDaysEndDate(selectedObject, listOfDays);
        int result = 0;
        boolean isThere = false;

        for (ScheduledTaskProcesses sch : listOfObjects) {
            if (sch.getType().getId() == selectedObject.getType().getId() && sch.getStatus().getId() == 62 && sch.getId() != selectedObject.getId() && selectedObject.getStatus().getId() != 63) {
                isThere = true;
                break;
            }
        }
        if (isThere) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("thesametypehasanactivescheduledjobdescriptionsavingisnotpossible")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            if (processType == 1) {
                result = scheduledTaskProcessesService.create(selectedObject);
            } else if (processType == 2) {
                result = scheduledTaskProcessesService.update(selectedObject);
            }
            if (result > 0) {
                listOfObjects = findall();
                listOfObjects = scheduledTaskProcessesService.convertDaysEndDate(listOfObjects, listOfDays);
                RequestContext.getCurrentInstance().execute("PF('dlg_scheduledtaskproc').hide();");
                RequestContext.getCurrentInstance().execute("PF('scheduledTaskPF').filter();");
                RequestContext.getCurrentInstance().update("frmScheduledTaskDefinition:dtbScheduledTaskDefinition");
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    @Override
    public List<ScheduledTaskProcesses> findall() {
        return scheduledTaskProcessesService.findAll();
    }

    public void update() throws ParseException {
        processType = 2;

        if (selectedObject.getDays() != null && !selectedObject.getDays().isEmpty()) {
            String[] words = selectedObject.getDays().split(",");
            for (int i = 0; i < listOfDays.size(); i++) {
                for (int j = 0; j < words.length; j++) {
                    if (i == j && words[j].toString().equalsIgnoreCase("1")) {
                        listOfDays.get(i).setIsSelected(true);
                    }
                }
            }
        }
        RequestContext.getCurrentInstance().update("frmScheduledTaskDefinition");
        RequestContext.getCurrentInstance().update("frmDefinitionProcess");
        RequestContext.getCurrentInstance().update("dlgScheduledTaskProc");
        RequestContext.getCurrentInstance().execute("PF('dlg_scheduledtaskproc').show();");

    }

    public void delete() {
        int result = 0;
        result = scheduledTaskProcessesService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_scheduledtaskproc').hide();");
            context.update("frmScheduledTaskDefinition:dtbScheduledTaskDefinition");
            context.execute("PF('scheduledTaskPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }


    public void closeDialog() {
        for (ScheduledTaskProcessesDay days : listOfDays) {
            days.setIsSelected(false);
        }
    }
    
    public void goToOperateManually(){
        int createOrderJob = createOrderService.createOrderJob(selectedObject.getBranch().getId());
        if(createOrderJob>0){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("ordercreated")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }else if (createOrderJob==-100 || createOrderJob==-1){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("noproducttoorder")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }else{
            sessionBean.createUpdateMessage(createOrderJob);
        }
    }

}
