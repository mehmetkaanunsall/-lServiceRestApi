/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.12.2019 01:51:59
 */
package com.mepsan.marwiz.inventory.taxdepartment.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.HistoryService;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import com.mepsan.marwiz.inventory.taxdepartment.business.ITaxDepartmentService;
import java.util.Date;

@ManagedBean
@ViewScoped
public class TaxDepartmentBean extends GeneralDefinitionBean<TaxDepartment> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{taxDepartmentService}")
    private ITaxDepartmentService taxDepartmentService;

    @ManagedProperty(value = "#{taxGroupService}")
    private ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{historyService}")
    private HistoryService historyService;

    private int processType;
    private List<TaxGroup> listOfTaxGroup;

    private List<History> listOfHistoryObjects;
    private String createdDate;
    private String createdPerson;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setTaxDepartmentService(ITaxDepartmentService taxDepartmentService) {
        this.taxDepartmentService = taxDepartmentService;
    }

    public List<TaxGroup> getListOfTaxGroup() {
        return listOfTaxGroup;
    }

    public void setListOfTaxGroup(List<TaxGroup> listOfTaxGroup) {
        this.listOfTaxGroup = listOfTaxGroup;
    }

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    public List<History> getListOfHistoryObjects() {
        return listOfHistoryObjects;
    }

    public void setListOfHistoryObjects(List<History> listOfHistoryObjects) {
        this.listOfHistoryObjects = listOfHistoryObjects;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("----------TaxDepartmentBean--------");

        listOfTaxGroup = new ArrayList<>();
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{292, 293, 294}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new TaxDepartment();
        listOfTaxGroup = taxGroupService.findAll();

        RequestContext.getCurrentInstance().execute("PF('dlg_TaxDepartmentProcess').show();");

    }

    public void update() {
        processType = 2;
        listOfTaxGroup = taxGroupService.findAll();
        listOfHistoryObjects = new ArrayList<>();

        RequestContext.getCurrentInstance().execute("PF('dlg_TaxDepartmentProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;
        boolean isThere = false;

        selectedObject.setUserCreated(sessionBean.getUser());
        selectedObject.setDateCreated(new Date());

        for (TaxDepartment tempTaxDe : listOfObjects) {
            if (tempTaxDe.getDepartmentNo() == selectedObject.getDepartmentNo() && tempTaxDe.getId() != selectedObject.getId()) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisdepartmentnoisavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
        if (!isThere) {
            if (selectedObject.getDepartmentNo() <= 8 && selectedObject.getDepartmentNo() >= 1) {
                if (processType == 1) {
                    result = taxDepartmentService.create(selectedObject);
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);

                } else if (processType == 2) {
                    result = taxDepartmentService.update(selectedObject);

                }

                if (result > 0) {
                    bringTaxGroup();
                    RequestContext.getCurrentInstance().execute("PF('dlg_TaxDepartmentProcess').hide();");
                    RequestContext.getCurrentInstance().update("frmTaxDepartment:dtbTaxDepartment");
                    RequestContext.getCurrentInstance().execute("PF('taxDepartmentPF').filter();");
                }
                sessionBean.createUpdateMessage(result);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("departmentnomustbebetween18")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }

    }

    @Override
    public List<TaxDepartment> findall() {
        return taxDepartmentService.listOfTaxDepartment();
    }

    public void bringTaxGroup() {
        for (TaxGroup taxGroup : listOfTaxGroup) {
            if (taxGroup.getId() == selectedObject.getTaxGroup().getId()) {
                selectedObject.getTaxGroup().setName(taxGroup.getName());
            }
        }
    }

    public void testBeforeDelete() {
        int result = 0;
        result = taxDepartmentService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmTaxDepartmentProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausedepartmentisrelatedtostock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = taxDepartmentService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_TaxDepartmentProcess').hide();");
            context.update("frmTaxDepartment:dtbTaxDepartment");
            context.execute("PF('taxDepartmentPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void beforeChangeStockTaxDepartment() {
        RequestContext.getCurrentInstance().update("dlgChangeStockTaxDepartment");
        RequestContext.getCurrentInstance().execute("PF('dlg_ChangeStockTaxDepartment').show();");
    }

    public void changeStockTaxDepartment() {
        int result = 0;
        result = taxDepartmentService.changeStockTaxDepartment();
        sessionBean.createUpdateMessage(result);
    }

    public void goToHistory() {
        listOfHistoryObjects = historyService.findAll(0, 0, null, "", selectedObject.getId(), "inventory.taxdepartment", 0);
        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDateCreated());
        createdPerson = selectedObject.getUserCreated().getFullName() + " - " + selectedObject.getUserCreated().getUsername();
        RequestContext.getCurrentInstance().execute("PF('ovlHistory').loadContents()");
    }
}
