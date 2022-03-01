/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   06.02.2018 02:22:35
 */

package com.mepsan.marwiz.system.authorize.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.userdata.business.IUserDataAuthorizeConnectionService;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AuthorizeUserTabBean extends AuthenticationLists{
    
    private UserDataAuthorizeConnection selectedObject;
    private Authorize authorize;
    private List<UserDataAuthorizeConnection> userDataAuthorizeConnectionList;
    private int processType;
    private List<UserData> userList;
    
    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;
    
    @ManagedProperty(value = "#{userDataAuthorizeConnectionService}")
    private IUserDataAuthorizeConnectionService userDataAuthorizeConnectionService;
    
    @ManagedProperty(value = "#{userDataService}")
    private IUserDataService userDataService;

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

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<UserData> getUserList() {
        return userList;
    }

    public void setUserList(List<UserData> userList) {
        this.userList = userList;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setUserDataAuthorizeConnectionService(IUserDataAuthorizeConnectionService userDataAuthorizeConnectionService) {
        this.userDataAuthorizeConnectionService = userDataAuthorizeConnectionService;
    }

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }
    

    @PostConstruct
    public void init(){
        System.out.println("----------AuthorizeUserTabBean-----------------");
        selectedObject=new UserDataAuthorizeConnection();
        userDataAuthorizeConnectionList=new ArrayList<>();
        userList=new ArrayList<>();
        
        if(sessionBean.parameter instanceof Authorize){
            authorize=(Authorize)sessionBean.parameter;
            selectedObject.setAuthorize(authorize);
        }
        
        userDataAuthorizeConnectionList=userDataAuthorizeConnectionService.findAllUserAuthorize("authorizePage", selectedObject);
        
        setListBtn(sessionBean.checkAuthority(new int[]{183, 184, 185}, 0));
    }
    
    public void createDialog(int type){
        
        processType=type;
        userList=userDataService.selectUserDataWithoutAuthorizeConn(authorize);
        
        if(processType==1){
            selectedObject=new UserDataAuthorizeConnection();
        }else if (processType==2){
            UserData u=new UserData();
            u.setId(selectedObject.getUserData().getId());
            u.setName(selectedObject.getUserData().getName());
            u.setSurname(selectedObject.getUserData().getSurname());
            userList.add(0, u);
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_userprocess').show();");
    }
    
     public void save() {        
        
        int result = 0;
        selectedObject.setAuthorize(authorize);
            
            if (processType == 1) {
                result = userDataAuthorizeConnectionService.create(selectedObject);
                
                if (result > 0) {
                    selectedObject.setId(result);
                    userDataAuthorizeConnectionList.add(selectedObject);
                }
                
            } else if (processType == 2) {
                
                result = userDataAuthorizeConnectionService.update(selectedObject);
            }
            
            if (result > 0) {
                bringUser();
                RequestContext.getCurrentInstance().update("tbvModules:frmAuthorizeUserTab:dtbUsers");
                RequestContext.getCurrentInstance().execute("PF('dlg_userprocess').hide();");
            }
            sessionBean.createUpdateMessage(result);
        
        
    }
    
    public void bringUser() {
        for (UserData userData : userList) {

            if (userData.getId() == selectedObject.getUserData().getId()) {

                selectedObject.getUserData().setName(userData.getName());
                selectedObject.getUserData().setSurname(userData.getSurname());

                break;
            }
        }
        
    }
    
    public void delete(){
        
        System.out.println("-----delete-----AuthorizeUserTabBean--");
        int result=0;
        result=userDataAuthorizeConnectionService.delete(selectedObject);
        if(result>0){
            userDataAuthorizeConnectionList.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_userprocess').hide();");
            context.update("tbvModules:frmAuthorizeUserTab:dtbUsers");
        }
        sessionBean.createUpdateMessage(result);
    }
    
    
}
