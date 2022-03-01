/*
 *Bu sınıf stok işlem sayfası için yazılmıştır.
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.ftpConnection.presentation.FtpConnectionBean;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.taxdepartment.business.ITaxDepartmentService;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class StockProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    @ManagedProperty(value = "#{unitService}")
    public IUnitService unitService;

    @ManagedProperty(value = "#{brandService}")
    public IBrandService brandService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{ftpConnectionBean}")
    private FtpConnectionBean ftpConnectionBean;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    private AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{taxDepartmentService}")
    public ITaxDepartmentService taxDepartmentService;

    private int processType, activeIndex;

    private Stock selectedObject;
    private List<Unit> unitList;
    private List<Brand> brandList;
    private Unit oldUnit;
    private boolean isUnitChange, isProfitRate;
    private List<TaxDepartment> listOfTaxDepartment;
    private boolean isAvailableStock;
    private String barcode = "";
    private int oldId;
    private boolean isFoundStock;
    private int specialItem;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setFtpConnectionBean(FtpConnectionBean ftpConnectionBean) {
        this.ftpConnectionBean = ftpConnectionBean;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Stock getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Stock selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public List<Brand> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public Unit getOldUnit() {
        return oldUnit;
    }

    public void setOldUnit(Unit oldUnit) {
        this.oldUnit = oldUnit;
    }

    public boolean isIsProfitRate() {
        return isProfitRate;
    }

    public void setIsProfitRate(boolean isProfitRate) {
        this.isProfitRate = isProfitRate;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public List<TaxDepartment> getListOfTaxDepartment() {
        return listOfTaxDepartment;
    }

    public void setListOfTaxDepartment(List<TaxDepartment> listOfTaxDepartment) {
        this.listOfTaxDepartment = listOfTaxDepartment;
    }

    public void setTaxDepartmentService(ITaxDepartmentService taxDepartmentService) {
        this.taxDepartmentService = taxDepartmentService;
    }

    public boolean isIsAvailableStock() {
        return isAvailableStock;
    }

    public void setIsAvailableStock(boolean isAvailableStock) {
        this.isAvailableStock = isAvailableStock;
    }

    public boolean isIsFoundStock() {
        return isFoundStock;
    }

    public void setIsFoundStock(boolean isFoundStock) {
        this.isFoundStock = isFoundStock;
    }

    public int getSpecialItem() {
        return specialItem;
    }

    public void setSpecialItem(int specialItem) {
        this.specialItem = specialItem;
    }

    /**
     * Comboboxlar için verileri çeker, buton ve tablar için yetki kontrolü
     * yapar,
     *
     */
    @PostConstruct
    public void init() {
        System.out.println("---------StockProcessBean---------");
        selectedObject = new Stock();
        oldUnit = new Unit();
        unitList = unitService.findAll();
        brandList = brandService.findAll(new Item(2));
        listOfTaxDepartment = taxDepartmentService.listOfTaxDepartment();
        setListBtn(sessionBean.checkAuthority(new int[]{123, 124}, 0));
        
        if(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()){
        setListTab(sessionBean.checkAuthority(new int[]{30, 31, 32, 33, 80, 34, 35, 36}, 1));
        }else{
        setListTab(sessionBean.checkAuthority(new int[]{30, 31, 32, 33, 34, 35, 36}, 1));
        }
        
        int countSessionParam = 0;
        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

        if (sessionBean.parameter instanceof ArrayList) {
            //Stok İşlemleri Sayfasına Birden Fazla Sayfadan Yönlendirme Olduğu İçin Break Kullanıldı.
            //Break Kullanıldığı İçin Öncelikle LazyView Değeri Kontrol Edildi.

            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {

                    processType = 2;
                    selectedObject = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    oldId = selectedObject.getId();
                    barcode = selectedObject.getBarcode();
                    if (marwiz.getOldId() == 54 || marwiz.getOldId() == 83) {//Depo Sayımlarından Geldi İse Hareket Tabını Aç
                        if (getRendered(35, 1)) {
                            activeIndex = 35;
                            marwiz.settabIndex(getListTab().indexOf(activeIndex));
                        }
                    }
                    oldUnit.setId(selectedObject.getUnit().getId());
                    if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                        if (selectedObject.getStockInfo().getMinProfitRate() != null) {
                            isProfitRate = true;
                        }
                        if (!selectedObject.getBarcode().equals("") && selectedObject.getBarcode() != null) {
                            findStockAccordingToBarcode();
                        }
                    }
                    break;
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Object) {
                    processType = 1;
                    oldId = 0;
                    Parameter parameter = applicationBean.getParameterMap().get("default_country");
                    selectedObject.getCountry().setId(Integer.parseInt(parameter.getValue()));
                    selectedObject.setPurchasePriceListItem(new PriceListItem());
                    selectedObject.setSalePriceListItem(new PriceListItem());
                    barcode = "";
                }
            }

            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) { // Sayfa yenilenince barkod kontrolünün bozulmaması için barkod için çalışan ajaxta sessiona bir stok daha eklenmiş mi siye bakıldı
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    countSessionParam++;
                }
            }

            if (countSessionParam > 1) { // Eğer sessionda iki tane stok tipinde parametre varsa sayfa yenilenmeden önce barkod için yapılan kontrol sonucunda bulunan stok selected object e set edildi ve tekrar uyarı verildi.
                isFoundStock = true;
                int sizeParam = ((ArrayList) sessionBean.parameter).size() - 1;
                barcode = ((Stock) ((ArrayList) sessionBean.parameter).get(sizeParam)).getBarcode();
                selectedObject = ((Stock) ((ArrayList) sessionBean.parameter).get(sizeParam));
                selectedObject.setPurchasePriceListItem(new PriceListItem());
                selectedObject.setSalePriceListItem(new PriceListItem());
                isAvailableStock = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisstockisavailablealreadyincentralintegrationbranches")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                isFoundStock = false;
            }
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            if (ftpConnectionBean.exists(String.valueOf(selectedObject.getId()), "stock")) {
                ftpConnectionBean.showImageStock("stock", String.valueOf(selectedObject.getId()));
            } else {
                ftpConnectionBean.showImageStock("stock", String.valueOf(selectedObject.getCenterstock_id()));
            }

            if (selectedObject.getCenterstock_id() == 0) {
                ftpConnectionBean.setOtherNewName("0");
            } else {
                ftpConnectionBean.setOtherNewName(String.valueOf(selectedObject.getCenterstock_id()));
            }
        }
        ftpConnectionBean.initializeImage("stock", String.valueOf(selectedObject.getId()));

        specialItem = sessionBean.getUser().getLastBranchSetting().getSpecialItem();

    }

    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account account = new Account();
                account.setId(0);
                account.setIsPerson(true);
                account.setName(sessionBean.getLoc().getString("nott"));
                selectedObject.setSupplier(account);
            } else {
                selectedObject.setSupplier(accountBookFilterBean.getSelectedData());
            }
            RequestContext.getCurrentInstance().update("txtSupplier");
            accountBookFilterBean.isAll = false;
            accountBookFilterBean.setSelectedData(null);
        }
    }

    /**
     * Stok sayfasındaki kaydet butonunda çalıştırılır.İşlem tipine göre ekleme
     * ya da güncelleme yapar.
     */
    public void save() {

        int result = 0;

        boolean isThere = false;

        if (selectedObject.getBarcode() != null) {
            int count = stockService.stockBarcodeControl(selectedObject);
            if (count > 0) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisbarcodeisavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }

        if (!isThere) {

            if (processType == 1) {
                result = stockService.create(selectedObject, isAvailableStock);
                if (result > 0) {
                    selectedObject.setUserCreated(sessionBean.getUser());
                    selectedObject.setDateCreated(new Date());
                    selectedObject.setId(result);
                    if (isFoundStock) {
                        for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                            if (((ArrayList) sessionBean.parameter).get(((ArrayList) sessionBean.parameter).size() - (i + 1)) instanceof Stock) {
                                ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - (i + 1));
                            }
                        }
                    } else {
                        ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                    }

                    List<Object> list = new ArrayList<>();
                    list.addAll((ArrayList) sessionBean.parameter);
                    list.add(selectedObject);
                    if (sessionBean.getUser().getLastBranchSetting().isIsTaxMandatory()) { //Şubedeki parametreye göre, vergi grup tanımlarının unutulmaması için diyalog açılarak uyarı verilir
                        RequestContext.getCurrentInstance().update("dlgConfirmOK");
                        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmOK').show()");
                    }

                    marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", list, 1, 12);
                }
            } else {

                result = stockService.update(selectedObject, isAvailableStock);
                if (result > 0) {

                    if (isFoundStock) {
                        for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                            if (((ArrayList) sessionBean.parameter).get(((ArrayList) sessionBean.parameter).size() - (i + 1)) instanceof Stock) {
                                ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - (i + 1));
                            }
                        }
                    } else {
                        ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                    }

                    List<Object> list = new ArrayList<>();
                    list.addAll((ArrayList) sessionBean.parameter);
                    marwiz.goToPage("/pages/inventory/stock/stock.xhtml", list, 1, 2);
                }
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));

        if (activeIndex == 31) { // birim değiştiyse alternatif birimler tabını güncelle
            if (isUnitChange) { // birim değiştiyse

                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                StockAlternativeUnitsTabBean stockAlternativeUnitsTabBean = (StockAlternativeUnitsTabBean) viewMap.get("stockAlternativeUnitsTabBean");
                if (stockAlternativeUnitsTabBean != null) {
                    stockAlternativeUnitsTabBean.init();
                }
                RequestContext.getCurrentInstance().update("tbvStokProc:frmAlternativeUnits");
                isUnitChange = false;
            }

        }
        if (activeIndex == 32) {
            RequestContext.getCurrentInstance().update("tbvStokProc:frmAlternativeBarcodes");
        }

    }

    public void changeUnit() {
        if (processType == 2) {
            RequestContext.getCurrentInstance().execute("PF('confirmDialog').show()");
        }
    }

    public void updateUnit() {

        int result = 0;
        result = stockService.updateUnit(selectedObject);
        if (result > 0) {
            bringUnit();
            oldUnit.setId(selectedObject.getUnit().getId());
            isUnitChange = true;
            if (activeIndex == 31) { // birim değiştiyse alternatif birimler tabını güncelle
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                StockAlternativeUnitsTabBean stockAlternativeUnitsTabBean = (StockAlternativeUnitsTabBean) viewMap.get("stockAlternativeUnitsTabBean");
                stockAlternativeUnitsTabBean.init();
                RequestContext.getCurrentInstance().update("tbvStokProc:frmAlternativeUnits");
                isUnitChange = false;
            }
            if (activeIndex == 32) {
                RequestContext.getCurrentInstance().update("tbvStokProc:frmAlternativeBarcodes");
            }
        }

        sessionBean.createUpdateMessage(result);
    }

    public void cancelUnit() {
        selectedObject.getUnit().setId(oldUnit.getId());
        RequestContext.getCurrentInstance().update("slcUnit");
    }

    public void bringUnit() {
        for (Unit unit : unitList) {
            if (unit.getId() == selectedObject.getUnit().getId()) {
                selectedObject.getUnit().setName(unit.getName());
            }
        }
    }

    public void changeProfitRate() {
        isProfitRate = !isProfitRate;
        if (isProfitRate) {
            selectedObject.getStockInfo().setRecommendedPrice(null);
            selectedObject.getStockInfo().getCurrency().setId(0);
        } else {
            selectedObject.getStockInfo().setMinProfitRate(null);
        }
    }

    public void testBeforeDelete() {
        int result = 0;
        result = stockService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausestockhasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {

        int result = 0;
        result = stockService.delete(selectedObject);
        if (result > 0) {
            List<Object> list = new ArrayList<>();
            list.addAll((ArrayList) sessionBean.parameter);

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Stock) {
                    list.remove(list.get(i));
                }
            }
            marwiz.goToPage("/pages/inventory/stock/stock.xhtml", list, 1, 2);
        }

        sessionBean.createUpdateMessage(result);
    }

    /**
     * Entegrasyonu olmayan şubede barkod değişikliği yaptığında o ürün sistemde
     * var mı diye kontrol edilir. Merkezi entegrasyonu olan şubede ürün zaten
     * varsa direk bilgileri getirilir ve değiştirilmeye izin verilmez.
     */
    public void findStockAccordingToBarcode() {
        if (!barcode.trim().toLowerCase().equals(selectedObject.getBarcode().trim().toLowerCase())) {
            if (isFoundStock) { //Sessionda iki tane stok varsa sonuncu kaldırılır
                int sizeParam = ((ArrayList) sessionBean.parameter).size() - 1;
                ((ArrayList) sessionBean.parameter).remove(((Stock) ((ArrayList) sessionBean.parameter).get(sizeParam)));
                isFoundStock = false;
            }

            isAvailableStock = false;
            Stock foundStock = new Stock();
            foundStock = stockService.findStockAccordingToBarcode(selectedObject);

            if (foundStock.getId() > 0) {
                barcode = foundStock.getBarcode();
                selectedObject = foundStock;
                selectedObject.setPurchasePriceListItem(new PriceListItem());
                selectedObject.setSalePriceListItem(new PriceListItem());
                isAvailableStock = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisstockisavailablealreadyincentralintegrationbranches")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                if (processType == 2) {
                    ((ArrayList) sessionBean.parameter).add(selectedObject);//sayfa yenilenince bu kontrolü kaybetmemek için bulunan stok sessiona eklendi.
                    isFoundStock = true;
                }

            } else {

                selectedObject.setCenterstock_id(0);
                selectedObject.setId(oldId);
            }
            RequestContext.getCurrentInstance().update("pgrStockProcess");

        }
        barcode = selectedObject.getBarcode();
    }

    public void changeIsService() {

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        StockDetailTabBean stockDetailTabBean = (StockDetailTabBean) viewMap.get("stockDetailTabBean");

        if (selectedObject.isIsService()) {
            stockDetailTabBean.setIsIncomeExpenseView(true);
            stockDetailTabBean.setIsIncome(true);
        } else {
            stockDetailTabBean.setIsIncomeExpenseView(false);
            stockDetailTabBean.setIsIncome(false);
        }
        RequestContext.getCurrentInstance().update("tbvStokProc:frmStockDetailTab");
    }

    public void beforeChangeStockTaxDepartment() {
        RequestContext.getCurrentInstance().update("dlgChangeTaxDepartment");
        RequestContext.getCurrentInstance().execute("PF('dlg_ChangeTaxDepartment').show();");
    }

    public void changeStockTaxDepartment() {
        int result = 0;
        result = taxDepartmentService.changeTaxDepartmentToStockInNonCentralBranch(selectedObject);
        sessionBean.createUpdateMessage(result);
    }

}
