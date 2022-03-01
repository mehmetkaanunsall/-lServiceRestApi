/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:38:13
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.inventory.pricelist.dao.IPriceListItemDao;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

public class PriceListItemService implements IPriceListItemService {

    @Autowired
    private IPriceListItemDao priceListItemDao;

    @Autowired
    public SessionBean sessionBean;

    public void setPriceListItemDao(IPriceListItemDao priceListItemDao) {
        this.priceListItemDao = priceListItemDao;
    }

    @Override
    public List<PriceListItem> listofPriceListItem(PriceList obj, String where) {
        return priceListItemDao.listofPriceListItem(obj, where);
    }

    @Override
    public int create(PriceListItem obj) {
        return priceListItemDao.create(obj);
    }

    @Override
    public int update(PriceListItem obj) {
        return priceListItemDao.update(obj);
    }

    @Override
    public PriceListItem findStockPrice(Stock stock, boolean isPurchase, Branch branch) {
        return priceListItemDao.findStockPrice(stock, isPurchase, branch);
    }

    @Override
    public int delete(PriceListItem obj) {
        return priceListItemDao.delete(obj);
    }

    @Override
    public List<PriceListItem> listOfStock(int type, int priceListId, String where) {
        return priceListItemDao.listOfStock(type, priceListId, where);
    }

