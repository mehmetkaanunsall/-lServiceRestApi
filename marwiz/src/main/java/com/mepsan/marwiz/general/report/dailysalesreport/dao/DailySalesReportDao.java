/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 03:22:55
 */
package com.mepsan.marwiz.general.report.dailysalesreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DailySalesReportDao extends JdbcDaoSupport implements IDailySalesReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public DailySalesReport findAll(DailySalesReport dailySalesReport, String branchList, String sortBy) {

        String sql = " SELECT r_result FROM general.rpt_dailysalesreport(?, ?, ?, ?, ?)";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(),
            dailySalesReport.getBeginDate(), dailySalesReport.getEndDate(), branchList, sortBy};

        
        List<DailySalesReport> result = getJdbcTemplate().query(sql, param, new DailySalesReportMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new DailySalesReport();
        }
    }

}
