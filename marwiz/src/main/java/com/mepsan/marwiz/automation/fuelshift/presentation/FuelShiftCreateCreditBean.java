/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2019 08:26:00
 */
package com.mepsan.marwiz.automation.fuelshift.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.general.account.business.IVehicleService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Vehicle;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
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
public class FuelShiftCreateCreditBean extends GeneralDefinitionBean<FuelShiftSales> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    private IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{vehicleService}")
    public IVehicleService vehicleService;

    private List<FuelShiftSales> listOfSelectedObjects;
    private FuelShift selectedFuelShift;
    private BigDecimal total;
    private Vehicle selectedPlate;
    private boolean isCheck;//Bütün Satışları Getir

    public List<FuelShiftSales> getListOfSelectedObjects() {
        return listOfSelectedObjects;
    }

    public void setListOfSelectedObjects(List<FuelShiftSales> listOfSelectedObjects) {
        this.listOfSelectedObjects = listOfSelectedObjects;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public void setVehicleService(IVehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public Vehicle getSelectedPlate() {
        return selectedPlate;
    }

    public void setSelectedPlate(Vehicle selectedPlate) {
        this.selectedPlate = selectedPlate;
    }

    public boolean isIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = new ArrayList<>();
        listOfSelectedObjects = new ArrayList<>();
        selectedObject = new FuelShiftSales();
        selectedPlate = new Vehicle();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FuelShift) {
                    selectedFuelShift = (FuelShift) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }
    }

    public void updateAllInformation() {
        
        
        
        int result = 0;
        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                selectedObject.getCredit().setAccount(new Account());
            } else {
                selectedObject.getCredit().setAccount(accountBookFilterBean.getSelectedData());
            }
            for (FuelShiftSales s : listOfObjects) {
                if (s.getId() == selectedObject.getId()) {
                    s.getCredit().setAccount(selectedObject.getCredit().getAccount());
                    result = fuelShiftTransferService.controlVehicleAccountCon(s);
                    if (result == 1) {//Varsa
                        s.setIsConnectVehicle(false);
                    } else {
                        s.setIsConnectVehicle(true);
                    }
                    break;
                }
            }

            RequestContext.getCurrentInstance().execute("PF('creditSalesPF').filter();");
            RequestContext.getCurrentInstance().update("frmCreditSales:dtbCreditSales");

            accountBookFilterBean.setSelectedData(null);
        }
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        listOfSelectedObjects.clear();
        total = BigDecimal.valueOf(0);

        for (FuelShiftSales f : listOfObjects) {
            if (f.getCredit().getAccount().getId() != 0) {
                listOfSelectedObjects.add(f);
            }
        }

        if (!listOfSelectedObjects.isEmpty()) {
            int result = 0;
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            FuelShiftTransferBean fuelShiftTransferBean = (FuelShiftTransferBean) viewMap.get("fuelShiftTransferBean");

            String accounts = null;

            accounts = fuelShiftTransferService.jsonArrayShiftSale(listOfSelectedObjects);

            ShiftPayment shiftPayment = new ShiftPayment();
            shiftPayment.getShift().setId(selectedFuelShift.getId());
            shiftPayment.getAccount().setId(fuelShiftTransferBean.getActiveTabFuelShiftSale().getAccount().getId());

            result = fuelShiftTransferService.createFinDocAndShiftPayment(2, shiftPayment, accounts);
            for (FuelShiftSales f : listOfSelectedObjects) {
                total = total.add(f.getTotalMoney());
            }

            if (result > 0) {

                fuelShiftTransferBean.setListOfShiftPayment(fuelShiftTransferService.findAllShiftPayment(selectedFuelShift, fuelShiftTransferBean.getActiveTabFuelShiftSale().getAccount(), 0));
                if (fuelShiftTransferBean.getOverallTotalShiftPayment() != null) {
                    fuelShiftTransferBean.setOverallTotalShiftPayment(total.add(fuelShiftTransferBean.getOverallTotalShiftPayment()));
                } else {
                    fuelShiftTransferBean.setOverallTotalShiftPayment(BigDecimal.valueOf(0));
                    fuelShiftTransferBean.setOverallTotalShiftPayment(total);
                }

                fuelShiftTransferBean.calculateTotal();
                RequestContext.getCurrentInstance().update("frmFuelShiftTransfer");
                RequestContext.getCurrentInstance().execute("PF('dlg_CreditSales').hide();");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectaccount")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    @Override
    public List<FuelShiftSales> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void clickSale(int saleid) {
        selectedObject.setId(saleid);
    }

    public void connectVehicleToAccount(FuelShiftSales temp) {
        selectedPlate.setId(temp.getId());
        selectedPlate.setPlate(temp.getPlate());
        selectedPlate.setAccount(temp.getCredit().getAccount());
        RequestContext.getCurrentInstance().update("dlgConfirmationConnectVehicle");
        RequestContext.getCurrentInstance().execute("PF('dlgConfirmationConnectVehicle').show();");
    }

    public void saveVehicleToAccount() {
        int result = 0;
        result = vehicleService.create(selectedPlate);
        if (result > 0) {
            for (FuelShiftSales s : listOfObjects) {
                if (s.getId() == selectedPlate.getId()) {
                    s.setIsConnectVehicle(false);
                    break;
                }
            }
            RequestContext.getCurrentInstance().execute("PF('creditSalesPF').filter();");
            RequestContext.getCurrentInstance().update("frmCreditSales:dtbCreditSales");
        }
        sessionBean.createUpdateMessage(result);
        RequestContext.getCurrentInstance().execute("PF('dlgConfirmationConnectVehicle').hide();");
    }

    public void showList() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        FuelShiftTransferBean fuelShiftTransferBean = (FuelShiftTransferBean) viewMap.get("fuelShiftTransferBean");
        listOfObjects = fuelShiftTransferService.findAllCreditSales(fuelShiftTransferBean.getSelectedShift(), fuelShiftTransferBean.getActiveTabFuelShiftSale(), fuelShiftTransferBean.getBranchSetting(), isCheck);
        autoCompleteValue = null;
        RequestContext.getCurrentInstance().update("frmToolbarCreditSales:globalFilter");
        RequestContext.getCurrentInstance().execute("PF('creditSalesPF').filter();");
        RequestContext.getCurrentInstance().update("frmCreditSales:dtbCreditSales");
    }
}
