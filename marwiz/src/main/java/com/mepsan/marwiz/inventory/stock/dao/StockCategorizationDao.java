/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   18.01.2018 04:47:35
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.categorization.dao.CategorizationMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockCategorizationConnection;
import com.mepsan.marwiz.general.model.system.Item;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockCategorizationDao extends JdbcDaoSupport implements IStockCategorizationDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Categorization> listCategorization(Stock obj, Item ci) {
        Object[] param = null;
        String sql = "";
        if (ci.getId() == 45) {
            sql = "SELECT \n"
                    + "    gct.id AS gctid,\n"
                    + "    gct.name AS gctname,\n"
                    + "    gct.parent_id AS gctparent_id,\n"
                    + "    CAST(COALESCE((SELECT gsct.id FROM inventory.stock_centercategorization_con gsct WHERE gsct.stock_id = ? AND gsct.categorization_id = gct.id AND gsct.deleted = false), 0) AS boolean) AS gctchecked\n"
                    + "FROM \n"
                    + "	general.categorization gct \n"
                    + "WHERE\n"
                    + "	gct.item_id = ? AND gct.deleted = false"
                    + " ORDER BY COALESCE(gct.parent_id ,0),gct.id ";
        } else {
            sql = "SELECT \n"
                    + "    gct.id AS gctid,\n"
                    + "    gct.name AS gctname,\n"
                    + "    gct.parent_id AS gctparent_id,\n"
                    + "    CAST(COALESCE((SELECT gsct.id FROM inventory.stock_categorization_con gsct WHERE gsct.stock_id = ? AND gsct.categorization_id = gct.id AND gsct.deleted = false), 0) AS boolean) AS gctchecked\n"
                    + "FROM \n"
                    + "	general.categorization gct \n"
                    + "WHERE\n"
                    + "	gct.item_id = ? AND gct.deleted = false"
                    + " ORDER BY COALESCE(gct.parent_id ,0),gct.id ";

        }
        param = new Object[]{obj.getId(), ci.getId()};
        List<Categorization> result = getJdbcTemplate().query(sql, param, new CategorizationMapper());
        return result;
    }

    @Override
    public int create(StockCategorizationConnection obj) {
        String sql = "INSERT INTO inventory.stock_categorization_con\n"
                + "(categorization_id,stock_id, c_id,u_id)\n"
                + "SELECT\n"
                + "?,\n"
                + "?,\n"
                + "?,\n"
                + "?\n"
                + "WHERE NOT EXISTS (SELECT sc.categorization_id FROM inventory.stock_categorization_con sc WHERE sc.deleted=FALSE AND sc.stock_id = ? AND sc.categorization_id = ?)\n"
                + "RETURNING id;";

        Object[] param = new Object[]{obj.getCategorization().getId(), obj.getStock().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getStock().getId(), obj.getCategorization().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            if (((SQLException) e.getCause()) == null) {//Varsa default değeri -1 döndürmek için 
                return 1;
            } else {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }
        }
    }

    @Override
    public int update(StockCategorizationConnection obj) {
        String sql = "UPDATE inventory.stock_categorization_con SET u_id = ?, u_time = now(), deleted = true WHERE categorization_id = ? AND stock_id = ? AND deleted = false";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getCategorization().getId(), obj.getStock().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int allCreat(Stock obj, String choseeCategorizations, Item ci) {
        String sql = "INSERT INTO inventory.stock_categorization_con (categorization_id,stock_id,c_id,u_id) \n"
                + "SELECT\n"
                + "    gct.id,\n"
                + "    ? AS stock_id,"
                + "    ? AS c_id,\n"
                + "    ? AS u_id\n"
                + "FROM\n"
                + "	general.categorization gct\n"
                + "WHERE\n"
                + "	gct.item_id = ? AND gct.deleted = false AND gct.id NOT IN( " + choseeCategorizations + " )";

        Object[] param = new Object[]{obj.getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), ci.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int allUpdate(Stock obj, String choseeCategorizations) {
        String sql = "UPDATE "
                + "inventory.stock_categorization_con "
                + "SET "
                + "deleted = true, "
                + "u_id = ?, "
                + "u_time = now() "
                + "WHERE deleted = false AND stock_id = ? AND categorization_id IN ( " + choseeCategorizations + " )";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Categorization> listOfCategorization() {
        String sql = "SELECT \n"
                + "          ct.id as gctid,\n"
                + "          ct.name as gctname,\n"
                + "          ct2.name as gctparentname, \n"
                + "          ct2.id as gctparent_id \n"
                + "FROM general.categorization ct \n"
                + "LEFT JOIN general.categorization ct2 ON (ct2.parent_id=ct.id AND ct2.deleted=FALSE)\n"
                + "WHERE ct.deleted=FALSE AND ct.item_id=2  AND (ct2.parent_id IS NOT NULL OR  ct.parent_id IS NULL)"
                + "ORDER BY ct.id ";

        Object[] param = new Object[]{};

        List<Categorization> result = getJdbcTemplate().query(sql, new CategorizationMapper());
        return result;
    }

}
