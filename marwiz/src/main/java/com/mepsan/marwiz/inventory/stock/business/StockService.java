/**
 *
 *
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 08:28:46
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.model.inventory.StockUpload;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.inventory.stock.dao.IStockDao;
import com.mepsan.marwiz.inventory.stock.dao.StockDao;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

public class StockService implements IStockService {

    @Autowired
    private IStockDao stockDao;

    @Autowired
    private SessionBean sessionBean;

    public void setStockDao(IStockDao stockDao) {
        this.stockDao = stockDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(Stock obj, boolean isAvailableStock) {
        return stockDao.create(obj, isAvailableStock);
    }

    @Override
    public int update(Stock obj, boolean isAvailableStock) {
        return stockDao.update(obj, isAvailableStock);
    }

    @Override
    public String createWhere(boolean isWithoutSalePrice, boolean isNoneZero, boolean isPassiveStock, List<Categorization> listCategorization, boolean isService) {
        String where = "";
        if (isWithoutSalePrice) {
            where += " AND COALESCE(si.currentsaleprice,0) >0 ";
        } else {
            where += " ";
        }
        if (isPassiveStock) {
            where += " AND (stck.status_id = 4 OR si.is_passive = TRUE) ";
        } else {
            where += " AND stck.status_id <> 4 AND si.is_passive = FALSE ";
        }

        if (isNoneZero) {
            where += " AND COALESCE(si.balance,0) <>0 ";
        } else {
            where += " ";
        }

        if (isService) {
            where += " AND stck.is_service = TRUE ";
        } else {
            where += " ";
        }

        if (!listCategorization.isEmpty()) {
            if (listCategorization.get(0).getId() != 0) {
                String categories = "";
                for (Categorization categorization : listCategorization) {
                    categories = categories + "," + String.valueOf(categorization.getId());
                }

                if (!categories.equals("")) {
                    categories = categories.substring(1, categories.length());
                    where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + categories + ") ) ";
                }    

            }
        }

        return where;
    }

    @Override
    public List<Stock> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return stockDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return stockDao.count(where);
    }

    @Override
    public List<Stock> stockBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        return stockDao.stockBook(first, pageSize, sortField, sortOrder, filters, where, type, param);
    }

    @Override
    public int stockBookCount(String where, String type, List<Object> param) {
        return stockDao.stockBookCount(where, type, param);
    }

    @Override
    public int stockBarcodeControl(Stock stock) {
        return stockDao.stockBarcodeControl(stock);
    }

    @Override
    public int stockBarcodeControl(StockAlternativeBarcode stockAlternativeBarcode) {
        return stockDao.stockBarcodeControl(stockAlternativeBarcode);
    }

    @Override
    public int updateUnit(Stock stock) {
        return stockDao.updateUnit(stock);
    }

    @Override
    public int testBeforeDelete(Stock stock) {
        return stockDao.testBeforeDelete(stock);
    }

    @Override
    public int delete(Stock stock) {
        return stockDao.delete(stock);
    }

    @Override
    public String importProductList(List<StockUpload> stocks) {
        JsonArray jsonArray = new JsonArray();

        for (StockUpload obj : stocks) {
            if (obj.getExcelDataType() == 1) { // hatalı kayıtları GÖNDERME
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("barcode", obj.getBarcode());
                jsonObject.addProperty("name", obj.getName().equals("") ? null : obj.getName());
                jsonObject.addProperty("code", obj.getCode().equals("") ? null : obj.getCode());
                jsonObject.addProperty("unit", obj.getUnit().getId() == -1 ? null : obj.getUnit().getId());
                jsonObject.addProperty("is_service", obj.getIsServiceTemp());
                jsonObject.addProperty("minstocklevel", obj.getStockInfo().getMinStockLevel());
                jsonObject.addProperty("is_quicksale", obj.getStockInfo().getIsQuickSaleTemp());
                jsonObject.addProperty("taxgroupsale", obj.getSaleTaxGruopId() == -1 ? null : obj.getSaleTaxGruopId());
                jsonObject.addProperty("taxgrouppurchase", obj.getPurchaseTaxGroupId() == -1 ? null : obj.getPurchaseTaxGroupId());
                jsonObject.addProperty("stocktype_id", obj.getStockType_id() == -1 ? null : obj.getStockType_id());
                jsonObject.addProperty("brand", obj.getBrand().getId() == -1 ? null : obj.getBrand().getId());
                jsonObject.addProperty("supplier", obj.getSupplier().getId() == -1 ? null : obj.getSupplier().getId());
                jsonObject.addProperty("supplierproductcode", "-1".equals(obj.getSupplierProductCode()) ? null : obj.getSupplierProductCode());
                jsonObject.addProperty("country", obj.getCountry().getId() == -1 ? null : obj.getCountry().getId());
                jsonObject.addProperty("alternativebarcode", "-1".equals(obj.getAlternativeBarcode().getBarcode()) ? null : obj.getAlternativeBarcode().getBarcode());
                jsonObject.addProperty("alternativebarcodequantity", obj.getAlternativeBarcode().getQuantity().compareTo(BigDecimal.valueOf(-1)) == 0 ? null : obj.getAlternativeBarcode().getQuantity());
                jsonObject.addProperty("alternativebarcode2", "-1".equals(obj.getAlternativeBarcode2().getBarcode()) ? null : obj.getAlternativeBarcode2().getBarcode());
                jsonObject.addProperty("alternativebarcodequantity2", obj.getAlternativeBarcode2().getQuantity().compareTo(BigDecimal.valueOf(-1)) == 0 ? null : obj.getAlternativeBarcode2().getQuantity());
                jsonObject.addProperty("alternativebarcode3", "-1".equals(obj.getAlternativeBarcode3().getBarcode()) ? null : obj.getAlternativeBarcode3().getBarcode());
                jsonObject.addProperty("alternativebarcodequantity3", obj.getAlternativeBarcode3().getQuantity().compareTo(BigDecimal.valueOf(-1)) == 0 ? null : obj.getAlternativeBarcode3().getQuantity());
                jsonObject.addProperty("alternativebarcode4", "-1".equals(obj.getAlternativeBarcode4().getBarcode()) ? null : obj.getAlternativeBarcode4().getBarcode());
                jsonObject.addProperty("alternativebarcodequantity4", obj.getAlternativeBarcode4().getQuantity().compareTo(BigDecimal.valueOf(-1)) == 0 ? null : obj.getAlternativeBarcode4().getQuantity());
                jsonObject.addProperty("alternativebarcode5", "-1".equals(obj.getAlternativeBarcode5().getBarcode()) ? null : obj.getAlternativeBarcode5().getBarcode());
                jsonObject.addProperty("alternativebarcodequantity5", obj.getAlternativeBarcode5().getQuantity().compareTo(BigDecimal.valueOf(-1)) == 0 ? null : obj.getAlternativeBarcode5().getQuantity());
                jsonObject.addProperty("parentcategory", obj.getParentCategory().getId() == -1 ? null : obj.getParentCategory().getId());
                jsonObject.addProperty("subcategory", obj.getSubCategoty().getId() == -1 ? null : obj.getSubCategoty().getId());
                jsonObject.addProperty("maxstocklevel", obj.getStockInfo().getMaxStockLevel());
                jsonObject.addProperty("is_minusstocklevel", obj.getStockInfo().getIsMinusStockLevelTemp());
                jsonObject.addProperty("taxdepartment", obj.getStockInfo().getTaxDepartment().getId() == -1 ? null : obj.getStockInfo().getTaxDepartment().getId());

                jsonArray.add(jsonObject);
            }

        }
        return stockDao.importProductList(jsonArray.toString());// 
    }

    @Override
    public String importProductListForCentral(List<StockUpload> stocks) {
        JsonArray jsonArray = new JsonArray();

        for (StockUpload obj : stocks) {
            if (obj.getExcelDataType() == 1) { // hatalı kayıtları GÖNDERME
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("barcode", obj.getBarcode().trim());
                jsonObject.addProperty("code", obj.getCode().equals("-1") ? null : obj.getCode());
                jsonObject.addProperty("country", obj.getCountry().getId() == -1 ? null : obj.getCountry().getId());
                jsonObject.addProperty("supplier", obj.getSupplier().getId() == -1 ? null : obj.getSupplier().getId());
                jsonObject.addProperty("supplierproductcode", "-1".equals(obj.getSupplierProductCode()) ? null : obj.getSupplierProductCode());
                jsonObject.addProperty("is_quicksale", obj.getStockInfo().getIsQuickSaleTemp());
                jsonObject.addProperty("minstocklevel", obj.getStockInfo().getMinStockLevel());
                jsonObject.addProperty("maxstocklevel", obj.getStockInfo().getMaxStockLevel());
                jsonObject.addProperty("is_minusstocklevel", obj.getStockInfo().getIsMinusStockLevelTemp());
                jsonObject.addProperty("taxdepartment", obj.getStockInfo().getTaxDepartment().getId() == -1 ? null : obj.getStockInfo().getTaxDepartment().getId());
                jsonObject.addProperty("parentcategory", obj.getParentCategory().getId() == -1 ? null : obj.getParentCategory().getId());
                jsonObject.addProperty("subcategory", obj.getSubCategoty().getId() == -1 ? null : obj.getSubCategoty().getId());

                jsonArray.add(jsonObject);
            }

        }
        return stockDao.importProductListForCentral(jsonArray.toString());// 
    }

    @Override
    public int updateDetail(Stock stock) {
        return stockDao.updateDetail(stock);
    }

    @Override
    public int batchUpdate(List<Stock> stockList, int changeField, Stock stock, List<Categorization> listCategorization) {
        String stocks = "";
        String where = "";
        for (Stock stock1 : stockList) {
            stocks = stocks + "," + String.valueOf(stock1.getId());
            if (stock1.getId() == 0) {
                stocks = "";
                break;
            } else if (stock1.getId() == -1) { //filtreyi uygula
                stocks = "-1";
                break;
            }
        }
        if (!stocks.equals("") && !stocks.equals("-1")) {
            stocks = stocks.substring(1, stocks.length());
            where = where + " id IN(" + stocks + ") ";
        } else {
            where = where + " deleted = FALSE  ";
            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                if (changeField == 7 || changeField == 8) {
                    where = where + " AND si.is_valid = TRUE ";
                } else {
                    where = where + " AND id IN (SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                            + "where si1.deleted=FALSE AND si1.branch_id =" + sessionBean.getUser().getLastBranch().getId() + " AND si1.stock_id =stck.id\n"
                            + "AND  si1.is_valid  =TRUE)  ";
                }
            } else {
                if (changeField == 7 || changeField == 8) {
                    where = where + " AND stock_id IN(SELECT stck.id FROM inventory.stock stck\n"
                            + "WHERE stck.is_otherbranch = TRUE AND stck.deleted = FALSE AND stck.id = si.stock_id)";
                } else {
                    where = where + " AND is_otherbranch = TRUE ";
                }
            }
            if (stocks.equals("-1")) { //filtreyi uygula
                if (!listCategorization.isEmpty()) {
                    if (listCategorization.get(0).getId() != 0) {
                        String categories = "";
                        for (Categorization categorization : listCategorization) {
                            categories = categories + "," + String.valueOf(categorization.getId());
                        }

                        if (!categories.equals("")) {
                            categories = categories.substring(1, categories.length());
                            where += " AND id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + categories + ") ) ";
                        }

                    }
                }
            }
        }

        return stockDao.batchUpdate(where, changeField, stock);

    }

    @Override
    public Stock findStockLastPrice(int stockId, BranchSetting branchSetting) {
        return stockDao.findStockLastPrice(stockId, branchSetting);
    }

    @Override
    public List<Stock> totals(String where) {
        return stockDao.totals(where);
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
    public List<StockUpload> processUploadFile(InputStream inputStream) {
        StockUpload stockItem = new StockUpload();
        List<StockUpload> excelStockList = new ArrayList<>();
        try {
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream); // HSSF veya XSSF olarak oluşması için bu şekilde çalışma dosyası oluşturuldu.
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows;
            rows = sheet.getPhysicalNumberOfRows();
            int cols = 4;
            int tmp = 0;

            for (int i = 0; i < 10 || i < rows; i++) {
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

                stockItem = new StockUpload();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 
                    stockItem.setExcelDataType(1);
                    if (row.getCell(0) != null) { //ürün adı 
                        try {
                            CellValue cellValue0 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue0.getCellTypeEnum()) {
                                case STRING:
                                    stockItem.setName(String.valueOf(String.valueOf(row.getCell(0).getRichStringCellValue())));
                                    break;
                                case NUMERIC:
                                    String sname = String.valueOf(row.getCell(0).getNumericCellValue());
                                    BigDecimal sNameBigDecimal = new BigDecimal(sname);
                                    BigInteger bigInteger = sNameBigDecimal.toBigInteger();
                                    String n = String.valueOf(bigInteger);

                                    stockItem.setName(n);

                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setName("");
                        }
                    } else if (row.getCell(0) == null) {
                        stockItem.setName("");
                    }

                    if (row.getCell(1) != null) { // Kod
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) {
                                case NUMERIC:
                                    String sCode = String.valueOf(row.getCell(1).getNumericCellValue()); // bilimsel gösterimdeki stok kodu değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal sCodeBigDecimal = new BigDecimal(sCode);
                                    BigInteger bigInteger = sCodeBigDecimal.toBigInteger();
                                    String code = String.valueOf(bigInteger);

                                    stockItem.setCode(code);

                                    break;
                                case STRING:
                                    stockItem.setCode(String.valueOf(row.getCell(1).getRichStringCellValue()));
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setCode("");

                        }
                    } else if (row.getCell(1) == null) {
                        stockItem.setCode("");

                    }

                    if (row.getCell(2) != null) {
                        try {
                            CellValue cellValue2 = evaluator.evaluate(row.getCell(2));
                            switch (cellValue2.getCellTypeEnum()) { //Barcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(2).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.setBarcode(String.valueOf(row.getCell(2).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.setBarcode("-1");
                            stockItem.setExcelDataType(-1);

                        }
                    } else if (row.getCell(2) == null) {
                        stockItem.setBarcode("-1");
                        stockItem.setExcelDataType(-1);

                    }

                    if (row.getCell(3) != null) {
                        try {
                            CellValue cellValue3 = evaluator.evaluate(row.getCell(3));
                            switch (cellValue3.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dUnit = row.getCell(3).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int unit = dUnit.intValue();

                                    stockItem.getUnit().setId(unit);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(3).getRichStringCellValue()));
                                    stockItem.getUnit().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getUnit().setId(-1);
                        }
                    } else if (row.getCell(3) == null) {
                        stockItem.getUnit().setId(-1);
                    }

                    if (row.getCell(4) != null) {
                        try {
                            CellValue cellValue4 = evaluator.evaluate(row.getCell(4));
                            switch (cellValue4.getCellTypeEnum()) { // Servis mi 
                                case NUMERIC:
                                    Double bData = row.getCell(4).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        stockItem.setIsServiceTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.setIsServiceTemp(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(4).getRichStringCellValue()));
                                    if (value == 1) {
                                        stockItem.setIsServiceTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.setIsServiceTemp(Boolean.FALSE);
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setIsServiceTemp(null);
                        }
                    } else if (row.getCell(4) == null) {
                        stockItem.setIsServiceTemp(null);
                    }

                    if (row.getCell(5) != null) {
                        try {
                            CellValue cellValue5 = evaluator.evaluate(row.getCell(5));
                            switch (cellValue5.getCellTypeEnum()) { // Hızlı Satış
                                case NUMERIC:
                                    Double bData = row.getCell(5).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(5).getRichStringCellValue()));
                                    if (value == 1) {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.FALSE);
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setIsQuickSaleTemp(null);
                        }
                    } else if (row.getCell(5) == null) {
                        stockItem.getStockInfo().setIsQuickSaleTemp(null);
                    }
                    if (row.getCell(6) != null) { // marka
                        try {
                            CellValue cellValue6 = evaluator.evaluate(row.getCell(6));
                            switch (cellValue6.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dBrand = row.getCell(6).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int unit = dBrand.intValue();

                                    stockItem.getBrand().setId(unit);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(6).getRichStringCellValue()));
                                    stockItem.getBrand().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getBrand().setId(-1);
                        }
                    } else if (row.getCell(6) == null) {
                        stockItem.getBrand().setId(-1);
                    }

                    if (row.getCell(7) != null) { // tedarikçi 
                        try {
                            CellValue cellValue7 = evaluator.evaluate(row.getCell(7));
                            switch (cellValue7.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dSupplier = row.getCell(7).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int unit = dSupplier.intValue();

                                    stockItem.getSupplier().setId(unit);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(7).getRichStringCellValue()));
                                    stockItem.getSupplier().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getSupplier().setId(-1);
                        }
                    } else if (row.getCell(7) == null) {
                        stockItem.getSupplier().setId(-1);
                    }

                    if (row.getCell(8) != null) { // tedarikçi ürün kodu
                        try {
                            CellValue cellValue8 = evaluator.evaluate(row.getCell(8));
                            switch (cellValue8.getCellTypeEnum()) {
                                case NUMERIC:
                                    String sCode = String.valueOf(row.getCell(8).getNumericCellValue());
                                    BigDecimal sCodeBigDecimal = new BigDecimal(sCode);
                                    BigInteger bigInteger = sCodeBigDecimal.toBigInteger();
                                    String code = String.valueOf(bigInteger);

                                    stockItem.setSupplierProductCode(code);
                                    break;
                                case STRING:
                                    stockItem.setSupplierProductCode(String.valueOf(row.getCell(8).getRichStringCellValue()));
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setSupplierProductCode(null);
                        }
                    } else if (row.getCell(8) == null) {
                        stockItem.setSupplierProductCode(null);
                    }

                    if (row.getCell(9) != null) { // üretim yeri bilgisi
                        try {
                            CellValue cellValue9 = evaluator.evaluate(row.getCell(9));
                            switch (cellValue9.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dCountry = row.getCell(9).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int unit = dCountry.intValue();

                                    stockItem.getCountry().setId(unit);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(9).getRichStringCellValue()));
                                    stockItem.getCountry().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getCountry().setId(-1);
                        }
                    } else if (row.getCell(9) == null) {
                        stockItem.getCountry().setId(-1);
                    }

                    if (row.getCell(10) != null) {
                        try {
                            CellValue cellValue10 = evaluator.evaluate(row.getCell(10));
                            switch (cellValue10.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dLevel = row.getCell(10).getNumericCellValue();
                                    BigDecimal bMinStockLevel = new BigDecimal(dLevel);

                                    stockItem.getStockInfo().setMinStockLevel(bMinStockLevel);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setMinStockLevel(null);
                        }

                    } else if (row.getCell(10) == null) {
                        stockItem.getStockInfo().setMinStockLevel(null);
                    }

                    if (row.getCell(11) != null) { //max stock level
                        try {
                            CellValue cellValue11 = evaluator.evaluate(row.getCell(11));
                            switch (cellValue11.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dLevel = row.getCell(11).getNumericCellValue();
                                    BigDecimal bMaxStockLevel = new BigDecimal(dLevel);

                                    stockItem.getStockInfo().setMaxStockLevel(bMaxStockLevel);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setMaxStockLevel(null);
                        }

                    } else if (row.getCell(11) == null) {
                        stockItem.getStockInfo().setMaxStockLevel(null);
                    }

                    if (row.getCell(12) != null) { // / Stok Eksiye Düşebilir Mi
                        try {
                            CellValue cellValue12 = evaluator.evaluate(row.getCell(12));
                            switch (cellValue12.getCellTypeEnum()) { // Stok Eksiye Düşebilir Mi
                                case NUMERIC:
                                    Double bData = row.getCell(12).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(12).getRichStringCellValue()));
                                    if (value == 1) {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.FALSE);
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setIsMinusStockLevelTemp(null);
                        }
                    } else if (row.getCell(12) == null) {
                        stockItem.getStockInfo().setIsMinusStockLevelTemp(null);
                    }

                    if (row.getCell(13) != null) { // tax department 
                        try {
                            CellValue cellValue13 = evaluator.evaluate(row.getCell(13));
                            switch (cellValue13.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dTaxDep = row.getCell(13).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int dep = dTaxDep.intValue();

                                    stockItem.getStockInfo().getTaxDepartment().setId(dep);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(13).getRichStringCellValue()));
                                    stockItem.getStockInfo().getTaxDepartment().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().getTaxDepartment().setId(-1);
                        }
                    } else if (row.getCell(13) == null) {
                        stockItem.getStockInfo().getTaxDepartment().setId(-1);
                    }

                    if (row.getCell(14) != null) { // ürün tipi
                        try {
                            CellValue cellValue14 = evaluator.evaluate(row.getCell(14));
                            switch (cellValue14.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dStockTypeId = row.getCell(14).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int stock = dStockTypeId.intValue();

                                    stockItem.setStockType_id(stock);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(14).getRichStringCellValue()));
                                    stockItem.setStockType_id(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setStockType_id(-1);
                        }
                    } else if (row.getCell(14) == null) {
                        stockItem.setStockType_id(-1);
                    }

                    if (row.getCell(15) != null) {
                        try {
                            CellValue cellValue15 = evaluator.evaluate(row.getCell(15));
                            switch (cellValue15.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dTaxGroup = row.getCell(15).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int taxGroup = dTaxGroup.intValue();
                                    stockItem.setSaleTaxGruopId(taxGroup);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(15).getRichStringCellValue()));
                                    stockItem.setSaleTaxGruopId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setSaleTaxGruopId(-1);
                        }
                    } else if (row.getCell(15) == null) {
                        stockItem.setSaleTaxGruopId(-1);
                    }

                    if (row.getCell(16) != null) {
                        try {
                            CellValue cellValue16 = evaluator.evaluate(row.getCell(16));
                            switch (cellValue16.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dTaxGroup = row.getCell(16).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int taxGroupPurchase = dTaxGroup.intValue();

                                    stockItem.setPurchaseTaxGroupId(taxGroupPurchase);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(16).getRichStringCellValue()));
                                    stockItem.setPurchaseTaxGroupId(value);
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.setPurchaseTaxGroupId(-1);
                        }
                    } else if (row.getCell(16) == null) {
                        stockItem.setPurchaseTaxGroupId(-1);
                    }
                    if (row.getCell(17) != null) {
                        try {
                            CellValue cellValue17 = evaluator.evaluate(row.getCell(17));
                            switch (cellValue17.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(17).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.getAlternativeBarcode().setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.getAlternativeBarcode().setBarcode(String.valueOf(row.getCell(17).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode().setBarcode(null);
                        }
                    } else if (row.getCell(17) == null) {
                        stockItem.getAlternativeBarcode().setBarcode(null);
                    }

                    if (row.getCell(18) != null) {
                        try {
                            CellValue cellValue18 = evaluator.evaluate(row.getCell(18));
                            switch (cellValue18.getCellTypeEnum()) { //Alternatif barkod karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(18).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    stockItem.getAlternativeBarcode().setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(18).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    stockItem.getAlternativeBarcode().setQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode().setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(18) == null) {
                        stockItem.getAlternativeBarcode().setQuantity(BigDecimal.valueOf(0));
                    }
                    if (row.getCell(19) != null) {
                        try {
                            CellValue cellValue19 = evaluator.evaluate(row.getCell(19));
                            switch (cellValue19.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(19).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.getAlternativeBarcode2().setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.getAlternativeBarcode2().setBarcode(String.valueOf(row.getCell(19).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode2().setBarcode(null);
                        }
                    } else if (row.getCell(19) == null) {
                        stockItem.getAlternativeBarcode2().setBarcode(null);
                    }

                    if (row.getCell(20) != null) {
                        try {
                            CellValue cellValue20 = evaluator.evaluate(row.getCell(20));
                            switch (cellValue20.getCellTypeEnum()) { //Alternatif barkod 2 karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(20).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    stockItem.getAlternativeBarcode2().setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(20).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    stockItem.getAlternativeBarcode2().setQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode2().setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(20) == null) {
                        stockItem.getAlternativeBarcode2().setQuantity(BigDecimal.valueOf(0));
                    }

                    if (row.getCell(21) != null) {
                        try {
                            CellValue cellValue21 = evaluator.evaluate(row.getCell(21));
                            switch (cellValue21.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(21).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.getAlternativeBarcode3().setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.getAlternativeBarcode3().setBarcode(String.valueOf(row.getCell(21).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode3().setBarcode(null);
                        }
                    } else if (row.getCell(21) == null) {
                        stockItem.getAlternativeBarcode3().setBarcode(null);
                    }

                    if (row.getCell(22) != null) {
                        try {
                            CellValue cellValue22 = evaluator.evaluate(row.getCell(22));
                            switch (cellValue22.getCellTypeEnum()) { //Alternatif barkod 3 karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(22).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    stockItem.getAlternativeBarcode3().setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(22).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    stockItem.getAlternativeBarcode3().setQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode3().setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(22) == null) {
                        stockItem.getAlternativeBarcode3().setQuantity(BigDecimal.valueOf(0));
                    }

                    if (row.getCell(23) != null) {
                        try {
                            CellValue cellValue23 = evaluator.evaluate(row.getCell(23));
                            switch (cellValue23.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(23).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.getAlternativeBarcode4().setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.getAlternativeBarcode4().setBarcode(String.valueOf(row.getCell(23).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode4().setBarcode(null);
                        }
                    } else if (row.getCell(23) == null) {
                        stockItem.getAlternativeBarcode4().setBarcode(null);
                    }

                    if (row.getCell(24) != null) {
                        try {
                            CellValue cellValue24 = evaluator.evaluate(row.getCell(24));
                            switch (cellValue24.getCellTypeEnum()) { //Alternatif barkod 3 karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(24).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    stockItem.getAlternativeBarcode4().setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(24).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    stockItem.getAlternativeBarcode4().setQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode4().setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(24) == null) {
                        stockItem.getAlternativeBarcode4().setQuantity(BigDecimal.valueOf(0));
                    }

                    if (row.getCell(25) != null) {
                        try {
                            CellValue cellValue25 = evaluator.evaluate(row.getCell(25));
                            switch (cellValue25.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(25).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.getAlternativeBarcode5().setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.getAlternativeBarcode5().setBarcode(String.valueOf(row.getCell(25).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode5().setBarcode(null);
                        }
                    } else if (row.getCell(25) == null) {
                        stockItem.getAlternativeBarcode5().setBarcode(null);
                    }

                    if (row.getCell(26) != null) {
                        try {
                            CellValue cellValue26 = evaluator.evaluate(row.getCell(26));
                            switch (cellValue26.getCellTypeEnum()) { //Alternatif barkod 3 karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(26).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    stockItem.getAlternativeBarcode5().setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(26).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    stockItem.getAlternativeBarcode5().setQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.getAlternativeBarcode5().setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(26) == null) {
                        stockItem.getAlternativeBarcode5().setQuantity(BigDecimal.valueOf(0));
                    }

                    ///////////////////////////Ana Kategori ////////////////
                    if (row.getCell(27) != null) {
                        try {
                            CellValue cellValue27 = evaluator.evaluate(row.getCell(27));
                            switch (cellValue27.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double bData = row.getCell(27).getNumericCellValue();
                                    int value = bData.intValue();
                                    stockItem.getParentCategory().setId(value);
                                    break;
                                default:
                                    int value2 = Integer.valueOf(String.valueOf(row.getCell(27).getRichStringCellValue()));
                                    stockItem.getParentCategory().setId(value2);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getParentCategory().setId(-1);
                        }
                    } else if (row.getCell(27) == null) {
                        stockItem.getParentCategory().setId(-1);
                    }

                    ///////////////////////////ALt Kategori ////////////////
                    if (row.getCell(28) != null) {
                        try {
                            CellValue cellValue28 = evaluator.evaluate(row.getCell(28));
                            switch (cellValue28.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double bData = row.getCell(28).getNumericCellValue();
                                    int value = bData.intValue();
                                    stockItem.getSubCategoty().setId(value);
                                    break;
                                default:
                                    int value2 = Integer.valueOf(String.valueOf(row.getCell(28).getRichStringCellValue()));
                                    stockItem.getSubCategoty().setId(value2);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getSubCategoty().setId(-1);
                        }
                    } else if (row.getCell(28) == null) {
                        stockItem.getSubCategoty().setId(-1);
                    }

                    excelStockList.add(stockItem);

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
    public List<StockUpload> processUploadFileForCentral(InputStream inputStream) {
        StockUpload stockItem = new StockUpload();
        List<StockUpload> excelStockList = new ArrayList<>();
        try {
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream); // HSSF veya XSSF olarak oluşması için bu şekilde çalışma dosyası oluşturuldu.
            Sheet sheet = workbook.getSheetAt(0);
            Row row;

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            excelStockList.clear();
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);

                stockItem = new StockUpload();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 
                    stockItem.setExcelDataType(1);
                    if (row.getCell(0) != null) { //Barkod
                        try {
                            CellValue cellValue2 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue2.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stockItem.setBarcode(barcode);

                                    break;
                                case STRING:
                                    stockItem.setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.setBarcode("-1");
                            stockItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(0) == null) {
                        stockItem.setBarcode("-1");
                        stockItem.setExcelDataType(-1);
                    }
                    if (row.getCell(1) != null) { // Kod
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) {
                                case NUMERIC:
                                    String sCode = String.valueOf(row.getCell(1).getNumericCellValue()); // bilimsel gösterimdeki stok kodu değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal sCodeBigDecimal = new BigDecimal(sCode);
                                    BigInteger bigInteger = sCodeBigDecimal.toBigInteger();
                                    String code = String.valueOf(bigInteger);

                                    stockItem.setCode(code);

                                    break;
                                case STRING:
                                    stockItem.setCode(String.valueOf(row.getCell(1).getRichStringCellValue()));
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.setCode("-1");

                        }
                    } else if (row.getCell(1) == null) {
                        stockItem.setCode("-1");

                    }

                    if (row.getCell(2) != null) { // üretim yeri bilgisi
                        try {
                            CellValue cellValue9 = evaluator.evaluate(row.getCell(2));
                            switch (cellValue9.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dCountry = row.getCell(2).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int c = dCountry.intValue();

                                    stockItem.getCountry().setId(c);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(2).getRichStringCellValue()));
                                    stockItem.getCountry().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getCountry().setId(-1);
                        }
                    } else if (row.getCell(2) == null) {
                        stockItem.getCountry().setId(-1);
                    }

                    if (row.getCell(3) != null) { // tedarikçi 
                        try {
                            CellValue cellValue73 = evaluator.evaluate(row.getCell(3));
                            switch (cellValue73.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dSupplier = row.getCell(3).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int unit = dSupplier.intValue();

                                    stockItem.getSupplier().setId(unit);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(3).getRichStringCellValue()));
                                    stockItem.getSupplier().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getSupplier().setId(-1);
                        }
                    } else if (row.getCell(3) == null) {
                        stockItem.getSupplier().setId(-1);
                    }

                    if (row.getCell(4) != null) { // tedarikçi ürün kodu
                        try {
                            CellValue cellValue84 = evaluator.evaluate(row.getCell(4));
                            switch (cellValue84.getCellTypeEnum()) {
                                case NUMERIC:
                                    String sCode = String.valueOf(row.getCell(4).getNumericCellValue());
                                    BigDecimal sCodeBigDecimal = new BigDecimal(sCode);
                                    BigInteger bigInteger = sCodeBigDecimal.toBigInteger();
                                    String code = String.valueOf(bigInteger);

                                    stockItem.setSupplierProductCode(code);
                                    break;

                                case STRING:
                                    stockItem.setSupplierProductCode(String.valueOf(row.getCell(4).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            stockItem.setSupplierProductCode(null);
                        }
                    } else if (row.getCell(4) == null) {
                        stockItem.setSupplierProductCode(null);
                    }
                    if (row.getCell(5) != null) {// Hızlı Satış
                        try {
                            CellValue cellValue5 = evaluator.evaluate(row.getCell(5));
                            switch (cellValue5.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double bData = row.getCell(5).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(5).getRichStringCellValue()));
                                    if (value == 1) {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsQuickSaleTemp(Boolean.FALSE);
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setIsQuickSaleTemp(null);
                        }
                    } else if (row.getCell(5) == null) {
                        stockItem.getStockInfo().setIsQuickSaleTemp(null);
                    }

                    if (row.getCell(6) != null) {//mİN
                        try {
                            CellValue cellValue10 = evaluator.evaluate(row.getCell(6));
                            switch (cellValue10.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dLevel = row.getCell(6).getNumericCellValue();
                                    BigDecimal bMinStockLevel = new BigDecimal(dLevel);

                                    stockItem.getStockInfo().setMinStockLevel(bMinStockLevel);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setMinStockLevel(null);
                        }

                    } else if (row.getCell(6) == null) {
                        stockItem.getStockInfo().setMinStockLevel(null);
                    }

                    if (row.getCell(7) != null) { //max stock level
                        try {
                            CellValue cellValue10 = evaluator.evaluate(row.getCell(7));
                            switch (cellValue10.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dLevel = row.getCell(7).getNumericCellValue();
                                    BigDecimal bMaxStockLevel = new BigDecimal(dLevel);

                                    stockItem.getStockInfo().setMaxStockLevel(bMaxStockLevel);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setMaxStockLevel(null);
                        }

                    } else if (row.getCell(7) == null) {
                        stockItem.getStockInfo().setMaxStockLevel(null);
                    }

                    if (row.getCell(8) != null) { // / Stok Eksiye Düşebilir Mi
                        try {
                            CellValue cellValue5 = evaluator.evaluate(row.getCell(8));
                            switch (cellValue5.getCellTypeEnum()) { // Stok Eksiye Düşebilir Mi
                                case NUMERIC:
                                    Double bData = row.getCell(8).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(8).getRichStringCellValue()));
                                    if (value == 1) {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.TRUE);
                                    } else {
                                        stockItem.getStockInfo().setIsMinusStockLevelTemp(Boolean.FALSE);
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().setIsMinusStockLevelTemp(null);
                        }
                    } else if (row.getCell(8) == null) {
                        stockItem.getStockInfo().setIsMinusStockLevelTemp(null);
                    }

                    if (row.getCell(9) != null) { // tax department 
                        try {
                            CellValue cellValue7 = evaluator.evaluate(row.getCell(9));
                            switch (cellValue7.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double dTaxDep = row.getCell(9).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int dep = dTaxDep.intValue();

                                    stockItem.getStockInfo().getTaxDepartment().setId(dep);
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(9).getRichStringCellValue()));
                                    stockItem.getStockInfo().getTaxDepartment().setId(value);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getStockInfo().getTaxDepartment().setId(-1);
                        }
                    } else if (row.getCell(9) == null) {
                        stockItem.getStockInfo().getTaxDepartment().setId(-1);
                    }

                    if (row.getCell(10) != null) { //Ana Kategori
                        try {
                            CellValue cellValue23 = evaluator.evaluate(row.getCell(10));
                            switch (cellValue23.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double bData = row.getCell(10).getNumericCellValue();
                                    int value = bData.intValue();
                                    stockItem.getParentCategory().setId(value);
                                    break;
                                default:
                                    int value2 = Integer.valueOf(String.valueOf(row.getCell(10).getRichStringCellValue()));
                                    stockItem.getParentCategory().setId(value2);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getParentCategory().setId(-1);
                        }
                    } else if (row.getCell(10) == null) {
                        stockItem.getParentCategory().setId(-1);
                    }

                    ///////////////////////////ALt Kategori ////////////////
                    if (row.getCell(11) != null) {
                        try {
                            CellValue cellValue24 = evaluator.evaluate(row.getCell(11));
                            switch (cellValue24.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double bData = row.getCell(11).getNumericCellValue();
                                    int value = bData.intValue();
                                    stockItem.getSubCategoty().setId(value);
                                    break;
                                default:
                                    int value2 = Integer.valueOf(String.valueOf(row.getCell(11).getRichStringCellValue()));
                                    stockItem.getSubCategoty().setId(value2);
                                    break;
                            }

                        } catch (Exception e) {
                            stockItem.getSubCategoty().setId(-1);
                        }
                    } else if (row.getCell(11) == null) {
                        stockItem.getSubCategoty().setId(-1);
                    }

                    excelStockList.add(stockItem);

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
    public List<StockUpload> openUploadProcessPage() {
        List<StockUpload> sampleList = new ArrayList<>();
        /**
         * Örnek formattak bilgilerin girişinin yapıldığı yerdir.
         */
        StockUpload stock = new StockUpload();

        stock.setName("Browni Intense");
        stock.setCode("8694470529943");
        stock.setBarcode("8690504080886");
        stock.getUnit().setId(7);
        stock.setIsServiceTemp(true);
        stock.getStockInfo().setIsQuickSaleTemp(true);
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(50.0));
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(500.0));
        stock.setSaleTaxGruopId(4);
        stock.setPurchaseTaxGroupId(2);
        stock.getBrand().setId(2);
        stock.getSupplier().setId(5);
        stock.setSupplierProductCode("CD123");
        stock.getCountry().setId(123);
        stock.getAlternativeBarcode().setBarcode("1234546");
        stock.getAlternativeBarcode().setQuantity(BigDecimal.valueOf(12));
        stock.getAlternativeBarcode2().setBarcode("67887789");
        stock.getAlternativeBarcode2().setQuantity(BigDecimal.valueOf(6));
        stock.getAlternativeBarcode3().setBarcode("789789056");
        stock.getAlternativeBarcode3().setQuantity(BigDecimal.valueOf(2));
        stock.getAlternativeBarcode4().setBarcode("21334345");
        stock.getAlternativeBarcode4().setQuantity(BigDecimal.valueOf(70));
        stock.getAlternativeBarcode5().setBarcode("9805645");
        stock.getAlternativeBarcode5().setQuantity(BigDecimal.valueOf(3));
        stock.getParentCategory().setId(1);
        stock.getSubCategoty().setId(2);
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(400));
        stock.getStockInfo().setIsMinusStockLevelTemp(true);
        stock.getStockInfo().getTaxDepartment().setId(1);
        stock.setStockType_id(1);

        sampleList.add(stock);
        stock = new StockUpload();
        stock.setName("Ice Tea Mango 1.5LT");
        stock.setCode("8692534394322");
        stock.setBarcode("8690458962536");
        stock.getUnit().setId(5);
        stock.setIsServiceTemp(false);
        stock.getStockInfo().setIsQuickSaleTemp(true);
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(60.0));
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(600.0));
        stock.setSaleTaxGruopId(1);
        stock.setPurchaseTaxGroupId(3);
        stock.getBrand().setId(5);
        stock.getSupplier().setId(0);
        stock.setSupplierProductCode("TT-1");
        stock.getCountry().setId(1);
        stock.getAlternativeBarcode().setBarcode("6767867");
        stock.getAlternativeBarcode().setQuantity(BigDecimal.valueOf(3));
        stock.getAlternativeBarcode2().setBarcode("");
        stock.getAlternativeBarcode2().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode3().setBarcode("7878989");
        stock.getAlternativeBarcode3().setQuantity(BigDecimal.valueOf(14));
        stock.getAlternativeBarcode4().setBarcode("86525454");
        stock.getAlternativeBarcode4().setQuantity(BigDecimal.valueOf(9));
        stock.getAlternativeBarcode5().setBarcode("4200451242");
        stock.getAlternativeBarcode5().setQuantity(BigDecimal.valueOf(30));
        stock.getParentCategory().setId(4);
        stock.getSubCategoty().setId(8);
        stock.getStockInfo().setIsMinusStockLevelTemp(false);
        stock.getStockInfo().getTaxDepartment().setId(4);
        stock.setStockType_id(2);

        sampleList.add(stock);
        stock = new StockUpload();

        stock.setName("MAGNUM DOUBLE KARADUT 100ML");
        stock.setCode("8690637788505");
        stock.setBarcode("86906377885051");
        stock.getUnit().setId(2);
        stock.setIsServiceTemp(false);
        stock.getStockInfo().setIsQuickSaleTemp(false);
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(15.0));
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(150.0));
        stock.setSaleTaxGruopId(3);
        stock.setPurchaseTaxGroupId(4);
        stock.getBrand().setId(30);
        stock.getSupplier().setId(70);
        stock.setSupplierProductCode("TT-2");
        stock.getCountry().setId(45);
        stock.getAlternativeBarcode().setBarcode("5y77867879");
        stock.getAlternativeBarcode().setQuantity(BigDecimal.valueOf(30));
        stock.getAlternativeBarcode2().setBarcode("");
        stock.getAlternativeBarcode2().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode3().setBarcode("");
        stock.getAlternativeBarcode3().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode4().setBarcode("");
        stock.getAlternativeBarcode4().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode5().setBarcode("");
        stock.getAlternativeBarcode5().setQuantity(BigDecimal.valueOf(0));
        stock.getParentCategory().setId(4);
        stock.getSubCategoty().setId(8);
        stock.getStockInfo().setIsMinusStockLevelTemp(true);
        stock.getStockInfo().getTaxDepartment().setId(2);
        stock.setStockType_id(2);

        sampleList.add(stock);
        stock = new StockUpload();

        stock.setName("Form Limonlu Bisküvi");
        stock.setCode("225");
        stock.setBarcode("534545");
        stock.getUnit().setId(6);
        stock.setIsServiceTemp(true);
        stock.getStockInfo().setIsQuickSaleTemp(true);
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(25));
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(250));
        stock.setSaleTaxGruopId(2);
        stock.setPurchaseTaxGroupId(1);
        stock.getBrand().setId(78);
        stock.getSupplier().setId(12);
        stock.setSupplierProductCode("TT-3");
        stock.getCountry().setId(55);
        stock.getAlternativeBarcode().setBarcode("");
        stock.getAlternativeBarcode().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode2().setBarcode("");
        stock.getAlternativeBarcode2().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode3().setBarcode("");
        stock.getAlternativeBarcode3().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode4().setBarcode("");
        stock.getAlternativeBarcode4().setQuantity(BigDecimal.valueOf(0));
        stock.getAlternativeBarcode5().setBarcode("");
        stock.getAlternativeBarcode5().setQuantity(BigDecimal.valueOf(0));
        stock.getParentCategory().setId(45);
        stock.getSubCategoty().setId(43);
        stock.getStockInfo().setIsMinusStockLevelTemp(false);
        stock.getStockInfo().getTaxDepartment().setId(4);
        stock.setStockType_id(1);

        sampleList.add(stock);

        return sampleList;
    }

    @Override
    public List<StockUpload> openUploadProcessPageForCentral() {
        List<StockUpload> sampleList = new ArrayList<>();
        /**
         * Örnek formattak bilgilerin girişinin yapıldığı yerdir.
         */
        StockUpload stock = new StockUpload();

        stock.setBarcode("8690506");
        stock.setCode("1589");
        stock.getCountry().setId(1);
        stock.getStockInfo().setIsQuickSaleTemp(true);
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(45));
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(450));
        stock.getStockInfo().setIsMinusStockLevelTemp(false);
        stock.getStockInfo().getTaxDepartment().setId(1);
        stock.getParentCategory().setId(45);
        stock.getSubCategoty().setId(43);
        stock.getSupplier().setId(5);
        stock.setSupplierProductCode("CD123");

        sampleList.add(stock);
        stock = new StockUpload();

        stock.setBarcode("86906377");
        stock.setCode("7894");
        stock.getCountry().setId(45);
        stock.getStockInfo().setIsQuickSaleTemp(false);
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(10));
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(100));
        stock.getStockInfo().setIsMinusStockLevelTemp(true);
        stock.getStockInfo().getTaxDepartment().setId(2);
        stock.getParentCategory().setId(15);
        stock.getSubCategoty().setId(20);
        stock.getSupplier().setId(6);
        stock.setSupplierProductCode("A454");

        sampleList.add(stock);
        stock = new StockUpload();

        stock.setBarcode("534545");
        stock.setCode("225");
        stock.getCountry().setId(55);
        stock.getStockInfo().setIsQuickSaleTemp(false);
        stock.getStockInfo().setMinStockLevel(BigDecimal.valueOf(25));
        stock.getStockInfo().setMaxStockLevel(BigDecimal.valueOf(250));
        stock.getStockInfo().setIsMinusStockLevelTemp(false);
        stock.getStockInfo().getTaxDepartment().setId(1);
        stock.getParentCategory().setId(78);
        stock.getSubCategoty().setId(80);
        // stock.getSupplier().setId(16);
        sampleList.add(stock);

        return sampleList;
    }

    @Override
    public List<Stock> findFuelStock() {
        return stockDao.findFuelStock();
    }

    @Override
    public Stock findStokcUnit(String barcode, Invoice obj, boolean isAlternativeBarcode, BranchSetting branchSetting) {
        return stockDao.findStokcUnit(barcode, obj, isAlternativeBarcode, branchSetting);
    }

    @Override
    public Stock findStockBarcode(String barcode) {
        return stockDao.findStockBarcode(barcode);
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList) {

        String control = "";
        String newValue = "";

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            List<Boolean> tempToogleList = new ArrayList<>();
            tempToogleList.addAll(toogleList);

            tempToogleList.add(Boolean.TRUE);
            tempToogleList.add(Boolean.TRUE);
            tempToogleList.add(Boolean.TRUE);
            tempToogleList.add(Boolean.TRUE);
            tempToogleList.add(Boolean.TRUE);

            connection = stockDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockDao.exportData(where));

            prep.setInt(1, sessionBean.getUser().getLanguage().getId());
            prep.setInt(2, sessionBean.getUser().getLanguage().getId());
            prep.setInt(3, sessionBean.getUser().getLastBranch().getId());
            prep.setInt(4, sessionBean.getUser().getLanguage().getId());
            prep.setInt(5, sessionBean.getUser().getLastBranch().getId());
            prep.setInt(6, sessionBean.getUser().getLastBranch().getId());

            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(tempToogleList, 1);
            pdfDocument.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

            List<Float> listOfColumnWidth = new ArrayList<Float>();

