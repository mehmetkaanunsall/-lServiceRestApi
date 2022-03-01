/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   01.02.2018 04:54:47
 */
package com.mepsan.marwiz.system.userdata.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.authorize.business.IAuthorizeService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.userdata.business.IUserDataAuthorizeConnectionService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped

public class UserDataAuthorizeTabBean extends AuthenticationLists {

    private int processType;
    private UserDataAuthorizeConnection selectedObject;
    private UserData userData;
    private List<UserDataAuthorizeConnection> userDataAuthorizeConnectionList;
    private boolean isDisableAuthorize;

    private List<Branch> branchList;
    private List<Authorize> authorizeList;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{userDataAuthorizeConnectionService}")
    private IUserDataAuthorizeConnectionService userDataAuthorizeConnectionService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{authorizeService}")
    private IAuthorizeService authorizeService;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public UserDataAuthorizeConnection getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(UserDataAuthorizeConnection selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<UserDataAuthorizeConnection> getUserDataAuthorizeConnectionList() {
        return userDataAuthorizeConnectionList;
    }

    public void setUserDataAuthorizeConnectionList(List<UserDataAuthorizeConnection> userDataAuthorizeConnectionList) {
        this.userDataAuthorizeConnectionList = userDataAuthorizeConnectionList;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    public List<Authorize> getAuthorizeList() {
        return authorizeList;
    }

    public void setAuthorizeList(List<Authorize> authorizeList) {
        this.authorizeList = authorizeList;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setUserDataAuthorizeConnectionService(IUserDataAuthorizeConnectionService userDataAuthorizeConnectionService) {
        this.userDataAuthorizeConnectionService = userDataAuthorizeConnectionService;
    }

    public void setAuthorizeService(IAuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public boolean isIsDisableAuthorize() {
        return isDisableAuthorize;
    }

    public void setIsDisableAuthorize(boolean isDisableAuthorize) {
        this.isDisableAuthorize = isDisableAuthorize;
    }

    @PostConstruct
    public void init() {

        System.out.println("----------UserDataAuthorizeTabBean-----------------");

        userDataAuthorizeConnectionList = new ArrayList<>();
        branchList = new ArrayList<>();
        authorizeList = new ArrayList<>();
        selectedObject = new UserDataAuthorizeConnection();

        if (sessionBean.parameter instanceof UserData) {
            userData = (UserData) sessionBean.parameter;
            selectedObject.setUserData(userData);
        }
        userDataAuthorizeConnectionList = userDataAuthorizeConnectionService.findAllUserAuthorize("userPage", selectedObject);

        setListBtn(sessionBean.checkAuthority(new int[]{177, 178, 179}, 0));

    }

    public void createDialog(int type) {

        processType = type;
        branchList = branchService.selectBranchs();
        isDisableAuthorize = false;
        if (processType == 1) {

            selectedObject = new UserDataAuthorizeConnection();

        } else if (processType == 2) {

            authorizeList = authorizeService.selectAuthorizeToTheBranch(selectedObject.getAuthorize().getBranch());

        }
        RequestContext.getCurrentInstance().execute("PF('dlg_authorizeprocess').show();");

    }

    public void changeBranch() {
        isDisableAuthorize = false;
        selectedObject.getAuthorize().setId(0);
        for (UserDataAuthorizeConnection userDataAuthorizeConnection : userDataAuthorizeConnectionList) {
            if (userDataAuthorizeConnection.getAuthorize().getBranch().getId() == selectedObject.getAuthorize().getBranch().getId()) {
                isDisableAuthorize = true;
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanotherauthorizationthatwasassignedtobranch")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            }
        }
        if (!isDisableAuthorize) {
            authorizeList = authorizeService.selectAuthorizeToTheBranch(selectedObject.getAuthorize().getBranch());
        }
    }

    public void save() {

        System.out.println("---isDisableAuthorize----" + isDisableAuthorize);

        int result = 0;
        selectedObject.setUserData(userData);
        if (isDisableAuthorize) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanotherauthorizationthatwasassignedtobranch")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            if (processType == 1) {
                result = userDataAuthorizeConnectionService.create(selectedObject);

                System.out.println("-------result 1----------" + result);
                if (result > 0) {
                    selectedObject.setId(result);
                    userDataAuthorizeConnectionList.add(selectedObject);
                }

            } else if (processType == 2) {

                result = userDataAuthorizeConnectionService.update(selectedObject);
                System.out.println("-------result 2----------" + result);
            }

            if (result > 0) {
                bringAll();
                RequestContext.getCurrentInstance().update("tbvUserDataProc:frmUserAuthorizeTab:dtbAuthorizations");
                RequestContext.getCurrentInstance().execute("PF('dlg_authorizeprocess').hide();");
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    public void bringAll() {
        for (Authorize authorize : authorizeList) {
            if (authorize.getId() == selectedObject.getAuthorize().getId()) {
                selectedObject.getAuthorize().setName(authorize.getName());
                break;
            }
        }
        for (Branch branch : branchList) {
            if (branch.getId() == selectedObject.getAuthorize().getBranch().getId()) {
                selectedObject.getAuthorize().getBranch().setName(branch.getName());
                break;
            }
        }
    }

    public void delete() {
        if (selectedObject.getUserData().getId() == sessionBean.getUser().getId() && selectedObject.getAuthorize().getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseyouareinthatbranch")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            int result = 0;
            result = userDataAuthorizeConnectionService.delete(selectedObject);
            if (result > 0) {
                userDataAuthorizeConnectionList.remove(selectedObject);
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlg_authorizeprocess').hide();");
                context.update("tbvUserDataProc:frmUserAuthorizeTab:dtbAuthorizations");
            }
            sessionBean.createUpdateMessage(result);
        }
    }

}
