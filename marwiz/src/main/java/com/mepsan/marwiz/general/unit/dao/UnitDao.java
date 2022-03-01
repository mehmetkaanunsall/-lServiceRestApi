/**
 *
 * Bu sınıf, unit tablosunda created, updated, deleted ve listing işlemlerini yapar.
 *
 * @author Ali Kurt
 *
 * Created on 26.10.2016 08:46:48
 */
package com.mepsan.marwiz.general.unit.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UnitDao extends JdbcDaoSupport implements IUnitDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Unit> findAll() {

        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND gunt.centerunit_id IS NOT NULL  ";
        } else {
            where = where + " AND gunt.is_otherbranch = TRUE ";
        }
        String sql = "SELECT "
                + " gunt.id AS guntid, \n"
                + "gunt.centerunit_id AS guntcenterunit_id, \n"
                + "gunt.name AS guntname, \n"
                + "gunt.sortname AS guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "gunt.internationalcode as guntinternationalcode, \n"
                + "gunt.integrationcode as guntintegrationcode,\n"
                + "gunt.mainweight as guntmainweight, \n"
                + "gunt.mainweightunit_id as guntmainweightunit_id, \n"
                + "gunt2.name AS gunt2name, \n"
                + "gunt2.sortname AS gunt2sortname,\n"
                + "gunt2.unitrounding as gunt2unitrounding \n"
                + "FROM  general.unit gunt\n"
                + "left join general.unit gunt2 on(gunt.mainweightunit_id = gunt2.id and gunt2.deleted=false)\n"
                + "WHERE  gunt.deleted = false " + where + " \n"
                + "ORDER BY gunt.name"
                + "";
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new UnitMapper());
    }

    @Override
    public int create(Unit obj) {
        String sql = "INSERT INTO general.unit (name,sortname,unitrounding,internationalcode,is_otherbranch,integrationcode,c_id,u_id,mainweight,mainweightunit_id) VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id ;";

        Object[] param = new Object[]{obj.getName(), obj.getSortName(), obj.getUnitRounding(), obj.getInternationalCode(), true, obj.getIntegrationCode(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getMainWeight(),
            obj.getMainWeightUnit().getId() == 0 ? null : obj.getMainWeightUnit().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Unit obj) {
        String sql = "UPDATE general.unit SET name= ?, sortname = ?, unitrounding = ? , internationalcode=?, is_otherbranch=?, integrationcode=?,  mainweight= ?, mainweightunit_id= ?, u_id = ?, u_time = now() WHERE id= ?";
        Object[] param = new Object[]{obj.getName(), obj.getSortName(), obj.getUnitRounding(), obj.getInternationalCode(), true, obj.getIntegrationCode(),
            obj.getMainWeight(), obj.getMainWeightUnit().getId() == 0 ? null : obj.getMainWeightUnit().getId(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Unit unit) {
        String where = "";
        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT\n"
                + "   CASE WHEN EXISTS (SELECT suc.unit_id FROM inventory.stock_unit_con suc LEFT JOIN inventory.stock stck ON (suc.stock_id = stck.id) WHERE suc.unit_id=? AND suc.deleted=False " + where + ") THEN 1\n"
                + "        WHEN EXISTS (SELECT si.weightunit_id FROM inventory.stockinfo si LEFT JOIN inventory.stock stck ON (si.stock_id = stck.id) WHERE si.weightunit_id=? AND si.deleted=False " + where + ") THEN 1\n"
                + "        WHEN EXISTS (SELECT stck.unit_id FROM inventory.stock stck WHERE stck.unit_id=? AND stck.deleted=False " + where + ") THEN 1\n"
                + "        WHEN EXISTS (SELECT gunt.mainweightunit_id FROM general.unit gunt WHERE gunt.mainweightunit_id=? AND gunt.deleted=False) THEN 2\n"
                + "        ELSE 0 END";

        Object[] param = new Object[]{unit.getId(), unit.getId(), unit.getId(), unit.getId()};
        System.out.println("param"+Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Unit unit) {
        String sql = "UPDATE general.unit SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), unit.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Unit> listOfUnit() {
        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND gunt.centerunit_id IS NOT NULL  ";
        } else {
            where = where + " AND gunt.is_otherbranch = TRUE ";
        }
        String sql = "SELECT "
                + " gunt.id AS guntid, "
                + "gunt.centerunit_id AS guntcenterunit_id, \n"
                + "gunt.name AS guntname, "
                + "gunt.sortname AS guntsortname,"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "gunt.internationalcode as guntinternationalcode \n"
                + "FROM  general.unit gunt "
                + "WHERE gunt.deleted=FALSE " + where + " \n"
                + "ORDER BY gunt.name";
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new UnitMapper());
    }

    @Override
    public Unit findUnitAccordingToName(Unit unit) {
        String where = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND gunt.is_otherbranch = TRUE ";
        } else {
            where = where + " AND gunt.centerunit_id IS NOT NULL  ";
        }

        String sql = "SELECT "
                + " gunt.id AS guntid, "
                + "gunt.centerunit_id AS guntcenterunit_id, \n"
                + "gunt.name AS guntname, "
                + "gunt.sortname AS guntsortname,"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "gunt.internationalcode as guntinternationalcode \n"
                + "FROM  general.unit gunt \n"
                + "WHERE gunt.deleted=FALSE AND\n"
                + "(LOWER(LTRIM(RTRIM(gunt.name))) = ? AND gunt.id <> ?)\n"
                + where;

        Object[] param = new Object[]{unit.getName().toLowerCase().trim(), unit.getId()};

        List<Unit> result = getJdbcTemplate().query(sql, param, new UnitMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Unit();
        }
    }

    @Override
    public int deleteForOtherBranch(Unit unit) {
        String sql = "UPDATE general.unit SET is_otherbranch=FALSE, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), unit.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateAvailableUnit(int oldId, int newId) {
        String sql = "UPDATE inventory.stock SET unit_id = ?, u_id=? , u_time=NOW() WHERE deleted=False AND is_otherbranch = TRUE AND unit_id=?;\n"
                + "UPDATE inventory.stock_unit_con SET unit_id = ?, u_id=? , u_time=NOW() WHERE deleted=False AND is_otherbranch = TRUE AND unit_id=?;\n"
                + "UPDATE general.unit SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                + "UPDATE general.unit SET is_otherbranch=TRUE, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{newId, sessionBean.getUser().getId(), oldId, newId, sessionBean.getUser().getId(), oldId, sessionBean.getUser().getId(), oldId, sessionBean.getUser().getId(), newId};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Unit> listOfUnitAllBranches() {

        String sql = "SELECT\n"
                + "     gunt.id AS guntid, \n"
                + "     gunt.name AS guntname, \n"
                + "     gunt.sortname AS guntsortname,\n"
                + "     gunt.unitrounding as guntunitrounding,\n"
                + "     gunt.mainweight as guntmainweight, \n"
                + "     gunt.mainweightunit_id as guntmainweightunit_id, \n"
                + "     gunt2.name AS gunt2name, \n"
                + "     gunt2.sortname AS gunt2sortname,\n"
                + "     gunt2.unitrounding as gunt2unitrounding \n"
                + "FROM  general.unit gunt\n"
                + "LEFT JOIN general.unit gunt2 on(gunt.mainweightunit_id = gunt2.id and gunt2.deleted=false)\n"
                + "WHERE  gunt.deleted = FALSE\n"
                + "ORDER BY gunt.name\n";
        return getJdbcTemplate().query(sql, new UnitMapper());
    }
}
