/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 08:36:15
 */
package com.mepsan.marwiz.finance.discount.presentation;

import com.mepsan.marwiz.finance.discount.business.IDiscountBranchTabService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.IHistoryService;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountBranchConnection;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class DiscountBranchTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{discountBranchTabService}")
    private IDiscountBranchTabService discountBranchTabService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{historyService}")
    public IHistoryService historyService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDiscountBranchTabService(IDiscountBranchTabService discountBranchTabService) {
        this.discountBranchTabService = discountBranchTabService;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    private DiscountBranchConnection selectedObject;

    public void setHistoryService(IHistoryService historyService) {
        this.historyService = historyService;
    }

    private Discount selectedDiscount;
    private List<DiscountBranchConnection> listOfObject;
    private List<Branch> branchList;

    private int processType;

    private List<History> listOfHistoryObjects;
    private String createdPerson;
    private String createdDate;

    public DiscountBranchConnection getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(DiscountBranchConnection selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<DiscountBranchConnection> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<DiscountBranchConnection> listOfObject) {
        this.listOfObject = listOfObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    public List<History> getListOfHistoryObjects() {
        return listOfHistoryObjects;
    }

    public void setListOfHistoryObjects(List<History> listOfHistoryObjects) {
        this.listOfHistoryObjects = listOfHistoryObjects;
    }

    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Discount getSelectedDiscount() {
        return selectedDiscount;
    }

    public void setSelectedDiscount(Discount selectedDiscount) {
        this.selectedDiscount = selectedDiscount;
    }

    @PostConstruct
    public void init() {
        System.out.println("----DiscountBranchTabBean");
        if (sessionBean.parameter instanceof Discount) {
            selectedDiscount = (Discount) sessionBean.parameter;
            selectedObject = new DiscountBranchConnection();
            find();
            branchList = new ArrayList<>();
        }

        setListBtn(sessionBean.checkAuthority(new int[]{107, 108, 109}, 0));
    }

    public void find() {
        listOfObject = discountBranchTabService.listOfDiscountBranch(selectedDiscount);
    }

    public void createDialog(int type) {
        if (!selectedDiscount.isIsAllBranch()) {

            processType = type;
            branchList = branchService.selectBranchs();
            RequestContext context = RequestContext.getCurrentInstance();
            if (processType == 1) { //ekle
                selectedObject = new DiscountBranchConnection();
            } else if (processType == 2) {

            }
            context.execute("PF('dlg_BranchProcess').show()");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("youdontselectbranchbecauseofallbranchissigned")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void save() {
        int result = 0;
        boolean isThere = false;
        selectedObject.setDiscount(selectedDiscount);
        for (DiscountBranchConnection discountBranchConnection : listOfObject) {
            if (discountBranchConnection.getBranch().getId() == selectedObject.getBranch().getId() && selectedObject.getId() != discountBranchConnection.getId()) {
                isThere = true;
            }

        }
        if (!isThere) {
            if (processType == 1) {
                result = discountBranchTabService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObject.add(selectedObject);
                }
            } else {
                result = discountBranchTabService.update(selectedObject);
            }
            if (result > 0) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlg_BranchProcess').hide();");
                find();
                context.update("tbvDiscount:frmDiscountBranchTab:dtbDiscountBranchTab");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisbranchhasbeenaddedbefore")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

    public void testBeforeDelete() {
        int result = 0;
        result = discountBranchTabService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmCustomerProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("campaigndetailslinkedtothiscampaigncannotbedeletedbecauseithasacurrentorcurrentcategory")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = discountBranchTabService.delete(selectedObject);
        if (result > 0) {
            listOfObject.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_BranchProcess').hide();");
            context.update("tbvDiscount:frmDiscountBranchTab:dtbDiscountBranchTab");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void goToHistory() {
        listOfHistoryObjects = historyService.findAll(0, 0, null, "", selectedObject.getId(), "finance.discount_branch_con", 0);
        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDateCreated());
        try {
            createdPerson = selectedObject.getUserCreated().getFullName() + " - " + selectedObject.getUserCreated().getUsername();

        } catch (Exception e) {
        }
        RequestContext.getCurrentInstance().execute("PF('ovlHistory').loadContents()");

    }

}
