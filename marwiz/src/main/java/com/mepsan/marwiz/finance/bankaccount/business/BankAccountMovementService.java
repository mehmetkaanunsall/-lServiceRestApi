/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.finance.bankaccount.dao.BankAccountMovementDao;
import com.mepsan.marwiz.finance.bankaccount.dao.IBankAccountMovementDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
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
import org.springframework.beans.factory.annotation.Autowired;

public class BankAccountMovementService implements IBankAccountMovementService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IBankAccountMovementDao bankAccountMovementDao;

    public void setBankAccountMovementDao(IBankAccountMovementDao bankAccountMovementDao) {
        this.bankAccountMovementDao = bankAccountMovementDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<BankAccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId) {
        return bankAccountMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where, bankAcount, opType, beginDate, endDate, branchList, financingTypeId);
    }

    /*
    @Override
    public int count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate) {
        return bankAccountMovementDao.count(where, bankAcount, opType, beginDate, endDate);
    }
     */
    @Override
    public BankAccountMovement count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId) {
        return bankAccountMovementDao.count(where, bankAcount, opType, beginDate, endDate, branchList, financingTypeId);
    }

    @Override
    public int count(String where, BankAccount bankAcount, Branch branch) {
        return bankAccountMovementDao.count(where, bankAcount, branch);
    }

    @Override
    public void exportPdf(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, BankAccount selectedBankAccount, boolean isExtract, List<Branch> listOfBranch) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            String branchList = "";
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }

            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
            connection = bankAccountMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(bankAccountMovementDao.exportData(createWhere, selectedBankAccount, opType, beginDate, endDate, branchList, financingTypeId));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            Currency currency = new Currency(selectedBankAccount.getCurrency().getId());

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("bankaccountmovements"), pdfDocument.getFontHeader()));

            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (!isExtract) {
                String branchName = "";
                if (listOfBranch.isEmpty()) {
                    branchName = sessionBean.getLoc().getString("all");
                } else {
                    for (Branch br : listOfBranch) {
                        branchName += " , " + br.getName();
                    }

                    branchName = branchName.substring(3, branchName.length());
                }
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("bankaccount") + " : " + selectedBankAccount.getName(), pdfDocument.getFont()));
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
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance") + " : " + sessionBean.getNumberFormat().format(transferringBalance) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());

            if (toogleList.get(0)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(1)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("documentdate"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(2)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("documentnumber"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(3)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("createddate"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(4)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("createdperson"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(5)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("updateddate"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(6)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("updatedperson"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(7)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomeexpense"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(8)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(9)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("processtype"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(10)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("processamount"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(11)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("balance"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocdocumnetdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("fdocid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fdocdocumentnumber"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("openingbalance"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocc_time")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getInt("fdocc_id") == 0 ? "" : rs.getString("usrname") + " " + rs.getString("usrsurname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocu_time")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getInt("fdocu_id") == 0 ? "" : rs.getString("usr1name") + " " + rs.getString("usr1surname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fiename"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fdocdescription"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("typdname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    if (rs.getBoolean("bkamis_direction")) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("+" + sessionBean.getNumberFormat().format(rs.getBigDecimal("bkamprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-" + sessionBean.getNumberFormat().format(rs.getBigDecimal("bkamprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    int comp = rs.getBigDecimal("bkabalance").compareTo(BigDecimal.valueOf(0));
                    if (comp == 1) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("+" + sessionBean.getNumberFormat().format(rs.getBigDecimal("bkabalance")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-" + sessionBean.getNumberFormat().format((rs.getBigDecimal("bkabalance").multiply(BigDecimal.valueOf(-1)))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
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
                pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            }
            if (toogleList.get(3)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
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
                pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(9)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(10)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(11)) {
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(transferringBalance) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getCell());
            pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            if (opType == 3 || opType == 1) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofincoming") + " : " + sessionBean.getNumberFormat().format(totalIncoming) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            if (opType == 2 || opType == 3) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofoutcoming") + " : " + sessionBean.getNumberFormat().format(totalOutcoming) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalbalance") + " : " + sessionBean.getNumberFormat().format(totalBalance) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("bankaccountmovements"));

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
                Logger.getLogger(BankAccountMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, BankAccount selectedBankAccount, boolean isExtract, List<Branch> listOfBranch) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        CellStyle dateFormatStyle = excelDocument.getWorkbook().createCellStyle();
        CreationHelper createHelper = excelDocument.getWorkbook().getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat(sessionBean.getUser().getLastBranch().getDateFormat());
        dateFormatStyle.setDataFormat(dateFormat);
        dateFormatStyle.setAlignment(HorizontalAlignment.LEFT);

        try {
            String branchList = "";
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
            connection = bankAccountMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(bankAccountMovementDao.exportData(createWhere, selectedBankAccount, opType, beginDate, endDate, branchList, financingTypeId));
            rs = prep.executeQuery();

            Currency currency = new Currency(selectedBankAccount.getCurrency().getId());

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());

            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);

            cellheader.setCellValue(sessionBean.getLoc().getString("bankaccountmovements"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            if (!isExtract) {
                String branchName = "";
                if (listOfBranch.isEmpty()) {
                    branchName = sessionBean.getLoc().getString("all");
                } else {
                    for (Branch br : listOfBranch) {
                        branchName += " , " + br.getName();
                    }

                    branchName = branchName.substring(3, branchName.length());
                }
                SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
                brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);
            }

            SXSSFRow bankname = excelDocument.getSheet().createRow(jRow++);
            bankname.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("bankaccount") + " : " + selectedBankAccount.getName());

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

            SXSSFRow celltransferringTop = excelDocument.getSheet().createRow(jRow++);
            celltransferringTop.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("transferringbalance") + " : " + StaticMethods.round(transferringBalance, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            int a = 0;
            SXSSFRow rowh = excelDocument.getSheet().createRow(jRow++);
            if (toogleList.get(0)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("branch"));
                cell.setCellStyle(styleheader);
            }
            if (toogleList.get(1)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("documentdate"));
                cell.setCellStyle(styleheader);
            }
            if (toogleList.get(2)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("documentnumber"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(3)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("createddate"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(4)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("createdperson"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(5)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("updateddate"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(6)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("updatedperson"));
                cell1.setCellStyle(styleheader);
            }

            if (toogleList.get(7)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("incomeexpense"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(8)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("description"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(9)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("processtype"));
                cell1.setCellStyle(styleheader);
            }

            if (toogleList.get(10)) {
                SXSSFCell cell2 = rowh.createCell((short) a++);
                cell2.setCellValue(sessionBean.getLoc().getString("processamount"));
                cell2.setCellStyle(styleheader);
            }
            if (toogleList.get(11)) {
                SXSSFCell cell3 = rowh.createCell((short) a++);
                cell3.setCellValue(sessionBean.getLoc().getString("balance"));
                cell3.setCellStyle(styleheader);
            }

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(1)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("fdocdocumnetdate"));
                    cell0.setCellStyle(dateFormatStyle);
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("fdocid") > 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("fdocdocumentnumber"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("openingbalance"));
                    }

                }
                if (toogleList.get(3)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("fdocc_time"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(4)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(rs.getInt("fdocc_id") == 0 ? "" : rs.getString("usrname") + " " + rs.getString("usrsurname"));
                }
                if (toogleList.get(5)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("fdocu_time"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(6)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(rs.getInt("fdocu_id") == 0 ? "" : rs.getString("usr1name") + " " + rs.getString("usr1surname"));
                }
                if (toogleList.get(7)) {

                    row.createCell((short) b++).setCellValue(rs.getString("fiename"));
                }
                if (toogleList.get(8)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(rs.getString("fdocdescription"));
                }
                if (toogleList.get(9)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(rs.getString("typdname"));
                }

                if (toogleList.get(10)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    if (rs.getBoolean("bkamis_direction")) {
                        cell2.setCellValue(StaticMethods.round(rs.getBigDecimal("bkamprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        cell2.setCellValue(StaticMethods.round(rs.getBigDecimal("bkamprice").multiply(BigDecimal.valueOf(-1)).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    cell2.setCellType(CellType.NUMERIC);
                }
                if (toogleList.get(11)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(StaticMethods.round(rs.getBigDecimal("bkabalance").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
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

                row.createCell((short) c++).setCellValue(sessionBean.getLoc().getString("transferringbalance"));

            }
            if (toogleList.get(3)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(4)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(5)) {
                SXSSFCell cell0 = row.createCell((short) c++);
                cell0.setCellValue("");
            }
            if (toogleList.get(6)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(7)) {
                row.createCell((short) c++).setCellValue("");

            }
            if (toogleList.get(8)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue(sessionBean.getLoc().getString("transferringbalance"));
            }
            if (toogleList.get(9)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(10)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(11)) {
                SXSSFCell cell3 = row.createCell((short) c++);
                cell3.setCellValue(StaticMethods.round(transferringBalance.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell3.setCellType(CellType.NUMERIC);
            }

            stylefooter.setAlignment(HorizontalAlignment.LEFT);

            if (opType == 1 || opType == 3) {
                SXSSFRow row1 = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row1.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sumofincoming") + " : " + StaticMethods.round(totalIncoming, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));
                cell.setCellStyle(stylefooter);
            }
            if (opType == 2 || opType == 3) {
                SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row2.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sumofoutcoming") + " : " + StaticMethods.round(totalOutcoming, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));
                cell.setCellStyle(stylefooter);
            }
            SXSSFRow row3 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellbalance = row3.createCell((short) 0);
            cellbalance.setCellValue(sessionBean.getLoc().getString("totalbalance") + " : " + StaticMethods.round(totalBalance, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));
            cellbalance.setCellStyle(stylefooter);

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("bankaccountmovements"));
            } catch (IOException ex) {
                Logger.getLogger(BankAccountMovementService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException e) {
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
                Logger.getLogger(BankAccountMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, int financingTypeId, BankAccount selectedBankAccount, boolean isExtract, List<Branch> listOfBranch) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        try {
            String branchList = "";
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
            connection = bankAccountMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(bankAccountMovementDao.exportData(createWhere, selectedBankAccount, opType, beginDate, endDate, branchList, financingTypeId));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            Currency currency = new Currency(selectedBankAccount.getCurrency().getId());

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            if (!isExtract) {
                String branchName = "";
                if (listOfBranch.isEmpty()) {
                    branchName = sessionBean.getLoc().getString("all");
                } else {
                    for (Branch br : listOfBranch) {
                        branchName += " , " + br.getName();
                    }

                    branchName = branchName.substring(3, branchName.length());
                }
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("bankaccount")).append(" : ").append(selectedBankAccount.getName()).append(" </div> ");
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
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("transferringbalance")).append(" : ").append(sessionBean.getNumberFormat().format(transferringBalance)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append(" </div> ");

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
                      + "    </style> <table> ");

            if (toogleList.get(0)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("branch")).append("</th>");
            }
            if (toogleList.get(1)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("documentdate")).append("</th>");
            }
            if (toogleList.get(2)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("documentnumber")).append("</th>");
            }
            if (toogleList.get(3)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("createddate")).append("</th>");
            }

            if (toogleList.get(4)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("createdperson")).append("</th>");
            }

            if (toogleList.get(5)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("updateddate")).append("</th>");
            }

            if (toogleList.get(6)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("updatedperson")).append("</th>");
            }
            if (toogleList.get(7)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("incomeexpense")).append("</th>");
            }
            if (toogleList.get(8)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("description")).append("</th>");
            }
            if (toogleList.get(9)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("processtype")).append("</th>");
            }

            if (toogleList.get(10)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("processamount")).append("</th>");
            }
            if (toogleList.get(11)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("balance")).append("</th>");
            }

            sb.append(" </tr>  ");

            while (rs.next()) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getTimestamp("fdocdocumnetdate") == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocdocumnetdate"))).append("</td>");
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("fdocid") > 0) {
                        sb.append("<td>").append(rs.getString("fdocdocumentnumber") == null ? "" : rs.getString("fdocdocumentnumber")).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("openingbalance")).append("</td>");
                    }

                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getTimestamp("fdocc_time") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocc_time"))).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("usrname") == null ? "" : rs.getString("usrname")).append(" ").append(rs.getString("usrsurname") == null ? "" : rs.getString("usrsurname")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getTimestamp("fdocu_time") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocu_time"))).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("usr1name") == null ? "" : rs.getString("usr1name")).append(" ").append(rs.getString("usr1surname") == null ? "" : rs.getString("usr1surname")).append("</td>");
                }
                if (toogleList.get(7)) {

                    sb.append("<td>").append(rs.getString("fiename") == null ? "" : rs.getString("fiename")).append("</td>");

                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("fdocdescription") == null ? "" : rs.getString("fdocdescription")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("typdname") == null ? "" : rs.getString("typdname")).append("</td>");
                }

                if (toogleList.get(10)) {
                    if (rs.getBoolean("bkamis_direction")) {
                        sb.append("<td style=\"text-align: right\">").append("+").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("bkamprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("bkamprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    }
                }
                if (toogleList.get(11)) {
                    int comp = rs.getBigDecimal("bkabalance").compareTo(BigDecimal.valueOf(0));
                    if (comp == 1) {
                        sb.append("<td style=\"text-align: right\">").append("+").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("bkabalance"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("bkabalance").multiply(BigDecimal.valueOf(-1))))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
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
                sb.append("<td>").append(sessionBean.getLoc().getString("transferringbalance")).append("</td>");

            }
            if (toogleList.get(3)) {
                sb.append("<td>").append("").append("</td>");
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
                sb.append("<td>").append(sessionBean.getLoc().getString("transferringbalance")).append("</td>");
            }
            if (toogleList.get(9)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(10)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(11)) {
                sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(transferringBalance)).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
            }

            sb.append(" </tr> ");

            if (opType == 3 || opType == 1) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumofincoming")).append(" : ").append(sessionBean.getNumberFormat().format(totalIncoming)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                sb.append(" </tr> ");
            }
            if (opType == 3 || opType == 2) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumofoutcoming")).append(" : ").append(sessionBean.getNumberFormat().format(totalOutcoming)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                sb.append(" </tr> ");
            }
            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalbalance")).append(" : ").append(sessionBean.getNumberFormat().format(totalBalance)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
            sb.append(" </tr> ");

            sb.append(" </table> ");
        } catch (SQLException e) {
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
                Logger.getLogger(BankAccountMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return sb.toString();
    }

    @Override
    public int controlMovement(String where, BankAccount bankAcount, Branch branch) {
        return bankAccountMovementDao.controlMovement(where, bankAcount, branch);
    }

}
