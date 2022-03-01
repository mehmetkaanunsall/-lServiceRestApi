/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.01.2019 02:46:01
 */
package com.mepsan.marwiz.automation.fuelshift.presentation;

import com.mepsan.marwiz.general.account.business.AccountService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class FuelShiftAttendantBean extends GeneralDefinitionBean<AccountMovement> {

    @ManagedProperty(value = "#{accountService}")
    public AccountService accountService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private List<AccountMovement> listOfSelectedObjects;
    private boolean isAll;
    private BigDecimal attendantPrice;
    private List<Account> tempAccountList;

    public List<AccountMovement> getListOfSelectedObjects() {
        return listOfSelectedObjects;
    }

    public void setListOfSelectedObjects(List<AccountMovement> listOfSelectedObjects) {
        this.listOfSelectedObjects = listOfSelectedObjects;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public BigDecimal getAttendantPrice() {
        return attendantPrice;
    }

    public void setAttendantPrice(BigDecimal attendantPrice) {
        this.attendantPrice = attendantPrice;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = new ArrayList<>();
        listOfSelectedObjects = new ArrayList<>();
        tempAccountList = new ArrayList<>();
        attendantPrice = null;

    }

    public void setIsAll() {
        listOfSelectedObjects.clear();
        if (isAll) {
            listOfSelectedObjects.addAll(listOfObjects);
        }
        selectAttendant();

    }

    public void selectAttendant() {

        for (AccountMovement listOfObject : listOfObjects) {
            listOfObject.setPrice(null);
        }
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        FuelShiftTransferBean fuelShiftTransferBean = (FuelShiftTransferBean) viewMap.get("fuelShiftTransferBean");
        attendantPrice = null;
        if (listOfSelectedObjects.size() > 0) {
            if (fuelShiftTransferBean.getTotalRemaining() != null) {
                if (fuelShiftTransferBean.getTotalRemaining().compareTo(BigDecimal.valueOf(0)) == -1) {
                    attendantPrice = fuelShiftTransferBean.getTotalRemaining().multiply(BigDecimal.valueOf(-1)).divide(BigDecimal.valueOf(listOfSelectedObjects.size()), 10, RoundingMode.HALF_EVEN);
                } else {
                    attendantPrice = fuelShiftTransferBean.getTotalRemaining().divide(BigDecimal.valueOf(listOfSelectedObjects.size()), 10, RoundingMode.HALF_EVEN);
                }

            }
        }

        for (AccountMovement listOfObject : listOfSelectedObjects) {
            listOfObject.setPrice(attendantPrice);
        }
        RequestContext.getCurrentInstance().update("frmFuelShiftAttendant:dtbFuelShiftAttendant");
    }

    @Override
    public void create() {
        listOfObjects.clear();
        listOfSelectedObjects.clear();
        attendantPrice = null;
        for (AccountMovement listOfObject : listOfObjects) {
            listOfObject.setPrice(null);
        }
        tempAccountList = accountService.findAllAccountToIntegrationCode();
        for (Account a : tempAccountList) {
            AccountMovement accountMovement = new AccountMovement();
            accountMovement.setAccount(a);
            listOfObjects.add(accountMovement);
        }
    }

    @Override
    public void save() {
        if (!listOfSelectedObjects.isEmpty()) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            FuelShiftTransferBean fuelShiftTransferBean = (FuelShiftTransferBean) viewMap.get("fuelShiftTransferBean");
            RequestContext.getCurrentInstance().execute("PF('dlg_WithoutAttendantFD').hide();");
            if (fuelShiftTransferBean.getFtype() == 1) {//Kullanıcıyı borçlandır
                fuelShiftTransferBean.setMessageFinancing(sessionBean.getLoc().getString("areyousuretowantcharginguser"));
            } else if (fuelShiftTransferBean.getFtype() == 2) {//Kullanıcya Ver
                fuelShiftTransferBean.setMessageFinancing(sessionBean.getLoc().getString("areyousuretowanttransferringusertoexcessamount"));
            }
            RequestContext.getCurrentInstance().update("dlgConfirmationFD");
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmationFD').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectattendant")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    @Override
    public List<AccountMovement> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
