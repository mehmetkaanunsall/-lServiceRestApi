/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asli.can
 */
package com.mepsan.marwiz.system.branch.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.branch.business.IBranchIntegrationService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

@ManagedBean
@ViewScoped
public class BranchIntegrationTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchIntegrationService}")
    public IBranchIntegrationService branchIntegrationService;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    private List<BranchIntegration> listOfIntegration;

    private BranchIntegration selectedObject;
    private int processType;
    private List<Branch> branchList;
    private Branch selectedBranch;
    private boolean isAvailableHepsiburadaBegin;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchIntegrationService(IBranchIntegrationService branchIntegrationService) {
        this.branchIntegrationService = branchIntegrationService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<BranchIntegration> getListOfIntegration() {
        return listOfIntegration;
    }

    public void setListOfIntegration(List<BranchIntegration> listOfIntegration) {
        this.listOfIntegration = listOfIntegration;
    }

    public BranchIntegration getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(BranchIntegration selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    @PostConstruct
    public void init() {
        System.out.println("----------BranchIntegrationTabBean--------");

        listOfIntegration = new ArrayList<>();
        selectedObject = new BranchIntegration();

        if (sessionBean.parameter instanceof Branch) {
            selectedBranch = (Branch) sessionBean.parameter;

        }

        listOfIntegration = branchIntegrationService.listOfIntegration(selectedBranch);

        if (applicationBean.getAppService().controlBranchIntegration() == 1) {
            isAvailableHepsiburadaBegin = true;
        }

        setListBtn(sessionBean.checkAuthority(new int[]{346, 347, 348}, 0));
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void createDialog(int type) {
        processType = type;

        if (processType == 1) {

            selectedObject = new BranchIntegration();
            selectedObject.getBranch().setId(selectedBranch.getId());
            selectedObject.setIntegrationtype(1);
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_BranchIntegration').show()");
        RequestContext.getCurrentInstance().update("frmBranchIntegrationProcess");

    }

    public void save() {

        int result = 0;
        boolean isAvailableHepsiburadaEnd = false;
        boolean isIntegration = false;//kayıt eklenecekse false
        boolean isIntegrationCoffee = false;

        for (BranchIntegration b : listOfIntegration) {
            if (b.getIntegrationtype() == selectedObject.getIntegrationtype() && b.getId() != selectedObject.getId()) {
                isIntegration = true;//mesaj verilecekse true
                break;
            }
            if ((b.getIntegrationtype() == 2 && selectedObject.getIntegrationtype() == 3 && b.getId() != selectedObject.getId()) || (b.getIntegrationtype() == 3 && selectedObject.getIntegrationtype() == 2 && b.getId() != selectedObject.getId())) {
                isIntegrationCoffee = true;
                break;
            }
        }
        if (isIntegration) {//aynı tipte kayıt eklenmemesi için
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                      sessionBean.loc.getString("warning"), sessionBean.loc.getString("thistypeofrecordingisavailableinthesystem"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("grwProcessMessage");

        } else if (isIntegrationCoffee) {//gloria ve starbucks eklenmemesi için
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                      sessionBean.loc.getString("warning"), sessionBean.loc.getString("starbucksandgloriatypescannotbeaddtothesysteminthesametime"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("grwProcessMessage");

        } else if (!isIntegration) {
            if (processType == 1) {

                result = branchIntegrationService.create(selectedObject);
                if (result > 0) {

                    selectedObject.setId(result);
                    listOfIntegration.add(selectedObject);
                }
            } else if (processType == 2) {

                result = branchIntegrationService.update(selectedObject);
            }

            if (result > 0) {

                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlg_BranchIntegration').hide();");
                context.update("tbvBranchProcess:frmIntegration:dtbIntegration");
                if (isAvailableHepsiburadaBegin) {
                    for (BranchIntegration br : listOfIntegration) {
                        if (br.getIntegrationtype() == 1) {
                            isAvailableHepsiburadaEnd = true;
                            break;
                        }
                    }
                    if (!isAvailableHepsiburadaEnd) {
                        try {
                            applicationBean.getScheduler().deleteJob(new JobKey("job_listhepsiburada", "group_listhepsiburada"));
                            TriggerKey triggerKey = new TriggerKey("trigger_listhepsiburada", "group_listhepsiburada");
                            applicationBean.getScheduler().unscheduleJob(triggerKey);
                        } catch (SchedulerException ex) {
                            Logger.getLogger(BranchIntegrationTabBean.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                } else {
                    for (BranchIntegration br : listOfIntegration) {
                        if (br.getIntegrationtype() == 1) {
                            isAvailableHepsiburadaEnd = true;
                            break;
                        }
                    }
                    if (isAvailableHepsiburadaEnd) {
                        applicationBean.createListHepsiBuradaJob();
                    }
                }
                if (applicationBean.getAppService().controlBranchIntegration() == 1) {
                    isAvailableHepsiburadaBegin = true;
                } else {
                    isAvailableHepsiburadaBegin = false;
                }
            }

            sessionBean.createUpdateMessage(result);
        }

    }

    public void delete() {
        int result = 0;
        boolean isAvailableHepsiburadaEnd = false;
        result = branchIntegrationService.delete(selectedObject);

        if (result > 0) {
            listOfIntegration.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_BranchIntegration').hide();");
            context.update("tbvBranchProcess:frmIntegration:dtbIntegration");
            if (isAvailableHepsiburadaBegin) {
                for (BranchIntegration br : listOfIntegration) {
                    if (br.getIntegrationtype() == 1) {
                        isAvailableHepsiburadaEnd = true;
                        break;
                    }
                }
                if (!isAvailableHepsiburadaEnd) {
                    try {
                        applicationBean.getScheduler().deleteJob(new JobKey("job_listhepsiburada", "group_listhepsiburada"));
                        TriggerKey triggerKey = new TriggerKey("trigger_listhepsiburada", "group_listhepsiburada");
                        applicationBean.getScheduler().unscheduleJob(triggerKey);
                    } catch (SchedulerException ex) {
                        Logger.getLogger(BranchIntegrationTabBean.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } else {
                for (BranchIntegration br : listOfIntegration) {
                    if (br.getIntegrationtype() == 1) {
                        isAvailableHepsiburadaEnd = true;
                        break;
                    }
                }
                if (isAvailableHepsiburadaEnd) {
                    applicationBean.createListHepsiBuradaJob();
                }
            }
            if (applicationBean.getAppService().controlBranchIntegration() == 1) {
                isAvailableHepsiburadaBegin = true;
            } else {
                isAvailableHepsiburadaBegin = false;
            }
        }
        sessionBean.createUpdateMessage(result);

    }

}
