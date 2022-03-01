/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.trialbalancereport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStyleExcel;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.trialbalancereport.dao.ITrialBalanceDao;
import com.mepsan.marwiz.general.report.trialbalancereport.dao.TrialBalance;
import com.mepsan.marwiz.system.branch.dao.IBranchDao;
import com.mepsan.marwiz.system.branch.dao.IBranchSettingDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.json.JSONArray;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author samet.dag
 */
public class TrialBalanceService implements ITrialBalanceService {

    @Autowired
    public ITrialBalanceDao trialBalanceDao;

    @Autowired
    public IBranchDao branchDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchDao(IBranchDao branchDao) {
        this.branchDao = branchDao;
    }

    public void setTrialBalanceDao(ITrialBalanceDao trialBalanceDao) {
        this.trialBalanceDao = trialBalanceDao;
    }

    @Override
    public HashMap<Integer, TrialBalance> findAll(Date date, Date firstPeriod, List<Boolean> chkBoxList, int typeStock, String whereBranch) {
        List<TrialBalance> list = trialBalanceDao.findAll(date, firstPeriod, chkBoxList, typeStock, whereBranch);
        HashMap<Integer, TrialBalance> hashList = new HashMap<>();
        for (TrialBalance trialBalance : list) {
            switch (trialBalance.getAccountName()) {
                case "0":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("totalofincomeinbank"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("totalofbankexpense"));
                    break;
                case "1":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("totalofincomeinsafe"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("totalofsafeexpense"));
                    break;
                case "2"://banka gelir
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("totalofreceivableofcurrent"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("totalofdebtofcurrent"));
                    break;
                case "3":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("sumofreceivedchequeandbill"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("sumofgivenchequeandbill"));
                    break;
                case "4":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("totalofreceivableofemployee"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("totalofdebtofemployee"));
                    break;
                case "5":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("incomebalance"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("expensebalance"));
                    break;
                case "6":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("totalofincomepostpaid"));
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("totalofexpensepostpaid"));
                    break;
                case "7":
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("currentamountofstock"));
                    break;
                case "8"://banka slip
                    trialBalance.setIncomeText(sessionBean.getLoc().getString("waitingposreceivable"));
                    break;
                case "9"://banka gider
                    trialBalance.setExpenseText(sessionBean.getLoc().getString("totalofbankexpense"));
                    break;
                default:
                    break;
            }

            hashList.put(Integer.parseInt(trialBalance.accountName), trialBalance);
        }
        return hashList;
        // return trialBalanceDao.findAll(date, firstPeriod, chkBoxList, typeStock);
    }

    @Override
    public List<TrialBalance> findDetail(Date date, Date firstPeriod, List<Boolean> chkBoxList, int typeStock, String whereBranch) {
        return trialBalanceDao.findDetail(date, firstPeriod, chkBoxList, typeStock, whereBranch);
    }

    /**
     * Arayüzde tree table için root oluşturur
     *
     * @param detailTrialList
     * @return
     */
    @Override
    public TreeNode createDetailTree(List<TrialBalance> detailTrialList) {
        TreeNode root = new DefaultTreeNode(new TrialBalance("", "", BigDecimal.ZERO, BigDecimal.ZERO), null);

        TreeNode nodeIncome = null,
                nodeExpense = null,
                nodeBank = null,
                nodeSafe = null,
                nodeCurrent = null,
                nodeChequeBill = null,
                nodeEmployee = null,
                nodeStock = null,
                nodePostPaid = null,
                nodeIncomeExpense = null,
                nodeWaitingSlips = null;

        for (int i = 0; i < detailTrialList.size(); i++) {

            if (detailTrialList.get(i).getAccountName().equals("0")) {
                if (nodeBank == null) {
                    nodeBank = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("bank"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                TreeNode newBank = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodeBank);
                ((TrialBalance) nodeBank.getData()).setIncome(((TrialBalance) nodeBank.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodeBank.getData()).setExpense(((TrialBalance) nodeBank.getData()).getExpense().add(detailTrialList.get(i).getExpense()));
            }

            if (detailTrialList.get(i).getAccountName().equals("8")) {
                if (nodeWaitingSlips == null) {
                    nodeWaitingSlips = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("waitingposreceivable"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                TreeNode newWaitingSlips = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodeWaitingSlips);
                ((TrialBalance) nodeWaitingSlips.getData()).setIncome(((TrialBalance) nodeWaitingSlips.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodeWaitingSlips.getData()).setExpense(((TrialBalance) nodeWaitingSlips.getData()).getExpense().add(detailTrialList.get(i).getExpense()));
            }

            if (detailTrialList.get(i).getAccountName().equals("1")) {
                if (nodeSafe == null) {
                    nodeSafe = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("safe"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                TreeNode newSafe = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodeSafe);
                ((TrialBalance) nodeSafe.getData()).setIncome(((TrialBalance) nodeSafe.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodeSafe.getData()).setExpense(((TrialBalance) nodeSafe.getData()).getExpense().add(detailTrialList.get(i).getExpense()));
            }
            if (detailTrialList.get(i).getAccountName().equals("2")) {
                if (nodeCurrent == null) {
                    nodeCurrent = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("current"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                TreeNode newCurrent = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodeCurrent);
                ((TrialBalance) nodeCurrent.getData()).setIncome(((TrialBalance) nodeCurrent.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodeCurrent.getData()).setExpense(((TrialBalance) nodeCurrent.getData()).getExpense().add(detailTrialList.get(i).getExpense()));
            }
            if (detailTrialList.get(i).getAccountName().equals("3")) {
                if (nodeChequeBill == null) {
                    nodeChequeBill = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("chequeandbill"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                TreeNode newChequeAndBill = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodeChequeBill);
                ((TrialBalance) nodeChequeBill.getData()).setIncome(((TrialBalance) nodeChequeBill.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodeChequeBill.getData()).setExpense(((TrialBalance) nodeChequeBill.getData()).getExpense().add(detailTrialList.get(i).getExpense()));
            }
            if (detailTrialList.get(i).getAccountName().equals("4")) {

                if (nodeEmployee == null) {
                    nodeEmployee = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("employee"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }

                TreeNode newEmployee = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodeEmployee);
                ((TrialBalance) nodeEmployee.getData()).setIncome(((TrialBalance) nodeEmployee.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodeEmployee.getData()).setExpense(((TrialBalance) nodeEmployee.getData()).getExpense().add(detailTrialList.get(i).getExpense()));

            }
            if (detailTrialList.get(i).getAccountName().equals("5")) {

                if (nodeStock == null) {
                    nodeStock = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("stock"), "5", "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                ((TrialBalance) nodeStock.getData()).setIncome(((TrialBalance) nodeStock.getData()).getIncome().add(detailTrialList.get(i).getIncome()));

                JSONArray obj = new JSONArray(detailTrialList.get(i).getName());

                String[] products = new String[3];

                products[0] = obj.getJSONObject(0).getString("warehouse");

                products[1] = obj.getJSONObject(1).getString("brand");

                products[2] = obj.getJSONObject(2).getString("stock");

                TreeNode newBrand = null;

                boolean isThere = false;//depo ismi eklendi mi kontrol
                TreeNode insertBrand = null;
                for (TreeNode warehouse : nodeStock.getChildren()) {//depoları dönüyoruz

                    TrialBalance trialBalance = (TrialBalance) warehouse.getData();

                    if (trialBalance.getName().equalsIgnoreCase(products[0])) {//aynı isimde depo varsa yeni depo oluşturma.
                        isThere = true;
                        insertBrand = warehouse;//aynı isimdeki depoyu al.
                    }

                }

                TreeNode nodeBrand = null;
                TreeNode nodeProduct = null;

                if (!isThere) {//aynı isimde depo yoksa yeni oluşturuluyorsa

                    TreeNode newWarehouse = new DefaultTreeNode(new TrialBalance(products[0], "5", "", new BigDecimal("-2121.2121"), new BigDecimal("-2121.2121")), nodeStock);

                    newBrand = new DefaultTreeNode(new TrialBalance(products[1], "5", "", new BigDecimal("-2121.2121"), new BigDecimal("-2121.2121")), newWarehouse);

                    nodeProduct = new DefaultTreeNode(new TrialBalance(products[2], "5", detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), newBrand);

                } else {//aynı isimde depo varsa 
                    isThere = false;
                    for (TreeNode brand : insertBrand.getChildren()) {//depodaki markaları dönüyoruz(Mar.Dep->Tadım,Ülker..)

                        TrialBalance trialBalance = (TrialBalance) brand.getData();

                        if (trialBalance.getName().equalsIgnoreCase(products[1])) {//markaları dönüyoruz var mı kontrol varsa ilgili markaya ekliyoruz.
                            isThere = true;
                            nodeProduct = new DefaultTreeNode(new TrialBalance(products[2], "5", detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), brand);
                        }

                    }

                    if (!isThere) {//marka yoksa ekle
                        newBrand = new DefaultTreeNode(new TrialBalance(products[1], "5", "", new BigDecimal("-2121.2121"), new BigDecimal("-2121.2121")), insertBrand);
                        nodeProduct = new DefaultTreeNode(new TrialBalance(products[2], "5", detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), newBrand);
                    }

                }

            }
            if (detailTrialList.get(i).getAccountName().equals("6")) {

                if (nodePostPaid == null) {
                    nodePostPaid = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("postpaid"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }

                TreeNode newPostPaid = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense()), nodePostPaid);
                ((TrialBalance) nodePostPaid.getData()).setIncome(((TrialBalance) nodePostPaid.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                ((TrialBalance) nodePostPaid.getData()).setExpense(((TrialBalance) nodePostPaid.getData()).getExpense().add(detailTrialList.get(i).getExpense()));

            }

            if (detailTrialList.get(i).getAccountName().equals("7")) {

                if (nodeIncomeExpense == null) {
                    nodeIncomeExpense = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("income") + "/" + sessionBean.getLoc().getString("expense"), "", BigDecimal.ZERO, BigDecimal.ZERO), root);
                }
                if (nodeIncome == null) {
                    nodeIncome = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("income"), "", BigDecimal.ZERO, BigDecimal.ZERO), nodeIncomeExpense);
                }
                if (nodeExpense == null) {
                    nodeExpense = new DefaultTreeNode(new TrialBalance(sessionBean.getLoc().getString("expense"), "", BigDecimal.ZERO, BigDecimal.ZERO), nodeIncomeExpense);
                }

                if (detailTrialList.get(i).getIncome().compareTo(new BigDecimal("-1")) == 0) {

                    TreeNode newIncomeExpense = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), BigDecimal.ZERO, detailTrialList.get(i).getExpense()), nodeExpense);
                    ((TrialBalance) nodeExpense.getData()).setExpense(((TrialBalance) nodeExpense.getData()).getExpense().add(detailTrialList.get(i).getExpense()));
                    ((TrialBalance) nodeIncomeExpense.getData()).setExpense(((TrialBalance) nodeIncomeExpense.getData()).getExpense().add(detailTrialList.get(i).getExpense()));

                } else if (detailTrialList.get(i).getExpense().compareTo(new BigDecimal("-2")) == 0) {

                    TreeNode newIncomeExpense = new DefaultTreeNode(new TrialBalance(detailTrialList.get(i).getName(), detailTrialList.get(i).getBranchName(), detailTrialList.get(i).getIncome(), BigDecimal.ZERO), nodeIncome);
                    ((TrialBalance) nodeIncome.getData()).setIncome(((TrialBalance) nodeIncome.getData()).getIncome().add(detailTrialList.get(i).getIncome()));
                    ((TrialBalance) nodeIncomeExpense.getData()).setIncome(((TrialBalance) nodeIncomeExpense.getData()).getIncome().add(detailTrialList.get(i).getIncome()));

                }

            }

        }
        return root;
    }

    @Override
    public void exportPdf(HashMap<Integer, TrialBalance> listOfTrialBalance, BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal totalBalance, Date date, Date firstPeriod, List<TrialBalance> detailTrialList, List<Boolean> chkBoxList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            PdfDocument pdfDocument = StaticMethods.preparePdf(Arrays.asList(true, true), 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("generaltrialbalancereport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getHeader().setPhrase(new Phrase(dateFormat.format(firstPeriod) + "  -  " + dateFormat.format(date), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getDocument().add(new Paragraph("\n"));

            for (Map.Entry<Integer, TrialBalance> entry : listOfTrialBalance.entrySet()) {
                pdfDocument.getDataCell().setPhrase(new Phrase(entry.getValue().getIncomeText()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getIncome())
                        + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofincome")));
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalIncome)
                    + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getDocument().add(new Paragraph("\n"));

            for (Map.Entry<Integer, TrialBalance> entry : listOfTrialBalance.entrySet()) {
                if (!entry.getValue().getAccountName().equals("7") && !entry.getValue().getAccountName().equals("8")) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(entry.getValue().getExpenseText()));
                    pdfDocument.getDataCell().setBackgroundColor(Color.WHITE);
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getExpense())
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocument.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
            }

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofexpense")));
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalOutcome)
                    + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getDocument().add(new Paragraph("\n\n\n"));

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofincome")));
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalIncome)
                    + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofexpense")));
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalOutcome)
                    + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("overallnetvalue")));
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalBalance)
                    + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getDocument().add(new Paragraph("\n\n\n\n\n\n\n\n\n\n"));

            /*
            Kasa,Banka,Cari,Çek Senet Detay Tablosu Hazırlanıyoe..
             */
            PdfDocument pdfDocumentDetail = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true), 0);
            PdfDocument pdfDocumentDetailStock = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true), 0);

            boolean isSetBank = false;
            boolean isSetSafe = false;
            boolean isSetEmployee = false;
            boolean isSetCurrent = false;
            boolean isSetChequeBill = false;
            boolean isSetPostPaid = false;
            boolean isSetIncomeExpense = false;
            boolean isSetStock = false;
            boolean isSetWaitingSlips = false;

            List<String> listWarehouse = new ArrayList<>();

            for (int i = 0; i < detailTrialList.size(); i++) {

                if (!detailTrialList.get(i).getAccountName().equals("5")) {

                    if (!isSetBank && detailTrialList.get(i).getAccountName().equals("0")) {

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("bank") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetBank = true;
                    }

                    if (!isSetWaitingSlips && detailTrialList.get(i).getAccountName().equals("8")) {

                        pdfDocument.getDocument().add(new Paragraph("\n"));

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("waitingposreceivable"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetWaitingSlips = true;
                    }

                    if (!isSetSafe && detailTrialList.get(i).getAccountName().equals("1")) {
                        pdfDocumentDetail.getDocument().add(new Paragraph("\n"));
                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("safe") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetSafe = true;
                    }

                    if (!isSetEmployee && detailTrialList.get(i).getAccountName().equals("4")) {
                        pdfDocumentDetail.getDocument().add(new Paragraph("\n"));
                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("employee") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetEmployee = true;
                    }

                    if (!isSetCurrent && detailTrialList.get(i).getAccountName().equals("2")) {
                        pdfDocumentDetail.getDocument().add(new Paragraph("\n"));
                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("current") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetCurrent = true;
                    }

                    if (!isSetChequeBill && detailTrialList.get(i).getAccountName().equals("3")) {
                        pdfDocumentDetail.getDocument().add(new Paragraph("\n"));
                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("cheque") + " / " + sessionBean.getLoc().getString("bill") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetChequeBill = true;
                    }

                    if (!isSetPostPaid && detailTrialList.get(i).getAccountName().equals("6")) {
                        pdfDocumentDetail.getDocument().add(new Paragraph("\n"));
                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("postpaid") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetPostPaid = true;
                    }

                    if (!isSetIncomeExpense && detailTrialList.get(i).getAccountName().equals("7")) {
                        pdfDocumentDetail.getDocument().add(new Paragraph("\n"));
                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income") + "/" + sessionBean.getLoc().getString("expense") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expense"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetail, pdfDocumentDetail.getTableHeader());
                        pdfDocumentDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("expensebalance"), pdfDocumentDetail.getFontColumnTitle()));
                        pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getTableHeader());
                        isSetIncomeExpense = true;
                    }

                    pdfDocumentDetail.getDataCell().setPhrase(new Phrase(detailTrialList.get(i).getName(), pdfDocumentDetail.getFont()));
                    pdfDocumentDetail.getDataCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getDataCell());

                    if (detailTrialList.get(i).getAccountName().equals("7")) {
                        if (detailTrialList.get(i).getIncome().compareTo(new BigDecimal("-1")) == 0) {
                            detailTrialList.get(i).setIncome(BigDecimal.ZERO);
                        }
                        if (detailTrialList.get(i).getExpense().compareTo(new BigDecimal("-2")) == 0) {
                            detailTrialList.get(i).setExpense(BigDecimal.ZERO);
                        }
                    }

                    pdfDocumentDetail.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(detailTrialList.get(i).getIncome())
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocumentDetail.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getRightCell());

                    pdfDocumentDetail.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(detailTrialList.get(i).getExpense())
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocumentDetail.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getRightCell());

                    pdfDocumentDetail.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense(), true))
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocumentDetail.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getRightCell());

                    pdfDocumentDetail.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense(), false))
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocumentDetail.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetail.getPdfTable().addCell(pdfDocumentDetail.getRightCell());

                    //pdfDocumentDetail.getDocument().add(pdfDocumentDetail.getPdfTable());
                } else {//Ürünler sayfası ise

                    if (!isSetStock) {
                        createCellStylePdf("headerBlack", pdfDocumentDetailStock, pdfDocumentDetailStock.getTableHeader());
                        pdfDocumentDetailStock.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetailStock.getFontColumnTitle()));
                        pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetailStock, pdfDocumentDetailStock.getTableHeader());
                        pdfDocumentDetailStock.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("brand") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetailStock.getFontColumnTitle()));
                        pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetailStock, pdfDocumentDetailStock.getTableHeader());
                        pdfDocumentDetailStock.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("itsname"), pdfDocumentDetailStock.getFontColumnTitle()));
                        pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetailStock, pdfDocumentDetailStock.getTableHeader());
                        pdfDocumentDetailStock.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income"), pdfDocumentDetailStock.getFontColumnTitle()));
                        pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getTableHeader());

                        createCellStylePdf("headerBlack", pdfDocumentDetailStock, pdfDocumentDetailStock.getTableHeader());
                        pdfDocumentDetailStock.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomebalance"), pdfDocumentDetailStock.getFontColumnTitle()));
                        pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getTableHeader());
                        isSetStock = true;
                    }

                    JSONArray obj = new JSONArray(detailTrialList.get(i).getName());

                    String[] products = new String[3];

                    products[0] = obj.getJSONObject(0).getString("warehouse");

                    products[1] = obj.getJSONObject(1).getString("brand");

                    products[2] = obj.getJSONObject(2).getString("stock");

                    pdfDocumentDetailStock.getDataCell().setPhrase(new Phrase(products[0], pdfDocumentDetailStock.getFont()));
                    pdfDocumentDetailStock.getDataCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getDataCell());

                    pdfDocumentDetailStock.getDataCell().setPhrase(new Phrase(products[1], pdfDocumentDetail.getFont()));
                    pdfDocumentDetailStock.getDataCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getDataCell());

                    pdfDocumentDetailStock.getDataCell().setPhrase(new Phrase(products[2], pdfDocumentDetail.getFont()));
                    pdfDocumentDetailStock.getDataCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getDataCell());

                    pdfDocumentDetailStock.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(detailTrialList.get(i).getIncome())
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocumentDetailStock.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getRightCell());

                    pdfDocumentDetailStock.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(detailTrialList.get(i).getIncome())
                            + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)));
                    pdfDocumentDetailStock.getRightCell().setBackgroundColor(Color.WHITE);
                    pdfDocumentDetailStock.getPdfTable().addCell(pdfDocumentDetailStock.getRightCell());
                }

            }

            pdfDocument.getDocument().add(pdfDocumentDetail.getPdfTable());
            pdfDocument.getDocument().add(pdfDocumentDetailStock.getPdfTable());
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("generaltrialbalancereport"));
        } catch (DocumentException ex) {
            Logger.getLogger(TrialBalanceService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void exportExcel(HashMap<Integer, TrialBalance> listOfTrialBalance, BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal totalBalance, Date date, Date firstPeriod, List<TrialBalance> detailTrialList, List<Boolean> chkBoxList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        CellStyle styleRight = excelDocument.getWorkbook().createCellStyle();
        styleRight.setAlignment(HorizontalAlignment.RIGHT);

        int jRow = 0;

        SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell cellheader = header.createCell((short) 0);
        cellheader.setCellValue(sessionBean.getLoc().getString("generaltrialbalancereport"));
        cellheader.setCellStyle(excelDocument.getStyleHeader());

        SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

        SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
        startdate.createCell((short) 0).setCellValue(dateFormat.format(firstPeriod) + "  -  " + dateFormat.format(date));

        SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

        SXSSFRow rowSafe = excelDocument.getSheet().createRow(jRow++);
        rowSafe.createCell(0).setCellValue(sessionBean.getLoc().getString("income"));

        int b = 0;

        for (Map.Entry<Integer, TrialBalance> entry : listOfTrialBalance.entrySet()) {
            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            row.createCell(b).setCellValue(entry.getValue().getIncomeText());
            SXSSFCell cell = row.createCell(++b);
            cell.setCellValue(StaticMethods.round(entry.getValue().getIncome().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cell.setCellStyle(styleRight);
            cell.setCellType(CellType.NUMERIC);
            b = 0;
        }

        CellStyle cellStyleLeft = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        SXSSFRow row10 = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell incomeCell = row10.createCell(b);

        incomeCell.setCellValue(sessionBean.getLoc().getString("totalofincome"));

        incomeCell.setCellStyle(cellStyleLeft);

        SXSSFCell cell10 = row10.createCell(++b);

        CellStyle cellStyleRight = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
        cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);

        cell10.setCellValue(StaticMethods.round(totalIncome.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

        cell10.setCellStyle(cellStyleRight);

        cell10.setCellType(CellType.NUMERIC);

        rowEmpty = excelDocument.getSheet().createRow(jRow++);

        rowSafe = excelDocument.getSheet().createRow(jRow++);
        rowSafe.createCell(0).setCellValue(sessionBean.getLoc().getString("expense"));

        b = 0;

        for (Map.Entry<Integer, TrialBalance> entry : listOfTrialBalance.entrySet()) {
            if (!entry.getValue().getAccountName().equals("7") && !entry.getValue().getAccountName().equals("8")) {
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                row.createCell(b).setCellValue(entry.getValue().getExpenseText());
                SXSSFCell cell = row.createCell(++b);
                cell.setCellValue(StaticMethods.round(entry.getValue().getExpense().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(styleRight);
                cell.setCellType(CellType.NUMERIC);
                b = 0;
            }
        }

        SXSSFRow row11 = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell expenseCell = row11.createCell(b);

        expenseCell.setCellValue(sessionBean.getLoc().getString("totalofexpense"));

        expenseCell.setCellStyle(cellStyleLeft);

        SXSSFCell cell11 = row11.createCell(++b);

        cell11.setCellValue(StaticMethods.round(totalOutcome.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

        cell11.setCellStyle(cellStyleRight);

        cell11.setCellType(CellType.NUMERIC);

        rowEmpty = excelDocument.getSheet().createRow(jRow++);

        b = 0;

        SXSSFRow row12 = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell totalCell = row12.createCell(b);

        totalCell.setCellValue(sessionBean.getLoc().getString("overallnetvalue"));

        totalCell.setCellStyle(cellStyleLeft);

        SXSSFCell cell12 = row12.createCell(++b);

        cell12.setCellValue(StaticMethods.round(totalBalance.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

        cell12.setCellStyle(cellStyleRight);

        cell12.setCellType(CellType.NUMERIC);

        CellStyle cellStyle = createCellStyleExcel("headerBlack", excelDocument.getWorkbook());
        SXSSFRow rowDetailHeader = excelDocument.getSheet().createRow(jRow++);

        int a = 0;

        boolean isSetBank = false;
        boolean isSetSafe = false;
        boolean isSetEmployee = false;
        boolean isSetCurrent = false;
        boolean isSetChequeBill = false;
        boolean isSetPostPaid = false;
        boolean isSetIncomeExpense = false;
        boolean isSetStock = false;
        boolean isSetWaitingSlips = false;

        for (int i = 0; i < detailTrialList.size(); i++) {
            b = 0;
            a = 0;

            if (!isSetBank && detailTrialList.get(i).getAccountName().equals("0")) {
                isSetBank = true;
                SXSSFCell cellDetailHeader = rowDetailHeader.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("bank") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = rowDetailHeader.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = rowDetailHeader.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = rowDetailHeader.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = rowDetailHeader.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetWaitingSlips && detailTrialList.get(i).getAccountName().equals("8")) {
                isSetWaitingSlips = true;
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("waitingposreceivable"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetSafe && detailTrialList.get(i).getAccountName().equals("1")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetSafe = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("safe") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetEmployee && detailTrialList.get(i).getAccountName().equals("4")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetEmployee = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("employee") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetCurrent && detailTrialList.get(i).getAccountName().equals("2")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetCurrent = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("current") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }
            if (!isSetChequeBill && detailTrialList.get(i).getAccountName().equals("3")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetChequeBill = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("cheque") + "/" + sessionBean.getLoc().getString("cheque") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetPostPaid && detailTrialList.get(i).getAccountName().equals("6")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetPostPaid = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("postpaid") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetIncomeExpense && detailTrialList.get(i).getAccountName().equals("7")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetIncomeExpense = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income") + "/" + sessionBean.getLoc().getString("income") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expense"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("expensebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }

            if (!isSetStock && detailTrialList.get(i).getAccountName().equals("5")) {
                SXSSFRow emptysubdetrow = excelDocument.getSheet().createRow(jRow++);
                isSetStock = true;
                SXSSFCell cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("warehouse") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("brand") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("itsname"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("income"));
                cellDetailHeader.setCellStyle(cellStyle);

                cellDetailHeader = emptysubdetrow.createCell((short) a++);
                cellDetailHeader.setCellValue(sessionBean.getLoc().getString("incomebalance"));
                cellDetailHeader.setCellStyle(cellStyle);
            }
            if (detailTrialList.get(i).getAccountName().equals("5")) {
                JSONArray obj = new JSONArray(detailTrialList.get(i).getName());

                String[] products = new String[3];

                products[0] = obj.getJSONObject(0).getString("warehouse");

                products[1] = obj.getJSONObject(1).getString("brand");

                products[2] = obj.getJSONObject(2).getString("stock");
                SXSSFRow datarow = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellname = datarow.createCell(b++);

                cellname.setCellValue(products[0]);

                SXSSFCell cellbrandname = datarow.createCell(b++);

                cellbrandname.setCellValue(products[1]);

                SXSSFCell cellstockname = datarow.createCell(b++);

                cellstockname.setCellValue(products[2]);

                SXSSFCell cellincome = datarow.createCell(b++);

                cellincome.setCellValue(StaticMethods.round(detailTrialList.get(i).getIncome().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                cellincome.setCellType(CellType.NUMERIC);

                SXSSFCell cellincomebalance = datarow.createCell(b++);

                cellincomebalance.setCellValue(StaticMethods.round(detailTrialList.get(i).getIncome().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                cellincomebalance.setCellType(CellType.NUMERIC);

            } else {
                SXSSFRow datarow = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellname = datarow.createCell(b++);

                cellname.setCellValue(detailTrialList.get(i).getName());

                SXSSFCell cellincome = datarow.createCell(b++);

                if (detailTrialList.get(i).getAccountName().equals("7")) {

                    if (detailTrialList.get(i).getIncome().compareTo(new BigDecimal("-1")) == 0) {
                        detailTrialList.get(i).setIncome(BigDecimal.ZERO);
                    } else if (detailTrialList.get(i).getExpense().compareTo(new BigDecimal("-2")) == 0) {
                        detailTrialList.get(i).setExpense(BigDecimal.ZERO);
                    }

                }

                cellincome.setCellValue(StaticMethods.round(detailTrialList.get(i).getIncome().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                cellincome.setCellType(CellType.NUMERIC);

                SXSSFCell cellexpense = datarow.createCell(b++);

                cellexpense.setCellValue(StaticMethods.round(detailTrialList.get(i).getExpense().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                cellexpense.setCellType(CellType.NUMERIC);

                SXSSFCell cellincomebalance = datarow.createCell(b++);

                cellincomebalance.setCellValue(StaticMethods.round(calcBalance(detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense(), true).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                cellincomebalance.setCellType(CellType.NUMERIC);

                SXSSFCell cellexpensebalance = datarow.createCell(b++);

                cellexpensebalance.setCellValue(StaticMethods.round(calcBalance(detailTrialList.get(i).getIncome(), detailTrialList.get(i).getExpense(), false).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                cellexpensebalance.setCellType(CellType.NUMERIC);
            }

        }

        try {
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("generaltrialbalancereport"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public String exportPrinter(HashMap<Integer, TrialBalance> listOfTrialBalance, BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal totalBalance, Date date, Date firstPeriod, List<TrialBalance> detailTrialList, List<Boolean> chkBoxList) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;text-align:center;\">").append(dateFormat.format(firstPeriod)).append("  -  ").append(dateFormat.format(date)).append(" </div> ");
        sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("income")).append(" </div> ");
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
                + "    </style>  ");

        sb.append("<table>");

        for (Map.Entry<Integer, TrialBalance> entry : listOfTrialBalance.entrySet()) {
            sb.append(" <tr>");
            sb.append(" <td>").append(entry.getValue().getIncomeText()).append("</td>");
            sb.append("<td style=\"text-align:right;\">").append(sessionBean.getNumberFormat().format(entry.getValue().getIncome().doubleValue())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
            sb.append(" </tr>  ");

        }
        sb.append(" <tr>  ");
        sb.append("<td style=\"font-weight: bold;\">").append(sessionBean.getLoc().getString("totalofincome")).append("</td>");
        sb.append("<td style=\"text-align:right;font-weight: bold;\">").append(sessionBean.getNumberFormat().format(totalIncome)).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
        sb.append(" </tr>  ");
        sb.append(" </table> ");
        sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("expense")).append(" </div> ");
        sb.append(" <table> ");

        for (Map.Entry<Integer, TrialBalance> entry : listOfTrialBalance.entrySet()) {
            if (!entry.getValue().getAccountName().equals("7")) {
                sb.append(" <tr>");
                sb.append(" <td>").append(entry.getValue().getIncomeText()).append("</td>");
                sb.append("<td style=\"text-align:right;\">").append(sessionBean.getNumberFormat().format(entry.getValue().getIncome().doubleValue())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                sb.append(" </tr>  ");
            }
        }

        sb.append(" <tr>  ");
        sb.append("<td style=\"font-weight: bold;\">").append(sessionBean.getLoc().getString("totalofexpense")).append("</td>");
        sb.append("<td style=\"text-align:right;font-weight: bold;\">").append(sessionBean.getNumberFormat().format(totalOutcome)).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
        sb.append(" </tr>  ");
        sb.append(" </table> ");
        sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("sum")).append(" </div> ");
        sb.append(" <table> ");
        sb.append(" <tr>  ");
        sb.append("<td style=\"font-weight: bold;\">").append(sessionBean.getLoc().getString("overallnetvalue")).append("</td>");
        sb.append("<td style=\"text-align:right;font-weight: bold;\">").append(sessionBean.getNumberFormat().format(totalBalance)).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
        sb.append(" </tr>  ");
        sb.append(" </table> ");
        sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");
        sb.append(" <table> ");
        sb.append(" <tr>  ");
        boolean isSetBank = false;
        boolean isSetSafe = false;
        boolean isSetEmployee = false;
        boolean isSetCurrent = false;
        boolean isSetChequeBill = false;
        boolean isSetPostPaid = false;
        boolean isSetIncomeExpense = false;
        boolean isSetStock = false;
        boolean isSetWaitingSlips = false;

        for (int x = 0; x < detailTrialList.size(); x++) {

            if (detailTrialList.get(x).getAccountName().equals("0") && chkBoxList.get(2)) {

                if (!isSetBank) {
                    isSetBank = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("bank")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }

            }

            if (detailTrialList.get(x).getAccountName().equals("8") && chkBoxList.get(11)) {

                if (!isSetWaitingSlips) {
                    isSetWaitingSlips = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("waitingposreceivable")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }

            }

            if (detailTrialList.get(x).getAccountName().equals("1") && chkBoxList.get(3)) {

                if (!isSetSafe) {
                    isSetSafe = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("safe")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");
                }

            }

            if (detailTrialList.get(x).getAccountName().equals("2") && chkBoxList.get(4)) {

                if (!isSetCurrent) {
                    isSetCurrent = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("current")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");
                }
            }

            if (detailTrialList.get(x).getAccountName().equals("3") && chkBoxList.get(5)) {

                if (!isSetChequeBill) {
                    isSetChequeBill = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("cheque")).append(" / ").append(sessionBean.getLoc().getString("bill")).append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");

                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }
            }

            if (detailTrialList.get(x).getAccountName().equals("4") && chkBoxList.get(6)) {

                if (!isSetEmployee) {
                    isSetEmployee = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("employee")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }
            }

            if (detailTrialList.get(x).getAccountName().equals("5") && chkBoxList.get(1)) {

                JSONArray obj = new JSONArray(detailTrialList.get(x).getName());

                String[] products = new String[3];

                products[0] = obj.getJSONObject(0).getString("warehouse");

                products[1] = obj.getJSONObject(1).getString("brand");

                products[2] = obj.getJSONObject(2).getString("stock");
                if (!isSetStock) {
                    isSetStock = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("warehouse")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("brand")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("stock")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(products[0]).append("</td>");
                    sb.append("<td>").append(products[1]).append("</td>");
                    sb.append("<td>").append(products[2]).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(products[0]).append("</td>");
                    sb.append("<td>").append(products[1]).append("</td>");
                    sb.append("<td>").append(products[2]).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }

            }

            if (detailTrialList.get(x).getAccountName().equals("6") && chkBoxList.get(9)) {
                if (!isSetPostPaid) {
                    isSetPostPaid = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("postpaid")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }
            }

            if (detailTrialList.get(x).getAccountName().equals("7") && chkBoxList.get(10)) {
                if (!isSetIncomeExpense) {
                    isSetIncomeExpense = true;
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("/").append(sessionBean.getLoc().getString("expense")).append(" ").append(sessionBean.getLoc().getString("itsname")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("income")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expense")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("incomebalance")).append("</th>");
                    sb.append("<th>").append(sessionBean.getLoc().getString("expensebalance")).append("</th>");

                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                } else {
                    sb.append(" <tr> ");
                    sb.append("<td>").append(detailTrialList.get(x).getName()).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getIncome())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(detailTrialList.get(x).getExpense())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), true))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append("<td>").append(sessionBean.getNumberFormat().format(calcBalance(detailTrialList.get(x).getIncome(), detailTrialList.get(x).getExpense(), false))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }
            }

        }
        sb.append(" </tr>  ");

        sb.append(" </table> ");

        return sb.toString();
    }

    @Override                 
    public String whereBranch(List<BranchSetting> listOfBranch) {
        String branchList = "";
        List<Branch> list = new ArrayList<>();
        for (BranchSetting branchSetting : listOfBranch) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        } else {
            list = branchDao.findUserAuthorizeBranch();

            for (Branch branchSetting : list) {
                branchList = branchList + "," + String.valueOf(branchSetting.getId());
                if (branchSetting.getId() == 0) {
                    branchList = "";
                    break;
                }
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
        }

        return branchList;
    }

    public BigDecimal calcBalance(BigDecimal income, BigDecimal expense, boolean b) {

       

        if (income == null || expense == null) {
            return new BigDecimal(BigInteger.ZERO);
        } else if (income.doubleValue() == -2121.2121 || expense.doubleValue() == -2121.2121) {
            return new BigDecimal(BigInteger.ZERO);
        } else if (b) {// gelir bakiyesi hesapla
            if (income.compareTo(expense) == 1) {

                return income.subtract(expense);
            } else {
                return new BigDecimal(BigInteger.ZERO);
            }
        } else// gider bakiyesi hesapla
        {
            if (expense.compareTo(income) == 1) {

                return expense.subtract(income);
            } else {
                return new BigDecimal(BigInteger.ZERO);
            }
        }
    }

}
