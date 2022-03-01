/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 05.02.2019 18:27:47
 */
package com.mepsan.marwiz.automation.tank.business;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.automation.tank.dao.ITankMovementTabDao;
import com.mepsan.marwiz.automation.tank.dao.TankMovement;
import com.mepsan.marwiz.automation.tank.dao.TankMovementTabDao;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class TankMovementTabService implements ITankMovementTabService {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Autowired
    private ITankMovementTabDao tankMovementTabDao;

    @Override
    public List<TankMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, Warehouse warehouse) {
        return tankMovementTabDao.findAll(first, pageSize, sortField, sortOrder, filters, where, opType, begin, end, warehouse);
    }

    @Override
    public int count(String where, int opType, Date begin, Date end, Warehouse warehouse) {
        return tankMovementTabDao.count(where, opType, begin, end, warehouse);
    }

    @Override
    public void exportExcel(Warehouse warehouse, List<Boolean> toogleList, String where, int opType, Date begin, Date end) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        System.out.println("Tank Movement Excel");
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = tankMovementTabDao.getDatasource().getConnection();
            prep = connection.prepareStatement(tankMovementTabDao.exportData(where, opType, begin, end, warehouse));
            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("tankmovements"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow tankName = excelDocument.getSheet().createRow(jRow++);
            tankName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("tankname") + " : " +(warehouse.getName() == null ? ' ' : warehouse.getName()));

            SXSSFRow tankCode = excelDocument.getSheet().createRow(jRow++);
            tankCode.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("tankcode") + " : " + (warehouse.getCode() == null ? ' ' : warehouse.getCode()));

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + warehouse.getStock().getName());

            SXSSFRow capacity = excelDocument.getSheet().createRow(jRow++);
            capacity.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("capacity") + " : " + StaticMethods.round(warehouse.getCapacity(), warehouse.getStock().getUnit().getUnitRounding()));

            SXSSFRow minamount = excelDocument.getSheet().createRow(jRow++);
            minamount.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("minamount") + " : " + StaticMethods.round(warehouse.getMinCapacity(), warehouse.getStock().getUnit().getUnitRounding()));

            SXSSFRow concentrationRate = excelDocument.getSheet().createRow(jRow++);
            concentrationRate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("concentrationrate") + " : " + StaticMethods.round(warehouse.getConcentrationRate(), warehouse.getStock().getUnit().getUnitRounding()));

            SXSSFRow description = excelDocument.getSheet().createRow(jRow++);
            description.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("description") + " : " + (warehouse.getDescription() == null ? ' ' : warehouse.getDescription()));

            SXSSFRow availableQuantity = excelDocument.getSheet().createRow(jRow++);
            availableQuantity.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("availablequantity") + " : " + StaticMethods.round(warehouse.getLastQuantity(), warehouse.getStock().getUnit().getUnitRounding()));

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            System.out.println("tbvTankProc:dtbItems Form");
            StaticMethods.createHeaderExcel("tbvTankProc:dtbItems", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            int i = 0;

            while (rs.next()) {
//
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("movedate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("receiptnumber"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getInt("type") == 1 ? sessionBean.getLoc().getString("sales") : (rs.getInt("type") == 2 ? sessionBean.getLoc().getString("invoice") : sessionBean.getLoc().getString("warehousereceipt")));
                }
                if (toogleList.get(3)) {
                    BigDecimal Value = rs.getBoolean("wmis_direction") == true ? StaticMethods.round(rs.getBigDecimal("wmquantity"), rs.getInt("guntunitrounding")) : StaticMethods.round(BigDecimal.ZERO, rs.getInt("guntunitrounding"));
                    row.createCell((short) b++).setCellValue(Value.doubleValue());
                }
                if (toogleList.get(4)) {
                    BigDecimal Value = rs.getBoolean("wmis_direction") == false ? StaticMethods.round(rs.getBigDecimal("wmquantity"), rs.getInt("guntunitrounding")) : StaticMethods.round(BigDecimal.ZERO, rs.getInt("guntunitrounding"));
                    row.createCell((short) b++).setCellValue(Value.doubleValue());
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("lastquantity").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("unitpricewithouttaxrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("unitpricewithtaxrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("totalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("totalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("taxrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(rs.getString("usdname") + " " + rs.getString("usdsurname"));
                }
                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                }
                i++;
            }

            CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle.setAlignment(HorizontalAlignment.LEFT);

            /* for (Sales total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(total.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0));
                cell.setCellStyle(cellStyle);
            }
             */
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("tankmovements"));
            } catch (IOException ex) {
                            System.out.println("ex="+ex.toString());

                Logger.getLogger(TankMovementTabService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException e) {
            System.out.println("e="+e.toString());
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
                Logger.getLogger(TankMovementTabDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

}
