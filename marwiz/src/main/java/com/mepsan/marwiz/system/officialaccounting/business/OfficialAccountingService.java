/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.system.officialaccounting.dao.IOfficialAccountingDao;
import com.mepsan.marwiz.system.officialaccounting.dao.TotalCount;
import java.text.ParseException;
import java.util.Date;
import javax.sound.midi.Soundbank;
import org.json.JSONArray;

/**
 *
 * @author ali.kurt
 */
public class OfficialAccountingService implements IOfficialAccountingService {

    @Autowired
    private IOfficialAccountingDao officialAccountingDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    private int processType;

    public void setOfficialAccountingDao(IOfficialAccountingDao officialAccountingDao) {
        this.officialAccountingDao = officialAccountingDao;
    }

    @Override
    public List<OfficalAccounting> listOfIntegration(int processType, boolean isRetail, Date begin, Date end, BranchSetting selectedBranch) {

        this.processType = processType;
        List<OfficalAccounting> list = new ArrayList<>();
        List<OfficalAccounting> listTemp = new ArrayList<>();
        switch (processType) {
            case 1:
                list = officialAccountingDao.listOfAccount(selectedBranch);
                break;
            case 2:
                list = officialAccountingDao.listOfStock(selectedBranch);
                break;
            case 3:
                list = officialAccountingDao.listOfSafe(selectedBranch);
                break;
            case 4:
                list = officialAccountingDao.listOfBank(selectedBranch);
                break;
            case 5:
                list = officialAccountingDao.listOfWarehouse(selectedBranch);
                break;
            case 6:
                if (selectedBranch.isIsErpUseShift() && isRetail) {
                    listTemp = officialAccountingDao.listOfAccountMovement(1, isRetail, begin, end, selectedBranch);//finansman
                } else {
                    list = officialAccountingDao.listOfAccountMovement(1, isRetail, begin, end, selectedBranch);//finansman
                }

                try {
                    if (!listTemp.isEmpty() && selectedBranch.isIsErpUseShift() && isRetail) {
                        list = convertJsonToObject(listTemp);
                    }
                } catch (Exception e) {
                }

                break;
            case 7:
                list = officialAccountingDao.listOfAccountMovement(2, isRetail, begin, end, selectedBranch);//devir
                break;
            case 8:
                list = officialAccountingDao.listOfAccountMovement(3, isRetail, begin, end, selectedBranch);//çeksenet
                break;
            case 9:
                if (selectedBranch.isIsErpUseShift() && isRetail) {
                    listTemp = officialAccountingDao.listOfStockReceipt(1, isRetail, begin, end, selectedBranch);
                } else {
                    list = officialAccountingDao.listOfStockReceipt(1, isRetail, begin, end, selectedBranch);
                }

                try {
                    if (!listTemp.isEmpty() && selectedBranch.isIsErpUseShift() && isRetail) {
                        list = convertJsonToObject(listTemp);
                    }
                } catch (Exception e) {
                }

                break;
            case 10:
                list = officialAccountingDao.listOfStockReceipt(2, isRetail, begin, end, selectedBranch);
                break;
            case 11:
                list = officialAccountingDao.listOfStockReceipt(3, isRetail, begin, end, selectedBranch);
                break;
        }

        return list;
    }

    public List<OfficalAccounting> convertJsonToObject(List<OfficalAccounting> list) throws ParseException {

        List<OfficalAccounting> listResult = new ArrayList<>();
        for (OfficalAccounting oa : list) {
            if (!oa.getSendData().isEmpty()) {
                JsonParser jsonParser = new JsonParser();
                JsonObject json = (JsonObject) jsonParser.parse(oa.getSendData());
                String shiftNO = json.get("shiftno").getAsString();
                oa.setShiftNo(shiftNO);
            }
        }
        return list;
    }

    @Override
    public void sendDataIntegration(List<OfficalAccounting> list, BranchSetting branchSetting) {
        list.stream().map((accounting) -> {
            sendIntegration(accounting, branchSetting);
            return accounting;
        }).forEach((accounting) -> {
            officialAccountingDao.update(accounting, processType);//veritabanı güncelle
        });
    }

