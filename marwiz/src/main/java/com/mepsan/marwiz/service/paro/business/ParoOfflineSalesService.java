/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 09:07:55
 */
package com.mepsan.marwiz.service.paro.business;

import com.mepsan.marwiz.general.marketshift.dao.IMarketShiftDao;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.model.LogParo;
import com.mepsan.marwiz.service.paro.dao.IParoOfflineSalesDao;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ParoOfflineSalesService implements IParoOfflineSalesService {

    @Autowired
    IParoOfflineSalesDao paroOfflineSalesDao;

    private List<PointOfSale> listOfpointOfSale;

    private boolean isSendOfflineSale;

    public void setParoOfflineSalesDao(IParoOfflineSalesDao paroOfflineSalesDao) {
        this.paroOfflineSalesDao = paroOfflineSalesDao;
    }

    public List<PointOfSale> getListOfpointOfSale() {
        return listOfpointOfSale;
    }

    public void setListOfpointOfSale(List<PointOfSale> listOfpointOfSale) {
        this.listOfpointOfSale = listOfpointOfSale;
    }

    @Override
    public void sendSalesAsync() {
        System.out.println("##Paro Satışları Aktarılmaya Başlandı##");
        List<LogParo> listOfLog = paroOfflineSalesDao.listOfLog();
        if (!listOfLog.isEmpty()) {
            this.sendSales(listOfLog);
        }
    }

    @Override
    public void sendSales(List<LogParo> listOfLog) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        for (LogParo logParo : listOfLog) {
            processLogParo(logParo, simpleDateFormat);
        }
    }

    private boolean isNumeric(String str) {//
        return str.matches("-?\\d+(.\\d+)?");
    }

    /**
     * Paro Kaydının Offline İşlemlerini Gerçekleştirir Servise Gönderir.
     *
     * @param logParo
     */
    public void processLogParo(LogParo logParo, SimpleDateFormat simpleDateFormat) {
        try {
            LogParo responseLogParo = new LogParo();
            String decode = "";
            String sendData = "";
            if (logParo.getTypeId() == 3) {
                https://wstest2.paro.com.tr/prjWebServiceSSL2/WsAlisveris?invoke=alisverisBitti&trxNo=21042812433450015915&isyeriKod=2234&subeKod=8&yetkiliKod=1688&odemeStr=1%7C2%7C%20%7C10%2C0000&pauanKdvStr=&islemTip=10&param1=%7C1
                try {//Ödemler Offline Satışa Çevrildi.
                    String[] split = logParo.getSendData().split("param1=");
                    System.out.println(logParo.getSendData());
                    if (split.length > 1) {
                        decode = URLDecoder.decode(split[1], "UTF-8");
                        String replace = decode.replace("0", "1");
                        sendData = split[0] + "param1=" + URLEncoder.encode(replace, "UTF-8");

                    } else {
                        sendData = logParo.getSendData();
                    }

                    String[] splitTRXNO = sendData.split("trxNo=");
                    String[] splitIsyeriKod = sendData.split("&isyeriKod=");//transaction numarasını set edip gönderme işlemi yapar
                    if (splitTRXNO.length > 1 && splitIsyeriKod.length > 1) {
                        sendData = splitTRXNO[0] + "trxNo=" + logParo.getTransactionNo() + "&isyeriKod=" + splitIsyeriKod[1];
                    } else {
                        sendData = logParo.getSendData();
                    }

                } catch (UnsupportedEncodingException ex) {
                    sendData = logParo.getSendData();
                    Logger.getLogger(ParoOfflineSalesService.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (logParo.getTypeId() == 2 && logParo.isIsSuccess() == false) {//Satış Gönderilmemiş İse
//https://wstest2.paro.com.tr/prjWebServiceSSL2/WsAlisveris?invoke=alisverisBaslat&pcId=15915&kartNo=&ad=&soyad=&dogumTarihi=&cepTel=&isTel=&evTel=&yasakli=3&isyeriKod=2234&subeKod=8&yetkiliKod=1688&marka=&model=&urun=1%7CP%7C123456k%7C%20%7C1%7C10%2C00%7C18%7C1%7CF%7C1%7C-1.-1.-1.-1.-1.-1.-1.-1.-1.-1.-1%7C&islemTip=47&param1=20210428124109%7C+%7C8463642210428124109%7C+%7C+%7C+%7C0%7C1
                String[] split = logParo.getSendData().split("islemTip=");//Online Satış Ofline Satışa Dönüştürülür
                if (split.length > 1) {
                    //Opet test(Artık bu hali çıkmalı)
                    sendData = split[0] + "islemTip=47&param1=" + URLEncoder.encode("" + simpleDateFormat.format(logParo.getSendBeginDate()) + "| |" + logParo.getProvisionNo() + "| | | |" + (logParo.isIsQRCode() == true ? 1 : 0) + "|" + (logParo.isIsInvoice() == true ? 1 : 0)+"|"+logParo.getOrderId(), "UTF-8");
                    //Opet gerçek     
                    //sendData = split[0] + "islemTip=47&param1=" + URLEncoder.encode("" + simpleDateFormat.format(logParo.getSendBeginDate()) + "| | |" + logParo.getProvisionNo() + "| | | |", "UTF-8");

                } else {
                    sendData = logParo.getSendData();
                }
                System.out.println("Send Data" + sendData);
            } else if (logParo.getTypeId() == 2 && logParo.isIsSuccess() == true) {//Satış Gönderme İşlemi Başarılı Ama Satışa Ait Ödeme İşlemi Yok İse  Satışın İade İşlemi Yapılacak

                listOfpointOfSale = paroOfflineSalesDao.listPointOfSale(logParo.getBranchId());
                boolean isThereError = false;
                if (listOfpointOfSale != null) {
                    if (listOfpointOfSale.size() > 0) {
                        if (!isSendOfflineSale) {
                            WebServiceClient serviceClient = new WebServiceClient();
                            for (PointOfSale pointOfSale : listOfpointOfSale) {
                                isSendOfflineSale = true;
                                String request = serviceClient.requestJson("http://" + pointOfSale.getLocalIpAddress() + ":8081/offlineSendSales", null, null, "1");
                                try {
                                    JSONObject jSONObject = new JSONObject(request);
                                    isThereError = jSONObject.getBoolean("result") == true ? isThereError : true;
                                } catch (Exception exception) {
                                    isThereError = true;
                                    Logger.getLogger(ParoOfflineSalesService.class.getName()).log(Level.SEVERE, null, exception);
                                }
                            }

                        }
                    }
                }
                if (isThereError || paroOfflineSalesDao.isThereFinishedSale(logParo.getTransactionNo())) {
                    return;
                }

                /**
                 * https://wstest2.paro.com.tr/prjWebServiceSSL2/WsAlisveris?invoke=alisverisIptal&trxNo=19101015282200015915&isyeriKod=2234&subeKod=8&yetkiliKod=1688&islemTip=1&param1=
                 * https://wstest2.paro.com.tr/prjWebServiceSSL2/WsAlisveris?invoke=alisverisBaslat&isyeriKod=2234&subeKod=8&yetkiliKod=1688&
                 */
                String[] type = logParo.getSendData().split("=alisverisBaslat");//Online Satış Başlatma Ofline Satış Bitirmeye Dönüştürülür

                //İşyeri Kodu Alınır.
                String[] arrayIsyeriKod = logParo.getSendData().split("isyeriKod=");
                String isYeriKod = "";
                for (int i = 0; i < arrayIsyeriKod[1].length(); i++) {
                    if (arrayIsyeriKod[1].charAt(i) == '&') {
                        break;
                    }
                    isYeriKod += arrayIsyeriKod[1].charAt(i);
                }

                //Şube Kodu Alınır.
                String[] arraySubeKod = logParo.getSendData().split("subeKod=");
                String subeKod = "";
                for (int i = 0; i < arraySubeKod[1].length(); i++) {
                    if (arraySubeKod[1].charAt(i) == '&') {
                        break;
                    }
                    subeKod += arraySubeKod[1].charAt(i);
                }

                //Şube Kodu Alınır.
                String[] arrayYetkiliKod = logParo.getSendData().split("yetkiliKod=");
                String yetkiliKod = "";
                for (int i = 0; i < arrayYetkiliKod[1].length(); i++) {
                    if (arrayYetkiliKod[1].charAt(i) == '&') {
                        break;
                    }
                    yetkiliKod += arrayYetkiliKod[1].charAt(i);
                }

                if (type.length > 1 && arrayIsyeriKod.length > 1 && arraySubeKod.length > 1 && arrayYetkiliKod.length > 1) {
                    sendData = type[0] + "=alisverisIptal&trxNo=" + logParo.getTransactionNo() + "&isyeriKod=" + isYeriKod + "&subeKod=" + subeKod + "&yetkiliKod=" + yetkiliKod + "&islemTip=1&param1=";
                }
                System.out.println(logParo.getSendData());
            } else if (logParo.getTypeId() == 4) {
//https://wstest2.paro.com.tr/prjWebServiceSSL2/WsAlisveris?invoke=alisverisIptal&trxNo=21042812205449415915&isyeriKod=2234&subeKod=8&yetkiliKod=1688&islemTip=48&param1=
                String[] type = logParo.getSendData().split("=alisverisIptal");//Online Satış Başlatma Ofline Satış Bitirmeye Dönüştürülür

                //İşyeri Kodu Alınır.
                String[] arrayIsyeriKod = logParo.getSendData().split("isyeriKod=");
                String isYeriKod = "";
                for (int i = 0; i < arrayIsyeriKod[1].length(); i++) {
                    if (arrayIsyeriKod[1].charAt(i) == '&') {
                        break;
                    }
                    isYeriKod += arrayIsyeriKod[1].charAt(i);
                }

                //Şube Kodu Alınır.
                String[] arraySubeKod = logParo.getSendData().split("subeKod=");
                String subeKod = "";
                for (int i = 0; i < arraySubeKod[1].length(); i++) {
                    if (arraySubeKod[1].charAt(i) == '&') {
                        break;
                    }
                    subeKod += arraySubeKod[1].charAt(i);
                }

                //Şube Kodu Alınır.
                String[] arrayYetkiliKod = logParo.getSendData().split("yetkiliKod=");
                String yetkiliKod = "";
                for (int i = 0; i < arrayYetkiliKod[1].length(); i++) {
                    if (arrayYetkiliKod[1].charAt(i) == '&') {
                        break;
                    }
                    yetkiliKod += arrayYetkiliKod[1].charAt(i);
                }

                if (type.length > 1 && arrayIsyeriKod.length > 1 && arraySubeKod.length > 1 && arrayYetkiliKod.length > 1) {
                    sendData = type[0] + "=alisverisIptal&trxNo=" + logParo.getTransactionNo() + "&isyeriKod=" + isYeriKod + "&subeKod=" + subeKod + "&yetkiliKod=" + yetkiliKod + "&islemTip=48&param1=";
                }
            } else {
                sendData = logParo.getSendData();
            }
            //System.out.println("sendData=" + sendData);
            WebServiceClient serviceClient = new WebServiceClient();
            String resultMessage = serviceClient.requestGetMethod(sendData);
            if (resultMessage != null) {
                if (resultMessage.isEmpty()) {
                    responseLogParo.setIsSend(false);
                    responseLogParo.setIsSuccess(false);
                } else if (!resultMessage.equals("error")) {
                    responseLogParo.setId(logParo.getId());
                    responseLogParo.setRequestId(logParo.getRequestId());
                    responseLogParo.setPointOfSaleId(logParo.getPointOfSaleId());
                    responseLogParo.setProvisionNo(logParo.getProvisionNo());
                    responseLogParo.setErrorCode(logParo.getErrorCode());
                    responseLogParo.setErrorMessage(logParo.getErrorMessage());
                    responseLogParo.setIsSuccess(logParo.isIsSuccess());
                    responseLogParo.setErrorCount(logParo.getErrorCount());
                    responseLogParo.setIsSend(logParo.isIsSend());
                    responseLogParo.setSendData(sendData);
                    responseLogParo.setSendEndDate(new Date());
                    responseLogParo.setResponse(resultMessage);
                    responseLogParo.setBranchId(logParo.getBranchId());
                    responseLogParo.setCreatedId(logParo.getCreatedId());
                    responseLogParo.setSaleId(logParo.getSaleId());

                    LogParo parseResponsee = this.parseResponse(resultMessage, logParo.getTypeId());
                    responseLogParo.setIsSend(parseResponsee.isIsSend());
                    responseLogParo.setIsSuccess(parseResponsee.isIsSuccess());
                    responseLogParo.setErrorCode(parseResponsee.getErrorCode());
                    responseLogParo.setErrorMessage(parseResponsee.getErrorMessage());
                    responseLogParo.setTransactionNo(parseResponsee.getTransactionNo());
                    responseLogParo.setResponse(parseResponsee.getResponse());
                }

                if (logParo.getTypeId() == 2 && logParo.isIsSuccess() && responseLogParo.isIsSuccess() && (responseLogParo.getErrorCode() == 0 || responseLogParo.getErrorCode() == 203)) {//Ödemesi Olmayan Bir Satış İse Satış İptal Kaydı Ekler//-> Hata Kodu 203 Daha Önceden İptal Olduğunu Belirtir.
                    responseLogParo.setSendBeginDate(new Date());
                    responseLogParo.setTransactionNo(logParo.getTransactionNo());
                    responseLogParo.setTypeId(4);
                    paroOfflineSalesDao.createSaleLog(responseLogParo);
                } else if (logParo.getTypeId() == 4) {
                    responseLogParo.setTransactionNo(logParo.getTransactionNo());
                    responseLogParo.setProvisionNo(logParo.getProvisionNo());
                    responseLogParo.setTypeId(4);
                    paroOfflineSalesDao.updateSaleCancelLog(responseLogParo);
                } else if (!logParo.isIsSuccess()) {//Paro Loglarını Günceller

                    int id = paroOfflineSalesDao.updateSaleLog(responseLogParo);//type 3 bitir kaydını günceller
                    if (logParo.getTypeId() == 2 && responseLogParo.isIsSuccess()) {//Paro Loglarının Transaction Numarasını Günceller 
                        paroOfflineSalesDao.updateAllRequestLog(responseLogParo.getTransactionNo(), logParo.getRequestId());
                        int result = paroOfflineSalesDao.updateSale(responseLogParo.getTransactionNo(), logParo.getSaleId());
                        if (result > 0) {//Satışların merkeze gönderilmesi için json verilerinin oluşturulması sağlanır
                            try {
                                int jsonSaleId = paroOfflineSalesDao.createJsonSale(logParo);
                            } catch (Exception exception) {
                                Logger.getLogger(ParoOfflineSalesService.class.getName()).log(Level.SEVERE, null, exception);
                            }
                        }
                        LogParo logParoPayment = paroOfflineSalesDao.selectParoPayment(logParo.getRequestId());//Paro Satış Başlatma İşleminin Satış Bitir Logunu Bulur Satışı Bitirir
                        if (logParoPayment.getId() > 0) {
                            processLogParo(logParoPayment, simpleDateFormat);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            Logger.getLogger(ParoOfflineSalesService.class.getName()).log(Level.SEVERE, null, exception);
        }

    }

    @Override
    public LogParo parseResponse(String response, int typeId) {
        String resultString = "";
        LogParo logParo = new LogParo();
        logParo.setResponse(response);
        logParo.setIsSend(true);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(response));
            Document document = builder.parse(inputSource);
            resultString = document.getElementsByTagName("return").item(0).getTextContent();

            DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder2;
            int sonuc = 0;
            //Resonse İçerisinden Gelen Sonuç Çözümlenir.
            builder2 = factory2.newDocumentBuilder();
            InputSource inputSource2 = new InputSource(new StringReader(resultString));
            Document document2 = builder2.parse(inputSource2);
            sonuc = Integer.valueOf(document2.getElementsByTagName("SONUC").item(0).getTextContent());
            logParo.setErrorCode(Integer.valueOf(document2.getElementsByTagName("DURUM").item(0).getTextContent()));
            if (document2.getElementsByTagName("ACIKLAMA").getLength() > 0) {
                logParo.setErrorMessage(document2.getElementsByTagName("ACIKLAMA").item(0).getTextContent());
//
                if (logParo.getErrorCode() == 2182) {//Provizyon Numarası Daha Evvelden Gönderilmiştir.22.04.2021 Eklendi
                    //Gönderilen 7323337210422032645 provizyon numarası  daha önce kullanılmıştır. TrxNo: #21042203263700015915
                    String[] trxArray = logParo.getErrorMessage().toLowerCase().split("trxno:");
                    String trx = "";
                    for (int i = 0; i < trxArray[1].length(); i++) {
                        if (trxArray[1].charAt(i) != '#' && trxArray[1].charAt(i) != ' ') {
                            trx += trxArray[1].charAt(i);
                        }
                    }

                    logParo.setTransactionNo(trx);

                }

            }
            if (typeId == 2 && sonuc == 1) {//Satış İse TRXNO alanı alınır.
                try {
                    if (document2.getElementsByTagName("TRXNO").getLength() > 0) {
                        logParo.setTransactionNo(document2.getElementsByTagName("TRXNO").item(0).getTextContent());
                    }
                } catch (Exception exception) {
                    Logger.getLogger(ParoOfflineSalesService.class.getName()).log(Level.SEVERE, null, exception);
                }
            }
            if (sonuc > 0) {
                logParo.setIsSuccess(true);
            } else if (logParo.getErrorCode() == 2182 && typeId == 2) {//Provizyon Daha Evvelden Gönderilmiştir.
                logParo.setIsSuccess(true);
            } else if (logParo.getErrorCode() == 666 && typeId == 3) {//Bitirilmiş Alışverişi Bitirmeye Çalışıyorsunuz
                logParo.setIsSuccess(true);
            } else {
                logParo.setIsSuccess(false);
            }
        } catch (Exception exception) {
            logParo.setIsSuccess(false);
            Logger.getLogger(ParoOfflineSalesService.class.getName()).log(Level.SEVERE, null, exception);

        }
        return logParo;

    }

}
