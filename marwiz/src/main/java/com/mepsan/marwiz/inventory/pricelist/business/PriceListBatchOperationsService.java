/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 14.09.2018 08:42:00
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.inventory.pricelist.dao.IPriceListBatchOperationsDao;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PriceListBatchOperationsService implements IPriceListBatchOperationsService {

    @Autowired
    private IPriceListBatchOperationsDao priceListBatchOperationsDao;

    public void setPriceListBatchOperationsDao(IPriceListBatchOperationsDao priceListBatchOperationsDao) {
        this.priceListBatchOperationsDao = priceListBatchOperationsDao;
    }

    @Override
    public int updateStocks(int processType, int priceListId, boolean isRate, BigDecimal price, String where) {
        return priceListBatchOperationsDao.updateStocks(processType, priceListId, isRate, price, where);
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
    public List<PriceListItem> processUploadFileStock(InputStream inputStream) {

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
                                    priceListItem.getStock().setBarcode(stringV.trim());
                                    break;
                                case STRING:
                                    priceListItem.getStock().setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()).trim());
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
                    if (row.getCell(1) != null) { // Miktar 
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) { //Alternatif barkod karşılığı
                                case NUMERIC:
                                    double quantity = row.getCell(1).getNumericCellValue();
                                    int i = (int) quantity;

                                    priceListItem.setTagQuantity(i);
                                    break;
                                case STRING:

                                    String s = String.valueOf(row.getCell(1).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    int bd = (int) d;

                                    priceListItem.setTagQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            priceListItem.setTagQuantity(0);
                            priceListItem.setType(-1);
                        }
                    } else if (row.getCell(1) == null) {
                        priceListItem.setTagQuantity(0);
                        priceListItem.setType(-1);

                    }

                    if (priceListItem.getTagQuantity() < 0) {
                        priceListItem.setType(-1);
                    }

                    listItems.add(priceListItem);

                }
            }
            return listItems;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

}
