/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:02:28 PM
 */
package com.mepsan.marwiz.general.refinerypurchase.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author OguzhanUyanik
 */
public class RefineryPurchaseDao extends JdbcDaoSupport implements IRefineryPurchaseDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<RefineryStockPrice> findAll() {
        String sql = "SELECT \n"
                + "                   rsp.id as rspid,\n"
                + "                   rsp.refinery_id as rsprefinreyid,\n"
                + "                   COALESCE(rsp.price,0) as rspprice,\n"
                + "                   stck.id as rspstock_id,\n"
                + "                   stck.name as stckname,\n"
                + "                   COALESCE(rsp.currency_id,0) as rspcurrency_id,\n"
                + "                   cryd.name as crydname\n"
                + "FROM inventory.stock stck\n"
                + "INNER JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.branch_id=? AND stcki.is_fuel=TRUE)\n"
                + "LEFT JOIN automation.refinerystockprice rsp ON(rsp.stock_id=stck.id AND rsp.deleted=FALSE AND rsp.branch_id = ?)\n"
                + "LEFT JOIN system.currency_dict cryd ON(cryd.currency_id=rsp.currency_id AND cryd.language_id=?)\n"
                + "WHERE stck.deleted=FALSE \n"
                + "ORDER BY stck.id,stck.name\n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().query(sql, param, new RefineryPurchaseMapper());
    }

    @Override
    public int testBeforeDelete(RefineryStockPrice obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(RefineryStockPrice obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int create(RefineryStockPrice obj) {
        String sql = "INSERT INTO \n"
                + "  automation.refinerystockprice\n"
                + "(\n"
                + "  branch_id,\n"
                + "  refinery_id,\n"
                + "  stock_id,\n"
                + "  currency_id,\n"
                + "  price,\n"
                + "  c_id,\n"
                + "  u_id\n"
                + ")\n"
                + "VALUES (\n"
                + " ?, ?, ?, ?, ?, ?, ? \n"
                + ") RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getRefineryId(), obj.getStock().getId(), obj.getCurrency().getId(), obj.getPrice(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(RefineryStockPrice obj) {
        String sql = "UPDATE \n"
                + "  automation.refinerystockprice \n"
                + "SET \n"
                + "  refinery_id =? ,\n"
                + "  stock_id = ?,\n"
                + "  currency_id =? ,\n"
                + "  price = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?;";

        Object[] param = new Object[]{obj.getRefineryId(), obj.getStock().getId(), obj.getCurrency().getId(), obj.getPrice(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int findRefineryPrice(RefineryStockPrice obj) {
        String sql = "  SELECT CASE WHEN EXISTS (SELECT rsp.id FROM automation.refinerystockprice rsp WHERE id=? AND deleted=FALSE)THEN 1 ELSE 0 END";

        Object[] param = new Object[]{obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public RefineryStockPrice findStockRefineryPrice(int stockId, Branch branch) {
        String sql = "SELECT \n"
                + "rsp.id as rspid,\n"
                + "rsp.price rspprice,\n"
                + "rsp.refinery_id as rsprefinreyid,\n"
                + "rsp.currency_id as rspcurrency_id,\n"
                + "rsp.stock_id as rspstock_id\n"
                + "FROM automation.refinerystockprice rsp \n"
                + "WHERE rsp.deleted=FALSE AND rsp.stock_id=? AND rsp.branch_id = ? \n"
                + "LIMIT 1";

        Object[] param = new Object[]{stockId, branch.getId()};
        List<RefineryStockPrice> result = getJdbcTemplate().query(sql, param, new RefineryPurchaseMapper());

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new RefineryStockPrice();
        }
    }

}
