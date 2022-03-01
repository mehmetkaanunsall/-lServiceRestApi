/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class SapStockControlProcessDao extends JdbcDaoSupport implements ISapStockControlProcessDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SapStockControlProcess> compareStockInfos(SapStockControlProcess sapStock, Date date, int differenceReasonType) {

        String sql = " Select * from integration.process_sap_stockcontrol (?, ?, ?, ?)";

        Object[] param = new Object[]{sapStock.getItemJson(), date, sessionBean.getUser().getLastBranch().getId(), differenceReasonType};

        try {
            return getJdbcTemplate().query(sql, param, new SapStockControlProcessMapper());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }

    }

    @Override
    public int insertOrUpdateLog(SapStockControlProcess obj) {
        Object[] param;

        String sql = "                       UPDATE \n"
                + "                             integration.sap_stockcontrol\n"
                + "                          SET \n"
                + "                              requestjson = ?,\n"
                + "                              responsejson = ?,\n"
                + "                              u_id = ?, \n"
                + "                              u_time = now()\n"
                + "                           WHERE\n"
                + "                             deleted = FALSE \n"
                + "                             AND branch_id = ?\n"
                + "                             AND controldate = ? \n"
                + "                             AND EXISTS (\n"
                + "                                 Select \n"
                + "                                   id\n"
                + "                                 FROM integration.sap_stockcontrol ssc\n"
                + "                                 WHERE \n"
                + "                                 ssc.deleted =FALSE\n"
                + "                                 AND ssc.branch_id = ?\n"
                + "                                 AND ssc.controldate = ?\n"
                + "                                 );\n"
                + "                          INSERT INTO integration.sap_stockcontrol (branch_id, controldate, requestjson, responsejson, c_id, u_id )\n"
                + "                             SELECT ?,?,?,?,?,?\n"
                + "                               WHERE NOT EXISTS(\n"
                + "                                 Select \n"
                + "                                  id\n"
                + "                                 FROM integration.sap_stockcontrol ssc\n"
                + "                                 WHERE \n"
                + "                                 ssc.deleted =FALSE\n"
                + "                                 AND ssc.branch_id = ?\n"
                + "                                 AND ssc.controldate = ?\n"
                + "                                 );";

        param = new Object[]{obj.getSendData(), obj.getGetData(), sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getProcessDate(), sessionBean.getUser().getLastBranch().getId(), obj.getProcessDate(),
            sessionBean.getUser().getLastBranch().getId(), obj.getProcessDate(), obj.getSendData(), obj.getGetData(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getProcessDate()};


        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            if (((SQLException) e.getCause()) == null) {
                return -1;
            } else {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }

        }

    }

}
