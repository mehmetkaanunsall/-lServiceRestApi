/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 18:02:40
 */
package com.mepsan.marwiz.service.sale.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.login.dao.ILoginDao;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendSale;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.service.sale.dao.ISendSaleDao;

public class SendSaleService implements ISendSaleService {

    @Autowired
    ISendSaleDao sendSaleDao;

    @Autowired
    ILoginDao loginDao;

    @Override
    public SendSale findBySaleId(int saleId) {
        return sendSaleDao.findBySaleId(saleId);
    }

    @Override
    public int updateSendSaleResult(SendSale sendSale) {
        return sendSaleDao.updateSendSaleResult(sendSale);
    }

    @Override
    public boolean checkUser(String username, String password) {
        return loginDao.checkUser(username, password);
    }

    @Override
    public void sendSaleToCenter(SendSale sendSale) {
        BranchSetting branchSetting = sendSale.getBranchSetting();
        String res = null;
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetMarketSale xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + sendSale.getLicenceCode() + "]]></station>\n"
                    + "            <sale><![CDATA[" + sendSale.getSenddata() + "]]></sale>\n"
                    + "        </ns2:GetMarketSale>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new Gson();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                sendSale.setIssend(true);
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
           
        }
        sendSale.setResponse(res);
        updateSendSaleResult(sendSale);
    }

    @Override
    public void sendSaleToCenterAsync(SendSale sendSale) {
        ArrayList<SendSale> sendSales = new ArrayList<SendSale>();
        sendSales.add(sendSale);
        executeSendSaleToCenter(sendSales);
    }

    @Override
    public void sendSaleNotSendedToCenterAsync() {
        List<SendSale> sendSales = sendSaleDao.findNotSendedAll();
        if (!sendSales.isEmpty()) {
            executeSendSaleToCenter(sendSales);
        }
    }

    private void executeSendSaleToCenter(List<SendSale> sendSales) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendSale sendSale : sendSales) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendSaleToCenter(sendSale);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

}
