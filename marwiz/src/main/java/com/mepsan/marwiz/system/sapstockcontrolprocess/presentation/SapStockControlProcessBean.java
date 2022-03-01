/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.presentation;

import com.google.common.collect.Lists;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.sapstockcontrolprocess.business.ISapStockControlProcessService;
import com.mepsan.marwiz.system.sapstockcontrolprocess.dao.SapStockControlProcess;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class SapStockControlProcessBean extends GeneralDefinitionBean<SapStockControlProcess> {

    @ManagedProperty(value = "#{sapStockControlProcessService}")
    private ISapStockControlProcessService sapStockControlProcessService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private Date date;
    private boolean isFind;
    private SapStockControlProcess sapStockControlProcess;
    private List<SapStockControlProcess> listOfDifferentStocks;
    private int differenceReasonType;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public void setSapStockControlProcessService(ISapStockControlProcessService sapStockControlProcessService) {
        this.sapStockControlProcessService = sapStockControlProcessService;
    }

    public SapStockControlProcess getSapStockControlProcess() {
        return sapStockControlProcess;
    }

    public void setSapStockControlProcess(SapStockControlProcess sapStockControlProcess) {
        this.sapStockControlProcess = sapStockControlProcess;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<SapStockControlProcess> getListOfDifferentStocks() {
        return listOfDifferentStocks;
    }

    public void setListOfDifferentStocks(List<SapStockControlProcess> listOfDifferentStocks) {
        this.listOfDifferentStocks = listOfDifferentStocks;
    }

    public int getDifferenceReasonType() {
        return differenceReasonType;
    }

    public void setDifferenceReasonType(int differenceReasonType) {
        this.differenceReasonType = differenceReasonType;
    }

    @PostConstruct
    @Override
    public void init() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(calendar.getTime());
        setDate(calendar.getTime());

        sapStockControlProcess = new SapStockControlProcess();
        listOfDifferentStocks = new ArrayList<>();
        listOfObjects = new ArrayList<>();
        toogleList = new ArrayList<>();
        differenceReasonType = 1;

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);

    }

    public void find() {

        sapStockControlProcess = new SapStockControlProcess();
        isFind = true;
        findall();

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSapStockControlProcessDatatable:dtbSapStockControlProcessIntegration");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        RequestContext.getCurrentInstance().update("frmSapStockControlProcessDatatable:dtbSapStockControlProcessIntegration");

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SapStockControlProcess> findall() {
        if (!listOfObjects.isEmpty()) {
            listOfObjects.clear();
        }
        sapStockControlProcess = sapStockControlProcessService.getSapStockInfos(date);
        if (sapStockControlProcess.isIsSuccess()) {
            if (!sapStockControlProcess.getItemJson().isEmpty()) {

                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.set(Calendar.HOUR_OF_DAY, 23);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);

                if (differenceReasonType != 4) {
                    listOfObjects = sapStockControlProcessService.compareStockInfos(sapStockControlProcess, c.getTime(), differenceReasonType);
                } else {

                    for (int i = 1; i <= 3; i++) {
                        listOfDifferentStocks = new ArrayList<>();
                        listOfDifferentStocks = sapStockControlProcessService.compareStockInfos(sapStockControlProcess, c.getTime(), i);
                        listOfObjects.addAll(listOfDifferentStocks);
                    }

                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), "  " + sessionBean.loc.getString("succesfuloperation")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), "  " + sessionBean.loc.getString("stockinformationnotsentfromsap")));
                RequestContext.getCurrentInstance().update("grwSapStockControlProcessMessage");
            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sapStockControlProcess.getMessage()));
            RequestContext.getCurrentInstance().update("grwSapStockControlProcessMessage");

        }

        return listOfObjects;

    }

    public void createExcel() throws IOException {
        sapStockControlProcessService.exportExcel(toogleList, listOfObjects, date, differenceReasonType);
    }

    public void createPdf() throws IOException {
        sapStockControlProcessService.exportPdf(toogleList, listOfObjects, date, differenceReasonType);
    }

}
