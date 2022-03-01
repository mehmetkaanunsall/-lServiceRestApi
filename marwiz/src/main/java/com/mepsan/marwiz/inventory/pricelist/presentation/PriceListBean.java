/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 10:34:08
 */
package com.mepsan.marwiz.inventory.pricelist.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
public class PriceListBean extends GeneralDefinitionBean<PriceList> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{priceListService}")
    private IPriceListService priceListService;

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
    @Override
    public void init() {
        System.out.println("------PriceListBean");

        toogleList = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true);
        }
        listOfObjects = findall();
        setListBtn(sessionBean.checkAuthority(new int[]{139, 140}, 0));
    }

    @Override
    public void create() {

        selectedObject = new PriceList();
        RequestContext.getCurrentInstance().execute("PF('dlg_PriceList').show();");

    }

    @Override
    public void save() {
        int result = 0;

        result = priceListService.create(selectedObject);

        if (result > 0) {
            selectedObject.setUserCreated(sessionBean.getUser());
            selectedObject.setDateCreated(new Date());
            RequestContext.getCurrentInstance().execute("PF('dlg_PriceList').hide();");
            selectedObject.setId(result);
            marwiz.goToPage("/pages/inventory/pricelist/pricelistprocess.xhtml", selectedObject, 0, 30);
        }

        if (result == -1) {//başka default fiyat listesi varsa
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("defaultpricelistalreadyexists")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            sessionBean.createUpdateMessage(result);
        }

    }

    @Override
    public List<PriceList> findall() {
        return priceListService.listofPriceList();
    }

}
