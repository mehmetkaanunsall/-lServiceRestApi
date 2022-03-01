/**
 * Bu sınıf yeni marka ekler hızlı eklemeler için
 *Gerekli parametreler:
 * item :Hangi marka yapılcaksa onun item id si
 * model: Markanın modeli var mı yok mu bilgisi 0:yok 1:var
 * btnNew: artı butonunun id si yetki için
 * btnSave: kaydet butonunun id si yetki için
 * btnDelete: sil butonunun id si yetki için
 * whichPage: hızlı ekleme yapılcak sayfanın bean i
 *
 * @author Ali Kurt
 *
 * @date   12.01.2018 04:30:50
 */
package com.mepsan.marwiz.general.brand.presentation;

import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.cashregister.presentation.CashRegisterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stock.presentation.StockProcessBean;
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

@ManagedBean
@ViewScoped

public class NewBrandBean extends GeneralDefinitionBean<Brand> {

    @ManagedProperty(value = "#{brandService}")
    private IBrandService brandService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int processType;
    private String whichPage;
    private Item item;
    private List<Brand> brandList;
    private int oldId;
    private int newId;

    public List<Brand> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("----------NewBrandBean--------");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Long itemId = (Long) request.getAttribute("itemId");
        whichPage = (String) request.getAttribute("whichPage");
        item = new Item();
        item.setId((int) (long) itemId);
        Long btnSave = (Long) request.getAttribute("btnRenderedSave");
        Long btnNew = (Long) request.getAttribute("btnRenderedNew");
        setListBtn(sessionBean.checkAuthority(new int[]{(int) (long) btnNew, (int) (long) btnSave}, 0));
        brandList = new ArrayList<>();
    }

    public void create(List<Brand> list) {
        brandList = list;
        processType = 1;
        selectedObject = new Brand();
        selectedObject.setItem(item);
        oldId = 0;
        newId = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_brandproc').show();");
    }

    /**
     * Bu metot ekleme işlemi için yeni dialog açar
     */
    @Override
    public void create() {
        processType = 1;
        selectedObject = new Brand();
        selectedObject.setItem(item);
        oldId = 0;
        newId = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_brandproc').show();");
    }

    /**
     * Bu metot güncelleştirme işlemi için yeni dialog açar
     */
    public void update() {
        processType = 2;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_brandproc').show();");
    }

    /**
     * Bu metot marka kaydetmeye veya güncelleştirmeye yarar.
     */
    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        if (processType == 1) {
            for (Brand brand : brandList) {
                if (brand.getName().equalsIgnoreCase(selectedObject.getName()) && brand.getId() != selectedObject.getId()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("thisbrandisavailable")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }

            boolean isAvailableBrand = false;

            if (item.getId() == 2) {
                if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    Brand foundBrand = new Brand();
                    foundBrand = brandService.findBrandAccordingToName(selectedObject);
                    if (foundBrand.getId() > 0) {
                        newId = foundBrand.getId();
                        selectedObject = foundBrand;
                        selectedObject.setId(oldId);
                        isAvailableBrand = true;
                    }

                }
            }

            if (isAvailableBrand) {
                selectedObject.setId(newId);
                result = brandService.update(selectedObject);
            } else {
                result = brandService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                }
            }
            if (result > 0) {

                if (whichPage.equals("stockProcessBean")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    StockProcessBean stockProcessBean = (StockProcessBean) viewMap.get("stockProcessBean");

                    stockProcessBean.getBrandList().add(selectedObject);
                    stockProcessBean.getSelectedObject().setBrand(selectedObject);
                    context.update("slcBrand");
                    context.execute("PF('dlg_brandproc').hide();");
                } else if (whichPage.equals("cashRegisterBean")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    CashRegisterBean cashRegisterBean = (CashRegisterBean) viewMap.get("cashRegisterBean");

                    cashRegisterBean.getBrandList().add(selectedObject);
                    cashRegisterBean.getSelectedObject().setBrand(selectedObject);
                    context.update("frmNewCash:slcBrand");
                    context.execute("PF('dlg_brandproc').hide();");
                }
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    @Override
    public List<Brand> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
