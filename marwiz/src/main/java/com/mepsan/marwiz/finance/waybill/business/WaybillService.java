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
import com.mepsan.marwiz.finance.waybill.dao.IWaybillDao;
import com.mepsan.marwiz.finance.waybill.presentation.WaybillBean.WaybillParam;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class WaybillService implements IWaybillService {

    @Autowired
    private IWaybillDao waybillDao;

    @Autowired
    private SessionBean sessionBean;

    public void setWaybillDao(IWaybillDao waybillDao) {
        this.waybillDao = waybillDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(WaybillParam searchObject, List<Branch> listOfBranch) {
        String where = "";
        String branchs = "";
        where = where + " AND wb.branch_id IN (";
        if (!searchObject.getSelectedBranchList().isEmpty()) {
            for (Branch br : searchObject.getSelectedBranchList()) {
                branchs = branchs + br.getId() + ",";
            }
        } else {
            for (Branch br : listOfBranch) {
                branchs = branchs + br.getId() + ",";
            }
        }
        branchs = branchs.substring(0, branchs.length() - 1);
        where = where + branchs + ") ";

        return where;
    }

    @Override
    public String jsonArrayWarehouses(List<Warehouse> warehouses) {
        JsonArray jsonArray = new JsonArray();
        for (Warehouse obj : warehouses) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public int create(Waybill obj) {
        obj.setJsonWarehouses(jsonArrayWarehouses(obj.getListOfWarehouse()));
        return waybillDao.create(obj);
    }

    @Override
    public int update(Waybill obj) {
        obj.setJsonWarehouses(jsonArrayWarehouses(obj.getListOfWarehouse()));
        return waybillDao.update(obj);
    }

    @Override
    public List<Waybill> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return waybillDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return waybillDao.count(where);
    }

    @Override
    public Waybill find(int waybillId) {

        Map<String, Object> filt = new HashMap<>();

        List<Waybill> list = waybillDao.findAll(0, 10, "wb.id", "ASC", filt, " AND wb.id = " + waybillId);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new Waybill();
        }
    }

    @Override
    public CheckDelete testBeforeDelete(Waybill waybill) {
        return waybillDao.testBeforeDelete(waybill);
    }

    @Override
    public int delete(Waybill waybill) {
        waybill.setJsonWarehouses(jsonArrayWarehouses(waybill.getListOfWarehouse()));
        return waybillDao.delete(waybill);
    }

    @Override
    public void createExcelFile(Waybill waybill, List<WaybillItem> listOfIWaybillItems) {
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        int jRow = 0;

        List<Boolean> tempToggle = new ArrayList<>();
        tempToggle = Arrays.asList(true, true, true, true);

        SXSSFRow branchnamerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchnameVal = branchnamerow.createCell((short) 0);
        branchnameVal.setCellValue(sessionBean.getUser().getLastBranch().getName());

        SXSSFRow branchaddressrow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchaddress = branchaddressrow.createCell((short) 0);
        branchaddress.setCellValue(sessionBean.getLoc().getString("address") + " : ");
        branchaddress.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchaddressVal = branchaddressrow.createCell((short) 1);
        branchaddressVal.setCellValue(sessionBean.getUser().getLastBranch().getAddress());

        SXSSFCell documentno = branchaddressrow.createCell((short) 4);
        documentno.setCellValue(sessionBean.getLoc().getString("documentno") + " : ");
        documentno.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell documentnoVal = branchaddressrow.createCell((short) 5);
        String temp = "";
        temp = temp + (waybill.getDocumentSerial() != null ? waybill.getDocumentSerial() : "");
        temp = temp + (waybill.getDocumentNumber() != null ? waybill.getDocumentNumber() : "");
        documentnoVal.setCellValue(temp);

        SXSSFRow branchmailrow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchmail = branchmailrow.createCell((short) 0);
        branchmail.setCellValue(sessionBean.getLoc().getString("mail") + " : ");
        branchmail.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchmailVal = branchmailrow.createCell((short) 1);
        branchmailVal.setCellValue(sessionBean.getUser().getLastBranch().getMail());

        SXSSFCell termdate = branchmailrow.createCell((short) 4);
        termdate.setCellValue(sessionBean.getLoc().getString("documentdate") + " : ");
        termdate.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell termdateVal = branchmailrow.createCell((short) 5);
        termdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), waybill.getWaybillDate()));

        SXSSFRow branchtaxofficerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchtaxoffice = branchtaxofficerow.createCell((short) 0);
        branchtaxoffice.setCellValue(sessionBean.getLoc().getString("taxoffice") + " : ");
        branchtaxoffice.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchtaxofficeVal = branchtaxofficerow.createCell((short) 1);
        branchtaxofficeVal.setCellValue(sessionBean.getUser().getLastBranch().getTaxOffice());

        SXSSFCell dispatchdate = branchtaxofficerow.createCell((short) 4);
        dispatchdate.setCellValue(sessionBean.getLoc().getString("dispatchdate") + " : ");
        dispatchdate.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell dispatchdateVal = branchtaxofficerow.createCell((short) 5);
        dispatchdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), waybill.getDispatchDate()));

        SXSSFRow branchtaxnoerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchtaxno = branchtaxnoerow.createCell((short) 0);
        branchtaxno.setCellValue(sessionBean.getLoc().getString("taxno") + " : ");
        branchtaxno.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchtaxnoVal = branchtaxnoerow.createCell((short) 1);
        branchtaxnoVal.setCellValue(sessionBean.getUser().getLastBranch().getTaxNo());

        SXSSFRow empty1 = excelDocument.getSheet().createRow(jRow++);

        SXSSFRow customertitlerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell customertitle = customertitlerow.createCell((short) 0);
        customertitle.setCellValue(sessionBean.getLoc().getString("customertitle") + " : ");
        customertitle.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell customertitleVal = customertitlerow.createCell((short) 1);
        customertitleVal.setCellValue(waybill.getAccount().getName());

        SXSSFRow customertaxrow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell customertax = customertaxrow.createCell((short) 0);
        customertax.setCellValue(sessionBean.getLoc().getString("taxinformationofficeandnumber") + " : ");
        customertax.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell customertaxVal = customertaxrow.createCell((short) 1);
        customertaxVal.setCellValue((waybill.getAccount().getTaxOffice() != null ? waybill.getAccount().getTaxOffice() : "") + " - "
                + (waybill.getAccount().getTaxNo() != null ? waybill.getAccount().getTaxNo() : ""));

        SXSSFRow empty2 = excelDocument.getSheet().createRow(jRow++);

        StaticMethods.createHeaderExcel("tbvWaybill:frmWaybillItemsTab:dtbItems", tempToggle, "headerBlack", excelDocument.getWorkbook());
        jRow++;

        for (WaybillItem waybillItem : listOfIWaybillItems) {

            int b = 0;
            SXSSFRow row = excelDocument.getSheet().createRow(jRow++); 
            
            row.createCell((short) b++).setCellValue(waybillItem.getStock().getBarcode());
            row.createCell((short) b++).setCellValue(waybillItem.getStock().getCenterProductCode());
            row.createCell((short) b++).setCellValue(waybillItem.getStock().getName());
            row.createCell((short) b++).setCellValue(StaticMethods.round(waybillItem.getQuantity().doubleValue(), waybillItem.getStock().getUnit().getUnitRounding()));
            row.createCell((short) b++).setCellValue(waybillItem.getDescription());

        }

        try {
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("waybill"));
        } catch (IOException ex) {
            Logger.getLogger(WaybillService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public int updateLogSap(Waybill waybill) {
        return waybillDao.updateLogSap(waybill);
    }

    @Override
    public Waybill findWaybill(Waybill waybill) {
        Map<String, Object> filt = new HashMap<>();
        List<Waybill> list = waybillDao.findAll(0, 10, "wb.id", "ASC", filt, " AND wb.id = " + waybill.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new Waybill();
        }
    }

    @Override
    public int createWaybillForOrder(Waybill waybill, List<WaybillItem> listOfItem) {
        waybill.setJsonWarehouses(jsonArrayWarehouses(waybill.getListOfWarehouse()));
        String waybilItems = jsonArrayWaybillItems(listOfItem);
        return waybillDao.createWaybillForOrder(waybill, waybilItems);
    }

    @Override
    public String jsonArrayWaybillItems(List<WaybillItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (WaybillItem obj : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", 0);
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
            if (obj.getOrderItemIds() != null && !obj.getOrderItemIds().equals("")) {
                String[] orderitemids = obj.getOrderItemIds().split(",");
                String[] quantitys = obj.getOrderItemQuantitys().split(",");
                for (int i = 0; i < orderitemids.length; i++) {
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("orderitem_id", Integer.parseInt(orderitemids[i]));
                    jsonObject1.addProperty("quantity", new BigDecimal(quantitys[i]));
                    jsonArrayOrderItems.add(jsonObject1);

                }
            }

            jsonObject.add("orderitem_json", jsonArrayOrderItems);
            jsonArray.add(jsonObject);
        }
//        System.out.println("--jsonArray.toString()----" + jsonArray.toString());
        return jsonArray.toString();
    }

}
