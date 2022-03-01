/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:54:06 PM
 */
package com.mepsan.marwiz.general.refinerypurchase.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.refinerypurchase.business.IRefineryPurchaseService;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

@ManagedBean
@ViewScoped
public class RefineryPurchaseBean extends GeneralDefinitionBean<RefineryStockPrice> {

    @ManagedProperty(value = "#{refineryPurchaseService}")
    private IRefineryPurchaseService refineryPurchaseService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setRefineryPurchaseService(IRefineryPurchaseService refineryPurchaseService) {
        this.refineryPurchaseService = refineryPurchaseService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--RefineryPurchaseBean---");
        listOfObjects = findall();
        listCreate();
    }

    public void listCreate() {
        boolean isThere1 = false;
        boolean isThere2 = false;
        boolean isThere3 = false;
        boolean isThere4 = false;
        for (int i = 0; i < listOfObjects.size(); i++) {
            isThere1 = false;
            isThere2 = false;
            isThere3 = false;
            isThere4 = false;
            for (int j = 0; j < listOfObjects.size(); j++) {
                if (listOfObjects.get(i).getStock().getId() == listOfObjects.get(j).getStock().getId()) {
                    if (listOfObjects.get(j).getRefineryId() == 1) {
                        isThere1 = true;
                    }
                    if (listOfObjects.get(j).getRefineryId() == 2) {
                        isThere2 = true;
                    }
                    if (listOfObjects.get(j).getRefineryId() == 3) {
                        isThere3 = true;
                    }
                    if (listOfObjects.get(j).getRefineryId() == 4) {
                        isThere4 = true;
                    }
                }
            }
            Currency currency = new Currency();
            currency.setId(sessionBean.getUser().getLastBranch().getId());
            currency.setTag(getSessionCurrency());
            RefineryStockPrice refineryStockPrice = new RefineryStockPrice();
            if (!isThere1) {
                refineryStockPrice.setStock(listOfObjects.get(i).getStock());
                refineryStockPrice.setRefineryId(1);
                refineryStockPrice.setCurrency(currency);
                refineryStockPrice.setPrice(BigDecimal.ZERO);

                listOfObjects.add(i + 1, refineryStockPrice);
            }
            if (!isThere2) {
                refineryStockPrice = new RefineryStockPrice();
                refineryStockPrice.setStock(listOfObjects.get(i).getStock());
                refineryStockPrice.setRefineryId(2);
                refineryStockPrice.setCurrency(currency);
                refineryStockPrice.setPrice(BigDecimal.ZERO);

                listOfObjects.add(i + 1, refineryStockPrice);
            }
            if (!isThere3) {
                refineryStockPrice = new RefineryStockPrice();
                refineryStockPrice.setStock(listOfObjects.get(i).getStock());
                refineryStockPrice.setRefineryId(3);
                refineryStockPrice.setCurrency(currency);
                refineryStockPrice.setPrice(BigDecimal.ZERO);

                listOfObjects.add(i + 1, refineryStockPrice);

            }
            if (!isThere4) {
                refineryStockPrice = new RefineryStockPrice();
                refineryStockPrice.setStock(listOfObjects.get(i).getStock());
                refineryStockPrice.setRefineryId(4);
                refineryStockPrice.setCurrency(currency);
                refineryStockPrice.setPrice(BigDecimal.ZERO);

                listOfObjects.add(i + 1, refineryStockPrice);
            }
        }
        for (Iterator<RefineryStockPrice> iterator = listOfObjects.iterator(); iterator.hasNext();) { // rafinerisi olmayanları sildik
            RefineryStockPrice next = iterator.next();
            if (next.getRefineryId() == 0) {
                iterator.remove();
            }
        }
        for (int i = 0; i < listOfObjects.size(); i++) {
            listOfObjects.get(i).setRowId(i + 1);
        }
    }

    public String getSessionCurrency() {
        for (Currency s : sessionBean.getCurrencies()) {
            if (s.getId() == sessionBean.getUser().getLastBranch().getCurrency().getId()) {
                return s.getNameMap().get(sessionBean.getLangId()).getName();
            }
        }
        return null;
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedObject.getPrice().compareTo(BigDecimal.ZERO) > 0 && selectedObject.getCurrency().getId() != 0) {
            int isThere = refineryPurchaseService.findRefineryPrice(selectedObject);
            int result = 0;
            if (isThere == 1) {
                result = refineryPurchaseService.update(selectedObject);

            } else {
                result = refineryPurchaseService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                }
            }
            if (result > 0) {
                context.execute("PF('refinreyPurchasePF').filter();");
                listOfObjects = findall();
                listCreate();
                RequestContext.getCurrentInstance().execute("updateDatatable()");
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    @Override
    public List<RefineryStockPrice> findall() {
        return refineryPurchaseService.findAll();
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedObject = context.getApplication().evaluateExpressionGet(context, "#{RefineryPurchase}", RefineryStockPrice.class);

        for (Currency s : sessionBean.getCurrencies()) {
            if (s.getId() == selectedObject.getCurrency().getId()) {
                selectedObject.getCurrency().setTag(s.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
        save();
    }
}
