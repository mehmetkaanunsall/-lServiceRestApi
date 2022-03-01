/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 12:54:53
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
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListService;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingItemService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class StockTakingProcessBean extends AuthenticationLists {

    private int processType;
    private StockTaking selectedObject;
    private StockMovement stockMovement;
    private List<Warehouse> listOfWarehouse;
    private List<Account> accountList;
    private List<PriceList> priceList;
    private List<Categorization> oldCategoryList;
    private List<StockTakingItem> deletedStockTakingItemList;
    private int activeIndex;
    private List<Stock> categoryOfStock;

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

    @ManagedProperty(value = "#{stockTakingItemService}")
    private IStockTakingItemService stockTakingItemService;

    public void setPriceListService(IPriceListService priceListService) {
        this.priceListService = priceListService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public StockTaking getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(StockTaking selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
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

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public void setStockTakingItemService(IStockTakingItemService stockTakingItemService) {
        this.stockTakingItemService = stockTakingItemService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<Stock> getCategoryOfStock() {
        return categoryOfStock;
    }

    public void setCategoryOfStock(List<Stock> categoryOfStock) {
        this.categoryOfStock = categoryOfStock;
    }

    @PostConstruct
    public void init() {
        System.out.println("------------StockTakingProcessBean-------------");
        selectedObject = new StockTaking();
        listOfWarehouse = new ArrayList<>();
        listOfWarehouse = warehouseService.selectListWarehouse(" ");
        priceList = new ArrayList<>();
        accountList = new ArrayList<>();
        priceList = priceListService.listofPriceList();
        accountList = stockTakingService.employeList();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {//Sayım Sayfaası
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking) {
                    selectedObject = (StockTaking) ((ArrayList) sessionBean.parameter).get(i);
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof StockMovement) {//Ürün Hareketleri Tabından Geldi İSe
                    stockMovement = (StockMovement) ((ArrayList) sessionBean.parameter).get(i);
                    if (stockMovement.getStockTaking().getId() > 0) {
                        List<StockTaking> stockTakings = stockTakingService.stockTakingDetail(stockMovement.getStockTaking());
                        if (stockTakings.size() > 0) {
                            selectedObject = stockTakings.get(0);
                        }
                    }

                }
            }

        }
        selectedObject.getListOfCategorization().clear();
        String[] tempArray;
        if (selectedObject.getCategories() != null && !selectedObject.getCategories().equals("")) {
            tempArray = selectedObject.getCategories().split(",");
            for (int i = 0; i < tempArray.length; i++) {
                String string = tempArray[i];
                selectedObject.getListOfCategorization().add(new Categorization(Integer.valueOf(string)));
            }

        }

        deletedStockTakingItemList = new ArrayList<>();
        oldCategoryList = new ArrayList<>();
        for (Categorization categorization : selectedObject.getListOfCategorization()) {
            oldCategoryList.add(categorization);
        }

        if (selectedObject.getListOfCategorization().isEmpty()) {
            categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            categoryBookCheckboxFilterBean.setSelectedCount(selectedObject.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
        }

        setListBtn(sessionBean.checkAuthority(new int[]{38, 39, 40}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{10, 81}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
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

    public void save() {

        String where = "AND ist.id NOT IN (" + selectedObject.getId() + ")";

        RequestContext context = RequestContext.getCurrentInstance();
        deletedStockTakingItemList.clear();
        int isThere = 0; // EKLENECEK YENİ SAYIM İÇİN KATEGORİ KONTROLÜ YAPAR
        boolean isThere2 = false;//GELEN STOKLARLA BENİM KATEGORİLERİMİN STOKLARI AYNI MI
        boolean isThere3 = false; // KATEGORİLİ BİR SAYIM EKLENECEĞİNDE KATEGORİSİZ AÇIK BİR SAYIM VAR MI
        boolean isThere4 = false; // AÇIK SAYIM VAR MI
        List<StockTaking> listOfObject = stockTakingService.stockTakingProcessList();

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

        for (StockTaking stockTaking : listOfObject) {
            if (!selectedObject.getCategories().equals("") && selectedObject.getCategories() != null && selectedObject.getStatus().getId() == 15 && stockTaking.getId() != selectedObject.getId()) { // KATEGORİLİ BİR SAYIM YAPILACAKSA

                isThere = stockTakingService.findCategories(selectedObject, where);

                if (stockTaking.getWarehouse().getId() == selectedObject.getWarehouse().getId() && stockTaking.getStatus().getId() == 15) {

                    if (stockTaking.getCategories() == null || stockTaking.getCategories().equals("")) {
                        isThere3 = true;
                    }
                }

                if (isThere == 1 || selectedObject.getCategories().equals("")) { // BENİM KATEGORİMDE BİR SAYIM VAR MI
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("acountalreadyexistsinthesamecategory")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    break;

                } else if (isThere == 0) {

                    categoryOfStock = stockTakingService.categoryOfStock(selectedObject, where);

                    if (!categoryOfStock.isEmpty()) {
                        isThere2 = true;
                    }

                    if (isThere3) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanopenoverallcountinthiswarehouse")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    } else if (isThere2) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thesameproductiscountedinanothercategory")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        context.execute("PF('dlg_CategoryStock').show()");
                        break;
                    }
                }
            } else {

                if (stockTaking.getWarehouse().getId() == selectedObject.getWarehouse().getId() && stockTaking.getId() != selectedObject.getId()) {
                    isThere4 = true;
                }

                if (isThere4) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanopenstocktakinginthiswarehouse")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    break;

                }
            }
        }

        if (isThere == 0 && isThere2 == false && isThere3 == false && isThere4 == false) {
            if (!selectedObject.getListOfCategorization().isEmpty()) {
                if (selectedObject.getListOfCategorization().get(0).getId() != 0) {

                    if (oldCategoryList.isEmpty() || !selectedObject.getListOfCategorization().containsAll(oldCategoryList)) {

                        //-----kategori değiştiyse şuanki kategoride ait olmayan itemlar varsa silmek için onları bulur.
                        deletedStockTakingItemList = stockTakingItemService.findWithoutCategorization(selectedObject, categories);

                    }
                }
            }

            //-----silinecek item varsa uyarı dialoğu açar yoksa güncelleme işlemini yapar.
            if (deletedStockTakingItemList.isEmpty()) {
                int result = 0;
                if ((selectedObject.getListOfCategorization().isEmpty() || selectedObject.getListOfCategorization().get(0).getId() == 0) && (oldCategoryList.isEmpty() || oldCategoryList.get(0).getId() == 0)) {
                    result = stockTakingService.update(selectedObject);
                } else {
                    result = stockTakingService.update(selectedObject, deletedStockTakingItemList, oldCategoryList);
                }
                if (result > 0) {
                    marwiz.goToPage("/pages/inventory/stocktaking/stocktaking.xhtml", null, 1, 53);
                }
                sessionBean.createUpdateMessage(result);
            } else {
                RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmUpdate').show();");
            }
        }
    }

    public void saveConfirm() {

        int result = 0;

        result = stockTakingService.update(selectedObject, deletedStockTakingItemList, oldCategoryList);

        if (result > 0) {
            marwiz.goToPage("/pages/inventory/stocktaking/stocktaking.xhtml", null, 1, 53);
        }
        sessionBean.createUpdateMessage(result);
    }

    public void controlOpenStockTaking() {
        boolean isThere = false;
        StockTaking stockTaking = stockTakingService.findOpenStock(selectedObject);
        if (stockTaking.getId() > 0) {
            isThere = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cannotbeopenedbecausethereisastocktakingafterthisstocktakinginthiswarehouse")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
        if (!isThere) {
            RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmStatus').show();");
        }

    }

    public void openStokTaking() {
        int result = 0;
        result = stockTakingService.openStockTaking(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/inventory/stocktaking/stocktaking.xhtml", null, 1, 53);
        }
        sessionBean.createUpdateMessage(result);
    }

    public void testBeforeDelete() {
        if (selectedObject.getStatus().getId() == 16) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cannotbedeletedbecausethestocktakingisclosed")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().update("dlgConfirmDeleteStockTaking");
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmDeleteStockTaking').show();");
        }
    }

    public void delete() {

        int result = 0;
        result = stockTakingService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/inventory/stocktaking/stocktaking.xhtml", null, 1, 53);
        }
        sessionBean.createUpdateMessage(result);
    }

    public void btnStartStockTaking() {

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

        if (!selectedObject.getListOfCategorization().isEmpty()) {
            if (selectedObject.getListOfCategorization().get(0).getId() != 0) {

                if (oldCategoryList.isEmpty() || !selectedObject.getListOfCategorization().containsAll(oldCategoryList)) {

                    //-----kategori değiştiyse şuanki kategoride ait olmayan itemlar varsa silmek için onları bulur.
                    deletedStockTakingItemList = stockTakingItemService.findWithoutCategorization(selectedObject, categories);

                }
            }
        }

        //-----silinecek item varsa uyarı dialoğu açar yoksa güncelleme işlemini yapar.
        if (deletedStockTakingItemList.isEmpty()) {
            int result = 0;
            if ((selectedObject.getListOfCategorization().isEmpty() || selectedObject.getListOfCategorization().get(0).getId() == 0) && (oldCategoryList.isEmpty() || oldCategoryList.get(0).getId() == 0)) {
                result = stockTakingService.update(selectedObject);
            } else {
                result = stockTakingService.update(selectedObject, deletedStockTakingItemList, oldCategoryList);
            }
            if (result > 0) {
                marwiz.goToPage("/pages/inventory/stocktaking/stocktaking.xhtml", null, 1, 53);
            }
            sessionBean.createUpdateMessage(result);
        } else {
            RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmUpdate').show();");
        }

        RequestContext.getCurrentInstance().update("frmStockTakingProcess:txtCategory");
    }

}
