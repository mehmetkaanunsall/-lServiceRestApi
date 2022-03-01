/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.waybill.dao.IWaybillItemDao;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.stock.dao.IStockDao;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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

public class WaybillItemService implements IWaybillItemService {
    
    @Autowired
    private IWaybillItemDao waybillItemDao;
    
    @Autowired
    private IStockDao stockDao;
    
    @Autowired
    private SessionBean sessionBean;
    
    public void setWaybillItemDao(IWaybillItemDao waybillItemDao) {
        this.waybillItemDao = waybillItemDao;
    }
    
    public void setStockDao(IStockDao stockDao) {
        this.stockDao = stockDao;
    }
    
    @Override
    public List<WaybillItem> listWaybillItem(Waybill waybill) {
        return waybillItemDao.listWaybillItem(waybill, "");
    }
    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    @Override
    public int create(WaybillItem obj) {
        
        List<WaybillItem> listOfWaybillItem = new ArrayList<>();
        listOfWaybillItem.add(obj);
        
        obj.setJsonItems(jsonArrayWaybillItems(listOfWaybillItem));
        
        return waybillItemDao.create(obj);
    }
    
    @Override
    public int update(WaybillItem obj) {
        
        List<WaybillItem> listOfWaybillItem = new ArrayList<>();
        listOfWaybillItem.add(obj);

//        System.out.println("obj.getOrderItemIds()"+obj.getOrderItemIds());
        if (obj.getOrderItemIds() != null && !obj.getOrderItemIds().equals("")) {
            obj.setJsonItems(jsonArrayWaybillItemsforOrder(listOfWaybillItem));
        } else {
            obj.setJsonItems(jsonArrayWaybillItems(listOfWaybillItem));
        }
        
        return waybillItemDao.update(obj);
    }

    /**
     * Bu metot fatura tarafında irsaliyeden hazır stok aktarmak için
     * yazılmıştır.
     *
     * @param invoice
     * @return
     */
    @Override
    public List<WaybillItem> listWaybillItemForInvoice(Invoice invoice) {
        return waybillItemDao.listWaybillItemForInvoice(invoice);
    }

    /**
     * Bu metot irsaliyenin altındaki kalan miktarı sıfırdan büyük olan ürünleri
     * getirir.
     *
     * @param waybill
     * @return
     */
    @Override
    public List<WaybillItem> listWaybillItemOpenStock(Waybill waybill) {
        return waybillItemDao.listWaybillItem(waybill, " AND wbi.remainingquantity > 0 ");
    }

    /**
     * Bu metto irsaliye item silmeden önce kontrol eder.
     *
     * @param waybillItem
     * @return
     */
    @Override
    public CheckDelete testBeforeDelete(WaybillItem waybillItem) {
        return waybillItemDao.testBeforeDelete(waybillItem);
    }
    
    @Override
    public int delete(WaybillItem waybillItem) {
        return waybillItemDao.delete(waybillItem);
    }
    
    @Override
    public WaybillItem findStock(String barcode, Waybill obj, boolean isAlternativeBarcode) {
        return waybillItemDao.findStock(barcode, obj, isAlternativeBarcode);
    }
    
    @Override
    public int createAll(List<WaybillItem> list, Waybill obj) {
        
        WaybillItem waybillItem = list.get(0);
        waybillItem.setWaybill(obj);
        for (WaybillItem w : list) {
            w.setWaybill(obj);
        }
        waybillItem.setJsonItems(jsonArrayWaybillItems(list));
        
        return waybillItemDao.create(waybillItem);
    }
    
    @Override
    public String jsonArrayWaybillItems(List<WaybillItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (WaybillItem obj : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            if (!obj.getWaybill().getListOfWarehouse().isEmpty()) {
                jsonObject.addProperty("warehouse_id", obj.getWaybill().getListOfWarehouse().get(0).getId());
            } else {
                jsonObject.addProperty("warehouse_id", obj.getWarehouse().getId() > 0 ? obj.getWarehouse().getId() : 0);
            }
            jsonArray.add(jsonObject);
        }
        //System.out.println("--jsonArray.toString()----" + jsonArray.toString());
        return jsonArray.toString();
    }
    
    @Override
    public String jsonArrayForExcelUpload(Waybill waybill, List<WaybillItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (WaybillItem obj : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("barcode", obj.getStock().getBarcode());
            jsonObject.addProperty("quantity", obj.getQuantity());
            
            jsonArray.add(jsonObject);
        }
//        System.out.println("--jsonArray.toString()----" + jsonArray.toString());
        return waybillItemDao.excelItemInsert(waybill, jsonArray.toString());
    }
    
