/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   20.02.2018 12:32:45
 */
package com.mepsan.marwiz.inventory.stocktaking.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingItemService;
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
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class StockTakingStockSaleControlBean extends GeneralDefinitionBean<StockTakingItem> {

    @ManagedProperty(value = "#{stockTakingItemService}")
    private IStockTakingItemService stockTakingItemService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private StockTaking stockTaking;

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockTakingItemService(IStockTakingItemService stockTakingItemService) {
        this.stockTakingItemService = stockTakingItemService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---------StockTakingStockSaleControlBean---------");
        stockTaking = new StockTaking();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking) {
                    stockTaking = (StockTaking) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }
        listOfObjects = new ArrayList<>();

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void openDialog() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        listOfObjects = stockTakingItemService.findAllSaleControlList(stockTaking);
        if (listOfObjects.isEmpty()) { // yoksa

            StockTakingMinusStockBean stockTakingMinusStockBean = (StockTakingMinusStockBean) viewMap.get("stockTakingMinusStockBean");
            stockTakingMinusStockBean.openDialog();

        } else { // varsa giriş çıkışları gösteren dialoğu açar.

            setAutoCompleteValue(null);
            RequestContext.getCurrentInstance().execute("PF('stockTakingItemSaleControlPF').filter();");
            RequestContext.getCurrentInstance().execute("PF('dlg_StockTakingStockControl').show();");
        }
    }

    /**
     *
     */
    @Override
    public void save() {


        int result = 0;
        // -------Farkı kapattıktan sonra giriş çıkış olmuşsa ona göre hesaplanarak sayım miktarları güncellenir-------
        result = stockTakingItemService.updateSaleControl(stockTaking);
        if (result > 0) {


            //  güncelledikten sonra tabı günceller.  
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            StockTakingStockTabBean stockTakingStockTabBean = (StockTakingStockTabBean) viewMap.get("stockTakingStockTabBean");
            stockTakingStockTabBean.setListOfObjects(stockTakingStockTabBean.findAll("", stockTaking));
            RequestContext.getCurrentInstance().update("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem");

            RequestContext.getCurrentInstance().execute("PF('dlg_StockTakingStockControl').hide();");

            //-----eğer güncellendikten sonra girilen miktarı eksiye düşen stok olursa uyarı diyaloğu açar.
            StockTakingMinusStockBean stockTakingMinusStockBean = (StockTakingMinusStockBean) viewMap.get("stockTakingMinusStockBean");
            stockTakingMinusStockBean.openDialog();

        }

    }

    @Override
    public List<StockTakingItem> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
