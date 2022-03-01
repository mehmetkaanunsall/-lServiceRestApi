/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 10:15:44
 */
package com.mepsan.marwiz.finance.safe.dao;

import com.mepsan.marwiz.general.model.finance.Safe;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SafeMapper implements RowMapper<Safe> {

    @Override
    public Safe mapRow(ResultSet rs, int i) throws SQLException {
        Safe safe = new Safe();

        try {
            safe.setReportBalance(rs.getBigDecimal("balance"));
        } catch (Exception e) {
        }

        try {
            safe.setId(rs.getInt("sfid"));
            safe.setName(rs.getString("sfname"));
        } catch (Exception e) {
        }
        try {
            safe.getCurrency().setId(rs.getInt("sfcurrency_id"));
        } catch (Exception e) {
        }
        try {
            safe.getCurrency().setTag(rs.getString("crrdname"));
        } catch (Exception e) {
        }
        try {
            safe.getCurrency().setCode(rs.getString("crrcode"));
        } catch (Exception e) {
        }
        try {
            safe.setCode(rs.getString("sfcode"));
        } catch (Exception e) {
        }

        try {
            safe.setBalance(rs.getBigDecimal("sfbalance"));
        } catch (Exception e) {
        }

        try {

            safe.getCurrency().setSign(rs.getString("crrsign"));

            safe.getCurrency().setTag(rs.getString("crrdname"));

            safe.getStatus().setId(rs.getInt("sfstatus_id"));
            safe.getStatus().setTag(rs.getString("sttdname"));
        } catch (Exception e) {
        }
        try {
            safe.setShiftmovementsafe_id(rs.getInt("sfshiftmovsf_id"));
        } catch (Exception e) {

        }
        try { // sistem hariç giriş çıkış yapılsın mı ?
            safe.setIsMposMovement(rs.getBoolean("sfis_mposmovement"));
        } catch (Exception e) {
        }
        try {
            safe.getBranch().setId(rs.getInt("sfbranch_id"));
        } catch (Exception e) {
        }


        return safe;
    }

}
