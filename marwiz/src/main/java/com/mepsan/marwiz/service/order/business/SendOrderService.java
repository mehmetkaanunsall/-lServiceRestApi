/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendOrder;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.order.dao.ISendOrderDao;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class SendOrderService implements ISendOrderService{

    @Autowired
    ISendOrderDao sendOrderDao;
    
    @Override
    public SendOrder findByOrderId(int orderId) {
        return sendOrderDao.findByOrderId(orderId);
    }

    @Override
    public int updateSendOrderResult(SendOrder sendOrder) {
      return sendOrderDao.updateSendOrderResult(sendOrder);
    }

    @Override
    public void sendOrderToCenter(SendOrder sendOrder) {
    BranchSetting branchSetting = sendOrder.getBranchSetting();
        String res = null;
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetOrder xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + sendOrder.getLicenceCode() + "]]></station>\n"
                    + "            <order><![CDATA[" + sendOrder.getSenddata() + "]]></order>\n"
                    + "        </ns2:GetOrder>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            System.out.println("SendOrder res"+res.toString());
            Gson gson = new Gson();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                sendOrder.setIssend(true);
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }
        sendOrder.setResponse(res);
        updateSendOrderResult(sendOrder);
    }

    @Override
    public void sendOrderToCenterAsync(SendOrder sendOrder) {
        ArrayList<SendOrder> sendOrders = new ArrayList<SendOrder>();
        sendOrders.add(sendOrder);
        executeSendOrderToCenter(sendOrders);    }

    @Override
    public void sendOrderNotSendedToCenterAsync() {
        List<SendOrder> sendOrders = sendOrderDao.findNotSendedAll();
        if (!sendOrders.isEmpty()) {
            executeSendOrderToCenter(sendOrders);
        }
    }
    
    private void executeSendOrderToCenter(List<SendOrder> sendOrders) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendOrder sendOrder : sendOrders) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendOrderToCenter(sendOrder);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }
    
}
