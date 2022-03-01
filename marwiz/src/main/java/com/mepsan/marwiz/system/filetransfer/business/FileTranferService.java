/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 10:01:04
 */
package com.mepsan.marwiz.system.filetransfer.business;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.Document;
import com.mepsan.marwiz.general.model.wot.Line;
import com.mepsan.marwiz.system.filetransfer.dao.FileTransfer;
import com.mepsan.marwiz.system.filetransfer.dao.IFileTransferDao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FileTranferService implements IFileTranferService {

    @Autowired
    private IFileTransferDao fileTransferDao;

    public void setFileTransferDao(IFileTransferDao fileTransferDao) {
        this.fileTransferDao = fileTransferDao;
    }

    @Override
    public Document listOfSale(FileTransfer obj, int reportType, int extentionType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";
        if (reportType == 1) {
            where = where + " AND sl.processdate BETWEEN '" + dateFormat.format(obj.getBeginDate()) + "' AND '" + dateFormat.format(obj.getEndDate()) + "' ";
        } else if (reportType == 2) {
            where = where + " AND sl.shift_id = " + obj.getShift().getId() + " ";

        }
        return createSaleDetailDocument(fileTransferDao.listOfSale(where), extentionType);

    }

    /**
     * Bu Fonksiyon Miktarların Hepsinin Yuvarlama İşlemlerini Yapar
     *
     * @param value
     * @return
     */
    public Double rounding(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public Document createSaleDetailDocument(String sale, int extentionType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormatSpecial = new SimpleDateFormat("HHmmss");
        Document document = new Document(extentionType == 1 ? "SIGNATURE=GNDSALES.GTF" : "SIGNATURE=GNDSALES.GDF");
        Gson gson = new Gson();
        JsonArray arraySale = gson.fromJson(sale, JsonArray.class);
        
        System.out.println("----sale----"+sale);
        try {

            if (arraySale != null) {
                for (JsonElement jsonElement : arraySale) {
                    JsonArray payments = null;
                    JsonArray items = null;
                    boolean isNullPayment = true;
                    boolean isNullItem = true;
                    int totalLineCount = 5;
                    if (!jsonElement.getAsJsonObject().get("saleitem").isJsonNull()) {
                        items = jsonElement.getAsJsonObject().getAsJsonArray("saleitem").getAsJsonArray();
                        totalLineCount += items.size();
                        isNullItem = false;
                    }
                    if (!jsonElement.getAsJsonObject().get("salepayment").isJsonNull()) {
                        payments = jsonElement.getAsJsonObject().getAsJsonArray("salepayment").getAsJsonArray();
                        totalLineCount += payments.size();
                        isNullPayment = false;
                    }

                    Line line01 = new Line(260);

                    line01.addText(1, 2, "01");//Info
                    line01.addText(4, 6, jsonElement.getAsJsonObject().get("poscode").getAsString());//Pos Id -> 25.06.2019 Pos Code Alanı Basıldı.

                    Date date = null;
                    try {
                        date = sdf.parse(jsonElement.getAsJsonObject().get("slprocessdate").getAsString());
                    } catch (ParseException ex) {
                    }
                    line01.addDate(10, 14, date);//Tarih
                    line01.addText(24, 12, String.valueOf(jsonElement.getAsJsonObject().get("slid").getAsInt()));//Fiş Numarası

                    Date hours = null;
                    try {
                        hours = sdf.parse(jsonElement.getAsJsonObject().get("slprocessdate").getAsString());
                    } catch (ParseException ex) {
                        Logger.getLogger(FileTranferService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    line01.addText(36, 12, dateFormatSpecial.format(hours));//Saat Bilgisi
                    line01.addText(48, 6, String.valueOf(jsonElement.getAsJsonObject().get("slbranch_id")));//Mağaza No 
                    line01.addText(54, 8, String.valueOf(jsonElement.getAsJsonObject().get("sluserdata_id")));//Satışı Yapan Kasiyer Id Bilgisi
                    line01.addText(62, 1, jsonElement.getAsJsonObject().get("slis_return").getAsBoolean() ? "2" : (jsonElement.getAsJsonObject().get("slreceipt_id").getAsInt() > 0 ? "0" : "1"));//Belge Tipi 3:İade 0:Fiş 1:Fatura
                    line01.addText(63, 1, jsonElement.getAsJsonObject().get("slis_return").getAsBoolean() ? "1" : "0");//Belge Tipi İade İse 1 Değil İse 0 Set Edildi.
                    line01.addText(64, 10, jsonElement.getAsJsonObject().get("slreceipt_id").getAsInt() > 0 ? "0" : jsonElement.getAsJsonObject().get("invoiceserialno").getAsString());//Fatura Seri Ve numarası Girildi
                    line01.addText(74, 6, "0");//Kasiyer Başlangıç Zamanı 
                    line01.addText(80, 6, "0");//Kasiyer Bitiş Zamanı 
                    line01.addText(86, 6, String.valueOf(totalLineCount));//Toplam Satır Sayısı
                    line01.addDouble(92, 15, rounding(jsonElement.getAsJsonObject().get("sltotalmoney").getAsBigDecimal()));//KDV dahil genel toplam
                    line01.addDouble(107, 15, rounding(jsonElement.getAsJsonObject().get("sltotaltax").getAsBigDecimal()));//Belgenin KDV toplamı
                    line01.addDouble(122, 15, rounding(jsonElement.getAsJsonObject().get("sltotaldiscount").getAsBigDecimal()));//Belgeye uygulanan toplam indirim
                    line01.addDouble(137, 15, 0);//Satırlara uygulamış toplam indirim 
                    line01.addDouble(152, 15, 0);//Otomatik olarak yapılmış indirimler
                    line01.addDouble(167, 15, 0);//Müşteri için yapılmış indirimler
                    line01.addDouble(182, 15, 0);//Promosyon nedeni ile yapılmış indirimler
                    line01.addDouble(197, 15, 0);//Yuvarlama miktari (- ise aşağı)
                    line01.addText(212, 24, String.valueOf(jsonElement.getAsJsonObject().get("slaccount_id").getAsInt()));//Müşteri numarası
                    line01.addText(236, 1, "0");//Taksitli satış bilgisi mevcut
                    line01.addText(237, 24, "");//Custom Field
                    document.addLine(line01);

                    //System.out.println("slreceipt_id=" + jsonElement.getAsJsonObject().get("slreceipt_id").getAsInt());
                    int itemCount = 0;
                    if (!isNullItem) {
                        for (JsonElement item : items) {
                            //  System.out.println("slistock_id=" + item.getAsJsonObject().get("slistock_id").getAsInt());
                            itemCount += 1;
                            Line line02 = new Line(290);
                            line02.addText(1, 2, "02");//Info
                            line02.addText(4, 6, String.valueOf((itemCount)));//Satış hareketinin, belge içindeki sıra numarası (1'den başlar)
                            line02.addText(10, 24, item.getAsJsonObject().get("stckcode").getAsString().length() >= 14 ? item.getAsJsonObject().get("stckcode").getAsString().substring(0, 14) : item.getAsJsonObject().get("stckcode").getAsString());//Stoğun kodu
                            line02.addText(34, 1, "0");//Hareket tip
                            line02.addDouble(35, 6, 0);//Satıcı Kodu
                            //line02.addText(41, 2, "");//Kdv Referansı
                            line02.addText(43, 3, String.valueOf(item.getAsJsonObject().get("slitaxrate").getAsInt()));//Kdv Oranı

                            String unitSortName = String.valueOf(item.getAsJsonObject().get("untsortname").getAsString());
                            line02.addDouble(46, 15, rounding(item.getAsJsonObject().get("sliquantity").getAsBigDecimal().multiply(unitSortName.toLowerCase().equals("ad") ? BigDecimal.valueOf(1) : (unitSortName.toLowerCase().equals("kg") ? BigDecimal.valueOf(1000) : (unitSortName.toLowerCase().equals("mt") ? BigDecimal.valueOf(1) : (unitSortName.toLowerCase().equals("lt") ? BigDecimal.valueOf(1000) : unitSortName.toLowerCase().equals("m2") ? BigDecimal.valueOf(1) : (unitSortName.toLowerCase().equals("m3") ? BigDecimal.valueOf(1) : BigDecimal.valueOf(1))))))));//Miktar
                            line02.addText(61, 1, unitSortName.toLowerCase().equals("ad") ? "0" : (unitSortName.toLowerCase().equals("kg") ? "1" : (unitSortName.toLowerCase().equals("mt") ? "2" : (unitSortName.toLowerCase().equals("lt") ? "3" : unitSortName.toLowerCase().equals("m2") ? "4" : (unitSortName.toLowerCase().equals("m3") ? "5" : "0")))));//Birim
                            line02.addDouble(62, 15, rounding(item.getAsJsonObject().get("sliunitprice").getAsBigDecimal()));//Birim Fiyat
                            line02.addDouble(77, 15, rounding(item.getAsJsonObject().get("slitotalmoney").getAsBigDecimal()));//Toplam Fiyat

                            line02.addDouble(92, 15, rounding(item.getAsJsonObject().get("slitotaltax").getAsBigDecimal()));//Toplam Kdv
                            line02.addDouble(107, 15, rounding(item.getAsJsonObject().get("slidiscountprice").getAsBigDecimal()));//Satıra Uygulanan Toplam İndirim
                            line02.addDouble(122, 15, 0);//Kasiyer Tarafından Uygulanmış İndirim
                            line02.addDouble(137, 15, 0);//Otomatik Olarak Yapılmış İndirim
                            line02.addDouble(152, 15, 0);//Müşteri İçin Yapılmış İndirim
                            line02.addDouble(167, 15, 0);//Promosyon Nedeni İle Yapılmış İndirim
                            line02.addText(182, 1, "0");// Normal satış 
                            line02.addText(183, 24, item.getAsJsonObject().get("stckbarcode").getAsString());// BarKod Girildi
                            line02.addDouble(207, 15, item.getAsJsonObject().get("sliexchangerate").getAsBigDecimal().doubleValue() > 1.1 ? rounding(item.getAsJsonObject().get("sliexchangerate").getAsBigDecimal()) : 0);//Promosyon Nedeni İle Yapılmış İndirim
                            line02.addDouble(222, 2, 0);// Ödeme Tipi Referansı
                            line02.addDouble(224, 1, 0);// Normal Fiyat
                            line02.addDouble(225, 1, 0);// Anahtar Kullanılmadı
                            line02.addDouble(226, 1, 0);// Barkodlu Satış
                            line02.addDouble(227, 15, 0);//Kazanılan Puan Bilgisi
                            //  line02.addText(242, 24, "");//Extra Bilgi
                            line02.addDouble(266, 12, 0);//İade Nedeni Numarası
                            line02.addDouble(278, 12, 0);//Fiyat Değişim Nedeni

                            document.addLine(line02);
                        }
                    }
                    int paymentCount = 0;//
                    if (!isNullPayment) {//jsonElement.getAsJsonObject().getAsJsonArray("salepayment").isJsonNull()
                        for (JsonElement payment : payments) {
                            paymentCount += 1;
                            Line line03 = new Line(115);
                            line03.addText(1, 2, "03");//Info
                            line03.addText(4, 6, String.valueOf((paymentCount)));//Ödeme hareketinin, belge içindeki sıra numarası (1'den başlar)
                            line03.addText(10, 2, String.valueOf(payment.getAsJsonObject().get("slptype_id").getAsInt() == 17 ? 0 : (payment.getAsJsonObject().get("slptype_id").getAsInt() == 18 ? 1 : 3)));//Ödeme Tipi Nakit 
                            line03.addText(12, 1, "0");//Ödeme
                            line03.addText(13, 1, "1");//Normal Ödeme İşlemi Yapılmıştır
                            line03.addDouble(14, 15, rounding(payment.getAsJsonObject().get("slpprice").getAsBigDecimal()));//Ödeme Toplamı
                            line03.addDouble(29, 15, payment.getAsJsonObject().get("slpexchangerate").getAsBigDecimal().doubleValue() > 1 ? rounding(payment.getAsJsonObject().get("slpprice2").getAsBigDecimal()) : 0);//Döviz Toplamı
                            //  line03.addText(44, 24, "1");//Kredi Kartı Numarası
                            line03.addText(68, 1, "1");//Anahtar Kullanılmadı
                            line03.addText(69, 6, "0");//Taksit Sayısı
                            // line03.addText(75, 40, "");//Özel Veri
                            document.addLine(line03);

                        }

                        Line line05 = new Line(79);
                        line05.addText(1, 2, "05");//Info
                        line05.addText(4, 1, "1");
                        line05.addText(5, 6, "0");
                        line05.addText(11, 15, "0");
                        line05.addText(26, 24, "0");
                        line05.addText(50, 24, "0");
                        line05.addText(74, 4, "0");
                        line05.addText(78, 1, "0");
                        line05.addText(78, 1, "0");
                        document.addLine(line05);

                        Line line06 = new Line(306);
                        line06.addText(1, 2, "06");//Info
                        line06.addText(4, 15, "0");
                        line06.addText(19, 15, "0");
                        line06.addText(34, 15, "0");
                        line06.addText(49, 15, "0");
                        line06.addText(64, 15, "0");
                        line06.addText(79, 15, "0");
                        line06.addText(94, 15, "0");
                        line06.addText(109, 15, "0");
                        line06.addText(124, 15, "0");
                        line06.addText(139, 15, "0");
                        line06.addText(154, 15, "0");
                        line06.addText(169, 15, "0");
                        line06.addText(184, 15, "0");
                        line06.addText(199, 15, "0");
                        line06.addText(214, 15, "0");
                        line06.addText(229, 15, "0");
                        line06.addText(244, 15, "0");
                        line06.addText(259, 15, "0");
                        line06.addText(274, 15, "0");
                        line06.addText(289, 15, "0");
                        document.addLine(line06);

                    }

                }
            }
        } catch (Throwable e) {
            Logger.getLogger(FileTranferService.class.getName()).log(Level.SEVERE, null, e);
            document = null;
        }
        return document;
    }

    @Override
    public List<Shift> listOfShift(String where) {
        return fileTransferDao.listOfShift(where);
    }

}
