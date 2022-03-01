/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * Created on 04.11.2016 08:23:51
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaxGroupConnection;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.inventory.taxgroup.dao.TaxGroupMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockTaxGroupDao extends JdbcDaoSupport implements IStockTaxGroupDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockTaxGroupConnection> listStokTaxGroup(Stock obj) {
        String sql = "SELECT\n"
                  + "    stgc.id AS stgcid,\n"
                  + "    txg.id AS txgid,\n"
                  + "    txg.name AS txgname,\n"
                  + "    txg.rate as txgrate,\n"
                  + "    txg.type_id as txgtype_id,\n"
                  + "    stgc.is_purchase as stgcis_purchase, \n"
                  + "    stgc.c_time as stgcc_time,\n"
                  + "    usd.id as usdid,\n"
                  + "    usd.name as usdname,\n"
                  + "    usd.surname as usdsurname,\n"
                  + "    usd.username as usdusername\n"
                  + "FROM\n"
                  + "	inventory.stock_taxgroup_con stgc  \n"
                  + "    INNER JOIN inventory.taxgroup txg  ON (txg.id = stgc.taxgroup_id AND txg.deleted=FALSE )"
                  + "    INNER JOIN general.userdata usd ON(usd.id=stgc.c_id) \n"
                  + "WHERE\n"
                  + "	stgc.stock_id = ? AND stgc.deleted=false ";
        Object[] param = new Object[]{obj.getId()};
        List<StockTaxGroupConnection> result = getJdbcTemplate().query(sql, param, new StockTaxGroupMapper());
        return result;
    }

    @Override
    public int create(StockTaxGroupConnection obj) {
        String sql = "INSERT INTO inventory.stock_taxgroup_con (stock_id,taxgroup_id,is_purchase,c_id) VALUES (?,?,?,?) RETURNING id ";

        Object[] param = new Object[]{obj.getStock().getId(), obj.getTaxGroup().getId(), obj.isIsPurchase(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StockTaxGroupConnection obj) {
        String sql = "UPDATE inventory.stock_taxgroup_con\n"
                  + "   SET taxgroup_id = ?,is_purchase=? ,u_id = ?, u_time = now()\n"
                  + "   WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getTaxGroup().getId(), obj.isIsPurchase(), sessionBean.getUser().getId(), obj.getId()};
//        System.out.println("----"+Arrays.toString(param));
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Ekleme ise comboboxa daha önce eklenmiş vergi gruplarının tipindeki vergi
     * grupları gelmicek.
     *
     * @param obj
     * @param isPurchase
     * @return
     */
    @Override
    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase) {
        
        String where="";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND txg.centertaxgroup_id IS NOT NULL  ";
        } else {
            where = where + " AND txg.is_otherbranch = TRUE  ";
        }
        
        String sql = "SELECT\n"
                + "    txg.id AS txgid,\n"
                + "    txg.name AS txgname,\n"
                + "    txg.rate AS txgrate,\n"
                + "    txg.type_id AS txgtype_id\n"
                + "FROM\n"
                + "    inventory.taxgroup txg  \n"
                + "WHERE txg.deleted = false "+where+" \n"
                + " AND txg.type_id NOT IN(\n"
                + "                  SELECT txg2.type_id \n"
                + "                  FROM inventory.stock_taxgroup_con stgc \n"
                + "                  INNER JOIN inventory.taxgroup txg2 ON (txg2.id = stgc.taxgroup_id)\n"
                + "                  WHERE stgc.deleted = false\n"
                + "                  AND stgc.stock_id = ? \n"
                + "                  AND stgc.is_purchase = ?\n"
                + " ) \n";
        Object[] param = new Object[]{obj.getId(), isPurchase};
        List<TaxGroup> result = getJdbcTemplate().query(sql, param, new TaxGroupMapper());
        return result;
    }

    /**
     * Güncelleme ise comboboxa daha önce eklenmiş vergi gruplarının tipindeki
     * vergi grupları gelmicek ama kendi tipindeki vergi grupları gelcek.
     *
     * @param obj
     * @param isPurchase
     * @param type
     * @return
     */
    @Override
    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase, int type) {
        
        String where="";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND txg.centertaxgroup_id IS NOT NULL  ";
        } else {
            where = where + " AND txg.is_otherbranch = TRUE  ";
        }
        String sql = "SELECT\n"
                + "    txg.id AS txgid,\n"
                + "    txg.name AS txgname,\n"
                + "    txg.rate AS txgrate,\n"
                + "    txg.type_id AS txgtype_id\n"
                + "FROM\n"
                + "    inventory.taxgroup txg  \n"
                + "WHERE txg.deleted = false" +where+ "\n"
                + " AND txg.type_id NOT IN(\n"
                + "                  SELECT txg2.type_id \n"
                + "                  FROM inventory.stock_taxgroup_con stgc \n"
                + "                  INNER JOIN inventory.taxgroup txg2 ON (txg2.id = stgc.taxgroup_id)\n"
                + "                  WHERE stgc.deleted = false\n"
                + "                  AND stgc.stock_id = ? \n"
                + "                  AND stgc.is_purchase = ?\n"
                + "                  AND txg2.type_id<>? \n"
                + " ) \n";
        Object[] param = new Object[]{obj.getId(), isPurchase,type};
        List<TaxGroup> result = getJdbcTemplate().query(sql, param, new TaxGroupMapper());
        return result;
    }

    @Override
    public int delete(StockTaxGroupConnection stockTaxGroupConnection) {
        String sql = "UPDATE inventory.stock_taxgroup_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?";
                 

        Object[] param = new Object[]{sessionBean.getUser().getId(), stockTaxGroupConnection.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
