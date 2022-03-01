/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.09.2020 10:50:04
 */
package com.mepsan.marwiz.service.synchronize.presentation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.exchange.business.ExchangeService;
import com.mepsan.marwiz.service.branchinfo.business.GetBranchInfoService;
import com.mepsan.marwiz.service.item.business.CheckItemService;
import com.mepsan.marwiz.service.model.SendResult;
import com.mepsan.marwiz.service.order.business.SendOrderService;
import com.mepsan.marwiz.service.paro.business.ParoOfflineSalesService;
import com.mepsan.marwiz.service.price.business.SendPriceChangeRequestService;
import com.mepsan.marwiz.service.purchace.business.SendPurchaseService;
import com.mepsan.marwiz.service.sale.business.ISendSaleService;
import com.mepsan.marwiz.service.stock.business.SendStockInfoService;
import com.mepsan.marwiz.service.stock.business.SendStockRequestService;
import com.mepsan.marwiz.service.synchronize.business.ISynchronizeService;
import com.mepsan.marwiz.service.waste.business.SendWasteService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.annotation.RequestScope;

@ManagedBean
@RequestScope
public class SynchronizeBean {

    @ManagedProperty(value = "#{synchronizeService}")
    private ISynchronizeService synchronizeService;

    @ManagedProperty(value = "#{sendSaleService}")
    private ISendSaleService sendSaleService;

    @ManagedProperty(value = "#{exchangeService}")
    private ExchangeService exchangeService;

    @ManagedProperty(value = "#{sendPurchaseService}")
    private SendPurchaseService sendPurchaseService;

    @ManagedProperty(value = "#{sendStockRequestService}")
    private SendStockRequestService sendStockRequestService;

    @ManagedProperty(value = "#{sendPriceChangeRequestService}")
    private SendPriceChangeRequestService sendPriceChangeRequestService;

    @ManagedProperty(value = "#{checkItemService}")
    private CheckItemService checkItemService;

    @ManagedProperty(value = "#{sendStockInfoService}")
    private SendStockInfoService sendStockInfoService;

    @ManagedProperty(value = "#{paroOfflineSalesService}")
    private ParoOfflineSalesService paroOfflineSalesService;

    @ManagedProperty(value = "#{getBranchInfoService}")
    private GetBranchInfoService getBranchInfoService;

    @ManagedProperty(value = "#{sendWasteService}")
    private SendWasteService sendWasteService;
    
    @ManagedProperty(value = "#{sendOrderService}")
    private SendOrderService sendOrderService;

    private Gson gson;
    private SendResult result;
    private String udata;

    private String username;
    private String password;

    public void setSendSaleService(ISendSaleService sendSaleService) {
        this.sendSaleService = sendSaleService;
    }

    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setSendStockRequestService(SendStockRequestService sendStockRequestService) {
        this.sendStockRequestService = sendStockRequestService;
    }

    public void setSendPriceChangeRequestService(SendPriceChangeRequestService sendPriceChangeRequestService) {
        this.sendPriceChangeRequestService = sendPriceChangeRequestService;
    }

    public void setCheckItemService(CheckItemService checkItemService) {
        this.checkItemService = checkItemService;
    }

    public void setSendStockInfoService(SendStockInfoService sendStockInfoService) {
        this.sendStockInfoService = sendStockInfoService;
    }

    public void setParoOfflineSalesService(ParoOfflineSalesService paroOfflineSalesService) {
        this.paroOfflineSalesService = paroOfflineSalesService;
    }

    public void setGetBranchInfoService(GetBranchInfoService getBranchInfoService) {
        this.getBranchInfoService = getBranchInfoService;
    }

    public void setSendWasteService(SendWasteService sendWasteService) {
        this.sendWasteService = sendWasteService;
    }

    public void setSendPurchaseService(SendPurchaseService sendPurchaseService) {
        this.sendPurchaseService = sendPurchaseService;
    }

    public void setSynchronizeService(ISynchronizeService synchronizeService) {
        this.synchronizeService = synchronizeService;
    }

    public void setSendOrderService(SendOrderService sendOrderService) {
        this.sendOrderService = sendOrderService;
    }

    @PostConstruct
    public void init() {        
        gson = new Gson();

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
        udata = request.getHeader("udata");

        if (!checkUserJson()) {
            result = new SendResult(false, 102, "wrong udata");
            return;
        }
        if (!checkUser()) {
            result = new SendResult(false, 201, "authentication error");
            return;
        }

        synchronize();
        result = new SendResult(true);

    }

    public String printResult() {
        String toJson = gson.toJson(result);
        return toJson;
    }
   

    private boolean checkUserJson() {
        boolean result = false;
        try {
            JsonObject fromJson = gson.fromJson(udata, JsonObject.class);
            username = fromJson.get("username").getAsString();
            password = fromJson.get("password").getAsString();
            result = true;
        } catch (Exception ex) {
            Logger.getLogger(SynchronizeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private boolean checkUser() {
        return synchronizeService.checkUser(username, password);
    }

    public void synchronize() {
        exchangeService.updateExchange();
        sendSaleService.sendSaleNotSendedToCenterAsync();
        sendPurchaseService.sendPurchaseNotSendedToCenterAsync();
        sendStockRequestService.sendNotSendedStockRequestAsync();
        sendStockRequestService.checkStockRequestAsync();
        checkItemService.listBrand();
        checkItemService.listUnit();
        checkItemService.listTax();
        checkItemService.listCentralSupplier();
        checkItemService.listStock();
        sendStockInfoService.sendStockInfoAsync();
        checkItemService.listNotificationAsync();
        sendPriceChangeRequestService.sendNotSendedPriceChangeRequestAsync();
        sendPriceChangeRequestService.checkPriceChangeRequestAsync();
        paroOfflineSalesService.sendSalesAsync();
        sendWasteService.sendWasteAsync();
        checkItemService.listCampaign();
        checkItemService.listAccount();
        checkItemService.listWasteReason();
        checkItemService.listStarbucksStock();
        getBranchInfoService.callBranchInfoForAllBranches();
        sendOrderService.sendOrderNotSendedToCenterAsync();
        checkItemService.listCurrency();

    }

}
