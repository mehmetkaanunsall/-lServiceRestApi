/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 03.07.2018 08:57:44
 */
package com.mepsan.marwiz.service.purchace.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendPurchase;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.purchace.dao.ISendPurchaseDao;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

public class SendPurchaseService implements ISendPurchaseService {

    @Autowired
    ISendPurchaseDao sendPurchaseDao;

    @Override
    public SendPurchase findByInvoiceId(int invoiceId) {
        return sendPurchaseDao.findByInvoiceId(invoiceId);
    }

    @Override
    public int updateSendPurchaseResult(SendPurchase sendPurchase) {
        return sendPurchaseDao.updateSendPurchaseResult(sendPurchase);
    }

    @Override
    public void sendPurchaseToCenter(SendPurchase sendPurchase) {
        BranchSetting branchSetting = sendPurchase.getBranchSetting();
        String res = null;
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetPurchase xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + sendPurchase.getLicenceCode() + "]]></station>\n"
                    + "            <purchase><![CDATA[" + sendPurchase.getSenddata() + "]]></purchase>\n"
                    + "        </ns2:GetPurchase>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new Gson();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                sendPurchase.setIssend(true);
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }
        sendPurchase.setResponse(res);
        updateSendPurchaseResult(sendPurchase);
    }

    @Override
    public void sendPurchaseToCenterAsync(SendPurchase sendPurchase) {
        ArrayList<SendPurchase> sendPurchases = new ArrayList<SendPurchase>();
        sendPurchases.add(sendPurchase);
        executeSendPurchaseToCenter(sendPurchases);
    }

    @Override
    public void sendPurchaseNotSendedToCenterAsync() {
        List<SendPurchase> sendPurchases = sendPurchaseDao.findNotSendedAll();
        if (!sendPurchases.isEmpty()) {
            executeSendPurchaseToCenter(sendPurchases);
        }
    }

    private void executeSendPurchaseToCenter(List<SendPurchase> sendPurchases) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendPurchase sendPurchase : sendPurchases) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendPurchaseToCenter(sendPurchase);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

}
