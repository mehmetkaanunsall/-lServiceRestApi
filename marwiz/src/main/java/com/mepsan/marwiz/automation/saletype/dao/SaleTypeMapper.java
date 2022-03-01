package com.mepsan.marwiz.automation.saletype.dao;

import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Samet Dağ
 */
public class SaleTypeMapper implements RowMapper<FuelSaleType> {

    @Override
    public FuelSaleType mapRow(ResultSet rs, int i) throws SQLException {
        FuelSaleType saleType = new FuelSaleType();

        saleType.setId(rs.getInt("fstid"));
        saleType.setName(rs.getString("fstname"));
        saleType.setTypeno(rs.getInt("fsttypeno"));

        return saleType;
    }

}
