/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.salesnottransferredtotanı.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author sinem.arslan
 */
public class SalesNotTransferredToTanıMapper implements RowMapper<SalesNotTransferredToTanı> {

    @Override
    public SalesNotTransferredToTanı mapRow(ResultSet rs, int i) throws SQLException {
        SalesNotTransferredToTanı slt = new SalesNotTransferredToTanı();

        slt.setSentSalesCount(rs.getBigDecimal("sentsales"));
        slt.setUnsentSalesCount(rs.getBigDecimal("unsentsales"));
        return slt;
    }

}
