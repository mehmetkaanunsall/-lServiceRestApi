package com.mepsan.marwiz.system.unsuccessfulsalesprocess.presantation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UnsuccessfulSalesProcess;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import com.mepsan.marwiz.system.unsuccessfulsalesprocess.business.IUnsuccessfulSalesProcessService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class UnsuccessfulSalesProcessBean extends GeneralReportBean<UnsuccessfulSalesProcess> {

    @ManagedProperty(value = "#{unsuccessfulSalesProcessService}")
    private IUnsuccessfulSalesProcessService unsuccessfulSalesProcessService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private List<BranchSetting> listOfBranch;
    private BranchSetting selectedBranch;
    private String branchList;

    public void setUnsuccessfulSalesProcessService(IUnsuccessfulSalesProcessService unsuccessfulSalesProcessService) {
        this.unsuccessfulSalesProcessService = unsuccessfulSalesProcessService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public BranchSetting getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(BranchSetting selectedBranch) {
        this.selectedBranch = selectedBranch;
    }

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    @Override
    @PostConstruct
    public void init() {

        System.out.println("--------UnsuccessfulSalesProcessBean----");
        branchList = "";
        isFind = false;
        listOfBranch = new ArrayList<>();
        selectedBranch = new BranchSetting();
        listOfBranch = branchSettingService.findUserAuthorizeBranch(); // kullanıcının yetkili olduğu branch listesini çeker
        BranchSetting brSetting = new BranchSetting();
        brSetting.getBranch().setName(sessionBean.getLoc().getString("all"));
        brSetting.getBranch().setId(0);
        brSetting.setId(0);

        BranchSetting temp = new BranchSetting();
        temp = listOfBranch.get(0);

        listOfBranch.set(0, brSetting);
        listOfBranch.add(temp);
        toogleList = Arrays.asList(true, true, true);
        changeBranch();

    }

    @Override
    public void find() {

        isFind = true;
        listOfObjects = findall("");
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
    public LazyDataModel<UnsuccessfulSalesProcess> findall(String where) {
        return new CentrowizLazyDataModel<UnsuccessfulSalesProcess>() {
            @Override
            public List<UnsuccessfulSalesProcess> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<UnsuccessfulSalesProcess> result = unsuccessfulSalesProcessService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList);
                int count = 0;
                count = unsuccessfulSalesProcessService.count(branchList);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void sendIntegration() {
        List<UnsuccessfulSalesProcess> result = new ArrayList<>();
        result = unsuccessfulSalesProcessService.sendIntegration(branchList);

        if (result.get(0).getResponseCode() == 1) {
            findall("");
            RequestContext.getCurrentInstance().update("pgrUnsuccessfulSalesProcessDatatable");

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("notification"), sessionBean.loc.getString("unsuccesfuloperation")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void changeBranch() {
        branchList = "";

        if (selectedBranch.getBranch().getId() == 0) {
            for (BranchSetting branchSetting : listOfBranch) {

                if (branchSetting.getBranch().getId() != 0) {
                    branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                }
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
        } else {
            branchList = String.valueOf(selectedBranch.getBranch().getId());
        }

    }
    
    

}
