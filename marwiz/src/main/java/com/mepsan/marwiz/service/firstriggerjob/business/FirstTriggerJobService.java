/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.07.2021 01:32:49
 */
package com.mepsan.marwiz.service.firstriggerjob.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.firstriggerjob.dao.IFirstTriggerJobDao;
import org.springframework.beans.factory.annotation.Autowired;

public class FirstTriggerJobService implements IFirstTriggerJobService {

    @Autowired
    private IFirstTriggerJobDao firstTriggerJobDao;

    public void setFirstTriggerJobDao(IFirstTriggerJobDao firstTriggerJobDao) {
        this.firstTriggerJobDao = firstTriggerJobDao;
    }

    @Override
    public void callFirstTriggerJob() {
        String res = null;

        BranchSetting branchSetting = firstTriggerJobDao.findTopCentralIntegratedBranchSetting();
        String jsonData = "";
        boolean resBoolean = false;
        jsonData = firstTriggerJobDao.callFirstTriggerJob();
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                      = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:FirstTriggerJob xmlns:ns2=\"http://ws/\">\n"
                      + "            <json><![CDATA[" + jsonData + "]]></json>\n"
                      + "        </ns2:FirstTriggerJob>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";

            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            resBoolean = resJson.get("Result").getAsBoolean();

        } catch (Exception ex) {
            resBoolean = false;
        }

        if (resBoolean == true) {
            firstTriggerJobDao.updateSystemParameters();
        }
    }

}
