/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.09.2018 12:14:03
 */
package com.mepsan.marwiz.service.stock.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendStockInfo;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.stock.dao.ISendStockInfoDao;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

public class SendStockInfoService implements ISendStockInfoService {

    @Autowired
    ISendStockInfoDao sendStockInfoDao;

    @Override
    public void sendStockInfo(SendStockInfo sendStockInfo) {
        BranchSetting branchSetting = sendStockInfo.getBranchSetting();
        sendStockInfo.setSendBeginDate(new Date());
        sendStockInfo.setSendCount(1);
        String res = null;
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetStockInfo xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <stockInfo><![CDATA[" + sendStockInfo.getSenddata() + "]]></stockInfo>\n"
                    + "        </ns2:GetStockInfo>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            
            Gson gson = new Gson();
            try {
                JsonObject resJson = gson.fromJson(res, JsonObject.class);
                boolean resBoolean = resJson.get("Result").getAsBoolean();
                if (resBoolean) {
                    sendStockInfo.setIsSend(true);
                    sendStockInfo.setSendEndDate(new Date());
                }
            } catch (Exception e) {
                
            }
        } catch (Exception ex) {
            res = res+"---Error:"+ex.getMessage();
        }
        sendStockInfo.setResponse(res);
        sendStockInfoDao.insertResult(sendStockInfo);
    }

    @Override
    public void sendStockInfoAsync() {
        List<SendStockInfo> sendStockInfos = sendStockInfoDao.getStockInfoData();
        executeSendStockInfo(sendStockInfos);
    }

    @Override
    public void executeSendStockInfo(List<SendStockInfo> sendStockInfos) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendStockInfo sendStockInfo : sendStockInfos) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendStockInfo(sendStockInfo);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

}
