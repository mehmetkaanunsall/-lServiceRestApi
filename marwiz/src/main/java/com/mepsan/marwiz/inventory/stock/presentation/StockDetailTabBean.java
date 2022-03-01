/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class StockDetailTabBean extends AuthenticationLists {

    private Stock stock;
    private boolean isProfitRate;
    private PriceListItem oldSalePriceListItem;
    private List<Unit> unitList;
    private boolean isShowEquivalent;
    private boolean isIncome;
    private boolean isIncomeExpenseView;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{taxGroupService}")
    public ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{unitService}")
    public IUnitService unitService;

    @ManagedProperty(value = "#{priceListService}")
    public IPriceListService priceListService;

    @ManagedProperty(value = "#{priceListItemService}")
    public IPriceListItemService priceListItemService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public boolean isIsProfitRate() {
        return isProfitRate;
    }

    public void setIsProfitRate(boolean isProfitRate) {
        this.isProfitRate = isProfitRate;
    }

    public PriceListItem getOldSalePriceListItem() {
        return oldSalePriceListItem;
    }

    public void setOldSalePriceListItem(PriceListItem oldSalePriceListItem) {
        this.oldSalePriceListItem = oldSalePriceListItem;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public boolean isIsShowEquivalent() {
        return isShowEquivalent;
    }

    public void setIsShowEquivalent(boolean isShowEquivalent) {
        this.isShowEquivalent = isShowEquivalent;
    }

    public boolean isIsIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public boolean isIsIncomeExpenseView() {
        return isIncomeExpenseView;
    }

    public void setIsIncomeExpenseView(boolean isIncomeExpenseView) {
        this.isIncomeExpenseView = isIncomeExpenseView;
    }

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    public void setPriceListService(IPriceListService priceListService) {
        this.priceListService = priceListService;
    }

    @PostConstruct
    public void init() {

        stock = new Stock();
        unitList = new ArrayList<>();
        isIncome = true;
        unitList = unitService.findAll();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    stock = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                        if (stock.getStockInfo().getMinProfitRate() != null) {
                            isProfitRate = true;
                        }
                    }
                    //Satınalma fiyatı her zaman kdv hariç gösterildi 07.01.2019 ali kurt
                    if (stock.getPurchasePriceListItem().getPrice() == null) {
                        stock.getPurchasePriceListItem().getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                        stock.getPurchasePriceListItem().setIs_taxIncluded(false);
                    } else {//satınalma fiyatı var ise  
                        if (stock.getPurchasePriceListItem().isIs_taxIncluded()) {
                            TaxGroup taxGroup = taxGroupService.findTaxGroupsKDV(stock, true, sessionBean.getUser().getLastBranchSetting());//stokun satınalma vergisini bul
                            if (taxGroup != null && taxGroup.getRate() != null && taxGroup.getRate().doubleValue() > 0) {
                                BigDecimal x = BigDecimal.ONE.add(taxGroup.getRate().divide(new BigDecimal(100), 16, RoundingMode.HALF_EVEN));
                                stock.getPurchasePriceListItem().setPrice(stock.getPurchasePriceListItem().getPrice().divide(x, 16, RoundingMode.HALF_EVEN));
                            }
                        }
                        stock.getPurchasePriceListItem().setIs_taxIncluded(false);
                    }
                    if (stock.getSalePriceListItem().getPrice() == null) {
                        stock.getSalePriceListItem().getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                        stock.getSalePriceListItem().setIs_taxIncluded(true);
                    }
                }
            }
        }
        oldSalePriceListItem = new PriceListItem();
        oldSalePriceListItem.setPrice(stock.getSalePriceListItem().getPrice());
        oldSalePriceListItem.getCurrency().setId(stock.getSalePriceListItem().getCurrency().getId());
        oldSalePriceListItem.setIs_taxIncluded(stock.getSalePriceListItem().isIs_taxIncluded());
        if (stock.getStockInfo().getWeightUnit().getId() != stock.getStockInfo().getMainWeightUnit().getId()) {
            isShowEquivalent = true;
        } else {
            isShowEquivalent = false;
        }
        bringUnit();

        if (stock.isIsService()) {
            isIncomeExpenseView = true;

            if (stock.getStockInfo().getIncomeExpense().getId() != 0) {
                if (stock.getStockInfo().getIncomeExpense().isIsIncome()) {
                    isIncome = true;
                } else {
                    isIncome = false;
                }
            }

        } else {
            isIncomeExpenseView = false;
        }

        setListBtn(sessionBean.checkAuthority(new int[]{125, 143}, 0));
    }

    public void changeProfitRate() {
        isProfitRate = !isProfitRate;
        if (isProfitRate) {
            stock.getStockInfo().setRecommendedPrice(null);
            stock.getStockInfo().getCurrency().setId(0);
        } else {
            stock.getStockInfo().setMinProfitRate(null);
        }
    }

    public void changeUnit() {
        bringUnit();
        if (stock.getStockInfo().getWeightUnit().getId() == 0 || stock.getStockInfo().getMainWeightUnit().getId() == 0) {
            stock.getStockInfo().setMainWeight(null);
            isShowEquivalent = false;
        } else if (stock.getStockInfo().getWeightUnit().getId() == stock.getStockInfo().getMainWeightUnit().getId()) {
            stock.getStockInfo().setMainWeight(BigDecimal.ONE);
            isShowEquivalent = false;
        } else if (stock.getStockInfo().getWeightUnit().getId() != stock.getStockInfo().getMainWeightUnit().getId()) {
            stock.getStockInfo().setMainWeight(null);
            isShowEquivalent = true;
        }
    }

    public void bringUnit() {

        for (Unit unit : unitList) {
            if (unit.getId() == stock.getStockInfo().getWeightUnit().getId()) {
                stock.getStockInfo().getWeightUnit().setName(unit.getName());
            }
            if (unit.getId() == stock.getStockInfo().getMainWeightUnit().getId()) {
                stock.getStockInfo().getMainWeightUnit().setName(unit.getName());
            }
        }

    }

    public BigDecimal calUnitPrice() {
        BigDecimal price = BigDecimal.ZERO;
        boolean isMainWeight = false;
        if (stock.getStockInfo().getSaleMandatoryPrice() != null && stock.getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            price = stock.getStockInfo().getSaleMandatoryPrice();
        } else if (stock.getStockInfo().getCurrentSalePrice() != null && stock.getStockInfo().getCurrentSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            price = stock.getStockInfo().getCurrentSalePrice();
        } else {
            price = BigDecimal.ZERO;
        }

        for (Unit unit : unitList) {
            if (unit.getId() == stock.getStockInfo().getWeightUnit().getId()) {
                if (unit.getMainWeightUnit().getId() > 0 && unit.getMainWeight() != null) {
                    isMainWeight = true;
                    stock.getStockInfo().getWeightUnit().setMainWeightUnit(new Unit());
                    stock.getStockInfo().getWeightUnit().getMainWeightUnit().setId(unit.getId());
                    stock.getStockInfo().getWeightUnit().setMainWeight(unit.getMainWeight());
                }
                break;
            }
        }
        if (isMainWeight) {

            if (stock.getStockInfo().getWeight() != null && stock.getStockInfo().getWeightUnit().getMainWeight() != null && stock.getStockInfo().getWeight().compareTo(BigDecimal.ZERO) != 0) {
                price = price.multiply(stock.getStockInfo().getWeightUnit().getMainWeight()).divide(stock.getStockInfo().getWeight(), RoundingMode.HALF_EVEN);
            } else {
                price = BigDecimal.ZERO;
            }
        } else {

            if (stock.getStockInfo().getWeight() != null && stock.getStockInfo().getWeight().compareTo(BigDecimal.ZERO) != 0 && stock.getStockInfo().getWeightUnit().getId() > 0) {
                price = price.divide(stock.getStockInfo().getWeight(), 4, RoundingMode.HALF_EVEN);
            } else {
                price = BigDecimal.ZERO;
            }
        }
        return price;

    }

    public void save() {
        int result = stockService.updateDetail(stock);

        sessionBean.createUpdateMessage(result);
    }

    public void savePriceList() {
        stock.getSalePriceListItem().getStock().setId(stock.getId());
        if (stock.getSalePriceListItem().getPriceList().getId() == 0) {
            PriceList priceList = new PriceList();
            priceList = priceListService.findDefaultPriceList(false, sessionBean.getUser().getLastBranch());
            stock.getSalePriceListItem().getPriceList().setId(priceList.getId());
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && stock.getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (oldSalePriceListItem.getPrice() == null && stock.getSalePriceListItem().getPrice() == null) {

            } else if ((oldSalePriceListItem.getPrice() != null && stock.getSalePriceListItem().getPrice() == null) || (oldSalePriceListItem.getPrice() == null && stock.getSalePriceListItem().getPrice() != null)) {
                stock.getSalePriceListItem().setPrice(oldSalePriceListItem.getPrice());
                stock.getSalePriceListItem().getCurrency().setId(oldSalePriceListItem.getCurrency().getId());
                stock.getSalePriceListItem().setIs_taxIncluded(oldSalePriceListItem.isIs_taxIncluded());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thesalepriceofthestockhasnotchangedbecausethecenterhasbeendetermined")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else if (oldSalePriceListItem.getCurrency().getId() != stock.getSalePriceListItem().getCurrency().getId() || oldSalePriceListItem.isIs_taxIncluded() != stock.getSalePriceListItem().isIs_taxIncluded() || oldSalePriceListItem.getPrice().compareTo(stock.getSalePriceListItem().getPrice()) != 0) {
                stock.getSalePriceListItem().setPrice(oldSalePriceListItem.getPrice());
                stock.getSalePriceListItem().getCurrency().setId(oldSalePriceListItem.getCurrency().getId());
                stock.getSalePriceListItem().setIs_taxIncluded(oldSalePriceListItem.isIs_taxIncluded());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thesalepriceofthestockhasnotchangedbecausethecenterhasbeendetermined")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

        }
        boolean isInsertPriceList = true;
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            if (stock.getStockInfo().isIsPassive() && stock.getStatus().getId() == 3) {
                isInsertPriceList = false;
            } else if (stock.getStatus().getId() == 4) {
                isInsertPriceList = false;
            }
        } else {
            if (stock.getStatus().getId() == 4) {
                isInsertPriceList = false;
            }
        }
        if (isInsertPriceList) {//Pasif değildir fiyat listesine eklenebilir.
            int result = 0;
            if (stock.getSalePriceListItem().getId() == 0) {//Fiyat Listesinde Yok
                result = priceListItemService.create(stock.getSalePriceListItem());
                if (result > 0) {
                    stock.getSalePriceListItem().setId(result);
                }
            } else {//Fiyat Listesinde Var 
                result = priceListItemService.update(stock.getSalePriceListItem());
            }

            if (result > 0) {
                stock.getStockInfo().setCurrentSalePrice(stock.getSalePriceListItem().getPrice());
            }

            if (stock.getSalePriceListItem().getPrice() == null) {
                stock.getSalePriceListItem().setPrice(oldSalePriceListItem.getPrice());
                stock.getSalePriceListItem().getCurrency().setId(oldSalePriceListItem.getCurrency().getId());
                stock.getSalePriceListItem().setIs_taxIncluded(oldSalePriceListItem.isIs_taxIncluded());
            }

            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("pricedoesnotenteredbecauseofbeingpassivetostock")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void updateAllInformation() {

        if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            stock.getStockInfo().setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());

            incomeExpenseBookFilterBean.setSelectedData(null);
        }

        RequestContext.getCurrentInstance().update("tbvStokProc:frmStockDetailTab:pngIncomeExpense");

    }

    public void incomeOrExpense(int type) {

        if (type == 0) {
            isIncome = false;
        } else {
            isIncome = true;
        }
        stock.getStockInfo().setIncomeExpense(null);
        RequestContext.getCurrentInstance().update("tbvStokProc:frmStockDetailTab:pngIncomeExpense");

    }

}
