/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 17:21:02
 */
package com.mepsan.marwiz.finance.discount.presentation;

import com.mepsan.marwiz.finance.discount.business.IDiscountService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
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
public class DiscountProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{discountService}")
    private IDiscountService discountService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDiscountService(IDiscountService discountService) {
        this.discountService = discountService;
    }

    private int activeIndex;
    public int processType;

    private Discount selectedObject;

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Discount getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Discount selectedObject) {
        this.selectedObject = selectedObject;
    }

    @PostConstruct
    public void init() {
        System.out.println("----Discount Process Bean---");
        if (sessionBean.parameter instanceof Discount) {
            selectedObject = (Discount) sessionBean.parameter;
            processType = 2;

        } else {
            processType = 1;
            selectedObject = new Discount();

        }

        setListBtn(sessionBean.checkAuthority(new int[]{96, 97}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{24, 25, 26, 27}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }
    }

    public void save() {
        int result = 0;
        if (processType == 1) {
            result = discountService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                marwiz.goToPage("/pages/finance/discount/discountprocess.xhtml", selectedObject, 1, 127);
            }
        } else if (processType == 2) {
            result = discountService.update(selectedObject);
            if (result > 0) {
                marwiz.goToPage("/pages/finance/discount/discount.xhtml", null, 1, 126);

            }
        }
        sessionBean.createUpdateMessage(result);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void testBeforeDelete() {
        int result = 0;
        result = discountService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmDiscountProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("campaigndetailslinkedtothiscampaigncannotbedeletedbecauseithasacurrentorcurrentcategory")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = discountService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/finance/discount/discount.xhtml", null, 1, 126);
        }
        sessionBean.createUpdateMessage(result);

    }

}
