/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   18.04.2018 12:20:13
 */
package com.mepsan.marwiz.inventory.stockrequest.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import com.mepsan.marwiz.service.stock.dao.SendStockRequestMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockRequestDao extends JdbcDaoSupport implements IStockRequestDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockRequest> findall() {
        String sql = "SELECT \n"
                + "       msrt.id as msrtid, \n"
                + "       msrt.name as msrtname, \n"
                + "       msrt.barcode as msrtbarcode, \n"
                + "       msrt.unit_id as msrtunit_id, \n"
                + "       gunt.name as msrtguntname, \n"
                + "       msrt.brand as msrtbrand, \n"
                + "       msrt.country_id as msrtcountry_id,\n"
                + "       msrt.is_service as msrtis_service, \n"
                + "       msrt.description as msrtdescription, \n"
                + "       msrt.saletaxgroup_id as msrtsaletaxgroup_id, \n"
                + "       msrt.purchasetaxgroup_id as msrtpurchasetaxgroup_id, \n"
                + "       msrt.approval as msrtapproval, \n"
                + "       msrt.approvaldate as msrtapprovaldate,\n"
                + "       msrt.approvalcenterstock_id as msrtapprovalcenterstock_id,\n"
                + "       msrt.approvalstock_id as msrtapprovalstock_id,\n"
                + "       msrt.price as msrtprice,\n"
                + "       msrt.currency_id as msrtcurrency_id,\n"
                + "       cryd.name as crydname,\n"
                + "       msrt.code AS msrtcode,\n"
                + "       msrt.weight as msrtweight,\n"
                + "       msrt.weightunit_id as msrtweightunit_id\n"
                + "       FROM inventory.stockrequest msrt \n"
                + "       LEFT JOIN general.unit gunt ON(gunt.id = msrt.unit_id AND gunt.deleted = FALSE)  \n"
                + "       LEFT JOIN system.currency_dict cryd ON(cryd.currency_id=msrt.currency_id AND cryd.language_id = ?) \n"
                + "       WHERE msrt.deleted=FALSE AND msrt.branch_id = ? \n"
                + "       ORDER BY msrt.approval, msrt.id DESC, msrt.approvaldate DESC;";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<StockRequest> result = getJdbcTemplate().query(sql, param, new StockRequestMapper());
        return result;
    }

    @Override
    public int create(StockRequest obj) {

        String sql = " SELECT r_request_id FROM inventory.process_stockrequest(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{1, obj.getId(), false, obj.getBarcode(), obj.getName(), obj.getUnit().getId(), obj.getBrand(), obj.getCountry().getId() != 0 ? obj.getCountry().getId() : null, obj.getPrice(), obj.getCurrency().getId(), obj.isIsService(), obj.getDescription(),
            obj.getSaleTaxGroup().getId(), obj.getPurchaseTaxGroup().getId(), sessionBean.getUser().getId(), obj.getCode(), obj.getWeight(), obj.getWeightUnit().getId(), sessionBean.getUser().getLastBranch().getId()};
        try {
            System.out.println("--param---" + Arrays.toString(param));
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StockRequest obj, boolean isSend, int type) {
        String sql = " SELECT r_request_id FROM inventory.process_stockrequest(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{type, obj.getId(), isSend, obj.getBarcode(), obj.getName(), obj.getUnit().getId(), obj.getBrand(), obj.getCountry().getId() != 0 ? obj.getCountry().getId() : null, obj.getPrice(), obj.getCurrency().getId(), obj.isIsService(), obj.getDescription(),
            obj.getSaleTaxGroup().getId(), obj.getPurchaseTaxGroup().getId(), sessionBean.getUser().getId(), obj.getCode(),  obj.getWeight(), obj.getWeightUnit().getId(), sessionBean.getUser().getLastBranch().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<SendStockRequest> checkRequestSend(StockRequest obj) {
        String sql = "SELECT \n"
                + "       sr.is_send AS srissend\n"
                + "       FROM log.sendstockrequest sr \n"
                + "       WHERE sr.deleted=FALSE AND sr.stockrequest_id = ?\n"
                + "ORDER BY sr.id DESC LIMIT 1";

        Object[] param = new Object[]{obj.getId()};

        List<SendStockRequest> result = getJdbcTemplate().query(sql, param, new SendStockRequestMapper());
        return result;
    }

    @Override
    public List<SendStockRequest> checkRequestSendAllRecord(StockRequest obj) {
        String sql = "SELECT \n"
                + "       sr.is_send AS srissend\n"
                + "       FROM log.sendstockrequest sr \n"
                + "       WHERE sr.deleted=FALSE AND sr.stockrequest_id = ?\n"
                + "ORDER BY sr.id DESC";

        Object[] param = new Object[]{obj.getId()};

        List<SendStockRequest> result = getJdbcTemplate().query(sql, param, new SendStockRequestMapper());
        return result;
    }

    @Override
    public int update(StockRequest obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int controlStockRequest(String where, StockRequest stockRequest) {
        int id;

        String sql = "SELECT \n"
                + "	COUNT(msrt.id)\n"
                + "FROM inventory.stockrequest msrt \n"
                + "WHERE msrt.deleted=FALSE AND UPPER(LTRIM(RTRIM(msrt.barcode))) = ? \n"
                + "AND msrt.approval = 0 AND msrt.branch_id = ?\n"
                + where + "\n";

        Object[] param = new Object[]{stockRequest.getBarcode().toUpperCase().trim(), sessionBean.getUser().getLastBranch().getId()};
        List<Integer> list = getJdbcTemplate().queryForList(sql, param, Integer.class);
        if (list.size() > 0) {
            id = list.get(0);
        } else {
            id = 0;
        }
        return id;
    }

}
