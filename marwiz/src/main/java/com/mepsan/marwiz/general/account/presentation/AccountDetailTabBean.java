package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountDetailService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.AccountInfo;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Samet Dağ
 */
@ManagedBean
@ViewScoped
public class AccountDetailTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{accountDetailService}")
    public IAccountDetailService accountDetailService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountProcessBean}")
    public AccountProcessBean accountProcessBean;

    String fuelintegrationcode, accountingintegrationcode;

    int account_id;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountProcessBean(AccountProcessBean accountProcessBean) {
        this.accountProcessBean = accountProcessBean;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public void setAccountDetailService(IAccountDetailService accountDetailService) {
        this.accountDetailService = accountDetailService;
    }

    public String getFuelintegrationcode() {
        return fuelintegrationcode;
    }

    public void setFuelintegrationcode(String fuelintegrationcode) {
        this.fuelintegrationcode = fuelintegrationcode;
    }

    public String getAccountingintegrationcode() {
        return accountingintegrationcode;
    }

    public void setAccountingintegrationcode(String accountingintegrationcode) {
        this.accountingintegrationcode = accountingintegrationcode;
    }

    @PostConstruct
    public void init() {
        if (!accountProcessBean.getSelectedObject().isIsEmployee()) {
            account_id = accountProcessBean.getSelectedObject().getId();
        }

        setListBtn(sessionBean.checkAuthority(new int[]{70}, 0));
    }

    public void save() {

        int result = accountDetailService.update(fuelintegrationcode, accountingintegrationcode, account_id);
        find();
        switch (result) {
            case -2:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("thisaccountintegrationcodeisalreadyinthesystemandthefuelintegrationcodehasbeensuccessfullyregistered")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case -3:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("thisfuelintegrationcodeisalreadyinthesystemandtheaccountingintegrationcodehasbeensuccessfullyregistered")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case -4:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("bothintegrationcodesareavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            default:
                sessionBean.createUpdateMessage(result);
                break;
        }
        RequestContext.getCurrentInstance().update("tbvAccountProc:frmAccountDetailTab");
    }

    public void find() {

        AccountInfo accountInfo = (AccountInfo) accountDetailService.find(account_id);
        fuelintegrationcode = accountInfo.getFuelintegrationcode();
        accountingintegrationcode = accountInfo.getAccountingintegrationcode();

        RequestContext.getCurrentInstance().update("tbvAccountProc:frmAccountDetailTab");
    }

}
