/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 10:51:02
 */
package com.mepsan.marwiz.inventory.pricelist.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListService;
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
public class PriceListProcessBean extends AuthenticationLists{

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{priceListService}")
    private IPriceListService priceListService;
    


    private PriceList selectedObject;
    private int activeIndex;

    public PriceList getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(PriceList selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
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

    @PostConstruct
    public void init() {
        selectedObject = new PriceList();
        System.out.println("----------PriceListProcessBean----------");
        if (sessionBean.parameter instanceof PriceList) {
            selectedObject = (PriceList) sessionBean.parameter;

        }
        
       setListBtn(sessionBean.checkAuthority(new int[]{140, 141}, 0));
       setListTab(sessionBean.checkAuthority(new int[]{37, 38, 39}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    /**
     * Bu methot güncelleme sayfasında kişi bilgilerini günceller.
     */
    public void save() {
        int result = 0;
        result = priceListService.update(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/inventory/pricelist/pricelist.xhtml", null, 1, 29);
        }

        if (result == -1) {//başka default fiyat listesi varsa
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("defaultpricelistalreadyexists")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            sessionBean.createUpdateMessage(result);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }
    
    public void testBeforeDelete(){
         if(selectedObject.isIsDefault()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cannotbedeletedthedefaultpricelist")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }else{
            RequestContext.getCurrentInstance().update("frmPriceListProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        }
    }
    
    public void delete(){
        
        int result = 0;
        result = priceListService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/inventory/pricelist/pricelist.xhtml", null, 1, 29);
        }
        sessionBean.createUpdateMessage(result);
        
    }
   
   
}
