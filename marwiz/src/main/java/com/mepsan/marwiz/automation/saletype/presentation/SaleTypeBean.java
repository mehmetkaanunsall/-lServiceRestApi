package com.mepsan.marwiz.automation.saletype.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.automation.saletype.business.ISaleTypeService;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 * Bu cLass satış tipi tanımlarını listeler.
 *
 * @author Samet Dağ
 */
@ManagedBean
@ViewScoped
public class SaleTypeBean extends GeneralDefinitionBean<FuelSaleType> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{saleTypeService}")
    public ISaleTypeService saleTypeService;

    private int processType;

    public void setSaleTypeService(ISaleTypeService saleTypeService) {
        this.saleTypeService = saleTypeService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------SaleTypeBean");
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true);
        
        setListBtn(sessionBean.checkAuthority(new int[]{229, 230, 231}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new FuelSaleType();
        RequestContext.getCurrentInstance().execute("PF('dlg_SaleTypeProcess').show();");
    }

    public void update() {
        processType = 2;
        RequestContext.getCurrentInstance().execute("PF('dlg_SaleTypeProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        for (FuelSaleType saleType : listOfObjects) {
            if (saleType.getTypeno()== (selectedObject.getTypeno()) && saleType.getId() != selectedObject.getId()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("saletypealreadyavailable")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }

        if (processType == 1) {
            result = saleTypeService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
            }
        } else if (processType == 2) {
            result = saleTypeService.update(selectedObject);
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_SaleTypeProcess').hide();");
        RequestContext.getCurrentInstance().update("frmSaleType:dtbSaleType");
        RequestContext.getCurrentInstance().execute("PF('saleTypePF').filter();");

        sessionBean.createUpdateMessage(result);
    }

    public void delete() {
        int result = 0;
        result = saleTypeService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_SaleTypeProcess').hide();");
            context.update("frmSaleType:dtbSaleType");
            context.execute("PF('saleTypePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<FuelSaleType> findall() {
        return saleTypeService.findAll();
    }

}
