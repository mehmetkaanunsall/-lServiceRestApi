/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 01:34:46
 */
package com.mepsan.marwiz.general.account.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.mepsan.marwiz.general.account.dao.IAccountDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountUpload;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.primefaces.context.RequestContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AccountService implements IAccountService {

    @Autowired
    public IAccountDao accountDao;

    @Autowired
    public SessionBean sessionBean;

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(boolean isWithoutMovement, boolean isZeroBalance, List<Categorization> listCategorization, int type) {
        String where = "";

        String categories = "";
        if (!listCategorization.isEmpty()) {
            if (listCategorization.get(0).getId() != 0) {
                for (Categorization categorization : listCategorization) {
                    categories = categories + "," + String.valueOf(categorization.getId());
                }
            }
        }

        if (type == 0) {//CARİ
            if (!listCategorization.isEmpty()) {
                if (listCategorization.get(0).getId() != 0) {
                    if (!categories.equals("")) {
                        categories = categories.substring(1, categories.length());
                        where += " AND acc.id IN (SELECT accn.account_id FROM general.account_categorization_con accn WHERE accn.deleted=FALSE AND accn.categorization_id IN ( " + categories + ") ) ";
                    }

                }
            }
        } else if (type == 1) { //PERSONEL
            if (!listCategorization.isEmpty()) {
                if (listCategorization.get(0).getId() != 0) {
                    if (!categories.equals("")) {
                        categories = categories.substring(1, categories.length());
                        where += " AND acc.id IN (SELECT eccn.account_id FROM general.employee_categorization_con eccn WHERE eccn.deleted=FALSE AND eccn.categorization_id IN ( " + categories + ") ) ";
                    }

                }
            }
        }

        if (isWithoutMovement) {
            where += " AND EXISTS\n"
                    + "(SELECT account_id FROM general.accountmovement WHERE account_id=acc.id AND deleted=False AND \n"
                    + "(financingdocument_id IS NOT NULL OR chequebill_id IS NOT NULL OR invoice_id IS NOT NULL OR receipt_id IS NOT NULL) limit 1)";
        }

        if (isZeroBalance) {
            where += " AND COALESCE(abc.balance, 0) <> 0 ";
        }

        return where;
    }

    @Override
    public int create(Account obj) {
        return accountDao.create(obj);
    }

    @Override
    public int update(Account obj) {
        return accountDao.update(obj);
    }

    @Override
    public List<Account> accountBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        return accountDao.accountBook(first, pageSize, sortField, sortOrder, filters, where, type, param);
    }

    @Override
    public int accountBookCount(String where, String type, List<Object> param) {
        return accountDao.accountBookCount(where, type, param);
    }

    @Override
    public List<Account> findAll(String where) {
        return accountDao.findAll(where);
    }

    @Override
    public List<Account> findAllAccount(int typeId) {
        return accountDao.findAllAccount(typeId);
    }

    @Override
    public int delete(Account account) {
        return accountDao.delete(account);
    }

    @Override
    public int testBeforeDelete(Account account) {
        return accountDao.testBeforeDelete(account);
    }

    @Override
    public List<Account> findAllAccountToIntegrationCode() {
        return accountDao.findAllAccountToIntegrationCode();
    }

    @Override
    public List<Account> findSupplier() {
        return accountDao.findSupplier();
    }

    @Override
    public List<AccountUpload> createSampleList() {
        List<AccountUpload> list = new ArrayList<>();

        AccountUpload account = new AccountUpload();
        account.setIsPerson(Boolean.TRUE);
        account.setName("2AK OTO BAKIM ÜRÜNLERİ GIDA İHT.MADDELERİ");
        account.setTitle("2AK OTO BAKIM ÜRÜNLER A.Ş");
        account.setTaxNo("6789890890");
        account.setTaxOffice("Konya");
        account.setCode("456658678");
        account.getType().setId(3);
        account.getStatus().setId(5);
        account.setTransferBalance(BigDecimal.valueOf(305));
        account.setAddress("");
        account.setDueDay(40);
        account.setTaxpayertype_id(2);
        account.setIsEmployee(false);

        list.add(account);

        account = new AccountUpload();
        account.setIsPerson(Boolean.FALSE);
        account.setName("ADNAN SEVDAN");
        account.setTaxNo("10235641806014");
        account.setTaxOffice("İzmir");
        account.setCode("1234454");
        account.getType().setId(4);
        account.getStatus().setId(5);
        account.setTransferBalance(BigDecimal.valueOf(625.23));
        account.setDueDay(30);
        account.setTaxpayertype_id(1);
        account.setAddress("İZMİR IŞIKKENT");
        account.setIsEmployee(false);

        list.add(account);

        account = new AccountUpload();
        account.setIsPerson(Boolean.TRUE);
        account.setName("AKAR OPTİK");
        account.setTaxOffice("Ankara");
        account.setCode("2323345");
        account.setTitle("AKAR OPTİK LIMITED");
        account.setTaxNo("57678902343454");
        account.getType().setId(5);
        account.getStatus().setId(5);
        account.setTransferBalance(BigDecimal.valueOf(65.98));
        account.setTaxpayertype_id(3);
        account.setDueDay(30);
        account.setAddress("KONYA SELÇUKLU ");
        account.setIsEmployee(false);

        list.add(account);

        return list;
    }

    public boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<AccountUpload> processUploadFile(InputStream inputStream) {
        System.out.println("-processUploadFile---");
        AccountUpload accountItem = new AccountUpload();
        List<AccountUpload> excelStockList = new ArrayList<>();
        try {
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream); // HSSF veya XSSF olarak oluşması için bu şekilde çalışma dosyası oluşturuldu.
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows;
            rows = sheet.getPhysicalNumberOfRows();
            int cols = 4;
            int tmp = 0;

            for (int i = 0; i < 11 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        tmp = 4;
                    }
                }
            }

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            excelStockList.clear();
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);

                accountItem = new AccountUpload();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 
                    accountItem.setExcelDataType(1);

                    if (row.getCell(0) != null) {
                        try {
                            CellValue cellValue0 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue0.getCellTypeEnum()) { // Cari tipi
                                case NUMERIC:
                                    Double bData = row.getCell(0).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        accountItem.setIsPerson(Boolean.TRUE);
                                    } else {
                                        accountItem.setIsPerson(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    if (value == 1) {
                                        accountItem.setIsPerson(Boolean.TRUE);
                                    } else {
                                        accountItem.setIsPerson(Boolean.FALSE);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setIsPerson(Boolean.FALSE);
                        }
                    } else if (row.getCell(0) == null) {
                        accountItem.setIsPerson(Boolean.FALSE);
                    }

                    if (row.getCell(1) != null) {
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) { // Cari kodu
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(1).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    accountItem.setCode(barcode);

                                    break;
                                case STRING:
                                    accountItem.setCode(String.valueOf(row.getCell(1).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setCode("");
                            accountItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(1) == null) {
                        accountItem.setCode("");
                        accountItem.setExcelDataType(-1);
                    }

                    if (row.getCell(2) != null) { //Cari adı
                        try {
                            CellValue cellValue2 = evaluator.evaluate(row.getCell(2));
                            switch (cellValue2.getCellTypeEnum()) {
                                case STRING:
                                    accountItem.setName(String.valueOf(String.valueOf(row.getCell(2).getRichStringCellValue())));
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setName("");
                        }
                    } else if (row.getCell(2) == null) {
                        accountItem.setName("");
                    }

                    if (row.getCell(3) != null) { //ticari unvan
                        try {
                            CellValue cellValue3 = evaluator.evaluate(row.getCell(3));
                            switch (cellValue3.getCellTypeEnum()) {
                                case STRING:
                                    accountItem.setTitle(String.valueOf(String.valueOf(row.getCell(3).getRichStringCellValue())));
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setTitle("");
                        }
                    } else if (row.getCell(3) == null) {
                        accountItem.setTitle("");
                    }

                    if (row.getCell(4) != null) { //verii dairesi
                        try {
                            CellValue cellValue4 = evaluator.evaluate(row.getCell(4));
                            switch (cellValue4.getCellTypeEnum()) {
                                case STRING:
                                    accountItem.setTaxOffice(String.valueOf(String.valueOf(row.getCell(4).getRichStringCellValue())));
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setTaxOffice("");
                        }
                    } else if (row.getCell(4) == null) {
                        accountItem.setTaxOffice("");
                    }

                    if (row.getCell(5) != null) {
                        try {
                            CellValue cellValue3 = evaluator.evaluate(row.getCell(5));
                            switch (cellValue3.getCellTypeEnum()) { //Tax no
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(5).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    accountItem.setTaxNo(barcode);

                                    break;
                                case STRING:
                                    accountItem.setTaxNo(String.valueOf(row.getCell(5).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setTaxNo("");
                            accountItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(5) == null) {
                        accountItem.setTaxNo("");
                        accountItem.setExcelDataType(-1);
                    }

                    if (row.getCell(6) != null) { // tip (müşteri - tedarikçi )
                        try {
                            CellValue cellValue4 = evaluator.evaluate(row.getCell(6));
                            switch (cellValue4.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dBrand = row.getCell(6).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int typeId = dBrand.intValue();

                                    accountItem.getType().setId(typeId);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(6).getRichStringCellValue()));
                                    accountItem.getType().setId(value);
                                    break;
                            }
                            if (accountItem.getType().getId() != 0) {
                                for (Type type : sessionBean.getTypes(3)) {
                                    if (accountItem.getType().getId() == type.getId()) {
                                        accountItem.getType().setTag(type.getNameMap().get(sessionBean.getLangId()).getName());
                                    }
                                }
                                if (accountItem.getType().getId() != 3 && accountItem.getType().getId() != 4 && accountItem.getType().getId() != 5) {
                                    System.out.println("*---tip---" + accountItem.getType().getId());
                                    accountItem.setExcelDataType(-1);
                                    accountItem.getType().setId(-1);
                                }
                            }

                        } catch (Exception e) {
                            accountItem.setExcelDataType(-1);
                            accountItem.getType().setId(-1);
                        }
                    } else if (row.getCell(6) == null) {
                        accountItem.setExcelDataType(-1);
                        accountItem.getType().setId(-1);
                    }

                    if (row.getCell(7) != null) { // Mükellef tipi (normal - efatura , earşiv)
                        try {
                            CellValue cellValue7 = evaluator.evaluate(row.getCell(7));
                            switch (cellValue7.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dBrand = row.getCell(7).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int typeId = dBrand.intValue();

                                    System.out.println("-typeId---" + typeId);

                                    accountItem.setTaxpayertype_id(typeId);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(7).getRichStringCellValue()));
                                    accountItem.setTaxpayertype_id(value);
                                    break;
                            }
                            if (accountItem.getTaxpayertype_id() != 0) {
                                if (accountItem.getTaxpayertype_id() != 1 && accountItem.getTaxpayertype_id() != 2 && accountItem.getTaxpayertype_id() != 3) {
                                    accountItem.setExcelDataType(-1);
                                    accountItem.setTaxpayertype_id(-1);
                                }
                                if (accountItem.getTaxpayertype_id() == 1) {

                                }
                            } else {
                                accountItem.setTaxpayertype_id(-1);
                                accountItem.setExcelDataType(-1);
                            }

                        } catch (Exception e) {
                            accountItem.setTaxpayertype_id(-1);
                            accountItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(7) == null) {
                        accountItem.setTaxpayertype_id(-1);
                        accountItem.setExcelDataType(-1);
                    }
                    if (row.getCell(8) != null) { // Vade Tarihi (Gün)
                        try {
                            CellValue cellValue8 = evaluator.evaluate(row.getCell(8));
                            switch (cellValue8.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dBrand = row.getCell(8).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int dueDay = dBrand.intValue();

                                    accountItem.setDueDay(dueDay);
                                    break;
                                default:
                                    int dueDay1 = Integer.valueOf(String.valueOf(row.getCell(7).getRichStringCellValue()));
                                    accountItem.setDueDay(dueDay1);
                                    break;
                            }

                        } catch (Exception e) {
                            accountItem.setDueDay(null);
                        }
                    } else if (row.getCell(8) == null) {
                        accountItem.setDueDay(null);
                    }

                    if (row.getCell(9) != null) { // devir bakiyesi
                        try {
                            CellValue cellValue9 = evaluator.evaluate(row.getCell(9));
                            switch (cellValue9.getCellTypeEnum()) {
                                case NUMERIC:
                                    double equavilent = row.getCell(9).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    accountItem.setTransferBalance(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(9).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    accountItem.setTransferBalance(bd);
                                    break;
                                default:
                                    String s1 = String.valueOf(row.getCell(9).getRichStringCellValue());
                                    double d1 = Double.valueOf(s1);
                                    BigDecimal bd1 = BigDecimal.valueOf(d1);
                                    accountItem.setTransferBalance(bd1);
                                    break;
                            }
                            if (accountItem.getTransferBalance().compareTo(BigDecimal.ZERO) >= 0) { // transfer bakiyesine göre hareket yönü set edildi.
                                accountItem.getAccountMovement().setIsDirection(true);
                            } else {
                                accountItem.getAccountMovement().setIsDirection(false);
                            }

                        } catch (Exception e) {
                            accountItem.setTransferBalance(BigDecimal.valueOf(0));
                            if (accountItem.getTransferBalance().compareTo(BigDecimal.ZERO) >= 0) {
                                accountItem.getAccountMovement().setIsDirection(true);
                            } else {
                                accountItem.getAccountMovement().setIsDirection(false);
                            }
                        }
                    } else if (row.getCell(9) == null) {
                        accountItem.setTransferBalance(BigDecimal.valueOf(0));
                        if (accountItem.getTransferBalance().compareTo(BigDecimal.ZERO) >= 0) {
                            accountItem.getAccountMovement().setIsDirection(true);
                        } else {
                            accountItem.getAccountMovement().setIsDirection(false);
                        }
                    }

                    if (row.getCell(10) != null) { //adres
                        try {
                            CellValue cellValue10 = evaluator.evaluate(row.getCell(10));
                            switch (cellValue10.getCellTypeEnum()) {
                                case STRING:
                                    accountItem.setAddress(String.valueOf(String.valueOf(row.getCell(10).getRichStringCellValue())));
                                    break;
                                case NUMERIC:
                                    accountItem.setAddress(String.valueOf(String.valueOf(row.getCell(10).getNumericCellValue())));
                                    break;
                            }
                        } catch (Exception e) {
                            accountItem.setAddress("");
                        }
                    } else if (row.getCell(10) == null) {
                        accountItem.setAddress("");
                    }
                    excelStockList.add(accountItem);
                }
            }
            return excelStockList;
        } catch (IOException ex) {
            return new ArrayList<>();
        } catch (InvalidFormatException ex) {
            return new ArrayList<>();
        } catch (EncryptedDocumentException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public String jsonToList(List<AccountUpload> uploadList) {
        JsonArray jsonArray = new JsonArray();

        for (AccountUpload obj : uploadList) {
            if (obj.getExcelDataType() == 1) { // hatalı kayıtları GÖNDERME
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("accounttype", obj.getIsPerson());
                jsonObject.addProperty("name", obj.getName());
                jsonObject.addProperty("title", obj.getTitle());
                jsonObject.addProperty("taxno", obj.getTaxNo());
                jsonObject.addProperty("type", obj.getType().getId());
                jsonObject.addProperty("transferbalance", (obj.getTransferBalance().compareTo(BigDecimal.valueOf(0)) == -1 ? obj.getTransferBalance().multiply(BigDecimal.valueOf(-1)) : obj.getTransferBalance()));
                jsonObject.addProperty("address", obj.getAddress());
                jsonObject.addProperty("isdirection", obj.getAccountMovement().isIsDirection());
                jsonObject.addProperty("code", obj.getCode());
                jsonObject.addProperty("taxoffice", obj.getTaxOffice());
                jsonObject.addProperty("payertype", obj.getTaxpayertype_id());
                jsonObject.addProperty("dueday", obj.getDueDay());
                jsonArray.add(jsonObject);
            }
        }
        return accountDao.saveAccount(jsonArray.toString());
    }

    @Override
    public void exportPdf(List<Account> listOfObjects, String clmName, List<Boolean> toogleList) {

        try {

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 1);
            pdfDocument.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

            List<Float> listOfColumnWidth = new ArrayList<Float>();

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accounts"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());
            pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, Color.WHITE));
            if (toogleList.get(0)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accounttypes"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(1)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("code"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(2)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("name"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            }

            if (toogleList.get(3)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(clmName, pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(4)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("taxno"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(5)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("taxoffice"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(6)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("balance"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            }

            if (toogleList.get(7)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("type"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(8)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("statu"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(0)) {
                listOfColumnWidth.add(6f);

            }

            if (toogleList.get(1)) {
                listOfColumnWidth.add(8f);

            }

            if (toogleList.get(2)) {
                listOfColumnWidth.add(28f);

            }

            if (toogleList.get(3)) {
                listOfColumnWidth.add(28f);

            }

            if (toogleList.get(4)) {
                listOfColumnWidth.add(8f);

            }

            if (toogleList.get(5)) {
                listOfColumnWidth.add(8f);

            }

            if (toogleList.get(6)) {
                listOfColumnWidth.add(10f);

            }

            if (toogleList.get(7)) {
                listOfColumnWidth.add(12f);

            }

            if (toogleList.get(8)) {
                listOfColumnWidth.add(5f);

            }

            float[] columnWidths = new float[listOfColumnWidth.size()];
            for (int i = 0; i < listOfColumnWidth.size(); i++) {
                columnWidths[i] = (float) listOfColumnWidth.get(i);

            }

            pdfDocument.getPdfTable().setWidths((float[]) columnWidths);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            for (Account account : listOfObjects) {

                if (toogleList.get(0)) {
                    if (account.getIsPerson()) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("individual"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("corporate"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getCode(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getOnlyAccountName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getTitle(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getTaxNo(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getTaxOffice(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(account.getBalance()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getType().getTag(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(account.getStatus().getTag(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("accounts"));

        } catch (DocumentException e) {
        } finally {

        }
    }

    //Innova entegratör firması web servisinden carinin mükellef tipini sorgular
    @Override
    public List<Account> taxPayerİnquiryRequest(Account account) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String result = null;
        List<Account> listResult;
        listResult = new ArrayList<>();

        try {

            String data = " <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay9=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.CustomerInquiry\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">\n"
                    + "    <x:Header/>\n"
                    + "    <x:Body>\n"
                    + "        <tem:CustomerInquiry>\n"
                    + "            <tem:request>\n"
                    + "                <pay1:Header>\n"
                    + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                    + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                    + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                    + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                    + "                </pay1:Header>\n"
                    + "                <pay9:IdentityNumbers>\n"
                    + "                    <arr:string>" + account.getTaxNo() + "</arr:string>\n"
                    + "                </pay9:IdentityNumbers>\n"
                    + "            </tem:request>\n"
                    + "        </tem:CustomerInquiry>\n"
                    + "    </x:Body>\n"
                    + "</x:Envelope>";

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IClientInterfaceService/CustomerInquiry");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));

                int returnCode = httpClient.executeMethod(methodPost);
                if (returnCode == 200) {
                    br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String readLine;

                    while (((readLine = br.readLine()) != null)) {
                        sb.append(readLine);
                    }

                    result = sb.toString();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                    Document document = builder.parse(inputSource);
                    result = document.getElementsByTagName("ResponseCode").item(0).getTextContent();

                    NodeList returnList = document.getElementsByTagName("CustomerInquiryResult").item(0).getChildNodes().item(1).getChildNodes();

                    for (int i = 0; i < returnList.getLength(); i++) {
                        Account acc = new Account();
                        Element node = (Element) returnList.item(i);

                        if (node.getElementsByTagName("a:IsExists").item(0).getTextContent().equalsIgnoreCase("true")) {
                            acc.setTaxpayertype_id(1);
                            acc.setTagInfo(node.getElementsByTagName("a:Alias").item(0).getTextContent());
                            acc.setTitle(node.getElementsByTagName("a:Name").item(0).getTextContent());
                        } else {
                            acc.setTaxpayertype_id(2);
                        }
                        listResult.add(acc);
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    System.out.println("-----returncode------" + returnCode);

                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("-------CATCH-----" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("------CATCH-----" + fe.getMessage());
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("-------CATCH-------" + ex.getMessage());
        }

        return listResult;
    }

    //Uyumsoft entegratör firması web servisinden carinin mükellef tipini sorgular
    @Override
    public Account taxPayerİnquiryRequestU(Account account) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String result = null;

        try {
            String data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                    + "    <x:Header/>\n"
                    + "    <x:Body>\n"
                    + "        <tem:IsEInvoiceUser>\n"
                    + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                    + "            <tem:vknTckn>" + account.getTaxNo() + "</tem:vknTckn>\n"
                    + "        </tem:IsEInvoiceUser>\n"
                    + "    </x:Body>\n"
                    + "</x:Envelope>";

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/IsEInvoiceUser");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));

                int returnCode = httpClient.executeMethod(methodPost);

                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;

                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }

                result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                System.out.println("-----data-----" + data);
                System.out.println("-----result----" + result);
                if (returnCode == 200) {
                    if (document.getElementsByTagName("IsEInvoiceUserResponse").item(0).getChildNodes().item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true")) {
                        if (document.getElementsByTagName("IsEInvoiceUserResponse").item(0).getChildNodes().item(0).getAttributes().getNamedItem("Value").getTextContent().equalsIgnoreCase("true")) {
                            account.setTaxpayertype_id(1);
                        } else {
                            account.setTaxpayertype_id(2);
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("currenttaxpayertypequeryfailedpleasetryagain")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else if (returnCode == 500) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("s:Fault").item(0).getChildNodes().item(1).getTextContent()));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    account.setTaxpayertype_id(0);
                    return account;
                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("--------CATCH----" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("-------CATCH-----" + fe.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            System.out.println("-------CATCH-----" + ex.getMessage());
        }
        return account;
    }

    //Uyumsoft entegratör firması web servisinden carinin vkn, ticari unvan vb. bilgilerini sorgular
    @Override
    public Account requestAccountInfo(Account acc) {
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String result = "";

        try {

            String data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                    + "    <x:Header/>\n"
                    + "    <x:Body>\n"
                    + "        <tem:GetUserAliasses>\n"
                    + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                    + "            <tem:vknTckn>" + acc.getTaxNo() + "</tem:vknTckn>\n"
                    + "        </tem:GetUserAliasses>\n"
                    + "    </x:Body>\n"
                    + "</x:Envelope>";

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/GetUserAliasses");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));

                int returnCode = httpClient.executeMethod(methodPost);

                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;

                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }

                result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                if (returnCode == 200) {
                    if (document.getElementsByTagName("GetUserAliassesResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("GetUserAliassesResult").item(0).getChildNodes().getLength() != 0) {
                        acc.setTaxNo(document.getElementsByTagName("Definition").item(0).getAttributes().getNamedItem("Identifier").getTextContent());
                        acc.setTitle(document.getElementsByTagName("Definition").item(0).getAttributes().getNamedItem("Title").getTextContent());
                        acc.setTagInfo(document.getElementsByTagName("ReceiverboxAliases").item(0).getAttributes().getNamedItem("Alias").getTextContent());
                    } else if (document.getElementsByTagName("GetUserAliassesResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("GetUserAliassesResult").item(0).getChildNodes().getLength() == 0) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("companyinformationforthenumberyouarequeryingcouldnotbefound")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else if (returnCode == 500) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("s:Fault").item(0).getChildNodes().item(1).getTextContent()));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("--------CATCH-----" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("------CATCH-----" + fe.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            System.out.println("--------CATCH-------" + ex.getMessage());
        }
        return acc;

    }

    @Override
    public void downloadSampleList(List<AccountUpload> sampleList) {
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());

        try {
            int jRow = 0;

            CellStyle cellStyle = excelDocument.getWorkbook().createCellStyle();
            cellStyle.setBorderRight(BorderStyle.MEDIUM);
            cellStyle.setFillForegroundColor(IndexedColors.BLACK.index);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font font = excelDocument.getWorkbook().createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.index);
            cellStyle.setFont(font);

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell accounttypes = row.createCell((short) 0);
            accounttypes.setCellValue(sessionBean.getLoc().getString("accounttypes"));
            accounttypes.setCellStyle(cellStyle);

            SXSSFCell code = row.createCell((short) 1);
            code.setCellValue(sessionBean.getLoc().getString("code"));
            code.setCellStyle(cellStyle);

            SXSSFCell name = row.createCell((short) 2);
            name.setCellValue(sessionBean.getLoc().getString("name"));
            name.setCellStyle(cellStyle);

            SXSSFCell title = row.createCell((short) 3);
            title.setCellValue(sessionBean.getLoc().getString("title"));
            title.setCellStyle(cellStyle);

            SXSSFCell taxoffice = row.createCell((short) 4);
            taxoffice.setCellValue(sessionBean.getLoc().getString("taxoffice"));
            taxoffice.setCellStyle(cellStyle);

            SXSSFCell taxno = row.createCell((short) 5);
            taxno.setCellValue(sessionBean.getLoc().getString("taxno"));
            taxno.setCellStyle(cellStyle);

            SXSSFCell type = row.createCell((short) 6);
            type.setCellValue(sessionBean.getLoc().getString("type"));
            type.setCellStyle(cellStyle);

            SXSSFCell payertype = row.createCell((short) 7);
            payertype.setCellValue(sessionBean.getLoc().getString("payertype"));
            payertype.setCellStyle(cellStyle);

            SXSSFCell dueday = row.createCell((short) 8);
            dueday.setCellValue(sessionBean.getLoc().getString("ga.dueday"));
            dueday.setCellStyle(cellStyle);

            SXSSFCell transferbalance = row.createCell((short) 9);
            transferbalance.setCellValue(sessionBean.getLoc().getString("transferbalance"));
            transferbalance.setCellStyle(cellStyle);

            SXSSFCell statu = row.createCell((short) 10);
            statu.setCellValue(sessionBean.getLoc().getString("address"));
            statu.setCellStyle(cellStyle);

            for (AccountUpload upload : sampleList) {
                row = excelDocument.getSheet().createRow(jRow++);

                row.createCell((short) 0).setCellValue(upload.getIsPerson() ? 1 : 0);
                row.createCell((short) 1).setCellValue(upload.getCode());
                row.createCell((short) 2).setCellValue(upload.getName());
                row.createCell((short) 3).setCellValue(upload.getTitle());
                row.createCell((short) 4).setCellValue(upload.getTaxOffice());
                row.createCell((short) 5).setCellValue(upload.getTaxNo());
                row.createCell((short) 6).setCellValue(upload.getType().getId());
                row.createCell((short) 7).setCellValue(upload.getTaxpayertype_id());
                row.createCell((short) 8).setCellValue(upload.getDueDay());
                row.createCell((short) 9).setCellValue(upload.getTransferBalance().toString());
                row.createCell((short) 10).setCellValue(upload.getAddress());

            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("sampleexcelfile"));
            } catch (IOException ex) {
                Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public int controlCashierUser(Account account) {
       return accountDao.controlCashierUser(account);
    }

}
