/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.zseries.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.ZSeries;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.zseries.business.IZSeriesService;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author m.duzoylum
 */
@ManagedBean
@ViewScoped
public class ZSeriesBean extends GeneralDefinitionBean<ZSeries> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{zSeriesService}")
    private IZSeriesService zSeriesService;

    private int processType;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IZSeriesService getzSeriesService() {
        return zSeriesService;
    }

    public void setzSeriesService(IZSeriesService zSeriesService) {
        this.zSeriesService = zSeriesService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------Z Series Bean--------");
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{325, 326, 327}, 0));

    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new ZSeries();
        RequestContext.getCurrentInstance().execute("PF('dlg_zseriesProcess').show();");
    }

    public void update() {
        processType = 2;
        RequestContext.getCurrentInstance().execute("PF('dlg_zseriesProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (processType == 1) {
            result = zSeriesService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
                RequestContext.getCurrentInstance().execute("PF('dlg_zseriesProcess').hide();");
            }
        } else if (processType == 2) {
            result = zSeriesService.update(selectedObject);
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('dlg_zseriesProcess').hide();");
            }
        }

        if (result > 0) {
            RequestContext.getCurrentInstance().update("frmZseries:dtbZseries");
            RequestContext.getCurrentInstance().execute("PF('zseriesPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void delete() {
        int result = 0;
        result = zSeriesService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_zseriesProcess').hide();");
            context.update("frmZseries:dtbZseries");
            context.execute("PF('zseriesPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<ZSeries> findall() {
        return zSeriesService.listofZseries(sessionBean.getUser().getLastBranch().getId());
    }

}
