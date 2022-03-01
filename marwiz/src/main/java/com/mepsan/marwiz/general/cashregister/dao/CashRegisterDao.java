/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 06:03:46
 */
package com.mepsan.marwiz.general.cashregister.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.CashRegister;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CashRegisterDao extends JdbcDaoSupport implements ICashRegisterDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<CashRegister> listOfCashRegister() {
        String sql = "Select \n"
                + "              cash.id AS cashid,\n"
                + "              cash.name AS cashname,\n"
                + "              cash.brand_id AS cashbrand_id,\n"
                + "              br.name AS brname,\n"
                + "              cash.model AS cashmodel,\n"
                + "              cash.serialnumber AS cashserialnumber,\n"
                + "              cash.version AS cashversion,\n"
                + "              cash.macaddress AS cashmacaddress,\n"
                + "              cash.ipaddress AS cashipaddress,\n"
                + "              cash.is_externaleftpos AS cashisexternaleftpos,\n"
                + "              cash.port AS cashport\n"
                + "FROM general.cashregister cash \n"
                + "LEFT JOIN general.brand br   ON (br.id = cash.brand_id AND br.deleted=false AND br.deleted = False)\n"
                + "WHERE cash.deleted=false AND cash.branch_id=? ";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<CashRegister> result = getJdbcTemplate().query(sql, param, new CashRegisterMapper());
        return result;
    }

    @Override
    public List<CashRegister> selectListCashRegister() {
        String sql = "Select \n"
                + "cash.id AS cashid,\n"
                + "cash.name AS cashname\n"
                + "FROM general.cashregister cash \n"
                + "WHERE cash.deleted=false AND cash.branch_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<CashRegister> result = getJdbcTemplate().query(sql, param, new CashRegisterMapper());
        return result;
    }

    @Override
    public int create(CashRegister obj) {
        String sql = "INSERT INTO general.cashregister\n"
                + "(branch_id,name,brand_id,is_externaleftpos,model,serialnumber,version,"
                + "macaddress,ipaddress,port, c_id,u_id) \n"
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n"
                + "RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getBrand().getId(), obj.isIsExternalEftPos(), obj.getModel(),
            obj.getSerialNumber(), obj.getVersion(), obj.getMacAddress(), obj.getIpAddress(),
            obj.getPort(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(CashRegister obj) {
        String sql = "UPDATE general.cashregister "
                + "SET "
                + "name = ?, "
                + "brand_id = ? ,"
                + "is_externaleftpos = ?,"
                + "model = ? ,"
                + "serialnumber = ? ,"
                + "version = ? ,"
                + "macaddress = ? ,"
                + "ipaddress = ? ,"
                + "port = ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), obj.getBrand().getId(), obj.isIsExternalEftPos(), obj.getModel(),
            obj.getSerialNumber(), obj.getVersion(), obj.getMacAddress(), obj.getIpAddress(),
            obj.getPort(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(CashRegister obj) {
        String sql = "UPDATE general.cashregister set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