    @Override
    public List<WaybillItem> createSampleList() {
        
        List<WaybillItem> list = new ArrayList<>();
        
        WaybillItem waybillItem = new WaybillItem();
        waybillItem.getStock().setBarcode("963852741123");
        waybillItem.setQuantity(BigDecimal.valueOf(5));
        
        list.add(waybillItem);
        
        waybillItem = new WaybillItem();
        waybillItem.getStock().setBarcode("8690504086529");
        waybillItem.setQuantity(BigDecimal.valueOf(12));
        
        list.add(waybillItem);
        
        waybillItem = new WaybillItem();
        waybillItem.getStock().setBarcode("8690504067108");
        waybillItem.setQuantity(BigDecimal.valueOf(1));
        
        list.add(waybillItem);
        
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
    public List<WaybillItem> processUploadFile(InputStream inputStream, Waybill selectedWaybill) {
        
        WaybillItem waybillItem = new WaybillItem();
        List<WaybillItem> excelStockList = new ArrayList<>();
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
                
                waybillItem = new WaybillItem();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 
                    waybillItem.setExcelDataType(1);
                    
                    if (row.getCell(0) != null) {
                        try {
                            CellValue cellValue0 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue0.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);
                                    
                                    waybillItem.getStock().setBarcode(barcode);
                                    
                                    break;
                                case STRING:
                                    waybillItem.getStock().setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            waybillItem.getStock().setBarcode("-1");
                            waybillItem.setExcelDataType(-1);
                            
                        }
                    } else if (row.getCell(0) == null) {
                        waybillItem.getStock().setBarcode("-1");
                        waybillItem.setExcelDataType(-1);
                    }
                    
                    if (row.getCell(1) != null) { // Miktar
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) { //Alternatif barkod karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(1).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);
                                    
                                    waybillItem.setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(1).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    waybillItem.setQuantity(bd);
                                    break;
                            }
                            
                            if (waybillItem.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                                waybillItem.setExcelDataType(-1);
                                
                            }
                            
                        } catch (Exception e) {
                            waybillItem.setExcelDataType(-1);
                            waybillItem.setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(1) == null) {
                        waybillItem.setQuantity(BigDecimal.valueOf(0));
                        waybillItem.setExcelDataType(-1);
                    }

                    // Şube ayarlarındaki satın alma faturasında satış fiyat listesindek ürünler mi eklensin durumu burada kontrol edilir.
                    if (selectedWaybill.getBranchSetting().isIsInvoiceStockSalePriceList()) {
                        if (selectedWaybill.isIsPurchase()) {
                            int result = 0;
                            if (waybillItem.getStock().getBarcode() != null || waybillItem.getStock().getBarcode().equals("")) {
                                result = waybillItemDao.checkStockSalePriceList(waybillItem.getStock().getBarcode(), selectedWaybill.isIsPurchase(), selectedWaybill.getBranchSetting());
                                
                                if (result == 0) { // eğer satış fiyat listesinde ürün yok ise
                                    waybillItem.setExcelDataType(-1);
                                }
                            }
                        }
                    }
                    
                    if (excelStockList.isEmpty()) { // liste boş değilse
                        excelStockList.add(waybillItem);
                    } else {
                        boolean isThere = false;
                        for (WaybillItem item : excelStockList) {
                            if (waybillItem.getStock().getBarcode() == item.getStock().getBarcode()) {
                                item.setQuantity(waybillItem.getQuantity().add(item.getQuantity()));
                                isThere = true;
                            }
                        }
                        if (!isThere) {
                            excelStockList.add(waybillItem);
                        }
                    }
                    
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
    public int checkStockSalePriceList(String barcode, boolean isPurchase, BranchSetting branchSetting) {
        return waybillItemDao.checkStockSalePriceList(barcode, isPurchase, branchSetting);
    }
    
    @Override
    public String jsonArrayWaybillItemsforOrder(List<WaybillItem> list) {
        
        JsonArray jsonArray = new JsonArray();
        for (WaybillItem obj : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            if (!obj.getWaybill().getListOfWarehouse().isEmpty()) {
                jsonObject.addProperty("warehouse_id", obj.getWaybill().getListOfWarehouse().get(0).getId());
            } else {
                jsonObject.addProperty("warehouse_id", 0);
            }
            
            JsonArray jsonArrayOrderItems = new JsonArray();
//            System.out.println("obj.getOrderItemIds()"+obj.getOrderItemIds());
//            System.out.println("obj.getOrderItemQuantitys()"+obj.getOrderItemQuantitys());
            String[] orderitemids = obj.getOrderItemIds().split(",");
            String[] quantitys = obj.getOrderItemQuantitys().split(",");
            for (int i = 0; i < orderitemids.length; i++) {
                JsonObject jsonObject1 = new JsonObject();
//                jsonObject1.addProperty("orderitem_id",Integer.parseInt(orderitemids[i]));
//                jsonObject1.addProperty("quantity", new BigDecimal(quantitys[i]));
                jsonArrayOrderItems.add(jsonObject1);
                
            }
            jsonObject.add("orderitem_json", jsonArrayOrderItems);
            jsonArray.add(jsonObject);
            
        }
//        System.out.println("--jsonArray.toString()----" + jsonArray.toString());

        return jsonArray.toString();
    }
    
    @Override
    public List<Warehouse> findFuelStockWarehouse(WaybillItem waybillItem, Waybill waybill) {
        
        return waybillItemDao.findFuelStockWarehouse(waybillItem, waybill);
    }
    
}
