/**
 * This class ...
 *
 *
 * @author Ali Kurt
 *
 * @date   12.01.2018 12:26:39
 */
package com.mepsan.marwiz.inventory.taxgroup.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stock.presentation.StockTaxGroupsTabBean;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.math.BigDecimal;
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
public class NewTaxGroupBean extends GeneralDefinitionBean<TaxGroup> {

    @ManagedProperty(value = "#{taxGroupService}")
    private ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int processType;
    private String whichPage;
    private List<TaxGroup> taxGroupList;

    private boolean isAvailable;
    private BigDecimal oldRate;
    private int oldType;
    private int oldId;
    private int newId;

    public List<TaxGroup> getTaxGroupList() {
        return taxGroupList;
    }

    public void setTaxGroupList(List<TaxGroup> taxGroupList) {
        this.taxGroupList = taxGroupList;
    }

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

    @PostConstruct
    @Override
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        whichPage = (String) request.getAttribute("whichPage");
        System.out.println("-----NewTaxGroupBean--" + whichPage);
        setListBtn(sessionBean.checkAuthority(new int[]{186, 187, 188}, 0));
        taxGroupList = new ArrayList<>();
    }

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

    public void create(List<TaxGroup> list) {
        taxGroupList = list;
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

                if (whichPage.equals("stockTaxGroupsTabBean")) {
                    RequestContext.getCurrentInstance().update("tbvStokProc:frmtaxGroupProcess:pgrTaxGroupProcess");
                } else {
                    RequestContext.getCurrentInstance().update("frmtaxGroupProcess:pgrTaxGroupProcess");
                }

            }
        }
    }

    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (processType == 1) {
            for (TaxGroup taxGroup : taxGroupList) {
                if (taxGroup.getType().getId() == (selectedObject.getType().getId()) && taxGroup.getRate().compareTo(selectedObject.getRate()) == 0 && taxGroup.getId() != selectedObject.getId()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("groupalreadyavailable")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }
            int result = 0;
            if (isAvailable) {
                selectedObject.setId(newId);
                result = taxGroupService.update(selectedObject);
            } else {
                result = taxGroupService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                }
            }
            if (result > 0) {
                selectedObject.setId(result);
                if (whichPage.equals("stockTaxGroupsTabBean")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    StockTaxGroupsTabBean stockTaxGroupsTabBean = (StockTaxGroupsTabBean) viewMap.get("stockTaxGroupsTabBean");
                    stockTaxGroupsTabBean.selectTaxGroup();
                    stockTaxGroupsTabBean.getStocktaxgroup().setTaxGroup(selectedObject);
                    context.update("frmTaxGroupProcess:slcTaxGroup");
                }
                context.execute("PF('dlg_taxgroupproc').hide();");
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    @Override
    public List<TaxGroup> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
