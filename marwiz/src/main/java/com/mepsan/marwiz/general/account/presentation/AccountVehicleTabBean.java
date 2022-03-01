/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 08:57:11
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IVehicleService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Vehicle;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AccountVehicleTabBean extends GeneralDefinitionBean<Vehicle> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{vehicleService}")
    public IVehicleService vehicleService;

    private Account selectedAccount;
    private int processType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setVehicleService(IVehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public Account getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------AccountVehicleTabBean------");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedAccount = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    listOfObjects = findall();
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{78, 79, 80}, 0));

    }

    @Override
    public void create() {
        processType = 1;

        selectedObject = new Vehicle();
        selectedObject.setAccount(selectedAccount);
        RequestContext.getCurrentInstance().execute("PF('dlg_VehicleProcess').show()");
    }

    public void update() {
        processType = 2;

        RequestContext.getCurrentInstance().execute("PF('dlg_VehicleProcess').show()");
    }

    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        selectedObject.setPlate(selectedObject.getPlate().toUpperCase());
        if (processType == 1) {
            result = vehicleService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
            }
        } else {
            result = vehicleService.update(selectedObject);
        }

        if (result > 0) {
            context.execute("PF('dlg_VehicleProcess').hide()");
            context.update("tbvAccountProc:frmVehicleTab:dtbVehicle");
            context.execute("PF('vehiclePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = vehicleService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("tbvAccountProc:frmVehicleTab:dtbVehicle");
            context.execute("PF('vehiclePF').filter();");
            context.execute("PF('dlg_VehicleProcess').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<Vehicle> findall() {
        return vehicleService.findVehicle(selectedAccount);
    }

}
