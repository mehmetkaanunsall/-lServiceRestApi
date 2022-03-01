/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.07.2019 02:26:37
 */
package com.mepsan.marwiz.service.waste.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendWaste;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.waste.dao.ISendWasteDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

public class SendWasteService implements ISendWasteService {

    @Autowired
    ISendWasteDao sendWasteDao;

    public void setSendWasteDao(ISendWasteDao sendWasteDao) {
        this.sendWasteDao = sendWasteDao;
    }

    @Override
    public void sendWaste(SendWaste sendWaste) {
        BranchSetting branchSetting = sendWaste.getBranchSetting();
        String res = null;
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:GetWasteStock xmlns:ns2=\"http://ws/\">\n"
                      + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                      + "            <WasteStock><![CDATA[" + sendWaste.getSendData()+ "]]></WasteStock>\n"
                      + "        </ns2:GetWasteStock>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new Gson();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                sendWaste.setIsSend(true);
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();

        }
        sendWaste.setResponse(res);
        updateSendWasteResult(sendWaste);
    }

    @Override
    public void sendWasteAsync() {
        List<SendWaste> sendWastes = sendWasteDao.findNotSendedAll();
        if (!sendWastes.isEmpty()) {
            executeSendWaste(sendWastes);
        }
    }

    @Override
    public void executeSendWaste(List<SendWaste> sendWastes) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendWaste sendSale : sendWastes) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendWaste(sendSale);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    @Override
    public int updateSendWasteResult(SendWaste sendWaste) {
        return sendWasteDao.updateSendWasteResult(sendWaste);
    }

}