    /**
     * Bu metot gelen objeyi web servise gönderecek
     *
     * @param accounting
     */
    private void sendIntegration(OfficalAccounting accounting, BranchSetting branchSetting) {

        BranchSetting bs = branchSetting;

        System.out.println("-----BS url------"+bs.getErpUrl());
        System.out.println("-----BS erpintegrationid----"+bs.getErpIntegrationId());
        
        
        
        String response;
        boolean isSend = false;

        if (bs.getErpIntegrationId() == 2 || bs.getErpIntegrationId() == 3) {//Logo veya Netsis ise
            String url = bs.getErpUrl();
            switch (processType) {
                case 1:
                    url = url + "/carikart";
                    break;
                case 2:
                    url = url + "/stokkart";
                    break;
                case 3:
                    url = url + "/kasakart";
                    break;
                case 4:
                    url = url + "/bankakart";
                    break;
                case 5:
                    url = url + "/depokart";
                    break;
                case 6://finansman
                case 7://devir
                case 8://çeksenet
                    url = url + "/carifis";
                    break;
                case 9://fatura
                case 10://irsaliye
                case 11://depo fişi
                    url = url + "/stokfis";
                    break;
            }
            System.out.println("----URL----"+url);
            System.out.println("-----senddata----"+accounting.getSendData());
            //gönderme işlemi başlangıç
            WebServiceClient webServiceClient = new WebServiceClient();
            response = webServiceClient.requestJson(url, null, null, accounting.getSendData());
            
            System.out.println("---RESPONSE----"+response);
            System.out.println("Send Data: " + accounting.getSendData());
            if (response.trim().equals("0")) {
                isSend = true;
            }
            //gönderme işlemi bitiş
            Date sendDate = new Date();
            accounting.setIsSend(isSend);
            accounting.setResponse(getErrorMessage(response));
            accounting.setSendDate(sendDate);
            System.out.println("Response Data: " + getErrorMessage(response));
        }

    }

    @Override
    public TotalCount getTotalCounts(String branchList) {
        return officialAccountingDao.getTotalCounts(processType, branchList);
    }

    @Override
    public String getFinancingType(int typeId) {
        String tag = "";

        //Cari fiş ise
        if (processType == 6 || processType == 7 || processType == 8) {
            switch (typeId) {
                case 0:
                    tag = sessionBean.getLoc().getString("accounttransferreceipt");
                    break;
                case 1:
                    typeId = 47;
                    break;
                case 2:
                    typeId = 48;
                    break;
                case 3:
                    typeId = 73;
                    break;
                case 4:
                    typeId = 74;
                    break;
                case 5:
                    tag = sessionBean.getLoc().getString("givencheque");
                    break;
                case 6:
                    tag = sessionBean.getLoc().getString("receivecheque");
                    break;
                case 7:
                    typeId = 55;
                    break;
                case 8:
                    typeId = 56;
                    break;
                case 9:
                    typeId = 50;
                    break;
                case 10:
                    typeId = 49;
                    break;
                case 11:
                    tag = sessionBean.getLoc().getString("expensereceipt");
                    break;
                case 12:
                    tag = sessionBean.getLoc().getString("safetransferreceipt");
                    break;
                case 13:
                    tag = sessionBean.getLoc().getString("banktransferreceipt");
                    break;
                case 14:
                    tag = sessionBean.getLoc().getString("incomereceipt");
                    break;
                case 15:
                    typeId = 53;
                    break;
                case 16:
                    typeId = 54;
                    break;
                case 17:
                    tag = sessionBean.getLoc().getString("givenbill");
                    break;
                case 18:
                    tag = sessionBean.getLoc().getString("receivebill");
                    break;
                case 19:
                    typeId = 51;
                    break;
                case 20:
                    typeId = 52;
                    break;
                default:
                    break;
            }

            for (Type type : sessionBean.getTypes(20)) {
                if (type.getId() == typeId) {
                    return type.getNameMap().get(sessionBean.getUser().getLanguage().getId()).getName();
                }
            }
        } else {//Stok fiş ise
            switch (typeId) {
                case 6://toptan alım irsaliyesi
                    tag = sessionBean.getLoc().getString("purchasewaybill");
                    break;
                case 7://toptan satış irsaliyesi
                    tag = sessionBean.getLoc().getString("saleswaybill");
                    break;
                case 8://toptan alım iade irsaliyesi
                    tag = sessionBean.getLoc().getString("purchasereturnwaybill");
                    break;
                case 9://toptan satış iade irsaliyesi
                    tag = sessionBean.getLoc().getString("salereturnwaybill");
                    break;
                case 10://perakende alım faturası
                    tag = sessionBean.getLoc().getString("retail") + " " + sessionBean.getLoc().getString("purchaseinvoice");
                    break;
                case 11://perakende satış faturası
                    tag = sessionBean.getLoc().getString("retail") + " " + sessionBean.getLoc().getString("salesinvoice");
                    break;
                case 12://perakende alım iade faturası
                    tag = sessionBean.getLoc().getString("retail") + " " + sessionBean.getLoc().getString("purchasereturninvoice");
                    break;
                case 13://perakende satış iade faturası
                    tag = sessionBean.getLoc().getString("retail") + " " + sessionBean.getLoc().getString("saleretuninvoice");
                    break;
                case 14://toptan alım faturası
                    tag = sessionBean.getLoc().getString("purchaseinvoice");
                    break;
                case 15://toptan satış faturası
                    tag = sessionBean.getLoc().getString("salesinvoice");
                    break;
                case 16://toptan alım iade faturası
                    tag = sessionBean.getLoc().getString("purchasereturninvoice");
                    break;
                case 17://toptan satış iade faturası
                    tag = sessionBean.getLoc().getString("saleretuninvoice");
                    break;
                case 23://depo transfer giriş
                    tag = sessionBean.getLoc().getString("warehousetransferinput");
                    break;
                case 24://depo transfer çıkış
                    tag = sessionBean.getLoc().getString("warehousetransferoutput");
                    break;
                case 25://sayım giriş fişi
                    tag = sessionBean.getLoc().getString("stocktakinginputslip");
                    break;
                case 26://sayım çıkış fişi
                    tag = sessionBean.getLoc().getString("stocktakingoutputslip");
                    break;
                case 27://atık fişi
                    tag = sessionBean.getLoc().getString("wastereceipt");
                    break;
                default:
                    break;
            }
        }

        return tag;
    }

