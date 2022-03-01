/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:43:52
 */
package com.mepsan.marwiz.service.stock.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequestCheck;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.service.stock.dao.ISendStockRequestDao;

public class SendStockRequestService implements ISendStockRequestService {

    @Autowired
    ISendStockRequestDao sendStockRequestDao;

    @Override
    public void sendStockRequest(int stockRequestId) {
        SendStockRequest sendStockRequest = sendStockRequestDao.findByIdStockRequestId(stockRequestId);
        this.sendStockRequestAsync(sendStockRequest);
    }

    @Override
    public void sendStockRequest(SendStockRequest sendStockRequest) {
        BranchSetting branchSetting = sendStockRequest.getBranchSetting();
        String res = null;
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:GetStockRequest xmlns:ns2=\"http://ws/\">\n"
                      + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                      + "            <stockRequest><![CDATA[" + sendStockRequest.getSenddata() + "]]></stockRequest>\n"
                      + "        </ns2:GetStockRequest>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new Gson();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                sendStockRequest.setIsSend(true);
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }
        sendStockRequest.setResponse(res);
        sendStockRequestDao.updateStockRequestResult(sendStockRequest);
    }

    @Override
    public void sendStockRequestAsync(SendStockRequest sendStockRequest) {
        ArrayList<SendStockRequest> sendStockRequests = new ArrayList<>();
        sendStockRequests.add(sendStockRequest);
        executeSendStockRequest(sendStockRequests);
    }

    @Override
    public void sendNotSendedStockRequestAsync() {
        List<SendStockRequest> sendStockRequests = sendStockRequestDao.findNotSendedAll();
        if (!sendStockRequests.isEmpty()) {
            executeSendStockRequest(sendStockRequests);
        }

    }

    private void executeSendStockRequest(List<SendStockRequest> sendStockRequests) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendStockRequest sendStockRequest : sendStockRequests) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendStockRequest(sendStockRequest);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    private void executeCheckStockRequest(List<SendStockRequestCheck> requestChecks) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (SendStockRequestCheck requestCheck : requestChecks) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    checkStockRequest(requestCheck);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    @Override
    public void checkStockRequestAsync() {
        List<SendStockRequestCheck> notApprovedList = sendStockRequestDao.findNotAprovedAll();

        if (!notApprovedList.isEmpty()) {
            executeCheckStockRequest(notApprovedList);
        }
    }

    @Override
    public void checkStockRequest(SendStockRequestCheck requestCheck) {
        BranchSetting branchSetting = requestCheck.getBranchSetting();
        String res = null;
        StockRequest stockRequest = new StockRequest();
        try {
            WebServiceClient webServiceClient = new WebServiceClient();
            String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:CheckStockRequest xmlns:ns2=\"http://ws/\">\n"
                      + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                      + "            <stockrequest><![CDATA[" + requestCheck.getStockRequestIds() + "]]></stockrequest>\n"
                      + "        </ns2:CheckStockRequest>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean;
            resBoolean = resJson.get("Result").getAsBoolean();

            if (resBoolean) {
                for (JsonElement jElement : resJson.get("ResponseStockRequest").getAsJsonArray()) {
                    Date date;

                    stockRequest.setId(jElement.getAsJsonObject().get("stockrequest_id").getAsInt());
                    String dateString = jElement.getAsJsonObject().get("approvaldate").getAsString();

                    if (dateString == null || dateString.equals("")) {
                        date = new Date();
                    } else {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = format.parse(dateString);
                    }

                    stockRequest.setApproval(jElement.getAsJsonObject().get("approval").getAsInt());

                    stockRequest.setApprovalDate(date);

                    stockRequest.setDescription(jElement.getAsJsonObject().get("approvaldescription").getAsString());
                    stockRequest.setApprovalCenterStockId(jElement.getAsJsonObject().get("approvalstock_id").getAsInt());
                    sendStockRequestDao.updateStockRequest(stockRequest);
                }
            }

        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }

        String[] requestids = requestCheck.getStockRequestIds().split(",");
        for (String requestidString : requestids) {
            try {
                int requestid = Integer.parseInt(requestidString);
                sendStockRequestDao.updateStockRequestCheckResponse(requestid, res);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public SendStockRequest findByIdStockRequestId(int stockRequestId) {
        return sendStockRequestDao.findByIdStockRequestId(stockRequestId);
    }

    @Override
    public List<SendStockRequestCheck> findStockRequest(StockRequest stockRequest) {
        return sendStockRequestDao.findStockRequest(stockRequest);
    }


}
