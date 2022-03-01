/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:12:05
 */
package com.mepsan.marwiz.general.pointofsale.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.PointOfSaleSafeConnection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PointOfSaleSafeDao extends JdbcDaoSupport implements IPointOfSaleSafeDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PointOfSaleSafeConnection> listofPOSSafe(PointOfSale obj) {
        String sql = "SELECT \n"
                + "    psc.id AS pscid,\n"
                + "    psc.safe_id AS pscsafe_id,\n"
                + "    sf.name AS sfname, \n"
                + "    sf.code AS sfcode,\n"
                + "    sf.currency_id AS sfcurrency_id\n"
                + "FROM general.pointofsale_safe_con psc\n"
                + "LEFT JOIN finance.safe sf ON(sf.id=psc.safe_id AND sf.deleted = False)\n"
                + "WHERE psc.pointofsale_id=? AND psc.deleted=False";

        Object[] param = new Object[]{obj.getId()};
        List<PointOfSaleSafeConnection> result = getJdbcTemplate().query(sql, param, new PointOfSaleSafeMapper());
        return result;
    }

    @Override
    public int create(PointOfSaleSafeConnection obj) {
        String sql = "INSERT INTO general.pointofsale_safe_con\n"
                + "(pointofsale_id,safe_id, c_id,u_id) \n"
                + "VALUES(?, ?, ?, ?) \n"
                + "RETURNING id ;";
        Object[] param = new Object[]{obj.getPointOfSale().getId(), obj.getSafe().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(PointOfSaleSafeConnection obj) {
        String sql = "UPDATE general.pointofsale_safe_con "
                + "SET "
                + "pointofsale_id = ?, "
                + "safe_id = ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getPointOfSale().getId(), obj.getSafe().getId(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(PointOfSaleSafeConnection obj) {
        String sql = "UPDATE general.pointofsale_safe_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
