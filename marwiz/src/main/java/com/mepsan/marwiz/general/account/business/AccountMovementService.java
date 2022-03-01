/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.01.2018 01:17:19
 */
package com.mepsan.marwiz.general.account.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.account.dao.AccountMovementDao;
import com.mepsan.marwiz.general.account.dao.IAccountMovementDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import java.awt.Color;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountMovementService implements IAccountMovementService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IAccountMovementDao accountMovementDao;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountMovementDao(IAccountMovementDao accountMovementDao) {
        this.accountMovementDao = accountMovementDao;
    }

    @Override
    public List<AccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId) {
        return accountMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where, account, opType, beginDate, endDate, termDate, termDateOpType, branchList, financingTypeId);
    }

    @Override
    public AccountMovement count(String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId) {
        return accountMovementDao.count(where, account, opType, beginDate, endDate, termDate, termDateOpType, branchList, financingTypeId);
    }

    @Override
    public void exportPdf(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, Account account, boolean isExtract, int pageId, String sortField, String sortOrder, Date termDate, int termDateUpType, List<Branch> listOfBranch, int financingTypeId) {

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
            connection = accountMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(accountMovementDao.exportData(createWhere, account, opType, beginDate, endDate, sortField, sortOrder, termDate, termDateUpType, branchList, financingTypeId));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            if (pageId == 11 || pageId == 72) {
                pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accountmovements"), pdfDocument.getFontHeader()));
            } else {
                pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("employeemovements"), pdfDocument.getFontHeader()));

            }
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

            if (pageId == 11 || pageId == 72) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("account") + " : " + account.getName(), pdfDocument.getFont()));
            } else {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("employee") + " : " + account.getName(), pdfDocument.getFont()));
            }
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

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

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
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("termdate"), pdfDocument.getFontColumnTitle()));
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
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("foreignexchangetransactionamount"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(11)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("debt"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(12)) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("receivable"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }
            if (toogleList.get(13)) {
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
                    if (rs.getInt("fdocid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocdocumentdate")), pdfDocument.getFont()));
                    } else if (rs.getInt("invid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate")), pdfDocument.getFont()));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("rcpprocessdate")), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("accmmovementdate")), pdfDocument.getFont()));

                    }

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("fdocid") == 0 && rs.getInt("accmchequebill_id") == 0 && rs.getInt("accmreceipt_id") == 0 && rs.getInt("invid") == 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferbalance"), pdfDocument.getFont()));
                    } else if (rs.getInt("fdocid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fdocdocumentnumber"), pdfDocument.getFont()));
                    } else if (rs.getInt("accmchequebill_id") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("cqbportfolionumber"), pdfDocument.getFont()));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("rcpreceiptno"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumentnumber"), pdfDocument.getFont()));
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
                    if (rs.getInt("fdocid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    } else if (rs.getInt("invid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invduedate")), pdfDocument.getFont()));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));

                    }

                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fdocdescription"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    if (rs.getInt("fdocid") == 0 && rs.getInt("accmchequebill_id") == 0 && rs.getInt("accmreceipt_id") == 0 && rs.getInt("invid") == 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    } else if (rs.getInt("fdocid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("typdname"), pdfDocument.getFont()));
                    } else if (rs.getInt("accmchequebill_id") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("chequebill"), pdfDocument.getFont()));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("receipt"), pdfDocument.getFont()));
                    } else if (rs.getInt("invid") > 0) {
                        if (rs.getBoolean("invis_purchase")) {
                            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("purchaseinvoice"), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesinvoice"), pdfDocument.getFont()));
                        }
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(10)) {
                    Currency currencyMovement = new Currency(rs.getInt("accmcurrency_id"));
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("accmprice")) + sessionBean.currencySignOrCode(currencyMovement.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    if (rs.getBoolean("accmis_direction")) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-", pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("accmprice").multiply(rs.getBigDecimal("accmexchangerate"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    if (rs.getBoolean("accmis_direction")) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("accmprice").multiply(rs.getBigDecimal("accmexchangerate"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    int comp = rs.getBigDecimal("accbalance").compareTo(BigDecimal.valueOf(0));
                    if (comp == 1) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("accbalance")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format((rs.getBigDecimal("accbalance").multiply(BigDecimal.valueOf(-1)))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
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
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(12)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(13)) {
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(transferringBalance) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getCell());

            if (opType == 3 || opType == 1) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofincoming") + " : " + sessionBean.getNumberFormat().format(totalIncoming) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            if (opType == 2 || opType == 3) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofoutcoming") + " : " + sessionBean.getNumberFormat().format(totalOutcoming) + " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalbalance") + " : " + (totalBalance.compareTo(BigDecimal.valueOf(0)) == -1 ? sessionBean.getNumberFormat().format(totalBalance.multiply(BigDecimal.valueOf(-1))) : sessionBean.getNumberFormat().format(totalBalance)) + " " + sessionBean.currencySignOrCode(currency.getId(), 0) + " (" + (manageBalanceSign(totalBalance) ? sessionBean.getLoc().getString("payable") : sessionBean.getLoc().getString("receivabled")) + ")", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            if (pageId == 11 || pageId == 72) {
                StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("accountmovements"));
            } else {
                StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("employeemovements"));
            }

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
                Logger.getLogger(AccountMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, Account account, boolean isExtract, int pageId, String sortField, String sortOrder, Date termDate, int termDateUpType, List<Branch> listOfBranch, int financingTypeId) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            String branchList = "";
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
            connection = accountMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(accountMovementDao.exportData(createWhere, account, opType, beginDate, endDate, sortField, sortOrder, termDate, termDateUpType, branchList, financingTypeId));
            rs = prep.executeQuery();

            int jRow = 0;

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());

            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);

            if (pageId == 11 || pageId == 72) {
                cellheader.setCellValue(sessionBean.getLoc().getString("accountmovements"));
            } else {
                cellheader.setCellValue(sessionBean.getLoc().getString("employeemovements"));
            }
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

            SXSSFRow accountname = excelDocument.getSheet().createRow(jRow++);
            if (pageId == 11 || pageId == 72) {
                accountname.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("account") + " : " + account.getName());
            } else {
                accountname.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("employee") + " : " + account.getName());
            }

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
            SXSSFRow transfer = excelDocument.getSheet().createRow(jRow++);
            transfer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("transferringbalance") + " : " + StaticMethods.round(transferringBalance, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(currency.getId(), 0));

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
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("termdate"));
                cell.setCellStyle(styleheader);
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
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("foreignexchangetransactionamount"));
                cell1.setCellStyle(styleheader);
            }
            if (toogleList.get(11)) {
                SXSSFCell cell2 = rowh.createCell((short) a++);
                cell2.setCellValue(sessionBean.getLoc().getString("debt"));
                cell2.setCellStyle(styleheader);
            }
            if (toogleList.get(12)) {
                SXSSFCell cell2 = rowh.createCell((short) a++);
                cell2.setCellValue(sessionBean.getLoc().getString("receivable"));
                cell2.setCellStyle(styleheader);
            }
            if (toogleList.get(13)) {
                SXSSFCell cell3 = rowh.createCell((short) a++);
                cell3.setCellValue(sessionBean.getLoc().getString("balance"));
                cell3.setCellStyle(styleheader);
            }

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    SXSSFCell cell00 = row.createCell((short) b++);
                    cell00.setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(1)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    if (rs.getInt("fdocid") > 0) {
                        cell0.setCellValue(rs.getTimestamp("fdocdocumentdate"));
                    } else if (rs.getInt("invid") > 0) {
                        cell0.setCellValue(rs.getTimestamp("invinvoicedate"));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        cell0.setCellValue(rs.getTimestamp("rcpprocessdate"));
                    } else {
                        cell0.setCellValue(rs.getTimestamp("accmmovementdate"));
                    }

                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("fdocid") == 0 && rs.getInt("accmchequebill_id") == 0 && rs.getInt("accmreceipt_id") == 0 && rs.getInt("invid") == 0) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("transferbalance"));
                    } else if (rs.getInt("fdocid") > 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("fdocdocumentnumber"));
                    } else if (rs.getInt("accmchequebill_id") > 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("cqbportfolionumber"));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("rcpreceiptno"));
                    } else {
                        row.createCell((short) b++).setCellValue(rs.getString("invdocumentnumber"));
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
                    SXSSFCell cell0 = row.createCell((short) b++);
                    if (rs.getInt("fdocid") > 0) {
                        cell0.setCellValue("");
                    } else if (rs.getInt("invid") > 0) {
                        cell0.setCellValue(rs.getTimestamp("invduedate"));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        cell0.setCellValue("");
                    } else {
                        cell0.setCellValue("");
                    }

                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }

                if (toogleList.get(8)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(rs.getString("fdocdescription"));
                }
                if (toogleList.get(9)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    if (rs.getInt("fdocid") == 0 && rs.getInt("accmchequebill_id") == 0 && rs.getInt("accmreceipt_id") == 0 && rs.getInt("invid") == 0) {
                        cell2.setCellValue("");
                    } else if (rs.getInt("fdocid") > 0) {
                        cell2.setCellValue(rs.getString("typdname"));
                    } else if (rs.getInt("accmchequebill_id") > 0) {
                        cell2.setCellValue(sessionBean.getLoc().getString("chequebill"));
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        cell2.setCellValue(sessionBean.getLoc().getString("receipt"));
                    } else if (rs.getInt("invid") > 0) {
                        if (rs.getBoolean("invis_purchase")) {
                            cell2.setCellValue(sessionBean.getLoc().getString("purchaseinvoice"));
                        } else {
                            cell2.setCellValue(sessionBean.getLoc().getString("salesinvoice"));
                        }
                    }

                }

                if (toogleList.get(10)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(StaticMethods.round(rs.getBigDecimal("accmprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(11)) {
                    SXSSFCell cell4 = row.createCell((short) b++);
                    if (!rs.getBoolean("accmis_direction")) {
                        cell4.setCellValue(StaticMethods.round((rs.getBigDecimal("accmprice").multiply(rs.getBigDecimal("accmexchangerate"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        cell4.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                }
                if (toogleList.get(12)) {
                    SXSSFCell cell4 = row.createCell((short) b++);
                    if (rs.getBoolean("accmis_direction")) {
                        cell4.setCellValue(StaticMethods.round((rs.getBigDecimal("accmprice").multiply(rs.getBigDecimal("accmexchangerate"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        cell4.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                }
                if (toogleList.get(13)) {
                    SXSSFCell cell5 = row.createCell((short) b++);
                    cell5.setCellValue(StaticMethods.round(rs.getBigDecimal("accbalance").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
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
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
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
                SXSSFCell cell3 = row.createCell((short) c++);
                cell3.setCellValue("");
            }
            if (toogleList.get(11)) {
                SXSSFCell cell4 = row.createCell((short) c++);
                cell4.setCellValue("");
            }
            if (toogleList.get(12)) {
                SXSSFCell cell2 = row.createCell((short) c++);
                cell2.setCellValue("");
            }
            if (toogleList.get(13)) {
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
            cellbalance.setCellValue(sessionBean.getLoc().getString("totalbalance") + " : " + (totalBalance.compareTo(BigDecimal.valueOf(0)) == -1 ? StaticMethods.round(totalBalance.multiply(BigDecimal.valueOf(-1)), sessionBean.getUser().getLastBranch().getCurrencyrounding()) : StaticMethods.round(totalBalance, sessionBean.getUser().getLastBranch().getCurrencyrounding())) + " " + sessionBean.currencySignOrCode(currency.getId(), 0) + " (" + (manageBalanceSign(totalBalance) ? sessionBean.getLoc().getString("payable") : sessionBean.getLoc().getString("receivabled")) + ")");
            cellbalance.setCellStyle(stylefooter);
            try {

                if (pageId == 11 || pageId == 72) {
                    StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("accountmovements"));
                } else {
                    StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("employeemovements"));
                }

            } catch (IOException ex) {
                Logger.getLogger(AccountMovementService.class
                          .getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(AccountMovementDao.class
                          .getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String createWhere, List<Boolean> toogleList, BigDecimal transferringBalance, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal totalBalance, int opType, Date beginDate, Date endDate, Account account, boolean isExtract, int pageId, String sortField, String sortOrder, Date termDate, int termDateUpType, List<Branch> listOfBranch, int financingTypeId) {

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
            connection = accountMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(accountMovementDao.exportData(createWhere, account, opType, beginDate, endDate, sortField, sortOrder, termDate, termDateUpType, branchList, financingTypeId));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

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
            if (pageId == 11 || pageId == 72) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("account")).append(" : ").append(account.getName()).append(" </div> ");
            } else {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("employee")).append(" : ").append(account.getName()).append(" </div> ");
            }
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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("transferringbalance")).append(" : ").append(sessionBean.getNumberFormat().format(transferringBalance)).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</div>");

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
                      + "    </style> <table> <tr>");

            if (toogleList.get(0)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("branch")).append("</th>");
            }
            if (toogleList.get(1)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("documentdate")).append("</th>");
            }
            if (toogleList.get(2)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("documentnumber")).append("</th>");
            }
//            if (toogleList.get(2)) {
//                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("createddate")).append("</th>");
//            }
//
//            if (toogleList.get(3)) {
//                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("createdperson")).append("</th>");
//            }
//
//            if (toogleList.get(4)) {
//                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("updateddate")).append("</th>");
//            }
//
//            if (toogleList.get(5)) {
//                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("updatedperson")).append("</th>");
//            }

            if (toogleList.get(7)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("termdate")).append("</th>");
            }
            if (toogleList.get(8)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("description")).append("</th>");
            }
            if (toogleList.get(9)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("processtype")).append("</th>");
            }

            if (toogleList.get(10)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("foreignexchangetransactionamount")).append("</th>");
            }
            if (toogleList.get(11)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("debt")).append("</th>");
            }
            if (toogleList.get(12)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("receivable")).append("</th>");
            }
            if (toogleList.get(13)) {
                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("balance")).append("</th>");
            }

            sb.append(" </tr>  ");

            while (rs.next()) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(1)) {
                    if (rs.getInt("fdocid") > 0) {
                        sb.append("<td>").append(rs.getTimestamp("fdocdocumentdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocdocumentdate"))).append("</td>");
                    } else if (rs.getInt("invid") > 0) {
                        sb.append("<td>").append(rs.getTimestamp("invinvoicedate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate"))).append("</td>");
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        sb.append("<td>").append(rs.getTimestamp("rcpprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("rcpprocessdate"))).append("</td>");
                    } else {
                        sb.append("<td>").append(rs.getTimestamp("accmmovementdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("accmmovementdate"))).append("</td>");
                    }

                }
                if (toogleList.get(2)) {
                    if (rs.getInt("fdocid") == 0 && rs.getInt("accmchequebill_id") == 0 && rs.getInt("accmreceipt_id") == 0 && rs.getInt("invid") == 0) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("transferbalance")).append("</td>");
                    } else if (rs.getInt("fdocid") > 0) {
                        sb.append("<td>").append(rs.getString("fdocdocumentnumber") == null ? "" : rs.getString("fdocdocumentnumber")).append("</td>");
                    } else if (rs.getInt("accmchequebill_id") > 0) {
                        sb.append("<td>").append(rs.getString("cqbportfolionumber") == null ? "" : rs.getString("cqbportfolionumber")).append("</td>");
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        sb.append("<td>").append(rs.getString("rcpreceiptno") == null ? "" : rs.getString("rcpreceiptno")).append("</td>");
                    } else {
                        sb.append("<td>").append(rs.getString("invdocumentnumber") == null ? "" : rs.getString("invdocumentnumber")).append("</td>");
                    }
                }
//                if (toogleList.get(2)) {
//                    sb.append("<td>").append(rs.getTimestamp("fdocc_time") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocc_time"))).append("</td>");
//                }
//                if (toogleList.get(3)) {
//                    sb.append("<td>").append(rs.getString("usrname") == null ? "" : rs.getString("usrname")).append(" ").append(rs.getString("usrsurname") == null ? "" : rs.getString("usrsurname")).append("</td>");
//                }
//                if (toogleList.get(4)) {
//                    sb.append("<td>").append(rs.getTimestamp("fdocu_time") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fdocu_time"))).append("</td>");
//                }
//                if (toogleList.get(5)) {
//                    sb.append("<td>").append(rs.getString("usr1name") == null ? "" : rs.getString("usr1name")).append(" ").append(rs.getString("usr1surname") == null ? "" : rs.getString("usr1surname")).append("</td>");
//                }

                if (toogleList.get(7)) {
                    if (rs.getInt("fdocid") > 0) {
                        sb.append("<td>").append("").append("</td>");
                    } else if (rs.getInt("invid") > 0) {
                        sb.append("<td>").append(rs.getTimestamp("invduedate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invduedate"))).append("</td>");
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        sb.append("<td>").append("").append("</td>");
                    } else {
                        sb.append("<td>").append("").append("</td>");
                    }

                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("fdocdescription") == null ? "" : rs.getString("fdocdescription")).append("</td>");
                }
                if (toogleList.get(9)) {
                    if (rs.getInt("fdocid") == 0 && rs.getInt("accmchequebill_id") == 0 && rs.getInt("accmreceipt_id") == 0 && rs.getInt("invid") == 0) {
                        sb.append("<td>").append("").append("</td>");
                    } else if (rs.getInt("fdocid") > 0) {
                        sb.append("<td>").append(rs.getString("typdname") == null ? "" : rs.getString("typdname")).append("</td>");
                    } else if (rs.getInt("accmchequebill_id") > 0) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("chequebill")).append("</td>");
                    } else if (rs.getInt("accmreceipt_id") > 0) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("receipt")).append("</td>");
                    } else if (rs.getInt("invid") > 0) {
                        if (rs.getBoolean("invis_purchase")) {
                            sb.append("<td>").append(sessionBean.getLoc().getString("purchaseinvoice")).append("</td>");
                        } else {
                            sb.append("<td>").append(sessionBean.getLoc().getString("salesinvoice")).append("</td>");
                        }
                    }

                }

                if (toogleList.get(10)) {
                    Currency currencyMovement = new Currency(rs.getInt("accmcurrency_id"));
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("accmprice"))).append(sessionBean.currencySignOrCode(currencyMovement.getId(), 0)).append("</td>");
                }
                if (toogleList.get(11)) {
                    if (!rs.getBoolean("accmis_direction")) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("accmprice").multiply(rs.getBigDecimal("accmexchangerate")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td>").append("-").append("</td>");
                    }
                }
                if (toogleList.get(12)) {
                    if (rs.getBoolean("accmis_direction")) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("accmprice").multiply(rs.getBigDecimal("accmexchangerate")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td>").append("-").append("</td>");
                    }
                }
                if (toogleList.get(13)) {
                    int comp = rs.getBigDecimal("accbalance").compareTo(BigDecimal.valueOf(0));
                    if (comp == 1) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("accbalance"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("accbalance").multiply(BigDecimal.valueOf(-1))))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
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
////            if (toogleList.get(2)) {
////                sb.append("<td>").append("").append("</td>");
////            }
////            if (toogleList.get(3)) {
////                sb.append("<td>").append("").append("</td>");
////            }
////            if (toogleList.get(4)) {
////                sb.append("<td>").append("").append("</td>");
////            }
////            if (toogleList.get(5)) {
////                sb.append("<td>").append("").append("</td>");
////            }
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
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(12)) {
                sb.append("<td>").append("").append("</td>");
            }
            if (toogleList.get(13)) {
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
            sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalbalance")).append(" : ").append((totalBalance.compareTo(BigDecimal.valueOf(0)) == -1 ? sessionBean.getNumberFormat().format(totalBalance.multiply(BigDecimal.valueOf(-1))) : sessionBean.getNumberFormat().format(totalBalance))).append(" ").append(sessionBean.currencySignOrCode(currency.getId(), 0)).append(" (").append(manageBalanceSign(totalBalance) ? sessionBean.getLoc().getString("payable") : sessionBean.getLoc().getString("receivabled"))
                      .append(")").append("</td>");
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
                Logger.getLogger(AccountMovementDao.class
                          .getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    public boolean manageBalanceSign(BigDecimal balance) {
        if (balance != null) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int updatePrice(AccountMovement accountMovement) {
        return accountMovementDao.updatePrice(accountMovement);
    }

}
