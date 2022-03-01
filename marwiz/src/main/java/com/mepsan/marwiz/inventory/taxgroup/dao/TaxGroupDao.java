/**
 *
 * Bu sınıf, taxgroup tablosunda created, updated, deleted ve listing işlemlerini yapar.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 16:56:57
 */
package com.mepsan.marwiz.inventory.taxgroup.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TaxGroupDao extends JdbcDaoSupport implements ITaxGroupDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<TaxGroup> findAll() {

        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND txg.centertaxgroup_id IS NOT NULL  ";
        } else {
            where = where + " AND txg.is_otherbranch = TRUE  ";
        }
        String sql = "SELECT "
                + "txg.id AS txgid, "
                + "txg.centertaxgroup_id AS txgcentertaxgroup_id, "
                + "txg.name AS txgname, "
                + "txg.rate AS txgrate, "
                + "typd.type_id AS typdid, "
                + "typd.name AS typdname "
                + "   FROM "
                + "inventory.taxgroup txg "
                + "INNER JOIN system.type_dict typd  ON (typd.type_id = txg.type_id AND typd.language_id = ?)"
                + "WHERE  txg.deleted = false " + where + "\n";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(),};
        return getJdbcTemplate().query(sql, param, new TaxGroupMapper());
    }

    @Override
    public int create(TaxGroup obj) {
        String sql = "INSERT INTO inventory.taxgroup (name,rate,type_id,is_otherbranch,c_id,u_id) VALUES (?,?,?,?,?,?) RETURNING id ";

        Object[] param = new Object[]{obj.getName(), obj.getRate(), obj.getType().getId(), true,
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(TaxGroup obj) {
        String sql = "UPDATE inventory.taxgroup SET name= ?, rate = ?, type_id = ?, is_otherbranch = ?, u_id = ?, u_time = now() WHERE id = ? ";
        Object[] param = new Object[]{obj.getName(), obj.getRate(), obj.getType().getId(), true, sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<TaxGroup> findTaxGroupsForStock(Stock stock) {
        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND txg.centertaxgroup_id IS NOT NULL  ";
        } else {
            where = where + " AND txg.is_otherbranch = TRUE  ";
        }
        String sql = "SELECT \n"
                + "	txg.id AS txgid, \n"
                + "	txg.name AS txgname, \n"
                + "	txg.rate AS txgrate, \n"
                + "	typd.type_id AS typdid, \n"
                + "	typd.name AS typdname \n"
                + "FROM inventory.stock_taxgroup_con stc  \n"
                + "	LEFT JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id)\n"
                + "	INNER JOIN system.type_dict typd  ON (typd.type_id = txg.type_id AND typd.language_id =?)\n"
                + "WHERE   txg.deleted = false AND stc.stock_id=? \n"
                + where;
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(),
            stock.getId()};
        return getJdbcTemplate().query(sql, param, new TaxGroupMapper());
    }

    @Override
    public TaxGroup findTaxGroupsKDV(Stock stock, boolean isPurchase, BranchSetting branchSetting) {
        String where = "";
        if (branchSetting.isIsCentralIntegration()) {
            where = where + " AND txg.centertaxgroup_id IS NOT NULL  ";
        } else {
            where = where + " AND txg.is_otherbranch = TRUE  ";
        }
        String sql = "SELECT \n"
                + "	txg.id AS txgid, \n"
                + "	txg.rate AS txgrate \n"
                + "FROM inventory.stock_taxgroup_con stc  \n"
                + "INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "WHERE stc.deleted = false "
                + "AND txg.type_id = 10 --kdv grubundan \n"
                + "AND stc.stock_id = ? \n"
                + "AND stc.is_purchase = ?\n"
                + where + "\n"
                + " LIMIT 1 ";

        Object[] param = new Object[]{stock.getId(), isPurchase};
        try {
            return getJdbcTemplate().queryForObject(sql, param, new TaxGroupMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int testBeforeDelete(TaxGroup taxGroup) {
        String where = "";
        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT CASE WHEN EXISTS (SELECT stc.taxgroup_id FROM inventory.stock_taxgroup_con stc LEFT JOIN inventory.stock stck ON (stc.stock_id = stck.id) WHERE stc.taxgroup_id=? AND stc.deleted=False "+where+") THEN 1 ELSE 0 END";

        Object[] param = new Object[]{taxGroup.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(TaxGroup taxGroup) {
        String sql = "UPDATE inventory.taxgroup SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), taxGroup.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public TaxGroup findAccordingToTypeAndRate(TaxGroup taxGroup) {
        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND txg.is_otherbranch = TRUE  ";
        } else {
            where = where + " AND txg.centertaxgroup_id IS NOT NULL  ";
        }
        
         String sql = "SELECT "
                + "txg.id AS txgid, "
                + "txg.centertaxgroup_id AS txgcentertaxgroup_id, "
                + "txg.name AS txgname, "
                + "txg.rate AS txgrate, "
                + "typd.type_id AS typdid, "
                + "typd.name AS typdname "
                + "   FROM "
                + "inventory.taxgroup txg "
                + "INNER JOIN system.type_dict typd  ON (typd.type_id = txg.type_id AND typd.language_id = ?)"
                + "WHERE  txg.deleted = false \n"
                + "AND txg.rate = ? AND txg.type_id  = ? AND txg.id <> ?\n"
                + "" + where + "\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(),taxGroup.getRate(),taxGroup.getType().getId(),taxGroup.getId()};

        List<TaxGroup> result = getJdbcTemplate().query(sql, param, new TaxGroupMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new TaxGroup();
        }
    }

    @Override
    public int deleteForOtherBranch(TaxGroup taxGroup) {
        String sql = "UPDATE inventory.taxgroup SET is_otherbranch=FALSE, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), taxGroup.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateAvailableTaxGroup(int oldId, int newId) {
        String sql = "UPDATE inventory.stock_taxgroup_con SET taxgroup_id = ?, u_id=? , u_time=NOW() WHERE deleted=False AND taxgroup_id=?;\n"
                + "UPDATE inventory.taxgroup SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                + "UPDATE inventory.taxgroup SET is_otherbranch=TRUE, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{newId, sessionBean.getUser().getId(), oldId, sessionBean.getUser().getId(), oldId, sessionBean.getUser().getId(), newId};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