    public String getErrorMessage(String response) {
        String message = null;

        switch (response) {
            case "0":
                message = "İşlem başarılı";
                break;
            case "101":
                message = "/App_Data/Brika.ini dosyası bulunamadı";
                break;
            case "102":
                message = "SQL connection hatası. Sql server ayarları yanlış. Sunucu adı yada IP si hatalı olabilir";
                break;
            case "103":
                message = "Tabloya bağlanılamadı. Tablo adı hatalı olabilir";
                break;
            case "104":
                message = "Metin taşma hatası";
                break;
            case "105":
                message = "Sayısal taşma hatası";
                break;
            case "106":
                message = "SQL geçersiz yetkilendirme. Kullanıcı yok yada kullanıcının yazma yetkisi yok";
                break;
            case "107":
                message = "Null hatası doldurulması zorunlu bir alana null gelmesi";
                break;
            case "108":
                message = "SQL time out ";
                break;
            case "201":
                message = "Cari hesap kodu boş";
                break;
            case "202":
                message = "Unvan boş";
                break;
            case "251":
                message = "Stok kodu boş";
                break;
            case "252":
                message = "Barkod boş";
                break;
            case "253":
                message = "Stok adı boş";
                break;
            case "301":
                message = "Banka kodu boş";
                break;
            case "302":
                message = "Banka adı boş";
                break;
            case "303":
                message = "Banka hesap kodu boş";
                break;
            case "304":
                message = "Banka hesap adı boş";
                break;
            case "351":
                message = "Kasa kodu boş";
                break;
            case "352":
                message = "Kasa adı boş";
                break;
            case "401":
                message = "Depo kodu boş";
                break;
            case "402":
                message = "Depo adı boş";
                break;
            case "501":
                message = "Cari referansı sıfır";
                break;
            case "502":
                message = "Hatalı hareket tarihi";
                break;
            case "503":
                message = "Hatalı çek vade tarihi";
                break;
            case "504":
                message = "Tutar sıfır";
                break;
            case "505":
                message = "Cari kart yok";
                break;
            case "506":
                message = "BankaHesap ref sıfır(Cek,KK,Havale İşlemlerinde)";
                break;
            case "507":
                message = "KasaRef sıfır (Nakit İşlemlerde)";
                break;
            case "601":
                message = "Hatali İşlem tarihi";
                break;
            case "602":
                message = "Tutar sıfır";
                break;
            case "603":
                message = "Cari Referansı sıfır";
                break;
        }
        return message == null ? response : (response + " : " + message);
    }

    @Override
    public List<BranchSetting> findBranch() {
        return officialAccountingDao.findBranch();
    }

}
