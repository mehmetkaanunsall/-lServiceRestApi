package com.mepsan.marwiz.system.sapagreement.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.sapagreement.dao.ISapAgreementDao;
import com.mepsan.marwiz.system.sapagreement.dao.SapAgreement;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class SapAgreementService implements ISapAgreementService {

    @Autowired
    private ISapAgreementDao sapAgreementDoa;
    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSapAgreementDoa(ISapAgreementDao sapAgreementDoa) {
        this.sapAgreementDoa = sapAgreementDoa;
    }

    @Override
    public SapAgreement getDataSap(Date date) { //Web servisten otomasyon, satış tipi, kasa devirleri, pos satışları vb. verileri alır
        BranchSetting bs = sessionBean.getUser().getLastBranchSetting();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String response = "";
        String message = "";
        boolean isSend = false;
        String result = null;
        String url = bs.getErpUrl();
        SapAgreement sapAgreement = new SapAgreement();
        HttpPost httpPost = new HttpPost(url);

        List<SapAgreement> list = new ArrayList<>();

        try {

            HttpClient httpClient = WebServiceClient.createHttpClient_AcceptsUntrustedCerts();

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

            byte[] encodedAuth = Base64.getEncoder().encode((bs.getErpUsername() + ":" + bs.getErpPassword()).getBytes("UTF-8"));
            String authHeader = "Basic " + new String(encodedAuth);
            httpPost.addHeader("Authorization", authHeader);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("PARTN", bs.getBranch().getLicenceCode());
            jsonObject.addProperty("PDATE", sdf.format(date));

            JsonObject jsonSendData = new JsonObject();
            jsonSendData.add("IMPORT", jsonObject);

            String json = jsonSendData.toString();
            StringEntity requestEntity = new StringEntity(
                    json,
                    ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);
            System.out.println("----json send data----" + jsonSendData.toString());
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int returnCode = httpResponse.getStatusLine().getStatusCode();

            if (returnCode == 200) {
                result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                JSONObject resJson = new JSONObject(result);
                System.out.println("RESULT : " + result);

                JSONObject resJsonResult = new JSONObject(resJson.get("EXPORT").toString());

                sapAgreement.setPosSaleJson(resJsonResult.get("POSDT").toString());
                sapAgreement.setSafeTransferJson(resJsonResult.get("ENDDY").toString());
                sapAgreement.setAutomationJson(resJsonResult.get("OTOFB").toString());
                sapAgreement.setTotalJson(resJsonResult.get("OTOST").toString());
                sapAgreement.setMessage(resJsonResult.get("MESSAGE").toString());
                sapAgreement.setMessageType(resJsonResult.get("MESSTYP").toString());
                if (!resJsonResult.get("MESSTYP").toString().equalsIgnoreCase("E")) {
                    sapAgreement.setIsSuccess(true);
                } else {
                    sapAgreement.setIsSuccess(false);
                }
                sapAgreement.setMessage(resJsonResult.get("MESSAGE").toString());
                sapAgreement.setMessageType(resJsonResult.get("MESSTYP").toString());

            } else {
                System.out.println("HTTP STATUS : " + returnCode);
                sapAgreement.setIsSuccess(false);
                sapAgreement.setMessage("HTTP STATUS : " + returnCode);

            }

        } catch (Exception e) {
            System.out.println("Catch 1 : " + e.toString());
            sapAgreement.setIsSuccess(false);
            sapAgreement.setMessage("Catch 1 : " + e.toString());

        } finally {
            try {
                httpPost.releaseConnection();
            } catch (Exception fe) {
                System.out.println("Catch 2 : " + fe.toString());
                sapAgreement.setIsSuccess(false);
                sapAgreement.setMessage("Catch 2 : " + fe.toString());

            }
        }

        return sapAgreement;

    }

    @Override
    public List<SapAgreement> listOfFuel(SapAgreement sap) {//Web servisten dönen response jsondan akarkayıt verilerini listeler
        List<SapAgreement> listFuel = new ArrayList<>();

        if (!sap.getAutomationJson().equalsIgnoreCase("") && !sap.getAutomationJson().isEmpty()) {
            JSONObject jsonObject = new JSONObject(sap.getAutomationJson().toString());
            JSONArray jsonArray = new JSONArray(jsonObject.get("item").toString());
            SapAgreement sapTestSale = new SapAgreement();
            sapTestSale.getStock().setName("Test Satışı");
            sapTestSale.setQuantity(BigDecimal.ZERO);
            sapTestSale.setTotalMoney(BigDecimal.ZERO);
            for (int j = 0; j < jsonArray.length(); j++) {
                if (!jsonArray.getJSONObject(j).toString().isEmpty()) {

                    SapAgreement sapPos = new SapAgreement();
                    if (jsonArray.getJSONObject(j).getString("IS_TEST_SALES").equalsIgnoreCase("X")) {
                        sapPos.setIsTestSale(true);
                    } else {
                        sapPos.setIsTestSale(false);
                    }

                    sapPos.getStock().setName(jsonArray.getJSONObject(j).getString("PRODUCT_DESCRIPTION"));
                    sapPos.getStock().setCode(jsonArray.getJSONObject(j).getString("PRODUCT_CODE_ERP"));
                    sapPos.setUnitPrice(jsonArray.getJSONObject(j).getBigDecimal("UNIT_PRICE"));
                    sapPos.setQuantity(jsonArray.getJSONObject(j).getBigDecimal("VOLUME"));
                    sapPos.setTotalMoney(jsonArray.getJSONObject(j).getBigDecimal("TOTAL"));
                    listFuel.add(sapPos);

                }

            }

        }

        return listFuel;

    }

    @Override
    public List<SapAgreement> listOfPaymentTypes(SapAgreement sap, List<SapAgreement> listOfPaymentTypes) { // Web servisten dönen reponse jsondan ödeme tiplerini listeler

        List<SapAgreement> listPaymentType = new ArrayList<>();
        if (!sap.getTotalJson().isEmpty() && !sap.getTotalJson().equalsIgnoreCase("")) {
            JSONObject jsonObject = new JSONObject(sap.getTotalJson());
            JSONArray jsonArray = new JSONArray(jsonObject.get("item").toString());
            for (int j = 0; j < jsonArray.length(); j++) {
                if (!jsonArray.getJSONObject(j).toString().isEmpty()) {
                    SapAgreement sapPaymentType = new SapAgreement();
                    sapPaymentType.getPaymentType().setEntegrationcode(String.valueOf(jsonArray.getJSONObject(j).getInt("SALES_TYPE_ID")));
                    sapPaymentType.setTotalMoney(jsonArray.getJSONObject(j).getBigDecimal("TOTAL"));

                    listPaymentType.add(sapPaymentType);
                }

            }

            for (SapAgreement paymentType : listOfPaymentTypes) {
                for (SapAgreement sapPaymentType : listPaymentType) {

                    if (Integer.parseInt(sapPaymentType.getPaymentType().getEntegrationcode()) == Integer.parseInt(paymentType.getPaymentType().getEntegrationcode())) {
                        paymentType.setTotalMoney(sapPaymentType.getTotalMoney());

                    }

                }

            }

        }

        for (Iterator<SapAgreement> iterator = listOfPaymentTypes.iterator(); iterator.hasNext();) {
            SapAgreement value = iterator.next();
            if (value.getTotalMoney() == null && Integer.parseInt(value.getPaymentType().getEntegrationcode()) != 30) {
                iterator.remove();
            }
        }
        return listOfPaymentTypes;
    }

    @Override
    public List<SapAgreement> listCurrency() {
        return sapAgreementDoa.listCurrency();
    }

    @Override
    public List<SapAgreement> listOfPosSales(SapAgreement sap) { //Web servisten dönen response jsondan pos satış detaylarını listeler
        List<SapAgreement> listPosSales = new ArrayList<>();
        if (!sap.getPosSaleJson().isEmpty() && !sap.getPosSaleJson().equalsIgnoreCase("")) {
            JSONObject jsonObject = new JSONObject(sap.getPosSaleJson());
            JSONArray jsonArrayİtem = new JSONArray(jsonObject.get("item").toString());

            if (!jsonArrayİtem.toString().isEmpty()) {

                for (int i = 0; i < jsonArrayİtem.length(); i++) {
                    if (!jsonArrayİtem.getJSONObject(i).toString().isEmpty()) {

                        JSONObject jsonObjectPOSDT = new JSONObject(jsonArrayİtem.getJSONObject(i).get("POSDT").toString());

                        if (jsonObjectPOSDT.get("item").toString().charAt(0) == '[') {

                            JSONArray jsonArrayİtemDT = new JSONArray(jsonObjectPOSDT.get("item").toString());

                            for (int j = 0; j < jsonArrayİtemDT.length(); j++) {
                                if (!jsonArrayİtemDT.getJSONObject(j).toString().isEmpty()) {
                                    SapAgreement sapPos = new SapAgreement();
                                    sapPos.setBankName(jsonArrayİtemDT.getJSONObject(j).getString("BANKT"));
                                    sapPos.setMerchantNumber(jsonArrayİtemDT.getJSONObject(j).getBigDecimal("UYEIS"));
                                    sapPos.setPosId(jsonArrayİtemDT.getJSONObject(j).getString("TERMN"));
                                    sapPos.setPosDailyEnd(jsonArrayİtemDT.getJSONObject(j).getBigDecimal("BRUTT"));

                                    listPosSales.add(sapPos);
                                }

                            }

                        } else if (jsonObjectPOSDT.get("item").toString().charAt(0) == '{') {

                            JSONObject jsonİtemDT = new JSONObject(jsonObjectPOSDT.get("item").toString());

                            if (!jsonİtemDT.toString().isEmpty()) {
                                SapAgreement sapPos = new SapAgreement();
                                sapPos.setBankName(jsonİtemDT.getString("BANKT"));
                                sapPos.setMerchantNumber(jsonİtemDT.getBigDecimal("UYEIS"));
                                sapPos.setPosId(jsonİtemDT.getString("TERMN"));
                                sapPos.setPosDailyEnd(jsonİtemDT.getBigDecimal("BRUTT"));

                                listPosSales.add(sapPos);
                            }

                        }
                    }

                }

            }

        }
        return listPosSales;

    }

    @Override
    public List<SapAgreement> listOfSafeTransfer(SapAgreement sap, List<SapAgreement> listSafeTransfer) {//Web servisten dönen response jsondan kasa devir bakiyelerini listeler
        List<SapAgreement> listOfSafeTransferSap = new ArrayList<>();
        if (!sap.getSafeTransferJson().isEmpty() && !sap.getSafeTransferJson().equalsIgnoreCase("")) {
            JSONObject jsonObject = new JSONObject(sap.getSafeTransferJson());
            JSONArray jsonArrayItem = new JSONArray(jsonObject.get("item").toString());

            for (int j = 0; j < jsonArrayItem.length(); j++) {
                if (!jsonArrayItem.getJSONObject(j).toString().isEmpty()) {
                    SapAgreement sapSafe = new SapAgreement();
                    sapSafe.setTotalMoney(jsonArrayItem.getJSONObject(j).getBigDecimal("DMBTR"));
                    sapSafe.getCurrency().setCode(jsonArrayItem.getJSONObject(j).getString("WAERS"));

                    listOfSafeTransferSap.add(sapSafe);
                }

            }

            for (SapAgreement safe : listSafeTransfer) {

                for (SapAgreement sapSafe : listOfSafeTransferSap) {
                    if (safe.getCurrency().getCode().equalsIgnoreCase(sapSafe.getCurrency().getCode())) {
                        safe.setTotalMoney(sapSafe.getTotalMoney());

                    } else if (sapSafe.getCurrency().getCode().equalsIgnoreCase("TRY") && safe.getCurrency().getCode().equalsIgnoreCase("TL")) {
                        safe.setTotalMoney(sapSafe.getTotalMoney());

                    }

                }

            }

        }

        return listSafeTransfer;

    }

    @Override
    public List<SapAgreement> findAllExchange(Date beginDate, Date endDate) {

        List<SapAgreement> returnList = new ArrayList<>();
        List<Exchange> exchangeList = new ArrayList<>();
        exchangeList = sapAgreementDoa.findAllExchange(beginDate, endDate);
        if (!exchangeList.isEmpty()) {
            for (Exchange exc : exchangeList) {
                SapAgreement sap = new SapAgreement();
                sap.setExchange(exc);
                returnList.add(sap);

            }

        }
        return returnList;
    }

    @Override
    public List<SapAgreement> listOfExpense() {//Masraf table ı için kayıtlar sabit olduğu için liste manuel oluşturuldu
        List<SapAgreement> expenseList = new ArrayList<>();
        SapAgreement obj = new SapAgreement();
        obj.setExpenseDefinition("JENERATÖR YAKIT BEDELİ");
        obj.setExpensecode(1);
        obj.setId(1);
        expenseList.add(obj);

        SapAgreement obj1 = new SapAgreement();
        obj1.setExpenseDefinition("TEST SATIŞI");
        obj1.setExpensecode(2);
        obj1.setId(2);
        expenseList.add(obj1);

        return expenseList;
    }

    @Override
    public SapAgreement sendIntegration(List<SapAgreement> listOfExchangeEntries, List<SapAgreement> listOfExpense, List<SapAgreement> listOfFuelOilZSeries, List<SapAgreement> listOfMarketZSeries, List<SapAgreement> listOfSendToBank, BigDecimal totalFuelLiter, BigDecimal difference, List<SapAgreement> listOfPaymentTypes,
            Date date, List<SapAgreement> listOfFuel, List<SapAgreement> listOfPosSales,
            BigDecimal totalFuelTotalMoney, BigDecimal totalMarketSales, BigDecimal cashPayment, BigDecimal totalExchangeTotalMoney,
            BigDecimal totalMarketSaleReturn, BigDecimal nonPosCollection, BigDecimal totalCollection, BigDecimal saleCollection,
            BigDecimal automationSaleDifference,
            List<SapAgreement> listOfSafeTransfer, List<SapAgreement> listOfEndDay, BigDecimal totalPaymentTypes, BigDecimal totalExpense, BigDecimal transferAutomationSaleDifference,
            BigDecimal totalPosSalesMoney, BigDecimal totalFuelZSeriesQuantity, BigDecimal totalFuelZSeriesTotalMoney, BigDecimal totalMarketZSeries,
            BigDecimal totalExchangeQuantity, BigDecimal differenceMarket, BigDecimal totalMarketReturnWithSale, BigDecimal testSalesQuantityTotal, BigDecimal testSalesTotalMoneyTotal, List<SapAgreement> listFuelTestSales) {
        BranchSetting bs = sessionBean.getUser().getLastBranchSetting();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Currency currency = sessionBean.getCurrency(sessionBean.getUser().getLastBranch().getCurrency().getId());
        SapAgreement sapAgreement = new SapAgreement();
        sapAgreement = createJson(date, listOfFuel, listOfPosSales, listOfExpense, listOfExchangeEntries, listOfFuelOilZSeries, listOfMarketZSeries, listOfPaymentTypes,
                totalFuelTotalMoney, totalMarketSales, cashPayment, totalExchangeTotalMoney, totalMarketSaleReturn, nonPosCollection, totalCollection, saleCollection, automationSaleDifference, listOfSafeTransfer, listOfSendToBank, listOfEndDay,
                totalPaymentTypes, totalExpense, transferAutomationSaleDifference, totalFuelLiter, totalPosSalesMoney, totalFuelZSeriesQuantity, totalFuelZSeriesTotalMoney, totalMarketZSeries,
                totalExchangeQuantity, differenceMarket, totalMarketReturnWithSale, testSalesQuantityTotal,
                testSalesTotalMoneyTotal, listFuelTestSales);

        JsonObject jsonSendDataEnd = new JsonObject();
        JsonObject jsonSendData = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for (SapAgreement exchange : listOfExchangeEntries) {
            if ((exchange.getTotalMoney() != null && exchange.getTotalMoney().compareTo(BigDecimal.ZERO) == 1) && (exchange.getQuantity() != null && exchange.getQuantity().compareTo(BigDecimal.ZERO) == 1)) {
                JsonObject currencyObject = new JsonObject();
                currencyObject.addProperty("CURRY", exchange.getExchange().getCurrency().getCode());
                currencyObject.addProperty("WRBTR", exchange.getQuantity());
                currencyObject.addProperty("FFACT", exchange.getExchange().getBuying());
                currencyObject.addProperty("DMBTR", exchange.getTotalMoney());
                currencyObject.addProperty("WAERS", currency.getInternationalCode());
                jsonArray.add(currencyObject);
            }

        }
        JsonObject currencyObjectItem = new JsonObject();
        currencyObjectItem.add("item", jsonArray);
        jsonSendData.add("CURRTTYPE", currencyObjectItem);

        JsonArray jsonArrayExpense = new JsonArray();

        for (SapAgreement expense : listOfExpense) {
            if (expense.getTotalMoney() != null && expense.getTotalMoney().compareTo(BigDecimal.ZERO) == 1) {
                JsonObject expenseJson = new JsonObject();
                expenseJson.addProperty("MTYPE", expense.getExpensecode());
                expenseJson.addProperty("DMBTR", expense.getTotalMoney());
                expenseJson.addProperty("WAERS", currency.getInternationalCode());
                jsonArrayExpense.add(expenseJson);
            }

        }
        JsonObject expenseJsonItem = new JsonObject();
        expenseJsonItem.add("item", jsonArrayExpense);

        jsonSendData.add("EXPENSE", expenseJsonItem);

        JsonArray jsonArrayBankSend = new JsonArray();

        for (SapAgreement bankSend : listOfSendToBank) {
            if (bankSend.getTotalMoney() != null && bankSend.getTotalMoney().compareTo(BigDecimal.ZERO) == 1) {
                JsonObject bankSendJson = new JsonObject();
                bankSendJson.addProperty("CURRY", bankSend.getCurrency().getInternationalCode());
                bankSendJson.addProperty("WRBTR", bankSend.getTotalMoney());
                jsonArrayBankSend.add(bankSendJson);
            }

        }
        JsonObject bankSendJsonItem = new JsonObject();
        bankSendJsonItem.add("item", jsonArrayBankSend);
        jsonSendData.add("BANKSEND", bankSendJsonItem);

        JsonArray jsonArrayParo = new JsonArray();
        for (SapAgreement paymentType : listOfPaymentTypes) {
            if (paymentType.getPaymentType().getEntegrationcode().equals("30")) {
                if (paymentType.getTotalMoney() != null) {
                    JsonObject paroJson = new JsonObject();
                    paroJson.addProperty("DMBTR", paymentType.getTotalMoney());
                    paroJson.addProperty("WAERS", currency.getInternationalCode());
                    jsonArrayParo.add(paroJson);
                }
            }
        }

        jsonSendData.add("PARO", jsonArrayParo);

        JsonArray jsonArrayMarketZ = new JsonArray();

        for (SapAgreement marketZ : listOfMarketZSeries) {
            if (marketZ.getTotalMoney() != null && marketZ.getTotalMoney().compareTo(BigDecimal.ZERO) == 1) {
                JsonObject marketZJson = new JsonObject();
                marketZJson.addProperty("ZSRAL", Integer.parseInt(marketZ.getzSeries().getNumber()));
                marketZJson.addProperty("DMBTR", marketZ.getTotalMoney());
                marketZJson.addProperty("WAERS", currency.getInternationalCode());
                jsonArrayMarketZ.add(marketZJson);
            }

        }
        JsonObject marketZJsonItem = new JsonObject();
        marketZJsonItem.add("item", jsonArrayMarketZ);
        jsonSendData.add("ZREPORTMRKT", marketZJsonItem);

        JsonArray jsonArrayFuelZ = new JsonArray();

        for (SapAgreement fuelZ : listOfFuelOilZSeries) {

            if (fuelZ.getTotalMoney() != null && fuelZ.getTotalMoney().compareTo(BigDecimal.ZERO) == 1) {
                JsonObject fuelZJson = new JsonObject();
                fuelZJson.addProperty("ZSRAL", Integer.parseInt(fuelZ.getzSeries().getNumber()));
                fuelZJson.addProperty("LITER", fuelZ.getQuantity());
                fuelZJson.addProperty("DMBTR", fuelZ.getTotalMoney());
                fuelZJson.addProperty("WAERS", currency.getInternationalCode());
                jsonArrayFuelZ.add(fuelZJson);
            }
        }
        JsonObject fuelZJsonItem = new JsonObject();
        fuelZJsonItem.add("item", jsonArrayFuelZ);
        JsonObject zReportJson = new JsonObject();
        zReportJson.add("ZREPORT", fuelZJsonItem);
        zReportJson.addProperty("TOTAL", totalFuelLiter);
        zReportJson.addProperty("DIFFR", difference);
        zReportJson.addProperty("WAERS", currency.getInternationalCode());

        jsonSendData.add("ZREPORTGNL", zReportJson);

        jsonSendData.addProperty("PARTN", bs.getBranch().getLicenceCode());
        jsonSendData.addProperty("PDATE", dateFormat.format(date));

        jsonSendDataEnd.add("IMPORT", jsonSendData);
        System.out.println("-----JSON SEND DATA---" + jsonSendDataEnd.toString());

        String response = "";
        String message = "";
        boolean isSend = false;
        String result = null;
        String url = bs.getErpUrl();

        HttpPost httpPost = new HttpPost(url);
        try {

            HttpClient httpClient = WebServiceClient.createHttpClient_AcceptsUntrustedCerts();
            httpPost.addHeader("Operation", "MepsanDataSet");

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

            byte[] encodedAuth = Base64.getEncoder().encode((bs.getErpUsername() + ":" + bs.getErpPassword()).getBytes("UTF-8"));
            String authHeader = "Basic " + new String(encodedAuth);
            httpPost.addHeader("Authorization", authHeader);

            String json = jsonSendDataEnd.toString();
            StringEntity requestEntity = new StringEntity(
                    json,
                    ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println("return code --" + returnCode);

            if (returnCode == 200) {
                result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                JSONObject resJson = new JSONObject(result);
                System.out.println("RESULT : " + result);

                JSONObject resJsonReturn = new JSONObject(resJson.getJSONObject("EXPORT").toString());

                if (resJsonReturn.getString("MESSTYP").equals("S")) {
                    isSend = true;
                } else if (resJsonReturn.getString("MESSTYP").equals("E")) {
                    isSend = false;
                }
                message = resJsonReturn.getString("MESSAGE");

                Date sendDate = new Date();
                sapAgreement.setIsSend(isSend);
                sapAgreement.setResponse(result);
                sapAgreement.setMessage(message);
                sapAgreement.setSendDate(sendDate);
                sapAgreement.setSendData(json);

            } else {
                System.out.println("HTTP STATUS : " + returnCode);
                Date sendDate = new Date();
                sapAgreement.setIsSend(isSend);
                sapAgreement.setResponse("HTTP STATUS : " + returnCode);
                sapAgreement.setMessage("HTTP STATUS : " + returnCode);
                sapAgreement.setSendDate(sendDate);
                sapAgreement.setSendData(json);
            }

        } catch (Exception e) {
            System.out.println("Catch 1 : " + e.toString());
            Date sendDate = new Date();
            sapAgreement.setIsSend(isSend);
            sapAgreement.setResponse("CATCH : " + e.toString());
            sapAgreement.setMessage("CATCH : " + e.toString());
            sapAgreement.setSendDate(sendDate);

        } finally {
            try {
                httpPost.releaseConnection();
            } catch (Exception fe) {
                System.out.println("Catch 2 : " + fe.toString());
                Date sendDate = new Date();
                sapAgreement.setIsSend(isSend);
                sapAgreement.setResponse("Catch 2 : " + fe.toString());
                sapAgreement.setMessage("CATCH : " + fe.toString());
                sapAgreement.setSendDate(sendDate);

            }
        }

        return sapAgreement;

    }

    @Override
    public BigDecimal findMarketSalesTotal(Date beginDate, Date endDate) {
        List<SapAgreement> totalMarketSales = new ArrayList<>();
        BigDecimal marketTotal = BigDecimal.ZERO;
        totalMarketSales = sapAgreementDoa.findMarketSalesTotal(beginDate, endDate);
        if (!totalMarketSales.isEmpty()) {
            marketTotal = totalMarketSales.get(0).getTotalMoney();
        }
        return marketTotal;

    }

    @Override
    public List<SapAgreement> findMarketSaleReturnTotal(SapAgreement sapAgreement, Date beginDate, Date endDate) {
        List<SapAgreement> totalMarketSaleReturn = new ArrayList<>();
        totalMarketSaleReturn = sapAgreementDoa.findMarketSaleReturnTotal(beginDate, endDate);
        return totalMarketSaleReturn;

    }

    @Override
    public int save(Date date, List<SapAgreement> listOfFuel, List<SapAgreement> listOfPosSales, List<SapAgreement> listOfExpense, List<SapAgreement> listOfExchangeEntries,
            List<SapAgreement> listOfFuelZ, List<SapAgreement> ListOfMarketZ, List<SapAgreement> listOfPaymentType,
            BigDecimal totalFuelTotalMoney, BigDecimal totalMarketSales, BigDecimal cashPayment, BigDecimal totalExchangeTotalMoney,
            BigDecimal totalMarketSaleReturn, BigDecimal nonPosCollection, BigDecimal totalCollection, BigDecimal saleCollection,
            BigDecimal automationSaleDifference,
            List<SapAgreement> listOfSafeTransfer, List<SapAgreement> listOfBankSend, List<SapAgreement> listOfEndDay, BigDecimal totalPaymentTypes, BigDecimal totalExpense, BigDecimal transferAutomationSaleDifference,
            BigDecimal totalFuelLiter, BigDecimal totalPosSalesMoney, BigDecimal totalFuelZSeriesQuantity, BigDecimal totalFuelZSeriesTotalMoney, BigDecimal totalMarketZSeries,
            BigDecimal totalExchangeQuantity, BigDecimal differenceMarket, BigDecimal totalMarketReturnWithSale, BigDecimal testSalesQuantityTotal, BigDecimal testSalesTotalMoneyTotal, List<SapAgreement> listFuelTestSales) {
        int result = 0;
        SapAgreement sap = new SapAgreement();
        sap = createJson(date, listOfFuel, listOfPosSales, listOfExpense, listOfExchangeEntries, listOfFuelZ, ListOfMarketZ, listOfPaymentType, totalFuelTotalMoney,
                totalMarketSales, cashPayment, totalExchangeTotalMoney, totalMarketSaleReturn, nonPosCollection, totalCollection, saleCollection,
                automationSaleDifference, listOfSafeTransfer, listOfBankSend, listOfEndDay, totalPaymentTypes, totalExpense, transferAutomationSaleDifference,
                totalFuelLiter, totalPosSalesMoney, totalFuelZSeriesQuantity, totalFuelZSeriesTotalMoney, totalMarketZSeries,
                totalExchangeQuantity, differenceMarket, totalMarketReturnWithSale, testSalesQuantityTotal, testSalesTotalMoneyTotal, listFuelTestSales);
        result = sapAgreementDoa.save(sap.getAutomationJson(), sap.getPosSaleJson(), sap.getExpenseJson(), sap.getExchangeJson(), sap.getFuelZJson(), sap.getMarketZJson(), sap.getSafeTransferJson(), sap.getBankTransferJson(), sap.getTotalJson(), date, sap.getPeriod(), sap.getAutomationDiffAmount(),
                1, sap.getSendData() != null ? sap.getSendData() : "", false, sap.getSendDate() != null ? sap.getSendDate() : new Date(), sap.getResponse() != null ? sap.getResponse() : "", differenceMarket);
        return result;
    }

    @Override
    public SapAgreement createJson(Date date, List<SapAgreement> listOfFuel, List<SapAgreement> listOfPosSales, List<SapAgreement> listOfExpense, List<SapAgreement> listOfExchangeEntries,
            List<SapAgreement> listOfFuelZ, List<SapAgreement> ListOfMarketZ, List<SapAgreement> listOfPaymentType,
            BigDecimal totalFuelTotalMoney, BigDecimal totalMarketSales, BigDecimal cashPayment, BigDecimal totalExchangeTotalMoney,
            BigDecimal totalMarketSaleReturn, BigDecimal nonPosCollection, BigDecimal totalCollection, BigDecimal saleCollection,
            BigDecimal automationSaleDifference,
            List<SapAgreement> listOfSafeTransfer, List<SapAgreement> listOfBankSend, List<SapAgreement> listOfEndDay, BigDecimal totalPaymentTypes,
            BigDecimal totalExpense, BigDecimal transferAutomationSaleDifference, BigDecimal totalFuelLiter, BigDecimal totalPosSalesMoney, BigDecimal totalFuelZSeriesQuantity, BigDecimal totalFuelZSeriesTotalMoney, BigDecimal totalMarketZSeries,
            BigDecimal totalExchangeQuantity, BigDecimal differenceMarket, BigDecimal totalMarketReturnWithSale, BigDecimal testSalesQuantityTotal, BigDecimal testSalesTotalMoneyTotal, List<SapAgreement> listFuelTestSales) {

        SapAgreement sapAgreement = new SapAgreement();

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
        String formatedDate = newFormat.format(date);
        int dateint = Integer.valueOf(formatedDate);

        sapAgreement.setPeriod(dateint);
        sapAgreement.setProcessDate(date);
        sapAgreement.setAutomationDiffAmount(automationSaleDifference);

        String automationJson = "";
        String posSaleJson = "";
        String expenseJson = "";
        String exchangeJson = "";
        String fuelZJson = "";
        String marketZJson = "";
        String safeTransferJson = "";
        String bankSendJson = "";
        String totalJson = "";

        //Otomasyon satışları tablosu json 
        JsonObject fuelSaleJson = new JsonObject();

        JsonArray jsonArrayAutomation = new JsonArray();
        for (SapAgreement fuel : listOfFuel) {
            JsonObject fuelJson = new JsonObject();
            fuelJson.addProperty("STOCK", fuel.getStock().getName());
            fuelJson.addProperty("UNITPRICE", fuel.getUnitPrice());
            fuelJson.addProperty("QUANTITY", fuel.getQuantity());
            fuelJson.addProperty("TOTALMONEY", fuel.getTotalMoney());
            jsonArrayAutomation.add(fuelJson);
        }

        fuelSaleJson.add("FUELSALES", jsonArrayAutomation);

        JsonArray jsonArrayAutomationTestSale = new JsonArray();

        for (SapAgreement fuel : listFuelTestSales) {
            JsonObject fuelTestJson = new JsonObject();
            fuelTestJson.addProperty("STOCK", fuel.getStock().getName());
            fuelTestJson.addProperty("UNITPRICE", fuel.getUnitPrice());
            fuelTestJson.addProperty("QUANTITY", fuel.getQuantity());
            fuelTestJson.addProperty("TOTALMONEY", fuel.getTotalMoney());
            jsonArrayAutomationTestSale.add(fuelTestJson);
        }
        fuelSaleJson.add("FUELTESTSALES", jsonArrayAutomationTestSale);

        automationJson = fuelSaleJson.toString();

        // Pos Satışları tablosu json
        JsonArray jsonArrayPosSales = new JsonArray();
        for (SapAgreement posSale : listOfPosSales) {
            JsonObject posJson = new JsonObject();
            posJson.addProperty("BANK", posSale.getBankName());
            posJson.addProperty("MERCHANTNUMBER", posSale.getMerchantNumber());
            posJson.addProperty("POSID", posSale.getPosId());
            posJson.addProperty("TOTALMONEY", posSale.getPosDailyEnd());
            jsonArrayPosSales.add(posJson);
        }
        posSaleJson = jsonArrayPosSales.toString();

        // Masraf tablosu json
        JsonArray jsonArrayExpense = new JsonArray();
        for (SapAgreement expense : listOfExpense) {
            JsonObject expenseJsonObj = new JsonObject();
            expenseJsonObj.addProperty("DEFINITION", expense.getExpenseDefinition());
            expenseJsonObj.addProperty("CODE", expense.getExpensecode());
            expenseJsonObj.addProperty("ID", expense.getId());
            expenseJsonObj.addProperty("TOTALMONEY", expense.getTotalMoney() == null ? BigDecimal.ZERO : expense.getTotalMoney());
            jsonArrayExpense.add(expenseJsonObj);
        }
        expenseJson = jsonArrayExpense.toString();

        // Döviz Girişleri tablosu json
        JsonArray jsonArrayExchange = new JsonArray();
        for (SapAgreement exchange : listOfExchangeEntries) {
            JsonObject exchangeJsonObj = new JsonObject();
            exchangeJsonObj.addProperty("CODE", exchange.getExchange().getCurrency().getCode());
            exchangeJsonObj.addProperty("ID", exchange.getExchange().getCurrency().getId());
            exchangeJsonObj.addProperty("QUANTITY", exchange.getQuantity() == null ? BigDecimal.ZERO : exchange.getQuantity());
            exchangeJsonObj.addProperty("EXCHANGERATE", exchange.getExchange().getBuying() == null ? BigDecimal.ZERO : exchange.getExchange().getBuying());
            exchangeJsonObj.addProperty("TOTALMONEY", exchange.getTotalMoney() == null ? BigDecimal.ZERO : exchange.getTotalMoney());
            jsonArrayExchange.add(exchangeJsonObj);
        }
        exchangeJson = jsonArrayExchange.toString();

        // Akaryakıt Z tablosu json
        JsonArray jsonArrayFuelZ = new JsonArray();
        for (SapAgreement fuelZ : listOfFuelZ) {
            JsonObject fuelZJsonObj = new JsonObject();
            fuelZJsonObj.addProperty("SERIALNO", fuelZ.getzSeries().getNumber());
            fuelZJsonObj.addProperty("SERIALID", fuelZ.getzSeries().getId());
            fuelZJsonObj.addProperty("QUANTITY", fuelZ.getQuantity() == null ? BigDecimal.ZERO : fuelZ.getQuantity());
            fuelZJsonObj.addProperty("TOTALMONEY", fuelZ.getTotalMoney() == null ? BigDecimal.ZERO : fuelZ.getTotalMoney());
            jsonArrayFuelZ.add(fuelZJsonObj);
        }
        fuelZJson = jsonArrayFuelZ.toString();

        // MArket Z tablosu json
        JsonArray jsonArrayMarketZ = new JsonArray();
        for (SapAgreement marketZ : ListOfMarketZ) {
            JsonObject marketZJsonObj = new JsonObject();
            marketZJsonObj.addProperty("SERIALNO", marketZ.getzSeries().getNumber());
            marketZJsonObj.addProperty("SERIALID", marketZ.getzSeries().getId());
            marketZJsonObj.addProperty("TOTALMONEY", marketZ.getTotalMoney() == null ? BigDecimal.ZERO : marketZ.getTotalMoney());
            jsonArrayMarketZ.add(marketZJsonObj);
        }
        marketZJson = jsonArrayMarketZ.toString();

        // Kasa Devirleri ve günsonu tablosu json
        JsonObject safeJson = new JsonObject();

        JsonArray jsonArraySafeTransfer = new JsonArray();
        for (SapAgreement safeTransfer : listOfSafeTransfer) {
            JsonObject safeTransferJsonObj = new JsonObject();
            safeTransferJsonObj.addProperty("CURRENCYCODE", safeTransfer.getCurrency().getCode());
            safeTransferJsonObj.addProperty("CURRENCYID", safeTransfer.getCurrency().getId());
            safeTransferJsonObj.addProperty("TOTALMONEY", safeTransfer.getTotalMoney() == null ? BigDecimal.ZERO : safeTransfer.getTotalMoney());
            jsonArraySafeTransfer.add(safeTransferJsonObj);
        }

        safeJson.add("SAFETRANSFER", jsonArraySafeTransfer);

        JsonArray jsonArrayDailyEnd = new JsonArray();
        for (SapAgreement dailyEnd : listOfEndDay) {
            JsonObject dailyEndJsonObj = new JsonObject();
            dailyEndJsonObj.addProperty("CURRENCYCODE", dailyEnd.getCurrency().getCode());
            dailyEndJsonObj.addProperty("CURRENCYID", dailyEnd.getCurrency().getId());
            dailyEndJsonObj.addProperty("TOTALMONEY", dailyEnd.getEndDayTotal() == null ? BigDecimal.ZERO : dailyEnd.getEndDayTotal());
            jsonArrayDailyEnd.add(dailyEndJsonObj);
        }

        safeJson.add("DAILYEND", jsonArrayDailyEnd);

        safeTransferJson = safeJson.toString();

        // Bankaya Gönderilen tablosu json
        JsonArray jsonArrayBankSend = new JsonArray();
        for (SapAgreement bankSend : listOfBankSend) {
            JsonObject bankSendJsonObj = new JsonObject();
            bankSendJsonObj.addProperty("CURRENCYINTERNATIONALCODE", bankSend.getCurrency().getInternationalCode());
            bankSendJsonObj.addProperty("CURRENCYCODE", bankSend.getCurrency().getCode());
            bankSendJsonObj.addProperty("CURRENCYID", bankSend.getCurrency().getId());
            bankSendJsonObj.addProperty("TOTALMONEY", bankSend.getTotalMoney() == null ? BigDecimal.ZERO : bankSend.getTotalMoney());
            jsonArrayBankSend.add(bankSendJsonObj);
        }
        bankSendJson = jsonArrayBankSend.toString();

        // Total verileri json
        JsonObject totalJsonObj = new JsonObject();
        totalJsonObj.addProperty("AUTOMATIONSALESTOTAL", totalFuelTotalMoney);
        totalJsonObj.addProperty("MARKETSALESTOTAL", totalMarketSales);
        totalJsonObj.addProperty("CASHPAYMENT", cashPayment);
        totalJsonObj.addProperty("EXCHANGETOTAL", totalExchangeTotalMoney);
        totalJsonObj.addProperty("MARKETSALERETURNTOTAL", totalMarketSaleReturn);
        totalJsonObj.addProperty("TOTALCOLLECTION", totalCollection);
        totalJsonObj.addProperty("SALECOLLECTION", saleCollection);
        totalJsonObj.addProperty("NONPOSCOLLECTION", nonPosCollection);
        totalJsonObj.addProperty("AUTOMATIONSALEDIFFERENCE", automationSaleDifference);
        totalJsonObj.addProperty("TOTALPAYMENTTYPES", totalPaymentTypes);
        totalJsonObj.addProperty("TOTALEXPENSE", totalExpense);
        totalJsonObj.addProperty("TRANSFERAUTOMATIONDIFFAMOUNT", transferAutomationSaleDifference != null ? transferAutomationSaleDifference : BigDecimal.ZERO);
        totalJsonObj.addProperty("TOTALFUELLITER", totalFuelLiter);
        totalJsonObj.addProperty("TOTALPOSSALESMONEY", totalPosSalesMoney);
        totalJsonObj.addProperty("TOTALFUELZSERIESQUANTITY", totalFuelZSeriesQuantity);
        totalJsonObj.addProperty("TOTALFUELZSERIESTOTALMONEY", totalFuelZSeriesTotalMoney);
        totalJsonObj.addProperty("TOTALMARKETZSERIES", totalMarketZSeries);
        totalJsonObj.addProperty("TOTALEXCHANGEQUANTITY", totalExchangeQuantity);
        totalJsonObj.addProperty("TOTALEXCHANGETOTALMONEY", totalExchangeTotalMoney);
        totalJsonObj.addProperty("MARKETSALEDIFFERENCE", differenceMarket);
        totalJsonObj.addProperty("TOTALMARKETRETURNWİTHSALE", totalMarketReturnWithSale);
        totalJsonObj.addProperty("TESTSALESQUANTITYTOTAL", testSalesQuantityTotal);
        totalJsonObj.addProperty("TESTSALESTOTALMONEYTOTAL", testSalesTotalMoneyTotal);

        JsonArray jsonArrayPaymentTypes = new JsonArray();
        for (SapAgreement paymentType : listOfPaymentType) {
            JsonObject paymentTypeJsonObj = new JsonObject();
            paymentTypeJsonObj.addProperty("NAME", paymentType.getPaymentType().getEntegrationname());
            paymentTypeJsonObj.addProperty("CODE", paymentType.getPaymentType().getEntegrationcode());
            paymentTypeJsonObj.addProperty("ID", paymentType.getPaymentType().getId());
            paymentTypeJsonObj.addProperty("TOTALMONEY", paymentType.getTotalMoney() == null ? BigDecimal.ZERO : paymentType.getTotalMoney());
            jsonArrayPaymentTypes.add(paymentTypeJsonObj);
        }
        totalJsonObj.add("PAYMENTTYPE", jsonArrayPaymentTypes);

        totalJson = totalJsonObj.toString();

        sapAgreement.setAutomationJson(automationJson);
        sapAgreement.setPosSaleJson(posSaleJson);
        sapAgreement.setBankTransferJson(bankSendJson);
        sapAgreement.setExchangeJson(exchangeJson);
        sapAgreement.setExpenseJson(expenseJson);
        sapAgreement.setFuelZJson(fuelZJson);
        sapAgreement.setMarketZJson(marketZJson);
        sapAgreement.setTotalJson(totalJson);
        sapAgreement.setSafeTransferJson(safeTransferJson);

        return sapAgreement;
    }

    @Override
    public int insertOrUpdateLog(SapAgreement sap, BigDecimal automationSaleDifference, int type, BigDecimal marketSaleDifference) {
        return sapAgreementDoa.insertOrUpdateLog(sap, automationSaleDifference, type, marketSaleDifference);
    }

    @Override
    public SapAgreement findall(Date beginDate, Date endDate, Date date) {
        List<SapAgreement> result = new ArrayList<>();
        SapAgreement sap = new SapAgreement();
        result = sapAgreementDoa.findall(beginDate, endDate, date);
        if (!result.isEmpty()) {
            sap = result.get(0);
        }
        return sap;
    }

    @Override
    public List<SapAgreement> listFuelData(SapAgreement obj) {
        List<SapAgreement> listOfFuel = new ArrayList<>();
        if (obj.getAutomationJson() != null && !obj.getAutomationJson().equalsIgnoreCase("")) {

            if (obj.getAutomationJson().charAt(0) == '{') {

                JSONObject jsonFuelSales = new JSONObject(obj.getAutomationJson());

                JSONArray fuelSalesArray = new JSONArray(jsonFuelSales.getJSONArray("FUELSALES").toString());

                for (int i = 0; i < fuelSalesArray.length(); i++) {
                    if (!fuelSalesArray.getJSONObject(i).toString().isEmpty()) {
                        SapAgreement sapFuel = new SapAgreement();
                        sapFuel.getStock().setName(fuelSalesArray.getJSONObject(i).getString("STOCK"));
                        sapFuel.setUnitPrice(fuelSalesArray.getJSONObject(i).getBigDecimal("UNITPRICE"));
                        sapFuel.setQuantity(fuelSalesArray.getJSONObject(i).getBigDecimal("QUANTITY"));
                        sapFuel.setTotalMoney(fuelSalesArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                        listOfFuel.add(sapFuel);
                    }
                }
            } else if (obj.getAutomationJson().charAt(0) == '[') {

                JSONArray fuelSalesArray = new JSONArray(obj.getAutomationJson());

                for (int i = 0; i < fuelSalesArray.length(); i++) {
                    if (!fuelSalesArray.getJSONObject(i).toString().isEmpty()) {
                        SapAgreement sapFuel = new SapAgreement();
                        sapFuel.getStock().setName(fuelSalesArray.getJSONObject(i).getString("STOCK"));
                        sapFuel.setUnitPrice(fuelSalesArray.getJSONObject(i).getBigDecimal("UNITPRICE"));
                        sapFuel.setQuantity(fuelSalesArray.getJSONObject(i).getBigDecimal("QUANTITY"));
                        sapFuel.setTotalMoney(fuelSalesArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                        listOfFuel.add(sapFuel);
                    }
                }

            }

        }

        return listOfFuel;
    }

    @Override
    public List<SapAgreement> listFuelTestData(SapAgreement obj) {
        List<SapAgreement> listOfFuelTest = new ArrayList<>();

        if (obj.getAutomationJson() != null && !obj.getAutomationJson().equalsIgnoreCase("")) {

            if (obj.getAutomationJson().charAt(0) == '{') {

                JSONObject jsonFuelSales = new JSONObject(obj.getAutomationJson());

                if (jsonFuelSales.getJSONArray("FUELTESTSALES") != null && !jsonFuelSales.getJSONArray("FUELTESTSALES").toString().equalsIgnoreCase("")) {
                    JSONArray fuelSalesArray = new JSONArray(jsonFuelSales.getJSONArray("FUELTESTSALES").toString());

                    for (int i = 0; i < fuelSalesArray.length(); i++) {
                        if (!fuelSalesArray.getJSONObject(i).toString().isEmpty()) {
                            SapAgreement sapFuel = new SapAgreement();
                            sapFuel.getStock().setName(fuelSalesArray.getJSONObject(i).getString("STOCK"));
                            sapFuel.setUnitPrice(fuelSalesArray.getJSONObject(i).getBigDecimal("UNITPRICE"));
                            sapFuel.setQuantity(fuelSalesArray.getJSONObject(i).getBigDecimal("QUANTITY"));
                            sapFuel.setTotalMoney(fuelSalesArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                            listOfFuelTest.add(sapFuel);
                        }
                    }

                }

            }

        }
        return listOfFuelTest;
    }

    @Override
    public List<SapAgreement> listPosSalesData(SapAgreement obj) {
        List<SapAgreement> listOfPosSales = new ArrayList<>();
        if (obj.getPosSaleJson() != null && !obj.getPosSaleJson().equalsIgnoreCase("")) {
            JSONArray posSalesArray = new JSONArray(obj.getPosSaleJson());
            for (int i = 0; i < posSalesArray.length(); i++) {
                if (!posSalesArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapPosSale = new SapAgreement();
                    sapPosSale.setBankName(posSalesArray.getJSONObject(i).getString("BANK"));
                    sapPosSale.setMerchantNumber(posSalesArray.getJSONObject(i).getBigDecimal("MERCHANTNUMBER"));
                    sapPosSale.setPosId(posSalesArray.getJSONObject(i).getString("POSID"));
                    sapPosSale.setPosDailyEnd(posSalesArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfPosSales.add(sapPosSale);
                }
            }
        }

        return listOfPosSales;
    }

    @Override
    public List<SapAgreement> listFuelZSeriesData(SapAgreement obj) {
        List<SapAgreement> listOfFuelZSeries = new ArrayList<>();
        if (obj.getFuelZJson() != null && !obj.getFuelZJson().equalsIgnoreCase("")) {
            JSONArray fuelZArray = new JSONArray(obj.getFuelZJson());
            for (int i = 0; i < fuelZArray.length(); i++) {
                if (!fuelZArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapFuelZ = new SapAgreement();
                    sapFuelZ.getzSeries().setNumber(fuelZArray.getJSONObject(i).getString("SERIALNO"));
                    sapFuelZ.getzSeries().setId(fuelZArray.getJSONObject(i).getInt("SERIALID"));
                    sapFuelZ.setQuantity(fuelZArray.getJSONObject(i).getBigDecimal("QUANTITY"));
                    sapFuelZ.setTotalMoney(fuelZArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfFuelZSeries.add(sapFuelZ);
                }
            }
        }

        return listOfFuelZSeries;
    }

    @Override
    public List<SapAgreement> listMarketZSeriesData(SapAgreement obj) {
        List<SapAgreement> listOfMarketZSeries = new ArrayList<>();
        if (obj.getMarketZJson() != null && !obj.getMarketZJson().equalsIgnoreCase("")) {
            JSONArray marketZArray = new JSONArray(obj.getMarketZJson());
            for (int i = 0; i < marketZArray.length(); i++) {
                if (!marketZArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapMarketZ = new SapAgreement();
                    sapMarketZ.getzSeries().setNumber(marketZArray.getJSONObject(i).getString("SERIALNO"));
                    sapMarketZ.getzSeries().setId(marketZArray.getJSONObject(i).getInt("SERIALID"));
                    sapMarketZ.setTotalMoney(marketZArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfMarketZSeries.add(sapMarketZ);
                }
            }
        }

        return listOfMarketZSeries;
    }

    @Override
    public List<SapAgreement> listExpenseData(SapAgreement obj) {
        List<SapAgreement> listOfExpense = new ArrayList<>();
        if (obj.getExpenseJson() != null && !obj.getExpenseJson().equalsIgnoreCase("")) {
            JSONArray expenseArray = new JSONArray(obj.getExpenseJson());
            for (int i = 0; i < expenseArray.length(); i++) {
                if (!expenseArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapExpense = new SapAgreement();
                    sapExpense.setExpenseDefinition(expenseArray.getJSONObject(i).getString("DEFINITION"));
                    sapExpense.setExpensecode(expenseArray.getJSONObject(i).getInt("CODE"));
                    sapExpense.setTotalMoney(expenseArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    sapExpense.setId(expenseArray.getJSONObject(i).getInt("ID"));
                    listOfExpense.add(sapExpense);
                }
            }
        }
        return listOfExpense;
    }

    @Override
    public List<SapAgreement> listExchangeEntriesData(SapAgreement obj) {
        List<SapAgreement> listOfExchange = new ArrayList<>();
        if (obj.getExchangeJson() != null && !obj.getExchangeJson().equalsIgnoreCase("")) {
            JSONArray exchangeArray = new JSONArray(obj.getExchangeJson());
            for (int i = 0; i < exchangeArray.length(); i++) {
                if (!exchangeArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapExchange = new SapAgreement();
                    sapExchange.getExchange().getCurrency().setCode(exchangeArray.getJSONObject(i).getString("CODE"));
                    sapExchange.getExchange().getCurrency().setId(exchangeArray.getJSONObject(i).getInt("ID"));
                    sapExchange.setQuantity(exchangeArray.getJSONObject(i).getBigDecimal("QUANTITY"));
                    sapExchange.getExchange().setBuying(exchangeArray.getJSONObject(i).getBigDecimal("EXCHANGERATE"));
                    sapExchange.setTotalMoney(exchangeArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfExchange.add(sapExchange);
                }
            }
        }
        return listOfExchange;
    }

    @Override
    public List<SapAgreement> listSafeTransferData(SapAgreement obj) {
        List<SapAgreement> listOfSafeTransfer = new ArrayList<>();
        if (obj.getSafeTransferJson() != null && !obj.getSafeTransferJson().equalsIgnoreCase("")) {
            JSONObject jsonSafeTransfer = new JSONObject(obj.getSafeTransferJson());

            JSONArray safeTransferArray = new JSONArray(jsonSafeTransfer.get("SAFETRANSFER").toString());
            for (int i = 0; i < safeTransferArray.length(); i++) {
                if (!safeTransferArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapSafeTransfer = new SapAgreement();
                    sapSafeTransfer.getCurrency().setCode(safeTransferArray.getJSONObject(i).getString("CURRENCYCODE"));
                    sapSafeTransfer.getCurrency().setId(safeTransferArray.getJSONObject(i).getInt("CURRENCYID"));
                    sapSafeTransfer.setTotalMoney(safeTransferArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfSafeTransfer.add(sapSafeTransfer);
                }
            }
        }
        return listOfSafeTransfer;
    }

    @Override
    public List<SapAgreement> listBankSendData(SapAgreement obj) {
        List<SapAgreement> listOfBankSend = new ArrayList<>();
        if (obj.getBankTransferJson() != null && !obj.getBankTransferJson().equalsIgnoreCase("")) {
            JSONArray bankSendArray = new JSONArray(obj.getBankTransferJson());
            for (int i = 0; i < bankSendArray.length(); i++) {
                if (!bankSendArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapBankSend = new SapAgreement();
                    sapBankSend.getCurrency().setCode(bankSendArray.getJSONObject(i).getString("CURRENCYCODE"));
                    sapBankSend.getCurrency().setId(bankSendArray.getJSONObject(i).getInt("CURRENCYID"));
                    sapBankSend.setTotalMoney(bankSendArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    try {
                        sapBankSend.getCurrency().setInternationalCode(bankSendArray.getJSONObject(i).getString("CURRENCYINTERNATIONALCODE"));
                    } catch (Exception e) {
                    }

                    listOfBankSend.add(sapBankSend);
                }
            }
        }
        return listOfBankSend;
    }

    @Override
    public List<SapAgreement> listDailyEndData(SapAgreement obj) {
        List<SapAgreement> listOfDailyEnd = new ArrayList<>();
        if (obj.getSafeTransferJson() != null && !obj.getSafeTransferJson().equalsIgnoreCase("")) {
            JSONObject jsonDailyEnd = new JSONObject(obj.getSafeTransferJson());

            JSONArray dailyEndArray = new JSONArray(jsonDailyEnd.get("DAILYEND").toString());
            for (int i = 0; i < dailyEndArray.length(); i++) {
                if (!dailyEndArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapDailyEnd = new SapAgreement();
                    sapDailyEnd.getCurrency().setCode(dailyEndArray.getJSONObject(i).getString("CURRENCYCODE"));
                    sapDailyEnd.getCurrency().setId(dailyEndArray.getJSONObject(i).getInt("CURRENCYID"));
                    sapDailyEnd.setEndDayTotal(dailyEndArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfDailyEnd.add(sapDailyEnd);
                }
            }
        }
        return listOfDailyEnd;
    }

    @Override
    public List<SapAgreement> listPaymentTypesData(SapAgreement obj) {
        List<SapAgreement> listOfPaymentTypes = new ArrayList<>();
        if (obj.getTotalJson() != null && !obj.getTotalJson().equalsIgnoreCase("")) {
            JSONObject jsonPayment = new JSONObject(obj.getTotalJson());
            JSONArray paymentTypeArray = new JSONArray(jsonPayment.get("PAYMENTTYPE").toString());
            for (int i = 0; i < paymentTypeArray.length(); i++) {
                if (!paymentTypeArray.getJSONObject(i).toString().isEmpty()) {
                    SapAgreement sapPaymentType = new SapAgreement();
                    sapPaymentType.getPaymentType().setEntegrationname(paymentTypeArray.getJSONObject(i).getString("NAME"));
                    sapPaymentType.getPaymentType().setId(paymentTypeArray.getJSONObject(i).getInt("ID"));
                    sapPaymentType.getPaymentType().setEntegrationcode(paymentTypeArray.getJSONObject(i).getString("CODE"));
                    sapPaymentType.setTotalMoney(paymentTypeArray.getJSONObject(i).getBigDecimal("TOTALMONEY"));
                    listOfPaymentTypes.add(sapPaymentType);
                }
            }
        }

        return listOfPaymentTypes;
    }

    @Override
    public List<SapAgreement> calculateTransferSaleDiffAmount(Date beginDate, Date endDate) {
        return sapAgreementDoa.calculateTransferSaleDiffAmount(beginDate, endDate);

    }

    @Override
    public int delete(SapAgreement obj) {
        return sapAgreementDoa.delete(obj);
    }

    @Override
    public int update(SapAgreement obj) {
        return sapAgreementDoa.update(obj);
    }

}
