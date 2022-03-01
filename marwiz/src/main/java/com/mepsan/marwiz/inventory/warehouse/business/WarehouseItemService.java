/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   22.01.2018 12:35:42
 */
package com.mepsan.marwiz.inventory.warehouse.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.inventory.warehouse.dao.IWarehouseItemDao;
import com.mepsan.marwiz.inventory.warehouse.dao.WarehouseItemDao;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseItemService implements IWarehouseItemService {

    @Autowired
    private IWarehouseItemDao warehouseItemDao;

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWarehouseItemDao(IWarehouseItemDao warehouseItemDao) {
        this.warehouseItemDao = warehouseItemDao;
    }

    @Override
    public List<WarehouseItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse wareHouse) {
        return warehouseItemDao.findAll(first, pageSize, sortField, sortOrder, filters, where, wareHouse);
    }

    @Override
    public int count(String where, Warehouse wareHouse) {
        return warehouseItemDao.count(where, wareHouse);
    }

    @Override
    public int addStock(Warehouse warehouse, List<Stock> listOfStock) {
        String where = "";
        String stockList = "";
        for (Stock stock : listOfStock) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            } else if (stock.getId() == -1) {
                stockList = "-1";
                break;
            }
        }
        if (!stockList.equals("") && !stockList.equals("-1")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " stck.id IN(" + stockList + ") ";
        } else {
            String where2 = "";
            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where2 = " AND si.is_valid = TRUE ";
            } else {
                where2 = " AND stck.is_otherbranch = TRUE  ";
            }
            where = where + " stck.deleted=FALSE " + where2;
            if (stockList.equals("-1")) {
                where += " AND si.currentsaleprice >0 ";
            }

        }
        where = where
                + " AND NOT EXISTS (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = " + warehouse.getId() + " AND iwi.deleted=FALSE AND stck.id = iwi.stock_id) ";
        return warehouseItemDao.addStock(warehouse, where);
    }

    @Override
    public int update(WarehouseItem obj) {
        return warehouseItemDao.update(obj);
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList, Warehouse wareHouse) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {

            connection = warehouseItemDao.getDatasource().getConnection();
            prep = connection.prepareStatement(warehouseItemDao.exportData(where));
            prep.setInt(1, wareHouse.getId());

            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 1);
            pdfDocument.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

            float[] widths = {10f, 10f, 28f, 28f, 10f, 10f};
            pdfDocument.getPdfTable().setWidths(widths);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stocks"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());
            pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, Color.WHITE));

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("centerstockcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockbarcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockname"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("balance"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("minstocklevel"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("iwiquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {

                    if (rs.getBigDecimal("iwiminstocklevel") != null) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("iwiminstocklevel")), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
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
                Logger.getLogger(WarehouseItemDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

}