//            float[] widths = {22f, 20f, 8f, 8f, 9f, 12f, 10f, 10f,
//                10f, 10f, 12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f,
//                8f, 6f, 20f, 5f};
//            pdfDocument.getPdfTable().setWidths(widths);
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stocks"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("abbreviationdescriptions"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stockcodeshortname") + "  :  " + sessionBean.getLoc().getString("stockcode") + "                                              -     " + sessionBean.getLoc().getString("centerproductcodeshortname") + "  :  " + sessionBean.getLoc().getString("centerstockcode"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("recommendedsalepriceshortname") + "  :  " + sessionBean.getLoc().getString("recommendedsaleprice") + "                            -     " + sessionBean.getLoc().getString("recommendedpurchasepriceshortname") + "  :  " + sessionBean.getLoc().getString("recommendedpurchaseprice") + "           - " + sessionBean.getLoc().getString("mandatorysalepriceshortname") + "  :  " + sessionBean.getLoc().getString("mandatorysaleprice"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasequantityshortname") + "  :  " + sessionBean.getLoc().getString("purchasequantity") + "                                         -     " + sessionBean.getLoc().getString("salesamountshortname") + "  :  " + sessionBean.getLoc().getString("salesamount"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("availablequantityshortname") + "  :  " + sessionBean.getLoc().getString("availablequantity") + "                                    -     " + sessionBean.getLoc().getString("otherınputoutputsshortname") + "  :  " + sessionBean.getLoc().getString("otherınputoutputs"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("lastpurchasepricewithtaxshortname") + "  :  " + sessionBean.getLoc().getString("lastpurchaseprice") + " " + sessionBean.getLoc().getString("withtax") + "                        -     " + sessionBean.getLoc().getString("lastpurchasepricewithouttaxshortname") + "  :  " + sessionBean.getLoc().getString("lastpurchaseprice") + " " + sessionBean.getLoc().getString("withouttax"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("lastsalepricewithtaxshortname") + "  :  " + sessionBean.getLoc().getString("lastsaleprice") + " " + sessionBean.getLoc().getString("withtax") + "                      -     " + sessionBean.getLoc().getString("lastsalepricewithouttaxshortname") + "  :  " + sessionBean.getLoc().getString("lastsaleprice") + " " + sessionBean.getLoc().getString("withouttax"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("availablestockpurchasepricewithtaxshortname") + "  :  " + sessionBean.getLoc().getString("availablestockpurchaseprice") + " " + sessionBean.getLoc().getString("withtax") + "       -     " + sessionBean.getLoc().getString("availablestockpurchasepricewithouttaxshortname") + "  :  " + sessionBean.getLoc().getString("availablestockpurchaseprice") + " " + sessionBean.getLoc().getString("withouttax"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("availablestocksalepricewithtaxshortname") + "  :  " + sessionBean.getLoc().getString("availablestocksaleprice") + " " + sessionBean.getLoc().getString("withtax") + "     -     " + sessionBean.getLoc().getString("availablestocksalepricewithouttaxshortname") + "  :  " + sessionBean.getLoc().getString("availablestocksaleprice") + " " + sessionBean.getLoc().getString("withouttax"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("profitpercentageshortname") + "  :  " + sessionBean.getLoc().getString("profitpercentage") + "                                          -     " + sessionBean.getLoc().getString("salestaxshortname") + "  :  " + sessionBean.getLoc().getString("salestax") + "  -    " + sessionBean.getLoc().getString("purchasetaxshortname") + "  :  " + sessionBean.getLoc().getString("purchasetax"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("statushortname") + "  :  " + sessionBean.getLoc().getString("statu") + "                                                    -     " + sessionBean.getLoc().getString("activeshortname") + "  :  " + sessionBean.getLoc().getString("active") + "     -     " + sessionBean.getLoc().getString("passiveshortname") + "  :  " + sessionBean.getLoc().getString("passive"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());
            pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, Color.WHITE));
            if (toogleList.get(0)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("barcode"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(1)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(2)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockcodeshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(3)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("centerproductcodeshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(4)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("unit"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(5)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("departmentname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(6)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("brand"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(7)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(8)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(9)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("recommendedpurchasepriceshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(10)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("recommendedsalepriceshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(11)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("mandatorysalepriceshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(12)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasequantityshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(13)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamountshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(14)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("otherınputoutputsshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(15)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("availablequantityshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(16)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("lastpurchasepricewithtaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(17)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("lastpurchasepricewithouttaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(18)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("lastsalepricewithtaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(19)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("lastsalepricewithouttaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(20)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("availablestockpurchasepricewithtaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(21)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("availablestockpurchasepricewithouttaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(22)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("availablestocksalepricewithtaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(23)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("availablestocksalepricewithouttaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(24)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitpercentageshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(25)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salestaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(26)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasetaxshortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(27)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("category"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(28)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("statushortname"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            for (int i = 0; i < 5; i++) {

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("alternativebarcodeshortname") + " " + Integer.toString(i + 1), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            if (toogleList.get(0)) {
                listOfColumnWidth.add(22f);

            }

            if (toogleList.get(1)) {
                listOfColumnWidth.add(20f);

            }

            if (toogleList.get(2)) {
                listOfColumnWidth.add(9f);

            }

            if (toogleList.get(3)) {
                listOfColumnWidth.add(9f);

            }

            if (toogleList.get(4)) {
                listOfColumnWidth.add(9f);

            }

            if (toogleList.get(5)) {
                listOfColumnWidth.add(9f);

            }

            if (toogleList.get(6)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(7)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(8)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(9)) {
                listOfColumnWidth.add(10f);

            }

            if (toogleList.get(10)) {
                listOfColumnWidth.add(10f);

            }

            if (toogleList.get(11)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(12)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(13)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(14)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(15)) {
                listOfColumnWidth.add(10f);

            }
            if (toogleList.get(16)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(17)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(18)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(19)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(20)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(21)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(22)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(23)) {
                listOfColumnWidth.add(12f);

            }
            if (toogleList.get(24)) {
                listOfColumnWidth.add(8f);

            }
            if (toogleList.get(25)) {
                listOfColumnWidth.add(6f);

            }
            if (toogleList.get(26)) {
                listOfColumnWidth.add(6f);

            }
            if (toogleList.get(27)) {
                listOfColumnWidth.add(20f);

            }
            if (toogleList.get(28)) {
                listOfColumnWidth.add(5f);

            }
            listOfColumnWidth.add(10f);
            listOfColumnWidth.add(10f);
            listOfColumnWidth.add(10f);
            listOfColumnWidth.add(10f);
            listOfColumnWidth.add(10f);

            float[] columnWidths = new float[listOfColumnWidth.size()];
            for (int i = 0; i < listOfColumnWidth.size(); i++) {
                columnWidths[i] = (float) listOfColumnWidth.get(i);

            }

            pdfDocument.getPdfTable().setWidths((float[]) columnWidths);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                if (toogleList.get(0)) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {

                    control = rs.getString("stckname");
                    if (control.length() > 15) {
                        newValue = control.substring(0, 15) + ".";
                    } else {
                        newValue = control.substring(0, control.length());
                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(2)) {

                    control = rs.getString("stckcode");
                    if (rs.getString("stckcode") == null) {
                        newValue = " ";

                    } else if (control.length() > 3) {
                        newValue = control.substring(0, 3) + "..";

                    } else {
                        newValue = control.substring(0, control.length());

                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {

                    control = rs.getString("stckcenterproductcode");
                    if (rs.getString("stckcenterproductcode") == null) {
                        newValue = " ";

                    } else if (control.length() > 3) {
                        newValue = control.substring(0, 3) + "..";

                    } else {
                        newValue = control.substring(0, control.length());

                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("guntname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("txdname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {

                    control = rs.getString("brname");
                    if (rs.getString("brname") == null) {
                        newValue = " ";

                    } else if (control.length() > 8) {
                        newValue = control.substring(0, 8) + ".";

                    } else {
                        newValue = control.substring(0, control.length());

                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {

                    control = rs.getString("accname");
                    if (rs.getString("accname") == null) {
                        newValue = " ";

                    } else if (control.length() > 8) {
                        newValue = control.substring(0, 8) + ".";

                    } else {
                        newValue = control.substring(0, control.length());

                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {

                    control = rs.getString("csppname");
                    if (rs.getString("csppname") == null) {
                        newValue = " ";

                    } else if (control.length() > 8) {
                        newValue = control.substring(0, 8) + ".";

                    } else {
                        newValue = control.substring(0, control.length());

                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sipurchaserecommendedprice")), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sirecommendedprice")), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(11)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sisalemandatoryprice")), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sipurchasecount")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sisalecount")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("otherquantity")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(15)) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("availablequantity")), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(16)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchasepricewithkdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(17)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchaseprice")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(18)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentsaleprice")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(19)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentsalepricewithoutkdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(20)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("availablepurchasepricewithkdv")), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(21)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("availablepurchasepricewithoutkdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(22)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("availablesalepricewithkdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(23)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("availablesalepricewithoutkdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(24)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("profitpercentage")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(25)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("salekdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(26)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("purchasekdv")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(27)) {
                    control = StaticMethods.findCategories(rs.getString("category"));
                    if (StaticMethods.findCategories(rs.getString("category")) == null) {
                        newValue = " ";

                    } else if (control.length() > 18) {
                        newValue = control.substring(0, 18) + "..";

                    } else {
                        newValue = control.substring(0, control.length());

                    }
                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(28)) {
                    control = rs.getString("sttdname");
                    if (rs.getString("sttdname") == null) {

                        newValue = " ";

                    } else {

                        newValue = control.substring(0, 1) + ".";
                        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && rs.getBoolean("sii_passive") == true && rs.getInt("sttdid") == 3) {
                            newValue = newValue + "(P)";
                        }

                    }

                    pdfDocument.getDataCell().setPhrase(new Phrase(newValue, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                String[] split = null;
                if (rs.getString("alternativebarcodes") != null) {
                    split = rs.getString("alternativebarcodes").split(",", 5);

                }
                for (int i = 0; i < 5; i++) {
                    if (split != null && split.length > i) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(split[i], pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                }

            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("stocks"));

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
                Logger.getLogger(StockDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, List<Boolean> toogleList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());

        int numberOfColumns = 0;

        for (boolean b : toogleList) {
            if (b) {
                numberOfColumns++;
            }
        }

        try {
            connection = stockDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockDao.exportData(where));

            prep.setInt(1, sessionBean.getUser().getLanguage().getId());
            prep.setInt(2, sessionBean.getUser().getLanguage().getId());
            prep.setInt(3, sessionBean.getUser().getLastBranch().getId());
            prep.setInt(4, sessionBean.getUser().getLanguage().getId());
            prep.setInt(5, sessionBean.getUser().getLastBranch().getId());
            prep.setInt(6, sessionBean.getUser().getLastBranch().getId());

            rs = prep.executeQuery();

            int jRow = 1;

            int a = 0;
            CellStyle cellStyle = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());
            SXSSFRow rowh = null;
            rowh = excelDocument.getWorkbook().getSheetAt(0).createRow(jRow);

            if (toogleList.get(0)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("barcode"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(1)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("stockname"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(2)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("stockcode"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(3)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("centerstockcode"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(4)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("unit"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(5)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("departmentname"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(6)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("brand"));
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(7)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("supplier"));
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(8)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("centralsupplier"));
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(9)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("recommendedpurchaseprice"));
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(10)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("recommendedsaleprice"));
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(11)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("mandatorysaleprice"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(12)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("purchasequantity"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(13)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("salesamount"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(14)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("otherentryexit"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(15)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("availablequantity"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(16)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("lastpurchaseprice") + "(" + sessionBean.getLoc().getString("withtax") + ")");
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(17)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("lastpurchaseprice") + "(" + sessionBean.getLoc().getString("withouttax") + ")");
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(18)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("lastsaleprice") + "(" + sessionBean.getLoc().getString("withtax") + ")");
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(19)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("lastsaleprice") + "(" + sessionBean.getLoc().getString("withouttax") + ")");
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(20)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("availablestockpurchaseprice") + "(" + sessionBean.getLoc().getString("withtax") + ")");
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(21)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("availablestockpurchaseprice") + "(" + sessionBean.getLoc().getString("withouttax") + ")");
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(22)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("availablestocksaleprice") + "(" + sessionBean.getLoc().getString("withtax") + ")");
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(23)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("availablestocksaleprice") + "(" + sessionBean.getLoc().getString("withouttax") + ")");
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(24)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("profitpercentage"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(25)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("salestax"));
                cell.setCellStyle(cellStyle);
            }
            if (toogleList.get(26)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("purchasetax"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(27)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("category"));
                cell.setCellStyle(cellStyle);
            }

            if (toogleList.get(28)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("statu"));
                cell.setCellStyle(cellStyle);
            }

            for (int i = 0; i < 5; i++) {
                SXSSFRow row = null;
                row = excelDocument.getWorkbook().getSheetAt(0).getRow(excelDocument.getWorkbook().getSheetAt(0).getLastRowNum());
                SXSSFCell cell = row.createCell((short) numberOfColumns + i);
                cell.setCellValue(sessionBean.getLoc().getString("alternativebarcode") + " " + Integer.toString(i + 1));
                cell.setCellStyle(cellStyle);
            }
            jRow++;

            while (rs.next()) {
                int c = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    row.createCell((short) c++).setCellValue(rs.getString("stckbarcode"));

                }
                if (toogleList.get(1)) {
                    row.createCell((short) c++).setCellValue(rs.getString("stckname"));

                }
                if (toogleList.get(2)) {
                    row.createCell((short) c++).setCellValue(rs.getString("stckcode"));

                }

                if (toogleList.get(3)) {
                    row.createCell((short) c++).setCellValue(rs.getString("stckcenterproductcode"));

                }
                if (toogleList.get(4)) {
                    row.createCell((short) c++).setCellValue(rs.getString("guntname"));

                }
                if (toogleList.get(5)) {
                    row.createCell((short) c++).setCellValue(rs.getString("txdname"));

                }
                if (toogleList.get(6)) {
                    row.createCell((short) c++).setCellValue(rs.getString("brname"));

                }
                if (toogleList.get(7)) {
                    row.createCell((short) c++).setCellValue(rs.getString("accname"));

                }
                if (toogleList.get(8)) {
                    row.createCell((short) c++).setCellValue(rs.getString("csppname"));

                }
                if (toogleList.get(9)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sipurchaserecommendedprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(10)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sirecommendedprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(11)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sisalemandatoryprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(12)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sipurchasecount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(13)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sisalecount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(14)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("otherquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(15)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("availablequantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(16)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sicurrentpurchasepricewithkdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(17)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sicurrentpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(18)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sicurrentsaleprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(19)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("sicurrentsalepricewithoutkdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(20)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("availablepurchasepricewithkdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(21)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("availablepurchasepricewithoutkdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(22)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("availablesalepricewithkdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(23)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("availablesalepricewithoutkdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(24)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("profitpercentage").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(25)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("salekdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(26)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.round(rs.getBigDecimal("purchasekdv").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(27)) {
                    row.createCell((short) c++).setCellValue(StaticMethods.findCategories(rs.getString("category")));

                }
                if (toogleList.get(28)) {
                    String status = rs.getString("sttdname");
                    if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && rs.getBoolean("sii_passive") == true && rs.getInt("sttdid") == 3) {
                        status = status + "(P)";
                    }
                    row.createCell((short) c++).setCellValue(status);

                }

                String[] split = null;
                if (rs.getString("alternativebarcodes") != null) {
                    split = rs.getString("alternativebarcodes").split(",", 5);

                }
                for (int i = 0; i < 5; i++) {
                    if (split != null && split.length > i) {
                        row.createCell((short) c++).setCellValue(split[i]);
                    } else {
                        row.createCell((short) c++).setCellValue("");
                    }
                }

            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("stocks"));
            } catch (IOException ex) {
                Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(StockDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public int stockBarcodeControlRequest(Stock stock) {
        return stockDao.stockBarcodeControlRequest(stock);
    }

    @Override
    public Stock findStockAccordingToBarcode(Stock stock) {
        return stockDao.findStockAccordingToBarcode(stock);
    }

    @Override
    public void downloadSampleList(List<StockUpload> list) {
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

            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {

                SXSSFCell stockbarcode = row.createCell((short) 0);
                stockbarcode.setCellValue(sessionBean.getLoc().getString("barcode"));
                stockbarcode.setCellStyle(cellStyle);

                SXSSFCell stockcode = row.createCell((short) 1);
                stockcode.setCellValue(sessionBean.getLoc().getString("stockcode"));
                stockcode.setCellStyle(cellStyle);

                SXSSFCell productionplace = row.createCell((short) 2);
                productionplace.setCellValue(sessionBean.getLoc().getString("productionplace"));
                productionplace.setCellStyle(cellStyle);

                SXSSFCell supplier = row.createCell((short) 3);
                supplier.setCellValue(sessionBean.getLoc().getString("supplier"));
                supplier.setCellStyle(cellStyle);

                SXSSFCell supplierstockcode = row.createCell((short) 4);
                supplierstockcode.setCellValue(sessionBean.getLoc().getString("supplierstockcode"));
                supplierstockcode.setCellStyle(cellStyle);

                SXSSFCell quicksale = row.createCell((short) 5);
                quicksale.setCellValue(sessionBean.getLoc().getString("quicksale"));
                quicksale.setCellStyle(cellStyle);

                SXSSFCell minstocklevel = row.createCell((short) 6);
                minstocklevel.setCellValue(sessionBean.getLoc().getString("minstocklevel"));
                minstocklevel.setCellStyle(cellStyle);

                SXSSFCell maxstocklevel = row.createCell((short) 7);
                maxstocklevel.setCellValue(sessionBean.getLoc().getString("maxstocklevel"));
                maxstocklevel.setCellStyle(cellStyle);

                SXSSFCell canthestockdroptonegativebalance = row.createCell((short) 8);
                canthestockdroptonegativebalance.setCellValue(sessionBean.getLoc().getString("canthestockdroptonegativebalance"));
                canthestockdroptonegativebalance.setCellStyle(cellStyle);

                SXSSFCell department = row.createCell((short) 9);
                department.setCellValue(sessionBean.getLoc().getString("department"));
                department.setCellStyle(cellStyle);

                SXSSFCell maincategory = row.createCell((short) 10);
                maincategory.setCellValue(sessionBean.getLoc().getString("maincategory"));
                maincategory.setCellStyle(cellStyle);

                SXSSFCell subcategory = row.createCell((short) 11);
                subcategory.setCellValue(sessionBean.getLoc().getString("subcategory"));
                subcategory.setCellStyle(cellStyle);

                for (StockUpload download : list) {

                    row = excelDocument.getSheet().createRow(jRow++);

                    row.createCell((short) 0).setCellValue(download.getBarcode());
                    row.createCell((short) 1).setCellValue(download.getCode());
                    row.createCell((short) 2).setCellValue(download.getCountry().getId());
                    if (download.getSupplier().getId() == 0) {
                        row.createCell((short) 3).setCellValue("");
                    } else {
                        row.createCell((short) 3).setCellValue(download.getSupplier().getId());
                    }

                    row.createCell((short) 4).setCellValue(download.getSupplierProductCode());
                    row.createCell((short) 5).setCellValue(download.getStockInfo().getIsQuickSaleTemp() ? 1 : 0);
                    row.createCell((short) 6).setCellValue(download.getStockInfo().getMinStockLevel().doubleValue());
                    row.createCell((short) 7).setCellValue(download.getStockInfo().getMaxStockLevel().doubleValue());
                    row.createCell((short) 8).setCellValue(download.getStockInfo().getIsMinusStockLevelTemp() ? 1 : 0);
                    row.createCell((short) 9).setCellValue(download.getStockInfo().getTaxDepartment().getId());
                    row.createCell((short) 10).setCellValue(download.getParentCategory().getId());
                    row.createCell((short) 11).setCellValue(download.getSubCategoty().getId());
                }

                try {
                    StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("sampleexcelfile"));
                } catch (Exception e) {
                    Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, e);

                }

            } else {

                SXSSFCell stockname = row.createCell((short) 0);
                stockname.setCellValue(sessionBean.getLoc().getString("stockname"));
                stockname.setCellStyle(cellStyle);

                SXSSFCell stockcode = row.createCell((short) 1);
                stockcode.setCellValue(sessionBean.getLoc().getString("stockcode"));
                stockcode.setCellStyle(cellStyle);

                SXSSFCell stockbarcode = row.createCell((short) 2);
                stockbarcode.setCellValue(sessionBean.getLoc().getString("barcode"));
                stockbarcode.setCellStyle(cellStyle);

                SXSSFCell unit = row.createCell((short) 3);
                unit.setCellValue(sessionBean.getLoc().getString("unit"));
                unit.setCellStyle(cellStyle);

                SXSSFCell isservice = row.createCell((short) 4);
                isservice.setCellValue(sessionBean.getLoc().getString("isservice"));
                isservice.setCellStyle(cellStyle);

                SXSSFCell quicksale = row.createCell((short) 5);
                quicksale.setCellValue(sessionBean.getLoc().getString("quicksale"));
                quicksale.setCellStyle(cellStyle);

                SXSSFCell brand = row.createCell((short) 6);
                brand.setCellValue(sessionBean.getLoc().getString("brand"));
                brand.setCellStyle(cellStyle);

                SXSSFCell supplier = row.createCell((short) 7);
                supplier.setCellValue(sessionBean.getLoc().getString("supplier"));
                supplier.setCellStyle(cellStyle);

                SXSSFCell supplierstockcode = row.createCell((short) 8);
                supplierstockcode.setCellValue(sessionBean.getLoc().getString("supplierstockcode"));
                supplierstockcode.setCellStyle(cellStyle);

                SXSSFCell productionplace = row.createCell((short) 9);
                productionplace.setCellValue(sessionBean.getLoc().getString("productionplace"));
                productionplace.setCellStyle(cellStyle);

                SXSSFCell minstocklevel = row.createCell((short) 10);
                minstocklevel.setCellValue(sessionBean.getLoc().getString("minstocklevel"));
                minstocklevel.setCellStyle(cellStyle);

                SXSSFCell maxstocklevel = row.createCell((short) 11);
                maxstocklevel.setCellValue(sessionBean.getLoc().getString("maxstocklevel"));
                maxstocklevel.setCellStyle(cellStyle);

                SXSSFCell canthestockdroptonegativebalance = row.createCell((short) 12);
                canthestockdroptonegativebalance.setCellValue(sessionBean.getLoc().getString("canthestockdroptonegativebalance"));
                canthestockdroptonegativebalance.setCellStyle(cellStyle);

                SXSSFCell department = row.createCell((short) 13);
                department.setCellValue(sessionBean.getLoc().getString("department"));
                department.setCellStyle(cellStyle);

                SXSSFCell stocktype = row.createCell((short) 14);
                stocktype.setCellValue(sessionBean.getLoc().getString("stocktype"));
                stocktype.setCellStyle(cellStyle);

                //vergi grubu satış
                SXSSFCell sales = row.createCell((short) 15);
                sales.setCellValue(sessionBean.getLoc().getString("taxgroup") + " " + sessionBean.getLoc().getString("sales"));
                sales.setCellStyle(cellStyle);

                //Vergi grubu alış
                SXSSFCell purchase = row.createCell((short) 16);
                purchase.setCellValue(sessionBean.getLoc().getString("taxgroup") + " " + sessionBean.getLoc().getString("purchase"));
                purchase.setCellStyle(cellStyle);

                SXSSFCell alternativebarcode = row.createCell((short) 17);
                alternativebarcode.setCellValue(sessionBean.getLoc().getString("alternativebarcode"));
                alternativebarcode.setCellStyle(cellStyle);

                SXSSFCell equivalent = row.createCell((short) 18);
                equivalent.setCellValue(sessionBean.getLoc().getString("equivalent"));
                equivalent.setCellStyle(cellStyle);

                SXSSFCell alternativebarcode2 = row.createCell((short) 19);
                alternativebarcode2.setCellValue(sessionBean.getLoc().getString("alternativebarcode") + " 2");
                alternativebarcode2.setCellStyle(cellStyle);

                SXSSFCell equivalent2 = row.createCell((short) 20);
                equivalent2.setCellValue(sessionBean.getLoc().getString("equivalent"));
                equivalent2.setCellStyle(cellStyle);

                SXSSFCell alternativebarcode3 = row.createCell((short) 21);
                alternativebarcode3.setCellValue(sessionBean.getLoc().getString("alternativebarcode") + " 3");
                alternativebarcode3.setCellStyle(cellStyle);

                SXSSFCell equivalent3 = row.createCell((short) 22);
                equivalent3.setCellValue(sessionBean.getLoc().getString("equivalent"));
                equivalent3.setCellStyle(cellStyle);

                SXSSFCell alternativebarcode4 = row.createCell((short) 23);
                alternativebarcode4.setCellValue(sessionBean.getLoc().getString("alternativebarcode") + " 4");
                alternativebarcode4.setCellStyle(cellStyle);

                SXSSFCell equivalent4 = row.createCell((short) 24);
                equivalent4.setCellValue(sessionBean.getLoc().getString("equivalent"));
                equivalent4.setCellStyle(cellStyle);

                SXSSFCell alternativebarcode5 = row.createCell((short) 25);
                alternativebarcode5.setCellValue(sessionBean.getLoc().getString("alternativebarcode") + " 5");
                alternativebarcode5.setCellStyle(cellStyle);

                SXSSFCell equivalent5 = row.createCell((short) 26);
                equivalent5.setCellValue(sessionBean.getLoc().getString("equivalent"));
                equivalent5.setCellStyle(cellStyle);

                //Ana Kategori - maincategory
                SXSSFCell maincategory = row.createCell((short) 27);
                maincategory.setCellValue(sessionBean.getLoc().getString("maincategory"));
                maincategory.setCellStyle(cellStyle);

                //alt kategori - subcategory
                SXSSFCell subcategory = row.createCell((short) 28);
                subcategory.setCellValue(sessionBean.getLoc().getString("subcategory"));
                subcategory.setCellStyle(cellStyle);

                for (StockUpload download : list) {
                    row = excelDocument.getSheet().createRow(jRow++);

                    row.createCell((short) 0).setCellValue(download.getName());
                    row.createCell((short) 1).setCellValue(download.getCode());
                    row.createCell((short) 2).setCellValue(download.getBarcode());
                    row.createCell((short) 3).setCellValue(download.getUnit().getId());
                    row.createCell((short) 4).setCellValue(download.getIsServiceTemp() ? 1 : 0);
                    row.createCell((short) 5).setCellValue(download.getStockInfo().getIsQuickSaleTemp() ? 1 : 0);
                    row.createCell((short) 6).setCellValue(download.getBrand().getId());
                    row.createCell((short) 7).setCellValue(download.getSupplier().getId());
                    row.createCell((short) 8).setCellValue(download.getSupplierProductCode());
                    row.createCell((short) 9).setCellValue(download.getCountry().getId());
                    row.createCell((short) 10).setCellValue(download.getStockInfo().getMinStockLevel().doubleValue());//burayı kontrol et 
                    row.createCell((short) 11).setCellValue(download.getStockInfo().getMaxStockLevel().doubleValue());
                    row.createCell((short) 12).setCellValue(download.getStockInfo().getIsMinusStockLevelTemp() ? 1 : 0);
                    row.createCell((short) 13).setCellValue(download.getStockInfo().getTaxDepartment().getId());
                    row.createCell((short) 14).setCellValue(download.getStockType_id());
                    row.createCell((short) 15).setCellValue(download.getSaleTaxGruopId());
                    row.createCell((short) 16).setCellValue(download.getPurchaseTaxGroupId());

                    row.createCell((short) 17).setCellValue(download.getAlternativeBarcode().getBarcode());

                    if (download.getAlternativeBarcode().getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        row.createCell((short) 18).setCellValue("");
                    } else {
                        row.createCell((short) 18).setCellValue(download.getAlternativeBarcode().getQuantity().doubleValue());
                    }

                    row.createCell((short) 19).setCellValue(download.getAlternativeBarcode2().getBarcode());

                    if (download.getAlternativeBarcode2().getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        row.createCell((short) 20).setCellValue("");
                    } else {
                        row.createCell((short) 20).setCellValue(download.getAlternativeBarcode2().getQuantity().doubleValue());
                    }

                    row.createCell((short) 21).setCellValue(download.getAlternativeBarcode3().getBarcode());

                    if (download.getAlternativeBarcode3().getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        row.createCell((short) 22).setCellValue("");
                    } else {
                        row.createCell((short) 22).setCellValue(download.getAlternativeBarcode3().getQuantity().doubleValue());
                    }

                    row.createCell((short) 23).setCellValue(download.getAlternativeBarcode4().getBarcode());

                    if (download.getAlternativeBarcode4().getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        row.createCell((short) 23).setCellValue("");
                    } else {
                        row.createCell((short) 24).setCellValue(download.getAlternativeBarcode4().getQuantity().doubleValue());
                    }

                    row.createCell((short) 25).setCellValue(download.getAlternativeBarcode5().getBarcode());

                    if (download.getAlternativeBarcode5().getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        row.createCell((short) 26).setCellValue("");
                    } else {
                        row.createCell((short) 26).setCellValue(download.getAlternativeBarcode5().getQuantity().doubleValue());
                    }

                    row.createCell((short) 27).setCellValue(download.getParentCategory().getId());
                    row.createCell((short) 28).setCellValue(download.getSubCategoty().getId());

                }

                try {
                    StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("sampleexcelfile"));

                } catch (Exception e) {
                    Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, e);
                }

            }

        } catch (Exception e) {
            Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, e);

        }

    }
}
