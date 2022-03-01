/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 06.02.2019 09:39:37
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TankMapper implements RowMapper<Warehouse> {

    @Override
    public Warehouse mapRow(ResultSet rs, int i) throws SQLException {
        Warehouse wareHouse = new Warehouse();
        wareHouse.setId(rs.getInt("iwid"));
        wareHouse.setName(rs.getString("iwname"));
        wareHouse.setCode(rs.getString("iwcode"));
        wareHouse.setDescription(rs.getString("iwdescription"));
        wareHouse.getStatus().setId(rs.getInt("iwstatus_id"));
        wareHouse.setIsFuel(rs.getBoolean("iwis_fuel"));
        wareHouse.setCapacity(rs.getBigDecimal("iwcapacity"));
        wareHouse.setMinCapacity(rs.getBigDecimal("iwmincapacity"));

        try {
            wareHouse.setConcentrationRate(rs.getBigDecimal("iwconcantrationrate"));
            wareHouse.setPurchaseTotalPrice(rs.getBigDecimal("purchasetotalprice"));
            wareHouse.setPurchaseTotalMoney(rs.getBigDecimal("purchasetotalmoney"));
            wareHouse.setPurchaseTotalLiter(rs.getBigDecimal("purchasetotalliter"));
            wareHouse.setSalesTotalLiter(rs.getBigDecimal("salestotalliter"));
            wareHouse.setSalesTotalMoney(rs.getBigDecimal("salestotalmoney"));
            wareHouse.setSalesTotalPrice(rs.getBigDecimal("salestotalprice"));
            wareHouse.setLastQuantity(rs.getBigDecimal("lastquantity"));
            wareHouse.setPurchaseUnitPriceWithTax(rs.getBigDecimal("purchaseunitpricewithtax"));
            wareHouse.setPurchaseUnitPriceWithoutTax(rs.getBigDecimal("purchaseunitpricewithouttax"));
            wareHouse.getStock().setId(rs.getInt("iwistock_id"));
            wareHouse.getStock().setName(rs.getString("stckname"));
            wareHouse.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            wareHouse.getStock().getUnit().setName(rs.getString("guntname"));
            wareHouse.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            wareHouse.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            wareHouse.setSalesUnitPriceWithTax(rs.getBigDecimal("salesunitpricewithtax"));
            wareHouse.setSalesUnitPriceWithoutTax(rs.getBigDecimal("salesunitpricewithouttax"));
            wareHouse.getStatus().setTag(rs.getString("stdname"));

        } catch (Exception Ex) {

        }
        return wareHouse;
    }

}
