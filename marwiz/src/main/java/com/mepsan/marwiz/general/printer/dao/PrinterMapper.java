/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.08.2020 02:21:16
 */
package com.mepsan.marwiz.general.printer.dao;

import com.mepsan.marwiz.general.model.general.Printer;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PrinterMapper implements RowMapper<Printer> {

    @Override
    public Printer mapRow(ResultSet rs, int i) throws SQLException {
        Printer printer = new Printer();
        printer.setId(rs.getInt("prid"));
        printer.setIpAddress(rs.getString("pripaddress"));
        printer.setPort(rs.getString("prport"));
        try {
            printer.setName(rs.getString("prname"));
            printer.setMacAddress(rs.getString("prmacaddress"));
            printer.getType().setId(rs.getInt("prtype_id"));
            printer.getType().setTag(rs.getString("typdname"));
            printer.setIsDefault(rs.getBoolean("pris_default"));
        } catch (Exception e) {
        }

        return printer;
    }

}