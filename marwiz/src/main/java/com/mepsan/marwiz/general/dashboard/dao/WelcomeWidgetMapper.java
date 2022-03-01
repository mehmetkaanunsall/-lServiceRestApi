/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.dashboard.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class WelcomeWidgetMapper implements RowMapper<WelcomeWidget> {

    @Override
    public WelcomeWidget mapRow(ResultSet rs, int i) throws SQLException {
        WelcomeWidget welcomeWidget = new WelcomeWidget();

        try {
            welcomeWidget.setShiftNo(rs.getString("shfshiftno"));
        } catch (Exception e) {
        }
        welcomeWidget.setTotalPrice(rs.getBigDecimal("slpprice"));
        welcomeWidget.getType().setId(rs.getInt("slptype_id"));
        welcomeWidget.getType().setTag(rs.getString("typdname"));
        welcomeWidget.getCurrency().setId(rs.getInt("slcurrency_id"));

        return welcomeWidget;
    }

}
