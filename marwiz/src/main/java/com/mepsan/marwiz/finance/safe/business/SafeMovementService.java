/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 08:30:36
 */
package com.mepsan.marwiz.finance.safe.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.finance.safe.dao.ISafeMovementDao;
import com.mepsan.marwiz.finance.safe.dao.SafeMovementDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

public class SafeMovementService implements ISafeMovementService {

    @Autowired
    public ISafeMovementDao safeMovementDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSafeMovementDao(ISafeMovementDao safeMovementDao) {
        this.safeMovementDao = safeMovementDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SafeMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId) {
       return safeMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where, safeString, listOfBranch, opType, beginDate, endDate, financingTypeId);
    }

  
    @Override
    public List<SafeMovement> count(String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId) {

        return safeMovementDao.count(where, safeString, listOfBranch, opType, beginDate, endDate, financingTypeId);
    }

    @Override
    public int count(String where, Safe safe, Branch branch) {
        return safeMovementDao.count(where, safe, branch);
    }

    @Override
    public void exportPdf(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, List<Safe> listOfSafe, List<Branch> listOfBranch, boolean isExtract, String inC, String outC, String balance, String transfer) {
        List<SafeMovement> resultList = new ArrayList<>();

        try {

            String safeList = "";
            for (Safe sf : listOfSafe) {
                safeList = safeList + "," + String.valueOf(sf.getId());
            }

            if (!safeList.equals("")) {
                safeList = safeList.substring(1, safeList.length());
            }
            resultList = safeMovementDao.exportData(createWhere, safeList, listOfBranch, opType, beginDate, endDate, financingTypeId);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            Currency currency = new Currency(listOfSafe.get(0).getCurrency().getId());
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("safemovements"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String safeName = "";
            if (listOfSafe.isEmpty()) {
                safeName = sessionBean.getLoc().getString("all");
            } else {
                for (Safe sf : listOfSafe) {
                    safeName += " , " + sf.getName();
                }

                safeName = safeName.substring(3, safeName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("safe") + " : " + safeName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (!isExtract) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("processtype") + " : " + (opType == 1 ? sessionBean.getLoc().getString("incoming") : opType == 2 ? sessionBean.getLoc().getString("outcoming") : sessionBean.getLoc().getString("all")), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                String financingType = "";
                if (financingTypeId == 0) {
                    financingType = sessionBean.getLoc().getString("all");
                } else {
                    for (Type t : sessionBean.getTypes(20)) {
                        if (t.getId() == financingTypeId) {
                            financingType = t.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                }
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("financingdocumenttype") + " : " + financingType, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            if (isExtract) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance") + " : " + transfer, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance") + " : " + sessionBean.getNumberFormat().format(transferringBalance) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());

            if (toogleList.get(0)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(1)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("safe"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(2)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("documentdate"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(3)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("documentnumber"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(4)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("createddate"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(5)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("createdperson"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(6)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("updateddate"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(7)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("updatedperson"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(8)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomeexpense"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(9)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(10)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("processtype"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(11)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("processamount"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(12)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("balance"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            for (SafeMovement sf : resultList) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getBranch().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getSafe().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), sf.getFinancingDocument().getDocumentDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    if (sf.getFinancingDocument().getId() > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sf.getFinancingDocument().getDocumentNumber(), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("openingbalance"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), sf.getDateCreated()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getUserCreated().getId() == 0 ? "" : sf.getUserCreated().getName() + " " + sf.getUserCreated().getSurname(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), sf.getDateUpdated()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getUserUpdated().getId() == 0 ? "" : sf.getUserUpdated().getName() + " " + sf.getUserUpdated().getSurname(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getFinancingDocument().getIncomeExpense().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getFinancingDocument().getDescription(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sf.getFinancingDocument().getFinancingType().getTag(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(11)) {
                    if (sf.getPrice() != null) {
                        if (sf.isIsDirection()) {
                            pdfDocument.getRightCell().setPhrase(new Phrase("+" + sessionBean.getNumberFormat().format(sf.getPrice()) + sessionBean.currencySignOrCode(sf.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getRightCell().setPhrase(new Phrase("-" + sessionBean.getNumberFormat().format(sf.getPrice()) + sessionBean.currencySignOrCode(sf.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                        }
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    int comp = sf.getBalance().compareTo(BigDecimal.valueOf(0));
                    if (comp == 1) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("+" + sessionBean.getNumberFormat().format(sf.getBalance()) + sessionBean.currencySignOrCode(sf.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-" + sessionBean.getNumberFormat().format((sf.getBalance().multiply(BigDecimal.valueOf(-1)))) + sessionBean.currencySignOrCode(sf.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            if (toogleList.get(0)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(1)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(2)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(3)) {
                pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            }
            if (toogleList.get(4)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(5)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(6)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(7)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(8)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(9)) {
                pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(10)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(11)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(12)) {
                if (isExtract) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(transfer, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                } else {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(transferringBalance) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

            }
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getCell());
            pdfDocument.getCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
            if (opType == 3 || opType == 1) {
                if (isExtract) {
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofincoming") + " : " + inC, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                } else {
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofincoming") + " : " + sessionBean.getNumberFormat().format(totalIncoming) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                }
            }
            if (opType == 2 || opType == 3) {
                if (isExtract) {
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofoutcoming") + " : " + outC, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                } else {
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofoutcoming") + " : " + sessionBean.getNumberFormat().format(totalOutcoming) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                }

            }
            if (isExtract) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalbalance") + " : " + balance, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalbalance") + " : " + sessionBean.getNumberFormat().format(totalBalance) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("safemovements"));

        } catch (DocumentException e) {
        }
    }

    @Override
    public void exportExcel(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, List<Safe> listOfSafe, List<Branch> listOfBranch, boolean isExtract, String inC, String outC, String balance, String transfer) {

        List<SafeMovement> resultList = new ArrayList<>();
        try {
            ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
            CellStyle dateFormatStyle = excelDocument.getWorkbook().createCellStyle();
            CreationHelper createHelper = excelDocument.getWorkbook().getCreationHelper();
            short dateFormat = createHelper.createDataFormat().getFormat(sessionBean.getUser().getLastBranch().getDateFormat());
            dateFormatStyle.setDataFormat(dateFormat);
            dateFormatStyle.setAlignment(HorizontalAlignment.LEFT);

            String safeList = "";
            for (Safe sf : listOfSafe) {
                safeList = safeList + "," + String.valueOf(sf.getId());
            }

            if (!safeList.equals("")) {
                safeList = safeList.substring(1, safeList.length());
            }

            resultList = safeMovementDao.exportData(createWhere, safeList, listOfBranch, opType, beginDate, endDate, financingTypeId);
            int jRow = 0;

            Currency currency = new Currency(listOfSafe.get(0).getCurrency().getId());
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("safemovements"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            String safeName = "";
            if (listOfSafe.isEmpty()) {
                safeName = sessionBean.getLoc().getString("all");
            } else {
                for (Safe sf : listOfSafe) {
                    safeName += " , " + sf.getName();
                }

                safeName = safeName.substring(3, safeName.length());
            }
            SXSSFRow safename = excelDocument.getSheet().createRow(jRow++);
            safename.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("safe") + " : " + safeName);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate));

            if (!isExtract) {
                SXSSFRow processtype = excelDocument.getSheet().createRow(jRow++);
                processtype.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("processtype") + " : " + (opType == 1 ? sessionBean.getLoc().getString("incoming") : opType == 2 ? sessionBean.getLoc().getString("outcoming") : sessionBean.getLoc().getString("all")));

                String financingType = "";
                if (financingTypeId == 0) {
                    financingType = sessionBean.getLoc().getString("all");
                } else {
                    for (Type t : sessionBean.getTypes(20)) {
                        if (t.getId() == financingTypeId) {
                            financingType = t.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                }

                SXSSFRow finantype = excelDocument.getSheet().createRow(jRow++);
                finantype.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("financingdocumenttype") + " : " + financingType);

            }

            if (isExtract) {
                SXSSFRow celltransferringTop = excelDocument.getSheet().createRow(jRow++);
                celltransferringTop.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("transferringbalance") + " : " + transfer);

            } else {
                SXSSFRow celltransferringTop = excelDocument.getSheet().createRow(jRow++);
                celltransferringTop.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("transferringbalance") + " : " + StaticMethods.round(transferringBalance, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));

            }

            SXSSFRow Rowempty = excelDocument.getSheet().createRow(jRow++);

            CellStyle styleTableHeader = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            int a = 0;
            SXSSFRow rowh = excelDocument.getSheet().createRow(jRow++);

            if (toogleList.get(0)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("branch"));
                cell.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(1)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("safe"));
                cell.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(2)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("documentdate"));
                cell.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(3)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("documentnumber"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(4)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("createddate"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(5)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("createdperson"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(6)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("updateddate"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(7)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("updatedperson"));
                cell1.setCellStyle(styleTableHeader);
            }

            if (toogleList.get(8)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("incomeexpense"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(9)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("description"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(10)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("processtype"));
                cell1.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(11)) {
                SXSSFCell cell2 = rowh.createCell((short) a++);
                cell2.setCellValue(sessionBean.getLoc().getString("processamount"));
                cell2.setCellStyle(styleTableHeader);
            }
            if (toogleList.get(12)) {
                SXSSFCell cell3 = rowh.createCell((short) a++);
                cell3.setCellValue(sessionBean.getLoc().getString("balance"));
                cell3.setCellStyle(styleTableHeader);
            }

            for (SafeMovement sf : resultList) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    SXSSFCell cell00 = row.createCell((short) b++);
                    cell00.setCellValue(sf.getBranch().getName());
                }
                if (toogleList.get(1)) {
                    SXSSFCell cell01 = row.createCell((short) b++);
                    cell01.setCellValue(sf.getSafe().getName());
                }
                if (toogleList.get(2)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(sf.getFinancingDocument().getDocumentDate());
                    cell0.setCellStyle(dateFormatStyle);
                }
                if (toogleList.get(3)) {
                    if (sf.getFinancingDocument().getId() > 0) {
                        row.createCell((short) b++).setCellValue(sf.getFinancingDocument().getDocumentNumber());
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("openingbalance"));
                    }

                }
                if (toogleList.get(4)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(sf.getDateCreated());
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(5)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(sf.getUserCreated().getId() == 0 ? "" : sf.getUserCreated().getName() + " " + sf.getUserCreated().getSurname());
                }
                if (toogleList.get(6)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(sf.getDateUpdated());
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(7)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(sf.getUserUpdated().getId() == 0 ? "" : sf.getUserUpdated().getName() + " " + sf.getUserUpdated().getSurname());
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(sf.getFinancingDocument().getIncomeExpense().getName());

                }
                if (toogleList.get(9)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(sf.getFinancingDocument().getDescription());
                }
                if (toogleList.get(10)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(sf.getFinancingDocument().getFinancingType().getTag());
                }
                if (toogleList.get(11)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    if (sf.getPrice() != null) {
                        if (sf.isIsDirection()) {
                            cell2.setCellValue(StaticMethods.round(sf.getPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        } else {
                            cell2.setCellValue(StaticMethods.round(sf.getPrice().multiply(BigDecimal.valueOf(-1)).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }
                    } else {
                        cell2.setCellValue(0);
                    }
                    cell2.setCellType(CellType.NUMERIC);
                }
                if (toogleList.get(12)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(StaticMethods.round(sf.getBalance().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell3.setCellType(CellType.NUMERIC);
                }

            }

            int c = 0;
            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            if (toogleList.get(0)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(1)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(2)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(3)) {

                row.createCell((short) c++).setCellValue(sessionBean.getLoc().getString("transferringbalance"));

            }
            if (toogleList.get(4)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(5)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(6)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(7)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(8)) {
                row.createCell((short) c++).setCellValue("");

            }
            if (toogleList.get(9)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue(sessionBean.getLoc().getString("transferringbalance"));
            }
            if (toogleList.get(10)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(11)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(12)) {
                SXSSFCell cell3 = row.createCell((short) c++);
                if (isExtract) {
                    cell3.setCellValue(transfer);
                    cell3.setCellType(CellType.NUMERIC);
                } else {
                    cell3.setCellValue(StaticMethods.round(transferringBalance.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell3.setCellType(CellType.NUMERIC);
                }

            }

            CellStyle styleFooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());

            if (opType == 1 || opType == 3) {
                SXSSFRow row1 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cell = row1.createCell((short) 0);
                if (isExtract) {
                    cell.setCellValue(sessionBean.getLoc().getString("sumofincoming") + " : " + inC);
                    cell.setCellStyle(styleFooter);
                } else {
                    cell.setCellValue(sessionBean.getLoc().getString("sumofincoming") + " : " + StaticMethods.round(totalIncoming, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));
                    cell.setCellStyle(styleFooter);
                }

            }
            if (opType == 2 || opType == 3) {

                SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cell = row2.createCell((short) 0);
                if (isExtract) {
                    cell.setCellValue(sessionBean.getLoc().getString("sumofoutcoming") + " : " + outC);
                    cell.setCellStyle(styleFooter);
                } else {
                    cell.setCellValue(sessionBean.getLoc().getString("sumofoutcoming") + " : " + StaticMethods.round(totalOutcoming, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));
                    cell.setCellStyle(styleFooter);
                }

            }

            SXSSFRow row3 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellbalance = row3.createCell((short) 0);
            if (isExtract) {
                cellbalance.setCellValue(sessionBean.getLoc().getString("totalbalance") + " : " + balance);
                cellbalance.setCellStyle(styleFooter);
            } else {
                cellbalance.setCellValue(sessionBean.getLoc().getString("totalbalance") + " : " + StaticMethods.round(totalBalance, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));
                cellbalance.setCellStyle(styleFooter);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("safemovements"));
            } catch (IOException ex) {
                Logger.getLogger(SafeMovementService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
        }

    }

    @Override
    public String exportPrinter(String createWhere, List<Boolean> toogleList,
            BigDecimal transferringBalance, BigDecimal totalIncoming,
            BigDecimal totalOutcoming, BigDecimal totalBalance,
            int opType, Date beginDate,
            Date endDate, int financingTypeId, List<Safe> listOfSafe,
            List<Branch> listOfBranch, boolean isExtract, String inC, String outC, String balance, String transfer
    ) {


        List<SafeMovement> listResult = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try {

            String safeList = "";
            for (Safe sf : listOfSafe) {
                safeList = safeList + "," + String.valueOf(sf.getId());
            }

            if (!safeList.equals("")) {
                safeList = safeList.substring(1, safeList.length());
            }
            listResult = safeMovementDao.exportData(createWhere, safeList, listOfBranch, opType, beginDate, endDate, financingTypeId);


            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            Currency currency = new Currency(listOfSafe.get(0).getCurrency().getId());
            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            String safeName = "";
            if (listOfSafe.isEmpty()) {
                safeName = sessionBean.getLoc().getString("all");
            } else {
                for (Safe sf : listOfSafe) {
                    safeName += " , " + sf.getName();
                }

                safeName = safeName.substring(3, safeName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("safe")).append(" : ").append(safeName).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate)).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate)).append(" </div> ");

            if (!isExtract) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("processtype")).append(" : ").append((opType == 1 ? sessionBean.getLoc().getString("incoming") : opType == 2 ? sessionBean.getLoc().getString("outcoming") : sessionBean.getLoc().getString("all"))).append(" </div> ");

                String financingType = "";
                if (financingTypeId == 0) {
                    financingType = sessionBean.getLoc().getString("all");
                } else {
                    for (Type t : sessionBean.getTypes(20)) {
                        if (t.getId() == financingTypeId) {
                            financingType = t.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("financingdocumenttype")).append(" : ").append(financingType).append(" </div> ");

            }
            if (isExtract) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("transferringbalance")).append(" : ").append(transfer).append(" ").append(" </div> ");
            } else {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("transferringbalance")).append(" : ").append(sessionBean.getNumberFormat().format(transferringBalance)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append(" </div> ");
            }
            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <style>"
                    + "        #printerDiv table {"
                    + "            font-family: arial, sans-serif;"
                    + "            border-collapse: collapse;"
                    + "            width: 100%;"
                    + "        }"
                    + "        #printerDiv table tr td, #printerDiv table tr th {"
                    + "            border: 1px solid #dddddd;"
                    + "            text-align: left;"
                    + "            padding: 8px;"
                    + "        }"
                    + "   @page { size: landscape; }"
                    + "    </style> <table>");

            if (toogleList.get(0)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("branch")).append("</th>");
            }
            if (toogleList.get(1)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("safe")).append("</th>");
            }
            if (toogleList.get(2)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("documentdate")).append("</th>");
            }
            if (toogleList.get(3)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("documentnumber")).append("</th>");
            }
            if (toogleList.get(4)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("createddate")).append("</th>");
            }

            if (toogleList.get(5)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("createdperson")).append("</th>");
            }

            if (toogleList.get(6)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("updateddate")).append("</th>");
            }

            if (toogleList.get(7)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("updatedperson")).append("</th>");
            }

            if (toogleList.get(8)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("incomeexpense")).append("</th>");
            }
            if (toogleList.get(9)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("description")).append("</th>");
            }
            if (toogleList.get(10)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("processtype")).append("</th>");
            }

            if (toogleList.get(11)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("processamount")).append("</th>");
            }
            if (toogleList.get(12)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("balance")).append("</th>");
            }

            sb.append(" </tr>  ");

            for (SafeMovement safemov : listResult) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(safemov.getBranch().getName() == null ? "" : safemov.getBranch().getName()).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(safemov.getSafe().getName() == null ? "" : safemov.getSafe().getName()).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(safemov.getFinancingDocument().getDocumentDate() == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), safemov.getFinancingDocument().getDocumentDate())).append("</td>");
                }
                if (toogleList.get(3)) {
                    if (safemov.getFinancingDocument().getId() > 0) {
                        sb.append("<td>").append(safemov.getFinancingDocument().getDocumentNumber() == null ? "" : safemov.getFinancingDocument().getDocumentNumber()).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("openingbalance")).append("</td>");
                    }

                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(safemov.getDateCreated() == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), safemov.getDateCreated())).append("</td>");
                }
                if (toogleList.get(5)) {

                    sb.append("<td>").append(safemov.getUserCreated().getName() == null ? "" : safemov.getUserCreated().getName()).append(" ").append(safemov.getUserCreated().getSurname() == null ? "" : safemov.getUserCreated().getSurname()).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(safemov.getDateUpdated() == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), safemov.getDateUpdated())).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(safemov.getUserUpdated().getName() == null ? "" : safemov.getUserUpdated().getName()).append(" ").append(safemov.getUserUpdated().getSurname() == null ? "" : safemov.getUserUpdated().getSurname()).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(safemov.getFinancingDocument().getIncomeExpense().getName() == null ? "" : safemov.getFinancingDocument().getIncomeExpense().getName()).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(safemov.getFinancingDocument().getDescription() == null ? "" : safemov.getFinancingDocument().getDescription()).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td>").append(safemov.getFinancingDocument().getFinancingType().getTag() == null ? "" : safemov.getFinancingDocument().getFinancingType().getTag()).append("</td>");
                }
                if (toogleList.get(11)) {
                    if (safemov.getPrice() != null) {
                        if (safemov.isIsDirection()) {
                            sb.append("<td style=\"text-align: right\">").append("+").append(sessionBean.getNumberFormat().format(safemov.getPrice())).append(sessionBean.currencySignOrCode(safemov.getSafe().getCurrency().getId(), 0)).append("</td>");
                        } else {
                            sb.append("<td style=\"text-align: right\">").append("-").append(sessionBean.getNumberFormat().format(safemov.getPrice())).append(sessionBean.currencySignOrCode(safemov.getSafe().getCurrency().getId(), 0)).append("</td>");
                        }
                    } else {
                        sb.append("<td>").append("").append("</td>");
                    }
                }
                if (toogleList.get(12)) {
                    int comp = safemov.getBalance().compareTo(BigDecimal.valueOf(-1));
                    if (comp == 1) {
                        sb.append("<td style=\"text-align: right\">").append("+").append(sessionBean.getNumberFormat().format(safemov.getBalance())).append(sessionBean.currencySignOrCode(safemov.getSafe().getCurrency().getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append(sessionBean.getNumberFormat().format((safemov.getBalance().multiply(BigDecimal.valueOf(-1))))).append(sessionBean.currencySignOrCode(safemov.getSafe().getCurrency().getId(), 0)).append("</td>");
                    }
                }

                sb.append(" </tr> ");

            }

            sb.append(" <tr> ");

            if (toogleList.get(0)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(1)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(2)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(3)) {
                sb.append("<td>").append(sessionBean.getLoc().getString("transferringbalance")).append("</td>");

            }
            if (toogleList.get(4)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(5)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(6)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(7)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(8)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(9)) {
                sb.append("<td>").append(sessionBean.getLoc().getString("transferringbalance")).append("</td>");
            }
            if (toogleList.get(10)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(11)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(12)) {
                if (isExtract) {
                    sb.append("<td style=\"text-align: right\">").append(transfer).append("</td>");
                } else {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(transferringBalance)).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
            }

            sb.append(" </tr> ");

            if (opType == 3 || opType == 1) {
                sb.append(" <tr> ");
                if (isExtract) {
                    sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumofincoming")).append(" : ").append(inC).append("</td>");
                } else {
                    sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumofincoming")).append(" : ").append(sessionBean.getNumberFormat().format(totalIncoming)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                sb.append(" </tr> ");
            }
            if (opType == 3 || opType == 2) {
                sb.append(" <tr> ");
                if (isExtract) {
                    sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumofoutcoming")).append(" : ").append(outC).append("</td>");
                } else {
                    sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumofoutcoming")).append(" : ").append(sessionBean.getNumberFormat().format(totalOutcoming)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                sb.append(" </tr> ");
            }
            sb.append(" <tr> ");
            if (isExtract) {

            } else {

            }
            if (isExtract) {
                sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalbalance")).append(" : ").append(balance).append("</td>");
            } else {
                sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalbalance")).append(" : ").append(sessionBean.getNumberFormat().format(totalBalance)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
            }
            sb.append(" </tr> ");

        } catch (Exception e) {
        }

        return sb.toString();
    }

   

}
