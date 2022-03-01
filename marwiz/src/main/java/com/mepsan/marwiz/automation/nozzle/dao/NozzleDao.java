/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:27:18
 */
package com.mepsan.marwiz.automation.nozzle.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class NozzleDao extends JdbcDaoSupport implements INozzleDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Nozzle> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {

        if (sortField == null) {
            sortField = "wr.id";
            sortOrder = "desc";
        }
        String sql = "SELECT \n"
                + "  nz.id AS nzid,\n"
                + "  nz.warehouse_id AS nzwarehouse_id,\n"
                + "  wr.name AS wrname,\n"
                + "  nz.name AS nzname,\n"
                + "  nz.pumpno AS nzpumpno,\n"
                + "  nz.nozzleno AS nznozzleno, \n"
                + "  nz.status_id AS nzstatus_id,\n"
                + "  sttd.name AS sttdname,\n"
                + "  nz.index AS nzindex,\n"
                + "  nz.is_ascending AS nzis_ascending,\n"
                + "  nz.description AS nzdescription \n"
                + "FROM \n"
                + "  automation.nozzle  nz\n"
                + "  INNER JOIN inventory.warehouse wr ON(wr.id = nz.warehouse_id AND wr.deleted = FALSE)\n"
                + "  INNER JOIN system.status_dict sttd ON(sttd.status_id  =  nz.status_id AND sttd.language_id = ?)\n"
                + "WHERE nz.deleted = FALSE AND wr.branch_id = ?\n" + where
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Nozzle> result = getJdbcTemplate().query(sql, param, new NozzleMapper());
        return result;
    }

    @Override
    public int count(String where) {
        String sql = "SELECT COUNT(nz.id)\n"
                + "FROM \n"
                + "  automation.nozzle  nz\n"
                + "  INNER JOIN inventory.warehouse wr ON(wr.id = nz.warehouse_id AND wr.deleted = FALSE)\n"
                + "  INNER JOIN system.status_dict sttd ON(sttd.status_id  =  nz.status_id AND sttd.language_id = ? )\n"
                + "WHERE nz.deleted = FALSE AND wr.branch_id = ? " + where;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int checkNozzle(Nozzle obj) {
        int id;
        String sql = " SELECT CASE WHEN EXISTS(\n"
                + " 	SELECT \n"
                + "        nz.id \n"
                + "    FROM  automation.nozzle nz \n"
                + "    INNER JOIN inventory.warehouse wr ON(wr.id = nz.warehouse_id AND wr.deleted = FALSE)\n"
                + "    WHERE nz.warehouse_id=? AND wr.branch_id = ? AND nz.deleted=False\n"
                + " 	AND nz.pumpno = ?::VARCHAR AND nz.nozzleno=?::VARCHAR AND nz.id <> ?\n"
                + "  ) THEN\n"
                + "	0  \n"
                + "   ELSE\n"
                + "   1 \n"
                + " END;";

        Object[] param = new Object[]{obj.getWarehouse().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getPumpNo(), obj.getNozzleNo(), obj.getId()};
        id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int create(Nozzle obj) {
        String sql = " \n"
                + "INSERT INTO \n"
                + "automation.nozzle \n"
                + " ( warehouse_id, name, pumpno, nozzleno,  status_id,  index,  is_ascending,  description,u_id,c_id)\n"
                + " VALUES \n"
                + "( ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?, ?) RETURNING id;\n";

        Object[] param = new Object[]{obj.getWarehouse().getId(), obj.getName(), obj.getPumpNo(), obj.getNozzleNo(), obj.getStatus().getId(), obj.getIndex(), obj.isIsAscending(), obj.getDescription(), sessionBean.getUser().getId(), sessionBean.getUser().getId()
        };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Nozzle obj) {
        String sql = " UPDATE \n"
                + "  automation.nozzle \n"
                + "SET \n"
                + "  warehouse_id = ?,\n"
                + "  name = ?,\n"
                + "  pumpno = ?,\n"
                + "  nozzleno = ?,\n"
                + "  status_id = ?,\n"
                + "  index = ?,\n"
                + "  is_ascending = ?,\n"
                + "  description = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = NOW() \n"
                + "WHERE \n"
                + "  id = ? ;\n";

        Object[] param = new Object[]{obj.getWarehouse().getId(), obj.getName(), obj.getPumpNo(), obj.getNozzleNo(), obj.getStatus().getId(), obj.getIndex(), obj.isIsAscending(), obj.getDescription(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Nozzle nozzle) {
        String sql = "UPDATE automation.nozzle SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), nozzle.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Nozzle nozzle) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT nozzle_id FROM automation.shiftsale WHERE nozzle_id=? AND deleted=False) THEN 1 ELSE 0 END ";

        Object[] param = new Object[]{nozzle.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
