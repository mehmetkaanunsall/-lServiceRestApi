/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 07.05.2018 09:29:37
 */
package com.mepsan.marwiz.service.item.business;

import com.mepsan.marwiz.service.item.dao.ICheckItemDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.CheckItem;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckItemService implements ICheckItemService {

    @Autowired
    private ICheckItemDao checkItemDao;

    public void setCheckItemDao(ICheckItemDao checkItemDao) {
        this.checkItemDao = checkItemDao;
    }

    @Override
    public void listStock() {
        int type = 1;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetStock xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetStock>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";

            System.out.println("branchSetting.getwSendPoint()=" + branchSetting.getwSendPoint());
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

            JsonObject resJson = gson.fromJson(res, JsonObject.class);

            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            System.out.println("---listStock Response--" + res);
            System.out.println("---ListStock Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);

        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listBrand() {
        int type = 2;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetBrand xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetBrand>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            System.out.println("---listBrand Response--" + res);
            System.out.println("---listBrand Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listUnit() {
        int type = 3;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetUnit xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "        </ns2:GetUnit>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            System.out.println("---listUnit Response--" + res);
            System.out.println("---listUnit Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listTax() {
        int type = 4;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetTax xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "        </ns2:GetTax>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();

            System.out.println("---listTax Response--" + res);
            System.out.println("---listTax Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listNotification(BranchSetting branchSetting) {
        int type = 5;
        Integer maxCenterNotificaionId = checkItemDao.getMaxCenterNotificaionId(branchSetting.getBranch().getId());
        System.out.println("branchSetting.id:" + branchSetting.getId());

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            if (maxCenterNotificaionId == null) {
                maxCenterNotificaionId = 0;
            }

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetNotification xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <lastNotificationID><![CDATA[" + maxCenterNotificaionId + "]]></lastNotificationID>\n"
                    + "        </ns2:GetNotification>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();

            System.out.println("---listNotification Response--" + res);
            System.out.println("---listNotification Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listNotificationAsync() {
        List<BranchSetting> branchSettings = checkItemDao.findTopCentralIntegratedBranchSettings();
        executeListNotification(branchSettings);
    }

    @Override
    public void executeListNotification(List<BranchSetting> branchSettings) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (BranchSetting branchSetting : branchSettings) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    listNotification(branchSetting);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    @Override
    public void listCampaign() {
        int type = 6;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetCampaign  xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetCampaign >\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);

            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();

            System.out.println("---listCampaign Response--" + res);
            System.out.println("---listCampaign Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listAccount() {
        int type = 7;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetAccount xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetAccount>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);

            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();

            System.out.println("---listAccount Response--" + res);
            System.out.println("---listAccount Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listWasteReason() {
        int type = 8;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetWasteReason xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "        </ns2:GetWasteReason>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();

            System.out.println("---listWasteReason Response--" + res);
            System.out.println("---listWasteReason Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listStarbucksStock() {
        int type = 9;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetStarbucksStock xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetStarbucksStock>>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);

            System.out.println("---listStarbucksStock Response--" + res);
            System.out.println("---listStarbucksStock Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listCentralSupplier() {
        int type = 10;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetCentralSupplier xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetCentralSupplier>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();

            System.out.println("---listCentralSupplier Response--" + res);
            System.out.println("---listCentralSupplier Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listCurrency() {

        System.out.println("listCurrency");
        int type = 11;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            System.out.println("branchSetting.getBranch().getLicenceCode()" + branchSetting.getBranch().getLicenceCode());

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetCurrencies xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "        </ns2:GetCurrencies>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            System.out.println("listCurrency res" + res.toString());

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
             System.out.println("---listCurrency Response--" + res);
            System.out.println("---listCurrency Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listExchange() {
        int type = 12;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetExchange xmlns:ns2=\"http://ws/\">\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetExchange>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            
              System.out.println("---listExchange Response--" + res);
            System.out.println("---listExchange Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listCampaingInfo() {
        int type = 13;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetCampaignInfo xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetCampaignInfo>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            
               System.out.println("---listCampaingInfo Response--" + res);
            System.out.println("---listCampaingInfo Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }

    @Override
    public void listVideos() {
        int type = 14;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetVideos xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetVideos>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            
             System.out.println("---listVideos Response--" + res);
            System.out.println("---listVideos Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }
    
      @Override
    public void listCentralCategories() {
        int type = 15;
        BranchSetting branchSetting = checkItemDao.findTopCentralIntegratedBranchSetting();
        Date maxProcessDateByType = checkItemDao.getMaxProcessDateByType(type);

        CheckItem checkItem = new CheckItem();
        checkItem.setType(type);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                    = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "    <SOAP-ENV:Header/>\n"
                    + "    <S:Body>\n"
                    + "        <ns2:GetCenterCategory xmlns:ns2=\"http://ws/\">\n"
                    + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                    + "            <processdate><![CDATA[" + processDateString + "]]></processdate>\n"
                    + "        </ns2:GetCenterCategory>\n"
                    + "    </S:Body>\n"
                    + "</S:Envelope>";
            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            JsonObject resJson = gson.fromJson(res, JsonObject.class);
            boolean resBoolean = resJson.get("Result").getAsBoolean();
            checkItem.setIsSuccess(resBoolean);
            if (resBoolean) {
                Date processDate = gson.fromJson(resJson.get("ProcessDate"), Date.class);
                checkItem.setProcessDate(processDate);
            }

        } catch (Exception ex) {
            checkItem.setProcessDate(new Date());
            checkItem.setIsSuccess(false);
            res = res + "---Error:" + ex.getMessage();
            
             System.out.println("---listCentralCategories Response--" + res);
            System.out.println("---listCentralCategories Catch--Error : " + ex.getMessage());
        }
        checkItem.setResponse(res);
        checkItemDao.insertCheckItem(checkItem);
    }
}
