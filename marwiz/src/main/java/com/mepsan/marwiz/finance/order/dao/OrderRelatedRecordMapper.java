/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ebubekir.buker
 */
public class OrderRelatedRecordMapper implements RowMapper<OrderRelatedRecord> {

    @Override
    public OrderRelatedRecord mapRow(ResultSet rs, int i) throws SQLException {
        OrderRelatedRecord orderRelatedRecord = new OrderRelatedRecord();

        orderRelatedRecord.setId(rs.getInt("id"));
        orderRelatedRecord.setDocumentNumber(rs.getString("documentnumber"));
        orderRelatedRecord.setDocumentDate(rs.getTimestamp("processdate"));
        orderRelatedRecord.setDocumentType(rs.getInt("type"));
        orderRelatedRecord.setRelatedId(rs.getInt("rowid"));

        return orderRelatedRecord;
    }

}
