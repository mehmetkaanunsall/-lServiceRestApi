/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:43:52
 */
package com.mepsan.marwiz.service.price.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequestCheck;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.service.price.dao.ISendPriceChangeRequestDao;

public class SendPriceChangeRequestService implements ISendPriceChangeRequestService {

    @Autowired
    ISendPriceChangeRequestDao sendPriceChangeRequestDao;

    @Override
    public void sendPriceChangeRequest(int priceChangeRequestId) {
        SendPriceChangeRequest sendPriceChangeRequest = sendPriceChangeRequestDao.findByIdPriceChangeRequestId(priceChangeRequestId);
        this.sendPriceChangeRequestAsync(sendPriceChangeRequest);
    }

    @Override
    public void sendPriceChangeRequest(SendPriceChangeRequest sendPriceChangeRequest) {
        BranchSetting branchSetting = sendPriceChangeRequest.getBranchSetting();
        String res = null;
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetPriceChangeRequest xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <priceChangeRequest><![CDATA[" + sendPriceChangeRequest.getSenddata() + "]]></priceChangeRequest>\n"
                    + "        </ns2:GetPriceChangeRequest>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new Gson();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                sendPriceChangeRequest.setIsSend(true);
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }
        sendPriceChangeRequest.setResponse(res);
        sendPriceChangeRequestDao.updatePriceChangeRequestResult(sendPriceChangeRequest);
    }

    @Override
    public void sendPriceChangeRequestAsync(SendPriceChangeRequest sendPriceChangeRequest) {
        ArrayList<SendPriceChangeRequest> sendPriceChangeRequests = new ArrayList<>();
        sendPriceChangeRequests.add(sendPriceChangeRequest);
        executeSendPriceChangeRequest(sendPriceChangeRequests);
    }

    @Override
    public void sendNotSendedPriceChangeRequestAsync() {
        List<SendPriceChangeRequest> sendPriceChangeRequests = sendPriceChangeRequestDao.findNotSendedAll();
        if (!sendPriceChangeRequests.isEmpty()) {
            executeSendPriceChangeRequest(sendPriceChangeRequests);
        }

    }

    private void executeSendPriceChangeRequest(List<SendPriceChangeRequest> sendPriceChangeRequests) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendPriceChangeRequest sendStockRequest : sendPriceChangeRequests) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendPriceChangeRequest(sendStockRequest);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    private void executeCheckPriceChangeRequest(List<SendPriceChangeRequestCheck> sendPriceChangeRequestChecks) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendPriceChangeRequestCheck requestCheck : sendPriceChangeRequestChecks) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    checkPriceChangeRequest(requestCheck);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    @Override
    public void checkPriceChangeRequestAsync() {
        List<SendPriceChangeRequestCheck> notApprovedList = sendPriceChangeRequestDao.findNotAprovedAll();

        if (!notApprovedList.isEmpty()) {
            executeCheckPriceChangeRequest(notApprovedList);
        }
    }

    @Override
    public void checkPriceChangeRequest(SendPriceChangeRequestCheck sendPriceRequestCheck) {
        BranchSetting branchSetting = sendPriceRequestCheck.getBranchSetting();
        String res = null;
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:CheckPriceChangeRequest xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <priceChangeRequest><![CDATA[" + sendPriceRequestCheck.getPriceChangeRequestIds() + "]]></priceChangeRequest>\n"
                    + "        </ns2:CheckPriceChangeRequest>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            StockPriceRequest stockPriceRequest = new StockPriceRequest();
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean;
            resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                for (JsonElement jElement : resJson.get("ResponsePriceChangeRequest").getAsJsonArray()) {
                    Date date;

                    stockPriceRequest.setId(jElement.getAsJsonObject().get("pricechangerequest_id").getAsInt());
                    String dateString = jElement.getAsJsonObject().get("approvaldate").getAsString();

                    if (dateString == null || dateString.equals("")) {
                        date = new Date();
                    } else {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = format.parse(dateString);
                    }

                    stockPriceRequest.setApproval(jElement.getAsJsonObject().get("approval").getAsInt());

                    stockPriceRequest.setApprovalDate(date);

                    stockPriceRequest.setDescription(jElement.getAsJsonObject().get("approvaldescription").getAsString());

                }
            }

            sendPriceChangeRequestDao.updatePriceChangeRequest(stockPriceRequest);

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }

        String[] requestids = sendPriceRequestCheck.getPriceChangeRequestIds().split(",");
        for (String requestidString : requestids) {
            try {
                int requestid = Integer.parseInt(requestidString);
                sendPriceChangeRequestDao.updatePriceChangeRequestCheckResponse(requestid, res);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
