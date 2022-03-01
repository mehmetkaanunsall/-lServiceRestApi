/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.12.2018 03:31:44
 */
package com.mepsan.marwiz.inventory.pricelist.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;

@ManagedBean
@ViewScoped
public class PriceUpdateBean extends GeneralDefinitionBean<PriceListItem> {

    @ManagedProperty(value = "#{priceListItemService}")
    private IPriceListItemService priceListItemService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{priceListStockTabBean}")
    private PriceListStockTabBean priceListStockTabBean;

    private List<PriceListItem> selectedStocks;
    private PriceList selectedPriceList;
    private boolean isAll;

    public List<PriceListItem> getSelectedStocks() {
        return selectedStocks;
    }

    public void setSelectedStocks(List<PriceListItem> selectedStocks) {
        this.selectedStocks = selectedStocks;
    }

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setPriceListStockTabBean(PriceListStockTabBean priceListStockTabBean) {
        this.priceListStockTabBean = priceListStockTabBean;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    @Override
    @PostConstruct
    public void init() {
        selectedStocks = new ArrayList<>();
        listOfObjects = new ArrayList<>();
        if (sessionBean.parameter instanceof PriceList) {
            selectedPriceList = (PriceList) sessionBean.parameter;
        }
    }

    public void setIsAll() {
        if (isAll) {
            selectedStocks.clear();
            boolean isThere = false;
            for (PriceListItem list : listOfObjects) {
                if (list.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                    isThere = true;
                } else {
                    selectedStocks.add(list);
                }
            }
            if (isThere) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("onlyproductswhosesalespriceisnotdeterminedbythecentercanbeselected")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            selectedStocks.clear();
        }
    }

    @Override
    public void save() {
        if (!selectedStocks.isEmpty()) {
            int result = 0;
            result = priceListItemService.updatingPriceStock(selectedPriceList, selectedStocks, sessionBean.getUser().getLastBranch());
            if (result > 0) {//Liste baştan çekildi çünkü idler olmadığı için obje update edileceğinde kaydetmiyordu.
                priceListStockTabBean.setListOfObjects(priceListStockTabBean.findall(" "));

                RequestContext.getCurrentInstance().execute("PF('dlg_UpdatePriceAccordingToTesf').hide();");
                RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListStockTab:dtbStock");
                RequestContext.getCurrentInstance().execute("PF('stockPF').filter();");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectstock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void onRowSelect(SelectEvent event) {
        if (((PriceListItem) event.getObject()).getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            selectedStocks.remove((PriceListItem) event.getObject());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceUpdateDatatable:dtbPriceUpdate");
        }
    }

    public void onToggleSelect(ToggleSelectEvent event) {
        if (event.isSelected()) {
            Iterator i = selectedStocks.iterator();
            boolean isThere = false;
            while (i.hasNext()) {
                PriceListItem priceListItem;
                priceListItem = (PriceListItem) i.next();
                if (priceListItem.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                    isThere = true;
                    i.remove();
                }
            }
            if (isThere) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("onlyproductswhosesalespriceisnotdeterminedbythecentercanbeselected")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceUpdateDatatable:dtbPriceUpdate");
            }
        }
    }

    @Override
    public List<PriceListItem> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
