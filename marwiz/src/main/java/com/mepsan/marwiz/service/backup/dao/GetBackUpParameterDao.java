/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.05.2020 09:44:17
 */
package com.mepsan.marwiz.service.backup.dao;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GetBackUpParameterDao extends JdbcDaoSupport implements IGetBackUpParameteDao {

    @Override
    public int callTableSequence(String response) {
        String sql = "SELECT r_responsecode FROM log.set_tablesequence (?)";

        Object[] param = new Object[]{response};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
