package com.mepsan.marwiz.inventory.starbucksstock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class StarbucksStockDao extends JdbcDaoSupport implements IStarbucksStockDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(StarbucksStock obj) {
        String sql = "INSERT INTO inventory.starbucksstock (name,code,is_otherbranch,c_id,u_id) VALUES (?,?,?,?,?) RETURNING id ;";

        Object[] param = new Object[]{obj.getName(), obj.getCode(), true, sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StarbucksStock obj) {
        String sql = "UPDATE inventory.starbucksstock SET name = ?, code = ?, is_otherbranch = ?, u_id = ?, u_time = now() WHERE id= ? ";
        Object[] param = new Object[]{obj.getName(), obj.getCode(), true, sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<StarbucksStock> findAll() {
        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND iss.centerstarbucksstock_id IS NOT NULL ";
        } else {
            where = where + " AND iss.is_otherbranch = TRUE ";
        }

        String sql = "SELECT \n"
                  + "     iss.id AS issid,\n"
                  + "     iss.name AS issname,\n"
                  + "     iss.code AS isscode,\n"
                  + "     iss.centerstarbucksstock_id AS isscenterstarbucksstock_id\n"
                  + "   FROM inventory.starbucksstock iss\n"
                  + "      WHERE iss.deleted=FALSE " + where + "\n"
                  + "   ORDER BY iss.name";

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new StarbucksStockMapper());

    }

    @Override
    public int delete(StarbucksStock obj) {
        String sql = "UPDATE inventory.starbucksstock SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public StarbucksStock findAccordingToCode(StarbucksStock stock) {
        String where = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND iss.is_otherbranch = TRUE ";
        } else {
            where = where + " AND iss.centerstarbucksstock_id IS NOT NULL  ";
        }

        String sql = "SELECT \n"
                  + "     iss.id AS issid,\n"
                  + "     iss.name AS issname,\n"
                  + "     iss.code AS isscode,\n"
                  + "     iss.centerstarbucksstock_id AS isscenterstarbucksstock_id\n"
                  + "   FROM inventory.starbucksstock iss\n"
                  + "      WHERE iss.deleted=FALSE \n"
                  + "   AND (LOWER(LTRIM(RTRIM(iss.code))) = ? AND iss.id <> ?)\n"
                  + where;

        Object[] param = new Object[]{stock.getCode().toLowerCase().trim(), stock.getId()};

        List<StarbucksStock> result = getJdbcTemplate().query(sql, param, new StarbucksStockMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new StarbucksStock();
        }
    }

    @Override
    public int deleteForOtherBranch(StarbucksStock stock) {
        String sql = "UPDATE inventory.starbucksstock SET is_otherbranch=FALSE, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), stock.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateAvailableStarbucksStock(int oldId, int newId) {
        String sql = "UPDATE inventory.starbucksstock SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=False AND id=?;\n"
                  + " UPDATE inventory.starbucksstock SET is_otherbranch = ?, u_id=?, u_time=NOW() WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), oldId,
            true, sessionBean.getUser().getId(), newId};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
