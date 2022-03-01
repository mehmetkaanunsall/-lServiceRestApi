/**
 * Bu sınıf marka listesini görüntüler,yeni marka ekler,marka bilgisi güncelleştirmesi yapar.
 *Gerekli parametreler:
 * item :Hangi marka yapılcaksa onun item id si
 * model: Markanın modeli var mı yok mu bilgisi 0:yok 1:var
 * btnNew: artı butonunun id si yetki için
 * btnSave: kaydet butonunun id si yetki için
 * btnDelete: sil butonunun id si yetki için
 *
 * @author Ali Kurt
 *
 * @date   12.01.2018 04:55:06
 */
package com.mepsan.marwiz.general.brand.presentation;

import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.List;
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
public class BrandBean extends GeneralDefinitionBean<Brand> {

    @ManagedProperty(value = "#{brandService}")
    private IBrandService brandService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int processType;
    private Item item;
    private int oldId;
    private int newId;

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
        System.out.println("----------BrandBean--------");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Long itemId = (Long) request.getAttribute("itemId");
        item = new Item();
        item.setId((int) (long) itemId);
        listOfObjects = brandService.findAll(item);
        Long btnSave = (Long) request.getAttribute("btnRenderedSave");
        Long btnNew = (Long) request.getAttribute("btnRenderedNew");
        Long btnDelete = (Long) request.getAttribute("btnRenderedDelete");
        setListBtn(sessionBean.checkAuthority(new int[]{(int) (long) btnNew, (int) (long) btnSave, (int) (long) btnDelete}, 0));
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
        selectedObject.setItem(item);
        oldId = selectedObject.getId();
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

        for (Brand brand : listOfObjects) {
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

        if (processType == 1) {

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
                listOfObjects.add(selectedObject);
                context.update("frmBrand:dtbBrand");
                context.execute("PF('brandPF').filter();");
                context.execute("PF('dlg_brandproc').hide();");
                context.update("frmBrandProcess");

            }

        } else if (processType == 2) {
            if (isAvailableBrand) {
                selectedObject.setId(newId);
                result = brandService.updateAvailableBrand(oldId, newId);
            } else {
                result = brandService.update(selectedObject);
            }

            if (result > 0) {
                if (isAvailableBrand) {
                    listOfObjects = brandService.findAll(item);
                }
                context.update("frmBrand:dtbBrand");
                context.execute("PF('brandPF').filter();");
                context.execute("PF('dlg_brandproc').hide();");
            }
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<Brand> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void testBeforeDelete() {
        int result = 0;
        result = brandService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmBrandProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {

            switch (selectedObject.getItem().getId()) {
                case 2:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebrandisrelatedtostock")));
                    break;
                case 24:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebrandisrelatedtocashregister")));
                    break;
                case 36:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebrandisrelatedtoautomationdevice")));
                    break;
                default:
                    break;
            }
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        if (selectedObject.getCenterbrand_id() > 0) {
            result = brandService.deleteForOtherBranch(selectedObject);
        } else {
            result = brandService.delete(selectedObject);
        }
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_brandproc').hide();");
            context.update("frmBrand:dtbBrand");
            context.execute("PF('brandPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
