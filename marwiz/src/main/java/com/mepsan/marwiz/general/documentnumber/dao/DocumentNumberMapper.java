/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 01:24:34
 */
package com.mepsan.marwiz.general.documentnumber.dao;

import com.mepsan.marwiz.general.model.general.DocumentNumber;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DocumentNumberMapper implements RowMapper<DocumentNumber> {

    @Override
    public DocumentNumber mapRow(ResultSet rs, int i) throws SQLException {
        DocumentNumber documentNumber = new DocumentNumber();
        documentNumber.setId(rs.getInt("dcnid"));
        documentNumber.getItem().setId(rs.getInt("dcnitem_id"));
        documentNumber.setName(rs.getString("dcnname"));
        documentNumber.setSerial(rs.getString("dcnserial"));
        documentNumber.setBeginNumber(rs.getInt("dcnbeginnumber"));
        documentNumber.setEndNumber(rs.getInt("dcnendnumber"));
        documentNumber.setActualNumber(rs.getInt("dcnactualnumber"));

        return documentNumber;
    }
}
