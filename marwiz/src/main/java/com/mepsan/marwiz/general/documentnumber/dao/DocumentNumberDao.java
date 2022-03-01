/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 01:23:40
 */
package com.mepsan.marwiz.general.documentnumber.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DocumentNumberDao extends JdbcDaoSupport implements IDocumentNumberDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<DocumentNumber> listOfDocumentNumber() {
        String sql = "SELECT \n"
                  + "     dcn.id AS dcnid,\n"
                  + "     dcn.item_id AS dcnitem_id,\n"
                  + "     dcn.name AS dcnname,\n"
                  + "     dcn.serial AS dcnserial,\n"
                  + "     dcn.beginnumber AS dcnbeginnumber,\n"
                  + "     dcn.endnumber AS dcnendnumber,\n"
                  + "     dcn.actualnumber AS dcnactualnumber\n"
                  + "FROM general.documentnumber dcn\n"
                  + "WHERE dcn.branch_id=? AND dcn.deleted=false";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<DocumentNumber> result = getJdbcTemplate().query(sql, param, new DocumentNumberMapper());
        return result;
    }

    @Override
    public int create(DocumentNumber obj) {
        String sql = "INSERT INTO general.documentnumber\n"
                  + "(branch_id, name, item_id, serial, beginnumber, endnumber, actualnumber ,c_id, u_id) \n"
                  + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) \n"
                  + "RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getItem().getId(), obj.getSerial(),
            obj.getBeginNumber(), obj.getEndNumber(), obj.getActualNumber(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(DocumentNumber obj) {
        String sql = "UPDATE general.documentnumber "
                  + "SET "
                  + "name = ?, "
                  + "item_id = ?, "
                  + "serial = ?, "
                  + "beginnumber = ?, "
                  + "endnumber = ?, "
                  + "actualnumber = ?, "
                  + "u_id= ? ,"
                  + "u_time= now() "
                  + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), obj.getItem().getId(), obj.getSerial(), obj.getBeginNumber(), obj.getEndNumber(), obj.getActualNumber(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<DocumentNumber> listOfDocumentNumber(Item item, Branch branch) {
        String sql = "SELECT \n"
                  + "     dcn.id AS dcnid,\n"
                  + "     dcn.item_id AS dcnitem_id,\n"
                  + "     dcn.name AS dcnname,\n"
                  + "     dcn.serial AS dcnserial,\n"
                  + "     dcn.beginnumber AS dcnbeginnumber,\n"
                  + "     dcn.endnumber AS dcnendnumber,\n"
                  + "     dcn.actualnumber AS dcnactualnumber\n"
                  + "FROM general.documentnumber dcn\n"
                  + "WHERE dcn.branch_id=? AND dcn.deleted=false AND dcn.item_id = ? ";

        Object[] param = new Object[]{branch.getId(), item.getId()};
        List<DocumentNumber> result = getJdbcTemplate().query(sql, param, new DocumentNumberMapper());
        return result;
    }

    @Override
    public int delete(DocumentNumber documentNumber) {
        String sql = "UPDATE general.documentnumber set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), documentNumber.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(DocumentNumber documentNumber) {

        String sql = "";

        switch (documentNumber.getItem().getId()) {
            //irsaliye
            case 16:
                sql = "SELECT CASE WHEN EXISTS (SELECT id FROM finance.waybill WHERE documentnumber_id=? AND deleted=FALSE) THEN 1 ELSE 0 END";
                break;
            //fatura
            case 17:
                sql = "SELECT CASE WHEN EXISTS (SELECT id FROM finance.invoice WHERE documentnumber_id=? AND deleted=FALSE) THEN 1 ELSE 0 END";
                break;
            //çek
            case 18:
                sql = "SELECT CASE WHEN EXISTS (SELECT id FROM finance.chequebill WHERE documentnumber_id=? AND deleted=FALSE) THEN 1 ELSE 0 END";
                break;
            default:
                break;
        }

        Object param[] = new Object[]{documentNumber.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
