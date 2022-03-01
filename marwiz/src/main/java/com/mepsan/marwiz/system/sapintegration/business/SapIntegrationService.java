/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 29.04.2019 08:55:39
 */
package com.mepsan.marwiz.system.sapintegration.business;

import com.mepsan.marwiz.general.model.log.SendSap;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.mepsan.marwiz.system.sapintegration.dao.ISapIntegrationDao;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Element;

public class SapIntegrationService implements ISapIntegrationService {

    @Autowired
    private ISapIntegrationDao sapIntegrationDao;

    @Autowired
    private SessionBean sessionBean;

    private List<SapIntegration> listOfSocar;

    public void setSapIntegrationDao(ISapIntegrationDao sapIntegrationDao) {
        this.sapIntegrationDao = sapIntegrationDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SapIntegration> listOfCollections(Date begin, Date end, int isSend) {
        return sapIntegrationDao.listOfCollections(begin, end, isSend);
    }

    @Override
    public List<SapIntegration> listOfSafeToBank(Date begin, Date end, int isSend) {
        return sapIntegrationDao.listOfSafeToBank(begin, end, isSend);
    }

    /**
     * Bu metot gelen tahsilatları socar sap sistemine gönderir.
     *
     * @param litOfSocarIntegration
     * @return
     */
    @Override
    public boolean sendCollections(List<SapIntegration> litOfSocarIntegration) {
        BranchSetting brSetting = sessionBean.getLastBranchSetting();
        String data = "";
        String items = "";
        String res = "";
        if (listOfSocar == null) {
            listOfSocar = new ArrayList<>();
        }
        listOfSocar.clear();
        listOfSocar.addAll(litOfSocarIntegration);

        if (brSetting.getErpIntegrationId() == 1) {//Socar a gönderilecek ise

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(',');
            decimalFormatSymbols.setGroupingSeparator('.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            for (SapIntegration si : listOfSocar) {
                items="";
                items = items + "<item>\n"
                        + "<BelgeNo>M" + si.getFinancingDocument().getId() + si.getBranchCode()
                        + sdf.format(si.getFinancingDocument().getDocumentDate())
                        + "</BelgeNo>\n"
                        + "<IslemTipNo>" + (si.getFinancingDocument().getFinancingType().getId() == 47 ? 10 : 10533) + "</IslemTipNo>\n"
                        + "<IslemTarihi>" + sdf2.format(si.getFinancingDocument().getDocumentDate()) + "</IslemTarihi>\n"
                        + "<MusteriTipi>1</MusteriTipi>\n"
                        + "<TahsilatTipi>" + (si.getFinancingDocument().getFinancingType().getId() == 47 ? 1 : 2) + "</TahsilatTipi>\n"
                        + "<CariHesapId></CariHesapId>\n"
                        + "<CariHesapKodu></CariHesapKodu>\n"
                        + "<CariMuhasebeKodu></CariMuhasebeKodu>\n"
                        + "<CariHareketAciklama>" + si.getFinancingDocument().getDescription() + "</CariHareketAciklama>\n"
                        + "<BankaHesapKodu>" + si.getBankAccount().getAccountNumber() + "</BankaHesapKodu>\n"
                        + "<KkHesapKodu>" + si.getBankAccount().getAccountNumber() + "</KkHesapKodu>\n"
                        + "<KkHesapAdi>" + si.getBankAccount().getName() + "</KkHesapAdi>\n"
                        + "<SubeKodu>" + si.getBranchCode() + "</SubeKodu>\n"
                        + "<SubeId></SubeId>\n"
                        + "<Acilklama>" + si.getFinancingDocument().getDescription() + "</Acilklama>\n"
                        + "<Tutar>" + formatter.format(si.getFinancingDocument().getPrice()) + "</Tutar>\n"
                        + "<ParaBirimi>TRY</ParaBirimi>\n"
                        + "<KasaKodu>" + si.getSafe().getCode() + "</KasaKodu>\n"
                        + "<Company></Company>\n"
                        + "<ReferansBelge></ReferansBelge>\n"
                        + "<TayinNo></TayinNo>\n"
                        + "</item>\n";

                data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ıst=\"http://socar.com/MEPSAN/IstasyonSatisTahsilat\">\n"
                        + "    <x:Header/>\n"
                        + "    <x:Body>\n"
                        + "        <ıst:SatisTahsilatReq>\n"
                        + "            <KasaBanka/>\n"
                        + "            <Masraf/>\n"
                        + "            <Tahsilat>\n"
                        + items
                        + "            </Tahsilat>\n"
                        + "        </ıst:SatisTahsilatReq>\n"
                        + "    </x:Body>\n"
                        + "</x:Envelope>";

                res = sendSocarWebService(data, 1, si);

            }

            return res.equals("S"); // DÜZELTTTTTTTTTTT
        }

        return false;
    }

    /**
     * Bu metot gelen bankaya çıkış emirlerini socar için sap ye gönderir.
     *
     * @param litOfSocarIntegration
     * @return
     */
    @Override
    public boolean sendSafeToBank(List<SapIntegration> litOfSocarIntegration) {
        String data = "";
        String items = "";
        String res = "";
        BranchSetting brSetting = sessionBean.getLastBranchSetting();

        if (listOfSocar == null) {
            listOfSocar = new ArrayList<>();
        }
        listOfSocar.clear();
        listOfSocar.addAll(litOfSocarIntegration);

        if (brSetting.getErpIntegrationId() == 1) {//Socar a gönderilecek ise

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(',');
            decimalFormatSymbols.setGroupingSeparator('.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            for (SapIntegration si : listOfSocar) {
                items="";
                items = items + "<item>\n"
                        + "<BelgeNo>M" + si.getFinancingDocument().getId() + si.getBranchCode()
                        + sdf.format(si.getFinancingDocument().getDocumentDate())
                        + "</BelgeNo>\n"
                        + "<IslemTarihi>" + sdf2.format(si.getFinancingDocument().getDocumentDate()) + "</IslemTarihi>\n"
                        + "<HareketAciklama>" + si.getFinancingDocument().getDescription() + "</HareketAciklama>\n"
                        + "<SubeKodu>" + si.getBranchCode() + "</SubeKodu>\n"
                        + "<SubeId></SubeId>\n"
                        + "<Tutar>" + formatter.format(si.getFinancingDocument().getPrice()) + "</Tutar>\n"
                        + "<ParaBirimi>TRY</ParaBirimi>\n"
                        + "<KasaKodu>" + si.getSafe().getCode() + "</KasaKodu>\n"
                        + "<Company></Company>\n"
                        + "<ReferansBelge></ReferansBelge>\n"
                        + "<TayinNo></TayinNo>\n"
                        + "<TahsilatTipi>1</TahsilatTipi>\n"
                        + "<BankaAdi>" + si.getBankAccount().getName() + "</BankaAdi>\n"
                        + "<BankaHesapKodu>" + si.getBankAccount().getAccountNumber() + "</BankaHesapKodu>\n"
                        + "</item>\n";

                data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ıst=\"http://socar.com/MEPSAN/IstasyonSatisTahsilat\">\n"
                        + "    <x:Header/>\n"
                        + "    <x:Body>\n"
                        + "        <ıst:SatisTahsilatReq>\n"
                        + "            <KasaBanka>\n"
                        + items
                        + "            </KasaBanka>\n"
                        + "            <Masraf/>\n"
                        + "            <Tahsilat/>\n"
                        + "        </ıst:SatisTahsilatReq>\n"
                        + "    </x:Body>\n"
                        + "</x:Envelope>";

                res = sendSocarWebService(data, 2, si);
            }
            return res.equals("S");
        }
        return false;
    }

    /**
     * Bu metot gelen datayı socar web servisine gönderir.
     *
     * @param data
     * @param type 1: tahsilat,2:kasadan bankaya çıkış
     * @return
     */
    public String sendSocarWebService(String data, int type, SapIntegration sapSend) {
        Date begin = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        BranchSetting brSetting = sessionBean.getLastBranchSetting();
        List<SendSap> listResult = new ArrayList<>();
        String result = "";
        String documentNo = "";
        SendSap swr = new SendSap();
        swr.setFinancingDocumentId(sapSend.getFinancingDocument().getId());
        swr.setBranchCode(sapSend.getBranchCode());
        swr.setBranchId(sapSend.getBranchId());

        documentNo = "M" + sapSend.getFinancingDocument().getId() + sapSend.getBranchCode() + sdf.format(sapSend.getFinancingDocument().getDocumentDate());
        char[] beginChar = {'\u3010'};
        String beginc = "[";
        String boldBegin = beginc
                // Mathematical Sans-Serif 
                .replace("[", String.valueOf(beginChar));

        char[] endChar = {'\u3011'};
        String end = "]";
        String boldEnd = end
                // Mathematical Sans-Serif 
                .replace("]", String.valueOf(endChar));

        try {
            HttpPost httpPost = new HttpPost(brSetting.getErpUrl());
            org.apache.http.client.HttpClient httpClient = WebServiceClient.createHttpClient_AcceptsUntrustedCerts();

            byte[] encodedAuth = Base64.getEncoder().encode((brSetting.getErpUsername() + ":" + brSetting.getErpPassword()).getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            httpPost.addHeader("Authorization", authHeader);
            httpPost.addHeader("Content-Type", "text/xml; charset=utf-8");

            httpPost.addHeader("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
            RequestConfig rc = RequestConfig.DEFAULT;
            RequestConfig requestConfig
                    = RequestConfig
                            .copy(rc)
                            .setSocketTimeout(brSetting.getErpTimeout() * 1000)
                            .setConnectTimeout(brSetting.getErpTimeout() * 1000)
                            .setConnectionRequestTimeout(brSetting.getErpTimeout() * 1000)
                            .build();
            httpPost.setConfig(requestConfig);

            StringEntity stringEntity = new StringEntity(data, "UTF-8");

            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            BufferedReader br = null;
            try {
                System.out.println("------return code---------------" + returnCode);
                if (returnCode == 200) {
                    br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

                    StringBuilder sb = new StringBuilder();
                    String readLine;

                    while (((readLine = br.readLine()) != null)) {
                        byte[] b = readLine.getBytes();  // NOT UTF-8
                        sb.append(new String(b, "UTF-8"));

                    }

                    result = sb.toString(); 

                    System.out.println("------result*****" + result);

                    if (!result.equals("") && !result.isEmpty()) {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                        DocumentBuilder builder;

                        builder = factory.newDocumentBuilder();

                        InputSource inputSource = new InputSource(new StringReader(sb.toString()));

                        Document document = builder.parse(inputSource);

                        document.getDocumentElement().normalize();

                        Element root = null;

                        root = document.getDocumentElement();

                        NodeList returnHeader = root.getChildNodes();


                       NodeList returnList1 = document.getElementsByTagName("Return").item(0).getChildNodes();


                        for (int i = 0; i < returnList1.getLength(); i++) {

                            SendSap sap = new SendSap();
                            Element node = (Element) returnList1.item(i);
                            sap.setType(node.getElementsByTagName("Type").item(0).getTextContent());
                            sap.setErrorNumber(Integer.parseInt(node.getElementsByTagName("Number").item(0).getTextContent()));
                            sap.setMessage(node.getElementsByTagName("Message").item(0).getTextContent());
                            sap.setSendBeginDate(begin);
                            sap.setSendEndDate(new Date());
                            sap.setSendData(data);
                            listResult.add(sap);
                        }

                        String message = "";
                        int errorCount = 0;
                        for (SendSap sap : listResult) {
                            if (listResult.size() > 1) {
                                message = message + " - " + boldBegin + sap.getMessage() + boldEnd;
                            } else {
                                message = message + sap.getMessage();
                            }

                            if (sap.getType().equals("E")) {
                                errorCount++;
                            }
                            swr.setErrorNumber(sap.getErrorNumber());
                        }
                        if (!message.equals("") && listResult.size() > 1) {
                            message = message.substring(2, message.length());
                        }
                        if (errorCount > 0) {
                            swr.setIsSend(false);
                            swr.setType("E");
                        } else {
                            swr.setIsSend(true);
                            swr.setType("S");

                        }
                        swr.setMessage(message);
                        swr.setSendBeginDate(begin);
                        swr.setSendEndDate(new Date());
                        swr.setSendData(data);

                    } else {
                        swr.setType("E");
                        swr.setIsSend(false);
                        swr.setSendBeginDate(begin);
                        swr.setSendEndDate(new Date());
                        swr.setSendData(data);
                        swr.setMessage("İşlem Başarısız");

                    }

                } else {
                    swr.setType("E");
                    swr.setErrorNumber(0);
                    swr.setMessage("Http Status :" + returnCode);
                    swr.setSendBeginDate(begin);
                    swr.setSendEndDate(new Date());
                    swr.setSendData(data);
                    listResult.add(swr);
                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("catch---" + e.getMessage());
                swr.setType("E");
                swr.setErrorNumber(0);
                swr.setMessage("Error:" + e.getMessage());
                swr.setSendBeginDate(begin);
                swr.setSendEndDate(new Date());
                swr.setSendData(data);
                listResult.add(swr);

            } finally {
                httpPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("catch-1--" + ex.getMessage());

            swr.setType("E");
            swr.setErrorNumber(0);
            swr.setMessage("Error:" + ex.getMessage());
            swr.setSendBeginDate(begin);
            swr.setSendEndDate(new Date());
            swr.setSendData(data);
            listResult.add(swr);
        }

        //liste loglanacak
        webServiceLog(swr);
        if (swr.isIsSend()) {
            result = "S";
        } else {
            result = "E";
        }
        return result;
    }

    /**
     * Bu metot kayıtlar gönderildikten sonra log tablosuna kayıt atar.
     *
     *
     */
    public void webServiceLog(SendSap sapResult) {


        sapIntegrationDao.insertOrUpdateLog(sapResult);
    }

}
