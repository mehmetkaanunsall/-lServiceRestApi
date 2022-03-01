/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2021 11:34:41
 */
package com.mepsan.marwiz.system.hepsiburadaintegration.presentation;

import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.ftpConnection.presentation.FtpConnectionBean;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.model.inventory.ECommerceStock;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.system.branch.business.IBranchIntegrationService;
import com.mepsan.marwiz.system.hepsiburadaintegration.business.IHepsiburadaIntegrationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class HepsiburadaIntegrationBean extends GeneralReportBean<ECommerceStock> {

    @ManagedProperty(value = "#{hepsiburadaIntegrationService}")
    private IHepsiburadaIntegrationService hepsiburadaIntegrationService;

    @ManagedProperty(value = "#{ftpConnectionBean}")
    private FtpConnectionBean ftpConnectionBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchIntegrationService}")
    private IBranchIntegrationService branchIntegrationService;

    private ECommerceStock selectedEcommerceStock, selectedFilterObject;
    private List<ECommerceStock> selectedEcommerceList, tempSelectedList;
    private BranchIntegration branchIntegration;
    private String responseListing = "";
    private boolean isBringListing;
    private List<Stock> listOfStock;
    private String createWhere;

    public void setHepsiburadaIntegrationService(IHepsiburadaIntegrationService hepsiburadaIntegrationService) {
        this.hepsiburadaIntegrationService = hepsiburadaIntegrationService;
    }

    public ECommerceStock getSelectedEcommerceStock() {
        return selectedEcommerceStock;
    }

    public void setSelectedEcommerceStock(ECommerceStock selectedEcommerceStock) {
        this.selectedEcommerceStock = selectedEcommerceStock;
    }

    public void setFtpConnectionBean(FtpConnectionBean ftpConnectionBean) {
        this.ftpConnectionBean = ftpConnectionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<ECommerceStock> getSelectedEcommerceList() {
        return selectedEcommerceList;
    }

    public void setSelectedEcommerceList(List<ECommerceStock> selectedEcommerceList) {
        this.selectedEcommerceList = selectedEcommerceList;
    }

    public void setBranchIntegrationService(IBranchIntegrationService branchIntegrationService) {
        this.branchIntegrationService = branchIntegrationService;
    }

    public ECommerceStock getSelectedFilterObject() {
        return selectedFilterObject;
    }

    public void setSelectedFilterObject(ECommerceStock selectedFilterObject) {
        this.selectedFilterObject = selectedFilterObject;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------HepsiburadaIntegrationBean");
        selectedObject = new ECommerceStock();
        selectedFilterObject = new ECommerceStock();

        selectedEcommerceStock = new ECommerceStock();
        listOfStock = new ArrayList<>();
        selectedEcommerceList = new ArrayList<>();
        tempSelectedList = new ArrayList<>();
        branchIntegration = new BranchIntegration();
        branchIntegration = branchIntegrationService.findBranchIntegration();
        find();

    }

    @Override
    public void find() {
        isFind = true;
        isBringListing = true;
        selectedFilterObject.getStockList().clear();
        selectedFilterObject.getStockList().addAll(listOfStock);
        createWhere = hepsiburadaIntegrationService.createWhere(selectedFilterObject);
        responseListing = hepsiburadaIntegrationService.listingStock(branchIntegration);
        listOfObjects = findall(createWhere);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmHepsiBuradaIntegrationDatatable:dtbHepsiBuradaIntegration");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

    }

    @Override
    public LazyDataModel<ECommerceStock> findall(String where) {
        return new CentrowizLazyDataModel<ECommerceStock>() {
            @Override
            public List<ECommerceStock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<ECommerceStock> result = hepsiburadaIntegrationService.bringListing(responseListing, first, pageSize, isBringListing, where);
                int count = hepsiburadaIntegrationService.count(where);
                listOfObjects.setRowCount(count);
                bringSelectedList(result);
                isBringListing = false;
                return result;
            }
        };
    }

    public void bringSelectedList(List<ECommerceStock> result) {
        selectedEcommerceList.clear();
        for (ECommerceStock e1 : tempSelectedList) {
            for (ECommerceStock r : result) {
                if (e1.getStock().getId() == r.getStock().getId()) {
                    selectedEcommerceList.add(e1);
                }
            }
        }
    }

    public void goToDetail() {
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            if (ftpConnectionBean.exists(String.valueOf(selectedEcommerceStock.getStock().getId()), "stock")) {
                ftpConnectionBean.showImageStock("stock", String.valueOf(selectedEcommerceStock.getStock().getId()));
            } else {
                ftpConnectionBean.showImageStock("stock", String.valueOf(selectedEcommerceStock.getStock().getCenterstock_id()));
            }
        } else {
            ftpConnectionBean.initializeImage("stock", String.valueOf(selectedEcommerceStock.getStock().getId()));
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_Detail').show()");
    }

    public void sendIntegration() {
        if (tempSelectedList.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectatleastonestock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            boolean isSuccess = hepsiburadaIntegrationService.updateListing(tempSelectedList, branchIntegration, false, false, "");
            if (isSuccess) {
                selectedEcommerceList.clear();
                tempSelectedList.clear();
                RequestContext.getCurrentInstance().update("frmHepsiBuradaIntegrationDatatable:dtbHepsiBuradaIntegration");
            }

        }
    }

    public void removeStocksFromSale() {
        if (tempSelectedList.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectatleastonestock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            boolean isSuccess = hepsiburadaIntegrationService.updateListing(tempSelectedList, branchIntegration, true, false, "");
            if (isSuccess) {
                selectedEcommerceList.clear();
                tempSelectedList.clear();
                RequestContext.getCurrentInstance().update("frmHepsiBuradaIntegrationDatatable:dtbHepsiBuradaIntegration");
            }
        }
    }

    public void rowSelect(SelectEvent evt) {
        boolean isThere = false;
        if (evt != null && evt.getObject() != null
                  && evt.getObject() instanceof ECommerceStock) {

            ECommerceStock ei = (ECommerceStock) evt.getObject();
            if (ei.getStock().getId() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisrecordcannotbesendbecauseofmissinghepsiburadastockmatching")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                selectedEcommerceList.remove(ei);
                RequestContext.getCurrentInstance().update("frmHepsiBuradaIntegrationDatatable:dtbHepsiBuradaIntegration");
            } else {
                for (ECommerceStock e1 : tempSelectedList) {
                    if (e1.getStock().getId() == ei.getStock().getId()) {
                        isThere = true;
                        break;
                    }
                }
                if (!isThere) {
                    tempSelectedList.add(ei);
                }
            }
        }
    }

    public void rowUnSelect(UnselectEvent evt) {
        boolean isThere = false;
        if (evt != null && evt.getObject() != null
                  && evt.getObject() instanceof ECommerceStock) {

            ECommerceStock ei = (ECommerceStock) evt.getObject();

            for (ECommerceStock e1 : tempSelectedList) {
                if (e1.getStock().getId() == ei.getStock().getId()) {
                    isThere = true;
                    break;
                }
            }
            if (isThere) {
                for (Iterator<ECommerceStock> iterator = tempSelectedList.iterator(); iterator.hasNext();) {
                    ECommerceStock value = iterator.next();
                    if (value.getStock().getId() == ei.getStock().getId()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void updateAllInformation(ActionEvent event) {
        listOfStock.clear();
        if (stockBookCheckboxFilterBean.isAll) {
            Stock s = new Stock(0);
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Stock stock = new Stock(0);
                stock.setName(sessionBean.loc.getString("all"));
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            }
        } else if (!stockBookCheckboxFilterBean.isAll) {
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

        if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
        }
        RequestContext.getCurrentInstance().update("frmHepsiburadaIntegration:txtStock");
    }

    public void openDialog() {
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        if (!listOfStock.isEmpty()) {
            if (listOfStock.get(0).getId() == 0) {
                stockBookCheckboxFilterBean.isAll = true;
            } else {
                stockBookCheckboxFilterBean.isAll = false;
            }
        }
        stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
    }

    public void sendAllStocks() {
        String updateResult = hepsiburadaIntegrationService.findSendingHepsiburada();///Değişmesi gereken ürünler geldi!!!
        boolean isSuccess = hepsiburadaIntegrationService.updateListing(tempSelectedList, branchIntegration, false, true, updateResult);
        if (isSuccess) {
            selectedEcommerceList.clear();
            tempSelectedList.clear();
            RequestContext.getCurrentInstance().update("frmHepsiBuradaIntegrationDatatable:dtbHepsiBuradaIntegration");
        }
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
