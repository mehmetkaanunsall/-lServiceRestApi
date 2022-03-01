/**
 *
 * Bu sınıf, brand tablosunda created, updated, deleted ve listing işlemlerini yapar.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 16:57:46
 */
package com.mepsan.marwiz.general.brand.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.system.Item;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BrandDao extends JdbcDaoSupport implements IBrandDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Brand> findAll(Item item) {

        String where = "";
        if (item.getId() == 2) {

            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where = where + " AND br.centerbrand_id IS NOT NULL  ";
            } else {
                where = where + " AND br.is_otherbranch = TRUE ";
            }
        }

        String sql = "SELECT "
                  + "br.id AS brid, "
                  + "br.centerbrand_id AS brcenterbrand_id, "
                  + "br.name AS brname "
                  + "   FROM "
                  + "general.brand br   "
                  + "WHERE br.item_id = ? AND br.deleted = false" + where + "\n"
                  + " ORDER BY br.name";
        Object[] param = new Object[]{item.getId()};
        return getJdbcTemplate().query(sql, param, new BrandMapper());
    }

    @Override
    public int create(Brand obj) {
        String sql = "INSERT INTO general.brand (name,item_id,is_otherbranch,c_id,u_id) VALUES (?, ?, ?, ?, ?) RETURNING id ;";

        Object[] param = new Object[]{obj.getName(), obj.getItem().getId(), true, sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Brand obj) {
        String sql = "UPDATE general.brand SET name= ?, is_otherbranch = ?, u_id = ?, u_time = now() WHERE id = ? ";
        Object[] param = new Object[]{obj.getName(), true, sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Brand obj) {
        String sql = "";
        if (obj.getItem().getId() == 2) { //Stok
            String where = "";
            if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where = where + " AND is_otherbranch = TRUE ";
            }
            sql = "SELECT CASE WHEN EXISTS (SELECT brand_id FROM inventory.stock WHERE brand_id=? AND deleted=False " + where + " ) THEN 1 ELSE 0 END";
        } else if (obj.getItem().getId() == 24) { //Yazarkasa
            sql = "SELECT CASE WHEN EXISTS (SELECT brand_id FROM general.cashregister WHERE brand_id=? AND deleted=False) THEN 1 ELSE 0 END";
        } else if (obj.getItem().getId() == 36) { // Otomasyon Cihazı
            sql = "SELECT CASE WHEN EXISTS (SELECT brand_id FROM inventory.vendingmachine WHERE brand_id=? AND deleted=False) THEN 1 ELSE 0 END";
        }

        Object[] param = new Object[]{obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Brand obj) {
        String sql = "UPDATE general.brand set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public Brand findBrandAccordingToName(Brand obj) {

        String where = "";

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND br.is_otherbranch = TRUE  ";
        } else {
            where = where + " AND br.centerbrand_id IS NOT NULL";
        }

        String sql = "SELECT "
                  + "br.id AS brid, "
                  + "br.centerbrand_id AS brcenterbrand_id, "
                  + "br.name AS brname "
                  + "   FROM "
                  + "general.brand br   "
                  + "WHERE br.item_id = 2 AND br.deleted = false \n"
                  + "AND (LOWER(LTRIM(RTRIM(br.name))) = ? AND br.id <> ?)\n"
                  + where;
        Object[] param = new Object[]{obj.getName().toLowerCase().trim(), obj.getId()};

        List<Brand> result = getJdbcTemplate().query(sql, param, new BrandMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Brand();
        }
    }

    @Override
    public int deleteForOtherBranch(Brand obj) {
        String sql = "UPDATE general.brand set is_otherbranch=FALSE ,u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateAvailableBrand(int oldId, int newId) {
        String sql = "UPDATE general.brand SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=False AND id=?;\n"
                  + " UPDATE general.brand SET is_otherbranch = ?, u_id=?, u_time=NOW() WHERE deleted=False AND id=?;\n"
                  + " UPDATE inventory.stock SET brand_id = ?, u_id=?, u_time=NOW() WHERE deleted=False AND is_otherbranch=TRUE AND brand_id= ?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), oldId,
            true, sessionBean.getUser().getId(), newId,
            newId, sessionBean.getUser().getId(), oldId};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
