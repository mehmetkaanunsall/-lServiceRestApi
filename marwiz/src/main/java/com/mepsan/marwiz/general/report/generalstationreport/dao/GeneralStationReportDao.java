/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.generalstationreport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.GeneralStation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author m.duzoylum
 */
public class GeneralStationReportDao extends JdbcDaoSupport implements IGeneralStationReportDao {

    @Override
    public List<GeneralStation> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeneralStation> findAll(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable, int centralIntegrationIf, int costType) {

        String sql = "";

        sql = " SELECT * FROM general.rpt_generalstationreportfuelstocks (?,?,?,?,?,?,?)";

        Object[] param = new Object[]{branchList, beginDate, endDate, lastUnitPrice, costType, centralIntegrationIf, 1};

        List<GeneralStation> list = getJdbcTemplate().query(sql, param, new GeneralStationReportMapper());
        return list;

    }

    @Override
    public List<GeneralStation> findAllMarket(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int centralIntegrationIf, int costType) {
        String sql = "select * from general.rpt_generalstationreport (?,?,?,?,?,?,?)";
        Object param[] = new Object[]{beginDate, endDate, branchList, lastUnitPrice, 1, centralIntegrationIf, costType};//1->Rapor 2->Total ve Count

        List<GeneralStation> list = getJdbcTemplate().query(sql, param, new GeneralStationReportMapper());
        return list;
    }

    @Override
    public List<GeneralStation> findAllAutomat(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int costType) {

        String sql = "";

        sql = "select * from general.rpt_generalstationreportautomat (?,?,?,?,?,?)";
        Object param[] = new Object[]{branchList, beginDate, endDate, lastUnitPrice, costType, 1};

        List<GeneralStation> list = getJdbcTemplate().query(sql, param, new GeneralStationReportMapper());
        return list;

    }

    @Override
    public List<GeneralStation> totals(String where, Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable, int centralIntegrationIf, int costType) {

        String sql = "";

        Object[] param = new Object[]{};

        switch (typeOfTable) {
            case 1:

                sql = " SELECT * FROM general.rpt_generalstationreportfuelstocks (?,?,?,?,?,?,?)";

                param = new Object[]{branchList, beginDate, endDate, lastUnitPrice, costType, centralIntegrationIf, 2};
                break;
            case 2:

                sql = "select * from general.rpt_generalstationreportautomat (?,?,?,?,?,?)";

                param = new Object[]{branchList, beginDate, endDate, lastUnitPrice, costType, 2};
                break;
            case 3:

                sql = "select * from general.rpt_generalstationreport (?,?,?,?,?,?)";
                param = new Object[]{beginDate, endDate, branchList, lastUnitPrice, 2, centralIntegrationIf};//1->Rapor 2->Total ve Count
                break;
        }

        List<GeneralStation> list = getJdbcTemplate().query(sql, param, new GeneralStationReportMapper());

        return list;

    }

}
