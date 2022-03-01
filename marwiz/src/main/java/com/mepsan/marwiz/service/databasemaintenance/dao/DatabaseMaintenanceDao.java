/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.04.2021 03:33:02
 */
package com.mepsan.marwiz.service.databasemaintenance.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DatabaseMaintenanceDao extends JdbcDaoSupport implements IDatabaseMaintenanceDao {

    @Override
    public void analyzeReindexDatabase() {
        String sql = "ANALYZE;\n"
                  + "REINDEX DATABASE marwiz;";

        getJdbcTemplate().execute(sql);
    }

    @Override
    public void vacuumDatabase() {
        String sql = "VACUUM;";

        getJdbcTemplate().execute(sql);
    }

}
