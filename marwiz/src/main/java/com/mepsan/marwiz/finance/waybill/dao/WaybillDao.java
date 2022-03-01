/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WaybillDao extends JdbcDaoSupport implements IWaybillDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Waybill> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String column = "";
        if (sortField == null) {
            sortField = "wb.id";
            sortOrder = "desc";
        }
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranchSetting().getErpIntegrationId() == 1) {
            column = "  (SELECT \n"
                      + "        spi.is_send AS is_send\n"
                      + "      FROM integration.sap_purchaseinvoice spi \n"
                      + "      WHERE spi.deleted =FALSE AND spi.type_id = 2 AND spi.object_id = wb.id AND spi.branch_id = wb.branch_id\n"
                      + "       ORDER BY spi.c_time ASC LIMIT 1) AS spinvis_send,\n";
        }

        String sql = "SELECT\n"
                  + "    wb.id as wbid,  \n"
                  + "    wb.account_id as wbaccount_id,   \n"
                  + "    wb.waybilldate as wbwaybilldate,  \n"
                  + "    wb.documentnumber as wbdocumentnumber,  \n"
                  + "    wb.documentnumber_id as wbdocumentnumber_id,\n"
                  + "    wb.documentserial as wbdocumentserial,\n"
                  + "    wb.deliveryperson as wbdeliveryperson,  \n"
                  + "    wb.dispatchdate as wbdispatchdate,  \n"
                  + "    wb.dispatchaddress as wbdispatchaddress,\n"
                  + "    wb.description as wbdescription,\n"
                  + "    wb.is_purchase as wbis_purchase,\n"
                  + "    wb.type_id as wbtype_id,\n"
                  + "    typd.name as typdname,\n"
                  + "    wb.status_id as wbstatus_id,\n"
                  + "    sttd.name as sttdname,\n"
                  + "    acc.is_person as accis_person,\n"
                  + "    acc.name AS accname,\n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    acc.phone as accphone,\n"
                  + "    acc.email as accemail,\n"
                  + "    acc.address as accaddress,\n"
                  + "    acc.taxno as acctaxno,\n"
                  + "    acc.taxoffice as acctaxoffice,\n"
                  + "    acc.balance as accbalance,\n"
                  + "    acc.dueday as accdueday,\n"
                  + "    (SELECT \n"
                  + "       		STRING_AGG(CAST(COALESCE(iw.id,0) as varchar),',') \n"
                  + "       		FROM\n"
                  + "       			finance.waybill_warehousereceipt_con wwc\n"
                  + "            LEFT JOIN inventory.warehousereceipt wr ON(wr.id=wwc.warehousereceipt_id AND wr.deleted=FALSE)\n"
                  + "            LEFT JOIN inventory.warehouse iw ON(iw.id=wr.warehouse_id)\n"
                  + "            WHERE wwc.deleted=FALSE AND wwc.waybill_id = wb.id\n"
                  + "    ) as warehouseids,\n"
                  + "    (SELECT \n"
                  + "       		COALESCE(STRING_AGG(COALESCE(iw.name,''),','),'') \n"
                  + "       		FROM\n"
                  + "       			finance.waybill_warehousereceipt_con wwc\n"
                  + "            LEFT JOIN inventory.warehousereceipt wr ON(wr.id=wwc.warehousereceipt_id AND wr.deleted=FALSE)\n"
                  + "            LEFT JOIN inventory.warehouse iw ON(iw.id=wr.warehouse_id)\n"
                  + "            WHERE wwc.deleted=FALSE AND wwc.waybill_id = wb.id\n"
                  + "    ) as warehousenames,\n"
                  + "    CASE WHEN EXISTS( SELECT \n"
                  + "                         inv.id \n"
                  + "          		 FROM finance.waybill_invoice_con wic\n"
                  + "          		 INNER JOIN finance.invoice inv ON (inv.id = wic.invoice_id AND inv.deleted = FALSE)\n"
                  + "                      WHERE wic.waybill_id = wb.id \n"
                  + "                      AND wic.deleted = FALSE\n"
                  + "          		) THEN true \n"
                  + "          ELSE false END as isinvoice,\n"
                  + "      CASE WHEN EXISTS\n"
                  + "       (SELECT \n"
                  + "           inv.type_id\n"
                  + "       FROM finance.waybill_invoice_con wic\n"
                  + "       INNER JOIN finance.invoice inv ON (inv.id = wic.invoice_id AND inv.deleted = FALSE)\n"
                  + "       WHERE wic.waybill_id =  wb.id \n"
                  + "       AND wic.deleted = FALSE AND inv.type_id = 59) THEN TRUE ELSE FALSE END AS iswaybillinvoice,\n"
                  + "      CASE WHEN EXISTS\n"
                  + "       (SELECT \n"
                  + "           fo.id\n"
                  + "       FROM finance.order_waybill_con owc\n"
                  + "       INNER JOIN finance.order fo ON (fo.id = owc.order_id AND fo.deleted = FALSE)\n"
                  + "       WHERE owc.waybill_id =  wb.id \n"
                  + "       AND owc.deleted = FALSE) THEN TRUE ELSE FALSE END AS isorderconnection,\n"
                  + "       wb.branch_id AS wbbranch_id,\n"
                  + "       brs.is_centralintegration AS brsis_centralintegration,\n"
                  + "       brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                  + "       br.currency_id AS brcurrency_id,\n"
                  + "       br.name AS brname,\n"
                  + column
                  + "       br.is_agency AS bris_agency,\n"
                  + "       wb.is_fuel AS wbis_fuel,\n"
                  + "       brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount\n"
                  + "FROM \n"
                  + "    finance.waybill wb  \n"
                  + "    INNER JOIN general.account acc ON (acc.id = wb.account_id) \n"
                  + "    INNER JOIN system.type_dict typd ON (typd.type_id = wb.type_id AND typd.language_id = ?)\n"
                  + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = wb.status_id AND sttd.language_id = ?)\n"
                  + "    INNER JOIN general.branchsetting brs ON (brs.branch_id = wb.branch_id AND brs.deleted = FALSE)\n"
                  + "    INNER JOIN general.branch br ON (br.id = wb.branch_id AND br.deleted = FALSE)\n"
                  + "WHERE    \n"
                  + "  wb.deleted = FALSE " + where + "\n"
                  + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + "LIMIT " + pageSize + " OFFSET " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        List<Waybill> result = getJdbcTemplate().query(sql, param, new WaybillMapper());
        return result;
    }

    @Override
    public int count(String where) {
        String sql = "SELECT\n"
                  + "    COUNT(wb.id) \n"
                  + "FROM \n"
                  + "    finance.waybill wb  \n"
                  + "    INNER JOIN general.account acc ON (acc.id = wb.account_id)   \n"
                  + "    INNER JOIN system.type_dict typd ON (typd.type_id = wb.type_id AND typd.language_id = ?)\n"
                  + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = wb.status_id AND sttd.language_id = ?)\n"
                  + "    INNER JOIN general.branchsetting brs ON (brs.branch_id = wb.branch_id AND brs.deleted = FALSE)\n"
                  + "    INNER JOIN general.branch br ON (br.id = wb.branch_id AND br.deleted = FALSE)\n"
                  + "WHERE    \n"
                  + "     wb.deleted = FALSE " + where + "\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        int result = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return result;
    }

    /**
     * Bu metot hem irsaliye ekleme hemde irsaliyeye ürün ekleme esnasında
     * kullanılır.
     *
     * fatura id bilgisi 0 ise irsaliye ekler fatura id bilgisi 0 dan büyük ise
     * ve json data(ürün) var ise ürün ekler
     *
     * @param obj
     * @return
     */
    @Override
    public int create(Waybill obj) {
        String sql = "SELECT r_waybill_id FROM finance.process_waybill(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{0, obj.getId(), sessionBean.getUser().getId(), obj.isIsPurchase(), obj.getAccount().getId(),
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(), obj.getDocumentSerial(), obj.getDocumentNumber(),
            new Timestamp(obj.getWaybillDate().getTime()), new Timestamp(obj.getDispatchDate().getTime()), obj.getDispatchAddress(), obj.getDescription(),
            obj.getStatus().getId(), obj.getType().getId(), obj.getDeliveryPerson(), obj.getJsonWarehouses(), obj.getBranchSetting().getBranch().getId(), null, obj.isIsFuel()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu metot hem irsaliye güncelleme hemde irsaliyeye ürün güncelleme
     * esnasında kullanılır.
     *
     * processtype 1 ise irsaliye günceller processtype 2 ise ürün günceller
     *
     * @param obj
     * @return
     */
    @Override
    public int update(Waybill obj) {
        String sql = "SELECT r_waybill_id FROM finance.process_waybill(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{1, obj.getId(), sessionBean.getUser().getId(), obj.isIsPurchase(), obj.getAccount().getId(),
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(), obj.getDocumentSerial(), obj.getDocumentNumber(),
            new Timestamp(obj.getWaybillDate().getTime()), new Timestamp(obj.getDispatchDate().getTime()), obj.getDispatchAddress(), obj.getDescription(),
            obj.getStatus().getId(), obj.getType().getId(), obj.getDeliveryPerson(), obj.getJsonWarehouses(), obj.getBranchSetting().getBranch().getId(), null, obj.isIsFuel()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public CheckDelete testBeforeDelete(Waybill waybill) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {5, waybill.getId()};
        try {
            List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
            return result.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int delete(Waybill obj) {
        String sql = "SELECT r_waybill_id FROM finance.process_waybill(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{2, obj.getId(), sessionBean.getUser().getId(), obj.isIsPurchase(), obj.getAccount().getId(),
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(), obj.getDocumentSerial(), obj.getDocumentNumber(),
            new Timestamp(obj.getWaybillDate().getTime()), new Timestamp(obj.getDispatchDate().getTime()), obj.getDispatchAddress(), obj.getDescription(),
            obj.getStatus().getId(), obj.getType().getId(), obj.getDeliveryPerson(), obj.getJsonWarehouses(), obj.getBranchSetting().getBranch().getId(), null, obj.isIsFuel()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateLogSap(Waybill waybill) {
        String sql = "UPDATE \n"
                  + "      	integration.sap_purchaseinvoice\n"
                  + "    SET\n"
                  + "        is_send   		= FALSE,\n"
                  + "        senddate  		= NULL,\n"
                  + "        sendcount 		= NULL,\n"
                  + "        response 		= NULL,\n"
                  + "        invoicenumber	= NULL,\n"
                  + "        sessionnumber        = NULL,\n"
                  + "        u_id	  		= ?,\n"
                  + "        u_time	        = NOW()\n"
                  + "    WHERE \n"
                  + "        object_id = ? AND branch_id = ? AND type_id = 2;\n";
        Object[] param = new Object[]{sessionBean.getUser().getId(), waybill.getId(), waybill.getBranchSetting().getBranch().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createWaybillForOrder(Waybill waybill, String waybillItems) {

        String sql = "SELECT r_waybill_id FROM finance.create_waybill_fororder(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{0, waybill.getId(), sessionBean.getUser().getId(), waybill.isIsPurchase(), waybill.getAccount().getId(),
            waybill.getdNumber().getId() == 0 ? null : waybill.getdNumber().getId(), waybill.getDocumentSerial(), waybill.getDocumentNumber(),
            new Timestamp(waybill.getWaybillDate().getTime()), new Timestamp(waybill.getDispatchDate().getTime()), waybill.getDispatchAddress(), waybill.getDescription(),
            waybill.getStatus().getId(), waybill.getType().getId(), waybill.getDeliveryPerson(), waybill.getJsonWarehouses(), waybill.getBranchSetting().getBranch().getId(),
            waybill.getOrderIds(), waybillItems};
        System.out.println("arrays" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
