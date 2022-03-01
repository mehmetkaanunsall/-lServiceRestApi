/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 09:35:34
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.finance.invoice.dao.IInvoiceDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.InvoiceReport;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSap;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class InvoiceService implements IInvoiceService {

    @Autowired
    private IInvoiceDao invoiceDao;

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IInvoiceItemService invoiceItemService;

    public void setInvoiceItemService(IInvoiceItemService invoiceItemService) {
        this.invoiceItemService = invoiceItemService;
    }

    public void setInvoiceDao(IInvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(InvoiceReport invoice, List<BranchSetting> listOfBranch) {
        String where = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (invoice.getType() == 0) {
            where = where + " AND inv.is_purchase=false ";
        } else if (invoice.getType() == 1) {
            where = where + " AND inv.is_purchase=true ";
        }

        where = where + " AND inv.invoicedate BETWEEN '" + sdf.format(invoice.getBeginDate()) + "' AND '" + sdf.format(invoice.getEndDate()) + "' ";

        if (!invoice.getAccountList().isEmpty() && invoice.getAccountList().get(0).getId() != 0) {
            String ids = "";
            where = where + " AND inv.account_id IN (";
            for (Account acc : invoice.getAccountList()) {
                ids = ids + acc.getId() + ",";
            }

            ids = ids.substring(0, ids.length() - 1);
            where = where + ids + ") ";
        }

        if (!invoice.getStockList().isEmpty() && invoice.getStockList().get(0).getId() != 0) {
            String ids = "";
            where = where + " AND EXISTS(SELECT invi.id FROM finance.invoiceitem invi WHERE invi.deleted=FALSE AND invi.invoice_id = inv.id AND invi.stock_id IN (";
            for (Stock stck : invoice.getStockList()) {
                ids = ids + stck.getId() + ",";
            }

            ids = ids.substring(0, ids.length() - 1);
            where = where + ids + ") )";
        }

        if (invoice.getSalePriceMax() != null && invoice.getSalePriceMin() != null) {

            if (invoice.isIsTaxIncluded()) {
                where = where + " AND inv.totalmoney BETWEEN " + invoice.getSalePriceMin() + " AND " + invoice.getSalePriceMax() + " ";
            } else {
                where = where + " AND inv.totalprice BETWEEN " + invoice.getSalePriceMin() + " AND " + invoice.getSalePriceMax() + " ";

            }
        } else if (invoice.getSalePriceMin() != null) {

            if (invoice.isIsTaxIncluded()) {
                where = where + "AND inv.totalmoney >= " + invoice.getSalePriceMin() + " ";
            } else {
                where = where + "AND inv.totalprice >= " + invoice.getSalePriceMin() + " ";
            }
        } else if (invoice.getSalePriceMax() != null) {

            if (invoice.isIsTaxIncluded()) {
                where = where + "AND inv.totalmoney <= " + invoice.getSalePriceMax() + " ";
            } else {
                where = where + "AND inv.totalprice <= " + invoice.getSalePriceMax() + " ";
            }
        }
        String branchs = "";
        where = where + " AND inv.branch_id IN (";
        if (!invoice.getSelectedBranchList().isEmpty()) {
            for (BranchSetting br : invoice.getSelectedBranchList()) {
                branchs = branchs + br.getBranch().getId() + ",";
            }
        } else {
            for (BranchSetting br : listOfBranch) {
                branchs = branchs + br.getBranch().getId() + ",";
            }
        }
        branchs = branchs.substring(0, branchs.length() - 1);
        where = where + branchs + ") ";

        String types = "";
        where = where + " AND inv.type_id IN (";
        if (!invoice.getListOfInvoiceType().isEmpty()) {
            for (Type ty : invoice.getListOfInvoiceType()) {
                types = types + ty.getId() + ",";
            }
        } else {
            for (Type ty : sessionBean.getTypes(17)) {
                types = types + ty.getId() + ",";
            }
        }
        types = types.substring(0, types.length() - 1);
        where = where + types + ") ";

        return where;
    }

    /**
     *
     *
     * @param where
     * @return
     */
    public String bringWhere(String where) {

        return where;

    }

    @Override
    public List<Invoice> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        where = bringWhere(where);
        return invoiceDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        where = bringWhere(where);
        return invoiceDao.count(where);
    }

    public String jsonArrayWarehouses(List<Warehouse> listOfWarehouse) {
        JsonArray jsonWarehouses = new JsonArray();
        for (Warehouse w : listOfWarehouse) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", w.getId());
            jsonWarehouses.add(jsonObject);
        }
        return jsonWarehouses.toString();
    }

    @Override
    public int create(Invoice obj) {
        obj.setJsonWarehouses(jsonArrayWarehouses(obj.getListOfWarehouse()));

        return invoiceDao.create(obj);
    }

    @Override
    public int update(Invoice obj) {

        obj.setJsonWarehouses(jsonArrayWarehouses(obj.getListOfWarehouse()));

        return invoiceDao.update(obj);
    }

    /**
     * Bu metot gelen irsaliyeden fatura oluşturur, fatura ile irsaliyeyi
     * bağlar. Oluşan faturaya ürün ekler.
     *
     * @param obj
     * @param waybill
     * @param listOfItem
     * @return
     */
    @Override
    public int createInvoiceForWaybill(Invoice obj, Waybill waybill, List<InvoiceItem> listOfItem) {

        int invoiceId = invoiceDao.create(obj);
        int conId = 0;
        int result = 0;

        if (invoiceId > 0) {
            obj.setId(invoiceId);
            conId = invoiceDao.createInvoiceWaybillCon(obj, waybill);
            if (conId > 0) {//bağlantı tablosuna eklendi ise itemları faturaya ekle.
                result = invoiceItemService.createAll(listOfItem, obj);
                return result;
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    /**
     * müşteri hareketlerinden fatura sayfasına gitmeden once faturanın tum
     * bilgilerini getirmek için yazılmıstır
     *
     * @param invoice
     * @return
     */
    @Override
    public Invoice findInvoice(Invoice invoice) {
        Map<String, Object> filt = new HashMap<>();
        List<Invoice> list = invoiceDao.findAll(0, 10, "inv.id", "ASC", filt, " AND inv.id = " + invoice.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new Invoice();
        }
    }

    @Override
    public int sendInvoiceCenter(Invoice invoice) {
        return invoiceDao.sendInvoiceCenter(invoice);
    }

    /**
     * Bu metot, fatura hemen eklendiğinde ödeme tabı için sale id bilgisi alıp
     * getirir. Faturaya ödeme işlemi hemen yapıldığında hata vermemesi için.
     * 10.09.2018
     *
     * @param invoice
     * @return
     */
    @Override
    public int getInvoiceSaleId(Invoice invoice) {
        return invoiceDao.getInvoiceSaleId(invoice);
    }

    /**
     * Faturaya bağlı kayıt yok ise faturayı siler
     *
     * @param invoice
     * @return
     */
    @Override
    public int delete(Invoice invoice) {
        invoice.setJsonWarehouses(jsonArrayWarehouses(invoice.getListOfWarehouse()));

        return invoiceDao.delete(invoice);
    }

    @Override
    public int createInvoiceForAgreement(Invoice invoice, List<InvoiceItem> listOfItem, CustomerAgreements customerAgreements, CreditReport creditReport) {
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setJsonItems(invoiceItemService.jsonArrayInvoiceItems(listOfItem));

        return invoiceDao.createInvoiceForAgreement(invoice, invoiceItem, customerAgreements, creditReport);
    }

    @Override
    public Invoice findDuplicateInvoice(Invoice invoice) {
        return invoiceDao.findDuplicateInvoice(invoice);
    }

    @Override
    public void createExcelFile(Invoice invoice, List<InvoiceItem> listOfInvoiceItems, String totalAmount, BigDecimal subTotal, BigDecimal totalDiscount, String taxRates, List<InvoiceItem> listOfTaxs, BranchSetting branchSetting) {
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        int jRow = 0;
        List<Boolean> tempToggle = new ArrayList<>();
        if (invoice.getType().getId() != 26) {
            if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                tempToggle = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            } else {
                tempToggle = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false);
            }
        } else {
            if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                tempToggle = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);
            } else {
                tempToggle = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false);
            }

        }

        CellStyle stylesub = StaticMethods.createCellStyleExcel("footerBlack", excelDocument.getWorkbook());
        stylesub.setAlignment(HorizontalAlignment.RIGHT);

        SXSSFRow branchnamerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchnameVal = branchnamerow.createCell((short) 0);
        branchnameVal.setCellValue(branchSetting.getBranch().getName());

        SXSSFRow branchaddressrow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchaddress = branchaddressrow.createCell((short) 0);
        branchaddress.setCellValue(sessionBean.getLoc().getString("address") + " : ");
        branchaddress.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchaddressVal = branchaddressrow.createCell((short) 1);
        branchaddressVal.setCellValue(branchSetting.getBranch().getAddress());

        SXSSFCell documentno = branchaddressrow.createCell((short) 4);
        documentno.setCellValue(sessionBean.getLoc().getString("documentno") + " : ");
        documentno.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell documentnoVal = branchaddressrow.createCell((short) 5);
        String temp = "";
        temp += (invoice.getDocumentSerial() != null ? invoice.getDocumentSerial() : "");
        temp += (invoice.getDocumentNumber() != null ? invoice.getDocumentNumber() : "");
        documentnoVal.setCellValue(temp);

        SXSSFRow branchmailrow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchmail = branchmailrow.createCell((short) 0);
        branchmail.setCellValue(sessionBean.getLoc().getString("mail") + " : ");
        branchmail.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchmailVal = branchmailrow.createCell((short) 1);
        branchmailVal.setCellValue(branchSetting.getBranch().getMail());

        SXSSFCell dispatchdate = branchmailrow.createCell((short) 4);
        dispatchdate.setCellValue(sessionBean.getLoc().getString("dispatchdate") + " : ");
        dispatchdate.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell dispatchdateVal = branchmailrow.createCell((short) 5);
        dispatchdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), invoice.getDispatchDate()));

        SXSSFRow branchtaxofficerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchtaxoffice = branchtaxofficerow.createCell((short) 0);
        branchtaxoffice.setCellValue(sessionBean.getLoc().getString("taxoffice") + " : ");
        branchtaxoffice.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchtaxofficeVal = branchtaxofficerow.createCell((short) 1);
        branchtaxofficeVal.setCellValue(branchSetting.getBranch().getTaxOffice());

        SXSSFCell termdate = branchtaxofficerow.createCell((short) 4);
        termdate.setCellValue(sessionBean.getLoc().getString("termdate") + " : ");
        termdate.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell termdateVal = branchtaxofficerow.createCell((short) 5);
        termdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), invoice.getDueDate()));

        SXSSFRow branchtaxnoerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell branchtaxno = branchtaxnoerow.createCell((short) 0);
        branchtaxno.setCellValue(sessionBean.getLoc().getString("taxno") + " : ");
        branchtaxno.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell branchtaxnoVal = branchtaxnoerow.createCell((short) 1);
        branchtaxnoVal.setCellValue(branchSetting.getBranch().getTaxNo());

        SXSSFRow empty1 = excelDocument.getSheet().createRow(jRow++);

        SXSSFRow customertitlerow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell customertitle = customertitlerow.createCell((short) 0);
        customertitle.setCellValue(sessionBean.getLoc().getString("customertitle") + " : ");
        customertitle.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell customertitleVal = customertitlerow.createCell((short) 1);
        customertitleVal.setCellValue(invoice.getAccount().getName());

        SXSSFRow customertaxrow = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell customertax = customertaxrow.createCell((short) 0);
        customertax.setCellValue(sessionBean.getLoc().getString("taxinformationofficeandnumber") + " : ");
        customertax.setCellStyle(excelDocument.getStyleHeader());

        SXSSFCell customertaxVal = customertaxrow.createCell((short) 1);
        customertaxVal.setCellValue((invoice.getAccount().getTaxOffice() != null ? invoice.getAccount().getTaxOffice() : "") + " - "
                  + (invoice.getAccount().getTaxNo() != null ? invoice.getAccount().getTaxNo() : ""));

        SXSSFRow empty2 = excelDocument.getSheet().createRow(jRow++);

        if (invoice.getType().getId() == 26) {
            StaticMethods.createHeaderExcel("tbvInvoice:frmPriceDifferenceTab:dtbPriceDifferenceTab", tempToggle, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            for (InvoiceItem invoiceItem : listOfInvoiceItems) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                row.createCell((short) b++).setCellValue(invoiceItem.getStock().getBarcode());
                row.createCell((short) b++).setCellValue(invoiceItem.getStock().getCenterProductCode());
                row.createCell((short) b++).setCellValue(invoiceItem.getStock().getName());
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getQuantity() != null ? invoiceItem.getQuantity() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getOldUnitPrice() != null ? invoiceItem.getOldUnitPrice() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getUnitPrice() != null ? invoiceItem.getUnitPrice() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTaxRate() != null ? invoiceItem.getTaxRate() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTotalTax() != null ? invoiceItem.getTotalTax() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getTotalPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTotalMoney().multiply(invoiceItem.getExchangeRate())).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
            }
            if (!listOfInvoiceItems.isEmpty()) {
                SXSSFRow rowsub5 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell totalAmountCell1 = rowsub5.createCell((short) 2);
                totalAmountCell1.setCellValue(sessionBean.getLoc().getString("totalamount") + ":");
                totalAmountCell1.setCellStyle(stylesub);

                SXSSFCell totalAmountCell2 = rowsub5.createCell((short) 3);
                totalAmountCell2.setCellValue(totalAmount);
                totalAmountCell2.setCellStyle(stylesub);

                SXSSFCell subTotalCell;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    subTotalCell = rowsub5.createCell((short) 9);
                } else {
                    subTotalCell = rowsub5.createCell((short) 8);
                }
                subTotalCell.setCellValue(sessionBean.getLoc().getString("subtotal") + ":");
                subTotalCell.setCellStyle(stylesub);

                SXSSFCell subTotalCell2;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    subTotalCell2 = rowsub5.createCell((short) 10);
                } else {
                    subTotalCell2 = rowsub5.createCell((short) 9);
                }
                subTotalCell2.setCellValue(StaticMethods.round(subTotal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                subTotalCell2.setCellStyle(stylesub);

                BigDecimal totalTax = BigDecimal.ZERO;
                for (InvoiceItem item : listOfInvoiceItems) {
                    totalTax = totalTax.add(item.getTotalTax());
                }

//                //Toplam Row3
//                SXSSFRow rowsub2 = excelDocument.getSheet().createRow(jRow++);
//
//                SXSSFCell totaltaxprice2;
//                SXSSFCell totaltaxprice3;
//                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
//                    SXSSFCell taxwithexchange = rowsub2.createCell((short) 15);
//                    taxwithexchange.setCellValue(sessionBean.getLoc().getString("taxamountwithexchange") + ":" + taxRates);
//                    taxwithexchange.setCellStyle(stylesub);
//
//                    totaltaxprice2 = rowsub2.createCell((short) 16);
//                    totaltaxprice3 = rowsub2.createCell((short) 17);
//                } else {
//                    totaltaxprice2 = rowsub2.createCell((short) 15);
//                    totaltaxprice3 = rowsub2.createCell((short) 16);
//                }
//
//                totaltaxprice2.setCellValue(sessionBean.getLoc().getString("totaltaxprice") + ":");
//                totaltaxprice2.setCellStyle(stylesub);
//
//                totaltaxprice3.setCellValue(StaticMethods.round(totalTax.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
//                totaltaxprice3.setCellStyle(stylesub);
                SXSSFRow rowsub3 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell totaltaxprice;
                SXSSFCell totaltaxprice1;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    SXSSFCell taxwithexchange = rowsub3.createCell((short) 7);
                    taxwithexchange.setCellValue(sessionBean.getLoc().getString("taxamountwithexchange") + ":" + taxRates);
                    taxwithexchange.setCellStyle(stylesub);

                    totaltaxprice = rowsub3.createCell((short) 9);
                    totaltaxprice1 = rowsub3.createCell((short) 10);
                } else {
                    totaltaxprice = rowsub3.createCell((short) 8);
                    totaltaxprice1 = rowsub3.createCell((short) 9);
                }

                totaltaxprice.setCellValue(sessionBean.getLoc().getString("totaltaxprice") + ":");
                totaltaxprice.setCellStyle(stylesub);

                totaltaxprice1.setCellValue(StaticMethods.round(totalTax.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                totaltaxprice1.setCellStyle(stylesub);

                //Toplam KDV BAZLI
                for (InvoiceItem tax : listOfTaxs) {

                    SXSSFRow rowsub7 = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell grandtotalgroupbytax;
                    SXSSFCell grandtotalgroupbytax1;

                    if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                        grandtotalgroupbytax = rowsub7.createCell((short) 9);
                        grandtotalgroupbytax1 = rowsub7.createCell((short) 10);
                    } else {
                        grandtotalgroupbytax = rowsub7.createCell((short) 8);
                        grandtotalgroupbytax1 = rowsub7.createCell((short) 9);
                    }

                    grandtotalgroupbytax.setCellValue(sessionBean.getLoc().getString("tax") + " (" + StaticMethods.round(tax.getTaxRate().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + ") :");
                    grandtotalgroupbytax.setCellStyle(stylesub);

                    grandtotalgroupbytax1.setCellValue(StaticMethods.round(tax.getTotalTax().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                    grandtotalgroupbytax1.setCellStyle(stylesub);

                }

                //Toplam Row5
                SXSSFRow rowsub8 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell grandtotal;
                SXSSFCell grandtotal1;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    grandtotal = rowsub8.createCell((short) 9);
                    grandtotal1 = rowsub8.createCell((short) 10);
                } else {
                    grandtotal = rowsub8.createCell((short) 8);
                    grandtotal1 = rowsub8.createCell((short) 9);
                }

                grandtotal.setCellValue(sessionBean.getLoc().getString("grandtotal") + ":");
                grandtotal.setCellStyle(stylesub);

                grandtotal1.setCellValue(StaticMethods.round(invoice.getPriceDifferenceTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                grandtotal1.setCellStyle(stylesub);

            }
        } else {
            StaticMethods.createHeaderExcel("tbvInvoice:frmInvoiceStokTab:dtbStock", tempToggle, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            for (InvoiceItem invoiceItem : listOfInvoiceItems) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                row.createCell((short) b++).setCellValue(invoiceItem.getStock().getBarcode());
                row.createCell((short) b++).setCellValue(invoiceItem.getStock().getCenterProductCode());
                row.createCell((short) b++).setCellValue(invoiceItem.getStock().getName());
                row.createCell((short) b++).setCellValue(invoiceItem.isIsService() ? sessionBean.getLoc().getString("service") : sessionBean.getLoc().getString("product"));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getStock().getStockInfo().getCurrentSalePrice() != null ? invoiceItem.getStock().getStockInfo().getCurrentSalePrice() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getQuantity().doubleValue(), invoiceItem.getUnit().getUnitRounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getUnitPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getDiscountRate() != null ? invoiceItem.getDiscountRate() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getDiscountPrice() != null ? invoiceItem.getDiscountPrice() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getDiscountRate2() != null ? invoiceItem.getDiscountRate2() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getDiscountPrice2() != null ? invoiceItem.getDiscountPrice2() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getInvoiceDiscountPrice() != null ? invoiceItem.getInvoiceDiscountPrice() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTotalPrice()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTaxRate() != null ? invoiceItem.getTaxRate() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTotalTax() != null ? invoiceItem.getTotalTax() : 0.0).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                if (invoiceItem.getProfitPercentage() == null) {
                    row.createCell((short) b++).setCellValue("-");
                } else {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getProfitPercentage().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (invoiceItem.getProfitPrice() == null) {
                    row.createCell((short) b++).setCellValue("-");
                } else {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getProfitPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                row.createCell((short) b++).setCellValue(StaticMethods.round(invoiceItem.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((invoiceItem.getTotalMoney().multiply(invoiceItem.getExchangeRate())).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

            }
            if (!listOfInvoiceItems.isEmpty()) {
                //Toplam Row1
                SXSSFRow rowsub1 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell totalAmountCell1 = rowsub1.createCell((short) 3);
                totalAmountCell1.setCellValue(sessionBean.getLoc().getString("totalamount") + ":");
                totalAmountCell1.setCellStyle(stylesub);

                SXSSFCell totalAmountCell2 = rowsub1.createCell((short) 5);
                totalAmountCell2.setCellValue(totalAmount);
                totalAmountCell2.setCellStyle(stylesub);

                SXSSFCell subTotalCell;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    subTotalCell = rowsub1.createCell((short) 17);
                } else {
                    subTotalCell = rowsub1.createCell((short) 16);
                }
                subTotalCell.setCellValue(sessionBean.getLoc().getString("subtotal") + ":");
                subTotalCell.setCellStyle(stylesub);

                SXSSFCell subTotalCell2;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    subTotalCell2 = rowsub1.createCell((short) 18);
                } else {
                    subTotalCell2 = rowsub1.createCell((short) 17);
                }
                subTotalCell2.setCellValue(StaticMethods.round(subTotal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                subTotalCell2.setCellStyle(stylesub);

                //Toplam Row2
                SXSSFRow rowsub2 = excelDocument.getSheet().createRow(jRow++);
                int r2;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    r2 = 11;
                } else {
                    r2 = 10;
                }

                SXSSFCell invoicebaseddiscountcell = rowsub2.createCell((short) r2);
                invoicebaseddiscountcell.setCellValue(sessionBean.getLoc().getString("invoicebaseddiscount") + ":");
                invoicebaseddiscountcell.setCellStyle(stylesub);

                r2++;
                SXSSFCell discountprice = rowsub2.createCell((short) r2);
                if (invoice.getDiscountRate().compareTo(BigDecimal.valueOf(0)) == 0) {
                    discountprice.setCellValue(StaticMethods.round(invoice.getDiscountPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                    discountprice.setCellStyle(stylesub);
                } else if (invoice.getDiscountRate().compareTo(BigDecimal.valueOf(0)) == 1) {
                    String param = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param += "%";
                    }
                    param += StaticMethods.round(invoice.getDiscountRate().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding());
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param += "%";
                    }
                    discountprice.setCellValue(param);
                    discountprice.setCellStyle(stylesub);
                }

                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    r2 = 14;
                } else {
                    r2 = 13;
                }

                r2++;
                SXSSFCell penbaseddiscount = rowsub2.createCell((short) r2);
                penbaseddiscount.setCellValue(sessionBean.getLoc().getString("penbaseddiscount") + ":");
                penbaseddiscount.setCellStyle(stylesub);

                r2++;
                SXSSFCell penbaseddiscount1 = rowsub2.createCell((short) r2);
                penbaseddiscount1.setCellValue(StaticMethods.round(totalDiscount.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                penbaseddiscount1.setCellStyle(stylesub);

                r2++;
                SXSSFCell totaldiscount = rowsub2.createCell((short) r2);
                totaldiscount.setCellValue(sessionBean.getLoc().getString("totaldiscount") + ":");
                totaldiscount.setCellStyle(stylesub);

                r2++;
                SXSSFCell totaldiscount1 = rowsub2.createCell((short) r2);
                totaldiscount1.setCellValue(StaticMethods.round((totalDiscount.add(invoice.getDiscountPrice())).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                totaldiscount1.setCellStyle(stylesub);

                //Toplam Row3
                SXSSFRow rowsub3 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell totaltaxprice;
                SXSSFCell totaltaxprice1;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    SXSSFCell taxwithexchange = rowsub3.createCell((short) 16);
                    taxwithexchange.setCellValue(sessionBean.getLoc().getString("taxamountwithexchange") + ":" + taxRates);
                    taxwithexchange.setCellStyle(stylesub);

                    totaltaxprice = rowsub3.createCell((short) 17);
                    totaltaxprice1 = rowsub3.createCell((short) 18);
                } else {
                    totaltaxprice = rowsub3.createCell((short) 16);
                    totaltaxprice1 = rowsub3.createCell((short) 17);
                }

                totaltaxprice.setCellValue(sessionBean.getLoc().getString("totaltaxprice") + ":");
                totaltaxprice.setCellStyle(stylesub);

                totaltaxprice1.setCellValue(StaticMethods.round(invoice.getTotalTax().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                totaltaxprice1.setCellStyle(stylesub);

                //Toplam Kar
                SXSSFRow rowsub6 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell profit;
                SXSSFCell profit1;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    profit = rowsub6.createCell((short) 17);
                    profit1 = rowsub6.createCell((short) 18);
                } else {
                    profit = rowsub6.createCell((short) 16);
                    profit1 = rowsub6.createCell((short) 17);
                }

                profit.setCellValue(sessionBean.getLoc().getString("totalprofitprice") + ":");
                profit.setCellStyle(stylesub);

                profit1.setCellValue(StaticMethods.round(invoice.getTotalProfit().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                profit1.setCellStyle(stylesub);

                //Toplam Row4
                SXSSFRow rowsub4 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell roundingprice;
                SXSSFCell roundingprice1;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    roundingprice = rowsub4.createCell((short) 17);
                    roundingprice1 = rowsub4.createCell((short) 18);
                } else {
                    roundingprice = rowsub4.createCell((short) 16);
                    roundingprice1 = rowsub4.createCell((short) 17);
                }

                roundingprice.setCellValue(sessionBean.getLoc().getString("roundingprice") + ":");
                roundingprice.setCellStyle(stylesub);

                roundingprice1.setCellValue(StaticMethods.round(invoice.getRoundingPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                roundingprice1.setCellStyle(stylesub);

                //Toplam KDV BAZLI
                for (InvoiceItem tax : listOfTaxs) {

                    SXSSFRow rowsub7 = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell grandtotalgroupbytax;
                    SXSSFCell grandtotalgroupbytax1;

                    if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                        grandtotalgroupbytax = rowsub7.createCell((short) 17);
                        grandtotalgroupbytax1 = rowsub7.createCell((short) 18);
                    } else {
                        grandtotalgroupbytax = rowsub7.createCell((short) 16);
                        grandtotalgroupbytax1 = rowsub7.createCell((short) 17);
                    }

                    grandtotalgroupbytax.setCellValue(sessionBean.getLoc().getString("tax") + " (" + StaticMethods.round(tax.getTaxRate().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + ") :");
                    grandtotalgroupbytax.setCellStyle(stylesub);

                    grandtotalgroupbytax1.setCellValue(StaticMethods.round(tax.getTotalTax().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                    grandtotalgroupbytax1.setCellStyle(stylesub);

                }

                //Toplam Row5
                SXSSFRow rowsub5 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell grandtotal;
                SXSSFCell grandtotal1;
                if (sessionBean.getLastBranchSetting().isIsForeignCurrency()) {
                    grandtotal = rowsub5.createCell((short) 17);
                    grandtotal1 = rowsub5.createCell((short) 18);
                } else {
                    grandtotal = rowsub5.createCell((short) 16);
                    grandtotal1 = rowsub5.createCell((short) 17);
                }

                grandtotal.setCellValue(sessionBean.getLoc().getString("grandtotal") + ":");
                grandtotal.setCellStyle(stylesub);

                grandtotal1.setCellValue(StaticMethods.round(invoice.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(invoice.getCurrency().getId(), 0));
                grandtotal1.setCellStyle(stylesub);
            }
        }

        try {
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("invoice"));
        } catch (IOException ex) {
            Logger.getLogger(InvoiceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int deletePeriodInvoice(Invoice invoice) {
        return invoiceDao.deletePeriodInvoice(invoice);
    }

    @Override
    public List<CheckDelete> testBeforeDelete(Invoice invoice) {
        return invoiceDao.testBeforeDelete(invoice);
    }

    @Override
    public List<Invoice> invoiceBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        return invoiceDao.invoiceBook(first, pageSize, sortField, sortOrder, filters, where, type, param);
    }

    @Override
    public int invoiceBookCount(String where, String type, List<Object> param) {
        return invoiceDao.invoiceBookCount(where, type, param);
    }

    @Override
    public int controlTankItemAvailable(String warehouseList, Stock stock) {
        return invoiceDao.controlTankItemAvailable(warehouseList, stock);
    }

    @Override
    public int controlAutomationWarehouse(String warehouseList) {
        return invoiceDao.controlAutomationWarehouse(warehouseList);
    }

    //Daha önce SAP'ye gönderilmiş fatura üzerinde güncelleme yapılacağında Sap'ye ters kayıt gönderir.
    @Override
    public boolean sendSapReverse(Invoice obj) {

        BranchSetting bs = sessionBean.getUser().getLastBranchSetting();
        String response = "";
        String message = "";
        String documentNumber = "";
        String sapIDocNo = "";
        boolean isSend = false;
        String result = null;
        String url = bs.getErpUrl();
        IntegrationForSap sap = new IntegrationForSap();

        HttpPost httpPost = new HttpPost(url);
        try {

            HttpClient httpClient = WebServiceClient.createHttpClient_AcceptsUntrustedCerts();
            httpPost.addHeader("Operation", "SiparisYaratma");

            byte[] encodedAuth = Base64.getEncoder().encode((bs.getErpUsername() + ":" + bs.getErpPassword()).getBytes("UTF-8"));
            String authHeader = "Basic " + new String(encodedAuth);
            httpPost.addHeader("Authorization", authHeader);

            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("REFERENCE", obj.getId());
            jsonObject.addProperty("ISTASYON_KOD", obj.getBranchSetting().getBranch().getLicenceCode());
            jsonArray.add(jsonObject);

            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("item", jsonArray);

            JsonObject jsonObjectData = new JsonObject();
            jsonObjectData.add("IT_IPTAL", jsonObject1);
            jsonObjectData.addProperty("IV_TYPE", 5);

            System.out.println("-----JSOB OBJECT DATA---" + jsonObjectData.toString());
            String json = jsonObjectData.toString();
            StringEntity requestEntity = new StringEntity(
                      json,
                      ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            RequestConfig rc = RequestConfig.DEFAULT;
            RequestConfig requestConfig
                      = RequestConfig
                                .copy(rc)
                                .setSocketTimeout(bs.getErpTimeout() * 1000)
                                .setConnectTimeout(bs.getErpTimeout() * 1000)
                                .setConnectionRequestTimeout(bs.getErpTimeout() * 1000)
                                .build();
            httpPost.setConfig(requestConfig);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println("return code --" + returnCode);

            if (returnCode == 200) {
                result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                JSONObject resJson = new JSONObject(result);
                System.out.println("RESULT : " + result);

                JSONObject resJsonReturn = new JSONObject(resJson.getJSONObject("ET_RETURN").toString());

                if (resJsonReturn.get("item").toString().charAt(0) == '{') {

                    JSONObject resJsonItem = new JSONObject(resJsonReturn.get("item").toString());

                    if (resJsonItem.getString("TYPE").equals("S")) {
                        isSend = true;
                    } else if (resJsonItem.getString("TYPE").equals("E")) {
                        isSend = false;
                    }
                    message = resJsonItem.getString("MESSAGE");

                } else if (resJsonReturn.get("item").toString().charAt(0) == '[') {

                    JSONArray jsonArrItem1 = new JSONArray(resJsonReturn.get("item").toString());

                    for (int j = 0; j < jsonArrItem1.length(); j++) {

                        if (!jsonArrItem1.getJSONObject(j).toString().equals("")) {

                            JSONObject resJsonItem1 = new JSONObject(jsonArrItem1.getJSONObject(j).toString());

                            if (resJsonItem1.getString("TYPE").equals("S")) {
                                isSend = true;
                            } else if (resJsonItem1.getString("TYPE").equals("E")) {
                                isSend = false;
                            }
                            message = resJsonItem1.getString("MESSAGE");
                        }
                    }

                }

                JSONObject resJsonDocNum = new JSONObject(resJson.getJSONObject("ET_DOCNUM").toString());

                if (resJsonDocNum.get("item").toString().charAt(0) == '{') {

                    JSONObject resJsonItemDoc = new JSONObject(resJsonDocNum.get("item").toString());

                    if (!resJsonItemDoc.isNull("VBELNVA")) {
                        documentNumber = documentNumber + "," + " VBELNVA : " + resJsonItemDoc.get("VBELNVA");
                    }
                    if (!resJsonItemDoc.isNull("VBELNVL")) {
                        documentNumber = documentNumber + "," + " VBELNVL : " + resJsonItemDoc.get("VBELNVL");
                    }

                    if (!resJsonItemDoc.isNull("VBELNVF")) {
                        documentNumber = documentNumber + "," + " VBELNVF : " + resJsonItemDoc.get("VBELNVF");
                    }

                    if (!documentNumber.equals("")) {
                        documentNumber = documentNumber.substring(1, documentNumber.length());
                    }

                    sapIDocNo = resJsonItemDoc.getString("DOCNUM");

                } else if (resJsonDocNum.get("item").toString().charAt(0) == '[') {

                    JSONArray jsonArrItem = new JSONArray(resJsonDocNum.get("item").toString());

                    for (int j = 0; j < jsonArrItem.length(); j++) {
                        if (!jsonArrItem.getJSONObject(j).toString().equals("")) {
                            if (!jsonArrItem.getJSONObject(j).isNull("VBELNVA")) {
                                documentNumber = documentNumber + "," + " VBELNVA : " + jsonArrItem.getJSONObject(j).get("VBELNVA");
                            }
                            if (!jsonArrItem.getJSONObject(j).isNull("VBELNVL")) {
                                documentNumber = documentNumber + "," + " VBELNVL : " + jsonArrItem.getJSONObject(j).get("VBELNVL");
                            }

                            if (!jsonArrItem.getJSONObject(j).isNull("VBELNVF")) {
                                documentNumber = documentNumber + "," + " VBELNVF : " + jsonArrItem.getJSONObject(j).get("VBELNVF");
                            }

                            sapIDocNo = sapIDocNo + jsonArrItem.getJSONObject(j).getString("DOCNUM");
                        }
                    }

                    if (!documentNumber.equals("")) {
                        documentNumber = documentNumber.substring(1, documentNumber.length());
                    }

                }

                Date sendDate = new Date();
                sap.setIsSend(isSend);
                sap.setResponse(result);
                sap.setMessage(message);
                sap.setSendDate(sendDate);
                sap.setSapDocumentNumber(documentNumber);
                sap.setSapIDocNo(sapIDocNo);
                sap.setJsonData(json);

                if (isSend) {
                    invoiceDao.createLogSapSaleInvoice(sap, obj); //Eğer SAP'ye ters kayıt başarılı olarak gönderilmişse log tablosuna insert ya da update yapılır.
                }

            } else {
                System.out.println("HTTP STATUS : " + returnCode);
            }

        } catch (Exception e) {
            System.out.println("Catch 1 : " + e.toString());

        } finally {
            try {
                httpPost.releaseConnection();
            } catch (Exception fe) {
                System.out.println("Catch 2 : " + fe.toString());

            }
        }
        return isSend;
    }

    @Override
    public int updateLogSap(Invoice invoice) {
        return invoiceDao.updateLogSap(invoice);
    }

    @Override
    public int createInvoiceForOrder(Invoice invoice, List<InvoiceItem> listOfItem) {
        invoice.setJsonWarehouses(jsonArrayWarehouses(invoice.getListOfWarehouse()));
        String invoiceItems = jsonArrayInvoiceItems(listOfItem);
        return invoiceDao.createInvoiceForOrder(invoice, invoiceItems);
    }

    @Override
    public String jsonArrayInvoiceItems(List<InvoiceItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (InvoiceItem obj : list) {

            //kdv hariç ise kdv'li birim fiyat hesapla
            if (!obj.isIsTaxIncluded()
                      && obj.getTaxRate() != null
                      && obj.getTaxRate().doubleValue() > 0
                      && obj.getUnitPrice() != null
                      && obj.getUnitPrice().doubleValue() > 0) {

                BigDecimal x = BigDecimal.ONE.add(obj.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                obj.setUnitPrice(obj.getUnitPrice().multiply(x));
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("is_service", obj.isIsService());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getId() : obj.getStock().getUnit().getId());
            jsonObject.addProperty("unitprice", obj.getUnitPrice());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("totalprice", obj.getTotalPrice());
            jsonObject.addProperty("taxrate", obj.getTaxRate());
            jsonObject.addProperty("totaltax", obj.getTotalTax());
            jsonObject.addProperty("is_discountrate", obj.isIsDiscountRate());
            jsonObject.addProperty("discountrate", obj.getDiscountRate() == null ? 0 : obj.getDiscountRate());
            jsonObject.addProperty("discountprice", obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice());
            jsonObject.addProperty("is_discountrate2", obj.isIsDiscountRate2());
            jsonObject.addProperty("discountrate2", obj.getDiscountRate2() == null ? 0 : obj.getDiscountRate2());
            jsonObject.addProperty("discountprice2", obj.getDiscountPrice2() == null ? 0 : obj.getDiscountPrice2());
            jsonObject.addProperty("currency_id", obj.getCurrency().getId());
            jsonObject.addProperty("exchangerate", obj.getExchangeRate());
            jsonObject.addProperty("totalmoney", obj.getTotalMoney());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            jsonObject.addProperty("waybillitem_id", obj.getWaybillItemIds());//irsaliyeden aktarıldı ise
            jsonObject.addProperty("managerUserDataid", ((obj.getDiscountPrice() == null || obj.getDiscountPrice().doubleValue() == 0)
                      && (obj.getDiscountPrice2() == null || obj.getDiscountPrice2().doubleValue() == 0)) ? null : sessionBean.getUser().getId());
            jsonObject.addProperty("isManagerDiscount", ((obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0)
                      || (obj.getDiscountPrice2() != null && obj.getDiscountPrice2().doubleValue() > 0)));
            jsonObject.addProperty("recommendedprice", obj.getStock().getStockInfo().getRecommendedPrice() == null ? BigDecimal.ZERO : obj.getStock().getStockInfo().getRecommendedPrice());
            jsonObject.addProperty("warehouse_id", obj.getInvoice().getListOfWarehouse().isEmpty() ? 0 : obj.getInvoice().getListOfWarehouse().get(0).getId());
            jsonObject.addProperty("is_free", obj.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 0 ? true : false);
            jsonObject.addProperty("differentinvoiceitem_id", obj.getPriceDifferentInvoiceItem() != null ? obj.getPriceDifferentInvoiceItem().getId() == 0 ? null : obj.getPriceDifferentInvoiceItem().getId() : null);
            jsonObject.addProperty("differenttotalmoney", obj.getPriceDifferentTotalMoney() == null ? 0 : obj.getPriceDifferentTotalMoney());

            JsonArray jsonArrayOrderItems = new JsonArray();
            System.out.println("obj.getOrderItemIds()" + obj.getOrderItemIds());
            System.out.println("obj.getOrderItemQuantitys()" + obj.getOrderItemQuantitys());
            String[] orderitemids;
            String[] quantitys;
            if (obj.getOrderItemIds() != null && !obj.getOrderItemIds().equals("")) {
                orderitemids = obj.getOrderItemIds().split(",");
                quantitys = obj.getOrderItemQuantitys().split(",");

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

        return jsonArray.toString();
    }

    @Override
    public int insertHistory(Invoice invoice) {

        return invoiceDao.insertHistory(invoice);

    }

    @Override
    public int findSaleForInvoice(BranchSetting branchSetting, int invoiceId, boolean isDelete) {
        return invoiceDao.findSaleForInvoice(branchSetting, invoiceId, isDelete);
    }

    @Override
    public int createParoSales(int saleId) {
        return invoiceDao.createParoSales(saleId);
    }
}
