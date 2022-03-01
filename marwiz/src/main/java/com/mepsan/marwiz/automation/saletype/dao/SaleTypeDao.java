package com.mepsan.marwiz.automation.saletype.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class SaleTypeDao extends JdbcDaoSupport implements ISaleTypeDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(FuelSaleType obj) {
        String sql = "INSERT INTO\n"
                + " automation.fuelsaletype(branch_id,name,typeno,c_id,u_id) \n"
                + " VALUES(?,?,?,?,?) RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getTypeno(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(FuelSaleType obj) {
        String sql = "UPDATE automation.fuelsaletype "
                + "SET "
                + " name = ? ,"
                + " typeno = ?, "
                + " u_id= ? ,"
                + " u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), obj.getTypeno(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<FuelSaleType> findAll() {
        String sql = " SELECT \n"
                + "fst.id fstid,\n"
                + "fst.name fstname,\n"
                + "fst.typeno fsttypeno \n"
                + "FROM\n"
                + " automation.fuelsaletype fst "
                + "WHERE deleted=false AND branch_id= ?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<FuelSaleType> result = getJdbcTemplate().query(sql, param, new SaleTypeMapper());
        return result;
    }

    @Override
    public int delete(FuelSaleType obj) {
        String sql = "UPDATE automation.fuelsaletype "
                + "SET "
                + " deleted = true ,"
                + "u_id=? , "
                + " d_time=NOW() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<FuelSaleType> findSaleTypeForBranch(String where) {
        String sql = "SELECT \n"
                + "        fst.id fstid,\n"
                + "        fst.name fstname,\n"
                + "        fst.typeno fsttypeno\n"
                + "FROM automation.fuelsaletype fst\n"
                + "WHERE deleted=false\n"
                + where;

        List<FuelSaleType> result = getJdbcTemplate().query(sql, new SaleTypeMapper());
        return result;
    }

}
