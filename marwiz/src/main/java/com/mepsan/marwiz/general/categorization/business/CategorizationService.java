/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date 17.01.2018 10:00:52
 */
package com.mepsan.marwiz.general.categorization.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.categorization.dao.ICategorizationDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.springframework.beans.factory.annotation.Autowired;

public class CategorizationService implements ICategorizationService {

    @Autowired
    private ICategorizationDao categorizationDao;
    
    @Autowired
    public SessionBean sessionBean;

    public void setCategorizationDao(ICategorizationDao categorizationDao) {
        this.categorizationDao = categorizationDao;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    

    @Override
    public List<Categorization> listCategorization(Categorization obj) {
        return categorizationDao.listCategorization(obj);
    }

    @Override
    public int create(Categorization obj) {
        return categorizationDao.create(obj);
    }

    @Override
    public int update(Categorization obj) {
        return categorizationDao.update(obj);
    }

    @Override
    public int testBeforeDelete(Categorization categorization) {
        return categorizationDao.testBeforeDelete(categorization);
    }

    @Override
    public int delete(Categorization categorization) {
        return categorizationDao.delete(categorization);
    }

    @Override
    public String jsonArrayCategories(List<Categorization> categorizations) {
        JsonArray jsonArray = new JsonArray();
        for (Categorization obj : categorizations) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public String jsonArrayStocks(List<Stock> stocks) {
        JsonArray jsonArray = new JsonArray();
        for (Stock obj : stocks) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public String jsonArrayAccounts(List<Account> accounts) {
        JsonArray jsonArray = new JsonArray();
        for (Account obj : accounts) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public int addToItem(int itemId, Categorization categorization, String categorizations, String items) {
        return categorizationDao.addToItem(itemId, categorization, categorizations, items);
    }

    @Override
    public List<Stock> createSampleList() {
        List<Stock> sampleList = new ArrayList<>();

        Stock stock = new Stock();
        stock.setBarcode("8690504080886");

        sampleList.add(stock);

        Stock stock2 = new Stock();
        stock2.setBarcode("8690526083254");

        sampleList.add(stock2);

        Stock stock3 = new Stock();
        stock3.setBarcode("8690504082651");

        sampleList.add(stock3);

        return sampleList;
    }

    @Override
    public List<Stock> processUploadFile(InputStream inputStream) {
        List<Stock> excelStockList = new ArrayList<>();
        try {
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream); // HSSF veya XSSF olarak oluşması için bu şekilde çalışma dosyası oluşturuldu.
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows;
            rows = sheet.getPhysicalNumberOfRows();
            int cols = 0;
            int tmp = 0;

            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        tmp = 0;
                    }
                }
            }
            excelStockList.clear();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);

                Stock stock = new Stock();
                if (row != null && !isRowEmpty(row)) {
                    stock.setExcelDataType(1);

                    if (row.getCell(0) != null) {
                        try {
                            CellValue cellValue0 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue0.getCellTypeEnum()) {
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    stock.setBarcode(barcode);
                                    break;
                                case STRING:
                                    stock.setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    break;
                            }

                        } catch (Exception e) {
                            stock.setBarcode("-1");
                            stock.setExcelDataType(-1);
                        }
                    } else if (row.getCell(0) == null) {
                        stock.setBarcode("-1");
                        stock.setExcelDataType(-1);

                    }

                    excelStockList.add(stock);
                }
            }
            return excelStockList;
        } catch (Exception e) {
            return new ArrayList<>();
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
    public String importProductList(List<Stock> stocks) {
        JsonArray jsonArray = new JsonArray();

        for (Stock obj : stocks) {
            if (obj.getExcelDataType() == 1) { // hatalı kayıtları GÖNDERME
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("barcode", obj.getBarcode());

                jsonArray.add(jsonObject);
            }

        }
        System.out.println("----jsonArray--" + jsonArray.toString());
        return categorizationDao.importItemList(jsonArray.toString());
    }

    @Override
    public void downloadSampleList(List<Stock> sampleList) {
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
            accounttypes.setCellValue(sessionBean.getLoc().getString("barcode"));
            accounttypes.setCellStyle(cellStyle);
            
            for (Stock upload : sampleList) {
                
                row = excelDocument.getSheet().createRow(jRow++);
                row.createCell((short) 0).setCellValue(upload.getBarcode());
            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("sampleexcelfile"));
            } catch (Exception ex) {
                Logger.getLogger(CategorizationService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } 
        catch (Exception e) 
        {
            Logger.getLogger(CategorizationService.class.getName()).log(Level.SEVERE, null, e);
        }
       
       
    }

}
