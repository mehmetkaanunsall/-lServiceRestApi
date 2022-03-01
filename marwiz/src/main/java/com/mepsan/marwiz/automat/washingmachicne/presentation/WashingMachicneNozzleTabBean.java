/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:36:56 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.presentation;

import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneNozzleService;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneTankService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class WashingMachicneNozzleTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{washingMachicneNozzleService}")
    public IWashingMachicneNozzleService washingMachicneNozzleService;

    @ManagedProperty(value = "#{washingMachicneTankService}")
    public IWashingMachicneTankService washingMachicneTankService;

    private WashingNozzle selectedObject;
    private List<WashingNozzle> listOfObject;
    private List<WashingTank> listOfTank;
    private int processType;
    private WashingPlatform selectedPlatform;
    private WashingMachicne selectedWashingMachicne;

    public void setWashingMachicneNozzleService(IWashingMachicneNozzleService washingMachicneNozzleService) {
        this.washingMachicneNozzleService = washingMachicneNozzleService;
    }

    public void setWashingMachicneTankService(IWashingMachicneTankService washingMachicneTankService) {
        this.washingMachicneTankService = washingMachicneTankService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public WashingNozzle getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WashingNozzle selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<WashingNozzle> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<WashingNozzle> listOfObject) {
        this.listOfObject = listOfObject;
    }

    public List<WashingTank> getListOfTank() {
        return listOfTank;
    }     

    public void setListOfTank(List<WashingTank> listOfTank) {
        this.listOfTank = listOfTank;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public WashingPlatform getSelectedPlatform() {
        return selectedPlatform;
    }

    public void setSelectedPlatform(WashingPlatform selectedPlatform) {
        this.selectedPlatform = selectedPlatform;
    }

    public WashingMachicne getSelectedWashingMachicne() {
        return selectedWashingMachicne;
    }

    public void setSelectedWashingMachicne(WashingMachicne selectedWashingMachicne) {
        this.selectedWashingMachicne = selectedWashingMachicne;
    }

    @PostConstruct
    public void init() {
        System.out.println("*-********WashingMachicneNozzleTabBean*");
        selectedObject = new WashingNozzle();
        selectedPlatform = new WashingPlatform();
        selectedWashingMachicne = new WashingMachicne();
        if (sessionBean.parameter instanceof WashingMachicne) {
            selectedWashingMachicne = (WashingMachicne) sessionBean.parameter;
        }

        findTank();
        findAll();
        
        setListBtn(sessionBean.checkAuthority(new int[]{242, 243, 244}, 0));
    }
     
    public void createDialog(int type) {
        processType = type;
        if (processType == 1) {
            selectedObject = new WashingNozzle();
            selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        }
        selectedObject.setWashingMachicnePlatform(selectedPlatform);
        findTank();
        RequestContext.getCurrentInstance().execute("PF('dlg_washingMachicneNozlleDlg').show()");
    }

    public void findAll() {
        listOfObject = new ArrayList<>();
        listOfObject = washingMachicneNozzleService.findAll(selectedPlatform);
    }

    /**
     * Tabancaya eklenecek tankları listelemek için kullanılır.
     */
    public void findTank() {
        listOfTank = new ArrayList<>();
        selectedObject.setWashingMachicnePlatform(selectedPlatform);
        listOfTank = washingMachicneTankService.findAll(selectedWashingMachicne);
    }

    /**
     * Tankın stoğuna ait birim ve name bilgilerini set etmek için kullanılır.
     */
    public void bringTank() {
        for (WashingTank machicneTank : listOfTank) {
            if (machicneTank.getId() == selectedObject.getWashingMachicneTank().getId()) {
                selectedObject.getWashingMachicneTank().setTankNo(machicneTank.getTankNo());
                selectedObject.setUnit(machicneTank.getStock().getUnit());
                selectedObject.setStockName(machicneTank.getStock().getName());

            }
        }
    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            result = washingMachicneNozzleService.create(selectedObject);
        } else {
            result = washingMachicneNozzleService.update(selectedObject);
        }

        if (result > 0) {
            if (processType == 1) {
                selectedObject.setId(result);
                listOfObject.add(selectedObject);
            }
            RequestContext.getCurrentInstance().update("tbvNozzleTab:frmWashingMachicneNozzleTab");
            RequestContext.getCurrentInstance().execute("PF('dlg_washingMachicneNozlleDlg').hide()");
        }
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Para Birimi name alanını set etmek için kullanılır.
     */
    public void bringTagOfCurrency() {
        sessionBean.getCurrencies().stream().filter((s) -> (s.getId() == selectedObject.getCurrency().getId())).forEach((s) -> {
            selectedObject.getCurrency().setTag(s.getNameMap().get(sessionBean.getLangId()).getName());
        });
    }

    public void delete() {
        int result = 0;
        result = washingMachicneNozzleService.delete(selectedObject);
        if (result > 0) {
            listOfObject.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_washingMachicneNozlleDlg').hide();");
            context.update("tbvNozzleTab:frmWashingMachicneNozzleTab:dtbWashingMachicneNozzle");
        }
        sessionBean.createUpdateMessage(result);
    }
}
