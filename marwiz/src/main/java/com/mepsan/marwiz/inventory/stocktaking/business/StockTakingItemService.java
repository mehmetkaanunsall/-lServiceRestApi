/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.02.2018 07:42:06
 */
package com.mepsan.marwiz.inventory.stocktaking.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.fulltakingreport.business.FullTakingReportService;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReportDao;
import com.mepsan.marwiz.inventory.stock.dao.StockDao;
import com.mepsan.marwiz.inventory.stocktaking.dao.StockTakingItemDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

public class StockTakingItemService implements IStockTakingItemService {

    @Autowired
    private StockTakingItemDao stockTakingItemDao;

    @Autowired
    private StockDao stockDao;

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

//    @Override
//    public List<StockTakingItem> findAll(StockTaking stockTaking) {
//        return stockTakingItemDao.findAll(stockTaking);
//    }
    @Override
    public int processStockTakingItem(int type, StockTaking stockTaking, List<StockTakingItem> listOfItems, boolean isReset) {
        stockTaking.setJsonItems(jsonArrayStockTakingItems(listOfItems));
        return stockTakingItemDao.processStockTakingItem(type, stockTaking, isReset);
    }

    @Override
    public int updateSaleControl(StockTaking stockTaking) {
        return stockTakingItemDao.updateSaleControl(stockTaking);
    }

    /**
     * Bu metot depo sayımları stok listesini json array stringine dönüştürür.
     *
     * @param stockTakingItems
     * @return
     */
    @Override
    public String jsonArrayStockTakingItems(List<StockTakingItem> stockTakingItems) {
        JsonArray jsonArray = new JsonArray();
        for (StockTakingItem obj : stockTakingItems) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("systemquantity", obj.getSystemQuantity());
            jsonObject.addProperty("realquantity", obj.getRealQuantity());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public List<StockTakingItem> findAllSaleControlList(StockTaking obj) {
        return stockTakingItemDao.findAllSaleControlList(obj);
    }

    @Override
    public List<StockTakingItem> findAllUncountedStocks(StockTaking stockTaking) {
        return stockTakingItemDao.findAllUncountedStocks(stockTaking);
    }

    @Override
    public List<StockTakingItem> findAllMinusStocks(StockTaking stockTaking) {
        return stockTakingItemDao.findAllMinusStocks(stockTaking);
    }

    @Override
    public List<StockTakingItem> findWithoutCategorization(StockTaking obj, String categories) {
        return stockTakingItemDao.findWithoutCategorization(obj, categories);
    }

    @Override
    public String importStockTakingItem(List<StockTakingItem> stockTakingItems, StockTaking stockTaking) {
        JsonArray jsonArray = new JsonArray();

        for (StockTakingItem obj : stockTakingItems) {
            if (obj.getExcelDataType() == 1) { // hatalı kayıtları GÖNDERME
                JsonObject jsonObject = new JsonObject();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                jsonObject.addProperty("barcode", obj.getStock().getBarcode());
                jsonObject.addProperty("realquantity", obj.getRealQuantity());
                jsonObject.addProperty("processdate", sdf.format(obj.getProcessDate()));

                jsonArray.add(jsonObject);
            }

        }
        return stockTakingItemDao.importStockTakingItem(jsonArray.toString(), stockTaking);
    }

    @Override
    public void exportStocks(String where, StockTaking stockTaking) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());
        try {
            connection = stockTakingItemDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockTakingItemDao.exportData(where, stockTaking));
            rs = prep.executeQuery();

