/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class OrderItemDao extends JdbcDaoSupport implements IOrderItemDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<OrderItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "odi.u_time";
            sortOrder = "desc";
        }

        String sql = "select\n"
                + "odi.id as odiid,\n"
                + "odi.stock_id as odistockid,\n"
                + "stck.name as stckname,\n"
                + "stck.barcode as stckbarcode,\n"
                + "odi.unit_id as odiunitid,\n"
                + "gunt.name as guntname,\n"
                + "gunt.sortname as guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "COALESCE(odi.boxquantity,0) as odiboxquantity,\n"
                + "COALESCE(odi.shelfquantity,0) as odishelfquantity,\n"
                + "CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 OR COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN 0 ELSE COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) END as requiredwarehousestockquantity,\n"
                + "CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 OR COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN ceil(0 + odi.shelfquantity) ELSE COALESCE(ceil((((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity) + odi.shelfquantity),0) END as totalstockquantity,\n"
                + "COALESCE(odi.warehousequantity,0) as odiwarehousequantity,\n"
                + "COALESCE(odi.twomonthsale) as oditwomonthsale,\n"
                + "COALESCE(CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 THEN 0\n"
                + "              ELSE COALESCE(odi.twomonthsale,0)/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END) END,0) as averageweeklyorderquantity,\n"
                + "COALESCE(odi.twomonthsaleactiveday,0) as oditwomonthsaleactiveday,\n"
                + "(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END) as averageweeklyorderquantityfordayscount,\n"
                + "CASE WHEN odi.twomonthsale = 0 THEN 0 ELSE COALESCE((odi.warehousequantity/odi.twomonthsale)*8,0) END as stockenoughdays,\n"
                + "CASE WHEN odi.boxquantity = 0 OR COALESCE(odi.twomonthsaleactiveday,0) = 0 THEN 0 ELSE CASE WHEN COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN  Round(((0 + odi.shelfquantity) - odi.warehousequantity) / odi.boxquantity, 0) * odi.boxquantity  ELSE Round(COALESCE(((((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity) + odi.shelfquantity) - odi.warehousequantity,0) / odi.boxquantity, 0) * odi.boxquantity  END END as ordercalculation,\n"
                + "COALESCE(odi.minimumquantity,0) as odiminimumquantity,\n"
                + "COALESCE(odi.maximumquantity,0) as odimaximumquantity,\n"
                + "COALESCE(odi.minfactorvalue,0) as odiminfactorvalue,\n"
                + "COALESCE(odi.maxfactorvalue,0) as odimaxfactorvalue,\n"
                + "COALESCE(odi.warehousestockdivisorvalue,0) as odiwarehousestockdivisorvalue,\n"
                + "COALESCE(odi.quantity,0) as odiquantity,\n"
                + "CASE WHEN od.status_id = 59 or od.status_id=61 THEN isi.currency_id  ELSE odi.currency_id END as odicurrency_id,\n"
                + "CASE WHEN od.status_id = 59 or od.status_id=61 THEN COALESCE(isi.purchaserecommendedprice,0) ELSE COALESCE(odi.recommendedprice,0) END as odirecommendedprice,\n"
                + "od.id as odid,\n"
                + "od.documentnumber_id as oddocumentnumber_id,  \n"
                + "od.documentserial as oddocumentserial,  \n"
                + "od.documentnumber as oddocumentnumber,  \n"
                + "od.orderdate as odorderdate,\n"
                + "od.status_id as odstatus_id,  \n"
                + "sttd.name as sttdname, \n"
                + "od.type_id as odtype_id,  \n"
                + "od.typeno as odtypeno, \n"
                + "od.account_id as odaccount_id,  \n"
                + "acc.name as accname,  \n"
                + "acc.title as acctitle,  \n"
                + "acc.is_person as accis_person,  \n"
                + "acc.is_employee AS accis_employee,  \n"
                + "acc.phone as accphone,  \n"
                + "acc.email as accemail,  \n"
                + "acc.address as accaddress,  \n"
                + "acc.taxno as acctaxno,  \n"
                + "acc.taxoffice as acctaxoffice,  \n"
                + "acc.balance as accbalance,  \n"
                + "acc.dueday as accdueday, \n"
                + "od.c_id as odc_id,  \n"
                + "usd.name AS  usname,  \n"
                + "usd.surname AS ussurname,  \n"
                + "usd.username AS ususername,  \n"
                + "od.c_time AS odc_time, \n"
                + "od.branch_id AS odbranch_id,\n"
                + "brs.is_centralintegration AS brsis_centralintegration,  \n"
                + "brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "br.currency_id AS brcurrency_id,  \n"
                + "br.name AS brname,  \n"
                + "br.is_agency AS bris_agency, \n"
                + "brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                + "odi.remainingquantity AS odiremainingquantity,\n"
                + "CASE WHEN (stck.c_time >= CURRENT_DATE - INTERVAL '3 months' AND (isi.balance =0 OR isi.balance IS NULL)) THEN TRUE ELSE FALSE END AS isnewstockcontrol\n"
                + "FROM finance.orderitem odi \n"
                + "INNER JOIN finance.order od ON (odi.order_id=od.id AND od.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt  ON(gunt.id=odi.unit_id)\n"
                + "LEFT JOIN inventory.stock stck  ON(stck.id=odi.stock_id)\n"
                + "INNER JOIN inventory.stockinfo isi ON(isi.stock_id=stck.id AND isi.branch_id = od.branch_id AND isi.deleted=FALSE)"
                + "INNER JOIN general.account acc   ON (acc.id=od.account_id)  \n"
                + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = od.status_id AND sttd.language_id = ?)    \n"
                + "LEFT JOIN general.userdata usd ON(usd.id=od.c_id)\n"
                + "INNER JOIN general.branchsetting brs ON (brs.branch_id = od.branch_id AND brs.deleted = FALSE)  \n"
                + "INNER JOIN general.branch br ON (br.id = od.branch_id AND br.deleted = FALSE) \n "
                + "      WHERE odi.deleted = false \n" + where
                + " ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, params, new OrderItemMapper());
    }

    @Override
    public int count(String where) {
        String sql = "select\n"
                + "count(odi.id)\n"
                + "FROM finance.orderitem odi \n"
                + "INNER JOIN finance.order od ON (odi.order_id=od.id AND od.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt  ON(gunt.id=odi.unit_id)\n"
                + "LEFT JOIN inventory.stock stck  ON(stck.id=odi.stock_id)\n"
                + "INNER JOIN general.account acc   ON (acc.id=od.account_id)  \n"
                + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = od.status_id AND sttd.language_id = ?)    \n"
                + "LEFT JOIN general.userdata usd ON(usd.id=od.c_id)\n"
                + "INNER JOIN general.branchsetting brs ON (brs.branch_id = od.branch_id AND brs.deleted = FALSE)  \n"
                + "INNER JOIN general.branch br ON (br.id = od.branch_id AND br.deleted = FALSE) \n "
                + "WHERE odi.deleted = false " + where;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

    @Override
    public int create(OrderItem obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public int update(OrderItem obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OrderItem> findAllAccordingToOrder(int processType, Order order) {

        String sql = " SELECT * FROM finance.list_orderstock(?,?,?,?,?);";

        Object[] param = {processType, order.getAccount().getId(), 0, order.getBranchSetting().getBranch().getId(), order.getId()};
        System.out.println("--param---" + Arrays.toString(param));
        List<OrderItem> result = getJdbcTemplate().query(sql, param, new OrderItemMapper());
        return result;

    }

    @Override
    public int createAll(Order obj) {
        String sql = "SELECT r_orderitem_id FROM finance.process_orderitem(?, ?, ?, ?);";

        Object[] param = new Object[]{0, obj.getId(), sessionBean.getUser().getId(), obj.getJsonItems()};
        System.out.println("--param---" + Arrays.toString(param));

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateAll(Order obj) {
        String sql = "SELECT r_orderitem_id FROM finance.process_orderitem(?, ?, ?, ?);";

        Object[] param = new Object[]{1, obj.getId(), sessionBean.getUser().getId(), obj.getJsonItems()};
        System.out.println("--param---" + Arrays.toString(param));

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<OrderItem> listOrderItemForCreateInvoice(String where) {
        String sql = "select\n"
                + "odi.id as odiid,\n"
                + "odi.stock_id as odistockid,\n"
                + "stck.name as stckname,\n"
                + "stck.barcode as stckbarcode,\n"
                + "stck.centerproductcode as centerproductcode,\n"
                + "odi.unit_id as odiunitid,\n"
                + "gunt.name as guntname,\n"
                + "gunt.sortname as guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "COALESCE(odi.boxquantity,0) as odiboxquantity,\n"
                + "COALESCE(odi.shelfquantity,0) as odishelfquantity,\n"
                + "CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 OR COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN 0 ELSE COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) END as requiredwarehousestockquantity,\n"
                + "CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 OR COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN ceil(0 + odi.shelfquantity) ELSE COALESCE(ceil((((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity) + odi.shelfquantity),0) END as totalstockquantity,\n"
                + "COALESCE(odi.warehousequantity,0) as odiwarehousequantity,\n"
                + "COALESCE(odi.twomonthsale) as oditwomonthsale,\n"
                + "COALESCE(CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 THEN 0\n"
                + "              ELSE COALESCE(odi.twomonthsale,0)/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END) END,0) as averageweeklyorderquantity,\n"
                + "COALESCE(odi.twomonthsaleactiveday,0) as oditwomonthsaleactiveday,\n"
                + "(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END) as averageweeklyorderquantityfordayscount,\n"
                + "CASE WHEN odi.twomonthsale = 0 THEN 0 ELSE COALESCE((odi.warehousequantity/odi.twomonthsale)*8,0) END as stockenoughdays,\n"
                + "CASE WHEN odi.boxquantity = 0 OR  COALESCE(odi.twomonthsaleactiveday,0) = 0 THEN 0 ELSE CASE WHEN COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN  Round(((0 + odi.shelfquantity) - odi.warehousequantity) / odi.boxquantity, 0) * odi.boxquantity  ELSE Round(COALESCE(((((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END))/1)-odi.shelfquantity) + odi.shelfquantity) - odi.warehousequantity,0) / odi.boxquantity, 0) * odi.boxquantity  END END as ordercalculation,\n"
                + "COALESCE(odi.minimumquantity,0) as odiminimumquantity,\n"
                + "COALESCE(odi.maximumquantity,0) as odimaximumquantity,\n"
                + "COALESCE(odi.minfactorvalue,0) as odiminfactorvalue,\n"
                + "COALESCE(odi.maxfactorvalue,0) as odimaxfactorvalue,\n"
                + "COALESCE(odi.warehousestockdivisorvalue,0) as odiwarehousestockdivisorvalue,\n"
                + "COALESCE(odi.quantity,0) as odiquantity,\n"
                + "CASE WHEN od.status_id = 59 or od.status_id = 61 THEN si.currency_id  ELSE odi.currency_id END as odicurrency_id,\n"
                + "CASE WHEN od.status_id = 59 or od.status_id = 61 THEN COALESCE(si.purchaserecommendedprice,0) ELSE COALESCE(odi.recommendedprice,0) END as odirecommendedprice,\n"
                + "COALESCE(odi.remainingquantity,0) as odiremainingquantity,\n"
                + "od.id as odid,\n"
                + "od.documentnumber_id as oddocumentnumber_id,  \n"
                + "od.documentserial as oddocumentserial,  \n"
                + "od.documentnumber as oddocumentnumber,  \n"
                + "od.orderdate as odorderdate,\n"
                + "od.status_id as odstatus_id,  \n"
                + "sttd.name as sttdname, \n"
                + "od.type_id as odtype_id,  \n"
                + "od.typeno as odtypeno, \n"
                + "od.account_id as odaccount_id,  \n"
                + "acc.name as accname,  \n"
                + "acc.title as acctitle,  \n"
                + "acc.is_person as accis_person,  \n"
                + "acc.is_employee AS accis_employee,  \n"
                + "acc.phone as accphone,  \n"
                + "acc.email as accemail,  \n"
                + "acc.address as accaddress,  \n"
                + "acc.taxno as acctaxno,  \n"
                + "acc.taxoffice as acctaxoffice,  \n"
                + "acc.balance as accbalance,  \n"
                + "acc.dueday as accdueday, \n"
                + "od.c_id as odc_id,  \n"
                + "usd.name AS  usname,  \n"
                + "usd.surname AS ussurname,  \n"
                + "usd.username AS ususername,  \n"
                + "od.c_time AS odc_time, \n"
                + "COALESCE(si.currentsaleprice,0) as sicurrentsaleprice,\n"
                + "si.currentsalecurrency_id as sicurrentsalecurrency_id,\n"
                + "od.branch_id AS odbranch_id,\n"
                + "brs.is_centralintegration AS brsis_centralintegration,  \n"
                + "brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "br.currency_id AS brcurrency_id,  \n"
                + "br.name AS brname,  \n"
                + "br.is_agency AS bris_agency, \n"
                + "brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                + "COALESCE(ptg.rate,0) purchasekdv\n"
                + "FROM finance.orderitem odi \n"
                + "INNER JOIN finance.order od ON (odi.order_id=od.id AND od.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt  ON(gunt.id=odi.unit_id)\n"
                + "LEFT JOIN inventory.stock stck  ON(stck.id=odi.stock_id)\n"
                + "INNER JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.branch_id = od.branch_id AND si.deleted=FALSE)"
                + "INNER JOIN general.account acc   ON (acc.id=od.account_id)  \n"
                + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = od.status_id AND sttd.language_id = ?)    \n"
                + "LEFT JOIN general.userdata usd ON(usd.id=od.c_id)\n"
                + "INNER JOIN general.branchsetting brs ON (brs.branch_id = od.branch_id AND brs.deleted = FALSE)  \n"
                + "INNER JOIN general.branch br ON (br.id = od.branch_id AND br.deleted = FALSE) \n "
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "WHERE odi.deleted = false and odi.remainingquantity <>0 \n" + where;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, params, new OrderItemMapper());
    }

    @Override
    public List<OrderItem> findAllNotLazy(String where) {

        String sql = "select\n"
                + "odi.id as odiid,\n"
                + "odi.stock_id as odistockid,\n"
                + "stck.name as stckname,\n"
                + "stck.barcode as stckbarcode,\n"
                + "odi.unit_id as odiunitid,\n"
                + "gunt.name as guntname,\n"
                + "gunt.sortname as guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "COALESCE(odi.boxquantity,0) as odiboxquantity,\n"
                + "COALESCE(odi.shelfquantity,0) as odishelfquantity,\n"
                + "CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 OR COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN 0 ELSE COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END))/1)-odi.shelfquantity,0) END as requiredwarehousestockquantity,\n"
                + "CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 OR COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN ceil(0 + odi.shelfquantity) ELSE COALESCE(ceil((((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END))/1)-odi.shelfquantity) + odi.shelfquantity),0) END as totalstockquantity,\n"
                + "COALESCE(odi.warehousequantity,0) as odiwarehousequantity,\n"
                + "COALESCE(odi.twomonthsale) as oditwomonthsale,\n"
                + "COALESCE(CASE WHEN COALESCE(odi.twomonthsaleactiveday,0) = 0 THEN 0\n"
                + "              ELSE COALESCE(odi.twomonthsale,0)/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "                                          WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "                                          ELSE 0 END) END,0) as averageweeklyorderquantity,\n"
                + "COALESCE(odi.twomonthsaleactiveday,0) as oditwomonthsaleactiveday,\n"
                + "(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END) as averageweeklyorderquantityfordayscount,\n"
                + "CASE WHEN odi.twomonthsale = 0 THEN 0 ELSE COALESCE((odi.warehousequantity/odi.twomonthsale)*8,0) END as stockenoughdays,\n"
                + "CASE WHEN odi.boxquantity = 0 OR COALESCE(odi.twomonthsaleactiveday,0) = 0 THEN 0 ELSE CASE WHEN COALESCE(((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END))/1)-odi.shelfquantity,0) < 0 THEN  Round(((0 + odi.shelfquantity) - odi.warehousequantity) / odi.boxquantity, 0) * odi.boxquantity  ELSE Round(COALESCE(((((odi.twomonthsale/(CASE WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 45 THEN 8\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 15 AND COALESCE(odi.twomonthsaleactiveday,0) < 45 THEN 5\n"
                + "      WHEN COALESCE(odi.twomonthsaleactiveday, 0) >= 1  AND COALESCE(odi.twomonthsaleactiveday,0) < 15 THEN 3\n"
                + "      ELSE 0 END))/1)-odi.shelfquantity) + odi.shelfquantity) - odi.warehousequantity,0) / odi.boxquantity, 0) * odi.boxquantity  END END as ordercalculation,\n"
                + "COALESCE(odi.minimumquantity,0) as odiminimumquantity,\n"
                + "COALESCE(odi.maximumquantity,0) as odimaximumquantity,\n"
                + "COALESCE(odi.minfactorvalue,0) as odiminfactorvalue,\n"
                + "COALESCE(odi.maxfactorvalue,0) as odimaxfactorvalue,\n"
                + "COALESCE(odi.warehousestockdivisorvalue,0) as odiwarehousestockdivisorvalue,\n"
                + "COALESCE(odi.quantity,0) as odiquantity,\n"
                + "CASE WHEN od.status_id = 59 or od.status_id=61 THEN isi.currency_id  ELSE odi.currency_id END as odicurrency_id,\n"
                + "CASE WHEN od.status_id = 59 or od.status_id=61 THEN COALESCE(isi.purchaserecommendedprice,0) ELSE COALESCE(odi.recommendedprice,0) END as odirecommendedprice,\n"
                + "od.id as odid,\n"
                + "od.documentnumber_id as oddocumentnumber_id,  \n"
                + "od.documentserial as oddocumentserial,  \n"
                + "od.documentnumber as oddocumentnumber,  \n"
                + "od.orderdate as odorderdate,\n"
                + "od.status_id as odstatus_id,  \n"
                + "sttd.name as sttdname, \n"
                + "od.type_id as odtype_id,  \n"
                + "od.typeno as odtypeno, \n"
                + "od.account_id as odaccount_id,  \n"
                + "acc.name as accname,  \n"
                + "acc.title as acctitle,  \n"
                + "acc.is_person as accis_person,  \n"
                + "acc.is_employee AS accis_employee,  \n"
                + "acc.phone as accphone,  \n"
                + "acc.email as accemail,  \n"
                + "acc.address as accaddress,  \n"
                + "acc.taxno as acctaxno,  \n"
                + "acc.taxoffice as acctaxoffice,  \n"
                + "acc.balance as accbalance,  \n"
                + "acc.dueday as accdueday, \n"
                + "od.c_id as odc_id,  \n"
                + "usd.name AS  usname,  \n"
                + "usd.surname AS ussurname,  \n"
                + "usd.username AS ususername,  \n"
                + "od.c_time AS odc_time, \n"
                + "od.branch_id AS odbranch_id,\n"
                + "brs.is_centralintegration AS brsis_centralintegration,  \n"
                + "brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "br.currency_id AS brcurrency_id,  \n"
                + "br.name AS brname,  \n"
                + "br.is_agency AS bris_agency, \n"
                + "brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount\n"
                + "FROM finance.orderitem odi \n"
                + "INNER JOIN finance.order od ON (odi.order_id=od.id AND od.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt  ON(gunt.id=odi.unit_id)\n"
                + "LEFT JOIN inventory.stock stck  ON(stck.id=odi.stock_id)\n"
                + "INNER JOIN inventory.stockinfo isi ON(isi.stock_id=stck.id AND isi.branch_id = od.branch_id AND isi.deleted=FALSE)"
                + "INNER JOIN general.account acc   ON (acc.id=od.account_id)  \n"
                + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = od.status_id AND sttd.language_id = ?)    \n"
                + "LEFT JOIN general.userdata usd ON(usd.id=od.c_id)\n"
                + "INNER JOIN general.branchsetting brs ON (brs.branch_id = od.branch_id AND brs.deleted = FALSE)  \n"
                + "INNER JOIN general.branch br ON (br.id = od.branch_id AND br.deleted = FALSE) \n "
                + "      WHERE odi.deleted = false \n" + where;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, params, new OrderItemMapper());
    }

    @Override
    public int saveDescription(OrderItem orderItem) {
        String sql = "\n"
                + "    UPDATE \n"
                + "  finance.orderitem  \n"
                + "SET \n"
                + "  description = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "  \n"
                + "WHERE \n"
                + "  order_id = ? and stock_id=?";

        Object[] param = new Object[]{orderItem.getDescription(), sessionBean.getUser().getId(), orderItem.getOrder().getId(), orderItem.getStock().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public List<OrderItem> findAllAccordingToStock(int processType, OrderItem orderItem) {

        String sql = " SELECT * FROM finance.list_orderstock(?,?,?,?,?);";

        Object[] param = {processType, orderItem.getOrder().getAccount().getId(), orderItem.getStock().getId(), orderItem.getOrder().getBranchSetting().getBranch().getId(), orderItem.getOrder().getId()};
        System.out.println("--param---" + Arrays.toString(param));
        List<OrderItem> result = getJdbcTemplate().query(sql, param, new OrderItemMapper());
        return result;
    }

}
