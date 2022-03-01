package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import com.mepsan.marwiz.general.account.business.IPersonelDetailService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
public class PersonelDetailTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{personelDetailService}")
    public IPersonelDetailService personelDetailService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountProcessBean}")
    public AccountProcessBean accountProcessBean;

    String integrationcode;
    BigDecimal exactsalary, grossSalary;
    int agi;
    Date startDate, endDate;
    int account_id;

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

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

    public void setPersonelDetailService(IPersonelDetailService personelDetailService) {
        this.personelDetailService = personelDetailService;
    }

    public String getIntegrationcode() {
        return integrationcode;
    }

    public void setIntegrationcode(String integrationcode) {
        this.integrationcode = integrationcode;
    }

    public BigDecimal getExactsalary() {
        return exactsalary;
    }

    public void setExactsalary(BigDecimal exactsalary) {
        this.exactsalary = exactsalary;
    }

    public int getAgi() {
        return agi;
    }

    public void setAgi(int agi) {
        this.agi = agi;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @PostConstruct
    public void init() {
        if (accountProcessBean.getSelectedObject().isIsEmployee()) {
            account_id = accountProcessBean.getSelectedObject().getId();
        }
        setListBtn(sessionBean.checkAuthority(new int[]{71}, 0));
    }

    public void save() {
        int result = personelDetailService.update(integrationcode, exactsalary, agi, startDate, endDate, account_id);
        find();
        RequestContext.getCurrentInstance().update("tbvAccountProc:frmPersonDetailTab");
        sessionBean.createUpdateMessage(result);
    }

    public void find() {

        EmployeeInfo employeeInfo = (EmployeeInfo) personelDetailService.find(account_id);
        integrationcode = employeeInfo.getIntegrationcode();
        exactsalary = employeeInfo.getExactsalary();
        agi = employeeInfo.getAgi();
        startDate = employeeInfo.getStartdate();
        endDate = employeeInfo.getEnddate();
        grossSalary = BigDecimal.ZERO;
        if (exactsalary == null) {
            exactsalary = BigDecimal.ZERO;
        }
        grossSalary = grossSalary.add(exactsalary.add(new BigDecimal(agi)));
        RequestContext.getCurrentInstance().update("tbvAccountProc:frmPersonDetailTab");
    }

}
