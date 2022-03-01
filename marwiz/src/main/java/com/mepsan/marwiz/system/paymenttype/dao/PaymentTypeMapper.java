/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.paymenttype.dao;

import com.mepsan.marwiz.general.model.general.PaymentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author m.duzoylum
 */
public class PaymentTypeMapper implements RowMapper<PaymentType> {

    @Override
    public PaymentType mapRow(ResultSet rs, int i) throws SQLException {
        PaymentType paymentType = new PaymentType();

        paymentType.setId(rs.getInt("id"));
        paymentType.setEntegrationcode(rs.getString("integrationcode"));
        paymentType.setEntegrationname(rs.getString("name"));

        return paymentType;

    }
}
