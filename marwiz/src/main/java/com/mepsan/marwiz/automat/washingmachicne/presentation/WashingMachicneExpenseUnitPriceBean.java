/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:50:16 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.presentation;

import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneExpenseUnitPricesService;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneTankService;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.ExpenseUnitPrice;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class WashingMachicneExpenseUnitPriceBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}") // session
    public Marwiz marwiz;

    @ManagedProperty(value = "#{washingMachicneTankService}")
    public IWashingMachicneTankService washingMachicneTankService;

    @ManagedProperty(value = "#{washingMachicneExpenseUnitPricesService}")
    public IWashingMachicneExpenseUnitPricesService washingMachicneExpenseUnitPricesService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    public StockBookFilterBean stockBookFilterBean;

    private int processType;
    private List<ExpenseUnitPrice> listOfObjects;
    private ExpenseUnitPrice selectedObject;
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

    public void setWashingMachicneExpenseUnitPricesService(IWashingMachicneExpenseUnitPricesService washingMachicneExpenseUnitPricesService) {
        this.washingMachicneExpenseUnitPricesService = washingMachicneExpenseUnitPricesService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<ExpenseUnitPrice> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<ExpenseUnitPrice> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public ExpenseUnitPrice getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(ExpenseUnitPrice selectedObject) {
        this.selectedObject = selectedObject;
    }

    public WashingMachicne getSelectedWashingMachicne() {
        return selectedWashingMachicne;
    }

    public void setSelectedWashingMachicne(WashingMachicne selectedWashingMachicne) {
        this.selectedWashingMachicne = selectedWashingMachicne;
    }

    @PostConstruct
    public void init() {
        System.out.println("--WashingMachicneExpenseUnitPriceBean---");

        selectedWashingMachicne = new WashingMachicne();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof WashingMachicne) {
                    selectedWashingMachicne = (WashingMachicne) ((ArrayList) sessionBean.parameter).get(i);
                } else {  //ekle
                    selectedWashingMachicne = new WashingMachicne();
                }
            }

        } else if (sessionBean.parameter instanceof WashingMachicne) {
            selectedWashingMachicne = (WashingMachicne) sessionBean.parameter;
        }

        listOfObjects = findall();
        
        setListBtn(sessionBean.checkAuthority(new int[]{248, 249, 250}, 0));

    }

    /**
     * Bu metot ekleme işlemi için yeni dialog açar
     */
    public void createDialog(int type) {
        processType = type;
        if (processType == 1) {
            selectedObject = new ExpenseUnitPrice();
        }
        selectedObject.setWashingMachicne(selectedWashingMachicne);
        Currency currency = new Currency();
        currency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        currency.setCode(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));
        selectedObject.setCurrency(currency);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_expenseunitpriceproc').show();");

    }

    public void update() {
        processType = 2;
        selectedObject.setWashingMachicne(selectedWashingMachicne);
        Currency currency = new Currency();
        currency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        currency.setCode(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));;

        selectedObject.setCurrency(currency);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgExpenseUnitPriceProc");
        context.execute("PF('dlg_expenseunitpriceproc').show();");
    }

    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        for (int i = 0; i < listOfObjects.size(); i++) {
            if (Objects.equals(listOfObjects.get(i).getStock().getId(), selectedObject.getStock().getId()) && processType == 1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("previouslyaddedproductinformation")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }
        if (processType == 1) {
            result = washingMachicneExpenseUnitPricesService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
                context.update("tbvWashingMachicneProc:frmExpenseUnitPrice:dtbExpenseUnitPrice");
            }
        } else {
            result = washingMachicneExpenseUnitPricesService.update(selectedObject);
        }

        if (result > 0) {
            context.update("tbvWashingMachicneProc:frmExpenseUnitPrice:dtbExpenseUnitPrice");
            context.execute("PF('dlg_expenseunitpriceproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public List<ExpenseUnitPrice> findall() {
        return washingMachicneExpenseUnitPricesService.findAll(selectedWashingMachicne);
    }

    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null) {
            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmExpenseUnitPriceProcess:txtStock");
            RequestContext.getCurrentInstance().update("frmExpenseUnitPriceProcess:outpnlUnitPrices");
            stockBookFilterBean.setSelectedData(null);
        }

    }

    public void delete() {
        int result = 0;
        result = washingMachicneExpenseUnitPricesService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_expenseunitpriceproc').hide();");
            context.update("tbvWashingMachicneProc:frmExpenseUnitPrice:dtbExpenseUnitPrice");
        }
        sessionBean.createUpdateMessage(result);
    }

}
