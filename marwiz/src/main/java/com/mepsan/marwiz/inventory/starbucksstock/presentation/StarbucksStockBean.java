package com.mepsan.marwiz.inventory.starbucksstock.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.starbucksstock.business.IStarbucksStockService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class StarbucksStockBean extends GeneralDefinitionBean<StarbucksStock> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{starbucksStockService}")
    private IStarbucksStockService starbucksStockService;

    private int processType;

    private boolean isAvailable;
    private String oldCode;
    private int oldId;
    private int newId;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStarbucksStockService(IStarbucksStockService starbucksStockService) {
        this.starbucksStockService = starbucksStockService;
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

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--------StarbucksStockBean----------");

        listOfObjects = findall();
        setListBtn(sessionBean.checkAuthority(new int[]{305, 306, 307}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new StarbucksStock();
        oldCode = "";
        oldId = 0;
        newId = 0;
        isAvailable = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_starbuckstockdeffinitionproc').show();");
    }

    /**
     * Entegrasyonu olmayan şubede isim değişikliği yaptığında o ürün sistemde
     * var mı diye kontrol edilir. Merkezi entegrasyonu olan şubede ürün zaten
     * varsa direk bilgileri getirilir ve değiştirilmeye izin verilmez.
     */
    public void findAccordingToCode() {

        if (oldCode != null && !oldCode.equals(selectedObject.getCode())) {
            isAvailable = false;

            if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                StarbucksStock foundStarbucksStock = new StarbucksStock();
                foundStarbucksStock = starbucksStockService.findAccordingToCode(selectedObject);
                if (foundStarbucksStock.getId() > 0) {
                    newId = foundStarbucksStock.getId();
                    selectedObject = foundStarbucksStock;
                    selectedObject.setId(oldId);
                    oldCode = selectedObject.getCode();
                    isAvailable = true;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisstockisavailablealreadyincentralintegrationbranches")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    selectedObject.setCenterStarbucksStock_id(0);
                    oldCode = selectedObject.getCode();
                }
                RequestContext.getCurrentInstance().update("frmStarbucksStockDefinitionProcess:pgrStarbucksStockDefinitionProcess");

            }
        }
    }

    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();

        if (processType == 1) {
            // birim merkezi entegrasyonu olan şubelerde var update yapılcak sadece
            if (isAvailable) {
                selectedObject.setId(newId);
                result = starbucksStockService.update(selectedObject);
            } else {
                result = starbucksStockService.create(selectedObject);
            }
            if (result > 0) {
                selectedObject.setId(result);
            }

        }
        if (processType == 2) {
            if (isAvailable) {
                selectedObject.setId(newId);
                result = starbucksStockService.updateAvailableStarbucksStock(oldId, newId);
            } else {
                result = starbucksStockService.update(selectedObject);
            }

        }
        if (result > 0) {
            context.execute("PF('dlg_starbuckstockdeffinitionproc').hide();");
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
        context.execute("PF('starbucksStockPF').filter();");
        context.update("frmStarbucksStockDefinition:dtbStarbucksStockDefinition");
    }

    public void update() {
        processType = 2;
        oldCode = selectedObject.getCode();
        oldId = selectedObject.getId();
        isAvailable = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgStarbucksStockDefinitionProc");
        context.execute("PF('dlg_starbuckstockdeffinitionproc').show();");

    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedObject.getCenterStarbucksStock_id() > 0) {
            result = starbucksStockService.deleteForOtherBranch(selectedObject);
        } else {
            result = starbucksStockService.delete(selectedObject);
        }
        if (result > 0) {
            context.execute("PF('dlg_starbuckstockdeffinitionproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<StarbucksStock> findall() {
        return starbucksStockService.findAll();
    }

}
