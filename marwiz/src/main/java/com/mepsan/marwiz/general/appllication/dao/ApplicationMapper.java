/**
 * Bu sınıf, ApplicationList nesnesini oluşturur ve özelliklerini set eder.
 *
 *
 * @author Salem walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.appllication.dao;

import com.mepsan.marwiz.general.model.wot.ApplicationList;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ApplicationMapper implements RowMapper<ApplicationList> {

    @Override
    public ApplicationList mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApplicationList appList = new ApplicationList();
        appList.setStatusJson(rs.getString("status"));
        appList.setTypeJson(rs.getString("type"));
        appList.setLangJson(rs.getString("language"));
        appList.setCurrencyJson(rs.getString("currency"));
        appList.setParameters(rs.getString("parameter"));
        appList.setBranchShiftPayment(rs.getString("branchshiftpayment"));
        // appList.setQuartzJobJson(rs.getString("quartzjob"));

        return appList;
    }

}
