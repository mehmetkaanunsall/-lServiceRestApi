/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:34:07
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.FuelShiftReport;
import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.IFuelShiftDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FuelShiftService implements IFuelShiftService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IFuelShiftDao fuelShiftDao;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setFuelShiftDao(IFuelShiftDao fuelShiftDao) {
        this.fuelShiftDao = fuelShiftDao;
    }

    @Override
    public String createWhere(FuelShiftReport obj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "AND shf.begindate >='" + dateFormat.format(obj.getReportBeginDate()) + "' AND shf.enddate <='" + dateFormat.format(obj.getReportEndDate()) + "'";
        return where;
    }

    @Override
    public int createShift(FuelShift fuelShift) {
        return fuelShiftDao.createShift(fuelShift);
    }

    @Override
    public List<FuelShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BranchSetting branchSetting) {
        return fuelShiftDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchSetting);
    }

    @Override
    public List<FuelShiftReport> totals(String where, BranchSetting branchSetting) {
        return fuelShiftDao.totals(where, branchSetting);
    }

    @Override
    public String jsonArrayIntegrationName(List<FuelShiftSales> listOfSaleStock, int processType) {
        JsonArray jsonArray = new JsonArray();
        for (FuelShiftSales obj : listOfSaleStock) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("fuelintegrationcode", obj.getStockCode());
            jsonObject.addProperty("attendantcode", obj.getAttendantCode());
            jsonObject.addProperty("pumpno", obj.getPumpno());
            jsonObject.addProperty("nozzleno", obj.getNozzleNo());
            jsonArray.add(jsonObject);
        }
        System.out.println("-jsonArrayIntegrationName-jsonArray.toString()=\n " + jsonArray.toString());
        return fuelShiftDao.findIntegrationName(jsonArray.toString(), processType);
    }

    /**
     * bu metot gelen xml tipindeki dosyadan akaryakıt vardiyasını Listeye
     * dönüştürür. Turpak içindir.
     *
     * @param inputStream
     * @param shiftNo
     * @return
     */
    @Override
    public List<FuelShiftSales> importFuelShiftFromXml(InputStream inputStream, String shiftNo, BranchSetting branchSetting) {

        int unitPriceDecimal = 2, totalDecimal = 2, amountDecimal = 2;
        List<FuelShiftSales> rows = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-9"));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(sb.toString().getBytes("iso-8859-9"));
            Document doc = builder.parse(input);

            NodeList globalNode = doc.getElementsByTagName("GlobalParams");
            if (globalNode.getLength() > 0) {
                if (globalNode.item(0).getChildNodes().item(3).getTextContent() != null && !globalNode.item(0).getChildNodes().item(3).getTextContent().trim().equals("")) {
                    unitPriceDecimal = Integer.parseInt(globalNode.item(0).getChildNodes().item(3).getTextContent());
                }
                if (globalNode.item(0).getChildNodes().item(4).getTextContent() != null && !globalNode.item(0).getChildNodes().item(4).getTextContent().trim().equals("")) {
                    amountDecimal = Integer.parseInt(globalNode.item(0).getChildNodes().item(4).getTextContent());
                }
                if (globalNode.item(0).getChildNodes().item(5).getTextContent() != null && !globalNode.item(0).getChildNodes().item(5).getTextContent().trim().equals("")) {
                    totalDecimal = Integer.parseInt(globalNode.item(0).getChildNodes().item(5).getTextContent());

                    System.out.println("----totalDecimal--" + totalDecimal);
                }
            }
            NodeList nList = doc.getElementsByTagName("Txn");

            for (int i = 0; i < nList.getLength(); i++) {
                if (branchSetting.getAutomationId() == 5) {
                    rows.add(getFuelShiftSalesToShell(nList.item(i), unitPriceDecimal, amountDecimal, totalDecimal, shiftNo, branchSetting));
                } else {
                    rows.add(getFuelShiftSales(nList.item(i), unitPriceDecimal, amountDecimal, totalDecimal, shiftNo, branchSetting));
                }
            }

        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rows;
    }

    private FuelShiftSales getFuelShiftSales(Node node, int upDec, int amDec, int totDec, String shiftNo, BranchSetting branchSetting) {

        System.out.println("----totDEC---" + totDec);

        FuelShiftSales sales = new FuelShiftSales();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            org.w3c.dom.Element element = (org.w3c.dom.Element) node;

            try {
                sales.setProcessDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(getTagValue("DateTime", element)));
            } catch (ParseException ex) {
                Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
            }
            sales.getFuelShift().setShiftNo(shiftNo);
            sales.setNozzleNo(getTagValue("NozzleNr", element));
            sales.setPumpno(getTagValue("PumpNr", element));
            sales.setAttendantCode(getTagValue("ECRPlate", element).trim().equals(getTagValue("Plate", element).trim()) ? "000000" : getTagValue("Plate", element).trim());//türpak için entegrasyon koduna pompacı adı girilecek
            sales.setStockCode(getTagValue("FuelType", element));
            sales.setAccountCode(Integer.parseInt(getTagValue("TxnType", element)) == 3 ? "0000000000MRWZ3" : Integer.parseInt(getTagValue("TxnType", element)) == 4 ? "0000000000MRWZ4" : getTagValue("FleetCode", element));//Otobil için sabit MRWZ03 - Otobilim için MRWZ04 gönderildi.
            sales.setLiter(new BigDecimal(getTagValue("Amount", element)).movePointLeft(amDec).setScale(amDec, BigDecimal.ROUND_HALF_UP));
            sales.setPrice(new BigDecimal(getTagValue("UnitPrice", element)).movePointLeft(upDec).setScale(upDec, BigDecimal.ROUND_HALF_UP));
            sales.setTotalMoney(new BigDecimal(getTagValue("Total", element)).movePointLeft(totDec).setScale(totDec, BigDecimal.ROUND_HALF_UP));
            sales.setPlate(getTagValue("ECRPlate", element));
            sales.setAttendant(getTagValue("ECRPlate", element).equals(getTagValue("Plate", element)) ? "" : getTagValue("Plate", element));

            if (branchSetting.getAutomationTestKeyword() != null && getTagValue("Plate", element).trim().equals(branchSetting.getAutomationTestKeyword().trim())) {//Test Satışı
                if (branchSetting.getAutomationTestKeyword() != null && !branchSetting.getAutomationTestKeyword().trim().equals("")) {
                    sales.setSalteType(6);
                } else {
                    sales.setSalteType(Integer.parseInt(getTagValue("TxnType", element)));
                }
            } else {
                sales.setSalteType(Integer.parseInt(getTagValue("TxnType", element)));
            }

            if (getTagValue("FleetCode", element).equals("M-ODEM")) {// Mobil Ödeme
                sales.setSalteType(99);
            }

            sales.setPaymentType(Integer.parseInt(getTagValue("PaymentType", element)));
            sales.setDiscountTotal(new BigDecimal(Double.parseDouble(getTagValue("DiscountAmount", element).replace(",", "."))).setScale(2, BigDecimal.ROUND_HALF_UP));
            sales.setReceiptNo(getTagValue("ReceiptNr", element));

        }

        return sales;
    }

    private String getTagValue(String tag, org.w3c.dom.Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        if (node != null) {
            return node.getNodeValue();
        } else {
            return "";
        }
    }

    /*Türpak Shell Entegrasyonu İçin veriyi okur */
    private FuelShiftSales getFuelShiftSalesToShell(Node node, int upDec, int amDec, int totDec, String shiftNo, BranchSetting branchSetting) {

        FuelShiftSales sales = new FuelShiftSales();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            org.w3c.dom.Element element = (org.w3c.dom.Element) node;

            try {
                sales.setProcessDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(getTagValue("DateTime", element)));
            } catch (ParseException ex) {
                Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
            }
            sales.getFuelShift().setShiftNo(shiftNo);
            sales.setNozzleNo(getTagValue("NozzleNr", element));
            sales.setPumpno(getTagValue("PumpNr", element));
            sales.setAttendantCode(getTagValue("ECRPlate", element).trim().equals(getTagValue("Plate", element).trim()) ? "000000" : getTagValue("Plate", element).trim());//türpak için entegrasyon koduna pompacı adı girilecek
            sales.setStockCode(getTagValue("FuelType", element));
            sales.setAccountCode(getTagValue("FleetCode", element));

            sales.setPrice(new BigDecimal(getTagValue("FullUnitPrice", element)).movePointLeft(upDec).setScale(upDec, BigDecimal.ROUND_HALF_UP));
            sales.setPlate(getTagValue("ECRPlate", element));
            sales.setAttendant(getTagValue("ECRPlate", element).equals(getTagValue("Plate", element)) ? "" : getTagValue("Plate", element));

            sales.setSalteType(Integer.parseInt(getTagValue("TxnType", element)));

            sales.setPaymentType(Integer.parseInt(getTagValue("PaymentType", element)));
            sales.setDiscountTotal(new BigDecimal(Double.parseDouble(getTagValue("DiscountAmount", element).replace(",", "."))).setScale(2, BigDecimal.ROUND_HALF_UP));
            sales.setReceiptNo(getTagValue("ReceiptNr", element));
            if ((sales.getSalteType() == 1 && sales.getFuelCardType() == 1) || (sales.getSalteType() == 1 && sales.getFuelCardType() == 4)
                    || (sales.getSalteType() == 1 && sales.getFuelCardType() == 6) || (sales.getSalteType() == 1 && sales.getFuelCardType() == 8)) {
                sales.setTotalMoney(new BigDecimal(getTagValue("Total", element)).movePointLeft(totDec).setScale(totDec, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(Double.parseDouble(getTagValue("Redemption", element).replace(",", "."))).setScale(2, BigDecimal.ROUND_HALF_UP)));
                sales.setLiter((new BigDecimal(getTagValue("Total", element)).movePointLeft(totDec).setScale(totDec, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(Double.parseDouble(getTagValue("Redemption", element).replace(",", "."))).setScale(2, BigDecimal.ROUND_HALF_UP))).divide(new BigDecimal(getTagValue("FullUnitPrice", element)).movePointLeft(upDec).setScale(upDec, BigDecimal.ROUND_HALF_UP), 4, RoundingMode.HALF_EVEN));
            } else {
                sales.setTotalMoney(new BigDecimal(getTagValue("Total", element)).movePointLeft(totDec).setScale(totDec, BigDecimal.ROUND_HALF_UP));
                sales.setLiter(new BigDecimal(getTagValue("Amount", element)).movePointLeft(amDec).setScale(amDec, BigDecimal.ROUND_HALF_UP));
            }

            sales.setFuelCardType(Integer.parseInt(getTagValue("LoyaltyCardType", element)));
            sales.setRedemption(new BigDecimal(Double.parseDouble(getTagValue("Redemption", element).replace(",", "."))).setScale(2, BigDecimal.ROUND_HALF_UP));

        }

        return sales;
    }

    @Override
    public List<FuelShiftSales> importFuelShiftFromAsis(InputStream inputStream, String shiftNo) {
        String row = "";
        FuelShiftSales rowOfList;
        List<FuelShiftSales> rows = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-9"));

            if (inputStream != null) {
                reader.readLine();
                reader.readLine();
                while ((row = reader.readLine()) != null) {

                    rowOfList = new FuelShiftSales();
                    rowOfList.getFuelShift().setShiftNo(shiftNo);
                    rowOfList.setProcessDate(formatter.parse(row.substring(0, 10) + " " + row.substring(11, 19)));
                    rowOfList.setNozzleNo(row.substring(101, 102).replaceFirst("^0+(?!$)", ""));
                    rowOfList.setPumpno(row.substring(103, 105).replaceAll("\\s", "").replaceFirst("^0+(?!$)", ""));
                    rowOfList.setStockName(row.substring(68, 78).replaceAll("\\s", ""));
                    rowOfList.setStockCode((row.substring(68, 78)).trim());
                    rowOfList.setPrice(new BigDecimal(row.substring(86, 90).trim()));
                    if (rowOfList.getPrice() != null) {
                        rowOfList.setPrice(rowOfList.getPrice().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
                    }
                    rowOfList.setTotalMoney(new BigDecimal(row.substring(91, 99).trim()));
                    if (rowOfList.getTotalMoney() != null) {
                        rowOfList.setTotalMoney(rowOfList.getTotalMoney().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
                    }
                    rowOfList.setLiter(new BigDecimal(row.substring(80, 85).trim()));
                    if (rowOfList.getLiter()!= null) {
                        rowOfList.setLiter(rowOfList.getLiter().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
                    }
                    rowOfList.setAccountCode((row.substring(51, 57).trim().replaceAll("\\s", "")));
                    rowOfList.setSalteType(Integer.parseInt(rowOfList.getAccountCode().trim().equals("C0000") ? "1" : "2"));
                    rowOfList.setAttendant(row.substring(51, 57).trim().equals("C0000") ? row.substring(58, 67).trim() : "");
                    rowOfList.setAttendantCode(row.substring(51, 57).trim().equals("C0000") ? row.substring(58, 67).trim() : "000000");
                    rowOfList.setPlate(row.substring(51, 57).trim().equals("C0000") ? "" : row.substring(58, 67).trim());
                    rowOfList.setDiscountTotal(BigDecimal.valueOf(0));
                    rowOfList.setReceiptNo(row.substring(106, 112));
                    rows.add(rowOfList);

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (Throwable ignore) {
            }
        }

        return rows;
    }

    @Override
    public List<FuelShiftSales> importFuelShiftFromTxt(InputStream inputStream, String shiftNo) {

        String row = "";
        FuelShiftSales rowOfList;
        List<FuelShiftSales> rows = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-9"));
            if (inputStream != null) {

                while ((row = reader.readLine()) != null) {

                    rowOfList = new FuelShiftSales();
                    rowOfList.getFuelShift().setShiftNo(shiftNo);
                    rowOfList.setId(Integer.parseInt(row.substring(0, 6).replaceFirst("^0+(?!$)", "")));
                    rowOfList.setProcessDate(formatter.parse(row.substring(7, 17) + " " + row.substring(18, 26)));
                    rowOfList.setPumpno(row.substring(27, 29).replaceFirst("^0+(?!$)", ""));
                    rowOfList.setNozzleNo(row.substring(30, 32).replaceFirst("^0+(?!$)", ""));
                    rowOfList.setAccountCode(row.substring(33, 48));
                    rowOfList.setStockCode(row.substring(49, 53));
                    rowOfList.setStockName(row.substring(54, 66));
                    rowOfList.setLiter(new BigDecimal(row.substring(67, 79).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setPrice(new BigDecimal(row.substring(80, 88).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setTotalMoney(new BigDecimal(row.substring(89, 97).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setPlate(row.substring(98, 108).trim());
                    rowOfList.setAttendantCode(row.substring(109, 115));
                    rowOfList.setAttendant(row.substring(116, 140));
                    rowOfList.setSalteType(Integer.parseInt(row.substring(141, 143)));
                    rowOfList.setPaymentType(Integer.parseInt(row.substring(144, 146)));
                    rowOfList.setDiscountTotal(new BigDecimal(row.substring(147, 155).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setReceiptNo(row.substring(156, 162));
                    rows.add(rowOfList);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (Throwable ignore) {
            }
        }

        return rows;
    }

    @Override
    public List<FuelShiftSales> importFuelShiftFromTxtForStawiz(InputStream inputStream, String shiftNo) {

        String row = "";
        FuelShiftSales rowOfList;
        List<FuelShiftSales> rows = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-9"));
            if (inputStream != null) {

                while ((row = reader.readLine()) != null) {

                    rowOfList = new FuelShiftSales();
                    rowOfList.getFuelShift().setShiftNo(shiftNo);
                    rowOfList.setId(Integer.parseInt(row.substring(0, 6).replaceFirst("^0+(?!$)", "")));
                    rowOfList.setProcessDate(formatter.parse(row.substring(7, 17) + " " + row.substring(18, 26)));
                    rowOfList.setPumpno(row.substring(27, 29).replaceFirst("^0+(?!$)", ""));
                    rowOfList.setNozzleNo(row.substring(30, 32).replaceFirst("^0+(?!$)", ""));
                    rowOfList.setAccountCode(row.substring(33, 39).trim());
                    rowOfList.setStockCode(row.substring(40, 52).trim());
                    rowOfList.setStockName(row.substring(40, 52).trim());
                    rowOfList.setLiter(new BigDecimal(row.substring(53, 65).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setPrice(new BigDecimal(row.substring(66, 74).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setTotalMoney(new BigDecimal(row.substring(75, 83).replaceAll("\\s", "").replaceAll("0*$", "").replaceAll("\\.$", "").replaceFirst("^0+(?!$)", "")));
                    rowOfList.setPlate(row.substring(84, 94).trim());
                    rowOfList.setAttendantCode(row.substring(95, 119).trim().equals("") || row.substring(95, 119).trim() == null ? "000000000000000000000000" : row.substring(95, 119).trim());
                    rowOfList.setAttendant(row.substring(95, 119).trim().equals("") || row.substring(95, 119).trim() == null ? "000000000000000000000000" : row.substring(95, 119).trim());
                    rowOfList.setSalteType(Integer.parseInt(rowOfList.getAccountCode().trim().equals("000000") ? "0" : rowOfList.getAccountCode().trim().equals("000001") ? "1" : "2"));
                    rowOfList.setPaymentType(Integer.parseInt(rowOfList.getAccountCode().trim().equals("000000") ? "0" : "2"));
                    rowOfList.setDiscountTotal(BigDecimal.valueOf(0));
                    rowOfList.setReceiptNo("0");
                    rows.add(rowOfList);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(FuelShiftService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (Throwable ignore) {
            }
        }

        return rows;
    }

    @Override
    public FuelShift insertShiftAndShiftSales(List<FuelShiftSales> shiftSales) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        HashMap<String, List<FuelShiftSales>> hashMap = new HashMap<String, List<FuelShiftSales>>();
        for (FuelShiftSales s : shiftSales) {
            if (!hashMap.containsKey(s.getFuelShift().getShiftNo())) {
                List<FuelShiftSales> list = new ArrayList<>();
                list.add(s);

                hashMap.put(s.getFuelShift().getShiftNo(), list);
            } else {
                hashMap.get(s.getFuelShift().getShiftNo()).add(s);
            }
        }

        for (Map.Entry<String, List<FuelShiftSales>> entry : hashMap.entrySet()) {
            JsonObject jo = new JsonObject();
            jo.addProperty("shiftno", entry.getKey());
            jo.addProperty("begindate", !entry.getValue().isEmpty() ? sdf.format(entry.getValue().get(0).getProcessDate()) : sdf.format(new Date()));
            jo.addProperty("enddate", !entry.getValue().isEmpty() ? sdf.format(entry.getValue().get(entry.getValue().size() - 1).getProcessDate()) : sdf.format(new Date()));

            JsonArray jsonArrayDetail = new JsonArray();
            for (FuelShiftSales f : entry.getValue()) {
                jsonObject = new JsonObject();

                jsonObject.addProperty("pumpno", f.getPumpno());
                jsonObject.addProperty("nozzleno", f.getNozzleNo());
                jsonObject.addProperty("stockname", f.getStockName());
                jsonObject.addProperty("liter", f.getLiter());
                jsonObject.addProperty("price", f.getPrice());
                jsonObject.addProperty("discounttotal", f.getDiscountTotal());
                jsonObject.addProperty("totalmoney", f.getTotalMoney());
                jsonObject.addProperty("plate", f.getPlate());
                jsonObject.addProperty("attendant", f.getAttendant());
                jsonObject.addProperty("saletype", f.getSalteType());
                jsonObject.addProperty("cardtype", f.getFuelCardType());
                jsonObject.addProperty("paymenttype", f.getPaymentType());
                jsonObject.addProperty("attendantcode", f.getAttendantCode());
                jsonObject.addProperty("stockcode", f.getStockCode());
                jsonObject.addProperty("accountcode", f.getAccountCode());
                jsonObject.addProperty("processdate", f.getProcessDate().toString());
                jsonObject.addProperty("receiptno", f.getReceiptNo());
                jsonObject.addProperty("redemption", f.getRedemption());
                jsonArrayDetail.add(jsonObject);
            }
            jo.add("shiftdetail", jsonArrayDetail);
            jsonArray.add(jo);

        }

        return fuelShiftDao.insertShiftAndShiftSales(jsonArray.toString());
    }

    @Override
    public List<FuelShiftSales> findAttendantSales(FuelShift fuelShift, BranchSetting branchSetting) {
        return fuelShiftDao.findAttendantSales(fuelShift, branchSetting);
    }

    @Override
    public List<FuelShiftSales> findStockNameSales(FuelShift fuelShift, BranchSetting branchSetting) {
        return fuelShiftDao.findStockNameSales(fuelShift, branchSetting);
    }

    @Override
    public List<FuelShiftSales> findSaleTypeSales(FuelShift fuelShift, BranchSetting branchSetting) {
        return fuelShiftDao.findSaleTypeSales(fuelShift, branchSetting);
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList, FuelShiftReport fuelShiftReport, BranchSetting branchSetting, List<FuelShiftReport> listOfTotal) {

        List<Boolean> tempTogList = new ArrayList<>();

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = fuelShiftDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftDao.exportData(where, branchSetting));
            rs = prep.executeQuery();

            int tempCount = 0;

            tempTogList.addAll(toogleList);
            tempTogList.set(tempTogList.size() - 1, Boolean.FALSE);
            tempTogList.set(tempTogList.size() - 2, Boolean.FALSE);

            for (Boolean b : tempTogList) {
                if (!b) {
                    tempCount++;
                }
            }
            if (tempCount == tempTogList.size()) {
                tempTogList.set(tempTogList.size() - 1, true);
                tempTogList.set(tempTogList.size() - 2, true);
            }

            PdfDocument pdfDocument = StaticMethods.preparePdf(tempTogList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("fuelshiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShiftReport.getReportBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShiftReport.getReportEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("frmFuelShiftReport:dtbFuelShiftReport", tempTogList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            String[] colums = new String[]{"shfbegindate", "shfenddate", "shfshiftno", "ssltotalamount", "ssltotalprice", "iemincomeprices", "shpcreditcardpaymentprice",
                "iemexpenseprices", "cashpaymentprice", "deficitsurplus", "paropaymentprice", "ttspaymentprice", "fuelcardpaymentprice", "presentpaymentprice", "dkvpaymentprice",
                "utapaymentprice", "salecount"};
            String[] extension = new String[]{"HH:mm:ss", "HH:mm:ss", "", "LT", sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0),
                sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0),
                sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), ""};

            if (tempCount != tempTogList.size()) {
                while (rs.next()) {
                    StaticMethods.pdfAddCell(pdfDocument, rs, tempTogList, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                }
            }

            //Alt Toplam
            if (!listOfTotal.isEmpty()) {
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                if (toogleList.get(0)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getTotalSalesAmount()) + " LT", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getTotalSalesPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getIncomePrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getCreditCardPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getExpensePrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getCashPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getDeficitSurplusPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getParoPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getTtsPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getFuelCardPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getPresentPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getDkvPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(15)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getUtaPaymentPrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(16)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.get(0).getNumberofVehicle()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("fuelshiftreport"));
        } catch (DocumentException | SQLException e) {
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }

    }

    @Override
    public void exportExcel(String where, List<Boolean> toogleList, FuelShiftReport fuelShiftReport, BranchSetting branchSetting, List<FuelShiftReport> listOfTotal) {
        int tempCount = 0;
        List<Boolean> tempTogList = new ArrayList<>();

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        tempTogList.addAll(toogleList);

        tempTogList.set(tempTogList.size() - 1, Boolean.FALSE);
        tempTogList.set(tempTogList.size() - 2, Boolean.FALSE);

        for (Boolean b : tempTogList) {
            if (!b) {
                tempCount++;
            }
        }
        if (tempCount == tempTogList.size()) {
            tempTogList.set(tempTogList.size() - 1, true);
            tempTogList.set(tempTogList.size() - 2, true);
        }

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + "HH:mm:ss");
        try {
            connection = fuelShiftDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftDao.exportData(where, branchSetting));
            rs = prep.executeQuery();
            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("fuelshiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startDate = excelDocument.getSheet().createRow(jRow++);
            startDate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShiftReport.getReportBeginDate()));

            SXSSFRow endDate = excelDocument.getSheet().createRow(jRow++);
            endDate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShiftReport.getReportEndDate()));

            SXSSFRow empty1 = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmFuelShiftReport:dtbFuelShiftReport", tempTogList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            String[] colums = new String[]{"shfbegindate", "shfenddate", "shfshiftno", "ssltotalamount", "ssltotalprice", "iemincomeprices", "shpcreditcardpaymentprice",
                "iemexpenseprices", "cashpaymentprice", "deficitsurplus", "paropaymentprice", "ttspaymentprice", "fuelcardpaymentprice", "presentpaymentprice", "dkvpaymentprice",
                "utapaymentprice", "salecount"};
            String[] extension = new String[]{"HH:mm:ss", "HH:mm:ss", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

            if (tempCount != tempTogList.size()) {
                while (rs.next()) {
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    StaticMethods.excelAddCell(row, rs, tempTogList, colums, excelDocument.getDateFormatStyle(), excelDocument.getWorkbook().getCreationHelper(), sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                }
            }
            if (!listOfTotal.isEmpty()) {
                int f = 0;

                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                SXSSFRow rowf = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    SXSSFCell e1 = rowf.createCell((short) f++);
                    e1.setCellValue("");
                    e1.setCellStyle(cellStyle1);
                }
                if (toogleList.get(1)) {
                    SXSSFCell e2 = rowf.createCell((short) f++);
                    e2.setCellValue("");
                    e2.setCellStyle(cellStyle1);
                }
                if (toogleList.get(2)) {
                    SXSSFCell e3 = rowf.createCell((short) f++);
                    e3.setCellValue("");
                    e3.setCellStyle(cellStyle1);
                }
                if (toogleList.get(3)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getTotalSalesAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(4)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getTotalSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(5)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getIncomePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(6)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getCreditCardPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(7)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getExpensePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(8)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getCashPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(9)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getDeficitSurplusPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(10)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getParoPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(11)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getTtsPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(12)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getFuelCardPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(13)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getPresentPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(14)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getDkvPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(15)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(StaticMethods.round(listOfTotal.get(0).getUtaPaymentPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
                if (toogleList.get(16)) {
                    SXSSFCell warehouseStartQuantity = rowf.createCell((short) f++);
                    warehouseStartQuantity.setCellValue(listOfTotal.get(0).getNumberofVehicle().doubleValue());
                    warehouseStartQuantity.setCellStyle(cellStyle1);
                }
            }

            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("fuelshiftreport"));
        } catch (Exception e) {
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
            }
        }
    }

    @Override
    public String exportPrinter(String where, List<Boolean> toogleList, FuelShiftReport fuelShiftReport, BranchSetting branchSetting, List<FuelShiftReport> listOfTotal) {
        List<Boolean> tempTogList = new ArrayList<>();

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        tempTogList.addAll(toogleList);

        int tempCount = 0;

        tempTogList.set(tempTogList.size() - 1, Boolean.FALSE);
        tempTogList.set(tempTogList.size() - 2, Boolean.FALSE);

        for (Boolean b : tempTogList) {
            if (!b) {
                tempCount++;
            }
        }
        if (tempCount == tempTogList.size()) {
            tempTogList.set(tempTogList.size() - 1, true);
            tempTogList.set(tempTogList.size() - 2, true);
        }
        StringBuilder sb = new StringBuilder();

        try {
            connection = fuelShiftDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftDao.exportData(where, branchSetting));
            rs = prep.executeQuery();
            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShiftReport.getReportBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShiftReport.getReportEndDate())).append(" </div> ");
            sb.append(" <style>"
                    + "        #printerDiv table {"
                    + "            font-family: arial, sans-serif;"
                    + "            border-collapse: collapse;"
                    + "            width: 100%;"
                    + "        }"
                    + "        #printerDiv table tr td, #printerDiv table tr th {"
                    + "            border: 1px solid #dddddd;"
                    + "            text-align: left;"
                    + "            padding: 7px;"
                    + "        }"
                    + "   @page { size: landscape; }"
                    + "    </style> <table>");
            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");
            StaticMethods.createHeaderPrint("frmFuelShiftReport:dtbFuelShiftReport", tempTogList, "headerBlack", sb);

            String[] colums = new String[]{"shfbegindate", "shfenddate", "shfshiftno", "ssltotalamount", "ssltotalprice", "iemincomeprices", "shpcreditcardpaymentprice",
                "iemexpenseprices", "cashpaymentprice", "deficitsurplus", "paropaymentprice", "ttspaymentprice", "fuelcardpaymentprice", "presentpaymentprice", "dkvpaymentprice",
                "utapaymentprice", "salecount"};
            String[] extension = new String[]{"HH:mm:ss", "HH:mm:ss", "", "LT", sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0),
                sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0),
                sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), ""};

            if (tempCount != tempTogList.size()) {
                while (rs.next()) {
                    sb.append(" <tr> ");
                    StaticMethods.printAddCell(sb, rs, tempTogList, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                    sb.append(" </tr> ");
                }
            }

            if (!listOfTotal.isEmpty()) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getTotalSalesAmount())).append(" LT ").append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getTotalSalesPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getIncomePrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getCreditCardPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getExpensePrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getCashPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getDeficitSurplusPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getParoPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getTtsPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getFuelCardPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getPresentPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getDkvPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(15)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.get(0).getUtaPaymentPrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(16)) {
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(listOfTotal.get(0).getNumberofVehicle()).append("</td>");
                }
                sb.append(" </tr> ");
            }

            sb.append(" </table> ");
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }

        return sb.toString();

    }

}
