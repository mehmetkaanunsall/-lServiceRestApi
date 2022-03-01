/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 10:03:50
 */
package com.mepsan.marwiz.inventory.stocktaking.presentation;

import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class StockTakingBean extends GeneralDefinitionBean<StockTaking> {

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value = "#{priceListService}")
    private IPriceListService priceListService;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    private List<Warehouse> listOfWarehouse;
    private List<Account> accountList;
    private List<PriceList> priceList;
    private List<Stock> categoryOfStock; // Farklı kategorideki aynı ürünleri tutmak için

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setPriceListService(IPriceListService priceListService) {
        this.priceListService = priceListService;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public List<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceList> priceList) {
        this.priceList = priceList;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public List<Stock> getCategoryOfStock() {
        return categoryOfStock;
    }

    public void setCategoryOfStock(List<Stock> categoryOfStock) {
        this.categoryOfStock = categoryOfStock;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---------StockTakingBean-------------");
        listOfObjects = findall();
        listOfWarehouse = new ArrayList();
        priceList = new ArrayList<>();
        accountList = new ArrayList<>();
        priceList = priceListService.listofPriceList();
        accountList = stockTakingService.employeList();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{37, 38, 39}, 0));
    }

    @Override
    public void create() {
        categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmOK').show();");
    }

    public void createAfterConfirm() {
        selectedObject = new StockTaking();
        selectedObject.getStatus().setId(15);
        selectedObject.setIsControl(true);
        listOfWarehouse = warehouseService.selectListWarehouse(" ");
        RequestContext.getCurrentInstance().update("dlgStockTakingProcess");
        RequestContext.getCurrentInstance().execute("PF('dlg_StockTakingProcess').show();");
    }

    public void update() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 0, 54);
    }

    @Override
    public void save() {

        String where = "";

        RequestContext context = RequestContext.getCurrentInstance();

        if (selectedObject.isIsRetrospective() && selectedObject.getBeginDate().compareTo(selectedObject.getEndDate()) > 0) {//BAŞLANGIÇ TARİHİ İLE BİTİŞ TARİHİ KONTROLÜ YAPILIR
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("startdatecannotbegreaterthanenddate")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            boolean isThere = false;//AÇIK STOK SAYIMI OLUP OLMADIĞINI KONTROL EDEN BOOLEAN DEĞER
            boolean isThere2 = false;// STOK SAYIMINI BAŞLANGIÇ TARİHİNE GÖRE TUTAN DEĞER 
            int isThere3; // EKLENECEK YENİ SAYIM İÇİN KATEGORİ KONTROLÜ YAPAR
            boolean isThere4 = false;//GELEN STOKLARLA BENİM KATEGORİLERİMİN STOKLARI AYNI MI
            boolean isThere5 = false; // KATEGORİLİ BİR SAYIM EKLENECEĞİNDE KATEGORİSİZ AÇIK BİR SAYIM VAR MI 

            String categories = "";
            for (Categorization categorization : selectedObject.getListOfCategorization()) {
                categories = categories + "," + String.valueOf(categorization.getId());
                if (categorization.getId() == 0) {
                    categories = "";
                    break;
                }
            }
            if (!categories.equals("")) {
                categories = categories.substring(1, categories.length());
            }
            selectedObject.setCategories(categories);

            if (!selectedObject.getCategories().equals("") && selectedObject.getCategories() != null) { // KATEGORİLİ BİR SAYIM YAPILACAKSA

                isThere3 = stockTakingService.findCategories(selectedObject, where);

                for (StockTaking stockTaking : listOfObjects) {
                    if (stockTaking.getWarehouse().getId() == selectedObject.getWarehouse().getId() && stockTaking.getStatus().getId() == 15 && selectedObject.getStatus().getId() == 15) {

                        if (stockTaking.getCategories() == null || stockTaking.getCategories().equals("")) {
                            isThere5 = true;
                        }
                    }
                }

                if (isThere3 == 1 || selectedObject.getCategories().equals("")) { // BENİM KATEGORİMDE BİR SAYIM VAR MI
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("acountalreadyexistsinthesamecategory")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else if (isThere3 == 0) {

                    categoryOfStock = stockTakingService.categoryOfStock(selectedObject, where);

                    if (!categoryOfStock.isEmpty()) {
                        isThere4 = true;
                    }

                    if (isThere5) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanopenoverallcountinthiswarehouse")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else if (isThere4) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thesameproductiscountedinanothercategory")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");

                        context.execute("PF('dlg_CategoryStock').show()");
                    } else {
                        int result = 0;

                        result = stockTakingService.create(selectedObject, selectedObject.getListOfCategorization());

                        if (result > 0) {
                            if (!selectedObject.isIsRetrospective()) {
                                selectedObject.setBeginDate(new Date());
                            }
                            selectedObject.setId(result);
                            List<Object> list = new ArrayList<>();
                            list.add(selectedObject);
                            marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 0, 54);
                        }
                        sessionBean.createUpdateMessage(result);
                    }
                }
            } else {

                for (StockTaking stockTaking : listOfObjects) {
                    if (stockTaking.getWarehouse().getId() == selectedObject.getWarehouse().getId() && stockTaking.getStatus().getId() == 15 && selectedObject.getStatus().getId() == 15) {
                        isThere = true;
                        break;
                    }
                }
                if (!isThere) {

                    for (StockTaking stockTaking : listOfObjects) {

                        if (stockTaking.getWarehouse().getId() == selectedObject.getWarehouse().getId()) {
                            if ((selectedObject.isIsRetrospective() && selectedObject.getBeginDate().compareTo(stockTaking.getEndDate()) < 0)) {
                                isThere2 = true;
                                break;
                            }
                        }
                    }
                }

                if (isThere) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanopenstocktakinginthiswarehouse")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else if (isThere2) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cannotbeopenedbecausestocktakingismadeafterthestartdateinthiswarehouse")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    int result = 0;

                    result = stockTakingService.create(selectedObject, selectedObject.getListOfCategorization());

                    if (result > 0) {
                        if (!selectedObject.isIsRetrospective()) {
                            selectedObject.setBeginDate(new Date());
                        }
                        selectedObject.setId(result);
                        List<Object> list = new ArrayList<>();
                        list.add(selectedObject);
                        marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 0, 54);
                    }
                    sessionBean.createUpdateMessage(result);
                }

            }

        }
    }

    @Override
    public List<StockTaking> findall() {
        return stockTakingService.findAll(" ");
    }

    public void updateAllInformation(ActionEvent event) {

        selectedObject.getListOfCategorization().clear();
        if (categoryBookCheckboxFilterBean.isAll) {
            Categorization s = new Categorization(0);
            if (!categoryBookCheckboxFilterBean.getListOfCategorization().contains(s)) {
                categoryBookCheckboxFilterBean.getListOfCategorization().add(0, new Categorization(0, sessionBean.loc.getString("all")));
            }
        } else if (!categoryBookCheckboxFilterBean.isAll) {
            if (!categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.getListOfCategorization().remove(categoryBookCheckboxFilterBean.getListOfCategorization().get(0));
                }
            }
        }
        selectedObject.getListOfCategorization().addAll(categoryBookCheckboxFilterBean.getListOfCategorization());
        if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
            categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
            categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
        }

        RequestContext.getCurrentInstance().update("frmStockTakingProcess:txtCategory");

    }

    public void openDialog() {
        categoryBookCheckboxFilterBean.getListOfCategorization().clear();
        if (!selectedObject.getListOfCategorization().isEmpty()) {
            if (selectedObject.getListOfCategorization().get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.isAll = true;
            } else {
                categoryBookCheckboxFilterBean.isAll = false;
            }
        }

        categoryBookCheckboxFilterBean.getListOfCategorization().addAll(selectedObject.getListOfCategorization());

    }

    public void delete() {
    }

    public void btnStartStockTaking() {
        int result = 0;

        result = stockTakingService.create(selectedObject, selectedObject.getListOfCategorization());

        if (result > 0) {
            if (!selectedObject.isIsRetrospective()) {
                selectedObject.setBeginDate(new Date());
            }
            selectedObject.setId(result);
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 0, 54);
        }
        sessionBean.createUpdateMessage(result);

    }

}
