/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   24.04.2018 10:36:04
 */
package com.mepsan.marwiz.inventory.stockrequest.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.stockrequest.business.IStockRequestService;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import com.mepsan.marwiz.service.stock.business.ISendStockRequestService;

@ManagedBean
@ViewScoped
public class NewStockRequestBean extends GeneralDefinitionBean<StockRequest> {

    private String whichPage;
    public List<Unit> unitList;
    public List<TaxGroup> taxGroupList;
    public List<TaxGroup> saleTaxGroupList;
    public List<TaxGroup> purchaseTaxGroupList;
    private int processType;

    @ManagedProperty(value = "#{stockRequestService}")
    private IStockRequestService stockRequestService;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    @ManagedProperty(value = "#{taxGroupService}")
    private ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{sendStockRequestService}")
    private ISendStockRequestService sendStockRequestService;

    @ManagedProperty(value = "#{stockService}")
    private IStockService stockService;

    public String getWhichPage() {
        return whichPage;
    }

    public void setWhichPage(String whichPage) {
        this.whichPage = whichPage;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public void setStockRequestService(IStockRequestService stockRequestService) {
        this.stockRequestService = stockRequestService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSendStockRequestService(ISendStockRequestService sendStockRequestService) {
        this.sendStockRequestService = sendStockRequestService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<TaxGroup> getTaxGroupList() {
        return taxGroupList;
    }

    public void setTaxGroupList(List<TaxGroup> taxGroupList) {
        this.taxGroupList = taxGroupList;
    }

    public List<TaxGroup> getSaleTaxGroupList() {
        return saleTaxGroupList;
    }

    public void setSaleTaxGroupList(List<TaxGroup> saleTaxGroupList) {
        this.saleTaxGroupList = saleTaxGroupList;
    }

    public List<TaxGroup> getPurchaseTaxGroupList() {
        return purchaseTaxGroupList;
    }

    public void setPurchaseTaxGroupList(List<TaxGroup> purchaseTaxGroupList) {
        this.purchaseTaxGroupList = purchaseTaxGroupList;
    }

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    @PostConstruct
    @Override
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        whichPage = (String) request.getAttribute("whichPage");
        System.out.println("------NewStockRequestBean-----whichPage----" + whichPage);
        processType = 1;
        taxGroupList = new ArrayList<>();
        saleTaxGroupList = new ArrayList<>();
        purchaseTaxGroupList = new ArrayList<>();

        setListBtn(sessionBean.checkAuthority(new int[]{43}, 0));
    }

    @Override
    public void create() {
        selectedObject = new StockRequest();
        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        unitList = unitService.findAll();
        taxGroupList = taxGroupService.findAll();
        saleTaxGroupList.clear();
        purchaseTaxGroupList.clear();
        for (TaxGroup taxGroup : taxGroupList) {
            if (taxGroup.getType().getId() == 10) {
                saleTaxGroupList.add(taxGroup);
                purchaseTaxGroupList.add(taxGroup);
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgStockRequest");
        context.execute("PF('dlg_StockRequest').show();");

    }

    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();

        boolean isThere = false;
        Stock stock = new Stock();
        stock.setBarcode(selectedObject.getBarcode());
        int count = stockService.stockBarcodeControlRequest(stock);
        if (count > 0) {
            isThere = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisbarcodeisavailableinthesystem")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            int control = 0;
            control = stockRequestService.controlStockRequest(" ", selectedObject);
            if (control > 0) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisbarcodeisavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
        if (!isThere) {
            result = stockRequestService.create(selectedObject);

            if (result > 0) {
                selectedObject.setId(result);
                sendStockRequestService.sendStockRequest(selectedObject.getId());

                if (whichPage.equals("stockBean")) {
                    context.execute("PF('dlg_StockRequest').hide();");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("yourstockrequesthasbeenreceived"), ""));
                    context.update("grwProcessMessage");
                }
            } else {
                sessionBean.createUpdateMessage(result);
            }
        }

    }

    @Override
    public List<StockRequest> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