            int jRow = 0;
            SXSSFRow warehouse = excelDocument.getSheet().createRow(jRow++);
            warehouse.createCell((short) 0).setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), stockTaking.getBeginDate()));

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                row.createCell((short) b++).setCellValue(rs.getString("istbarcode"));
                row.createCell((short) b++).setCellValue(rs.getString("istname"));

                SXSSFCell sysquantity = row.createCell((short) b++);
                sysquantity.setCellValue(StaticMethods.round(rs.getBigDecimal("iwiquantity").doubleValue(), rs.getInt("guntunitrounding")));

            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("warehousestocktaking"));
            } catch (IOException ex) {
                Logger.getLogger(FullTakingReportService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException e) {
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
                Logger.getLogger(FullTakingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

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
    public List<StockTakingItem> processUploadFile(InputStream inputStream) {
        List<StockTakingItem> excelStockList = new ArrayList<>();
        try {
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream); // HSSF veya XSSF olarak oluşması için bu şekilde çalışma dosyası oluşturuldu.
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows;
            rows = sheet.getPhysicalNumberOfRows();
            int cols = 2;
            int tmp = 0;

            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        tmp = 2;
                    }
                }
            }
            excelStockList.clear();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);

                StockTakingItem stockTakingItem = new StockTakingItem();
                if (row != null && !isRowEmpty(row)) {
                    stockTakingItem.setExcelDataType(1);

                    if (row.getCell(0) != null) {
                        try {
                            CellValue cellValue0 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue0.getCellTypeEnum()) {
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockTakingItem.getStock().setBarcode(barcode.trim());
                                    break;
                                case STRING:
                                    stockTakingItem.getStock().setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()).trim());
                                    break;
                            }

                        } catch (Exception e) {
                            stockTakingItem.getStock().setBarcode("-1");
                            stockTakingItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(0) == null) {
                        stockTakingItem.getStock().setBarcode("-1");
                        stockTakingItem.setExcelDataType(-1);

                    }

                    if (row.getCell(1) != null) {
                        try {

                            CellValue cellValue3 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue3.getCellTypeEnum()) {
                                case NUMERIC:
                                    BigDecimal bdAmount = BigDecimal.valueOf(row.getCell(1).getNumericCellValue());
                                    if (bdAmount.compareTo(BigDecimal.ZERO) == -1) {
                                        stockTakingItem.setExcelDataType(-1);
                                    }
                                    stockTakingItem.setRealQuantity(bdAmount);
                                    break;
                                case STRING:
                                    Double d = new Double(String.valueOf(row.getCell(1).getRichStringCellValue()).trim());
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    if (bd.compareTo(BigDecimal.ZERO) == -1) {
                                        stockTakingItem.setExcelDataType(-1);
                                    }
                                    stockTakingItem.setRealQuantity(bd);
                                    break;
                            }

                        } catch (Exception e) {
                            stockTakingItem.setRealQuantity(BigDecimal.ZERO);
                            stockTakingItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(1) == null) {
                        stockTakingItem.setRealQuantity(BigDecimal.ZERO);
                        stockTakingItem.setExcelDataType(-1);
                    }

                    if (row.getCell(2) != null) {
                        try {
                            CellValue cellValue4 = evaluator.evaluate(row.getCell(2));
                            switch (cellValue4.getCellTypeEnum()) {
                                case NUMERIC:
                                    stockTakingItem.setProcessDate(new Date(row.getCell(2).getDateCellValue().getTime()));
                                    break;
                            }

                        } catch (Exception e) {
                            stockTakingItem.setExcelDataType(-1);
                        }
                    } else {
                        stockTakingItem.setExcelDataType(-1);
                    }

                    if (!"-1".equals(stockTakingItem.getStock().getBarcode())) {
//                        Stock stock = new Stock();
//                        stock = stockDao.findStockBarcode(stockTakingItem.getStock().getBarcode());
//                        if (stock.getId() > 0) {
//                            stockTakingItem.getStock().setBarcode(stock.getBarcode());
                        boolean isThere = false;
                        //----------aynı barkoddan listede var mı diye kontrol edilir.Varsa miktar üstüne eklenir, tarih düşük olan alınır.Tekrar eklemeye gerek yok
                        for (StockTakingItem sti : excelStockList) {
                            if (sti.getStock().getBarcode().equals(stockTakingItem.getStock().getBarcode())) {
                                isThere = true;
                                sti.setRealQuantity(sti.getRealQuantity().add(stockTakingItem.getRealQuantity()));
                                if (stockTakingItem.getProcessDate().getTime() < sti.getProcessDate().getTime()) {
                                    sti.setProcessDate(stockTakingItem.getProcessDate());
                                }
                                break;
                            }
                        }

                        if (!isThere) {
                            excelStockList.add(stockTakingItem);
                        }
//                        }else{
//                            stockTakingItem.getStock().setBarcode(sessionBean.getLoc().getString("stockinformationnotfound"));
//                            stockTakingItem.setExcelDataType(-1);
//                            excelStockList.add(stockTakingItem);
//                        }
                    } else {
                        excelStockList.add(stockTakingItem);
                    }
                }
            }
            return excelStockList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    //Txt dosyası upload işleminde çalışır
    @Override
    public List<StockTakingItem> processUploadFileTxt(InputStream inputStream, Integer barcodeLengthStart, Integer barcodeLengthEnd, Integer pieceLengthStart, Integer pieceLengthEnd, Integer processDateLengthStart, Integer processDateLengthEnd, Date batchProcessDate, Integer dateFormatId) {

        List<StockTakingItem> excelStockList = new ArrayList<>();

        String row = "";
        StockTakingItem stockTakingItem;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-9"));
            if (inputStream != null) {
                while ((row = reader.readLine()) != null) {
                    stockTakingItem = new StockTakingItem();
                    stockTakingItem.setExcelDataType(1);

                    if (row.substring(barcodeLengthStart, barcodeLengthEnd) != null && (row.substring(barcodeLengthStart, barcodeLengthEnd).replaceAll("\\s", "").length() != 0)) {
                        try {
                            stockTakingItem.getStock().setBarcode(row.substring(barcodeLengthStart, barcodeLengthEnd).replaceAll("\\s", ""));//Txt dosyasından alınan barkod bilgisinden boşluklar silindi
                        } catch (Exception e) {
                            stockTakingItem.getStock().setBarcode("-1");
                            stockTakingItem.setExcelDataType(-1);
                        }
                    } else {
                        stockTakingItem.getStock().setBarcode("-1");
                        stockTakingItem.setExcelDataType(-1);
                    }

                    if (row.substring(pieceLengthStart, pieceLengthEnd) != null && (row.substring(pieceLengthStart, pieceLengthEnd).replaceAll("\\s", "").length() != 0)) {
                        try {

                            String adet = row.substring(pieceLengthStart, pieceLengthEnd).replaceAll("\\s", "").replaceAll("\\.$", "").replaceAll("[^0-9 && [^.,]]", "").replaceAll("^\\.", "").replaceAll("^\\,", "").replaceFirst("^0+(?!$)", "");
                            if (adet.contains(",")) {
                                BigDecimal bigDecimal = new BigDecimal(NumberFormat.getNumberInstance(Locale.FRANCE).parse(adet).toString());
                                stockTakingItem.setRealQuantity(bigDecimal);
                            } else {
                                BigDecimal bigDecimal = new BigDecimal(adet);
                                stockTakingItem.setRealQuantity(bigDecimal);
                            }

                            if (stockTakingItem.getRealQuantity().compareTo(BigDecimal.ZERO) == -1) {
                                stockTakingItem.setExcelDataType(-1);
                            }
                        } catch (Exception e) {
                            stockTakingItem.setRealQuantity(BigDecimal.ZERO);
                            stockTakingItem.setExcelDataType(-1);
                        }
                    } else {
                        stockTakingItem.setRealQuantity(BigDecimal.ZERO);
                        stockTakingItem.setExcelDataType(-1);
                    }

                    if (processDateLengthEnd != 0) {

                        if (row.substring(processDateLengthStart, processDateLengthEnd) != null && (row.substring(processDateLengthStart, processDateLengthEnd).replaceAll("\\s", "").length() != 0)) {

                            try {
                                String newDate = row.substring(processDateLengthStart, processDateLengthEnd).replaceAll("/", ".").replaceAll("-", ".");
                                if ((row.substring(processDateLengthStart, processDateLengthEnd).trim()).length() == 10) {

                                    if (dateFormatId == 1) {
                                        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.MM.dd");
                                        stockTakingItem.setProcessDate(formatterDate.parse(newDate));
                                    } else if (dateFormatId == 2) {
                                        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.dd.MM");
                                        stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                    } else if (dateFormatId == 3) {
                                        SimpleDateFormat formatterDate = new SimpleDateFormat("MM.dd.yyyy");
                                        stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                    } else if (dateFormatId == 4) {
                                        SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy");
                                        stockTakingItem.setProcessDate(formatterDate.parse(newDate));
                                    }

                                } else if ((row.substring(processDateLengthStart, processDateLengthEnd).trim()).length() == 16) {
                                    if (((row.substring(processDateLengthStart, processDateLengthStart + 10) != null && row.substring(processDateLengthStart, processDateLengthStart + 10).replaceAll("\\s", "").length() != 0)) && (row.substring(processDateLengthStart + 11, processDateLengthStart + 16) != null && row.substring(processDateLengthStart + 11, processDateLengthStart + 16).replaceAll("\\s", "").length() != 0)) {
                                        if (dateFormatId == 1) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 2) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.dd.MM HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 3) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("MM.dd.yyyy HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 4) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));
                                        }

                                    } else if ((row.substring(processDateLengthStart, processDateLengthStart + 10) != null && row.substring(processDateLengthStart, processDateLengthStart + 10).replaceAll("\\s", "").length() != 0) && (row.substring(processDateLengthStart + 11, processDateLengthStart + 16) == null || row.substring(processDateLengthStart + 11, processDateLengthStart + 16).replaceAll("\\s", "").length() == 0)) {
                                        SimpleDateFormat formatterDate1 = new SimpleDateFormat("dd.MM.yyyy");
                                        stockTakingItem.setProcessDate(formatterDate1.parse(newDate));
                                    }

                                } else if ((row.substring(processDateLengthStart, processDateLengthEnd).trim()).length() == 19) {
                                    int count1 = 0;
                                    int count2 = 0;
                                    int count3 = 0;
                                    int count4 = 0;
                                    for (int i = 0; i < row.substring(processDateLengthStart, processDateLengthStart + 10).length(); i++) {
                                        String char1 = row.substring(processDateLengthStart, processDateLengthStart + 10).substring(i, i + 1);
                                        if (char1.equals(" ")) {
                                            count1++;
                                        }
                                    }
                                    for (int i = 0; i < row.substring(processDateLengthStart + 11, processDateLengthStart + 19).length(); i++) {
                                        String char2 = row.substring(processDateLengthStart + 11, processDateLengthStart + 19).substring(i, i + 1);
                                        if (char2.equals(" ")) {
                                            count2++;
                                        }
                                    }

                                    for (int i = 0; i < row.substring(processDateLengthStart + 16, processDateLengthStart + 19).length(); i++) {
                                        String char3 = row.substring(processDateLengthStart + 16, processDateLengthStart + 19).substring(i, i + 1);
                                        if (char3.equals(" ")) {
                                            count3++;
                                        }
                                    }
                                    for (int i = 0; i < row.substring(processDateLengthStart + 11, processDateLengthStart + 16).length(); i++) {
                                        String char4 = row.substring(processDateLengthStart + 11, processDateLengthStart + 16).substring(i, i + 1);
                                        if (char4.equals(" ")) {
                                            count4++;
                                        }
                                    }

                                    if ((row.substring(processDateLengthStart, processDateLengthStart + 10) != null && row.substring(processDateLengthStart, processDateLengthStart + 10).length() != count1) && row.substring(processDateLengthStart + 11, processDateLengthStart + 19) != null && row.substring(processDateLengthStart + 11, processDateLengthStart + 19).length() != count2 && count2 == 0) {
                                        if (dateFormatId == 1) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 2) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.dd.MM HH:mm:ss");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 3) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 4) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));
                                        }

                                    } else if ((row.substring(processDateLengthStart, processDateLengthStart + 10) != null && row.substring(processDateLengthStart, processDateLengthStart + 10).length() != count1) && (row.substring(processDateLengthStart + 16, processDateLengthStart + 19) == null || row.substring(processDateLengthStart + 16, processDateLengthStart + 19).length() == count3) && (row.substring(processDateLengthStart + 11, processDateLengthStart + 16) != null && row.substring(processDateLengthStart + 11, processDateLengthStart + 16).length() != count4 && count4 == 0)) {
                                        if (dateFormatId == 1) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 2) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.dd.MM HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 3) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("MM.dd.yyyy HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 4) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));
                                        }

                                    } else if ((row.substring(processDateLengthStart, processDateLengthStart + 10) != null && row.substring(processDateLengthStart, processDateLengthStart + 10).length() != count1) && (row.substring(processDateLengthStart + 11, processDateLengthStart + 19) == null || row.substring(processDateLengthStart + 11, processDateLengthStart + 19).length() == count2)) {
                                        if (dateFormatId == 1) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.MM.dd");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 2) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy.dd.MM");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 3) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("MM.dd.yyyy");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));

                                        } else if (dateFormatId == 4) {
                                            SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy");
                                            stockTakingItem.setProcessDate(formatterDate.parse(newDate));
                                        }

                                    }
                                }

                            } catch (Exception e) {
                                RequestContext.getCurrentInstance().update("grwProcessMessage");
                                stockTakingItem.setExcelDataType(-1);
                            }
                        } else {
                            stockTakingItem.setExcelDataType(-1);
                        }

                    }

                    if (batchProcessDate != null) {
                        stockTakingItem.setProcessDate(batchProcessDate);
                        if (stockTakingItem.getProcessDate() == null) {
                            stockTakingItem.setExcelDataType(-1);
                        }
                    }

                    if (stockTakingItem.getProcessDate() == null) {
                        stockTakingItem.setExcelDataType(-1);
                    }
                    if (stockTakingItem.getRealQuantity() == null) {
                        stockTakingItem.setExcelDataType(-1);
                    }
                    if (stockTakingItem.getStock().getBarcode() == null) {
                        stockTakingItem.setExcelDataType(-1);
                    }

                    if (!"-1".equals(stockTakingItem.getStock().getBarcode())) {
                        boolean isThere = false;
                        //----------aynı barkoddan listede var mı diye kontrol edilir.Varsa miktar üstüne eklenir, tarih düşük olan alınır.Tekrar eklemeye gerek yok
                        for (StockTakingItem sti : excelStockList) {
                            if (sti.getStock().getBarcode().equals(stockTakingItem.getStock().getBarcode())) {
                                isThere = true;
                                sti.setRealQuantity(sti.getRealQuantity().add(stockTakingItem.getRealQuantity()));
                                if (stockTakingItem.getProcessDate().getTime() < sti.getProcessDate().getTime()) {
                                    sti.setProcessDate(stockTakingItem.getProcessDate());
                                }
                                break;
                            }
                        }

                        if (!isThere) {
                            excelStockList.add(stockTakingItem);
                        }
                    } else {
                        excelStockList.add(stockTakingItem);
                    }

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(StockTakingItemService.class.getName()).log(Level.SEVERE, null, ex);

        } catch (StringIndexOutOfBoundsException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("alldatainthefilecouldnotberetrievedbecausetherearelinesthatarenotcompatiblewiththefileformatyouspecifiedinthetextfile")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        } finally {
            try {
                inputStream.close();
            } catch (Throwable ignore) {
            }
        }

        return excelStockList;
    }

    @Override
    public List<StockTakingItem> createSampleList() {

        List<StockTakingItem> sampleList = new ArrayList<>();

        StockTakingItem stockTakingItem = new StockTakingItem();
        stockTakingItem.getStock().setBarcode("8690504080886");
        stockTakingItem.setRealQuantity(BigDecimal.valueOf(15));
        stockTakingItem.setProcessDate(new Date());

        sampleList.add(stockTakingItem);

        StockTakingItem stockTakingItem2 = new StockTakingItem();
        stockTakingItem2.getStock().setBarcode("8690504082651");
        stockTakingItem2.setRealQuantity(BigDecimal.valueOf(20));
        stockTakingItem2.setProcessDate(new Date());

        sampleList.add(stockTakingItem2);

        StockTakingItem stockTakingItem3 = new StockTakingItem();
        stockTakingItem3.getStock().setBarcode("8690526083254");
        stockTakingItem3.setRealQuantity(BigDecimal.valueOf(30));
        stockTakingItem3.setProcessDate(new Date());

        sampleList.add(stockTakingItem3);

        return sampleList;
    }

    @Override
    public int delete(List<StockTakingItem> stockTakingItems) {
        String items = "";
        for (StockTakingItem stockTakingItem : stockTakingItems) {
            items = items + "," + String.valueOf(stockTakingItem.getId());
            if (stockTakingItem.getId() == 0) {
                items = "";
                break;
            }
        }
        if (!items.equals("")) {
            items = items.substring(1, items.length());
        }

        return stockTakingItemDao.delete(items);
    }

    @Override
    public void exportExcel(List<StockTakingItem> listOfItemUpdate, StockTaking stockTaking) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            connection = stockTakingItemDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockTakingItemDao.exportData(listOfItemUpdate, stockTaking));
            rs = prep.executeQuery();

            ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());
            int jRow = 0;

            List<Boolean> list = new ArrayList<>();
            list.add(0, Boolean.FALSE);
            list.add(1, Boolean.TRUE);
            list.add(2, Boolean.TRUE);
            list.add(3, Boolean.TRUE);
            list.add(4, Boolean.TRUE);
            list.add(5, Boolean.TRUE);
            list.add(6, Boolean.TRUE);
            list.add(7, Boolean.TRUE);
            list.add(8, Boolean.TRUE);
            list.add(9, Boolean.TRUE);
            list.add(10, Boolean.TRUE);
            list.add(11, Boolean.TRUE);
            list.add(12, Boolean.TRUE);

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem", list, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                row.createCell((short) b++).setCellValue(rs.getString("stckcode") == null ? "" : rs.getString("stckcode"));
                row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode"));
                row.createCell((short) b++).setCellValue(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode"));
                row.createCell((short) b++).setCellValue(rs.getString("stckname") == null ? "" : rs.getString("stckname"));
                BigDecimal systemQuantity = BigDecimal.ZERO;
                if (rs.getInt("stiid") == 0) {
                    systemQuantity = rs.getBigDecimal("iwiquantity");
                } else {
                    systemQuantity = rs.getBigDecimal("stisystemquantity");
                }
                row.createCell((short) b++).setCellValue((systemQuantity.compareTo(BigDecimal.ZERO) == 0 || systemQuantity == null) ? "0" : String.valueOf(StaticMethods.round(systemQuantity, sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                BigDecimal realQuantity = rs.getBigDecimal("stirealquantity");
                if (!listOfItemUpdate.isEmpty()) {

                    for (StockTakingItem sti : listOfItemUpdate) {

                        if (rs.getInt("stckid") == sti.getStock().getId()) {
                            realQuantity = sti.getRealQuantity();
                            break;
                        } else {
                            realQuantity = rs.getBigDecimal("stirealquantity");

                        }
                    }

                }

                try {
                    row.createCell((short) b++).setCellValue((realQuantity.compareTo(BigDecimal.ZERO) == 0 || realQuantity == null) ? "0" : String.valueOf(StaticMethods.round(realQuantity, sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                } catch (Exception e) {
                }

                BigDecimal difference = BigDecimal.ZERO;

                if (realQuantity != null) {
                    difference = (realQuantity == null ? BigDecimal.ZERO : realQuantity).subtract(realQuantity == null ? BigDecimal.ZERO : systemQuantity);
                    row.createCell((short) b++).setCellValue((difference.compareTo(BigDecimal.ZERO) == 0 || difference == null) ? "0" : String.valueOf(StaticMethods.round(difference, sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                } else {
                    row.createCell((short) b++).setCellValue("");
                }

                row.createCell((short) b++).setCellValue(String.valueOf(StaticMethods.round(systemQuantity.multiply(rs.getBigDecimal("price")), sessionBean.getUser().getLastBranch().getCurrencyrounding())));

                BigDecimal enteredPrice = BigDecimal.ZERO;
                if (realQuantity != null) {
                    enteredPrice = (realQuantity == null ? BigDecimal.ZERO : realQuantity).multiply(rs.getBigDecimal("price") == null ? BigDecimal.ZERO : rs.getBigDecimal("price"));
                    row.createCell((short) b++).setCellValue((enteredPrice.compareTo(BigDecimal.ZERO) == 0 || enteredPrice == null) ? "0" : String.valueOf(StaticMethods.round(enteredPrice, sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                } else {
                    row.createCell((short) b++).setCellValue("");
                }
                BigDecimal differencePrice = BigDecimal.valueOf(0);

                if (realQuantity != null) {
                    differencePrice = ((realQuantity == null ? BigDecimal.ZERO : realQuantity).subtract((systemQuantity == null ? BigDecimal.ZERO : systemQuantity))).multiply(rs.getBigDecimal("price") == null ? BigDecimal.ZERO : rs.getBigDecimal("price"));
                    row.createCell((short) b++).setCellValue((differencePrice.compareTo(BigDecimal.ZERO) == 0 || differencePrice == null) ? "0" : String.valueOf(StaticMethods.round(differencePrice, sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                } else {
                    row.createCell((short) b++).setCellValue("");
                }

                row.createCell((short) b++).setCellValue(rs.getString("guntsortname") == null ? "" : rs.getString("guntsortname"));
            }

            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("warehousestocktaking"));

        } catch (Exception e) {
        }

    }

    @Override
    public void exportPdf(List<StockTakingItem> listOfItemUpdate, StockTaking stockTaking) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            connection = stockTakingItemDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockTakingItemDao.exportData(listOfItemUpdate, stockTaking));
            rs = prep.executeQuery();

            List<Boolean> list = new ArrayList<>();
            list.add(0, Boolean.FALSE);
            list.add(1, Boolean.TRUE);  
            list.add(2, Boolean.TRUE);
            list.add(3, Boolean.TRUE);
            list.add(4, Boolean.TRUE);
            list.add(5, Boolean.TRUE);
            list.add(6, Boolean.TRUE);
            list.add(7, Boolean.TRUE);
            list.add(8, Boolean.TRUE);
            list.add(9, Boolean.TRUE);
            list.add(10, Boolean.TRUE);
            list.add(11, Boolean.TRUE);

            PdfDocument pdfDocument = StaticMethods.preparePdf(list, 0);

            pdfDocument.getHeader().setPhrase(new Phrase((sessionBean.getLoc().getString("warehousestocktaking")), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem", list, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode") == null ? "" : rs.getString("stckcode"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname") == null ? "" : rs.getString("stckname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                BigDecimal systemQuantity = BigDecimal.ZERO;

                if (rs.getInt("stiid") == 0) {
                    systemQuantity = rs.getBigDecimal("iwiquantity");
                } else {
                    systemQuantity = rs.getBigDecimal("stisystemquantity");
                }
                pdfDocument.getRightCell().setPhrase(new Phrase((systemQuantity == null || systemQuantity.compareTo(BigDecimal.ZERO) == 0) ? "0" : String.valueOf(StaticMethods.round(systemQuantity, sessionBean.getUser().getLastBranch().getCurrencyrounding())), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                BigDecimal realQuantity = rs.getBigDecimal("stirealquantity");

                if (!listOfItemUpdate.isEmpty()) {
                    for (StockTakingItem sti : listOfItemUpdate) {
                        if (rs.getInt("stckid") == sti.getStock().getId()) {
                            realQuantity = sti.getRealQuantity();
                            break;
                        } else {
                            realQuantity = rs.getBigDecimal("stirealquantity");
                        }
                    }
                }

                pdfDocument.getRightCell().setPhrase(new Phrase((realQuantity == null || realQuantity.compareTo(BigDecimal.ZERO) == 0) ? "0" : String.valueOf(StaticMethods.round(realQuantity, sessionBean.getUser().getLastBranch().getCurrencyrounding())), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                BigDecimal difference = BigDecimal.ZERO;

                if (realQuantity != null) {
                    difference = (realQuantity == null ? BigDecimal.ZERO : realQuantity).subtract(systemQuantity == null ? BigDecimal.ZERO : systemQuantity);
                    pdfDocument.getRightCell().setPhrase(new Phrase((difference == null || difference.compareTo(BigDecimal.ZERO) == 0) ? "0" : String.valueOf(StaticMethods.round(difference, sessionBean.getUser().getLastBranch().getCurrencyrounding())), pdfDocument.getFont()));
                } else {
                    pdfDocument.getRightCell().setPhrase(new Phrase(""));
                }
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                pdfDocument.getRightCell().setPhrase(new Phrase((systemQuantity == null || systemQuantity.compareTo(BigDecimal.ZERO) == 0) ? "0" : String.valueOf(StaticMethods.round(systemQuantity.multiply(rs.getBigDecimal("price")), sessionBean.getUser().getLastBranch().getCurrencyrounding())), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                BigDecimal enteredPrice = BigDecimal.ZERO;
                if (realQuantity != null) {
                    enteredPrice = (realQuantity == null ? BigDecimal.ZERO : realQuantity).multiply(rs.getBigDecimal("price") == null ? BigDecimal.ZERO : rs.getBigDecimal("price"));
                    pdfDocument.getRightCell().setPhrase(new Phrase((enteredPrice == null || enteredPrice.compareTo(BigDecimal.ZERO) == 0) ? "0" : String.valueOf(StaticMethods.round(enteredPrice, sessionBean.getUser().getLastBranch().getCurrencyrounding())), pdfDocument.getFont()));
                } else {
                    pdfDocument.getRightCell().setPhrase(new Phrase(""));
                }
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                BigDecimal differencePrice = BigDecimal.valueOf(0);

                if (realQuantity != null) {
                    differencePrice = ((realQuantity == null ? BigDecimal.ZERO : realQuantity).subtract((systemQuantity == null ? BigDecimal.ZERO : systemQuantity))).multiply(rs.getBigDecimal("price") == null ? BigDecimal.ZERO : rs.getBigDecimal("price"));
                    pdfDocument.getRightCell().setPhrase(new Phrase((differencePrice == null || differencePrice.compareTo(BigDecimal.ZERO) == 0) ? "0" : String.valueOf(StaticMethods.round(differencePrice, sessionBean.getUser().getLastBranch().getCurrencyrounding())), pdfDocument.getFont()));
                } else {
                    pdfDocument.getRightCell().setPhrase(new Phrase(""));
                }
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("guntsortname") == null ? "" : rs.getString("guntsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("warehousestocktaking"));

        } catch (Exception e) {
        }

    }

    @Override
    public List<StockTakingItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockTaking stockTaking) {
        return stockTakingItemDao.findAll(first, pageSize, sortField, sortOrder, filters, where, stockTaking);
    }

    @Override
    public int count(String where, StockTaking stockTaking) {

        return stockTakingItemDao.count(where, stockTaking);
    }

}
