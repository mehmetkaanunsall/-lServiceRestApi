/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapintegration.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.sapintegration.dao.IIntegrationForSapDao;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSap;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSapResponseDetail;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Text;

/**
 *
 * @author elif.mart
 */
public class IntegrationForSapService implements IIntegrationForSapService {

    @Autowired
    private IIntegrationForSapDao integrationForSapDao;

    @Autowired
    private SessionBean sessionBean;

    public IIntegrationForSapDao getIntegrationForSapDao() {
        return integrationForSapDao;
    }

    public void setIntegrationForSapDao(IIntegrationForSapDao integrationForSapDao) {
        this.integrationForSapDao = integrationForSapDao;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<IntegrationForSap> listOfWarehouseReceipt(Date beginDate, Date endDate, Boolean send, BranchSetting selectedBranch) {
        List<IntegrationForSap> returnList = new ArrayList<>();
        returnList = integrationForSapDao.listOfWarehouseReceipt(beginDate, endDate, send, selectedBranch);
        returnList = setMessage(returnList, 1);
        return returnList;
    }

    @Override
    public List<IntegrationForSap> listOfPurchaseInvoices(Date beginDate, Date endDate, int purchaseInvoiceType, BranchSetting selectedBranch) {
        List<IntegrationForSap> returnList = new ArrayList<>();
        returnList = integrationForSapDao.listOfPurchaseInvoices(beginDate, endDate, purchaseInvoiceType, selectedBranch);
        returnList = setMessage(returnList, 2);
        return returnList;
    }

    @Override
    public List<IntegrationForSap> listOfSaleInvoices(Date beginDate, Date endDate, Boolean isRetail, BranchSetting selectedBranch) {
        List<IntegrationForSap> returnList = new ArrayList<>();
        returnList = integrationForSapDao.listOfSaleInvoices(beginDate, endDate, isRetail, selectedBranch);
        returnList = setMessage(returnList, 3);
        return returnList;
    }

    public List<IntegrationForSap> setMessage(List<IntegrationForSap> listOfSap, int processType) {
        String message = "";
        String message1 = "";
        String message2 = "";
        String message3 = "";
        String message4 = "";
        String docNumber = "";
        String sapIDocNo = "";

        char[] beginChar = {'\u3010'};
        String begin = "[";
        String boldBegin = begin
                // Mathematical Sans-Serif 
                .replace("[", String.valueOf(beginChar));

        char[] endChar = {'\u3011'};
        String end = "]";
        String boldEnd = end
                // Mathematical Sans-Serif 
                .replace("]", String.valueOf(endChar));

        for (IntegrationForSap sap : listOfSap) {
            message = "";
            message1 = "";
            message2 = "";
            message3 = "";
            message4 = "";
            docNumber = "";
            sapIDocNo = "";
            if (sap.getMessage() != null && !sap.getMessage().equals("") && !sap.getMessage().isEmpty()) {
                if (sap.getMessage().charAt(0) != '[' && sap.getMessage().charAt(0) != '{') {
                    sap.setMessage(sap.getMessage());
                } else {

                    IntegrationForSapResponseDetail responseDetail = new IntegrationForSapResponseDetail();
                    List<IntegrationForSapResponseDetail> listOfResponseDetail = new ArrayList<>();

                    JSONObject resJson = new JSONObject(sap.getMessage());
                    JSONObject resJsonReturn = new JSONObject(resJson.getJSONObject("ET_RETURN").toString());
                    if (resJsonReturn.get("item").toString().charAt(0) == '{') { //Response bazen json array bazen de json object olduğu için ilk karakter kontrolü yapıldı
                        JSONObject resJsonItem = new JSONObject(resJsonReturn.getJSONObject("item").toString());

                        if (resJsonItem.getString("TYPE").equalsIgnoreCase("S")) {
                            responseDetail.setIsSend(true);
                        } else if (resJsonItem.getString("TYPE").equalsIgnoreCase("E")) {
                            responseDetail.setIsSend(false);
                        }

                        message = resJsonItem.getString("MESSAGE");
                        message1 = resJsonItem.get("MESSAGE_V1").toString();
                        message2 = resJsonItem.get("MESSAGE_V2").toString();
                        message3 = resJsonItem.get("MESSAGE_V3").toString();
                        message4 = resJsonItem.get("MESSAGE_V4").toString();

                        if (message != null && !message.isEmpty() && !message.equals("")) {
                            char c;
                            int count = 0;
                            for (int j = 0; j < message.length(); j++) {
                                c = message.charAt(j);
                                if (c == '&') {
                                    count++;
                                }
                            }
                            if (count > 0) {

                                int index = 0;
                                for (int i = 0; i < message.length(); i++) {
                                    c = message.charAt(i);
                                    if (c == '&') {
                                        if (index == 0) {
                                            if (i == 0) {
                                                if (!message1.equals("")) {
                                                    message = message.substring(0, 0) + " (" + message1 + ") " + message.substring(i + 1, message.length());
                                                }
                                            } else {
                                                if (!message1.equals("")) {
                                                    message = message.substring(0, i - 1) + " (" + message1 + ") " + message.substring(i + 1, message.length());
                                                }
                                            }
                                        } else if (index == 1) {
                                            if (!message2.equals("")) {
                                                message = message.substring(0, i - 1) + " (" + message2 + ") " + message.substring(i + 1, message.length());
                                            }
                                        } else if (index == 2) {
                                            if (!message3.equals("")) {
                                                message = message.substring(0, i - 1) + " (" + message3 + ") " + message.substring(i + 1, message.length());
                                            }
                                        } else if (index == 3) {
                                            if (!message4.equals("")) {
                                                message = message.substring(0, i - 1) + " (" + message4 + ") " + message.substring(i + 1, message.length());
                                            }
                                        }
                                        index++;
                                    }
                                }
                            }
                        }
                        responseDetail.setMessage(message);
                        sap.setMessage(message);

                    } else if (resJsonReturn.get("item").toString().charAt(0) == '[') {
                        JSONArray jsonArrItem = new JSONArray(resJsonReturn.getJSONArray("item").toString());
                        boolean isSuccess = true;
                        String responseMessage = "";
                        for (int j = 0; j < jsonArrItem.length(); j++) {

                            IntegrationForSapResponseDetail responseDetailArray = new IntegrationForSapResponseDetail();
                            responseMessage = "";
                            if (!jsonArrItem.getJSONObject(j).isNull("TYPE")) {
                                if (jsonArrItem.getJSONObject(j).getString("TYPE").equalsIgnoreCase("E")) {
                                    responseMessage = jsonArrItem.getJSONObject(j).getString("MESSAGE");
                                    responseDetailArray.setIsSend(false);
                                } else if (jsonArrItem.getJSONObject(j).getString("TYPE").equalsIgnoreCase("S")) {
                                    responseMessage = jsonArrItem.getJSONObject(j).getString("MESSAGE");
                                    responseDetailArray.setIsSend(true);
                                }
                            }

                            message1 = jsonArrItem.getJSONObject(j).get("MESSAGE_V1").toString();
                            message2 = jsonArrItem.getJSONObject(j).get("MESSAGE_V2").toString();
                            message3 = jsonArrItem.getJSONObject(j).get("MESSAGE_V3").toString();
                            message4 = jsonArrItem.getJSONObject(j).get("MESSAGE_V4").toString();

                            if (responseMessage != null && !responseMessage.isEmpty() && !responseMessage.equals("")) {
                                char c;
                                int count = 0;
                                for (int i = 0; i < responseMessage.length(); i++) {
                                    c = responseMessage.charAt(i);
                                    if (c == '&') {
                                        count++;
                                    }
                                }
                                if (count > 0) {
                                    int index = 0;
                                    for (int i = 0; i < responseMessage.length(); i++) {
                                        c = responseMessage.charAt(i);
                                        if (c == '&') {
                                            if (index == 0) {
                                                if (i == 0) {
                                                    if (!message1.equals("")) {
                                                        responseMessage = responseMessage.substring(0, 0) + " (" + message1 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                    }
                                                } else {
                                                    if (!message1.equals("")) {
                                                        responseMessage = responseMessage.substring(0, i - 1) + " (" + message1 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                    }
                                                }
                                            } else if (index == 1) {
                                                if (!message2.equals("")) {
                                                    responseMessage = responseMessage.substring(0, i - 1) + " (" + message2 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                }
                                            } else if (index == 2) {
                                                if (!message3.equals("")) {
                                                    responseMessage = responseMessage.substring(0, i - 1) + " (" + message3 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                }
                                            } else if (index == 3) {
                                                if (!message4.equals("")) {
                                                    responseMessage = responseMessage.substring(0, i - 1) + " (" + message4 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                }
                                            }
                                            index++;
                                        }
                                    }

                                }

                            }

                            responseDetailArray.setMessage(responseMessage);
                            responseDetailArray.setId(j);

                            if (!responseMessage.equals("")) {
                                if (jsonArrItem.length() > 1) {
                                    message = message + " - " + boldBegin + responseMessage + boldEnd;
                                } else {
                                    message = message + " - " + responseMessage;
                                }

                            }
                            listOfResponseDetail.add(responseDetailArray);

                        }

                        if (!message.equals("")) {
                            sap.setMessage(message.substring(2, message.length()));
                        }

                    }

                    JSONObject resJsonDocNum = new JSONObject(resJson.getJSONObject("ET_DOCNUM").toString());

                    if (resJsonDocNum.get("item").toString().charAt(0) == '{') { //Response bazen json array bazen de json object olduğu için ilk karakter kontrolü yapıldı
                        JSONObject resJsonItem = new JSONObject(resJsonDocNum.getJSONObject("item").toString());
                        if (processType == 1) {

                            if (!resJsonItem.isNull("MBLNR")) {
                                docNumber = docNumber + "," + " MBLNR : " + resJsonItem.get("MBLNR");
                            }

                            if (!resJsonItem.isNull("MJAHR")) {
                                docNumber = docNumber + "," + " MJAHR : " + resJsonItem.get("MJAHR");
                            }

                        } else if (processType == 3) {
                            if (!resJsonItem.isNull("VBELNVA")) {

                                if (!resJsonItem.getString("VBELNVA").equals("")) {
                                    docNumber = docNumber + "," + " VBELNVA : " + resJsonItem.getString("VBELNVA");
                                }
                            }
                            if (!resJsonItem.isNull("VBELNVL")) {

                                if (!resJsonItem.getString("VBELNVL").equals("")) {
                                    docNumber = docNumber + "," + " VBELNVL : " + resJsonItem.getString("VBELNVL");
                                }
                            }

                            if (!resJsonItem.isNull("VBELNVF")) {

                                if (!resJsonItem.getString("VBELNVF").equals("")) {
                                    docNumber = docNumber + "," + " VBELNVF : " + resJsonItem.getString("VBELNVF");
                                }
                            }

                        } else if (processType == 2) {
                            if (!resJsonItem.isNull("MBLNR")) {

                                docNumber = docNumber + "," + " MBLNR : " + resJsonItem.get("MBLNR");

                            }

                            if (!resJsonItem.isNull("MJAHR")) {

                                docNumber = docNumber + "," + " MJAHR : " + resJsonItem.get("MJAHR");
                            }

                            if (!resJsonItem.isNull("BELNR")) {

                                docNumber = docNumber + "," + " BELNR : " + resJsonItem.get("BELNR");
                            }

                            if (!resJsonItem.isNull("GJAHR")) {

                                docNumber = docNumber + "," + " GJAHR : " + resJsonItem.get("GJAHR");

                            }
                        }

                        if (!docNumber.equals("")) {
                            responseDetail.setSapDocumentNumber(docNumber.substring(1, docNumber.length()));
                        }

                        sapIDocNo = resJsonItem.getString("DOCNUM");
                        responseDetail.setSapIDocNo(sapIDocNo);

                    } else if (resJsonDocNum.get("item").toString().charAt(0) == '[') {

                        JSONArray jsonArrItem = new JSONArray(resJsonDocNum.getJSONArray("item").toString());

                        for (int j = 0; j < jsonArrItem.length(); j++) {
                            String documentNumber = "";
                            if (processType == 1) {

                                if (!jsonArrItem.getJSONObject(j).isNull("MBLNR")) {
                                    documentNumber = documentNumber + "," + " MBLNR : " + jsonArrItem.getJSONObject(j).get("MBLNR");
                                }
                                if (!jsonArrItem.getJSONObject(j).isNull("MJAHR")) {
                                    documentNumber = documentNumber + "," + " MJAHR : " + jsonArrItem.getJSONObject(j).get("MJAHR");
                                }
                            } else if (processType == 3) {

                                if (!jsonArrItem.getJSONObject(j).isNull("VBELNVA")) {

                                    if (!jsonArrItem.getJSONObject(j).getString("VBELNVA").equals("")) {
                                        documentNumber = documentNumber + "," + " VBELNVA : " + jsonArrItem.getJSONObject(j).getString("VBELNVA");
                                    }
                                }
                                if (!jsonArrItem.getJSONObject(j).isNull("VBELNVL")) {

                                    if (!jsonArrItem.getJSONObject(j).getString("VBELNVL").equals("")) {
                                        documentNumber = documentNumber + "," + " VBELNVL : " + jsonArrItem.getJSONObject(j).getString("VBELNVL");
                                    }
                                }

                                if (!jsonArrItem.getJSONObject(j).isNull("VBELNVF")) {

                                    if (!jsonArrItem.getJSONObject(j).getString("VBELNVF").equals("")) {
                                        documentNumber = documentNumber + "," + " VBELNVF : " + jsonArrItem.getJSONObject(j).getString("VBELNVF");
                                    }
                                }

                            } else if (processType == 2) {

                                if (!jsonArrItem.getJSONObject(j).isNull("MBLNR")) {

                                    documentNumber = documentNumber + "," + " MBLNR : " + jsonArrItem.getJSONObject(j).get("MBLNR");

                                }

                                if (!jsonArrItem.getJSONObject(j).isNull("MJAHR")) {

                                    documentNumber = documentNumber + "," + " MJAHR : " + jsonArrItem.getJSONObject(j).get("MJAHR");

                                }

                                if (!jsonArrItem.getJSONObject(j).isNull("BELNR")) {

                                    documentNumber = documentNumber + "," + " BELNR : " + jsonArrItem.getJSONObject(j).get("BELNR");

                                }

                                if (!jsonArrItem.getJSONObject(j).isNull("GJAHR")) {

                                    documentNumber = documentNumber + "," + " GJAHR : " + jsonArrItem.getJSONObject(j).get("GJAHR");

                                }

                            }

                            if (!documentNumber.equals("")) {
                                listOfResponseDetail.get(j).setSapDocumentNumber(documentNumber.substring(1, documentNumber.length()));

                            }

                            if (!jsonArrItem.getJSONObject(j).isNull("DOCNUM")) {
                                if (!jsonArrItem.getJSONObject(j).getString("DOCNUM").equals("")) {
                                    listOfResponseDetail.get(j).setSapIDocNo(jsonArrItem.getJSONObject(j).getString("DOCNUM"));
                                }
                            }

                        }

                    }

                    if (listOfResponseDetail.size() > 0) {
                        sap.getListOfResponseDetails().addAll(listOfResponseDetail);
                    } else {
                        sap.getListOfResponseDetails().add(responseDetail);
                    }

                }

            }

        }

        return listOfSap;

    }

    @Override
    public void sendDataIntegration(List<IntegrationForSap> listOfSelectedSap, BranchSetting selectedBranch, int processType) {

        listOfSelectedSap.stream().map((sap) -> {
            sendDataIntegrationSap(sap, selectedBranch, processType);
            return sap;
        }).forEach((sap) -> {
            integrationForSapDao.update(sap, processType);//veritabanı güncelle
        });

    }

    /**
     * Bu metot gelen objeyi web servise gönderecek
     */
    private void sendDataIntegrationSap(IntegrationForSap sap, BranchSetting branchSetting, int processType) {
        String operation = "";
        boolean isSendWaybill = false;

        if (processType == 1) { //Depo Fişleri
            operation = "MalHareketleri";
        } else if (processType == 2) { //Satınalma
            operation = "SASYaratma";
        } else if (processType == 3) { //Satış faturası
            operation = "SiparisYaratma";
        }

        BranchSetting bs = branchSetting;
        String response = "";
        String message = "";
        String docNumber = "";
        String sapIDocNo = "";
        boolean isSend = false;

        String message1 = "";
        String message2 = "";
        String message3 = "";
        String message4 = "";

        if (bs.getErpIntegrationId() == 1) {//SAP ise
            String url = sessionBean.getUser().getLastBranchSetting().getErpUrl();

            try {

                String result = null;
                HttpPost httpPost = new HttpPost(url);
                try {

                    HttpClient httpClient = WebServiceClient.createHttpClient_AcceptsUntrustedCerts();
                    httpPost.addHeader("Operation", operation);

                    byte[] encodedAuth = Base64.getEncoder().encode((sessionBean.getUser().getLastBranchSetting().getErpUsername() + ":" + sessionBean.getUser().getLastBranchSetting().getErpPassword()).getBytes("UTF-8"));
                    String authHeader = "Basic " + new String(encodedAuth);
                    httpPost.addHeader("Authorization", authHeader);

                    httpPost.addHeader("Operation", "MepsanDailyReportGet");
                    RequestConfig rc = RequestConfig.DEFAULT;
                    RequestConfig requestConfig
                            = RequestConfig
                                    .copy(rc)
                                    .setSocketTimeout(bs.getErpTimeout() * 1000)
                                    .setConnectTimeout(bs.getErpTimeout() * 1000)
                                    .setConnectionRequestTimeout(bs.getErpTimeout() * 1000)
                                    .build();
                    httpPost.setConfig(requestConfig);
                    System.out.println("------SEND DATA******" + sap.getJsonData());

                    String json = sap.getJsonData();
                    StringEntity requestEntity = new StringEntity(
                            json,
                            ContentType.APPLICATION_JSON);
                    httpPost.setEntity(requestEntity);
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    int returnCode = httpResponse.getStatusLine().getStatusCode();
                    System.out.println("Return Code : " + returnCode);

                    if (returnCode == 200) {

                        char[] beginChar = {'\u3010'};
                        String begin = "[";
                        String boldBegin = begin
                                // Mathematical Sans-Serif 
                                .replace("[", String.valueOf(beginChar));

                        char[] endChar = {'\u3011'};
                        String end = "]";
                        String boldEnd = end
                                // Mathematical Sans-Serif 
                                .replace("]", String.valueOf(endChar));

                        result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

                        System.out.println("Result : " + result);
                        if (!result.equals("")) {
                            IntegrationForSapResponseDetail responseDetail = new IntegrationForSapResponseDetail();
                            List<IntegrationForSapResponseDetail> listOfResponseDetail = new ArrayList<>();
                            JSONObject resJsonResult = new JSONObject(result);
                            JSONObject resJsonReturn = new JSONObject(resJsonResult.getJSONObject("ET_RETURN").toString());

                            if (resJsonReturn.get("item").toString().charAt(0) == '{') { //Response bazen json array bazen de json object olduğu için ilk karakter kontrolü yapıldı
                                JSONObject resJsonItem = new JSONObject(resJsonReturn.getJSONObject("item").toString());

                                if (resJsonItem.getString("TYPE").equalsIgnoreCase("S")) {
                                    isSend = true;
                                    responseDetail.setIsSend(true);
                                } else if (resJsonItem.getString("TYPE").equalsIgnoreCase("E")) {
                                    isSend = false;
                                    responseDetail.setIsSend(false);
                                }
                                message = resJsonItem.getString("MESSAGE");
                                message1 = resJsonItem.get("MESSAGE_V1").toString();
                                message2 = resJsonItem.get("MESSAGE_V2").toString();
                                message3 = resJsonItem.get("MESSAGE_V3").toString();
                                message4 = resJsonItem.get("MESSAGE_V4").toString();

                                if (message != null && !message.isEmpty() && !message.equals("")) {
                                    char c;
                                    int count = 0;
                                    for (int j = 0; j < message.length(); j++) {
                                        c = message.charAt(j);
                                        if (c == '&') {
                                            count++;
                                        }
                                    }
                                    if (count > 0) {
                                        int index = 0;
                                        for (int i = 0; i < message.length(); i++) {
                                            c = message.charAt(i);
                                            if (c == '&') {
                                                if (index == 0) {
                                                    if (i == 0) {
                                                        if (!message1.equals("")) {
                                                            message = message.substring(0, 0) + " (" + message1 + ") " + message.substring(i + 1, message.length());
                                                        }
                                                    } else {
                                                        if (!message1.equals("")) {
                                                            message = message.substring(0, i - 1) + " (" + message1 + ") " + message.substring(i + 1, message.length());
                                                        }
                                                    }
                                                } else if (index == 1) {
                                                    if (!message2.equals("")) {
                                                        message = message.substring(0, i - 1) + " (" + message2 + ") " + message.substring(i + 1, message.length());
                                                    }
                                                } else if (index == 2) {
                                                    if (!message3.equals("")) {
                                                        message = message.substring(0, i - 1) + " (" + message3 + ") " + message.substring(i + 1, message.length());
                                                    }
                                                } else if (index == 3) {
                                                    if (!message4.equals("")) {
                                                        message = message.substring(0, i - 1) + " (" + message4 + ") " + message.substring(i + 1, message.length());
                                                    }
                                                }
                                                index++;
                                            }

                                        }
                                    }

                                }
                                responseDetail.setMessage(message);

                            } else if (resJsonReturn.get("item").toString().charAt(0) == '[') {
                                JSONArray jsonArrItem = new JSONArray(resJsonReturn.getJSONArray("item").toString());
                                boolean isSuccess = true;
                                int unsuccessfullcount = 0;
                                String responseMessage = "";
                                for (int j = 0; j < jsonArrItem.length(); j++) {
                                    IntegrationForSapResponseDetail responseDetailArray = new IntegrationForSapResponseDetail();
                                    responseMessage = "";
                                    if (!jsonArrItem.getJSONObject(j).isNull("TYPE")) {
                                        if (jsonArrItem.getJSONObject(j).getString("TYPE").equalsIgnoreCase("E")) {
                                            unsuccessfullcount++;
                                            responseMessage = jsonArrItem.getJSONObject(j).getString("MESSAGE");
                                            responseDetailArray.setIsSend(false);
                                        } else if (jsonArrItem.getJSONObject(j).getString("TYPE").equalsIgnoreCase("S")) {
                                            responseMessage = jsonArrItem.getJSONObject(j).getString("MESSAGE");
                                            responseDetailArray.setIsSend(true);
                                        }
                                    }
                                    message1 = jsonArrItem.getJSONObject(j).get("MESSAGE_V1").toString();
                                    message2 = jsonArrItem.getJSONObject(j).get("MESSAGE_V2").toString();
                                    message3 = jsonArrItem.getJSONObject(j).get("MESSAGE_V3").toString();
                                    message4 = jsonArrItem.getJSONObject(j).get("MESSAGE_V4").toString();
                                    if (responseMessage != null && !responseMessage.isEmpty() && !responseMessage.equals("")) {
                                        char c;
                                        int count = 0;
                                        for (int i = 0; i < responseMessage.length(); i++) {
                                            c = responseMessage.charAt(i);
                                            if (c == '&') {
                                                count++;
                                            }
                                        }
                                        if (count > 0) {
                                            int index = 0;
                                            for (int i = 0; i < responseMessage.length(); i++) {
                                                c = responseMessage.charAt(i);
                                                if (c == '&') {
                                                    if (index == 0) {
                                                        if (i == 0) {
                                                            if (!message1.equals("")) {
                                                                responseMessage = responseMessage.substring(0, 0) + " (" + message1 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                            }
                                                        } else {
                                                            if (!message1.equals("")) {
                                                                responseMessage = responseMessage.substring(0, i - 1) + " (" + message1 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                            }
                                                        }
                                                    } else if (index == 1) {
                                                        if (!message2.equals("")) {
                                                            responseMessage = responseMessage.substring(0, i - 1) + " (" + message2 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                        }
                                                    } else if (index == 2) {
                                                        if (!message3.equals("")) {
                                                            responseMessage = responseMessage.substring(0, i - 1) + " (" + message3 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                        }
                                                    } else if (index == 3) {
                                                        if (!message4.equals("")) {
                                                            responseMessage = responseMessage.substring(0, i - 1) + " (" + message4 + ") " + responseMessage.substring(i + 1, responseMessage.length());
                                                        }
                                                    }
                                                    index++;
                                                }
                                            }
                                        }
                                    }

                                    responseDetailArray.setMessage(responseMessage);
                                    responseDetailArray.setId(j);

                                    if (!responseMessage.equals("")) {
                                        if (jsonArrItem.length() > 1) {
                                            message = message + " - " + boldBegin + responseMessage + boldEnd;
                                        } else {
                                            message = message + " - " + responseMessage;
                                        }

                                    }

                                    listOfResponseDetail.add(responseDetailArray);

                                }

                                if (!message.equals("")) {
                                    message = message.substring(2, message.length());
                                }

                                if (unsuccessfullcount > 0) {
                                    isSend = false;
                                } else {
                                    isSend = true;
                                }
                            }

                            JSONObject resJsonDocNum = new JSONObject(resJsonResult.getJSONObject("ET_DOCNUM").toString());

                            if (resJsonDocNum.get("item").toString().charAt(0) == '{') { //Response bazen json array bazen de json object olduğu için ilk karakter kontrolü yapıldı
                                JSONObject resJsonItem = new JSONObject(resJsonDocNum.getJSONObject("item").toString());
                                if (processType == 1) {

                                    if (!resJsonItem.isNull("MBLNR")) {
                                        docNumber = docNumber + " ," + " MBLNR : " + resJsonItem.get("MBLNR");
                                    }

                                    if (!resJsonItem.isNull("MJAHR")) {
                                        docNumber = docNumber + " ," + " MJAHR : " + resJsonItem.get("MJAHR");
                                    }

                                } else if (processType == 3) {
                                    if (!resJsonItem.isNull("VBELNVA")) {

                                        if (!resJsonItem.getString("VBELNVA").equals("")) {
                                            docNumber = docNumber + " ," + " VBELNVA : " + resJsonItem.getString("VBELNVA");
                                        }
                                    }
                                    if (!resJsonItem.isNull("VBELNVL")) {

                                        if (!resJsonItem.getString("VBELNVL").equals("")) {
                                            docNumber = docNumber + " ," + " VBELNVL : " + resJsonItem.getString("VBELNVL");
                                        }
                                    }

                                    if (!resJsonItem.isNull("VBELNVF")) {

                                        if (!resJsonItem.getString("VBELNVF").equals("")) {
                                            docNumber = docNumber + " ," + " VBELNVF : " + resJsonItem.getString("VBELNVF");
                                        }
                                    }

                                } else if (processType == 2) {
                                    if (!resJsonItem.isNull("MBLNR")) {

                                        docNumber = docNumber + " ," + " MBLNR : " + resJsonItem.get("MBLNR");

                                        if (sap.getTypeId() == 59) {
                                            if (resJsonItem.get("MBLNR").toString().equals("") || resJsonItem.get("MBLNR").toString().equals("0000")) {
                                                isSendWaybill = false;
                                            } else {
                                                isSendWaybill = true;
                                            }
                                        }
                                    }

                                    if (!resJsonItem.isNull("MJAHR")) {

                                        docNumber = docNumber + " ," + " MJAHR : " + resJsonItem.get("MJAHR");
                                    }

                                    if (!resJsonItem.isNull("BELNR")) {

                                        docNumber = docNumber + " ," + " BELNR : " + resJsonItem.get("BELNR");
                                    }

                                    if (!resJsonItem.isNull("GJAHR")) {

                                        docNumber = docNumber + " ," + " GJAHR : " + resJsonItem.get("GJAHR");

                                    }
                                }

                                if (!docNumber.equals("")) {
                                    responseDetail.setSapDocumentNumber(docNumber.substring(1, docNumber.length()));
                                }

                                sapIDocNo = resJsonItem.getString("DOCNUM");
                                responseDetail.setSapIDocNo(sapIDocNo);

                            } else if (resJsonDocNum.get("item").toString().charAt(0) == '[') {

                                JSONArray jsonArrItem = new JSONArray(resJsonDocNum.getJSONArray("item").toString());
                                int isSendUnSuccessfullCount = 0;
                                for (int j = 0; j < jsonArrItem.length(); j++) {
                                    String documentNumber = "";
                                    if (processType == 1) {

                                        if (!jsonArrItem.getJSONObject(j).isNull("MBLNR")) {
                                            documentNumber = documentNumber + "," + " MBLNR : " + jsonArrItem.getJSONObject(j).get("MBLNR");
                                        }
                                        if (!jsonArrItem.getJSONObject(j).isNull("MJAHR")) {
                                            documentNumber = documentNumber + "," + " MJAHR : " + jsonArrItem.getJSONObject(j).get("MJAHR");
                                        }
                                    } else if (processType == 3) {

                                        if (!jsonArrItem.getJSONObject(j).isNull("VBELNVA")) {

                                            if (!jsonArrItem.getJSONObject(j).getString("VBELNVA").equals("")) {
                                                documentNumber = documentNumber + "," + " VBELNVA : " + jsonArrItem.getJSONObject(j).getString("VBELNVA");
                                            }
                                        }
                                        if (!jsonArrItem.getJSONObject(j).isNull("VBELNVL")) {

                                            if (!jsonArrItem.getJSONObject(j).getString("VBELNVL").equals("")) {
                                                documentNumber = documentNumber + "," + " VBELNVL : " + jsonArrItem.getJSONObject(j).getString("VBELNVL");
                                            }
                                        }

                                        if (!jsonArrItem.getJSONObject(j).isNull("VBELNVF")) {

                                            if (!jsonArrItem.getJSONObject(j).getString("VBELNVF").equals("")) {
                                                documentNumber = documentNumber + "," + " VBELNVF : " + jsonArrItem.getJSONObject(j).getString("VBELNVF");
                                            }
                                        }

                                    } else if (processType == 2) {

                                        if (!jsonArrItem.getJSONObject(j).isNull("MBLNR")) {

                                            documentNumber = documentNumber + "," + " MBLNR : " + jsonArrItem.getJSONObject(j).get("MBLNR");
                                            if (sap.getTypeId() == 59) {
                                                if (jsonArrItem.getJSONObject(j).get("MBLNR").toString().equals("") || jsonArrItem.getJSONObject(j).get("MBLNR").toString().equals("0000")) {
                                                    isSendUnSuccessfullCount++;
                                                }
                                            }

                                        }

                                        if (!jsonArrItem.getJSONObject(j).isNull("MJAHR")) {

                                            documentNumber = documentNumber + "," + " MJAHR : " + jsonArrItem.getJSONObject(j).get("MJAHR");

                                        }

                                        if (!jsonArrItem.getJSONObject(j).isNull("BELNR")) {

                                            documentNumber = documentNumber + "," + " BELNR : " + jsonArrItem.getJSONObject(j).get("BELNR");

                                        }

                                        if (!jsonArrItem.getJSONObject(j).isNull("GJAHR")) {

                                            documentNumber = documentNumber + "," + " GJAHR : " + jsonArrItem.getJSONObject(j).get("GJAHR");

                                        }

                                    }

                                    if (!documentNumber.equals("")) {
                                        listOfResponseDetail.get(j).setSapDocumentNumber(documentNumber.substring(1, documentNumber.length()));
                                    }

                                    if (!jsonArrItem.getJSONObject(j).isNull("DOCNUM")) {
                                        if (!jsonArrItem.getJSONObject(j).getString("DOCNUM").equals("")) {
                                            sapIDocNo = sapIDocNo + "," + jsonArrItem.getJSONObject(j).getString("DOCNUM");
                                            listOfResponseDetail.get(j).setSapIDocNo(jsonArrItem.getJSONObject(j).getString("DOCNUM"));
                                        }
                                    }

                                    if (!documentNumber.equals("")) {
                                        if (jsonArrItem.length() > 1) {
                                            docNumber = docNumber + " - " + boldBegin + documentNumber.substring(1, documentNumber.length()) + boldEnd;
                                        } else {

                                            docNumber = docNumber + " - " + documentNumber.substring(1, documentNumber.length());
                                        }

                                    }

                                }

                                if (!sapIDocNo.equals("")) {
                                    sapIDocNo = sapIDocNo.substring(1, sapIDocNo.length());
                                }

                                if (sap.getTypeId() == 59) {
                                    if (isSendUnSuccessfullCount > 0) {
                                        isSendWaybill = false;
                                    } else {
                                        isSendWaybill = true;
                                    }
                                }
                            }

                            if (listOfResponseDetail.size() > 0) {
                                sap.getListOfResponseDetails().addAll(listOfResponseDetail);
                            } else {
                                sap.getListOfResponseDetails().add(responseDetail);
                            }

                            if (!docNumber.equals("")) {
                                docNumber = docNumber.substring(2, docNumber.length());
                            }

                            Date sendDate = new Date();
                            sap.setIsSend(isSend);
                            sap.setResponse(result);
                            sap.setMessage(message);
                            sap.setSendDate(sendDate);
                            sap.setSapDocumentNumber(docNumber);
                            sap.setSapIDocNo(sapIDocNo);
                            sap.setIsSendWaybill(isSendWaybill);

                        } else {

                            Date sendDate = new Date();
                            sap.setIsSend(isSend);
                            sap.setResponse(result);
                            sap.setMessage(message);
                            sap.setSendDate(sendDate);
                            sap.setSapDocumentNumber(docNumber);
                            sap.setSapIDocNo(sapIDocNo);
                            sap.setIsSendWaybill(isSendWaybill);

                        }

                    } else {

                        Date sendDate = new Date();
                        sap.setIsSend(false);
                        sap.setResponse(result);
                        sap.setMessage("Http Status : " + returnCode);
                        sap.setSendDate(sendDate);
                        sap.setSapDocumentNumber(docNumber);
                        sap.setSapIDocNo(sapIDocNo);
                        sap.setIsSendWaybill(isSendWaybill);

                    }

                } catch (Exception e) {

                    System.out.println("cath 1 : " + e.toString());

                    Date sendDate = new Date();
                    sap.setIsSend(false);
                    sap.setResponse(result);
                    sap.setMessage(sessionBean.loc.getString("unsuccesfuloperation"));
                    sap.setSendDate(sendDate);
                    sap.setSapDocumentNumber(docNumber);
                    sap.setSapIDocNo(sapIDocNo);
                    sap.setIsSendWaybill(isSendWaybill);

                } finally {
                    try {
                        httpPost.releaseConnection();
                    } catch (Exception fe) {
                        System.out.println("catch 2 : " + fe.toString());

                        Date sendDate = new Date();
                        sap.setIsSend(false);
                        sap.setResponse(result);
                        sap.setMessage(sessionBean.loc.getString("unsuccesfuloperation"));
                        sap.setSendDate(sendDate);
                        sap.setSapDocumentNumber(docNumber);
                        sap.setSapIDocNo(sapIDocNo);
                        sap.setIsSendWaybill(isSendWaybill);

                    }
                }

            } catch (Exception ex) {
                System.out.println("Catch 3 : " + ex.toString());
                Date sendDate = new Date();
                sap.setIsSend(false);
                sap.setSendDate(sendDate);
                sap.setMessage(sessionBean.loc.getString("unsuccesfuloperation"));
                sap.setSapDocumentNumber(docNumber);
                sap.setSapIDocNo(sapIDocNo);
                sap.setIsSendWaybill(isSendWaybill);

            }

        }

    }

    @Override
    public List<BranchSetting> findBranch() {
        return integrationForSapDao.findBranch();
    }

    @Override
    public String getFinancingType(int typeId) {

        String tag = "";
        Type type = new Type();
        type.setId(typeId);

        for (Type t : sessionBean.getTypes(17)) {
            if (t.getId() == typeId) {
                tag = t.getNameMap().get(sessionBean.getLangId()).getName();
                break;
            }
        }
        for (Type t : sessionBean.getTypes(16)) {
            if (t.getId() == typeId) {
                tag = t.getNameMap().get(sessionBean.getLangId()).getName();
                break;
            }
        }

        switch (typeId) {
            case 1://Perakende Satış Faturası 
                tag = sessionBean.getLoc().getString("retail") + " " + sessionBean.getLoc().getString("salesinvoice");
                break;
            case 2://Perakende Satış İade Faturası
                tag = sessionBean.getLoc().getString("retail") + " " + sessionBean.getLoc().getString("saleretuninvoice");
                break;

            default:
                break;
        }
        return tag;
    }

    @Override
    public WarehouseReceipt findWarehouseReceipt(WarehouseReceipt warehouseReceipt, BranchSetting selectedBranch) {
        Map<String, Object> filt = new HashMap<>();
        List<WarehouseReceipt> list = integrationForSapDao.findWarehouseReceipt(0, 10, "whr.id", "ASC", filt, " AND whr.id = " + warehouseReceipt.getId(), selectedBranch);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new WarehouseReceipt();
        }

    }

    @Override
    public int openUpdate(List<IntegrationForSap> listOfSelectedSap) {

        String sapIdList = "";

        for (IntegrationForSap sap : listOfSelectedSap) {
            sapIdList = sapIdList + "," + sap.getId();
        }

        if (!sapIdList.isEmpty()) {
            sapIdList = sapIdList.substring(1, sapIdList.length());
        }

        return integrationForSapDao.openUpdate(listOfSelectedSap, sapIdList);

    }

    @Override
    public int sendStatusUpdate(List<IntegrationForSap> listOfSelectedSap, int processType) {

        int result[];

        result = integrationForSapDao.sendStatusUpdate(listOfSelectedSap, processType);

        return result[0];

    }

}
