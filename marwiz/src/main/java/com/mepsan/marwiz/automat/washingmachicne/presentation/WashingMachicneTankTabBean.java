/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:35:17 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.presentation;

import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneTankService;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
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
public class WashingMachicneTankTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{washingMachicneTankService}")
    public IWashingMachicneTankService washingMachicneTankService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    private StockBookFilterBean stockBookFilterBean;

    private int processType;
    private WashingTank selectedObject;
    private List<WashingTank> listOfObject;
    private List<Stock> listOfStock;
    private WashingMachicne selectedWashingMachicne;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWashingMachicneTankService(IWashingMachicneTankService washingMachicneTankService) {
        this.washingMachicneTankService = washingMachicneTankService;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public WashingTank getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WashingTank selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<WashingTank> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<WashingTank> listOfObject) {
        this.listOfObject = listOfObject;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public WashingMachicne getSelectedWashingMachicne() {
        return selectedWashingMachicne;
    }

    public void setSelectedWashingMachicne(WashingMachicne selectedWashingMachicne) {
        this.selectedWashingMachicne = selectedWashingMachicne;
    }

    @PostConstruct
    public void init() {
        System.out.println("=====WashingMachicneTankTabBean======");
        selectedObject = new WashingTank();
        selectedWashingMachicne = new WashingMachicne();
        if (sessionBean.parameter instanceof WashingMachicne) {
            selectedWashingMachicne = (WashingMachicne) sessionBean.parameter;
        }
        findAll();
        
        setListBtn(sessionBean.checkAuthority(new int[]{245, 246, 247}, 0));

    }

    public void createDialog(int type) {
        processType = type;
        if (processType == 1) {
            selectedObject = new WashingTank();
        }
        selectedObject.setWashingMachicne(selectedWashingMachicne);
        RequestContext.getCurrentInstance().execute("PF('dlg_washingMachicneTankDlg').loadContents();");
        RequestContext.getCurrentInstance().update("dlgWashingMachicneTankDlg");
    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            result = washingMachicneTankService.create(selectedObject);
        } else {
            result = washingMachicneTankService.update(selectedObject);
        }
        sessionBean.createUpdateMessage(result);
        if (result > 0) {
            if (processType == 1) {
                selectedObject.setId(result);
                listOfObject.add(selectedObject);
            }
            RequestContext.getCurrentInstance().update("tbvWashingMachicneProc:frmWashingMachicneTankTab");
            RequestContext.getCurrentInstance().execute("PF('dlg_washingMachicneTankDlg').hide();");
        }

    }

    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null) {

            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmWashingMachicneTankProcess:txtStock");

            stockBookFilterBean.setSelectedData(null);
            RequestContext.getCurrentInstance().update("frmWashingMachicneTankProcess:txtCapaxity");
            RequestContext.getCurrentInstance().update("frmWashingMachicneTankProcess:txtBalance");
            RequestContext.getCurrentInstance().update("frmWashingMachicneTankProcess:txtMinCapacity");
        }

    }

    public void findAll() {
        listOfObject = new ArrayList<>();
        listOfObject = washingMachicneTankService.findAll(selectedWashingMachicne);
    }

    public void createTankAnimation() {
        if (processType == 2) {
            RequestContext context = RequestContext.getCurrentInstance();
            String name = "fillgauge1";

            context.execute("loadtankfill(" + "'" + name + "'," + (selectedObject.getBalance()).doubleValue() + "," + (selectedObject.getMinCapacity()).doubleValue() + "," + (selectedObject.getCapacity()).doubleValue() + ",'#6db9dc','#002344','#6db9dc')");
        }
    }

    public void delete() {
        int result = 0;
        result = washingMachicneTankService.delete(selectedObject);
        if (result > 0) {
            listOfObject.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_washingMachicneTankDlg').hide();");
            context.update("tbvWashingMachicneProc:frmWashingMachicneTankTab:dtbWashingMachicneTank");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void testBeforeDelete() {
        int result = 0;
        result = washingMachicneTankService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmWashingMachicneTankProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausetheregisterisconnectedwiththenozzle")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }
}
