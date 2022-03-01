/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.01.2019 15:09:25
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

@ManagedBean
@ViewScoped
public class InvoiceItemQuickAddBean extends AuthenticationLists {

    @ManagedProperty(value = "#{priceListItemService}")
    public IPriceListItemService priceListItemService;

    @ManagedProperty(value = "#{invoiceItemTabBean}")
    public InvoiceItemTabBean invoiceItemTabBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    PriceListItem selectedObject;
    int processType;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setInvoiceItemTabBean(InvoiceItemTabBean invoiceItemTabBean) {
        this.invoiceItemTabBean = invoiceItemTabBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public PriceListItem getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(PriceListItem selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    @PostConstruct
    public void init() {
        System.out.println("----InvoiceItemQuickAddBean----");
        setListBtn(sessionBean.checkAuthority(new int[]{142, 143}, 0));

    }

    /**
     * Satış fiyat listesine ürün ekleme dialogunu açar
     *
     * @param stock
     */
    public void createDialog(Stock stock) {
        processType = 1;
        selectedObject = new PriceListItem();
        selectedObject.setStock(stock);
        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        bringTagOfCurrency();
        selectedObject.setIs_taxIncluded(false);
        RequestContext.getCurrentInstance().execute("PF('dlg_pricelistprocess').show();");
        RequestContext.getCurrentInstance().update("frmPriceListStockProcess");
    }

    /**
     * Bu metot tablede düzenleme yapılan objeyi çekmek için kullanılır.
     *
     * @param event
     */
    public void onCellEditPriceList(CellEditEvent event) {
        processType = 2;
        FacesContext context = FacesContext.getCurrentInstance();
        selectedObject = context.getApplication().evaluateExpressionGet(context, "#{PriceListItem}", PriceListItem.class);
        BigDecimal priceListPrice = BigDecimal.valueOf(0);

        if (selectedObject.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (event.getColumn().getClientId().contains("clmPrice")) {
                selectedObject.setPrice((BigDecimal) event.getOldValue());
            } else if (event.getColumn().getClientId().contains("clmCurrency")) {
                selectedObject.getCurrency().setId((int) event.getOldValue());
                bringTagOfCurrency();
            } else if (event.getColumn().getClientId().contains("clmIsTaxIncluded")) {
                selectedObject.setIs_taxIncluded((boolean) event.getOldValue());
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            RequestContext.getCurrentInstance().execute("updateDatatable()");
            return;
        }

        priceListPrice = selectedObject.getPrice();
        if (selectedObject.isIs_taxIncluded()) {
            if (invoiceItemTabBean.getSelectedObject().getTaxRate() != null) {
                priceListPrice = priceListPrice.divide((BigDecimal.valueOf(1).add((invoiceItemTabBean.getSelectedObject().getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN)))), 4, RoundingMode.HALF_EVEN);
            }
        }

        BigDecimal bd = exchangeService.bringExchangeRate(invoiceItemTabBean.getSelectedObject().getCurrency(), selectedObject.getCurrency(), sessionBean.getUser());
        BigDecimal currencPurchasePrice = invoiceItemTabBean.getSelectedObject().getTotalPrice().divide(invoiceItemTabBean.getSelectedObject().getQuantity(), 4, RoundingMode.HALF_EVEN).multiply(bd);
        selectedObject.setProfitRate(((priceListPrice.subtract(currencPurchasePrice)).divide(currencPurchasePrice, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100)));
        bringTagOfCurrency();

    }

    /**
     * processType:2 ise Bu metot açılan birim fiyat güncelleme penceresi
     * üzerinden ürünün birim fiyatını güncellemek için kullanılır.
     * processType:1 ise varsayılan satış fiyat listesine ürün ekler
     */
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (invoiceItemTabBean.getListOfPriceListItem() != null && !invoiceItemTabBean.getListOfPriceListItem().isEmpty()) {
            selectedObject = invoiceItemTabBean.getListOfPriceListItem().get(0);
        }

        if (selectedObject != null && selectedObject.getPrice() != null && selectedObject.getCurrency().getId() != 0) {
            BigDecimal transferStockPrice = selectedObject.getPrice();
            //vergisiz ve iskonto uygulanmış birim fiyat
            BigDecimal uniPrice = invoiceItemTabBean.getSelectedObject().getTotalPrice().divide(invoiceItemTabBean.getSelectedObject().getQuantity(), 4, RoundingMode.HALF_EVEN);

            //fiyat listesinde vergi dahil ise vergisiz fiyatını bulduk.
            if (selectedObject.isIs_taxIncluded()) {
                BigDecimal div = BigDecimal.ONE.add(invoiceItemTabBean.getSelectedObject().getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                transferStockPrice = transferStockPrice.divide(div, 4, RoundingMode.HALF_EVEN);
            }

            //kur farkını uyguladık
            if (selectedObject.getCurrency().getId() != invoiceItemTabBean.getSelectedObject().getCurrency().getId()) {
                transferStockPrice = transferStockPrice.multiply(exchangeService.bringExchangeRate(selectedObject.getCurrency(), invoiceItemTabBean.getSelectedObject().getCurrency(), sessionBean.getUser()));
            }

            int result = 0;
            //ürünün alış fiyatı satış fiyatından büyükmü kontrol et.(vergisiz hali)
            if (transferStockPrice.compareTo(uniPrice) >= 0 || !sessionBean.getLastBranchSetting().isIsPurchaseControl()) {

                if (processType == 1) {//ekleme
                    result = priceListItemService.createItem(selectedObject, invoiceItemTabBean.getSelectedInvoice().getBranchSetting().getBranch());
                    if (result > 0) {
                        //karlılık hesapla
                        BigDecimal bd = exchangeService.bringExchangeRate(invoiceItemTabBean.getSelectedObject().getCurrency(), selectedObject.getCurrency(), sessionBean.getUser());
                        BigDecimal currencPurchasePrice = invoiceItemTabBean.getSelectedObject().getUnitPrice().multiply(bd);
                        if (currencPurchasePrice.compareTo(BigDecimal.ZERO) == 0) {
                            selectedObject.setProfitRate(BigDecimal.ZERO);
                        } else {
                            selectedObject.setProfitRate(((selectedObject.getPrice().subtract(currencPurchasePrice)).divide(currencPurchasePrice, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100)));
                        }
                        selectedObject.setId(result);
                        if (invoiceItemTabBean.getListOfPriceListItem() == null) {
                            invoiceItemTabBean.setListOfPriceListItem(new ArrayList<>());
                        }
                        invoiceItemTabBean.getListOfPriceListItem().clear();
                        invoiceItemTabBean.getListOfPriceListItem().add(selectedObject);
                        processType = 0;
                        RequestContext.getCurrentInstance().execute("PF('dlg_pricelistprocess').hide();");
                        RequestContext.getCurrentInstance().update("frmPurchaseControlInformation");
                        RequestContext.getCurrentInstance().update("dlg_purchasecontrolinformation");
                    }
                    sessionBean.createUpdateMessage(result);
                    return;
                } else if (processType == 2) {//satış fiyat listesi güncelleme
                    result = priceListItemService.update(selectedObject);

                    if (result > 0) {
                        invoiceItemTabBean.bringCurrency();
                        invoiceItemTabBean.calculater(1);
                        invoiceItemTabBean.getSelectedObject().setIsCanSaveItem(true);
                        RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:grdInvoiceStokProcess");
                        context.execute("PF('dlg_purchasecontrolinformation').hide()");
                        processType = 0;
                    }
                    sessionBean.createUpdateMessage(result);
                } else {
                    RequestContext.getCurrentInstance().execute("PF('dlg_purchasecontrolinformation').hide()");
                }

            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("thepurchasepriceoftheproductcannotbehigherthanthesalesprice"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("grwProcessMessage");
                return;
            }
        } else {
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            message.setDetail(sessionBean.getLoc().getString("thepurchasepriceoftheproductcannotbehigherthanthesalesprice"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            context.update("grwProcessMessage");
            return;
        }

        if (!invoiceItemTabBean.isIsSpeedAdd()) {//hızlı ekleme değilse
            invoiceItemTabBean.saveInvoiceItem();
        }

    }

    public void bringTagOfCurrency() {
        for (Currency s : sessionBean.getCurrencies()) {
            if (s.getId() == selectedObject.getCurrency().getId()) {
                selectedObject.getCurrency().setTag(s.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
    }
}
