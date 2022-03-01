/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.finance.order.dao.IOrderDao;
import com.mepsan.marwiz.finance.order.dao.OrderDao;
import com.mepsan.marwiz.finance.order.dao.OrderReport;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class OrderService implements IOrderService {

    @Autowired
    private IOrderDao orderDao;

    @Autowired
    private SessionBean sessionBean;

    public void setOrderDao(IOrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(OrderReport order, List<BranchSetting> listOfBranch) {
        String where = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where = where + " AND od.orderdate BETWEEN '" + sdf.format(order.getBeginDate()) + "' AND '" + sdf.format(order.getEndDate()) + "' ";

        if (order.getOrderType().getId() > 0) {
            where = where + " AND od.type_id = " + order.getOrderType().getId() + " ";
        }

        if (order.getTypeNo() != 0) {
            where = where + " AND od.typeno = " + order.getTypeNo() + " ";
        }

        if (!order.getAccountList().isEmpty() && order.getAccountList().get(0).getId() != 0) {
            String ids = "";
            where = where + " AND od.account_id IN (";
            for (Account acc : order.getAccountList()) {
                ids = ids + acc.getId() + ",";
            }

            if (!ids.equals("")) {
                ids = ids.substring(0, ids.length() - 1);
                where = where + ids + ") ";
            }
        }

        if (!order.getStockList().isEmpty() && order.getStockList().get(0).getId() != 0) {
            String ids = "";
            for (Stock stck : order.getStockList()) {
                ids = ids + stck.getId() + ",";
            }
            if (!ids.equals("")) {

                ids = ids.substring(0, ids.length() - 1);
                if (order.isIsCheckItem()) {
                    where = where + "AND stck.id IN (" + ids + ") ";

                } else {

                    where = where + " AND EXISTS(SELECT odi.id FROM finance.orderitem odi WHERE odi.deleted=FALSE AND odi.order_id = od.id AND odi.stock_id IN (";
                    where = where + ids + ") )";
                }
            }
        }

        String branchs = "";
        where = where + " AND od.branch_id IN (";
        if (!order.getSelectedBranchList().isEmpty()) {
            for (BranchSetting br : order.getSelectedBranchList()) {
                branchs = branchs + br.getBranch().getId() + ",";
            }
        } else {
            for (BranchSetting br : listOfBranch) {
                branchs = branchs + br.getBranch().getId() + ",";
            }
        }
        branchs = branchs.substring(0, branchs.length() - 1);
        where = where + branchs + ") ";

        return where;
    }

    @Override
    public int create(Order obj) {
        return orderDao.create(obj);
    }

    @Override
    public int update(Order obj) {
        return orderDao.update(obj);
    }

    @Override
    public List<Order> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return orderDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return orderDao.count(where);
    }

    @Override
    public int exportExcel(Order order) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = orderDao.getDatasource().getConnection();
            prep = connection.prepareStatement(orderDao.exportData(order));
            rs = prep.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            int jRow = 0;

            SXSSFRow branchnamerow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell branchname = branchnamerow.createCell((short) 0);
            branchname.setCellValue(sessionBean.getLoc().getString("stationname") + " : ");
            branchname.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell branchnameVal = branchnamerow.createCell((short) 1);
            branchnameVal.setCellValue(order.getBranchSetting().getBranch().getName());

            SXSSFRow branchaddressrow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell branchaddress = branchaddressrow.createCell((short) 0);
            branchaddress.setCellValue(sessionBean.getLoc().getString("address") + " : ");
            branchaddress.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell branchaddressVal = branchaddressrow.createCell((short) 1);
            branchaddressVal.setCellValue(order.getBranchSetting().getBranch().getAddress());

            SXSSFRow empty1 = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow orderdaterow = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell dispatchdate = orderdaterow.createCell((short) 0);
            dispatchdate.setCellValue(sessionBean.getLoc().getString("orderdate") + " : ");
            dispatchdate.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell dispatchdateVal = orderdaterow.createCell((short) 1);
            dispatchdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), order.getOrderDate()));

            SXSSFRow documentrow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell documentno = documentrow.createCell((short) 0);
            documentno.setCellValue(sessionBean.getLoc().getString("documentno") + " : ");
            documentno.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell documentnoVal = documentrow.createCell((short) 1);
            String temp = "";
            temp += (order.getDocumentSerial() != null ? order.getDocumentSerial() : "");
            temp += (order.getDocumentNumber() != null ? order.getDocumentNumber() : "");
            documentnoVal.setCellValue(temp);

            SXSSFRow ordercreatorrow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell ordercreator = ordercreatorrow.createCell((short) 0);
            ordercreator.setCellValue(sessionBean.getLoc().getString("ordercreator") + " : ");
            ordercreator.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell ordercreatorVal = ordercreatorrow.createCell((short) 1);
            ordercreatorVal.setCellValue(order.getUserCreated().getId() == 1 ? sessionBean.getLoc().getString("theorderiscreatedautomatically") : order.getUserCreated().getName() + " " + order.getUserCreated().getSurname());

            SXSSFRow empty2 = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow customertitlerow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell customertitle = customertitlerow.createCell((short) 0);
            customertitle.setCellValue(sessionBean.getLoc().getString("currentname") + " : ");
            customertitle.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell customertitleVal = customertitlerow.createCell((short) 1);
            customertitleVal.setCellValue(order.getAccount().getName());

            SXSSFRow empty3 = excelDocument.getSheet().createRow(jRow++);

            CellStyle cellStyle = excelDocument.getWorkbook().createCellStyle();
            Font font = excelDocument.getWorkbook().createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.index);
            cellStyle.setFont(font);
            cellStyle.setBorderRight(BorderStyle.MEDIUM);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int a = 0;
            SXSSFRow rowh = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cell1 = rowh.createCell((short) a++);
            cell1.setCellValue(sessionBean.getLoc().getString("barcode"));
            cell1.setCellStyle(cellStyle);

            SXSSFCell cell2 = rowh.createCell((short) a++);
            cell2.setCellValue(sessionBean.getLoc().getString("stockcode"));
            cell2.setCellStyle(cellStyle);

            SXSSFCell cell3 = rowh.createCell((short) a++);
            cell3.setCellValue(sessionBean.getLoc().getString("stockname"));
            cell3.setCellStyle(cellStyle);

            SXSSFCell cell4 = rowh.createCell((short) a++);
            cell4.setCellValue(sessionBean.getLoc().getString("amount"));
            cell4.setCellStyle(cellStyle);

            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));

                row.createCell((short) b++).setCellValue(rs.getString("stckcode"));

                row.createCell((short) b++).setCellValue(rs.getString("stckname"));

                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("odiquantity").doubleValue(), rs.getInt("guntunitrounding")));

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sdf.format(order.getOrderDate()) + "-" + order.getAccount().getName() + "-" + order.getBranchSetting().getBranch().getName());
                return 1;
            } catch (IOException ex) {
                Logger.getLogger(OrderService.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }

        } catch (SQLException ex) {
            return 0;
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
                Logger.getLogger(OrderDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public int updateStatus(Order order) {
        return orderDao.updateStatus(order);
    }

    @Override
    public int sendOrderCenter(Order order) {
        return orderDao.sendOrderCenter(order);
    }

    @Override
    public int delete(Order order) {
        return orderDao.delete(order);
    }

    @Override
    public CheckDelete testBeforeDelete(Order order) {
        return orderDao.testBeforeDelete(order);
    }

    @Override
    public Order findOrder(int orderId) {
        Map<String, Object> filt = new HashMap<>();

        List<Order> list = orderDao.findAll(0, 10, "od.orderdate", "desc", filt, " AND od.id= " + orderId);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new Order();
        }
    }

}
