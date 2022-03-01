/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * @date  23.11.2016 07:31:35
 */
package com.mepsan.marwiz.general.protocol.dao;

import com.mepsan.marwiz.general.model.general.Protocol;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProtocolMapper implements RowMapper<Protocol> {

    @Override
    public Protocol mapRow(ResultSet rs, int i) throws SQLException {

        Protocol protocol = new Protocol();
        protocol.setName(rs.getString("prtcname"));
        protocol.setId(rs.getInt("prtcid"));
        protocol.setProtocolNo(rs.getInt("prtcprotocolno"));

        return protocol;

    }

}
