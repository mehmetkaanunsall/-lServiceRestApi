/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 07.05.2019 10:31:45
 */
package com.mepsan.marwiz.system.sapintegration.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.system.sapintegration.business.SapIntegration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import com.mepsan.marwiz.system.sapintegration.business.ISapIntegrationService;
import java.util.ArrayList;
import java.util.Arrays;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class SapIntegrationBean extends GeneralReportBean<SapIntegration> {

    @ManagedProperty(value = "#{sapIntegrationService}")
    private ISapIntegrationService sapIntegrationService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private Date beginDate, endDate;
    private int processType;//1:tahsilat 2:kadasan bankaya çıkış
    private boolean isFind;
    private String isSend;
    private List<SapIntegration> listOfSap, selectedSap;

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<SapIntegration> getSelectedSap() {
        return selectedSap;
    }

    public void setSelectedSap(List<SapIntegration> selectedSap) {
        this.selectedSap = selectedSap;
    }

    public List<SapIntegration> getListOfSap() {
        return listOfSap;
    }

    public void setListOfSap(List<SapIntegration> listOfSap) {
        this.listOfSap = listOfSap;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSapIntegrationService(ISapIntegrationService sapIntegrationService) {
        this.sapIntegrationService = sapIntegrationService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-------------------SocarIntegrationBean");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        cal.add(Calendar.MONTH, -1);
        beginDate = cal.getTime();

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true);
        selectedSap = new ArrayList<>();
        processType = 1;
        isSend = "0";
    }

    @Override
    public void save() {

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void find() {
        isFind = true;
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSapIntegrationDatatable:dtbSapIntegration");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        int send = Integer.parseInt(isSend);

        if (processType == 1) {
            listOfSap = sapIntegrationService.listOfCollections(beginDate, endDate, send);
        } else {
            listOfSap = sapIntegrationService.listOfSafeToBank(beginDate, endDate, send);
        }

        RequestContext.getCurrentInstance().update("frmSapIntegrationDatatable:dtbSapIntegration");
    }

    @Override
    public LazyDataModel<SapIntegration> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Bu metot seçilen listeyi sap web servisine gönderir
     */
    public void sendIntegration() {
        int result = 0;
        if (processType == 1) {
            result = sapIntegrationService.sendCollections(selectedSap) ? 1 : 0;

        } else {
            result = sapIntegrationService.sendSafeToBank(selectedSap) ? 1 : 0;
        }
//        sessionBean.createUpdateMessage(result);
        
        

        selectedSap.clear();
        find();//tabloyu güncelle
    }
}
