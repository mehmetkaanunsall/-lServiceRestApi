/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.08.2020 02:21:03
 */
package com.mepsan.marwiz.general.printer.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Printer;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PrinterDao extends JdbcDaoSupport implements IPrinterDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Printer> listOfPrinter() {
        String sql = "SELECT \n"
                  + "	 pr.id AS prid,\n"
                  + "    pr.name AS prname,\n"
                  + "    pr.ipaddress AS pripaddress,\n"
                  + "    pr.port AS prport,\n"
                  + "    pr.macaddress AS prmacaddress,\n"
                  + "    pr.type_id AS prtype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    pr.is_default AS pris_default\n"
                  + "FROM general.printer pr \n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = pr.type_id AND typd.language_id = ?)\n"
                  + "WHERE pr.deleted=FALSE AND pr.branch_id = ?";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Printer> result = getJdbcTemplate().query(sql, param, new PrinterMapper());
        return result;
    }

    @Override
    public int create(Printer obj) {
        String sql = "INSERT INTO general.printer\n"
                  + "(branch_id, name, ipaddress, port, macaddress, type_id, is_default, c_id, u_id) \n"
                  + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) \n"
                  + "RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getIpAddress(), obj.getPort(), obj.getMacAddress(), obj.getType().getId(), obj.isIsDefault(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(Printer obj) {
        String sql = "UPDATE general.printer\n"
                  + "SET "
                  + "name = ?, "
                  + "ipaddress = ? ,"
                  + "port = ? ,"
                  + "macaddress = ? ,"
                  + "type_id = ? ,"
                  + "is_default = ? ,"
                  + "u_id= ? ,"
                  + "u_time= now() "
                  + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), obj.getIpAddress(), obj.getPort(), obj.getMacAddress(),
            obj.getType().getId(), obj.isIsDefault(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Printer obj) {

        String sql = "UPDATE general.printer set deleted=TRUE ,u_id=?, d_time=NOW() WHERE deleted=FALSE AND id=?;";
        
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public Printer listOfPrinterAccordingToType(int type, Branch branch) {
        String sql = "SELECT \n"
                  + "	pr.id AS prid,\n"
                  + "   pr.ipaddress AS pripaddress,\n"
                  + "   pr.port AS prport\n"
                  + "FROM general.printer pr \n"
                  + "WHERE pr.deleted=FALSE AND pr.is_default =TRUE AND pr.type_id = ? AND pr.branch_id=?";

        Object[] param = new Object[]{type, branch.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, new PrinterMapper());
        } catch (EmptyResultDataAccessException e) {
            return new Printer();
        }
    }

}
