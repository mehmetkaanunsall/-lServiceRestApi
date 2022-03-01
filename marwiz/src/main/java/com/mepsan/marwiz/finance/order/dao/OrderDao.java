/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class OrderDao extends JdbcDaoSupport implements IOrderDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Order> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "od.u_time";
            sortOrder = "desc";
        }else if(sortField.equals("remainingQuantity")){
            sortField = "COALESCE((Select sum(odi.remainingquantity) FROM finance.orderitem odi where odi.order_id = od.id AND odi.deleted = FALSE),0)";
        }

        String sql = "select\n"
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
                  + "COALESCE((Select sum(odi.remainingquantity) FROM finance.orderitem odi where odi.order_id = od.id AND odi.deleted = FALSE),0) AS odiremainingquantity \n"
                  + "from finance.order od\n"
                  + "INNER JOIN general.account acc   ON (acc.id=od.account_id)  \n"
                  + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = od.status_id AND sttd.language_id = ?)    \n"
                  + "LEFT JOIN general.userdata usd ON(usd.id=od.c_id)\n"
                  + "INNER JOIN general.branchsetting brs ON (brs.branch_id = od.branch_id AND brs.deleted = FALSE)  \n"
                  + "INNER JOIN general.branch br ON (br.id = od.branch_id AND br.deleted = FALSE) \n"
                  + "      WHERE od.deleted = false \n" + where
                  + " ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, params, new OrderMapper());
    }

    @Override
    public int count(String where) {
        String sql = "select\n"
                  + "count(od.id)\n"
                  + "from finance.order od\n"
                  + "INNER JOIN general.account acc   ON (acc.id=od.account_id)  \n"
                  + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = od.status_id AND sttd.language_id = ?)    \n"
                  + "LEFT JOIN general.userdata usd ON(usd.id=od.c_id)\n"
                  + "INNER JOIN general.branchsetting brs ON (brs.branch_id = od.branch_id AND brs.deleted = FALSE)  \n"
                  + "INNER JOIN general.branch br ON (br.id = od.branch_id AND br.deleted = FALSE) \n"
                  + "WHERE od.deleted = false " + where;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

    @Override
    public int create(Order obj) {
        String sql = "SELECT r_order_id FROM finance.process_order(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{
            0,
            obj.getId(),
            obj.getAccount().getId(),
            100,
            null,
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(),
            obj.getDocumentSerial(),
            obj.getDocumentNumber(),
            new Timestamp(obj.getOrderDate().getTime()),
            obj.getStatus().getId(),
            obj.getBranchSetting().getBranch().getId(),
            sessionBean.getUser().getId()
        };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Order obj) {
        String sql = "SELECT r_order_id FROM finance.process_order(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{
            1,
            obj.getId(),
            obj.getAccount().getId(),
            100,
            null,
            obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(),
            obj.getDocumentSerial(),
            obj.getDocumentNumber(),
            new Timestamp(obj.getOrderDate().getTime()),
            obj.getStatus().getId(),
            obj.getBranchSetting().getBranch().getId(),
            sessionBean.getUser().getId()
        };

        System.out.println("--param---" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String exportData(Order order) {
        String sql = "select\n"
                  + "odi.id as odiid,\n"
                  + "odi.stock_id as odistockid,\n"
                  + "stck.name as stckname,\n"
                  + "stck.barcode as stckbarcode,\n"
                  + "stck.code as stckcode,\n"
                  + "odi.unit_id as odiunitid,\n"
                  + "gunt.name as guntname,\n"
                  + "gunt.sortname as guntsortname,\n"
                  + "gunt.unitrounding as guntunitrounding,\n"
                  + "odi.quantity as odiquantity\n"
                  + "FROM finance.orderitem odi \n"
                  + "LEFT JOIN inventory.stock stck  ON(stck.id=odi.stock_id)\n"
                  + "LEFT JOIN general.unit gunt  ON(gunt.id=odi.unit_id)\n"
                  + "WHERE odi.deleted = false and odi.order_id=" + order.getId() + " \n"
                  + " ORDER BY  odi.id   \n";
        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public int updateStatus(Order order) {
        String sql = "UPDATE finance.order \n"
                  + "   SET status_id = ? , u_id = ?, u_time = NOW() \n"
                  + "   WHERE id = ?";
        Object[] param = new Object[]{order.getStatus().getId(), sessionBean.getUser().getId(), order.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int sendOrderCenter(Order order) {
        String sql;
        Object[] param;

        sql = "SELECT log.insertjson_order(?,?,?);";
        param = new Object[]{order.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Order order) {
        String sql = "SELECT r_order_id FROM finance.process_order(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{
            2,
            order.getId(),
            order.getAccount().getId(),
            100,
            null,
            order.getdNumber().getId() == 0 ? null : order.getdNumber().getId(),
            order.getDocumentSerial(),
            order.getDocumentNumber(),
            new Timestamp(order.getOrderDate().getTime()),
            order.getStatus().getId(),
            order.getBranchSetting().getBranch().getId(),
            sessionBean.getUser().getId()
        };
        System.out.println("--param---" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public CheckDelete testBeforeDelete(Order order) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {14, order.getId()};
        try {
            List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
            return result.get(0);
        } catch (Exception e) {
            return null;
        }
    }

}
