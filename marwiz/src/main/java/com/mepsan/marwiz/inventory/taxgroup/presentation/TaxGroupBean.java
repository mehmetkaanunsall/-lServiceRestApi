/**
 * Bu class vergi grup listesini görüntüler,yeni vergi grubu ekler,vergi güncelleştirmesi yapar.
 *
 *
 * @author Ali Kurt
 *
 * @date   12.01.2018 11:02:02
 */
package com.mepsan.marwiz.inventory.taxgroup.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class TaxGroupBean extends GeneralDefinitionBean<TaxGroup> {

    @ManagedProperty(value = "#{taxGroupService}")
    private ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int processType;

    private boolean isAvailable;
    private BigDecimal oldRate;
    private int oldType;
    private int oldId;
    private int newId;

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
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

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = findall();

        setListBtn(sessionBean.checkAuthority(new int[]{186, 187, 188}, 0));
    }

    /**
     * Bu metot ekleme işlemi için yeni dialog açar
     */
    @Override
    public void create() {
        processType = 1;
        selectedObject = new TaxGroup();
        oldRate = null;
        oldType = 0;
        oldId = 0;
        isAvailable = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_taxgroupproc').show();");
    }

    /**
     * Bu metot güncelleştirme işlemi için yeni dialog açar
     */
    public void update() {
        processType = 2;
        oldRate = selectedObject.getRate();
        oldType = selectedObject.getType().getId();
        oldId = selectedObject.getId();
        isAvailable = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_taxgroupproc').show();");
    }

    /**
     * Entegrasyonu olmayan şubede değişiklik yaptığında o vergi grubu sistemde
     * var mı diye kontrol edilir. Merkezi entegrasyonu olan şubede ergi grubu
     * zaten varsa direk bilgileri getirilir ve değiştirilmeye izin verilmez.
     */
    public void findAccordingToTypeAndRate() {

        if (oldRate == null || oldRate.compareTo(selectedObject.getRate()) != 0 || oldType != selectedObject.getType().getId()) {
            isAvailable = false;

            if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                TaxGroup foundTaxGroup = new TaxGroup();
                foundTaxGroup = taxGroupService.findAccordingToTypeAndRate(selectedObject);
                if (foundTaxGroup.getId() > 0) {
                    newId = foundTaxGroup.getId();
                    selectedObject = foundTaxGroup;
                    selectedObject.setId(oldId);
                    oldRate = selectedObject.getRate();
                    oldType = selectedObject.getType().getId();
                    isAvailable = true;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisstaxgroupisavailablealreadyincentralintegrationbranches")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    selectedObject.setCentertaxgroup_id(0);
                    oldRate = selectedObject.getRate();
                    oldType = selectedObject.getType().getId();
                }
                RequestContext.getCurrentInstance().update("frmtaxGroupProcess:pgrTaxGroupProcess");

            }
        }
    }

    /**
     * Bu metot vergi grubu kaydetmeye veya güncelleştirmeye yarar.
     */
    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;

        for (TaxGroup taxGroup : listOfObjects) {
            if (taxGroup.getType().getId() == (selectedObject.getType().getId()) && taxGroup.getRate().compareTo(selectedObject.getRate()) == 0 && taxGroup.getId() != selectedObject.getId()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("groupalreadyavailable")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }

        if (processType == 1) {
            if (isAvailable) {
                selectedObject.setId(newId);
                result = taxGroupService.update(selectedObject);
            } else {
                result = taxGroupService.create(selectedObject);
            }

        } else if (processType == 2) {
            if (isAvailable) {
                selectedObject.setId(newId);
                result = taxGroupService.updateAvailableTaxGroup(oldId, newId);
            } else {
                result = taxGroupService.update(selectedObject);
            }
        }
        if (result > 0) {
            context.execute("PF('dlg_taxgroupproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Dialoğun close eventinde çağırılıyor. Diyalog kapatıp açtığında
     * değişikler kaldığı için liste terkar çekildi.
     */
    public void closeDialog() {
        RequestContext context = RequestContext.getCurrentInstance();
        listOfObjects = findall();
        context.execute("PF('taxgroupPF').filter();");
        context.update("frmTaxGroup:dtbTaxGroup");
    }

    @Override
    public List<TaxGroup> findall() {
        return taxGroupService.findAll();
    }

    public void bringType() {
        for (Type t : sessionBean.getTypes(10)) {
            if (t.getId() == selectedObject.getType().getId()) {
                selectedObject.getType().setTag(t.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
    }

    public void testBeforeDelete() {
        int result = 0;
        result = taxGroupService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmtaxGroupProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausetaxgroupisrelatedtostock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedObject.getCentertaxgroup_id() > 0) {
            result = taxGroupService.deleteForOtherBranch(selectedObject);
        } else {
            result = taxGroupService.delete(selectedObject);
        }
        if (result > 0) {
            context.execute("PF('dlg_taxgroupproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
