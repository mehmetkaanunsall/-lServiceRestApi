/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:00:09
 */
package com.mepsan.marwiz.general.pointofsale.presentation;

import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.PointOfSaleSafeConnection;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.pointofsale.business.IPointOfSaleSafeService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class PointOfSaleSafeTabBean extends AuthenticationLists{

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{pointOfSaleSafeService}")
    public IPointOfSaleSafeService pointOfSaleSafeService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    private PointOfSale selectedPOS;
    private PointOfSaleSafeConnection selectedObject;
    private List<PointOfSaleSafeConnection> listOfSafe;
    private List<Safe> listOfSelectableSafe;
    private int processType;
    List<Currency> tempListCurrency;

    public void setPointOfSaleSafeService(IPointOfSaleSafeService pointOfSaleSafeService) {
        this.pointOfSaleSafeService = pointOfSaleSafeService;
    }

    public PointOfSaleSafeConnection getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(PointOfSaleSafeConnection selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<PointOfSaleSafeConnection> getListOfSafe() {
        return listOfSafe;
    }

    public void setListOfSafe(List<PointOfSaleSafeConnection> listOfSafe) {
        this.listOfSafe = listOfSafe;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public PointOfSale getSelectedPOS() {
        return selectedPOS;
    }

    public void setSelectedPOS(PointOfSale selectedPOS) {
        this.selectedPOS = selectedPOS;
    }

    public List<Safe> getListOfSelectableSafe() {
        return listOfSelectableSafe;
    }

    public void setListOfSelectableSafe(List<Safe> listOfSelectableSafe) {
        this.listOfSelectableSafe = listOfSelectableSafe;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    @PostConstruct
    public void init() {
        System.out.println("----PointOfSaleSafeTabBean---");
        selectedObject = new PointOfSaleSafeConnection();
        tempListCurrency = new ArrayList<>();
        listOfSafe = new ArrayList<>();
        
        setListBtn(sessionBean.checkAuthority(new int[]{201, 202, 203}, 0));
    }

    public void createDialog(int type) {

        processType = type;

        tempListCurrency.clear();
        for (PointOfSaleSafeConnection possc : listOfSafe) {
            if (processType == 2) {
                if (possc.getSafe().getCurrency().getId() != selectedObject.getSafe().getCurrency().getId()) {
                    Currency c = new Currency();
                    c.setId(possc.getSafe().getCurrency().getId());
                    tempListCurrency.add(c);
                }

            } else {
                Currency c = new Currency();
                c.setId(possc.getSafe().getCurrency().getId());
                tempListCurrency.add(c);
            }

        }
        tempListCurrency.add(new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId()));
        String createWhere = safeService.createWhere(1, tempListCurrency);
        listOfSelectableSafe = safeService.findSafeByCurrency(createWhere);

        if (processType == 1) { //ekle
            selectedObject = new PointOfSaleSafeConnection();
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_SafeProcess').show()");

    }

    public void save() {
        int result = 0;
        selectedObject.setPointOfSale(selectedPOS);

        if (processType == 1) {
            result = pointOfSaleSafeService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfSafe.add(selectedObject);
            }

        } else {
            result = pointOfSaleSafeService.update(selectedObject);
        }
        if (result > 0) {
            bringTagOfSafe();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_SafeProcess').hide();");
            context.update("tbvPOSProcess:frmDiscountTab:dtbSafe");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void bringTagOfSafe() {

        for (Safe s : listOfSelectableSafe) {
            if (s.getId() == selectedObject.getSafe().getId()) {
                selectedObject.getSafe().setName(s.getName());
                selectedObject.getSafe().setCode(s.getCode());
                selectedObject.getSafe().setCurrency(s.getCurrency());
            }
        }
    }
    
    public void delete(){
        int result=0;
        result=pointOfSaleSafeService.delete(selectedObject);
        if(result>0){
            listOfSafe.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_SafeProcess').hide();");
            context.update("tbvPOSProcess:frmDiscountTab:dtbSafe");
        }
        sessionBean.createUpdateMessage(result);
        
    }

}
