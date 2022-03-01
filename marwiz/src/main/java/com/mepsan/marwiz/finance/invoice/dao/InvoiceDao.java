/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 09:34:26
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSap;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class InvoiceDao extends JdbcDaoSupport implements IInvoiceDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Invoice> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String column = "";
        String join = "";

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranchSetting().getErpIntegrationId() == 1) {
            column = " (\n"
                    + "        SELECT \n"
                    + "         ssi.is_send AS is_send\n"
                    + "        FROM integration.sap_saleinvoice ssi \n"
                    + "        WHERE ssi.deleted=FALSE AND ssi.is_retail =FALSE AND ssi.event !=3\n"
                    + "        AND ssi.object_id = inv.id AND ssi.branch_id = inv.branch_id \n"
                    + "        ORDER BY ssi.c_time ASC LIMIT 1\n"
                    + "        )AS ssinvis_send,\n"
                    + "         (\n"
                    + "         SELECT \n"
                    + "          spi.is_send AS is_send\n"
                    + "        FROM integration.sap_purchaseinvoice spi \n"
                    + "         WHERE spi.deleted=FALSE AND  spi.type_id IN(1,3)\n"
                    + "        AND spi.object_id = inv.id AND spi.branch_id = inv.branch_id \n"
                    + "        ORDER BY spi.c_time ASC LIMIT 1\n"
                    + "        )AS spinvis_send,\n"
                    + "         (\n"
                    + "         SELECT \n"
                    + "          spi.is_sendwaybill as is_sendwaybill\n"
                    + "        FROM integration.sap_purchaseinvoice spi \n"
                    + "         WHERE spi.deleted=FALSE AND  spi.type_id IN(1,3)\n"
                    + "        AND spi.object_id = inv.id AND spi.branch_id = inv.branch_id\n"
                    + "        ORDER BY spi.c_time ASC LIMIT 1\n"
                    + "        )AS spinvis_sendwaybill,\n";

        }

        if (sortField == null) {
            sortField = "inv.id";
            sortOrder = "desc";
        }

        String sql = "SELECT\n"
                + "        inv.id as invid,\n"
                + "        inv.is_purchase as invis_purchase,\n"
                + "        inv.account_id as invaccount_id,\n"
                + "        acc.name as accname,\n"
                + "        acc.title as acctitle,\n"
                + "        acc.is_person as accis_person,\n"
                + "        acc.is_employee AS accis_employee,\n"
                + "        acc.phone as accphone,\n"
                + "        acc.email as accemail,\n"
                + "        acc.address as accaddress,\n"
                + "        acc.taxno as acctaxno,\n"
                + "        acc.taxoffice as acctaxoffice,\n"
                + "        acc.balance as accbalance,\n"
                + "        acc.dueday as accdueday,\n"
                + "        acc.code as acccode,\n"
                + "        inv.documentnumber_id as invdocumentnumber_id,\n"
                + "        inv.documentserial as invdocumentserial,\n"
                + "        inv.documentnumber as invdocumentnumber,\n"
                + "        inv.invoicedate as invinvoicedate,\n"
                + "        inv.duedate as invduedate,\n"
                + "        inv.dispatchdate as invdispatchdate,\n"
                + "        inv.dispatchaddress as invdispatchaddress,\n"
                + "        inv.description as invdescription,\n"
                + "        inv.type_id as invtype_id,\n"
                + "        inv.is_periodinvoice as invis_periodinvoice,\n"
                + "        typd.name as typdname,\n"
                + "        inv.status_id as invstatus_id,\n"
                + "        sttd.name as sttdname,\n"
                + "        inv.c_id as invc_id,\n"
                + "        usd.name AS  usname,\n"
                + "        usd.surname AS ussurname,\n"
                + "        usd.username AS ususername,\n"
                + "        inv.c_time AS invc_time, \n"
                + "        inv.is_discountrate as invis_discountrate,\n"
                + "        COALESCE(inv.discountrate,0) as invdiscountrate,\n"
                + "        COALESCE(inv.discountprice,0) as invdiscountprice,\n"
                + "        COALESCE(inv.totaldiscount,0) as invtotaldiscount,\n"
                + "        COALESCE(inv.remainingmoney,0) as invremainingmoney,\n"
                + "        COALESCE(inv.totaltax,0) as invtotaltax,\n"
                + "        COALESCE(inv.totalprice,0) as invtotalprice,\n"
                + "        COALESCE(inv.totalmoney,0) as invtotalmoney,\n"
                + "        COALESCE(inv.roundingprice,0) as invroundingprice,\n"
                + "        inv.currency_id as invcurrency_id,\n"
                + "        inv.exchangerate as invexchangerate,\n"
                + "        s.id as sid,\n"
                + "        s.pointofsale_id as spointofsale_id,\n"
                + "          inv.taxpayertype_id AS invtaxpayertype_id,\n"
                + "          inv.deliverytype_id AS invdeliverytype_id,\n"
                + "          inv.invoicescenario_id AS invinvoicescenario_id,\n"
                + "            inv.is_einvoice As invis_einvoice,\n"
                + "        (SELECT \n"
                + "           STRING_AGG(CAST(COALESCE(iw.id,0) as varchar),',') \n"
                + "         FROM\n"
                + "           finance.waybill_warehousereceipt_con wwc\n"
                + "         LEFT JOIN inventory.warehousereceipt wr ON(wr.id=wwc.warehousereceipt_id AND wr.deleted=FALSE)\n"
                + "         LEFT JOIN inventory.warehouse iw ON(iw.id=wr.warehouse_id)\n"
                + "         WHERE wwc.deleted=FALSE AND wwc.waybill_id = wb.id\n"
                + "        ) as warehouseid,\n"
                + "        wb.deliveryperson as wbdeliveryperson,\n"
                + "        (SELECT \n"
                + "           COALESCE(SUM(invi.profitprice),0)\n"
                + "        FROM finance.invoiceitem invi\n"
                + "           WHERE invi.deleted=FALSE AND invi.invoice_id=inv.id\n"
                + "        ) AS invoiceitemprofit,\n"
                + "        CASE WHEN EXISTS (\n"
                + "        	SELECT \n"
                + "            	invp.id\n"
                + "            FROM finance.invoicepayment invp \n"
                + "            WHERE invp.deleted=FALSE AND invp.invoice_id = inv.id\n"
                + "        ) THEN TRUE ELSE FALSE END AS ispayment,\n"
                + "        CASE WHEN EXISTS\n"
                + "        (SELECT \n"
                + "           fo.id\n"
                + "        FROM finance.order_waybill_con owc\n"
                + "        INNER JOIN finance.order fo ON (fo.id = owc.order_id AND fo.deleted = FALSE)\n"
                + "        WHERE owc.waybill_id =  wb.id \n"
                + "        AND owc.deleted = FALSE) THEN TRUE ELSE FALSE END AS isorderconnection,\n"
                + "        inv.branch_id AS invbranch_id,\n"
                + "        brs.is_centralintegration AS brsis_centralintegration,\n"
                + "        brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "        br.currency_id AS brcurrency_id,\n"
                + "        brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                + "        br.name AS brname,\n"
                + "        br.is_agency AS bris_agency,\n"
                + "        br.licencecode AS brlicencecode,\n"
                + "        inv.differentinvoice_id AS invdifferentinvoice_id,\n"
                + "        inv.is_differentdirection AS invis_differentdirection,\n"
                + "        inv1.documentnumber_id as inv1documentnumber_id,\n"
                + "        inv1.documentserial as inv1documentserial,\n"
                + "        inv1.documentnumber as inv1documentnumber,\n"
                + "        COALESCE(inv.differenttotalmoney,0) AS invoicedifferentprice,\n"
                + "        inv.is_wait AS invis_wait,\n"
                + column
                + "        inv.waitinvoiceitemjson AS invwaitinvoiceitemjson,\n"
                + "        inv.is_fuel AS invis_fuel,\n"
                + "        CASE WHEN inv.type_id = 26 THEN\n"
                + "          COALESCE((SELECT \n"
                + "             SUM(COALESCE(invi.differenttotalmoney,0)/(1 + (COALESCE(invi.taxrate,0)/100)))\n"
                + "           FROM finance.invoiceitem invi\n"
                + "           WHERE invi.deleted = FALSE\n"
                + "           AND invi.invoice_id = inv.id\n"
                + "          ),0) ELSE 0 END AS invoicedifferenttotalprice,\n"
                + "        brs.parourl AS brsparourl,\n"
                + "        brs.paroaccountcode AS brsparoaccountcode,\n"
                + "        brs.parobranchcode AS brsparobranchcode,\n"
                + "        brs.paroresponsiblecode AS brsparoresponsiblecode\n"
                + "      FROM  finance.invoice inv  \n"
                + "      INNER JOIN general.account acc   ON (acc.id=inv.account_id)\n"
                + "      INNER JOIN system.type_dict typd  ON (typd.type_id = inv.type_id AND typd.language_id = ?) \n"
                + "      INNER JOIN system.status_dict sttd  ON (sttd.status_id = inv.status_id AND sttd.language_id = ?)  \n"
                + "      LEFT JOIN general.sale s ON (s.invoice_id = inv.id AND s.deleted = FALSE AND s.is_return = FALSE AND inv.is_periodinvoice=False)\n"
                + "      LEFT JOIN finance.waybill_invoice_con wic ON (wic.invoice_id = inv.id AND wic.deleted = FALSE AND inv.type_id = 59)\n"
                + "      LEFT JOIN finance.waybill wb ON (wb.id = wic.waybill_id)\n"
                + "      LEFT JOIN general.userdata usd ON(usd.id=inv.c_id)\n"
                + "      INNER JOIN general.branchsetting brs ON (brs.branch_id = inv.branch_id AND brs.deleted = FALSE)\n"
                + "      INNER JOIN general.branch br ON (br.id = inv.branch_id AND br.deleted = FALSE)\n"
                + "      LEFT JOIN finance.invoice inv1 ON(inv1.id = inv.differentinvoice_id AND inv1.deleted=FALSE)\n"
                + join
                + "      WHERE inv.deleted = false \n" + where
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().query(sql, params, new InvoiceMapper());
    }

    @Override
    public int count(String where) {
        String sql = "SELECT COUNT(inv.id) FROM finance.invoice inv  "
                + "	INNER JOIN general.account acc   ON (acc.id=inv.account_id)\n"
                + "	INNER JOIN system.type_dict typd   ON (typd.type_id = inv.type_id AND typd.language_id = ?) \n"
                + "	INNER JOIN system.status_dict sttd  ON (sttd.status_id = inv.status_id AND sttd.language_id = ?)  \n"
                + "   INNER JOIN general.branchsetting brs ON (brs.branch_id = inv.branch_id AND brs.deleted = FALSE)\n"
                + "   INNER JOIN general.branch br ON (br.id = inv.branch_id AND br.deleted = FALSE)\n"
                + "WHERE inv.deleted = false " + where;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

    /**
     * Bu metot hem fatura ekleme işlemini gerçekleştirir.
     *
     *
     * @param obj
     * @return
     */
    @Override
    public int create(Invoice obj) {
        String sql = "SELECT r_invoice_id FROM finance.process_invoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{
            0,
            obj.getId(),
            obj.getBranchSetting().getBranch().getId(),
            sessionBean.getUser().getId(),
            obj.isIsPurchase(),
            obj.isIsPeriodInvoice(),
            obj.getAccount().getId(),
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(),
            obj.getDocumentSerial(),
            obj.getDocumentNumber(),
            new Timestamp(obj.getInvoiceDate().getTime()),
            new Timestamp(obj.getDueDate().getTime()),
            new Timestamp(obj.getDispatchDate().getTime()),
            obj.getDispatchAddress(),
            obj.getDescription(),
            obj.getStatus().getId(),
            obj.getType().getId(),
            obj.isIsDiscountRate(),
            obj.getDiscountRate() == null ? 0 : obj.getDiscountRate(),
            obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice(),
            obj.getRoundingPrice() == null ? 0 : obj.getRoundingPrice(),
            obj.getCurrency().getId(),
            obj.getExchangeRate(),
            obj.getDeliveryPerson() == null ? null : obj.getDeliveryPerson(),
            obj.getJsonWarehouses(),
            obj.getTaxPayerTypeId(),
            obj.getDeliveryTypeId(),
            obj.getInvoiceScenarioId(),
            80,
            (obj.getTotalDiscount() == null || obj.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82,
            null,
            obj.isIsEInvoice(),
            obj.getPriceDifferenceInvoice().getId() == 0 ? null : obj.getPriceDifferenceInvoice().getId(),
            obj.isIsDifferenceDirection(),
            obj.isIsWait(), obj.getWaitInvoiceItemJson(),
            null,
            obj.isIsFuel()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu metot hem fatura güncelleme işleminin gerçekleştirir.
     *
     * @param obj
     * @return
     */
    @Override
    public int update(Invoice obj) {
        String sql = "SELECT r_invoice_id FROM finance.process_invoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{
            1,
            obj.getId(),
            obj.getBranchSetting().getBranch().getId(),
            sessionBean.getUser().getId(),
            obj.isIsPurchase(),
            obj.isIsPeriodInvoice(),
            obj.getAccount().getId(),
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(),
            obj.getDocumentSerial(),
            obj.getDocumentNumber(),
            new Timestamp(obj.getInvoiceDate().getTime()),
            new Timestamp(obj.getDueDate().getTime()),
            new Timestamp(obj.getDispatchDate().getTime()),
            obj.getDispatchAddress(),
            obj.getDescription(),
            obj.getStatus().getId(),
            obj.getType().getId(),
            obj.isIsDiscountRate(),
            obj.getDiscountRate() == null ? 0 : obj.getDiscountRate(),
            obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice(),
            obj.getRoundingPrice() == null ? 0 : obj.getRoundingPrice(),
            obj.getCurrency().getId(),
            obj.getExchangeRate(),
            obj.getDeliveryPerson() == null ? null : obj.getDeliveryPerson(),
            obj.getJsonWarehouses(),
            obj.getTaxPayerTypeId(),
            obj.getDeliveryTypeId(),
            obj.getInvoiceScenarioId(),
            80,
            (obj.getTotalDiscount() == null || obj.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82,
            null,
            obj.isIsEInvoice(),
            obj.getPriceDifferenceInvoice().getId() == 0 ? null : obj.getPriceDifferenceInvoice().getId(),
            obj.isIsDifferenceDirection(),
            obj.isIsWait(), obj.getWaitInvoiceItemJson(),
            null,
            obj.isIsFuel()};
        System.out.println("param:" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * İrsaliye ile faturayı eşleştirir.
     *
     * @param obj
     * @param waybill
     * @return
     */
    @Override
    public int createInvoiceWaybillCon(Invoice obj, Waybill waybill) {

        String sql = "INSERT INTO finance.waybill_invoice_con (invoice_id,waybill_id,c_id,u_id) VALUES (?,?,?,?) RETURNING id";

        Object[] param = new Object[]{obj.getId(), waybill.getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int sendInvoiceCenter(Invoice invoice) {
        String sql;
        Object[] param;

        if (invoice.isIsPurchase()) {
            sql = "SELECT log.insertjson_purchase(?,?,?);";
            param = new Object[]{invoice.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};
        } else {
            sql = "SELECT log.insertjson_sale(?,?,?,?);";
            param = new Object[]{0, invoice.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};
        }

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
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
        String sql = "SELECT id FROM general.sale WHERE invoice_id = ? ";
        Object[] params = new Object[]{invoice.getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

    @Override
    public int delete(Invoice obj) {
        String sql = "SELECT r_invoice_id FROM finance.process_invoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{
            2, obj.getId(), obj.getBranchSetting().getBranch().getId(), sessionBean.getUser().getId(), obj.isIsPurchase(), obj.isIsPeriodInvoice(), obj.getAccount().getId(), obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(),
            obj.getDocumentSerial(), obj.getDocumentNumber(), new Timestamp(obj.getInvoiceDate().getTime()),
            new Timestamp(obj.getDueDate().getTime()), new Timestamp(obj.getDispatchDate().getTime()),
            obj.getDispatchAddress(), obj.getDescription(), obj.getStatus().getId(), obj.getType().getId(), obj.isIsDiscountRate(), obj.getDiscountRate(), obj.getDiscountPrice(), null, obj.getCurrency().getId(),
            obj.getExchangeRate(), obj.getDeliveryPerson() == null ? null : obj.getDeliveryPerson(), obj.getJsonWarehouses(),
            obj.getTaxPayerTypeId(), obj.getDeliveryTypeId(), obj.getInvoiceScenarioId(),
            80, (obj.getTotalDiscount() == null || obj.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82, null, obj.isIsEInvoice(), obj.getPriceDifferenceInvoice().getId() == 0 ? null : obj.getPriceDifferenceInvoice().getId(),
            obj.isIsDifferenceDirection(), obj.isIsWait(), obj.getWaitInvoiceItemJson(), null, obj.isIsFuel()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createInvoiceForAgreement(Invoice invoice, InvoiceItem invoiceItem, CustomerAgreements customerAgreements, CreditReport creditReport) {
        String sql = "SELECT r_period_invoice_id FROM finance.period_invoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{0, invoice.getId(), invoice.getBranchSetting().getBranch().getId(), sessionBean.getUser().getId(), invoice.isIsPurchase(), invoice.getAccount().getId(), invoice.getdNumber().getId() == 0 ? null : invoice.getdNumber().getId(),
            invoice.getDocumentSerial(), invoice.getDocumentNumber(), new Timestamp(invoice.getInvoiceDate().getTime()),
            new Timestamp(invoice.getDueDate().getTime()), new Timestamp(invoice.getDispatchDate().getTime()),
            invoice.getDispatchAddress(), invoice.getDescription(), invoice.getStatus().getId(), invoice.getType().getId(), invoice.isIsDiscountRate(), invoice.getDiscountRate(), invoice.getDiscountPrice(),
            invoice.getRoundingPrice() == null ? 0 : invoice.getRoundingPrice(), invoice.getCurrency().getId(),
            invoice.getExchangeRate(), invoiceItem.getJsonItems(), customerAgreements.getBeginDate(), customerAgreements.getEndDate(), creditReport.getId(),
            invoice.getTaxPayerTypeId(), invoice.getDeliveryTypeId(), invoice.getInvoiceScenarioId(),
            80, (invoice.getTotalDiscount() == null || invoice.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82, null, invoice.isIsEInvoice(), customerAgreements.isChcCredit(), customerAgreements.getCreditIds()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu metot aynı belge numarasına ait faturanın bilgilerini göstermek için
     * çeker
     *
     * @param invoice
     * @return
     */
    @Override
    public Invoice findDuplicateInvoice(Invoice invoice) {
        String sql = "SELECT\n"
                + "  inv.id as invid,\n"
                + "  inv.is_purchase as invis_purchase,\n"
                + "  acc.name as accname,\n"
                + "  acc.title as acctitle,\n"
                + "  inv.documentnumber_id as invdocumentnumber_id,\n"
                + "  inv.documentserial as invdocumentserial,\n"
                + "  inv.documentnumber as invdocumentnumber,\n"
                + "  inv.invoicedate as invinvoicedate,\n"
                + "  typd.name as typdname,\n"
                + "  COALESCE(inv.totalmoney,0) as invtotalmoney,\n"
                + "  inv.currency_id as invcurrency_id,\n"
                + "  us.name as usname, \n"
                + "  us.username as ususername, \n"
                + "  us.surname as ussurname\n"
                + "FROM  finance.invoice inv  \n"
                + "INNER JOIN general.account acc ON (acc.id=inv.account_id)\n"
                + "INNER JOIN system.type_dict typd ON (typd.type_id = inv.type_id AND typd.language_id = ?) \n"
                + "INNER JOIN general.userdata us ON (us.id = inv.c_id)\n"
                + "WHERE inv.deleted = false \n"
                + "AND inv.account_id = ?\n"
                + "AND inv.documentserial = ?\n"
                + "AND inv.documentnumber = ? ";

        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), invoice.getAccount().getId(), invoice.getDocumentSerial(), invoice.getDocumentNumber()};

        return getJdbcTemplate().queryForObject(sql, params, new InvoiceMapper());
    }

    @Override
    public int deletePeriodInvoice(Invoice invoice) {
        String sql = "SELECT r_period_invoice_id FROM finance.period_invoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{1, invoice.getId(), invoice.getBranchSetting().getBranch().getId(), sessionBean.getUser().getId(), invoice.isIsPurchase(), invoice.getAccount().getId(), invoice.getdNumber().getId() == 0 ? null : invoice.getdNumber().getId(),
            invoice.getDocumentSerial(), invoice.getDocumentNumber(), new Timestamp(invoice.getInvoiceDate().getTime()),
            new Timestamp(invoice.getDueDate().getTime()), new Timestamp(invoice.getDispatchDate().getTime()),
            invoice.getDispatchAddress(), invoice.getDescription(), invoice.getStatus().getId(), invoice.getType().getId(), invoice.isIsDiscountRate(), invoice.getDiscountRate(), invoice.getDiscountPrice(),
            invoice.getRoundingPrice() == null ? 0 : invoice.getRoundingPrice(), invoice.getCurrency().getId(),
            invoice.getExchangeRate(), null, null, null, null, invoice.getTaxPayerTypeId(), invoice.getDeliveryTypeId(), invoice.getInvoiceScenarioId(),
            80, (invoice.getTotalDiscount() == null || invoice.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82, null, invoice.isIsEInvoice(), false, ""};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(Invoice invoice) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {11, invoice.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public List<Invoice> invoiceBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {

        if (type.equals("differencepriceinvoice")) {
            where = where + " AND inv.account_id = " + ((Invoice) param.get(0)).getAccount().getId() + " AND inv.type_id <> 26\n"
                    + " AND inv.branch_id = " + ((Invoice) param.get(0)).getBranchSetting().getBranch().getId() + "\n"
                    + " AND inv.is_purchase = " + ((Invoice) param.get(0)).isIsPurchase() + " \n"
                    + " AND NOT EXISTS(SELECT inv1.id FROM finance.invoice inv1 WHERE inv1.deleted=FALSE AND inv1.differentinvoice_id = inv.id)\n";

        }

        String sql = "SELECT\n"
                + "        inv.id as invid,\n"
                + "        inv.is_purchase as invis_purchase,\n"
                + "        inv.account_id as invaccount_id,\n"
                + "        acc.name as accname,\n"
                + "        acc.title as acctitle,\n"
                + "        acc.is_person as accis_person,\n"
                + "        acc.is_employee AS accis_employee,\n"
                + "        acc.balance as accbalance,\n"
                + "        inv.documentnumber_id as invdocumentnumber_id,\n"
                + "        inv.documentserial as invdocumentserial,\n"
                + "        inv.documentnumber as invdocumentnumber,\n"
                + "        inv.invoicedate as invinvoicedate,\n"
                + "        inv.totalmoney AS invtotalmoney,\n"
                + "        inv.currency_id AS invcurrency_id\n"
                + "      FROM  finance.invoice inv  \n"
                + "      INNER JOIN general.account acc   ON (acc.id=inv.account_id)\n"
                + "      WHERE inv.deleted = false \n" + where
                + "ORDER BY inv.id DESC\n"
                + " limit " + pageSize + " offset " + first;

        return getJdbcTemplate().query(sql, new InvoiceMapper());
    }

    @Override
    public int invoiceBookCount(String where, String type, List<Object> param) {

        if (type.equals("differencepriceinvoice")) {
            where = where + " AND inv.account_id = " + ((Invoice) param.get(0)).getAccount().getId() + " AND inv.type_id <> 26\n"
                    + " AND inv.branch_id = " + ((Invoice) param.get(0)).getBranchSetting().getBranch().getId() + "\n"
                    + " AND inv.is_purchase = " + ((Invoice) param.get(0)).isIsPurchase() + " \n"
                    + " AND NOT EXISTS(SELECT inv1.id FROM finance.invoice inv1 WHERE inv1.deleted=FALSE AND inv1.differentinvoice_id = inv.id)\n";
        }

        String sql = "SELECT\n"
                + "   COUNT(inv.id) AS invid \n"
                + "FROM  finance.invoice inv  \n"
                + "INNER JOIN general.account acc   ON (acc.id=inv.account_id)\n"
                + "WHERE inv.deleted = false \n" + where;

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public int controlTankItemAvailable(String warehouseList, Stock stock) {

        String sql = "SELECT CASE WHEN EXISTS (SELECT wi.stock_id FROM inventory.warehouseitem wi WHERE wi.deleted=FALSE AND \n"
                + "wi.stock_id=? and wi.warehouse_id IN (" + warehouseList + ")) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{stock.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int controlAutomationWarehouse(String warehouseList) {

        String sql = "SELECT CASE WHEN EXISTS (SELECT wr.id FROM inventory.warehouse wr WHERE wr.deleted=FALSE AND \n"
                + " wr.id IN (" + warehouseList + ") AND wr.is_fuel = TRUE) THEN 1 ELSE 0 END";

        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createLogSapSaleInvoice(IntegrationForSap obj, Invoice invoice) {
        String sql = "SELECT r_sap_id FROM integration.process_sap_logsaleinvoice (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{invoice.getBranchSetting().getBranch().getId(), invoice.getId(), 3, obj.getJsonData(), obj.isIsSend(), obj.getSendDate(), obj.getResponse(), obj.getSapDocumentNumber(), obj.getSapIDocNo(), sessionBean.getUser().getId()};
        try {
            int i = 0;
            i = getJdbcTemplate().queryForObject(sql, param, Integer.class);
            return i;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateLogSap(Invoice invoice) {
        String sql = "UPDATE \n"
                + "      	integration.sap_purchaseinvoice\n"
                + "    SET\n"
                + "        is_send   		= FALSE,\n"
                + "        senddate  		= NULL,\n"
                + "        sendcount 		= NULL,\n"
                + "        response 		= NULL,\n"
                + "        invoicenumber	= NULL,\n"
                + "        sessionnumber        = NULL,\n"
                + "        is_sendwaybill       = FALSE,\n"
                + "        u_id	  		= ?,\n"
                + "        u_time	        = NOW()\n"
                + "    WHERE \n"
                + "        object_id = ? AND branch_id = ? AND type_id IN(1,3);\n";
        Object[] param = new Object[]{sessionBean.getUser().getId(), invoice.getId(), invoice.getBranchSetting().getBranch().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    //Sapye başarılı olarak gönderilip kilitlenen faturada güncelleme yapabilmek için düzenlemeyi aç butonuna tıklanınca history ye kayıt atıldı.
    @Override
    public int insertHistory(Invoice invoice) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String sql = "INSERT INTO\n"
                + " general.history(action, tablename, processdate, db_username, userdata_id, row_id, oldvalue, newvalue, itemvalue, columnname, columntype, fk_tablename, fk_oldvalue, fk_newvalue, branch_id) \n"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id ;";
        Object[] param = new Object[]{"U", "finance.invoice", calendar.getTime(), "postgres", sessionBean.getUser().getId(), invoice.getId(), "f", "t", null, "lock", "boolean", null, null, null, invoice.getBranchSetting().getBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            System.out.println("------e getmessage***" + e.getMessage());
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createInvoiceForOrder(Invoice invoice, String invoiceItems) {
        String sql = "SELECT r_invoice_id FROM finance.create_invoice_fororder(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";

        Object[] param = new Object[]{
            0,
            invoice.getId(),
            invoice.getBranchSetting().getBranch().getId(),
            sessionBean.getUser().getId(),
            invoice.isIsPurchase(),
            invoice.isIsPeriodInvoice(),
            invoice.getAccount().getId(),
            invoice.getdNumber().getId() == 0 ? null : invoice.getdNumber().getId(),
            invoice.getDocumentSerial(),
            invoice.getDocumentNumber(),
            new Timestamp(invoice.getInvoiceDate().getTime()),
            new Timestamp(invoice.getDueDate().getTime()),
            new Timestamp(invoice.getDispatchDate().getTime()),
            invoice.getDispatchAddress(),
            invoice.getDescription(),
            invoice.getStatus().getId(),
            invoice.getType().getId(),
            invoice.isIsDiscountRate(),
            invoice.getDiscountRate() == null ? 0 : invoice.getDiscountRate(),
            invoice.getDiscountPrice() == null ? 0 : invoice.getDiscountPrice(),
            invoice.getRoundingPrice() == null ? 0 : invoice.getRoundingPrice(),
            invoice.getCurrency().getId(),
            invoice.getExchangeRate(),
            invoice.getDeliveryPerson() == null ? null : invoice.getDeliveryPerson(),
            invoice.getJsonWarehouses(),
            invoice.getTaxPayerTypeId(),
            invoice.getDeliveryTypeId(),
            invoice.getInvoiceScenarioId(),
            80,
            (invoice.getTotalDiscount() == null || invoice.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82,
            null,
            invoice.isIsEInvoice(),
            invoice.getPriceDifferenceInvoice().getId() == 0 ? null : invoice.getPriceDifferenceInvoice().getId(),
            invoice.isIsDifferenceDirection(),
            invoice.isIsWait(),
            invoice.getWaitInvoiceItemJson(),
            invoice.getOrderIds(),
            invoiceItems
        };
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int findSaleForInvoice(BranchSetting branchSetting, int invoiceId, boolean isDelete) {
        String whereinv = "";
        String wheresl = "";
        
        if(isDelete){
        whereinv=" AND inv.deleted = TRUE ";
        wheresl=" sl.deleted = TRUE ";
        
        }else{
         whereinv=" AND inv.deleted = FALSE ";
        wheresl=" sl.deleted = FALSE ";
        }
        
        
        String sql = "SELECT \n"
                + "sl.id \n"
                + "FROM general.sale sl\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id "+ whereinv+") \n"
                + "WHERE "+wheresl+" AND sl.branch_id = ? AND inv.id = ? AND inv.branch_id = ?\n"
                + "Order By sl.id ASC LIMIT 1";

        Object[] param = {branchSetting.getBranch().getId(), invoiceId, branchSetting.getBranch().getId()};
        int saleid = getJdbcTemplate().queryForObject(sql, param, Integer.class);

        return saleid;
    }

    //Faturanın paroya gönderilmesini sağlar.
    @Override
    public int createParoSales(int saleId) {
        String sql = "SELECT * FROM log.process_createorcancelparosales(?, ?, ?);";
        Object[] param = new Object[]{1, saleId, true};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
