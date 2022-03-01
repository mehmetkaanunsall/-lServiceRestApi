/*
 Bu sınıf MPOS bilgilerinin stawiz+' a aktarılmasını sağlar. Http POST yöntemi yapılan isteklere cevap verir
 Header ve Body içerisinde alanlar Bekler
 Örnek URL    = http://ip_address:8080/marwiz/pages/service/sendmposinformaion.xhtml
 Örnek Body   = {
    "username":"us",
    "password":"ps.",
    "licenceCode":"1",
    "wsaddress":"ws",
    "log": {jsonObject}}
}

 */
package com.mepsan.marwiz.service.mposinformation.presentation;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.service.model.SendResult;
import com.mepsan.marwiz.service.mposinformation.business.IMposInformationService;
import com.mepsan.marwiz.service.synchronize.presentation.SynchronizeBean;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author emrullah.yakisan
 */
@ManagedBean
@RequestScope
public class MposInformationBean {

    @ManagedProperty(value = "#{mposInformationService}")
    public IMposInformationService mposInformationService;

    public void setMposInformationService(IMposInformationService mposInformationService) {
        this.mposInformationService = mposInformationService;
    }

    private Gson gson;
    private SendResult result;
    private String bodyData;

   
    private String wsUsername;
    private String wsPassword;
    private String wsAddress;
    private String licenceCode;
    private String log;

    @PostConstruct
    public void init() {
        gson = new Gson();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        //Post ile istek gelmemiş ise hata mesajı döndürür
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                bodyData = CharStreams.toString(request.getReader());
            } catch (IOException ex) {
                result = new SendResult(false, 101, "could not read data in body.");
                Logger.getLogger(MposInformationBean.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            result = new SendResult(false, 102, "wrong request type");
            return;
        }

       

        if (!checkBodyData()) {
            result = new SendResult(false, 104, "wrong body data");
            return;
        }

       
        //Herhangi bir hata yok ise web servis tetiklenir.
        String resultMessage = sendInformation();
        result = new SendResult(true);
        result.setResultmessage(resultMessage);

    }

    /**
     * Stawiz' e aktarılacak Json Dataların çözülüp değişkenlere set edilmesini
     * sağlar.
     *
     * @return
     */
    public boolean checkBodyData() {
        boolean result = false;
        try {
            JsonObject fromJson = gson.fromJson(bodyData, JsonObject.class);
            wsUsername = fromJson.get("username").getAsString();
            wsPassword = fromJson.get("password").getAsString();
            wsAddress = fromJson.get("wsaddress").getAsString();
            licenceCode = fromJson.get("licenceCode").getAsString();
            JsonObject jsonObject = fromJson.get("log").getAsJsonObject();
            log = jsonObject.toString();
            result = true;
        } catch (Exception ex) {
            Logger.getLogger(SynchronizeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Sayfaya yapılan isteğin sonucunun döndürülmesini sağlar
     *
     * @return
     */
    public String printResult() {
        String toJson = gson.toJson(result);
        return toJson;
    }

   
    /**
     * Stawiz' e Mpos bilgilerinin aktarılmasını sağlar
     *
     * @return
     */
    public String sendInformation() {
        return mposInformationService.sendInformationLog(log, wsUsername, wsPassword, licenceCode, wsAddress);
    }

}