    @Override
    public String processStockPriceList(List<PriceListItem> listItems, int priceListId, Boolean isUpdate) {
        JsonObject jsonObject = null;
        JsonArray jsonArray = new JsonArray();
        for (PriceListItem obj : listItems) {
            if (obj.getType() == 1) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("barcode", obj.getStock().getBarcode());
                jsonObject.addProperty("is_taxincluded", obj.isIs_taxIncluded());
                jsonObject.addProperty("price", obj.getPrice());
                jsonObject.addProperty("currency_id", obj.getCurrency().getId());

                jsonArray.add(jsonObject);
            }

        }
        return priceListItemDao.processStockPriceList(jsonArray.toString(), priceListId, isUpdate);

    }

    @Override
    public List<PriceListItem> listOfUpdatingPriceStock(PriceList obj) {
        return priceListItemDao.listOfUpdatingPriceStock(obj);
    }

    /**
     * Bu metot fiyat listesinde tavsiye edilen satış fiyatını güncelelr veya
     * ekler.
     *
     * Ayrıca faturalardan toplu fiyat listesine ürün ekler veya günceller. Hep
     * vergi dahil gelecektir. 01.02.2019
     *
     * @param priceList
     * @param listOfItem
     * @return
     */
    @Override
    public int updatingPriceStock(PriceList priceList, List<PriceListItem> listOfItem, Branch branch) {

        JsonArray jsonArray = new JsonArray();
        for (PriceListItem obj : listOfItem) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("is_taxincluded", true);
            jsonObject.addProperty("price", obj.getStock().getStockInfo().getRecommendedPrice());
            jsonObject.addProperty("currency_id", obj.getStock().getStockInfo().getCurrency().getId());

            jsonArray.add(jsonObject);

        }
        return priceListItemDao.updatingPriceStock(jsonArray.toString(), priceList, branch);
    }

    @Override
    public int createItem(PriceListItem item, Branch branch) {
        return priceListItemDao.createItem(item, branch);
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

    /**
     * Bu metot yüklenen excel dosyasını okumada kullanılır.
     *
     * @param inputStream okunacak olan dosya
     * @return
     */
    @Override
    public List<PriceListItem> processUploadFile(InputStream inputStream) {

        List<PriceListItem> listItems = new ArrayList<>();
        PriceListItem priceListItem = new PriceListItem();
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

            listItems.clear();
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);

                priceListItem = new PriceListItem();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 

                    if (row.getCell(0) != null) {
                        priceListItem.setType(1);
                        try {
                            CellValue cellValue = evaluator.evaluate(row.getCell(0));
                            switch (cellValue.getCellTypeEnum()) {
                                case NUMERIC:
                                    String string23 = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(string23);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();

                                    String stringV = String.valueOf(toBigInteger);
                                    priceListItem.getStock().setBarcode(stringV);
                                    break;
                                case STRING:
                                    priceListItem.getStock().setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            priceListItem.getStock().setBarcode("-1");
                            priceListItem.setType(-1);
                        }

                    } else if (row.getCell(0) == null) {
                        priceListItem.getStock().setBarcode("-1");
                        priceListItem.setType(-1);
                    }
                    if (row.getCell(1) != null) { // Fiyat 
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) {
                                case NUMERIC:
                                    priceListItem.setPrice(BigDecimal.valueOf(row.getCell(1).getNumericCellValue()));
                                    break;
                                case STRING:
                                    priceListItem.setPrice(BigDecimal.valueOf(Double.valueOf(String.valueOf(row.getCell(1).getRichStringCellValue()))));
                                    break;
                            }
                            if (priceListItem.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                                priceListItem.setType(-1);
                            }
                        } catch (Exception e) {
                            priceListItem.setPrice(BigDecimal.ZERO);
                            priceListItem.setType(-1);
                        }
                    } else if (row.getCell(1) == null) {
                        priceListItem.setPrice(BigDecimal.ZERO);
                        priceListItem.setType(-1);

                    }
                    if (row.getCell(2) != null) { // Para Birimi
                        try {
                            CellValue cellValue2 = evaluator.evaluate(row.getCell(2));
                            switch (cellValue2.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double data = row.getCell(2).getNumericCellValue();// veriyi integer şekilde set etmek için kullanıldı.
                                    int value = data.intValue();
                                    priceListItem.getCurrency().setId(value);
                                    break;
                                case STRING:
                                    priceListItem.getCurrency().setId(Integer.valueOf(String.valueOf(row.getCell(2).getRichStringCellValue())));
                                    break;
                            }
                        } catch (Exception e) {
                            priceListItem.getCurrency().setId(0);
                            priceListItem.setType(-1);
                        }
                    } else if (row.getCell(2) == null) { // eğer currency bilgisi yok ise -1 set edildi.
                        priceListItem.getCurrency().setId(0);
                        priceListItem.setType(-1);
                    }
                    if (row.getCell(3) != null) { // Kdv Dahil mi 
                        try {
                            CellValue cellValue3 = evaluator.evaluate(row.getCell(3));
                            switch (cellValue3.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double data2 = row.getCell(3).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int value2 = data2.intValue();
                                    if (value2 == 1) {
                                        priceListItem.setIs_taxIncluded(Boolean.TRUE);
                                    } else {
                                        priceListItem.setIs_taxIncluded(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(3).getRichStringCellValue()));
                                    if (value == 1) {
                                        priceListItem.setIs_taxIncluded(Boolean.TRUE);
                                    } else {
                                        priceListItem.setIs_taxIncluded(Boolean.FALSE);
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            priceListItem.setIs_taxIncluded(Boolean.FALSE);
                            priceListItem.setType(-1);
                        }
                    }

                    listItems.add(priceListItem);

                }
            }
            return listItems;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    /**
     * Bu metot excelden upload işlemi için örnek liste oluşturur.Oluşturulan
     * liste upload dialogu üzerinde format olarak görüntülenir.
     *
     * @return
     */
    @Override
    public List<PriceListItem> createSampleList() {
        List<PriceListItem> listItems = new ArrayList<>();

        PriceListItem pli = new PriceListItem();

        pli.getStock().setBarcode("8690504080886");
        pli.setPrice(BigDecimal.valueOf(3.45));
        pli.getCurrency().setId(3);
        pli.setIs_taxIncluded(true);
        listItems.add(pli);

        PriceListItem pli2 = new PriceListItem();
        pli2.getStock().setBarcode("8690504033936");
        pli2.setPrice(BigDecimal.valueOf(2.5));
        pli2.getCurrency().setId(1);
        pli2.setIs_taxIncluded(false);
        listItems.add(pli2);

        PriceListItem pli3 = new PriceListItem();
        pli3.getStock().setBarcode("8690504015239");
        pli3.setPrice(BigDecimal.valueOf(2.55));
        pli3.getCurrency().setId(2);
        pli3.setIs_taxIncluded(true);
        listItems.add(pli3);

        PriceListItem pli4 = new PriceListItem();
        pli4.getStock().setBarcode("8690504007203");
        pli4.setPrice(BigDecimal.valueOf(2));
        pli4.getCurrency().setId(4);
        pli4.setIs_taxIncluded(false);
        listItems.add(pli4);

        return listItems;
    }

    @Override
    public List<PriceListItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj) {
        return priceListItemDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj);
    }

    @Override
    public int count(String where, PriceList obj) {
        return priceListItemDao.count(where, obj);
    }

    @Override
    public List<PriceListItem> matchExcelToList(List<PriceListItem> excelList, PriceList obj) {
        String barcodeString = "";
        String where = "";
        for (PriceListItem pi : excelList) {
            barcodeString = barcodeString + "," + "'" + String.valueOf(pi.getStock().getBarcode()) + "'";
            System.out.println("***matchExcelToList**" + pi.getTagQuantity());
        }
        if (!barcodeString.equals("")) {
            barcodeString = barcodeString.substring(1, barcodeString.length());
            where = where + " AND stck.barcode IN(" + barcodeString + ") ";
        }
        return priceListItemDao.listofPriceListItem(obj, where);
    }

    @Override
    public List<PriceListItem> findAllRecordedStock(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj, int type) {
        return priceListItemDao.findAllRecordedStock(first, pageSize, sortField, sortOrder, filters, where, obj, type);
    }

    @Override
    public int countRecordedStock(String where, PriceList obj) {
        return priceListItemDao.countRecordedStock(where, obj);
    }

    @Override
    public int deleteRecordedStock(String deleteList) {
        return priceListItemDao.deleteRecordedStock(deleteList);
    }

    @Override
    public void downloadSampleList(List<PriceListItem> sampleList) {
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

            SXSSFCell barcode = row.createCell((short) 0);
            barcode.setCellValue(sessionBean.getLoc().getString("barcode"));
            barcode.setCellStyle(cellStyle);

            SXSSFCell accounttypes = row.createCell((short) 1);
            accounttypes.setCellValue(sessionBean.getLoc().getString("price"));
            accounttypes.setCellStyle(cellStyle);

            SXSSFCell currency = row.createCell((short) 2);
            currency.setCellValue(sessionBean.getLoc().getString("currency"));
            currency.setCellStyle(cellStyle);

            SXSSFCell kdv = row.createCell((short) 3);
            kdv.setCellValue(sessionBean.getLoc().getString("kdv"));
            kdv.setCellStyle(cellStyle);

            for (PriceListItem upload : sampleList) {
                row = excelDocument.getSheet().createRow(jRow++);

                row.createCell((short) 0).setCellValue(upload.getStock().getBarcode());
                row.createCell((short) 1).setCellValue(upload.getPrice().toString());
                row.createCell((short) 2).setCellValue(upload.getCurrency().getId());
                row.createCell((short) 3).setCellValue(upload.isIs_taxIncluded() ? 1 : 0);
            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("sampleexcelfile"));
            } catch (IOException ex) {
                Logger.getLogger(PriceListItemService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e) {
            Logger.getLogger(PriceListItemService.class.getName()).log(Level.SEVERE, null, e);
        }

    }

}
