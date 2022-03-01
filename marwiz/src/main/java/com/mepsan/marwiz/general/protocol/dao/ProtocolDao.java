/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * @date  23.11.2016 07:31:23
 */
package com.mepsan.marwiz.general.protocol.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.general.Protocol;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ProtocolDao extends JdbcDaoSupport implements IProtocolDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Protocol> findAll(Item item) {
        String sql = "SELECT\n"
                  + "     prtc.id AS prtcid,\n"
                  + "     prtc.name AS prtcname,\n"
                  + "     prtc.protocolno AS prtcprotocolno\n"
                  + "FROM\n"
                  + "inventory.protocol prtc\n"
                  + "WHERE prtc.item_id = ? AND prtc.deleted = False\n";
        Object[] param = new Object[]{item.getId()};
        return getJdbcTemplate().query(sql, param, new ProtocolMapper());
    }

    @Override
    public int create(Protocol obj) {
        String sql = "INSERT INTO inventory.protocol (name, protocolno, item_id, c_id, u_id) VALUES (?, ?, ?, ?, ?)RETURNING id;";

        Object[] param = new Object[]{obj.getName(), obj.getProtocolNo(), obj.getItem().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(Protocol obj) {
        String sql = "UPDATE inventory.protocol SET name= ?, protocolno = ?, u_id = ?, u_time = NOW() WHERE id = ? ";
        Object[] param = new Object[]{obj.getName(), obj.getProtocolNo(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int testBeforeDelete(Protocol protocol) {
        String sql = "SELECT\n"
                  + "   CASE WHEN EXISTS (SELECT protocol_id FROM inventory.vendingmachine WHERE protocol_id=? AND deleted=False) THEN 1\n"
                  + "        ELSE 0 END";

        Object[] param = new Object[]{protocol.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Protocol protocol) {
        String sql = "UPDATE inventory.protocol SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), protocol.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
