/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stockpricerequest.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class StockPriceRequestDao extends JdbcDaoSupport implements IStockPriceRequestDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    @Override
    public List<StockPriceRequest> findall() {
        String sql = "SELECT\n"
                + "pcr.id AS pcrid,\n"
                + "pcr.stock_id AS pcrstock_id,\n"
                + "stck.name AS stckname,\n"
                + "stck.barcode AS stckbarcode,\n"
                + "pcr.centerstock_id AS pcrcenterstock_id,\n"
                + "pcr.avaibleprice AS pcravaibleprice,\n"
                + "pcr.avaiblecurrency_id AS pcravailablecurrency_id,\n"
                + "pcr.recommendedprice AS pcrrecommendedprice,\n"
                + "pcr.recommendedcurrency_id AS pcrrecommendedcurrency_id,\n"
                + "pcr.requestprice AS pcrrequestprice,\n"
                + "pcr.requestcurrency_id AS pcrrequestcurrency_id,\n"
                + "pcr.description AS pcrdescription,\n"
                + "pcr.approval AS pcrapproval,\n"
                + "pcr.approvaldate AS pcrapprovaldate\n"
                + "FROM inventory.pricechangerequest pcr\n"
                + "LEFT JOIN inventory.stock stck ON (pcr.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "WHERE pcr.deleted=FALSE AND pcr.branch_id=?\n"
                + "ORDER BY pcr.approval, pcr.id DESC, pcr.approvaldate DESC;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<StockPriceRequest> result = getJdbcTemplate().query(sql, param, new StockPriceRequestMapper());
        return result;
    }

    @Override
    public int create(StockPriceRequest obj) {
        String sql = " SELECT r_request_id FROM inventory.insert_pricechangerequest(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(),obj.getStock().getId(),obj.getStock().getCenterstock_id(),obj.getStock().getStockInfo().getCurrentSalePrice(),obj.getStock().getStockInfo().getCurrentSaleCurrency().getId(),
            obj.getStock().getStockInfo().getRecommendedPrice(),obj.getStock().getStockInfo().getCurrency().getId(), obj.getRequestPrice(), obj.getRequestCurrency().getId(), obj.getDescription(),
            sessionBean.getUser().getId()};        
        try {
            System.out.println("--param---" + Arrays.toString(param));
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
