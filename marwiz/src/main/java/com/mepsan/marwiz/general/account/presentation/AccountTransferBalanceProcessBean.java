/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.11.2018 11:31:47
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountMovementService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AccountTransferBalanceProcessBean extends AuthenticationLists{

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountMovementService}")
    public IAccountMovementService accountMovementService;

    private AccountMovement selectedObject;

    public AccountMovement getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AccountMovement selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountMovementService(IAccountMovementService accountMovementService) {
        this.accountMovementService = accountMovementService;
    }

    @PostConstruct
    public void init() {
        selectedObject = new AccountMovement();
        System.out.println("--AccountTransferBalanceProcessBean----");
        
        setListBtn(sessionBean.checkAuthority(new int[]{265}, 0));

    }

    public void save() {
        int result = 0;
        result = accountMovementService.updatePrice(selectedObject);
        if (result > 0) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            AccountMovementTabBean accountMovementTabBean = (AccountMovementTabBean) viewMap.get("accountMovementTabBean");

            RequestContext.getCurrentInstance().execute("PF('dlg_TransferBalance').hide()");
            if (accountMovementTabBean.isIsAccountExtract()) {//Cari Extreden geldiyse
                RequestContext.getCurrentInstance().update("frmMovementDataTable:dtbMovement");
            } else {
                RequestContext.getCurrentInstance().update("tbvAccountProc:frmMovementDataTable:dtbMovement");
            }

        }
        sessionBean.createUpdateMessage(result);
    }

}
