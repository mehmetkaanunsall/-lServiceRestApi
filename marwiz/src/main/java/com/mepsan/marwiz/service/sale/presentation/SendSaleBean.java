/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 22.03.2018 13:54:15
 */
package com.mepsan.marwiz.service.sale.presentation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.log.SendSale;
import com.mepsan.marwiz.general.model.wot.AesCrypt;
import com.mepsan.marwiz.service.model.SendResult;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.annotation.RequestScope;
import javax.faces.bean.ManagedProperty;
import com.mepsan.marwiz.service.sale.business.ISendSaleService;

@ManagedBean
@RequestScope
public class SendSaleBean {

    @ManagedProperty(value = "#{sendSaleService}")
    private ISendSaleService sendSaleService;

    private Gson gson;
    private AesCrypt aesCrypt;
    private SendResult result;
    private String encUdata;
    private String encData;
    private String udata;
    private String data;

    private final static String KEY = "1234567812345678"; // 128 bit key
    private final static String INIT_VECTOR = "8765432187654321"; // 16 bytes IV

    private int saleid;
    private String username;
    private String password;
    private SendSale sendSale;

    public void setSendSaleService(ISendSaleService sendSaleService) {
        this.sendSaleService = sendSaleService;
    }    

    @PostConstruct
    public void init() {
        gson = new Gson();
        aesCrypt = new AesCrypt(KEY, INIT_VECTOR);

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        encUdata = request.getHeader("udata");
        //System.out.println("udata:" + encUdata);
        String[] dataStringArray = request.getParameterMap().get("data");
        encData = mergeStringArray(dataStringArray);
        //System.out.println("data:" + encData);

        if (!checkParameters()) {
            result = new SendResult(false, 100, "missing parameter");
            return;
        }
        if (!checkEncryption()) {
            result = new SendResult(false, 101, "incompatible encryption");
            return;
        }
        if (!checkUserJson()) {
            result = new SendResult(false, 102, "wrong udata");
            return;
        }
        if (!checkDataJson()) {
            result = new SendResult(false, 103, "wrong data");
            return;
        }
        if (!checkUser()) {
            result = new SendResult(false, 201, "authentication error");
            return;
        }
        if (!checkData()) {
            result = new SendResult(false, 202, "no sendsale data");
            return;
        }
        if (!checkDataIsNotSend()) {
            result = new SendResult(false, 203, "sendsale data already sent");
            return;
        }
        sendSaleService.sendSaleToCenterAsync(sendSale);
        result = new SendResult(true);

    }

    public String printResult() {
        String toJson = gson.toJson(result);
        return toJson;
    }

    private String mergeStringArray(String[] strings) {
        if (strings == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (String string : strings) {
            sb.append(string);
        }
        return sb.toString();
    }

    private boolean checkParameters() {
        boolean result = false;
        if (encUdata != null && !encUdata.isEmpty() && encData != null && !encData.isEmpty()) {
            result = true;
        }
        return result;
    }

    private boolean checkEncryption() {
        boolean result = false;
        try {
            udata = aesCrypt.decryptBase64(encUdata);
            data = aesCrypt.decryptBase64(encData);
            result = true;
        } catch (Exception ex) {
            Logger.getLogger(SendSaleBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private boolean checkUserJson() {
        boolean result = false;
        try {
            //System.out.println("udata" + udata);
            JsonObject fromJson = gson.fromJson(udata, JsonObject.class);
            username = fromJson.get("username").getAsString();
            password = fromJson.get("password").getAsString();
            result = true;
        } catch (Exception ex) {
            Logger.getLogger(SendSaleBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private boolean checkUser() {
        return sendSaleService.checkUser(username, password);
    }

    private boolean checkDataJson() {
        boolean result = false;
        try {
            //System.out.println("udata" + data);
            JsonObject fromJson = gson.fromJson(data, JsonObject.class);
            saleid = fromJson.get("saleid").getAsInt();
            result = true;
        } catch (Exception ex) {
            Logger.getLogger(SendSaleBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private boolean checkData() {
        sendSale = sendSaleService.findBySaleId(saleid);
        return sendSale != null;
    }

    private boolean checkDataIsNotSend() {
        return !sendSale.isIssend();
    }

   

    

}
