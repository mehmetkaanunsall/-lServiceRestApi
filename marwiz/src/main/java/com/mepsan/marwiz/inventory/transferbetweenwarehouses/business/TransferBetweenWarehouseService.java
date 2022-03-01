/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   09.02.2018 04:54:13
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.dao.ITransferBetweenWarehouseDao;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TransferBetweenWarehouseService implements ITransferBetweenWarehouseService {

    @Autowired
    private ITransferBetweenWarehouseDao transferBetweenWarehouseDao;

    public void setTransferBetweenWarehouseDao(ITransferBetweenWarehouseDao transferBetweenWarehouseDao) {
        this.transferBetweenWarehouseDao = transferBetweenWarehouseDao;
    }

    @Override
    public int save(Warehouse entry, Warehouse exit, List<WarehouseMovement> listOfWarehouseMovement, int type, WarehouseTransfer warehouseTransfer) {
        return transferBetweenWarehouseDao.save(entry, exit, jsonArrayWarehouseMovements(listOfWarehouseMovement), type, warehouseTransfer);

    }

    /**
     * Bu metot gelen depo hareketi listesini json array stringine dönüştürür.
     *
     * @param movements
     * @return
     */
    @Override
    public String jsonArrayWarehouseMovements(List<WarehouseMovement> movements) {
        JsonArray jsonArray = new JsonArray();
        for (WarehouseMovement obj : movements) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("quantity", Integer.valueOf(obj.getQuantity().intValue()));
            jsonObject.addProperty("stockcount", 1);
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public List<WarehouseTransfer> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return transferBetweenWarehouseDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return transferBetweenWarehouseDao.count(where);
    }

    @Override
    public int delete(Warehouse entry, Warehouse exit, List<WarehouseMovement> listOfWarehouseMovement, int type, WarehouseTransfer warehouseTransfer, WarehouseMovement warehouseMovement) {
        return transferBetweenWarehouseDao.delete(entry, exit, jsonArrayWarehouseMovements(listOfWarehouseMovement), type, warehouseTransfer, warehouseMovement);
    }

    @Override
    public WarehouseTransfer find(WarehouseTransfer warehouseTransfer) {

        Map<String, Object> filt = new HashMap<>();

        List<WarehouseTransfer> list = transferBetweenWarehouseDao.findAll(0, 10, "whr.id", "ASC", filt, " AND (wht.warehousereceipt_id = " + warehouseTransfer.getWarehouseReceipt().getId() + " OR wht.transferwarehousereceipt_id = " + warehouseTransfer.getWarehouseReceipt().getId() + ")");

        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new WarehouseTransfer();
        }
    }

    /* 
    *Bu Method Aktarılacak Excel Dosyası için Örnek Liste Oluşturur.   
     */
    @Override
    public List<WarehouseMovement> createSampleList() {

        List<WarehouseMovement> listItems = new ArrayList<>();

        WarehouseMovement wm = new WarehouseMovement();

        wm.getStock().setBarcode("8690504080886");
        wm.setQuantity(BigDecimal.valueOf(3));

        listItems.add(wm);

        WarehouseMovement wm2 = new WarehouseMovement();
        wm2.getStock().setBarcode("8690504033936");
        wm2.setQuantity(BigDecimal.valueOf(4));

        listItems.add(wm2);

        WarehouseMovement wm3 = new WarehouseMovement();
        wm3.getStock().setBarcode("8690504015239");
        wm3.setQuantity(BigDecimal.valueOf(5));

        listItems.add(wm3);

        WarehouseMovement wm4 = new WarehouseMovement();
        wm4.getStock().setBarcode("8690504007203");
        wm4.setQuantity(BigDecimal.valueOf(2));

        listItems.add(wm4);

        return listItems;
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
    public List<WarehouseMovement> processUploadFile(InputStream inputStream) {
        List<WarehouseMovement> listItems = new ArrayList<>();
        WarehouseMovement warehouseMovement = new WarehouseMovement();
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

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            listItems.clear();
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);

                warehouseMovement = new WarehouseMovement();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 

                    if (row.getCell(0) != null) {
                        warehouseMovement.setType(1);
                        try {
                            CellValue cellValue = evaluator.evaluate(row.getCell(0));
                            switch (cellValue.getCellTypeEnum()) {
                                case NUMERIC:
                                    String string23 = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(string23);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();

                                    String stringV = String.valueOf(toBigInteger);
                                    warehouseMovement.getStock().setBarcode(stringV);
                                    break;
                                case STRING:
                                    warehouseMovement.getStock().setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            warehouseMovement.getStock().setBarcode("-1");
                            warehouseMovement.setType(-1);
                        }

                    } else if (row.getCell(0) == null) {
                        warehouseMovement.getStock().setBarcode("-1");
                        warehouseMovement.setType(-1);
                    }
                    if (row.getCell(1) != null) { // Miktar 
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) { //Alternatif barkod karşılığı
                                case NUMERIC:
                                    double quantity = row.getCell(1).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(quantity);

                                    warehouseMovement.setQuantity(bigDecimal);

                                    break;
                                case STRING:

                                    String s = String.valueOf(row.getCell(1).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);

                                    warehouseMovement.setQuantity(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            warehouseMovement.setQuantity(BigDecimal.ZERO);
                            warehouseMovement.setType(-1);
                        }
                    } else if (row.getCell(1) == null) {
                        warehouseMovement.setQuantity(BigDecimal.ZERO);
                        warehouseMovement.setType(-1);

                    }

                    listItems.add(warehouseMovement);

                }

            }
            return listItems;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    @Override
    public String jsonArrayForExcelUpload(List<WarehouseMovement> listItems, int exitWarehouseId, Warehouse entryWarehouse) {
        JsonObject jsonObject = null;
        JsonArray jsonArray = new JsonArray();

        for (WarehouseMovement obj : listItems) {

            if (obj.getType() == 1) /*hatali olmayan kayitlar alinir. */ {
                jsonObject = new JsonObject();
                jsonObject.addProperty("barcode", obj.getStock().getBarcode());
                jsonObject.addProperty("quantity", Integer.valueOf(obj.getQuantity().intValue()));
                jsonArray.add(jsonObject);
            }

        }

        return transferBetweenWarehouseDao.jsonArrayForExcelUpload(jsonArray.toString(), exitWarehouseId, entryWarehouse);

    }

}
