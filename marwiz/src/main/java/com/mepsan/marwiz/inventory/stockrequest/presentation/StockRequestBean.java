/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.04.2018 04:39:10
 */
package com.mepsan.marwiz.inventory.stockrequest.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequestCheck;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.stockrequest.business.IStockRequestService;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import com.mepsan.marwiz.service.stock.business.ISendStockRequestService;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class StockRequestBean extends GeneralDefinitionBean<StockRequest> {

    private int processType;
    public List<Unit> unitList;
    public List<TaxGroup> taxGroupList;
    public List<TaxGroup> saleTaxGroupList;
    public List<TaxGroup> purchaseTaxGroupList;

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

    @ManagedProperty(value = "#{marwiz}") // marwiz
    public Marwiz marwiz;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
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

    public void setStockRequestService(IStockRequestService stockRequestService) {
        this.stockRequestService = stockRequestService;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSendStockRequestService(ISendStockRequestService sendStockRequestService) {
        this.sendStockRequestService = sendStockRequestService;
    }

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-----------StockRequestBean--------");
        selectedObject = new StockRequest();
        listOfObjects = findall();
        taxGroupList = new ArrayList<>();
        saleTaxGroupList = new ArrayList<>();
        purchaseTaxGroupList = new ArrayList<>();

        setListBtn(sessionBean.checkAuthority(new int[]{42, 43}, 0));
    }

    @Override
    public void create() {
        processType = 1;
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
        RequestContext.getCurrentInstance().execute("PF('dlg_StockRequest').show();");
    }

    public void update() {
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && selectedObject.getApproval() == 0) {
            //Çift tıklandığı an talepleri tekrar kontrol et !!
            List<SendStockRequestCheck> tempList = new ArrayList<>();
            tempList = sendStockRequestService.findStockRequest(selectedObject);
            if (!tempList.isEmpty()) {
                SendStockRequestCheck sendStockRequestCheck = new SendStockRequestCheck();
                sendStockRequestCheck = tempList.get(0);
                sendStockRequestService.checkStockRequest(sendStockRequestCheck);
            }

            listOfObjects = findall();
            for (StockRequest s : listOfObjects) {
                if (s.getId() == selectedObject.getId()) {
                    selectedObject = s;
                    break;
                }
            }
            RequestContext.getCurrentInstance().update("frmStock:dtbStock");
        }
        RequestContext.getCurrentInstance().execute("openStockRequestDialog()");
    }

    public void afterCheckStockRequest() {
        processType = 2;
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
        RequestContext.getCurrentInstance().execute("PF('dlg_StockRequest').show();");
    }

    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
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
            if (processType == 1) {
               control =  stockRequestService.controlStockRequest(" ", selectedObject);
            } else {
               control= stockRequestService.controlStockRequest(" AND msrt.id <> " + selectedObject.getId(), selectedObject);
            }
            if (control > 0) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisbarcodeisavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
        if (!isThere) {
            if (processType == 1) {

                result = stockRequestService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(0, selectedObject);
                    context.update("frmStock:dtbStock");
                    if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                        sendStockRequestService.sendStockRequest(selectedObject.getId());
                    }
                }
                sessionBean.createUpdateMessage(result);

            } else if (processType == 2) {
                boolean isUpdate = true;
                result = stockRequestService.update(selectedObject, false, 2);
                if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    List<SendStockRequest> list = new ArrayList<>();
                    List<SendStockRequest> allList = new ArrayList<>();
                    SendStockRequest sendStockRequest = sendStockRequestService.findByIdStockRequestId(selectedObject.getId());
                    sendStockRequestService.sendStockRequest(sendStockRequest);//İsteği gönderdim

                    allList = stockRequestService.checkRequestSendAllRecord(selectedObject);
                    boolean isSend = false;
                    for (SendStockRequest s : allList) {
                        if (s.isIsSend() == true) {
                            isSend = true;
                            break;
                        }
                    }
                    if (isSend) {
                        list = stockRequestService.checkRequestSend(selectedObject);
                        if (!list.isEmpty()) {
                            if (list.get(0).isIsSend() == true) {//gücelleme yapılabilir
                                result = stockRequestService.update(selectedObject, true, 4);
                            } else {//güncelleme yapılamaz log tablosundan sil
                                result = stockRequestService.update(selectedObject, false, 4);
                                isUpdate = false;
                            }
                        } else {
                            isUpdate = false;
                        }
                    } else {
                        result = stockRequestService.update(selectedObject, false, 3);
                    }

                }
                if (isUpdate) {
                    sessionBean.createUpdateMessage(result);
                } else {
                    listOfObjects = findall();
                    RequestContext.getCurrentInstance().update("frmStock:dtbStock");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("recordcannotbeupdatedbecausethereisnoconnectionwithcenral")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                }

            }
            if (result > 0) {
                context.execute("PF('stockPF').filter();");
                context.execute("PF('dlg_StockRequest').hide();");
            }
        }

    }

    @Override
    public List<StockRequest> findall() {
        return stockRequestService.findall();
    }

    public void goToProduct() {
        List<Stock> list = stockService.findAll(0, 20, null, "ASC", null, " AND stck.id = " + selectedObject.getApprovalStock().getId());
        Stock stock = new Stock();
        if (list.size() > 0) {
            stock = list.get(0);
            List<Object> items = new ArrayList<>();
            items.add(stock);

            marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", items, 1, 12);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productnotfound")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

}
