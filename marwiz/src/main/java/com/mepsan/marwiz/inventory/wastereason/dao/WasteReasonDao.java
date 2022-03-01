package com.mepsan.marwiz.inventory.wastereason.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.WasteReason;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class WasteReasonDao extends JdbcDaoSupport implements IWasteReasonDao {

    @Autowired
    private SessionBean sessionBean;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WasteReason> findAll() {

        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND iwre.centerwastereason_id IS NOT NULL ";
        } else {
            where = where + " AND iwre.is_otherbranch = TRUE ";
        }

        String sql = " SELECT \n"
                  + "       iwre.id AS iwreid,\n"
                  + "       iwre.centerwastereason_id AS iwrecenterwastereason_id,\n"
                  + "       iwre.name AS iwrename\n"
                  + "    FROM inventory.wastereason iwre \n"
                  + "       WHERE iwre.deleted=FALSE " + where + "\n"
                  + "        ORDER BY iwre.name";

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new WasteReasonMapper());

    }

    @Override
    public int create(WasteReason obj) {

        String sql = "INSERT INTO inventory.wastereason (name,is_otherbranch,c_id,u_id) VALUES (?,?,?,?) RETURNING id ;";

        Object[] param = new Object[]{obj.getName(), true, sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WasteReason obj) {
        String sql = "UPDATE inventory.wastereason SET name= ?, is_otherbranch = ?, u_id = ?, u_time = now() WHERE id= ? ";
        Object[] param = new Object[]{obj.getName(), true, sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int delete(WasteReason wasteReason) {

        String sql = "UPDATE inventory.wastereason SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), wasteReason.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(WasteReason wasteReason) {
        String sql = "SELECT\n"
                  + "   CASE WHEN EXISTS (SELECT wastereason_id FROM inventory.wasteiteminfo WHERE wastereason_id=? AND deleted=False) THEN 1\n"
                  + " 	     WHEN EXISTS (SELECT wastereason_id FROM inventory.wasteiteminfo WHERE wastereason_id=? AND deleted=False) THEN 1\n"
                  + "        ELSE 0 END";

        Object[] param = new Object[]{wasteReason.getId(), wasteReason.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public WasteReason findAccordingToName(WasteReason wasteReason) {
        String where = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND iwre.is_otherbranch = TRUE ";
        } else {
            where = where + " AND iwre.centerwastereason_id IS NOT NULL ";
        }

        String sql = " SELECT \n"
                  + "       iwre.id AS iwreid,\n"
                  + "       iwre.centerwastereason_id AS iwrecenterwastereason_id,\n"
                  + "       iwre.name AS iwrename\n"
                  + "    FROM inventory.wastereason iwre \n"
                  + "       WHERE iwre.deleted=FALSE \n"
                  + "    AND (LOWER(LTRIM(RTRIM(iwre.name))) = ? AND iwre.id <> ?)\n"
                  + where;

        Object[] param = new Object[]{wasteReason.getName().toLowerCase().trim(), wasteReason.getId()};

        List<WasteReason> result = getJdbcTemplate().query(sql, param, new WasteReasonMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new WasteReason();
        }
    }

    @Override
    public int deleteForOtherBranch(WasteReason wasteReason) {
        String sql = "UPDATE inventory.wastereason SET is_otherbranch=FALSE, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), wasteReason.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<WasteReason> selectWasteReason(int isCentral) {

        String where = "";
        if (isCentral == 0) {
            where = where + " AND iwre.centerwastereason_id IS NOT NULL ";
        } else if (isCentral == 1) {
            where = where + " AND iwre.is_otherbranch = TRUE ";
        }

        String sql = " SELECT \n"
                  + "       iwre.id AS iwreid,\n"
                  + "       iwre.centerwastereason_id AS iwrecenterwastereason_id,\n"
                  + "       iwre.name AS iwrename\n"
                  + "    FROM inventory.wastereason iwre \n"
                  + "       WHERE iwre.deleted=FALSE " + where + "\n"
                  + "        ORDER BY iwre.name";

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new WasteReasonMapper());

    }

    @Override
    public int updateAvailableWasteReason(int oldId, int newId) {
        String sql = "UPDATE inventory.wastereason SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=False AND id=?;\n"
                  + " UPDATE inventory.wastereason SET is_otherbranch = ?, u_id=?, u_time=NOW() WHERE deleted=False AND id=?;\n"
                  + " UPDATE inventory.wasteiteminfo SET wastereason_id = ?, u_id=?, u_time=NOW() WHERE deleted=False AND wastereason_id= ?;\n";

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
